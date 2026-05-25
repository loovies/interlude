package com.interlude.web.controller.video;

import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.service.video.WebVideoQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 排行榜接口（热度、播放、点赞）。
 */
@RestController
@RequestMapping("/video/rank")
public class VideoRankController extends WebBaseController {

    @Resource
    private WebVideoQueryService webVideoQueryService;

    /**
     * 热度榜（按 hot_score）。
     */
    @GetMapping("/hot")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> hot(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getRankFeed("hot", pageNo, pageSize));
    }

    /**
     * 播放榜（按播放量）。
     */
    @GetMapping("/play")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> play(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getRankFeed("play", pageNo, pageSize));
    }

    /**
     * 点赞榜（按点赞量）。
     */
    @GetMapping("/like")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> like(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getRankFeed("like", pageNo, pageSize));
    }
}
