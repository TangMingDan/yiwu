package com.example.yiwu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.adapter.NineGridAdapter;
import com.example.yiwu.adapter.SailGoodsImageAdapter;
import com.example.yiwu.adapter.SendShareImageAdapter;
import com.example.yiwu.customView.PickerView;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 朱旭辉
 * Time  2019/11/19
 * Describe:上架物品
 */

public class SailGoodsActivity extends BaseActivity implements View.OnClickListener {
    private EditText goodsNameEdit;
    private EditText goodsPriceNameEdit;
    private EditText goodsContentEdit;
    private LinearLayout goodNumLayout;
    private LinearLayout goodsSchoolLayout;
    private LinearLayout goodsTagLayout;
    private TextView goodsNumTxt;
    private TextView goodsSchoolTxt;
    private TextView goodsTagTxt;
    private GridView goodsSailGridView;
    private TextView cancelTxt;
    private TextView sendTxt;
    private AlertDialog alertDialog = null;//进度条
    private User user;
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private SailGoodsImageAdapter sailGoodsImageAdapter;
    private PickerView pickerView;
    private String goodsNumStr;
    private String goodsSchoolStr;
    private String goodsNameStr;
    private String goodsPriceStr;
    private String goodsContentStr;
    private String goodsTagStr;
    private String goodsTagID;

    private final int SUCCESSFUL = 200;
    private int size = 0;//记录图片数量
    private Thread sendImageThread = new Thread(new Runnable() {
        @Override
        public void run() {
            sendImage();
        }
    });

    private void sendImage() {
        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        List<File> fileList = new ArrayList<>();//装文件
//            final String[] filePaths = new String[imageItems.size()];//装文件路径
        for (int i = 0; i < imageItems.size(); i++) {
//                filePaths[i] = imageItems.get(i).path;
            File img = new File(imageItems.get(i).path);
            fileList.add(img);
        }
        builder.addFormDataPart("owner_id",user.getUs_id());
        builder.addFormDataPart("size", size + "");
        builder.addFormDataPart("goods_name", goodsNameStr);
        builder.addFormDataPart("goods_price", goodsPriceStr);
        builder.addFormDataPart("goods_content", goodsContentStr);
        builder.addFormDataPart("goods_num", goodsNumStr);
        builder.addFormDataPart("goods_school", goodsSchoolStr);
        builder.addFormDataPart("goods_tag",goodsTagID);
        for (int i = 0; i < fileList.size(); i++) {
            builder.addFormDataPart("image" + i, fileList.get(i).getName(),
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            RequestBody.create(MediaType.parse("image/*"), fileList.get(i)));
        }
        RequestBody requestBody =builder.build();
        okHttpUtils.SendPostRequst(requestBody,HttpContants.SAIL_GOODS);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.ToastShortTime("发布成功");
                alertDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void initDate() {
        user = YiWuApplication.getInstance().getUser();
    }

    @Override
    protected void initView() {
        goodsNameEdit = findViewById(R.id.input_goods_name);
        goodsPriceNameEdit = findViewById(R.id.input_goods_price);
        goodsContentEdit = findViewById(R.id.input_content);
        goodNumLayout = findViewById(R.id.sail_goods_num_layout);
        goodNumLayout.setOnClickListener(this);
        goodsSchoolLayout = findViewById(R.id.sail_school_layout);
        goodsSchoolLayout.setOnClickListener(this);
        goodsTagLayout = findViewById(R.id.sail_tag_layout);
        goodsTagLayout.setOnClickListener(this);


        goodsNumTxt = findViewById(R.id.sail_goods_num_txt);
        goodsSchoolTxt = findViewById(R.id.sail_goods_school_txt);
        goodsTagTxt = findViewById(R.id.sail_goods_tag_txt);
        goodsSailGridView = findViewById(R.id.sailGridView);
        cancelTxt = findViewById(R.id.tv_cancle);
        cancelTxt.setOnClickListener(this);
        sendTxt = findViewById(R.id.tv_send);
        sendTxt.setOnClickListener(this);

        sailGoodsImageAdapter = new SailGoodsImageAdapter(imageItems);
        goodsSailGridView.setAdapter(sailGoodsImageAdapter);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_want_sail;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SailGoodsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sail_goods_num_layout://物品数量
                showNumPickerDialog();//显示数量学长对话框
                break;
            case R.id.sail_school_layout://学校
                showSchoolPickerDialog();//选则学校对话框
                break;
            case R.id.sail_tag_layout:
                Log.d(TAG, "onClick: 点了没得");
                showTagPickerDialog();//选则总类对话框
                break;
            case R.id.tv_cancle:
                finish();
                break;
            case R.id.tv_send:
                goodsNameStr = goodsNameEdit.getText().toString();
                goodsPriceStr = goodsPriceNameEdit.getText().toString();
                goodsContentStr = goodsContentEdit.getText().toString();
                goodsNumStr = goodsNumTxt.getText().toString();
                goodsSchoolStr = goodsSchoolTxt.getText().toString();
                goodsTagStr = goodsTagTxt.getText().toString();
                if (isLegal()) {
                    sendTxt.setEnabled(false);
                    //上传服务器
                    tv_upload_database();
                }
                break;

        }
    }

