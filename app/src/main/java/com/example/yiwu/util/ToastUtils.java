package com.example.yiwu.util;

import android.content.Context;
import android.widget.Toast;

import com.example.yiwu.YiWuApplication;

/**
 * Created by 朱旭辉
 * Time  2019/09/07
 * Describe: 土司工具类
 */
public class ToastUtils {
    public static void ToastShortTime(String value){
        Toast.makeText(YiWuApplication.getContext(),value,Toast.LENGTH_SHORT).show();
    }
    public static void ToastLongTime(String value){
        Toast.makeText(YiWuApplication.getContext(),value,Toast.LENGTH_LONG).show();
    }
}
