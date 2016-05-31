package com.kainat.app.android.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.kainat.app.android.bean.ChannelPostAPI;
import com.kainat.app.android.bean.ChannelPostAPIGetFeed;
import com.kainat.app.android.bean.MediaContentUrl;
import com.kainat.app.android.bean.UserAPI;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.CommunityFeed.Entry;

public class CommunityChatDB {
	String TAG = "CommunityChatDB";
	boolean log = false;
	Context context;
	Cursor cursor;
	private final int CHANNEL_MSG_CACHE_LIMIT = 5;

	public CommunityChatDB(Context con) {
		context = con;
	}

	// MANOJ SINGH
	// Date 25-08-2015
	// START COUNTER TASK

	// SAVE AND UPDATE DATA IN CONVERSATION_COUNTER_TABLE
	public void saveConversitionCounter(ContentValues values, String groupId) {
		// ==============================-NEW-===============================
		try {
			cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_CHANNEL_COUNTER_LIST,
					null, CommunityCounterTable.GROUPID + " = ?  ",
					new String[] { groupId }, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String id = cursor.getString(cursor
						.getColumnIndex(CommunityCounterTable.GROUPID));
				if (!id.equals("")) {
					int updatedRow = context
							.getContentResolver()
							.update(MediaContentProvider.CONTENT_URI_CHANNEL_COUNTER_LIST,
									values,
									CommunityCounterTable.GROUPID + " =?",
									new String[] { id });
					if (log)
						Log.i(TAG, " UPDATE Details -- " + updatedRow);
				}
			} else {

				Uri u = context.getContentResolver().insert(
						MediaContentProvider.CONTENT_URI_CHANNEL_COUNTER_LIST,
						values);
				if (log)
					Log.v(TAG, "INSERTED -->" + u);
			}

		} catch (Exception e) {
			Log.e(TAG, "" + e);
		} finally {
			cursor.close();
		}

	}

	// END --COUNTER TASK
	//
	//

	public void saveConversitionList(Entry entry, String NextUrl) {
		// ==============================-NEW-===============================
		String mediaStr = "";
		ContentValues values = new ContentValues();
		values.put(CommunityMessageTable.GROUPID, entry.groupId);// int
		values.put(CommunityMessageTable.GROUP_NAME, entry.groupName);// String
		values.put(CommunityMessageTable.IS_OWNER, "");// int
		values.put(CommunityMessageTable.IS_ADMIN, entry.adminState);// int
		values.put(CommunityMessageTable.MSG_ID, entry.messageId);// int
		values.put(CommunityMessageTable.PARENT_ID, entry.parentMessageId);// String
		values.put(CommunityMessageTable.SENDER_ID, entry.senderId);// String
		values.put(CommunityMessageTable.SENDER_NAME, entry.senderName);// String
		values.put(CommunityMessageTable.FIRST_NAME, entry.firstName);// String
		values.put(CommunityMessageTable.LAST_NAME, entry.lastName);// String
		values.put(CommunityMessageTable.MSG_STATE, entry.messageState);// String
		values.put(CommunityMessageTable.DRM_FLAGS, entry.drmFlags);// String
		values.put(CommunityMessageTable.CREATED_DATE, entry.createdDate);// String
		values.put(CommunityMessageTable.MSG_TEXT, entry.messageText);// String
		values.put(CommunityMessageTable.NEXTURL, NextUrl);// String
		if (entry.media != null && entry.media.size() > 0) {
			Vector<String> Media = new Vector<String>();
			Media = entry.media;
			for (int i = 0; i < Media.size(); i++) {
				mediaStr = mediaStr + Media.get(i) + "@@##@@##";
			}
			mediaStr = mediaStr.substring(0, mediaStr.length() - 8);
		}
		if (mediaStr != null)
			values.put(CommunityMessageTable.MEDIA, mediaStr);// String
		// public static final String TABLE = "TABLE_MASSAGE_COMMUNITY";
		// ==================================================================
		String id = "";
		mediaStr = "";
		try {
			/*
			 * Cursor cursor=
			 * BusinessProxy.sSelf.sqlDB.query(CommunityMessageTable.TABLE,
			 * null, CommunityMessageTable.MSG_ID +
			 * " = ? and "+CommunityMessageTable.GROUPID +" = ?  ", new String[]
			 * { entry.messageId,entry.groupId+"" }, null, null,
			 * CommunityMessageTable.CREATED_DATE + " ASC");
			 */
			cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
					null,
					CommunityMessageTable.MSG_ID + " = ? and "
							+ CommunityMessageTable.GROUPID + " = ?  ",
					new String[] { entry.messageId, entry.groupId + "" }, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getString(cursor
						.getColumnIndex(CommunityMessageTable.MSG_ID));
				// String msg
				// =cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MSG_TEXT));
				if (!id.equals("")) {
					int updatedRow = context
							.getContentResolver()
							.update(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
									values,
									CommunityMessageTable.MSG_ID + " =?",
									new String[] { id });
					if (log)
						Log.i(TAG, " UPDATE Details -- " + updatedRow);
				}
			} else {

				Uri u = context
						.getContentResolver()
						.insert(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
								values);
				if (log)
					Log.v(TAG, "INSERTED -->" + u);
			}

		} catch (Exception e) {
			Log.e(TAG, "" + e);
		} finally {
			cursor.close();
		}

	}

	// DATE 5-5-2016
	// TIMELINE DATA STORE
	public void saveTimeLineList(ChannelPostAPIGetFeed channelPostForm) {
		// ==============================-NEW-===============================

		List<ChannelPostAPI> channelPostAPIList = channelPostForm
				.getChannelPostAPIList();
		int count = channelPostAPIList.size();
		for (int i = 0; i < count; i++) {
			String mediaStr = "";
			ContentValues values = new ContentValues();
			values.put(CommunityTimlineTable.CHANNEL_POST_ID,
					channelPostAPIList.get(i).getChannelPostId());// int
			values.put(CommunityTimlineTable.GROUP_NAME, channelPostAPIList
					.get(i).getGroupName());// String

			// USER DETAIL STORED
			// userId +#$#$#+ userName +#$#$#+ name +#$#$#+ thumbUrl +#$#$#+
			// profileUrl
			UserAPI PostedvyUser = channelPostAPIList.get(i).getPostedByUser();
			String postUSer = "" + PostedvyUser.getUserId() + "#####"
					+ PostedvyUser.getUserName() + "#####"
					+ PostedvyUser.getName() + "#####"
					+ PostedvyUser.getThumbUrl() + "#####"
					+ PostedvyUser.getProfileUrl();

			values.put(CommunityTimlineTable.POSTED_BY_USER, postUSer);// int
			values.put(CommunityTimlineTable.HASHTAG, channelPostAPIList.get(i)
					.getHashtags());// int
			values.put(CommunityTimlineTable.TEXT, channelPostAPIList.get(i)
					.getText());
			values.put(CommunityTimlineTable.AUDIO_PLAY_COUNT,
					channelPostAPIList.get(i).getAudioPlayCount());// String
			values.put(CommunityTimlineTable.VIDEO_PLAY_COUNT,
					channelPostAPIList.get(i).getVideoPlayCount());// String
			values.put(CommunityTimlineTable.LIKES_COUNT, channelPostAPIList
					.get(i).getLikesCount());// String
			values.put(CommunityTimlineTable.COMMENT_COUNT, channelPostAPIList
					.get(i).getCommentsCount());// String
			values.put(CommunityTimlineTable.SHARE_COUNT, channelPostAPIList
					.get(i).getShareCount());// String
			values.put(CommunityTimlineTable.VIEW_COUNT, channelPostAPIList
					.get(i).getViewCount());// String
			values.put(CommunityTimlineTable.CREATED_DATE, ""
					+ channelPostAPIList.get(i).getCreatedDate());// String
			

			// contentType + ##I$I## + contentUrl + ##I$I## +thumbUrl + ##N$N##
			List<MediaContentUrl> audiolist = channelPostAPIList.get(i)
					.getAudioMediaContentUrlList();
			if (audiolist != null) {
				String audioStr = "";
				for (int j = 0; j < audiolist.size(); j++) {
					audioStr = audioStr + audiolist.get(j).getContentId()
							+ "##I#I##" + audiolist.get(j).getContentType()
							+ "##I#I##" + audiolist.get(j).getContentUrl()
							+ "##I#I##" + audiolist.get(j).getThumbUrl()
							+ "##N#N##";
				}
				values.put(CommunityTimlineTable.AUDIO_CONTENT, audioStr);// String
			}
			List<MediaContentUrl> imagelist = channelPostAPIList.get(i)
					.getImageMediaContentUrlList();
			if (imagelist != null) {
				String ImageStr = "";
				for (int j = 0; j < imagelist.size(); j++) {
					ImageStr = ImageStr + imagelist.get(j).getContentId()
							+ "##I#I##" + imagelist.get(j).getContentType()
							+ "##I#I##" + imagelist.get(j).getContentUrl()
							+ "##I#I##" + imagelist.get(j).getThumbUrl()
							+ "##N#N##";
				}
				values.put(CommunityTimlineTable.IMAGE_CONTENT, ImageStr);// String
			}
			List<MediaContentUrl> Videolist = channelPostAPIList.get(i)
					.getVideoMediaContentUrlList();
			if (Videolist != null) {
				String videostr = "";
				for (int j = 0; j < Videolist.size(); j++) {
					videostr = videostr + Videolist.get(j).getContentId()
							+ "##I#I##" + Videolist.get(j).getContentType()
							+ "##I#I##" + Videolist.get(j).getContentUrl()
							+ "##I#I##" + Videolist.get(j).getThumbUrl()
							+ "##N#N##";
				}
				values.put(CommunityTimlineTable.VIDEO_CONTENT, videostr);// String
			}
			// ==================================================================
			String id = "";
			mediaStr = "";
			try {
				/*
				 * Cursor cursor=
				 * BusinessProxy.sSelf.sqlDB.query(CommunityMessageTable.TABLE,
				 * null, CommunityMessageTable.MSG_ID +
				 * " = ? and "+CommunityMessageTable.GROUPID +" = ?  ", new
				 * String[] { entry.messageId,entry.groupId+"" }, null, null,
				 * CommunityMessageTable.CREATED_DATE + " ASC");
				 */
				cursor = context
						.getContentResolver()
						.query(MediaContentProvider.CONTENT_URI_CHANNEL_TIMELINE_LIST,
								null,
								CommunityTimlineTable.CHANNEL_POST_ID + " = ?",
								new String[] { ""
										+ channelPostAPIList.get(i)
												.getChannelPostId(), }, null);
				if (cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					id = cursor
							.getString(cursor
									.getColumnIndex(CommunityTimlineTable.CHANNEL_POST_ID));
					// String msg
					// =cursor.getString(cursor.getColumnIndex(CommunityMessageTable.MSG_TEXT));
					if (!id.equals("")) {
						int updatedRow = context
								.getContentResolver()
								.update(MediaContentProvider.CONTENT_URI_CHANNEL_TIMELINE_LIST,
										values,
										CommunityTimlineTable.CHANNEL_POST_ID
												+ " =?", new String[] { id });
						if (log)
							Log.i(TAG, " UPDATE Details -- " + updatedRow);
					}
				} else {

					Uri u = context
							.getContentResolver()
							.insert(MediaContentProvider.CONTENT_URI_CHANNEL_TIMELINE_LIST,
									values);
					if (log)
						Log.v(TAG, "INSERTED -->" + u);
				}

			} catch (Exception e) {
				Log.e(TAG, "" + e);
			} finally {
				cursor.close();
			}
		}
	}

	
	public void TImelineMessageDeletion(String channelpostid) {
		int mRowsDeleted = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_CHANNEL_TIMELINE_LIST,
				CommunityTimlineTable.CHANNEL_POST_ID + "='" + channelpostid + "'", null);
		Log.e(TAG + "---> MessageDeletion", "" + mRowsDeleted + "==>"
				+ channelpostid);
	}
	public ChannelPostAPIGetFeed fetchTimelinedata(String groupName) {
		
		
		ChannelPostAPIGetFeed channelPostAPIGetFeed  = null;
		try{
			List<ChannelPostAPI> channelPostAPI = new ArrayList<ChannelPostAPI>();
			Cursor cursor = context.getContentResolver().query(MediaContentProvider.CONTENT_URI_CHANNEL_TIMELINE_LIST, null,
					CommunityTimlineTable.GROUP_NAME + " = ?",
					new String[] { "" + groupName.trim(), }, CommunityTimlineTable.CHANNEL_POST_ID + " desc");
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String channelPostId = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.CHANNEL_POST_ID));
				String groupname = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.GROUP_NAME));
				String postbyStr = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.POSTED_BY_USER));
				UserAPI uspi = null;
				if (postbyStr != null) {
					String[] post_arr = postbyStr.split("#####");
					uspi = new UserAPI(Long.valueOf(post_arr[0]).longValue(),
							post_arr[1], post_arr[2], post_arr[3], post_arr[4]);
				}
				String text = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.TEXT));
				String hashtags = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.HASHTAG));
				String audioPlayCount = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.AUDIO_PLAY_COUNT));
				String videoPlayCount = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.VIDEO_PLAY_COUNT));
				String likesCount = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.LIKES_COUNT));
				String commentsCount = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.COMMENT_COUNT));
				String shareCount = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.SHARE_COUNT));
				String viewCount = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.VIEW_COUNT));
				String createdDate = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.CREATED_DATE));
				String audioliststr = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.AUDIO_CONTENT));
				String imageliststr = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.IMAGE_CONTENT));
				String videoliststr = cursor.getString(cursor
						.getColumnIndex(CommunityTimlineTable.VIDEO_CONTENT));

				ChannelPostAPI CPA = new ChannelPostAPI(Long.valueOf(channelPostId)
						.longValue(), groupName, uspi, text, hashtags,
						parseStringTolist(imageliststr),
						parseStringTolist(audioliststr), Long.valueOf(
								audioPlayCount).longValue(),
						parseStringTolist(videoliststr), Long.valueOf(
								videoPlayCount).longValue(), Long.valueOf(
								likesCount).longValue(), Long
								.valueOf(commentsCount).longValue(), Long.valueOf(
								shareCount).longValue(), Long.valueOf(viewCount)
								.longValue(), createdDate);
				channelPostAPI.add(CPA);
				cursor.moveToNext();
			}
			 channelPostAPIGetFeed = new ChannelPostAPIGetFeed(
					"", channelPostAPI, "", "");
		}catch(Exception e){
			
		}
		
		
		return channelPostAPIGetFeed;
	}

	public List<MediaContentUrl> parseStringTolist(String str) {
		List<MediaContentUrl> commlist = new ArrayList<MediaContentUrl>();
		if (str != null) {
			String[] splitComm = str.split("##N#N##");
			for (int jj = 0; jj < splitComm.length; jj++) {
				String[] splitRow = splitComm[jj].split("##I#I##");
				// for(int kk =0; kk < splitRow.length;kk++){
				MediaContentUrl MCU = new MediaContentUrl(splitRow[0],
						splitRow[1], splitRow[2], splitRow[3]);
				commlist.add(MCU);
				// }
			}
		}
		return commlist;

	}

	public void removeExtraMessagesForChannel(int group_id) {
		try {
			String msg_ID = null;
			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
					null, CommunityMessageTable.GROUPID + " = ?  ",
					new String[] { group_id + "" }, null);
			// Check if cursor count is greater than cash Limit, then remove
			// older ones.
			if (cursor.getCount() > CHANNEL_MSG_CACHE_LIMIT) {
				int message_to_delete = cursor.getCount()
						- CHANNEL_MSG_CACHE_LIMIT;
				cursor.moveToFirst();
				for (int m = 0; m < message_to_delete; m++) {
					msg_ID = cursor.getString(cursor
							.getColumnIndex(CommunityMessageTable.MSG_ID));
					int deletedRow = context
							.getContentResolver()
							.delete(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
									CommunityMessageTable.MSG_ID + " =?",
									new String[] { msg_ID });
					if (deletedRow == 1) {
						Log.w(TAG,
								"saveConversitionList :: Row deleted for msgID : "
										+ msg_ID);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void MessageDeletion(String messageid) {
		int mRowsDeleted = context.getContentResolver().delete(
				MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
				CommunityMessageTable.MSG_ID + "='" + messageid + "'", null);
		Log.e(TAG + "---> MessageDeletion", "" + mRowsDeleted + "==>"
				+ messageid);
	}

	/*
	 * public void MessageReport(String messageid){ int mRowsDeleted
	 * =context.getContentResolver
	 * ().delete(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST
	 * ,CommunityMessageTable.MSG_ID+"='"+ messageid+"'", null);
	 * Log.e(TAG+"---> MessageDeletion", ""+mRowsDeleted+"==>"+messageid); }
	 */

	public static long gettimemillis(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long convertedtime = TimeZone.getDefault().getRawOffset();
		if (datetime.indexOf('/') != -1) {
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");// 24/01/2015
			// 00:38:52
			convertedtime = 0;
		}
		Date date = null;
		// 2015-01-06T17:45:15-05:00, 2015-01-23 18:37:30
		if (datetime == null)
			return convertedtime;
		if (datetime.length() > 19) {
			if (datetime.lastIndexOf('-') != -1)
				datetime = datetime.substring(0, datetime.lastIndexOf('-'));
			else if (datetime.lastIndexOf('+') != -1)
				datetime = datetime.substring(0, datetime.lastIndexOf('+'));
			if (datetime.indexOf('T') != -1)
				datetime = datetime.replace('T', ' ');
		}

		try {
			date = sdf.parse(datetime);
			// System.out.println(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (date != null)
			convertedtime += date.getTime();
		return convertedtime;
	}

	public Entry getCommunityDetail(String groupId) {
		Entry entry = new Entry();
		try {
			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_COMMUNITY, null,
					CommunityTable.GROUPID + " = ?", new String[] { groupId },
					null);
			cursor.moveToFirst();
			if (cursor != null) {
				entry.groupId = Integer.parseInt(groupId);
				entry.ownerUsername = cursor.getString(cursor
						.getColumnIndex(CommunityTable.OWNER_USERNAME));
				entry.groupName = cursor.getString(cursor
						.getColumnIndex(CommunityTable.GROUP_NAME));
				entry.messageUrl = cursor.getString(cursor
						.getColumnIndex(CommunityTable.MESSAGE_URL));
				// entry.messageText =
				// cursor.getString(cursor.getColumnIndex(CommunityTable.))
				// feed = CursorToObject.ChannelCommentEntryObject(cursor);
				cursor.close();
			}
		} catch (Exception e) {
			entry = null;
			Log.e(TAG, "" + e);
		}
		return entry;

	}

	public int getCommunityChatCount(String groupId) {
		int count = 0;
		try {

			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
					null, CommunityMessageTable.GROUPID + " = ?",
					new String[] { groupId },
					CommunityMessageTable.MSG_ID + " desc");
			cursor.moveToFirst();
			if (cursor != null) {
				count = cursor.getCount();
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "" + e);
		}
		return count;

	}

	public String getCommunityChatMessageId(String groupId, int i) {
		String Messageid = null;
		int val = 0;
		try {
			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
					null, CommunityMessageTable.GROUPID + " = ?",
					new String[] { groupId },
					CommunityMessageTable.MSG_ID + " asc");
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount() > i) {
				val = cursor.getCount() - i;
			}
			for (int ii = 0; ii < val; ii++) {
				Messageid = cursor.getString(cursor
						.getColumnIndex(CommunityTable.MSG_ID));
				int deletedRow = context
						.getContentResolver()
						.delete(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
								CommunityMessageTable.MSG_ID + " =?",
								new String[] { Messageid });
				if (deletedRow == 1) {
					Log.w(TAG,
							"saveConversitionList :: Row deleted for msgID : "
									+ Messageid);
				}
				cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "" + e);
		}
		return Messageid;

	}
}
