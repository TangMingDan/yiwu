package com.example.yiwu.http;

/**
 * <pre>
 *     author : 朱旭辉
 *     time   : 2019/09/10
 *     desc   :用与记录服务器连接信息
 */

public class HttpContants {
    //义乌项目119服务器基地址
    public static final String BASE_URL_119 = "http://119.23.235.106/yiwuPro";
    //义乌项目119服务器图片存储基地址
    public static final String IMAGE_URL_119 = "http://119.23.235.106";
    //义乌项目120服务器基地址
    public static final String BASE_URL_120 = "http://120.25.151.18";
    //首页banner请求url
    public static final String HOME_BANNER_URL = BASE_URL_119+"/home_fragment/bannerinfo";
    //首页cardView请求url
    public static final String HOME_GOODSINFO_URL = BASE_URL_119+"/home_fragment/goodsinfo";
    //分享页面说说url
    public static final String SHARE_SHAREINFO_URL = BASE_URL_119+"/share_fragment/shareinfo";
    //上传说说
    public static final String SEND_SHAREYIXIA = BASE_URL_119+"/share_fragment/send_images";
    //我的收藏url
    public static final String COLLECTINFO_URL = BASE_URL_119+"/collect_fragment/mycollect";
    //添加收藏
    public static final String ADD_COLLECTION_URL = BASE_URL_119 + "/collect_fragment/add_collect";
    //删除收藏
    public static final String DELECT_COLLECTION_URL = BASE_URL_119+"/collect_fragment/delete_collect";
    //搜索
    public static final String SEARCH_GOODSINFO_URL = BASE_URL_119+"/home_fragment/search";
    //登陆
    public static final String LOGIN_URL = BASE_URL_119+"/mine_fragment/login";
    //注册邮箱验证码
    public static final String REGISTER_EMAIL_CODE = BASE_URL_119+"/mine_fragment/register/send_email";
    //用户注册信息提交
    public static final String REGISTER_COMMIT = BASE_URL_119+"/mine_fragment/register/sign_in";
    //忘记密码第一步
    public static final String FORGET_PASSWORD_ONE = BASE_URL_119+"/mine_fragment/forget_password/step_one";
    //忘记密码第二步
    public static final String FORGET_PASSWORD_TWO = BASE_URL_119+"/mine_fragment/forget_password/step_two";
    //忘记密码第三步
    public static final String FORGET_PASSWORD_THREE = BASE_URL_119+"/mine_fragment/forget_password/step_three";
    //得到聊天对象的信息
    public static final String GET_CHAT_INFO = BASE_URL_119+"/message_fragment/get_chat_info";
    //添加聊天对象
    public static final String ADD_CHAT_PEOPLE = BASE_URL_119+"/message_fragment/add_chat_people";
    //添加删除对象
    public static final String DELETE_CHAT_PEOPLE = BASE_URL_119+"/message_fragment/delete_chat_people";
    //得到自己上架的商品信息
    public static final String GET_MYGOODS_INFO = BASE_URL_119+"/mine_fragment/my_goods";
    //图书分类
    public static final String GET_GOODS_CLASSIFICATION = BASE_URL_119+"/home_fragment/classification";
    //获取分类图书
    public static final String GET_GOODS_CLASSIFICATION_DETIAL = BASE_URL_119+"/home_fragment/classification_goods";
    //访问者访问物品将其访问id和访问物品id发晚服务器
    public static final String VISIT_GOODS_INFO = BASE_URL_119+"/home_fragment/visit_goods_to_recommend";
    //下架自己的物品
    public static final String DELETE_MYGOOGS = BASE_URL_119+"/mine_fragment/delete_my_goods";
    //上架物品
    public static final String SAIL_GOODS = BASE_URL_119+"/home_fragment/want_sail_goods";
    //说说点赞
    public static final String SHARELIKEADD = BASE_URL_119+"/share_fragment/share_like_add";
    //取消点赞
    public static final String SHARELIKEDEL = BASE_URL_119+"/share_fragment/share_like_del";
    //管理员审核
    public static final String MANAGER_CHECKGOODS = BASE_URL_119+"/mine_fragment/check_goods";
    //获取是否通过审核的消息
    public static final String CHECK_PASSORNOT = BASE_URL_119+"/mine_fragment/check_pass";
    //审核反馈
    public static final String CHECK_RESPONSE = BASE_URL_119+"/mine_fragment/check_response";
}
