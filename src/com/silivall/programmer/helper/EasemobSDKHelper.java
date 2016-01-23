package com.silivall.programmer.helper;

import android.content.Context;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.model.HXSDKModel;
import com.silivall.programmer.model.CustomerHXSDKModel;

public class EasemobSDKHelper extends HXSDKHelper {

	@Override
	protected HXSDKModel createModel() {
		return new CustomerHXSDKModel(appContext);
	}

	@Override
	public synchronized boolean onInit(Context context) {
		if (super.onInit(context)) {
			//此处可以做自定义开发的代码操作
			//if your app is supposed to user Google Push, please set project number
//          String projectNumber = "562451699741";
//          EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
			//此处环信sdk重置成功,之后可以调用环信代码,否则不可以调用
			return true;
		}
		return false;
	}

}
