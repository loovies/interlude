package com.interlude.entity.constants;

public class Constants {

    public static final Integer REDIS_TIME_ONE_SECOND = 1000;
    public static final Integer REDIS_TIME_ONE_MINUTE = 60 * 1000;
    public static final Integer REDIS_TIME_ONE_HOUR = 60 * 60 * 1000;
    public static final Integer REDIS_TIME_ONE_DAY = 24 * 60 * 60 * 1000;

    public static final String REDIS_PREFIX_NAME = "interlude:";
    public static final String REDIS_CHECKCODE_PREFIX = REDIS_PREFIX_NAME + "checkcode:";

    // 分类
    public static final String REDIS_KEY_CATEGORY_LIST = REDIS_PREFIX_NAME+ "category:list:";

    // 数字
    public static final Integer NUMBER_10 = 10;
    public static final Integer NUMBER_15 = 15;
    public static final Integer NUMBER_ZERO = 0;

    // Admin
    public static final String REDIS_ADMIN_TOKEN = "adminToken";
    public static final String REDIS_TOKEN_ADMIN_KEY = REDIS_PREFIX_NAME +  "token:admin:";

    // 地址
    public static final String FILE_FOLDER="file/";
    public static final String FILE_VIDEO="video/";
    public static final String FILE_COVER="cover/";
    public static final String FILE_FOLDER_TEMP="temp/";
    public static final String FILE_AVATAR = "avatar/";

    // 文件名
    public static final String TEMP_VIDEO_NAME = "/temp.mp4";
    public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";
    public static final String MP4_SUFFIX = ".mp4";
    public static final String TS_NAME = "index.ts";
    public static final String M3U8_NAME = "index.m3u8";


    // 系统
    public static final String REDIS_SYS_SETTING_KEY = REDIS_PREFIX_NAME + "setting:";
    public static final String REDIS_KEY_UPLOADING_FILE = REDIS_PREFIX_NAME + "uploading:";
    public static final String REDIS_KEY_QUEUE_TRANSFER = REDIS_PREFIX_NAME + "queue:transfer:";

    // 存储单位
    public static final Long MB_SIZE = 1024 * 1024L;
}
