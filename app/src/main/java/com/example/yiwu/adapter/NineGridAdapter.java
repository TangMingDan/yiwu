package com.example.yiwu.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yiwu.Info.ClassifyImage;
import com.example.yiwu.Info.ShareInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.LoginActivity;
import com.example.yiwu.activity.MainActivity;
import com.example.yiwu.customView.CustomImageView;
import com.example.yiwu.customView.DialogShow;
import com.example.yiwu.customView.NineGridLayout;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ScreenTools;
import com.example.yiwu.util.ToastUtils;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.yiwu.fragment.BaseFragment.TAG;

/**
 * Created by 朱旭辉
 * Time  2019/9/2
 * Describe:九宫格展示的Adapter
 * 修改 2019/10/8 朱鸿
 * 增加点击图片放大功能
 */

public class NineGridAdapter extends BaseAdapter {
    private Context context;
   //private List<List<ClassifyImage>> datalist=new ArrayList<>();//总图片集
    private List<ShareInfo> shareInfoList;
    //private ArrayList<ClassifyImage> itemList=new ArrayList<>();//单张图片集


    public NineGridAdapter(Context context, List<ShareInfo> shareInfoList) {
        this.context = context;
        this.shareInfoList = shareInfoList;
//        for (int i=0;i<shareInfoList.size();i++){
//            itemList = new ArrayList<>();
//            for (int j=0;j<shareInfoList.get(i).getTalk_pic_addr().size();j++){
//                itemList.add(new ClassifyImage(HttpContants.IMAGE_URL_119+shareInfoList.get(i).getTalk_pic_addr().get(j),200,200));
//            }
//            datalist.add(itemList);
//        }

    }

    @Override
    public int getCount() {
        return shareInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return  shareInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final ArrayList<ClassifyImage> imageList = new ArrayList<>();
        /**
         * imageList 只能在这里加载，因为如果在构造函数中加载那刷新过后imageList是不会更新的，导致图片于描述不对应
         */
        for (int j=0;j<shareInfoList.get(position).getTalk_pic_addr().size();j++){
            imageList.add(new ClassifyImage(HttpContants.IMAGE_URL_119+shareInfoList.get(position).getTalk_pic_addr().get(j),200,200));
        }
        //List<ClassifyImage> imageList = datalist.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ninegridlayout,parent,false);
            viewHolder = new ViewHolder();
            //NineGridlayout
            viewHolder.ivMore = (NineGridLayout) convertView.findViewById(R.id.iv_ngrid_layout);
            //得到CustomImageView
            viewHolder.ivOne = (CustomImageView) convertView.findViewById(R.id.iv_oneimage);
            //发布内容
            viewHolder.share_content = convertView.findViewById(R.id.share_content);
            //发布时间
            viewHolder.share_time = convertView.findViewById(R.id.share_time);
            //发布头像
            viewHolder.share_head = convertView.findViewById(R.id.share_head);
            //发布名字
            viewHolder.share_name = convertView.findViewById(R.id.share_name);
            //喜欢图标
            viewHolder.likeGood = convertView.findViewById(R.id.good);
            //喜欢人数
            viewHolder.likeNum = convertView.findViewById(R.id.like_num_txt);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.likeThumb.setUnLikeType(ThumbUpView.LikeType.broken);
//        viewHolder.likeThumb.setOnThumbUp(new ThumbUpView.OnThumbUp() {
//            @Override
//            public void like(boolean like) {
//                Log.d(TAG, "like: =="+like);
//                if (YiWuApplication.getInstance().getUser() == null){
//                    viewHolder.likeThumb.UnLike();
//                    LoginActivity.actionStart(YiWuApplication.getContext());
//                }else {
//                    final OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
//                    final RequestBody requestBody;
//                    if (like){
//                        viewHolder.likeNum.setText((Integer.valueOf(viewHolder.likeNum.getText().toString())+1)+"");
//                        requestBody = new FormBody.Builder()
//                                .add("like_user_id", YiWuApplication.getInstance().getUser().getUs_id())
//                                .add("talk_id",shareInfoList.get(position).getTalk_id())
//                                .build();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                okHttpUtils.SendPostRequst(requestBody,HttpContants.SHARELIKEADD);
//                            }
//                        }).start();
//
//                    }else{
//                        viewHolder.likeNum.setText((Integer.valueOf(viewHolder.likeNum.getText().toString())-1)+"");
//                        requestBody = new FormBody.Builder()
//                                .add("like_user_id", YiWuApplication.getInstance().getUser().getUs_id())
//                                .add("talk_id",shareInfoList.get(position).getTalk_id())
//                                .build();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                okHttpUtils.SendPostRequst(requestBody,HttpContants.SHARELIKEDEL);
//                            }
//                        }).start();
//
//                    }
//
//                }
//
//            }
//        });
        viewHolder.likeGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (YiWuApplication.getInstance().getUser() == null){
                    LoginActivity.actionStart(YiWuApplication.getContext());
                }else {
                    final OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                    final RequestBody requestBody;
                    if ( !shareInfoList.get(position).getUser_like_or_not()){//如果没有点过赞
                        viewHolder.likeNum.setText((Integer.valueOf(viewHolder.likeNum.getText().toString())+1)+"");
                        requestBody = new FormBody.Builder()
                                .add("like_user_id", YiWuApplication.getInstance().getUser().getUs_id())
                                .add("talk_id",shareInfoList.get(position).getTalk_id())
                                .build();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                okHttpUtils.SendPostRequst(requestBody,HttpContants.SHARELIKEADD);
                            }
                        }).start();
                        shareInfoList.get(position).setUser_like_or_not(true);
                        viewHolder.likeGood.setImageResource(R.mipmap.good_checked);
                    }else{//如果点过赞了
                        viewHolder.likeNum.setText((Integer.valueOf(viewHolder.likeNum.getText().toString())-1)+"");
                        requestBody = new FormBody.Builder()
                                .add("like_user_id", YiWuApplication.getInstance().getUser().getUs_id())
                                .add("talk_id",shareInfoList.get(position).getTalk_id())
                                .build();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                okHttpUtils.SendPostRequst(requestBody,HttpContants.SHARELIKEDEL);
                            }
                        }).start();
                        shareInfoList.get(position).setUser_like_or_not(false);
                        viewHolder.likeGood.setImageResource(R.mipmap.good);
                    }

                }

            }
        });
        if (shareInfoList.get(position).getUser_like_or_not()){
            viewHolder.likeGood.setImageResource(R.mipmap.good_checked);
        }else {
            viewHolder.likeGood.setImageResource(R.mipmap.good);
        }
