package com.example.yiwu.util;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.yiwu.fragment.BaseFragment.TAG;

/**
 * Created by 朱旭辉
 * Time  2019/10/07
 * Describe:活动管理器
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }


    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
