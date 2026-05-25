package com.interlude.web.service.video;

import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.po.video.VideoReaction;
import com.interlude.entity.po.video.VideoStats;
import com.interlude.entity.vo.video.VideoReactionStatusVO;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.VideoReactionTypeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.video.VideoReactionService;
import com.interlude.service.video.VideoStatsService;
import com.interlude.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * Web-side service for handling like/collect/share reactions.
 */
@Service
public class WebVideoReactionService {

    private static final String REACTION_STATUS_ACTIVE = "active";
    private static final String REACTION_STATUS_CANCELLED = "cancelled";

    @Resource
    private WebVideoQueryService webVideoQueryService;

    @Resource
    private VideoStatsService videoStatsService;

    @Resource
    private VideoReactionService videoReactionService;

    public VideoReactionStatusVO getReactionStatus(Long videoId, String userId) {
        ensureVideoAvailable(videoId, userId);
        VideoStats stats = videoStatsService.getVideoStatsByVideoId(videoId);
        VideoReactionStatusVO statusVO = buildStatus(videoId, stats);
        if (!StringTools.isEmpty(userId)) {
            statusVO.setLiked(hasReaction(videoId, userId, VideoReactionTypeEnum.LIKE));
            statusVO.setCollected(hasReaction(videoId, userId, VideoReactionTypeEnum.COLLECT));
            statusVO.setShared(hasReaction(videoId, userId, VideoReactionTypeEnum.SHARE));
        } else {
            statusVO.setLiked(false);
            statusVO.setCollected(false);
            statusVO.setShared(false);
        }
        return statusVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public VideoReactionStatusVO toggleReaction(Long videoId, String userId, VideoReactionTypeEnum type) {
        if (StringTools.isEmpty(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        VideoInfo videoInfo = ensureVideoAvailable(videoId, userId);

        VideoReaction existing = videoReactionService.getByVideoUserAndType(videoId, userId, type.getDbValue());
        if (type == VideoReactionTypeEnum.SHARE) {
            handleShareReaction(existing, videoId, userId, videoInfo);
            return getReactionStatus(videoId, userId);
        }

        if (existing == null) {
            VideoReaction record = new VideoReaction();
            record.setVideoId(videoId);
            record.setUserId(userId);
            record.setAuthorId(videoInfo.getUserId());
            record.setReactionType(type.getDbValue());
            record.setStatus(REACTION_STATUS_ACTIVE);
            videoReactionService.add(record);
            updateStats(videoId, type, 1);
        } else {
            VideoReaction update = new VideoReaction();
            if (REACTION_STATUS_ACTIVE.equalsIgnoreCase(existing.getStatus())) {
                update.setStatus(REACTION_STATUS_CANCELLED);
                videoReactionService.updateVideoReactionByReactionId(update, existing.getReactionId());
                updateStats(videoId, type, -1);
            } else {
                update.setStatus(REACTION_STATUS_ACTIVE);
                videoReactionService.updateVideoReactionByReactionId(update, existing.getReactionId());
                updateStats(videoId, type, 1);
            }
        }
        return getReactionStatus(videoId, userId);
    }

    private void handleShareReaction(VideoReaction existing, Long videoId, String userId, VideoInfo videoInfo) {
        if (existing == null) {
            VideoReaction record = new VideoReaction();
            record.setVideoId(videoId);
            record.setUserId(userId);
            record.setAuthorId(videoInfo.getUserId());
            record.setReactionType(VideoReactionTypeEnum.SHARE.getDbValue());
            record.setStatus(REACTION_STATUS_ACTIVE);
            videoReactionService.add(record);
            updateStats(videoId, VideoReactionTypeEnum.SHARE, 1);
            return;
        }
        if (!REACTION_STATUS_ACTIVE.equalsIgnoreCase(existing.getStatus())) {
            VideoReaction update = new VideoReaction();
            update.setStatus(REACTION_STATUS_ACTIVE);
            videoReactionService.updateVideoReactionByReactionId(update, existing.getReactionId());
        }
        // 分享行为不可取消，每次点击都累计一次分享数
        updateStats(videoId, VideoReactionTypeEnum.SHARE, 1);
    }

    private VideoReactionStatusVO buildStatus(Long videoId, VideoStats stats) {
        VideoReactionStatusVO statusVO = new VideoReactionStatusVO();
        statusVO.setVideoId(videoId);
        statusVO.setLikeCount(stats == null || stats.getLikeCount() == null ? 0 : stats.getLikeCount());
        statusVO.setCollectCount(stats == null || stats.getCollectCount() == null ? 0 : stats.getCollectCount());
        statusVO.setShareCount(stats == null || stats.getShareCount() == null ? 0 : stats.getShareCount());
        return statusVO;
    }

    private void updateStats(Long videoId, VideoReactionTypeEnum type, int delta) {
        Date now = new Date();
        VideoStats stats = videoStatsService.getVideoStatsByVideoId(videoId);
        if (stats == null) {
            stats = new VideoStats();
            stats.setVideoId(videoId);
            stats.setPlayCount(0L);
            stats.setLikeCount(type == VideoReactionTypeEnum.LIKE ? Math.max(0, delta) : 0);
            stats.setCollectCount(type == VideoReactionTypeEnum.COLLECT ? Math.max(0, delta) : 0);
            stats.setShareCount(type == VideoReactionTypeEnum.SHARE ? Math.max(0, delta) : 0);
            stats.setUpdateTime(now);
            videoStatsService.add(stats);
            return;
        }

        VideoStats update = new VideoStats();
        update.setUpdateTime(now);
        if (type == VideoReactionTypeEnum.LIKE) {
            int current = stats.getLikeCount() == null ? 0 : stats.getLikeCount();
            int next = Math.max(0, current + delta);
            update.setLikeCount(next);
        } else if (type == VideoReactionTypeEnum.COLLECT) {
            int current = stats.getCollectCount() == null ? 0 : stats.getCollectCount();
            int next = Math.max(0, current + delta);
            update.setCollectCount(next);
        } else if (type == VideoReactionTypeEnum.SHARE) {
            int current = stats.getShareCount() == null ? 0 : stats.getShareCount();
            int next = Math.max(0, current + delta);
            update.setShareCount(next);
        }
        videoStatsService.updateVideoStatsByVideoId(update, videoId);
    }

    private boolean hasReaction(Long videoId, String userId, VideoReactionTypeEnum type) {
        VideoReaction reaction = videoReactionService.getByVideoUserAndType(videoId, userId, type.getDbValue());
        return reaction != null && REACTION_STATUS_ACTIVE.equalsIgnoreCase(reaction.getStatus());
    }

    private VideoInfo ensureVideoAvailable(Long videoId, String loginUserId) {
        return webVideoQueryService.getAccessibleVideoOrThrow(videoId, loginUserId, true);
    }
}
