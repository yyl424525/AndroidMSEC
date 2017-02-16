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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.activity.ContentActivity;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.entity.Const;
import com.example.yyl.msec.entity.News;
import com.example.yyl.msec.utils.NewsAdapter;
import com.example.yyl.msec.utils.ReFlashListView;
import com.example.yyl.msec.utils.ReFlashListView.IReflashListener;

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
public class FragmentNews extends Fragment implements IReflashListener,SearchView.OnQueryTextListener{
    private MyDataBaseHelper dataBaseHelper;
    private ReFlashListView listView;
    private static List<News> newsList=new ArrayList<>();
    private static List<News> tempnewsList=new ArrayList<>();

    private Handler handler=new Handler();
    private String MoreUrl;
    private  String username;
private  int loadCount=1;
    private SearchView searchView;
    private LinearLayout main_linearlayout;

    //private MyAdapterWithCommonViewHolder myadapter;
    private static NewsAdapter jsonAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initView(view);


        return view;
    }

    private void initView(View view) {
       // newsList=new ArrayList<>();
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            username=bundle.getString("username");
            System.out.println("news bundle+username:"+username);
        }else
        {
            System.out.println("news bundle  is null null");
            username="未登录";
        }
main_linearlayout= (LinearLayout) view.findViewById(R.id.mail_linearlayout);
       searchView= (SearchView) view.findViewById(R.id.search_view);
//        int id = searchView.getContext().getResources().getIdentifier("android:id/search_view", null, null);
//        TextView textView = (TextView) searchView.findViewById(id);
//        textView.setTextColor(Color.RED);
        searchView.setOnQueryTextListener(this);
        searchView.setFocusable(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                main_linearlayout.setVisibility(View.VISIBLE);
                return false;
            }
        });


        listView= (ReFlashListView) view.findViewById(R.id.listview);
        listView.setInterface(this);


        listView.setTextFilterEnabled(true);

        jsonAdapter=new NewsAdapter(getContext());
        //初始化数据
        getNewDataFromNet();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context;
                context = getActivity().getApplicationContext();
                Intent intent=new Intent(context, ContentActivity.class);
                News news=newsList.get(position-1);
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("news", (Serializable) news);

                intent.putExtra("picurl",news.picUrl);
                intent.putExtra("isnews","isnews");
                intent.putExtra("contenturl",news.contentUrl);
                intent.putExtra("title",news.title);
                intent.putExtra("time",news.time);
                System.out.println("usernamenews"+username);
                intent.putExtra("usrname",username);
                intent.putExtra("isCollected",news.isCollected);


              startActivity(intent);
            }
        });

//        ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//               newsList.remove(position);
//              jsonAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });



    }




    private void setIntent(Intent intent,int position){
//        //打开或创建test.db数据库
//        SQLiteDatabase db = getContext().openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        dataBaseHelper=new MyDataBaseHelper(getContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor c = db.query("news",null,null,null,null,null,null);//查询并获得游标
        // Toast.makeText(LoginActivity.this,"用户数："+c.getCount(),Toast.LENGTH_SHORT).show();
        System.out.println("FragmentNew setIntent:newssqlite:"+c.getCount());
        c.moveToFirst();
        c.moveToPosition(position-1);
            intent.putExtra("picurl",c.getString(c.getColumnIndex("picUrl")));
            intent.putExtra("contenturl",c.getString(c.getColumnIndex("contenturl")));
            intent.putExtra("title",c.getString(c.getColumnIndex("title")));
            intent.putExtra("time",c.getString(c.getColumnIndex("time")));

        db.close();
    }

    public static void remove(String contenturl){
        List<News> templist=new ArrayList<>();
        for(News news:newsList)
        {
            if(news.contentUrl.equals(contenturl))
            {
                templist.add(news);
            }

        }
        newsList.removeAll(templist);
        if(jsonAdapter!=null)
        {
            System.out.println("jsonAdapter is not null");
            jsonAdapter.onDateChange(newsList);

        }else {
            System.out.println("jsonAdapter is null");
        }

    }

    /**
     * 用来保存数据
     */
    public static void save(String title,String Picurl,String time,String contentUrl)
    {

        News news=new News();
        news.picUrl=Picurl;
        news.time=time;
        news.title=title;
        news.contentUrl=contentUrl;


        System.out.println("news:title"+title);
        System.out.println("news:Picurl"+Picurl);
        System.out.println("news:time"+time);
        System.out.println("news:contentUrl"+contentUrl);


        if(!issave(contentUrl))
        {
            newsList.add(0,news);
        }

        System.out.println("saveDataListSize:"+newsList.size());
        if(jsonAdapter!=null)
        {
            System.out.println("saveDataAdapter is not null");
            jsonAdapter.onDateChange(newsList);

        }else {
            System.out.println("saveDataAdapter is null");
        }
    }

    /**
     * 判断是否已保存过
     * @return
     */
    public static boolean issave(String contenturl)
    {
        boolean flag=false;
        for(News news:newsList)
        {
            if(news.contentUrl.equals(contenturl))
            {
                flag=true;
                break;
            }
        }
        return flag;
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
                    String url=Const.NEWS+"1";
                    getNews(url);
                    System.out.println("newsList --url1："+newsList.size());

                    handler.post(new Runnable() {
                        @Override

                        public void run() {
                            jsonAdapter.setData(newsList);
                            jsonAdapter.notifyDataSetChanged();
                            listView.setAdapter(jsonAdapter);
                        }

                    });

            }
        }).start();

    }
    private List<News> getNews(String url) {

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


        return newsList;
    }
    private List<News> getNews2(String url) {

        List<News> newslist = new ArrayList<>();
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


        return newslist;
    }
    private List<News> parseJson(String result) {
        //JsonObject object=new JsonObject(json);


        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jsonObject;
            News news;
            for (int i = 0; i < ja.length(); i++) {
                jsonObject = (JSONObject) ja.get(i);

                news = new News();
                news.id = jsonObject.getInt("id");
                news.contentUrl = jsonObject.getString("content");
                news.picUrl = jsonObject.getString("pic");
                news.time = jsonObject.getString("time");
                news.title = jsonObject.getString("title");
                if (jsonObject.has("pic")) {
                    if (jsonObject.getString("pic") != null) {
                        news.picUrl = jsonObject.getString("pic");
                    }
                } else {
                    news.picUrl = "";
                }
//                for(News news1:newsList)
//                {
//                    if(news1.contentUrl.equals(news.contentUrl))
//                    {
//
//                    }else
//
//                        newsList.add(news);
//                }

                if(!issave(news.contentUrl))
                {
                    newsList.add(news);
                }

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        tempnewsList=newsList;
        return newsList;
    }
    //获取页数，用于下拉刷新加载数据
    public int getPage()
    {
        int count=newsList.size();
        int page=count/10;
        System.out.println("news page"+page);
        return page+1;
    }
    @Override
    public void onLoad() {


        MoreUrl=Const.NEWS+getPage()+"";
        if(loadCount<4)
        {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    newsList = getNews(MoreUrl);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //通知Listview更新Listview
                            //listView.setAdapter(jsonAdapter);
                            jsonAdapter.onDateChange(newsList);
                            listView.loadComplete();
                        }
                    },2000);
                }
            }).start();
loadCount++;
        }
        else{
            Toast.makeText(getContext(), "已没有更多数据", Toast.LENGTH_SHORT).show();
            listView.loadComplete();

        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
//        if (searchView != null) {
//            // 得到输入管理对象
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
//                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
//            }
            searchView.clearFocus(); // 不获取焦点
//        }
        return true;
    }
    // 当搜索框内内容发生改变时执行
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











