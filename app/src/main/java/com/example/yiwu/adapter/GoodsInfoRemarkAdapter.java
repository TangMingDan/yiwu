package com.example.yiwu.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yiwu.Info.GoodsInfo;
import com.example.yiwu.R;
import com.example.yiwu.YiWuApplication;

import java.util.List;
/**
 * 朱鸿
 * 2019/9/10
 * 商品评价recyclerView的适配器
 */
public class GoodsInfoRemarkAdapter extends BaseMultiItemQuickAdapter<GoodsInfo, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public GoodsInfoRemarkAdapter(List<GoodsInfo> data) {
        super(data);
        addItemType(GoodsInfo.ITEM_CARD_VIEW, R.layout.item_goodsinforemark);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsInfo item) {
        helper.setText(R.id.remark_content,item.getGoods_num() + "库存");
        helper.setText(R.id.remark_time,"￥" + item.getGoods_price());
        helper.setText(R.id.remark_introducation,"浙江教育出版社");
        Glide.with(YiWuApplication.getContext()).load("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259" +
                "ohaj30ci08c74r.jpg").into((ImageView) helper.getView(R.id.remark_img));
        helper.setText(R.id.remark_user,"马云商店>" );
    }
}
