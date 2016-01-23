package com.silivall.programmer.statis;

public class WeiboLoginStatis {

	public static final String WEIBO_APP_KEY = "1441124418";
	
	public static final String WEIBO_APP_SECRET = "d7cd40875be63e124cc559dab8924826";
	
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页

	public static final String SCOPE = 							// 应用申请的高级权限
			"email,direct_messages_read,direct_messages_write,"
	            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
	            + "follow_app_official_microblog," + "invitation_write";

}
