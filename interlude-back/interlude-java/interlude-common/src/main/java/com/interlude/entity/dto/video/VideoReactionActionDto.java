package com.interlude.entity.dto.video;

import java.io.Serializable;

/**
 * 请求点赞/收藏操作的入参
 */
public class VideoReactionActionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long videoId;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }
}
