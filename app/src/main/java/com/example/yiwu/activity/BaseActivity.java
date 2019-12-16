package com.example.yiwu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yiwu.util.ActivityCollector;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:基础活动，所有活动继承它
 */

public abstract  class BaseActivity extends AppCompatActivity {

    protected static final String TAG = BaseActivity.class.getSimpleName();
    private static volatile Activity mCurrentActivity;//获取当前的Activity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResourseId());
        initDate();
        initView();
        ActivityCollector.addActivity(this);//加入管理栈
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);//退出管理栈
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentActivity(this);
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    private void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    protected abstract void initDate();//初始化数据
    protected abstract void initView();//初始化控件
    protected abstract int getContentResourseId();//得到布局
}
