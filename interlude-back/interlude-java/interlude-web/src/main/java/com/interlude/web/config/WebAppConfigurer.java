package com.interlude.web.config;

import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Resource
    private AppInterceptor appInterceptor;

    @Resource
    private AppConfig appConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Only public account endpoints bypass token validation.
        registry.addInterceptor(appInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/account/checkCode",
                        "/account/login",
                        "/account/autoLogin",
                        "/account/logout",
                        "/account/sendEmailCode",
                        "/account/sendResetPasswordEmailCode",
                        "/account/register",
                        "/account/resetPassword",
                        "/error"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose stored files so uploaded avatars can be loaded from /file/**.
        String location = "file:" + (appConfig.getProjectFolder() + Constants.FILE_FOLDER).replace("\\", "/");
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/file/**")
                .addResourceLocations(location)
                .setCachePeriod(0);
    }
}
