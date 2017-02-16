package com.example.yyl.msec.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.copy.CustomWebView;
import com.example.yyl.msec.database.MyDataBaseHelper;
import com.example.yyl.msec.dialog.ShareBottomDialog;
import com.example.yyl.msec.dialog.ShareTopDialog;
import com.example.yyl.msec.entity.CommentBean;
import com.example.yyl.msec.entity.News;
import com.example.yyl.msec.entity.Notice;
import com.example.yyl.msec.entity.ReplyBean;
import com.example.yyl.msec.fragment.FragmentCollection;
import com.example.yyl.msec.service.SaveCommentsService;
import com.example.yyl.msec.utils.CommentAdapter;
import com.example.yyl.msec.utils.DownPicUtil;
import com.example.yyl.msec.utils.NoScrollListView;
import com.example.yyl.msec.utils.SysApplication;
import com.example.yyl.msec.view.GoodView;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by YYL on 2016/11/5.
 */
public class ContentActivity extends Activity {

    private CustomWebView webView;
    private ImageView imageView;
    private ImageView img_dianzan;
    private ImageView back;
    private boolean image_unpressed = true;
    private MyDataBaseHelper dataBaseHelper;
    private String contenturl, picurl, time, title;
    private News news;
    private Notice notice;
    private Intent intent;

    public static String username ="";
    private boolean isCollected=false;
    private boolean isDianzan=false;
    private Button btn_previous,btn_next;


    private Button commentButton;		//评论按钮
    private EditText commentEdit;		//评论输入框
    private TextView senderNickname;	//发表者昵称
    private TextView sendTime;			//发表的时间
    private TextView sendContent;		//发表的内容
    private TextView shareText;			//底部分享
    private TextView commentText;		//底部评论
    private TextView praiseText;		//底部点赞
    private TextView collectionText;	//底部收藏
    private ImageView senderImg;		//发送者图片
    private ImageView shareImg;			//分享的图片
    private ImageView commentImg;		//评论的图片
    private ImageView praiseImg;		//点赞的图片
    private NoScrollListView commentListview;//评论数据列表
  //  private LinearLayout bottomLinear;	//底部分享、评论等线性布局
    private LinearLayout commentLinear;	//评论输入框线性布局
    private LinearLayout linearLayout_listview;
    private int count;					//记录评论ID
    private int position;				//记录回复评论的索引
    private int[] imgs;					//图片资源ID数组
    private boolean isReply;			//是否是回复
    private String comment = "";		//记录对话框中的内容
    private CommentAdapter adapter;
    private List<CommentBean> list=new ArrayList<>();
    private static final String APP_ID = "wxeaeb619df7cac74a";
    private IWXAPI  api;
    private CheckBox checkBox;
    private ProgressBar progressBar;

    private int clickCount=0;
    private static final String APP_ID_QQ="1105755529";
    private Tencent mTencent;
    private TextView content_title;
    private int ZanCount=0;

    private  GoodView mGoodView;
    private  GoodView mGoodView2;


    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.bas_in = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.bas_out = bas_out;
    }
    public void showShareTopDialog(){
         ShareTopDialog dialog2 = new ShareTopDialog(this);
        dialog2.showAnim(bas_in)
                .show();


        dialog2.setPicurl(picurl);
        dialog2.setUsername(username);
        dialog2.setTime(time);
        dialog2.setContenturl(contenturl);
        dialog2.setTitle(title);
    }
    public void showShareBottomDialog(){
        final ShareBottomDialog dialog = new ShareBottomDialog(this);
        dialog.showAnim(bas_in)//
                .show();//
    }
    private void NormalDialogOneBtn() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("这不是你的评论，不可以删除哦≧▽≦")//
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_content);

