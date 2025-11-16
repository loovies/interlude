package com.interlude.entity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "task.queue")
@Data
public class TaskQueueConfig {
    private int threadPoolSize = 2;     // 线程数 2
    private long emptyQueueSleepMs = 1500;  // 空队列休眠时间
    private boolean enabled = true;     // 是否启用消费
}