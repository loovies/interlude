package com.interlude.entity.dto;

import java.io.Serializable;

/**
 * 前台用户资料更新对象，只暴露允许用户自己修改的字段。
 */
public class UserWebInfoUpdateDto implements Serializable {
    private String avatar;
    private String nickName;
    private Integer sex;
    private String birthday;
    private String phone;
    private String address;
    private String school;
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
