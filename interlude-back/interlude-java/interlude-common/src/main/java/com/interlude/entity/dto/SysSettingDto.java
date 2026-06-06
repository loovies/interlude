package com.interlude.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer videoSize = 2000;
    private Integer videoPCount = 100;
    private Integer videoCount = 100;
    private Integer commentCount = 50;
    private Integer danmuCount = 50;

    private String registerEMailTitle = "邮箱验证码";

    private String registerEmailContent = "您好, 您的邮箱验证码是, %s, 15分钟有效";

    public String getRegisterEMailTitle() {
        return registerEMailTitle;
    }

    public void setRegisterEMailTitle(String registerEMailTitle) {
        this.registerEMailTitle = registerEMailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public Integer getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(Integer videoSize) {
        this.videoSize = videoSize;
    }

    public Integer getVideoPCount() {
        return videoPCount;
    }

    public void setVideoPCount(Integer videoPCount) {
        this.videoPCount = videoPCount;
    }

    public Integer getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount) {
        this.videoCount = videoCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getDanmuCount() {
        return danmuCount;
    }

    public void setDanmuCount(Integer danmuCount) {
        this.danmuCount = danmuCount;
    }
}
