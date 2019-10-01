package com.lq.im.service.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lq.im.service.connect.SocketConnect;
import com.lq.im.service.listener.ExceptListener;
import com.lq.im.service.listener.MessageListener;
import com.lq.im.util.AppTool;
import com.pcm.mina.service.model.IMPackage;
import com.lq.im.util.SafeCheck;

import java.util.List;

public class IMManager {
    private static final String TAG="[IMManager]";

    private SocketConnect socketConnect;
    private static IMManager imManager;
    private Context context;

    public static synchronized IMManager getInstance(Context context){
        if (imManager==null){
            imManager=new IMManager(context);
        }
        return imManager;
    }

    private IMManager(Context context) {
        this.context=context;

        if (socketConnect==null){
            socketConnect=new SocketConnect();
            socketConnect.addListener(new ExceptListener());
            socketConnect.addListener(new MessageListener(true));
        }
    }

    public void startConnect(){
        if (isOnline()){
            return;
        }
        AppTool.runAsyTask(new Runnable() {
            @Override
            public void run() {
                socketConnect.startConnect();
            }
        });
    }

    public void notifyNetConnected(){
        if (socketConnect!=null){
            socketConnect.notifyNetConnected();
        }
    }

    public void sendMessages(List<IMPackage> list){
        if (SafeCheck.isListEmpty(list)){
            return;
        }
        for (IMPackage sentBody: list) {
            sendMessage(sentBody);
        }
    }

    public void sendMessage(IMPackage sentBody){
        if (socketConnect.isConnected()){
            socketConnect.send(sentBody);
        }else {
            Log.i(TAG,"连接中断无法登陆！");
        }
    }

    public boolean isOnline(){
        return socketConnect.isOnline();
    }

    public String getServerInfo(){
        return "";
    }


    public void destroy(){
        if (socketConnect!=null)
            socketConnect.clear();
        imManager=null;
        context=null;
    }

}
