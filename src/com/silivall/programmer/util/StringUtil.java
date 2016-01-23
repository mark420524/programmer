package com.silivall.programmer.util;

import android.content.Context;

public class StringUtil {

	public static String getResourcesString(Context context,int resId) {
		if (context==null) {
			return "";
		}
		return context.getResources().getString(resId);
	}
}
