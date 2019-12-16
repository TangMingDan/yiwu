package com.example.yiwu.ItemDecortion;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yiwu.R;

/**
 * <pre>
 *     author : 朱旭辉
 *     time   : 2019/08/31
 *     desc   :recyclerview修饰器，可以设置间隔
 */
public class MyItemDecortion extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //outRect为该item在上下左右撑开的距离，默认为 0；
        // view为item；
        // parent为RecyclerView本身；
        // state为RecyclerView状态，也可以通过其在各组件之间传递参数
    }
}
