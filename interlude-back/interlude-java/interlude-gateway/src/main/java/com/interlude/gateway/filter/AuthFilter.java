package com.interlude.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class AuthFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    // 前台用户 token 名称，需要和 interlude-web 保持一致。
    private static final String WEB_TOKEN = "webToken";

    // interlude-web 登录成功后写入 Redis 的 token key 前缀。
    private static final String WEB_TOKEN_REDIS_KEY_PREFIX = "interlude:token:web:";

    // 网关校验通过后，把当前用户信息透传给后端服务。
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_ID_HEADER = "X-User-Role-Id";

    // 内部网关密钥，后端服务用它判断 X-User-Id 是否可信。
    private static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";

    @Value("${gateway.auth.secret:interlude-gateway-local-secret}")
    private String gatewaySecret;

    @Bean
    public GlobalFilter authLogger(ReactiveStringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        return (exchange, chain) -> {
            // 兼容前端通过 /api 访问网关的情况，鉴权判断时统一转成真实后端路径。
            String path = normalizePath(exchange.getRequest().getURI().getRawPath());
            boolean white = isWhitePath(exchange, path);
            String token = resolveToken(exchange);

            // 白名单接口不强制登录，例如登录、验证码、公开视频、静态文件。
            if (white) {
                log.info("gateway auth pass whitelist, path={}, token={}", path, token == null ? "none" : "found");
                return chain.filter(exchange);
            }

            // 非白名单接口必须携带 token，否则直接在网关返回 401。
            if (token == null || token.isBlank()) {
                log.warn("gateway auth rejected, reason=no token, path={}", path);
                return writeUnauthorized(exchange, "\u8bf7\u5148\u767b\u5f55");
            }

            // 到 Redis 查询 token。只有 Redis 存在该 token，才认为登录状态有效。
            String redisKey = WEB_TOKEN_REDIS_KEY_PREFIX + token;
            return redisTemplate.opsForValue().get(redisKey)
                    .switchIfEmpty(Mono.error(new NoSuchElementException("invalid token")))
                    .flatMap(tokenUserJson -> {
                        // 从 Redis 保存的用户信息里解析 userId/roleId，后续微服务可直接读取请求头。
                        String userId = resolveFieldValue(tokenUserJson, "userId", objectMapper);
                        String roleId = resolveFieldValue(tokenUserJson, "roleId", objectMapper);
                        log.info("gateway auth token valid, path={}, userId={}", path, userId == null ? "unknown" : userId);

                        // 向下游服务补充可信用户头，同时保留原 webToken，兼容旧代码。
                        ServerWebExchange authenticatedExchange = exchange.mutate()
                                .request(builder -> builder.headers(headers -> {
                                    headers.set(WEB_TOKEN, token);
                                    headers.set(GATEWAY_SECRET_HEADER, gatewaySecret);
                                    setHeaderIfNotBlank(headers, USER_ID_HEADER, userId);
                                    setHeaderIfNotBlank(headers, USER_ROLE_ID_HEADER, roleId);
                                }))
                                .build();
                        return chain.filter(authenticatedExchange);
                    })
                    .onErrorResume(NoSuchElementException.class, error -> {
                        // Redis 查不到 token，说明登录已过期或 token 被清理。
                        log.warn("gateway auth rejected, reason=invalid token, path={}", path);
                        return writeUnauthorized(exchange, "\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55");
                    })
                    .onErrorResume(error -> {
                        // Redis 异常时不要放行，避免认证服务故障时出现越权访问。
                        log.error("gateway auth redis check failed, path={}", path, error);
                        return writeServiceUnavailable(exchange, "\u8ba4\u8bc1\u670d\u52a1\u6682\u65f6\u4e0d\u53ef\u7528");
                    });
        };
    }

    private boolean isWhitePath(ServerWebExchange exchange, String path) {
        if (path == null || path.isBlank()) {
            return false;
        }

        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return true;
        }

        // 只把明确公开的接口加入白名单，避免 /video/play/finish 这类动作接口被误放行。
        return path.equals("/account/login")
                || path.equals("/account/register")
                || path.equals("/account/checkCode")
                || path.equals("/account/autoLogin")
                || path.equals("/account/sendEmailCode")
                || path.equals("/account/sendResetPasswordEmailCode")
                || path.equals("/account/resetPassword")
                || pathEqualsOrChild(path, "/video/discover")
                || pathEqualsOrChild(path, "/video/feed")
                || path.equals("/video/comment/list")
                || path.equals("/video/danmu/list")
                || path.equals("/video/follow/status")
                || pathEqualsOrChild(path, "/search")
                || pathEqualsOrChild(path, "/file")
                || path.matches("^/video/play/\\d+$")
                || path.matches("^/video/play/\\d+/playlist$")
                || path.matches("^/video/play/\\d+/related$");
    }

    private boolean pathEqualsOrChild(String path, String whitePath) {
        return path.equals(whitePath) || path.startsWith(whitePath + "/");
    }

    private String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        if (path.equals("/api")) {
            return "/";
        }
        if (path.startsWith("/api/")) {
            return path.substring(4);
        }
        return path;
    }

    private String resolveToken(ServerWebExchange exchange) {
        // 优先从 webToken 请求头读取，适合前端或服务间主动传 token。
        String webTokenHeader = exchange.getRequest().getHeaders().getFirst(WEB_TOKEN);
        if (webTokenHeader != null && !webTokenHeader.isBlank()) {
            return webTokenHeader;
        }

        // 兼容 Authorization: Bearer xxx 格式。
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && !authorization.isBlank()) {
            if (authorization.startsWith("Bearer ")) {
                return authorization.substring(7);
            }
            return authorization;
        }

        // 浏览器正常登录后主要通过 webToken Cookie 携带 token。
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(WEB_TOKEN);
        if (cookie != null && cookie.getValue() != null && !cookie.getValue().isBlank()) {
            return cookie.getValue();
        }

        return null;
    }

    private String resolveFieldValue(String tokenUserJson, String fieldName, ObjectMapper objectMapper) {
        if (tokenUserJson == null || tokenUserJson.isBlank()) {
            return null;
        }
        try {
            // Redis value 是 JSON 时，优先使用 Jackson 解析。
            JsonNode node = objectMapper.readTree(tokenUserJson).get(fieldName);
            if (node != null && !node.isNull()) {
                return node.asText();
            }
        } catch (Exception ignored) {
            // 兜底处理非标准 JSON 字符串，避免字段解析失败影响正常转发。
            Matcher matcher = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"?([^\",}]+)").matcher(tokenUserJson);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private void setHeaderIfNotBlank(HttpHeaders headers, String name, String value) {
        if (value != null && !value.isBlank()) {
            headers.set(name, value);
        }
    }

    private Mono<Void> writeUnauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return writeJson(exchange, 401, message);
    }

    private Mono<Void> writeServiceUnavailable(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return writeJson(exchange, 503, message);
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, int code, String message) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":" + code + ",\"message\":\"" + message + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