//        Intent intent_comment=new Intent(ContentActivity.this, SaveCommentsService.class);
//        startService(intent_comment);
        initView();

    }


    private void initView() {
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        mGoodView=new GoodView(this);
        mGoodView2=new GoodView(this);

        mTencent = Tencent.createInstance(APP_ID_QQ, this.getApplicationContext());
        api= WXAPIFactory.createWXAPI(this,APP_ID);
        api.registerApp(APP_ID);
        intent = getIntent();
        contenturl = intent.getStringExtra("contenturl");
        time = intent.getStringExtra("time");
        title = intent.getStringExtra("title");
        picurl = intent.getStringExtra("picurl");
        System.out.println("contentactivity usernameusername:"+username);
     //   username = intent.getStringExtra("username");

        if(username.equals("未登录")){
            isCollected=false;
            isDianzan=false;
        }


        else
            isCollected=showSqlite(username,title);
        isDianzan=showSqliteDianzan(username,title);
        System.out.println("contentactivity:b11"+isCollected);



//判断是否接收到username
        System.out.println("contentactivity:" + ContentActivity.getUesr());

        commentButton = (Button) findViewById(R.id.commentButton);
        commentEdit = (EditText) findViewById(R.id.commentEdit);
        shareImg= (ImageView) findViewById(R.id.newscon_img_share);
        commentImg= (ImageView) findViewById(R.id.newscon_img_comment);
        commentListview = (NoScrollListView) findViewById(R.id.commentList);
        commentLinear = (LinearLayout) findViewById(R.id.commentLinear);
        btn_next= (Button) findViewById(R.id.btn_next);
        btn_previous= (Button) findViewById(R.id.btn_previous);
        linearLayout_listview= (LinearLayout) findViewById(R.id.linearlayout_listview);
        ClickListener cl = new ClickListener();
        commentButton.setOnClickListener(cl);
        shareImg.setOnClickListener(cl);
        commentImg.setOnClickListener(cl);
        btn_next.setOnClickListener(cl);
        btn_previous.setOnClickListener(cl);


        //标题
        content_title= (TextView ) findViewById(R.id.content_title);
        content_title.setText(title);

        if(username.equals("未登录"))
        {
            list.clear();
        }else
        {
            initList(contenturl,list);//从服务器初始化list
        }

        adapter = new CommentAdapter(this, list,R.layout.comment_item,handler);
        commentListview.setAdapter(adapter);

        TextView emptyView = new TextView(getApplicationContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      if(username.equals("未登录"))
      {

          emptyView.setText("未登录，无法查看评论");
      }
        else
        emptyView.setText("还没有评论哦");

        emptyView.setTextColor(Color.BLACK);
        emptyView.setTextSize(20);
        emptyView.setPadding(0,50,0,400);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)commentListview.getParent()).addView(emptyView);
        commentListview.setEmptyView(emptyView);

        commentListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int position_test=position;

                if(list.get(position).getCommentNickname().equals(username))
                {
                    DialogShowAndDelete(position_test,list,adapter);


//                    LayoutInflater inflater_delete=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    final View view_delete=inflater_delete.inflate(R.layout.delete_reply_item,null);
//                    AlertDialog.Builder builder=new AlertDialog.Builder(ContentActivity.this);
//                    builder.setView(view_delete);
//                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            list.remove(position_test);
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                        }
//                    });
//                    builder.create().show();

                }
                else
                    NormalDialogOneBtn();

               // Toast.makeText(getApplicationContext(),"不是你的评论，不能删除",Toast.LENGTH_SHORT).show();



                return true;
            }
        });

        SysApplication.getInstance().addActivity(this);
        imageView = (ImageView) findViewById(R.id.newscon_img_love);
        imageView.setOnClickListener(cl);

        img_dianzan= (ImageView) findViewById(R.id.newscon_img_admire);
        img_dianzan.setOnClickListener(cl);
        isCollected = intent.getBooleanExtra("isCollected", false);
        System.out.println("isCollected:" + isCollected);


        System.out.println("threadb:" + isCollected);
        if (isCollected) {

            imageView.setImageResource(R.drawable.main_love_pressed);
        } else {
            imageView.setImageResource(R.drawable.main_love_normal);
        }
        if (isDianzan) {

            img_dianzan.setImageResource(R.drawable.zan_pressed);
        } else {
            img_dianzan.setImageResource(R.drawable.zan_normal);
        }



        back = (ImageView) findViewById(R.id.newscon_image_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (CustomWebView) findViewById(R.id.webView);

        //   Toast.makeText(getApplicationContext(),contenturl,Toast.LENGTH_SHORT).show();
        System.out.println("contenturl" + contenturl);

        webView.loadUrl(contenturl);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                commentListview.setVisibility(View.GONE);
                linearLayout_listview.setVisibility(View.GONE);

            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                commentListview.setVisibility(View.VISIBLE);
                linearLayout_listview.setVisibility(View.VISIBLE);

            }
        }
        );
        //支持自动加载图片

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
      //  settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        settings.setLoadWithOverviewMode(true);
        // 使页面支持缩放
