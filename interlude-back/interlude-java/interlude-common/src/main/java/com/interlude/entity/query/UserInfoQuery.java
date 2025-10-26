package com.interlude.entity.query;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;


/**
 * @Description:用户信息表
查询对象
 * @auther:dazhi
 * @date:2025/10/14
 */
public class UserInfoQuery<T> extends BaseParam{
	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;

	/**
	 * 头像
	 */
	private String avatar;

	private String avatarFuzzy;

	/**
	 * 昵称
	 */
	private String nickName;

	private String nickNameFuzzy;

	/**
	 * 密码
	 */
	private String password;

	private String passwordFuzzy;

	/**
	 * 0: 女 1: 男 2: 未知
	 */
	private Integer sex;

	/**
	 * 生日
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Size(max = 10, message = "生日格式不正确")
	private String birthday;

	private String birthdayFuzzy;

	/**
	 * 手机号
	 */
	private String phone;

	private String phoneFuzzy;

	/**
	 * 地址
	 */
	private String address;

	private String addressFuzzy;

	/**
	 * 学校
	 */
	private String school;

	private String schoolFuzzy;

	/**
	 * 个人简介
	 */
	private String personIntroduction;

	private String personIntroductionFuzzy;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 更新时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;

	/**
	 * 密码更新时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date pwdResetTime;

	private String pwdResetTimeStart;

	private String pwdResetTimeEnd;

	/**
	 * 主题
	 */
	private Integer theme;

	/**
	 * 创建者
	 */
	private String createBy;

	private String createByFuzzy;

	/**
	 * 更新者
	 */
	private String updateBy;

	private String updateByFuzzy;

	/**
	 * 是否启用 0 : 否 1: 是
	 */
	private Integer enabled;

	private String account;

	private ArrayList<Date> createTimeArray;

	private ArrayList<String> createTimeFormatArray;

	private ArrayList<Integer> roleName;

	private Long roleNameIndex;

	private String isCreatTimeDesc;

	public String getIsCreatTimeDesc() {
		return isCreatTimeDesc;
	}

	public void setIsCreatTimeDesc(String isCreatTimeDesc) {
		this.isCreatTimeDesc = isCreatTimeDesc;
	}

	public Long getRoleNameIndex() {
		return roleNameIndex;
	}

	public void setRoleNameIndex(Long roleNameIndex) {
		this.roleNameIndex = roleNameIndex;
	}

	public ArrayList<Date> getCreateTimeArray() {
		return createTimeArray;
	}

	public void setCreateTimeArray(ArrayList<Date> createTimeArray) {
		this.createTimeArray = createTimeArray;
	}

	public ArrayList<String> getCreateTimeFormatArray() {
		return createTimeFormatArray;
	}

	public void setCreateTimeFormatArray(ArrayList<String> createTimeFormatArray) {
		this.createTimeFormatArray = createTimeFormatArray;
	}



	public ArrayList<Integer> getRoleName() {
		return roleName;
	}

	public void setRoleName(ArrayList<Integer> roleName) {
		this.roleName = roleName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return this.userIdFuzzy;
	}

	public void setEmailFuzzy(String emailFuzzy) {
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy() {
		return this.emailFuzzy;
	}

	public void setAvatarFuzzy(String avatarFuzzy) {
		this.avatarFuzzy = avatarFuzzy;
	}

	public String getAvatarFuzzy() {
		return this.avatarFuzzy;
	}

	public void setNickNameFuzzy(String nickNameFuzzy) {
		this.nickNameFuzzy = nickNameFuzzy;
	}

	public String getNickNameFuzzy() {
		return this.nickNameFuzzy;
	}

	public void setPasswordFuzzy(String passwordFuzzy) {
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy() {
		return this.passwordFuzzy;
	}

	public void setBirthdayFuzzy(String birthdayFuzzy) {
		this.birthdayFuzzy = birthdayFuzzy;
	}

	public String getBirthdayFuzzy() {
		return this.birthdayFuzzy;
	}

	public void setPhoneFuzzy(String phoneFuzzy) {
		this.phoneFuzzy = phoneFuzzy;
	}

	public String getPhoneFuzzy() {
		return this.phoneFuzzy;
	}

	public void setAddressFuzzy(String addressFuzzy) {
		this.addressFuzzy = addressFuzzy;
	}

	public String getAddressFuzzy() {
		return this.addressFuzzy;
	}

	public void setSchoolFuzzy(String schoolFuzzy) {
		this.schoolFuzzy = schoolFuzzy;
	}

	public String getSchoolFuzzy() {
		return this.schoolFuzzy;
	}

	public void setPersonIntroductionFuzzy(String personIntroductionFuzzy) {
		this.personIntroductionFuzzy = personIntroductionFuzzy;
	}

	public String getPersonIntroductionFuzzy() {
		return this.personIntroductionFuzzy;
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

	public void setPwdResetTimeStart(String pwdResetTimeStart) {
		this.pwdResetTimeStart = pwdResetTimeStart;
	}

	public String getPwdResetTimeStart() {
		return this.pwdResetTimeStart;
	}

	public void setPwdResetTimeEnd(String pwdResetTimeEnd) {
		this.pwdResetTimeEnd = pwdResetTimeEnd;
	}

	public String getPwdResetTimeEnd() {
		return this.pwdResetTimeEnd;
	}

	public void setCreateByFuzzy(String createByFuzzy) {
		this.createByFuzzy = createByFuzzy;
	}

	public String getCreateByFuzzy() {
		return this.createByFuzzy;
	}

	public void setUpdateByFuzzy(String updateByFuzzy) {
		this.updateByFuzzy = updateByFuzzy;
	}

	public String getUpdateByFuzzy() {
		return this.updateByFuzzy;
	}

}