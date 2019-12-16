package com.example.yiwu.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.customView.DialogShow;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.example.yiwu.util.callback.CallbackManger;
import com.example.yiwu.util.callback.CallbackType;
import com.example.yiwu.util.callback.IGlobalCallback;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.hyphenate.chat.EMClient.TAG;

/**
 * Created by 朱鸿
 * Time  2019/9/9
 * Describe:商品页面
 * 修改：添加banner点击事件，放大显示图片2019/9/28
 */

public class GoodInfoActivity extends BaseActivity implements OnBannerListener, View.OnClickListener {
    private Banner banner;
    private ArrayList<String> list_path;
    private ArrayList<String> list_title;
    private TextView evaluate,introducation,price,detial,back;  //商品的评价，简介标题，价格，详情介绍
    private ImageButton collect; //收藏图标
    private Button add,buy;  //加入收藏和购买按钮
    private GoodsInfo goodsInfo;
    private String goods_id;
    private User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initDate() {
        goodsInfo = getIntent().getParcelableExtra("Good_info");
        goods_id = goodsInfo.getGoods_id();
        list_path = new ArrayList<>();
        list_title = new ArrayList<>();
        for (int i = 0; i < goodsInfo.getGoods_pic_addr().size(); i++) {
            list_path.add(HttpContants.IMAGE_URL_119+ goodsInfo.getGoods_pic_addr().get(i));
            list_title.add(goodsInfo.getGoods_name());
        }
        user = YiWuApplication.getInstance().getUser();
        postData();//将访问信息发给服务器



    }

    private void postData() {
        final RequestBody requestBody;
        if (user != null){
            requestBody = new FormBody.Builder()
                    .add("goods_id",goodsInfo.getGoods_id())
                    .add("visitor_id",user.getUs_id())
                    .build();
        }else {//游客
            requestBody = new FormBody.Builder()
                    .add("goods_id",goodsInfo.getGoods_id())
                    .add("visitor_id","0")
                    .build();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                okHttpUtils.SendPostRequst(requestBody,HttpContants.VISIT_GOODS_INFO);
            }
        }).start();

    }

    protected void initView() {

        banner = findViewById(R.id.goods_info_img);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new MyLoader());
        banner.setImages(list_path);
        banner.setBannerAnimation(Transformer.Default);
        banner.setBannerTitles(list_title);
        banner.setDelayTime(2000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(this).start();
        evaluate = findViewById(R.id.goods_info_evaluate);
        evaluate.setOnClickListener(this);
        introducation = findViewById(R.id.goods_info_introducation);
        price = findViewById(R.id.goods_info_price);
        detial = findViewById(R.id.goods_info_detial);
        collect = findViewById(R.id.goods_info_collect);
        add = findViewById(R.id.goods_info_add);
        buy = findViewById(R.id.goods_info_buy);
        buy.setOnClickListener(this);
        add.setOnClickListener(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                introducation.setText(goodsInfo.getGoods_name());
                price.setText("￥" + goodsInfo.getGoods_price());
                detial.setText(goodsInfo.getGoods_info());
            }
        });
        collect = findViewById(R.id.goods_info_collect);
        collect.setOnClickListener(this);

        back = findViewById(R.id.header_back);
        back.setOnClickListener(this);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_good_info;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goods_info_evaluate:
                GoodInfoRemarkActivity.startActivity(this);
                break;
            case R.id.goods_info_collect:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        collect.setImageResource(R.drawable.heart_a);
                    }
                });
                break;
            case R.id.goods_info_add:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addCollcet();
                    }
                }).start();
                break;
            case R.id.goods_info_buy:
                String ownerId = goodsInfo.getOwner_id();
                Log.d(TAG, "onClick: ownerId"+ownerId);
                if (YiWuApplication.getInstance().getUser() == null){
                    ToastUtils.ToastShortTime("请先登陆哦");
                    LoginActivity.actionStart(this);

                }else {
                    IGlobalCallback<String> callback = CallbackManger.getInstance().getCallback(CallbackType.ADD_CHAT_USER);
                    if(callback != null){
                        callback.executeCallback(ownerId);
                    }else {
                        MainActivity.setCurrentTab(2);
                        MainActivity.actionStart(this,ownerId);
                    }
                }

                break;
            case R.id.header_back:
                finish();
                break;
        }
    }

    private void addCollcet() {
        User currentUser = YiWuApplication.getInstance().getUser();
        if(currentUser != null){
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("user_id",currentUser.getUs_id())
                    .add("goods_id",goods_id)
                    .build();
            Request addcollect_request = new Request.Builder()
                    .url(HttpContants.ADD_COLLECTION_URL)
                    .post(requestBody)
                    .build();
            try {
                Response collectinfo_response = client.newCall(addcollect_request).execute();
                if (collectinfo_response.code() == 200) {
                    final String collection_str = collectinfo_response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (collection_str.equals("添加成功")){
                                ToastUtils.ToastShortTime("收藏成功！");
                            }else {//已经收藏
                                ToastUtils.ToastShortTime("已经收藏了哦~");
                            }
                        }
                    });

                }
            }catch (Exception e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.ToastShortTime("数据请求失败");
                    }
                });
            }
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.ToastShortTime("请先登录，才能够添加收藏额");
                    LoginActivity.actionStart(GoodInfoActivity.this);
                }
            });
        }
    }

    //自定义的图片加载器
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);
            Log.d("123456", (String) path);
        }
    }
    //轮播图的监听方法
    public void OnBannerClick(int position) {
        Log.i("tag", "你点了第"+position+"张轮播图");
        String imgUrl = list_path.get(position);
        DialogShow.showDialog(GoodInfoActivity.this,imgUrl);
    }
    /**
     * 启动活动
     * @param context
     */
    public static void actionStart(Context context, GoodsInfo goodsInfo){
        Intent intent = new Intent(context,GoodInfoActivity.class);
        intent.putExtra("Good_info",goodsInfo);
        context.startActivity(intent);
    }
}
