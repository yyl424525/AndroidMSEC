package com.example.yyl.msec.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by YYL on 2016/11/4.
 */
public class HttpImageThread extends Thread {
    private ImageView imageView;
    private Handler handler;
    private String url;

    public HttpImageThread(ImageView imageView, Handler handler, String url){
        this.imageView=imageView;
        this.handler=handler;
        this.url=url;
    }

    @Override
    public void run() {
        super.run();

        URL httpurl= null;
        try {
            httpurl = new URL(url);

            HttpURLConnection httpURLConnection= (HttpURLConnection) httpurl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(5000);
            InputStream in=httpURLConnection.getInputStream();
            final Bitmap bitmap= BitmapFactory.decodeStream(in);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
