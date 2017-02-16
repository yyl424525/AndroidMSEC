package com.example.yyl.msec.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.fragment.FragmentCollection;
import com.example.yyl.msec.service.SaveNewsService;
import com.example.yyl.msec.service.SaveNoticeService;
import com.example.yyl.msec.service.SaveUsersService;
import com.example.yyl.msec.utils.SysApplication;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYL on 2016/10/27.
 */
public class LoginActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{
    // 声明控件对象
    private EditText et_name, et_pass;
    private Button mLoginButton,mLoginError,mRegister,mSkip;
    int selectIndex=1;
    int tempSelect=selectIndex;
    boolean isReLogin=false;
    private int SERVER_FLAG=0;
    private RelativeLayout countryselect;
    private TextView coutry_phone_sn, coutryName;
    private List<String >usernamelist;
    private List<String >passwordlist;
    private MyDataBaseHelper dbHelper;

    public static String loginuser="hello";
 int count=0;



    private Boolean isFirstIn=false;//判断是否是第一次进入
    private Boolean match;
    private Handler mHandler;
    public static final String TAG = "LoginActivity";
    private boolean mRunning = false;


    private ImageView img_login;
    private static final String APPID = "1105755529";


    private Tencent mTencent;
    private IUiListener loginListener;
    private IUiListener userInfoListener;
    private String scope;
    private UserInfo userInfo;
    private String username,mail,sex;


    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    @Override
    public void onBackPressed() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        Intent intent_service=new Intent(this, SaveUsersService.class);
        startService(intent_service);
        initView();



