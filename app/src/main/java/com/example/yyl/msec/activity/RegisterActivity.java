package com.example.yyl.msec.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.entity.User;
import com.example.yyl.msec.utils.SysApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by YYL on 2016/10/27.
 */
public class RegisterActivity  extends Activity implements View.OnClickListener{
    private EditText edit_username,edit_password,edit_mail,edit_password_again,edit_sex;
    private Button btn_rigister,btn_show;
    private ImageView iamge_rigister_back;
    private User user;
    private String mysql_username;

    private int count;

    private Boolean match;

    public static final String TAG = "MainActivity";
    private boolean mRunning = false;
    private Handler mHandler;

    private List<String >usernamelist;
    private List<String >passwordlist;
    private List<String> usermaillist;
    private List<String> usersexlist;
    private ResultSet resultSet=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initView();

    }

    private void initView() {
        SysApplication.getInstance().addActivity(this);
        edit_username= (EditText) findViewById(R.id.eidt_username);
        edit_password= (EditText) findViewById(R.id.eidt_userpassword);
        edit_password_again= (EditText) findViewById(R.id.eidt_userpassword_again);
        edit_mail= (EditText) findViewById(R.id.eidt_user_mail);
        edit_sex= (EditText) findViewById(R.id.eidt_usergender);


        btn_rigister= (Button) findViewById(R.id.btn_register_end);
        iamge_rigister_back= (ImageView) findViewById(R.id.image_register_back);
     //   btn_show= (Button) findViewById(R.id.btn_show);
        btn_rigister.setOnClickListener(this);
        iamge_rigister_back.setOnClickListener(this);
     //   btn_show.setOnClickListener(this);

        usernamelist=new ArrayList<>() ;
        passwordlist=new ArrayList<>();
        usermaillist=new ArrayList<>();
        usersexlist=new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_show:
//                showSqlite();
//                break;
            case R.id.image_register_back:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                break;
            case R.id.btn_register_end:

                if (edit_username.getText().toString().equals("") ) {
                    Toast.makeText(RegisterActivity.this, "用户名为空，请重新输入", Toast.LENGTH_SHORT).show();
                } else if(edit_password.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "密码为空，请重新输入", Toast.LENGTH_SHORT).show();
                }
                else if(!edit_password.getText().toString().equals(edit_password_again.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this, "密码不相同", Toast.LENGTH_SHORT).show();
                }
                else if(edit_sex.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "请输入性别", Toast.LENGTH_SHORT).show();
                }
                else if(edit_mail.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                }
                else if(!isMatchMail(edit_mail.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this, "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    user = new User();
                    user.setUsername(edit_username.getText().toString());
                    user.setPassword(edit_password.getText().toString());
                    user.setPasswordagain(edit_sex.getText().toString());
                    user.setMail(edit_mail.getText().toString());
                    user.setSex(edit_sex.getText().toString());

                    createUserThread();
                }
                //  Toast.makeText(RegisterActivity.this,edit_password.getText().toString()+"==="+edit_password_again.getText().toString() , Toast.LENGTH_SHORT).show();
               break;
        }
        }
    //判断邮箱格式是否正确
    public boolean isMatchMail(String mail)
    {
        String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(mail);
        boolean isMatched = matcher.matches();
        return isMatched;
    }

    private void showSqlite() {


        //打开或创建test.db数据库
      SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        Cursor c = db.query("users",null,null,null,null,null,null);//查询并获得游标
        Toast.makeText(RegisterActivity.this,"用户数："+c.getCount(),Toast.LENGTH_SHORT).show();
        c.moveToFirst();
     for(int i=0;i<c.getCount();i++)
     { c.moveToPosition(i);
         String id=c.getString(c.getColumnIndex("id"));
                String username = c.getString(c.getColumnIndex("username"));
                String password = c.getString(c.getColumnIndex("password"));
                String mail = c.getString(c.getColumnIndex("mail"));
                String sex = c.getString(c.getColumnIndex("sex"));

                Toast.makeText(RegisterActivity.this,"id:"+id+"   usernmae:"+username+"   password:"+password+"   mail:"+mail+"   sex:"+sex,Toast.LENGTH_SHORT).show();


        }
        db.close();

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
            connectToRegister();
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
            sql ="select username from users";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next())
            {
                usernamelist.add(rs.getString("username"));
            }
            if(search(usernamelist,edit_username.getText().toString()))
            {
                Toast.makeText(RegisterActivity.this,"此用户名已存在",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                sql = user.userInsertIntoMysql();
                stmt.executeUpdate(sql);
                sql ="select * from users";
                rs = stmt.executeQuery(sql);
                writeToSqlite(rs);

                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
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

    private boolean search(List<String> usernamelist, String edit_username){
        for(int i=0;i<usernamelist.size();i++)
        {
            if (usernamelist.get(i).equals( edit_username)) {
               count=i;
                return true;
            }
        }
        return false;
    }
    private ResultSet connectMysql()
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
            Toast.makeText(RegisterActivity.this,"成功加载MySQL驱动程序",Toast.LENGTH_SHORT).show();

            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            sql ="select username from users";
             rs = stmt.executeQuery(sql);

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
        return  rs;
    }



    private void writeToSqlite(ResultSet rs) throws SQLException {
        //打开或创建test.db数据库
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS users");
        //创建users表
        db.execSQL("CREATE TABLE users( id INTEGER PRIMARY KEY AUTOINCREMENT,username  VARCHAR,password VARCHAR,mail VARCHAR,sex VARCHAR)");

        //   SELECT count(*) FROM sqlite_master WHERE type='table' AND name='tableName';
        while(rs.next()){
            ContentValues cv = new ContentValues();
            cv.put("id",rs.getString("id"));
            cv.put("username",rs.getString("username"));
            cv.put("password",rs.getString("password"));
            cv.put("mail",rs.getString("mail"));
            cv.put("sex",rs.getString("sex"));
            //插入ContentValues中的数据
            db.insert("users", null, cv);
        }


        db.close();

    }

}
