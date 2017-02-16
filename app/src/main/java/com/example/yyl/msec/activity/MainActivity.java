package com.example.yyl.msec.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yyl.msec.R;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.dialog.CustomBaseDialog;
import com.example.yyl.msec.fragment.FragmentCollection;
import com.example.yyl.msec.fragment.FragmentHome;
import com.example.yyl.msec.fragment.FragmentMe;
import com.example.yyl.msec.interfaces.BackHandledFragment;
import com.example.yyl.msec.interfaces.BackHandledInterface;
import com.example.yyl.msec.service.SaveCollectionsService;
import com.example.yyl.msec.service.SaveCommentsService;
import com.example.yyl.msec.service.SaveUsersService;
import com.example.yyl.msec.service.SaveZanService;
import com.example.yyl.msec.utils.SysApplication;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends FragmentActivity implements BackHandledInterface {
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;


    public static MainActivity instance = null;

    private ImageView image_home,image_love,image_me;
    private TextView   text_home,text_love,text_me;
    private  Fragment fragmentHome,fragmentCollection,fragmentMe;
    private String id;
    private String username;
    private static String staticusrname;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private MyDataBaseHelper dataBaseHelper;

//    private List<View> viewList;
//    private ViewPager viewPager;
//    private PagerTabStrip pagerTabStrip;
//    private List<String> titleList;
private static final int BIGGER = 1;
    private static final int SMALLER = 2;
    private static final int MSG_RESIZE = 1;

    private static final int HEIGHT_THREADHOLD = 30;



    private BackHandledFragment mBackHandedFragment;
    private boolean hadIntercept;
    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }


    @Override
    public void onBackPressed() {
       // showExitDialog();
        final CustomBaseDialog dialog = new CustomBaseDialog(this);
        dialog.show();
    }
public void showExitDialog(){
    final NormalDialog dialog = new NormalDialog(this);
    dialog.content("亲,真的要走吗?再看会儿吧~(●—●)")//
            .style(NormalDialog.STYLE_TWO)//
            .titleTextSize(23)//
            .btnText("继续逛逛", "残忍退出")//
            .btnTextColor(Color.parseColor("#383838"), Color.parseColor("#D4D4D4"))//
            .btnTextSize(16f, 16f)//
            .showAnim(bas_in)//
            .dismissAnim(bas_out)//
            .show();

    dialog.setOnBtnClickL(
            new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                }
            },
            new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.superDismiss();
                   finish();
                }
            });
}

    public void setBasIn(BaseAnimatorSet bas_in) {
        this.bas_in = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.bas_out = bas_out;
    }
    class InputHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RESIZE: {
                if (msg.arg1 == BIGGER) {
                    findViewById(R.id.main_linearlayout).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.main_linearlayout).setVisibility(View.GONE);
                }
            }
            break;

            default:
                break;
        }
        super.handleMessage(msg);
    }
}

    private InputHandler mHandler = new InputHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();


        Intent intent_service=new Intent(this, SaveUsersService.class);
        startService(intent_service);

        Intent collection_service=new Intent(this, SaveCollectionsService.class);
        startService(collection_service);

        Intent comments_service=new Intent(this, SaveCommentsService.class);
        startService(comments_service);

        Intent comments_dianzan=new Intent(this, SaveZanService.class);
        startService(comments_dianzan);


    }

    private void initView() {

//        bas_in = new BounceTopEnter();
//        bas_out = new SlideBottomExit();

        instance=this;
//        fragmentCollection=new FragmentCollection();
//        fragmentHome=new FragmentHome();
//        fragmentMe=new FragmentMe();

        //初始化
        SysApplication.getInstance().addActivity(this);
        Bundle extras = getIntent().getExtras();
        if(extras.getString("id")!=null){
            id = extras.getString("id");
        }
        else if(extras.getString("username")!=null)
        {
            username=extras.getString("username");
        }
        else
        username="未登录";


       // Toast.makeText(this,username,Toast.LENGTH_SHORT).show();

        System.out.print("bundle"+id);




        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.main_content,new FragmentMe());
        transaction.commit();




        text_home= (TextView) findViewById(R.id.text_home);
        text_love= (TextView) findViewById(R.id.text_love);
        text_me= (TextView) findViewById(R.id.text_me);

        image_home= (ImageView) findViewById(R.id.main_home);
        image_love= (ImageView) findViewById(R.id.main_love);

        image_me= (ImageView) findViewById(R.id.main_me);


        final View view_home=findViewById(R.id.linearLayout_home);
        final   View  view_love=findViewById(R.id.linearLayout_love);
        final   View  view_me=findViewById(R.id.linearLayout_me);

        view_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentHome());

                view_home.setBackgroundColor(Color.parseColor("#FF00B8EE"));
                view_love.setBackgroundColor(Color.parseColor("#7700B8EE"));
                view_me.setBackgroundColor(Color.parseColor("#7700B8EE"));

            }
        });

        view_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentCollection());
                view_home.setBackgroundColor(Color.parseColor("#7700B8EE"));
                view_love.setBackgroundColor(Color.parseColor("#FF00B8EE"));
                view_me.setBackgroundColor(Color.parseColor("#7700B8EE"));

            }
        });


        view_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentMe());

                view_home.setBackgroundColor(Color.parseColor("#7700B8EE"));
                view_love.setBackgroundColor(Color.parseColor("#7700B8EE"));
                view_me.setBackgroundColor(Color.parseColor("#FF00B8EE"));
            }
        });


        //切换不同的Faragment
        changeFragment(new FragmentHome());
        view_home.setBackgroundColor(Color.parseColor("#FF00B8EE"));

