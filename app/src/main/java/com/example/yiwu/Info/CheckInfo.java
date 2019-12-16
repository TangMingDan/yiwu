package com.example.yiwu.Info;

import java.util.List;

public class CheckInfo {
    private String goods_id;
    private String goods_name;
    private String owner_id;
    private String goods_info;
    private String goods_num;
    private String goods_addr;
    private String goods_price;
    private String owner_name;
    private String checker_name;
    private String reason;
    private String pass_or_not;
    private List<String> goods_pic_addr;
    private String isManager;

    public String getIsManager() {
        return isManager;
    }

    public List<String> getGoods_pic_addr() {
        return goods_pic_addr;
    }

    public void setGoods_pic_addr(List<String> goods_pic_addr) {
        this.goods_pic_addr = goods_pic_addr;
    }

    public void setIsManager(String isManager) {
        this.isManager = isManager;
    }


    public String getOwner_id() {
        return owner_id;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public String getGoods_addr() {
        return goods_addr;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getGoods_info() {
        return goods_info;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public String getChecker_name() {
        return checker_name;
    }

    public String getGoods_num() {
        return goods_num;
    }


    public String getPass_or_not() {
        return pass_or_not;
    }

    public String getReason() {
        return reason;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }



    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public void setChecker_name(String checker_name) {
        this.checker_name = checker_name;
    }

    public void setGoods_addr(String goods_addr) {
        this.goods_addr = goods_addr;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public void setGoods_info(String goods_info) {
        this.goods_info = goods_info;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public void setGoods_num(String goods_num) {
        this.goods_num = goods_num;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setPass_or_not(String pass_or_not) {
        this.pass_or_not = pass_or_not;
    }

}
