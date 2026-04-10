package com.interlude.service.video;

import com.interlude.entity.po.video.VideoReaction;
import com.interlude.entity.query.video.VideoReactionQuery;
import com.interlude.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * Service for video reactions.
 */
public interface VideoReactionService {

    List<VideoReaction> findListByParam(VideoReactionQuery param);

    Integer findCountByParam(VideoReactionQuery param);

    PaginationResultVO<VideoReaction> findListByPage(VideoReactionQuery param);

    Integer add(VideoReaction bean);

    Integer addBatch(List<VideoReaction> listBean);

    Integer addOrUpdateBatch(List<VideoReaction> listBean);

    VideoReaction getVideoReactionByReactionId(Long reactionId);

    Integer updateVideoReactionByReactionId(VideoReaction bean, Long reactionId);

    Integer deleteVideoReactionByReactionId(Long reactionId);

    VideoReaction getByVideoUserAndType(Long videoId, String userId, String reactionType);

    Integer deleteByVideoUserAndType(Long videoId, String userId, String reactionType);
}
