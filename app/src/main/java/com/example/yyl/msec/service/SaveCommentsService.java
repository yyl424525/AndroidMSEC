package com.example.yyl.msec.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.entity.CommentBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by YYL on 2016/11/7.
 */
public class SaveCommentsService extends Service {
    private MyDataBaseHelper dataBaseHelper;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new Thread(new Runnable() {
            @Override
            public void run() {
              connectMysql();

            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }
    public void initSavadataList(String sql, List<CommentBean> commentBeanList){
        Connection conn = null;

        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
        // 避免中文乱码要指定useUnicode和characterEncoding
        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
        // 下面语句之前就要先创建javademo数据库
        String url = "jdbc:mysql://www.yylcloud.com:3306/test?"
                + "user=root&password=BDS26Ia2C0KX&useUnicode=true&characterEncoding=UTF8";

        try {
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以

            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动


            System.out.println("成功加载MySQL驱动程序");

            Context context=getApplicationContext();

            MyDataBaseHelper dataBaseHelper=new MyDataBaseHelper(context,"test.db",null,1);
            SQLiteDatabase  db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            sql ="select * from comments";
           ResultSet rs = stmt.executeQuery(sql);
           while (rs.next()){
               CommentBean commentBean=new CommentBean();
               commentBean.setCommentContent(rs.getString("content"));
               commentBean.setCommentTime(rs.getString("time"));
               commentBean.setCommentNickname(rs.getString("username"));
               commentBean.setContentUrl(rs.getString("contenturl"));

               commentBeanList.add(commentBean);

           }
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        }

    }
    public ResultSet connectMysql()
    {ResultSet rs = null;
        Connection conn = null;
        String sql;
        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
        // 避免中文乱码要指定useUnicode和characterEncoding
        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
        // 下面语句之前就要先创建javademo数据库
        String url = "jdbc:mysql://www.yylcloud.com:3306/test?"
                + "user=root&password=BDS26Ia2C0KX&useUnicode=true&characterEncoding=UTF8";

        try {
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以

            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动


            System.out.println("成功加载MySQL驱动程序");

            Context context=getApplicationContext();

            MyDataBaseHelper dataBaseHelper=new MyDataBaseHelper(context,"test.db",null,1);
            SQLiteDatabase  db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            sql ="select * from comments";
            rs = stmt.executeQuery(sql);
           writeToSqlite(rs);

        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        }
        return rs;
    }

    private void showsqlite(){
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //打开或创建test.db数据库
       // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        Cursor c = db.query("comments",null,null,null,null,null,null);//查询并获得游标
      //  System.out.println("news size:"+c.getCount());
        System.out.println("total comments size:"+c.getCount());
    }

    private void writeToSqlite(ResultSet rs) throws SQLException {


        //打开或创建test.db数据库
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS comments");
        db.execSQL("CREATE TABLE comments(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(30) NOT NULL," +
                "contenturl VARCHAR(100) NOT NULL," +
                "content VARCHAR(100)," +
                "time  VARCHAR(30) )");
        int i=1;
        while (rs.next())
        {

            ContentValues cv = new ContentValues();

            cv.put("username", rs.getString("username"));
            cv.put("content",rs.getString("content"));
            cv.put("contenturl",rs.getString("contenturl"));
            cv.put("time",rs.getString("time"));


            //插入ContentValues中的数据
            db.insert("comments", null, cv);
            System.out.println("comments"+i);
            i++;

        }
    //    db.endTransaction();
        db.close();
    }


}
