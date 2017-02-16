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
import com.example.yyl.msec.entity.Notice;

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
public class SaveNoticeService extends Service {
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
                String url= Const.NOTICE;
                System.out.println("page+url"+url+page);
                while(!isBlank(url+page))
                {

                    try {
                        System.out.println("page+url"+url+page);
                        writeToSqlite(getNotice(url+page));

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


    private List<Notice> getNotice(String  url)
    {

        List<Notice>  noticelist=new ArrayList<>();
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

                noticelist=parseJson(sb.toString());
                System.out.println(url+"      "+noticelist.size());
                for(int i=0;i<noticelist.size();i++){
                    System.out.println("noticelize:"+noticelist.size());
                    System.out.println("time:"+noticelist.get(i).time);
                    System.out.println("content"+noticelist.get(i).contentUrl);
                    System.out.println("title"+noticelist.get(i).title);
                  //  System.out.println("pic"+noticelist.get(i).picUrl);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return noticelist;
    }
    private void showsqlite(){

//创建MySQLiteOpenHelper辅助类对象
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //打开或创建test.db数据库
//        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        Cursor c = db.query("notice",null,null,null,null,null,null);//查询并获得游标

        System.out.println("total notice size:"+c.getCount());
    }

    private void writeToSqlite( List<Notice> noticeList) throws SQLException {
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

//        //打开或创建test.db数据库
//        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
//        // db.execSQL("DROP TABLE IF EXISTS news");
//        //创建users表
//        db.execSQL("CREATE TABLE IF NOT EXISTS notice("+
//                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "contenturl VARCHAR,"+
//              //  "picUrl VARCHAR,"+
//                "title VARCHAR,"+
//                "time VARCHAR)");
        for(int i=0;i<noticeList.size();i++){
            ContentValues cv = new ContentValues();

            cv.put("contenturl", noticeList.get(i).getContentUrl());
          //  cv.put("picUrl", newslist.get(i).getPicUrl());
            cv.put("title", noticeList.get(i).getTitle());
            cv.put("time", noticeList.get(i).getTime());
            //插入ContentValues中的数据
            db.insert("notice", null, cv);
            System.out.println(" notice is writing to sqlite"+noticeList.get(i).getContentUrl());
        }

        Cursor c = db.query("notice",null,null,null,null,null,null);//查询并获得游标
        System.out.println("notice size:"+c.getCount());
 //       db.endTransaction();
        db.close();
    }

    private List<Notice> parseJson(String result){
        //JsonObject object=new JsonObject(json);
        List<Notice> notceList=new ArrayList<Notice>();

        try {
            JSONArray ja=new JSONArray(result);
            JSONObject jsonObject;
            Notice notice;
            for(int i=0;i<ja.length();i++){
                jsonObject = (JSONObject) ja.get(i);

                notice=new Notice();
                notice.id=jsonObject.getInt("id");
                notice.contentUrl=jsonObject.getString("content");

                notice.time=jsonObject.getString("time");
                notice.title=jsonObject.getString("title");
//                if(jsonObject.has("pic"))
//                {
//                    if(jsonObject.getString("pic")!=null)
//                    {
//                        news.picUrl=jsonObject.getString("pic");
//                    }
//                }
//                else
//                {
//                    news.picUrl="";
//                }
                notceList.add(notice);
            }

            return notceList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notceList;
    }

}
