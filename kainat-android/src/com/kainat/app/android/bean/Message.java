package com.kainat.app.android.bean;

import java.util.Vector;

public class Message {
	
	public  static final byte MESSAGE_TYPE_BUDDY = 0;
	public  static final byte MESSAGE_TYPE_FRIEND_REQUEST = -1;
	public  static final byte MESSAGE_TYPE_SYSTEM_MESSAGE = -2;
//	public  static final byte MESSAGE_TYPE_ADDCONVERSATION_LEFTCONVERSATION_CHANGED_SUBJECT = 91;
	
	
	public  static final byte MESSAGE_TYPE_LEAVE_CONVERSATION     =    91;
	public  static final byte MESSAGE_TYPE_ADD_TO_CONVERSATION    =   92;
	public  static final byte MESSAGE_TYPE_UPDATE_SUBJECT        =       93;
	public  static final byte MESSAGE_TYPE_ADD_TO_CONVERSATION_ALREADY    =   94;
	public  static final byte MESSAGE_TYPE_BUDDY_CAN_NOT_ADD_TO_CONVERSATION    =   95;
	public  static final byte MESSAGE_TYPE_EMAIL_VERIFY    =   114;
	
	public  String parent_id;
	public  String clientTransactionId;
	public  String contentBitMap;
	public  String conversationId;
	public  String datetime;
	public  int drmFlags;
	public  String hasBeenViewed;
	public  String hasBeenViewedBySIP;
	public  String messageId;
	public  String usmId;
	public  String mfuId;
	public  int msgOp;
	public  String msgTxt;
	public  long inserTime;
	public  long sortTime;
	public  int notificationFlags;
	public  String refMessageId;
	public  String senderUserId;
	public  String source;
	public  String targetUserId;
	public  String user_firstname ;
	public  String user_lastname ;
	public  String user_name;
	public  String user_uri ;
	public  String user_id ;
	public  String subject ;
	public String imageFileId;
	
	public  String tag ;
	public  int messageType =MESSAGE_TYPE_BUDDY;
	public  int isGroup=0;
	public  int isSelected;
	public  int isBookmark=0;
	public  int isNext=0;
	public  String isLastMessage;
	public  String more;
	public  int isLeft;
//	public  int sendingStatus=0;
	
	public  String participantInfoStr ;
//	public  String participant_info_username ;
	public  Vector<ParticipantInfo> participantInfo ;
	public int audio_download_statue = 0;
	public int video_download_statue = 0;
	public int sending_state =0;
	
	public String video_content_thumb_urls ;
	public String video_size ;
	public String video_content_urls ;
	public String voice_content_thumb_urls;
	public String voice_content_urls;
	public String voice_ID;
	public String video_ID;
	public String image_content_thumb_urls;
	public String image_content_urls ;
	public long cltTxnId ;
	
	public String audio_size ;
	public String image_size ;
	public ParticipantInfo participantInfoClazz ;
	public String messageAttribute ;
	public String aniType ;
	public String aniValue ;
	public String direction ;
	public boolean lastMsg ;
	public  String comments;
	
	@Override
	public String toString() {
		String s = "[parent_id :"+parent_id+"]\n[" +
				"[clientTransactionId :"+clientTransactionId+"]\n[" +
				"[contentBitMap :"+contentBitMap+"]\n[" +
				"[cltTxnId :"+cltTxnId+"]\n[" +
				"[user_firstname :"+user_firstname+"]\n[" +
				"[user_lastname :"+user_lastname+"]\n[" +
				"[user_name :"+user_name+"]\n[" +
				"[user_uri :"+user_uri+"]\n[" +
				"[user_id :"+user_id+"]\n[" +
				"[participant_info_sender :"+participantInfoStr+"]\n[" +
//				"[participant_info_username :"+participant_info_username+"]\n[" +
				"[conversationId :"+conversationId+"]\n[" +
				"[datetime :"+datetime+"]\n[" +
				"[drmFlags :"+drmFlags+"]\n[" +
				"[hasBeenViewed :"+hasBeenViewed+"]\n[" +
				"[hasBeenViewedBySIP :"+hasBeenViewedBySIP+"]\n[" +
				"[messageId :"+messageId+"]\n[" +
				"[mfuId :"+mfuId+"]\n[" +
				"[msgOp :"+msgOp+"]\n[" +
				"[msgTxt :"+msgTxt+"]\n[" +
				"[notificationFlags :"+notificationFlags+"]\n[" +
				"[refMessageId :"+refMessageId+"]\n[" +
				"[senderUserId :"+senderUserId+"]\n[" +
				"[source :"+source+"]\n[" +
				"[targetUserId :"+targetUserId+"]\n[" +
				"[participantInfoStr :"+participantInfoStr+"]\n["+
				"[audio_download_statue :"+audio_download_statue+"]\n[" +
				"[video_download_statue :"+video_download_statue+"]\n[" +
				"[sending_state :"+sending_state+"]\n[" ;
				
		return s;
	}
	
	
}
