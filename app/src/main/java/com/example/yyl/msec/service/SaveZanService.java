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
import com.example.yyl.msec.entity.News;
import com.example.yyl.msec.entity.SaveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYL on 2016/11/7.
 */
public class SaveZanService extends Service {
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
    public void initSavadataList(String sql, List<SaveData> saveDataList){
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
            sql ="select * from dianzan";
           ResultSet rs = stmt.executeQuery(sql);


           while (rs.next()){
               SaveData saveData=new SaveData();
               saveData.contenturl=rs.getString("contenturl");
               saveData.title=rs.getString("title");
               saveData.time=rs.getString("time");
               saveData.picurl=rs.getString("picurl");
               saveDataList.add(saveData);

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

//            MyDataBaseHelper dataBaseHelper=new MyDataBaseHelper(context,"test.db",null,1);
//            SQLiteDatabase  db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            sql ="select * from dianzan";
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
private boolean isBlank(String url){

    URL httpurl = null;
    try {
        httpurl = new URL(url);
        HttpURLConnection httpURLConnection= null;
        try {
            httpURLConnection = (HttpURLConnection) httpurl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(5000);
            BufferedReader reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuffer sb=new StringBuffer();

            String str;
            if ((str = reader.readLine()) .equals( "[]")) {

             return true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    } catch (MalformedURLException e) {
        e.printStackTrace();
    }

return false;

}


    private List<News> getNews(String  url)
    {

        List<News>  newslist=new ArrayList<>();
        URL httpurl = null;
        try {
            httpurl = new URL(url);
            HttpURLConnection httpURLConnection= null;
            try {
                httpURLConnection = (HttpURLConnection) httpurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(5000);
                BufferedReader reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuffer sb=new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {

                    sb.append(str);
                }

                newslist=parseJson(sb.toString());
//                System.out.println(url+"      "+newslist.size());
//                for(int i=0;i<newslist.size();i++){
//                    System.out.println("newslize:"+newslist.size());
//                    System.out.println("time:"+newslist.get(i).time);
//                    System.out.println("content"+newslist.get(i).contentUrl);
//                    System.out.println("title"+newslist.get(i).title);
//                    System.out.println("pic"+newslist.get(i).picUrl);
//                }



            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return newslist;
    }
    private void showsqlite(){
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //打开或创建test.db数据库
       // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        Cursor c = db.query("dianzan",null,null,null,null,null,null);//查询并获得游标
      //  System.out.println("news size:"+c.getCount());
        System.out.println("total dianzan size:"+c.getCount());
    }

    private void writeToSqlite(ResultSet rs) throws SQLException {
//        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);
////获取数据库对象
//        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();


       // 打开或创建test.db数据库
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS dianzan");
        db.execSQL("CREATE TABLE dianzan(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(30) NOT NULL," +
                "contenturl VARCHAR(100) NOT NULL," +
                "picurl VARCHAR(100)," +
                "title     VARCHAR(30) NOT NULL," +
                "time  VARCHAR(30))");

        int i=1;
        while (rs.next())
        {

            ContentValues cv = new ContentValues();

            cv.put("username", rs.getString("username"));
            cv.put("picurl",rs.getString("picurl"));
            cv.put("contenturl",rs.getString("contenturl"));
            cv.put("time",rs.getString("time"));
            cv.put("title",rs.getString("title"));

            //插入ContentValues中的数据
            db.insert("dianzan", null, cv);
            System.out.println("dianzan"+i);
            i++;
        }
      //  db.endTransaction();
        db.close();
    }

    private List<News> parseJson(String result){
        //JsonObject object=new JsonObject(json);
        List<News> newsList=new ArrayList<News>();

        try {
            JSONArray ja=new JSONArray(result);
            JSONObject jsonObject;
            News news;
            for(int i=0;i<ja.length();i++){
                jsonObject = (JSONObject) ja.get(i);

                news=new News();
                news.id=jsonObject.getInt("id");
                news.contentUrl=jsonObject.getString("content");
                news.picUrl=jsonObject.getString("pic");
                news.time=jsonObject.getString("time");
                news.title=jsonObject.getString("title");
                if(jsonObject.has("pic"))
                {
                    if(jsonObject.getString("pic")!=null)
                    {
                        news.picUrl=jsonObject.getString("pic");
                    }
                }
                else
                {
                    news.picUrl="";
                }
                newsList.add(news);
            }

            return newsList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

}
