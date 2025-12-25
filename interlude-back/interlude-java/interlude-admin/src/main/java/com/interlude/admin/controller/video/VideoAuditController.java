package com.interlude.admin.controller.video;

import com.interlude.admin.controller.ABaseController;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.video.VideoAudit;
import com.interlude.entity.po.video.VideoAuditLog;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.video.VideoAuditLogQuery;
import com.interlude.entity.query.video.VideoAuditQuery;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.entity.vo.video.VideoInfoVo;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.VideoAuditEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.CategoryInfoService;
import com.interlude.service.UserInfoService;
import com.interlude.service.video.VideoAuditLogService;
import com.interlude.service.video.VideoAuditService;
import com.interlude.service.video.VideoFileService;
import com.interlude.service.video.VideoInfoService;
import com.interlude.utils.DateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit")
public class VideoAuditController extends ABaseController {

    @Resource
    private VideoAuditService videoAuditService;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoAuditLogService videoAuditLogService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private CategoryInfoService categoryInfoService;


    /**
     * 获取视频审核信息
     * @param videoInfoQuery   模糊查询
     * @return
     */
    @RequestMapping("/loadVideoAuditInfo")
    public ResponseVO loadVideoAudit(VideoInfoQuery videoInfoQuery) {

        ArrayList<Date> createTimeArray = videoInfoQuery.getCreateTimeArray();
        ArrayList<String> createFormatArray = new ArrayList<>();
        // 查询多个创建时间
        if(createTimeArray != null && !createTimeArray.isEmpty()){
            for(Date item : createTimeArray){
                String format = DateUtils.format(item, "yyyy-MM-dd");
                createFormatArray.add(format);
            }
            videoInfoQuery.setCreateTimeFormatArray(createFormatArray);
        }

        if(videoInfoQuery.getIsDescOrAscCreateTime() != null && videoInfoQuery.getIsDescOrAscCreateTime().equals("desc")){
            videoInfoQuery.setOrderBy("create_time desc");
        }else{
            videoInfoQuery.setOrderBy("create_time asc");
        }
        if(!"".equals(videoInfoQuery.getVisibilityArray())&& videoInfoQuery.getVisibilityArray() != null ){
            ArrayList<Integer> vis = Arrays.stream(videoInfoQuery.getVisibilityArray().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toCollection(ArrayList::new));
            videoInfoQuery.setVisibilityArrayList(vis);
        }

        PaginationResultVO<VideoInfo> listByPage = videoInfoService.findListByPage(videoInfoQuery);

        List<VideoInfoVo> infoVos = new ArrayList<>();
        listByPage.getList().stream().forEach(item -> {
            if(item.getStatus() != 0){
                return;
            }
            VideoAudit videoAuditByVideoId = videoAuditService.getVideoAuditByVideoId(item.getVideoId());
            if(videoAuditByVideoId == null){
                return;
            }
            VideoInfoVo vo = new VideoInfoVo();
            UserInfo userInfo = userInfoService.getUserInfoByUserId(item.getUserId());
            if(userInfo == null){
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if(userInfo.getUserId().equals(item.getUserId())){
                vo.setNickName(userInfo.getNickName());
            }

            vo.setTags(item.getTags());
            vo.setDescription(item.getDescription());
            vo.setUserId(item.getUserId());
            vo.setVisibility(item.getVisibility());
            vo.setInteractionArray(item.getInteractionSettings());
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
            vo.setStatus(videoAuditByVideoId.getAuditStatus());
            vo.setPublishTime(DateUtils.format(item.getPublishTime(), DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
            infoVos.add(vo);
        });
        PaginationResultVO<VideoInfo> result = new PaginationResultVO(infoVos);
        result.setTotalCount(infoVos.size());
        result.setPageNo(listByPage.getPageNo());
        result.setPageSize(listByPage.getPageSize());
        result.setPageTotal(listByPage.getPageTotal());
        return getSuccessResponseVO(result);
    }

    /**
     * 更新审核信息
     * @param videoAuditQuery
     * @return
     */
    @RequestMapping("/updateVideoAudit")
    public ResponseVO videoAudit(VideoAuditQuery videoAuditQuery) {

        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();

        // 先判断前端返回的videoId是否存在
        Integer i = videoAuditService.selectVideoIdByExist(videoAuditQuery.getVideoId(),videoAuditQuery.getUserId());
        if(i <= 0){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        videoAuditQuery.setAuditorId(tokenUserInfo.getUserId());
        videoAuditQuery.setAuditTime(new Date());
        if(videoAuditQuery.getAuditStatus() == VideoAuditEnum.APPROVED.getStatus()){
            // 更新 video_audit 和 video_info 里的视频状态, 增加video_audit_log
            videoAuditQuery.setAuditStatus(VideoAuditEnum.APPROVED.getStatus());

            // 判断log表中是否存在,如何存在就获取旧的视频状态,并新增审核log
            VideoAuditLog videoIdByNewLogInfo = videoAuditLogService.getVideoIdByNewLogInfo(videoAuditQuery.getVideoId());
            VideoAuditLogQuery videoAuditLogQuery = new VideoAuditLogQuery();
            videoAuditLogQuery.setVideoId(videoAuditQuery.getVideoId());
            videoAuditLogQuery.setAuditorId(tokenUserInfo.getUserId());
            videoAuditLogQuery.setCreateTime(new Date());
            if(videoIdByNewLogInfo == null){
                videoAuditLogQuery.setOldStatus(0);
                videoAuditLogQuery.setNewStatus(videoAuditQuery.getAuditStatus());
                videoAuditLogQuery.setActionComment("新增视频审核日志成功: 视频状态为:"+ VideoAuditEnum.getByStatus(videoAuditQuery.getAuditStatus()).getDesc());
            }else{
                videoAuditLogQuery.setOldStatus(videoIdByNewLogInfo.getNewStatus());
                videoAuditLogQuery.setNewStatus(videoAuditQuery.getAuditStatus());
                videoAuditLogQuery.setActionComment("修改视频日志成功: 将视频状态从:"+ VideoAuditEnum.getByStatus(videoIdByNewLogInfo.getNewStatus()).getDesc() + "" +
                        "更改为:"+VideoAuditEnum.getByStatus(videoAuditQuery.getAuditStatus()).getDesc());
            }

        }else if (videoAuditQuery.getAuditStatus() == VideoAuditEnum.REJECTED.getStatus()){

        }else {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        return getSuccessResponseVO("顶顶顶顶");
    }
}
