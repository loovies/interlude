package com.interlude.utils;

import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.enums.FileStatusEnum;
import com.interlude.enums.QualityStatusEnum;
import com.interlude.enums.VideoQualityEnum;
import com.interlude.service.video.VideoFileService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FFmpegUtils {

    @Resource
    private VideoFileService videoFileService;

    /**
     * 创建缩略图
     * @param filePath
     */
    public void createTimageThumbnail(String filePath)  {
        String cmd = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        cmd = String.format(cmd,filePath+ Constants.IMAGE_THUMBNAIL_SUFFIX);
        ProcessUtils.executeCommand(cmd,true);
    }

    /**
     * 获取视频持续时长
     * @param completeVideo
     * @return
     */
    public Integer getVideoInfoDuration(String completeVideo){
        final String CMD_GET_CODE="ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"%s\"";
        String cmd = String.format(CMD_GET_CODE,completeVideo);
        String result = ProcessUtils.executeCommand(cmd,true);
        if(StringTools.isEmpty(result)){
            return 0;
        }
        result = result.replace("\n","");   // 移除结果中的换行符，确保结果是纯数字字符串
        return new BigDecimal(result).intValue();   //将字符串结果转换为BigDecimal，然后获取整数值 使用BigDecimal可以避免浮点数精度问题，但直接取整会截断小数部
    }

    /**
     * 获取视频文件的编码格式
     * @param videoFilePath
     * @return
     */
    public String getVideoCodec(String videoFilePath) {
        final String CMD_GET_CODE="ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        String cmd = String.format(CMD_GET_CODE,videoFilePath);
        String result = ProcessUtils.executeCommand(cmd,true);
        result = result.replace("\n","");
        result = result.substring(result.indexOf("=")+1);
        String codec = result.substring(0,result.indexOf("["));
        return codec;
    }

    // 用于将 HEVC/H.265 编码的视频转换为 H.264 编码的 MP4 文件
    public void convertHevc2Mp4(String newFileName,String videoFilePath) {
        String CMD_HEVC_265="ffmpeg -i %s -c:v libx264 -crf 20 %s -y";
        String cmd = String.format(CMD_HEVC_265,newFileName,videoFilePath);
        ProcessUtils.executeCommand(cmd,true);
    }

    /**
     * 生成 HLS 流媒体所需的 .m3u8 索引文件和多个 .ts 切片文件
     * @param tsFolder
     * @param videoFilePath
     */
    public void convertVideo2Ts(File tsFolder, String videoFilePath){
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i \"%s\" -vcodec copy -acodec copy -vbsf h264_mp4toannexb \"%s\"";
        final String CMD_CUT_TS="ffmpeg -i \"%s\" -c copy -map 0 -f segment -segment_list \"%s\" -segment_time 10 %s/%%4d.ts";
        String tsPath = tsFolder+"/"+Constants.TS_NAME;
        // 生成 .ts
        String cmd = String.format(CMD_TRANSFER_2TS,videoFilePath,tsPath);
        ProcessUtils.executeCommand(cmd,true);
        // 生成索引文件 .m3u8 和切片 .ts
        cmd = String.format(CMD_CUT_TS,tsPath,tsFolder.getPath() +"/"+Constants.M3U8_NAME,tsFolder.getPath());
        ProcessUtils.executeCommand(cmd,true);
        // 删除index.ts
        new File(tsPath).delete();
    }
    /**
     * 将视频转换为多清晰度的 HLS 格式
     * 为每个清晰度生成独立的文件夹，包含对应的 .m3u8 和 .ts 文件
     *
     * @param outputFolder 输出目录（视频文件所在目录）
     * @param videoFilePath 视频文件路径
     */
    public void convertVideoToMultiQualityHLS(File outputFolder, String videoFilePath, List<VideoQualityEnum> qualities, UploadResultDto resultDto){
        // 如果未指定清晰度列表，使用所有支持的清晰度
        if (qualities == null || qualities.isEmpty()) {
            qualities = VideoQualityEnum.getSupportedQualities();
        }

        VideoFile videoFile = new VideoFile();
        videoFile.setUserId(resultDto.getUserId());
        videoFile.setVideoId(resultDto.getVideoId());
        videoFile.setUploadId(resultDto.getUploadId());
        videoFile.setFileName(resultDto.getFileName());
        videoFile.setFileSize(resultDto.getFileSize());
        System.out.println("开始生成多清晰度 HLS 视频，原始文件: " + videoFilePath);
        System.out.println("将生成 " + qualities.size() + " 个清晰度: " +
                qualities.stream().map(VideoQualityEnum::getName).collect(Collectors.joining(", ")));

        // 为每个清晰度生成对应的 HLS 流
        for (VideoQualityEnum quality : qualities) {
            try {
                System.out.println("正在生成 " + quality.getDescription() + " (" + quality.getName() + ") ...");

                videoFile.setQuality(QualityStatusEnum.getByDesc(quality.getName()).getStatus());
                videoFile.setWidth(quality.getWidth());
                videoFile.setHeight(quality.getHeight());
                videoFile.setBitrate(quality.getBitrate());
                videoFile.setFilePath(videoFilePath);

                // 为当前清晰度创建专属文件夹
                File qualityFolder = new File(outputFolder, quality.getFolderName());
                if (!qualityFolder.exists()) {
                    qualityFolder.mkdirs();
                }

                // 生成该清晰度的 HLS 流
                convertVideoToSpecificQualityHLS(qualityFolder, videoFilePath, quality);

                System.out.println(quality.getDescription() + " 生成完成");
                videoFile.setFileStatus(FileStatusEnum.READY.getStatus());
            } catch (Exception e) {
                System.err.println("生成 " + quality.getDescription() + " 失败: " + e.getMessage());
                videoFile.setFileStatus(FileStatusEnum.FAILED.getStatus());
                e.printStackTrace();
                // 继续处理其他清晰度
            }finally {
                // 添加
                videoFileService.add(videoFile);
            }
        }

        System.out.println("所有清晰度生成完成");
    }

    /**
     * 使用默认清晰度配置转换视频
     */
    public void convertVideoToMultiQualityHLS(File outputFolder, String videoFilePath,UploadResultDto resultDto) {
        // 使用所有支持的清晰度
        convertVideoToMultiQualityHLS(outputFolder, videoFilePath, VideoQualityEnum.getSupportedQualities(),resultDto);
    }

    /**
     * 将视频转换为特定清晰度的 HLS 格式
     */
    private void convertVideoToSpecificQualityHLS(File qualityFolder, String videoFilePath, VideoQualityEnum quality) {
        // 步骤1: 先将视频转码到目标清晰度（临时文件）
        String tempVideoPath = qualityFolder.getPath() + "/temp_" + quality.getName() + ".mp4";
        transcodeToQuality(videoFilePath, tempVideoPath, quality);

        // 步骤2: 将转码后的视频转换为 HLS 格式
        convertVideo2Ts(qualityFolder, tempVideoPath);

        // 步骤3: 清理临时转码文件
        File tempFile = new File(tempVideoPath);
        if (tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                System.err.println("警告: 无法删除临时文件: " + tempVideoPath);
            }
        }
    }

    /**
     * 将视频转码到特定清晰度
     */
    private void transcodeToQuality(String inputFile, String outputFile, VideoQualityEnum quality) {
        // ffmpeg 转码命令
        String transcodeCmd = String.format(
                "ffmpeg -i \"%s\" -c:v libx264 -b:v %dk -s %dx%d -c:a aac \"%s\" -y",
                inputFile, quality.getBitrate(), quality.getWidth(), quality.getHeight(), outputFile
        );

        System.out.println("执行转码命令: " + transcodeCmd);
        ProcessUtils.executeCommand(transcodeCmd, true);
    }

    /**
     * 生成主播放列表文件 (master.m3u8)
     * 根据实际生成的清晰度创建主播放列表
     *
     * @param outputFolder 输出目录
     * @param qualities 实际生成的清晰度列表
     */
    public void generateMasterPlaylist(File outputFolder, List<VideoQualityEnum> qualities) {
        StringBuilder masterContent = new StringBuilder();
        masterContent.append("#EXTM3U\n");
        masterContent.append("#EXT-X-VERSION:3\n");

        int generatedCount = 0;

        // 为每个清晰度添加流信息（只添加实际存在的）
        for (VideoQualityEnum quality : qualities) {
            File qualityFolder = new File(outputFolder, quality.getFolderName());
            File playlistFile = new File(qualityFolder, Constants.M3U8_NAME);

            if (playlistFile.exists()) {
                // 添加流信息到主播放列表
                masterContent.append(String.format(
                        "#EXT-X-STREAM-INF:BANDWIDTH=%d,RESOLUTION=%dx%d,NAME=\"%s\"\n",
                        quality.getBitrateBps(),  // 使用 bps 格式
                        quality.getWidth(),
                        quality.getHeight(),
                        quality.getName()
                ));
                // 添加该清晰度的播放列表路径
                masterContent.append(quality.getFolderName()).append("/").append(Constants.M3U8_NAME).append("\n");
                generatedCount++;
            }
        }

        if (generatedCount == 0) {
            System.err.println("警告: 没有找到任何可用的清晰度文件，无法生成主播放列表");
            return;
        }

        // 写入主播放列表文件
        try {
            File masterFile = new File(outputFolder, "master.m3u8");
            Files.write(masterFile.toPath(), masterContent.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("主播放列表生成完成: " + masterFile.getPath() + " (包含 " + generatedCount + " 个清晰度)");
        } catch (IOException e) {
            System.err.println("生成主播放列表失败: " + e.getMessage());
            throw new RuntimeException("生成主播放列表失败", e);
        }
    }

    /**
     * 使用默认清晰度配置生成主播放列表
     */
    public void generateMasterPlaylist(File outputFolder) {
        generateMasterPlaylist(outputFolder, VideoQualityEnum.getSupportedQualities());
    }
}
