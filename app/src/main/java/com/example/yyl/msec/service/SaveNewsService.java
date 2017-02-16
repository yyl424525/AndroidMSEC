package com.example.yyl.msec.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.entity.Const;
import com.example.yyl.msec.entity.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYL on 2016/11/7.
 */
public class SaveNewsService extends Service {
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
                int page=1;
               // String url= "http://120.27.33.180:8080/SCU_News_Notice/findPageNews/14/";
                String url= Const.NEWS;
                System.out.println("page+url"+url+page);
                while(!isBlank(url+page))
                {

                    try {
                        System.out.println("page+url"+url+page);
                        writeToSqlite(getNews(url+page));

                        page++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }


             showsqlite();

            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
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
        Cursor c = db.query("news",null,null,null,null,null,null);//查询并获得游标
      //  System.out.println("news size:"+c.getCount());
        System.out.println("total news size:"+c.getCount());
    }

    private void writeToSqlite( List<News> newslist) throws SQLException {


        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
          SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //打开或创建test.db数据库
//        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
//         db.execSQL("DROP TABLE IF EXISTS news");
//        //创建users表
//        db.execSQL("CREATE TABLE IF NOT EXISTS news("+
//                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "contenturl VARCHAR,"+
//                "picUrl VARCHAR,"+
//                "title VARCHAR,"+
//                "time VARCHAR)");
        for(int i=0;i<newslist.size();i++){
            ContentValues cv = new ContentValues();

            cv.put("contenturl", newslist.get(i).getContentUrl());
            cv.put("picUrl", newslist.get(i).getPicUrl());
            cv.put("title", newslist.get(i).getTitle());
            cv.put("time", newslist.get(i).getTime());
            //插入ContentValues中的数据
            db.insert("news", null, cv);
            System.out.println(" news is writing to sqlite"+newslist.get(i).getContentUrl());
        }

        Cursor c = db.query("news",null,null,null,null,null,null);//查询并获得游标
        System.out.println("news size:"+c.getCount());
    //    db.endTransaction();
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
