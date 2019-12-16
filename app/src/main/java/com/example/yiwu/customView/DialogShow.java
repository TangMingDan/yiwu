package com.example.yiwu.customView;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.yiwu.R;
import com.example.yiwu.util.ScreenTools;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatDialog;

/**
 * Create by 朱鸿 on 2019/9/28
 * dialog用于放大显示图片
 * 修改 2019/10/8
 * 增加Theme设置因为AppCompatActivity对Theme的限制使用
 * */


public class DialogShow {
    private static final int IMG_SIZE_SCALE = 2;
    private static final int IMG_OFFSET_SCALE = 6;
    private static int styleId = R.style.dialog;
    private static final ArrayList<AppCompatDialog> LOADERS = new ArrayList<>();
    public static void showDialog(Context context,String imgUrl){
        final AppCompatDialog dialog = new AppCompatDialog(context,styleId);
        final ImageView img = new ImageView(context);
        Glide.with(context).load(imgUrl).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLoading();
            }
        });
        dialog.setContentView(img);
        ScreenTools tools = ScreenTools.instance(context);
        int deviceWidth = tools.getScreenWidth();
        int deviceHeight = tools.getScreenHeight();
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        final Window dialogWindow = dialog.getWindow();
        if(dialogWindow != null){
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = deviceWidth;
            lp.height = deviceHeight / IMG_SIZE_SCALE;
            lp.gravity = Gravity.CENTER;

        }
        LOADERS.add(dialog);
        dialog.show();
    }

    public static void stopLoading(){
        for (AppCompatDialog dialog:LOADERS) {
            if(dialog != null){
                if(dialog.isShowing()){
                    dialog.cancel();
                }
            }
        }
    }
    public static void dialogSetTheme(int mStyleId){
        styleId = mStyleId;
    }
}
