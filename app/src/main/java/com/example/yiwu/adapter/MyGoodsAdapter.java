package com.example.yiwu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.activity.BaseActivity;
import com.example.yiwu.activity.GoodInfoActivity;
import com.example.yiwu.customView.DeleteMyGoodsDialog;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.ToastUtils;


import java.util.List;
/**
 * Created by 朱旭辉
 * Time  2019/11/05
 * Describe:我的物品Adapter
 */
public class MyGoodsAdapter extends RecyclerView.Adapter<MyGoodsAdapter.ViewHolder>{
    private List<GoodsInfo> goodsInfoList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView myGoodsImage;
        private TextView myGoodsTitle;
        private TextView myGoodsPrice;
        private Button myGoodsOutButton;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            myGoodsImage = itemView.findViewById(R.id.my_goods_image);
            myGoodsPrice = itemView.findViewById(R.id.my_goods_price);
            myGoodsTitle = itemView.findViewById(R.id.my_goods_title);
            myGoodsOutButton = itemView.findViewById(R.id.btn_goods_out);
        }

    }




    public MyGoodsAdapter(List<GoodsInfo> list,Context context){
        this.context = context;
        this.goodsInfoList = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(YiWuApplication.getContext()).inflate(R.layout.item_mygoods,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.myGoodsOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处的context，需要用Activity，而不是Application的Context.因为要在Acitivity中显示这个Dialog
                DeleteMyGoodsDialog deleteMyGoodsDialog = new DeleteMyGoodsDialog(context,YiWuApplication.getInstance().getUser(),goodsInfoList,holder.getAdapterPosition());
                deleteMyGoodsDialog.show();
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodInfoActivity.actionStart(YiWuApplication.getContext(),goodsInfoList.get(holder.getAdapterPosition()));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        GoodsInfo goodsInfo = goodsInfoList.get(position);
        Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+goodsInfo.getGoods_pic_addr().get(0)).into(holder.myGoodsImage);
        holder.myGoodsTitle.setText(goodsInfo.getGoods_name());
        holder.myGoodsPrice.setText("￥" + goodsInfo.getGoods_price());

    }


    @Override
    public int getItemCount() {
        return goodsInfoList.size();
    }


}