//        settings.setBuiltInZoomControls(true);
//        settings.setSupportZoom(true);
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
       // settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);// 排版适应屏幕
        // 缩放按钮
      //  settings.setDisplayZoomControls(false);
        // 网页加载进度显示



    }
    //webview长按事件
    public void webViewLongClick( CustomWebView webview)
    {
        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                // 如果是图片类型或者是带有图片链接的类型
                if(hitTestResult.getType()== WebView.HitTestResult.IMAGE_TYPE||
                        hitTestResult.getType()== WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
                    // 弹出保存图片的对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("保存图片到本地");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String url = hitTestResult.getExtra();
                            // 下载图片到本地
                            DownPicUtil.downPic(url, new DownPicUtil.DownFinishListener(){

                                @Override
                                public void getDownPath(String s) {
                                    Toast.makeText(ContentActivity.this,"下载完成",Toast.LENGTH_LONG).show();
                                    Message msg = Message.obtain();
                                    msg.obj=s;
                                    handler.sendMessage(msg);
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        // 自动dismiss
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });
    }
    //webview copy 所需
    @Override
    public void onContextMenuClosed(Menu menu) {

        Log.e("menu", menu.getItem(0).getItemId()+"");


        super.onContextMenuClosed(menu);
    }


    public void DialogShowAndDelete(int position, final List<CommentBean> list, final CommentAdapter adapter)
    {
        final int pos=position;
        final List<CommentBean> commentlist=list;
        final CommentAdapter comadapter=adapter;

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this comment!")
                .setCancelText("No,cancel plx!")
                .setConfirmText("Yes,delete it!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog.setTitleText("Cancelled!")
                                .setContentText("Your comment is safe :)")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                        // or you can new a SweetAlertDialog to show
                               /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.setTitleText("Deleted!")
                                .setContentText("Your comment has been deleted!")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);




                        CommentBean commentBean=new CommentBean();
                        commentBean=list.get(pos);
                        final String time=commentBean.getCommentTime();
                        final String content=commentBean.getCommentContent();
                        deleteCommentFromSqlite(username,content,contenturl,time);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                deleteComment(username,contenturl,time,content);
                            }
                        }).start();

                        commentlist.remove(pos);
                        comadapter.notifyDataSetChanged();
                    }
                })
                .show();
    }
    // 监听返回键返回网页的上一层
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            if(webView != null){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 10){
                isReply = true;
                position = (Integer)msg.obj;
                commentLinear.setVisibility(View.VISIBLE);
                onFocusChange(true);
            }
            String picFile = (String) msg.obj;
          //  String picFile = Integer.toString((Integer) msg.obj);
            String[] split = picFile.split("/");
            String fileName = split[split.length-1];
            try {
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), picFile, fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picFile)));
            Toast.makeText(ContentActivity.this,"图片保存图库成功",Toast.LENGTH_LONG).show();

        }
    };

    /**
     * 获取评论列表数据
     */
    public List<CommentBean>  initList(String contenturl,List<CommentBean> list){

        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Cursor c= db.rawQuery("select * from comments where contenturl=?",new String[]{contenturl});

        System.out.println("csize"+c.getCount());
        c.moveToFirst();
        list.clear();
        for(int i=0;i<c.getCount();i++)
        {
            c.moveToPosition(i);
           CommentBean commentBean=new CommentBean();
            commentBean.setContentUrl(c.getString(c.getColumnIndex("contenturl")));
            commentBean.setCommentTime(c.getString(c.getColumnIndex("time")));
            commentBean.setCommentNickname(c.getString(c.getColumnIndex("username")));
            commentBean.setCommentContent(c.getString(c.getColumnIndex("content")));
            list.add(commentBean);
        }
        
        db.close();
        return list;
    }
    private boolean showSqlite(String username,String title){
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //打开或创建test.db数据库
        // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        Cursor c= db.rawQuery("select * from collections where username=? and title=?",new String[]{username,title});
        System.out.println("csize"+c.getCount());

        if(c.getCount()==1){
            System.out.println("true:contenturl");
            db.close();
            return true;
        }
        else{
            System.out.println("false:contenturl");
            System.out.println("contenturl"+c.getCount());
            return false;
        }
    }
    private boolean showSqliteDianzan(String username,String title){
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);

