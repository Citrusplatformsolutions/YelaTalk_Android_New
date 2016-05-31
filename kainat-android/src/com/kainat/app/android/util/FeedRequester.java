package com.kainat.app.android.util;

import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.widget.Toast;

import com.kainat.app.android.bean.DiscoveryUrl;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;

public class FeedRequester {

	public static Vector<Message> latestMessage = new Vector<Message>();
	private static boolean log = false;
	public static String lastMessageId = "0";
	public static long lastMessageIdTime;
	public static String lastChannelRefreshMessageID = "1";//Default value in case of error
	public static final int feedCount = 50;
	public static final int loadMore = 10;
	public static int systemMessage = 0;
	public static int systemMessageOld = 0;
	public static FeedTask feedTaskLeftMenu, feedTaskActivityFeed,
			feedTaskActivityFeedPullRefresh, feedTaskGroupevent,
			feedTaskMediaFeed, feedTaskFeatureUserFeed, feedTaskCommnunity,
			feedTaskConversationList, feedTaskConversationListMore,
			feedTaskMessageList, feedTaskConversationMessages,
			feedTaskBookMarkMessages, feedTaskBookMarkMessagesMore,
			feedTaskConversationMessagesRefresh,feedTaskLikeUser, feedTaskChannelRefresh;//
	public static Hashtable<String, DiscoveryUrl> discovery = null;
	private static final String TAG = FeedRequester.class.getSimpleName();

	public static void requestLeftMenu(Context context)
	{
		if ((feedTaskLeftMenu == null
				|| feedTaskLeftMenu.getStatus() == Status.FINISHED 
				|| (BusinessProxy.sSelf.gridMenu == null || BusinessProxy.sSelf.gridMenu.size() <= 0))
				&& ((System.currentTimeMillis() - Utilities.getLong(context, Constants.MENU_RFRESH_TIME))) > Constants.MENU_RFRESH_TIME_INTERVAL) 
		{
			Utilities.setLong(context, Constants.MENU_RFRESH_TIME, System.currentTimeMillis());
			feedTaskLeftMenu = new FeedTask(context);
			feedTaskLeftMenu.setRequestId(Constants.FEED_INITIAL_LEFT_MENU);
			feedTaskLeftMenu.setClientType(1);
			if (log) {
				Log.i(TAG, "requestLeftMenu :: LeftMenu===Url==="+ Utilities.getString(context, Constants.LEFT_MENU_URL));
			}
			feedTaskLeftMenu.execute(Utilities.getString(context, Constants.LEFT_MENU_URL));
		}
	}

	public static void requestActivityFeed(Context context) 
	{
		if (feedTaskActivityFeed == null || feedTaskActivityFeed.getStatus() == Status.FINISHED) 
		{
			feedTaskActivityFeed = new FeedTask(context);
			feedTaskActivityFeed.setRequestId(Constants.FEED_ACTIVITY);
			feedTaskActivityFeed.setClientType(1);
			
			if (log)
				Log.i(TAG, "requestActivityFeed :: Utilities.getString(context, Constants.ACTIVITY_FEED_URL)+\"?\" + BusinessProxy.sSelf.thumbInfo-----"
								+ (Utilities.getString(context,
										Constants.ACTIVITY_FEED_URL) + "?" + BusinessProxy.sSelf.thumbInfo));
			
			feedTaskActivityFeed.execute(Utilities.getString(context,
					Constants.ACTIVITY_FEED_URL)
					+ "?"
					+ BusinessProxy.sSelf.thumbInfo);
		}
	}

	public static void requestActivityFeedPullRefresh(Context context, String topId) 
	{
		if (feedTaskActivityFeedPullRefresh == null
				|| feedTaskActivityFeedPullRefresh.getStatus() == Status.FINISHED) 
		{
			feedTaskActivityFeedPullRefresh = new FeedTask(context);
			feedTaskActivityFeedPullRefresh.pullRefresh = true;
			feedTaskActivityFeedPullRefresh .setRequestId(Constants.FEED_ACTIVITY);
			feedTaskActivityFeedPullRefresh.setClientType(1);
			
			feedTaskActivityFeedPullRefresh.execute(Utilities.getString(
					context, Constants.ACTIVITY_FEED_URL)
					+ "?id="
					+ topId
					+ "&direction=up" + "&" + BusinessProxy.thumbInfo);
		}
	}

