package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;


/**
 * @Description:视频统计表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoStats implements Serializable {
	/**
	 * 视频ID
	 */
	private Long videoId;

	/**
	 * 播放次数
	 */
	private Long playCount;

	/**
	 * 喜欢数量
	 */
	private Integer likeCount;

	/**
	 * 弹幕数量
	 */
	private Integer danmuCount;

	/**
	 * 评论数量
	 */
	private Integer commentCount;

	/**
	 * 收藏数量
	 */
	private Integer collectCount;

	/**
	 * 分享数量
	 */
	private Integer shareCount;

	/**
	 * 不感兴趣数量
	 */
	private Integer unconcernCount;

	/**
	 * 上次播放时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastPlayTime;

	/**
	 * 热度分数
	 */
	private Integer hotScore;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

	public void setPlayCount(Long playCount) {
		this.playCount = playCount;
	}

	public Long getPlayCount() {
		return this.playCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getLikeCount() {
		return this.likeCount;
	}

	public void setDanmuCount(Integer danmuCount) {
		this.danmuCount = danmuCount;
	}

	public Integer getDanmuCount() {
		return this.danmuCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getCommentCount() {
		return this.commentCount;
	}

	public void setCollectCount(Integer collectCount) {
		this.collectCount = collectCount;
	}

	public Integer getCollectCount() {
		return this.collectCount;
	}

	public void setShareCount(Integer shareCount) {
		this.shareCount = shareCount;
	}

	public Integer getShareCount() {
		return this.shareCount;
	}

	public void setUnconcernCount(Integer unconcernCount) {
		this.unconcernCount = unconcernCount;
	}

	public Integer getUnconcernCount() {
		return this.unconcernCount;
	}

	public void setLastPlayTime(Date lastPlayTime) {
		this.lastPlayTime = lastPlayTime;
	}

	public Date getLastPlayTime() {
		return this.lastPlayTime;
	}

	public void setHotScore(Integer hotScore) {
		this.hotScore = hotScore;
	}

	public Integer getHotScore() {
		return this.hotScore;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	@Override
	public String toString() {
		return "视频ID:" + (videoId == null ? "空" : videoId) + ",播放次数:" + (playCount == null ? "空" : playCount) + ",喜欢数量:" + (likeCount == null ? "空" : likeCount) + ",弹幕数量:" + (danmuCount == null ? "空" : danmuCount) + ",评论数量:" + (commentCount == null ? "空" : commentCount) + ",收藏数量:" + (collectCount == null ? "空" : collectCount) + ",分享数量:" + (shareCount == null ? "空" : shareCount) + ",不感兴趣数量:" + (unconcernCount == null ? "空" : unconcernCount) + ",上次播放时间:" + (lastPlayTime == null ? "空" : DateUtils.format(lastPlayTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",热度分数:" + (hotScore == null ? "空" : hotScore) + ",:" + (updateTime == null ? "空" : DateUtils.format(updateTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}