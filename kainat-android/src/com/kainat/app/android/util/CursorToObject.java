 package com.kainat.app.android.util;
 
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.FeaturedUserBean;
import com.kainat.app.android.bean.GroupEventBean;
import com.kainat.app.android.bean.LandingPageBean;
import com.kainat.app.android.bean.MediaLikeDislike;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.bean.ParticipantInfo;
import com.kainat.app.android.helper.CommentTable;
import com.kainat.app.android.helper.CommunityMessageTable;
import com.kainat.app.android.helper.CommunityTable;
import com.kainat.app.android.helper.FeatureUserTable;
import com.kainat.app.android.helper.GroupEventTable;
import com.kainat.app.android.helper.LandingPageTable;
import com.kainat.app.android.helper.MediaLikeUserTable;
import com.kainat.app.android.helper.MediaTable;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.util.Feed.Entry;

public class CursorToObject {
	
	private static boolean log = false; 
	public static  LandingPageBean activityObject(Cursor cursor){
		LandingPageBean landingPageBean = new LandingPageBean() ;
		landingPageBean.parent_id=cursor.getString(cursor.getColumnIndex(LandingPageTable.PARENT_ID));
		landingPageBean.story_id=cursor.getString(cursor.getColumnIndex(LandingPageTable.STORY_ID));
		landingPageBean.tracking=cursor.getString(cursor.getColumnIndex(LandingPageTable.TRACKING));
		landingPageBean.sue=cursor.getString(cursor.getColumnIndex(LandingPageTable.SUE));
		landingPageBean.title=cursor.getString(cursor.getColumnIndex(LandingPageTable.TITLE));
		landingPageBean.story_type=cursor.getString(cursor.getColumnIndex(LandingPageTable.TYPE));
		landingPageBean.image_content_thumb_urls=cursor.getString(cursor.getColumnIndex(LandingPageTable.IMAGE_CONTENT_THUMB_URLS));
		landingPageBean.image_content_urls=cursor.getString(cursor.getColumnIndex(LandingPageTable.IMAGE_CONTENT_URLS));
		landingPageBean.image_content_click_action=cursor.getString(cursor.getColumnIndex(LandingPageTable.IMAGE_CONTENT_CLICK_URL));		
		landingPageBean.desc=cursor.getString(cursor.getColumnIndex(LandingPageTable.DESC));
		landingPageBean.descOri=cursor.getString(cursor.getColumnIndex(LandingPageTable.DESC_ORI));
		landingPageBean.published=cursor.getString(cursor.getColumnIndex(LandingPageTable.PUBLISHED));
		landingPageBean.like=cursor.getString(cursor.getColumnIndex(LandingPageTable.LIKE));
		landingPageBean.dislike=cursor.getString(cursor.getColumnIndex(LandingPageTable.DISLIKE));
		landingPageBean.userthumb_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.USER_THUMB_URL));		
		landingPageBean.comment_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.COMMENT_URL));
		landingPageBean.share_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.SHARE_URL));
		landingPageBean.prev_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.PREV_URL));
		landingPageBean.next_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.NEXT_URL));
		landingPageBean.firstname=cursor.getString(cursor.getColumnIndex(LandingPageTable.FIRSTNAME));
		landingPageBean.lastname=cursor.getString(cursor.getColumnIndex(LandingPageTable.LASTNAME));
		landingPageBean.username=cursor.getString(cursor.getColumnIndex(LandingPageTable.USERNAME));		
		landingPageBean.message_state=cursor.getString(cursor.getColumnIndex(LandingPageTable.MESSAGE_STATE));
		landingPageBean.drm=cursor.getString(cursor.getColumnIndex(LandingPageTable.DRM));
		landingPageBean.action=cursor.getString(cursor.getColumnIndex(LandingPageTable.ACTION));
		landingPageBean.loginUser=cursor.getString(cursor.getColumnIndex(LandingPageTable.LOGIN_USER));
		landingPageBean.tab=cursor.getString(cursor.getColumnIndex(LandingPageTable.TAB));
		landingPageBean.desc2=cursor.getString(cursor.getColumnIndex(LandingPageTable.DESC2));
		landingPageBean.video_content_thumb_urls=cursor.getString(cursor.getColumnIndex(LandingPageTable.VIDEO_CONTENT_THUMB_URLS));
		landingPageBean.video_content_urls=cursor.getString(cursor.getColumnIndex(LandingPageTable.VIDEO_CONTENT_URLS));
		landingPageBean.video_content_click_action=cursor.getString(cursor.getColumnIndex(LandingPageTable.VIDEO_CONTENT_CLICK_URL));
		landingPageBean.voice_content_thumb_urls=cursor.getString(cursor.getColumnIndex(LandingPageTable.VOICE_CONTENT_THUMB_URLS));		
		landingPageBean.voice_content_urls=cursor.getString(cursor.getColumnIndex(LandingPageTable.VOICE_CONTENT_URLS));
		landingPageBean.voice_content_click_action=cursor.getString(cursor.getColumnIndex(LandingPageTable.VOICE_CONTENT_CLICK_URL));
		landingPageBean.other_user_thumb_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.OTHER_USER_THUMB_URL));
		landingPageBean.other_user_firstname=cursor.getString(cursor.getColumnIndex(LandingPageTable.OTHER_USER_FIRSTNAME));
		landingPageBean.other_user_lastname=cursor.getString(cursor.getColumnIndex(LandingPageTable.OTHER_USER_LASTNAME));
		landingPageBean.other_user_username=cursor.getString(cursor.getColumnIndex(LandingPageTable.OTHER_USER_USERNAME));
		landingPageBean.other_user_profile_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.OTHER_USER_PROFILE_URL));
		landingPageBean.userthumb_profile_url=cursor.getString(cursor.getColumnIndex(LandingPageTable.USERTHUMB_PROFILE_URL));
		landingPageBean.mediaText=cursor.getString(cursor.getColumnIndex(LandingPageTable.MEDIA_TEXT));
		landingPageBean.direction=cursor.getInt(cursor.getColumnIndex(LandingPageTable.DIRECTION));
		landingPageBean.insertDate=cursor.getLong(cursor.getColumnIndex(LandingPageTable.INSERT_TIME));
		landingPageBean.opType=cursor.getInt(cursor.getColumnIndex(LandingPageTable.OP_TYPE));
		
		return landingPageBean;
	}

	public static  Feed.Entry mediaEntryObject(Cursor cursor){
		Feed.Entry entry = new Feed().new Entry();
//		entry.content.put(Feed.ATTR_SRC,"http://distilleryimage6.instagram.com/05971f645e8511e1b9f1123138140926_7.jpg" /*cursor.getString(cursor.getColumnIndex(MediaTable.CONTENT_PICTURE))*/);
		
		entry.content.put(Feed.ATTR_SRC,cursor.getString(cursor.getColumnIndex(MediaTable.CONTENT_PICTURE)));
		entry.published = cursor.getString(cursor.getColumnIndex(MediaTable.PUBLISHED));
		entry.author.name = cursor.getString(cursor.getColumnIndex(MediaTable.AUTHOR_NAME));
		entry.author.uri = cursor.getString(cursor.getColumnIndex(MediaTable.AUTHOR_URI));
		entry.author.firstName = cursor.getString(cursor.getColumnIndex(MediaTable.AUTHOR_FIRSTNAME));
		entry.author.lastName = cursor.getString(cursor.getColumnIndex(MediaTable.AUTHOR_LASTNAME));
		
		
		entry.rt_voting.put(Feed.ATTR_VOTECOUNT, cursor.getString(cursor.getColumnIndex(MediaTable.VOTE_UP_COUNT)));
		
		entry.rt_voting.put(Feed.ATTR_VOTEUPCOUNT, cursor.getString(cursor.getColumnIndex(MediaTable.VOTE_UP_COUNT)));
		entry.rt_voting.put(Feed.ATTR_VOTEDOWNCOUNT, cursor.getString(cursor.getColumnIndex(MediaTable.VOTE_DOWN_COUNT)));
		
		
		entry.rt_statistics.put(Feed.ATTR_VIEWCOUNT, cursor.getString(cursor.getColumnIndex(MediaTable.CONTENT_VIEW)));
		
		entry.comment_count = cursor.getString(cursor.getColumnIndex(MediaTable.COMMENT_COUNT));
		entry.mediastatsurl = cursor.getString(cursor.getColumnIndex(MediaTable.MEDIASTATSURL));
		entry.fburl = cursor.getString(cursor.getColumnIndex(MediaTable.FBURL));
		entry.comment_url = cursor.getString(cursor.getColumnIndex(MediaTable.COMMENT_URL));
		entry.location = cursor.getString(cursor.getColumnIndex(MediaTable.LOCATION));
		entry.title = cursor.getString(cursor.getColumnIndex(MediaTable.TITLE));
		Hashtable<String, String> temp = new Hashtable<String, String>();
		String attr = cursor.getString(cursor.getColumnIndex(MediaTable.CATEGORY)); 
		String srr[] = Utilities.split(new StringBuffer(attr), Constants.ROW_SEPRETOR);
		
//		 <category scheme="http://schemas.rocketalk.com/2010#tags.cat" term="Main"/>
//		 02-20 17:06:53.496: I/System.out(31812):         <category scheme="http://schemas.rocketalk.com/2010#tags.cat" term="zindagi"/>
//		 02-20 17:06:53.496: I/System.out(31812):         <category scheme="http://schemas.rocketalk.com/2010#tags.cat" term="saath"/>
//		 [http://schemas.rocketalk.com/2010#tags.cat, Main, NA]
		for (int i = 0; i < srr.length; i++) {
			temp = new Hashtable<String, String>();
			String arr[] = Utilities.split(new StringBuffer(srr[i]), Constants.COL_SEPRETOR);
			if(arr.length>=1)
			temp.put("scheme", arr[0]);
			if(arr.length>=2)
			temp.put("term", arr[1]);
			if(arr.length>=3)
			temp.put("label", arr[2]);
//			System.out.println("-----------entry CATEGORY :"+arr);
			entry.category.add(temp);
			//feed.category.put(attributes.getLocalName(i).trim().toLowerCase(), value);
		}
		
		temp = new Hashtable<String, String>();
		try{
		attr = cursor.getString(cursor.getColumnIndex(MediaTable.LINK_MEDIA)); 
		srr = Utilities.split(new StringBuffer(attr), Constants.ROW_SEPRETOR);
		Vector mediaUrl = new Vector() ;
		for (int i = 0; i < srr.length; i++) {
//			System.out.println("-----------entry LINK_MEDIA :"+srr[i]);
			String arr[] = Utilities.split(new StringBuffer(srr[i]), Constants.COL_SEPRETOR);
			if(arr.length>=1)
				mediaUrl.add(arr[0]);
			if(arr.length>=2)
				mediaUrl.add(arr[1]);
			if(arr.length>=3)
				mediaUrl.add(arr[2]);
			if(arr.length>=4)
				mediaUrl.add(arr[3]);		
		}
		entry.media = mediaUrl ;
		}catch (Exception e) {
			// TODO: handle exception
		}
//		entry.link.add(temp);
		attr = cursor.getString(cursor.getColumnIndex(MediaTable.LINK_OTHER)); 
		srr = Utilities.split(new StringBuffer(attr), Constants.ROW_SEPRETOR);
		for (int i = 0; i < srr.length; i++) {
			temp = new Hashtable<String, String>();
//			System.out.println("-----------entry LINK_OTHER :"+srr[i]);
			String arr[] = Utilities.split(new StringBuffer(srr[i]), Constants.COL_SEPRETOR);
			if(arr.length>=1)
			temp.put("rel", arr[0]);
			if(arr.length>=2)
			temp.put("type", arr[1]);
			if(arr.length>=3)
			temp.put("href", arr[2]);	
			entry.link.add(temp);
		}
//		System.out.println("--------------community :  "+entry.comment_url);
		return entry;
	}
	public static  CommunityFeed.Entry communityEntryObject(Cursor cursor){
		CommunityFeed.Entry entry = new CommunityFeed.Entry();
//		entry.c = cursor.getString(cursor.getColumnIndex(CommunityTable.COLUMN_ID));
//		entry.createdOn = cursor.getString(cursor.getColumnIndex(CommunityTable.PARENT_ID));
		entry.groupId = cursor.getInt(cursor.getColumnIndex(CommunityTable.GROUPID));
//		entry.createdOn = cursor.getString(cursor.getColumnIndex(CommunityTable.TRACKING));
//		entry.se = cursor.getString(cursor.getColumnIndex(CommunityTable.SUE));
		entry.groupName = cursor.getString(cursor.getColumnIndex(CommunityTable.GROUP_NAME));
		entry.type = cursor.getString(cursor.getColumnIndex(CommunityTable.TYPE));
		entry.category = cursor.getString(cursor.getColumnIndex(CommunityTable.CATEGORY));
		entry.description = cursor.getString(cursor.getColumnIndex(CommunityTable.DESCRIPTION));
		entry.moderated = cursor.getString(cursor.getColumnIndex(CommunityTable.IS_MODERATED));
		entry.autoAcceptUser = cursor.getString(cursor.getColumnIndex(CommunityTable.AUTOA_CCEPTUSER));
		entry.publicCommunity = cursor.getString(cursor.getColumnIndex(CommunityTable.IS_PUBLIC));
		entry.lastUpdated = cursor.getString(cursor.getColumnIndex(CommunityTable.LAST_UPDATED));
		entry.createdOn = cursor.getString(cursor.getColumnIndex(CommunityTable.TIME_OF_CREATION));
		entry.noOfMember = cursor.getInt(cursor.getColumnIndex(CommunityTable.NUMBER_OF_MEMBERS));
		entry.noOfMessage = cursor.getInt(cursor.getColumnIndex(CommunityTable.NUMBER_OF_MESSAGES));
		entry.ownerId = cursor.getInt(cursor.getColumnIndex(CommunityTable.OWNERUSER_ID));
		entry.welcomeMsgId = cursor.getString(cursor.getColumnIndex(CommunityTable.WELCOME_MSG_ID));
		entry.msgId = cursor.getString(cursor.getColumnIndex(CommunityTable.MSG_ID));
		entry.vendorName = cursor.getString(cursor.getColumnIndex(CommunityTable.VENDOR_NAME));
		entry.noOfOnlineMember = cursor.getInt(cursor.getColumnIndex(CommunityTable.NUMBEROF_ONLINE_MEMBERS));
		entry.commonFriendCount = cursor.getInt(cursor.getColumnIndex(CommunityTable.COMM_ON_FRIENDS_COUNT));
		entry.activeMember = cursor.getInt(cursor.getColumnIndex(CommunityTable.ACTIVE_MEMBERS));
		entry.memberPermission = cursor.getString(cursor.getColumnIndex(CommunityTable.MEMBER_PERMISSION));		
		entry.admin = cursor.getInt(cursor.getColumnIndex(CommunityTable.IS_ADMIN));
		entry.thumbUrl = cursor.getString(cursor.getColumnIndex(CommunityTable.THUMB_URL));
		entry.messageUrl = cursor.getString(cursor.getColumnIndex(CommunityTable.MESSAGE_URL));
		entry.profileUrl = cursor.getString(cursor.getColumnIndex(CommunityTable.PROFILE_URL));
		entry.searchMemberUrl = cursor.getString(cursor.getColumnIndex(CommunityTable.SEARCH_MEMBER_URL));
		
		entry.ownerDisplayName = cursor.getString(cursor.getColumnIndex(CommunityTable.OWNER_DISPLAY_NAME));
		entry.ownerProfileUrl = cursor.getString(cursor.getColumnIndex(CommunityTable.OWNER_PROFILE_URL));
		entry.ownerThumbUrl = cursor.getString(cursor.getColumnIndex(CommunityTable.OWNER_THUMB_URL));
		entry.ownerUsername = cursor.getString(cursor.getColumnIndex(CommunityTable.OWNER_USERNAME));
		entry.newMessageCount = cursor.getInt(cursor.getColumnIndex(CommunityTable.GROUP_NEW_MSG_COUNT));
		entry.adminState = cursor.getString(cursor.getColumnIndex(CommunityTable.ADMIN_STATE));
		entry.featured = cursor.getString(cursor.getColumnIndex(CommunityTable.FEATURED));
//		System.out.println("--------------community :  "+entry.thumbUrl);
		return entry;
	}

	public static  FeaturedUserBean featuredUserObject(Cursor cursor){
		FeaturedUserBean featuredUserBean = new FeaturedUserBean();
//		landingPageBean.parent_id=cursor.getString(cursor.getColumnIndex(LandingPageTable.PARENT_ID));
		featuredUserBean.communitiesMember =cursor.getString(cursor.getColumnIndex(FeatureUserTable.COMMUNITIES_MEMBER));
		featuredUserBean.displayName =cursor.getString(cursor.getColumnIndex(FeatureUserTable.DISPLAY_NAME));
		featuredUserBean.id =cursor.getString(cursor.getColumnIndex(FeatureUserTable.STORY_ID));
		featuredUserBean.mediaFollowers =cursor.getString(cursor.getColumnIndex(FeatureUserTable.MEDIA_FOLLOWERS));
		featuredUserBean.mediaPosts =cursor.getString(cursor.getColumnIndex(FeatureUserTable.MEDIA_POSTS));
		featuredUserBean.profileUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.PROFIL_EURL));
		featuredUserBean.thumbUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.THUMB_URL));
		featuredUserBean.title =cursor.getString(cursor.getColumnIndex(FeatureUserTable.TITLE));

		featuredUserBean.nextUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.NEXT_URL));
		featuredUserBean.prevUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.PREV_URL));
		featuredUserBean.tracking =cursor.getString(cursor.getColumnIndex(FeatureUserTable.TRACKING));
		
