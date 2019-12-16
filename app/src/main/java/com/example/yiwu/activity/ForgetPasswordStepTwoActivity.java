package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiwu.R;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by 朱旭辉
 * Time  2019/10/06
 * Describe:忘记密码第二步
 */

public class ForgetPasswordStepTwoActivity extends BaseActivity implements View.OnClickListener {
    private String userEmailStr;

    private TextView emailTextView;
    private EditText codeEditText;
    private Button NextStepButton;
    private ImageView backButton;
    private String respose;

    @Override
    protected void initDate() {
        GetItent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                RequestBody requestBody = new FormBody.Builder()
                        .add("email",userEmailStr)
                        .build();
                respose = okHttpUtils.SendPostRequst(requestBody, HttpContants.FORGET_PASSWORD_TWO);
            }
        }).start();

    }

    private void GetItent() {
        Intent intent = getIntent();
        userEmailStr = intent.getStringExtra("email");
    }

    @Override
    protected void initView() {
        emailTextView = findViewById(R.id.user_email);
        emailTextView.setText(userEmailStr);
        codeEditText = findViewById(R.id.forget_et_code);
        backButton = findViewById(R.id.titleBar_iv_back);
        backButton.setOnClickListener(this);
        NextStepButton = findViewById(R.id.next_step_btn);
        NextStepButton.setOnClickListener(this);
    }

    public static void actionStart(Context context,String email){
        Intent intent = new Intent(context,ForgetPasswordStepTwoActivity.class);
        intent.putExtra("email",email);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget_password_two;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_step_btn://下一步
                String codeStr = codeEditText.getText().toString();
                if (TextUtils.equals(codeStr,respose)){
                    ForgetPasswordSteepThreeActivity.actionStart(this,userEmailStr);
                }else{
                    ToastUtils.ToastShortTime("验证码错误");
                }
                break;
            case R.id.titleBar_iv_back:
                finish();
                break;
        }
    }
}
