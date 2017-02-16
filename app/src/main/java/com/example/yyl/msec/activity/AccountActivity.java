package com.example.yyl.msec.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.dialog.CustomBaseDialog;
import com.example.yyl.msec.fragment.FragmentMe;
import com.example.yyl.msec.service.SaveUsersService;
import com.example.yyl.msec.utils.SetPicUtil;
import com.example.yyl.msec.utils.SysApplication;
import com.example.yyl.msec.utils.UploadPicUtils;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by YYL on 2016/11/5.
 */
public class AccountActivity extends Activity implements View.OnClickListener{
    private View account_linearlayout,account_change_linearlayout,sex_linearlayout,mail_linearlayout,img_me_linearlayout;
    private static  ImageView img_me;
    private static TextView text_sex,text_mail,text_account,text_username;
    private static String textaccoun =null;
    private String username;
    private  String sex;
    private String mail;
    private String id;
    private MyDataBaseHelper dataBaseHelper;
    private  LayoutInflater inflater;
    private Handler handler=new Handler();
    private ImageView back;
    private Button btn_exit;
    private final int    USERNMAE=1;//用于区分修改服务器MySQL的users，comments,collections表
    private final int    SEX_MAIL=0;


    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.bas_in = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.bas_out = bas_out;
    }
    public void ShowExitDialog2()
    {
        final CustomBaseDialog dialog = new CustomBaseDialog(this);
        dialog.show();
    }
    private void NormalDialogStyleTwo() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("为保证咖啡豆的新鲜度和咖啡的香味，并配以特有的传统烘焙和手工冲。")//
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
                        dialog.dismiss();
                    }
                });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountmangement);
        initView();
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

    private void initView() {
        SysApplication.getInstance().addActivity(this);
        account_change_linearlayout=findViewById(R.id.account_change_linearlayout);
        account_linearlayout=findViewById(R.id.account_linearlayout);
        sex_linearlayout=findViewById(R.id.sex_linearlayout);
        mail_linearlayout=findViewById(R.id.mail_linearlayout);
        img_me_linearlayout=findViewById(R.id.img_me_linearlayout);
        img_me= (ImageView) findViewById(R.id.img_me);
//        text_username= (TextView) findViewById(R.id.text_username);
//        text_username.setText(username);

        back= (ImageView) findViewById(R.id.image_register_back);
        back.setOnClickListener(this);
        btn_exit= (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);
        img_me_linearlayout.setOnClickListener(this);
        img_me.setOnClickListener(this);

        //SetPicUtil.setBitmapMe(img_me);



        text_sex= (TextView) findViewById(R.id.text_sex);
        text_mail= (TextView) findViewById(R.id.text_mail);
        text_account= (TextView) findViewById(R.id.text_account);

        account_change_linearlayout.setOnClickListener(this);
        account_linearlayout.setOnClickListener(this);
        sex_linearlayout.setOnClickListener(this);
        mail_linearlayout.setOnClickListener(this);


        //初始化
        Bundle extras = getIntent().getExtras();
       id= extras.getString("id");
        username=extras.getString("username");
        // Toast.makeText(this,username,Toast.LENGTH_SHORT).show();
        text_account.setText(username);
        if(username.equals("未登录"))
        {}
        else
        setUserData(username);

    }

    private Boolean setUserData(String username) {
        //打开或创建test.db数据库
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        //打开或创建test.db数据库
        // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);




        Cursor c = db.rawQuery("select * from users where username=?",new String[]{username});
        if(c.getCount()==1){
            System.out.println("true:cccc");
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++)
            { c.moveToPosition(i);

                mail=c.getString(c.getColumnIndex("mail"));
                sex=c.getString(c.getColumnIndex("sex"));
                System.out.println("mail"+mail);
                System.out.println("sex"+sex);


                text_sex.setText(c.getString(c.getColumnIndex("sex")).toString());
                if(c.getString(c.getColumnIndex("mail"))==null){
                    System.out.println("mail==nulll");
                    text_mail.setText("");
                }
                else
                {
                    text_mail.setText(c.getString(c.getColumnIndex("mail")).toString());

                }


            }
            db.close();
            return true;
        }
        else{
            System.out.println("false:cccc");
            System.out.println("username"+c.getCount());
        }
