package com.silivall.programmer.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easemob.applib.utils.Constant;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.silivall.programmer.R;
import com.silivall.programmer.util.DateUtils;
import com.silivall.programmer.util.SmileUtils;
import com.silivall.programmer.util.StringUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class ChatConversationAdapter extends ArrayAdapter<EMConversation> {

	private static final String TAG = ChatConversationAdapter.class.getName();
	
	//filter 使用,暂时无用到
	@SuppressWarnings("unused")
	private List<EMConversation> conversationList;
	private List<EMConversation> copyConversationList;
	private LayoutInflater inflater;
	private int resourceId;
	private boolean notifyByFilter = false;
	
	public ChatConversationAdapter(Context context, int resource,
			List<EMConversation> objects) {
		super(context, resource, objects);
		this.conversationList = objects;
		copyConversationList = new ArrayList<EMConversation>();
		copyConversationList.addAll(objects);
		this.resourceId = resource;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		if (convertView==null) {
			convertView = this.inflater.inflate(this.resourceId, parent,false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder==null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		if (position%2 == 0) {
			holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
		} else {
			holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
		}
		
		//聊天记录显示
		EMConversation conversation = getItem(position);
		String userName = conversation.getUserName();
		if (conversation.getType()==EMConversationType.GroupChat) {
			//群组头像,可以自定义
			holder.avatar.setImageResource(R.drawable.group_icon);
			EMGroup group = EMGroupManager.getInstance().getGroup(userName);
			holder.name.setText(group!=null && !TextUtils.isEmpty(group.getGroupName())?group.getGroupName():userName);
		}else if (conversation.getType()==EMConversationType.ChatRoom) {
			//聊天室
			holder.avatar.setImageResource(R.drawable.group_icon);
	        EMChatRoom room = EMChatManager.getInstance().getChatRoom(userName);
	        holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : userName);
		}else{
			//单个聊天
			if (userName.equals(Constant.GROUP_USERNAME)) {
				holder.name.setText("群聊");
			} else if (userName.equals(Constant.NEW_FRIENDS_USERNAME)) {
				holder.name.setText("申请与通知");
			} else {
				//设置聊天用户的用户名,需从服务端读取
				holder.name.setText(userName);
			}
		}
		
		if (conversation.getUnreadMsgCount() > 0) {
			//未读消息数量显示
			holder.unreadLabel.setText(""+conversation.getUnreadMsgCount());
			holder.unreadLabel.setVisibility(View.VISIBLE);
		}else{
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}
		if (conversation.getMsgCount()!=0) {
			EMMessage lastMessage = conversation.getLastMessage();
			holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, this.getContext())),
					BufferType.SPANNABLE);
			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		ImageView avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_item_layout;
	}
	
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION:
			if (message.direct==EMMessage.Direct.RECEIVE) {
				digest = StringUtil.getResourcesString(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
			}else{
				digest = StringUtil.getResourcesString(context, R.string.location_prefix);
			}
			break;
		case IMAGE:
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = StringUtil.getResourcesString(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:
			digest = StringUtil.getResourcesString(context, R.string.voice);
			break;
		case VIDEO:
			digest = StringUtil.getResourcesString(context, R.string.video);
			break;
		case TXT:
			//文本消息需要格式化
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				digest = StringUtil.getResourcesString(context, R.string.voice_call) + txtBody.getMessage();
			}else{
				digest = txtBody.getMessage();
			}
			break;
		case FILE:
			digest = StringUtil.getResourcesString(context, R.string.file);
			break;
		default:
			Log.e(TAG, "unknown messag type");
			break;
		}
		return digest;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (!notifyByFilter) {
			copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notifyByFilter = false;
		}
	}
	
	
}
