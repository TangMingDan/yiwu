package com.example.yiwu.activity.chat;

import org.litepal.crud.LitePalSupport;


/**
 * created by 朱鸿 2019/10/23
 * 用于本地存储已经接收的用户的信息，方便直接读取
 *
 */


public class ChatUser extends LitePalSupport {
    private String userId;
    private String userName;
    private String userImg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
