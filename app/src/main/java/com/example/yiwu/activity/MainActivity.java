package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.fragment.app.FragmentTabHost;

import com.example.yiwu.R;
import com.example.yiwu.fragment.ClassifyFragment;
import com.example.yiwu.fragment.CollectFragment;
import com.example.yiwu.fragment.HomeFragment;
import com.example.yiwu.fragment.MessageFragment;
import com.example.yiwu.fragment.MineFragment;
import com.example.yiwu.util.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import okhttp3.OkHttpClient;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:主活动
 */

public class MainActivity extends BaseActivity {

    private FragmentTabHost myTabhost;
    private static int mCurrentPosition = 0;

    //加载的Fragment
    private Class mFragment[] = new Class[]{
            HomeFragment.class, ClassifyFragment.class,
            MessageFragment.class, CollectFragment.class,
            MineFragment.class
    };

    //标记
    private String mFragmentTags[] = new String[]{
            "主页", "分享",
            "消息","收藏",
            "我的"
    };

    //Tab图片
    private int mImages[] = new int[]{
            R.drawable.tab_home_selector, R.drawable.tab_classify_selector,
            R.drawable.tab_message_selector, R.drawable.tab_collect_selector,
            R.drawable.tab_mine_selector
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initDate() {

    }

    @Override
    protected int getContentResourseId() {

        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTabHost();
    }

    private void initTabHost() {

        TextView textView = findViewById(R.id.text);
        //Log.d(TAG, "initTabHost: "+textView);
        //View view = getLayoutInflater().inflate(R.layout.activity_main,null);//不加这句tabhost会为null
        myTabhost = findViewById(android.R.id.tabhost);
        //Log.d(TAG, "initTabHost: "+myTabhost);
        myTabhost.setup(this, getSupportFragmentManager(),android.R.id.tabcontent);
        //去掉分割线
        myTabhost.getTabWidget().setDividerDrawable(null);
        for (int i = 0; i < mImages.length; i++) {
            //对Tab按钮添加标记和图片
            TabHost.TabSpec tabSpec = myTabhost.newTabSpec(mFragmentTags[i]).setIndicator(getImageViewAndTextView(i));
            //添加Fragment
            myTabhost.addTab(tabSpec, mFragment[i], null);
            myTabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.white);
        }
        myTabhost.setCurrentTab(mCurrentPosition);

    }

    public static void setCurrentTab(int position){
        mCurrentPosition = position;
    }

    //获取图片资源
    private View getImageViewAndTextView(int index) {
        View view = getLayoutInflater().inflate(R.layout.item_tab, null);
        ImageView tab_imageView = view.findViewById(R.id.item_tab_iv);
        TextView tab_textView = view.findViewById(R.id.item_tab_tv);
        tab_imageView.setImageResource(mImages[index]);//加载图片
        tab_textView.setText(mFragmentTags[index]);//加载标签
        Log.d(TAG, "getImageViewAndTextView: "+view);
        return view;
    }
    public static void actionStart(Context context,String chatId){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("chatId",chatId);
        context.startActivity(intent);
    }

}