package com.example.yiwu.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yiwu.Info.SortInfo;
import com.example.yiwu.R;

import java.util.List;

public class GoodsSortsAdapter extends BaseMultiItemQuickAdapter<SortInfo,BaseViewHolder> {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public GoodsSortsAdapter(List<SortInfo> data) {
        super(data);
        addItemType(0, R.layout.item_goods_sorts);
    }

    @Override
    protected void convert(BaseViewHolder helper, SortInfo item) {
        helper.setText(R.id.goods_sorts_name,item.getSortName());
        helper.setImageResource(R.id.goods_sorts_img,item.getSortImg());
    }
}
