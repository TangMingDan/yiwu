package com.example.yiwu.state;

/***
 * Created by 朱鸿 2019/10/31
 * 判断添加聊天用户的返回状态
 */

public enum AddState {
    ADD_CHAT_USER_SUCCESS,  //添加成功
    NOT_FOUND_USER,  //没有找到该用户
    ADDED_USER  //已经添加过了
}
