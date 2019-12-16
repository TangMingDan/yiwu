package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONObject;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.adapter.MyGoodsAdapter;
import com.example.yiwu.customView.DeleteMyGoodsDialog;
import com.example.yiwu.customView.OrdinaryTitle;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by 朱鸿
 * Time  2019/9/15
 * Describe:我上架的物品
 * 修改：添加了信息
 */
public class MyGoodsActivity extends BaseActivity{
    private SmartRefreshLayout smartRefreshLayout;
    private OrdinaryTitle ordinaryTitle;
    private TextView noticeContent;
    private RecyclerView myGoodsRecyclerView;
    private User user;
    private String respose;
    private List<GoodsInfo> goodsInfoList;
    private int NO_GOODS = 0;
    private static MyGoodsAdapter adapter = null;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void showDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setView(R.layout.dialog_load)//加载中
                .show();
    }
    @Override
    protected void initDate() {
        smartRefreshLayout = findViewById(R.id.my_goods_smartrefresh);
        user = YiWuApplication.getInstance().getUser();
        getData();
    }

    @Override
    protected void initView() {
        ordinaryTitle = findViewById(R.id.my_goods_title);
        ordinaryTitle.setTitleName("我上架的物品");
        noticeContent = findViewById(R.id.txt_no);
        noticeContent.setText("没有物品上架");
        myGoodsRecyclerView = findViewById(R.id.my_goods_recyclerview);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        });
        showDialog();
    }

    private void getData() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id",user.getUs_id())
                        .build();
                respose = okHttpUtils.SendPostRequst(requestBody, HttpContants.GET_MYGOODS_INFO);
                goodsInfoList = JSONObject.parseArray(respose,GoodsInfo.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (goodsInfoList.size()==NO_GOODS){//没有物品
                            smartRefreshLayout.setVisibility(View.GONE);
                        }else {//有物品
                            noticeContent.setVisibility(View.GONE);
                            showData();
                        }
                        alertDialog.dismiss();
                    }
                });

            }
        }).start();
    }
    /**
     * 展示数据
     */
    private void showData() {
        //不能再此处写runONUI
        adapter = new MyGoodsAdapter(goodsInfoList,this);
        final GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myGoodsRecyclerView.setLayoutManager(layoutManager);
                myGoodsRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_my_goods;
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, MyGoodsActivity.class);
        context.startActivity(intent);
    }
    public static void adapterNotifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

}
