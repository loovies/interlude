package com.interlude.enums;

public enum VideoTaskEnum {

    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    SUCCESS(3, "成功"),
    FAILURE(4, "失败");

    private Integer status;
    private String desc;

    VideoTaskEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    // 用于根据给定的状态值查找对应的枚举常量。这种设计方便在代码中根据状态值快速获取到对应的枚举常量，从而进行状态的判断和处理
    public static VideoTaskEnum getByStatus(Integer status) {
        for (VideoTaskEnum item : VideoTaskEnum.values()) {
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
