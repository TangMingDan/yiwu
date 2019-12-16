package com.example.yiwu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.yiwu.activity.SailGoodsActivity;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.state.App;
import com.example.yiwu.Info.User;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.MyGoodsActivity;
import com.example.yiwu.activity.LoginActivity;
import com.example.yiwu.activity.NoticeActivity;
import com.example.yiwu.util.LattePreference;
import com.hyphenate.chat.EMClient;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.yiwu.http.HttpContants.BASE_URL_120;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:个人信息碎片
 * 修改:朱鸿
 * 2019/9/15
 * 添加通知和购买历史点击事件
 * 修改：加入物品上架
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout noticeLayout;//通知
    private LinearLayout myGoodsLayout;//购买历史
    private LinearLayout wantSailLayout;

    private Button me_nologin_btn_login;//点击登陆按钮
    private LinearLayout me_nologin_layout;//点击登陆Layout
    private LinearLayout me_login_profile_layout;//用户权限管理
    private RelativeLayout me_login_layout;//用户信息Layout
    private CircleImageView me_login_head;//头像
    private TextView me_login_username;//用户名
    private TextView me_login_userID;//用户ID


    private final int REQUEST_CODE = 1;

    private Button btn_logout;//退出登陆

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        me_login_profile_layout = view.findViewById(R.id.me_login_profile_layout);
        me_login_profile_layout.setOnClickListener(this);


        me_nologin_layout = view.findViewById(R.id.me_nologin_layout);//没有登陆的layout
        me_nologin_btn_login = view.findViewById(R.id.me_nologin_btn_login);//登陆按钮
        me_nologin_btn_login.setOnClickListener(this);

        me_login_layout = view.findViewById(R.id.me_login_layout);//用户登陆layout
        me_login_username = view.findViewById(R.id.me_login_tv_username);//用户名
        me_login_head = view.findViewById(R.id.me_login_iv_head);//用户头像
        me_login_userID = view.findViewById(R.id.me_login_tv_userID);//用户id

        //退出登陆
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        //通知
        noticeLayout = super.view.findViewById(R.id.message_layout);
        noticeLayout.setOnClickListener(this);
        //我上架的物品
        myGoodsLayout = super.view.findViewById(R.id.my_goods_layout);
        myGoodsLayout.setOnClickListener(this);
        //我也要上架
        wantSailLayout = super.view.findViewById(R.id.want_sail_layout);
        wantSailLayout.setOnClickListener(this);


        User user = YiWuApplication.getInstance().getUser();
        showUser(user);
    }

    @Override
    protected void initDate() {


    }

    @Override
    public void onResume() {
        super.onResume();
        if(LattePreference.getAppFlag(App.ISLOGINED.name())){
            User user = YiWuApplication.getInstance().getUser();
            showUser(user);
        }
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        User user = YiWuApplication.getInstance().getUser();
        showUser(user);
    }

    private void showUser(User user) {
        if (user != null) {//登陆成功
            me_nologin_layout.setVisibility(View.GONE);
            me_login_layout.setVisibility(View.VISIBLE);
            me_login_username.setText(user.getUs_name());
            me_login_userID.setText(user.getUs_id());
            Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119 + user.getUs_headpic()).into(me_login_head);

        } else {
            me_nologin_layout.setVisibility(View.VISIBLE);
            me_login_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        User user = YiWuApplication.getInstance().getUser();
        Intent intent;
        if (user == null && view.getId()!=R.id.btn_logout) {//用户为空跳转到登陆界面
            intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            switch (view.getId()) {
                case R.id.message_layout://通知
                    NoticeActivity.actionStart(YiWuApplication.getContext());
                    break;
                case R.id.me_nologin_btn_login://立即登陆
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                    break;
                case R.id.my_goods_layout://购买历史
                    MyGoodsActivity.actionStart(YiWuApplication.getContext());
                    break;
                case R.id.want_sail_layout://我也要上架
                    SailGoodsActivity.actionStart(YiWuApplication.getContext());
                    break;
                case R.id.btn_logout:
                    YiWuApplication.getInstance().clearUser();
                    LattePreference.clearAppPreferences();
                    LattePreference.setAppFlag(App.ISLOGINED.name(),false);
                    EMClient.getInstance().logout(true);
                    showUser(null);
                    break;
                case R.id.me_login_profile_layout://用户权限管理

                    break;
            }

        }
    }
}
