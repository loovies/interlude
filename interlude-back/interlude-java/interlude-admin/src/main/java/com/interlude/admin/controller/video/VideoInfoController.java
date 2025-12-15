package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.video.VideoFileQuery;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.entity.vo.video.PlayListInfoVo;
import com.interlude.entity.vo.video.VideoInfoVo;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.VideoQualityEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.CategoryInfoService;
import com.interlude.service.UserInfoService;
import com.interlude.service.video.VideoFileService;
import com.interlude.service.video.VideoInfoService;
import com.interlude.utils.DateUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/videoInfo")
@Valid
public class VideoInfoController extends ABaseController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private CategoryInfoService categoryInfoService;

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
            vo.setVideoId(item.getVideoId());
            vo.setVideoCover(item.getVideoCover());
            vo.setpCategoryId(item.getPCategoryId());
            vo.setCategoryId(item.getCategoryId());
            List<CategoryInfo> categoryInfoList = categoryInfoService.selectCategoryById(item.getPCategoryId(), item.getCategoryId());
            if(item.getPCategoryId() == Constants.NUMBER_ZERO){
                vo.setCategoryName(categoryInfoList.get(0).getCategoryName());
            }else{
                vo.setCategoryName(categoryInfoList.get(0).getCategoryName());
                vo.setpCategoryName(categoryInfoList.get(1).getCategoryName());
            }
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

    @RequestMapping("/{videoId}/playList")
    public ResponseVO getPlayInfo(@PathVariable Long videoId){
        VideoInfo infoByVideoId = videoInfoService.getVideoInfoByVideoId(videoId);
        if(infoByVideoId == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (infoByVideoId.getStatus() ==3 || infoByVideoId.getStatus() ==-1){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        PlayListInfoVo playListInfoVo = new PlayListInfoVo();
        playListInfoVo.setVideoId(infoByVideoId.getVideoId());
        playListInfoVo.setTitle(infoByVideoId.getVideoName());
        ArrayList<Map<String,Object>> qualities = new ArrayList<>();
        VideoFileQuery videoFileQuery = new VideoFileQuery();
        videoFileQuery.setVideoId(videoId);
        List<VideoFile> listByParam = videoFileService.findListByParam(videoFileQuery);
        listByParam.stream().forEach(item -> {
            Map<String,Object> map = new HashMap<>();
            map.put("quality", VideoQualityEnum.getByCode(item.getQuality()-1).getName());
            map.put("m3u8Url","/admin/videos/"+item.getFilePath()+"/index.m3u8");
            map.put("resolution",item.getWidth()+"×"+item.getHeight());
            map.put("bitrate",item.getBitrate()+"kbps");
            qualities.add(map);
            playListInfoVo.setDuration(item.getDuration());
        });
        playListInfoVo.setQualities(qualities);
        return getSuccessResponseVO(playListInfoVo);
    }
}
