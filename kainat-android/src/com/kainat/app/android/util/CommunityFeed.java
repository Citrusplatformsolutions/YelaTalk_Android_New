package com.kainat.app.android.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.graphics.drawable.Drawable;

public final class CommunityFeed implements Serializable{
	//Community Tags
	public static final String TAG_TITLE = "title";
	transient public static final String TAG_SEO = "seo";
	public static final String TAG_WATER_MARK = "watermarkurl";//320x420
	public static final String TAG_WATER_MARK_WIDE = "watermarkurlwide";//320x240 watermarkurlwide
	public static final String TAG_TOTAL_GROUP_COUNT = "totalGroupCount";
	public static final String TAG_RT_TRACKING = "tracking";
	public static final String TAG_LINK = "link";
	public static final String TAG_ENTRY = "entry";
	public static final String TAG_TOTAL_COUNT = "totalCount";
//	public static final String TAG_NEXT_URL = "nexturl";
//	public static final String TAG_TOTAL_COUNT = "totalCount";
	
	//Community Entry Tags
	public static final String TAG_GROUP_ID = "groupId";
	public static final String TAG_GROUP_NAME = "groupName";
	public static final String TAG_TAGS = "tags";
	//MANOJ SINGH --- REPORT CHANGES
	
	public static final String TAG_REPORT_COUNT = "reportcount";
	public static final String TAG_REPORT_MESSAGE_URL = "reportedmessageurl";
	
	//---------------------
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_IS_MODERATED = "isModerated";
	public static final String TAG_AUTO_ACCEPT_USER = "autoAcceptUser";
	public static final String TAG_IS_PUBLIC = "isPublic";
	public static final String TAG_LAST_UPDATED = "lastUpdated";
	public static final String TAG_TIME_OF_CREATION = "timeOfCreation";
	public static final String TAG_NO_OF_MEMBERS = "numberOfMembers";
	public static final String TAG_NO_OF_MESSAGES = "numberOfMessages";
	public static final String TAG_OWNER_USER_ID = "ownerUserId";
	public static final String TAG_WELCOME_MSG_ID = "welcomeMsgId";
	public static final String TAG_MSG_ID = "msgId";
	public static final String TAG_VENDOR_NAME = "vendorName";
	public static final String TAG_NO_OF_ONLINE_USERD = "numberOfOnlineMembers";
	public static final String TAG_COMMON_FRIEND_COUNT = "commonFriendsCount";
	public static final String TAG_ACTIVE_MEMBERS = "activeMembers";
	public static final String TAG_MEMBER_PERMISSION = "memberPermission";
	public static final String TAG_IS_ADMIN = "isAdmin";
	public static final String TAG_THUMB_URL = "thumbUrl";
	public static final String TAG_MESSAGE_URL = "messageUrl";
	public static final String TAG_PROFILE_URL = "profileUrl";
	public static final String TAG_SEARCH_MEMBER_URL = "searchMemberUrl";
	public static final String TAG_browseMsgUrl = "browseMsgUrl";
	public static final String TAG_adminState = "adminState";
	public static final String TAG_pushNotif = "pushNotif";
	public static final String NEW_MSG_COUNT = "newmsgcount";
	public static final String TAG_TOP_MSG_ID = "topMessageId";


