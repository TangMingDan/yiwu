package com.example.yiwu.adapter;


import android.content.Context;
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
 * Created by 朱旭辉
 * Time  2019/09/07
 * Describe: HomeFragment中Recycler的适配器
 */
public class HomeRecyclerViewAdapter extends BaseMultiItemQuickAdapter<GoodsInfo,BaseViewHolder> {
    private Context context;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public HomeRecyclerViewAdapter(List<GoodsInfo> data) {
        super(data);
        /**
         * addItemType中的type种类，必须和接收到的种类数目一模一样。
         * 种类：有几种type，就要写几个addItemType。少写或者错写，会直接报错！！！
         *  (android.content.res.Resources$NotFoundException: Resource ID *******)
         *  时刻注意！！！！
         *  例如：这个type有10种！。（type=1,2,3...10）你就得写
         *     addItemType(1, R.layout.item_test_one);
         *     addItemType(2, R.layout.item_test_two);
         *     addItemType(3, R.layout.item_test_two);
         *     ....
         *     addItemType(10, R.layout.item_test_two);
         *     漏写一个就会报错！！！血的教训啊！！！！
         */
        addItemType(GoodsInfo.ITEM_CARD_VIEW, R.layout.item_cardview);

    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsInfo item) {
        switch (item.getItemType()){
            //CardView
            case GoodsInfo.ITEM_CARD_VIEW:
                helper.setText(R.id.goods_name,item.getGoods_name());
                Log.d(TAG, "convert: "+HttpContants.IMAGE_URL_119+item.getGoods_pic_addr().get(0));
                Glide.with(YiWuApplication.getContext()).load(HttpContants.IMAGE_URL_119+item.getGoods_pic_addr().get(0)).into((ImageView) helper.getView(R.id.goods_image));
                break;
        }
    }
}
