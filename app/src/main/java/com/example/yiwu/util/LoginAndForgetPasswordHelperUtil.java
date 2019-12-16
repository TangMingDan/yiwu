package com.example.yiwu.util;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 朱旭辉
 * Time  2019/10/04
 * Describe:注册帮助Util
 */
public class LoginAndForgetPasswordHelperUtil {

    private static final String TAG = LoginAndForgetPasswordHelperUtil.class.getSimpleName();

    /**
     * 手机号输入是否正确
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        if (!phoneNumber.matches("^[0-9]*$")) {
            Log.i(TAG, "isPhoneNumber: match error--"+phoneNumber);
            return false;
        }
        if (phoneNumber.length() != 11) {
            Log.i(TAG, "isPhoneNumber: length error--"+phoneNumber);
            return false;
        }

        return true;
    }
    /**
     * 密码输入是否正确
     * @param passwordNumber
     * @return
     */
    public static boolean isPasswordNumber(String passwordNumber) {
        if (TextUtils.isEmpty(passwordNumber)) {
            return false;
        }
        if (passwordNumber.length() < 6) {
            Log.i(TAG, "isPhoneNumber: length error--"+passwordNumber);
            return false;
        }

        return true;
    }
    /**
     * 邮箱输入是否正确
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(email);
        if (m.matches())
            return true;
        else
            return false;
    }

}
