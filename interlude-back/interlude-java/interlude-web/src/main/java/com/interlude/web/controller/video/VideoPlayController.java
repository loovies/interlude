package com.interlude.web.controller.video;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.video.VideoPlayFinishDto;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.entity.vo.video.PlayListInfoVo;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.entity.vo.video.WebVideoDetailVO;
import com.interlude.web.service.video.WebVideoQueryService;
import com.interlude.web.service.video.WebVideoUserCenterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单视频播放与详情接口。
 */
@RestController
@RequestMapping("/video/play")
public class VideoPlayController extends WebBaseController {

    @Resource
    private WebVideoQueryService webVideoQueryService;

    @Resource
    private WebVideoUserCenterService webVideoUserCenterService;

    /**
     * 获取视频详情。
     */
    @GetMapping("/{videoId}")
    public ResponseVO<WebVideoDetailVO> detail(@PathVariable("videoId") Long videoId) {
        return getSuccessResponseVO(webVideoQueryService.getVideoDetail(videoId, getLoginUserId()));
    }

    /**
     * 获取播放器清晰度与播放地址列表。
     */
    @GetMapping("/{videoId}/playlist")
    public ResponseVO<PlayListInfoVo> playList(@PathVariable("videoId") Long videoId) {
        return getSuccessResponseVO(webVideoQueryService.getPlayList(videoId, getLoginUserId()));
    }

    /**
     * 获取“接下来播放”相关推荐。
     */
    @GetMapping("/{videoId}/related")
    public ResponseVO<List<WebVideoCardVO>> related(@PathVariable("videoId") Long videoId,
                                                    @RequestParam(value = "limit", required = false) Integer limit) {
        return getSuccessResponseVO(webVideoQueryService.getRelatedVideos(videoId, limit, getLoginUserId()));
    }

    /**
     * 上报一次完整播放（用于播放次数与观看历史）。
     */
    @PostMapping("/finish")
    public ResponseVO<Boolean> finish(@RequestBody VideoPlayFinishDto request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        String userId = tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
        webVideoUserCenterService.reportPlayFinished(
                request == null ? null : request.getVideoId(),
                userId,
                request == null ? null : request.getWatchDuration(),
                request == null ? null : request.getLastWatchTimeOffset(),
                request == null ? null : request.getCompleteWatch()
        );
        return getSuccessResponseVO(Boolean.TRUE);
    }

    /**
     * 播放开始后即写入观看历史（不增加播放次数）。
     */
    @PostMapping("/history")
    public ResponseVO<Boolean> history(@RequestBody VideoPlayFinishDto request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        String userId = tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
        webVideoUserCenterService.reportPlayStarted(
                request == null ? null : request.getVideoId(),
                userId,
                request == null ? null : request.getWatchDuration(),
                request == null ? null : request.getLastWatchTimeOffset()
        );
        return getSuccessResponseVO(Boolean.TRUE);
    }

    private String getLoginUserId() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        return tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
    }
}
