package com.example.yiwu.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.yiwu.R;

import java.util.List;

/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:热门搜索适配器
 */

public class HotSearchAdapter  extends BaseQuickAdapter<String, BaseViewHolder> {

    public HotSearchAdapter(List<String> datas) {
        super(R.layout.item_search, datas);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content, item);
    }
}
