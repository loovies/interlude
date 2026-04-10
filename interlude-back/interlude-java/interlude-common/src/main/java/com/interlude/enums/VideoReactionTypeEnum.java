package com.interlude.enums;

/**
 * Video reaction types for like/collect actions.
 */
public enum VideoReactionTypeEnum {
    LIKE(1, "点赞", "like"),
    COLLECT(2, "收藏", "favorite"),
    SHARE(3, "分享", "share");

    private final int code;
    private final String desc;
    private final String dbValue;

    VideoReactionTypeEnum(int code, String desc, String dbValue) {
        this.code = code;
        this.desc = desc;
        this.dbValue = dbValue;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static VideoReactionTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (VideoReactionTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
