package com.lq.im.db.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lq.im.db.MySqlTool;
import com.lq.im.util.SafeCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShaoQuanwei on 2017/2/15.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static int Version=2;
    private static final String TAG = "[MySqliteHelper]";
    private static final String DB_NAME="IM_DB";
    private static DBOpenHelper openHelper;

    public static synchronized DBOpenHelper getInstance(Context context){
        if (openHelper==null){
            openHelper= new DBOpenHelper(context);
        }
        return openHelper;
    }

    private DBOpenHelper(Context context)
    {
        this(context, DB_NAME, Version);
    }


    public DBOpenHelper(Context context,String name)
    {
        this(context, name, Version);
    }

    //在SQLiteOpenHelper的子类当中，必须有该构造函数
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        //必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }
    //参数说明
    //context:上下文对象
    //name:数据库名称
    //param:factory
    //version:当前数据库的版本，值必须是整数并且是递增的状态

    public DBOpenHelper(Context context,String name,int version)
    {
        this(context,name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> list= MySqlTool.getDBSqls();
        for (int i=0;i<list.size();i++) {
            db.execSQL(list.get(i));
        }
        Log.d(TAG, "-------> onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "-------> onUpgrade");
    }

    public void exeSqls(List<String> sqls){
        if (!SafeCheck.isListEmpty(sqls)){
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();//开启事物
            try {
                for (String sql:sqls) {
                    db.execSQL(sql);
                }
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
                db.close();
            }
        }
    }
}