	public static void requestGroupevent(Context context) 
	{
		try {
			if (discovery == null)
				discovery = Utilities.getDiscoveryUrl(context);
			if (feedTaskGroupevent == null
					|| feedTaskGroupevent.getStatus() == Status.FINISHED) 
			{
				feedTaskGroupevent = new FeedTask(context);
				feedTaskGroupevent.setRequestId("groupevent".hashCode());
				feedTaskGroupevent.setClientType(1);
				
				feedTaskGroupevent.execute(((DiscoveryUrl) discovery
						.get("groupevent")).url
						+ "&"
						+ BusinessProxy.thumbInfo);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void requestMediaFeed(Context context) {
		if (discovery == null)
			discovery = Utilities.getDiscoveryUrl(context);
		if (feedTaskMediaFeed == null
				|| feedTaskMediaFeed.getStatus() == Status.FINISHED) {
			feedTaskMediaFeed = new FeedTask(context);
			feedTaskMediaFeed.setRequestId("media".hashCode());
			feedTaskMediaFeed.setClientType(0);
			feedTaskMediaFeed.execute(((DiscoveryUrl) discovery.get("media")).url + "&"
							+ BusinessProxy.thumbInfo);
		}
	}

	public static void requestFeatureUserFeed(Context context) {
		if (discovery == null)
			discovery = Utilities.getDiscoveryUrl(context);
		if (feedTaskFeatureUserFeed == null
				|| feedTaskFeatureUserFeed.getStatus() == Status.FINISHED) {
			feedTaskFeatureUserFeed = new FeedTask(context);
			feedTaskFeatureUserFeed.setRequestId("user".hashCode());
			feedTaskFeatureUserFeed.setClientType(1);
			feedTaskFeatureUserFeed.execute(((DiscoveryUrl) discovery
					.get("user")).url + "&" + BusinessProxy.thumbInfo);
		}
	}

	public static void requestCommnunity(Context context) {
		if (discovery == null)
			discovery = Utilities.getDiscoveryUrl(context);
		if (feedTaskCommnunity == null
				|| feedTaskCommnunity.getStatus() == Status.FINISHED) {
			feedTaskCommnunity = new FeedTask(context);
			feedTaskCommnunity.setRequestId("community".hashCode());
			feedTaskCommnunity.setClientType(0);
			feedTaskCommnunity.execute(((DiscoveryUrl) discovery
					.get("community")).url
					+ "&"
					+ BusinessProxy.thumbInfo);
		}
	}

	public static void requestInboxConversiotionListFeedMore(Context context, String mfuId) 
	{
		// mfuId="2999668" ;
		if (feedTaskConversationListMore == null
				|| feedTaskConversationListMore.getStatus() == Status.FINISHED) 
		{
			feedTaskConversationListMore = new FeedTask(context);
			feedTaskConversationListMore.setRequestId(Constants.FEED_CONVERSATION_LIST);
			feedTaskConversationListMore.setClientType(1);

			BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf
					.getTransactionId() + 1);
			long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																	// ;

			String url = "http://"
					+ Urls.TEJAS_HOST
					+ "/tejas/rest/rtmessaging/getConversations/#USERID#?lastMFUId=#lastMFUId#&deviceId="
					+ BusinessProxy.sSelf.getClientId()
					+ "&isNext=false&pageNumber=1&numRecordsPerPage="
					+ loadMore + "&sortOrder=desc&"
					+ BusinessProxy.thumbInfo + "&transactionId="
					+ cltTxnId;
			url = url.replace("#USERID#", "" + BusinessProxy.sSelf.getUserId());
			url = url.replace("#lastMFUId#", mfuId);
			feedTaskConversationListMore.execute(url);
			if (log)
				Log.i(TAG, "requestInboxConversiotionListFeedMore :: FeedRequester------------requestInboxConversiotionListFeed hh url :"+ url);
//			Logger.conversationLog("FeedRequester requestInboxConversiotionListFeedMore : ",url);
		}
	}

	public static void requestInboxConversiotionListFeed(Context context) {
		try {

			if (feedTaskConversationList == null
					|| feedTaskConversationList.getStatus() == Status.FINISHED) {
				feedTaskConversationList = new FeedTask(context);
				feedTaskConversationList
						.setRequestId(Constants.FEED_CONVERSATION_LIST);
				feedTaskConversationList.setClientType(1);

				BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf.getTransactionId() + 1);
				long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																		// ;

				String lastMsgId = "0";
				String lastMfuId = "0";
				String[] columns = new String[] { MessageTable.MESSAGE_ID,
						MessageTable.MFU_ID, MessageTable.SORT_TIME };
				// Cursor cursor = context.getContentResolver().query(
				// MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
				// columns,
				// MessageTable.CONVERSATION_ID +
				// "= ? and "+MessageTable.USER_ID +
				// " =? and "+MessageTable.MESSAGE_ID
				// +" !='-999' and "+MessageTable.MFU_ID +" != -1", new
				// String[]{conversationId,""+BusinessProxy.sSelf.getUserId()},
				// MessageTable.SORT_TIME + " ASC");

				Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
						MessageConversationTable.TABLE, null,
						MessageConversationTable.USER_ID + " =? and "
								+ MessageTable.MESSAGE_ID + " !='-999' and "
								+ MessageTable.MFU_ID + " != -1",
						new String[] { BusinessProxy.sSelf.getUserId() + "" },
						null, null, MessageConversationTable.INSERT_TIME
								+ " DESC");

				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					int dataColumnIndex = cursor
							.getColumnIndex(MessageTable.MESSAGE_ID);
					lastMsgId = cursor.getString(dataColumnIndex);
					lastMfuId = cursor.getString(cursor
							.getColumnIndex(MessageTable.MFU_ID));
					// feedTaskConversationMessages.sortTime =
					// cursor.getLong(cursor.getColumnIndex(MessageTable.SORT_TIME));
					// lastMfuId="-1" ;
					// if(force){
					// feedTaskConversationMessages.clearAll=true ;
					// lastMfuId="0" ;
					// feedTaskConversationMessages.conversationID =
					// conversationId ;
					// }
				}

				String url = "http://"
						+ Urls.TEJAS_HOST
						+ "/tejas/rest/rtmessaging/getConversations/#USERID#?lastMFUId=#lastMFUId#&deviceId="
						+ BusinessProxy.sSelf.getClientId()
						+ "&isNext=true&pageNumber=1&numRecordsPerPage="
						+ feedCount + "&sortOrder=desc&"
						+ BusinessProxy.thumbInfoSmall + "&transactionId="
						+ cltTxnId;
				url = url.replace("#USERID#",
						"" + BusinessProxy.sSelf.getUserId());
				url = url.replace("#lastMFUId#", lastMfuId);// FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST)
															// ;
				feedTaskConversationList.execute(url);
				// System.out.println(" top message id for conversation list : "+lastMfuId);
				if (log)
					Log.i(TAG, "requestInboxConversiotionListFeed :: FeedRequester------------requestInboxConversiotionListFeed hh url :"+ url);
//				Logger.conversationLog("FeedRequester requestInboxConversiotionListFeed : ",url);
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}

	public static long refreshTime = 0;

	public static void requestInboxMessage(Context context) {
		try {
			if (feedTaskMessageList == null
					|| feedTaskMessageList.getStatus() == Status.FINISHED) 
			{
				feedTaskMessageList = new FeedTask(context);
				feedTaskMessageList.setRequestId(Constants.FEED_GET_MESSAGE);
				feedTaskMessageList.setClientType(1);
				refreshTime = 0;

				BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf
						.getTransactionId() + 1);
				long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																		// ;
				if (lastMessageId == null || lastMessageId.trim().length() <= 0)
					lastMessageId = "0";
				if (log)
					Log.i(TAG, "requestInboxMessage :: ------------last mfu id 2 lastMessageId :"
									+ lastMessageId);
				String url = "http://"
						+ Urls.TEJAS_HOST
						+ "/tejas/rest/rtmessaging/getMessages/#USERID#?lastMFUId=#lastMFUId#&deviceId="
						+ BusinessProxy.sSelf.getClientId()
						+ "&isNext=true&pageNumber=1&numRecordsPerPage="
						+ feedCount + "&sortOrder=asc&"
						+ BusinessProxy.sSelf.thumbInfo + "&transactionId="
						+ cltTxnId;
				url = url.replace("#USERID#",
						"" + BusinessProxy.sSelf.getUserId());
				url = url.replace("#lastMFUId#", lastMessageId);
				if (log)
					Log.i(TAG, "requestInboxMessage :: ------------requestInboxMessage url :"+ url);
				if (lastMessageIdTime > 0) {
					Utilities.setString(context, "lastMessageId", lastMessageId);
					Utilities.setLong(context, "lastMessageIdTime", lastMessageIdTime);
				}
				feedTaskMessageList.execute(url);
			} else {
				refreshTime++;
				if (log)
					Log.i(TAG, "requestInboxMessage :: ----------stop refresh searvice onError---23-----");

			}
		} catch (Exception e) {
			e.printStackTrace();
//			Logger.conversationLog("FeedRequester requestInboxMessage : ", e.getMessage());
		}
		if (log)
			Log.i(TAG, "requestInboxMessage :: ----------stop refresh service refreshTime----"+ refreshTime);
	}
	public static void requestChannelRefresh(Context context) 
	{
		try {
			if(lastChannelRefreshMessageID == null || (lastChannelRefreshMessageID != null && lastChannelRefreshMessageID.equals("1")))
				return;
			if (log)
				Log.i(TAG, "requestChannelRefresh :: requestChannelRefresh with lastChannelRefreshMessageID :" + lastChannelRefreshMessageID);
			
			if (feedTaskChannelRefresh == null || feedTaskChannelRefresh.getStatus() == Status.FINISHED) 
			{
				feedTaskChannelRefresh = new FeedTask(context);
				feedTaskChannelRefresh.setRequestId(Constants.FEED_GET_CHANNEL_REFRESH);
				feedTaskChannelRefresh.setClientType(1);
				refreshTime = 0;
				
				BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf.getTransactionId() + 1);
				
				//Get last Channel Refresh ID
				if (lastChannelRefreshMessageID == null || lastChannelRefreshMessageID.trim().length() <= 0)
					lastChannelRefreshMessageID = "1";
				
				if (log)
					Log.i(TAG, "requestChannelRefresh :: Last lastChannelRefreshMessageID :" + lastChannelRefreshMessageID);
				//http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/messagecount?messageId=22589
				if (log)
					Log.i(TAG, "requestChannelRefresh :: lastChannelRefreshMessageID for Shared Pref : " +lastChannelRefreshMessageID);
				
				String url = "http://"
						+ Urls.TEJAS_HOST
						+ "/tejas/feeds/api/group/messagecount?messageId=#lastChannelRefreshMessageID#";
						
				url = url.replace("#lastChannelRefreshMessageID#", lastChannelRefreshMessageID);
				
				if (log)
					Log.i(TAG, "requestChannelRefresh :: ------------requestChannelRefresh url :"+ url);
				
				feedTaskChannelRefresh.execute(url);
			} 
			else 
			{
				refreshTime++;
				if (log)
					Log.i(TAG, "requestChannelRefresh :: ----------stop refresh searvice onError---23-----");
			}
		} catch (Exception e) {
			e.printStackTrace();
//			Logger.conversationLog("FeedRequester requestInboxMessage : ", e.getMessage());
		}
		if (log)
			Log.i(TAG, "requestInboxMessage :: ----------stop refresh searvice refreshTime----"+ refreshTime);
	}

	public static void requestConversationMessages(Context context,
			String conversationId, HttpSynchInf httpSynchInf, boolean force) 
	{

		if (log)
			Log.i(TAG, "requestConversationMessages :: FeedRequester----------------force conversation  :"+ force);
		if (feedTaskConversationMessages == null
				|| feedTaskConversationMessages.getStatus() == Status.FINISHED) {
			feedTaskConversationMessages = new FeedTask(context);
			feedTaskConversationMessages.setRequestId(Constants.FEED_GET_CONVERSATION_MESSAGES2);
			feedTaskConversationMessages.setClientType(1);
			feedTaskConversationMessages.dontIncrementUnreadCount = true;
			String lastMsgId = "0";
			String lastMfuId = "0";
			String[] columns = new String[] { MessageTable.MESSAGE_ID,
					MessageTable.MFU_ID, MessageTable.SORT_TIME };
			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX,
					columns,
					MessageTable.CONVERSATION_ID + "= ? and "
							+ MessageTable.USER_ID + " =? and "
							+ MessageTable.MESSAGE_ID + " !='-999' and "
							+ MessageTable.MFU_ID + " != -1",
					new String[] { conversationId,
							"" + BusinessProxy.sSelf.getUserId() },
					MessageTable.SORT_TIME + " ASC");

			if (cursor.getCount() > 1) {
				cursor.moveToLast();
				int dataColumnIndex = cursor.getColumnIndex(MessageTable.MESSAGE_ID);
				lastMsgId = cursor.getString(dataColumnIndex);
				lastMfuId = cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
				feedTaskConversationMessages.sortTime = cursor.getLong(cursor.getColumnIndex(MessageTable.SORT_TIME));
			}
			
			feedTaskConversationMessages.conversationID = conversationId;
			if (cursor.getCount() == 1) 
			{
				cursor.moveToFirst();
				int dataColumnIndex = cursor.getColumnIndex(MessageTable.MESSAGE_ID);
				lastMsgId = cursor.getString(dataColumnIndex);
				if (cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID)).equalsIgnoreCase("-1"))
					lastMfuId = "0";// "-1";
				feedTaskConversationMessages.sortTime = cursor.getLong(cursor.getColumnIndex(MessageTable.SORT_TIME));
				// lastMfuId="-1" ;
			}
			cursor.close();
			if (!lastMfuId.equalsIgnoreCase("-1")) {
				BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf.getTransactionId() + 1);
				long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																		// ;
				if (conversationId.trim().equals("-1"))
					conversationId = "0";

				if (log) {
					Log.i(TAG, "requestConversationMessages :: FeedRequester------------last lastMsgId id :"+ lastMsgId);
					Log.i(TAG, "requestConversationMessages :: FeedRequester------------conversationId :" + conversationId);
					Log.i(TAG, "requestConversationMessages :: FeedRequester------------sorttime :"+ feedTaskConversationMessages.sortTime);
				}
				
					String url = "http://"
						+ Urls.TEJAS_HOST
						+ "/tejas/rest/rtmessaging/getConversationMessages/#USERID#/#CONVERSATIONID#?lastMsgId=#lastMsgId#&deviceId="
						+ BusinessProxy.sSelf.getClientId()
						+ "&isNext=true&pageNumber=1&numRecordsPerPage="
						+ feedCount
						+ "&displayOrder=asc&sortOrder=desc&displayOrder=asc&"
						+ BusinessProxy.thumbInfo + "&transactionId="
						+ cltTxnId;
				if (!lastMsgId.equalsIgnoreCase("0"))
					url = "http://"
							+ Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/getConversationMessages/#USERID#/#CONVERSATIONID#?lastMsgId=#lastMsgId#&deviceId="
							+ BusinessProxy.sSelf.getClientId()
							+ "&isNext=true&pageNumber=1&numRecordsPerPage="
							+ feedCount
							+ "&displayOrder=asc&sortOrder=asc&displayOrder=asc&"
							+ BusinessProxy.thumbInfo + "&transactionId="
							+ cltTxnId;
				else
					feedTaskConversationMessages.clearAll = true;

				// if(lastMsgId.length()>2)
				// url =
				// "http://"+UIActivityManager.TEJAS_HOST+"/tejas/rest/rtmessaging/getConversationMessages/#USERID#/#CONVERSATIONID#?lastMsgId=#lastMsgId#&deviceId="+BusinessProxy.sSelf.getClientId()+"&isNext=false&&pageNumber=1&numRecordsPerPage="+feedCount+"&sortOrder=desc&BusinessProxy.sSelf.thumbInfo";

				url = url.replace("#USERID#", "" + BusinessProxy.sSelf.getUserId());
				url = url.replace("#lastMsgId#", lastMsgId/* "0" */);
				url = url.replace("#CONVERSATIONID#", conversationId);
				if (log)
					Log.i(TAG, "requestConversationMessages :: FeedRequester-----lastMfuId--"+ lastMfuId
									+ " : ------------requestConversationMessages url :"
									+ url);
				feedTaskConversationMessages.execute(url);
				if (Logger.CHEAT)
					Toast.makeText(context, lastMfuId + " sending... : " + lastMsgId, Toast.LENGTH_LONG).show();
