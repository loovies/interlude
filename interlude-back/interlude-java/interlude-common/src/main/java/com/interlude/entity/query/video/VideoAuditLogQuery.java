package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:审核日志表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoAuditLogQuery extends BaseParam {
	/**
	 * 
	 */
	private Long logId;

	/**
	 * 
	 */
	private Long videoId;

	/**
	 * 老状态 “1:待处理”、“2:已批准”、“3:已拒绝”、“4:已取消”
	 */
	private Integer oldStatus;

	/**
	 * 新状态 “1:待处理”、“2:已批准”、“3:已拒绝”、“4:已取消”
	 */
	private Integer newStatus;

	/**
	 * 审计员编号
	 */
	private String auditorId;

	/**
	 * 行动评论
	 */
	private String actionComment;

	private String actionCommentFuzzy;

	/**
	 * 
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getLogId() {
		return this.logId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

	public void setOldStatus(Integer oldStatus) {
		this.oldStatus = oldStatus;
	}

	public Integer getOldStatus() {
		return this.oldStatus;
	}

	public void setNewStatus(Integer newStatus) {
		this.newStatus = newStatus;
	}

	public Integer getNewStatus() {
		return this.newStatus;
	}

	public String getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(String auditorId) {
		this.auditorId = auditorId;
	}

	public void setActionComment(String actionComment) {
		this.actionComment = actionComment;
	}

	public String getActionComment() {
		return this.actionComment;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setActionCommentFuzzy(String actionCommentFuzzy) {
		this.actionCommentFuzzy = actionCommentFuzzy;
	}

	public String getActionCommentFuzzy() {
		return this.actionCommentFuzzy;
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