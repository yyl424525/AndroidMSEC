package com.example.yyl.msec.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by YYL on 2016/12/11.
 */

public class SetPicUtil {
    public  static ImageView img_me;
    private static Bitmap bitmap;
    private static Uri   uri;
    private static Intent data;
    public static  void setPicInfo(Bitmap bitma, Uri ur, Intent dat)
    {
        bitmap=bitma;
       uri=ur;
        data=dat;
    }
    public  static void setBitmapMe(ImageView img) {

        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = UploadPicUtils.toRoundBitmap(photo, uri); // 这个时候的图片已经被处理成圆形的了
            //将头像设置成新的
            img.setImageBitmap(photo);

        }


    }
}
