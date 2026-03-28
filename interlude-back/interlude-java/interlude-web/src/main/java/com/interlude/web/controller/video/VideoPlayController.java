package com.interlude.web.controller.video;

import com.interlude.entity.vo.ResponseVO;
import com.interlude.entity.vo.video.PlayListInfoVo;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.entity.vo.video.WebVideoDetailVO;
import com.interlude.web.service.video.WebVideoQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 获取视频详情。
     */
    @GetMapping("/{videoId}")
    public ResponseVO<WebVideoDetailVO> detail(@PathVariable("videoId") Long videoId) {
        return getSuccessResponseVO(webVideoQueryService.getVideoDetail(videoId));
    }

    /**
     * 获取播放器清晰度与播放地址列表。
     */
    @GetMapping("/{videoId}/playlist")
    public ResponseVO<PlayListInfoVo> playList(@PathVariable("videoId") Long videoId) {
        return getSuccessResponseVO(webVideoQueryService.getPlayList(videoId));
    }

    /**
     * 获取“接下来播放”相关推荐。
     */
    @GetMapping("/{videoId}/related")
    public ResponseVO<List<WebVideoCardVO>> related(@PathVariable("videoId") Long videoId,
                                                    @RequestParam(value = "limit", required = false) Integer limit) {
        return getSuccessResponseVO(webVideoQueryService.getRelatedVideos(videoId, limit));
    }
}
