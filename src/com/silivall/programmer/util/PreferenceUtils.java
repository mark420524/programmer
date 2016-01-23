package com.silivall.programmer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {

	private static final String PROGRAMMER_PREFERENCE_NAME = "programmerSharedPreferences";
	
	private static final String PROGRAMMER_USER_IS_LOGIN = "programmer_user_is_login";
	
	private static SharedPreferences sharedPreferencs;
	
	private static PreferenceUtils preferenceUtils;
	
	private static Editor editor;
	
	private PreferenceUtils(Context context) {
		sharedPreferencs = context.getSharedPreferences(PROGRAMMER_PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferencs.edit();
	}
	
	/**
	 * 项目启动初始化该类
	 * @param context
	 */
	public static synchronized void init(Context context){
	    if(preferenceUtils == null){
	    	preferenceUtils = new PreferenceUtils(context);
	    }
	}
	
	/**
	 * 单例模式，获取instance实例
	 * @return
	 */
	public static PreferenceUtils getInstance() {
		if (preferenceUtils == null) {
			throw new RuntimeException("please init first!");
		}
		return preferenceUtils;
	}
	
	/**
	 * 设置用户是否登录
	 * @param paramBoolean
	 */
	public void setUserIsLogin(boolean paramBoolean) {
		editor.putBoolean(PROGRAMMER_USER_IS_LOGIN, paramBoolean);
		editor.commit();
	}
	
	/**
	 * 查询用户是否登录过用户记录登录状态
	 * @return
	 */
	public boolean getUserIsLogin() {
		return sharedPreferencs.getBoolean(PROGRAMMER_USER_IS_LOGIN, false);
	}
}
