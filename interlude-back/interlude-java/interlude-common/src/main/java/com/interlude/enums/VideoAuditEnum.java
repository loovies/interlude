package com.interlude.enums;

public enum VideoAuditEnum {

    PENDING(1, "待处理"),
    APPROVED(2, "已批准"),
    REJECTED(3, "已拒绝"),
    CANCELLED(4, "已取消");

    private Integer status;
    private String desc;

    VideoAuditEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    // 用于根据给定的状态值查找对应的枚举常量。这种设计方便在代码中根据状态值快速获取到对应的枚举常量，从而进行状态的判断和处理
    public static VideoAuditEnum getByStatus(Integer status) {
        for (VideoAuditEnum item : VideoAuditEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
