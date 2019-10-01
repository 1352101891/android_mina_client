package com.lq.im.service.handle;

import android.util.Log;

import com.lq.im.service.listener.Listener;
import com.pcm.mina.service.model.IMPackage;
import com.lq.im.service.model.Message;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author ZERO
 * @Description 客户端handle
 */
public class MinaClientHandler extends IoHandlerAdapter {
    public static String TAG="[MinaClientHandler]";
    public Listener listener;
    public int idleCount=0;

    public MinaClientHandler(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        String msg = message.toString();
        Log.w(TAG,"客户端接收的数据:" + msg);
        if(msg.equals(Message.CMD_HEARTBEAT_REQUEST)){
            Log.w(TAG,"客户端接收心跳包:" + msg);
            session.write(Message.CMD_HEARTBEAT_RESPONSE);
            idleCount=0;
            return;
        }

        if (message instanceof IMPackage){
            listener.process((IMPackage) message);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        Log.e(TAG,"发生错误...", cause);
        session.close(true);
        IMPackage imPackage=new IMPackage();
        imPackage.setKey(Message.CLOSE_TYPE);
        imPackage.setCode(Message.ReturnCode.CODE_500);
        imPackage.setMessage(cause.getMessage());
        listener.process(imPackage);
        Log.i(TAG,"连接关闭");
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        Log.i(TAG,"messageSent");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        Log.i(TAG,"sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        Log.i(TAG,"sessionOpened");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        session.close(true);
        IMPackage imPackage=new IMPackage();
        imPackage.setKey(Message.CLOSE_TYPE);
        listener.process(imPackage);
        Log.i(TAG,"连接关闭");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        Log.i(TAG,"sessionIdle");
        idleCount++;
        if (idleCount>2){
            sessionClosed(session);
        }
    }

}
