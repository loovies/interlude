package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.entity.vo.video.VideoInfoVo;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.UserInfoService;
import com.interlude.service.video.VideoFileService;
import com.interlude.service.video.VideoInfoService;
import com.interlude.utils.DateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseVO loadVideoInfo(VideoInfoQuery videoInfoQuery){
        List<VideoInfoVo> infoVos = new ArrayList<>();
        PaginationResultVO<VideoInfo> listByPage = videoInfoService.findListByPage(videoInfoQuery);
        listByPage.getList().stream().forEach(item ->{
            VideoInfoVo vo = new VideoInfoVo();
            UserInfo userInfo = userInfoService.getUserInfoByUserId(item.getUserId());
            if(userInfo == null){
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if(userInfo.getUserId().equals(item.getUserId())){
                vo.setNickName(userInfo.getNickName());
            }
            vo.setVideoName(item.getVideoName());
            vo.setVideoCover(item.getVideoCover());
            vo.setpCategoryId(item.getPCategoryId());
            vo.setCategoryId(item.getCategoryId());
            vo.setVideoType(item.getVideoType());
            vo.setStatus(item.getStatus());
            vo.setPublishTime(DateUtils.format(item.getPublishTime(),DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
            infoVos.add(vo);
        });
        PaginationResultVO<VideoInfo> result = new PaginationResultVO(infoVos);
        result.setTotalCount(listByPage.getTotalCount());
        result.setPageNo(listByPage.getPageNo());
        result.setPageSize(listByPage.getPageSize());
        result.setPageTotal(listByPage.getPageTotal());
        return getSuccessResponseVO(result);
    }
}
