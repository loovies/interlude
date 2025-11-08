package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.component.RedisComponent;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.exception.BusinessException;
import com.interlude.service.video.VideoAuditService;
import com.interlude.service.video.VideoDraftService;
import com.interlude.service.video.VideoInfoService;
import com.interlude.utils.StringTools;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RestController
@RequestMapping("/vp")
@Validated
public class VideoPostController extends ABaseController {

    @Resource
    private VideoDraftService videoDraftService;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoAuditService videoAuditService;

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("postVideo")
    public ResponseVO postVideo(@NotNull String uploadId){
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();

        UploadResultDto fileInfoByKey = redisComponent.getUploadVideoFileInfoByKey(Constants.REDIS_KEY_UPLOADING_FILE + tokenUserInfo.getUserId() + uploadId);
        if(fileInfoByKey == null){
            throw new BusinessException("文件不存在");
        }

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoId(Long.parseLong(StringTools.getRandomNumber(Constants.NUMBER_15)));
        videoInfo.setUserId(tokenUserInfo.getUserId());
        videoInfo.setVideoName(fileInfoByKey.getVideoName());
        videoInfo.setVideoCover(fileInfoByKey.getVideoCover());
        videoInfo.setDescription(fileInfoByKey.getDescription());
        videoInfo.setPCategoryId(fileInfoByKey.getpCategoryId());
        videoInfo.setCategoryId(fileInfoByKey.getCategoryId());
        videoInfo.setVideoType(fileInfoByKey.getPostType());
        videoInfo.setTags(fileInfoByKey.getTags());
        videoInfo.setVisibility(fileInfoByKey.getVisibility());
        Integer status = fileInfoByKey.getStatus().equals("success") ? 1 : 0;
        videoInfo.setStatus(status);
        videoInfo.setPublishTime(new Date());

        videoInfoService.saveVideoInfo(videoInfo);

        return getSuccessResponseVO(null);
    }
}
