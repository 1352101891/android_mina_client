package com.lq.im.db;

import android.content.Context;
import com.lq.im.db.DBHelper.DBOpenHelper;
import com.lq.im.db.DBHelper.UserDBHelper;
import com.lq.im.db.model.PersonModel;

import java.util.ArrayList;
import java.util.List;

public class MySqlTool {

    public static List<String> getDBSqls(){
        List<String> list=new ArrayList<>();
        //用户表
        list.add("create table if not exists  person (" +
                " _id integer primary key AUTOINCREMENT ," +
                " name text ," +
                " isboy integer," +
                " age integer," +
                " address text," +
                " username text," +
                " password text" +
                ")");
        //消息表
        list.add("create table if not exists  IMPackage (" +
                " id integer primary key AUTOINCREMENT ," +
                " sender text ," +
                " receiver text," +
                " message text," +
                " createTime INTEGER," +
                " isRead integer" +
                ")");
        return list;
    }

    public static void alterDB(Context context){
        PersonModel personModel=new PersonModel("123456","1478963");
        new UserDBHelper().add(personModel);
    }
}