//获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //打开或创建test.db数据库
        // SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);

        Cursor c= db.rawQuery("select * from dianzan where username=? and title=?",new String[]{username,title});
        System.out.println("csize"+c.getCount());

        if(c.getCount()==1){
            System.out.println("true:contenturl");
            db.close();
            return true;
        }
        else{
            System.out.println("false:contenturl");
            System.out.println("contenturl"+c.getCount());
            return false;
        }
    }
    /**
     * 获取回复列表数据
     */
    private List<ReplyBean> getReplyData(){
        List<ReplyBean> replyList = new ArrayList<ReplyBean>();
        return replyList;
    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus){
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        commentEdit.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if(isFocus)  {
                    //显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }else{
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(commentEdit.getWindowToken(),0);
                }
            }
        }, 100);
    }

    /**
     * 判断对话框中是否输入内容
     */
    private boolean isEditEmply(){
        comment = commentEdit.getText().toString().trim();
        if(comment.equals("")){
            Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        commentEdit.setText("");
        return true;
    }

    /**
     * 发表评论
     */
    private void publishComment(){

        final CommentBean bean = new CommentBean();
        bean.setId(count);

        bean.setCommentNickname(username);
        bean.setCommentTime(getCurrentTime());
      //  bean.setCommnetAccount("12345"+count);
        bean.setCommentContent(comment);
        list.add(bean);
        adapter.notifyDataSetChanged();
        count++;
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveComment(contenturl,bean.getCommentContent(),bean.getCommentTime(),username);
                Intent intent_comment=new Intent(ContentActivity.this, SaveCommentsService.class);
                startService(intent_comment);
        }
        }).start();


    }
    public void saveComment(String contenturl,String content,String time,String username){
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

            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "select * from comments where contenturl='" + contenturl + "'" + " and username='" + username + "'"+" and content='"+content+"'";
            System.out.println(sql);
            ResultSet rs1 = stmt.executeQuery(sql);
            //   sql="select * from collections where username='"+username+"'";

            if (rs1.next()) {
                System.out.println(rs1.next());
            } else {
                sql = "insert into comments(username,contenturl,content,time) values('" + username + "','" + contenturl + "','" + content + "','" + time +  "')";
                System.out.println("modify  comments sql" + sql);
                int result = stmt.executeUpdate(sql);
                System.out.println("result" + result);
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

    public String getCurrentTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        return time;
    }
    /**
     * 回复评论
     */
    private void replyComment(){
        ReplyBean bean = new ReplyBean();
        bean.setId(count+10);
        bean.setCommentNickname(list.get(position).getCommentNickname());
        bean.setReplyNickname(username);
        bean.setReplyContent(comment);
        adapter.getReplyComment(bean, position);
        adapter.notifyDataSetChanged();
    }



    /**
     * 事件点击监听器
     */
  private  final class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.newscon_img_admire:
                    if(username.equals("未登录"))
                    {
                        Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if (isDianzan) {
                            deleteDianZanFromSqlite(title);
                          //  Toast.makeText(getApplicationContext(), "已取消点赞", Toast.LENGTH_SHORT).show();
                            img_dianzan.setImageResource(R.drawable.zan_normal);

                            mGoodView2.setText("-1");
                            mGoodView2.show(v);

                        //    Toast.makeText(getApplicationContext(), "已取消点赞", Toast.LENGTH_SHORT).show();
                            new Thread(

                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            deleteDianZan(username, contenturl);
//
                                        }
                                    }).start();



                            isDianzan=false;

                        } else {


                            ZanCount=addDianZanToSqlite(username, contenturl, picurl, title, time);
                            Toast.makeText(ContentActivity.this,"已有"+ZanCount+"人点赞",Toast.LENGTH_SHORT).show();

                            img_dianzan.setImageResource(R.drawable.zan_pressed);
                            mGoodView2.setText("+1");
                            mGoodView2.show(v);



                            new Thread(new Runnable() {
                                @Override

                                public void run() {
                                    modifyDianZan(username, contenturl, picurl, title, time);
//
                                }
                            }).start();

                           // Toast.makeText(getApplicationContext(), "已点赞", Toast.LENGTH_SHORT).show();

                            System.out.println("content:title" + title);
                            System.out.println("content:picurl" + picurl);
                            System.out.println("content:time" + time);
                            System.out.println("content:contenturl" + contenturl);
                            System.out.println("content:username" + username);

                            isDianzan=true;
                        }
                    }

                    break;
                case R.id.newscon_img_love:
                    if(username.equals("未登录"))
                    {
                        Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if (isCollected) {
                            FragmentCollection.remove(contenturl);
                            deleteCollectionFromSqlite(title);
                          //  Toast.makeText(getApplicationContext(), "已取消收藏", Toast.LENGTH_SHORT).show();

                            new Thread(

                                    new Runnable() {
                                @Override
                                public void run() {
                                    deleteCollection(username, contenturl);
//
                                }
                            }).start();
                            imageView.setImageResource(R.drawable.main_love_normal);
                            mGoodView.setTextInfo("已取消收藏", Color.parseColor("#f66467"), 20);
                            mGoodView.show(v);

                            isCollected=false;

                        } else {



                            new Thread(new Runnable() {
                                @Override

                                public void run() {
                                    modifyCollection(username, contenturl, picurl, title, time);
//
                                }
                            }).start();
                            FragmentCollection.save(title, picurl, time, contenturl);
                             addCollectionToSqlite(username, contenturl, picurl, title, time);
                           // Toast.makeText(getApplicationContext(), "已收藏", Toast.LENGTH_SHORT).show();

                            imageView.setImageResource(R.drawable.main_love_pressed);
                            mGoodView.setTextInfo("收藏成功", Color.parseColor("#f66467"), 30);
                            mGoodView.show(v);


                            System.out.println("content:title" + title);
                            System.out.println("content:picurl" + picurl);
                            System.out.println("content:time" + time);
                            System.out.println("content:contenturl" + contenturl);
                            System.out.println("content:username" + username);

                            isCollected=true;
                        }
                    }



                    break;
                case R.id.newscon_img_comment:
                    if(username.equals("未登录"))
                    {
                        Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        isReply = false;
                        commentLinear.setVisibility(View.VISIBLE);

                        onFocusChange(true);
                    }

                    break;

                case R.id.newscon_img_share:
                    if(username.equals("未登录"))
                    {
                        Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();

                    }
                    else{
                        //  share_url();
                        //   onClickShare();

                        showShareTopDialog();

//                        Intent intent=new Intent(ContentActivity.this, ShareDialogActivity.class);
//
//                        intent.putExtra("picurl",picurl);
//                        intent.putExtra("contenturl",contenturl);
//                        intent.putExtra("title",title);
//                        intent.putExtra("time",time);
//
//                        startActivity(intent);
                    }

                    break;

                case R.id.commentButton:	//发表评论按钮
                        if(isEditEmply()){		//判断用户是否输入内容
                            if(isReply){
                                replyComment();
                            }else{
                                publishComment();

                            }

                            commentLinear.setVisibility(View.GONE);
                            onFocusChange(false);
                        }
                    break;
