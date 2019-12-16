package com.example.yiwu.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.yiwu.R;

import com.example.yiwu.activity.BaseActivity;
/**
 * Created by 朱旭辉
 * Time  2019/10/29
 * Describe:普通的title
 */
public class OrdinaryTitle extends LinearLayout implements View.OnClickListener {
    private TextView back;
    private TextView titileInfo;
    public OrdinaryTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.header_goodsinfo,this);
        titileInfo = findViewById(R.id.goods_info_txt);

        back = findViewById(R.id.header_back);
        back.setOnClickListener(this);
    }
    public void setTitleName(String titleName){
        titileInfo.setText(titleName);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.header_back:
                BaseActivity.getCurrentActivity().finish();
        }
    }
}
