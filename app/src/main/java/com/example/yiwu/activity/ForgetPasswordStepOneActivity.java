package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yiwu.R;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.LoginAndForgetPasswordHelperUtil;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by 朱旭辉
 * Time  2019/10/06
 * Describe:忘记密码第一步
 */
public class ForgetPasswordStepOneActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backImageView;
    private EditText emailEditText;
    private String emailStr;
    private Button nextStepButton;
    String respose;//接受数据
    class SendRequstThread extends Thread implements Runnable{
        @Override
        public void run() {
            OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
            RequestBody requestBody = new FormBody.Builder()
                    .add("email",emailStr)
                    .build();
            respose = okHttpUtils.SendPostRequst(requestBody, HttpContants.FORGET_PASSWORD_ONE);
        }
    }

    @Override
    protected void initDate() {

    }

    @Override
    protected void initView() {
        backImageView = findViewById(R.id.forget_password_titleBar_iv_back);
        backImageView.setOnClickListener(this);
        emailEditText = findViewById(R.id.forget_passworf_emailNumber);
        nextStepButton = findViewById(R.id.forget_password_btn);
        nextStepButton.setOnClickListener(this);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget_password_one;
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,ForgetPasswordStepOneActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forget_password_titleBar_iv_back://返回键
                finish();
                break;
            case R.id.forget_password_btn:
                emailStr = emailEditText.getText().toString();
                if (LoginAndForgetPasswordHelperUtil.isEmail(emailStr)){
                    try {
                        SendRequstThread thread = new SendRequstThread();
                        thread.start();
                        thread.join();
                        if (respose.equals("此邮箱已注册")){
                            ForgetPasswordStepTwoActivity.actionStart(this,emailStr);
                        }else {
                            ToastUtils.ToastShortTime("此邮箱没有被注册哦");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtils.ToastShortTime("输入邮箱不合法");
                }
        }
    }
}
