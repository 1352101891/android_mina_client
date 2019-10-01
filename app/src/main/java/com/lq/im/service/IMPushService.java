package com.lq.im.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.lq.im.callback.NetListener;
import com.lq.im.db.model.MessageModel;
import com.lq.im.receiver.NetWorkChangeReceiver;
import com.lq.im.service.manager.IMManager;
import com.lq.im.service.model.IMPackageFactory;
import com.lq.im.service.model.Message;
import com.lq.im.util.AppTool;
import com.lq.im.util.Constants;

public class IMPushService extends Service {
    private static final String TAG="[IMPushService]";
    private BroadcastReceiver receiver,netWorkReceiver;
    private IMManager imManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void stop(Context context){
        Intent intent  = new Intent(context, IMPushService.class);
        intent.setAction(Constants.ACTION_START_IMSERVICE);
        context.stopService(intent);
    }

    public static void start(Context context){
        Intent intent  = new Intent(context, IMPushService.class);
        intent.setAction(Constants.ACTION_START_IMSERVICE);
        context.startService(intent);
    }

    public static void send(Context context,MessageModel messageModel){
        Intent intent  = new Intent(context, IMPushService.class);
        intent.setAction(Constants.ACTION_SEND_MESSAGE);
        intent.putExtra(Message.IMPACKAGE,messageModel);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBR();
        imManager= IMManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action=intent.getAction();
        if (action.equals(Constants.ACTION_START_IMSERVICE)){
            if (!imManager.isOnline()){
                imManager.startConnect();
            }
        }else if (action.equals(Constants.ACTION_SEND_MESSAGE)){
            if (!imManager.isOnline()){
                AppTool.Toast("离线状态，无法发送消息！");
            }else {
                MessageModel messageModel= (MessageModel) intent.getSerializableExtra(Message.IMPACKAGE);
                sendMessage(messageModel);
            }
        }
        return START_STICKY;
    }

    private void sendMessage(final MessageModel messageModel){
        AppTool.runAsyTask(new Runnable() {
            @Override
            public void run() {
                imManager.sendMessage(IMPackageFactory.get(messageModel));
            }
        });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBR();
        imManager.destroy();
    }

    private void registerBR() {

        //监听网络变化
        IntentFilter netFilter=new IntentFilter();
        netFilter.addAction(Constants.CONNECTIVITY_CHANGE);
        netFilter.addAction(Constants.WIFI_STATE_CHANGED);
        netFilter.addAction(Constants.STATE_CHANGE);
        netWorkReceiver=new NetWorkChangeReceiver();
        ((NetWorkChangeReceiver) netWorkReceiver).setNetListener(new NetListener() {
            @Override
            public void netStatusChange(boolean isConnected) {
                imManager.notifyNetConnected();
            }
        });
        //全局接收器
        this.registerReceiver(netWorkReceiver,netFilter);


        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                switch (action){
                    case Constants.BC_CONFLIT_IM:
                        break;
                    case Constants.BC_LOGINED_IM:
                        break;
                    case Constants.BC_MESSAGE_IM:
                        break;
                    case Constants.BC_OFFLINE_IM:
                        break;
                    case Constants.BC_RECONNECT_IM:
                        break;
                }
            }
        };
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.BC_CONFLIT_IM);
        filter.addAction(Constants.BC_LOGINED_IM);
        filter.addAction(Constants.BC_MESSAGE_IM);
        filter.addAction(Constants.BC_OFFLINE_IM);
        filter.addAction(Constants.BC_RECONNECT_IM);
        //应用内接收器
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
    }

    private void unRegisterBR() {
        if (receiver!=null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
        if (netWorkReceiver!=null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(netWorkReceiver);
        }
    }
}
