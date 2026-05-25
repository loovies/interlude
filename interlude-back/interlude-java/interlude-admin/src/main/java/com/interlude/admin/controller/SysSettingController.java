package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
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
}
