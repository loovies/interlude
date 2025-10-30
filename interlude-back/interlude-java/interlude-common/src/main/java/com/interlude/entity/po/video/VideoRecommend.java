package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;


/**
 * @Description:视频推荐表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoRecommend implements Serializable {
	/**
	 * 
	 */
	private Long id;

	/**
	 * 视频ID
	 */
	private Long videoId;

	/**
	 * 推荐类型（如：0-首页推荐，1-热门推荐等）
	 */
	private Integer recommendType;

	/**
	 * 推荐权重，越大越靠前
	 */
	private Integer recommendWeight;

	/**
	 * 推荐开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/**
	 * 推荐结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/**
	 * 操作员ID
	 */
	private Long operatorId;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

	public void setRecommendType(Integer recommendType) {
		this.recommendType = recommendType;
	}

	public Integer getRecommendType() {
		return this.recommendType;
	}

	public void setRecommendWeight(Integer recommendWeight) {
		this.recommendWeight = recommendWeight;
	}

	public Integer getRecommendWeight() {
		return this.recommendWeight;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public Long getOperatorId() {
		return this.operatorId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	@Override
	public String toString() {
		return ":" + (id == null ? "空" : id) + ",视频ID:" + (videoId == null ? "空" : videoId) + ",推荐类型（如：0-首页推荐，1-热门推荐等）:" + (recommendType == null ? "空" : recommendType) + ",推荐权重，越大越靠前:" + (recommendWeight == null ? "空" : recommendWeight) + ",推荐开始时间:" + (startTime == null ? "空" : DateUtils.format(startTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",推荐结束时间:" + (endTime == null ? "空" : DateUtils.format(endTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",操作员ID:" + (operatorId == null ? "空" : operatorId) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (updateTime == null ? "空" : DateUtils.format(updateTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}