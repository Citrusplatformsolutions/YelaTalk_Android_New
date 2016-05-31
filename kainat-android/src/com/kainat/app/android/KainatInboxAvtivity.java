package com.kainat.app.android;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.ConversationsListAdapter;
import com.kainat.app.android.adaptor.ConversationsViewHolder;
import com.kainat.app.android.adaptor.ConversiotionAdaptorInf;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.bean.ParticipantInfo;
import com.kainat.app.android.bean.UserStatus;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.ComposeService;
import com.kainat.app.android.engine.HttpSynch;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.DrawableClickListener;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;

public class KainatInboxAvtivity extends UIActivityManager implements
HttpSynchInf, ThumbnailReponseHandler, OnClickListener, ConversiotionAdaptorInf ,OnScrollListener{
//	MyHorizontalScrollView scrollView;
	View menuLeft, menuRight;
	View app;
	ArrayList<String> arrConversationList;
	public Handler handler =  new Handler(Looper.getMainLooper());
	private static final byte ID_DELETE_CONVERSATION = 101;
	//	private static final byte ID_LEAVE_CONVERSATION = 102;
	//	private static final byte ID_UPDATE_SUBJECT = 103;
	//	private static final byte ID_CANCEL = 104;

	boolean deleteMode ;
	ImageView btnSlideLeft, btnSlideRight;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 200;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	boolean menuOut = false;
	public static ScrollView leftMenu;
	ListView listViewActivity;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	ConversationsListAdapter activityAdapter;
	int idsOptions[] = new int[] { 1, 2, 3, 4 };
	String idsNameOptions[] = new String[] { "Users", "Community", "Media",
	"Cancle" };
	DisplayMetrics metrics;
	FeedTask feedTaskMore = null ;
	//	EditText search_edit_text ;
	boolean dontCloseCursol ;
	TextView load_prevoius_message;
	Bundle extras;
	int C_id,IsP2P;
	private static final String TAG = KainatInboxAvtivity.class.getSimpleName();
	ImageButton group_chat,chat;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversations_list_screen);
		extras = getIntent().getExtras();
	
		
		group_chat = (ImageButton) findViewById(R.id.group_chat);
		group_chat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(KainatInboxAvtivity.this,
						KainatContactActivity.class);
				intent.putExtra("HEADER_TITLE",
						getString(R.string.create_group_lbl));
				intent.putExtra("group", true);
				startActivity(intent);
			}
		});
		chat 	   = (ImageButton) findViewById(R.id.chat);
		chat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(KainatInboxAvtivity.this,
						KainatContactActivity.class);
				intent.putExtra("HEADER_TITLE", getString(R.string.start_chat_lbl));
				startActivity(intent);
				//startActivity(intent);
			}
		});
		if(extras != null){
			Log.i( "GCM_DATA","Extra:" + extras.getString("USERID") );
			Log.i( "GCM_DATA","Extra:" + extras.getString("CONVERSATIONID") );
			Log.i( "GCM_DATA","Extra:" + extras.getString("lastMsgId") );
			Log.i( "GCM_DATA","Extra:" + extras.getString("transactionId") );
		}
		//		app = screenSlide(KainatInboxAvtivity.this,
		//				R.layout.conversations_list_screen);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		search =(EditText) findViewById(R.id.search_text);
		search.addTextChangedListener(textWatcher);
		listViewActivity = (ListView) findViewById(R.id.landing_discovery_activity_list);
		listViewActivity.setOnItemClickListener(onitemClickList);
		menuNew = (ImageButton)findViewById(R.id.menu) ;		
		menuNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList) ;	
			}
		});
		initLeftMenu();
		IsP2P= extras.getInt("IsP2P",-1); 
		C_id = extras.getInt("CID",-1); 
		//if(IsP2P == 1){
		if(C_id != -1){
			getCursor("");
			openConversation(C_id);
		}
		/*else if(IsP2P == 2){
			String GROUP_URL_PUSH=extras.getString("GROUP_URL_PUSH");
			String GROUP_ID_PUSH=extras.getString("GROUP_ID_PUSH");
			String GROUP_NAME_PUSH=extras.getString("GROUP_NAME_PUSH");
			openChatFromPush(GROUP_URL_PUSH,Integer.GROUP_ID_PUSH,GROUP_NAME_PUSH,"");
			openChatFromPush(groupURL, groupID, groupName, "");
		}*/
			
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Conversation List Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(KainatInboxAvtivity.this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(KainatInboxAvtivity.this).reportActivityStop(this);
    }


	/*new OnItemClickListener() {

		@Override 
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
//			ConversationsActivity
			//System.out.println("onitemclick==0");
			ConversationsViewHolder conversationsViewHolder = (ConversationsViewHolder)view.getTag() ;

//			ContentValues values = new ContentValues();
//			values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, 0);//int
//			int updatedRow = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
//					values,MessageConversationTable.CONVERSATION_ID
//					+"=?",new String[] {conversationsViewHolder.conversationList.conversationId});
			//System.out.println("Feed Synch --------------conversiotion list updated row count ----------"+updatedRow);
//				
			//System.out.println("onitemclick==1");
			DataModel.sSelf.storeObject(DMKeys.CONVERSATION_SELECTED_LIST,
					conversationsViewHolder.conversationList);
			Intent intent1 = new Intent(InboxAvtivity.this,
					ConversationsActivity.class);
			//System.out.println("onitemclick==2");
			intent1.putExtra(Constants.CONVERSATION_ID,conversationsViewHolder.conversationList.conversationId);
			if(conversationsViewHolder.conversationList.subject==null || conversationsViewHolder.conversationList.subject.trim().length()<=0)
				intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.source);
			else
				intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.subject);
			//System.out.println("onitemclick==3");
			startActivityForResult(intent1, Constants.ACTIVITY_FOR_RESULT_INT);
		}
	});*/
	OnItemClickListener onitemClickList =new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> arg0, View view, int pos,
				long arg3) {
			//if(view==null){
			//	System.out.println("View===== null====");
			//}else{
			//	System.out.println("View===== not null====");
			//}

			//			ConversationsActivity
			//System.out.println("onitemclick==0");
			ConversationsViewHolder conversationsViewHolder = (ConversationsViewHolder)view.getTag() ;

			//			ContentValues values = new ContentValues();
			//			values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, 0);//int
			//			int updatedRow = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
			//					values,MessageConversationTable.CONVERSATION_ID
			//					+"=?",new String[] {conversationsViewHolder.conversationList.conversationId});
			//System.out.println("Feed Synch --------------conversiotion list updated row count ----------"+updatedRow);
			//				

			if(conversationsViewHolder.conversationList.messageId.equalsIgnoreCase("-999"))
				return;

			//			if(conversationsViewHolder.conversationList.unreadMessageCount>0)
			{
				/*ContentValues values = new ContentValues();
				values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, 0);//int
				int updatedRow = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
						values,MessageConversationTable.CONVERSATION_ID
						+"=?",new String[] {conversationsViewHolder.conversationList.conversationId});
				 */
				//System.out.println("Feed Synch --------------conversiotion list updated row count ----------"+updatedRow);
			}

			dontCloseCursol=false;

			Utilities.setInt(KainatInboxAvtivity.this, "listpos", pos) ;
			int top = (view == null) ? 0 : view.getTop();
			Utilities.setInt(KainatInboxAvtivity.this, "top", top) ;
			// ...

			// restore
			//System.out.println("---conversationList.unreadMessageCount: "+conversationList.unreadMessageCount);

			//System.out.println("onitemclick==1");
			DataModel.sSelf.storeObject(DMKeys.CONVERSATION_SELECTED_LIST,
					conversationsViewHolder.conversationList);
			Intent intent1 = new Intent(KainatInboxAvtivity.this,
					ConversationsActivity.class);
			//System.out.println("onitemclick==2");
			intent1.putExtra(Constants.CONVERSATION_ID,conversationsViewHolder.conversationList.conversationId);
			intent1.putExtra(Constants.AUTO_SWITCH,false);
			if(conversationsViewHolder.conversationList.subject==null || conversationsViewHolder.conversationList.subject.trim().length()<=0)
				intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.source);
			else
				intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.subject);
			if(conversationsViewHolder.conversationList.imageFileId != null 
					&& conversationsViewHolder.conversationList.imageFileId.trim().length() > 0)
				intent1.putExtra(Constants.MUC_PIC, conversationsViewHolder.conversationList.imageFileId);
			startActivity(intent1);
		};
	};


	OnClickListener  onClickList =new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			ConversationsViewHolder conversationsViewHolder = (ConversationsViewHolder)view.getTag() ;
			if(conversationsViewHolder.conversationList.messageId.equalsIgnoreCase("-999"))
				return;

			//UPDATION CODE FOR UNREAD MESSAGE 09999871983
			/*if(conversationsViewHolder.conversationList.unreadMessageCount>0)
					{
					ContentValues values = new ContentValues();
					values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, 0);//int
					int updatedRow = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
							values,MessageConversationTable.CONVERSATION_ID
							+"=?",new String[] {conversationsViewHolder.conversationList.conversationId});

					System.out.println("Feed Synch --------------conversiotion list updated row count ----------"+updatedRow);
					}*/
			//			
			dontCloseCursol =true;
			//System.out.println("onitemclick==1");
			if(search!=null&&search.length()>0){
				search.setText("");
			}

			DataModel.sSelf.storeObject(DMKeys.CONVERSATION_SELECTED_LIST,conversationsViewHolder.conversationList);
			Intent intent1 = new Intent(KainatInboxAvtivity.this,ConversationsActivity.class);
			intent1.putExtra(Constants.CONVERSATION_ID,conversationsViewHolder.conversationList.conversationId);
			if(conversationsViewHolder.conversationList.subject == null || conversationsViewHolder.conversationList.subject.trim().length() <= 0)
				intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.source);
			else
				intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.subject);
			if(conversationsViewHolder.conversationList.imageFileId != null 
					&& conversationsViewHolder.conversationList.imageFileId.trim().length() > 0)
				intent1.putExtra(Constants.MUC_PIC, conversationsViewHolder.conversationList.imageFileId);
			finish();
			startActivity(intent1);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if(FeedRequester.feedTaskConversationList != null)
			FeedRequester.feedTaskConversationList.setHttpSynch(this);
		myScreenName(Constants.SCRTEEN_NAME_MESSAGE_BOX);
		findViewById(R.conversation.bookmark).setOnClickListener(this);
		//Commenting, as there is no use in the call
		//		Utilities.manageDB(this);
		if(RefreshService.PARSE_REFRESH_CONVERSIOTION_LIST || RefreshService.CONVERSIOTION_LIST_OLD)
		{
			activityAdapter = new ConversationsListAdapter(KainatInboxAvtivity.this, getCursor(null), true, KainatInboxAvtivity.this,null);
			listViewActivity.setAdapter(activityAdapter);	
			//	getConversationId(cursor);
			handler.post(new Runnable() {

				@Override
				public void run() {
					activityAdapter.notifyDataSetChanged() ;
					activityAdapter.notifyDataSetInvalidated();
					if(listViewActivity!=null)
						listViewActivity.invalidateViews() ;
					int pos = Utilities.getInt(KainatInboxAvtivity.this, "listpos");//, pos) ;
					int top = Utilities.getInt(KainatInboxAvtivity.this, "top");//, pos) ;
					if(listViewActivity!=null)
					{
						listViewActivity.setSelection(pos) ;
						listViewActivity.setSelectionFromTop(pos, top);
					}
				}
			});
		}
		if(!isInternetOn()){
			activityAdapter = new ConversationsListAdapter(KainatInboxAvtivity.this, getCursor(null), true, KainatInboxAvtivity.this,null);
			listViewActivity.setAdapter(activityAdapter);	
			//	getConversationId(cursor);
			handler.post(new Runnable() {

				@Override
				public void run() {
					activityAdapter.notifyDataSetChanged() ;
					activityAdapter.notifyDataSetInvalidated();
					if(listViewActivity!=null)
						listViewActivity.invalidateViews() ;
					int pos = Utilities.getInt(KainatInboxAvtivity.this, "listpos");//, pos) ;
					int top = Utilities.getInt(KainatInboxAvtivity.this, "top");//, pos) ;
					if(listViewActivity!=null)
					{
						listViewActivity.setSelection(pos) ;
						listViewActivity.setSelectionFromTop(pos, top);
					}
				}});
		}
		if(activityAdapter == null || (activityAdapter.getCursor() != null 
				&& activityAdapter.getCursor().getCount() == 0))
		{
			((Button)findViewById(R.conversation.delete)).setVisibility(View.GONE);
			//Show default screen here
			((EditText) findViewById(R.id.search_text)).setVisibility(View.GONE);
			((TextView)findViewById(R.conversation.nocontent_textview)).setVisibility(View.GONE);
		}
		else
		{
			((Button)findViewById(R.conversation.delete)).setVisibility(View.VISIBLE);
			((EditText) findViewById(R.id.search_text)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.conversation.nocontent_textview)).setVisibility(View.VISIBLE);
		}

		FeedTask.setHttpSynchRefreshCurrentView(this) ;
		ComposeService.setHttpSynchRefreshCurrentView(this) ;
		if(search==null)
			search = ((EditText) findViewById(R.id.search_text));

		load_prevoius_message = ((TextView) findViewById(R.conversation.load_prevoius_message));
		if (FeedRequester.feedTaskConversationList != null 
				&& FeedRequester.feedTaskConversationList.getStatus() != Status.FINISHED) 
		{
			load_prevoius_message.setText(getResources().getString(R.string.please_wait_while_loadin));
			search.setEnabled(false) ;
			load_prevoius_message.setVisibility(View.VISIBLE) ;
			findViewById(R.id.loading_layout).setVisibility(View.VISIBLE) ;
			//			 showNoContent();
		}
		else
		{
			load_prevoius_message.setVisibility(View.GONE) ;
			findViewById(R.id.loading_layout).setVisibility(View.GONE) ;
		}
		if(activityAdapter != null && activityAdapter.getCursor() != null 
				&& activityAdapter.getCursor().getCount() > 0)
		{
			showContent();
			if (FeedRequester.feedTaskConversationList != null
					&& FeedRequester.feedTaskConversationList.getStatus() == Status.RUNNING) 
			{
				findViewById(R.conversation.nocontent).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.conversation.nocontent_textview)).setVisibility(View.GONE);
				((TextView)findViewById(R.conversation.nocontent_textview)).setText(getResources().getString(
						R.string.refreshing_list));
			}
		}
		else
		{
			if ((FeedRequester.feedTaskConversationList != null && FeedRequester.feedTaskConversationList.getStatus() 
					== Status.FINISHED) || FeedRequester.feedTaskConversationList == null) 
				showNoContent();
		}

		//search.setDrawableClickListener(new DrawableClickListenerImp());

		setCloseSearch();
		//if(search.getText().toString()==null ||search.toString().trim().length()<=0)
		if(search.getText().toString()==null ||search.getText().toString().trim().length()<=0)
			//		search.setText("");
			removeCloseSearch();
		findViewById(R.conversation.nocontent_textview).setOnClickListener(this);

		//		Bundle extras = getIntent().getExtras();
		//		if(extras != null)
		//		{
		//		    	UIActivityManager.PushNotification = 2;
		//		    	UIActivityManager.TabValue = 1;
		//		    	UIActivityManager.Push_CONVERSATION_ID = Integer.parseInt(extras.getString("CONVERSATIONID"));
		//		        Log.i( "GCM_DATA","Extra:" + extras.getString("USERID") );
		//		        Log.i( "GCM_DATA","Extra:" + extras.getString("CONVERSATIONID") );
		//		        Log.i( "GCM_DATA","Extra:" + extras.getString("lastMsgId") );
		//		        Log.i( "GCM_DATA","Extra:" + extras.getString("transactionId") );
		//		}
	}
	public void openConversation(int cid)
	{
		//UIActivityManager.fromPushNot = 2;
		if(cursor != null)
			getConversationId(cursor, cid);
	}
	class DrawableClickListenerImp implements DrawableClickListener {
		public void onClick(DrawablePosition target) {
			//			
			switch (target) {
			case LEFT:
				search.setText("") ;
				removeCloseSearch();
				break;

			case RIGHT:
				search.setText("") ;
				removeCloseSearch();
				break;

			case BOTTOM:
				search.setText("") ;
				removeCloseSearch();
				break;

			case TOP:
				search.setText("") ;
				removeCloseSearch();
				break;

			default:
				break;
			}
		}

		@Override
		public void onClick(DrawablePosition target, View view) {
			// TODO Auto-generated method stub

		}}
	EditText search;
	Rect r = null;
	public void setCloseSearch(){

		if(search==null)
			search = ((EditText) findViewById(R.id.search_text));


		Drawable search_unsel = getResources().getDrawable(
				R.drawable.close);

		//		EditText textview = ((EditText) findViewById(R.id.search_text));
		//		search.setFocusable(false);
		Drawable[] d = search.getCompoundDrawables();

		if(r==null){
			if (d[2] != null)
				r = d[2].getBounds();
		}
		if (r != null)
			search_unsel.setBounds(r);

		//search.setCompoundDrawables(null, null, search_unsel, null);
	}
	public void removeCloseSearch(){
		//		TextView textview = ((TextView) findViewById(R.id.search_text));

		if(search==null)
			search = ((EditText) findViewById(R.id.search_text));


		//search.setCompoundDrawables(null, null, null, null);
	}
	public void showNoContent(){
		//System.out.println("-----showNoContent-");
		//		@+conversation/nocontent
		//No conversation started yet
		findViewById(R.conversation.nocontent).setVisibility(View.VISIBLE) ;
		((TextView)findViewById(R.conversation.nocontent_textview)).setText(getResources().getString(
				R.string.no_conversation_started_yet));
		findViewById(R.conversation.load_prevoius_message).setVisibility(View.GONE) ;

		findViewById(R.id.loading_layout).setVisibility(View.GONE) ;
		//		findViewById(R.id.landing_discovery_search_layout).setVisibility(View.GONE) ;
		search.setEnabled(false) ;
		listViewActivity.setVisibility(View.GONE) ;
		((Button)findViewById(R.conversation.delete)).setVisibility(View.GONE);
		((LinearLayout)findViewById(R.id.default_chat_page)).setVisibility(View.VISIBLE);
		deleteMode = false;
		((Button)findViewById(R.conversation.delete)).setBackgroundResource(R.drawable.edit_chat);
		((Button)findViewById(R.conversation.delete)).setText("Edit");
		//((ImageView)findViewById(R.id.inboxLayout_arrowImage)).setVisibility(View.VISIBLE); 
		//listViewActivity.invalidateViews() ;
	}
	public void showContent(){
		//System.out.println("-----showNoContent-");
		//		@+conversation/nocontent
		findViewById(R.conversation.nocontent).setVisibility(View.GONE) ;
		findViewById(R.conversation.load_prevoius_message).setVisibility(View.GONE) ;

		findViewById(R.id.loading_layout).setVisibility(View.GONE) ;
		listViewActivity.setVisibility(View.VISIBLE) ;
		search.setEnabled(true) ;
		//		findViewById(R.id.landing_discovery_search_layout).setVisibility(View.VISIBLE) ;//nage
		((Button)findViewById(R.conversation.delete)).setVisibility(View.VISIBLE);
		((LinearLayout)findViewById(R.id.default_chat_page)).setVisibility(View.GONE);
		//((TextView)listViewActivity.findViewById(R.conversation_list.delete)).setVisibility(View.GONE);

	}
	public Vector<String> getConversationToDelete() {
		Vector<String> cId = new Vector<String>();
		Cursor cursor = getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
				MessageTable.ITEM_SELECTED + " = ? and "+MessageTable.USER_ID+" =?"
				,
				new String[] {"1",BusinessProxy.sSelf.getUserId()+"" },
				null);

		if(cursor.getCount()>0){
			cursor.moveToFirst() ;

			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cId.add(cursor.getString(cursor
						.getColumnIndex(MessageTable.CONVERSATION_ID)));

			}
		}

		return cId;
	}

	//	public int getListCount(){
	//		
	//			
	//			Cursor cursor = getContentResolver().query(
	//					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
	//					MessageConversationTable.USER_ID+" =?", new String[] { BusinessProxy.sSelf.getUserId()+"" },
	//					MessageConversationTable.INSERT_TIME + " DESC");
	//			
	//			
	//			
	//			int c= cursor.getCount() ;
	//			cursor.close() ;
	//			return c;
	//		
	//	}
	private class RtLiveListAdaptor extends BaseAdapter {

		private Context context;
		private final LinearLayout.LayoutParams LAYOUT_PARAM = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		String menuItems[];

		public RtLiveListAdaptor(Context context, String[] menuItems) {
			this.context = context;
			this.menuItems = menuItems;
		}

		public String[] getItem() {
			return menuItems;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		public int getCount() {
			return menuItems.length;
		}

		public String getItem(int position) {
			if (position > -1 && position < menuItems.length)
				return menuItems[position];
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(parent.getContext());

			View retView = inflater.inflate(R.layout.left_menu_grid, parent, false);

			return retView;
		}
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		//		System.out.println("-------onThumbnailResponse");
		if(activityAdapter!=null){
			handler.post(new Runnable() {

				@Override
				public void run() {
					activityAdapter.notifyDataSetChanged();
				}
			});

			//			listViewActivity.invalidateViews() ;
			//			activityAdapter.notifyDataSetInvalidated();
		}
	}


	ConversationList conversationList;
	@Override
	public void onClick(View v) {
		try{
			// TODO Auto-generated method stub
			super.onClick(v);
			//	System.out.println("click consume====");
			switch (v.getId()) {

			case R.conversation.nocontent_textview:
				//			showToast("refreshing");
				FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST="0";
				FeedRequester.requestInboxConversiotionListFeed(this);
				FeedRequester.feedTaskConversationList.setHttpSynchRefresh(this);

				if (FeedRequester.feedTaskConversationList != null
						&& FeedRequester.feedTaskConversationList.getStatus() != Status.FINISHED) {
					load_prevoius_message.setText(getResources().getString(
							R.string.please_wait_while_loadin));
					search.setEnabled(false) ;
					load_prevoius_message.setVisibility(View.VISIBLE) ;
					findViewById(R.id.loading_layout).setVisibility(View.VISIBLE) ;
					//				 showNoContent();
				}else{
					load_prevoius_message.setVisibility(View.GONE) ;
					findViewById(R.id.loading_layout).setVisibility(View.GONE) ;
				}

				break;
			case R.conversation_list.loading_text:
				if(v.getTag()!=null){
					FeedRequester.requestInboxConversiotionListFeedMore(this,Utilities.getString(this, Constants.CONVERSATION_LIST_MFUID_FOR_MORE) );
					FeedRequester.feedTaskConversationListMore.setHttpSynchRefresh(this);

					listViewActivity.invalidateViews();
				}else
				{
					//				showToast("ID is null");
				}
				break;
			case R.conversation_list.thumb:
				ParticipantInfo participantInfo = (ParticipantInfo)v.getTag() ;
				if(participantInfo.equals("-1")){
					showToast("Can't view profile");
				}else{
					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, participantInfo.userName);
					Intent intent = new Intent(KainatInboxAvtivity.this, ProfileViewActivity.class);
					intent.putExtra("USERID", participantInfo.userName);
					intent.putExtra("CallFrom",1);
					startActivity(intent);
				}
				break;
			case R.conversation.start_group_chat:
//				Intent intent = new Intent(KainatInboxAvtivity.this,
//						BuddyActivity.class);
//				//intent.putExtra(BuddyActivity.SCREEN_MODE,
//				//BuddyActivity.MODE_ONLY_FRIENDS);
//				intent.putExtra(BuddyActivity.SCREEN_MODE,
//						BuddyActivity.MODE_NORMAL);
//				intent.putExtra("MULTISELECTED", true);
//				intent.putExtra("ADDCONVERSATIONS", true);
//				intent.putExtra("GOTO", "ADDCONVERSATIONS");
//				intent.putExtra("CHAT", "CHATBUDDY");
//				//			.getByteExtra(SCREEN_MODE, MODE_NORMAL);
//				startActivityForResult(intent, 1);
				break;
			case R.conversation.bookmark:
				//				intent = new Intent(this, BookmarkActivity.class);
				//				startActivityForResult(intent, "BookmarkActivity".hashCode());//(intent);c
				break;
			case R.conversation.delete:


				if(!deleteMode){
					deleteMode = true;
					//((ImageView)findViewById(R.id.inboxLayout_arrowImage)).setVisibility(View.GONE);          
					listViewActivity.invalidateViews() ;

					//			((Button)v).setText(" Done ");
					((Button)findViewById(R.conversation.delete)).setBackgroundResource(R.drawable.doneinconversation);
					((Button)findViewById(R.conversation.delete)).setText("Done");
				}else
				{
					//((ImageView)findViewById(R.id.inboxLayout_arrowImage)).setVisibility(View.VISIBLE);    
					((Button)findViewById(R.conversation.delete)).setBackgroundResource(R.drawable.edit_chat);
					((Button)findViewById(R.conversation.delete)).setText("Edit");
					Vector<String> cId 	= getConversationToDelete() ;

					if(cId!=null && cId.size()>0){
						conversationList = (ConversationList)v.getTag() ;
						String url = "http://"+Urls.TEJAS_HOST+"/tejas/rest/rtmessaging/deleteConversations";///#USERID#";///#CONVERSATIONID#" ;

						url = url.replace("#USERID#", ""+BusinessProxy.sSelf.getUserId()) ;
						//				url = url.replace("#CONVERSATIONID#", ""+conversationList.conversationId);

						request = new Request(ID_DELETE_CONVERSATION, url,conversationList);
						request.cId = cId ;
						//				request.execute("LikeDislike");

						final View view =v ;
						//				DialogInterface.OnClickListener deleteHandler = null;
						//						final String user = "element.name";
						DialogInterface.OnClickListener deleteHandler = new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								deleteMode = false;
								listViewActivity.invalidateViews() ;
								//						((Button)view).setText(" Delete ");
								Vector<String> cId 	= new Vector<String>();
								for (int i = 0; i < request.cId.size(); i++) {
									//							String s = request.cId.get(i);
									if(request.cId.get(i).startsWith("NP")){
										cId.add(request.cId.get(i)) ;
										getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, MessageConversationTable.CONVERSATION_ID+" = ? ", new String[] {request.cId.get(i)});
									}
								}
								for (int i = 0; i < cId.size(); i++) {
									request.cId.remove(cId.get(i)) ;
								}
								if(request.cId.size()>0){
									if(!KainatInboxAvtivity.this.checkInternetConnection()){
										KainatInboxAvtivity.this.networkLossAlert();
										return ;
									}
									request.execute("LikeDislike");
								}
								setUnCheckAll();

							}
						};
						DialogInterface.OnClickListener	deleteHandlerNegative = new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								deleteMode = false;
								listViewActivity.invalidateViews() ;
								//						((Button)view).setText(" Delete ");
								((Button)findViewById(R.conversation.delete)).setBackgroundResource(R.drawable.edit_chat);
								((Button)findViewById(R.conversation.delete)).setText("Edit");
								//((ImageView)findViewById(R.id.inboxLayout_arrowImage)).setVisibility(View.VISIBLE); 
								setUnCheckAll();
							}
						};

						showAlertMessage("Confirm", "This will delete all messages from this conversation. Do you want to proceed?", new int[] { DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE }, new String[] { "Yes",
						"No" }, new DialogInterface.OnClickListener[] { deleteHandler, deleteHandlerNegative });

					}else
					{
						deleteMode = false;
						listViewActivity.invalidateViews() ;
						//					((Button)v).setText(" Delete ");
						setUnCheckAll();
					}





				}

				break;
			case R.conversation_list.delete:
				try{
					conversationList = (ConversationList)v.getTag() ;
					String url = "http://"+Urls.TEJAS_HOST+"/tejas/rest/rtmessaging/deleteMessagesInRange" ;

					url = url.replace("#USERID#", ""+BusinessProxy.sSelf.getUserId()) ;
					url = url.replace("#CONVERSATIONID#", ""+conversationList.conversationId);

					String lastMsgId = "0" ;
					String lastMfuId = "0" ;
					String[] columns = new String[] { MessageTable.MESSAGE_ID,MessageTable.MFU_ID };
					Cursor cursor = getContentResolver().query(
							MediaContentProvider.CONTENT_URI_INBOX, columns,
							MessageTable.CONVERSATION_ID + "= ? and "+MessageTable.USER_ID + " =? and "+MessageTable.MFU_ID+" != ?",
							new String[]{conversationList.conversationId,
									""+BusinessProxy.sSelf.getUserId(),"-1"},
									MessageTable.INSERT_TIME + " ASC");

					if(cursor.getCount()<=0){
						cursor.close();
						cursor = getContentResolver().query(
								MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, columns,
								MessageTable.CONVERSATION_ID + "= ? and "+MessageTable.USER_ID + " =? and "+MessageTable.MFU_ID+" != ?",
								new String[]{conversationList.conversationId,
										""+BusinessProxy.sSelf.getUserId(),"-1"},
										MessageTable.INSERT_TIME + " ASC");
					}
					if(cursor.getCount()>0){
						cursor.moveToLast();
						int dataColumnIndex = cursor.getColumnIndex(MessageTable.MESSAGE_ID);
						lastMsgId = cursor.getString(dataColumnIndex);
						lastMfuId = cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
					}
					request = new Request(ID_DELETE_CONVERSATION, url,conversationList);
					//System.out.println("-----------max lastMsgId : "+lastMsgId);
					//System.out.println("-----------max lastMfuId : "+lastMfuId);
					request.startMessageIdIndex = lastMsgId ;
					//					showToast("max : "+lastMsgId);
					int totMsg = cursor.getCount() ;
					if(cursor.getCount()>0){
						cursor.moveToFirst();
						int dataColumnIndex = cursor.getColumnIndex(MessageTable.MESSAGE_ID);
						lastMsgId = cursor.getString(dataColumnIndex);
						//				lastMfuId = cursor.getString(cursor.getColumnIndex(MessageTable.MESSAGE_ID));
					}
					cursor.close();
					//			request.endMessageIdIndex = lastMsgId ;
					//System.out.println("-----------min lastMsgId : "+lastMsgId);
					//	System.out.println("-----------min totMsg : "+totMsg);

					//			showToast("min : "+lastMsgId);

					//			if(request.startMessageIdIndex!=null && request.startMessageIdIndex.length()>0 && request.endMessageIdIndex!=null && request.endMessageIdIndex.length()>0)
					//			request.execute("LikeDislike");

					//
					DialogInterface.OnClickListener deleteHandler = new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							//					deleteMode = false;
							if(conversationList.conversationId.startsWith("NP") || (request.startMessageIdIndex.equalsIgnoreCase("0")))
							{

								getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, MessageConversationTable.CONVERSATION_ID+" = ? ", new String[] {conversationList.conversationId});
								getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX, MessageTable.CONVERSATION_ID+" = ? ", new String[] {conversationList.conversationId});

								//						getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, MessageConversationTable.CONVERSATION_ID+" = ? ", new String[] {conversationList.conversationId});
								if(activityAdapter!=null && activityAdapter.getCursor()!=null){
									activityAdapter.getCursor().requery() ;
									activityAdapter.notifyDataSetChanged();
								}
								if(listViewActivity!=null)
									listViewActivity.invalidate();
								if(activityAdapter==null || activityAdapter.getCursor().getCount()==0)
								{
									((Button)findViewById(R.conversation.delete)).setVisibility(View.GONE);
									((LinearLayout)findViewById(R.id.default_chat_page)).setVisibility(View.VISIBLE);
								}
								else
								{
									((Button)findViewById(R.conversation.delete)).setVisibility(View.VISIBLE);
									((LinearLayout)findViewById(R.id.default_chat_page)).setVisibility(View.GONE);
								}

							}
							else{
								if(!KainatInboxAvtivity.this.checkInternetConnection()){
									KainatInboxAvtivity.this.networkLossAlert();
									return ;
								}
								request.execute("LikeDislike");
							}
							if(activityAdapter==null || activityAdapter.getCursor().getCount()==0)
							{
								((Button)findViewById(R.conversation.delete)).setVisibility(View.GONE);
								((LinearLayout)findViewById(R.id.default_chat_page)).setVisibility(View.VISIBLE);
							}
							else
							{
								((Button)findViewById(R.conversation.delete)).setVisibility(View.VISIBLE);
								((LinearLayout)findViewById(R.id.default_chat_page)).setVisibility(View.GONE);
							}

						}
					};
					DialogInterface.OnClickListener	deleteHandlerNegative = new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							//					deleteMode = false;
							//					listViewActivity.invalidateViews() ;
							//					((Button)view).setText(" Delete ");
							setUnCheckAll();

						}
					};

					if(request.startMessageIdIndex!=null && request.startMessageIdIndex.length()>0 )
						showAlertMessage("Confirm", "This will delete all messages from this conversation. Do you want to proceed?", new int[] { DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE }, new String[] { "Yes",
						"No" }, new DialogInterface.OnClickListener[] { deleteHandler, deleteHandlerNegative });

				}catch (Exception e) {

				}
				break;
			case R.id.discovery_messageDisplay_FeatureUserName:
				//				FeaturedUserBean bean=(FeaturedUserBean)v.getTag();
				//
				//				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,bean.displayName );
				//				DataModel.sSelf.storeObject(DMKeys.XHTML_URL, bean.profileUrl);
				//				intent = new Intent(this, WebviewActivity.class);
				//				intent.putExtra("PAGE", (byte) 1);
				//				startActivity(intent);
				break;

			case R.id.discoveryRow_FeatureUserIcon:
				//				bean=(FeaturedUserBean)v.getTag();
				//
				//				if(bean.displayName != null)
				//					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,bean.displayName );
				//				if(bean.profileUrl != null)
				//					DataModel.sSelf.storeObject(DMKeys.XHTML_URL, bean.profileUrl);
				//				intent = new Intent(this, WebviewActivity.class);
				//				intent.putExtra("PAGE", (byte) 1);
				//				startActivity(intent);
				break;
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	Long datetime;
	@SuppressLint("ResourceAsColor")
	@Override
	public void viewConversations(View convertView, Context context, ConversationList conversationList){

		ConversationsViewHolder conversationsViewHolder = (ConversationsViewHolder)convertView.getTag() ;
		conversationsViewHolder.conversationList = conversationList;
		convertView.setTag(conversationsViewHolder);

		conversationsViewHolder.deleteConversations.setTag(conversationList) ;

		conversationsViewHolder.deleteConversations.setOnClickListener(this) ;
		if((conversationList.messageId.equalsIgnoreCase("-999") )//&& conversationList.mfuId.equalsIgnoreCase("-999"))
				)
		{

			conversationsViewHolder.datacontainer.setVisibility(View.VISIBLE) ;
			//			conversationsViewHolder.datacontainer.setOnClickListener(this) ;

			//			conversationsViewHolder.left.setVisibility(View.GONE) ;
			conversationsViewHolder.right.setVisibility(View.GONE) ;
			conversationsViewHolder.viewAll.setVisibility(View.GONE) ;
			conversationsViewHolder.viewAllRight.setVisibility(View.GONE) ;
			conversationsViewHolder.progressBar.setVisibility(View.GONE) ;
			conversationsViewHolder.loadingText.setVisibility(View.VISIBLE) ;
			conversationsViewHolder.loadingText.setOnClickListener(this) ;
			conversationsViewHolder.loadingText.setTag(conversationList.mfuId);
			if(FeedRequester.feedTaskConversationListMore!=null&&FeedRequester.feedTaskConversationListMore.getStatus()==Status.RUNNING){
				conversationsViewHolder.progressBar.setVisibility(View.VISIBLE) ;
				conversationsViewHolder.loadingText.setVisibility(View.GONE) ;
				conversationsViewHolder.loadingText.setTag(conversationList.mfuId);
			}
			return;
		}
		conversationsViewHolder.datacontainer.setVisibility(View.GONE) ;
		//Deleted from layout
		//		conversationsViewHolder.left.setVisibility(View.VISIBLE) ;

		conversationsViewHolder.right.setVisibility(View.VISIBLE) ;
		conversationsViewHolder.viewAll.setVisibility(View.VISIBLE) ;
		conversationsViewHolder.viewAllRight.setVisibility(View.VISIBLE) ;

		ParticipantInfo senderParticipantInfo = null ;
		ParticipantInfo otherParticipantInfo = null ;
		String partic = "" ;
		String u = "" ;
		//if(conversationsViewHolder.conversationList.subject==null){
		//		if(conversationList.conversationId.equalsIgnoreCase("200505")){
		//			System.out.println("ssssssssss");
		//		}
		if (conversationList.participantInfo != null
				&& conversationList.participantInfo.size() > 0) {
			//			if(conversationList.conversationId.equalsIgnoreCase("502434")){
			//			System.out.println("---------participantInfoStr view: "+conversationList.conversationId);
			//			System.out.println("---------participantInfoStr view: "+conversationList.participantInfo);
			//			}
			//			System.out.println("-------------messageViewHolder.message.participantInfo : "+);

			//			if(conversationList.conversationId.equalsIgnoreCase("7187104"))
			//			{
			//				System.out.println("ddddddddd");
			//			}
			for (int i = 0; i < conversationList.participantInfo.size(); i++) {
				ParticipantInfo participantInfo = conversationList.participantInfo.get(i);
				//					if(conversationList.conversationId.equalsIgnoreCase("7187104"))
				//					System.out.println("-----participantInfo : "+participantInfo);
				if(participantInfo.userName==null)continue ;
				if(!participantInfo.userName.toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toLowerCase())){
					if(participantInfo.firstName!=null && participantInfo.lastName!=null)
						partic = partic + participantInfo.firstName + " "+participantInfo.lastName +";";
					else if(participantInfo.firstName!=null && participantInfo.firstName.length() > 0)
						partic = partic + participantInfo.firstName+";";
					else if(participantInfo.lastName!=null && participantInfo.lastName.length()>0)
						partic = partic + participantInfo.lastName+";";
					else
						partic = partic + participantInfo.userName +";";
					u = participantInfo.userName ;

					otherParticipantInfo = participantInfo ;
				}
				if(participantInfo.isSender)
					senderParticipantInfo = participantInfo ;
				if(partic==null || partic.trim().length()<=0){	
					if(participantInfo.firstName!=null && participantInfo.lastName!=null)
						partic = partic + participantInfo.firstName + " "+participantInfo.lastName +";";
					else if(participantInfo.firstName!=null && participantInfo.firstName.length()>0)
						partic = partic + participantInfo.firstName+";";
					else
						partic = partic + participantInfo.userName +";";
				}
			}
		} 
		//	}

		if(otherParticipantInfo==null)
			otherParticipantInfo =senderParticipantInfo ;
		if(partic!=null&&partic.endsWith(";"))
			partic = partic.substring(0, partic.length()-1) ;

		if(senderParticipantInfo==null){
			senderParticipantInfo = new ParticipantInfo();
			senderParticipantInfo.userName = "-1" ;
		}
		conversationsViewHolder.me_image.setOnClickListener(this);
		conversationsViewHolder.me_image.setBackgroundDrawable(null) ;
		conversationsViewHolder.me_image.setImageBitmap(null) ;
		boolean isOnline = false ;
		if(otherParticipantInfo!=null&&!otherParticipantInfo.userName.equalsIgnoreCase("-1") && conversationList!=null&conversationList.isGroup==0){

			UserStatus userStatus = null ;
			if(ImageDownloader.getUserInfo(otherParticipantInfo.userName)!=null)
			{
				userStatus =  ImageDownloader.getUserInfo(otherParticipantInfo.userName);
				// System.out.println("userStatus:"+userStatus.toString());
			}
			if(userStatus==null)
				isOnline=	BusinessProxy.sSelf.isFriendaOnline(otherParticipantInfo.userName);//otherParticipantInfo.userName) ;
			else if(userStatus.onLineOffline==1)
				isOnline = true ;

			conversationsViewHolder.me_image.setBackgroundResource(R.drawable.def2) ;
			ImageDownloader imageManager = new ImageDownloader(2);
			if(otherParticipantInfo!=null && otherParticipantInfo.userName !=null)
			{
				conversationsViewHolder.me_image.setImageUrl(otherParticipantInfo.userName);
				imageManager.download(otherParticipantInfo.userName, conversationsViewHolder.me_image,
						KainatInboxAvtivity.this,ImageDownloader.TYPE_THUMB_BUDDY);
		
			///	imageLoader.displayImage(otherParticipantInfo.userName, conversationsViewHolder.me_image, options);
			
			}
			//conversationsViewHolder.me_image.setImageResource(R.drawable.profile);
		}else
		{
			//			if(conversationList.isGroup==0)
			//			conversationsViewHolder.me_image.setBackgroundResource(R.drawable.watch) ;
			conversationsViewHolder.me_image.setImageUrl("");
		}
		if(conversationList.isGroup == 1){
			if(conversationList.imageFileId != null){
//				Log.i(TAG, "viewConversations :: MUC Image URL : "+conversationList.imageFileId);
				conversationsViewHolder.me_image.setImageResource(R.drawable.groupicon);
				ImageDownloader imageManager = new ImageDownloader(2);
				conversationsViewHolder.me_image.setImageUrl(conversationList.imageFileId);
				imageManager.download(conversationList.imageFileId, conversationsViewHolder.me_image,
						KainatInboxAvtivity.this, ImageDownloader.TYPE_THUMB_COMMUNITY);
			}
			else
				conversationsViewHolder.me_image.setImageResource(R.drawable.groupicon);
			conversationsViewHolder.me_image.setOnClickListener(null);
		}else
			conversationsViewHolder.me_image.setOnClickListener(this);

		conversationsViewHolder.me_image.setVisibility(View.VISIBLE);
		conversationsViewHolder.me_image.setTag(otherParticipantInfo);

		//		if( conversationList.isGroup==0)
		//			if(isOnline)
		//			{
		//				//        	conversationsViewHolder.online_offline.setVisibility(View.VISIBLE);
		//				conversationsViewHolder.online_offline.setBackgroundResource(R.drawable.online_icon_chat) ;
		//			}else{
		//				//        	conversationsViewHolder.online_offline.setVisibility(View.VISIBLE);
		//				conversationsViewHolder.online_offline.setBackgroundResource(R.drawable.offline_icon_chat) ;
		//			}
		//		else
		//			conversationsViewHolder.online_offline.setVisibility(View.GONE);

		if (conversationList.datetime != null) {
			conversationsViewHolder.datetime.setVisibility(View.VISIBLE);



			conversationsViewHolder.datetime.setText(Utilities.compareDate(conversationList.inserTime,KainatInboxAvtivity.this));
			//conversationsViewHolder.datetime
			//.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else 
		{
			conversationsViewHolder.datetime.setVisibility(View.GONE);
		}


		//		if (conversationList.source != null) 
		//		{
		//			conversationsViewHolder.source.setVisibility(View.VISIBLE);
		//			if(conversationList.source.indexOf(":")!= -1)
		//				conversationsViewHolder.source.setText(BusinessProxy.sSelf.parseNameInformation(conversationList.source));
		//			else
		//				conversationsViewHolder.source.setText(BusinessProxy.sSelf.parseNameInformation(conversationList.source));
		//			
		//			conversationsViewHolder.source
		//					.setTextSize(BusinessProxy.sSelf.textFontSize);
		//		} else {
		//			conversationsViewHolder.source.setVisibility(View.GONE);
		//		}

		conversationsViewHolder.source.setVisibility(View.VISIBLE);

		if(conversationList.subject!=null && conversationList.subject.length() > 0)
			partic=conversationList.subject;

		if (partic != null && partic.length()>0) {
			if(conversationList.subject==null ||(conversationList.subject!=null&&conversationList.subject.trim().length()==0))
				partic=partic.toLowerCase();
			String dist[] = Utilities.split(new StringBuffer(partic), ";");
			//if(dist.length>1){
			Collections.sort(Arrays.asList(dist));

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < dist.length; i++) {
				String intValue = dist[i].trim();
				if(SettingData.sSelf.getDisplayName().trim() != null && !SettingData.sSelf.getDisplayName().trim().equalsIgnoreCase(intValue) || dist.length == 1)
				{
					builder.append((Utilities.capitalizeString(intValue)).trim());
					if (i < dist.length - 1) {
						builder.append(", ");
					}
				}
			} 
			if(conversationList.subject==null ||(conversationList.subject!=null&&conversationList.subject.trim().length()==0))
				partic=builder.toString();
			//}
			if(partic.trim().startsWith(","))
			{
				partic = partic.trim();
				partic.subSequence(1, partic.length()-1);
			}
			conversationsViewHolder.source.setText(partic);
		}
		else{
			if(conversationList.source!=null)
				conversationsViewHolder.source.setText(BusinessProxy.sSelf.parseNameInformation(conversationList.source));
		}

		String txt = conversationList.msgTxt ;
		if (txt != null && txt.trim().length()>0) {
			conversationsViewHolder.messageView.setVisibility(View.VISIBLE);
			if(txt.indexOf("<?xml version=")!=-1)
				txt = "";//"This is System Message" ;

			//will remove this login this temp because  m not getting notification flag in feed
			if(txt.startsWith("{")){
				Utilities.jsonParserEngine(txt);
				if( Utilities.accept()!=null){
					txt =""; //"This is Friend Request" ;
				}
			}

			//			Spanned spannedContent = getSmiledText(this, txt);
			//			conversationsViewHolder.messageView.setText(spannedContent,
			//					BufferType.SPANNABLE);
			//			Spanned spannedContent = getSmiledText(this, txt);
			//			conversationsViewHolder.messageView.setText(spannedContent ,BufferType.SPANNABLE);

			if(txt!=null && txt.length()<50){
				Spanned spannedContent = getSmiledText(this, txt);
				conversationsViewHolder.messageView.setText(spannedContent,
						BufferType.SPANNABLE);
			}else
				conversationsViewHolder.messageView.setText(txt);

			//			conversationsViewHolder.messageView.setText(conversationList.msgTxt);
			conversationsViewHolder.messageView
			.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			conversationsViewHolder.messageView.setVisibility(View.GONE);
		}
		//if (conversationList.conversationId != null) 
		{
			conversationsViewHolder.conversation_id.setVisibility(View.VISIBLE);

			if(conversationList.subject == null)
				conversationList.subject = "" ;
			if(Logger.CHEAT){
				conversationsViewHolder.conversation_id.setText(" comment:"+conversationList.comments+" cid:"+conversationList.conversationId+" mfuid:"+conversationList.mfuId+" isL:"+conversationList.isLastMessage+" left:"+conversationList.isLeft+" ig:"+conversationList.isGroup);
				conversationsViewHolder.conversation_id
				.setTextSize(BusinessProxy.sSelf.textFontSize);
			}else{
				conversationsViewHolder.conversation_id.setVisibility(View.GONE);
			}
		} 
		//		else {
		//			conversationsViewHolder.conversation_id.setVisibility(View.GONE);
		//		}
		int totparti = 0 ;
		if (conversationList.participantInfo != null
				&& conversationList.participantInfo.size() > 0) {
			totparti=conversationList.participantInfo.size()  ;
		}
		conversationsViewHolder.unread_count.setVisibility(View.GONE);
		conversationsViewHolder.unread.setVisibility(View.GONE);

		//conversationsViewHolder.messageView.
		//((LinearLayout)findViewById(R.layout.conversations_list_screen)).setBackgroundColor(R.drawable.landingpagebackground);
		if (conversationList.unreadMessageCount > 0) 
		{
			convertView.setBackgroundResource(R.drawable.landingpagebackground);
			//			convertView.setBackgroundResource(R.color.new_message_color);
			//((LinearLayout)findViewById(R.layout.conversations_list_screen)).setBackgroundColor(getResources().getColor(R.color.red));
			conversationsViewHolder.unread.setVisibility(View.VISIBLE);
			conversationsViewHolder.unread_count.setVisibility(View.VISIBLE);//VISIBLE
			conversationsViewHolder.unread_count.setText(ConversationList.unreadMessageCount+":"+conversationList.isGroup+":"+totparti);
			if(ConversationList.unreadMessageCount > 50)
			{
				conversationsViewHolder.unread_count.setText("50+");
			}
			else
			{
				conversationsViewHolder.unread_count.setText(""+ConversationList.unreadMessageCount);
			}
			//			conversationsViewHolder.unread_count.setText("1+");
			conversationsViewHolder.unread_count.setTextSize(BusinessProxy.sSelf.textFontSize);
			//Commented to show different color for unread messages
			//			conversationsViewHolder.messageView.setTypeface(Typeface.DEFAULT_BOLD);
			//			conversationsViewHolder.messageView.setTextColor(R.color.black);
			conversationsViewHolder.unread_count.addTextChangedListener(new RTextWatcher(conversationsViewHolder.unread_count));

		}
		else
		{
			convertView.setBackgroundResource(R.drawable.landingpagebackground);
		}
		//else

		if(deleteMode){
			conversationsViewHolder.deleteConversations.setVisibility(View.VISIBLE) ;
		}else
			conversationsViewHolder.deleteConversations.setVisibility(View.GONE) ;

		//		if(conversationList.conversationId.equalsIgnoreCase("502422")){
		//			System.out.println("----------------------------");
		//		}
		conversationsViewHolder.image.setVisibility(View.GONE);
		conversationsViewHolder.videoIcon.setVisibility(View.GONE);
		conversationsViewHolder.voice.setVisibility(View.GONE);
		conversationsViewHolder.animIcon.setVisibility(View.GONE);
		if (conversationList.image_content_thumb_urls != null&& conversationList.image_content_thumb_urls.trim().length()>0) {
			conversationsViewHolder.image.setVisibility(View.VISIBLE);
		}
		if (conversationList.video_content_thumb_urls != null&& conversationList.video_content_thumb_urls.trim().length()>0) {
			conversationsViewHolder.videoIcon.setVisibility(View.VISIBLE);
		}
		if (conversationList.voice_content_urls != null && conversationList.voice_content_urls.trim().length()>0) {
			conversationsViewHolder.voice.setVisibility(View.VISIBLE);
		}
		if(conversationList.aniType!=null && conversationList.aniType.equalsIgnoreCase("animation") && conversationList.aniValue!=null && conversationList.aniValue.length()>0 ){
			conversationsViewHolder.animIcon.setVisibility(View.VISIBLE);
		}
		//		if (conversationList.contentBitMap != null
		//				&& conversationList.contentBitMap.indexOf("RTML") != -1)// txt.indexOf("<?xml version=")
		//																// != -1)
		//		{
		////			txt = "You have received a system notification";
		//			conversationsViewHolder.messageView.setVisibility(View.VISIBLE);
		//			conversationsViewHolder.messageView.setText("You have received a system notification");
		//		}

		//		conversationsViewHolder.setOnItemClickListener(onitemClickList);
		//		convertView.setOnClickListener(onClickList);
		//		System.out.println("---------conversationList.voice_content_urls- :"+conversationList.voice_content_urls);
	}


	public void setCheclUnCheck(String conversationId, boolean isChecked){
		ContentValues values = new ContentValues();
		if(!isChecked)
			values.put(MessageConversationTable.ITEM_SELECTED, 0);//int
		else
			values.put(MessageConversationTable.ITEM_SELECTED, 1);//int
		int updatedRow = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
				values,MessageConversationTable.CONVERSATION_ID
				+"=?",new String[] {conversationId});
		activityAdapter.getCursor().requery() ;
		activityAdapter.notifyDataSetChanged();
		//			
	}
	public void setUnCheckAll(){
		ContentValues values = new ContentValues();

		values.put(MessageConversationTable.ITEM_SELECTED, 0);//int
		int updatedRow = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
				values,null,null);

		//			
	}
	class RTextWatcher implements TextWatcher
	{
		TextView unread_count ;
		String t = null ;
		public RTextWatcher(TextView unread_count){
			this.unread_count=unread_count ;
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			t = s.toString() ;


			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			//			if(!s.toString().equals(t))
			//			Utilities.startAnimition(InboxAvtivity.this, unread_count, R.anim.grow_from_midddle) ;
		}
	}

	@Override
	public void onResponseSucess(final String responseStr, final int requestForID) {

		if (FeedRequester.feedTaskConversationList != null
				&& FeedRequester.feedTaskConversationList.getStatus() == Status.FINISHED || (responseStr!=null&&responseStr.equalsIgnoreCase("REFRESH VIEW"))
				|| requestForID==Constants.FEED_CONVERSATION_LIST) 
			handler.post(new Runnable() {
				@Override
				public void run() {
					//					if(Logger.ENABLE)
					//						System.out.println("------onResponseSucess inboxavitity--"
					//								+ responseStr + "   : " + FeedTask.refreshList);
					try {
						if(activityAdapter==null || activityAdapter.getCount()<=0){
							activityAdapter = new ConversationsListAdapter(KainatInboxAvtivity.this, getCursor(null), true, KainatInboxAvtivity.this,null);
							listViewActivity.setAdapter(activityAdapter);	
							///
							///
							///
							///
							getConversationId(cursor, 0);
						}else{
							activityAdapter.getCursor().requery();
							activityAdapter.notifyDataSetChanged();
							listViewActivity.invalidate();
						}


						//					activityAdapter.getCursor().requery();
						//					activityAdapter.getCursor().moveToLast();
						//					activityAdapter.notifyDataSetInvalidated();
						//					if (activityAdapter != null) {
						//						activityAdapter.getCursor().requery();
						//						activityAdapter.notifyDataSetChanged();
						//					}
						if(activityAdapter!=null && activityAdapter.getCount()>0){
							showContent();
						}else{
							showNoContent();
						}
						listViewActivity.invalidate();

					} catch (Exception e) {
						e.printStackTrace();
					}

					if (Constants.FEED_CONVERSATION_LIST == requestForID) {
						load_prevoius_message.setText(getResources().getString(
								R.string.load_prevoius_message));
						load_prevoius_message.setVisibility(View.GONE);

						findViewById(R.id.loading_layout).setVisibility(View.GONE) ;
					}
				}
			});
	}

	//	@Override
	//	public void onResponseSucess(String responseStr, final int requestForID) {
	//		handler.post(new Runnable() {			
	//			@Override
	//			public void run() {
	//				if("user".hashCode()==requestForID){
	//
	////					listViewActivity.onRefreshComplete();
	//					final int oldPos = listViewActivity.getFirstVisiblePosition() ;
	//					activityAdapter.getCursor().close();
	//					activityAdapter = new ConversationsListAdapter(InboxAvtivity.this, getCursor(null), true, InboxAvtivity.this,null);
	//					listViewActivity.setAdapter(activityAdapter);					
	//					
	//					if(oldPos>1)
	//						listViewActivity.setSelection(oldPos) ;
	//					}	
	////				listViewActivity.hideRefresh();
	//			}
	//		});
	//	}

	@Override
	public void onResponseSucess(Object responseStr, final int requestForID) {
		handler.post(new Runnable() {			
			@Override
			public void run() {
				if("user".hashCode()==requestForID){
					//					listViewActivity.onRefreshComplete();
					final int oldPos = listViewActivity.getFirstVisiblePosition() ;
					activityAdapter.getCursor().close();
					activityAdapter = new ConversationsListAdapter(KainatInboxAvtivity.this, getCursor(null), true, KainatInboxAvtivity.this,null);
					listViewActivity.setAdapter(activityAdapter);					
					getConversationId(cursor, 0);
					if(oldPos>1)
						listViewActivity.setSelection(oldPos) ;				
				}								

			}
		});
	}
	@Override
	public void viewMessage(View convertView, Context context, Message message) {
		// TODO Auto-generated method stub

	}
	public void onBackPressed() {
		
		Intent intent1 = new Intent(KainatInboxAvtivity.this,KainatHomeActivity.class);
		startActivity(intent1);
		/*
		if(UIActivityManager.sharingFromOutside == 1)
		{
			UIActivityManager.sharingFromOutside = 0;
			finish();
		}
	//	this.getParent().onBackPressed(); 
		if(deleteMode){
			deleteMode = false;
			listViewActivity.invalidateViews() ;
			//			((Button)view).setText(" Delete ");
			((Button)findViewById(R.conversation.delete)).setBackgroundResource(R.drawable.edit_chat);
			((Button)findViewById(R.conversation.delete)).setText("Edit");
			//((ImageView)findViewById(R.id.inboxLayout_arrowImage)).setVisibility(View.VISIBLE); 
			setUnCheckAll();
			return;
		}
		if(onBackPressedCheck())return;
		dontCloseCursol=false;
		//		if(activityAdapter!=null){
		//			activityAdapter.getCursor().close() ;
		//		}
		//		finish();
		//		pushNewScreen(KainatHomeActivity.class, false);
		//		Intent intent1 = new Intent(KainatInboxAvtivity.this,
		//				KainatHomeActivity.class);
		//		startActivity(intent1);
		BusinessProxy.sSelf.newMessageCount = 0;
		//		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		HttpSynch.getInstance().cancel();
		moveTaskToBack(true);
	*/}
	final protected DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER_SUB = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// System.out.println("-----------PROGRESS_CANCEL_HANDLER_SUB-------------");
			if (request != null && !request.isCancelled()) {
				request.cancel(true);
			}
		}
	};
	boolean cancelOperation ;
	private HttpResponse getResponse(final String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(
				"RT-APP-KEY",
				HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(),
						SettingData.sSelf.getPassword()));
		httpPost.setHeader("Content-Type", "application/xml"); 
		httpPost.setHeader(
				"password",
				HttpHeaderUtils.encriptPass(

						SettingData.sSelf
						.getPassword()));
		httpPost.setHeader(
				"authkey","RTAPP@#$%!@");
		HttpResponse response = null;
		try {
			// System.out.println("----------httpget------"+httpget);
			response = client.execute(httpPost);
			if (cancelOperation)
				return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	Request request;
	public class Request extends AsyncTask<String, Integer, String> {

		int type = 0;
		String url;
		ConversationList conversationList;
		Vector<String> cId ;
		String startMessageIdIndex = "" ;
		//		String endMessageIdIndex = "" ;
		public Request(int type, String url,ConversationList conversationList) {
			this.conversationList = conversationList ;
			this.type = type;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {

			showAnimationDialogNonUIThread("", "Wait processing request...",
					true, PROGRESS_CANCEL_HANDLER_SUB);
		}

		protected void onPostExecute(String result) {

			activityAdapter.getCursor().requery() ;
			activityAdapter.notifyDataSetChanged();

			dismissAnimationDialog();
			DialogInterface.OnClickListener handler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					lDialog.dismiss();
				}
			};

			if(result!=null && result.length() > 200 )
				result = result.substring(0,199) ;
			if (result != null) {
				showAlertMessage("RockeTalk", result,
						new int[] { DialogInterface.BUTTON_POSITIVE },
						new String[] { "Ok" },
						new DialogInterface.OnClickListener[] { handler });
			} else
				showSimpleAlert("Error", "Please try after sometime!!!");

			if(activityAdapter.getCursor().getCount()<=0){
				showNoContent() ;
			}else
				showContent() ;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String ss = "" ;
				//				for (int i = 0; i < cId.size(); i++) {
				//					
				//					
				////					+" �<conversationIds>"+conversationList.conversationId+"</conversationIds>"
				//						if(!cId.get(i).startsWith("NP"))
				//						ss = ss+ " �<conversationIds>"+cId.get(i)+"</conversationIds>" ;
				//					}
				//				
				//				<deleteRangeMessageRequest>
				//				  <userId>10259985</userId>
				//				  <endMessageIdIndex>2150050283</endMessageIdIndex>
				//				<conversationId>790</conversationId>
				//				</deleteRangeMessageRequest>
				//				
				//				
				String content  = "<deleteRangeMessageRequest>"
						+" �<userId>"+BusinessProxy.sSelf.getUserId()+"</userId>"
						+" �<conversationId>"+conversationList.conversationId+"</conversationId>"
						//				 +"<startMessageIdIndex>"+endMessageIdIndex+"</startMessageIdIndex>"
						//				 +" <endMessageIdIndex>"+startMessageIdIndex+"</endMessageIdIndex>"
						+" <endMessageIdIndex>"+startMessageIdIndex+"</endMessageIdIndex>"

				+"</deleteRangeMessageRequest>";

				//				String content  = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				//						+"<deleteConversationsRequest>"
				//						+" �<isDeleteAll>false</isDeleteAll>"
				//						+" �<userId>"+BusinessProxy.sSelf.getUserId()+"</userId>"
				//						+" <conversationIds>"
				//						+ss
				//						+" </conversationIds>"
				//						+"</deleteConversationsRequest>";
				//				 
				//	System.out.println("-----------content------------:"+content);
				URLConnection  connection = new URL(url).openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/xml"); 
				connection.setRequestProperty(
						"password",
						HttpHeaderUtils.encriptPass(

								SettingData.sSelf
								.getPassword()));
				connection.setRequestProperty(
						"authkey","RTAPP@#$%!@");
				OutputStream output = connection.getOutputStream(); 

				output.write(content.getBytes());
				output.close();
				String s = Utilities.readStream(connection.getInputStream());
				//	System.out.println("-----------content-------response-----:"+s);
				if(s != null && s.trim().equalsIgnoreCase("1")){

					//						MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST
					//						for (int i = 0; i < cId.size(); i++) {
					//							String id = cId.get(i) ;
					getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, MessageConversationTable.CONVERSATION_ID+" = ? ", new String[] {conversationList.conversationId});
					getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX, MessageTable.CONVERSATION_ID+" = ? ", new String[] {conversationList.conversationId});
					//						}
					//						handler.post(new Runnable() {
					//							
					//							@Override
					//							public void run() {
					//								activityAdapter.notifyDataSetChanged() ;
					//							}
					//						});

					s = "You have successfully deleted this conversation!";

				}else
				{

					Hashtable<String, String> err = Utilities.parseError(s) ;
					if(err.containsKey("message"))
						s =err.get("message");
				}
				/*	if(activityAdapter==null || activityAdapter.getCursor().getCount()==0)
						   ((Button)findViewById(R.conversation.delete)).setVisibility(View.GONE);
						else
							((Button)findViewById(R.conversation.delete)).setVisibility(View.VISIBLE);*/
				//invalidate();
				//System.out.println("=====list empty======");
				return  s;

				//				HttpResponse response =	getResponse(url) ;
				//				if (response == null && !isCancelled()) {
				//					dismissAnimationDialog();
				//					// showSimpleAlert("Error",
				//					// "Please try after sometime!!!");
				//					return null;
				//				}
				//				// {"status":"1","desc":"Added to your likes"}
				//				if (isCancelled())
				//					return null;
				//				String responseStr = null;
				//				try {
				//					responseStr = EntityUtils
				//							.toString(response.getEntity());
				//				} catch (ParseException e) {
				//					e.printStackTrace();
				//				} catch (IOException e) {
				//					e.printStackTrace();
				//				}
				//				if ((responseStr == null || responseStr.equals(""))
				//						&& !isCancelled()) {
				//					dismissAnimationDialog();
				//					showSimpleAlert("Error", "Please try after sometime!!!");
				//					return null;
				//				}
				//				if (isCancelled())
				//					return null;
				//				return responseStr ;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return err;
		}

	}

	TextWatcher textWatcher = new TextWatcher() {

		public synchronized void onTextChanged(CharSequence s, int start,
				int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			try {
				StringBuilder where = new StringBuilder();
				where.append(MessageConversationTable.SOURCE + " not like ? ");


				String[] songs = new String[1]; 
				songs[0] = new String("%"+search.getText()+"%") ;
				//				String[] localObject1 = new String[1];
				//				localObject1[0] = "audio_id";

				//				String[] projection = { MediaStore.Audio.Media._ID,
				//						MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
				//						MediaStore.Audio.Media.DATA,
				//						MediaStore.Audio.Media.DISPLAY_NAME,
				//						MediaStore.Audio.Media.DURATION 
				//						
				//				};
				//
				//				
				//				cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				//						projection,  MediaStore.Audio.Media.DISPLAY_NAME + " like ? ", songs, null);
				//				count = cursor.getCount();
				//				
				//				listAdaptor = new CustomAdapter(LocalSystemView.this, cursor, true);
				//				if (count > 0)
				//					listView.setAdapter(listAdaptor);
				//				
				//				listAdaptor.notifyDataSetChanged();
				//				listAdaptor.notifyDataSetInvalidated();


				//if(search.getText().toString()==null ||search.toString().trim().length()<=0)
				if(search.getText().toString()==null ||search.getText().toString().trim().length()<=0)
					//				search.setText("");
					removeCloseSearch();
				//				else
				//					setCloseSearch();

				activityAdapter.getCursor().close();
				activityAdapter = new ConversationsListAdapter(KainatInboxAvtivity.this, getCursor("%"+search.getText()+"%"), true,
						KainatInboxAvtivity.this,"%"+search.getText()+"%");
				listViewActivity.setAdapter(activityAdapter);
				getConversationId(cursor, 0);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}
	};
	String err = "Some error occured on server!" ;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		idelTime = System.currentTimeMillis() ;

		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
		this.totalItemCount = totalItemCount;

	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if(((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).isActive()) {
			EditText edtView=(EditText)findViewById(R.id.search_text);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtView.getApplicationWindowToken(), 0);
		}
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			idel = true;
			ImageDownloader.idel = true;
			if(listViewActivity!=null && listViewActivity.getVisibility()==View.VISIBLE){
				int firstPosition = listViewActivity.getFirstVisiblePosition() ; 
				for (int i = 0; i <= firstPosition+visibleItemCount; i++) 
				{
					if(listViewActivity!=null){
						View convertView = listViewActivity.getChildAt(i);
						if(convertView!=null){
							ConversationsViewHolder conversationsViewHolder = (ConversationsViewHolder)convertView.getTag() ;
							ImageDownloader imageManager = new ImageDownloader(1);
							imageManager.handler=handler ;

							if(conversationsViewHolder.me_image.getImageUrl()!=null)
								imageManager.download(conversationsViewHolder.me_image.getImageUrl(),
										conversationsViewHolder.me_image, KainatInboxAvtivity.this,ImageDownloader.TYPE_IMAGE);
						}
					}
				}
			}
		} else {
			ImageDownloader.idel = false;
			idel = false;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

		//System.out.println("-------------on onDestroy inbox1 activity--------");
		try{
			if(activityAdapter!=null){
				activityAdapter.getCursor().close() ;
			}
			//		sqlDB.close(); database.close();
			super.onDestroy();

			if(listViewActivity!=null){
				listViewActivity.destroyDrawingCache();
				listViewActivity=null;
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		//System.gc();
	}
	//	 private LandingPageDatabaseHelper database;
	//	 private SQLiteDatabase sqlDB ;
	Cursor cursor;
	public Cursor getCursor(String search){

		try{
			/*if(BusinessProxy.sSelf.sqlDB == null)
			{
				 LandingPageDatabaseHelper database = null;
				 if(database==null){
						database = new LandingPageDatabaseHelper(KainatInboxAvtivity.this);
					}
					if(BusinessProxy.sSelf.sqlDB==null){
						BusinessProxy.sSelf.sqlDB = database.getWritableDatabase();
					}
				//BusinessProxy.sSelf.initializeDatabase();
			}*/

			if(search!=null){
				search = search.replace(" ", "%") ;
				String where = "LOWER("+MessageConversationTable.PARTICIPANT_INFO+")"+ " like '"+search.toLowerCase()+"' or LOWER("+MessageConversationTable.SUBJECT+") like '"+search.toLowerCase()+"' and "
						+MessageConversationTable.USER_ID+" = '"+BusinessProxy.sSelf.getUserId()+"'" ;

				cursor=  BusinessProxy.sSelf.sqlDB.query(MessageConversationTable.TABLE, null, 
						where, null
						, null, null,MessageConversationTable.INSERT_TIME + " DESC");

			}
			else{
				cursor = BusinessProxy.sSelf. sqlDB.query(MessageConversationTable.TABLE, null, 
						MessageConversationTable.USER_ID+" =? and "+MessageConversationTable.CONVERSATION_ID+" not like 'NP%'", new String[] { BusinessProxy.sSelf.getUserId()+"" },
						null, null, MessageConversationTable.INSERT_TIME + " DESC");
			}
			cursor.moveToFirst();

		}catch (Exception e) {
			e.printStackTrace() ;
			return null;
		}
		return cursor;




	}

	private void getConversationId(Cursor cursor2, int cid) {
		// TODO Auto-generated method stub
		Cursor cu= cursor2;	
		cu.moveToFirst();
		int rows = cu.getCount();
		arrConversationList = new ArrayList<String>();
		ConversationList conversationList = null;
		for (int i = 0; i < rows; i++) {
			if(cu.getString(cu.getColumnIndex(MessageConversationTable.CONVERSATION_ID)).equals(""+cid)){
				conversationList  = CursorToObject.conversationListObject(cursor,KainatInboxAvtivity.this) ;
				break;
			}
			// arrConversationList.add(cu.getString(cu.getColumnIndex(MessageConversationTable.CONVERSATION_ID)));
			cu.moveToNext();
		}
		if(UIActivityManager.PushNotification == 2 && UIActivityManager.TabValue == 1)
		{
			UIActivityManager.PushNotification = 1;
			if(conversationList != null)
			{
				DataModel.sSelf.storeObject(DMKeys.CONVERSATION_SELECTED_LIST,conversationList);
				Intent intent1 = new Intent(KainatInboxAvtivity.this, ConversationsActivity.class);
				intent1.putExtra(Constants.CONVERSATION_ID, conversationList.conversationId);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				if(conversationList.subject == null || conversationList.subject.trim().length() <= 0)
					intent1.putExtra(Constants.TITLE,conversationList.source);
				else
					intent1.putExtra(Constants.TITLE,conversationList.subject);
				startActivity(intent1);
				//				finish();
			}
			//		Push_CONVERSATION_ID = 1;
		}
	}


	long idelTime ;
	//	MyContentObserver contentObserver ;
	private class MyContentObserver extends ContentObserver {
		MyContentObserver(Handler handler) {
			super(handler);
		}

		public boolean deliverSelfNotifications() {
			return true;
		}

		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// Log.d(TAG, "Saw a change in row # 1");
			try{
				if(activityAdapter!=null && activityAdapter.getCursor()!=null){
					activityAdapter.getCursor().requery() ;
					activityAdapter.notifyDataSetChanged();
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			//			if((System.currentTimeMillis()-idelTime)>2000)
			//			listViewActivity
			//			.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			//			else
			//				listViewActivity
			//				.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);


		}

	}
	@Override
	protected void onPause() {

		if(FeedRequester.feedTaskConversationList!=null)
			FeedRequester.feedTaskConversationList.setHttpSynch(null) ;

		FeedTask.setHttpSynchRefreshCurrentView(null) ;
		ComposeService.setHttpSynchRefreshCurrentView(null) ;
		//System.out.println("-------------on pause----");
		if(activityAdapter!=null && activityAdapter.getCursor()!=null&&dontCloseCursol)
			activityAdapter.getCursor().close();
		//		getContentResolver().unregisterContentObserver(contentObserver);
		super.onPause();
	}
}
