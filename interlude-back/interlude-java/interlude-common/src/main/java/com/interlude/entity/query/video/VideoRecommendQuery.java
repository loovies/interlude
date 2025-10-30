package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:视频推荐表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoRecommendQuery extends BaseParam {
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
	private Date startTime;

	private String startTimeStart;

	private String startTimeEnd;

	/**
	 * 推荐结束时间
	 */
	private Date endTime;

	private String endTimeStart;

	private String endTimeEnd;

	/**
	 * 操作员ID
	 */
	private Long operatorId;

	/**
	 * 
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 
	 */
	private Date updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;

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

	public void setStartTimeStart(String startTimeStart) {
		this.startTimeStart = startTimeStart;
	}

	public String getStartTimeStart() {
		return this.startTimeStart;
	}

	public void setStartTimeEnd(String startTimeEnd) {
		this.startTimeEnd = startTimeEnd;
	}

	public String getStartTimeEnd() {
		return this.startTimeEnd;
	}

	public void setEndTimeStart(String endTimeStart) {
		this.endTimeStart = endTimeStart;
	}

	public String getEndTimeStart() {
		return this.endTimeStart;
	}

	public void setEndTimeEnd(String endTimeEnd) {
		this.endTimeEnd = endTimeEnd;
	}

	public String getEndTimeEnd() {
		return this.endTimeEnd;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return this.createTimeStart;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd() {
		return this.createTimeEnd;
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