package com.kainat.app.android.bean;

public class ConversationList extends Message {
	
	public static int unreadMessageCount;
	
	@Override
	public String toString() {
		String s = "[parent_id :"+parent_id+"]\n[" +
				"clientTransactionId :"+clientTransactionId+"]\n[" +
				"unreadMessageCount :"+unreadMessageCount+"]\n[" +
				"contentBitMap :"+contentBitMap+"]\n[" +
				"conversationId :"+conversationId+"]\n[" +
				"datetime :"+datetime+"]\n[" +
				"drmFlags :"+drmFlags+"]\n[" +
				"hasBeenViewed :"+hasBeenViewed+"]\n[" +
				"hasBeenViewedBySIP :"+hasBeenViewedBySIP+"]\n[" +
				"messageId :"+messageId+"]\n[" +
				"mfuId :"+mfuId+"]\n[" +
				"msgOp :"+msgOp+"]\n[" +
				"msgTxt :"+msgTxt+"]\n[" +
				"notificationFlags :"+notificationFlags+"]\n[" +
				"refMessageId :"+refMessageId+"]\n[" +
				"senderUserId :"+senderUserId+"]\n[" +
				"source :"+source+"]\n[" +
				"targetUserId :"+targetUserId+"]" ;
		return s;
	}
}
