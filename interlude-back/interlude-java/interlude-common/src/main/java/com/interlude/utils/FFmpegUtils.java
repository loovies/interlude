package com.interlude.utils;

import com.interlude.entity.constants.Constants;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;

@Component
public class FFmpegUtils {

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

    public String getVideoCodec(String videoFilePath) {
        final String CMD_GET_CODE="ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        String cmd = String.format(CMD_GET_CODE,videoFilePath);
        String result = ProcessUtils.executeCommand(cmd,true);
        result = result.replace("\n","");
        result = result.substring(result.indexOf("=")+1);
        String codec = result.substring(0,result.indexOf("["));
        return codec;
    }

    public void convertHevc2Mp4(String newFileName,String videoFilePath) {
        String CMD_HEVC_265="ffmpeg -i %s -c:v libx264 -crf 20 %s -y";
        String cmd = String.format(CMD_HEVC_265,newFileName,videoFilePath);
        ProcessUtils.executeCommand(cmd,true);
    }

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
}
