package com.example.yiwu.util;


import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by 朱旭辉
 * Time  2019/10/06
 * Describe:自定义OkHttpUtils，运用单例模式设计模式降低内存消耗
 */
public class OkHttpUtils {
    private OkHttpClient client;
    private Request request;

    private static OkHttpUtils instance;
    private Response response;
    private String responseData;
    //私有构造函数
    private OkHttpUtils(){
        if(client == null){
            client = new OkHttpClient();
        }
    }
    //得到实例
    public static OkHttpUtils GetInstance(){
        if (instance == null){
            instance = new OkHttpUtils();
        }
        return instance;
    }
    /**
     * @param SendURL 发送地址
     */

    public String SendGetRequst(String SendURL){
        request = new Request.Builder().url(SendURL).build();
        try {
            response = client.newCall(request).execute();
            responseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public String SendPostRequst(RequestBody requestBody,String SendURL){
        request = new Request.Builder().url(SendURL).post(requestBody).build();
        try {
            response = client.newCall(request).execute();
            responseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

}
