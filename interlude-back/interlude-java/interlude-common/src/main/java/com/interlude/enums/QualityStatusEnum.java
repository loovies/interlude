package com.interlude.enums;

public enum QualityStatusEnum {

    LD(1, "360p"),
    SD(2, "480p"),
    HD(3, "720p"),
    FHD(4, "1080p"),
    T2K(5, "2K");

    private Integer status;
    private String desc;

    QualityStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    // 用于根据给定的状态值查找对应的枚举常量。这种设计方便在代码中根据状态值快速获取到对应的枚举常量，从而进行状态的判断和处理
    public static QualityStatusEnum getByStatus(Integer status) {
        for (QualityStatusEnum item : QualityStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }
    public static QualityStatusEnum getByDesc(String desc) {
        for (QualityStatusEnum item : QualityStatusEnum.values()) {
            if (item.getDesc().equals(desc)) {
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
