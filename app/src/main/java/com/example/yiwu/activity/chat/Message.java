package com.example.yiwu.activity.chat;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.litepal.crud.LitePalSupport;

/***
 * Created by 朱鸿 2019/10/21
 * 消息
 *
 */

public class Message extends LitePalSupport implements MultiItemEntity{
    private String fromId;   //消息的发送者
    private String toId;    //消息的接收者
    private String type;   //消息类型
    private String text;
    private String imgUrl;
    private long timeMillis; //从1971年到现在的毫秒数
    private String time; //将毫秒数格式化的时间

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
