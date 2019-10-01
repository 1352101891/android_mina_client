package com.lq.im.db.DBHelper;

import android.database.Cursor;
import java.util.List;

public interface BaseDbHelper<T> {

    /**
     * @param model 数据模型
     * @return 返回添加数据有木有成功
     */
    public boolean add(T model);
    /**
     * 添加数据
     * @param model 数据模型
     * @return 返回添加数据有木有成功
     */
    public T addReturnID(T model);

    /**
     * sql语句添加数据，比较麻烦
     */
    public void addSql(T model);


    /**
     * 方法删除数据库数据
     */
    public void deleteData(T model);

    /**
     * 方法修改数据库数据
     */
    public void update(T model);

    /**
     * 查询全部数据
     */
    public List<T> queryAll(String where);

    /**
     * query()方法查询
     * 一些查询用法
     */
    public Cursor query();


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
    public Cursor rawQuery();

}
