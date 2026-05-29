package com.interlude.web.config;

import com.interlude.component.RedisComponent;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.UserInfo;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.UserStatusEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.UserInfoService;
import com.interlude.utils.StringTools;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Validate the front-end token before protected handlers execute.
 */
@Component
public class AppInterceptor implements HandlerInterceptor {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private GatewayAuthHelper gatewayAuthHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        TokenUserInfoDto gatewayUser = gatewayAuthHelper.getGatewayUser(request);
        if (gatewayUser != null) {
            checkUserEnabled(gatewayUser.getUserId());
            return true;
        }

        String token = request.getHeader(Constants.REDIS_WEB_TOKEN);
        if (StringTools.isEmpty(token)) {
            token = getTokenFromCookie(request);
        }

        TokenUserInfoDto tokenUserInfoDto = StringTools.isEmpty(token) ? null : redisComponent.getWebToken(token);
        if (tokenUserInfoDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        checkUserEnabled(tokenUserInfoDto.getUserId());
        return true;
    }

    private void checkUserEnabled(String userId) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo == null || (userInfo.getEnabled() != null && userInfo.getEnabled().intValue() == UserStatusEnum.DISABLE.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (Constants.REDIS_WEB_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
