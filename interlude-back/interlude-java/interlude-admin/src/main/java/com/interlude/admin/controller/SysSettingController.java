package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.dto.SysSettingDto;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.entity.vo.ResponseVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/sysSetting")
@Validated
public class SysSettingController extends ABaseController{

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/loadSysSetting")
    public ResponseVO getSysSettingInfo(){
        return getSuccessResponseVO(redisComponent.getSysSetting());
    }

    @RequestMapping("/saveSysSetting")
    public ResponseVO saveSysSetting(SysSettingDto settingDto){
        if (settingDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        normalizeSetting(settingDto);
        redisComponent.saveSysSetting(settingDto);
        return getSuccessResponseVO(redisComponent.getSysSetting());
    }

    /**
     * 对系统设置做基础兜底，避免写入空值后影响业务逻辑。
     */
    private void normalizeSetting(SysSettingDto settingDto){
        settingDto.setVideoSize(normalizePositive(settingDto.getVideoSize(), 2000));
        settingDto.setVideoPCount(normalizePositive(settingDto.getVideoPCount(), 100));
        settingDto.setVideoCount(normalizePositive(settingDto.getVideoCount(), 100));
        settingDto.setCommentCount(normalizePositive(settingDto.getCommentCount(), 50));
        settingDto.setDanmuCount(normalizePositive(settingDto.getDanmuCount(), 50));

        String title = settingDto.getRegisterEMailTitle();
        if (title == null || title.trim().isEmpty()){
            settingDto.setRegisterEMailTitle("邮箱验证码");
        }else{
            settingDto.setRegisterEMailTitle(title.trim());
        }

        String content = settingDto.getRegisterEmailContent();
        if (content == null || content.trim().isEmpty()){
            settingDto.setRegisterEmailContent("您好, 您的邮箱验证码是, %s, 15分钟有效");
        }else{
            settingDto.setRegisterEmailContent(content.trim());
        }
    }

    private Integer normalizePositive(Integer value, Integer defaultValue){
        if (value == null || value <= 0){
            return defaultValue;
        }
        return value;
    }
}
