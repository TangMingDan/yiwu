package com.example.yiwu.Info;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class SortInfo implements MultiItemEntity {
    private String sortName;
    private Integer sortImg;

    public SortInfo() {
    }

    public SortInfo(String sortName, Integer sortImg) {
        this.sortName = sortName;
        this.sortImg = sortImg;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Integer getSortImg() {
        return sortImg;
    }

    public void setSortImg(Integer sortImg) {
        this.sortImg = sortImg;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
