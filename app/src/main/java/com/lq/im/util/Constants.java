package com.lq.im.util;

public class Constants {
    public static final String HOST = "192.168.1.4";
    public static final int PORT = 1255;
    public static final int HB_INTERVAL= 30; //秒单位
    public static final int TIME_OUT= 30000; //毫秒单位
    public static final int TIME_IDLE= 60; //秒单位MAX_INTERVAL
    public static final int MAX_INTERVAL= 20;

    public static final String NET_STATUS= "NET_STATUS"; //网络状态


    public static final String BC_RECONNECT_IM= "com.lq.im.BC_RECONNECT_IM"; //重连通知
    public static final String BC_CONFLIT_IM= "com.lq.im.BC_CONFLIT_IM"; //账号冲突
    public static final String BC_OFFLINE_IM= "com.lq.im.BC_OFFLINE_IM"; //下线通知
    public static final String BC_MESSAGE_IM= "com.lq.im.BC_MESSAGE_IM"; //消息通知
    public static final String BC_LOGINED_IM= "com.lq.im.BC_LOGINED_IM"; //登陆成功通知

    //网络变化filter
    public static final String CONNECTIVITY_CHANGE= "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String WIFI_STATE_CHANGED= "android.net.wifi.WIFI_STATE_CHANGED";
    public static final String STATE_CHANGE= "android.net.wifi.STATE_CHANGE";

    //开启即时通讯
    public static final String ACTION_START_IMSERVICE="android.intent.action.IMPushService";
    //发送消息
    public static final String ACTION_SEND_MESSAGE="android.intent.action.ACTION_SEND_MESSAGE";
}
