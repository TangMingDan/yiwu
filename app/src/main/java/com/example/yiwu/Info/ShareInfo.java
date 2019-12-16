package com.example.yiwu.Info;

import java.util.List;

/**
 * Created by 朱旭辉
 * Time  2019/9/10
 * Describe:分享一下信息
 */

public class ShareInfo {

    /**
     * @param talk_id  说说ID
     * @param owner_id  发布者姓名
     * @param talk_describe  说说的描述
     * @param talk_time  发布时间
     * @param talk_like   点赞人数
     * @param talk_pic_addr 图片集合
     * @param owner_name 发布者姓名
     * @param talk_remark 评论
     * @param user_like_or_not 该用户是否点赞
     */
    private String talk_id;
    private String owner_id;
    private String owner_name;
    private String talk_describe;
    private String talk_time;
    private String talk_like;
    private List<String> talk_pic_addr;
    private String owner_head;
    private Boolean user_like_or_not;
    public String getOwner_head() {
        return owner_head;
    }

    public Boolean getUser_like_or_not() {
        return user_like_or_not;
    }

    public void setUser_like_or_not(Boolean user_like_or_not) {
        this.user_like_or_not = user_like_or_not;
    }

    public void setOwner_head(String owner_head) {
        this.owner_head = owner_head;
    }


    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public List<String> getTalk_pic_addr() {
        return talk_pic_addr;
    }

    public void setTalk_pic_addr(List<String> talk_pic_addr) {
        this.talk_pic_addr = talk_pic_addr;
    }


    public void setTalk_id(String talk_id) {
        this.talk_id = talk_id;
    }

    public String getTalk_id() {
        return talk_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setTalk_describe(String talk_describe) {
        this.talk_describe = talk_describe;
    }

    public String getTalk_describe() {
        return talk_describe;
    }

    public void setTalk_like(String talk_like) {
        this.talk_like = talk_like;
    }

    public String getTalk_like() {
        return talk_like;
    }

    public void setTalk_time(String talk_time) {
        this.talk_time = talk_time;
    }

    public String getTalk_time() {
        return talk_time;
    }
}
