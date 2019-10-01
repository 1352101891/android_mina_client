package com.lq.im.db.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.lq.im.IMApp;
import com.lq.im.db.model.MessageModel;
import com.lq.im.db.model.PersonModel;
import com.lq.im.util.SafeCheck;

import java.util.ArrayList;
import java.util.List;

public class MessageDBHelper implements BaseDbHelper<MessageModel> {
    private String TAG = "[UserDBHelper]";

    /*表名*/
    private static final String TABLE_NAME = "IMPackage";
    /*id字段*/
    private static final String VALUE_ID = "id";
    private static final String VALUE_SENDER = "sender";
    private static final String VALUE_RECEIVER = "receiver";
    private static final String VALUE_MESSAGE = "message";
    private static final String VALUE_CREATETIME = "createTime";
    private static final String VALUE_ISREAD = "isRead";

    private SQLiteDatabase sqLiteDatabase;


    public SQLiteDatabase getWritableDatabase(){
        if (sqLiteDatabase==null){
            sqLiteDatabase= DBOpenHelper.getInstance(IMApp.get()).getWritableDatabase();
        }
        return sqLiteDatabase;
    }

    public void closeDB(){
        if (sqLiteDatabase!=null){
            sqLiteDatabase.close();
        }
        sqLiteDatabase=null;
    }