//                case R.id.btn_next:
//                    Pattern p=Pattern.compile("manufucture/(\\d+)");
//                    Matcher m=p.matcher(contenturl);
//                    String s = null;
//                    if(m.find()){
//                        s=m.group(1);
//                        System.out.println("s+"+s);
//                    }
//                    int count=Integer.parseInt(s);
//                    count++;
//                    System.out.println("count:"+count);
//                    contenturl=contenturl.replace("manufucture/"+s,"manufucture/"+count);
//                    System.out.println("count:contenturl"+contenturl);
//                    webView.loadUrl(contenturl);
//
//
//
//
//                    break;
//                case R.id.btn_previous:
//
//                    break;
            }
        }
    }

    private void addCollectionToSqlite(String username, String contenturl, String picurl, String title, String time) {
      dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);
////获取数据库对象
      SQLiteDatabase db = dataBaseHelper.getWritableDatabase();


        ContentValues cv = new ContentValues();

        cv.put("username", username);
        cv.put("picurl",picurl);
        cv.put("contenturl",contenturl);
        cv.put("time",time);
        cv.put("title",title);

        //插入ContentValues中的数据
        Cursor c = db.rawQuery("select * from collections where username=?",new String[]{username});
       // Toast.makeText(ContentActivity.this,"增加前:"+c.getCount(),Toast.LENGTH_SHORT).show();

        db.insert("collections", null, cv);

        Cursor c2= db.rawQuery("select * from collections where username=?",new String[]{username});
       // Toast.makeText(ContentActivity.this,"增加后:"+c2.getCount(),Toast.LENGTH_SHORT).show();

        db.close();
    }
    private int  addDianZanToSqlite(String username, String contenturl, String picurl, String title, String time) {
        int count_zan=0;
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);
////获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();


        ContentValues cv = new ContentValues();

        cv.put("username", username);
        cv.put("picurl",picurl);
        cv.put("contenturl",contenturl);
        cv.put("time",time);
        cv.put("title",title);

        //插入ContentValues中的数据
        Cursor c = db.rawQuery("select * from dianzan where username=?",new String[]{username});
     //   Toast.makeText(ContentActivity.this,"增加前:"+c.getCount(),Toast.LENGTH_SHORT).show();

        db.insert("dianzan", null, cv);

        Cursor c2= db.rawQuery("select * from dianzan where contenturl=?",new String[]{contenturl});
     //   Toast.makeText(ContentActivity.this,"增加后:"+c2.getCount(),Toast.LENGTH_SHORT).show();

        count_zan=c2.getCount();
        db.close();
        return count_zan;
    }
    private void deleteCollectionFromSqlite(String title) {
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);
////获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //插入ContentValues中的数据
     //   String sql = "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
        //System.out.println("")
        //获得记录数
       //Cursor cursor=db.rawQuery("select count(*) from collections", null);
        Cursor c = db.rawQuery("select * from collections where username=?",new String[]{username});
      //  String sql="delete from collections where username=' "+username+"'"+" and title='"+title+"'"+" and time='"+time+"'";
     String sql="delete from collections where username='"+username+"'"+" and title='"+title+"'";
      /////////////////  Toast.makeText(ContentActivity.this,sql,Toast.LENGTH_SHORT).show();
       // String sql="delete from collections where username=' "+username+"'";
