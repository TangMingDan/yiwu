package com.example.yiwu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.customView.CollectionPopupWindow;
import com.example.yiwu.state.AddState;
import com.example.yiwu.state.App;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.LoginActivity;
import com.example.yiwu.activity.chat.ChatActivity;
import com.example.yiwu.adapter.ChatMessageUserAdapter;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.LattePreference;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.example.yiwu.util.callback.CallbackManger;
import com.example.yiwu.util.callback.CallbackType;
import com.example.yiwu.util.callback.IGlobalCallback;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:消息碎片，及时通信用
 * 修改 10/23-10/24  朱鸿
 * 基本完成实际的通讯功能
 * 修改10/31 朱鸿
 * 完成初始化聊天对象和添加删除聊天对象的功能
 */
public class MessageFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView = null;
    private AppCompatImageView addChat = null;
    private ChatMessageUserAdapter mAdapter = null;
    private List<User> userList = null;

    private AlertDialog dialog = null;
    private AppCompatButton cancel;
    private AppCompatButton decide;
    private AppCompatEditText addUserId;
    private RefreshLayout refreshLayout;

    private String mUserId = null;   //当前的userId
    private LinearLayout nothingLayout;
    private TextView mNomessage = null;  //没有登录时显示的View

    private AddState addState = null;  //用户判断添加状态

    private String mReSponseStr = null;

    private CollectionPopupWindow popupWindow;//删除对话框
    private int deletePosition = 0; //要删除的聊天用户的位置


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    protected void initView() {
        addChat = super.view.findViewById(R.id.chat_add);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                User user = userList.get(position);
                ChatActivity.actionStart(getContext(),user.getUs_phone());
            }
        });
        refreshLayout = super.view.findViewById(R.id.smart_refresh_message);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getChatPeopleData();
                    }
                });
                thread.start();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        addChat.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        String mCurrentId = LattePreference.getCustomAppProfile(App.USERID);
        if(mCurrentId == null){      // 发现已经退出之后进行的操作
            userList.clear();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.GONE);
                    mNomessage.setText("您未登录，请先登录！");
                    nothingLayout.setVisibility(View.VISIBLE);
                }
            });
        }else {
           if(!mCurrentId.equals(mUserId)){    //发现用户已经更改进行的操作
               userList.clear();
               mUserId = mCurrentId;     //将当前的userId进行更新
               Thread thread = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       getChatPeopleData();
                   }
               });
               thread.start();
           }
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   mAdapter.notifyDataSetChanged();
               }
           });
        }
    }

    @Override
    protected void initDate() {
        userList = new ArrayList<>();
        mAdapter = new ChatMessageUserAdapter(userList);
        mUserId = LattePreference.getCustomAppProfile(App.USERID);
        mRecyclerView = super.view.findViewById(R.id.rv_messages);
        nothingLayout = super.view.findViewById(R.id.nothing_layout);
        mNomessage = super.view.findViewById(R.id.txt_no);
        mNomessage.setText("暂无消息,请登录！");
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                deletePosition = position;
                initDeletePopupWindow(); //初始化删除窗口
                return true;
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        if(mUserId == null){
            mUserId = "0000";
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nothingLayout.setVisibility(View.VISIBLE);
                    mNomessage.setText("您未登录，请先登录！");
                    mRecyclerView.setVisibility(View.GONE);
                }
            });
        }else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getChatPeopleData();
                }
            });
            thread.start();
        }
        CallbackManger.getInstance().addCallback(CallbackType.ADD_CHAT_USER, new IGlobalCallback() {
            @Override
            public void executeCallback(@Nullable Object args) {
                Log.d("aaaaaaaaaaaaaaaaa", String.valueOf(args));
                String userId = YiWuApplication.getInstance().getUser().getUs_id();
                isUserSelf(String.valueOf(args),userId);
            }
        });
        String chatId = getActivity().getIntent().getStringExtra("chatId");
        if(chatId != null){
            String userId = YiWuApplication.getInstance().getUser().getUs_id();
            isUserSelf(chatId,userId);
        }
    }

    private void initDeletePopupWindow() {
        //实例化SelectPicPopupWindow
        popupWindow = new CollectionPopupWindow(BaseActivity.getCurrentActivity(),itemsOnClick);
        //显示窗口
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(),
                Gravity.CENTER | Gravity.BOTTOM, 0, 0);

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            popupWindow.dismiss();
            switch (v.getId()) {
                case R.id.delect_collect:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String deleteId = userList.get(deletePosition).getUs_id();
                            userList.remove(deletePosition);
                            OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("your_user_id", YiWuApplication.getInstance().getUser().getUs_id())
                                    .add("other_user_id",deleteId)
                                    .build();
                            mReSponseStr = okHttpUtils.SendPostRequst(requestBody,HttpContants.DELETE_CHAT_PEOPLE);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(userList.size() == 0){
                                        mNomessage.setText("您暂无聊天......");
                                        nothingLayout.setVisibility(View.VISIBLE);
                                        mRecyclerView.setVisibility(View.GONE);
                                    }
                                    ToastUtils.ToastShortTime("删除该聊天成功");
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                    break;
                case R.id.delect_collect_cancel_btn:
                    popupWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    //获取已有的聊天
    private void getChatPeopleData(){
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                final RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", YiWuApplication.getInstance().getUser().getUs_id())
                        .build();
                mReSponseStr = okHttpUtils.SendPostRequst(requestBody, HttpContants.GET_CHAT_INFO);
                Log.d(TAG, "run: mReSponseStr=="+mReSponseStr);
                List<User> users = JSONObject.parseArray(mReSponseStr,User.class);
                userList.clear();
                if(users == null || users.size() == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nothingLayout.setVisibility(View.VISIBLE);
                            mNomessage.setText("您暂无聊天......");
                            mRecyclerView.setVisibility(View.GONE);
                            refreshLayout.finishRefresh();
                        }
                    });
                }else {
                    for (User user : users) {
                        userList.add(user);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nothingLayout.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                            refreshLayout.finishRefresh();
                        }
                    });
                }

    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_message;
    }

    //展示dialog
    private void showAddDialog(){
        dialog = new AlertDialog.Builder(getContext()).create();
        View dialogView = View.inflate(getContext(),R.layout.dialog_chat_user_add,null);
        dialog.setView(dialogView);
        dialog.show();
        initDialog(dialogView);
    }

    //初始化添加聊天dialog
    private void initDialog(View dialogView){
        cancel = dialogView.findViewById(R.id.btn_cancel_add);
        decide = dialogView.findViewById(R.id.btn_decide_add);
        addUserId = dialogView.findViewById(R.id.txt_chat_add_userId);
        cancel.setOnClickListener(this);
        decide.setOnClickListener(this);
        addUserId.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chat_add:
                //如果已经登录了
                if(LattePreference.getAppFlag(App.ISLOGINED.name())){
                    showAddDialog();
                }else {
                    LoginActivity.actionStart(getContext()); //进入登录界面
                }
                break;
            case R.id.btn_cancel_add:
                dialog.dismiss();
                break;
            case R.id.btn_decide_add:
                String chatId = addUserId.getText().toString();  //添加对话的id
                String userId = YiWuApplication.getInstance().getUser().getUs_id();//当前登录用户的id
                isUserSelf(chatId,userId);
                break;
            default:
                break;
        }
    }

    //判断添加对话的id是否为用户自己
    private void isUserSelf(String chatId,String userId){
        if(chatId.equals(userId)){
            ToastUtils.ToastShortTime("不能添加与自己的对话");
        }else {
            addChatPeople(chatId,userId);
        }
    }

    User user = null; //添加的聊天对象
    //添加聊天
    private void addChatPeople(final String chatId, final String userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                RequestBody requestBody = new FormBody.Builder()
                        .add("your_user_id", userId)
                        .add("other_user_id",chatId)
                        .build();
                mReSponseStr = okHttpUtils.SendPostRequst(requestBody, HttpContants.ADD_CHAT_PEOPLE);
                if(mReSponseStr.equals("你已经添加了哦")){
                   addState = AddState.ADDED_USER;
                   int size = userList.size();
                    for (int i = 0; i < size; i++) {
                        if (userList.get(i).getUs_id().equals(chatId)){
                            user = userList.get(i);
                        }
                    }
                }else if(mReSponseStr.equals("没有此用户")){
                    addState = AddState.NOT_FOUND_USER;
                }else {
                    addState = AddState.ADD_CHAT_USER_SUCCESS;
                    user = JSONObject.parseObject(mReSponseStr,User.class);
                    userList.add(user);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (addState){
                            case ADDED_USER:
                                ToastUtils.ToastShortTime("已经添加了该聊天,跳转到该聊天界面");
                                ChatActivity.actionStart(getContext(),user.getUs_phone());
                                Log.d("aaaaaaaaaaaaaaaaaa",user.getUs_phone());
                            break;
                            case NOT_FOUND_USER:
                                ToastUtils.ToastShortTime("没有找到该用户");
                                break;
                            case ADD_CHAT_USER_SUCCESS:
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        nothingLayout.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                ChatActivity.actionStart(getContext(),user.getUs_phone());
                                break;
                            default:
                                break;
                        }
                        if(dialog != null){
                            dialog.dismiss();
                        }
                    }
                });
            }
        }).start();
    }
}
