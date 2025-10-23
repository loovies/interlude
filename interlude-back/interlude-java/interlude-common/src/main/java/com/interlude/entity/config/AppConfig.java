package com.interlude.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ，获取配置文件中的配置信息
 */
@Component("appConfig")
public class AppConfig {


    @Value("${project.folder:}")
    private String projectFolder;

    @Value("${spring.mail.username:}")
    private String sendUserName;

    public String getSendUserName() {
        return sendUserName;
    }

    public String getProjectFolder() {
        return projectFolder;
    }
}
