package com.example.yiwu.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.Info.SortInfo;
import com.example.yiwu.ItemDecortion.MyItemDecortion;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.GoodInfoActivity;
import com.example.yiwu.activity.MainActivity;
import com.example.yiwu.activity.SearchActivity;
import com.example.yiwu.activity.SearchListActivity;
import com.example.yiwu.adapter.GoodsSortsAdapter;
import com.example.yiwu.adapter.HomeRecyclerViewAdapter;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.angmarch.views.NiceSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:主页
 * 修改：朱鸿
 * Time:2019/9/15
 * 添加banner和recyclerview的点击事件
 * 添加spinner
 * 添加recyclerview，用于显示分类和分类页面跳转
 */
public class HomeFragment extends BaseFragment implements OnBannerListener, AdapterView.OnItemClickListener {
    private RecyclerView recyclerView;
    //    private View searchTitleView;//获取搜索栏
//    private LinearLayout searchBar;//搜索框
    private Banner banner;
    //*******************************************
    private RefreshLayout refreshLayout;   //用于刷新界面
    //*******************************************
    private View viewBanner;//用于获取banner
    private List<String> bannerImageList = new ArrayList<>();//banner的图片
    private List<String> bannerTitleList = new ArrayList<>();//banner的title
    private List<GoodsInfo> recyclerViewGoodsInfoList = new ArrayList<>();//recyclerView的商品信息
    private List<GoodsInfo> tempRecyclerGoodsInfoList; //暂时存储商品信息recyclerview
    private List<GoodsInfo> bannerViewGoodsInfoList = new ArrayList<>();//banner的商品信息
    private List<GoodsInfo> tempBannerGoodInfoList;  //暂时存储商品的banner
    private HomeRecyclerViewAdapter homeRecyclerViewAdapter;
    private NiceSpinner spinner;  //学校的下拉列表
    private List<String> schoolList;  //学校列表
    private List<String> goodsSorts; //图书种类
    private List<SortInfo> sortInfoList;//图书图标和名字的组合的类的数组
    private List<Integer> goodsSortsImg; // 图书种类的图标
    private RecyclerView mGoodsSortsRecler; //显示图书种类
    private Integer[] sortImgId = new Integer[]{R.mipmap.sort8,R.mipmap.sort2,R.mipmap.sort3,
            R.mipmap.sort4,R.mipmap.sort5,R.mipmap.sort6,R.mipmap.sort7,R.mipmap.sort1};
    private GoodsSortsAdapter goodsSortsAdapter;
    private int flag = 1; //首次进入为1，刷新为2
    private Thread getInfoThread = new Thread() {
        @Override
        public void run() {
            getJsonData();//获得json数据
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_home;
    }

    //初始化数据
    @Override
    protected void initDate() {

        if (bannerImageList.size() == 0 || bannerTitleList.size() == 0) {
            getGoodsInfoData();//请求goodsinfo数据
        }
        getSchoolData();

    }

    private void getBannerData() {
        bannerImageList.clear();
        bannerTitleList.clear();
        //加载数据
        for (int i = 0; i < bannerViewGoodsInfoList.size(); i++) {
            bannerImageList.add(HttpContants.IMAGE_URL_119 + bannerViewGoodsInfoList.get(i).getGoods_pic_addr().get(0));//得到第一张图片
            bannerTitleList.add(bannerViewGoodsInfoList.get(i).getGoods_name());
        }

//        for (int i=0;i<5;i++){
//           bannerImageList.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg");
//           bannerTitleList.add("好好学习"+i);
//        }

    }
    private void getSchoolData(){
        schoolList = new ArrayList<>();
        //初始化学校数据
        schoolList.add("重大");
        schoolList.add("西南大学");
        schoolList.add("西政");
        schoolList.add("重邮");
        schoolList.add("重师");
        schoolList.add("重交");
        schoolList.add("重庆工商大学");
    }


    private void getGoodsInfoData() {
        getInfoThread.start();
    }


    //初始化控件
    @Override
    protected void initView() {
        //recyclerView
        recyclerView = super.view.findViewById(R.id.home_recyclerView);
        Log.d(TAG, "initView: " + recyclerView);


        //布局映射，是用来找 res/layout 下的 xml 布局文件，并且实例化
        viewBanner = LayoutInflater.from(getActivity()).inflate(R.layout
                .view_banner, (ViewGroup) recyclerView.getParent(), false);
        banner = viewBanner.findViewById(R.id.banner);
        //设置bannner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());

        mGoodsSortsRecler = viewBanner.findViewById(R.id.recycler_goods_sorts);
        mGoodsSortsRecler.setBackgroundResource(R.drawable.bg_recycle_style_white);
        //*************************************
        refreshLayout = super.view.findViewById(R.id.home_refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getJsonData();
                    }
                });
                thread.start();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ToastUtils.ToastShortTime("没有更多了");
                refreshLayout.finishLoadMore();
            }
        });
        //*****************************************
        spinner = super.view.findViewById(R.id.school);
        spinner.attachDataSource(schoolList);
        //spinner点击事件
        spinner.addOnItemClickListener(this);
        spinner.setSelectedIndex(schoolList.indexOf("重邮"));
    }

    private void setRecyclerViewData() {
        homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(recyclerViewGoodsInfoList);
        recyclerView.setAdapter(homeRecyclerViewAdapter);
        homeRecyclerViewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodInfoActivity.actionStart(YiWuApplication.getContext(),recyclerViewGoodsInfoList.get(position));
            }
        });
        //加载修饰器
        recyclerView.addItemDecoration(new MyItemDecortion());
        //设置每列item
        GridLayoutManager gridLayoutManager = new GridLayoutManager(YiWuApplication.getContext(), 2);
        //修改第0列的item个数
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else {
                    return 1;
                }

            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        //添加bannner
        homeRecyclerViewAdapter.addHeaderView(viewBanner);

    }

    //设置图书种类的reclerview
    private void setRecyclerViewGoodsSorts(){
        sortInfoList = new ArrayList<>();
        int size = goodsSorts.size();
        goodsSortsImg = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            goodsSortsImg.add(sortImgId[i]);
        }
        for (int i = 0; i < size; i++) {
            SortInfo sortInfo = new SortInfo(goodsSorts.get(i),goodsSortsImg.get(i));
            sortInfoList.add(sortInfo);
        }
        goodsSortsAdapter = new GoodsSortsAdapter(sortInfoList);

        mGoodsSortsRecler.setAdapter(goodsSortsAdapter);
        goodsSortsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SearchListActivity.actionStart(getContext(),(position + 1));
            }
        });
        GridLayoutManager manager = new GridLayoutManager(getContext(),4);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        manager.setOrientation(RecyclerView.HORIZONTAL);
        mGoodsSortsRecler.setLayoutManager(manager);


    }
    private void setBannerData() {
        //设置图片集合
        banner.setImages(bannerImageList);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(bannerTitleList);

        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(this).start();
        banner.start();
    }


    private void getJsonData() {
        OkHttpClient client = new OkHttpClient();
        Request goodsinfo_request = new Request.Builder().url(HttpContants.HOME_GOODSINFO_URL).build();
        Request bannerinfo_request = new Request.Builder().url(HttpContants.HOME_BANNER_URL).build();
        Request goodsSort_request = new Request.Builder().url(HttpContants.GET_GOODS_CLASSIFICATION).build();
        try {
            Response goodsinfo_response = client.newCall(goodsinfo_request).execute();
            Response bannerinfo_response = client.newCall(bannerinfo_request).execute();
            Response goodsSort_response = client.newCall(goodsSort_request).execute();
            if (goodsinfo_response.code() == 200 && bannerinfo_response.code() == 200) {
                String goodsinfo_str = goodsinfo_response.body().string();
                String bannerinfo_str = bannerinfo_response.body().string();
                String goodSorts_str = goodsSort_response.body().string();

                tempRecyclerGoodsInfoList = JSONObject.parseArray(goodsinfo_str, GoodsInfo.class);
                recyclerViewGoodsInfoList.clear();
                int recyclerSize = tempRecyclerGoodsInfoList.size();
                for (int i = 0; i < recyclerSize; i++) {
                    recyclerViewGoodsInfoList.add(tempRecyclerGoodsInfoList.get(i));
                    Log.d("xxxxxxxx",goodsinfo_str);
                }
                tempBannerGoodInfoList = JSONObject.parseArray(bannerinfo_str, GoodsInfo.class);
                bannerViewGoodsInfoList.clear();
                int bannerSize = tempBannerGoodInfoList.size();
                for (int i = 0; i < bannerSize; i++) {
                    bannerViewGoodsInfoList.add(tempBannerGoodInfoList.get(i));
                }

                goodsSorts = JSONObject.parseArray(goodSorts_str,String.class);
                getBannerData();//请求banner数据
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBannerData();
                        if(flag == 1){
                            //设置banner数据
                            //设置RecyclerView数据
                            setRecyclerViewData();
                            //设置图书种类数据
                            setRecyclerViewGoodsSorts();
                            flag = 2;
                        }else if(flag == 2){
                            homeRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        refreshLayout.finishRefresh();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
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

    @Override
    public void OnBannerClick(int position) {
        GoodInfoActivity.actionStart(YiWuApplication.getContext(),bannerViewGoodsInfoList.get(position));
    }

    //spinner点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    //自定义的图片加载器
    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);
        }
    }
}