//      System.out.println("username"+c.getCount());
//      System.out.println("username"+c.getString(c.getColumnIndex("username")));


        return false;
    }
    private boolean isUsernameExists(String username){
        //打开或创建test.db数据库
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

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
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.img_me_linearlayout:
                showChoosePicDialog();
                break;
            case R.id.img_me:
                showChoosePicDialog();
                break;
            case R.id.btn_exit:
                ShowExitDialog2();
//                LayoutInflater inflater1=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View view1=inflater1.inflate(R.layout.exit_item,null);
//                AlertDialog.Builder builder0=new AlertDialog.Builder(AccountActivity.this);
//                builder0.setView(view1);
//
//                builder0.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(AccountActivity.this,"已取消",Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                builder0.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SysApplication.getInstance().exit();
//                    }
//                });
//                builder0.create().show();
                break;
            case R.id.image_register_back:

                finish();
                break;
            case R.id.account_change_linearlayout:
                Intent intent =new Intent(AccountActivity.this,LoginActivity.class);
                MainActivity.instance.finish();
                startActivity(intent);
                finish();
                break;
            case R.id.account_linearlayout:
                if(username.equals("未登录"))
                {
                    Toast.makeText(AccountActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                } else
                {

                    LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    final View view=inflater.inflate(R.layout.modify_account_item,null);


                    AlertDialog.Builder builder=new AlertDialog.Builder(AccountActivity.this);
                    builder.setView(view);
                    builder.setTitle("修改用户名");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(AccountActivity.this,"已取消",Toast.LENGTH_SHORT).show();

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
                                                Toast.makeText(AccountActivity.this,"此用户已存在",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(edit_name.getText().toString().equals(""))
                                    {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AccountActivity.this,"用户名为空，请重新输入",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        connectToMysql(USERNMAE,edit_name.getText().toString(),text_account.getText().toString(),"username");
                                        Intent intent_service=new Intent(AccountActivity.this, SaveUsersService.class);
                                        startService(intent_service);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                text_account.setText(edit_name.getText().toString());
                                              //  text_username.setText(edit_name.getText().toString());
                                                Intent intent=new Intent();
                                                intent.putExtra("result",text_account.getText().toString());
                                                setResult(1001,intent);

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
                    Toast.makeText(AccountActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                else{
                    LayoutInflater inflater2=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    final View view2=inflater2.inflate(R.layout.modify_account_item,null);


                    AlertDialog.Builder builder2=new AlertDialog.Builder(AccountActivity.this);
                    builder2.setView(view2);
                    builder2.setTitle("修改性别");
                    builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(AccountActivity.this,"已取消",Toast.LENGTH_SHORT).show();

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
                                    Intent intent_service=new Intent(AccountActivity.this, SaveUsersService.class);
                                    startService(intent_service);
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
                    Toast.makeText(AccountActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    LayoutInflater inflater3=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                    final View view3=inflater3.inflate(R.layout.modify_account_item,null);


                    AlertDialog.Builder builder3=new AlertDialog.Builder(AccountActivity.this);
                    builder3.setView(view3);
                    builder3.setTitle("修改邮箱");
                    builder3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(AccountActivity.this,"已取消",Toast.LENGTH_SHORT).show();

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
                                    Intent intent_service=new Intent(AccountActivity.this, SaveUsersService.class);
                                    startService(intent_service);

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

    public void ShowExitDialog()
    {

        SweetAlertDialog sd=  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        sd.setTitleText("Are you sure to exit?");
              //  .setContentText("Won't be able to recover this comment!")
        sd.setCancelText("No,cancel");
        sd .setConfirmText("Yes,exit");
        sd.showCancelButton(true);
        sd .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        // reuse previous dialog instance, keep widget user state, reset them if you need

                        // or you can new a SweetAlertDialog to show
                               /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                    }
                });
        sd .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SysApplication.getInstance().exit();
                    }
                });
        sd .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上


                    }
                    break;

            }
        }
    }


    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);

                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }


    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     *
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = UploadPicUtils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了

            //将头像设置成新的
            img_me.setImageBitmap(photo);
            SetPicUtil.setPicInfo(photo,tempUri,data);

            FragmentMe.setBitmapMe(photo,tempUri,data);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = UploadPicUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
        }
    }

}
