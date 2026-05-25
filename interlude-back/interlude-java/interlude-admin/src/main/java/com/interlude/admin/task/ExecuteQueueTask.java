package com.interlude.admin.task;

import com.interlude.component.RedisComponent;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.service.video.VideoInfoService;
import com.interlude.entity.config.TaskQueueConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
@RequiredArgsConstructor    // 为final字段生成构造函数
public class ExecuteQueueTask {

    private ExecutorService executorService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoInfoService videoInfoService;

    private final TaskQueueConfig taskQueueConfig;

    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        // 配置化线程池
        this.executorService = Executors.newFixedThreadPool(taskQueueConfig.getThreadPoolSize());

        if (taskQueueConfig.isEnabled()) {
            startConsuming();
        }
    }

    public void startConsuming() {
        // 创建多个消费线程（真正的并发消费）
        for (int i = 0; i < taskQueueConfig.getThreadPoolSize(); i++) {
            executorService.execute(this::consumeTransferFileQueue);
        }
        log.info("启动 {} 个队列消费线程", taskQueueConfig.getThreadPoolSize());
    }

    // 原有消费逻辑保持不变
    public void consumeTransferFileQueue() {
        while (running) {
            try {
                UploadResultDto transferQueue = redisComponent.getFileTransferQueue();
                if (transferQueue == null) {
                    Thread.sleep(taskQueueConfig.getEmptyQueueSleepMs());
                    continue;
                }
                videoInfoService.transferVideoFile(transferQueue);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("队列消费线程被中断");
                break;
            } catch (Exception e) {
                log.error("处理转码任务异常: {}", e.getMessage(), e);
            }
        }
    }

    @PreDestroy  // Spring容器销毁前执行
    public void shutdown() {
        log.info("开始关闭队列消费服务...");
        running = false;  // 通知所有消费线程停止

        if (executorService != null) {
            executorService.shutdown();  // 平缓关闭：不再接受新任务，等待现有任务完成

            try {
                // 等待30秒让正在执行的任务完成
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    // 30秒后还有任务未完成，强制关闭
                    executorService.shutdownNow();
                    log.warn("线程池未在30秒内正常关闭，已强制终止");
                }
            } catch (InterruptedException e) {
                // 等待过程中被中断，立即强制关闭
                executorService.shutdownNow();
                Thread.currentThread().interrupt();  // 保持中断状态
            }
        }
        log.info("队列消费服务已关闭");
    }

}
