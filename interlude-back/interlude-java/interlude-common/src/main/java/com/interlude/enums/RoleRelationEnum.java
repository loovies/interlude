package com.interlude.enums;

public enum RoleRelationEnum {
    USER(1,"普通用户"),
    ADMIN(2,"管理员"),
    SUPERADMIN(3,"超级管理员");

    private int status;
    private String desc;

    RoleRelationEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static RoleRelationEnum getByStatus(int status) {
        for (RoleRelationEnum roleRelationEnum : RoleRelationEnum.values()) {
            if (roleRelationEnum.getStatus() == status) {
                return roleRelationEnum;
            }
        }
        return null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
