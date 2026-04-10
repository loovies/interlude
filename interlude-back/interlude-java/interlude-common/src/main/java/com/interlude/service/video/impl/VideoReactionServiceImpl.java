package com.interlude.service.video.impl;

import com.interlude.entity.po.video.VideoReaction;
import com.interlude.entity.query.SimplePage;
import com.interlude.entity.query.video.VideoReactionQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.enums.PageSize;
import com.interlude.mapper.video.VideoReactionMapper;
import com.interlude.service.video.VideoReactionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Implementation for video reactions service.
 */
@Service("videoReactionService")
public class VideoReactionServiceImpl implements VideoReactionService {

    @Resource
    private VideoReactionMapper<VideoReaction, VideoReactionQuery> videoReactionMapper;

    @Override
    public List<VideoReaction> findListByParam(VideoReactionQuery param) {
        return this.videoReactionMapper.selectList(param);
    }

    @Override
    public Integer findCountByParam(VideoReactionQuery param) {
        return this.videoReactionMapper.selectCount(param);
    }

    @Override
    public PaginationResultVO<VideoReaction> findListByPage(VideoReactionQuery param) {
        Integer count = this.findCountByParam(param);
        Integer pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();
        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoReaction> list = this.findListByParam(param);
        return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    @Override
    public Integer add(VideoReaction bean) {
        return this.videoReactionMapper.insert(bean);
    }

    @Override
    public Integer addBatch(List<VideoReaction> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoReactionMapper.insertBatch(listBean);
    }

    @Override
    public Integer addOrUpdateBatch(List<VideoReaction> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoReactionMapper.insertOrUpdateBatch(listBean);
    }

    @Override
    public VideoReaction getVideoReactionByReactionId(Long reactionId) {
        return this.videoReactionMapper.selectByReactionId(reactionId);
    }

    @Override
    public Integer updateVideoReactionByReactionId(VideoReaction bean, Long reactionId) {
        return this.videoReactionMapper.updateByReactionId(bean, reactionId);
    }

    @Override
    public Integer deleteVideoReactionByReactionId(Long reactionId) {
        return this.videoReactionMapper.deleteByReactionId(reactionId);
    }

    @Override
    public VideoReaction getByVideoUserAndType(Long videoId, String userId, String reactionType) {
        return this.videoReactionMapper.selectByVideoUserAndType(videoId, userId, reactionType);
    }

    @Override
    public Integer deleteByVideoUserAndType(Long videoId, String userId, String reactionType) {
        return this.videoReactionMapper.deleteByVideoUserAndType(videoId, userId, reactionType);
    }
}
