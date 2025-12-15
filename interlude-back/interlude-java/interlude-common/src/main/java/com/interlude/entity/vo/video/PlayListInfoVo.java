package com.interlude.entity.vo.video;

import java.util.List;
import java.util.Map;

public class PlayListInfoVo {
    private Long videoId;
    private String title;
    private Integer duration;
    private List<Map<String,Object>> qualities;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Map<String, Object>> getQualities() {
        return qualities;
    }

    public void setQualities(List<Map<String, Object>> qualities) {
        this.qualities = qualities;
    }
}
