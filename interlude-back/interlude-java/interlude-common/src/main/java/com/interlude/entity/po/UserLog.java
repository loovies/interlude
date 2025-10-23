package com.interlude.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:用户日志表
 * @auther:dazhi
 * @date:2025/10/14
 */
public class UserLog implements Serializable {
	/**
	 * 日志id
	 */
	private Integer logId;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 日志类型
	 */
	private String logType;

	/**
	 * 输出方法
	 */
	private String method;

	/**
	 * 用户Id
	 */
	private String userId;

	/**
	 * ip地址
	 */
	private String lastLoginIp;

	/**
	 * 时间
	 */
	private Long time;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 浏览器
	 */
	private String browser;

	/**
	 * 报错信息
	 */
	private String exceptionDetail;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getLogId() {
		return this.logId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogType() {
		return this.logType;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return this.method;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getLastLoginIp() {
		return this.lastLoginIp;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getTime() {
		return this.time;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return this.address;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowser() {
		return this.browser;
	}

	public void setExceptionDetail(String exceptionDetail) {
		this.exceptionDetail = exceptionDetail;
	}

	public String getExceptionDetail() {
		return this.exceptionDetail;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	@Override
	public String toString() {
		return "日志id:" + (logId == null ? "空" : logId) + ",描述:" + (description == null ? "空" : description) + ",日志类型:" + (logType == null ? "空" : logType) + ",输出方法:" + (method == null ? "空" : method) + ",用户Id:" + (userId == null ? "空" : userId) + ",ip地址:" + (lastLoginIp == null ? "空" : lastLoginIp) + ",时间:" + (time == null ? "空" : time) + ",地址:" + (address == null ? "空" : address) + ",浏览器:" + (browser == null ? "空" : browser) + ",报错信息:" + (exceptionDetail == null ? "空" : exceptionDetail) + ",创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}