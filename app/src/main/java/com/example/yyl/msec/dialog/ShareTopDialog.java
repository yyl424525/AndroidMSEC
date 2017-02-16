package com.example.yyl.msec.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.yyl.msec.R;
import com.example.yyl.msec.extra.ViewFindUtils;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.widget.base.TopBaseDialog;
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


public class ShareTopDialog extends TopBaseDialog<ShareTopDialog> {
    private LinearLayout ll_wechat_friend_circle;
    private LinearLayout ll_wechat_friend;
    private LinearLayout ll_qq;
  //  private LinearLayout ll_sms;




    private LinearLayout qq,wechat,friends;
    private static final String APP_ID = "wxeaeb619df7cac74a";
    private IWXAPI api;
    private static final String APP_ID_QQ="1105755529";
    private Tencent mTencent;
    private Intent intent;
    private String username,contenturl,picurl,time,title;
    private Context context;


    public ShareTopDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public ShareTopDialog(Context context) {
        super(context);
        this.context=context;
    }

    public void setUsername(String Username)
    {
        username=Username;
    }
    public void setContenturl(String Contenturl){
        contenturl=Contenturl;
    }
    public void setPicurl(String Picurl){
        picurl=Picurl;
    }
    public void setTitle(String Title)
    {
        title=Title;
    }
    public void setTime(String Time)
    {
        time=Time;
    }

    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter());
        dismissAnim(null);
        View inflate = View.inflate(context, R.layout.dialog_share, null);
        ll_wechat_friend_circle = ViewFindUtils.find(inflate, R.id.ll_wechat_friend_circle);
        ll_wechat_friend = ViewFindUtils.find(inflate, R.id.ll_wechat_friend);
        ll_qq = ViewFindUtils.find(inflate, R.id.ll_qq);
       // ll_sms = ViewFindUtils.find(inflate, R.id.ll_sms);



//        intent = getIntent();
//        contenturl = intent.getStringExtra("contenturl");
//        time = intent.getStringExtra("time");
//        title = intent.getStringExtra("title");
//        picurl = intent.getStringExtra("picurl");

        System.out.println("dialogintent"+contenturl);
        mTencent = Tencent.createInstance(APP_ID_QQ, getContext());
        api= WXAPIFactory.createWXAPI(getContext(),APP_ID);
        api.registerApp(APP_ID);




        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        ll_wechat_friend_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  T.showShort(context, "朋友圈");
                shareToFreinds();
                dismiss();
            }
        });
        ll_wechat_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  T.showShort(context, "微信");
                shareToWechat();
               dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    T.showShort(context, "QQ");
                shareToQQ();
                dismiss();
            }
        });
//        ll_sms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                T.showShort(context, "短信");
//                dismiss();
//            }
//        });
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
        mTencent.shareToQQ((Activity) context, params, new BaseUiListener());
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
