package com.example.yiwu.activity.chat;


import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.state.App;
import com.example.yiwu.R;
import com.example.yiwu.util.LattePreference;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.yiwu.http.HttpContants.IMAGE_URL_119;

/**
 * created by 朱鸿 2019/10/21
 * Message的适配器用于处理消息内容和头像昵称
 *
 */


public class MessageAdapter extends BaseMultiItemQuickAdapter<Message, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */

    private String chatUserImg = null;   //聊天对象的头像
    private String chatUserName = null;  //聊天对象的昵称


    public MessageAdapter(List<Message> data) {
        super(data);
        addItemType(0, R.layout.item_chat_message);
    }

    @Override
    protected void convert(BaseViewHolder helper, Message item) {
        String fromId = item.getFromId();
        String toId = item.getToId();
        //如果fromId 等于当前登录id,则消息在右侧,消息为文本
        if(fromId.equals(LattePreference.getCustomAppProfile(App.USERID)) && item.getType().equals(MessageType.TXT.name())){
            helper.getView(R.id.layout_chat_right).setVisibility(View.VISIBLE);
            helper.getView(R.id.txt_chat_right_img).setVisibility(View.GONE);
            helper.setText(R.id.txt_chat_right_message,item.getText());
            helper.setText(R.id.txt_chat_right_time,String.valueOf(item.getTime()));
            helper.getView(R.id.layout_chat_left).setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(IMAGE_URL_119 + LattePreference.getCustomAppProfile(App.USERIMG))
                    .into((ImageView) helper.getView(R.id.img_userto));
            helper.setText(R.id.txt_userto,LattePreference.getCustomAppProfile(App.USERNAME));
//            Log.d("1234567890","发送消息");
        //消息为图片
        }else if(fromId.equals(LattePreference.getCustomAppProfile(App.USERID)) && item.getType().equals(MessageType.IMAGE.name())){
            helper.getView(R.id.layout_chat_right).setVisibility(View.VISIBLE);
            helper.getView(R.id.layout_chat_left).setVisibility(View.GONE);
            helper.getView(R.id.txt_chat_right_message).setVisibility(View.GONE);
//            helper.getView(R.id.txt_chat_right_img).setVisibility(View.GONE);

            helper.setText(R.id.txt_chat_right_time,item.getTime());
            if(item.getImgUrl().contains("/storage")){
                File file = new File(item.getImgUrl());
//                Log.d("1234567890","加载本地图片：" + item.getImgUrl());
                Glide.with(mContext)
                        .load(file)
                        .into((ImageView) helper.getView(R.id.txt_chat_right_img));
            }else {
//                Log.d("1234567890","加载云端图片：" + item.getImgUrl());
                Glide.with(mContext)
                        .load(item.getImgUrl())
                        .into((ImageView) helper.getView(R.id.txt_chat_right_img));
            }

            Glide.with(mContext)
                    .load(IMAGE_URL_119 + LattePreference.getCustomAppProfile(App.USERIMG))
                    .into((ImageView) helper.getView(R.id.img_userto));
            helper.setText(R.id.txt_userto,LattePreference.getCustomAppProfile(App.USERNAME));

        //如果接收消息在左侧
        }else if(!fromId.equals(LattePreference.getCustomAppProfile(App.USERID)) && item.getType().equals(MessageType.TXT.name())){
            helper.getView(R.id.layout_chat_left).setVisibility(View.VISIBLE);
            helper.getView(R.id.txt_chat_left_img).setVisibility(View.GONE);
            helper.setText(R.id.txt_chat_left_message,item.getText());
            helper.setText(R.id.txt_chat_left_time,String.valueOf(item.getTime()));

            helper.getView(R.id.layout_chat_right).setVisibility(View.GONE);
//            Log.d("1234567890","接收消息");
            Glide.with(mContext)
                    .load(IMAGE_URL_119 + chatUserImg)
                    .into((ImageView) helper.getView(R.id.img_userfrom));
            helper.setText(R.id.txt_userfrom,chatUserName);

        }else if(!fromId.equals(LattePreference.getCustomAppProfile(App.USERID)) && item.getType().equals(MessageType.IMAGE.name())){
            helper.getView(R.id.layout_chat_left).setVisibility(View.VISIBLE);
            helper.getView(R.id.txt_chat_left_message).setVisibility(View.GONE);
            helper.setText(R.id.txt_chat_left_time,item.getTime());
            Glide.with(mContext)
                    .load(item.getImgUrl())
                    .into((ImageView) helper.getView(R.id.txt_chat_left_img));

            helper.getView(R.id.layout_chat_right).setVisibility(View.GONE);
//            Log.d("1234567890","接收图片");
            Glide.with(mContext)
                    .load(IMAGE_URL_119 + chatUserImg)
                    .into((ImageView) helper.getView(R.id.img_userfrom));
            helper.setText(R.id.txt_userfrom,chatUserName);
        }
    }

    public String getChatUserImg() {
        return chatUserImg;
    }

    public void setChatUserImg(String chatUserImg) {
        this.chatUserImg = chatUserImg;
    }

    public String getChatUserName() {
        return chatUserName;
    }

    public void setChatUserName(String chatUserName) {
        this.chatUserName = chatUserName;
    }
}