package com.interlude.admin.interceptor;

import com.interlude.entity.constants.Constants;
import com.interlude.component.RedisComponent;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.utils.StringTools;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppInterceptor implements HandlerInterceptor {

    private final static String URL_ACCOUNT = "/account";
    private final static String URL_FILE = "/file";

    @Resource
    private RedisComponent redisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler == null){
            return false;
        }
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        if(request.getRequestURI().contains(URL_ACCOUNT)){
            return true;
        }

        String token = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 查找名为"admintoken"的cookie
                if (Constants.REDIS_ADMIN_TOKEN.equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // 获取图片
        if(request.getRequestURI().contains(URL_FILE)){
            token = getTokenFromRequest(request);
        }

        if(StringTools.isEmpty(token)){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        Object tokenInfo4Admin = redisComponent.getAdmin4Token(token);
        if(tokenInfo4Admin == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return true;
    }

    private String getTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(Constants.REDIS_ADMIN_TOKEN)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
