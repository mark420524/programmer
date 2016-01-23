package com.silivall.programmer.lisitener;

import org.json.JSONObject;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQloginListener implements IUiListener {

	@Override
	public void onComplete(Object response) {
	}

	protected void doComplete(JSONObject values) {

	}

	@Override
	public void onError(UiError e) {
	}

	@Override
	public void onCancel() {
	}

}
