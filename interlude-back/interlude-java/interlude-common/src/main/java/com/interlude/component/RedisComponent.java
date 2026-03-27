package com.interlude.component;

import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.SysSettingDto;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.redis.RedisUtils;
import com.interlude.utils.DateUtils;
import com.interlude.utils.StringTools;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Configuration
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private AppConfig appConfig;

    // 保存验证码 保存6分钟
    public String saveCheckCode(String code){
        String codeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_CHECKCODE_PREFIX+codeKey,code,Constants.REDIS_TIME_ONE_MINUTE * 6);
        return codeKey;
    }

    // 获取验证码
    public String getCheckCode(String code){
        String codeKey = Constants.REDIS_CHECKCODE_PREFIX+code;
        return  (String) redisUtils.get(codeKey);
    }

    // 删除验证码
    public void delCheckCode(String code){
        String codeKey = Constants.REDIS_CHECKCODE_PREFIX+code;
        redisUtils.delete(codeKey);
    }

    // 保存token 到redis
    public String saveAdmin4Token(TokenUserInfoDto tokenUserInfoDto){
        return saveToken(tokenUserInfoDto, Constants.REDIS_TOKEN_ADMIN_KEY);
    }

    // 获取token
    public TokenUserInfoDto getAdmin4Token(String token){
        return getToken(token, Constants.REDIS_TOKEN_ADMIN_KEY);
    }

    public String saveWebToken(TokenUserInfoDto tokenUserInfoDto){
        return saveToken(tokenUserInfoDto, Constants.REDIS_TOKEN_WEB_KEY);
    }

    // 前台用户 token 查询。
    public TokenUserInfoDto getWebToken(String token){
        return getToken(token, Constants.REDIS_TOKEN_WEB_KEY);
    }

    public Long incrementSendEmailCodeCount(String email){
        if (StringTools.isEmpty(email)) {
            return 0L;
        }
        // 同一邮箱一分钟内只允许发送一次验证码。
        return redisUtils.incrementex(Constants.REDIS_KEY_SEND_EMAIL_CODE + email, Constants.REDIS_TIME_ONE_MINUTE);
    }

    // 清除token
    public void cleanToken(String token){
        if (StringTools.isEmpty(token)) {
            return;
        }
        redisUtils.delete(Constants.REDIS_TOKEN_ADMIN_KEY + token, Constants.REDIS_TOKEN_WEB_KEY + token);
    }

    // 保存分类信息
    public void saveCategoryList(List<CategoryInfo> categoryInfoList){
        redisUtils.set(Constants.REDIS_KEY_CATEGORY_LIST,categoryInfoList);
    }

    // 获取分类信息
    public List<CategoryInfo> getCategoryList(){
        return  (List<CategoryInfo>) redisUtils.get(Constants.REDIS_KEY_CATEGORY_LIST);
    }

    //获取系统信息
    public SysSettingDto getSysSetting(){
        SysSettingDto settingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_SYS_SETTING_KEY);
        if (settingDto == null){
            settingDto = new SysSettingDto();
        }
        return  settingDto;
    }

    // 保存预上传文件
    public UploadResultDto savePreVideoFileInfo(String userId, String fileName,String fileIdentifier, Integer chunks) {

        String uploadId = StringTools.getRandomString(15);
        UploadResultDto uploadResultDto = new UploadResultDto();
        uploadResultDto.setUploadId(uploadId);
        uploadResultDto.setFileName(fileName);
        uploadResultDto.setFileIdentifier(fileIdentifier);
        uploadResultDto.setChunks(chunks);
        uploadResultDto.setChunkIndex(0);

        String day = DateUtils.format(new Date(), DateTimePatterEnum.YYYYMMDD.getPattern());
        String filePath = day + "/"+userId + uploadId;
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + filePath;
        File file = new File(folder);
        if(!file.exists()){
            file.mkdirs();
        }
        uploadResultDto.setFilePath(filePath);
        redisUtils.setex(Constants.REDIS_KEY_UPLOADING_FILE + userId + uploadId,uploadResultDto,Constants.REDIS_TIME_ONE_DAY * 3);
        return uploadResultDto;
    }

    // 获取上传的文件信息
    public UploadResultDto getUploadVideoFileInfo(String userId, String uploadId) {
        return (UploadResultDto) redisUtils.get(Constants.REDIS_KEY_UPLOADING_FILE+userId+uploadId);
    }

    // 获取上传的文件信息by key
    public UploadResultDto getUploadVideoFileInfoByKey(String key) {
        return (UploadResultDto) redisUtils.get(key);
    }

    // 更新上传文件信息
    public void uploadVideoFileInfo(String userId, UploadResultDto uploadResultDto) {
        redisUtils.setex(Constants.REDIS_KEY_UPLOADING_FILE+userId+uploadResultDto.getUploadId(),uploadResultDto,Constants.REDIS_TIME_ONE_DAY * 3);
    }

    // 删除上传文件信息
    public void delVideoFileInfo(String userId,String uploadId){
        redisUtils.delete(Constants.REDIS_KEY_UPLOADING_FILE + userId + uploadId);
    }

    public void addFile2TransferQueue(UploadResultDto resultDto) {
        redisUtils.lpush(Constants.REDIS_KEY_QUEUE_TRANSFER,resultDto, 0L);
    }

    public UploadResultDto getFileTransferQueue() {
        return (UploadResultDto) redisUtils.rpop(Constants.REDIS_KEY_QUEUE_TRANSFER);
    }

    private String saveToken(TokenUserInfoDto tokenUserInfoDto, String tokenKeyPrefix) {
        String token = StringTools.isEmpty(tokenUserInfoDto.getToken()) ? UUID.randomUUID().toString() : tokenUserInfoDto.getToken();
        long expireTime = Constants.REDIS_TIME_ONE_DAY.longValue() * 7;
        tokenUserInfoDto.setToken(token);
        tokenUserInfoDto.setExpireAt(System.currentTimeMillis() + expireTime);
        redisUtils.setex(tokenKeyPrefix + token, tokenUserInfoDto, expireTime);
        return token;
    }

    private TokenUserInfoDto getToken(String token, String tokenKeyPrefix) {
        if (StringTools.isEmpty(token)) {
            return null;
        }
        return (TokenUserInfoDto) redisUtils.get(tokenKeyPrefix + token);
    }
}
