package com.example.yiwu.Info;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朱旭辉
 * Time  2019/8/30
 * Describe:物品上架信息
 */

public class GoodsInfo implements MultiItemEntity, Parcelable {//implements MultiItemEntity是为了确定item的显示类型

    /**
     * item为CardView
     */
    public static final int ITEM_CARD_VIEW  = 0;



    /**
     * @param goods_id  商品ID
     * @param goods_name  商品名字
     * @param onwer_id  拥有者（商家ID）
     * @param goods_info  商品名字
     * @param goods_num   商品数量
     * @param goods_addr  商品地址
     * @param goods_price  商品价格
     * @param goods_pic_addr  商品图片集合
     *
     */
    private String goods_id;
    private String goods_name;
    private String owner_id;
    private String goods_info;
    private String goods_num;
    private String goods_addr;
    private String goods_price;
    private String owner_name;

    private List<String> goods_pic_addr;
    private int item_type;

    public String getOwner_id() {
        return owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public List<String> getGoods_pic_addr() {
        return goods_pic_addr;
    }

    public void setGoods_pic_addr(List<String> goods_pic_addr) {
        this.goods_pic_addr = goods_pic_addr;
    }

    //主页的构造函数
    public GoodsInfo(){

    }
    //我的收藏的构造函数
    public GoodsInfo(String goods_info, String goods_num, String goods_price) {
        this.goods_info = goods_info;
        this.goods_num = goods_num;
        this.goods_price = goods_price;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }


    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }



    public String getGoods_info() {
        return goods_info;
    }

    public void setGoods_info(String goods_info) {
        this.goods_info = goods_info;
    }

    public String getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(String goods_num) {
        this.goods_num = goods_num;
    }

    public String getGoods_addr() {
        return goods_addr;
    }

    public void setGoods_addr(String goods_addr) {
        this.goods_addr = goods_addr;
    }





    @Override
    public int getItemType() {
        return item_type;
    }
    public void setItemType(int item_type) {
        this.item_type = item_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.goods_id);
        parcel.writeString(this.goods_name);
        parcel.writeString(this.owner_id);
        parcel.writeString(this.goods_info);
        parcel.writeString(this.goods_num);
        parcel.writeString(this.goods_addr);
        parcel.writeString(this.goods_price);
        parcel.writeString(this.owner_name);
        parcel.writeList(this.getGoods_pic_addr());
    }



    protected GoodsInfo(Parcel in){
        this.goods_id = in.readString();
        this.goods_name = in.readString();
        this.owner_id = in.readString();
        this.goods_info = in.readString();
        this.goods_num = in.readString();
        this.goods_addr = in.readString();
        this.goods_price = in.readString();
        this.owner_name = in.readString();
        goods_pic_addr = new ArrayList<>();
        in.readList(this.goods_pic_addr,getClass().getClassLoader());
    }
    public static final Creator<GoodsInfo> CREATOR = new Creator<GoodsInfo>() {
        @Override
        public GoodsInfo createFromParcel(Parcel parcel) {
            return new GoodsInfo(parcel);
        }

        @Override
        public GoodsInfo[] newArray(int i) {
            return new GoodsInfo[i];
        }
    };
}
