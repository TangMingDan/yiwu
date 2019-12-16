package com.example.yiwu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.yiwu.Info.ClassifyImage;
import com.example.yiwu.Info.ShareInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.activity.LoginActivity;
import com.example.yiwu.activity.SendShareImageActivity;
import com.example.yiwu.adapter.NineGridAdapter;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:分类碎片
 */

public class ClassifyFragment extends BaseFragment implements View.OnClickListener {
    private ListView listView;
    private List<ShareInfo> shareInfoList = new ArrayList<>();
    private List<ShareInfo> shareInfoListForGet = new ArrayList<>();//用于接受数据
    private View shareView;
    private RefreshLayout refreshLayout;
    private FloatingActionButton floatingActionButton;
    private NineGridAdapter nineGridAdapter;
    private CircleImageView userIcon;
    private int FIRSTIN = 1;
    private int REFRESHIN = 2;
    private final int RESULTID = 1;

    private Thread getShareInfoThread = new Thread() {
        @Override
        public void run() {
            getJsonDate(FIRSTIN);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        floatingActionButton = view.findViewById(R.id.float_button);
        floatingActionButton.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.classify_listView);
//        shareView = LayoutInflater.from(getActivity()).inflate(R.layout
//                .title_share, (ViewGroup) listView.getParent(), false);
        shareView = View.inflate(YiWuApplication.getContext(), R.layout.title_share, null);
        userIcon = shareView.findViewById(R.id.userIcon);
        if (YiWuApplication.getInstance().getUser() != null){
            Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+YiWuApplication.getInstance().getUser().getUs_headpic()).into(userIcon);
        }
        refreshLayout = view.findViewById(R.id.classify_refresh);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getJsonDate(REFRESHIN);
                    }
                }).start();
                refreshLayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
                ToastUtils.ToastShortTime("没有更多内容了");
            }
        });


    }

    @Override
    protected void initDate() {
        Log.d(TAG, "initDate: ");

        getShareInfoThread.start();
//        try {
//            getShareInfoThread.start();
//            getShareInfoThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        //这里单独添加一条单条的测试数据，用来测试单张的时候横竖图片的效果
//        ArrayList<ClassifyImage> singleList=new ArrayList<>();
//        singleList.add(new ClassifyImage(images[8][0],Integer.parseInt(images[8][1]),Integer.parseInt(images[8][2])));
//        imagesList.add(singleList);

        //从一到9生成9条朋友圈内容，分别是1~9张图片
//        for(int i=0;i<9;i++){
//            ArrayList<ClassifyImage> itemList=new ArrayList<>();
//            for(int j=0;j<=i;j++){
//                itemList.add(new ClassifyImage(images[j][0],Integer.parseInt(images[j][1]),Integer.parseInt(images[j][2])));
//            }
//            imagesList.add(itemList);
//        }
    }

    private void getJsonDate(int flag) {
        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
        RequestBody requestBody;
        if (YiWuApplication.getInstance().getUser() == null) {//游客用户
            requestBody = new FormBody.Builder()
                    .add("user_id", "0")
                    .build();
        } else {
            requestBody = new FormBody.Builder()
                    .add("user_id", YiWuApplication.getInstance().getUser().getUs_id())
                    .build();
        }
        shareInfoList.clear();
        String shareinfo_str = okHttpUtils.SendPostRequst(requestBody, HttpContants.SHARE_SHAREINFO_URL);
        shareInfoListForGet = JSONObject.parseArray(shareinfo_str, ShareInfo.class);
        for (int i = 0; i < shareInfoListForGet.size(); i++) {
            shareInfoList.add(shareInfoListForGet.get(i));
        }
        Collections.reverse(shareInfoList);//实现list集合逆序排列


        if (flag == FIRSTIN) {
            firtRefresh();
        } else {
            otherRefresh();
        }

    }

    private void otherRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: shareInfoList.size ==" + shareInfoList.size());
                Log.d(TAG, "run: shareInfoList::" + shareInfoList.get(0).getTalk_describe());
                Log.d(TAG, "run: shareInfoList::" + shareInfoList.get(0).getTalk_pic_addr().get(0));
                nineGridAdapter.notifyDataSetChanged();
                if (YiWuApplication.getInstance().getUser() != null){
                    Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+YiWuApplication.getInstance().getUser().getUs_headpic()).into(userIcon);
                }
            }
        });
    }

    private void firtRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: AdapterHasChanged");
                nineGridAdapter = new NineGridAdapter(getContext(), shareInfoList);
                listView.setAdapter(nineGridAdapter);
                listView.addHeaderView(shareView);
                if (YiWuApplication.getInstance().getUser() != null){
                    Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+YiWuApplication.getInstance().getUser().getUs_headpic()).into(userIcon);
                }
            }
        });
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_classify;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","执行了分享");
        switch (requestCode) {
            case RESULTID:
                if (resultCode == YiWuApplication.getActivity().RESULT_OK) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getJsonDate(REFRESHIN);
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.float_button:
                if (YiWuApplication.getInstance().getUser() == null) {
                    LoginActivity.actionStart(YiWuApplication.getContext());
                } else {
                    Intent intent = new Intent(YiWuApplication.getContext(), SendShareImageActivity.class);
                    startActivityForResult(intent, RESULTID);
                }
                break;
            default:
                break;
        }
    }
}
