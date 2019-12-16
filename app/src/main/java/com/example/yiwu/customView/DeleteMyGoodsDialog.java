package com.example.yiwu.customView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.activity.MyGoodsActivity;

import com.example.yiwu.activity.NoticeActivity;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by 朱旭辉
 * Time  2019/11/05
 * Describe:删除物品时的提示框
 */
public class DeleteMyGoodsDialog extends Dialog implements View.OnClickListener{
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;
    private User user;
    private int postion;//记录删除的item
    private List<GoodsInfo> goodsInfoList;


    public DeleteMyGoodsDialog(@NonNull Context context, User user, List<GoodsInfo> goodsInfoList,int postion) {
        super(context);
        this.goodsInfoList = goodsInfoList;
        this.user = user;
        this.postion = postion;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_mygoods);
        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(false);
        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.submit:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("user_id", YiWuApplication.getInstance().getUser().getUs_id())
                                .add("goods_id",goodsInfoList.get(postion).getGoods_id())
                                .build();
                        String responseStr = okHttpUtils.SendPostRequst(requestBody, HttpContants.DELETE_MYGOOGS);
                        if(responseStr.equals("审核完毕")){
                            BaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.ToastShortTime("审核完毕");
                                    goodsInfoList.remove(postion);
                                    NoticeActivity.adapterNotifyDataSetChanged();
                                }
                            });
                        }else {
                            BaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.ToastShortTime("审核失败");
                                }
                            });

                        }
                    }
                }).start();
                this.dismiss();

        }
    }
}
