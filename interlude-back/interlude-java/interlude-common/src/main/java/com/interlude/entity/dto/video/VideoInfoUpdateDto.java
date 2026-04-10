package com.interlude.entity.dto.video;

import java.io.Serializable;

/**
 * DTO for updating video info from admin side.
 */
public class VideoInfoUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long videoId;

    private String videoName;

    private String description;

    private Integer pCategoryId;

    private Integer categoryId;

    private Integer videoType;

    private Integer visibility;

    private String tags;

    /**
     * 互动设置（0/1等逗号分隔）
     */
    private String interactionSettings;

    /**
     * 封面地址
     */
    private String videoCover;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getpCategoryId() {
        return pCategoryId;
    }

    public void setpCategoryId(Integer pCategoryId) {
        this.pCategoryId = pCategoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getVideoType() {
        return videoType;
    }

    public void setVideoType(Integer videoType) {
        this.videoType = videoType;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getInteractionSettings() {
        return interactionSettings;
    }

    public void setInteractionSettings(String interactionSettings) {
        this.interactionSettings = interactionSettings;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }
}