    /**
     * @param model 数据模型
     * @return 返回添加数据有木有成功
     */
    public boolean add(MessageModel model) {
        //把数据添加到ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_SENDER, model.getSender());
        values.put(VALUE_RECEIVER, model.getReceiver());
        values.put(VALUE_MESSAGE, model.getMessage());
        values.put(VALUE_CREATETIME, model.getCreateTime());
        values.put(VALUE_ISREAD, model.getIsRead());
        long index=0;
        try {
            //添加数据到数据库
            index= getWritableDatabase().insert(TABLE_NAME, null, values);
        }finally {
//            closeDB();
        }
        //大于0表示添加成功
        if (index > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加数据
     * @param model 数据模型
     * @return 返回添加数据有木有成功
     */
    public MessageModel addReturnID(MessageModel model) {
        //把数据添加到ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_SENDER, model.getSender());
        values.put(VALUE_RECEIVER, model.getReceiver());
        values.put(VALUE_MESSAGE, model.getMessage());
        values.put(VALUE_CREATETIME, model.getCreateTime());
        values.put(VALUE_ISREAD, model.getIsRead());
        long index=0;
        try {
            //添加数据到数据库
            index = getWritableDatabase().insert(TABLE_NAME, null, values);
        }finally {
//            closeDB();
        }
        if (index != -1) {
            model.setId(index);
            return model;
        } else {
            return null;
        }
    }

    /**
     * sql语句添加数据，比较麻烦
     */
    public void addSql(MessageModel model) {

        //格式： insert into 表名 （字段名,字段名,…）value('字段值','字段值','…')
        //看着很多，其实就是这个 insert into person (name,age,isboy,address) values('五天','3','0','上海市浦东新区x606','[B@5340395')
        String insertSql = "insert into " + TABLE_NAME + " (" +
                VALUE_SENDER + "," +
                VALUE_RECEIVER + "," +
                VALUE_MESSAGE + "," +
                VALUE_CREATETIME + "," +
                VALUE_ISREAD + ")" +
                " values" + "(" +
                "'" + model.getSender() + "'," +
                "'" + model.getReceiver() + "'," +
                "'" + model.getMessage() + "'," +
                "'" + model.getCreateTime() + "'," +
                "'" + model.getIsRead() +")";

        Log.e(TAG, "" + insertSql);
        try {
            getWritableDatabase().execSQL(insertSql);
        }finally {
//            closeDB();
        }
    }


    /**
     * 方法删除数据库数据
     */
    public void deleteData(MessageModel model) {
        if (model==null){
            getWritableDatabase().delete(TABLE_NAME, null,null);
        }else {
            //where后跟条件表达式 =,!=,>,<,>=,<=
            //多条件  and or
            try {
                //删除数据库里的model数据 因为_id具有唯一性。
                getWritableDatabase().delete(TABLE_NAME, VALUE_ID + "=?", new String[]{"" + model.getId()});
            } finally {
//            closeDB();
            }
        }
    }

    /**
     * 方法修改数据库数据
     */
    public void update(MessageModel model) {
        //条件表达式 =,!=,>,<,>=,<=
        //多条件 and or  and和or都可以无限连接
        //多条件示例 _id>=? and _id<=?
        //多条件示例 _id>=? or _id=? or _id=?

        //将数据添加至ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_SENDER, model.getSender());
        values.put(VALUE_RECEIVER, model.getReceiver());
        values.put(VALUE_MESSAGE, model.getMessage());
        values.put(VALUE_CREATETIME, model.getCreateTime());
        values.put(VALUE_ISREAD, model.getIsRead());
        try {
            //修改model的数据
            getWritableDatabase().update(TABLE_NAME, values, VALUE_ID + "=?", new String[]{"" + model.getId()});
        }finally {
//            closeDB();
        }

        /*//将 _id>20 的数据全部修改成model  适合重置数据
        getWritableDatabase().update(TABLE_NAME,values,VALUE_ID+">?",new String[]{"20"});
        //将 _id>=30 && _id<=40 的数据全部修改成model  适合重置数据
        getWritableDatabase().update(TABLE_NAME,values,VALUE_ID+">=? and "+VALUE_ID+"<=?",new String[]{"30","40"});
        //将 _id>=40 || _id=30 || _id=20的 age 修改成18 (需先将model的数据修成成18) 这里and 和 or 的效果时一样的 因为_id是唯一的
        int count = getWritableDatabase().update(TABLE_NAME,values,VALUE_ID+">=?"+" or "+VALUE_ID+"=?"+" or "+VALUE_ID+"=?",new String[]{"40","30","20"});*/

        // count 返回被修改的条数  >0 表示修改成功
        Log.e(TAG, "" + VALUE_ID + ">=? and " + VALUE_ID + "<=?");
        Log.e(TAG, "" + VALUE_ID + ">=?" + " or " + VALUE_ID + "=?" + " or " + VALUE_ID + "=?");

    }

    /**
     * -1全部，1已读，0未读
     * @param isRead
     * @return
     */
    public List<MessageModel> queryAllbyStatus(int isRead) {
        String where="";
        if (isRead==0 || isRead==1){
            where= " isRead= "+isRead+" ";
        }
        List<MessageModel> list= queryAll(where);
        StringBuffer ids=new StringBuffer();
        for (MessageModel m:list) {
            ids.append(","+m.getId());
        }
        //更新已读标志
        if (!SafeCheck.isNull(ids.toString())) {
            String sql = " update " + TABLE_NAME + " set " + VALUE_ISREAD + "=1 where " + VALUE_ID
                    + " in (" + ids.substring(1) + ") ";
            getWritableDatabase().execSQL(sql);
        }
        return list;
    }

    /**
     * 查询全部数据
     */
    public List<MessageModel> queryAll(String where) {

        String sql=" select * from "+TABLE_NAME ;
        if (!SafeCheck.isNull(where)){
            sql+=" where "+where;
        }
        //查询全部数据
        Cursor cursor = getWritableDatabase().rawQuery(sql,null);
        List<MessageModel> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            //移动到首位
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                int id = cursor.getInt(cursor.getColumnIndex(VALUE_ID));
                String sender = cursor.getString(cursor.getColumnIndex(VALUE_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndex(VALUE_RECEIVER));
                String message = cursor.getString(cursor.getColumnIndex(VALUE_MESSAGE));
                int isRead = cursor.getInt(cursor.getColumnIndex(VALUE_ISREAD));
                int createtime = cursor.getInt(cursor.getColumnIndex(VALUE_CREATETIME));

                MessageModel model = new MessageModel();
                model.setId(id);
                model.setMessage(message);
                model.setSender(sender);
                model.setReceiver(receiver);
                model.setIsRead(isRead);
                model.setCreateTime(createtime);

                list.add(model);
                //移动到下一位
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }


    /**
     * query()方法查询
     * 一些查询用法
     */
    public Cursor query() {

        Cursor cursor = null;
        try {
            //查询数据按_id降序排列 并且只取前4条。
            cursor = getWritableDatabase().query(TABLE_NAME, null, null, null, null, null, VALUE_ID + " desc", "0,4");
        }finally {
//            closeDB();
        }
        return cursor;
    }


    /**
     * rawQuery()方法查询
     *
     * 一些查询用法
     *
     * 容易出错，万千注意。
     *
     * 注意空格、单引号、单词不要写错了。
     *
     */
    public Cursor rawQuery() {

        Cursor cursor = null;
        String rawQuerySql = null;
        try {
            //查询年龄小于等于20 或者 大于等于 80的数据 并且按年龄升序排列 select * from person where age <= 20 or age >=80 order by age asc
            rawQuerySql = "select * from "+TABLE_NAME +" order by "+VALUE_CREATETIME;

            cursor = getWritableDatabase().rawQuery(rawQuerySql,null);
        }finally {
//            closeDB();
        }
        Log.e(TAG, rawQuerySql );

        return cursor;

    }

}
