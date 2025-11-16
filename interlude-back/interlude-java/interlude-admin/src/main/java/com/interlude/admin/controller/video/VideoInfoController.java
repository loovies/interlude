package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.service.UserInfoService;
import com.interlude.service.video.VideoFileService;
import com.interlude.service.video.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/videoInfo")
public class VideoInfoController extends ABaseController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoFileService videoFileService;

    @RequestMapping("/loadVideoInfo")
    public ResponseVO loadVideoInfo(){
        return getSuccessResponseVO(null);
    }
}
