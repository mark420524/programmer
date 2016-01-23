package com.silivall.programmer.activity;

import com.silivall.programmer.R;
import com.silivall.programmer.helper.EasemobSDKHelper;
import com.silivall.programmer.statis.EasemobConstant;
import com.silivall.programmer.util.PreferenceUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends BaseActivity {

	private int index;
	private Button[] mTabs;
	// 当前fragment的index
	private int currentTabIndex;
	
	private Fragment[] fragments;
	private ChatConversationFragment chatConversationFragment;
	
	// 账号在别处登录
	public static boolean isConflict = false;
	// 账号被移除
	private static boolean isCurrentAccountRemoved = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!PreferenceUtils.getInstance().getUserIsLogin()) {
			EasemobSDKHelper.getInstance().logout(true, null);
			goLogin();
			return;
		} else if(savedInstanceState != null && savedInstanceState.getBoolean(EasemobConstant.IS_CONFLICT, false)){
			goLogin();
			return;
		}
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		initView();
		
		//显示账号在别处登录及账号被移除的对话框暂时不做
		chatConversationFragment = new ChatConversationFragment();
		fragments = new Fragment[]{chatConversationFragment};
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatConversationFragment).commit();
		init();
	}
	
	/**
	 * 注册环信监听内容 
	 */
	private void init() {     
		// setContactListener监听联系人的变化等
//		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
	
//		connectionListener = new MyConnectionListener();
//		EMChatManager.getInstance().addConnectionListener(connectionListener);
		
//		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
//        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
		
	}

	
	/**
	 * 检查当前用户是否被删除
	 */
	public static boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}
	
	private void goLogin() {
		startActivity(new Intent(this, SelectLoginActivity.class));
		this.finish();
	}
	
	/**
	 * 初始化组件
	 */
	private void initView() {
//		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
//		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_programmer);
		mTabs[1] = (Button) findViewById(R.id.btn_contact);
		mTabs[2] = (Button) findViewById(R.id.btn_mine);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
//		菜单项注释
//		registerForContextMenu(mTabs[1]);
	}

	public void onButtonClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_programmer:
			index = 0;
			break;
		case R.id.btn_contact:
			index = 1;
			break;
		case R.id.btn_mine:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}
	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//判断账号是否别处登录及删除,显示对话框	暂时不做
	}
}
