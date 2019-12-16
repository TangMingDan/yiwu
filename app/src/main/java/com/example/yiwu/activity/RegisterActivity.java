package com.example.yiwu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.yiwu.R;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.LoginAndForgetPasswordHelperUtil;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 朱旭辉
 * Time  2019/10/04
 * Describe:注册界面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private ImageView BackButton;
    private EditText RegisterNameEditText;
    private EditText RegisterPhoneEditText;
    private EditText RegisterEmailEditText;
    private EditText RegisterCodeEditText;
    private Button RegisterCodeButton;
    private EditText RegisterPasswordEditText;
    private EditText RegisterRePasswordEditText;
    private EditText RegisterUserJobIDEditText;
    private TextView RegisterSchoolTextView;

    private Button RegisterButton;
    private String NameStr;
    private String UserJobIDStr;
    private String SchoolStr;
    private String PhoneNumberStr;
    private String EmailNumberStr;
    private String CodeNumberStr;
    private String CodeStr;
    private String Password;
    private String RePassword;
    private int SecCount;//验证码发送秒数


    @Override
    protected void initDate() {

    }

    @Override
    protected void initView() {

        BackButton = findViewById(R.id.register_titleBar_iv_back);
        BackButton.setOnClickListener(this);
        RegisterNameEditText = findViewById(R.id.register_et_username);
        RegisterPhoneEditText = findViewById(R.id.register_phoneNumber);
        RegisterEmailEditText = findViewById(R.id.register_emailNumber);
        RegisterCodeEditText = findViewById(R.id.register_et_code);
        RegisterCodeButton = findViewById(R.id.register_btn_getCode);
        RegisterCodeButton.setOnClickListener(this);
        RegisterPasswordEditText = findViewById(R.id.register_et_password);
        RegisterRePasswordEditText = findViewById(R.id.register_et_repassword);
        RegisterUserJobIDEditText = findViewById(R.id.register_et_userjobID);
        RegisterSchoolTextView = findViewById(R.id.register_et_school);
        RegisterSchoolTextView.setOnClickListener(this);
        RegisterButton = findViewById(R.id.register_btn_register);
        RegisterButton.setOnClickListener(this);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_register;
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_titleBar_iv_back://返回
                finish();
                break;
            case R.id.register_btn_getCode://获取邮箱验证码
                EmailNumberStr = RegisterEmailEditText.getText().toString();
                if (LoginAndForgetPasswordHelperUtil.isEmail(EmailNumberStr)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendCode(EmailNumberStr);
                        }
                    }).start();
                }else {
                    ToastUtils.ToastShortTime("邮箱输入不正确");
                }
                break;
            case R.id.register_btn_register://注册
                getInfo();//得到所有输入数据
                if (isLegalInfo()){//判断是否合法
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendRegisterOkHttp();
                        }
                    }).start();

                }else {
                    ToastUtils.ToastShortTime("输入参数不合法");
                }
                break;
            case R.id.register_et_school://选则学校
                showSchoolPickerDialog();
                break;
        }
    }
    private void showSchoolPickerDialog() {
        // 通过builder 构建器来构造
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选则学校");
        final String items[] = { "重庆邮电大学","重庆大学", "西南大学", "西南政法大学", "重庆邮电大学", "重庆师范大学", "重庆交通大学","重庆工商大学" };
        // -1代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // [1]把选择的条目给取出来
                String item = items[which];
//                Toast.makeText(getApplicationContext(), item, 1).show();
                SchoolStr = item;
                RegisterSchoolTextView.setText(item);
                // [2]把对话框关闭
                dialog.dismiss();
            }
        });

        // 最后一步 一定要记得 和Toast 一样 show出来
        builder.show();

    }
    private void sendRegisterOkHttp() {
        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
        RequestBody requestBody = new FormBody.Builder()
                .add("us_name",NameStr)
                .add("us_pwd",Password)
                .add("us_email",EmailNumberStr)
                .add("us_phone",PhoneNumberStr)
                .add("us_job_id",UserJobIDStr)
                .add("us_college",SchoolStr)
                .build();
//        Request request = new Request.Builder()
//                .url(HttpContants.REGISTER_COMMIT)
//                .post(requestBody)
//                .build();
//        try {

            String responseStr = okHttpUtils.SendPostRequst(requestBody,HttpContants.REGISTER_COMMIT);
            Log.d(TAG, "sendRegisterOkHttp: "+responseStr);
            switch (responseStr){
                case "邮箱已被注册":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.ToastShortTime("邮箱已被注册");
                        }
                    });
                    break;
                case "电话已被注册":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.ToastShortTime("电话已被注册");
                        }
                    });
                    break;
                case "该学生已被注册":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.ToastShortTime("该学生已被注册");
                        }
                    });
                    break;
                case "注册成功":
                    try {
                        EMClient.getInstance().createAccount(PhoneNumberStr,Password);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.ToastShortTime("注册成功");
                                finish();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    break;
            }
//        }  catch (HyphenateException e) {
//            e.printStackTrace();
//        }
    }




    private void getInfo() {
        NameStr = RegisterNameEditText.getText().toString();
        UserJobIDStr = RegisterUserJobIDEditText.getText().toString();

        PhoneNumberStr = RegisterPhoneEditText.getText().toString();
        EmailNumberStr = RegisterEmailEditText.getText().toString();
        CodeNumberStr = RegisterCodeEditText.getText().toString();
        Password = RegisterPasswordEditText.getText().toString();
        RePassword = RegisterRePasswordEditText.getText().toString();
    }

    private boolean isLegalInfo() {
        return LoginAndForgetPasswordHelperUtil.isPhoneNumber(PhoneNumberStr)&&
                LoginAndForgetPasswordHelperUtil.isEmail(EmailNumberStr)&&
                LoginAndForgetPasswordHelperUtil.isPasswordNumber(Password)&&
                TextUtils.equals(Password,RePassword)&&
                TextUtils.equals(CodeNumberStr,CodeStr)&&
                !TextUtils.isEmpty(NameStr)&&
                !TextUtils.isEmpty(UserJobIDStr)&&
                !TextUtils.isEmpty(SchoolStr);
    }

    private void sendCode(String emailNumberStr) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email",emailNumberStr)
                .build();
        Request request = new Request.Builder()
                .url(HttpContants.REGISTER_EMAIL_CODE)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!TextUtils.equals("邮箱已被注册",CodeStr = response.body().string())){
                //验证码发送成功，倒计时
                setCodeTimeDown();
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.ToastShortTime("邮箱已被注册");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCodeTimeDown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.ToastShortTime("验证码已成功发送至您的手机，请注意查收");
                RegisterCodeButton.setEnabled(false);
                final Timer timer = new Timer();
                SecCount = 60;
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SecCount--;
                                RegisterCodeButton.setText(SecCount+" s");
                                if (SecCount<=0) {
                                    timer.cancel();
                                    RegisterCodeButton.setText("重新发送");
                                    RegisterCodeButton.setEnabled(true);
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask,1000,1000);
            }
        });

    }
}
