package com.example.yiwu.Info;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by 朱旭辉
 * Time  2019/9/25
 * Describe:用户信息类
 */
public class User implements MultiItemEntity {
    private String us_id;
    private String us_name;
    private String us_pwd;
    private String us_email;
    private String us_phone;   // 用户的电话作为环信的登录ID
    private String us_headpic;
    private String us_job_id;
    private String us_college;

    public String getUs_email() {
        return us_email;
    }

    public String getUs_id() {
        return us_id;
    }

    public String getUs_headpic() {
        return us_headpic;
    }

    public String getUs_name() {
        return us_name;
    }

    public String getUs_job_id() {
        return us_job_id;
    }

    public String getUs_phone() {
        return us_phone;
    }

    public String getUs_college() {
        return us_college;
    }

    public String getUs_pwd() {
        return us_pwd;
    }

    public void setUs_email(String us_email) {
        this.us_email = us_email;
    }

    public void setUs_college(String us_college) {
        this.us_college = us_college;
    }

    public void setUs_headpic(String us_headpic) {
        this.us_headpic = us_headpic;
    }

    public void setUs_id(String us_id) {
        this.us_id = us_id;
    }

    public void setUs_job_id(String us_job_id) {
        this.us_job_id = us_job_id;
    }

    public void setUs_name(String us_name) {
        this.us_name = us_name;
    }

    public void setUs_phone(String us_phone) {
        this.us_phone = us_phone;
    }

    public void setUs_pwd(String us_pwd) {
        this.us_pwd = us_pwd;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}