    private void showTagPickerDialog() {
        // 通过builder 构建器来构造
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选则物品种类");
        final String items[] = {"图书", "家电", "美食", "数码", "文具", "服务", "游戏", "其他"};        // -1代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // [1]把选择的条目给取出来
                String item = items[which];
//                Toast.makeText(getApplicationContext(), item, 1).show();
                goodsTagID = (which+1)+"";
                goodsTagStr = item;
                goodsTagTxt.setText(item);
                // [2]把对话框关闭
                dialog.dismiss();
            }
        });
        // 最后一步 一定要记得 和Toast 一样 show出来
        builder.show();
    }

    private void tv_upload_database() {
        //隐藏软硬盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        ShowDialog();
        sendImageThread.start();
    }

    private void ShowDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("加载中...")
                .setView(R.layout.dialog_load)//加载中
                .setCancelable(false)
                .show();
    }

    private boolean isLegal() {
        if (goodsNameStr.length() > 15 || goodsNameStr.length() == 0) {
            ToastUtils.ToastShortTime("物品名称输入不合法");
            return false;
        }
        if (TextUtils.isEmpty(goodsPriceStr)) {
            ToastUtils.ToastShortTime("物品价格输入不合法");
            return false;
        }
        if (goodsContentStr.length() > 100 || goodsContentStr.length() == 0) {
            ToastUtils.ToastShortTime("物品详细信息输入不合法");
            return false;
        }
        if (goodsNumStr.equals("选则物品数量")) {
            ToastUtils.ToastShortTime("物品数量选则不合法");
            return false;
        }
        if (goodsSchoolStr.equals("选则上架学校")) {
            ToastUtils.ToastShortTime("学校选则不合法");
            return false;
        }
        if (goodsTagStr.equals("选则物品种类")) {
            ToastUtils.ToastShortTime("物品种类选则不合法");
            return false;
        }
        if (size <= 0) {
            ToastUtils.ToastShortTime("请选则上传图片");
            return false;
        }
        return true;
    }

    private void showSchoolPickerDialog() {
        // 通过builder 构建器来构造
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选则学校");
        final String items[] = {"重庆邮电大学", "重庆大学", "西南大学", "西南政法大学", "重庆邮电大学", "重庆师范大学", "重庆交通大学", "重庆工商大学"};        // -1代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // [1]把选择的条目给取出来
                String item = items[which];
//                Toast.makeText(getApplicationContext(), item, 1).show();
                goodsSchoolStr = item;
                goodsSchoolTxt.setText(item);
                // [2]把对话框关闭
                dialog.dismiss();
            }
        });
        // 最后一步 一定要记得 和Toast 一样 show出来
        builder.show();
    }


    private void showNumPickerDialog() {
        goodsNumStr = "1";//默认值为1
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //初始化自定义布局参数
        LayoutInflater layoutInflater = getLayoutInflater();
        final View customLayout = layoutInflater.inflate(R.layout.dialog_choice_num, (ViewGroup) findViewById(R.id.customDialog));
        //为对话框设置视图
        builder.setView(customLayout);
        pickerView = customLayout.findViewById(R.id.picker);
        //定义滚动选择器的数据项
        List<String> num = new ArrayList<>();
        for (int i = 1; i < 51; i++) {
            num.add(i + "");
        }
        //为滚动选择器设置数据
        pickerView.setData(num);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                goodsNumStr = text;
            }
        });

        //对话框的确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goodsNumTxt.setText(goodsNumStr);
            }
        });
        //对话框的取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        //显示对话框
        builder.show();
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
                for (int i = 0; i < imageItemsGet.size(); i++) {
                    imageItems.add(imageItemsGet.get(i));
                }
                sailGoodsImageAdapter.notifyDataSetChanged();
                size = imageItems.size();
            } else {
                ToastUtils.ToastShortTime("没有选择图片");
            }
        }
    }
}