//				Logger.conversationLog("FeedRequester requestConversationMessages : ", url);
//				Logger.conversationLog("FeedRequester remove sorttime greater than : ", "" + feedTaskConversationMessages.sortTime);
			} else {
//				Logger.conversationLog(
//						"FeedRequester requestConversationMessages else : ",
//						"else");
				feedTaskConversationMessages = null;
				httpSynchInf.onError("", Constants.FEED_GET_CONVERSATION_MESSAGES2);
			}
		}
	}

	public static void requestConversationMessagesRefresh(Context context,
			String conversationId, HttpSynchInf httpSynchInf) {
		try {
			FeedTask.insMoreTime2 = FeedTask.insMoreTime2 - (FeedTask.totLodedMore * 2);
			if (feedTaskConversationMessagesRefresh == null
					|| feedTaskConversationMessagesRefresh.getStatus() == Status.FINISHED) {
				feedTaskConversationMessagesRefresh = new FeedTask(context);
				feedTaskConversationMessagesRefresh.setRequestId(Constants.FEED_GET_CONVERSATION_MESSAGES);
				feedTaskConversationMessagesRefresh.setClientType(1);
				feedTaskConversationMessagesRefresh.more = true;
				feedTaskConversationMessagesRefresh.dontIncrementUnreadCount = true;


				String lastMsgId = "0";
				String lastMfuId = "0";
				String[] columns = new String[] { MessageTable.MESSAGE_ID,
						MessageTable.MFU_ID, MessageTable.INSERT_TIME };

				Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
						MessageTable.TABLE, null, MessageTable.CONVERSATION_ID
								+ " = ? and " + MessageTable.USER_ID
								+ " = ? and " + MessageTable.MESSAGE_TYPE
								+ " !=? and " + MessageTable.MESSAGE_ID
								+ " !='-999' and " + MessageTable.MFU_ID
								+ " != -1", new String[] { conversationId,
								BusinessProxy.sSelf.getUserId() + "",
								"" + Message.MESSAGE_TYPE_SYSTEM_MESSAGE },
						null, null,
						// MessageTable.INSERT_TIME + " ASC"
						MessageTable.DIRECTION + " DESC, " + MessageTable.SORT_TIME + " ASC"
				// order by dir desc, time desc
						);

				if (cursor.getCount() > 0) 
				{
					cursor.moveToFirst();
					int dataColumnIndex = cursor.getColumnIndex(MessageTable.MESSAGE_ID);
					lastMsgId = cursor.getString(dataColumnIndex);
					lastMfuId = cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
					feedTaskConversationMessagesRefresh.clearBelowTImeStamp = cursor.getLong(cursor.getColumnIndex(MessageTable.INSERT_TIME));

				}
				cursor.close();
				// if(!lastMfuId.equalsIgnoreCase("-1"))
				{

					if (conversationId.trim().equals("-1"))
						conversationId = "0";
					if (log) {
						Log.i(TAG, "requestConversationMessagesRefresh :: FeedRequester------------last lastMsgId id :"
										+ lastMsgId);
						Log.i(TAG, "requestConversationMessagesRefresh :: FeedRequester------------conversationId :"
										+ conversationId);
					}
					BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf.getTransactionId() + 1);
					long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																			// ;
					String url = "http://"
							+ Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/getConversationMessages/#USERID#/#CONVERSATIONID#?lastMsgId=#lastMsgId#&deviceId="
							+ BusinessProxy.sSelf.getClientId()
							+ "&isNext=false&pageNumber=1&numRecordsPerPage="
							+ loadMore
							+ "&displayOrder=asc&sortOrder=desc&displayOrder=asc&"
							+ BusinessProxy.thumbInfo + "&transactionId="
							+ cltTxnId;

					if (lastMfuId.equalsIgnoreCase("0")) {
						url = "http://"
								+ Urls.TEJAS_HOST
								+ "/tejas/rest/rtmessaging/getConversationMessages/#USERID#/#CONVERSATIONID#?lastMsgId=#lastMsgId#&deviceId="
								+ BusinessProxy.sSelf.getClientId()
								+ "&isNext=true&pageNumber=1&numRecordsPerPage="
								+ loadMore
								+ "&displayOrder=asc&sortOrder=desc&displayOrder=asc&"
								+ BusinessProxy.thumbInfo
								+ "&transactionId=" + cltTxnId;
						feedTaskConversationMessagesRefresh.clearAll = true;
					}
					feedTaskConversationMessagesRefresh.conversationID = conversationId;

					url = url.replace("#USERID#",
							"" + BusinessProxy.sSelf.getUserId());
					url = url.replace("#lastMsgId#", lastMsgId);
					url = url.replace("#CONVERSATIONID#", conversationId);
					if (log)
						Log.i(TAG, "requestConversationMessagesRefresh :: FeedRequester------------requestConversationMessages url :"
										+ url);

//					Logger.conversationLog("FeedRequester requestConversationMessagesRefresh : ",url);

					feedTaskConversationMessagesRefresh.execute(url);
					if (Logger.CHEAT)
						Toast.makeText(context, "lmid more :" + lastMsgId, Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			feedTaskConversationMessagesRefresh = null;
//			Logger.conversationLog(
//					"FeedRequester requestConversationMessagesRefresh : ",
//					e.getMessage());
		}
	}

	public static void requestBookmarkMessages(Context context,
			String conversationId) {
		if (feedTaskBookMarkMessages == null
				|| feedTaskBookMarkMessages.getStatus() == Status.FINISHED) 
		{
			feedTaskBookMarkMessages = new FeedTask(context);
			feedTaskBookMarkMessages.setRequestId(Constants.FEED_GET_BOOKMARK_MESSAGES);
			feedTaskBookMarkMessages.setClientType(1);
			String lastUSMId = "0";
			if (log) {
				Log.i(TAG, "requestBookmarkMessages :: FeedRequester------------last mfu id :"
						+ lastUSMId);
				Log.i(TAG, "requestBookmarkMessages :: FeedRequester------------conversationId :"
						+ conversationId);
			}

			String[] columns = new String[] { MessageTable.USMId };
			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_BOOKMARK,
					columns,
					MessageTable.IS_BOOKMARK + "= ? and "
							+ MessageTable.USER_ID + " =?",
					new String[] { "1", "" + BusinessProxy.sSelf.getUserId() },
					MessageTable.INSERT_TIME + " ASC");

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int dataColumnIndex = cursor.getColumnIndex(MessageTable.USMId);
				lastUSMId = cursor.getString(dataColumnIndex);
			}
			cursor.close();
			BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf.getTransactionId() + 1);
			long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																	// ;
			String url = "http://"
					+ Urls.TEJAS_HOST
					+ "/tejas/rest/rtmessaging/getBookmarkedMessages/#USERID#?lastUSMId=#lastUSMId#&deviceId="
					+ BusinessProxy.sSelf.getClientId()
					+ "&isNext=true&pageNumber=1&numRecordsPerPage="
					+ feedCount + "&sortOrder=desc&"
					+ BusinessProxy.thumbInfo + "&transactionId="
					+ cltTxnId;

			url = url.replace("#USERID#", "" + BusinessProxy.sSelf.getUserId());

			url = url.replace("#lastUSMId#", "0");
			//