//		System.out.println("--------------featuredUserBean :  "+featuredUserBean.toString());
		return featuredUserBean;
	}
	public static  GroupEventBean groupEventObject(Cursor cursor){
		GroupEventBean groupEventBean = new GroupEventBean();
//		landingPageBean.parent_id=cursor.getString(cursor.getColumnIndex(LandingPageTable.PARENT_ID));
		groupEventBean.id =cursor.getString(cursor.getColumnIndex(GroupEventTable.ID));
		groupEventBean.groupname =cursor.getString(cursor.getColumnIndex(GroupEventTable.GROUP_NAME));
		groupEventBean.description =cursor.getString(cursor.getColumnIndex(GroupEventTable.DESCRIPTION));
		groupEventBean.endDate =cursor.getString(cursor.getColumnIndex(GroupEventTable.END_DATE));
		groupEventBean.audioUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.AUDIO_URL));
		groupEventBean.comProfileUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.COM_PROFILE_URL));
		groupEventBean.hosterDisplayName =cursor.getString(cursor.getColumnIndex(GroupEventTable.HOSTER_DISPLAY_NAME));
		groupEventBean.hosterProfileUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.HOSTER_PROFILE_URL));
		groupEventBean.hosterThumbUr =cursor.getString(cursor.getColumnIndex(GroupEventTable.HOSTER_THUMBUR));
		groupEventBean.thumbPictureUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.THUMB_PICTURE_URL));
		groupEventBean.hosterUsername =cursor.getString(cursor.getColumnIndex(GroupEventTable.HOSTER_USER_NAME));		
		groupEventBean.startDate =cursor.getString(cursor.getColumnIndex(GroupEventTable.START_DATE));
		groupEventBean.pictureUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.PICTURE_URL));
		groupEventBean.startDate =cursor.getString(cursor.getColumnIndex(GroupEventTable.START_DATE));
		groupEventBean.title =cursor.getString(cursor.getColumnIndex(GroupEventTable.TITLE));
		groupEventBean.nextUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.NEXT_URL));
		groupEventBean.prevUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.PREV_URL));
		groupEventBean.insertTime =cursor.getLong(cursor.getColumnIndex(GroupEventTable.INSERT_TIME));
		
		groupEventBean.messageInfo1 =cursor.getString(cursor.getColumnIndex(GroupEventTable.MESSAGE_INFO1));
		groupEventBean.messageInfo2 =cursor.getString(cursor.getColumnIndex(GroupEventTable.MESSAGE_INFO2));
		groupEventBean.messageAlert1 =cursor.getString(cursor.getColumnIndex(GroupEventTable.MESSAGE_ALERT1));
		groupEventBean.messageAlert2 =cursor.getString(cursor.getColumnIndex(GroupEventTable.MESSAGE_ALERT2));
		groupEventBean.button_name =cursor.getString(cursor.getColumnIndex(GroupEventTable.BUTTON_NAME));
		groupEventBean.button_action =cursor.getString(cursor.getColumnIndex(GroupEventTable.BUTTON_ACTION));
		
		groupEventBean.ownerDisplayName =cursor.getString(cursor.getColumnIndex(GroupEventTable.OWNER_DISPLAY_NAME));
		groupEventBean.ownerProfileUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.OWNERP_ROFILE_URL));
		groupEventBean.ownerThumbUrl =cursor.getString(cursor.getColumnIndex(GroupEventTable.OWNER_THUMB_URL));
		groupEventBean.ownerUsername =cursor.getString(cursor.getColumnIndex(GroupEventTable.OWNER_USER_NAME));
		
