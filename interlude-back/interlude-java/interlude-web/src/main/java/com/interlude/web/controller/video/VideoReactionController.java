package com.interlude.web.controller.video;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.video.VideoReactionActionDto;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.entity.vo.video.VideoReactionStatusVO;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.VideoReactionTypeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.service.video.WebVideoReactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * Controller for like & collect actions.
 */
@RestController
@RequestMapping("/video/reaction")
public class VideoReactionController extends WebBaseController {

    @Resource
    private WebVideoReactionService webVideoReactionService;

    @GetMapping("/status")
    public ResponseVO<VideoReactionStatusVO> status(@RequestParam("videoId") Long videoId) {
        String userId = null;
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        if (tokenUserInfoDto != null) {
            userId = tokenUserInfoDto.getUserId();
        }
        return getSuccessResponseVO(webVideoReactionService.getReactionStatus(videoId, userId));
    }

    @PostMapping("/like")
    public ResponseVO<VideoReactionStatusVO> toggleLike(@RequestBody VideoReactionActionDto request) {
        Long videoId = requireVideoId(request);
        String userId = requireLoginUserId();
        return getSuccessResponseVO(webVideoReactionService.toggleReaction(videoId, userId, VideoReactionTypeEnum.LIKE));
    }

    @PostMapping("/collect")
    public ResponseVO<VideoReactionStatusVO> toggleCollect(@RequestBody VideoReactionActionDto request) {
        Long videoId = requireVideoId(request);
        String userId = requireLoginUserId();
        return getSuccessResponseVO(webVideoReactionService.toggleReaction(videoId, userId, VideoReactionTypeEnum.COLLECT));
    }

    @PostMapping("/share")
    public ResponseVO<VideoReactionStatusVO> toggleShare(@RequestBody VideoReactionActionDto request) {
        Long videoId = requireVideoId(request);
        String userId = requireLoginUserId();
        return getSuccessResponseVO(webVideoReactionService.toggleReaction(videoId, userId, VideoReactionTypeEnum.SHARE));
    }

    private Long requireVideoId(VideoReactionActionDto request) {
        if (request == null || request.getVideoId() == null || request.getVideoId() <= 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        return request.getVideoId();
    }

    private String requireLoginUserId() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        if (tokenUserInfoDto == null || tokenUserInfoDto.getUserId() == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return tokenUserInfoDto.getUserId();
    }
}
