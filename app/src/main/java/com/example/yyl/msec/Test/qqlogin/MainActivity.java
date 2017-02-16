package com.example.yyl.msec.Test.qqlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.activity.RegisterActivity;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 *
 * 通过调用Tencent类的login函数发起登录/校验登录态。
 * 该API具有两个作用：
 * （1）如果开发者没有调用mTencent实例的setOpenId、setAccessToken API，
 * 		则该API执行正常的登录操作；
 * （2）如果开发者先调用mTencent实例的setOpenId、setAccessToken
 * 		API，则该API执行校验登录态的操作。如果登录态有效，则返回成功给应用，
 * 		如果登录态失效，则会自动进入登录流程，将最新的登录态数据返回给应用
 *
 * @author super bear
 *
 */
public class MainActivity extends Activity {

	private static final String APPID = "1105755529";


	private Tencent mTencent;
	private IUiListener loginListener;
	private IUiListener userInfoListener;
	private String scope;
	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qqlogin_main);

		setupViews();
		initData();
	}

	@Override
	protected void onDestroy() {
		if (mTencent != null) {
			mTencent.logout(MainActivity.this);
		}
		super.onDestroy();
	}

	private void setupViews() {


		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("你点击了使用qq登录按钮");
				login();
			}
		});

		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("开始获取用户信息");
				if(mTencent.getQQToken() == null){
					System.out.println("qqtoken ====================== null");
				}

				userInfo = new UserInfo(MainActivity.this, mTencent.getQQToken());
				userInfo.getUserInfo(userInfoListener);
			}
		});
	}

	private void initData() {
		mTencent = Tencent.createInstance(APPID, MainActivity.this);
		//要所有权限，不用再次申请增量权限，这里不要设置成get_user_info,add_t


		//登录，第二个参数为实现IUiListener接口
		//mTencent.login(this,"all",loginListener);
		scope = "all";

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
						Toast.makeText(MainActivity.this, "登录成功",
								Toast.LENGTH_SHORT).show();

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

						userInfo = new UserInfo(MainActivity.this, mTencent.getQQToken());
						userInfo.getUserInfo(userInfoListener);



						Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
						startActivity(intent);

						String nickName = jo.getString("nickname");
						String gender = jo.getString("gender");

						Toast.makeText(MainActivity.this, "你好，" + nickName, Toast.LENGTH_LONG).show();
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
								mTencent.reAuth(MainActivity.this, "all", new IUiListener() {

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

						MainActivity.this.runOnUiThread(r);
					}else{
						String nickName = jo.getString("nickname");
						String gender = jo.getString("gender");

						Toast.makeText(MainActivity.this, "你好，" + nickName, Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					// TODO: handle exception
				}


			}


		};
	}

	private void login() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(MainActivity.this, scope, loginListener);

		}
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



}