//		System.out.println("--------------groupEventBean :  "+groupEventBean.toString());
		return groupEventBean;
	}
	
	public static  Feed.Entry mediaCommentEntryObject(Cursor cursor){
		Feed.Entry entry = new Feed().new Entry();
		entry.content.put(Feed.ATTR_SRC,"http://distilleryimage6.instagram.com/05971f645e8511e1b9f1123138140926_7.jpg" /*cursor.getString(cursor.getColumnIndex(MediaTable.CONTENT_PICTURE))*/);
		entry.parent_id = cursor.getString(cursor.getColumnIndex(CommentTable.PARENT_ID));
		entry.id = cursor.getString(cursor.getColumnIndex(CommentTable.STORY_ID));
		entry.text = cursor.getString(cursor.getColumnIndex(CommentTable.TEXT));
		entry.published = cursor.getString(cursor.getColumnIndex(CommentTable.PUBLISHED));
		entry.username = cursor.getString(cursor.getColumnIndex(CommentTable.USERNAME));
		entry.firstname = cursor.getString(cursor.getColumnIndex(CommentTable.FIRSTNAME));
		entry.lastname = cursor.getString(cursor.getColumnIndex(CommentTable.LASTNAME));
		entry.video_url = cursor.getString(cursor.getColumnIndex(CommentTable.VID_URL));
		entry.likes = cursor.getString(cursor.getColumnIndex(CommentTable.LIKES));
		entry.dislikes = cursor.getString(cursor.getColumnIndex(CommentTable.DISLIKES));
		entry.like_value = cursor.getString(cursor.getColumnIndex(CommentTable.LIKES_VALUE));
		entry.next = cursor.getString(cursor.getColumnIndex(CommentTable.NEXT_URL));
		entry.prev = cursor.getString(cursor.getColumnIndex(CommentTable.PREV_URL));
		entry.comment_count = cursor.getString(cursor.getColumnIndex(CommentTable.TOTAL_COMMENT));
		
		Hashtable<String, String> temp = new Hashtable<String, String>();
		temp = new Hashtable<String, String>();
		try{
		String attr = cursor.getString(cursor.getColumnIndex(CommentTable.LINK_MEDIA)); 
		String []srr = Utilities.split(new StringBuffer(attr), Constants.ROW_SEPRETOR);
		Vector mediaUrl = new Vector() ;
		for (int i = 0; i < srr.length; i++) {
//			System.out.println("-----------entry LINK_MEDIA :"+srr[i]);
			String arr[] = Utilities.split(new StringBuffer(srr[i]), Constants.COL_SEPRETOR);
			if(arr.length>=1)
				mediaUrl.add(arr[0]);
			if(arr.length>=2)
				mediaUrl.add(arr[1]);
			if(arr.length>=3)
				mediaUrl.add(arr[2]);
			if(arr.length>=4)
				mediaUrl.add(arr[3]);		
		}
		entry.media = mediaUrl ;
		}catch (Exception e) {
//			e.printStackTrace();
		}
//		String attr = cursor.getString(cursor.getColumnIndex(MediaTable.LINK_OTHER)); 
//		String []srr = Utilities.split(new StringBuffer(attr), Constants.ROW_SEPRETOR);
//		for (int i = 0; i < srr.length; i++) {
//			temp = new Hashtable<String, String>();
//			System.out.println("-----------entry LINK_OTHER :"+srr[i]);
//			String arr[] = Utilities.split(new StringBuffer(srr[i]), Constants.COL_SEPRETOR);
//			if(arr.length>=1)
//			temp.put("rel", arr[0]);
//			if(arr.length>=2)
//			temp.put("type", arr[1]);
//			if(arr.length>=3)
//			temp.put("href", arr[2]);	
//			entry.link.add(temp);
//		}
		
		return entry;
	}
	
	
	//MANOJ SINGH
	// DATE 23-06-2015
	//
	
	public static  CommunityFeed ChannelCommentEntryObject(Cursor cursor){
		CommunityFeed feed = new CommunityFeed();
		feed.socketUrl			= "";
		feed.nexturl  			= "";
		feed.socketedMessageUrl = "";
		if(cursor!= null)
		{ 
			cursor.moveToFirst();
			//feed.entry = null;
			String MediaStr;
			for(int i=0;i<cursor.getCount();i++){
				final CommunityFeed.Entry iCentry = new CommunityFeed.Entry();
				iCentry.groupId    = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CommunityMessageTable.GROUPID)));
				iCentry.groupName  = cursor.getString(cursor.getColumnIndex(CommunityMessageTable.GROUP_NAME));
				//iCentry.ISOWNER
				iCentry.adminState      =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.IS_ADMIN));
				iCentry.msgId 	      =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MSG_ID));
				iCentry.messageId 	      =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MSG_ID));
				iCentry.parentMessageId =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.PARENT_ID));
				iCentry.senderId        =  Integer.parseInt(cursor.getString(cursor.getColumnIndex(CommunityMessageTable.SENDER_ID)));
				iCentry.senderName      =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.SENDER_NAME));
				iCentry.firstName		=  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.FIRST_NAME));
				iCentry.lastName        =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.LAST_NAME));
				iCentry.messageState    =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MSG_STATE));
				iCentry.drmFlags        =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.DRM_FLAGS));
				iCentry.createdDate     =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.CREATED_DATE));
				iCentry.messageText     =  cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MSG_TEXT));
				MediaStr 			    = cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MEDIA));
				if(cursor.getString(cursor.getColumnIndex(CommunityMessageTable.NEXTURL))!= null &&  !cursor.getString(cursor.getColumnIndex(CommunityMessageTable.NEXTURL)).equals(""))
				{
					feed.nexturl = cursor.getString(cursor.getColumnIndex(CommunityMessageTable.NEXTURL));
				}
				if(MediaStr!=null && MediaStr.contains("@@##@@##")){
				String[] array = MediaStr.split("@@##@@##"); 
				 Vector<String> vMedia = new Vector<String>(Arrays.asList(array));
				 iCentry.media = vMedia;
				}
				cursor.moveToNext();
				feed.entry.add(iCentry);
				/*
				 * 
				 * values.put(CommunityMessageTable.GROUPID, entry.groupId);//int
		values.put(CommunityMessageTable.GROUP_NAME, entry.groupName);//String
		values.put(CommunityMessageTable.IS_OWNER, "");//int
		values.put(CommunityMessageTable.IS_ADMIN, entry.adminState);//int
		values.put(CommunityMessageTable.MSG_ID, entry.msgId);//int
		values.put(CommunityMessageTable.PARENT_ID, entry.parentMessageId);//D
		values.put(CommunityMessageTable.SENDER_ID, entry.senderId);//String
		values.put(CommunityMessageTable.SENDER_NAME, entry.senderName);//String
		values.put(CommunityMessageTable.FIRST_NAME, entry.firstName);//String
		values.put(CommunityMessageTable.LAST_NAME, entry.lastName);//String
		values.put(CommunityMessageTable.MSG_STATE, entry.messageState);//String
		values.put(CommunityMessageTable.DRM_FLAGS, entry.drmFlags);//String
		values.put(CommunityMessageTable.CREATED_DATE, entry.createdDate);//String
		values.put(CommunityMessageTable.MSG_TEXT, entry.messageText);//String
		values.put(CommunityMessageTable.NEXTURL, NextUrl);//String
				 * 
				 * */
			}
		}
		return feed;
	}
	
	
	
	
	
	public static  MediaLikeDislike cursorToMediaLikeDislike(Cursor cursor){
		MediaLikeDislike mediaLikeDislike = new MediaLikeDislike();
		mediaLikeDislike.userId = cursor.getString(cursor.getColumnIndex(MediaLikeUserTable.USERID));
		mediaLikeDislike.userName = cursor.getString(cursor.getColumnIndex(MediaLikeUserTable.USERNAME));
		mediaLikeDislike.userDisplayName = cursor.getString(cursor.getColumnIndex(MediaLikeUserTable.USERDISPLAYNAME));
		mediaLikeDislike.userThumbUrl = cursor.getString(cursor.getColumnIndex(MediaLikeUserTable.USERTHUMBURL));
		mediaLikeDislike.nextUrl = cursor.getString(cursor.getColumnIndex(MediaLikeUserTable.NEXT_URL));
		
		return mediaLikeDislike;
	}
	
	public static  ConversationList  conversationListObject(Cursor cursor,Context context){
		ConversationList conversationList = new ConversationList();
//		landingPageBean.parent_id=cursor.getString(cursor.getColumnIndex(LandingPageTable.PARENT_ID));
		conversationList.unreadMessageCount =cursor.getInt(cursor.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT));
		conversationList.conversationId =cursor.getString(cursor.getColumnIndex(MessageConversationTable.CONVERSATION_ID));
		conversationList.source =cursor.getString(cursor.getColumnIndex(MessageConversationTable.SOURCE));
		conversationList.datetime =cursor.getString(cursor.getColumnIndex(MessageConversationTable.DATE_TIME));
		conversationList.inserTime=cursor.getLong(cursor.getColumnIndex(MessageTable.INSERT_TIME));
		
		conversationList.messageId =cursor.getString(cursor.getColumnIndex(MessageConversationTable.MESSAGE_ID));
		conversationList.mfuId=cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
		
		
		if(cursor.getColumnIndex(MessageConversationTable.CONTENT_BITMAP)!=-1)
		conversationList.contentBitMap =cursor.getString(cursor.getColumnIndex(MessageTable.CONTENT_BITMAP));
		
		conversationList.msgTxt =cursor.getString(cursor.getColumnIndex(MessageConversationTable.MSG_TXT));
		if(conversationList.msgTxt!=null && conversationList.msgTxt.length()>50)
			conversationList.msgTxt = conversationList.msgTxt.substring(0, 49);
		
		//Poonam Kumari has changed the subject to hi
				if(conversationList.msgTxt!=null)
					if(conversationList.msgTxt.indexOf("changed the subject")!= -1){
						String lan = KeyValue.getString(context, KeyValue.LANGUAGE);
						if(!lan.equalsIgnoreCase("en")){
							conversationList.msgTxt = context.getString(R.string.subject_changed) ;
						}
					}
				if(conversationList.msgTxt!=null)
					if(conversationList.msgTxt.indexOf("to the conversation")!= -1){
						String lan = KeyValue.getString(context, KeyValue.LANGUAGE);
						if(!lan.equalsIgnoreCase("en")){
							conversationList.msgTxt = context.getString(R.string.buddy_added) ;
						}
					}
				
		conversationList.subject =cursor.getString(cursor.getColumnIndex(MessageConversationTable.SUBJECT));
		conversationList.imageFileId =cursor.getString(cursor.getColumnIndex(MessageConversationTable.GROUP_PIC));
		conversationList.isGroup =cursor.getInt(cursor.getColumnIndex(MessageConversationTable.IS_GROUP));
		String s = cursor.getString(cursor.getColumnIndex(MessageConversationTable.PARTICIPANT_INFO)); 
		conversationList.isSelected =cursor.getInt(cursor.getColumnIndex(MessageConversationTable.ITEM_SELECTED));
		conversationList.isLeft =cursor.getInt(cursor.getColumnIndex(MessageConversationTable.IS_LEFT));
		conversationList.isNext =cursor.getInt(cursor.getColumnIndex(MessageConversationTable.IS_NEXT));
		conversationList.isNext =cursor.getInt(cursor.getColumnIndex(MessageTable.IS_NEXT));
		conversationList.drmFlags =cursor.getInt(cursor.getColumnIndex(MessageTable.DRM_FLAGS));
		conversationList.comments =cursor.getString(cursor.getColumnIndex(MessageTable.COMMENT));
		conversationList.notificationFlags =cursor.getInt(cursor.getColumnIndex(MessageTable.NOTIFICATION_FLAGS));
		conversationList.hasBeenViewed =cursor.getString(cursor.getColumnIndex(MessageTable.HAS_BEEN_VIEWED));
		conversationList.hasBeenViewedBySIP =cursor.getString(cursor.getColumnIndex(MessageTable.HAS_BEEN_VIEWED_BY_SIP));
		conversationList.isLastMessage =cursor.getString(cursor.getColumnIndex(MessageTable.IS_LAST_MESSAGE));
