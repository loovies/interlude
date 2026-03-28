package com.interlude.web.entity.vo.video;

/**
 * 单个视频详情页数据模型。
 */
public class WebVideoDetailVO extends WebVideoCardVO {

    private Integer visibility;
    private Integer videoType;
    private String originAuthor;
    private String originUrl;

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Integer getVideoType() {
        return videoType;
    }

    public void setVideoType(Integer videoType) {
        this.videoType = videoType;
    }

    public String getOriginAuthor() {
        return originAuthor;
    }

    public void setOriginAuthor(String originAuthor) {
        this.originAuthor = originAuthor;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }
}