//        image_home.setImageResource(R.drawable.main_home_selected);
//        text_home.setTextColor(getResources().getColor(R.color.normal_green_color));



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
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            sql ="select * from collections";
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
    private void writeToSqlite(ResultSet rs) throws SQLException {


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

        while (rs.next())
        {
            ContentValues cv = new ContentValues();

            cv.put("username", rs.getString("username"));
            cv.put("picurl",rs.getString("picurl"));
            cv.put("contenturl",rs.getString("contenturl"));
            cv.put("time",rs.getString("time"));
            cv.put("title",rs.getString("title"));

            //插入ContentValues中的数据
            db.insert("collections", null, cv);
        }
        db.close();
    }

    public void changeFragment(Fragment fragment){

        Bundle bundle=new Bundle();
        bundle.putString("id", id);
        bundle.putString("username",username);
        System.out.println("mainactivity id"+id);
        fragment.setArguments(bundle);
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.main_content,fragment);
        transaction.commit();
    }




//    public void SelectedFragmentHome()
//    {
////        image_home.setImageResource(R.drawable.main_home_selected);
////        image_love.setImageResource(R.drawable.main_event);
////        image_question.setImageResource(R.drawable.main_event);
////        image_me.setImageResource(R.drawable.main_remove);
////
////        text_home.setTextColor(getResources().getColor(R.color.normal_green_color));
////        text_love.setTextColor(getResources().getColor(R.color.colorNormal));
////        text_question.setTextColor(getResources().getColor(R.color.colorNormal));
////        text_me.setTextColor(getResources().getColor(R.color.colorNormal));
//
//
//    }
//    public void SelectedFragmenthome(){
//
//    }
//    public void SelectedFragmentlove(){
//
//    }
//    public void SelectedFragmentMe()
//    {
//
//    }
//    public void SelectedFragmentQuestion()
//    {
//
//    }
//
//    public void SelectedFragmentEvent()
//    {
////        image_home.setImageResource(R.drawable.main_home_normal);
////        image_love.setImageResource(R.drawable.main_event_selected);
////        image_me.setImageResource(R.drawable.main_remove);
////
////        text_home.setTextColor(getResources().getColor(R.color.colorNormal));
////        text_love.setTextColor(getResources().getColor(R.color.normal_green_color));
////        text_me.setTextColor(getResources().getColor(R.color.colorNormal));
//    }
//


}