	public static final String TAG_profileUrl = "profileUrl";
	public static final String TAG_searchMemberUrl = "searchMemberUrl";
	public static final String TAG_ownerUsername = "ownerUsername";
	public static final String TAG_ownerProfileUrl = "ownerProfileUrl";
	public static final String TAG_ownerDisplayName = "ownerDisplayName";
	public static final String TAG_ownerThumbUrl = "ownerThumbUrl";
	
	
	//Recommended Entry Tags
	public static final String TAG_DISPLAY_NAME = "displayName";
	public static final String TAG_COUNT = "count";
	public static final String TAG_URL = "url";
	public static final String TAG_TYPE = "type";
	//Pending Request Tag
	public static final String TAG_USER_ID = "userId";
	public static final String TAG_USER_NAME = "userName";
	public static final String TAG_USER_GENDER = "gender";
	public static final String TAG_TIME_OF_REQUEST = "timeOfRequest";
	public static final String TAG_BUTTON = "button";
	//Community Chat
	public static final String TAG_SHOW_MODERATION_ALERT = "showModerationAlert";
	public static final String TAG_IS_OWNER = "isOwner";
	public static final String TAG_ADMIN_STATE = "adminState";
	public static final String TAG_FEATURED = "featured";
	public static final String TAG_COMMUNITY_URL = "communityUrl";
	public static final String TAG_SOCKETED_MESSAGE_URL = "socketedMessageUrl";
	public static final String TAG_NEXT_URL = "nexturl";
	public static final String TAG_MESSAGE_ID = "messageId";
	public static final String TAG_PARENT_MESSAGE_ID = "parentMessageId";
	public static final String TAG_SENDER_ID = "senderId";
	public static final String TAG_SENDER_NAME = "senderName";
	public static final String TAG_MESSAGE_STATE = "messageState";
	public static final String TAG_DRM_FLAGS = "drmFlags";
	public static final String TAG_CREATED_DATE = "createdDate";
	public static final String TAG_MESSAGE_TEXT = "messageText";
	public static final String TAG_IMG_THUMB_URL = "imgThumbUrl";
	public static final String TAG_IMG_URL = "imgUrl";
	public static final String TAG_AUDIO_URL = "audioUrl";
	public static final String TAG_VID_THUMB_URL = "vidThumbUrl";
	public static final String TAG_VID_URL = "vidUrl";
	//Community Members
	public static final String TAG_SOCKET_URL = "socketedUrl";
	public static final String TAG_FIRST_NAME = "firstName";
	public static final String TAG_LAST_NAME = "lastName";
	public static final String TAG_USER_STATUS = "userStatus";
	public static final String TAG_REPLYTO = "replyTo";
	public static final String TAG_REPLYTO_FIRST_NAME = "replyToFirstName";
	public static final String TAG_REPLYTO_LAST_NAME = "replyToLastName";
	
	//Attributes
	public static final String ATTR_REL = "rel";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_HREF = "href";
	public static final String ATTR_CODE = "code";
	public static final String ATTR_ACTION = "action";
	public static final String ATTR_NUM_IMAGES = "num_images";

	public Drawable waterMarkDrawable;
	public Drawable drawable;
	public String title = "";
	public String seo = "";
	public int totalGroupCount = 0;
	public int totalCount = 0;
	
	public Vector<Hashtable> links = new Vector<Hashtable>();
	public Hashtable<String, String> link = new Hashtable<String, String>();
	public Hashtable<String, String> tracking = new Hashtable<String, String>();
	public Vector<Entry> entry = new Vector<Entry>();
	//public Vector<ReportedByUser> ReportedByUser = new Vector<ReportedByUser>();
	//Community Chat
	public int groupId = 0;
	public String showModerationAlert = "";
	public int isOwner = 0;
	public int isAdmin = 0;
	public String searchMemberUrl = "";
	public String communityUrl = "";
	public String profileUrl = "";
	public String socketedMessageUrl = "";
	public String nexturl = "";
	public String topMessageId = "";
	public String socketUrl = "";
	public String waterMark = "";
	public String waterMarkWide = "";
	public String adminState = "";
	public String featured = "";
//	public String nexturl ;
	public String prevurl = ""; 
	
	public static final class ReportedByUser implements Serializable{
		
		public String userId = "";
		public String userName	 = "";
		public String firstName = "";
		public String lastName = "";
	}
	
