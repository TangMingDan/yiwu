package com.example.yiwu.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;
import com.example.yiwu.adapter.GoodsInfoRemarkAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朱鸿
 * Time  2019/9/10
 * Describe:商品评价页面

 *
 */
public class GoodInfoRemarkActivity extends BaseActivity {
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<GoodsInfo> mCollections;   //此处GooInfo应该换为每个商品的评价，由于暂时GoodInfo未添加评价，先暂用替代
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_info_remark);
        initDate();
        initView();
    }

    @Override
    protected void initDate() {
        mCollections = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mCollections.add(new GoodsInfo("吃鲨鱼的巨猿，沈石溪精读酷玩系列 全彩升级版 浙江教育出版社"
                    , "15", "15.2"));


        }
    }

    protected void initView(){
        LayoutInflater factorys = LayoutInflater.from(this);
        final View headerView = factorys.inflate(R.layout.header_goodsremark, null); //获取布局

        refreshLayout = findViewById(R.id.remark_refreshLayout);  //上拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }

        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {  //下拉加载
                refreshLayout.finishLoadMore(2000);
            }
        });

        recyclerView = findViewById(R.id.remark_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        GoodsInfoRemarkAdapter adapter = new GoodsInfoRemarkAdapter(mCollections);
        adapter.addHeaderView(headerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_good_info_remark;
    }
    public static void startActivity(Context context){
        Intent intent = new Intent(context, GoodInfoRemarkActivity.class);
        context.startActivity(intent);
    }
}
