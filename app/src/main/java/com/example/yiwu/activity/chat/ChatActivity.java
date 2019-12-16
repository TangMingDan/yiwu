package com.example.yiwu.activity.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.yiwu.YiWuApplication;
import com.example.yiwu.state.App;
import com.example.yiwu.R;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.util.LattePreference;
import com.example.yiwu.util.ShareImageLoader;
import com.example.yiwu.util.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 朱鸿
 * Time  2019/11/15
 * Describe:聊天页面
 * 修改：增加点击图片功能 by朱旭辉 2019/12/15
 */
public class ChatActivity extends BaseActivity implements EMMessageListener, View.OnClickListener {
    private LinearLayoutCompat layoutChatPhotos;
    private LinkedList<Message> messageList = null;
    private MessageAdapter mAdapter = null;
    private RecyclerView mRecyclerView = null;
    private EditText input = null;
    private SmartRefreshLayout refreshLayout = null;
    private Button send = null;
    private int mCount = 10; //每次刷新获取消息的数量
    private int offsetCount = 0; //每次刷新的偏移量
    private final int SUCCESSFUL = 200;
    private String userId = null;
    private long firstTime = 0; //当前页面显示的最早消息的时间
    private ChatUser chatUser = null;


    private ArrayList<ImageItem> imageItems = new ArrayList<>();//装图片
    private int size = 0;//记录图片数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initDate() {
        userId = getIntent().getStringExtra("userId");

