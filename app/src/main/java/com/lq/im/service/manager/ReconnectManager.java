package com.lq.im.service.manager;

import android.util.Log;
import com.lq.im.service.connect.SocketConnect;
import com.lq.im.util.AppTool;
import com.lq.im.util.Constants;

public class ReconnectManager {
    private static final String TAG="[ReconnectManager]";
    private Thread reConnectThread=null;
    private SocketConnect socketConnect;
    private boolean isConecting;
    private boolean isRunning=true;
    private int count=0;
    private int delay=1000;

    public ReconnectManager(SocketConnect socketConnect) {
        this.socketConnect = socketConnect;
        isConecting=false;
    }

    public void startReconnect() {
        if (socketConnect.isOnline() ){
            Log.e(TAG,"已经在线不需要重连！");
            return;
        }
        if (isConecting){
            Log.e(TAG,"正在重连，请勿重复操作！");
            return;
        }

        startTask();
    }

    private void startTask(){
        isConecting=false;
        count=0;

        if (reConnectThread==null){
            reConnectThread = new Thread() {
                @Override
                public void run() {
                    while (isRunning) {
                        try {
                            isConecting=true;
                            Log.i(TAG,"重连线程正在工作!，" +
                                    "在线状态：isConnected："+socketConnect.isConnected()
                                    +",isOnline："+socketConnect.isOnline());
                            if (!socketConnect.isConnected()){
                                socketConnect.startConnect();
                                count++;
                                Log.i(TAG,"再次尝试重连!");
                                //休眠一段时间
                                Thread.sleep(getIntervalTime());
                            }else if (socketConnect.isOnline()){
                                isConecting=false;
                                count=0;
                                Log.i(TAG,"已经重连，线程休眠等待下次调度!");
                                Thread.sleep(Integer.MAX_VALUE);
                            }else {
                                Log.i(TAG,"正在登陆!");
                                //休眠一段时间
                                Thread.sleep(delay);
                            }

                            netChangeHandle();

                        }catch (InterruptedException e){//阻塞状态中进行中断线程操作
                            Log.i(TAG,"睡眠终止，重连线程开始工作!");
                        }
                    }
                }
            };
            reConnectThread.start();
        }else if (reConnectThread.isAlive()){
            wakeThread();
        }
    }

    /**
     * 如果遇到断网的情况下，一直休眠
     */
    private void netChangeHandle() throws InterruptedException {
        int timeInterval = 0;
        if (!AppTool.getNetStatus()){
            timeInterval= Integer.MAX_VALUE;
        }
        if (reConnectThread!=null
                && reConnectThread == Thread.currentThread()){
            Log.i(TAG,"休眠时长："+ timeInterval);
            Thread.sleep(timeInterval);
        }
    }

    /**
     * 唤醒休眠线程
     */
    public void wakeThread(){
        if (reConnectThread!= null) {
            //唤醒线程
            reConnectThread.interrupt();
        }
    }

    public long getIntervalTime(){
        int totalInterval =delay*count;
        if (totalInterval > Constants.MAX_INTERVAL){
            totalInterval = Constants.MAX_INTERVAL;
        }
        return totalInterval;
    }

    public void destroy(){
        isConecting=false;
        socketConnect=null;
        isRunning=false;
        if (reConnectThread!=null) {
            reConnectThread.interrupt();
        }
        reConnectThread=null;
    }
}
