package com.lq.im.service.connect;

import android.util.Log;
import com.lq.im.service.handle.MinaClientHandler;
import com.lq.im.service.listener.Listener;
import com.lq.im.service.manager.ReconnectManager;
import com.lq.im.util.AppTool;
import com.lq.im.util.Cache;
import com.lq.im.util.Constants;
import com.pcm.mina.service.model.IMPackage;
import com.lq.im.service.model.IMPackageFactory;
import com.lq.im.service.model.Message;
import com.lq.im.util.SafeCheck;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import static com.lq.im.util.Constants.HOST;
import static com.lq.im.util.Constants.PORT;
import static com.lq.im.util.Constants.TIME_OUT;

public class SocketConnect implements Listener<IMPackage> {
    private static final String TAG="[SocketConnect]";
    private IoConnector connector;
    private IoSession session;
    private List<Listener<IMPackage>> listenerList;
    private boolean isOnline=false;
    private ReconnectManager reconnectManager;
    private ConnectFuture connectFuture;

    public SocketConnect() {
        listenerList=new ArrayList<>();
        if (reconnectManager==null){
            reconnectManager=new ReconnectManager(this);
        }
    }

    /**
     * 掉线重新连接
     */
    private void reConnect(){
        if (reconnectManager!=null){
            reconnectManager.startReconnect();
        }
    }

    public void notifyNetConnected(){
        if (reconnectManager!=null && !isOnline()){
            reconnectManager.wakeThread();
        }
    }

    public synchronized void startConnect(){
        if (isOnline()){
            AppTool.Toast("已经在线，不能再次登录！");
            return;
        }
        if (session!=null) {
            close();
        }

        if (connector==null) {
            connector = new NioSocketConnector();
            // 设置链接超时时间,30s
            connector.setConnectTimeoutMillis(TIME_OUT);
            // 添加过滤器  可序列话的对象
            connector.getFilterChain().addLast(
                    "codec",
                    new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
            // 添加业务逻辑处理器类
            connector.setHandler(new MinaClientHandler(this));
        }
        connect();
        login();
    }

    /**
    * 测试服务端与客户端程序！
        a. 启动服务端，然后再启动客户端
        b. 服务端接收消息并处理成功;
    */
    private synchronized void connect() {
        connectFuture = connector.connect(new InetSocketAddress(
                HOST, PORT));// 创建连接
        connectFuture.awaitUninterruptibly();// 等待连接创建完成
        try {
            session = connectFuture.getSession();// 获得session
        }catch (Exception e){
            //获取session失败之后，发送重连通知
            close();
            Log.e(TAG,"错误："+e.getMessage());

            reConnect();
        }
    }

    private void login(){
        IMPackage imPackage= IMPackageFactory.get(Message.BIND_TYPE,"我要登陆");
        send(imPackage);
    }

    public void send(IMPackage imPackage){
        try {
            if (session!=null) {
                session.write(imPackage);// 发送消息
                Log.i(TAG, "发送的消息为:" + imPackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("链接异常...", e.getMessage());
            close();
        }
    }

    public synchronized void close(){
        if (connectFuture!=null){
            connectFuture.cancel();
        }
        if (session!=null) {
            session.close(true);
        }
    }

    public void addListener(Listener listener){
        if (listenerList==null){
            listenerList=new ArrayList<>();
        }
        listenerList.add(listener);
    }

    @Override
    public void process(IMPackage imPackage) {
        if (!shouldProcess(imPackage)){
            return;
        }
        if (Message.BIND_TYPE.equals(imPackage.getKey())){
            //登陆成功
            if (imPackage.getCode().equals(Message.ReturnCode.CODE_200)) {
                setOnlineStatus(true);
            }
        }else if (Message.CLOSE_TYPE.equals(imPackage.getKey())){
            setOnlineStatus(false);
            reConnect();//意外下线，需要重连
        }else if (Message.CHAT_CONFLICT.equals(imPackage.getKey())){
            setOnlineStatus(false);//冲突下线，不处理
        }
        if (SafeCheck.isListValide(listenerList)){
            for (Listener listener:listenerList) {
                if (listener.shouldProcess(imPackage)) {
                    listener.process(imPackage);
                }
            }
        }
    }

    @Override
    public boolean shouldProcess(IMPackage imPackage) {
        return true;
    }

    public void setOnlineStatus(boolean isOnline){
        this.isOnline=isOnline;
        AppTool.setOnline(isOnline);
    }

    /**
     * 是否登录
     * @return
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * 是否联通
     */
    public boolean isConnected() {
        return session!=null && session.isConnected();
    }

    @Override
    public void clear() {
        //清空数据
        if (reconnectManager!=null) {
            reconnectManager.destroy();
            reconnectManager=null;
        }
        //关闭连接
        close();
        if (connector!=null) {
            connector.dispose();
            connector = null;
        }
        isOnline=false;
        if (SafeCheck.isListValide(listenerList)){
            for (Listener l:listenerList) {
                l.clear();
            }
            listenerList.clear();
            listenerList=null;
        }
        connectFuture=null;
        session=null;
    }

}
