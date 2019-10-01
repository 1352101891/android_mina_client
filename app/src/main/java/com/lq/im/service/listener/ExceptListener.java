package com.lq.im.service.listener;

import android.content.Intent;
import com.lq.im.util.AppTool;
import com.pcm.mina.service.model.IMPackage;
import com.lq.im.service.model.Message;
import com.lq.im.util.Constants;

public class ExceptListener implements Listener<IMPackage>{
    public static String TAG="[ExceptHandler]";


    @Override
    public void process(IMPackage imPackage) {
        if (Message.CLOSE_TYPE.equals(imPackage.getKey())){
            Intent intent=new Intent(Constants.BC_OFFLINE_IM);
            AppTool.sendBroadcast(intent);
        }else if (Message.CHAT_CONFLICT.equals(imPackage.getKey())){
            Intent intent=new Intent(Constants.BC_CONFLIT_IM);
            AppTool.sendBroadcast(intent);
        }
    }

    @Override
    public boolean shouldProcess(IMPackage imPackage) {
        if (Message.CLOSE_TYPE.equals(imPackage.getKey())
            || Message.CHAT_CONFLICT.equals(imPackage.getKey())){
            return true;
        }
        return false;
    }

    @Override
    public void clear() {

    }

}
