package com.interlude.admin.interceptor;

import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import io.lettuce.core.dynamic.annotation.Value;
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
        registry.addInterceptor(appInterceptor).addPathPatterns("/**").excludePathPatterns(
                "admin/videos/**"
        );
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将本地视频目录映射到 /videos/** URL路径
        String localVideoPath = appConfig.getProjectFolder()+ Constants.FILE_FOLDER+Constants.FILE_VIDEO;

        String location = "file:" + localVideoPath.replace("\\", "/");
        if (!location.endsWith("/")) {
            location += "/";
        }

        System.out.println("静态资源映射配置:");
        System.out.println("URL路径: /videos/**");
        System.out.println("本地路径: " + location);

        registry.addResourceHandler("/videos/**")
                .addResourceLocations(location)
                .setCachePeriod(0);
    }
}
