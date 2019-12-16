package com.example.yiwu.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.yiwu.state.App;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.util.LattePreference;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.yiwu.http.HttpContants.LOGIN_URL;
/**
 * Created by 朱旭辉
 * Time  2019/10/04
 * Describe:注册帮助activity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText loginPhone;
    private EditText loginPassWord;
    private Button login_in_btn;
    private ImageView login_titleBar_back;
    private TextView login_in_register;
    private User user;
    private TextView ForgetPasswordTextView;
    @Override
    protected void initDate() {

    }

    @Override
    protected void initView() {
        ForgetPasswordTextView = findViewById(R.id.login_in_forget_password);
        ForgetPasswordTextView.setOnClickListener(this);
        login_in_register = findViewById(R.id.login_in_register);
        login_in_register.setOnClickListener(this);
        login_titleBar_back = findViewById(R.id.login_titleBar_iv_back);
        login_titleBar_back.setOnClickListener(this);
        loginPhone = findViewById(R.id.login_phone);
        loginPassWord = findViewById(R.id.login_password);
        login_in_btn = findViewById(R.id.login_in_btn);
        login_in_btn.setOnClickListener(this);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_in_btn://登陆
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendLogInRequst();
                    }
                }).start();
                break;
            case R.id.login_titleBar_iv_back://返回键
                finish();
                break;
            case R.id.login_in_register://注册
                RegisterActivity.actionStart(this);
                break;
            case R.id.login_in_forget_password:
                ForgetPasswordStepOneActivity.actionStart(this);
        }
    }
    public static void actionStart(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }
    private void SendLogInRequst() {
        final String phone_str = loginPhone.getText().toString();
        String password_str = loginPassWord.getText().toString();
        if (!TextUtils.isEmpty(phone_str) && !TextUtils.isEmpty(password_str)) {
            OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
//            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("phone",phone_str)
                    .add("password",password_str)
                    .build();
//            Request request = new Request.Builder()
//                    .url(LOGIN_URL)
//                    .post(requestBody)
//                    .build();



//                Response response = client.newCall(request).execute();
                final String responseData =  okHttpUtils.SendPostRequst(requestBody,LOGIN_URL);
                if (!responseData.equals("密码错误") && !responseData.equals("未找到此用户")){
                    EMClient.getInstance().login(phone_str, password_str, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            user = JSONObject.parseObject(responseData,User.class);
                            YiWuApplication application = YiWuApplication.getInstance();
                            application.putUser(user);
                            Log.d(TAG, "onSuccess: 1");
                            LattePreference.clearAppPreferences();
                            LattePreference.setAppFlag(App.ISLOGINED.name(),true);
                            LattePreference.addCustomAppProfile(App.USERID,phone_str);
                            LattePreference.addCustomAppProfile(App.USERIMG,user.getUs_headpic());
                            LattePreference.addCustomAppProfile(App.USERNAME,user.getUs_name());
                            Log.d(TAG, "onSuccess: 2");
                            finish();//执行后会转到MineFragment的onActivityResult方法
                        }
                        @Override
                        public void onError(int i, String s) {
                            Log.d("123456",s);
                        }
                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                } else if ( responseData.equals("未找到此用户")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.ToastShortTime("未找到此用户");
                        }
                    });
                }else if (responseData.equals("密码错误")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.ToastShortTime("密码错误");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.ToastShortTime("请求失败");
                        }
                    });
                }

        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.ToastShortTime("手机号或密码为空");
                }
            });

        }
    }

}
