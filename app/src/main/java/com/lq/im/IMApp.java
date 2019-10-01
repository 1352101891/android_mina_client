package com.lq.im;

import android.app.Application;
import android.util.LruCache;

import com.lq.im.db.DBHelper.UserDBHelper;
import com.lq.im.db.MySqlTool;
import com.lq.im.db.model.PersonModel;
import com.lq.im.service.model.Message;
import com.lq.im.util.Cache;

import java.util.concurrent.SynchronousQueue;

public class IMApp extends Application {
    private static IMApp app =null;

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;

        Cache.init();
        alterDB();
    }


    public void alterDB(){
        MySqlTool.alterDB(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static IMApp get() {
        return app;
    }
}
