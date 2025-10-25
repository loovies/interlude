package com.interlude.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class StringTools {

    /**
     * 生成随机数
     *
     * @param count
     * @return
     */
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }


    /**
     * 判断是否为空, 为空返回true
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static String encodeByMd5(String orginString){
        return isEmpty(orginString) ? null: DigestUtils.md5Hex(orginString);
    }

    public static boolean pathIsOk(String path){
        if(StringTools.isEmpty(path)){
            return true;
        }
        if(path.contains("../") || path.contains("..\\")){
            return false;
        }
        return true;// 判断文件路径是否非法,阻止基本的路径遍历攻击
    }

    public static String rename(String fileName){
        // 拆分文件名和后缀名
        String fileNameReal = getFileNameNoSuffix(fileName);
        String suffix = getFileSuffix(fileName);

        // 中间加了5个随机字符
        return fileNameReal+"_"+getRandomString(5) + suffix;
    }

    // 只拿文件名
    // 判断文件是否有后缀名, 如果文件没有后缀名就直接返回文件名, 如果有后缀就 只取后缀名前的文件名
    public static String getFileNameNoSuffix(String fileName){
        Integer index = fileName.lastIndexOf(".");
        if(index == -1){
            return fileName;
        }
        fileName = fileName.substring(0,index);
        return fileName;
    }

    // 只拿后缀
    public static String getFileSuffix(String fileName){
        Integer index = fileName.lastIndexOf(".");
        if(index == -1){
            return "";
        }
        fileName = fileName.substring(index);
        return fileName;
    }
}
