package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoDraft;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.video.VideoDraftService;
import com.interlude.utils.DateUtils;
import com.interlude.utils.StringTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
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

    @Resource
    private AppConfig appConfig;

    @RequestMapping("getDraftInfoByUserId")
    public ResponseVO getDraftInfoByUserId() {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        List<UploadResultDto> resultDtoList = new ArrayList<>();
        List<VideoDraft> videoDraftList = videoDraftService.getVideoDraftByUserId(tokenUserInfo.getUserId());
        if (videoDraftList != null && videoDraftList.size() > 0) {
            videoDraftList.stream().forEach(videoDraft -> {
                if(videoDraft.getDraftStatus() == 1 && videoDraft.getUploadStatus() == 2){
                    UploadResultDto uploadVideoFileInfoByKey = redisComponent.getUploadVideoFileInfoByKey(videoDraft.getDraftKey());
                    if (uploadVideoFileInfoByKey != null){
                        resultDtoList.add(uploadVideoFileInfoByKey);
                    }else{
                        // 如果 redis的已过期或者不存在, 但是数据库的有就删除数据库对应的数据
                        videoDraftService.deleteVideoDraftByDraftKey(videoDraft.getDraftKey());
                    }
                }
            });
            return getSuccessResponseVO(resultDtoList);
        }
        return getSuccessResponseVO(null);
    }

    // 更新草稿
    @RequestMapping("updateDraftInfo")
    public ResponseVO updateDraftInfo(UploadResultDto uploadResultDto,MultipartFile videoCoverFile) throws IOException {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        String draftKey = Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadResultDto.getUploadId();

        // 更新redis
        UploadResultDto resultDto = redisComponent.getUploadVideoFileInfoByKey(draftKey);
        if(resultDto == null){
            throw new BusinessException("当前文件不存在");
        }
        if(videoCoverFile != null){
            resultDto.setVideoCover(uploadCover(videoCoverFile)); // 保存封面
        }
        resultDto.setpCategoryId(uploadResultDto.getpCategoryId());
        resultDto.setCategoryId(uploadResultDto.getCategoryId());
        resultDto.setInteractionSettings(uploadResultDto.getInteractionSettings());
        resultDto.setVideoName(uploadResultDto.getVideoName());
        resultDto.setTags(uploadResultDto.getTags());
        resultDto.setDescription(uploadResultDto.getDescription());
        resultDto.setVisibility(uploadResultDto.getVisibility());
        if(StringUtils.isNotBlank(uploadResultDto.getFileName())){
            resultDto.setFileName(uploadResultDto.getFileName());
        }
        if(StringUtils.isNotBlank(uploadResultDto.getUploadId())){
            resultDto.setUploadId(uploadResultDto.getUploadId());
        }
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

    // 上传封面
    public String uploadCover(MultipartFile file) throws IOException {
        String monthDD = DateUtils.format(new Date(), DateTimePatterEnum.YYYYMMDD.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_COVER + monthDD;
        File folderFile = new File(folder);
        if (!folderFile.exists()){
            folderFile.mkdirs();
        }
        String filename = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(filename);
        String realFileName = StringTools.getRandomString(30) + fileSuffix;
        String filePath = folder + File.separator + realFileName;
        file.transferTo(new File(filePath));
        String videoCover = Constants.FILE_COVER + monthDD + "/" + realFileName;
        return videoCover;
    }
}
