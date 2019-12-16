package com.example.yiwu.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.adapter.SendShareImageAdapter;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 朱旭辉
 * Time  2019/9/15
 * Describe:发朋友圈
 */

public class SendShareImageActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_send;//
    private GridView publishGridView;
    private TextView tv_upload,tv_cancle;//发送与取消
    private int size = 0;//记录图片数量
    private String content;//内容
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private AlertDialog alertDialog = null;//进度条
    private SendShareImageAdapter sendShareImageAdapter;
    private final int SUCCESSFUL = 200;


    private Thread sendImageThread = new Thread(new Runnable() {
        @Override
        public void run() {
            sendImage();
        }
    });

    private void sendImage() {
        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (size>0){
            List<File> fileList = new ArrayList<>();//装文件
//            final String[] filePaths = new String[imageItems.size()];//装文件路径
            for (int i = 0; i < imageItems.size(); i++) {
//                filePaths[i] = imageItems.get(i).path;
                File img = new File(imageItems.get(i).path);
                fileList.add(img);
            }
            Log.d(TAG, "sendImage: id==="+ YiWuApplication.getInstance().getUser().getUs_id());
            builder.addFormDataPart("owner_id", YiWuApplication.getInstance().getUser().getUs_id());
            builder.addFormDataPart("size",size+"");
            builder.addFormDataPart("talk_describe",content);
            for (int i = 0;i<fileList.size();i++) {
                builder.addFormDataPart("image"+i,fileList.get(i).getName(),
                // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                RequestBody.create(MediaType.parse("image/*"),fileList.get(i)));
            }

            RequestBody requestBody =builder.build();

                okHttpUtils.SendPostRequst(requestBody,HttpContants.SEND_SHAREYIXIA);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.ToastShortTime("发布成功");
                        alertDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.ToastShortTime("上传失败");
                    alertDialog.dismiss();
                    finish();
                }
            });
        }
    }

    private void ShowDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("加载中...")
                .setView(R.layout.dialog_load)//加载中
                .setCancelable(false)
                .show();
    }

    @Override
    protected void initDate() {

    }

    @Override
    protected void initView() {
        et_send= (EditText) findViewById(R.id.et_content);
        tv_upload= (TextView) findViewById(R.id.tv_send);
        tv_cancle=findViewById(R.id.tv_cancle);
        publishGridView= (GridView) findViewById(R.id.publishGridView);
        sendShareImageAdapter = new SendShareImageAdapter(imageItems);
        publishGridView.setAdapter(sendShareImageAdapter);
        tv_upload.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_send_shareimage;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_send:
                content = et_send.getText().toString();
                if (content.length() < 1 || size == 0) {
                    ToastUtils.ToastShortTime("发表内容或图片不能为空");
                } else {
                    tv_upload.setEnabled(false);
                    //上传服务器
                    tv_upload_database();
                }
                break;
            case R.id.tv_cancle:
                finish();
                break;
            default:
                break;
        }
    }

    private void tv_upload_database() {
        //隐藏软硬盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        ShowDialog();
        sendImageThread.start();
    }
    public static void actionStart(Context context){
        Intent intent = new Intent(context, SendShareImageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","执行了");
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {

            if (data != null && requestCode == SUCCESSFUL) {
                //用于转换
                ArrayList<ImageItem> imageItemsGet = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItems.clear();
                for (int i=0;i<imageItemsGet.size();i++){
                    imageItems.add(imageItemsGet.get(i));
                }
                sendShareImageAdapter.notifyDataSetChanged();
                size=imageItems.size();
            } else {
                ToastUtils.ToastShortTime("没有选择图片");
            }
        }
    }
}
