package com.example.yyl.msec.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.activity.ContentActivity;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.entity.Const;
import com.example.yyl.msec.entity.Notice;
import com.example.yyl.msec.utils.NoticeAdapter;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYL on 2016/8/7.
 */
public class FragmentNotices extends Fragment implements ReFlashListView.IReflashListener ,SearchView.OnQueryTextListener{
    private MyDataBaseHelper dataBaseHelper;
    private ReFlashListView listView;
    private List<Notice> noticeList=new ArrayList<>();
    private Handler handler=new Handler();
    private String MoreUrl;
    private  int loadCount=1;
    //private MyAdapterWithCommonViewHolder myadapter;
    private NoticeAdapter jsonAdapter;
    private SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initialize(view);


        return view;
    }

    private void initialize(View view) {
        // newsList=new ArrayList<>();
        searchView= (SearchView) view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
        searchView.setFocusable(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });

        listView= (ReFlashListView) view.findViewById(R.id.listview);
        listView.setInterface(this);
        listView.setTextFilterEnabled(true);

        jsonAdapter=new NoticeAdapter(getContext());
       // final String url ="http://120.27.33.180:8080/SCU_News_Notice/findPageNews/14/1";
        //   new HttpJsonThread(url,getContext(),listView,jsonAdapter,handler).start();


        getNewDataFromNet();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context;
                context = getActivity().getApplicationContext();
                Intent intent=new Intent(context, ContentActivity.class);
                Notice notice=noticeList.get(position-1);
                intent.putExtra("contenturl",notice.contentUrl);
                intent.putExtra("title",notice.title);
                intent.putExtra("time",notice.time);

                startActivity(intent);
            }
        });

    }




    private void setIntent(Intent intent,int position){
//        //打开或创建test.db数据库
//        SQLiteDatabase db = getContext().openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        dataBaseHelper=new MyDataBaseHelper(getContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor c = db.query("notice",null,null,null,null,null,null);//查询并获得游标
        // Toast.makeText(LoginActivity.this,"用户数："+c.getCount(),Toast.LENGTH_SHORT).show();
        System.out.println("FragmentNew setIntent:notice sqlite:"+c.getCount());
        c.moveToFirst();
        c.moveToPosition(position-1);

        intent.putExtra("contenturl",c.getString(c.getColumnIndex("contenturl")));
        intent.putExtra("title",c.getString(c.getColumnIndex("title")));
        intent.putExtra("time",c.getString(c.getColumnIndex("time")));

        db.close();
    }

    @Override
    public void onReflash() {
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                //获取最新数据
                getNewDataFromNet();
                Toast.makeText(getContext(),"已是最新数据",Toast.LENGTH_SHORT).show();
                //通知listview 刷新数据完毕；
                listView.reflashComplete();
            }
        }, 500);
    }

    private void getNewDataFromNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url=Const.NOTICE+"1";
                getNotice(url);
                System.out.println("noticeList --url1："+noticeList.size());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        jsonAdapter.setData(noticeList);
                        jsonAdapter.notifyDataSetChanged();
                        listView.setAdapter(jsonAdapter);
                    }
                });



            }
        }).start();

    }




    private List<Notice> getNotice(String url) {


        URL httpurl = null;
        try {
            httpurl = new URL(url);
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) httpurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(5000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {

                    sb.append(str);
                }

                parseJson(sb.toString());
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


        return noticeList;
    }


    private List<Notice> parseJson(String result) {
        //JsonObject object=new JsonObject(json);


        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jsonObject;
            Notice notice;
            for (int i = 0; i < ja.length(); i++) {
                jsonObject = (JSONObject) ja.get(i);

                notice = new Notice();
                notice.id = jsonObject.getInt("id");
                notice.contentUrl = jsonObject.getString("content");
               // notice.picUrl = jsonObject.getString("pic");
                notice.time = jsonObject.getString("time");
                notice.title = jsonObject.getString("title");

                noticeList.add(notice);
            }

            return noticeList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return noticeList;
    }

    //获取页数，用于下拉刷新加载数据
    public int getPage()
    {
        int count=noticeList.size();
        int page=count/10;
        return page+1;
    }
    @Override
    public void onLoad() {


        MoreUrl = Const.NOTICE +getPage()+ "";
//        if (loadCount<4) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    noticeList = getNotice(MoreUrl);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //通知Listview更新Listview
                            //listView.setAdapter(jsonAdapter);
                            jsonAdapter.onDateChange(noticeList);
                            listView.loadComplete();
                        }
                    }, 2000);
                }
            }).start();
//            loadCount++;

//        } else {
//            Toast.makeText(getContext(), "已没有更多数据", Toast.LENGTH_SHORT).show();
//            listView.loadComplete();
//
//        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus(); // 不获取焦点

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            // Clear the text filter.
            listView.clearTextFilter();
        } else {
            // Sets the initial value for the text filter.
            listView.setFilterText(newText.toString());
        }
        return true;
    }
}











