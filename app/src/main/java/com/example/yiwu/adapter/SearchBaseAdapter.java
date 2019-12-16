package com.example.yiwu.adapter;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;
import com.example.yiwu.http.HttpContants;

import java.util.List;

/**
 * Created by 朱鸿
 * Time  2019/9/2
 * Describe:搜索展示Adapter
 */
public class SearchBaseAdapter extends BaseMultiItemQuickAdapter<GoodsInfo,BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SearchBaseAdapter(List<GoodsInfo> data) {
        super(data);
        addItemType(GoodsInfo.ITEM_CARD_VIEW, R.layout.item_collect);
    }

    protected void convert(BaseViewHolder helper, GoodsInfo item) {

        helper.setText(R.id.collect_repertory,item.getGoods_num() + "库存");
        helper.setText(R.id.collect_price,"￥" + item.getGoods_price());
        helper.setText(R.id.colletc_introducation,item.getGoods_name());
        Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+item.getGoods_pic_addr().get(0)).into((ImageView) helper.getView(R.id.collect_img));
        helper.setText(R.id.collect_merchant,item.getOwner_name()+">" );
    }
}
