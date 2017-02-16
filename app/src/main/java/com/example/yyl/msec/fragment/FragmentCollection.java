package com.example.yyl.msec.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.activity.ContentActivity;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.entity.SaveData;
import com.example.yyl.msec.utils.ReFlashListView;
import com.example.yyl.msec.utils.SaveDataAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYL on 2016/8/7.
 */
public class FragmentCollection extends Fragment implements ReFlashListView.IReflashListener{
    private ReFlashListView listView;
    public static List<SaveData> saveDataList=new ArrayList<>();
    private static Handler handler=new Handler();
    public static SaveDataAdapter saveDataAdapter;
    private MyDataBaseHelper dataBaseHelper;
    private boolean isCollected=true;
    private  String id;
    private static  String username;
    private int count=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        initialize(view);


        return view;
    }
    public static  void setUsername(String user)
    {
        username=user;

    }


    private void initialize(View view) {
        saveDataAdapter=new SaveDataAdapter(getContext());
        listView= (ReFlashListView) view.findViewById(R.id.listview);
        listView.setInterface(this);


        //Collections.reverse(saveDataList);
        saveDataAdapter.setData(saveDataList);
        listView.setAdapter(saveDataAdapter);
        saveDataAdapter.onDateChange(saveDataList);


//        Bundle bundle = getArguments();
//
//            if (bundle != null) {
//                if (bundle.getString("id") != null) {
//                    id = bundle.getString("id");
//                    System.out.println("getStringid  new" + id);
//                    username = getusername(id);
//                } else
//                    username = "未登录";
//            } else {
//                System.out.println("bundle is null");
//            }

//Toast.makeText(getContext(),"collections:username:"+username,Toast.LENGTH_SHORT).show();








        if(username.equals("未登录"))
        {
            saveDataList.clear();

        }
        else {
            initSavadataList(saveDataList, username);
            saveDataAdapter.notifyDataSetChanged();
        }







        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context;
                context = getActivity().getApplicationContext();
                Intent intent=new Intent(context, ContentActivity.class);
                SaveData savedata=saveDataList.get(position-1);

                intent.putExtra("picurl",savedata.picurl);
                intent.putExtra("contenturl",savedata.contenturl);
                intent.putExtra("title",savedata.title);
//                System.out.println("usernameFragmentCollection"+username);
//                intent.putExtra("usrname",username);
                intent.putExtra("isCollected",isCollected);
                startActivity(intent);
            }
        });
        //listview为空时动态添加一个提示布局

        TextView emptyView = new TextView(getContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(username.equals("未登录"))
        {
            emptyView.setText("未登录,没有收藏");
        }
        else
        emptyView.setText("还木有收藏哦");

        emptyView.setTextColor(Color.BLACK);
        emptyView.setTextSize(20);
        emptyView.setPadding(0,50,0,0);

        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        saveDataAdapter.notifyDataSetChanged();
        listView.setEmptyView(emptyView);
        saveDataAdapter.notifyDataSetChanged();

    }
    private String   getusername(String id){

        //打开或创建test.db数据库
        SQLiteDatabase db = getContext().openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        //打开或创建test.db数据库
        // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);




        Cursor c = db.rawQuery("select * from users where id=?",new String[]{id});
        System.out.println("getusername id:"+c.getCount());

        c.moveToFirst();
        for(int j=0;j<c.getCount();j++)
        { c.moveToPosition(j);
            username=c.getString(c.getColumnIndex("username"));
            System.out.println("username:"+username);


        }
        db.close();





        return username;

    }

    private void setIntent(Intent intent,int position){
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
       // intent.getStringExtra("isCollected");

        db.close();

    }

    private boolean queryNotice( String title){
        dataBaseHelper=new MyDataBaseHelper(getContext(),"test.db",null,1);

        //获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from notice where title=?",new String[]{title});
        if(c.getCount()==1){
            System.out.println("true:title");
            db.close();
            return true;
        }
        else{
            System.out.println("false:title");
            System.out.println("title"+c.getCount());
        }
        return false;
    }

    private boolean queryNews(String title){

        dataBaseHelper=new MyDataBaseHelper(getContext(),"test.db",null,1);

        //获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from news where title=?",new String[]{title});
        if(c.getCount()==1){
            System.out.println("true:title");
            db.close();
            return true;
        }
        else{
            System.out.println("false:title");
            System.out.println("title"+c.getCount());
        }
        return false;
    }

    public static void remove(String contenturl){
    List<SaveData> templist=new ArrayList<>();
    for(SaveData save:saveDataList)
    {
        if(save.contenturl.equals(contenturl))
        {
            templist.add(save);

        }

    }
    saveDataList.removeAll(templist);
    if(saveDataAdapter!=null)
    {
        System.out.println("saveDataAdapter is not null");
      //  saveDataAdapter.onDateChange(saveDataList);
        saveDataAdapter.notifyDataSetChanged();

    }else {
        System.out.println("saveDataAdapter is null");
    }

}
  public void  initSavadataList(List<SaveData> saveDataList,String username){
      dataBaseHelper=new MyDataBaseHelper(getContext(),"test.db",null,1);

//获取数据库对象
      SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

      Cursor c= db.rawQuery("select * from collections where username=?",new String[]{username});

      System.out.println("csize"+c.getCount());
       c.moveToFirst();
      saveDataList.clear();
     for(int i=0;i<c.getCount();i++)
     {
         c.moveToPosition(i);
         SaveData saveData=new SaveData();
         //intent.putExtra("picurl",c.getString(c.getColumnIndex("picUrl")));

         saveData.picurl=c.getString(c.getColumnIndex("picurl"));
         saveData.contenturl=c.getString(c.getColumnIndex("contenturl"));
         saveData.title=c.getString(c.getColumnIndex("title"));
         saveData.time=c.getString(c.getColumnIndex("time"));
         if(!issave(saveData.contenturl))
         {
             saveDataList.add(saveData);
         }
     }
      db.close();
  }

    public void initSavadataList(String sql, List<SaveData> saveDataList,String username){
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

            Context context=getContext();

            MyDataBaseHelper dataBaseHelper=new MyDataBaseHelper(context,"test.db",null,1);
            SQLiteDatabase  db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();


            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                SaveData saveData=new SaveData();
                saveData.contenturl=rs.getString("contenturl");
                saveData.title=rs.getString("title");
                saveData.time=rs.getString("time");
                saveData.picurl=rs.getString("picurl");
                if(!issave(saveData.contenturl))
                {
                    saveDataList.add(0,saveData);
                }
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

    /**
     * 用来保存数据
     */
    public static void save(String title,String Picurl,String time,String contentUrl)
    {

        SaveData save=new SaveData();
        save.picurl=Picurl;
        save.time=time;
        save.title=title;
        save.contenturl=contentUrl;


        System.out.println("save:title"+title);
        System.out.println("save:Picurl"+Picurl);
        System.out.println("save:time"+time);
        System.out.println("save:contentUrl"+contentUrl);


        if(!issave(contentUrl))
        {
            saveDataList.add(save);
         //  saveDataAdapter.onDateChange(saveDataList);
          saveDataAdapter.notifyDataSetChanged();
        }
        System.out.println("saveDataListSize:"+saveDataList.size());
        if(saveDataAdapter!=null)
        {

            System.out.println("saveDataAdapter is not null");
            //saveDataAdapter.onDateChange(saveDataList);

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
        for(SaveData save:saveDataList)
        {
            if(save.contenturl.equals(contenturl))
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
                initSavadataList(saveDataList, username);
                saveDataAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(),"已是最新收藏",Toast.LENGTH_SHORT).show();
                //通知listview 刷新数据完毕；
                listView.reflashComplete();
            }
        }, 500);
    }
    @Override
    public void onLoad() {
                listView.loadComplete();


    }
}
