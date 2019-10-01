package com.lq.im.db.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lq.im.IMApp;
import com.lq.im.db.model.PersonModel;
import java.util.ArrayList;
import java.util.List;

public class UserDBHelper implements BaseDbHelper<PersonModel> {
    private String TAG = "[UserDBHelper]";

    /*表名*/
    private static final String TABLE_NAME_PERSON = "person";
    /*id字段*/
    private static final String VALUE_ID = "_id";
    private static final String VALUE_NAME = "name";
    private static final String VALUE_ISBOY = "isboy";
    private static final String VALUE_AGE = "age";
    private static final String VALUE_ADDRESS = "address";
    private static final String VALUE_USERNAME = "username";
    private static final String VALUE_PASSWORD = "password";

    private static SQLiteDatabase sqLiteDatabase;

    public synchronized SQLiteDatabase getWritableDatabase(){
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
    public boolean add(PersonModel model) {
        //把数据添加到ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_NAME, model.getName());
        values.put(VALUE_AGE, model.getAge());
        values.put(VALUE_ISBOY, model.getIsBoy());
        values.put(VALUE_ADDRESS, model.getAddress());
        values.put(VALUE_USERNAME, model.getUsername());
        values.put(VALUE_PASSWORD, model.getPassword());

        Cursor cursor= getWritableDatabase().rawQuery(" select count(*) from "+TABLE_NAME_PERSON +
               "  where "+VALUE_USERNAME+" = "+model.getUsername()+ " and "+
                VALUE_PASSWORD+" = "+model.getPassword(), null);
        if (cursor.moveToNext()){//有数据不添加
            Log.e(TAG,"已经存在用户，不添加数据库记录！");
            return false;
        }
        cursor.close();
        long index=0;
        try {
            //添加数据到数据库
            index= getWritableDatabase().insert(TABLE_NAME_PERSON, null, values);
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
    public PersonModel addReturnID(PersonModel model) {
        //把数据添加到ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_NAME, model.getName());
        values.put(VALUE_AGE, model.getAge());
        values.put(VALUE_ISBOY, model.getIsBoy());
        values.put(VALUE_ADDRESS, model.getAddress());
        long index=0;
        try {
            //添加数据到数据库
            index = getWritableDatabase().insert(TABLE_NAME_PERSON, null, values);
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
    public void addSql(PersonModel model) {

        //格式： insert into 表名 （字段名,字段名,…）value('字段值','字段值','…')
        //看着很多，其实就是这个 insert into person (name,age,isboy,address) values('五天','3','0','上海市浦东新区x606','[B@5340395')
        String insertSql = "insert into " + TABLE_NAME_PERSON + " (" +
                VALUE_NAME + "," +
                VALUE_AGE + "," +
                VALUE_ISBOY + "," +
                VALUE_ADDRESS + "," +
                VALUE_USERNAME + "," +
                VALUE_PASSWORD + ")" +
                " values" + "(" +
                "'" + model.getName() + "'," +
                "'" + model.getAge() + "'," +
                "'" + model.getIsBoy() + "'," +
                "'" + model.getAddress() + "'," +
                "'" + model.getUsername() + "'," +
                "'" + model.getPassword() + "'" +
                ")";

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
    public void deleteData(PersonModel model) {
        //where后跟条件表达式 =,!=,>,<,>=,<=
        //多条件  and or
        try {
            //删除数据库里的model数据 因为_id具有唯一性。
            getWritableDatabase().delete(TABLE_NAME_PERSON, VALUE_ID + "=?", new String[]{"" + model.getId()});
        }finally {
//            closeDB();
        }
    }

    /**
     * 方法修改数据库数据
     */
    public void update(PersonModel model) {
        //条件表达式 =,!=,>,<,>=,<=
        //多条件 and or  and和or都可以无限连接
        //多条件示例 _id>=? and _id<=?
        //多条件示例 _id>=? or _id=? or _id=?

        //将数据添加至ContentValues
        ContentValues values = new ContentValues();
        values.put(VALUE_NAME, model.getName());
        values.put(VALUE_ADDRESS, model.getAddress());
        values.put(VALUE_ISBOY, model.getIsBoy());
        values.put(VALUE_AGE, model.getAge());
        values.put(VALUE_USERNAME, model.getUsername());
        values.put(VALUE_PASSWORD, model.getPassword());
        try {
            //修改model的数据
            getWritableDatabase().update(TABLE_NAME_PERSON, values, VALUE_ID + "=?", new String[]{"" + model.getId()});
        }finally {
//            closeDB();
        }

        /*//将 _id>20 的数据全部修改成model  适合重置数据
        getWritableDatabase().update(TABLE_NAME_PERSON,values,VALUE_ID+">?",new String[]{"20"});
        //将 _id>=30 && _id<=40 的数据全部修改成model  适合重置数据
        getWritableDatabase().update(TABLE_NAME_PERSON,values,VALUE_ID+">=? and "+VALUE_ID+"<=?",new String[]{"30","40"});
        //将 _id>=40 || _id=30 || _id=20的 age 修改成18 (需先将model的数据修成成18) 这里and 和 or 的效果时一样的 因为_id是唯一的
        int count = getWritableDatabase().update(TABLE_NAME_PERSON,values,VALUE_ID+">=?"+" or "+VALUE_ID+"=?"+" or "+VALUE_ID+"=?",new String[]{"40","30","20"});*/

        // count 返回被修改的条数  >0 表示修改成功
        Log.e(TAG, "" + VALUE_ID + ">=? and " + VALUE_ID + "<=?");
        Log.e(TAG, "" + VALUE_ID + ">=?" + " or " + VALUE_ID + "=?" + " or " + VALUE_ID + "=?");

    }

    /**
     * 查询全部数据
     */
    public List<PersonModel> queryAll(String where) {
        String sql=" select * from "+TABLE_NAME_PERSON + " where "+where;
        //查询全部数据
        Cursor cursor = getWritableDatabase().rawQuery(sql,null);
        List<PersonModel> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            //移动到首位
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                int id = cursor.getInt(cursor.getColumnIndex(VALUE_ID));
                String name = cursor.getString(cursor.getColumnIndex(VALUE_NAME));
                int isBoy = cursor.getInt(cursor.getColumnIndex(VALUE_ISBOY));
                int age = cursor.getInt(cursor.getColumnIndex(VALUE_AGE));
                String address = cursor.getString(cursor.getColumnIndex(VALUE_ADDRESS));
                String username = cursor.getString(cursor.getColumnIndex(VALUE_USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(VALUE_PASSWORD));

                PersonModel model = new PersonModel();
                model.setId(id);
                model.setName(name);
                model.setIsBoy(isBoy);
                model.setAge(age);
                model.setAddress(address);
                model.setUsername(username);
                model.setPassword(password);

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
            //查询全部数据
            cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, null, null, null, null, null);
            //查询 _id = 1 的数据
            cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, VALUE_ID + "=?", new String[]{"1"}, null, null, null);
            //查询 name = 张三 并且 age > 23 的数据
            cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, VALUE_NAME + "=?" + " and " + VALUE_AGE + ">?", new String[]{"张三", "23"}, null, null, null);
            //查询 name = 张三 并且 age > 23 的数据  并按照id 降序排列
            cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, VALUE_NAME + "=?" + " and " + VALUE_AGE + ">?", new String[]{"张三", "23"}, null, null, VALUE_ID + " desc");
            //查询数据按_id降序排列 并且只取前4条。
            cursor = getWritableDatabase().query(TABLE_NAME_PERSON, null, null, null, null, null, VALUE_ID + " desc", "0,4");
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
            //查询全部数据
            rawQuerySql =  "select * from "+TABLE_NAME_PERSON;
            //查询_id = 1 的数据  select * from person where _id = 1
            rawQuerySql = "select * from "+TABLE_NAME_PERSON+" where "+VALUE_ID +" = 1";

            //查询 name = 张三 并且 age > 23 的数据  通配符？ select * from person where name = ? and age > ?
            rawQuerySql = "select * from "+TABLE_NAME_PERSON+" where "+VALUE_NAME +" = ?"+" and "+ VALUE_AGE +" > ?";
    //        cursor = getWritableDatabase().rawQuery(rawQuerySql,new String[]{"张三","23"});

            //查询 name = 张三 并且 age >= 23 的数据  select * from person where name = '张三' and age >= '23'
            rawQuerySql = "select * from "+TABLE_NAME_PERSON+" where "+VALUE_NAME +" = '张三'"+" and "+ VALUE_AGE +" >= '23'";

            //查询 name = 张三 并且 age >= 23 的数据  并按照id 降序排列  select * from person where name = '张三' and age >= '23' order by _id desc
            rawQuerySql = "select * from "+TABLE_NAME_PERSON+" where "+VALUE_NAME +" = '张三'"+" and "+ VALUE_AGE +" >= '23'"+" order by "+VALUE_ID +" desc";

            //查询数据按_id降序排列 并且只取前4条。(测试下标是从0开始)  select * from person order by _id desc limit 0, 4
            rawQuerySql = "select * from "+TABLE_NAME_PERSON+" order by "+VALUE_ID +" desc"+" limit 0, 4";

            //查询年龄在20岁以上或者是女生 的数据 select age,isboy from person where age > 20 or isboy != 1
            rawQuerySql = "select "+VALUE_AGE+","+VALUE_ISBOY +" from " +TABLE_NAME_PERSON+" where "+VALUE_AGE+" > 20"+" or "+VALUE_ISBOY +" != 1";

            //查询年龄小于等于20 或者 大于等于 80的数据 并且按年龄升序排列 select * from person where age <= 20 or age >=80 order by age asc
            rawQuerySql = "select * from "+TABLE_NAME_PERSON+" where "+VALUE_AGE+" <= 20"+" or "+VALUE_AGE+" >=80"+" order by "+VALUE_AGE+" asc";

            cursor = getWritableDatabase().rawQuery(rawQuerySql,null);
        }finally {
//            closeDB();
        }
        Log.e(TAG, rawQuerySql );

        return cursor;

    }

}