        doService();


//


    }
    @Override
    protected void onDestroy() {
        if (mTencent != null) {
            mTencent.logout(LoginActivity.this);
        }
        super.onDestroy();
    }

    private void doService() {
        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
//取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = pref.getBoolean("isFirstIn", true);
        if (isFirstIn) {
            //Toast.makeText(LoginActivity.this,"第一次登陆",Toast.LENGTH_SHORT).show();

            Intent intent_service_news = new Intent(this, SaveNewsService.class);
            startService(intent_service_news);


            Intent intent_service_notice=new Intent(this, SaveNoticeService.class);
            startService(intent_service_notice);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }
        //else
         //   Toast.makeText(LoginActivity.this,"不是第一次登陆",Toast.LENGTH_SHORT).show();

    }


    private void initView() {
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();

        img_login= (ImageView) findViewById(R.id.image_login_qq);
        img_login.setOnClickListener(this);
        mTencent = Tencent.createInstance(APPID, LoginActivity.this);
        scope = "all";
        initLoginListener();
        initUserInfoListener();


        SysApplication.getInstance().addActivity(this);
        et_name= (EditText) findViewById(R.id.username);
        et_pass= (EditText) findViewById(R.id.password);
        mLoginButton= (Button) findViewById(R.id.login);
        mRegister= (Button) findViewById(R.id.register);
        mSkip= (Button) findViewById(R.id.btn_skip);
        mLoginButton.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        mSkip.setOnClickListener(this);

        usernamelist=new ArrayList<>();
        passwordlist=new ArrayList<>();




    }
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.bas_in = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.bas_out = bas_out;
    }
    private void initUserInfoListener() {
        userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub
                System.out.print(arg0.toString());
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                System.out.print("cancel");
            }
            private String getUserId(String username) {
                String id = null;
                //打开或创建test.db数据库
                SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

                //打开或创建test.db数据库
                // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                Cursor c = db.rawQuery("select * from users where username=?",new String[]{username});
                System.out.println("username"+c.getCount());

                c.moveToFirst();
                for(int j=0;j<c.getCount();j++)
                { c.moveToPosition(j);
                    id=c.getString(c.getColumnIndex("id"));
                    System.out.println("id:"+id);
                }
                db.close();
                return id;
            }
            /**
             * {"is_yellow_year_vip":"0","ret":0,
             * "figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/40",
             * "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "nickname":"攀爬←蜗牛","yellow_vip_level":"0","is_lost":0,"msg":"",
             * "city":"黄冈","
             * figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/50",
             * "vip":"0","level":"0",
             * "figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "province":"湖北",
             * "is_yellow_vip":"0","gender":"男",
             * "figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/30"}
             */
            @Override
            public void onComplete(Object arg0) {
                // TODO Auto-generated method stub
                if(arg0 == null){
                    System.out.println("agr0=1234567null");
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) arg0;
                    int ret = jo.getInt("ret");
                    System.out.println("json======" + String.valueOf(jo));
//					String nickName=jo.getString("nickname");
//					String gender=jo.getString("gender");
//					System.out.println("nickname"+nickName);
//					Toast.makeText(MainActivity.this, "你好，" + nickName,
//							Toast.LENGTH_LONG).show();
                    if(ret == 100030){
                        //权限不够，需要增量授权
                        Runnable r = new Runnable() {
                            public void run() {
                                mTencent.reAuth(LoginActivity.this, "all", new IUiListener() {

                                    @Override
                                    public void onError(UiError arg0) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onComplete(Object arg0) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onCancel() {
                                        // TODO Auto-generated method stub

                                    }
                                });
                            }
                        };

                        LoginActivity.this.runOnUiThread(r);
                    }else{
                       username = jo.getString("nickname");
                      sex = jo.getString("gender");

                        if(isUsernameExists())
                        {

                        }
                        else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    connectToRegister();
                                }
                            }).start();

                        }


                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);


                        ContentActivity.setUesr(username);
                        FragmentCollection.setUsername(username);
                        Toast.makeText(LoginActivity.this,username+"，欢迎你", Toast.LENGTH_LONG).show();
                        Intent intent_service=new Intent(LoginActivity.this, SaveUsersService.class);
                        startService(intent_service);
                        intent.putExtra("id",getUserId(username) );
                        intent.putExtra("username",username);

                        startActivity(intent);
                        finish();


                     //   Toast.makeText(LoginActivity.this, "你好，" + username, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }


            }


        };
    }

    private void initLoginListener() {
        loginListener = new IUiListener() {
            /**
             * {"ret":0,"pay_token":"D3D678728DC580FBCDE15722B72E7365",
             * "pf":"desktop_m_qq-10000144-android-2002-",
             * "query_authority_cost":448,
             * "authority_cost":-136792089,
             * "openid":"015A22DED93BD15E0E6B0DDB3E59DE2D",
             * "expires_in":7776000,
             * "pfkey":"6068ea1c4a716d4141bca0ddb3df1bb9",
             * "msg":"",
             * "access_token":"A2455F491478233529D0106D2CE6EB45",
             * "login_cost":499}
             */
            @Override
            public void onComplete(Object value) {
                // TODO Auto-generated method stub
                if(value==null){
                    Toast.makeText(getApplicationContext(),"返回结果为空",Toast.LENGTH_LONG).show();
                    return;
                }
                System.out.println("有数据返回..");
                try {
                    JSONObject jo = (JSONObject) value;
                    if(null!=jo&&jo.length()==0){
                        Toast.makeText(getApplicationContext(),"返回结果为空",Toast.LENGTH_LONG).show();
                    }
                    //处理结果
                    System.out.println(jo.toString());

                    String msg = jo.getString("msg");
                    int ret = jo.getInt("ret");

                    System.out.println("json=" + String.valueOf(jo));


                    System.out.println("json=" + String.valueOf(jo));

                    System.out.println("msg="+msg);
                    if (ret == 0) {
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        //下面两个方法非常重要，否则会出现client request's parameters are invalid, invalid openid
                        mTencent.setOpenId(openID);
                        mTencent.setAccessToken(accessToken, expires);


                        System.out.println("开始获取用户信息");
                        if(mTencent.getQQToken() == null){
                            System.out.println("qqtoken ====================== null");
                        }

                        userInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                        userInfo.getUserInfo(userInfoListener);
                    }
//					if (ret == 0) {
//						Toast.makeText(MainActivity.this, "登录成功",
//								Toast.LENGTH_LONG).show();
//
//						String openID = jo.getString("openid");
//						String accessToken = jo.getString("access_token");
//						String expires = jo.getString("expires_in");
//						mTencent.setOpenId(openID);
//						mTencent.setAccessToken(accessToken, expires);
//					}

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

            private String getUserId(String username) {
                String id = null;
                //打开或创建test.db数据库
                SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

                //打开或创建test.db数据库
                // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
                Cursor c = db.rawQuery("select * from users where username=?",new String[]{username});
                System.out.println("username"+c.getCount());

                c.moveToFirst();
                for(int j=0;j<c.getCount();j++)
                { c.moveToPosition(j);
                    id=c.getString(c.getColumnIndex("id"));
                    System.out.println("id:"+id);
                }
                db.close();
                return id;
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplicationContext(),"登录错误",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),"登录取消",Toast.LENGTH_LONG).show();
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //由于在一些低端机器上，因为内存原因，无法返回到回调onComplete里面，是以onActivityResult的方式返回
        if(requestCode==11101&&resultCode==RESULT_OK){
            //处理返回的数据/
            if(data==null){
                Toast.makeText(getApplicationContext(),"返回数据为空",Toast.LENGTH_LONG);
            }else{
                Tencent.handleResultData(data,loginListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.image_login_qq:
                System.out.println("你点击了使用qq登录按钮");
                login();

                break;
            case R.id.login:  //登陆
                if(et_name.getText().toString().equals(""))
                    Toast.makeText(LoginActivity.this,"请输入账户",Toast.LENGTH_SHORT).show();
                else if(et_pass.getText().toString().equals(""))
                    Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                else
                createUserThread();

                break;
            case R.id.login_error: //无法登陆(忘记密码了吧)
//   Intent login_error_intent=new Intent();
//   login_error_intent.setClass(LoginActivity.this, ForgetCodeActivity.class);
//   startActivity(login_error_intent);
                break;
            case R.id.register:    //注册新的用户
               startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
                break;
            case R.id.btn_skip:

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username","未登录" );
                ContentActivity.setUesr("未登录");
                FragmentCollection.setUsername("未登录");
                startActivity(intent);
                finish();



        }
    }


    private void login() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(LoginActivity.this, scope, loginListener);

        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    private void createUserThread() {
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(mBackgroundRunnable);//将线程post到Handler中
    }



    //实现耗时操作的线程
    Runnable mBackgroundRunnable = new Runnable() {

        @Override
        public void run() {
//----------模拟耗时的操作，开始---------------
            login2();
            while(mRunning){
                Log.i(TAG, "thread running!");

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//----------模拟耗时的操作，结束---------------
        }

        private void login2() {
            showSqlite();
            if(search(usernamelist,et_name.getText().toString()))
            {
                if(passwordlist.get(count).toString().equals(et_pass.getText().toString()))
                {
                    ContentActivity.setUesr(et_name.getText().toString());
                    FragmentCollection.setUsername(et_name.getText().toString());
                    Toast.makeText(LoginActivity.this,et_name.getText().toString()+"，欢迎你", Toast.LENGTH_SHORT).show();
                    // Toast.makeText(LoginActivity.this, "此用户名已存在", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                      //  intent.putExtra("username",et_name.getText().toString() );
                    intent.putExtra("id",getUserId(et_name.getText().toString()) );
                    System.out.print("intent intent"+getUserId(et_name.getText().toString()));
                    getUserId(et_name.getText().toString());
                    startActivity(intent);
                    finish();
                }
                else{
                    // Toast.makeText(LoginActivity.this, passwordlist.get(count).toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                }

            } else {
                System.out.println("没有这个用户名");
                Toast.makeText(LoginActivity.this, "此用户未注册", Toast.LENGTH_SHORT).show();

            }

        }

      public String   getUserId(String username){
      String id = null;
          //打开或创建test.db数据库
          SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

          //打开或创建test.db数据库
          // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
          Cursor c = db.rawQuery("select * from users where username=?",new String[]{username});
      System.out.println("username"+c.getCount());

          c.moveToFirst();
          for(int j=0;j<c.getCount();j++)
          { c.moveToPosition(j);
           id=c.getString(c.getColumnIndex("id"));
             System.out.println("id:"+id);
          }
          db.close();
          return id;

        }

        private void showSqlite() {
            //打开或创建test.db数据库
            SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

            Cursor c = db.query("users",null,null,null,null,null,null);//查询并获得游标
          //  Toast.makeText(LoginActivity.this,"用户数："+c.getCount(),Toast.LENGTH_SHORT).show();
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++)
            { c.moveToPosition(i);

                String id=c.getString(c.getColumnIndex("id"));
                String username = c.getString(c.getColumnIndex("username"));
                String password = c.getString(c.getColumnIndex("password"));
                String mail = c.getString(c.getColumnIndex("mail"));
                String sex = c.getString(c.getColumnIndex("sex"));
                usernamelist.add(username);
                passwordlist.add(password);
            //    Toast.makeText(LoginActivity.this,"id:"+id+"   usernmae:"+username+"   password:"+password+"   mail:"+mail+"   sex:"+sex,Toast.LENGTH_SHORT).show();


            }
            db.close();

        }
        private void login() {
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
                //Toast.makeText(LoginActivity.this,"成功加载MySQL驱动程序",Toast.LENGTH_SHORT).show();

                // 一个Connection代表一个数据库连接
                conn = DriverManager.getConnection(url);
                // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
                Statement stmt = conn.createStatement();
                sql ="select username,password from users";
                ResultSet rs = stmt.executeQuery(sql);
if(rs!=null) {
    while (rs.next()) {
      //  Toast.makeText(LoginActivity.this, rs.getString("username"), Toast.LENGTH_SHORT).show();
        //Toast.makeText(LoginActivity.this, et_name.getText().toString(), Toast.LENGTH_SHORT).show();
       usernamelist.add(rs.getString("username"));
       passwordlist.add(rs.getString("password"));

    }
}else
    Toast.makeText(LoginActivity.this, "rs==null", Toast.LENGTH_SHORT).show();

                if(search(usernamelist,et_name.getText().toString()))
                {
                    if(passwordlist.get(count).toString().equals(et_pass.getText().toString()))
                    {
                        Toast.makeText(LoginActivity.this,et_name.getText().toString()+"欢迎你", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(LoginActivity.this, "此用户名已存在", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                    else{
                       // Toast.makeText(LoginActivity.this, passwordlist.get(count).toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(LoginActivity.this, "此用户名未注册", Toast.LENGTH_SHORT).show();

                }
            } catch (SQLException e) {
                System.out.println("MySQL操作错误");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }




        }
    };

    private void  connectToRegister() {
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
            //  Toast.makeText(RegisterActivity.this,"成功加载MySQL驱动程序",Toast.LENGTH_SHORT).show();

            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            sql ="insert into users(username,sex,mail) values('"+username+"','"+sex+"','"+"暂无"+"')";
            int  rs = stmt.executeUpdate(sql);
            if(rs!=-1)
            {
System.out.println("login insert success");
            }
            else
            {
                System.out.println("login insert fail");
            }
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }


    private boolean search(List<String> usernamelist, String username){
        for(int i=0;i<usernamelist.size();i++)
        {
            if (usernamelist.get(i).equals(username)) {
                count=i;
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameExists(){
        //打开或创建test.db数据库
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        //打开或创建test.db数据库
        // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);




        Cursor c = db.rawQuery("select * from users where username=?",new String[]{username});
        if(c.getCount()==1){
            db.close();
            return true;
        }
        else{
            System.out.println("false:cccc");
            System.out.println("username"+c.getCount());
        }
        return false;
    }
}
