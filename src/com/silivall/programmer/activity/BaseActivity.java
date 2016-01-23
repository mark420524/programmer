package com.silivall.programmer.activity;

import com.easemob.applib.controller.HXSDKHelper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class BaseActivity extends FragmentActivity {

	 @Override
	    protected void onCreate(Bundle arg0) {
	        super.onCreate(arg0);
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        // onresume时，取消notification显示
	        HXSDKHelper.getInstance().getNotifier().reset();
	        
	        // umeng
//	        MobclickAgent.onResume(this);
	    }

	    @Override
	    protected void onStart() {
	        super.onStart();
	        // umeng
//	        MobclickAgent.onPause(this);
	    }


	    /**
	     * 返回
	     * 
	     * @param view
	     */
	    public void back(View view) {
	        finish();
	    }
}
