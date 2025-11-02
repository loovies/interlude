package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.component.RedisComponent;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoDraft;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.exception.BusinessException;
import com.interlude.service.video.VideoDraftService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/draft")
@Validated
public class VideoDraftController extends ABaseController {

    @Resource
    private VideoDraftService videoDraftService;

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("getDraftInfoByUserId")
    public ResponseVO getDraftInfoByUserId() {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        List<UploadResultDto> resultDtoList = new ArrayList<>();
        List<VideoDraft> videoDraftList = videoDraftService.getVideoDraftByUserId(tokenUserInfo.getUserId());
        if (videoDraftList != null && videoDraftList.size() > 0) {
            videoDraftList.stream().forEach(videoDraft -> {
                if(videoDraft.getDraftStatus() == 1 && videoDraft.getUploadStatus() != 3){
                    UploadResultDto uploadVideoFileInfoByKey = redisComponent.getUploadVideoFileInfoByKey(videoDraft.getDraftKey());
                    resultDtoList.add(uploadVideoFileInfoByKey);
                }
            });
            return getSuccessResponseVO(resultDtoList);
        }
        return getSuccessResponseVO(null);
    }

    // 更新草稿
    @RequestMapping("updateDraftInfo")
    public ResponseVO updateDraftInfo(UploadResultDto uploadResultDto) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        String draftKey = Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadResultDto.getUploadId();

        // 更新redis
        UploadResultDto resultDto = redisComponent.getUploadVideoFileInfoByKey(draftKey);
        if(resultDto == null){
            throw new BusinessException("当前文件不存在");
        }
        resultDto.setFileName(uploadResultDto.getFileName());
        resultDto.setUploadId(uploadResultDto.getUploadId());
        redisComponent.uploadVideoFileInfo(tokenUserInfo.getUserId(), resultDto);

        // 更新数据库
        VideoDraft videoDraft = new VideoDraft();
        videoDraft.setVideoName(uploadResultDto.getFileName());
        videoDraft.setUpdateTime(new Date());

        VideoDraft videoDraftByDraftKey = videoDraftService.getVideoDraftByDraftKey(draftKey);
        if (videoDraftByDraftKey == null){
            throw new BusinessException("当前文件不存在");
        }
        videoDraftService.updateVideoDraftByDraftKey(videoDraft,draftKey);
        return getSuccessResponseVO(null);
    }


}
