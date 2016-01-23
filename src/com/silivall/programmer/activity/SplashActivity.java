package com.silivall.programmer.activity;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.silivall.programmer.R;
import com.silivall.programmer.util.PreferenceUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {
	
	private static final int GO_HOME = 0;
	
	private static final int GO_LOGIN = 1;
	
	private RelativeLayout rootLayout;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case GO_LOGIN:
				goLogin();
				break;
			case GO_HOME:
				goHome();
				break;
			}
		}

		
	};
	
	private void goLogin() {
		Intent intent = new Intent(SplashActivity.this,SelectLoginActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}
	
	private void goHome() {
		Intent intent = new Intent(SplashActivity.this,MainActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		init();
	}
	
	private void init() {
		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
		if (PreferenceUtils.getInstance().getUserIsLogin()) {
			EMGroupManager.getInstance().loadAllGroups();
			EMChatManager.getInstance().loadAllConversations();
			handler.sendEmptyMessageDelayed(GO_HOME, 2000);
		} else {
			handler.sendEmptyMessageDelayed(GO_LOGIN, 3000);
		}
	}
}
