package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:转码任务表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoTranscodeTaskQuery extends BaseParam {
	/**
	 * 任务ID
	 */
	private Long taskId;

	/**
	 * 视频ID
	 */
	private Long videoId;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 源文件ID
	 */
	private Long sourceFileId;

	/**
	 * 目标清晰度
	 */
	private String targetQuality;

	private String targetQualityFuzzy;

	/**
	 * “1:待处理”“2:处理中”“3:成功”“4:失败”
	 */
	private Integer taskStatus;

	/**
	 * 进度0-100
	 */
	private Integer progress;

	/**
	 * 错误信息
	 */
	private String errorMessage;

	private String errorMessageFuzzy;

	/**
	 * 
	 */
	private Date startTime;

	private String startTimeStart;

	private String startTimeEnd;

	/**
	 * 
	 */
	private Date endTime;

	private String endTimeStart;

	private String endTimeEnd;

	/**
	 * 
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getTaskId() {
		return this.taskId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setSourceFileId(Long sourceFileId) {
		this.sourceFileId = sourceFileId;
	}

	public Long getSourceFileId() {
		return this.sourceFileId;
	}

	public void setTargetQuality(String targetQuality) {
		this.targetQuality = targetQuality;
	}

	public String getTargetQuality() {
		return this.targetQuality;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Integer getTaskStatus() {
		return this.taskStatus;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public Integer getProgress() {
		return this.progress;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
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

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setTargetQualityFuzzy(String targetQualityFuzzy) {
		this.targetQualityFuzzy = targetQualityFuzzy;
	}

	public String getTargetQualityFuzzy() {
		return this.targetQualityFuzzy;
	}

	public void setErrorMessageFuzzy(String errorMessageFuzzy) {
		this.errorMessageFuzzy = errorMessageFuzzy;
	}

	public String getErrorMessageFuzzy() {
		return this.errorMessageFuzzy;
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

}