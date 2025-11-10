package com.interlude.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * JSON工具类
 */
public class JsonUtils {
    public static String convertObj2Json(Object obj){
        if(null == obj){
            return null;
        }

        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static <T> List<T> convertJsonArray2List(String json, Class<T> clazz){
        return JSONArray.parseArray(json, clazz);
    }
}
