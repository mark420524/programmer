package com.silivall.programmer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.easemob.applib.utils.Constant;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.silivall.programmer.ProgrammerApplication;
import com.silivall.programmer.R;
import com.silivall.programmer.adapter.ChatConversationAdapter;
import com.silivall.programmer.statis.EasemobConstant;
import com.silivall.programmer.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatConversationFragment extends Fragment {

	private InputMethodManager inputMethodManager;
	private ListView listView;
	private ChatConversationAdapter adapter;
	private RelativeLayout errorItem;
	private TextView errorText;
	private boolean hidden;

	private ArrayList<EMConversation> conversationList = new ArrayList<EMConversation>();
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_session_list, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null && savedInstanceState.getBoolean(EasemobConstant.IS_CONFLICT, false)) {
			return;
		}
		View view = getView();
		errorItem = (RelativeLayout) view.findViewById(R.id.network_error_layout);
		errorText = (TextView) view.findViewById(R.id.tv_connect_errormsg);
		//添加当前用户的会话
		conversationList.addAll(loadConversationsWithRecentChat());
		listView = (ListView) view.findViewById(R.id.list_view_layout);
		
		adapter = new ChatConversationAdapter(getActivity(), R.layout.item_chat_history, conversationList);
		//添加适配
		listView.setAdapter(adapter);
		
		final String canNotChat = StringUtil.getResourcesString(getContext(), R.string.Cant_chat_with_yourself);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String userName = conversation.getUserName();
				if (userName.equals(ProgrammerApplication.getInstance().getUserName())) {
					Toast.makeText(getActivity(), canNotChat, Toast.LENGTH_SHORT).show();
				}else{
					Intent intent = new Intent(getActivity(),ChatActivity.class);
					if (conversation.isGroup()) {
						if(conversation.getType() == EMConversationType.ChatRoom){
							// it is group chat
	                        intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
	                        intent.putExtra("groupId", userName);
				        }else{
				        	// it is group chat
	                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
	                        intent.putExtra("groupId", userName);
				        }
					}else{
						intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
						intent.putExtra("userId", userName);
					}
					startActivity(intent);
					getActivity().finish();
				}
			}
		});
		
		// 注册上下文菜单
		registerForContextMenu(listView);
	}
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		this.getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
		return true;
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideSoftKeyBoard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private ArrayList<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
		 * 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 
		 * 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					//if(conversation.getType() != EMConversationType.ChatRoom){
						sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
					//}
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}
	
	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden && !MainActivity.isConflict) {
			refresh();
		}
	}
	
	/**
	 * 刷新页面
	 */
	private void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if(adapter != null)
		    adapter.notifyDataSetChanged();
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//保存账号的状态
        if(MainActivity.isConflict) {
        	outState.putBoolean(EasemobConstant.IS_CONFLICT, true);
        }else if(MainActivity.getCurrentAccountRemoved()){
        	outState.putBoolean(EasemobConstant.ACCOUNT_REMOVED, true);
        }
    }
	
}
