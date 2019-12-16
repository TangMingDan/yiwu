package com.example.yiwu.util;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by 朱旭辉
 * Time  2019/09/07
 * Describe: 存储工具类
 */
public class PreferencesUtils {
    /**
     *
     * @param context 上下文
     * @param file_name 文件名
     * @param key 键
     * @param value 值
     */
    public static void putOneString(Context context, String file_name,String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(file_name,Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
    public static String getOneString(Context context, String fileName,String key) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context
                .MODE_PRIVATE);
        return preferences.getString(key,"");
    }
}
