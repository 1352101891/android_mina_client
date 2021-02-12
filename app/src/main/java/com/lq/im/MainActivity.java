package com.lq.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lq.im.callback.Listener;
import com.lq.im.db.DBHelper.MessageDBHelper;
import com.lq.im.db.model.MessageModel;
import com.lq.im.service.IMPushService;
import com.lq.im.util.AppTool;
import com.lq.im.util.Cache;
import com.lq.im.util.SafeCheck;
import com.lq.im.service.model.Message;
import com.lq.im.util.Constants;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Listener {
    TextView tVContent;
    BroadcastReceiver receiver;
    StringBuffer stringBuffer;
    Button bLogin;
    EditText etInput;
    ScrollView scrollview;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tVContent=findViewById(R.id.content);
        bLogin=findViewById(R.id.start);
        etInput=findViewById(R.id.input);
        scrollview=findViewById(R.id.scrollview);
        stringBuffer=new StringBuffer();
        handler=new Handler(getMainLooper());
        this.addOnSoftKeyBoardVisibleListener(this);
        etInput.invalidate();
        registerBR();
        loadData();
    }

    public void loadData(){
        Cache.addToMemoryCache(Message.SESSION_KEY,"123456");
        List<MessageModel> messageModels= new MessageDBHelper().queryAll(null);
        if (SafeCheck.isListValide(messageModels)){
            for (MessageModel m: messageModels) {
                appendChat("用户（"+m.getSender()+" ）："+m.getMessage());
            }
        }
    }

    public void registerBR(){

        //监听即时通讯
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.BC_CONFLIT_IM);
        filter.addAction(Constants.BC_LOGINED_IM);
        filter.addAction(Constants.BC_MESSAGE_IM);
        filter.addAction(Constants.BC_OFFLINE_IM);

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action= SafeCheck.s(intent.getAction());
                switch (action){
                    case Constants.BC_CONFLIT_IM:
                        bLogin.setEnabled(true);
                        appendChat("你被挤下线！");
                        break;
                    case Constants.BC_LOGINED_IM:
                        bLogin.setEnabled(false);
                        appendChat("登陆成功！");
                        break;
                    case Constants.BC_MESSAGE_IM:
                        List<MessageModel> messageModels= new MessageDBHelper().queryAllbyStatus(0);
                        if (SafeCheck.isListValide(messageModels)){
                            for (MessageModel m: messageModels) {
                                appendChat("用户（"+m.getSender()+" ）："+m.getMessage());
                            }
                        }
                        break;
                    case Constants.BC_OFFLINE_IM:
                        bLogin.setEnabled(true);
                        appendChat("您已经下线！");
                        break;
                }

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
    }

    private void unRegisterBR() {
        if (receiver!=null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterBR();
        IMPushService.stop(this);
        Cache.clear();
        super.onDestroy();
    }

    public void start(View view) {
        if (AppTool.isOnline()){
            AppTool.Toast("您在线，无需重复连接！");
            return;
        }
        IMPushService.start(this);
    }

    public void send(View view) {
        if (!AppTool.isOnline()){
            AppTool.Toast("您不在线，无法发送消息！");
            return;
        }
        if (etInput.getText().toString().trim().length()==0){
            AppTool.Toast("消息为空，不能发送！");
            return;
        }
        MessageModel messageModel=new MessageModel();
        messageModel.setSender(Cache.getFromMemCache(Message.SESSION_KEY));
        messageModel.setReceiver(Cache.getFromMemCache(Message.SESSION_KEY));
        messageModel.setCreateTime(Long.valueOf(System.currentTimeMillis()).intValue());
        messageModel.setMessage(etInput.getText().toString().trim());
        IMPushService.send(this,messageModel);
        appendChat("我："+messageModel.getMessage());
    }

    public void appendChat(String message){
        stringBuffer.setLength(0);
        stringBuffer.append(tVContent.getText().toString().trim());
        stringBuffer.append("\n");
        stringBuffer.append(message);
        tVContent.setText(stringBuffer);
        etInput.setText(null);
        scrollToBottom();
    }

    public void scrollToBottom(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },500);
    }

    public void addOnSoftKeyBoardVisibleListener(final Listener listener) {
        final View decorView= this.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect=new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                //计算出可见屏幕的高度
                int displayHight= rect.bottom - rect.top;
                //获得屏幕整体的高度
                int hight = decorView.getHeight();
                //获得键盘高度
                int keyboardHeight = hight - displayHight;
                        //如果新的可见高度小于一定比例了，我们就认为键盘已经弹起
                boolean visible= (displayHight / hight) < 0.8;
                listener.onSoftKeyBoardVisible(visible, keyboardHeight);
            }
        });
    }

    @Override
    public void onSoftKeyBoardVisible(boolean visible,int height) {
        scrollToBottom();
    }

    public void clear(View view) {
        new MessageDBHelper().deleteData(null);
        tVContent.setText("");
    }
}


