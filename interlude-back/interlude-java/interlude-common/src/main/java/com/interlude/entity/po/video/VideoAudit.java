package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:视频审核表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoAudit implements Serializable {
	/**
	 * 
	 */
	private Long auditId;

	/**
	 * 正式视频ID
	 */
	private Long videoId;

	/**
	 * 
	 */
	private String userId;

	/**
	 * “1:待处理”“2:已批准”“3:已拒绝”“4:已取消”
	 */
	private Integer auditStatus;

	/**
	 * 提交时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date submitTime;

	/**
	 * 审核时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date auditTime;

	/**
	 * 审核员ID
	 */
	private String auditorId;

	/**
	 * 审核意见
	 */
	private String auditComment;

	/**
	 * 拒绝原因分类 “1:版权”、“2:内容”、“3:质量”、“4:其他”
	 */
	private Integer rejectReason;

	/**
	 * 重试审核次数
	 */
	private Integer retryCount;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	public Long getAuditId() {
		return this.auditId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Integer getAuditStatus() {
		return this.auditStatus;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public Date getSubmitTime() {
		return this.submitTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Date getAuditTime() {
		return this.auditTime;
	}

	public String getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(String auditorId) {
		this.auditorId = auditorId;
	}

	public void setAuditComment(String auditComment) {
		this.auditComment = auditComment;
	}

	public String getAuditComment() {
		return this.auditComment;
	}

	public void setRejectReason(Integer rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Integer getRejectReason() {
		return this.rejectReason;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public Integer getRetryCount() {
		return this.retryCount;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	@Override
	public String toString() {
		return ":" + (auditId == null ? "空" : auditId) + ",正式视频ID:" + (videoId == null ? "空" : videoId) + ",:" + (userId == null ? "空" : userId) + ",“1:待处理”“2:已批准”“3:已拒绝”“4:已取消”:" + (auditStatus == null ? "空" : auditStatus) + ",提交时间:" + (submitTime == null ? "空" : DateUtils.format(submitTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",审核时间:" + (auditTime == null ? "空" : DateUtils.format(auditTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",审核员ID:" + (auditorId == null ? "空" : auditorId) + ",审核意见:" + (auditComment == null ? "空" : auditComment) + ",拒绝原因分类 “1:版权”、“2:内容”、“3:质量”、“4:其他”:" + (rejectReason == null ? "空" : rejectReason) + ",重试审核次数:" + (retryCount == null ? "空" : retryCount) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}