//		System.out.println("Feed Synch Discovery user---conversationList.isLastMessage------2-------"+conversationList.isLastMessage);
		
		conversationList.aniType =cursor.getString(cursor.getColumnIndex(MessageTable.ANI_TYPE));
		conversationList.aniValue =cursor.getString(cursor.getColumnIndex(MessageTable.ANI_VALUE));
		
		conversationList.voice_content_urls =cursor.getString(cursor.getColumnIndex(MessageTable.VOICE_CONTENT_URLS));
		
		conversationList.image_content_thumb_urls =cursor.getString(cursor.getColumnIndex(MessageTable.IMAGE_CONTENT_THUMB_URLS));
		conversationList.image_content_urls =cursor.getString(cursor.getColumnIndex(MessageTable.IMAGE_CONTENT_URLS));
		
		conversationList.video_content_thumb_urls =cursor.getString(cursor.getColumnIndex(MessageTable.VIDEO_CONTENT_THUMB_URLS));
		conversationList.video_content_urls =cursor.getString(cursor.getColumnIndex(MessageTable.VIDEO_CONTENT_URLS));
		
		if(s!=null){
			
			//04-21 00:50:29.664: I/System.out(18672): Rocketalk ------------false�achadha�Aman�Chadha�false����true�akm�akm�akm�

			if(s.indexOf(Constants.ROW_SEPRETOR+"false���")!= -1){
				s =s.replaceAll(Constants.ROW_SEPRETOR+"false���", "");
			}
//			if(log)
			{
//				System.out.println("Rocketalk ------------"+conversationList.conversationId);
//				System.out.println("Rocketalk ------------"+s);
			}
		String pi[] = Utilities.split(new StringBuffer(s), Constants.ROW_SEPRETOR) ;
		ParticipantInfo participantInfo = new ParticipantInfo() ;
		conversationList.participantInfo = new Vector<ParticipantInfo>() ;
		try
		{
			for (int i = 0; i < pi.length; i++) {
				participantInfo = new ParticipantInfo() ;
				String info[] = Utilities.split(new StringBuffer(pi[i]), Constants.COL_SEPRETOR) ;
				if(info!= null && info.length >1)
				{
					//false�danger�Dan�Ger�truedanger�danger�danger�
					participantInfo.isSender=Boolean.parseBoolean(info[0]);
//					System.out.println("Rocketalk ------------"+participantInfo.isSender);
					participantInfo.userName=info[1];
					if(info.length >=3)
					participantInfo.firstName=info[2];
					if(info.length >= 4)
					participantInfo.lastName=info[3];
				}
	
				conversationList.participantInfo.add(participantInfo);
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
//		featuredUserBean.displayName =cursor.getString(cursor.getColumnIndex(FeatureUserTable.DISPLAY_NAME));
//		featuredUserBean.id =cursor.getString(cursor.getColumnIndex(FeatureUserTable.STORY_ID));
//		featuredUserBean.mediaFollowers =cursor.getString(cursor.getColumnIndex(FeatureUserTable.MEDIA_FOLLOWERS));
//		featuredUserBean.mediaPosts =cursor.getString(cursor.getColumnIndex(FeatureUserTable.MEDIA_POSTS));
//		featuredUserBean.profileUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.PROFIL_EURL));
//		featuredUserBean.thumbUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.THUMB_URL));
//		featuredUserBean.title =cursor.getString(cursor.getColumnIndex(FeatureUserTable.TITLE));
//
//		featuredUserBean.nextUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.NEXT_URL));
//		featuredUserBean.prevUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.PREV_URL));
//		featuredUserBean.tracking =cursor.getString(cursor.getColumnIndex(FeatureUserTable.TRACKING));
		
//		System.out.println("--------------featuredUserBean :  "+conversationList.toString());
		return conversationList;
	}
	public static  Message  conversationObject(Cursor cursor,Context context){
		Message message = new Message();
//		landingPageBean.parent_id=cursor.getString(cursor.getColumnIndex(LandingPageTable.PARENT_ID));
//		message.unreadMessageCount =cursor.getInt(cursor.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT));
		message.conversationId =cursor.getString(cursor.getColumnIndex(MessageTable.CONVERSATION_ID));
		message.source =cursor.getString(cursor.getColumnIndex(MessageTable.SOURCE));
		message.datetime =cursor.getString(cursor.getColumnIndex(MessageTable.DATE_TIME));//2015-01-30T01:54:42-05:00
		message.inserTime =cursor.getLong(cursor.getColumnIndex(MessageTable.INSERT_TIME));
//		System.out.println("------------date conversationObject datetime : " + message.datetime);
//		 System.out.println("------------date conversationObject inserTime : " + message.inserTime);
		message.mfuId =cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
		message.messageType =cursor.getInt(cursor.getColumnIndex(MessageTable.MESSAGE_TYPE));
		message.contentBitMap =cursor.getString(cursor.getColumnIndex(MessageTable.CONTENT_BITMAP));
		message.aniType =cursor.getString(cursor.getColumnIndex(MessageTable.ANI_TYPE));
		message.aniValue =cursor.getString(cursor.getColumnIndex(MessageTable.ANI_VALUE));
		message.direction =cursor.getString(cursor.getColumnIndex(MessageTable.DIRECTION));
		
		message.sortTime =cursor.getLong(cursor.getColumnIndex(MessageTable.SORT_TIME));
		
		
		message.hasBeenViewed =cursor.getString(cursor.getColumnIndex(MessageTable.HAS_BEEN_VIEWED));
		message.hasBeenViewedBySIP =cursor.getString(cursor.getColumnIndex(MessageTable.HAS_BEEN_VIEWED_BY_SIP));
		
		message.isLeft =cursor.getInt(cursor.getColumnIndex(MessageTable.IS_LEFT));
		if(cursor.getColumnIndex(MessageTable.USMId)!=-1)
			message.usmId =cursor.getString(cursor.getColumnIndex(MessageTable.USMId));
		message.drmFlags =cursor.getInt(cursor.getColumnIndex(MessageTable.DRM_FLAGS));
		message.notificationFlags =cursor.getInt(cursor.getColumnIndex(MessageTable.NOTIFICATION_FLAGS));
		
//		if (message.contentBitMap==null || message.contentBitMap.indexOf("RTML")== -1)
		{
			if(cursor.getColumnIndex(MessageTable.MSG_TXT)!=-1)
				message.msgTxt =cursor.getString(cursor.getColumnIndex(MessageTable.MSG_TXT));
		}
		//Poonam Kumari has changed the subject to hi
		if(message.msgTxt!=null)
			if(message.msgTxt.indexOf("changed the subject")!= -1){
				String lan = KeyValue.getString(context, KeyValue.LANGUAGE);
				if(!lan.equalsIgnoreCase("en")){
					message.msgTxt = context.getString(R.string.subject_changed) ;
				}
			}
		
		if(message.msgTxt!=null)
			if(message.msgTxt.indexOf("to the conversation")!= -1){
				String lan = KeyValue.getString(context, KeyValue.LANGUAGE);
				if(!lan.equalsIgnoreCase("en")){
					message.msgTxt = context.getString(R.string.buddy_added) ;
				}
			}
		
		if(cursor.getColumnIndex(MessageTable.MESSAGE_ATTRIBUTE)!=-1)
		message.messageAttribute =cursor.getString(cursor.getColumnIndex(MessageTable.MESSAGE_ATTRIBUTE));
		message.messageId =cursor.getString(cursor.getColumnIndex(MessageTable.MESSAGE_ID));
		message.voice_content_urls =cursor.getString(cursor.getColumnIndex(MessageTable.VOICE_CONTENT_URLS));
		message.audio_download_statue =cursor.getInt(cursor.getColumnIndex(MessageTable.AUDIO_DOWNLOAD_STATUE));
//		message.video_download_statue =cursor.getInt(cursor.getColumnIndex(MessageTable.VIDEO_DOWNLOAD_STATUE));
		message.sending_state =cursor.getInt(cursor.getColumnIndex(MessageTable.SENDING_STATE));
		message.subject =cursor.getString(cursor.getColumnIndex(MessageTable.SUBJECT));
		message.imageFileId =cursor.getString(cursor.getColumnIndex(MessageTable.GROUP_PIC));
		message.tag =cursor.getString(cursor.getColumnIndex(MessageTable.TAG));
		message.isSelected =cursor.getInt(cursor.getColumnIndex(MessageTable.ITEM_SELECTED));
		message.isNext =cursor.getInt(cursor.getColumnIndex(MessageTable.IS_NEXT));
		message.more =cursor.getString(cursor.getColumnIndex(MessageTable.MORE));
		if(cursor.getColumnIndex(MessageTable.COMMENT)!=-1)
		message.comments =cursor.getString(cursor.getColumnIndex(MessageTable.COMMENT));
		
		message.image_content_thumb_urls =cursor.getString(cursor.getColumnIndex(MessageTable.IMAGE_CONTENT_THUMB_URLS));
		message.image_content_urls =cursor.getString(cursor.getColumnIndex(MessageTable.IMAGE_CONTENT_URLS));
		
//		if(message.image_content_thumb_urls!=null)
//			message.image_content_thumb_urls = message.image_content_thumb_urls.replace(".jpg", ".png");
//		
//		if(message.image_content_urls!=null)
//			message.image_content_urls = message.image_content_thumb_urls.replace(".jpg", ".png");
		
		
		if(cursor.getColumnIndex(MessageTable.AUDIO_ID)!=-1)
		message.voice_ID =cursor.getString(cursor.getColumnIndex(MessageTable.AUDIO_ID));
//		message.video_ID =cursor.getString(cursor.getColumnIndex(MessageTable.videoi));
		
		message.video_size =cursor.getString(cursor.getColumnIndex(MessageTable.VIDEO_SIZE));
		message.image_size =cursor.getString(cursor.getColumnIndex(MessageTable.IMAGE_SIZE));
		message.audio_size =cursor.getString(cursor.getColumnIndex(MessageTable.AUDIO_SIZE));
		message.video_content_thumb_urls =cursor.getString(cursor.getColumnIndex(MessageTable.VIDEO_CONTENT_THUMB_URLS));
		message.video_content_urls =cursor.getString(cursor.getColumnIndex(MessageTable.VIDEO_CONTENT_URLS));
		
//		if(message.image_content_thumb_urls!=null)
//			message.image_content_thumb_urls = message.image_content_thumb_urls.replace(".jpg", ".png");
//		
//		if(message.image_content_urls!=null)
//			message.image_content_urls = message.image_content_thumb_urls.replace(".jpg", ".png");
		
		
		String s = cursor.getString(cursor.getColumnIndex(MessageTable.PARTICIPANT_INFO)); 
		if(s!=null){
		String pi[] = Utilities.split(new StringBuffer(s), Constants.ROW_SEPRETOR) ;
		
//		System.out.println("------------message.video_size :"+message.video_size);
		ParticipantInfo participantInfo = new ParticipantInfo() ;
		message.participantInfo = new Vector<ParticipantInfo>() ;
		//sender username firstname lastname profileImageUrl profileUrl
		int i = 0;
		try{
			
		for ( i = 0; i < pi.length; i++) {
			try{
			 participantInfo = new ParticipantInfo() ;
			String info[] = Utilities.split(new StringBuffer(pi[i]), Constants.COL_SEPRETOR) ;
			participantInfo.isSender=Boolean.parseBoolean(info[0]);
			participantInfo.userName=info[1];
			if(info.length >= 3)
			participantInfo.firstName=info[2];
			if(info.length >= 4)
			participantInfo.lastName=info[3];
//			participantInfo.profileImageUrl=info[4];
//			participantInfo.profileUrl=info[5];
			message.participantInfo.add(participantInfo);
			}catch (Exception e) {
				
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
//			if(log)
//			System.out.println(pi[i]);
			message.participantInfo.add(participantInfo);
		}
		}
//		Feed Synch --------------message values----------
//		featuredUserBean.displayName =cursor.getString(cursor.getColumnIndex(FeatureUserTable.DISPLAY_NAME));
//		featuredUserBean.id =cursor.getString(cursor.getColumnIndex(FeatureUserTable.STORY_ID));
//		featuredUserBean.mediaFollowers =cursor.getString(cursor.getColumnIndex(FeatureUserTable.MEDIA_FOLLOWERS));
//		featuredUserBean.mediaPosts =cursor.getString(cursor.getColumnIndex(FeatureUserTable.MEDIA_POSTS));
//		featuredUserBean.profileUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.PROFIL_EURL));
//		featuredUserBean.thumbUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.THUMB_URL));
//		featuredUserBean.title =cursor.getString(cursor.getColumnIndex(FeatureUserTable.TITLE));
//
//		featuredUserBean.nextUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.NEXT_URL));
//		featuredUserBean.prevUrl =cursor.getString(cursor.getColumnIndex(FeatureUserTable.PREV_URL));
//		featuredUserBean.tracking =cursor.getString(cursor.getColumnIndex(FeatureUserTable.TRACKING));
//		System.out.println("--------------message.participantInfo :  "+s);
//		System.out.println("--------------message.participantInfo :  "+message.participantInfo.toString());
		return message;
	}
}
