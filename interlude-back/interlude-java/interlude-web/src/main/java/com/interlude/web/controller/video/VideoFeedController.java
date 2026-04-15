package com.interlude.web.controller.video;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.service.video.WebVideoQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频信息流接口（首页推荐、最新、热门、分类）。
 */
@RestController
@RequestMapping("/video/feed")
public class VideoFeedController extends WebBaseController {

    @Resource
    private WebVideoQueryService webVideoQueryService;

    /**
     * 首页推荐流。
     */
    @GetMapping("/recommend")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> recommend(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getRecommendFeed(pageNo, pageSize, getLoginUserId()));
    }

    /**
     * 闅忔満鎾斁娴併€?
     */
    @GetMapping("/random")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> random(
            @RequestParam(value = "seedVideoId", required = false) Long seedVideoId,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getRandomFeed(seedVideoId, pageNo, pageSize, getLoginUserId()));
    }

    /**
     * 最新发布流。
     */
    @GetMapping("/latest")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> latest(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getLatestFeed(pageNo, pageSize, getLoginUserId()));
    }

    /**
     * 热门流（按热度分）。
     */
    @GetMapping("/hot")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> hot(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getHotFeed(pageNo, pageSize, getLoginUserId()));
    }

    /**
     * 分类流。
     */
    @GetMapping("/category")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> byCategory(
            @RequestParam(value = "pCategoryId", required = false) Integer pCategoryId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.getCategoryFeed(pCategoryId, categoryId, pageNo, pageSize, getLoginUserId()));
    }

    private String getLoginUserId() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        return tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
    }
}
