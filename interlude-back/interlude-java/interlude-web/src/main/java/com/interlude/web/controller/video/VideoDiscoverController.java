package com.interlude.web.controller.video;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.utils.StringTools;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.entity.vo.video.WebVideoCategoryVO;
import com.interlude.web.service.video.WebVideoQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
        return getSuccessResponseVO(webVideoQueryService.search(keyword, pageNo, pageSize, getLoginUserId()));
    }

    /**
     * 获取前端分类 Tab 使用的分类树。
     */
    @GetMapping("/categories")
    public ResponseVO<List<WebVideoCategoryVO>> categories() {
        return getSuccessResponseVO(webVideoQueryService.getCategoryTree());
    }

    /**
     * 根据视频 ID 获取封面文件。
     */
    @GetMapping("/{videoId}/cover")
    public void cover(@PathVariable("videoId") Long videoId, HttpServletResponse response) throws Exception {
        String coverPath = webVideoQueryService.getVideoCover(videoId, getLoginUserId());
        if (StringTools.isEmpty(coverPath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (coverPath.startsWith("http://") || coverPath.startsWith("https://")) {
            response.sendRedirect(coverPath);
            return;
        }

        response.setContentType(resolveImageContentType(StringTools.getFileSuffix(coverPath)));
        response.setHeader("Cache-Control", "max-age=2592000");

        if (!readFile(response, coverPath)) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private String resolveImageContentType(String suffix) {
        if (StringTools.isEmpty(suffix)) {
            return "application/octet-stream";
        }

        String normalized = suffix.toLowerCase();
        if (".jpg".equals(normalized) || ".jpeg".equals(normalized)) {
            return "image/jpeg";
        }
        if (".png".equals(normalized)) {
            return "image/png";
        }
        if (".webp".equals(normalized)) {
            return "image/webp";
        }
        if (".gif".equals(normalized)) {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    private String getLoginUserId() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        return tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
    }
}
