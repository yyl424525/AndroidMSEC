package com.example.yyl.msec.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.activity.AboutActivity;
import com.example.yyl.msec.activity.AccountActivity;
import com.example.yyl.msec.activity.FeedbackActivity;
import com.example.yyl.msec.service.SaveUsersService;
import com.example.yyl.msec.utils.UploadPicUtils;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.yyl.msec.R.id.me_pingfen_linearlayout;

/**
 * Created by YYL on 2016/8/7.
 */
public class FragmentMe extends Fragment implements View.OnClickListener{
    private TextView text_username;
    private  View linearlayout_account;
    private  String id;
   private String username;
    private Handler handler=new Handler();
    private Switch switch_change;
    private static ImageView img_me;

    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.bas_in = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.bas_out = bas_out;
    }

    private void NormalDialogOneBtn() {
        final NormalDialog dialog = new NormalDialog(getContext());
        dialog.content("这可能是最新版本了，去应用宝看看吧≧▽≦")//
                .btnNum(1)
                .btnText("确定")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();


        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
              //  T.showShort(getContext(), "middle");
                dialog.dismiss();
            }
        });
    }
    private void NormalDialogStyleTwo() {
        final NormalDialog dialog = new NormalDialog(getContext());
        dialog.content("进入应用宝，登陆后搜索 '制造学院' 即可评分★~★")//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(23)//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        //   T.showShort(this, "left");
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        // T.showShort(this, "right");
                        Uri uri = Uri.parse("http://sj.qq.com/myapp/");
                        Intent   intent0 = new Intent(Intent.ACTION_VIEW,uri);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent0);
                        dialog.dismiss();
                    }
                });

    }

    private View me_about_linearlayout,me_pics_linearlayout,account_linearlayout,account_pingfen_linearlayout,sex_linearlayout,mail_linearlayout,me_feedback_linearlayout,me_update_linearlayout;
    private static TextView text_sex,text_mail,text_account;


    private final int    USERNMAE=1;//用于区分修改服务器MySQL的users，comments,collections表
    private final int    SEX_MAIL=0;

    public static  void setBitmapMe(Bitmap bitmap,Uri uri,Intent data) {

        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = UploadPicUtils.toRoundBitmap(photo, uri); // 这个时候的图片已经被处理成圆形的了
            //将头像设置成新的
            img_me.setImageBitmap(photo);

        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        initialize(view);
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();

        return view;
    }

    private void initialize(View view) {
       // switch_change= (Switch) view.findViewById(R.id.switch_change);


        text_username= (TextView) view.findViewById(R.id.text_username);
        linearlayout_account=view.findViewById(R.id.linearlayout_account);
        linearlayout_account.setOnClickListener(this);
        account_pingfen_linearlayout=view.findViewById(me_pingfen_linearlayout);
        account_pingfen_linearlayout.setOnClickListener(this);
        me_feedback_linearlayout=view.findViewById(R.id.me_feedback_linearlayout);
        me_update_linearlayout=view.findViewById(R.id.me_update_linearlayout);
        me_about_linearlayout=view.findViewById(R.id.me_about_linearlayout);
        me_pics_linearlayout=view.findViewById(R.id.me_pics_linearlayout);
        img_me= (ImageView) view.findViewById(R.id.img_me);
        //SetPicUtil.setBitmapMe(img_me);



        me_feedback_linearlayout.setOnClickListener(this);
        me_pics_linearlayout.setOnClickListener(this);
        me_update_linearlayout.setOnClickListener(this);
        me_about_linearlayout.setOnClickListener(this);



        Bundle bundle=getArguments();

        if(bundle!=null)
        {
            if( bundle.getString("id")!=null){
                id = bundle.getString("id");
                System.out.println("getStringid  new"+id);


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        username=getusername(id);
                        System.out.println("getusername_uuu"+username);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                text_username.setText(username);
                            }
                        });
                    }
                }).start();

            }
            else
            username="未登录";

        }
        else
            System.out.println("bundle is null");


        account_linearlayout=view.findViewById(R.id.account_linearlayout);
        sex_linearlayout=view.findViewById(R.id.sex_linearlayout);
        mail_linearlayout=view.findViewById(R.id.mail_linearlayout);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {


            case R.id.me_pics_linearlayout:


            Intent intent_pic = new Intent();
                /* 开启Pictures画面Type设定为image */
            intent_pic.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
            intent_pic.setAction(Intent.ACTION_GET_CONTENT);

            startActivity(intent_pic);


//            File file=new File("/sdcard/Pictures/");
//            Intent it = new Intent(Intent.ACTION_GET_CONTENT);
//            Uri mUri = Uri.parse("file://"+file.getPath());
//            it.setDataAndType(mUri, "image/*");
//            startActivity(it);
            break;
            case R.id.me_about_linearlayout:
                startActivity(new Intent(getContext(),AboutActivity.class));
                break;
            case R.id.me_update_linearlayout:
                //Toast.makeText(getContext(),"已是最新版本",Toast.LENGTH_SHORT).show();
                NormalDialogOneBtn();
                break;
            case R.id.me_feedback_linearlayout:
                Intent intent_feedbank=new Intent(getContext(), FeedbackActivity.class);
                startActivity(intent_feedbank);

                break;
            case R.id.me_pingfen_linearlayout:
              //  Uri uri = Uri.parse("market://details?id="+"com.example.yyl.msec");
//                Uri uri = Uri.parse("http://sj.qq.com/myapp/");
//             Intent   intent0 = new Intent(Intent.ACTION_VIEW,uri);
//                intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent0);

                NormalDialogStyleTwo();

//                GetUri gu = new GetUri();
//                Intent i = gu.getIntent(getContext());
//                boolean b = gu.judge(getContext(), i);
//                if(b==false)
//                {
//                    startActivity(i);
//                }
                break;
            case R.id.linearlayout_account:
                Intent intent=new Intent(getContext(), AccountActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                startActivityForResult(intent,1000);
                break;


            case R.id.account_linearlayout:
                if(username.equals("未登录"))
                {
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                } else
                {

                    LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    final View view=inflater.inflate(R.layout.modify_account_item,null);


                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setView(view);
                    builder.setTitle("修改用户名");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(),"已取消",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final EditText edit_name= (EditText) view.findViewById(R.id.edit_name);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("text_account:"+text_account.getText().toString());
                                    if(isUsernameExists(edit_name.getText().toString()))
                                    {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"此用户已存在",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        connectToMysql(USERNMAE,edit_name.getText().toString(),text_account.getText().toString(),"username");
                                        Intent intent_service=new Intent(getContext(), SaveUsersService.class);
                                        getActivity().startService(intent_service);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                text_account.setText(edit_name.getText().toString());
                                                Intent intent=new Intent();
                                                intent.putExtra("result",text_account.getText().toString());
                                                getActivity().setResult(1001,intent);

                                            }
                                        });
                                    }

                                }
                            }).start();
                        }
                    });
                    builder.create().show();
                }



                break;
            case R.id.sex_linearlayout:
                if(username.equals("未登录"))
                {
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
                else{
                    LayoutInflater inflater2=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    final View view2=inflater2.inflate(R.layout.modify_account_item,null);


                    AlertDialog.Builder builder2=new AlertDialog.Builder(getContext());
                    builder2.setView(view2);
                    builder2.setTitle("修改性别");
                    builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(),"已取消",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final EditText edit_sex= (EditText) view2.findViewById(R.id.edit_name);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("text_account:"+text_account.getText().toString());
                                    connectToMysql(SEX_MAIL,edit_sex.getText().toString(),text_sex.getText().toString(),"sex");
                                    Intent intent_service=new Intent(getContext(), SaveUsersService.class);
                                    getActivity().startService(intent_service);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            text_sex.setText(edit_sex.getText().toString());
                                        }
                                    });
                                }
                            }).start();

                        }
                    });
                    builder2.create().show();
                }


                break;
            case R.id.mail_linearlayout:
                if(username.equals("未登录"))
                {
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    LayoutInflater inflater3=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    final View view3=inflater3.inflate(R.layout.modify_account_item,null);


                    AlertDialog.Builder builder3=new AlertDialog.Builder(getContext());
                    builder3.setView(view3);
                    builder3.setTitle("修改邮箱");
                    builder3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(),"已取消",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final EditText edit_mail= (EditText) view3.findViewById(R.id.edit_name);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("text_account:"+text_account.getText().toString());
                                    connectToMysql(SEX_MAIL,edit_mail.getText().toString(),text_mail.getText().toString(),"mail");
                                    Intent intent_service=new Intent(getContext(), SaveUsersService.class);
                                    getActivity().startService(intent_service);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            text_mail.setText(edit_mail.getText().toString());
                                        }
                                    });
                                }
                            }).start();

                        }
                    });
                    builder3.create().show();

                }

                break;
        }
    }







    private void connectToMysql(int test,String oldvalue ,String newvalue,String label) {
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

            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();

            int result=  UpdateData(test,newvalue,oldvalue,label,stmt);
            if(result!=-1){
                System.out.print("update success"+result);

            }
            else
                System.out.print("update false"+result);




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
    private int UpdateData(int test,String oldvalue ,String newvalue,String label,Statement stmt)
    {int result=0;
        if(test==1)
        {
            String   sql="update users set "+label+"="+"'"+newvalue+"'"+" where "+ label+" = "+"'"+oldvalue+"'";
            String   sql2="update collections set "+label+"="+"'"+newvalue+"'"+" where "+ label+" = "+"'"+oldvalue+"'";
            String   sql3="update comments set "+label+"="+"'"+newvalue+"'"+" where "+ label+" = "+"'"+oldvalue+"'";
            try {

                result= stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
                result= stmt.executeUpdate(sql2);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
                result= stmt.executeUpdate(sql3);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
                if(result!=-1){
                    System.out.println("修改成功");

                }else
                {
                    System.out.println("修改失败");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(test==0)
        {
            String   sql="update users set "+label+"="+"'"+newvalue+"'"+" where "+ label+" = "+"'"+oldvalue+"'";
            try {

                result= stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
                if(result!=-1){
                    System.out.println("修改成功");

                }else
                {
                    System.out.println("修改失败");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }




        //  System.out.println("UpdateData()+"+sql);
        System.out.println("UpdateData()+"+"'name'");
        System.out.println("UpdateData()  newvalue+"+newvalue);
        System.out.println("UpdateData() oldvalue+"+oldvalue);


        return result;

    }
    private boolean isUsernameExists(String username){
        //打开或创建test.db数据库
        SQLiteDatabase db = getContext().openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        //打开或创建test.db数据库
        // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);




        Cursor c = db.rawQuery("select * from users where username=?",new String[]{username});
        if(c.getCount()==1){
            System.out.println("true:cccc");
            db.close();
            return true;
        }
        else{
            System.out.println("false:cccc");
            System.out.println("username"+c.getCount());
        }
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001)
        {
            String result_value = data.getStringExtra("result");
            System.out.println("onActivityResult: result_value "+result_value);
            text_username.setText(result_value);
            username=result_value;
            System.out.println("onActivityResult:"+text_username.getText().toString());

        }
        else
        {
            System.out.println("没有执行"+text_username.getText().toString());

        }
    }
}
