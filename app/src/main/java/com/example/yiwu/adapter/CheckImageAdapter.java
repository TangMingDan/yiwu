package com.example.yiwu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.http.HttpContants;
import com.example.yiwu.util.ToastUtils;

import java.util.List;
/**
 * Created by 朱旭辉
 * Time  2019/12/11
 * Describe:审核物品图片的adapter
 */
public class CheckImageAdapter extends RecyclerView.Adapter<CheckImageAdapter.ViewHolder>{
    private List<String> imageList;


    public CheckImageAdapter(List<String> imageList){
        this.imageList = imageList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView goods_image;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            goods_image = itemView.findViewById(R.id.goods_image);
        }
    }

    @NonNull
    @Override
    public CheckImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkimage_cardview,parent,false);
        final CheckImageAdapter.ViewHolder holder = new CheckImageAdapter.ViewHolder(view);
        holder.goods_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示图片
            }
        });
        return new CheckImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckImageAdapter.ViewHolder holder, int position) {
        String imageUrl = imageList.get(position);
        Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+imageUrl).into(holder.goods_image);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