//Toast.makeText(ContentActivity.this,"删除前:"+c.getCount(),Toast.LENGTH_SHORT).show();
        db.execSQL(sql);
        //db.delete("person", "personid<?", new String[]{"2"});
    //    Cursor     cursor2=db.rawQuery("select count(*) from collections", null);
        Cursor c2 = db.rawQuery("select * from collections where username=?",new String[]{username});

     //   Toast.makeText(ContentActivity.this,"删除后:"+c2.getCount(),Toast.LENGTH_SHORT).show();

        db.close();
    }
    private void deleteDianZanFromSqlite(String title) {
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);
////获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //插入ContentValues中的数据
        //   String sql = "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
        //System.out.println("")
        //获得记录数
        //Cursor cursor=db.rawQuery("select count(*) from collections", null);
        Cursor c = db.rawQuery("select * from dianzan where username=?",new String[]{username});
        //  String sql="delete from collections where username=' "+username+"'"+" and title='"+title+"'"+" and time='"+time+"'";
        String sql="delete from dianzan where username='"+username+"'"+" and title='"+title+"'";
       /////////////// Toast.makeText(ContentActivity.this,sql,Toast.LENGTH_SHORT).show();
        // String sql="delete from collections where username=' "+username+"'";
       ////// Toast.makeText(ContentActivity.this,"删除前:"+c.getCount(),Toast.LENGTH_SHORT).show();
        db.execSQL(sql);
        //db.delete("person", "personid<?", new String[]{"2"});
        //    Cursor     cursor2=db.rawQuery("select count(*) from collections", null);
        Cursor c2 = db.rawQuery("select * from dianzan where username=?",new String[]{username});

       //// Toast.makeText(ContentActivity.this,"删除后:"+c2.getCount(),Toast.LENGTH_SHORT).show();

        db.close();
    }
    private void deleteCommentFromSqlite(String username,String content,String contenturl,String time) {
        dataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"test.db",null,1);
////获取数据库对象
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        //插入ContentValues中的数据
        //   String sql = "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
        //System.out.println("")
        //获得记录数
        //Cursor cursor=db.rawQuery("select count(*) from collections", null);
        Cursor c = db.rawQuery("select * from comments where username=?",new String[]{username});
        //  String sql="delete from collections where username=' "+username+"'"+" and title='"+title+"'"+" and time='"+time+"'";
        String sql="delete from comments where username='"+username+"'"+" and time='"+time+"'"+" and content='"+content+"'"+" and time='"+time+"'";
        /////////////// Toast.makeText(ContentActivity.this,sql,Toast.LENGTH_SHORT).show();
        // String sql="delete from collections where username=' "+username+"'";
        ////// Toast.makeText(ContentActivity.this,"删除前:"+c.getCount(),Toast.LENGTH_SHORT).show();
        db.execSQL(sql);
        //db.delete("person", "personid<?", new String[]{"2"});
        //    Cursor     cursor2=db.rawQuery("select count(*) from collections", null);
        Cursor c2 = db.rawQuery("select * from comments where username=?",new String[]{username});

        //// Toast.makeText(ContentActivity.this,"删除后:"+c2.getCount(),Toast.LENGTH_SHORT).show();

        db.close();
    }


    //    private void shareToQzone () {
