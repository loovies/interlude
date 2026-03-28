package com.interlude.web.controller.video;

import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.entity.vo.video.WebVideoCategoryVO;
import com.interlude.web.service.video.WebVideoQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 发现页接口（搜索与分类）。
 */
@RestController
@RequestMapping("/video/discover")
public class VideoDiscoverController extends WebBaseController {

    @Resource
    private WebVideoQueryService webVideoQueryService;

    /**
     * 根据关键词搜索视频。
     */
    @GetMapping("/search")
    public ResponseVO<PaginationResultVO<WebVideoCardVO>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return getSuccessResponseVO(webVideoQueryService.search(keyword, pageNo, pageSize));
    }

    /**
     * 获取前端分类 Tab 使用的分类树。
     */
    @GetMapping("/categories")
    public ResponseVO<List<WebVideoCategoryVO>> categories() {
        return getSuccessResponseVO(webVideoQueryService.getCategoryTree());
    }
}
