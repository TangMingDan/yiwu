package com.example.yiwu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.yiwu.Info.CheckInfo;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.activity.GoodInfoActivity;
import com.example.yiwu.activity.MyGoodsActivity;
import com.example.yiwu.activity.NoticeActivity;
import com.example.yiwu.customView.DeleteMyGoodsDialog;
import com.example.yiwu.customView.NoPassReasonDialog;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.OkHttpUtils;
import com.example.yiwu.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.yiwu.customView.PickerView.TAG;

/**
 * Created by 朱旭辉
 * Time  2019/12/11
 * Describe:审核物品的adapter
 */
public class CheckGoodsAdapter extends RecyclerView.Adapter<CheckGoodsAdapter.ViewHolder> {
    private List<CheckInfo> checkInfos;
    private final String GOODSPASS = "1";
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(YiWuApplication.getContext()).inflate(R.layout.item_ckeck_goods, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.btn_goods_nopass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoPassReasonDialog noPassReasonDialog = new NoPassReasonDialog(context, YiWuApplication.getInstance().getUser(), checkInfos, holder.getAdapterPosition());
                noPassReasonDialog.show();

            }
        });
        holder.btn_goods_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int position = holder.getAdapterPosition();
                        CheckInfo checkInfo = checkInfos.get(position);
                        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("result", GOODSPASS)
                                .add("goods_id", checkInfo.getGoods_id() + "")
                                .add("goods_name", checkInfo.getGoods_name())
                                .add("email", YiWuApplication.getInstance().getUser().getUs_email())
                                .build();
                        String responseStr = okHttpUtils.SendPostRequst(requestBody, HttpContants.CHECK_RESPONSE);
                        Log.d(TAG, "run: result==" + GOODSPASS);
                        Log.d(TAG, "run: goods_id==" + checkInfo.getGoods_id());
                        Log.d(TAG, "run: email==" + GOODSPASS);
                        if (responseStr.equals("审核完毕")) {
                            BaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.ToastShortTime("审核通过");
                                    checkInfos.remove(position);
                                    NoticeActivity.adapterNotifyDataSetChanged();
                                }
                            });
                        } else {
                            BaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.ToastShortTime("审核失败");
                                }
                            });

                        }
                    }
                }).start();
            }

        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CheckImageAdapter checkGoodsImageAdapter;
        CheckInfo checkInfo = checkInfos.get(position);
        holder.goods_name.setText(checkInfo.getGoods_name());
        holder.goods_detail.setText(checkInfo.getGoods_info());
        holder.owner_name.setText(checkInfo.getOwner_name());
        holder.goods_num.setText(checkInfo.getGoods_num());
        holder.school_name.setText(checkInfo.getGoods_addr());
        holder.goods_price.setText(checkInfo.getGoods_price());
        checkGoodsImageAdapter = new CheckImageAdapter(checkInfo.getGoods_pic_addr());
        LinearLayoutManager layoutManager = new LinearLayoutManager(YiWuApplication.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.check_goods_images_recycler.setLayoutManager(layoutManager);
        holder.check_goods_images_recycler.setAdapter(checkGoodsImageAdapter);

        if (checkInfo.getPass_or_not().equals("1")) {
            holder.system_notice.setText("您的" + checkInfo.getGoods_name() + "已经通过了审核。");
            holder.system_notice.setTextColor(Color.GREEN);
        } else if (checkInfo.getPass_or_not().equals("0")) {
            holder.system_notice.setText("您的" + checkInfo.getGoods_name() + "正在审核中，请耐心等待。");
            holder.system_notice.setTextColor(Color.GRAY);
        } else {
            holder.system_notice.setText("您的" + checkInfo.getGoods_name() + "未能通过审核" + ",原因是：" + checkInfo.getReason() + "。");
            holder.system_notice.setTextColor(Color.RED);
        }
        holder.checker_name.setText(checkInfo.getChecker_name());
        if (checkInfo.getIsManager().equals("1")) {
            //是Manager说明需要审核
            holder.notice_linearLayout.setVisibility(View.GONE);
        } else {//说明是系统消息
            holder.check_linearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return checkInfos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout check_linearLayout;
        private LinearLayout notice_linearLayout;
        private TextView goods_name;
        private TextView goods_detail;
        private TextView owner_name;
        private TextView goods_num;
        private TextView school_name;
        private TextView goods_price;
        private RecyclerView check_goods_images_recycler;
        private Button btn_goods_pass;
        private Button btn_goods_nopass;
        private TextView system_notice;
        private TextView checker_name;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            goods_name = itemView.findViewById(R.id.goods_name);
            goods_detail = itemView.findViewById(R.id.goods_detail);
            owner_name = itemView.findViewById(R.id.owner_name);
            goods_num = itemView.findViewById(R.id.goods_num);
            school_name = itemView.findViewById(R.id.school_name);
            goods_price = itemView.findViewById(R.id.goods_price);
            check_goods_images_recycler = itemView.findViewById(R.id.check_goods_images_recycler);
            btn_goods_pass = itemView.findViewById(R.id.btn_goods_pass);
            btn_goods_nopass = itemView.findViewById(R.id.btn_goods_nopass);
            btn_goods_nopass = itemView.findViewById(R.id.btn_goods_nopass);
            system_notice = itemView.findViewById(R.id.system_notice);
            checker_name = itemView.findViewById(R.id.checker_name);
            check_linearLayout = itemView.findViewById(R.id.check_linearLayout);
            notice_linearLayout = itemView.findViewById(R.id.notice_linearLayout);
        }
    }

    public CheckGoodsAdapter(List<CheckInfo> checkInfos, Context context) {
        this.context = context;
        this.checkInfos = checkInfos;
    }


}
