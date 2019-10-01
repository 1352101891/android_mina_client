package com.lq.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lq.im.callback.NetListener;
import com.lq.im.util.AppTool;
import com.lq.im.util.Cache;
import com.lq.im.util.Constants;
import com.lq.im.util.SafeCheck;

/**
 * 监听网络状态变化
 * Created by Travis on 2017/10/11.
 */

public class NetWorkChangeReceiver extends BroadcastReceiver {

    public boolean hasNet(String netType){
        return !SafeCheck.isNull(netType);
    }

    /**
     * 获取连接类型
     *
     * @param type
     * @return
     */
    private String getConnectionType(int type) {
        String connType = null;
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "sim网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (netListener==null){
            return;
        }
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.e("TAG", "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            processCallback(AppTool.getNetStatus());
        }
    }

    public void processCallback(boolean isConnected){
        if (netListener!=null){
            netListener.netStatusChange(isConnected);
        }
        Cache.addToMemoryCache(Constants.NET_STATUS,isConnected?"1":"0");
    }

    private NetListener netListener;
    public void setNetListener(NetListener netListener) {
        this.netListener = netListener;
    }

}


