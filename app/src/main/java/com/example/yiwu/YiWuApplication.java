package com.example.yiwu;
/**
 * Created by 朱旭辉
 * Time  2019/09/28
 * Describe: application类，每当应用程序启动的时候，系统就会自动将这个类进行初始化
 */
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.yiwu.Info.User;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.util.LattePreference;
import com.example.yiwu.util.PreferencesUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.adapter.EMAChatRoom;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.util.Iterator;
import java.util.List;

import androidx.annotation.RequiresApi;

public class YiWuApplication extends Application {
    //文件名
    private String UserFile = "userData";
    private String UsetKey = "userInfo";
    private static Context context;
    private User user;
    private static YiWuApplication mInstance;

    public static YiWuApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();
        initUser();
        initEasemob();
        LitePal.initialize(this);
    }

    public  static  Context getContext(){
        return context;
    }

    private void initUser() {
        String userStr = PreferencesUtils.getOneString(this,UserFile,UsetKey);
        if (userStr != null){
            this.user = JSONObject.parseObject(userStr,User.class);
        }

    }
    public User getUser() {
        return user;
    }

    public void putUser(User user) {
        this.user = user;
        String userStr = JSON.toJSONString(user);
        PreferencesUtils.putOneString(this,UserFile,UsetKey,userStr);
    }

    public void clearUser() {
        this.user = null;
        String userStr = "";
        PreferencesUtils.putOneString(this,UserFile,UsetKey,userStr);
    }

    //注册环信
    private void initEasemob(){
        EMOptions options = new EMOptions();
        //添加好友更改为需要验证的
        options.setAcceptInvitationAlways(false);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if(processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())){
            //如果此application:onCreate是被service调用的直接返回
            return;
        }
        EMClient.getInstance().init(this,options);
        EMClient.getInstance().setDebugMode(true);
    }

    private String getAppName(int pID){
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()){
            ActivityManager.RunningAppProcessInfo info =
                    (ActivityManager.RunningAppProcessInfo)(i.next());
            try {
                if(info.pid == pID){
                    processName = info.processName;
                    return processName;
                }
            }catch (Exception e){

            }
        }
        return processName;
    }

    public static Activity getActivity(){
        return BaseActivity.getCurrentActivity();
    }
}