//        viewHolder.likeThumb.Like();
//        viewHolder.likeThumb.UnLike();
//        Log.d(TAG, "getView: count=="+shareInfoList.get(position).getTalk_like());
//        Log.d(TAG, "getView: like=="+shareInfoList.get(position).getUser_like_or_not());
        viewHolder.likeNum.setText(shareInfoList.get(position).getTalk_like());
        viewHolder.share_name.setText(shareInfoList.get(position).getOwner_name());
        viewHolder.share_time.setText(shareInfoList.get(position).getTalk_time());
        Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+shareInfoList.get(position).getOwner_head()).into(viewHolder.share_head);
        viewHolder.share_content.setText(shareInfoList.get(position).getTalk_describe());
        if (imageList.isEmpty()) {//没有图片，隐形
            viewHolder.ivMore.setVisibility(View.GONE);
            viewHolder.ivOne.setVisibility(View.GONE);
        } else if (imageList.size() == 1) {//有一张图片NineGridlayout隐形
            viewHolder.ivMore.setVisibility(View.GONE);
            viewHolder.ivOne.setVisibility(View.VISIBLE);
            viewHolder.ivOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String imgUrl = imageList.get(0).getUrl();
                    DialogShow.dialogSetTheme(R.style.dialogT);
                    DialogShow.showDialog(context,imgUrl);
                }
            });
            handlerOneImage(viewHolder, imageList.get(0));
        } else {//CustomImageView隐形
            viewHolder.ivMore.setVisibility(View.VISIBLE);
            viewHolder.ivOne.setVisibility(View.GONE);

            viewHolder.ivMore.setImagesData(imageList);
        }
        return convertView;
    }


    private void handlerOneImage(ViewHolder viewHolder, ClassifyImage image) {
        int totalWidth;
        int imageWidth;
        int imageHeight;
        ScreenTools screentools = ScreenTools.instance(context);
        totalWidth = screentools.getScreenWidth() - screentools.dip2px(80);
        imageWidth = screentools.dip2px(image.getWidth());//应该是获取image宽度
        imageHeight = screentools.dip2px(image.getHeight());//应该是获取image高度
        if (image.getWidth() <= image.getHeight()) {//图片如果高度大于宽度
            if (imageHeight > totalWidth) {//图片如果高度大于总高度，调整缩放比例
                imageHeight = totalWidth;
                imageWidth = (imageHeight * image.getWidth()) / image.getHeight();
            }
        } else {
            if (imageWidth > totalWidth) {//如过宽度大于总宽度调整缩放比例
                imageWidth = totalWidth;
                imageHeight = (imageWidth * image.getHeight()) / image.getWidth();
            }
        }
        //设置一张图片时的高度和宽度
        ViewGroup.LayoutParams layoutparams = viewHolder.ivOne.getLayoutParams();
        layoutparams.height = imageHeight;
        layoutparams.width = imageWidth;
        viewHolder.ivOne.setLayoutParams(layoutparams);
        viewHolder.ivOne.setClickable(true);
        viewHolder.ivOne.setScaleType(ImageView.ScaleType.FIT_XY);
        viewHolder.ivOne.setImageUrl(image.getUrl());

    }

    class ViewHolder {
        public NineGridLayout ivMore;
        public CustomImageView ivOne;
        public CircleImageView share_head;
        public TextView share_name;
        public TextView share_content;
        public TextView share_time;
        public ImageView likeGood;
        public TextView likeNum;
    }
}
