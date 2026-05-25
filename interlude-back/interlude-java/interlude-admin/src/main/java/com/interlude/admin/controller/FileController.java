package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.SysSettingDto;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoDraft;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.FileStatusEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.video.VideoDraftService;
import com.interlude.service.video.VideoFileService;
import com.interlude.utils.DateUtils;
import com.interlude.utils.FFmpegUtils;
import com.interlude.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController extends ABaseController{

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoDraftService videoDraftService;

    @Resource
    private VideoFileService videoFileService;


    /**
     * 获取资源
     * @param response
     * @param sourceName
     */
    @RequestMapping("/getResource")
    public void getResource(HttpServletResponse response, @NotNull String sourceName) {
        if(!StringTools.pathIsOk(sourceName)){      // 判断文件地址是否规范
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String suffix = StringTools.getFileSuffix(sourceName);
        response.setContentType("image/"+suffix.replace(".",""));
        response.setHeader("Cache-Control","max-age=2592000");
        readFile(response,sourceName);
    }

    /**
     * 上传图片
     */
    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file) throws IOException {
        String month = DateUtils.format(new Date(), DateTimePatterEnum.YYYY_MM.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_AVATAR + month;
        File folderFile = new File(folder);
        if (!folderFile.exists()){
            folderFile.mkdirs();
        }
        String filename = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(filename);
        String realFileName = StringTools.getRandomString(30) + fileSuffix;
        String filePath = folder + File.separator + realFileName;
        file.transferTo(new File(filePath));
        return getSuccessResponseVO(Constants.FILE_AVATAR + month + "/" + realFileName);
    }

    // 视频预上传
    @RequestMapping("/preUploadVideo")
    public ResponseVO preUploadVideo(@NotNull String fileName,@NotNull Integer chunks, @NotNull String fileIdentifier){
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();

        VideoDraft idAndFileName = videoDraftService.getVideoDraftByUserIdAndFileName(tokenUserInfo.getUserId(), fileName);
        if(idAndFileName != null && idAndFileName.getUploadStatus() == 1){ // 判断草稿表是否存在, 且 上传状态为 上传中
            UploadResultDto info = redisComponent.getUploadVideoFileInfoByKey(idAndFileName.getDraftKey());
            if(info != null){
                if(info.getFileIdentifier().equals(fileIdentifier)){
                    return getSuccessResponseVO(info);
                }
            }
        }else{
            // 存上传的视频文件到redis
            UploadResultDto uploadData = redisComponent.savePreVideoFileInfo(tokenUserInfo.getUserId(),fileName,fileIdentifier,chunks);
            VideoDraft videoDraft = new VideoDraft();
            // 保存上传的视频文件到 草稿表
            if(uploadData != null){
                videoDraft.setDraftKey(Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadData.getUploadId());
                videoDraft.setVideoName(uploadData.getFileName());
                videoDraft.setUserId(tokenUserInfo.getUserId());
                videoDraftService.add(videoDraft);
            }
            // 保存文件信息到文件视频表
            return getSuccessResponseVO(uploadData);
        }
        return getSuccessResponseVO(null);
    }

    // 视频上传
    @RequestMapping("uploadVideo")
    public ResponseVO uploadVideo(@NotNull MultipartFile file, @NotNull Integer chunksIndex, @NotNull Long uploadSize,
                                  @NotNull String uploadId, @NotNull Long uid , @NotNull String status,@NotNull Long fileSize, @NotNull Integer uploadPercent) throws IOException {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        VideoDraft videoDraft = new VideoDraft();
        UploadResultDto uploadResultDto = redisComponent.getUploadVideoFileInfo(tokenUserInfo.getUserId(), uploadId);

        List<VideoFile> videoFileByUploadIdAndUserId = videoFileService.getVideoFileByUploadIdAndUserId(uploadId, tokenUserInfo.getUserId());
        if(videoFileByUploadIdAndUserId == null && videoFileByUploadIdAndUserId.size() > 0 && uploadResultDto == null){
            videoDraft.setUploadStatus(3);
            throw new BusinessException("视频不存在请重新上传");
        }
        SysSettingDto settingDto = redisComponent.getSysSetting();
        if(uploadResultDto.getFileSize() > settingDto.getVideoSize() * Constants.MB_SIZE){
            videoDraft.setUploadStatus(3);
            throw new BusinessException("文件超过大小限制");
        }
        // 第一个条件 防止分片乱序上传  当前分片的前一个分片必须已经上传成功 例如3号分片还没上传，不能直接上传4号分片
        // 第二个条件： 防止超出总分片数,当前分片索引不能超过最大有效索引
        if((chunksIndex -1) > uploadResultDto.getChunkIndex() || chunksIndex > uploadResultDto.getChunks()-1){
            videoDraft.setUploadStatus(3);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + uploadResultDto.getFilePath();
        File targetFile = new File(folder + File.separator + chunksIndex);
        file.transferTo(targetFile);
        uploadResultDto.setChunkIndex(chunksIndex);
        uploadResultDto.setFileSize(fileSize);
        uploadResultDto.setUploadId(uploadId);
        uploadResultDto.setUid(uid);
        uploadResultDto.setStatus(status);
        uploadResultDto.setUserId(tokenUserInfo.getUserId());
        uploadResultDto.setUploadPercent(uploadPercent);
        uploadResultDto.setUploadSize(uploadSize);

        videoDraft.setUploadStatus(1);
        if(chunksIndex == uploadResultDto.getChunks() - 1){
            uploadResultDto.setUploadPercent(100);
            uploadResultDto.setStatus("success");
            videoDraft.setUploadStatus(2);
        }
        videoDraftService.updateVideoDraftByDraftKey(videoDraft,Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadResultDto.getUploadId());
        redisComponent.uploadVideoFileInfo(tokenUserInfo.getUserId(),uploadResultDto);
        return getSuccessResponseVO(null);
    }

    // 删除上传
    @RequestMapping("delUploadVideo")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO delUploadVideo(@NotEmpty String uploadId) throws IOException{
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UploadResultDto fileInfo = redisComponent.getUploadVideoFileInfo(tokenUserInfo.getUserId(), uploadId);
        if (fileInfo == null){
            throw new BusinessException("文件不存在请重新上传");
        }

        // 删除redis中的上传数据
        redisComponent.delVideoFileInfo(tokenUserInfo.getUserId(),uploadId);
        FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP+fileInfo.getFilePath()));

        // 删除数据库中的草稿表数据
        String draftKey = Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadId;
        VideoDraft videoDraftByDraftKey = videoDraftService.getVideoDraftByDraftKey(draftKey);
        if (videoDraftByDraftKey == null){
            throw new BusinessException("当前文件不存在");
        }
        videoDraftService.deleteVideoDraftByDraftKey(draftKey);
        return  getSuccessResponseVO(null);
    }
}
