package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.adapter.SearchBaseAdapter;
import com.example.yiwu.customView.ClearEditText;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 朱鸿
 * Time  2019/9/8
 * Describe:搜索列表页面
 * 修改：2019/9/12 添加网络请求
 * 修改：2019/9/13 添加查找刷新操作
 * 修改：加载图片，以及搜索按钮的添加
 * 修改：2019/11/21添加通过首页分类直接访问的判断功能
 */
public class SearchListActivity extends BaseActivity implements View.OnKeyListener, View.OnClickListener {
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ImageButton gosearch;
    private List<GoodsInfo> mGoodsInfo;
    //将接收mGoodsInfo2的数据，修改时用add,clear,remove,
    // 因为adeper只能对初始的list进行notifyDataSetChanged操作
    private List<GoodsInfo> mGoodsInfo2;   //用于接收数据
    private String searchStr;//搜索内容
    private SearchBaseAdapter adapter;
    private ClearEditText clearEditText;
//    private ImageButton search;//搜索键
    private AppCompatTextView nothing; //搜索为空的显示界面
    private LinearLayout nothingLayout;
    private final int FIRST_IN = 1;//第一次进入
    private final int OTHER_IN = 2;//其他进入
    private AlertDialog alertDialog;
    private static int flagChoose = -1;  //判断进入路径，搜索进入为1，首页进入为2
    private int tagId; //首页进入的搜索Id
    private Thread getSearchInfoThread = new Thread() {
        @Override
        public void run() {
            Log.d(TAG, "run: "+"thread is start");
            getJsonData(FIRST_IN);//获得json数据
        }
    };

    private void getJsonData(int flag) {
        OkHttpClient client = null;
        RequestBody requestBody;
        Request searchinfo_request = null;
        if(flagChoose == 1){
            client = new OkHttpClient();
            requestBody = new FormBody.Builder()
                    .add("search_info",searchStr)
                    .build();
            Log.d("111111111111111",searchStr);
            searchinfo_request = new Request.Builder()
                    .url(HttpContants.SEARCH_GOODSINFO_URL)
                    .post(requestBody)
                    .build();
        }else if(flagChoose == 2){
            client = new OkHttpClient();
            requestBody = new FormBody.Builder()
                    .add("tag_id", String.valueOf(tagId))
                    .build();
            searchinfo_request = new Request.Builder()
                    .url(HttpContants.GET_GOODS_CLASSIFICATION_DETIAL)
                    .post(requestBody)
                    .build();
            flagChoose = 1;
        }
        try {
            Response goodsinfo_response = client.newCall(searchinfo_request).execute();
            if (goodsinfo_response.code() == 200) {
                final String goodsinfo_str = goodsinfo_response.body().string();
                mGoodsInfo2 = JSONObject.parseArray(goodsinfo_str, GoodsInfo.class);
                mGoodsInfo.clear();
                    for (int i = 0; i < mGoodsInfo2.size(); i++) {
                        mGoodsInfo.add(mGoodsInfo2.get(i));
                    }
                    if(flag == FIRST_IN) {
                        getFirstData();//初次进入时的得到数据
                        alertDialog.dismiss();
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mGoodsInfo == null || mGoodsInfo.isEmpty() || mGoodsInfo.size() == 0){
                                    nothing.setText("暂无您想要查询的内容");
                                    recyclerView.setVisibility(View.GONE);
                                    nothingLayout.setVisibility(View.VISIBLE);
                                }else {
                                    nothingLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                }
                                alertDialog.dismiss();
                                Log.d(TAG, "run:  adapter.notifyDataSetChanged();");
                            }
                        });
                    }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.ToastShortTime("数据请求失败");
                    }
                });
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void getFirstData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mGoodsInfo == null || mGoodsInfo.isEmpty() || mGoodsInfo.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    nothing.setText("暂无您想要查询的内容");
                    nothingLayout.setVisibility(View.VISIBLE);
                }else {
                    nothingLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    adapter = new SearchBaseAdapter(mGoodsInfo);
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            GoodsInfo goodsInfo = mGoodsInfo.get(position);
                            GoodInfoActivity.actionStart(YiWuApplication.getContext(), goodsInfo);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(){
        gosearch = findViewById(R.id.gosearch);
        gosearch.setOnClickListener(this);
        clearEditText = findViewById(R.id.edittext_search);
        clearEditText.setOnKeyListener(this);
        nothing = findViewById(R.id.txt_no);
        nothingLayout = findViewById(R.id.nothing_layout);
        refreshLayout = findViewById(R.id.serach_list_refreshLayout);  //上拉刷新
        if (searchStr!=null){
            clearEditText.setText(searchStr);
        }

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

        recyclerView = findViewById(R.id.search_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchBaseAdapter(mGoodsInfo);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_serach_list;
    }
    @Override
    protected void initDate() {
        showDialog();
        mGoodsInfo = new ArrayList<>();
        getIntentDate();//获取intent参数
        getSearchInfoThread.start();

    }

    private void showDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setView(R.layout.dialog_load)//加载中
                .show();
    }

    private void getIntentDate() {
        Intent intent = getIntent();
        searchStr = intent.getStringExtra("searchStr");
        tagId = intent.getIntExtra("tagId",2);
        Log.d(TAG, "getIntentDate:searchStr= "+searchStr);
    }

    /**
     * 启动活动
     * @param context
     */
    public static void actionStart(Context context,String searchStr){
        if (searchStr==null){
            searchStr="";
        }
        Intent intent = new Intent(context, SearchListActivity.class);
        intent.putExtra("searchStr",searchStr);
        flagChoose = 1;
        context.startActivity(intent);
    }

    public static void actionStart(Context context,int tagId){
        Intent intent = new Intent(context, SearchListActivity.class);
        intent.putExtra("tagId",tagId);
        flagChoose = 2;
        context.startActivity(intent);
    }
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        switch (view.getId()){
            case R.id.edittext_search:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                            || keyCode == KeyEvent.KEYCODE_ENTER) {
                        searchStr = clearEditText.getText().toString();
                        if (TextUtils.isEmpty(searchStr)){
                            return false;
                        }
                        showDialog();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: onKey");
                                getJsonData(OTHER_IN);
                            }
                        }).start();
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gosearch:
                showDialog();
                searchStr = clearEditText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: onClick");
                        getJsonData(OTHER_IN);
                    }
                }).start();
                break;
            default:
                break;
        }
    }
}
