package com.example.yyl.msec.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YYL on 2016/10/28.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context context;

    private static final String CREATE_USERS="CREATE TABLE IF NOT EXISTS users( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username  VARCHAR," +
            "password VARCHAR," +
            "mail VARCHAR," +
            "sex VARCHAR)";
    private static final String CREATE_NEWS="CREATE TABLE IF NOT EXISTS news("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "contenturl VARCHAR,"+
            "picUrl VARCHAR,"+
            "title VARCHAR,"+
            "time VARCHAR)";
    private static  final String CREATE_NOTICE="CREATE TABLE IF NOT EXISTS notice("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "contenturl VARCHAR,"+
            "title VARCHAR,"+
            "time VARCHAR)";
    private static final String CREATE_COLLECTIONS="CREATE TABLE IF NOT EXISTS collections("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username VARCHAR(30) NOT NULL,"+
            "contenturl VARCHAR(100) NOT NULL,"+
            "picturl VARCHAR(100),"+
            "title     VARCHAR(30) NOT NULL,"+
            "time  VARCHAR(30) NOT NULL)";


    private static final String CREATE_COMMENTS="CREATE TABLE IF NOT EXISTS comments("+
            "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username    VARCHAR(30) NOT NULL,"+
            "contenturl  VARCHAR(100) NOT NULL,"+
            "content     VARCHAR(100) NOT NULL,"+
            "time        VARCHAR(30))";

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        //CursorFactory设置为null,使用默认值
        super(context, name, factory, version);
        this.context=context;
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_NOTICE);
        db.execSQL(CREATE_COLLECTIONS);
        db.execSQL(CREATE_COMMENTS);
      // Toast.makeText(context,"Create Users Succed",Toast.LENGTH_SHORT).show();
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //  db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
    }
}
