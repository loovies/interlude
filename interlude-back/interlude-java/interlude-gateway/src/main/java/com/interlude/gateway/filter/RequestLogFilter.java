package com.interlude.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class RequestLogFilter {

    // 打印日志
    @Bean
    public GlobalFilter requestLogger() {
        return (exchange, chain) -> {
            long start = System.currentTimeMillis();
            String method = exchange.getRequest().getMethod().name();
            String path = exchange.getRequest().getURI().getRawPath();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                int status = exchange.getResponse().getStatusCode() == null
                        ? 0
                        : exchange.getResponse().getStatusCode().value();
                long cost = System.currentTimeMillis() - start;
                log.info("gateway request, method={}, path={}, status={}, cost={}ms", method, path, status, cost);
            }));
        };
    }
}