//			Logger.conversationLog("FeedRequester requestConversationMessagesRefresh : ", url);
			if (log)
				Log.i(TAG, "requestBookmarkMessages :: FeedRequester------------requestBookmarkMessages url :"
								+ url);
			feedTaskBookMarkMessages.execute(url);
			if (Logger.CHEAT)
				Toast.makeText(context, "" + lastUSMId, Toast.LENGTH_LONG).show();
		}
	}

	public static void requestBookmarkMessagesMore(Context context,
			String conversationId) {
		if (feedTaskBookMarkMessagesMore == null
				|| feedTaskBookMarkMessagesMore.getStatus() == Status.FINISHED) {
			feedTaskBookMarkMessagesMore = new FeedTask(context);
			feedTaskBookMarkMessagesMore.setRequestId(Constants.FEED_GET_BOOKMARK_MESSAGES_MORE);
			feedTaskBookMarkMessagesMore.setClientType(1);
			feedTaskBookMarkMessagesMore.more = true;
			String lastUSMId = "0";
			String[] columns = new String[] { MessageTable.USMId };
			Cursor cursor = context
					.getContentResolver()
					.query(MediaContentProvider.CONTENT_URI_BOOKMARK,
							columns,
							MessageTable.USER_ID + " =? and "
									+ MessageTable.MESSAGE_ID + " !='-999' ",
							new String[] { "" + BusinessProxy.sSelf.getUserId() },
							null);

			if (cursor.getCount() > 0) {
				cursor.moveToPosition(cursor.getCount() - 1);
				int dataColumnIndex = cursor.getColumnIndex(MessageTable.USMId);
				lastUSMId = cursor.getString(dataColumnIndex);
			}

			if (log) {
						Log.i(TAG,"requestBookmarkMessagesMore :: FeedRequester------------last lastUSMId id bookmark :"
								+ lastUSMId);
						Log.i(TAG,"requestBookmarkMessagesMore :: FeedRequester------------(cursor.getCount() :"
								+ cursor.getCount());
			}
			cursor.close();
			BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf.getTransactionId() + 1);
			long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
																	// ;
			String url = "http://"
					+ Urls.TEJAS_HOST
					+ "/tejas/rest/rtmessaging/getBookmarkedMessages/#USERID#?lastUSMId=#lastUSMId#&deviceId="
					+ BusinessProxy.sSelf.getClientId()
					+ "&isNext=false&pageNumber=1&numRecordsPerPage="
					+ loadMore + "&sortOrder=desc&"
					+ BusinessProxy.thumbInfo + "&transactionId="
					+ cltTxnId;

			url = url.replace("#USERID#", "" + BusinessProxy.sSelf.getUserId());
			url = url.replace("#lastUSMId#", lastUSMId);
			if (log)
				Log.i(TAG,"requestBookmarkMessagesMore :: FeedRequester------------requestBookmarkMessages url :"
								+ url);
			feedTaskBookMarkMessagesMore.execute(url);
			if (Logger.CHEAT)
				Toast.makeText(context, "" + lastUSMId, Toast.LENGTH_LONG).show();
		}
	}

	public static void requestLikeUser(Context context,String url) {
		Utilities.setLong(context, Constants.MENU_RFRESH_TIME,
				System.currentTimeMillis());
		feedTaskLeftMenu = new FeedTask(context);
		feedTaskLeftMenu.setRequestId(Constants.FEED_GET_LIKE_USE);
		feedTaskLeftMenu.setClientType(1);
		feedTaskLeftMenu.setHttpSynch((HttpSynchInf)context) ;
		if (log)  
		{
			Log.i(TAG,"requestLikeUser :: -----requestLikeUser----"
					+ url);
		}
		feedTaskLeftMenu.execute(url);

	}
}
