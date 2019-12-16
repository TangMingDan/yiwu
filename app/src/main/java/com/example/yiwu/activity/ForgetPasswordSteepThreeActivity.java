package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yiwu.R;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.ActivityCollector;
import com.example.yiwu.util.LoginAndForgetPasswordHelperUtil;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ForgetPasswordSteepThreeActivity extends BaseActivity implements View.OnClickListener {
    private String userEmailStr;
    private ImageView backImage;
    private EditText passwordEditText;
    private EditText rePasswordEditText;
    private Button overButton;
    private String passwordStr;
    private int FINISHACTIVITIES = 3;//销毁前三个活动
    @Override
    protected void initDate() {
        GetIntent();
    }

    private void GetIntent() {
        Intent intent = getIntent();
        userEmailStr = intent.getStringExtra("email");
    }

    @Override
    protected void initView() {
        overButton = findViewById(R.id.over_btn);
        overButton.setOnClickListener(this);
        passwordEditText = findViewById(R.id.reset_password_et_password);
        rePasswordEditText = findViewById(R.id.reset_password_et_repassword);
        backImage = findViewById(R.id.reset_password_titleBar_iv_back);
        backImage.setOnClickListener(this);
    }

    public static void actionStart(Context context, String email){
        Intent intent = new Intent(context,ForgetPasswordSteepThreeActivity.class);
        intent.putExtra("email",email);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget_password_three;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reset_password_titleBar_iv_back:
                finish();
                break;
            case R.id.over_btn:
                passwordStr = passwordEditText.getText().toString();
                String repasswordStr = rePasswordEditText.getText().toString();
                if (TextUtils.equals(passwordStr,repasswordStr)&&
                    LoginAndForgetPasswordHelperUtil.isPasswordNumber(passwordStr)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendRequsest();
                        }
                    }).start();
                }else {
                    ToastUtils.ToastShortTime("两次密码不一致");
                }
                break;

        }
    }

    private void sendRequsest() {
        RequestBody requestBody = new FormBody.Builder()
                .add("email",userEmailStr)
                .add("password",passwordStr)
                .build();
        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
        String responseData = okHttpUtils.SendPostRequst(requestBody, HttpContants.FORGET_PASSWORD_THREE);
        if (responseData.equals("修改成功")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.ToastShortTime("密码修改成功");
                    /**
                     * 完成注册，销毁之前的注册活动，直接跳到登陆界面，且不可返回之前注册活动
                     * 所以Login一定为singleTask模式！！！
                     */
                    LoginActivity.actionStart(ForgetPasswordSteepThreeActivity.this);


                }
            });

        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.ToastShortTime("修改失败");

                }
            });

        }
    }
}
