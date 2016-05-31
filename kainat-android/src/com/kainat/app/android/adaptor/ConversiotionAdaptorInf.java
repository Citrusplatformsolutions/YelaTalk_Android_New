package com.kainat.app.android.adaptor;

import android.content.Context;
import android.view.View;

import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.Message;

public interface ConversiotionAdaptorInf {
	public void viewConversations(View convertView, Context context, ConversationList conversationList) ;
	public void viewMessage(View convertView, Context context, Message message) ;
	
}
