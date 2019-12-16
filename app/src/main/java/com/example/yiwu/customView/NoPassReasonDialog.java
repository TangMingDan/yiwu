package com.example.yiwu.customView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yiwu.Info.CheckInfo;
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

import static com.example.yiwu.customView.PickerView.TAG;

/**
 * Created by 朱旭辉
 * Time  2019/12/12
 * Describe:未通过审核的反馈
 */
public class NoPassReasonDialog extends Dialog implements View.OnClickListener{
    private EditText reasonEditText;
    private TextView nopassTextView;
    private TextView cancelTextView;
    private User user;
    private int position;//记录删除的item
    private List<CheckInfo> checkInfos;
    private final String GOODSNOPASS = "-1";
    private final int MAXLENGTH = 20;
    public NoPassReasonDialog(@NonNull Context context, User user, List<CheckInfo> checkInfos, int position) {
        super(context);
        this.checkInfos = checkInfos;
        this.user = user;
        this.position = position;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nopss_reason);

        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(false);
        reasonEditText = findViewById(R.id.nopass_edit);
        nopassTextView = findViewById(R.id.submit);
        nopassTextView.setOnClickListener(this);
        cancelTextView = findViewById(R.id.cancel);
        cancelTextView.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.submit:
                if (!TextUtils.isEmpty(reasonEditText.getText().toString())&&reasonEditText.getText().toString().length()<=MAXLENGTH){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CheckInfo checkInfo = checkInfos.get(position);
                            OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("result",GOODSNOPASS)
                                    .add("goods_id",checkInfo.getGoods_id()+"")
                                    .add("goods_name",checkInfo.getGoods_name())
                                    .add("email",YiWuApplication.getInstance().getUser().getUs_email())
                                    .add("reason",reasonEditText.getText().toString())
                                    .build();

                            String responseStr = okHttpUtils.SendPostRequst(requestBody, HttpContants.CHECK_RESPONSE);
                            if(responseStr.equals("审核完毕")){
                                BaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.ToastShortTime("审核通过");
                                        checkInfos.remove(position);
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
                }else {
                    ToastUtils.ToastShortTime("请输入正确反馈");
                }


        }
    }
}
