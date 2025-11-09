package com.interlude.utils;

import com.interlude.entity.constants.Constants;
import org.springframework.stereotype.Component;

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
}
