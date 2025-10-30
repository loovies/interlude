package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;


/**
 * @Description:审核日志表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoAuditLog implements Serializable {
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
	private Long auditorId;

	/**
	 * 行动评论
	 */
	private String actionComment;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

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

	public void setAuditorId(Long auditorId) {
		this.auditorId = auditorId;
	}

	public Long getAuditorId() {
		return this.auditorId;
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

	@Override
	public String toString() {
		return ":" + (logId == null ? "空" : logId) + ",:" + (videoId == null ? "空" : videoId) + ",老状态 “1:待处理”、“2:已批准”、“3:已拒绝”、“4:已取消”:" + (oldStatus == null ? "空" : oldStatus) + ",新状态 “1:待处理”、“2:已批准”、“3:已拒绝”、“4:已取消”:" + (newStatus == null ? "空" : newStatus) + ",审计员编号:" + (auditorId == null ? "空" : auditorId) + ",行动评论:" + (actionComment == null ? "空" : actionComment) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}