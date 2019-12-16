package com.example.yiwu.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.SearchActivity;

import static com.chad.library.adapter.base.listener.SimpleClickListener.TAG;

/**
 * Created by 朱旭辉
 * Time  2019/09/07
 * Describe: 搜索Bar
 */
public class TitleSearchLayout extends RelativeLayout implements View.OnClickListener {
    LinearLayout titleBar_Search;
    public TitleSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_search,this);
        Log.d(TAG, "TitleSearchLayout: ");
        titleBar_Search = findViewById(R.id.titleBar_Search);
        titleBar_Search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titleBar_Search:
                SearchActivity.actionStart(YiWuApplication.getContext());
        }
    }
}
