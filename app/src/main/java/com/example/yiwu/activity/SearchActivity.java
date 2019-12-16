package com.example.yiwu.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.adapter.HistorySearchAdapter;
import com.example.yiwu.adapter.HotSearchAdapter;
import com.example.yiwu.customView.ClearEditText;
import com.example.yiwu.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朱旭辉
 * Time  2019/09/07
 * Describe: 搜索界面
 *
 * 一开始使用的是greendao数据库.对于之前没有没有搜索的数据,添加是没有问题的
 * 对于之前存在的数据,不会出现重复添加,重复的数据点击后可以放在最前面,但后面的顺序有些错乱
 * 所以想到使用集合.集合和字符串之间的相互转化,然后用sp存起来.方法是有效的
 * 设计这个界面的目的是为了练习数据库.但还是有问题.
 * 这段时间太忙了,等抽空再次使用greendao尝试一些
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener, View.OnKeyListener{

    private ClearEditText clearEditText;
    private RecyclerView hotSearchRecyclerView;//热搜
    private RecyclerView historySearchRecyclerView;//历史记录

    //文件名
    private String historyFile = "historyData";
    //储存的key值
    private String getHistoryKey = "HistoryKey";
    //数据
    private List<String> historySearchListData;
    private List<String> hotSearchListData = new ArrayList<>();


    private HotSearchAdapter hotSearchAdapter;
    private HistorySearchAdapter historySearchAdapter;

    private ImageButton search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initDate() {
        getHistoryData();//得到历史记录


        getHotSearchData();//得到热门搜索

    }
    @Override
    protected void initView() {
        clearEditText = findViewById(R.id.edittext_search);
        clearEditText.setOnKeyListener(this);
        Log.d(TAG, "initView: clearEditText123"+clearEditText);
        hotSearchRecyclerView = findViewById(R.id.hot_search_ry);
        historySearchRecyclerView = findViewById(R.id.history_search_ry);
        search = findViewById(R.id.gosearch);
        search.setOnClickListener(this);
        setHotSearchData();//设置热门搜索
        setHistorySearchData();//设置历史记录
    }
    private void setHotSearchData() {
        hotSearchAdapter = new HotSearchAdapter(hotSearchListData);
        hotSearchRecyclerView.setAdapter(hotSearchAdapter);
        hotSearchRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        hotSearchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String content = (String) adapter.getData().get(position);
//                Log.d(TAG, "onItemClick: content="+content);
                //操作数据库数据
                SearchListActivity.actionStart(SearchActivity.this,content);
                doData(content);
            }
        });

    }

    private void getHistoryData() {
        String historyStr = PreferencesUtils.getOneString(this, historyFile,getHistoryKey);
        if (historyStr != null) {
            historySearchListData = JSONObject.parseArray(historyStr,String.class);
            if (historySearchListData == null){
                historySearchListData = new ArrayList<>();
            }
        }
    }
    /**
     * 初始化历史搜索相关的适配器及其信息
     */
    private void setHistorySearchData() {
        historySearchAdapter = new HistorySearchAdapter(historySearchListData);
        historySearchRecyclerView.setAdapter(historySearchAdapter);
        historySearchRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        historySearchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String content = (String) adapter.getData().get(position);
                SearchListActivity.actionStart(SearchActivity.this,content);
                //操作数据库数据
                doData(content);

            }
        });
    }

    /**
     * 操作数据库数据
     */
    private void doData(String content) {
        //刷新adapter
        RefreshAdapter(content);
        //历史记录存储到本地
        StoreLocally(content);
    }
    /**
     * 历史记录存储到本地
     * */
    private void StoreLocally(String content) {

        String historyStr = JSON.toJSONString(historySearchListData);
        PreferencesUtils.putOneString(this,historyFile,getHistoryKey,historyStr);

        //跳转到搜索结果界面

    }

    /**
     * 刷新adapter
     */
    private void RefreshAdapter(String content) {
        //有历史数据
        int position = -1;
//        Log.d(TAG, "RefreshAdapter: "+hotSearchListData);
//        Log.d(TAG, "RefreshAdapter: "+historySearchListData);
        //有历史数据
        if (historySearchListData != null && historySearchListData.size() > 0) {
            for (int i = 0; i < historySearchListData.size(); i++) {
                if (content.equals(historySearchListData.get(i))) {
                    //有重复的
                    position = i;
                }
            }

            if (position != -1) {//有重复的，删掉重复的，在添加
                historySearchListData.remove(position);
                historySearchListData.add(0, content);
            } else {//没有重复的就直接添加
                historySearchListData.add(0, content);
            }

        } else {

            historySearchListData.add(content);
        }

        historySearchAdapter.notifyDataSetChanged();
    }


    /**
     * 热门搜索
     */
    private void getHotSearchData() {
        hotSearchListData.add("高等数学");
        hotSearchListData.add("机器学习");
        hotSearchListData.add("嵌入式");
        hotSearchListData.add("数据库");
        hotSearchListData.add("计网");
    }



    @Override
    protected int getContentResourseId() {
        return R.layout.activity_search;
    }

    /**
     * 启动活动
     * @param context
     */
    public static void actionStart(Context context){
        Intent intent = new Intent(context,SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gosearch:
                SearchListActivity.actionStart(this,clearEditText.getText().toString());
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        switch (view.getId()){
            case R.id.edittext_search:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                            || keyCode == KeyEvent.KEYCODE_ENTER) {

                        String str = clearEditText.getText().toString();
                        if (TextUtils.isEmpty(str))
                            return false;
                        SearchListActivity.actionStart(YiWuApplication.getContext(),clearEditText.getText().toString());
                        return true;
                    }
                }
                break;
        }
        return false;
    }
}
