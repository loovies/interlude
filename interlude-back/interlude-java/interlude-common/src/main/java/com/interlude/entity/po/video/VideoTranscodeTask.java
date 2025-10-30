package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:转码任务表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoTranscodeTask implements Serializable {
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

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

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

	@Override
	public String toString() {
		return "任务ID:" + (taskId == null ? "空" : taskId) + ",视频ID:" + (videoId == null ? "空" : videoId) + ",用户ID:" + (userId == null ? "空" : userId) + ",源文件ID:" + (sourceFileId == null ? "空" : sourceFileId) + ",目标清晰度:" + (targetQuality == null ? "空" : targetQuality) + ",“1:待处理”“2:处理中”“3:成功”“4:失败”:" + (taskStatus == null ? "空" : taskStatus) + ",进度0-100:" + (progress == null ? "空" : progress) + ",错误信息:" + (errorMessage == null ? "空" : errorMessage) + ",:" + (startTime == null ? "空" : DateUtils.format(startTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (endTime == null ? "空" : DateUtils.format(endTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}