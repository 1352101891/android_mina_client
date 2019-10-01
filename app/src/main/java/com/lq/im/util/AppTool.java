package com.lq.im.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.lq.im.IMApp;
import com.lq.im.service.model.Message;

public class AppTool {
    public static final String TAG="[AppTool]";

    public static boolean isOnline(){
        String value=SafeCheck.s(Cache.getFromMemCache(Message.ONLINE_STATUS));
        if (value.equals("1")){
            return true;
        }else {
            return false;
        }
    }

    public static void setOnline(boolean isOnline){
        Cache.addToMemoryCache(Message.ONLINE_STATUS,isOnline?"1":"-1");
    }


    /**
     * 发送我们的局部广播
     */
    public static void sendBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(IMApp.get()).sendBroadcast(intent);
    }

    public static void runAsyTask(final Runnable runnable){
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public static void Toast(String mes){
        Toast.makeText(IMApp.get(),mes,Toast.LENGTH_SHORT).show();
    }

    /**
     *  true 代表有网，false 无网
     * @return
     */
    public static boolean getNetStatus(){
        //获取联网状态的NetworkInfo对象
        ConnectivityManager connectivityManager = (ConnectivityManager) IMApp.get().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.i(TAG,"设备有网络！");
                        return true;
                    }
                }
            }
        }
        Log.i(TAG,"设备无网络！");
        return false;
    }
}
