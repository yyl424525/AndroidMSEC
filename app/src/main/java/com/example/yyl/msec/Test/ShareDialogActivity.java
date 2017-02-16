package com.example.yyl.msec.Test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.yyl.msec.R;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by YYL on 2016/11/25.
 */
public class ShareDialogActivity extends Activity implements View.OnClickListener{
    private LinearLayout qq,wechat,friends;
    private static final String APP_ID = "wxeaeb619df7cac74a";
    private IWXAPI api;
    private static final String APP_ID_QQ="1105755529";
    private Tencent mTencent;
    private String contenturl, picurl, time, title;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        initView();
    }

    private void initView() {
        intent = getIntent();
        contenturl = intent.getStringExtra("contenturl");
        time = intent.getStringExtra("time");
        title = intent.getStringExtra("title");
        picurl = intent.getStringExtra("picurl");
        System.out.println("dialogintent"+contenturl);
        mTencent = Tencent.createInstance(APP_ID_QQ, this.getApplicationContext());
        api= WXAPIFactory.createWXAPI(this,APP_ID);
        api.registerApp(APP_ID);





        qq= (LinearLayout) findViewById(R.id.img_qq);
        wechat= (LinearLayout) findViewById(R.id.img_wechat);
        friends= (LinearLayout) findViewById(R.id.img_friends);

        qq.setOnClickListener(this);
        wechat.setOnClickListener(this);
        friends.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_qq:
                 shareToQQ();
                break;
            case R.id.img_wechat:
                shareToWechat();
                break;
            case R.id.img_friends:
               shareToFreinds();
                break;
        }
    }

    public void shareToQQ(){
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "sichuan");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  contenturl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,picurl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "制造学院");
        // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        mTencent.shareToQQ(ShareDialogActivity.this, params, new BaseUiListener());
    }

    public void shareToFreinds(){
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
        req.scene=SendMessageToWX.Req.WXSceneTimeline;


        System.out.println(String.valueOf(api.sendReq(req)));

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

    public void shareToWechat()
    {

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
                req.scene=SendMessageToWX.Req.WXSceneSession;


                System.out.println(String.valueOf(api.sendReq(req)));


            }
    //将bitmap转换成byte格式的数组
    public byte[] bmpToByteArray(final Bitmap bitmap, final  boolean needRecycle)
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


}


