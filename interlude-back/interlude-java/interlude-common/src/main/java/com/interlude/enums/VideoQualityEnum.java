package com.interlude.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 视频清晰度枚举
 * 使用枚举管理所有清晰度配置，确保类型安全
 */
public enum VideoQualityEnum {

    // 枚举实例定义
    QUALITY_360P("360p", 640, 360, 800, "流畅"),
    QUALITY_480P("480p", 854, 480, 1200, "标清"),
    QUALITY_720P("720p", 1280, 720, 2500, "高清"),
    QUALITY_1080P("1080p", 1920, 1080, 5000, "超清"),
    QUALITY_2K("2K", 2560, 1440, 8000, "2K");
//    QUALITY_4K("4K", 3840, 2160, 15000, "4K");

    // 枚举字段
    private final String name;        // 清晰度名称
    private final int width;          // 视频宽度
    private final int height;         // 视频高度
    private final int bitrate;        // 码率 (kbps)
    private final String description; // 清晰度描述

    /**
     * 枚举构造函数
     */
    VideoQualityEnum(String name, int width, int height, int bitrate, String description) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.description = description;
    }

    // Getter 方法
    public String getName() { return name; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getBitrate() { return bitrate; }
    public String getDescription() { return description; }

    /**
     * 获取文件夹名称（小写）
     */
    public String getFolderName() {
        return name.toLowerCase();
    }

    /**
     * 获取码率（bps格式）
     */
    public int getBitrateBps() {
        return bitrate * 1000;
    }

    // 静态工具方法

    /**
     * 根据名称获取清晰度枚举
     */
    public static VideoQualityEnum getByName(String name) {
        for (VideoQualityEnum quality : values()) {
            if (quality.name.equalsIgnoreCase(name)) {
                return quality;
            }
        }
        // 如果没有找到，返回默认清晰度
        return getDefault();
    }

    /**
     * 根据编码获取清晰度枚举
     */
    public static VideoQualityEnum getByCode(int code) {
        VideoQualityEnum[] values = values();
        if (code >= 0 && code < values.length) {
            return values[code];
        }
        return getDefault();
    }

    /**
     * 获取默认清晰度（推荐720p作为平衡选择）
     */
    public static VideoQualityEnum getDefault() {
        return QUALITY_720P;
    }

    /**
     * 获取所有支持的清晰度列表
     */
    public static List<VideoQualityEnum> getSupportedQualities() {
        return Arrays.asList(values());
    }

    public static List<VideoQualityEnum> getMainstreamQualities() {
        return Arrays.asList(QUALITY_480P, QUALITY_720P, QUALITY_1080P);
    }

    /**
     * 获取基础清晰度列表（360p, 480p, 720p）
     */
    public static List<VideoQualityEnum> getBasicQualities() {
        return Arrays.asList(QUALITY_360P, QUALITY_480P, QUALITY_720P);
    }

    /**
     * 获取高清清晰度列表（720p, 1080p）
     */
    public static List<VideoQualityEnum> getHDQualities() {
        return Arrays.asList(QUALITY_720P, QUALITY_1080P);
    }

    /**
     * 获取超高清清晰度列表（1080p, 2K, 4K）
     */
    public static List<VideoQualityEnum> getUHDQualities() {
//        return Arrays.asList(QUALITY_1080P, QUALITY_2K, QUALITY_4K);
        return Arrays.asList(QUALITY_1080P, QUALITY_2K);
    }

    /**
     * 根据设备屏幕分辨率推荐合适的清晰度
     */
    public static VideoQualityEnum recommendForScreen(int screenWidth, int screenHeight) {
        int maxDimension = Math.max(screenWidth, screenHeight);

        if (maxDimension <= 480) {
            return QUALITY_360P;
        } else if (maxDimension <= 720) {
            return QUALITY_480P;
        } else if (maxDimension <= 1080) {
            return QUALITY_720P;
        } else if (maxDimension <= 1440) {
            return QUALITY_1080P;
        }else{
            return QUALITY_2K;
        }
//        } else if (maxDimension <= 2160) {
//            return QUALITY_2K;
//        } else {
//            return QUALITY_4K;
//        }
    }

    /**
     * 根据网络带宽推荐合适的清晰度
     */
    public static VideoQualityEnum recommendForBandwidth(int bandwidthKbps) {
        if (bandwidthKbps < 500) {
            return QUALITY_360P;
        } else if (bandwidthKbps < 1000) {
            return QUALITY_480P;
        } else if (bandwidthKbps < 2000) {
            return QUALITY_720P;
        } else if (bandwidthKbps < 4000) {
            return QUALITY_1080P;
        }else{
            return QUALITY_2K;
        }
//        } else if (bandwidthKbps < 8000) {
//            return QUALITY_2K;
//        } else {
//            return QUALITY_4K;
//        }
    }

    @Override
    public String toString() {
        return String.format("%s (%dx%d, %dkbps)", description, width, height, bitrate);
    }
}