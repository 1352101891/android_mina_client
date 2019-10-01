package com.lq.im.service.model;

import com.lq.im.db.model.MessageModel;
import com.lq.im.util.Cache;
import com.lq.im.util.SafeCheck;
import com.pcm.mina.service.model.IMPackage;

public class IMPackageFactory {


    /**
     * 按类型获取IM包
     * @param type
     * @return
     */
    public static IMPackage get(String type, String message){
        String account=Cache.getFromMemCache(Message.SESSION_KEY);

        IMPackage imPackage=new IMPackage();
        if (Message.BIND_TYPE.equals(type)){
            imPackage.put(Message.SESSION_KEY, account);
        }else if (Message.CLOSE_TYPE.equals(type)){

        }else if (Message.PUSH_TYPE.equals(type)){
            imPackage.put(Message.MESSAGE,account);
        }
        imPackage.put(Message.MESSAGE,message);
        imPackage.setKey(type);
        imPackage.setTimestamp(System.currentTimeMillis());
        return imPackage;
    }


    /**
     * 封装聊天包
     */
    public static IMPackage getChatMessage(String message,String receiver){
        IMPackage imPackage=new IMPackage();
        imPackage.setKey(Message.CHAT_TYPE);
        imPackage.setTimestamp(System.currentTimeMillis());
        imPackage.put(Message.SENDER, Cache.getFromMemCache(Message.SESSION_KEY));
        imPackage.put(Message.MESSAGE, SafeCheck.s(message));
        imPackage.put(Message.RECEIVER,SafeCheck.s(receiver));
        return imPackage;
    }


    public static IMPackage get(MessageModel messageModel){
        IMPackage imPackag=getChatMessage(messageModel.getMessage(),messageModel.getReceiver());
        return imPackag;
    }

    public static MessageModel get(IMPackage imPackage){
        MessageModel messageModel=new MessageModel();
        messageModel.setCreateTime(Long.valueOf(System.currentTimeMillis()).intValue());
        messageModel.setSender(imPackage.get(Message.SENDER));
        messageModel.setReceiver(imPackage.get(Message.RECEIVER));
        messageModel.setMessage(imPackage.get(Message.MESSAGE));
        messageModel.setIsRead(0);
        return messageModel;
    }


}
