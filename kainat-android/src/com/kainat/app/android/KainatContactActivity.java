package com.kainat.app.android;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.WordUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.ContactUpDatedModel;
import com.kainat.app.android.bean.ContactUpDatedModel.UserDetail;
import com.kainat.app.android.bean.ContactUploadeModel;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.model.Buddy;
import com.kainat.app.android.model.KainatContact;
import com.kainat.app.android.model.RequestUserDetail;
import com.kainat.app.android.util.AdConnectionManager;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KainatContactActivity extends UIActivityManager implements ThumbnailReponseHandler{

	// public Handler handler = new Handler(Looper.getMainLooper());
	private static final String TAG = KainatContactActivity.class.getSimpleName();
	String countryCode ;
	TextView no_contact,no_pending_request,no_buddiess;
	public View done;
	public TextView outputText;
	ListView contact_list;
	ContactAdaptor contactAdaptor;
	ContactBuddyAdaptor contactBuddyAdaptor;
	PendingRequestAdapter pendingRequestAdaptor;
	KainatContactActivity mContext;
	ArrayList<KainatContact> kainatContactList = new ArrayList<KainatContact>();
	ArrayList<KainatContact> kainatBuddiesList = new ArrayList<KainatContact>();
	View refresh;
	ProgressDialog rtDialog;
	boolean group ;
	View recommended;
	private TextView syncMessageText;
	TextView create_group,pending_request_count ;
	private String communityName;
	String sharedText;
	RotateAnimation rotate;
	private int selCounter = 0;
	private boolean showRefresh = true;
	ImageView clean_search_iv_member;
	EditText contact_memeber_serch;
	DisplayImageOptions options ;
	TextView ytFriends, pendingRequest,yt_buddies;
	RelativeLayout searchLayout;
	private static final byte TAB_FRIENDS = 1;
	private static final byte TAB_PENDING_REQUESTS = 2;
	private static final byte TAB_CONTACT = 3;
	byte currentSelected = TAB_CONTACT;
	public static String [] prgmNameList={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
	ArrayList<RequestUserDetail> requestUserDetail = new ArrayList<RequestUserDetail>();
	ProgressDialog rTDialog;
	ProgressBar progress_loder;
	RelativeLayout rr_loder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		mContext = this;
		rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		countryCode		= getCountryZipCode()  ;//tm.getNetworkCountryIso();
		communityName 	= getIntent().getStringExtra("community_name");
		group 		  	= getIntent().getBooleanExtra("group", false);
		setContentView(R.layout.kainat_contact);
		progress_loder = (ProgressBar)findViewById(R.id.progress_loder);
		rr_loder = (RelativeLayout)findViewById(R.id.rr_loder);
		rr_loder.setVisibility(View.GONE);
		progress_loder.setVisibility(View.GONE);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).
				resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.male_icon)
				.showImageOnFail(R.drawable.male_icon)
				.showImageOnLoading(R.drawable.male_icon).build();
		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				handleSendText(intent); // Handle text being sent
			} else if (type.startsWith("image/")) {
				handleSendImage(intent); // Handle single image being sent
			}
		} else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
			if (type.startsWith("image/")) {
				handleSendMultipleImages(intent); // Handle multiple images being sent
			}
		} else {
			// Handle other intents, such as being started from the home screen
		}
		menuNew = (ImageButton)findViewById(R.id.menu) ;		
		menuNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList) ;	
			}
		});

		//Initiate Tabs
		searchLayout = (RelativeLayout)findViewById(R.id.search_bar_layout);
		yt_buddies = (TextView)findViewById(R.id.yt_buddies);
		yt_buddies.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if((currentSelected == TAB_PENDING_REQUESTS) ||(currentSelected == TAB_FRIENDS) ){
					searchLayout.setVisibility(View.VISIBLE);
					syncMessageText.setVisibility(View.GONE);
					refresh.setVisibility(View.VISIBLE);
					no_pending_request.setVisibility(View.GONE);
					yt_buddies.setBackgroundResource(R.drawable.selected_left_tab_withoutround_rect);
					ytFriends.setBackgroundResource(R.drawable.unselected_right_tab_withoutround_rect);
					pendingRequest.setBackgroundResource(R.drawable.unselected_right_tab_withoutround_rect);
					outputText.setText(getString(R.string.contact_only));
					GetBuddyFriendTask gBFT = new GetBuddyFriendTask();
					gBFT.execute("");
					if(contact_list != null){
						contactBuddyAdaptor = new ContactBuddyAdaptor() ;
						contact_list.setAdapter(contactBuddyAdaptor);
					}
					currentSelected = TAB_CONTACT;
				}
			}
		});
		ytFriends = (TextView)findViewById(R.id.yt_friends);
		ytFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if((currentSelected == TAB_PENDING_REQUESTS)||(currentSelected == TAB_CONTACT)){
					progress_loder.setVisibility(View.GONE);
					rr_loder.setVisibility(View.GONE);
					searchLayout.setVisibility(View.VISIBLE);
					syncMessageText.setVisibility(View.GONE);
					refresh.setVisibility(View.VISIBLE);
					no_pending_request.setVisibility(View.GONE);
					no_buddiess.setVisibility(View.GONE);
					yt_buddies.setBackgroundResource(R.drawable.unselected_right_tab_withoutround_rect);
					ytFriends.setBackgroundResource(R.drawable.selected_left_tab_withoutround_rect);
					pendingRequest.setBackgroundResource(R.drawable.unselected_right_tab_withoutround_rect);
					outputText.setText(getString(R.string.my_friends));
					contactAdaptor = new ContactAdaptor();
					contact_list.setAdapter(contactAdaptor);
					currentSelected = TAB_FRIENDS;
				}
			}
		});
		pendingRequest = (TextView)findViewById(R.id.pending_requests);
		//		ytFriends.setBackgroundResource(R.drawable.selected_tab_round_rect);
		//		pendingRequest.setBackgroundResource(R.drawable.rounded_header_color);
		pendingRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if((currentSelected == TAB_FRIENDS)||(currentSelected == TAB_CONTACT)){
					progress_loder.setVisibility(View.GONE);
					rr_loder.setVisibility(View.GONE);
					searchLayout.setVisibility(View.GONE);
					syncMessageText.setVisibility(View.GONE);
					pending_request_count.setVisibility(View.GONE);
					refresh.setVisibility(View.GONE);
					pendingRequest.setBackgroundResource(R.drawable.selected_left_tab_withoutround_rect);
					ytFriends.setBackgroundResource(R.drawable.unselected_right_tab_withoutround_rect);
					yt_buddies.setBackgroundResource(R.drawable.unselected_right_tab_withoutround_rect);
					outputText.setText(getString(R.string.pending_request));
					/*
					 * pendingRequestAdaptor = new PendingRequestAdapter(KainatContactActivity.this, requestUserDetail, null);
					if(contact_list != null)
						contact_list.setAdapter(pendingRequestAdaptor);
					 * */
					if(requestUserDetail.size()<=0){
						no_pending_request.setVisibility(View.VISIBLE);
						no_contact.setVisibility(View.GONE);
						no_buddiess.setVisibility(View.GONE);
					}else
					{
						no_pending_request.setVisibility(View.GONE);
						no_contact.setVisibility(View.GONE);
						no_buddiess.setVisibility(View.GONE);
					}
					pendingRequestAdaptor = new PendingRequestAdapter(KainatContactActivity.this, requestUserDetail, null);
					if(contact_list != null)
						contact_list.setAdapter(pendingRequestAdaptor);
					currentSelected = TAB_PENDING_REQUESTS;
				}

			}
		});
		currentSelected = TAB_CONTACT;

		// DATE : 10-12-2015
		// NAME : MANOJ SINGH
		clean_search_iv_member = (ImageView)findViewById(R.id.clean_search_iv_member);
		contact_memeber_serch = (EditText)findViewById(R.id.contact_memeber_serch);
		contact_memeber_serch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = contact_memeber_serch.getText().toString();
				localFilter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				/*String text = communitymemeberserch.getText().toString();
						localFilter(text);*/
				/*	String text = communitymemeberserch.getText().toString();
						localFilter(""+arg0.toString());*/
			}
		});
		clean_search_iv_member.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contact_memeber_serch.setText("");
			}
		});



		//
		//

		no_contact 		= (TextView)findViewById(R.id.no_contact) ;
		no_contact.setVisibility(View.GONE);
		no_pending_request 		= (TextView)findViewById(R.id.no_pending_request) ;
		no_pending_request.setVisibility(View.GONE);
		no_buddiess		= (TextView)findViewById(R.id.no_buddiess) ;
		no_buddiess.setVisibility(View.GONE);

		recommended		= findViewById(R.id.recommended) ;
		recommended.setVisibility(View.GONE);
		create_group	= (TextView)findViewById(R.id.create_group) ;
		pending_request_count= (TextView)findViewById(R.id.pending_request_count) ;
		pending_request_count.setVisibility(View.GONE);
		if (getIntent().getBooleanExtra("ADDCONVERSATIONS", false)) {
			showRefresh = false;
			create_group.setText(R.string.done);
			menuNew.setVisibility(View.GONE);
			onBackArrowClick();
		}
		if(!group){
			create_group.setVisibility(View.GONE);
		}else
		{
			showRefresh = false;
		}
		create_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuffer sb = new StringBuffer() ;
				for (KainatContact kainatContact :kainatContactList) {
					if(kainatContact.isSelected())
						sb.append(kainatContact.getRealName()+";") ;
				}
				if(sb.toString().length() > 10){
					if (getIntent().getBooleanExtra("ADDCONVERSATIONS", false)) {
						if (sb.toString() != null && sb.toString().trim().length() > 0) {
							DataModel.sSelf.storeObject(DMKeys.COMPOSE_DESTINATION_CONTACTS, sb.toString());
							setResult(ConversationsActivity.ADDCONVERSATIONS);
							finish();
						}
					}else{
						//Check if multiple contacts are there then its MUC
						if(sb.toString().split(";").length > 1){
							startChat(sb.toString(), true);
						}
						else
							startChat(sb.toString(), false);
					}
				}
				else{
					AppUtil.showTost(KainatContactActivity.this, getString(R.string.slectc_contact_to_start_chat)) ;
				}
			}
		});
		recommended.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer sb = new StringBuffer() ;
				for (KainatContact kainatContact :kainatContactList) {
					if(kainatContact.isSelected())
						sb.append(kainatContact.getRealName()+";") ;
				}
				if(sb.toString().length()>10){
					//					sb.toString()
					StringBuilder text = new StringBuilder("UpdateCommunity::Name##");
					text.append(communityName);
					text.append("::MEMBERS##");
					text.append(sb.toString());
					if (BusinessProxy.sSelf.sendNewTextMessage(
							"Community manager<a:communitymgr>", text.toString(), true)) {

						final DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (onBackPressedCheck())
									onBackPressed();
								else
									onBackPressed();
							}
						};
						showAlertMessage("RockeTalk", "Your Invitation has been sent!",
								new int[] { DialogInterface.BUTTON_POSITIVE },
								new String[] { "Yes" },
								new DialogInterface.OnClickListener[] { exitHandler });
					}
				}
				else{
					AppUtil.showTost(KainatContactActivity.this, getString(R.string.slectc_contact_to_start_chat)) ;
				}
			}
		});
		syncMessageText = (TextView) findViewById(R.id.message) ;
		syncMessageText.setVisibility(View.GONE);
		done= findViewById(R.id.done) ;
		done.setVisibility(View.GONE) ;
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getIntent().getBooleanExtra("ADDCONVERSATIONS", false)) {
					setResult(ConversationsActivity.ADDCONVERSATIONS);
					finish();
				}else{
					Intent intent = new Intent(KainatContactActivity.this,InterestActivity.class);
					intent.putExtra("REG_FLOW", true);
					startActivity(intent);
					/*Intent intent = new Intent(mContext,
							KainatHomeActivity.class);				
					startActivity(intent);*/
				}
				//				startChat("qts");
			}
		});

		outputText = (TextView) findViewById(R.id.textView1);
		try{
			String headerTitle = getIntent().getExtras().getString("HEADER_TITLE");

			if(headerTitle.equals(getString(R.string.create_group_lbl))
					|| headerTitle.equals(getString(R.string.start_chat_lbl))){
				outputText.setText(headerTitle);
				menuNew.setVisibility(View.GONE);
				onBackArrowClick();
			}else{
				outputText.setText(getString(R.string.friend));
			}
		}catch(Exception ex){
		}
		refresh    = findViewById(R.id.refresh);
		if(showRefresh)
			refresh.setVisibility(View.VISIBLE);
		else
			refresh.setVisibility(View.GONE);

		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(contactTask!=null)
					contactTask.cancel(true);
				if(currentSelected == TAB_FRIENDS || currentSelected == TAB_CONTACT ){
					contactTask = new LoadContact();
					contactTask.execute("") ;
					GetBuddyFriendTask gBFT = new GetBuddyFriendTask();
					gBFT.execute("");
				}else
				{
					refresh.setVisibility(View.GONE);
				}

			}
		});
		initLeftMenu();

		contact_list   = (ListView) findViewById(R.id.contact_list);
		if(currentSelected == TAB_FRIENDS){
			contactAdaptor = new ContactAdaptor() ;
			contact_list.setAdapter(contactAdaptor);
		}
		if(getIntent().getBooleanExtra("REG_FLOW",false)){
			done.setVisibility(View.VISIBLE) ;
			menuNew.setVisibility(View.GONE) ;
			ytFriends.performClick();
			refresh.setVisibility(View.GONE) ;
			((LinearLayout)findViewById(R.id.contact_tabs)).setVisibility(View.GONE);
			yt_buddies.setVisibility(View.GONE);
			pendingRequest.setVisibility(View.GONE);
			
		}

		contact_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//				if(getIntent().getBooleanExtra("REG_FLOW",false)){
				//					Intent intent = new Intent(mContext,
				//							KainatHomeActivity.class);				
				//					startActivity(intent);	
				//				}else
				{
					if(currentSelected == TAB_FRIENDS){
						startChat(kainatContactList.get(arg2).getRealName(), false);//+","+"nagendra1020");
					}
					else if(currentSelected == TAB_CONTACT)
					{
						startChat(kainatBuddiesList.get(arg2).getName()+"<"+kainatBuddiesList.get(arg2).getUserName()+">", false);
					}
					//kainatContact.getRealName()startChat(sb.toString()) ;
				}
			}
		});

		GetRequestFriendTask requestFriendTask = new GetRequestFriendTask() ;
		requestFriendTask.execute(""+BusinessProxy.sSelf.getUserId());

		GetBuddyFriendTask gBFT = new GetBuddyFriendTask();
		gBFT.execute("");
		//		loadBuddy();
		parseContact(KeyValue.getString(KainatContactActivity.this, KeyValue.CONTACT));
		if(kainatContactList.isEmpty()){
			contactTask = new LoadContact();
			contactTask.execute("") ;
		}


		if(getIntent().getBooleanExtra("from_community", false)){
			create_group.setVisibility(View.GONE);
			recommended.setVisibility(View.VISIBLE);
		}

		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Contact Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	class PendingRequestAdapter extends BaseAdapter{   
		ArrayList<RequestUserDetail> result;
		Context context;
		int [] imageId;
		private LayoutInflater inflater = null;

		public PendingRequestAdapter(KainatContactActivity mainActivity, ArrayList<RequestUserDetail> prgmNameList, int[] prgmImages) {
			// TODO Auto-generated constructor stub
			result = prgmNameList;
			context = mainActivity;
			imageId = prgmImages;
			inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public class Holder
		{
			TextView userName;
			ImageView userImage;
			Button actionConfirm;
			Button actionDelete;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder=new Holder();
			View rowView;       
			rowView = inflater.inflate(R.layout.pending_request_row, null);
			holder.userName = (TextView) rowView.findViewById(R.id.user_name);
			holder.userImage = (ImageView) rowView.findViewById(R.id.user_image);       
			holder.actionConfirm = (Button) rowView.findViewById(R.id.btn_confirm);       
			holder.actionDelete = (Button) rowView.findViewById(R.id.btn_delete);  

			holder.userImage.setTag(result.get(position).getUserName());
			holder.userImage.setOnClickListener(profileClick);
			holder.userName.setText(result.get(position).getName());
			//holder.userImage.setImageResource(imageId[position]);   
			holder.userImage.setImageResource(R.drawable.unisex);
			String url = "http://yelatalkprod.com/tejas/feeds/user/pic?format=150x150&user="+result.get(position).getUserName();
			imageLoader.displayImage(url.trim(),holder.userImage, options);
			rowView.setOnClickListener(new OnClickListener() {            
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Toast.makeText(context, "You Clicked "+result.get(position).getUserId(), Toast.LENGTH_LONG).show();
				}
			});  

			//CONFIRM CLICK
			holder.actionConfirm.setOnClickListener(new OnClickListener() {            
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//	Toast.makeText(context, "You actionConfirm Clicked "+result.get(position).getUserId(), Toast.LENGTH_LONG).show();
					AcceptFriendTask addFriendTask = new AcceptFriendTask() ;
					addFriendTask.execute(result.get(position).getUserId());
				}
			});  

			//CONFIRM CLICK
			holder.actionDelete.setOnClickListener(new OnClickListener() {            
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//	Toast.makeText(context, "You actionDelete Clicked "+result.get(position).getUserId(), Toast.LENGTH_LONG).show();
					DeAcceptFriendTask addFriendTask = new DeAcceptFriendTask() ;
					addFriendTask.execute(result.get(position).getUserId());
				}
			});  
			return rowView;
		}

	} 

	//Date :22-02-2016
	//Name :Manoj Singh
	//Accepting friend request

	private class AcceptFriendTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = AcceptFriendsOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatContactActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			if(response!= null)
			{
				try{
					JSONObject  jobj = new JSONObject(response);
					if(response.contains("message")){
						if(response.contains("status")){
							String stst = jobj.optString("status").toString();
						}
						String desc = jobj.optString("message").toString();
						Toast.makeText(KainatContactActivity.this, desc,  Toast.LENGTH_LONG).show();
					}
					GetRequestFriendTask requestFriendTask = new GetRequestFriendTask() ;
					requestFriendTask.execute(""+BusinessProxy.sSelf.getUserId());
				}catch(Exception e){

				}
			}

		}
	}

	//Date :22-02-2016
	//Name :Manoj Singh
	//Denying friend request

	private class DeAcceptFriendTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = DeleteFriendsOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatContactActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			if(response!= null)
			{
				try{
					JSONObject  jobj = new JSONObject(response);
					if(response.contains("message")){
						if(response.contains("status")){
							String stst = jobj.optString("status").toString();
						}
						String desc = jobj.optString("message").toString();
						Toast.makeText(KainatContactActivity.this, desc,  Toast.LENGTH_LONG).show();
					}

					GetRequestFriendTask requestFriendTask = new GetRequestFriendTask() ;
					requestFriendTask.execute(""+BusinessProxy.sSelf.getUserId());
					//	pendingRequestAdaptor.notifyDataSetChanged();
				}catch(Exception e){

				}
			}

		}
	}

	//Request Hit 
	private String AcceptFriendsOnServer(String userId){
		String strData = null;
		String url_a = null;
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/acceptrequest";

		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("buddyUserId", new String(userId.trim().getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
	}

	private String DeleteFriendsOnServer(String userId){
		String strData = null;
		String url_a = null;
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/deleterequest";

		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("buddyUserId", new String(userId.trim().getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
	}
	//test 
	private void localFilter(String val) {
		//String nexturl = feed.nexturl;
		//if(KeyValue.getString(KainatContactActivity.this, KeyValue.CONTACT) != null)
		if(val!=null){
			parseContact(KeyValue.getString(KainatContactActivity.this, KeyValue.CONTACT));
			final ArrayList<KainatContact> searchList = new ArrayList<KainatContact>();
			if(val.equals(""))
			{
				for (KainatContact bud : kainatContactList) {
					searchList.add(bud);
				}
				contactAdaptor.setNewItemList(searchList);
			}else
			{
				for (KainatContact bud : kainatContactList) {
					/*	if (bud.groupName.trim().startsWith(val.toLowerCase().toString()) || bud.groupName.trim().startsWith(val.toUpperCase().toString()) || bud.displayName.trim().startsWith(val.toLowerCase().toString()) || bud.displayName.trim().startsWith(val.toUpperCase().toString())) {
					searchList.add(bud);
				}*/
					if(bud.getName().toLowerCase().startsWith(val.toLowerCase().toString())){
						searchList.add(bud);
					}
				}
				if(contactAdaptor != null)
				contactAdaptor.setNewItemList(searchList);

			}
		}
	}




	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(KainatContactActivity.this).reportActivityStart(this);
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(KainatContactActivity.this).reportActivityStop(this);
	}

	private void onBackArrowClick(){
		ImageView backImageView = (ImageView) findViewById(R.id.back_iv);
		backImageView.setVisibility(View.VISIBLE);
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			// Update UI to reflect image being shared
		}
	}

	//	void handleSendImage(Intent intent) {
	//		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	//		mImagesPath = new ArrayList<String>();
	//		// Uri uri = Uri.parse("smsto:0800000123");
	//
	//		final String[] columns = { MediaStore.Images.Media.DATA,
	//				MediaStore.Images.Media._ID };
	//		final String orderBy = sortOrder();// MediaStore.Images.Media._ID;
	//
	//		Cursor imagecursor = managedQuery(
	//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
	//				null, orderBy);
	//		int image_column_index = imagecursor
	//				.getColumnIndex(MediaStore.Images.Media._ID);
	//		int count = imagecursor.getCount();
	//		Hashtable<String, String> sr = new Hashtable<String, String>();
	//		for (int i = 0; i < count; i++) {
	//			imagecursor.moveToPosition(i);
	//			int id = imagecursor.getInt(image_column_index);
	//			int dataColumnIndex = imagecursor
	//					.getColumnIndex(MediaStore.Images.Media.DATA);
	//			sr.put(id + "", imagecursor.getString(dataColumnIndex));
	//		}
	//
	//		if (imageUri != null) {
	//			// for (int i = 0; i < imageUris.size(); i++)
	//			{
	//				String is = imageUri.toString();
	//				is = is.substring(is.lastIndexOf("/") + 1, is.length());
	//				if(sr.get(is)==null){
	//					is = imageUri.toString();
	//					if(is.startsWith("file:///"))
	//						is = is.substring("file:///".length(), is.length()) ;
	//					mImagesPath.add(is);
	//				}
	//				else
	////					file:///storage/sdcard0/Android/data/com.facebook.katana/cache/.facebook_-1329943565.jpg
	//				mImagesPath.add(sr.get(is));// content://media/external/images/media/1787
	//			}
	//			// Update UI to reflect multiple images being shared
	//		}
	//		sr.clear();
	//	}
	//	protected String sortOrder() {
	//		String str;
	//		str = " ASC";
	//		return "case ifnull(datetaken,0) when 0 then date_modified*1000 else datetaken end"
	//				+ str + ", _id" + str;
	//	}

	void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// Update UI to reflect multiple images being shared
		}
	}
	void handleSendText(Intent intent) {
		sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			// Update UI to reflect text being shared

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(contactTask!=null)
			contactTask.cancel(true) ;
	}

	private void loadBuddy(){
		try{
			final List<Buddy> list = BusinessProxy.sSelf
					.getAllBuddyInformation();
			//kainatContactList = new ArrayList<KainatContact>();
			for (Buddy buddy : list) {
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setUserName(buddy.name);
				kainatContact.setName(BusinessProxy.sSelf.getDisplayName(buddy.realName));
				kainatContact.setRealName(buddy.realName);
				kainatContact.setUserId(""+buddy.id);

				//			kainatContactList.add(kainatContact) ;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	LoadContact contactTask ;
	private class LoadContact extends AsyncTask<String, String, String> {

		Bitmap bitmap;

		@Override
		protected void onPreExecute() 
		{
			if(getIntent().getBooleanExtra("REG_FLOW",false)){
				//				rtDialog = new RTDialog(mContext, null, getString(R.string.synch_contact));
				//				rtDialog.show();

				rtDialog = ProgressDialog.show(KainatContactActivity.this, "", getString(R.string.synch_contact), true);
			}else
			{
				rotateImage(refresh);
			}

			if(kainatContactList.size() <= 0){
				no_buddiess.setVisibility(View.GONE);
				no_pending_request.setVisibility(View.GONE);
				if(currentSelected == TAB_FRIENDS){
					no_contact.setVisibility(View.VISIBLE);
					no_contact.setText(getResources().getString(R.string.retrive_msg));
				}
			}
			else
				no_contact.setVisibility(View.GONE);
			no_pending_request.setVisibility(View.GONE);
			no_buddiess.setVisibility(View.GONE);
		}

		@Override
		protected String doInBackground(String... params) {
			String phoneNumber = null;
			String email = null;
			Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
			String _ID = ContactsContract.Contacts._ID;
			String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
			String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

			Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
			String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

			Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
			String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
			String DATA = ContactsContract.CommonDataKinds.Email.DATA;

			ContentResolver contentResolver = getContentResolver();

			Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
					null);
			kainatContactList.clear();
			// Loop for every contact in the phone
			ContactUploadeModel contactUploadeModel = new ContactUploadeModel() ;
			contactUploadeModel.setUserId(BusinessProxy.sSelf.getUserId());
			if (cursor.getCount() > 0) {

				while (cursor.moveToNext()) {

					if(isCancelled())
						return null ;
					KainatContact kainatContact = new KainatContact();
					Vector<String> phoneVec = null;//new Vector<String>();
					Vector<String> emailVec = null;//new Vector<String>();;

					String contact_id = cursor
							.getString(cursor.getColumnIndex(_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(DISPLAY_NAME));

					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
							.getColumnIndex(HAS_PHONE_NUMBER)));

					if (hasPhoneNumber > 0) {

						//						output.append("\n First Name:" + name);
						//						kainatContact.setName(name);
						// Query and loop for every phone number of the contact
						Cursor phoneCursor = contentResolver.query(
								PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
								new String[] { contact_id }, null);

						while (phoneCursor.moveToNext()) {
							phoneNumber = phoneCursor.getString(phoneCursor
									.getColumnIndex(NUMBER));

							//							phoneNumber = "+91965 04 41 907"; 

							//							output.append("\n Phone number:" + phoneNumber);
							phoneNumber = phoneNumber.replace(" " , "") ;
							phoneNumber = phoneNumber.replace("_" , "") ;
							phoneNumber = phoneNumber.replace("-" , "") ;

							if(countryCode==null || countryCode.trim().length()<=0)
								countryCode = "91" ;
							if(countryCode.equalsIgnoreCase("in"))
								countryCode = "91" ;

							phoneNumber = fixMobileNumbers(phoneNumber,countryCode, 10);
							//							//System.out.printlnprintln("Name-"+name+"-"+phoneNumber);

							if(phoneVec==null)
								phoneVec = new Vector<String>() ;

							if(!phoneVec.contains(phoneNumber))
								phoneVec.add(phoneNumber) ;

							if(contactUploadeModel.getMobileNumberList()==null){								
								contactUploadeModel.setMobileNumberList(new ArrayList<String>());
							}

							if(!contactUploadeModel.getMobileNumberList().contains(phoneNumber))
								contactUploadeModel.getMobileNumberList().add(phoneNumber);
						}

						phoneCursor.close();


						Cursor emailCursor = contentResolver.query(
								EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
								new String[] { contact_id }, null);

						while (emailCursor.moveToNext()) {

							email = emailCursor.getString(emailCursor
									.getColumnIndex(DATA));

							//							output.append("\nEmail:" + email);
							if(emailVec==null)
								emailVec = new Vector<String>() ;

							if(phoneVec != null && !phoneVec.contains(email))
								phoneVec.add(email) ;
							//							//System.out.printlnprintln("Email-"+email);

							if(contactUploadeModel.getEmailList()==null){								
								contactUploadeModel.setEmailList(new ArrayList<String>());
							}
							if(!contactUploadeModel.getEmailList().contains(email))
								contactUploadeModel.getEmailList().add(email);
						}
						emailCursor.close();
					}
					kainatContact.setPhone(phoneVec) ;
					kainatContact.setEmail(emailVec) ;
				}
			}
			Gson gson = new Gson();
			try {
				String response =  AdConnectionManager.getInstance()
						.uploadByteDataBody("http://"+ com.kainat.app.android.util.Urls.TEJAS_HOST +"/tejas/feeds/contact/upload", 
								gson.toJson(contactUploadeModel), null, null);
				if(response!=null){
					parseContact(response);
				}
				Log.d(TAG, "LoadContact :: doInBackground : Contact upload response : "+response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
			//			Log.i(TAG, "LoadContact :: doInBackground : Contact upload response "+gson.toJson(contactUploadeModel));			
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			//Cancel Animation dialog if showing..
			if(rtDialog != null && rtDialog.isShowing())
				rtDialog.dismiss();
			if(kainatContactList.size() <= 0){
				no_contact.setText(getResources().getString(R.string.nocontact));
				no_contact.setVisibility(View.VISIBLE);
				no_pending_request.setVisibility(View.GONE);
			}
			else
				no_contact.setVisibility(View.GONE);
			//no_pending_request.setVisibility(View.GONE);


			// In registration flow contact sync message. 
			if(getIntent().getBooleanExtra("REG_FLOW",false)){
				if(kainatContactList != null && kainatContactList.size() > 0){
					syncMessageText.setVisibility(View.VISIBLE);
					syncMessageText.setText(getString(R.string.contct_sync_aaha) + "\n" +kainatContactList.size() + " " + getString(R.string.contct_sync_count));
				}else{
					syncMessageText.setVisibility(View.GONE);
					no_contact.setVisibility(View.VISIBLE);
					no_pending_request.setVisibility(View.GONE);
					no_buddiess.setVisibility(View.GONE);
					no_contact.setText(getString(R.string.contct_sync_aaha) + "\n" +getString(R.string.contct_sync_zero));
				}
			}

			//Update contact adapter
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(currentSelected == TAB_FRIENDS){
						if(contactAdaptor!=null)
							contactAdaptor.notifyDataSetChanged();
					}
				}
			});
			if(!getIntent().getBooleanExtra("REG_FLOW",false))
			{
				rotate.cancel();
				UIActivityManager.ContactSelfChange = false;
			}

			if(getIntent().getBooleanExtra("REG_FLOW",false))
			{
				done.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.GONE);
			}
			try {
				handleLoginResponseNew(null);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			//			contactAdaptor.notifyDataSetChanged();
		}
	}


	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	class ContactAdaptor extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return kainatContactList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return kainatContactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		public void setNewItemList(ArrayList<KainatContact> itemList) {
			kainatContactList.clear();
			kainatContactList.addAll(itemList);
			runOnUiThread(new Runnable() {
				public void run() {
					notifyDataSetChanged();
					notifyDataSetInvalidated();
				}
			});
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			KainatContact contact = null;
			try
			{
				contact = (KainatContact) getItem(position);
			}catch(IndexOutOfBoundsException ex)
			{
				return convertView;
			}
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.kainat_contact_layout, null);
			//convertView.setBackgroundResource(R.drawable.landingpagebackground);

			((TextView) convertView
					.findViewById(R.id.contactLayout_contactName))
					.setText(BusinessProxy.sSelf.getDisplayName(contact.getName()));

			final com.kainat.app.android.uicontrol.CImageView imageView = (com.kainat.app.android.uicontrol.CImageView) convertView
					.findViewById(R.id.thumb) ;
			if(contact.getGender()!=null && contact.getGender().indexOf("F")!=-1){
				imageView.setImageResource(R.drawable.female) ;	
				imageView.setGender(3);
			}else{
				imageView.setImageResource(R.drawable.male) ;
				imageView.setGender(2);
			}
			//			else
			//			{
			//				imageView.setGender(4);
			//			}


			final ImageDownloader imageManager = new ImageDownloader(2);
			imageView.setTag(contact.getUserName()) ;
			ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

				@Override
				public void onThumbnailResponse(ThumbnailImage value,
						byte[] data) {
					try {
						//	hasProfilePic = true;

						imageView.setImageBitmap(value.mBitmap);
						imageView.invalidate() ;						
					} catch (Exception e) {
					}
				}
			};
			//System.out.printlnprintln("Contact image Url = "+contact.getUserName());
			imageView.setImageResource(R.drawable.male_icon);
			try{
				String url = "http://yelatalkprod.com/tejas/feeds/user/pic?format=150x150&user="+contact.getUserName();
				imageLoader.displayImage(url.trim(),imageView, options);
			}catch(Exception e){

			}
			//imageLoader.displayImage(entry.thumbUrl, (CImageView) findViewById(R.id.comm_profile_image), options);
			//imageManager.download(contact.getUserName(), imageView, handler,imageManager.TYPE_THUMB_BUDDY);
			if(!group)
				convertView
				.findViewById(R.id.checkBox1).setVisibility(View.GONE);
			else
				convertView
				.findViewById(R.id.checkBox1).setVisibility(View.VISIBLE);

			convertView
			.findViewById(R.id.checkBox1).setTag(contact) ;
			convertView
			.findViewById(R.id.checkBox1).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int i = 0 ;
					for (KainatContact contact:kainatContactList) {
						if(contact.isSelected())
							i++ ;
					}

					//is chkIos checked?
					KainatContact contact = (KainatContact) v.getTag();

					if(i>9){
						contact.setSelected(false);	
						((CheckBox)v).setChecked(false);
						AppUtil.showTost(KainatContactActivity.this, "Can not select more thar 9 contact!");
						return;
					}

					if (((CheckBox) v).isChecked()) {
						selCounter ++;
						contact.setSelected(true);
					}else{
						selCounter --;
						contact.setSelected(false);		
					}
					if(selCounter <= 0){
						create_group.setVisibility(View.GONE);
						if(showRefresh)
							refresh.setVisibility(View.VISIBLE);
					}else{
						create_group.setVisibility(View.VISIBLE);
						refresh.setVisibility(View.GONE);
					}
				}
			});
			if(contact.isSelected()){
				((CheckBox)convertView
						.findViewById(R.id.checkBox1)).setChecked(true) ;
			}else
			{
				((CheckBox)convertView
						.findViewById(R.id.checkBox1)).setChecked(false) ;
			}


			if(contact.getPhone()!=null){
				String number = contact.getPhone().toString().trim();
				number = number.replace("[", "");
				number = number.replace("]", "");
				((TextView) convertView.findViewById(R.id.mobile)).setText("+"+number);
			}
			if(contact.getEmail()!=null)
				((TextView) convertView
						.findViewById(R.id.email))
						.setText(contact.getEmail().toString());

			imageView.setOnClickListener(profileClick);

			return convertView;
		}

	}

	OnClickListener profileClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				if(!getIntent().getBooleanExtra("REG_FLOW",false)){
					String userName  = (String) v.getTag();
					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, userName);
					Intent intent = new Intent(KainatContactActivity.this, ProfileViewActivity.class);
					intent.putExtra("USERID", userName);
					intent.putExtra("CallFrom",1);
					startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	@Override
	public void onBackPressed() {
		finish();
		if(getIntent().getBooleanExtra("REG_FLOW",false)){
			Intent intent = new Intent(KainatContactActivity.this,InterestActivity.class);
			intent.putExtra("REG_FLOW", true);
			startActivity(intent);
		}
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	private void startChat(String destinations, boolean muc){
		//		destinations = "jhhjhjjh<897987789798978>" ;
		//		//System.out.printlnprintln("KainatContactActivity::startChat: "+destinations);
		Intent intent = null;
		if (destinations != null && destinations.trim().length() > 0 
				&& !getIntent().getBooleanExtra("REG_FLOW",false)) {
			DataModel.sSelf.storeObject(DMKeys.COMPOSE_DESTINATION_CONTACTS, destinations);			   
			if(muc)
				intent = new Intent(KainatContactActivity.this, CreateGroupActivity.class);
			else
				intent = new Intent(KainatContactActivity.this, ConversationsActivity.class);
			intent.putExtra(Constants.CONVERSATION_ID, "-1");
			intent.putExtra("START_CHAT_FROM_EXTERNAL",(byte) 1);
			intent.putExtra("MessageText",sharedText);
			startActivity(intent);
			finish();
		}
	}

	public  byte[] readRaw(Context context,int resId){
		try {
			Resources res = context.getResources();
			InputStream in_s = res.openRawResource(resId);

			byte[] b = new byte[in_s.available()];
			in_s.read(b);
			in_s.close() ;
			return b ;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}

	private void parseContact(String jsonStr){
		//id=91763554, name=nag nag<Qts>, realName=nag nag<Qts>, status=BKMARKONLINE, gender=F
		try{
			if(jsonStr == null)
				return;
			Log.i(TAG, "parseContact :: jsonStr ==> "+jsonStr);
			KeyValue.setString(KainatContactActivity.this, KeyValue.CONTACT, jsonStr);
			kainatContactList.clear();
			Gson gson = new Gson() ;
			ContactUpDatedModel contactUpDatedModel = gson.fromJson(jsonStr,					
					ContactUpDatedModel.class);
			Set <String>set = contactUpDatedModel.mobileNumberUserBaseMap.keySet() ;
			for(String key :set){
				UserDetail userDetail = contactUpDatedModel.mobileNumberUserBaseMap.get(key) ;
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setName(WordUtils.capitalize(userDetail.getName()));
				kainatContact.setUserId(userDetail.getUserName());
				kainatContact.setGender(userDetail.getGender());
				kainatContact.setUserName(userDetail.getUserName());
				kainatContact.setRealName(userDetail.getName()+"<"+userDetail.getUserName()+">") ;
				kainatContact.setPhone( new Vector<String>(Arrays.asList(key)));
				kainatContactList.add(kainatContact);
			}
			//ArrayList<KainatContact>

			set = contactUpDatedModel.emailUserBaseMap.keySet() ;

			for(String key :set){
				UserDetail userDetail = contactUpDatedModel.emailUserBaseMap.get(key) ;
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setName(WordUtils.capitalize(userDetail.getName()));
				kainatContact.setUserId(userDetail.getUserName());
				kainatContact.setUserName(userDetail.getUserName());
				kainatContact.setRealName(userDetail.getName()+"<"+userDetail.getUserName()+">") ;
				kainatContactList.add(kainatContact);
			}

			//Sorting
			Collections.sort(kainatContactList, new Comparator<KainatContact>() {
				@Override
				public int compare(KainatContact  entry1, KainatContact  entry2)
				{
					return  entry1.name.compareTo(entry2.name);
				}
			});

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	//	private void parseRT(String jsonStr){
	//		//id=91763554, name=nag nag<Qts>, realName=nag nag<Qts>, status=BKMARKONLINE, gender=F
	//		try{
	//
	//			kainatContactList.clear();
	//			Gson gson = new Gson() ;
	//			ContactUpDatedModel contactUpDatedModel = gson.fromJson(jsonStr,					
	//					ContactUpDatedModel.class);
	//
	//			Set <String>set = contactUpDatedModel.mobileNumberUserBaseMap.keySet() ;
	//
	//			for(String key :set){
	//				UserDetail userDetail = contactUpDatedModel.mobileNumberUserBaseMap.get(key) ;
	//				KainatContact kainatContact = new KainatContact() ;
	//				kainatContact.setName(userDetail.getName());
	//				kainatContactList.add(kainatContact);
	//			}
	//
	//			set = contactUpDatedModel.emailUserBaseMap.keySet() ;
	//
	//			for(String key :set){
	//				UserDetail userDetail = contactUpDatedModel.emailUserBaseMap.get(key) ;
	//				KainatContact kainatContact = new KainatContact() ;
	//				kainatContact.setName(userDetail.getName());
	//				kainatContact.setUserId(userDetail.getUserName());
	//				kainatContact.setUserName(userDetail.getUserName());
	//				kainatContact.setRealName(userDetail.getName()+"<"+userDetail.getUserName()+">") ;
	//				kainatContactList.add(kainatContact);
	//			}
	//
	//			//System.out.printlnprintln(contactUpDatedModel.toString());
	//		}catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}

	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(contactAdaptor!=null)
					contactAdaptor.notifyDataSetChanged();
			}
		});
	}

	public static String fixMobileNumbers(String mobileNumber, String defaultCountryCode, int minLengthNumber) {
		Set<String> fixedMobileNumberSet = new HashSet<String>();

		//		for (String mobileNumber : mobileNumberList) 
		{
			mobileNumber = mobileNumber.replaceFirst("[+ -]+", "");
			mobileNumber = mobileNumber.replaceFirst("^[0]+", "");
			if (mobileNumber.length() <= minLengthNumber) {
				mobileNumber = defaultCountryCode + mobileNumber;
			}
			fixedMobileNumberSet.add(mobileNumber);
		}

		return mobileNumber;
	}

	@Override
	protected void onResume() {
		reloadeViewAfterChangeLanguag();
		//refresh.performClick();
		if(UIActivityManager.ContactSelfChange){
			refresh.performClick();
			UIActivityManager.ContactSelfChange = false;
		}
		super.onResume();
	}


	public void rotateImage(View img)
	{

		rotate.setDuration(1000);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setInterpolator(new LinearInterpolator());
		img.startAnimation(rotate);
	}
	public String getCountryZipCode(){

		try{
			String CountryID="";
			String CountryZipCode="";

			TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			//getNetworkCountryIso
			CountryID= manager.getSimCountryIso().toUpperCase();

			if(CountryID==null || CountryID.trim().length()<=0)
				CountryID= manager.getNetworkCountryIso().toUpperCase();

			String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
			for(int i=0;i<rl.length;i++){
				String[] g=rl[i].split(",");
				if(g[1].trim().equals(CountryID.trim())){
					CountryZipCode=g[0];
					break;  
				}
			}

			return CountryZipCode;

		}catch (Exception e) {
			return null ;
		}
	}


	//
	//Date :18-02-2016
	//Name : Manoj Singh
	//
	//

	//  Add Friend
	//
	//
	private class GetRequestFriendTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = requestFriendsOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatContactActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			if(response!= null)
			{
				try{
					parserRequestFriend(response);	
					if(currentSelected == TAB_PENDING_REQUESTS){
						pendingRequestAdaptor = new PendingRequestAdapter(KainatContactActivity.this, requestUserDetail, null);
						if(contact_list != null)
							contact_list.setAdapter(pendingRequestAdaptor);

						if(requestUserDetail.size()<=0){
							no_pending_request.setVisibility(View.VISIBLE);
							no_contact.setVisibility(View.GONE);
							no_buddiess.setVisibility(View.GONE);
						}else
						{
							no_pending_request.setVisibility(View.GONE);
							no_contact.setVisibility(View.GONE);
							no_buddiess.setVisibility(View.GONE);
						}
					}
				}catch(Exception e){

				}
			}

		}

	}
	private void parserRequestFriend(String response) {
		// TODO Auto-generated method stub
		if(response!=null){
			try{
				requestUserDetail = new ArrayList<RequestUserDetail>();
				JSONObject  jobj = new JSONObject(response);
				if(response.contains("requesterUsers")){
					JSONArray requesterUsersArray = jobj.getJSONArray("requesterUsers");
					for (int i = 0; i < requesterUsersArray.length(); i++) {
						CommunityFeed.Entry entry = new CommunityFeed.Entry();
						JSONObject row = requesterUsersArray.getJSONObject(i);
						RequestUserDetail rud =  new RequestUserDetail();
						rud.setName(row.getString("name"));
						rud.setUserId(row.getString("userId"));
						rud.setUserName(row.getString("userName"));
						rud.setProfileUrl(row.getString("profileUrl"));
						requestUserDetail.add(rud);
					}
					if(requesterUsersArray.length()>0){
						pending_request_count.setText(""+requesterUsersArray.length());
						pending_request_count.setVisibility(View.VISIBLE);
					}else
					{
						pending_request_count.setVisibility(View.GONE);
					}
				}

			}
			catch(Exception e){

			}

			//
			/*pendingRequestAdaptor = new PendingRequestAdapter(KainatContactActivity.this, requestUserDetail, null);
			if(contact_list != null)
				contact_list.setAdapter(pendingRequestAdaptor);*/
			//

		}
	}


	private String requestFriendsOnServer(String userId){
		String strData = null;
		String url_a = null;
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/request";
		//	profileBean.setFriend(!profileBean.isFriend());
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("buddyUserId", new String(userId.trim().getBytes("UTF-8"))));
			/*	if(systemMessageAction){
			}
			else{
				if(type.equals(DELETE_REQUEST) || type.equals(REPORT_REQUEST) || type.equals(SPAM_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("groupName", new String(param.getBytes("UTF-8"))));
				}else if(type.equals(SHARE_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("sharedTo", new String(param.getBytes("UTF-8"))));
				}
			}*/
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
	}

	protected void showLoadingDialog() {
		//		showAnimationDialog("", getString(R.string.please_wait_while_loadin), true,
		//				PROGRESS_CANCEL_HANDLER);
		if(rTDialog !=null && !rTDialog.isShowing())
			rTDialog = ProgressDialog.show(KainatContactActivity.this, "", getString(R.string.loading_dot), true);
	}
	//
	//

	//
	//
	//DATE :19-02-2016
	//NAME :MANOJ SINGH
	//==================
	//Date :01-03-2016
	//Title:
	//
	//  Add Friend
	//
	//
	private class GetBuddyFriendTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				//showLoadingDialog();
				progress_loder.setVisibility(View.VISIBLE);
				rr_loder.setVisibility(View.VISIBLE);
			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = BuddyFriendsOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatContactActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			progress_loder.setVisibility(View.GONE);
			rr_loder.setVisibility(View.GONE);
			if(response!= null)
			{
				try{
					parserBuddieList(response);

					/*
					//BuddyFriendsOnServer(response);	
					if(currentSelected == TAB_PENDING_REQUESTS){
						pendingRequestAdaptor = new PendingRequestAdapter(KainatContactActivity.this, requestUserDetail, null);
						if(contact_list != null)
							contact_list.setAdapter(pendingRequestAdaptor);

						if(requestUserDetail.size()<=0){
							no_pending_request.setVisibility(View.VISIBLE);
							no_contact.setVisibility(View.GONE);
						}else
						{
							no_pending_request.setVisibility(View.GONE);
							no_contact.setVisibility(View.GONE);
						}
					}
					 */
				}catch(Exception e){

				}
			}

		}


	}
	private void parserBuddieList(String response) {
		// TODO Auto-generated method stub
		if(response!=null){
			try{
				kainatBuddiesList = new ArrayList<KainatContact>();
				JSONObject  jobj = new JSONObject(response);
				if(response.contains("friendUsers")){
					JSONArray requesterUsersArray = jobj.getJSONArray("friendUsers");
					for (int i = 0; i < requesterUsersArray.length(); i++) {
						JSONObject row = requesterUsersArray.getJSONObject(i);
						KainatContact rud =  new KainatContact();
						rud.setName(row.getString("name"));
						rud.setUserId(row.getString("userId"));
						rud.setUserName(row.getString("userName"));
						//rud.set(row.getString("profileUrl"));
						kainatBuddiesList.add(rud);
					}
				}

			}
			catch(Exception e){

			}

			// 
			if(currentSelected == TAB_CONTACT){
				if(contact_list != null){
					contactBuddyAdaptor = new ContactBuddyAdaptor() ;
					contact_list.setAdapter(contactBuddyAdaptor);
				}
				if(kainatBuddiesList.size() < 0 ||kainatBuddiesList.isEmpty()){
					no_pending_request.setVisibility(View.GONE);
					no_buddiess.setVisibility(View.VISIBLE);
					no_contact.setVisibility(View.GONE);
				}
			}
			//

		}

	}
	private String BuddyFriendsOnServer(String userId){
		String strData = null;
		String url_a = null;
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/buddies";
		//	profileBean.setFriend(!profileBean.isFriend());
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		/*	try {
			nameValuePair.add(new BasicNameValuePair("buddyUserId", new String(userId.trim().getBytes("UTF-8"))));
				if(systemMessageAction){
			}
			else{
				if(type.equals(DELETE_REQUEST) || type.equals(REPORT_REQUEST) || type.equals(SPAM_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("groupName", new String(param.getBytes("UTF-8"))));
				}else if(type.equals(SHARE_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("sharedTo", new String(param.getBytes("UTF-8"))));
				}
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
	}

	//End date 01-03-2016
	//End

	//////////////////////////////////////
	class ContactBuddyAdaptor extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return kainatBuddiesList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return kainatBuddiesList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		public void setNewItemList(ArrayList<KainatContact> itemList) {
			kainatBuddiesList.clear();
			kainatBuddiesList.addAll(itemList);
			runOnUiThread(new Runnable() {
				public void run() {
					notifyDataSetChanged();
					notifyDataSetInvalidated();
				}
			});
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			KainatContact contact = null;
			try
			{
				contact = (KainatContact) getItem(position);
			}catch(IndexOutOfBoundsException ex)
			{
				return convertView;
			}
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.kainat_contact_layout, null);
			//convertView.setBackgroundResource(R.drawable.landingpagebackground);

			((TextView) convertView
					.findViewById(R.id.contactLayout_contactName))
					.setText(BusinessProxy.sSelf.getDisplayName(contact.getName()));

			final com.kainat.app.android.uicontrol.CImageView imageView = (com.kainat.app.android.uicontrol.CImageView) convertView
					.findViewById(R.id.thumb) ;
			if(contact.getGender()!=null && contact.getGender().indexOf("F")!=-1){
				imageView.setImageResource(R.drawable.female) ;	
				imageView.setGender(3);
			}else{
				imageView.setImageResource(R.drawable.male) ;
				imageView.setGender(2);
			}
			//			else
			//			{
			//				imageView.setGender(4);
			//			}


			final ImageDownloader imageManager = new ImageDownloader(2);
			imageView.setTag(contact.getUserName()) ;
			ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

				@Override
				public void onThumbnailResponse(ThumbnailImage value,
						byte[] data) {
					try {
						//	hasProfilePic = true;

						imageView.setImageBitmap(value.mBitmap);
						imageView.invalidate() ;						
					} catch (Exception e) {
					}
				}
			};
			//System.out.printlnprintln("Contact image Url = "+contact.getUserName());
			imageView.setImageResource(R.drawable.male_icon);
			try{
				String url = "http://yelatalkprod.com/tejas/feeds/user/pic?format=150x150&user="+contact.getUserName();
				imageLoader.displayImage(url.trim(),imageView, options);
			}catch(Exception e){

			}
			//imageLoader.displayImage(entry.thumbUrl, (CImageView) findViewById(R.id.comm_profile_image), options);
			//imageManager.download(contact.getUserName(), imageView, handler,imageManager.TYPE_THUMB_BUDDY);
			if(!group)
				convertView
				.findViewById(R.id.checkBox1).setVisibility(View.GONE);
			else
				convertView
				.findViewById(R.id.checkBox1).setVisibility(View.VISIBLE);

			convertView
			.findViewById(R.id.checkBox1).setTag(contact) ;
			convertView
			.findViewById(R.id.checkBox1).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int i = 0 ;
					for (KainatContact contact:kainatBuddiesList) {
						if(contact.isSelected())
							i++ ;
					}

					//is chkIos checked?
					KainatContact contact = (KainatContact) v.getTag();

					if(i>9){
						contact.setSelected(false);	
						((CheckBox)v).setChecked(false);
						AppUtil.showTost(KainatContactActivity.this, "Can not select more thar 9 contact!");
						return;
					}

					if (((CheckBox) v).isChecked()) {
						selCounter ++;
						contact.setSelected(true);
					}else{
						selCounter --;
						contact.setSelected(false);		
					}
					if(selCounter <= 0){
						create_group.setVisibility(View.GONE);
						if(showRefresh)
							refresh.setVisibility(View.VISIBLE);
					}else{
						create_group.setVisibility(View.VISIBLE);
						refresh.setVisibility(View.GONE);
					}
				}
			});
			if(contact.isSelected()){
				((CheckBox)convertView
						.findViewById(R.id.checkBox1)).setChecked(true) ;
			}else
			{
				((CheckBox)convertView
						.findViewById(R.id.checkBox1)).setChecked(false) ;
			}


			if(contact.getPhone()!=null){
				String number = contact.getPhone().toString().trim();
				number = number.replace("[", "");
				number = number.replace("]", "");
				((TextView) convertView.findViewById(R.id.mobile)).setText("+"+number);
			}
			if(contact.getEmail()!=null)
				((TextView) convertView
						.findViewById(R.id.email))
						.setText(contact.getEmail().toString());

			imageView.setOnClickListener(profileClick);

			return convertView;
		}

	}


	//////////////////////////////////////

}
