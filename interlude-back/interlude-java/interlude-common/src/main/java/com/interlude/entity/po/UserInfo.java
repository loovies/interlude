package com.interlude.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:用户信息表

 * @auther:dazhi
 * @date:2025/10/14
 */
public class UserInfo implements Serializable {
	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 0: 女 1: 男 2: 未知
	 */
	private Integer sex;

	/**
	 * 生日
	 */
	private String birthday;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 学校
	 */
	private String school;

	/**
	 * 个人简介
	 */
	private String personIntroduction;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	/**
	 * 密码更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date pwdResetTime;

	/**
	 * 主题
	 */
	private Integer theme;

	/**
	 * 创建者
	 */
	private String createBy;

	/**
	 * 更新者
	 */
	private String updateBy;

	/**
	 * 是否启用 0 : 否 1: 是
	 */
	private Integer enabled;
	private Long roleId;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getSex() {
		return this.sex;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthday() {
		return this.birthday;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return this.address;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getSchool() {
		return this.school;
	}

	public void setPersonIntroduction(String personIntroduction) {
		this.personIntroduction = personIntroduction;
	}

	public String getPersonIntroduction() {
		return this.personIntroduction;
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

	public void setPwdResetTime(Date pwdResetTime) {
		this.pwdResetTime = pwdResetTime;
	}

	public Date getPwdResetTime() {
		return this.pwdResetTime;
	}

	public void setTheme(Integer theme) {
		this.theme = theme;
	}

	public Integer getTheme() {
		return this.theme;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateBy() {
		return this.createBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getUpdateBy() {
		return this.updateBy;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public Integer getEnabled() {
		return this.enabled;
	}

	@Override
	public String toString() {
		return "用户ID:" + (userId == null ? "空" : userId) + ",邮箱:" + (email == null ? "空" : email) + ",头像:" + (avatar == null ? "空" : avatar) + ",昵称:" + (nickName == null ? "空" : nickName) + ",密码:" + (password == null ? "空" : password) + ",0: 女 1: 男 2: 未知:" + (sex == null ? "空" : sex) + ",生日:" + (birthday == null ? "空" : birthday) + ",手机号:" + (phone == null ? "空" : phone) + ",地址:" + (address == null ? "空" : address) + ",学校:" + (school == null ? "空" : school) + ",个人简介:" + (personIntroduction == null ? "空" : personIntroduction) + ",创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",更新时间:" + (updateTime == null ? "空" : DateUtils.format(updateTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",密码更新时间:" + (pwdResetTime == null ? "空" : DateUtils.format(pwdResetTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",主题:" + (theme == null ? "空" : theme) + ",创建者:" + (createBy == null ? "空" : createBy) + ",更新者:" + (updateBy == null ? "空" : updateBy) + ",是否启用 0 : 否 1: 是:" + (enabled == null ? "空" : enabled);
	}
}