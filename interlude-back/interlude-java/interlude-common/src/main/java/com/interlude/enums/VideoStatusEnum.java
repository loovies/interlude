package com.interlude.enums;

public enum VideoStatusEnum {

    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已离线"),
    DELETED(3, "已删除");

    private Integer status;
    private String desc;

    VideoStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    // 用于根据给定的状态值查找对应的枚举常量。这种设计方便在代码中根据状态值快速获取到对应的枚举常量，从而进行状态的判断和处理
    public static VideoStatusEnum getByStatus(Integer status) {
        for (VideoStatusEnum item : VideoStatusEnum.values()) {
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
