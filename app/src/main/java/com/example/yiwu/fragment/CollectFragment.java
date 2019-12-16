package com.example.yiwu.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;

import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.activity.GoodInfoActivity;
import com.example.yiwu.activity.MainActivity;
import com.example.yiwu.adapter.CollectBaseAdapter;
import com.example.yiwu.customView.CollectionPopupWindow;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.state.App;
import com.example.yiwu.util.LattePreference;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 朱旭辉
 * Time  2019/9/5
 * Describe:收藏碎片
 * 修改：2019/9/12 增加了网路请求
 * 修改：朱鸿
 * 添加点击事件
 */

public class CollectFragment extends BaseFragment {
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<GoodsInfo> mCollections = new ArrayList<>();
    private List<GoodsInfo> mCollectionsForGet;
    private CollectBaseAdapter adapter;
    private CollectionPopupWindow popupWindow;//删除对话框
    private AppCompatTextView txtCellect;
    private LinearLayout nothingLayout;
    private AppCompatTextView nothing; //搜索为空的显示界面
    private View headerView;
    private int FIRSTIN = 1;
    private int REFRESHIN = 2;
    private int NOCOLLECTION = 3;
    private String mUserId = null;   //当前的userId
    private Thread getCollectInfoThread = new Thread() {
        @Override
        public void run() {
            getJsonData(FIRSTIN);//获得json数据
        }
    };

    private void getJsonData(int flag) {

        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id",YiWuApplication.getInstance().getUser().getUs_id())
                .build();
        mCollections.clear();
            String collectinfo_str = okHttpUtils.SendPostRequst(requestBody,HttpContants.COLLECTINFO_URL);
            if (!(collectinfo_str.length() == NOCOLLECTION)) {//是否有收藏
                mCollectionsForGet = JSONObject.parseArray(collectinfo_str, GoodsInfo.class);
                mCollections.clear();
                for (int i=0;i<mCollectionsForGet.size();i++){
                    mCollections.add(mCollectionsForGet.get(i));
                }
                Collections.reverse(mCollections);//实现list集合逆序排列
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        nothingLayout.setVisibility(View.GONE);
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.GONE);
                        nothingLayout.setVisibility(View.VISIBLE);
                        txtCellect.setText("我的收藏");
                        nothing.setText("您暂无收藏");
                    }
                });
            }
            if (flag == FIRSTIN){
                firtRefresh();
            }else {
                otherRefresh();
            }

    }

    private void otherRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

            }
        });
    }

    private void firtRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        GoodInfoActivity.actionStart(YiWuApplication.getContext(),mCollections.get(position));
                    }
                });
                adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        delete_position = position;
                        deleteCollcet();//删除收藏
                        return true;
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }
    int delete_position;//确认是删除哪一个item
    private void deleteCollcet() {
        //实例化SelectPicPopupWindow
        popupWindow = new CollectionPopupWindow(BaseActivity.getCurrentActivity(),itemsOnClick);
        //显示窗口
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(),
                Gravity.CENTER | Gravity.BOTTOM, 0, 0);

    }
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            popupWindow.dismiss();
            switch (v.getId()) {
                case R.id.delect_collect:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("user_id",YiWuApplication.getInstance().getUser().getUs_id())
                                    .add("goods_id",mCollections.get(delete_position).getGoods_id())
                                    .build();
                            Request delect_collect_request = new Request.Builder()
                                    .url(HttpContants.DELECT_COLLECTION_URL)
                                    .post(requestBody)
                                    .build();
                            try {
                                Response collectinfo_response = client.newCall(delect_collect_request).execute();
                                if (collectinfo_response.code() == 200 && collectinfo_response.body().string().equals("删除成功")) {
                                    mCollections.remove(delete_position);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(mCollections.size() == 0){
                                                recyclerView.setVisibility(View.GONE);
                                                nothingLayout.setVisibility(View.VISIBLE);
                                                txtCellect.setText("我的收藏");
                                                nothing.setText("您暂无收藏");
                                            }
                                            adapter.notifyDataSetChanged();
                                            ToastUtils.ToastShortTime("删除成功");
                                        }
                                    });

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.ToastShortTime("删除失败");
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case R.id.delect_collect_cancel_btn:
                    popupWindow.dismiss();
                    break;
                default:
                    break;
            }


        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        LayoutInflater factorys = LayoutInflater.from(getActivity());
        txtCellect = super.view.findViewById(R.id.chat_header_txt);
        nothing = super.view.findViewById(R.id.txt_no);
        nothingLayout = super.view.findViewById(R.id.nothing_layout_cellect);
        headerView = factorys.inflate(R.layout.header_collect, null); //获取布局
        adapter.addHeaderView(headerView);
        refreshLayout = super.view.findViewById(R.id.refreshLayout);  //上拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getJsonData(REFRESHIN);
                    }
                }).start();
                refreshLayout.finishRefresh(1000);
            }

        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {  //下拉加载
               ToastUtils.ToastShortTime("没有更多内容了");
               refreshLayout.finishLoadMore();
            }
        });

        recyclerView = super.view.findViewById(R.id.collect_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
//        CollcetAdapter adapter = new CollcetAdapter(mCollections);

    }


    @Override
    public void onResume() {
        super.onResume();
        String mCurrentId = LattePreference.getCustomAppProfile(App.USERID);
        Log.d(TAG, "mCurrentId=="+mCurrentId);
        if(mCurrentId == null){      // 发现已经退出之后进行的操作
            mCollections.clear();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.GONE);
                    nothingLayout.setVisibility(View.VISIBLE);
                    txtCellect.setText("我的收藏");
                    nothing.setText("您还没登陆，请先登录");
                }
            });
        }else {
            if(!mCurrentId.equals(mUserId)){    //发现用户已经更改进行的操作
                mUserId = mCurrentId;     //将当前的userId进行更新
            }
            Thread infoThread = new Thread() {
                @Override
                public void run() {
                    getJsonData(REFRESHIN);//获得json数据
                }
            };
            infoThread.start();
        }
    }

    @Override
    protected void initDate() {
        mUserId = LattePreference.getCustomAppProfile(App.USERID);
        adapter = new CollectBaseAdapter(mCollections);
        if(mUserId == null){
            mUserId = "0000";
        }else {
            getCollectInfoThread.start();
        }
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_collect;
    }
}
