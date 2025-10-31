package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.utils.DateUtils;
import com.interlude.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    @Autowired
    private RedisComponent redisComponent;

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
        String uploadId = redisComponent.savePreVideoFileInfo(tokenUserInfo.getUserId(),fileName,chunks);
        return getSuccessResponseVO(uploadId);
    }
}
