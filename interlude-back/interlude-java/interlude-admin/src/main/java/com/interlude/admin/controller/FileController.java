package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.SysSettingDto;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoDraft;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.video.VideoDraftService;
import com.interlude.utils.DateUtils;
import com.interlude.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Date;

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
    public ResponseVO uploadImage(@NotNull MultipartFile file,@NotNull Boolean createThumbnail) throws IOException {
        String month = DateUtils.format(new Date(), DateTimePatterEnum.YYYY_MM.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_COVER + month;
        File folderFile = new File(folder);
        if (!folderFile.exists()){
            folderFile.mkdirs();
        }
        String filename = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(filename);
        String realFileName = StringTools.getRandomString(30) + fileSuffix;
        String filePath = folder + File.separator + realFileName;
        file.transferTo(new File(filePath));
        if(createThumbnail){
            // TODO 生成缩略图
        }
        return getSuccessResponseVO(Constants.FILE_COVER + month + "/" + realFileName);
    }

    // 视频预上传
    @RequestMapping("/preUploadVideo")
    public ResponseVO preUploadVideo(@NotNull String fileName,@NotNull Integer chunks){
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        // 存上传的视频文件到redis
        UploadResultDto uploadData = redisComponent.savePreVideoFileInfo(tokenUserInfo.getUserId(),fileName,chunks);
        VideoDraft videoDraft = new VideoDraft();
        // 保存上传的视频文件到 草稿表
        if(uploadData != null){
            videoDraft.setDraftKey(Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadData.getUploadId());
            videoDraft.setVideoName(uploadData.getFileName());
            videoDraft.setUserId(tokenUserInfo.getUserId());
            videoDraftService.add(videoDraft);
        }
        return getSuccessResponseVO(uploadData.getUploadId());
    }

    // 视频上传
    @RequestMapping("uploadVideo")
    public ResponseVO uploadVideo(@NotNull MultipartFile file, @NotNull Integer chunksIndex, @NotNull Long uploadSize,
                                  @NotNull String uploadId, @NotNull Long uid , @NotNull String status) throws IOException {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        VideoDraft videoDraft = new VideoDraft();
        UploadResultDto uploadResultDto = redisComponent.getUploadVideoFileInfo(tokenUserInfo.getUserId(), uploadId);

        if(uploadResultDto == null){
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
        uploadResultDto.setFileSize(uploadResultDto.getFileSize() + file.getSize());
        uploadResultDto.setUploadId(uploadId);
        uploadResultDto.setUid(uid);
        uploadResultDto.setStatus(status);
        uploadResultDto.setUploadPercent(100);
        uploadResultDto.setUploadSize(uploadSize);

        videoDraft.setUploadStatus(1);
        if(chunksIndex == uploadResultDto.getChunks() - 1){
            uploadResultDto.setStatus("success");
            videoDraft.setUploadStatus(2);
        }
        videoDraftService.updateVideoDraftByDraftKey(videoDraft,Constants.REDIS_KEY_UPLOADING_FILE+tokenUserInfo.getUserId()+uploadResultDto.getUploadId());

        redisComponent.uploadVideoFileInfo(tokenUserInfo.getUserId(),uploadResultDto);
        return getSuccessResponseVO(null);
    }

    // 删除上传
    @RequestMapping("delUploadVideo")
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
