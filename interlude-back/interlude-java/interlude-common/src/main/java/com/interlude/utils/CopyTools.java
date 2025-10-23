package com.interlude.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class CopyTools {

    // 将 任意类型 转换为 指定的 classz
    public static <T,S>  T copy(S s, Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
        // 复制操作中如果 T 类中 没有 S 类中的字段值, 虽然会执行复制操作,
        // 但是在传输到的前端的是时候,或者是调用的时候还是S类中的值,没有添加额外的值
        BeanUtils.copyProperties(s, t);
        return t;
    }

    public static <T,S> List<T> copyList(List<S> sList, Class<T> classz){
        List<T> list = new ArrayList<T>();
        for (S s : sList) {
            T t = null;
            try {
                t = classz.newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
            // 复制操作中如果 T 类中 没有 S 类中的字段值, 虽然会执行复制操作,
            // 但是在传输到的前端的是时候,或者是调用的时候还是S类中的值,没有添加额外的值
            BeanUtils.copyProperties(s,t);
            list.add(t);
        }
        return list;
    }
}
