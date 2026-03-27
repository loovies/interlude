package com.interlude.web.entity.dto;

import javax.validation.constraints.Size;

/**
 * 当前用户资料修改请求。
 */
public class UpdateUserInfoRequestDto {

    @Size(max = 255, message = "avatar is too long")
    private String avatar;

    @Size(max = 50, message = "nickName is too long")
    private String nickName;

    private Integer sex;

    @Size(max = 10, message = "birthday format is invalid")
    private String birthday;

    @Size(max = 32, message = "phone is too long")
    private String phone;

    @Size(max = 255, message = "address is too long")
    private String address;

    @Size(max = 255, message = "school is too long")
    private String school;

    @Size(max = 1000, message = "personIntroduction is too long")
    private String personIntroduction;

    private Integer theme;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPersonIntroduction() {
        return personIntroduction;
    }

    public void setPersonIntroduction(String personIntroduction) {
        this.personIntroduction = personIntroduction;
    }

    public Integer getTheme() {
        return theme;
    }

    public void setTheme(Integer theme) {
        this.theme = theme;
    }
}
