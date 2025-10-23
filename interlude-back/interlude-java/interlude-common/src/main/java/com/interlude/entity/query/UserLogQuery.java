package com.interlude.entity.query;

import java.util.Date;


/**
 * @Description:用户日志表查询对象
 * @auther:dazhi
 * @date:2025/10/14
 */
public class UserLogQuery extends BaseParam{
	/**
	 * 日志id
	 */
	private Integer logId;

	/**
	 * 描述
	 */
	private String description;

	private String descriptionFuzzy;

	/**
	 * 日志类型
	 */
	private String logType;

	private String logTypeFuzzy;

	/**
	 * 输出方法
	 */
	private String method;

	private String methodFuzzy;

	/**
	 * 用户Id
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * ip地址
	 */
	private String lastLoginIp;

	private String lastLoginIpFuzzy;

	/**
	 * 时间
	 */
	private Long time;

	/**
	 * 地址
	 */
	private String address;

	private String addressFuzzy;

	/**
	 * 浏览器
	 */
	private String browser;

	private String browserFuzzy;

	/**
	 * 报错信息
	 */
	private String exceptionDetail;

	private String exceptionDetailFuzzy;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

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

	public void setDescriptionFuzzy(String descriptionFuzzy) {
		this.descriptionFuzzy = descriptionFuzzy;
	}

	public String getDescriptionFuzzy() {
		return this.descriptionFuzzy;
	}

	public void setLogTypeFuzzy(String logTypeFuzzy) {
		this.logTypeFuzzy = logTypeFuzzy;
	}

	public String getLogTypeFuzzy() {
		return this.logTypeFuzzy;
	}

	public void setMethodFuzzy(String methodFuzzy) {
		this.methodFuzzy = methodFuzzy;
	}

	public String getMethodFuzzy() {
		return this.methodFuzzy;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return this.userIdFuzzy;
	}

	public void setLastLoginIpFuzzy(String lastLoginIpFuzzy) {
		this.lastLoginIpFuzzy = lastLoginIpFuzzy;
	}

	public String getLastLoginIpFuzzy() {
		return this.lastLoginIpFuzzy;
	}

	public void setAddressFuzzy(String addressFuzzy) {
		this.addressFuzzy = addressFuzzy;
	}

	public String getAddressFuzzy() {
		return this.addressFuzzy;
	}

	public void setBrowserFuzzy(String browserFuzzy) {
		this.browserFuzzy = browserFuzzy;
	}

	public String getBrowserFuzzy() {
		return this.browserFuzzy;
	}

	public void setExceptionDetailFuzzy(String exceptionDetailFuzzy) {
		this.exceptionDetailFuzzy = exceptionDetailFuzzy;
	}

	public String getExceptionDetailFuzzy() {
		return this.exceptionDetailFuzzy;
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