        EMClient.getInstance().chatManager().loadAllConversations();   //将本地数据库加载到内存中
        EMConversation conversation = EMClient.getInstance().chatManager()
                .getConversation(userId, EMConversation.EMConversationType.Chat, true);
        List<EMMessage> messages = conversation.getAllMessages();
        messageList = new LinkedList<>();
        mAdapter = new MessageAdapter(messageList);
        if (!messages.isEmpty()) {
            firstTime = messages.get(0).getMsgTime();
            try {
                chageToMessage(messages);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
//        LitePal.deleteAll(Message.class);
    }

    @Override
    protected void initView() {
        layoutChatPhotos = findViewById(R.id.layout_chat_photos);
        layoutChatPhotos.setOnClickListener(this);


        mRecyclerView = findViewById(R.id.rv_chat);
        final LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        refreshLayout = findViewById(R.id.rf_chat_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                //从本地数据库中获取消息
                List<Message> allMessage = LitePal.where("((fromId = ? and toId = ?) or (fromId = ? and toId = ?)) and timeMillis < ?",
                        LattePreference.getCustomAppProfile(App.USERID), userId,
                        userId, LattePreference.getCustomAppProfile(App.USERID), String.valueOf(firstTime))
                        .order("timeMillis desc")  //以timeMillis排序，desc倒序，asc正序
                        .limit(mCount) //限制获取的数量
                        .offset(offsetCount)
                        .find(Message.class);
                if (allMessage.size() >= mCount) {
                    offsetCount += 10;
                }
                if (!allMessage.isEmpty()) {
                    for (Message message : allMessage) {
                        messageList.addFirst(message);
                    }
                    int size = allMessage.size();
                    firstTime = allMessage.get(size - 1).getTimeMillis();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(0);
                        refreshLayout.finishRefresh();
                    }
                });
            }
        });
        if (!messageList.isEmpty()) {
            mRecyclerView.scrollToPosition(0);
        }
        input = findViewById(R.id.edit_chat_input);
        send = findViewById(R.id.btn_chat_send);
        send.setOnClickListener(this);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_chat;
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        try {
            chageToMessage(list);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    private void chageToMessage(List<EMMessage> list) throws HyphenateException {
        if (!list.isEmpty()) {
            for (EMMessage emMessage : list) {
                Message message = new Message();
                message.setToId(emMessage.getTo());
                message.setFromId(emMessage.getFrom());

                long timeMillis = emMessage.getMsgTime();
                message.setTimeMillis(timeMillis);
                message.setTime(chageTime(timeMillis));

                if (emMessage.getType().name().equals(MessageType.TXT.name())) {
                    message.setType(MessageType.TXT.name());
                    message.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
                } else if (emMessage.getType().name().equals(MessageType.IMAGE.name())) {
                    message.setType(MessageType.IMAGE.name());
                    message.setImgUrl(((EMImageMessageBody) emMessage.getBody()).getRemoteUrl());
//                    Log.d("1234567890","获取图片的地址为：" +
//                            (((EMImageMessageBody) emMessage.getBody()).getFileName() +
//                                    "----------------------------------------" +
//                                    ((EMImageMessageBody) emMessage.getBody()).thumbnailLocalPath() +
//                                    "--------------------------------------------" +
//                                    ((EMImageMessageBody) emMessage.getBody()).toString()));
                }
                List<Message> theMessage = LitePal.where("timeMillis < ? and timeMillis > ?",
                        String.valueOf(emMessage.getMsgTime() + 10), String.valueOf(emMessage.getMsgTime() - 10))
                        .find(Message.class);
                if (theMessage.isEmpty()) {
                    message.save();
                }
//                LitePal.deleteAll(ChatUser.class);
                List<ChatUser> chatUserList = LitePal.where("userId = ?", userId).find(ChatUser.class);
                if (chatUserList.isEmpty()) {
                    chatUser = new ChatUser();
                    try {
                        chatUser.setUserName(emMessage.getStringAttribute(App.USERNAME.name()));
                        chatUser.setUserImg(emMessage.getStringAttribute(App.USERIMG.name()));
                        chatUser.setUserId(userId);
                        chatUser.save();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                } else {
                    chatUser = chatUserList.get(0);
                }

                if (chatUser.getUserImg() != null && !chatUser.getUserImg().isEmpty()) {
                    mAdapter.setChatUserImg(chatUser.getUserImg());
                }
                if (chatUser.getUserName() != null) {
                    mAdapter.setChatUserName(chatUser.getUserName());
                }
                messageList.add(message);
            }
        }
        EMClient.getInstance().chatManager().importMessages(list);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        try {
            chageToMessage(list);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    public static void actionStart(Context context, String userId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_chat_send:
                String content = input.getText().toString();
                if (content.equals("")) {
                    ToastUtils.ToastShortTime("输入消息内容不能为空");
                } else {
                    final EMMessage emessage = EMMessage.createTxtSendMessage(content, userId);

                    final List<EMMessage> emMessageList = new ArrayList<>();
                    emMessageList.add(emessage);

                    emessage.setChatType(EMMessage.ChatType.Chat);  //设置消息类型
                    //设置属性,发送自己的头像
                    emessage.setAttribute(App.USERIMG.name(), LattePreference.getCustomAppProfile(App.USERIMG));

                    //发送id
                    emessage.setAttribute(App.USERID.name(), LattePreference.getCustomAppProfile(App.USERID));

                    //发送昵称
                    emessage.setAttribute(App.USERNAME.name(), LattePreference.getCustomAppProfile(App.USERNAME));

                    EMClient.getInstance().chatManager().sendMessage(emessage);

                    emessage.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            final Message message = new Message();
                            message.setType(MessageType.TXT.name());
                            message.setText(input.getText().toString());
                            message.setFromId(LattePreference.getCustomAppProfile(App.USERID));
                            message.setToId(userId);

                            long timeMillis = emessage.getMsgTime();
                            message.setTime(chageTime(timeMillis));
                            message.setTimeMillis(timeMillis);

                            message.save();
                            messageList.add(message);

                            EMClient.getInstance().chatManager().importMessages(emMessageList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    mRecyclerView.scrollToPosition(messageList.size() - 1);
                                    input.setText("");
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
                break;
            case R.id.layout_chat_photos:
                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new ShareImageLoader());
                imagePicker.setMultiMode(true);   //多选
                imagePicker.setShowCamera(true);  //显示拍照按钮
                imagePicker.setSelectLimit(9);    //最多选择X张
                imagePicker.setCrop(false);       //不进行裁剪
                Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
                BaseActivity.getCurrentActivity().startActivityForResult(intent, SUCCESSFUL);
                break;
            default:
                break;
        }
    }

    //将毫秒时间格式化为正常时间
    private String chageTime(long timeMillis) {
        Date date = new Date();
        date.setTime(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        return time;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final List<File> fileList = new ArrayList<>();//装文件
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == SUCCESSFUL) {
                //用于转换
                ArrayList<ImageItem> imageItemsGet = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItems.clear();
                for (int i = 0; i < imageItemsGet.size(); i++) {
                    imageItems.add(imageItemsGet.get(i));
                }
                size = imageItems.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        final File img = new File(imageItems.get(i).path);
                        fileList.add(img);
                        final EMMessage emessage = EMMessage.createImageSendMessage(img.getPath(), false, userId);
                        final List<EMMessage> emMessageList = new ArrayList<>();
                        emMessageList.add(emessage);

                        emessage.setChatType(EMMessage.ChatType.Chat);  //设置消息类型
                        //设置属性,发送自己的头像
                        emessage.setAttribute(App.USERIMG.name(), LattePreference.getCustomAppProfile(App.USERIMG));

                        //发送id
                        emessage.setAttribute(App.USERID.name(), LattePreference.getCustomAppProfile(App.USERID));

                        //发送昵称
                        emessage.setAttribute(App.USERNAME.name(), LattePreference.getCustomAppProfile(App.USERNAME));

                        EMClient.getInstance().chatManager().sendMessage(emessage);

                        emessage.setMessageStatusCallback(new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                final Message message = new Message();
                                message.setType(MessageType.IMAGE.name());
                                message.setImgUrl(img.getPath());
                                message.setFromId(LattePreference.getCustomAppProfile(App.USERID));
                                message.setToId(userId);

                                long timeMillis = emessage.getMsgTime();
                                message.setTime(chageTime(timeMillis));
                                message.setTimeMillis(timeMillis);
//                            message.save();
                                messageList.add(message);

                                EMClient.getInstance().chatManager().importMessages(emMessageList);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                        mRecyclerView.scrollToPosition(messageList.size() - 1);
//                                    input.setText("");
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.d("xxxxxxxxxxxxxxxxx", s + i);
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }
                }
            } else {
                ToastUtils.ToastShortTime("没有选择图片");
            }
        }
    }

}
