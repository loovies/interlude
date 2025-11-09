package com.interlude.enums;

public enum FileStatusEnum {

    UPLOAD(1, "上传"),
    UPLOADED(2, "已上传"),
    TRANSCODING(3, "转码中"),
    READY(4, "准备就绪"),
    FAILED(5, "失败"),
    DELETED(6, "已删除");

    private Integer status;
    private String desc;

    FileStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    // 用于根据给定的状态值查找对应的枚举常量。这种设计方便在代码中根据状态值快速获取到对应的枚举常量，从而进行状态的判断和处理
    public static FileStatusEnum getByStatus(Integer status) {
        for (FileStatusEnum item : FileStatusEnum.values()) {
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