//        //分享类型
//        final Bundle params = new Bundle();
//        params.putString(QzoneShare.SHARE_TO_QQ_KEY_TYPE,SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
//        params.putString(QzoneShare.)
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
//        mTencent.shareToQzone(ContentActivity.this, params, new BaseUiListener());
//    }
    private void onClickShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "sichuan");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  contenturl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,picurl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "制造学院");
       // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        mTencent.shareToQQ(ContentActivity.this, params, new BaseUiListener());
    }
    private class BaseUiListener implements IUiListener {

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError e) {

        }
        @Override
        public void onCancel() {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }
    public void share_local_pic()
    {
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=inflater.inflate(R.layout.share_wechat,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(ContentActivity.this);
        builder.setView(view);
        builder.setTitle("分享到微信");
        //   builder.setMessage("message");
        builder.setIcon(R.drawable.msec_logo);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContentActivity.this,"已取消",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取分享的文本
                final CheckBox checkBox= (CheckBox) view.findViewById(R.id.checkbox);
                final LinearLayout img_qq= (LinearLayout) view.findViewById(R.id.img_qq);
                final LinearLayout img_wechat= (LinearLayout) view.findViewById(R.id.img_wechat);
                final LinearLayout img_friends= (LinearLayout) view.findViewById(R.id.img_friends);
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.msec_logo);

                WXImageObject imageObject=new WXImageObject(bitmap);

                WXMediaMessage msg=new WXMediaMessage();
                msg.mediaObject=imageObject;

//压缩图像
                Bitmap thumbitmap=Bitmap.createScaledBitmap(bitmap,120,150,true);
                //撤销图片所占用的资源
                bitmap.recycle();


                msg.thumbData=bmpToByteArray(thumbitmap,true);//设置缩略图

                SendMessageToWX.Req req=new SendMessageToWX.Req();
                req.message=msg;
                req.scene=checkBox.isChecked()?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;

                api.sendReq(req);

                System.out.println(String.valueOf(api.sendReq(req)));

            }

        });
        builder.create().show();
    }
    public void share_qq(){


    }

    public void share_text()
    {
        // Toast.makeText(getApplicationContext(),String.valueOf(api.openWXApp()),Toast.LENGTH_SHORT).show();
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=inflater.inflate(R.layout.modify_account_item,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(ContentActivity.this);
        builder.setView(view);
        builder.setTitle("分享到微信");
        //   builder.setMessage("message");
        builder.setIcon(R.drawable.msec_logo);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContentActivity.this,"已取消",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取分享的文本
               final EditText edit_name= (EditText) view.findViewById(R.id.edit_name);
                String text=edit_name.getText().toString();
                WXTextObject textObject=new WXTextObject();
                 textObject.text=text;


                WXMediaMessage msg=new WXMediaMessage();
                msg.mediaObject=textObject;
                 msg.description=text;


                SendMessageToWX.Req req=new SendMessageToWX.Req();
                req.message=msg;
                req.transaction=buildTransaction("text");
                //发送给朋友还是朋友圈
                 req.scene=SendMessageToWX.Req.WXSceneSession;

                //4.发送
                api.sendReq(req);
            }

        });
        builder.create().show();
    }
    public void share_pic()
    {
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=inflater.inflate(R.layout.share_wechat,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(ContentActivity.this);
        builder.setView(view);
        builder.setTitle("分享到微信");
        //   builder.setMessage("message");
        builder.setIcon(R.drawable.msec_logo);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContentActivity.this,"已取消",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取分享的文本
                final CheckBox checkBox= (CheckBox) view.findViewById(R.id.checkbox);
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.msec_logo);

                WXImageObject imageObject=new WXImageObject(bitmap);

                WXMediaMessage msg=new WXMediaMessage();
                msg.mediaObject=imageObject;

//压缩图像
                Bitmap thumbitmap=Bitmap.createScaledBitmap(bitmap,120,150,true);
                //撤销图片所占用的资源
                bitmap.recycle();


                msg.thumbData=bmpToByteArray(thumbitmap,true);//设置缩略图

                SendMessageToWX.Req req=new SendMessageToWX.Req();
                req.message=msg;
                req.scene=checkBox.isChecked()?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;

                api.sendReq(req);

                System.out.println(String.valueOf(api.sendReq(req)));


            }

        });
        builder.create().show();


    }
    public void share_url()
    {
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=inflater.inflate(R.layout.share_wechat,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(ContentActivity.this);
        builder.setView(view);
        builder.setTitle("分享到微信");
        //   builder.setMessage("message");
        builder.setIcon(R.drawable.msec_logo);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContentActivity.this,"已取消",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取分享的文本
                final CheckBox checkBox= (CheckBox) view.findViewById(R.id.checkbox);
                WXWebpageObject wxWebpageObject=new WXWebpageObject();
                wxWebpageObject.webpageUrl=contenturl;
                System.out.println("webpageurl"+wxWebpageObject.webpageUrl.toString());

                WXMediaMessage msg=new WXMediaMessage(wxWebpageObject);
                msg.title=title;
                msg.description="来自四川大学制造学院";
                System.out.println("webpagetitle"+msg.title.toString());


//                Bitmap thumb= BitmapFactory.decodeResource(getResources(),R.drawable.msec_logo28);
//                msg.thumbData=bmpToByteArray(thumb,true);

                SendMessageToWX.Req req=new SendMessageToWX.Req();
                req.transaction=buildTransaction("webpage");
                req.message=msg;
                req.scene=checkBox.isChecked()?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;


                System.out.println(String.valueOf(api.sendReq(req)));


            }

        });
        builder.create().show();

    }
    //将bitmap转换成byte格式的数组
    public byte[] bmpToByteArray(final Bitmap bitmap,final  boolean needRecycle)
    {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        if(needRecycle)
        {
            bitmap.recycle();
        }
        byte [] result=outputStream.toByteArray();
        try
        {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //生成唯一标识

    public String buildTransaction(final String type)
    {
        return (type==null)?String.valueOf(System.currentTimeMillis()):type+System.currentTimeMillis();
    }
public void launchWeiChat()
{
    WXWebpageObject wxWebpageObject=new WXWebpageObject();
    wxWebpageObject.webpageUrl=contenturl;

    WXMediaMessage msg=new WXMediaMessage(wxWebpageObject);
    msg.title=title;
    msg.description=title;
    msg.mediaObject=wxWebpageObject;


    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.comment);
    msg.thumbData=bmpToByteArray(bitmap,true);

    SendMessageToWX.Req req=new SendMessageToWX.Req();
    req.transaction=buildTransaction("webpage");
    req.message=msg;

    req.scene=SendMessageToWX.Req.WXSceneTimeline;

    finish();

}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //判断控件是否显示
        if(commentLinear.getVisibility() == View.VISIBLE){
            commentLinear.setVisibility(View.GONE);

        }
    }

    public static void setUesr(String user) {
        username = user;
    }

    public static String getUesr() {
        return username;
    }

    public void modifyCollection(String username, String contenturl, String picurl, String title, String time) {
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

            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
            System.out.println(sql);

            ResultSet rs1 = stmt.executeQuery(sql);
            //   sql="select * from collections where username='"+username+"'";

               System.out.println("connect username:"+username);
            if (rs1.next()) {
                System.out.println("test已存在"+rs1.next());

            } else {
                sql = "insert into collections(username,contenturl,picurl,time,title) values('" + username + "','" + contenturl + "','" + picurl + "','" + time + "','" + title + "')";
                System.out.println("modify sql" + sql);
                int result = stmt.executeUpdate(sql);
                System.out.println("result" + result);

            }
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//
////                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        }
    }
    public void modifyDianZan(String username, String contenturl, String picurl, String title, String time) {
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

            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "select * from dianzan where contenturl='" + contenturl + "'" + " and username='" + username + "'";
            System.out.println(sql);

            ResultSet rs1 = stmt.executeQuery(sql);
            //   sql="select * from collections where username='"+username+"'";

            System.out.println("connect username:"+username);
            if (rs1.next()) {
                System.out.println("test已存在"+rs1.next());

            } else {
                sql = "insert into dianzan(username,contenturl,picurl,time,title) values('" + username + "','" + contenturl + "','" + picurl + "','" + time + "','" + title + "')";
                System.out.println("modify sql" + sql);
                int result = stmt.executeUpdate(sql);
                System.out.println("result" + result);

            }
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//
////                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        }
    }
    public void deleteCollection(String username, String contenturl) {
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

            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "delete from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
            System.out.println(sql);

            int result = stmt.executeUpdate(sql);


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
    public void deleteDianZan(String username, String contenturl) {
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

            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "delete from dianzan where contenturl='" + contenturl + "'" + " and username='" + username + "'";
            System.out.println(sql);

            int result = stmt.executeUpdate(sql);


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
    public void deleteComment(String username, String contenturl,String time,String content) {
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

            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "delete from comments where contenturl='" + contenturl + "'" + " and username='" + username + "'"+" and time='"+time+"'"+" and content='"+content+"'";
            System.out.println(sql);

            int result = stmt.executeUpdate(sql);


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
    public boolean search(String username, String contenturl,boolean b) {
            boolean c;
            c=b;
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
//            String sqll= "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
//            System.out.println("searchsql"+sqll);
            System.out.println("content 成功加载MySQL驱动程序");


            Context context = getApplicationContext();

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, "test.db", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            String sql = "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";

        //    String sql = "select * from collections where contenturl='" + contenturl + "'" + " and username='" + username + "'";
            System.out.println("searchsql"+sql);

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("true content 成功加载MySQL驱动程序  ");
                return true;



            } else {
                System.out.println("false content 成功加载MySQL驱动程序  ");
                return false;


            }

        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
       }
//        finally {
//            try {
//
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//       }
return true;
    }


}
