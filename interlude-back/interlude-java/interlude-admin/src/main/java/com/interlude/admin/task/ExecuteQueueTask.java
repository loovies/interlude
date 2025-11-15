package com.interlude.admin.task;

import com.interlude.component.RedisComponent;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.service.video.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ExecuteQueueTask {

    // 创建固定大小的线程池 (2个线程)
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoInfoService videoInfoService;

    // 初始化方法 - 在Bean创建后自动执行
    @PostConstruct
    public void consumTransferFileQueue(){
        executorService.execute(() ->{
            while (true){
                try {
                    // 从redis队列中获取转码任务
                    UploadResultDto transferQueue = redisComponent.getFileTransferQueue();
                    if (transferQueue == null){
                        Thread.sleep(1500); // 队列为空时休眠
                        continue;
                    }
                    videoInfoService.transferVideoFile(transferQueue); // 处理视频文件转换任务
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        });
    }
}
