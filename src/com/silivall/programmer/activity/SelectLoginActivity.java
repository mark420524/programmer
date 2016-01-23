package com.silivall.programmer.activity;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.kaixin.connect.Kaixin;
import com.kaixin.connect.exception.KaixinAuthError;
import com.kaixin.connect.listener.KaixinAuthListener;
import com.silivall.programmer.R;
import com.silivall.programmer.statis.EasemobConstant;
import com.silivall.programmer.statis.QQLoginStatic;
import com.silivall.programmer.statis.ThirdLoginIndex;
import com.silivall.programmer.statis.WeiboLoginStatis;
import com.silivall.programmer.util.JsonUtils;
import com.silivall.programmer.util.PreferenceUtils;
import com.silivall.programmer.util.Util;
import com.silivall.programmer.util.dialog.LoginLoadDialog;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SelectLoginActivity extends Activity {

	private static final String TAG = SelectLoginActivity.class.getName();
	private int index;
	
	/*QQ登录 start*/
	private static Tencent tencent;
	public static boolean isServerSideLogin = false;
	/*QQ登录 end*/
	
	/*微博登录 start*/
	private AuthInfo authInfo;
	private SsoHandler mSsoHandler;
	private Oauth2AccessToken accessToken;
	/*微博登录 end*/
	
	private String userName;
	
	private Dialog dialog;
	
	private static final int LOGIN_HX = 0;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGIN_HX:
				loginHXUser(userName,EasemobConstant.ACCOUNT_PASSWORD);
				break;
			default:
				break;
			}
		}
	};
	
	private IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.d(TAG, "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
//            updateUserInfo();
//            updateLoginButton();
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//登录判断此处应该重写
		setContentView(R.layout.actity_third_login);
		if (tencent == null) {
			tencent = Tencent.createInstance(QQLoginStatic.QQ_APP_ID, this.getApplicationContext());
	    }
		authInfo = new AuthInfo(this, WeiboLoginStatis.WEIBO_APP_KEY, WeiboLoginStatis.REDIRECT_URL, WeiboLoginStatis.SCOPE);
		mSsoHandler = new SsoHandler(SelectLoginActivity.this, authInfo);
	}

	public void selectLogin(View view) {
		dialog = LoginLoadDialog.createLoadingDialog(this, getResources().getString(R.string.loginIng));
		switch (view.getId()) {
		case R.id.mobilelogin:
			index = ThirdLoginIndex.MOBILE_LOGIN;
			Toast.makeText(getApplicationContext(), "手机号登录敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.weixinlogin:
			index = ThirdLoginIndex.WECHAT_LOGIN;
			Toast.makeText(getApplicationContext(), "微信登录敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.qqlogin:
			index = ThirdLoginIndex.QQ_LOGIN;
			Toast.makeText(getApplicationContext(), "QQ登录", Toast.LENGTH_SHORT).show();
			onQQClickLogin();
			break;
		case R.id.weibologin:
			index = ThirdLoginIndex.WEIBO_LOGIN;
			Toast.makeText(getApplicationContext(), "微博登录", Toast.LENGTH_SHORT).show();
			weiboLoginProgram();
			break;
		case R.id.kaixinlogin:
			index = ThirdLoginIndex.WEIBO_LOGIN;
			Toast.makeText(getApplicationContext(), "开心网登录", Toast.LENGTH_SHORT).show();
			kaixinLoginProgram();
			break;
		default:
			break;
		}
		dialog.show();
	}
	
	private void weiboLoginProgram() {
		mSsoHandler.authorize(new MyWeiboAuthListener());
	}
	
	private void kaixinLoginProgram() {
		Kaixin kaixin = Kaixin.getInstance();
		kaixin.loadStorage(SelectLoginActivity.this);
		String[] permissions = {"basic", "create_records"};
		kaixin.authorize(SelectLoginActivity.this, permissions, new KaixinListener());
	}
	
	
	private void onQQClickLogin() {
		if (!tencent.isSessionValid()) {
			tencent.login(this, QQLoginStatic.QQ_LOGIN_SCOPE, loginListener);
            isServerSideLogin = false;
			Log.d(TAG, "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                tencent.logout(this);
                tencent.login(this, QQLoginStatic.QQ_LOGIN_SCOPE, loginListener);
                isServerSideLogin = false;
                Log.d(TAG, "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
		    tencent.logout(this);
//			updateUserInfo();
//			updateLoginButton();
		}
	}
	
	private void goHome() {
		Intent intent = new Intent(SelectLoginActivity.this,MainActivity.class);
		SelectLoginActivity.this.startActivity(intent);
		SelectLoginActivity.this.finish();
	};
	
	private class KaixinListener implements KaixinAuthListener {

		@Override
		public void onAuthCancel(Bundle arg0) {
			
		}

		@Override
		public void onAuthCancelLogin() {
			
		}

		@Override
		public void onAuthComplete(Bundle values) {
			if (null == values) {
                Util.showResultDialog(SelectLoginActivity.this, "开心网返回为空", "登录失败");
                return;
            }
			Kaixin kaixin = Kaixin.getInstance();
			try {
				String response = kaixin.refreshAccessToken(SelectLoginActivity.this, null);
				Util.showResultDialog(SelectLoginActivity.this, response.toString(), "登录成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAuthError(KaixinAuthError e) {
			Util.toastMessage(SelectLoginActivity.this, "onError: " + e.getMessage());
			Util.dismissDialog();
		}
		
	}
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(SelectLoginActivity.this, "QQ返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(SelectLoginActivity.this, "QQ返回为空", "登录失败");
                return;
            }
            String openid = "";
            try {
				Map<Object, Object> paramMap = JsonUtils.parseAPIResponseDataToMap(jsonResponse, false);
				openid = (String) paramMap.get("openid");
				//此处服务端请求openid 并记录该登录
				//注册环信
				String registerHXUserName = ThirdLoginIndex.LOGIN_QQ + openid;
				if (EasemobConstant.hxsdkSetOk) {
					userName = registerHXUserName;
					regitsterHXUser(registerHXUserName, EasemobConstant.ACCOUNT_PASSWORD);
				}else{
					Toast.makeText(getApplicationContext(), "环信初始化失败!", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				openid = "亲,稍等一会再试试吧~_~!";
			}
            // 有奖分享处理	代码暂时无用
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(SelectLoginActivity.this, "onError: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			PreferenceUtils.getInstance().setUserIsLogin(false);
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
		}
	}
	
	private void regitsterHXUser(final String userName,final String pwd) {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(userName, pwd);
					runOnUiThread(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(LOGIN_HX);
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (!SelectLoginActivity.this.isFinishing())
								dialog.dismiss();
							int errorCode=e.getErrorCode();
							if(errorCode==EMError.NONETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXISTS){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.UNAUTHORIZED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.ILLEGAL_USER_NAME){
							    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();
	}
	
	
	private void loginHXUser(String userName,String pwd) {
		EMChatManager.getInstance().login(userName, pwd, new EMCallBack() {
			
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						Log.d(TAG, "登陆聊天服务器成功！");	
						PreferenceUtils.getInstance().setUserIsLogin(true);
						dialog.dismiss();
						goHome();
					}
				});
			}
			
			@Override
			public void onProgress(int arg0, String arg1) {
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				Log.d(TAG, "登陆聊天服务器失败！");	
				dialog.dismiss();
				PreferenceUtils.getInstance().setUserIsLogin(false);
			}
		});
	}
	
	private class MyWeiboAuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			PreferenceUtils.getInstance().setUserIsLogin(false);
		}

		@Override
		public void onComplete(Bundle values) {
			if (null == values) {
                Util.showResultDialog(SelectLoginActivity.this, "微博返回为空", "登录失败");
                return;
            }
			accessToken = Oauth2AccessToken.parseAccessToken(values);
			String openid = "";
			if (accessToken.isSessionValid()) {
				openid = values.getString("uid");
				if (openid==null) {
					openid="";
				}
				//请求服务端记录保存uid信息
				//注册环信
				
				String registerHXUserName = ThirdLoginIndex.LOGIN_WEIBO + openid;
				if (EasemobConstant.hxsdkSetOk) {
					userName = registerHXUserName;
					regitsterHXUser(registerHXUserName, EasemobConstant.ACCOUNT_PASSWORD);
				}else{
					Toast.makeText(getApplicationContext(), "环信初始化失败!", Toast.LENGTH_SHORT).show();
				}
			} else {
				String code = values.getString("code");
				Util.showResultDialog(SelectLoginActivity.this,code,"错误信息");
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Util.toastMessage(SelectLoginActivity.this, "onError: " + e.getMessage());
			Util.dismissDialog();
		}
		
	}
	
	public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                tencent.setAccessToken(token, expires);
                tencent.setOpenId(openId);
            }
        } catch(Exception e) {
        	Log.e(TAG, getResources().getString(R.string.exception),e);
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (index==ThirdLoginIndex.QQ_LOGIN) {
			Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
		}
		if (index==ThirdLoginIndex.WEIBO_LOGIN && mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
