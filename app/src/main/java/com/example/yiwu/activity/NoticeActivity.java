package com.example.yiwu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.yiwu.Info.CheckInfo;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.adapter.CheckGoodsAdapter;
import com.example.yiwu.customView.OrdinaryTitle;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.internal.schedulers.NewThreadWorker;

/**
 * Created by 朱鸿
 * Time  2019/9/15
 * Describe:通知
 * 修改：增加审核功能 by朱旭辉 2019/12/12
 */
public class NoticeActivity extends BaseActivity {
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView noticeRecyclerView;
    private OrdinaryTitle ordinaryTitle;
    private TextView noticeContent;
    private User user;
    private String response;
    private List<CheckInfo> checkInfosUser = new ArrayList<>();
    private List<CheckInfo> checkInfosManager = new ArrayList<>();
    private List<CheckInfo> checkInfos = new ArrayList<>();
    private int NO_GOODS = 0;
    private AlertDialog alertDialog;
    private static CheckGoodsAdapter checkGoodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void showDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setCancelable(false)
                .setView(R.layout.dialog_load)//加载中
                .show();
    }

    @Override
    protected void initDate() {
        user = YiWuApplication.getInstance().getUser();
        getData();
    }

    private void getMangerData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (user.getUs_id().equals("19")) {
                    //是管理员
                    OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id", user.getUs_id())
                            .build();
                    response = okHttpUtils.SendPostRequst(requestBody, HttpContants.MANAGER_CHECKGOODS);
                    checkInfosManager = JSONObject.parseArray(response, CheckInfo.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showData();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showData();
                        }
                    });
                }
            }
        }).start();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", user.getUs_id())
                        .build();
                response = okHttpUtils.SendPostRequst(requestBody, HttpContants.CHECK_PASSORNOT);
                checkInfosUser = JSONObject.parseArray(response, CheckInfo.class);
                getMangerData();//获取需要审核的数据
            }
        }).start();
    }

    private void showData() {
        //不能再此处写runONUI
        checkInfos.addAll(checkInfosManager);
        checkInfos.addAll(checkInfosUser);
        if (checkInfos.size() == NO_GOODS) {//没有物品
            smartRefreshLayout.setVisibility(View.GONE);
        } else {//有物品
            noticeContent.setVisibility(View.GONE);
            checkGoodsAdapter = new CheckGoodsAdapter(checkInfos, this);
            noticeRecyclerView.setLayoutManager(new LinearLayoutManager(YiWuApplication.getContext()));
            noticeRecyclerView.setAdapter(checkGoodsAdapter);
        }
        alertDialog.dismiss();
    }


    @Override
    protected void initView() {
        ordinaryTitle = findViewById(R.id.notice_title);
        ordinaryTitle.setTitleName("通知");
        noticeContent = findViewById(R.id.txt_no);
        noticeContent.setText("暂无通知");
        smartRefreshLayout = findViewById(R.id.notice_smartrefresh);
        noticeRecyclerView = findViewById(R.id.notice_recyclerview);
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

    public static void adapterNotifyDataSetChanged() {
        checkGoodsAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_notice;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NoticeActivity.class);
        context.startActivity(intent);
    }

}
