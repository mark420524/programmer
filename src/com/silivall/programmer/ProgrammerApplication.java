package com.silivall.programmer;


import com.easemob.EMCallBack;
import com.silivall.programmer.helper.EasemobSDKHelper;
import com.silivall.programmer.statis.EasemobConstant;
import com.silivall.programmer.util.PreferenceUtils;

import android.app.Application;
import android.content.Context;

public class ProgrammerApplication extends Application {

	public static Context applicationContext;
	private static ProgrammerApplication instance;
	
	private static EasemobSDKHelper hxSDKHelper = new EasemobSDKHelper();
	
	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = this;
		instance = this;
		EasemobConstant.hxsdkSetOk=hxSDKHelper.onInit(applicationContext);
		//初始化 shared preference
		PreferenceUtils.init(applicationContext);
		//先设置已登录
//		PreferenceUtils.getInstance().setUserIsLogin(true);
	}

	public static ProgrammerApplication getInstance() {
		return instance;
	}
	
	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM,final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(isGCM,emCallBack);
	}
	
	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}
}
