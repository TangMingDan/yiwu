package com.example.yiwu.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.activity.chat.Message;
import com.example.yiwu.activity.chat.MessageType;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.state.App;
import com.example.yiwu.util.LattePreference;

import org.litepal.LitePal;

import java.util.List;

import androidx.appcompat.widget.AppCompatTextView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.yiwu.http.HttpContants.BASE_URL_120;

/**
 * Created by 朱鸿 2019/10/23
 * MessageFragment中 recyclerview的适配器
 *
 */


public class ChatMessageUserAdapter extends BaseMultiItemQuickAdapter<User, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ChatMessageUserAdapter(List<User> data) {
        super(data);
        addItemType(0, R.layout.item_chat_fragment);
    }


    @Override
    protected void convert(BaseViewHolder helper, User item) {
        AppCompatTextView name = helper.getView(R.id.txt_chat_user_name);
        CircleImageView imgUser =  helper.getView(R.id.img_chat_user);
        AppCompatTextView lastTime = helper.getView(R.id.txt_chat_last_time);
        AppCompatTextView lastMsg = helper.getView(R.id.txt_chat_last_msg);
//        AppCompatTextView lastImg = helper.getView(R.id.txt_chat_last_msg);
        name.setText(item.getUs_name());
        List<Message> allMessage = LitePal.where("((fromId = ? and toId = ?) or (fromId = ? and toId = ?))",
                LattePreference.getCustomAppProfile(App.USERID),item.getUs_phone(),
                item.getUs_phone(), LattePreference.getCustomAppProfile(App.USERID))
                .order("timeMillis desc")  //以timeMillis排序，desc倒序，asc正序
                .limit(1) //限制获取的数量
                .find(Message.class);
//        Log.d("xxxxxxxxx","接收消息的id" + item.getUs_phone() + "当前登录的ID" +LattePreference.getCustomAppProfile(App.USERID));
        if(allMessage != null && !allMessage.isEmpty()) {
            Message message = allMessage.get(0);
            lastTime.setText(message.getTime());
            if (message.getType().equals(MessageType.TXT.name())) {
                lastMsg.setVisibility(View.VISIBLE);
//                lastImg.setVisibility(View.GONE);
                lastMsg.setText(message.getText());
            }else if (message.getType().equals(MessageType.IMAGE.name())){
                lastMsg.setVisibility(View.GONE);
//                lastImg.setVisibility(View.VISIBLE);
            }
            }
            Glide.with(mContext)
                    .load(HttpContants.IMAGE_URL_119 + item.getUs_headpic())
                    .into((ImageView) imgUser);
        }

}
