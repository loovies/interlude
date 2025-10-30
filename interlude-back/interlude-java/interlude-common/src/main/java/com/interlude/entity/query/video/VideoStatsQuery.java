package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:视频统计表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoStatsQuery extends BaseParam {
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
	private Date lastPlayTime;

	private String lastPlayTimeStart;

	private String lastPlayTimeEnd;

	/**
	 * 热度分数
	 */
	private Integer hotScore;

	/**
	 * 
	 */
	private Date updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;

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

	public void setLastPlayTimeStart(String lastPlayTimeStart) {
		this.lastPlayTimeStart = lastPlayTimeStart;
	}

	public String getLastPlayTimeStart() {
		return this.lastPlayTimeStart;
	}

	public void setLastPlayTimeEnd(String lastPlayTimeEnd) {
		this.lastPlayTimeEnd = lastPlayTimeEnd;
	}

	public String getLastPlayTimeEnd() {
		return this.lastPlayTimeEnd;
	}

	public void setUpdateTimeStart(String updateTimeStart) {
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeStart() {
		return this.updateTimeStart;
	}

	public void setUpdateTimeEnd(String updateTimeEnd) {
		this.updateTimeEnd = updateTimeEnd;
	}

	public String getUpdateTimeEnd() {
		return this.updateTimeEnd;
	}

}