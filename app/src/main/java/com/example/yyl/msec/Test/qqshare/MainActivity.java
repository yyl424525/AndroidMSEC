package com.example.yyl.msec.Test.qqshare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.example.yyl.msec.R;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class MainActivity extends Activity {

	private Tencent mTencent;
	private static final String APP_ID = "1105755529";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qqshare);
		final Context ctxContext = this.getApplicationContext();
		mTencent = Tencent.createInstance(APP_ID, ctxContext);
		mHandler = new Handler();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickShareToQQ();
			}
		});
		
		Button btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}


	
	private void onClickShareToQQ() {
		Bundle b = getShareBundle();
		if(b != null){
			shareParams = b;
			Thread thread = new Thread(shareThread);
			thread.start();
		}
	}

	private Bundle getShareBundle(){
		 Bundle bundle = new Bundle();
         bundle.putString("title", "在线一起走");
         bundle.putString("imageUrl", "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
         bundle.putString("targetUrl", "http://www.com179.com/path/cms/downloads/client/");
         bundle.putString("summary", "我正在使用在线一起走科学健身管理运动和健康");
         bundle.putString("site",  "2222");
         bundle.putString("appName", "在线一起走");
         return bundle;
	}
	Bundle shareParams = null;

	Handler shareHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
	};

	// 线程类，该类使用匿名内部类的方式进行声明
	Runnable shareThread = new Runnable() {

		public void run() {
			doShareToQQ(shareParams);
			Message msg = shareHandler.obtainMessage();

			// 将Message对象加入到消息队列当中
			shareHandler.sendMessage(msg);

		}
	};

	private void doShareToQQ(Bundle params) {
		mTencent.shareToQQ(MainActivity.this, params, new BaseUiListener() {
			protected void doComplete(JSONObject values) {
				showResult("shareToQQ:", "onComplete");
			}

			@Override
			public void onError(UiError e) {
				showResult("shareToQQ:", "onError code:" + e.errorCode
						+ ", msg:" + e.errorMessage + ", detail:"
						+ e.errorDetail);
			}

			@Override
			public void onCancel() {
				showResult("shareToQQ", "onCancel");
			}
		});
	}

	private class BaseUiListener implements IUiListener {


		public void onComplete(JSONObject response) {
			// mBaseMessageText.setText("onComplete:");
			// mMessageText.setText(response.toString());
			doComplete(response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onComplete(Object o) {

		}

		@Override
		public void onError(UiError e) {
			showResult("onError:", "code:" + e.errorCode + ", msg:"
					+ e.errorMessage + ", detail:" + e.errorDetail);
		}

		@Override
		public void onCancel() {
			showResult("onCancel", "");
		}
	}

	private Handler mHandler;

	private void showResult(final String base, final String msg) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
			}
		});
	}
}
