package com.lq.im.service.listener;

import android.content.Intent;
import android.util.Log;

import com.lq.im.db.DBHelper.MessageDBHelper;
import com.lq.im.service.model.IMPackageFactory;
import com.lq.im.util.AppTool;
import com.pcm.mina.service.model.IMPackage;
import com.lq.im.service.model.Message;
import com.lq.im.util.Constants;

public class MessageListener implements Listener<IMPackage>{
    public static String TAG="[MessageHandler]";
    public boolean async;
    public MessageDBHelper dbHelper;

    public MessageListener(boolean async) {
        this.async = async;
        dbHelper=new MessageDBHelper();
    }

    @Override
    public void process(final IMPackage imPackage) {
        if (async){
            AppTool.runAsyTask(new Runnable() {
                @Override
                public void run() {
                    processActual(imPackage);
                }
            });
        }else {
            processActual(imPackage);
        }
    }

    public void processActual(IMPackage imPackage){
        Intent intent = null;
        if (Message.PUSH_TYPE.equals(imPackage.getKey())) {
            intent = new Intent(Constants.BC_MESSAGE_IM);
        } else if (Message.BIND_TYPE.equals(imPackage.getKey())) {
            //登陆成功
            if (imPackage.getCode().equals(Message.ReturnCode.CODE_200)) {
                intent = new Intent(Constants.BC_LOGINED_IM);
            }
        } else if (Message.CHAT_TYPE.equals(imPackage.getKey())) {
            intent = new Intent(Constants.BC_MESSAGE_IM);
            if (Message.ReturnCode.CODE_201.equals(imPackage.getCode())) {
                Log.i(TAG,"对方已经成功收到消息！");
            }else  if (Message.ReturnCode.CODE_202.equals(imPackage.getCode())){
                imPackage.put(Message.SENDER,"系统");
                imPackage.put(Message.MESSAGE,"对方不在线");
                dbHelper.add(IMPackageFactory.get(imPackage));
            }else {
                dbHelper.add(IMPackageFactory.get(imPackage));
            }
        }
        if (intent != null) {
            AppTool.sendBroadcast(intent);
        }
    }

    @Override
    public boolean shouldProcess(IMPackage imPackage) {
        if (Message.PUSH_TYPE.equals(imPackage.getKey())
                || Message.BIND_TYPE.equals(imPackage.getKey())
                || Message.CHAT_TYPE.equals(imPackage.getKey())){
            return true;
        }
        return false;
    }
    @Override
    public void clear() {

    }

}
