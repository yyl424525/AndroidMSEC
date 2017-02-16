package com.example.yyl.msec.thread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.yyl.msec.entity.News;
import com.example.yyl.msec.utils.NewsAdapter;
import com.example.yyl.msec.utils.ReFlashListView;

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
 * Created by YYL on 2016/11/4.
 */
public class HttpJsonThread extends  Thread {
    private String url;
    private Context context;
    private ReFlashListView listView;
    private NewsAdapter jsonAdapter;
    private Handler handler;
    public HttpJsonThread(String url, Context context, ReFlashListView listView, NewsAdapter jsonAdapter, Handler handler){
        this.url=url;
        this.context=context;
        this.listView=listView;
        this.jsonAdapter=jsonAdapter;
        this.handler=handler;

    }


    @Override
    public void run() {
        super.run();

        URL httpurl = null;
        try {
            httpurl = new URL(url);
            HttpURLConnection httpURLConnection= null;
            try {
                httpURLConnection = (HttpURLConnection) httpurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(5000);
                BufferedReader  reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuffer sb=new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {

                    sb.append(str);
                }

                final List<News> newslist=parseJson(sb.toString());
                for(int i=0;i<newslist.size();i++){
                    System.out.println("newslize:"+newslist.size());
                    System.out.println("time:"+newslist.get(i).time);
                    System.out.println("content"+newslist.get(i).contentUrl);
                    System.out.println("title"+newslist.get(i).title);
                    System.out.println("pic"+newslist.get(i).picUrl);
                }

                try {
                    writeToSqlite(newslist);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        jsonAdapter.setData(newslist);
                        listView.setAdapter(jsonAdapter);



                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private void writeToSqlite( List<News> newslist) throws SQLException {
        //打开或创建test.db数据库
        SQLiteDatabase db = context.openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
      //  db.execSQL("DROP TABLE IF EXISTS news");
        //创建users表
        db.execSQL("CREATE TABLE IF NOT EXISTS news("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "contenturl VARCHAR,"+
                "picUrl VARCHAR,"+
                "title VARCHAR,"+
                "time VARCHAR)");
        for(int i=0;i<newslist.size();i++){
            ContentValues cv = new ContentValues();

            cv.put("contenturl", newslist.get(i).getContentUrl());
            cv.put("picUrl", newslist.get(i).getPicUrl());
            cv.put("title", newslist.get(i).getTitle());
            cv.put("time", newslist.get(i).getTime());
            //插入ContentValues中的数据
            db.insert("news", null, cv);
            System.out.println("write"+newslist.get(i).getContentUrl());
        }


        db.close();
    }
    private void show(){
        //打开或创建test.db数据库
        SQLiteDatabase db = context.openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        Cursor c = db.query("news",null,null,null,null,null,null);//查询并获得游标
       // Toast.makeText(LoginActivity.this,"用户数："+c.getCount(),Toast.LENGTH_SHORT).show();
        System.out.println("newssqlite:"+c.getCount());
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++)
        { c.moveToPosition(i);


            String title = c.getString(c.getColumnIndex("title"));
            String time = c.getString(c.getColumnIndex("time"));
            String contentUrl = c.getString(c.getColumnIndex("contentUrl"));
            String picUrl = c.getString(c.getColumnIndex("picUrl"));
           System.out.println("title:"+title);
           System.out.println("time:"+time);
           System.out.println("contentUrl:"+contentUrl);
           System.out.println("picUrl:"+picUrl);
        }
db.close();
    }

    public List<News> parseJson(String result){
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
