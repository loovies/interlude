package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:视频审核表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoAuditQuery extends BaseParam {
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
	private Long userId;

	/**
	 * “1:待处理”“2:已批准”“3:已拒绝”“4:已取消”
	 */
	private Integer auditStatus;

	/**
	 * 提交时间
	 */
	private Date submitTime;

	private String submitTimeStart;

	private String submitTimeEnd;

	/**
	 * 审核时间
	 */
	private Date auditTime;

	private String auditTimeStart;

	private String auditTimeEnd;

	/**
	 * 审核员ID
	 */
	private Long auditorId;

	/**
	 * 审核意见
	 */
	private String auditComment;

	private String auditCommentFuzzy;

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
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

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

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
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

	public void setAuditorId(Long auditorId) {
		this.auditorId = auditorId;
	}

	public Long getAuditorId() {
		return this.auditorId;
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

	public void setSubmitTimeStart(String submitTimeStart) {
		this.submitTimeStart = submitTimeStart;
	}

	public String getSubmitTimeStart() {
		return this.submitTimeStart;
	}

	public void setSubmitTimeEnd(String submitTimeEnd) {
		this.submitTimeEnd = submitTimeEnd;
	}

	public String getSubmitTimeEnd() {
		return this.submitTimeEnd;
	}

	public void setAuditTimeStart(String auditTimeStart) {
		this.auditTimeStart = auditTimeStart;
	}

	public String getAuditTimeStart() {
		return this.auditTimeStart;
	}

	public void setAuditTimeEnd(String auditTimeEnd) {
		this.auditTimeEnd = auditTimeEnd;
	}

	public String getAuditTimeEnd() {
		return this.auditTimeEnd;
	}

	public void setAuditCommentFuzzy(String auditCommentFuzzy) {
		this.auditCommentFuzzy = auditCommentFuzzy;
	}

	public String getAuditCommentFuzzy() {
		return this.auditCommentFuzzy;
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