	public  static final class Entry implements Serializable{
		//My Communities
		
//		rt:activeMembers
		public String joinOrLeave = "";
		public int groupId = 0;
		public String groupName = "";
		public String tags = "";
		public int reportCount = 0;
		public String reportMessageUrl = "";
		public String category = "";
		public String description = "";
		public String moderated = "";
		public String autoAcceptUser = "";
		public String publicCommunity = "";
		public String lastUpdated = "";
		public String createdOn = "";
		public int noOfMember = 0;
		public int noOfMessage = 0;
		public int ownerId = 0;
		public String welcomeMsgId = "";
		public String msgId = "";
		public String vendorName = "";
		public int noOfOnlineMember = 0;
		public int commonFriendCount = 0;
		public int activeMember = 0;
		public String memberPermission = "";
		public int admin = 0;
		public String featured = "";
		public String thumbUrl = "";
		public String messageUrl = "";
		public String profileUrl = "";
		public String searchMemberUrl = "";
		public String browseMsgUrl = "";
		public String adminState = "";
		public String pushNotif = "";
		public int newMessageCount = 0;
		//Recommended Communities
		public String displayName = "";
		public int count = 0;
		public String type = "";
		public String url = "";
		//Pending Request
		public String userName = "";
		public String genderType = "";
		public int userId;
		public String timeOfRequest = "";
		//userName->buttonName->attrs
		public Hashtable<String, Hashtable<String, Hashtable<String, String>>> buttons = new Hashtable<String, Hashtable<String, Hashtable<String, String>>>();
		//Community Chat
		public String messageId = "";
		public String parentMessageId = "";
		public int senderId = 0;
		public String senderName = "";
		public String messageState = "";
		public String drmFlags = "";
		public String createdDate = "";
		public String messageText = "";
		public Drawable imgDrawable;
		public String imgThumbUrl = "";
		public String imgUrl = "";
		public Drawable vidDrawable;
		public String vidThumbUrl = "";
		public String vidUrl = "";
		public String audioUrl = "";
		public Hashtable<String, String> imgThumbUrlAttrs = new Hashtable<String, String>();
		//Community Members
		public String firstName = "";
		public String lastName = "";
		public String userStatus = "";
		public String replyTo = "";
		public String replyToFirstName = "";
		public String replyToLastName = "";
		public Vector media =null ;
//		public String profileUrl = "";
//		public String searchMemberUrl = "";
		public String ownerUsername = "";
		public String ownerProfileUrl = "";
		public String ownerDisplayName = "";
		public String ownerThumbUrl = "";
		
		//Properties for Audio and Autoplay
		public boolean isAutoPlay;
		public boolean isPlaying;
		public boolean isVideoDownloading;
		public boolean isMediaUploading;
		public int playProgress;
		public boolean isSelected;
		//MANOJ SINGH 
		//Date 2-11-2015
		public boolean notSpam;
		public String reportedBy = "";
		public Vector<ReportedByUser> ReportedByUser;
		
	}

	public void copyTo(CommunityFeed parent) {
		this.communityUrl = parent.communityUrl;
		this.drawable = parent.drawable;
		this.entry.addAll(parent.entry);
		this.groupId = parent.groupId;
		this.isAdmin = parent.isAdmin;
		this.isOwner = parent.isOwner;
		this.link.putAll(parent.link);
		this.nexturl = parent.nexturl;
		this.adminState = parent.adminState;
		this.topMessageId = parent.topMessageId;
		this.profileUrl = parent.profileUrl;
		this.searchMemberUrl = parent.searchMemberUrl;
		this.showModerationAlert = parent.showModerationAlert;
		this.socketedMessageUrl = parent.socketedMessageUrl;
		this.socketUrl = parent.socketUrl;
		this.title = parent.title;
		this.totalCount = parent.totalCount;
		this.totalGroupCount = parent.totalGroupCount;
		this.tracking.putAll(parent.tracking);
		this.waterMarkDrawable = parent.waterMarkDrawable;
	}

	public static String getAttrNumImages(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_NUM_IMAGES);
		if (s != null) {
			return s;
		}
		return "NA";
	}

	public static String getAttrRel(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_REL);
		if (s != null) {
			return s;
		}
		return "NA";
	}
	public static String getAttrRel(Vector<Hashtable> links, String value) {
		for (int i = 0; i < links.size(); i++) {
			Hashtable<String, String> temp = (Hashtable<String, String>)links.get(i);
			Enumeration<String> enumeration = temp.keys();
			boolean t = false ;
			String href = null;
			while(enumeration.hasMoreElements())
			{
				String s = enumeration.nextElement() ;//key  like rel
				if(s.equalsIgnoreCase(ATTR_REL))
				{
					String v = temp.get(s) ;
					if(v.equalsIgnoreCase(value)){
//						t = true ;
						return temp.get(ATTR_HREF) ;
//						return s;
					}
				}
//				if(s.equalsIgnoreCase(value)){
//					t = true ;
////					return s;
//				}
//				if(s.equalsIgnoreCase(ATTR_HREF)){
//					href = s ;
//				}
//				if(t && href != null )
//					return s;
//				break;
			}
		}
		return "NA";
	}

	public static String getAttrAction(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_ACTION);
		if (s != null) {
			return s;
		}
		return "NA";
	}

	public static String getAttrType(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_TYPE);
		if (s != null) {
			return s;
		}
		return "NA";
	}

	public static String getAttrHref(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_HREF);
		if (s != null) {
			return s;
		}
		return "NA";
	}

	public static String getAttrCode(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_CODE);
		if (s != null) {
			return s;
		}
		return "NA";
	}
}