package com.kainat.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.CommunityFallowerAdapter;
import com.kainat.app.android.bean.ContactUpDatedModel;
import com.kainat.app.android.bean.ContactUpDatedModel.UserDetail;
import com.kainat.app.android.bean.ContactUploadeModel;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.inf.InviteInfAdd;
import com.kainat.app.android.model.Buddy;
import com.kainat.app.android.model.KainatContact;
import com.kainat.app.android.util.AdConnectionManager;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class KainatCommunityFallowerContact extends UIActivityManager implements ThumbnailReponseHandler, InviteInfAdd{

	// public Handler handler = new Handler(Looper.getMainLooper());
	private static final String TAG = KainatCommunityFallowerContact.class
			.getSimpleName();
	String countryCode ;
	View no_contact;
	public View done;
	public TextView outputText;
	ListView contact_list;
	CommunityFallowerAdapter contactAdaptor;
	KainatCommunityFallowerContact mContext;
	private ArrayList<KainatContact> kainatContactList = new ArrayList<KainatContact>();
	private ArrayList<String> contactInviteArr = new ArrayList<String>();
	View refresh;
	private ProgressDialog rtDialog ;
	boolean group ;
	View message,recommended;
	TextView create_group ;
	TextView skip;
	String sharedText;
	private Button btn_invite;
	private TextView txt_msg;
	private int inviteCount = 0;
	private String gName = "";
	private Vector<String> numberArray = new Vector<String>();
	private ArrayList<Entry> preSelEntry = new ArrayList<CommunityFeed.Entry>();
	private static int MEMBER_COUNT = 50;
	
	ImageView clean_search_iv_member;
	EditText communitymemeberserch;
	int LOCALFILTER = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		mContext = this;
		countryCode		= getCountryZipCode()  ;//tm.getNetworkCountryIso();
		group 		  	= getIntent().getBooleanExtra("group", false);
		setContentView(R.layout.kainat_fallower_contact);
		//MANOJ SINGH
		//DATE :17-12-2015
		
		clean_search_iv_member = (ImageView)findViewById(R.id.clean_search_iv_member);
		communitymemeberserch = (EditText)findViewById(R.id.communitymemeberserch);
		communitymemeberserch.addTextChangedListener(new TextWatcher() {
			 
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = communitymemeberserch.getText().toString();
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
				communitymemeberserch.setText("");
			}
		});
		///
		///|END
		
		
		
//		screenSlide(this, R.layout.kainat_fallower_contact);
		preSelEntry = (ArrayList<Entry>) DataModel.sSelf.getObject("CommunityMember");
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
			if (type.startsWith("image/")){
				handleSendMultipleImages(intent); // Handle multiple images being sent
			}
		} else {
			// Handle other intents, such as being started from the home screen
		}
		no_contact 		= findViewById(R.id.no_contact) ;
		no_contact.setVisibility(View.GONE);
		recommended		= findViewById(R.id.recommended) ;
		recommended.setVisibility(View.GONE);
		create_group	= (TextView)findViewById(R.id.create_group);
		skip	= (TextView)findViewById(R.id.skip) ;
		if (getIntent().getBooleanExtra("ADDCONVERSATIONS", false)) {
			create_group.setText(R.string.done);
		}
		if(!group)
			create_group.setVisibility(View.GONE);
		create_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//open this code when, add picture is done in Multi-group
				//				Intent intent = new Intent(KainatContactActivity.this, CreateGroupActivity.class);
				//				Bundle bundleObject = new Bundle();
				//				bundleObject.putSerializable("KAINAT_CONTACT_LIST", kainatContactList);
				//				intent.putExtras(bundleObject);
				//				startActivity(intent);

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
						startChat(sb.toString()) ;
					}
				}
				else{
					AppUtil.showTost(KainatCommunityFallowerContact.this, getString(R.string.slectc_contact_to_start_chat));
				}
			}
		});

		if(UIActivityManager.directFromCreateChannel)
			skip.setVisibility(View.VISIBLE);
		else
			skip.setVisibility(View.GONE);

		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(UIActivityManager.directFromCreateChannel)
				{
//					Intent intent = new Intent(KainatCommunityFallowerContact.this, CommunityChatActivity.class);
//					startActivity(intent);
					CommunityFeed.Entry entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY+ "COMM");
					if(entry != null && userName != null && userName.length() > 0)
						entry.noOfMember = userName.split(",").length;
					UIActivityManager.refreshChannelList = true;
					UIActivityManager.directFromCreateChannel = false;
					Intent intent = new Intent(KainatCommunityFallowerContact.this, CommunityChatActivity.class);
					startActivity(intent);
					closeDialog();
				}
				finish();
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
					text.append("");
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
					AppUtil.showTost(KainatCommunityFallowerContact.this, getString(R.string.slectc_contact_to_start_chat)) ;
				}
			}
		});
		message= findViewById(R.id.message) ;
		//		if(group)
		message.setVisibility(View.GONE);
		message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//				startChat("qts");
				//				kainatContact.getRealName()startChat(sb.toString()) ;
			}
		});


		//		done= findViewById(R.id.done) ;
		//		done.setVisibility(View.GONE) ;
		//		done.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				if (getIntent().getBooleanExtra("ADDCONVERSATIONS", false)) {
		//					setResult(ConversationsActivity.ADDCONVERSATIONS);
		//					finish();
		//				}else{
		//					Intent intent = new Intent(mContext,
		//							KainatHomeActivity.class);				
		//					startActivity(intent);
		//				}
		//				//				startChat("qts");
		//			}
		//		});
		initLeftMenu();
		menuNew = (ImageButton)findViewById(R.id.menu) ;
		menuNew.setVisibility(View.GONE);
		mDrawerLayout.setDrawerLockMode(mDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		menuNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList) ;	
			}
		});


		outputText = (TextView) findViewById(R.id.textView1);

		refresh = findViewById(R.id.refresh);
		if(group)
			refresh.setVisibility(View.GONE) ;

		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(contactTask!=null)
					contactTask.cancel(true);

				kainatContactList.clear();
				contactAdaptor.notifyDataSetChanged();
				contactTask = new LoadContact();
				contactTask.execute("") ;
			}
		});


		//		loadBuddy();
		parseContact(KeyValue.getString(KainatCommunityFallowerContact.this, KeyValue.CONTACT));
		contact_list = (ListView) findViewById(R.id.contact_list);

		if(kainatContactList != null && kainatContactList.size() > 0){
			contactAdaptor = new CommunityFallowerAdapter(KainatCommunityFallowerContact.this, KainatCommunityFallowerContact.this, kainatContactList);
			contact_list.setAdapter(contactAdaptor);
		}

		if(getIntent().getBooleanExtra("REG_FLOW",false)){
			done.setVisibility(View.VISIBLE) ;
			refresh.setVisibility(View.GONE) ;
			menuNew.setVisibility(View.GONE) ;
		}

		contact_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				{
				}
			}
		});

		contact_list.setOnScrollListener(new OnScrollListener() {
			int first = 0;
			int last = 0;
			String number = null;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		            Log.i(TAG, "scrolling stopped...");
//		            if(first >= 3)
		            {
//			            int size = MEMBER_COUNT;
//			            if(kainatContactList.size() < MEMBER_COUNT){
//			            	if((kainatContactList.size() - first - 2) > 0)
//			            		size = kainatContactList.size() - first - 2;
//			            	else
//			            		size = kainatContactList.size() - first;
//			            }
			            numberArray = new Vector<String>();
			            for(int i = first; i < (last - 1); i++){
			            	if(((KainatContact)kainatContactList.get(i)).getIsInChannel() == 0){
				            	number = ((KainatContact)kainatContactList.get(i)).getMobileNumber();
				            	numberArray.add(number);
				            	Log.i(TAG, "Item at "+i+" = "+number);
			            	}
			            }
			            if(numberArray.size() > 0)
			            	new GetUserForInfoChannel().execute("");
			        }
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				first = firstVisibleItem;
				last = firstVisibleItem + visibleItemCount;
//				Log.i(TAG, "firstVisibleItem : "+firstVisibleItem);
//				Log.i(TAG, "visibleItemCount : "+visibleItemCount);
//				Log.i(TAG, "totalItemCount : "+totalItemCount);
			}
		});


		if(kainatContactList.size() <= 0)
		{
			contactTask = new LoadContact();
			contactTask.execute("") ;
		}else{
			//Send request for some numbers.
			if(kainatContactList.size() < MEMBER_COUNT)
				MEMBER_COUNT = kainatContactList.size();
            numberArray = new Vector<String>(MEMBER_COUNT);
            for(int i = 0; i < MEMBER_COUNT; i++){
            	if(((KainatContact)kainatContactList.get(i)).getIsInChannel() == 0)
            		numberArray.add(((KainatContact)kainatContactList.get(i)).getMobileNumber());
            }
            if(numberArray != null && numberArray.size() > 0)
            	new GetUserForInfoChannel().execute("");
		}

		if(getIntent().getBooleanExtra("from_community", false)){
			create_group.setVisibility(View.GONE);
			recommended.setVisibility(View.VISIBLE);
		}
		gName = getIntent().getExtras().getString("GROUP_NAME");
		//		System.out.println("Group Name = "+gName);
		if(gName != null && !gName.equals("")){
			drawBottomBar();
		}
		
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Channel Follower Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());	
	}
	
	
	private void localFilter(String val) {
		/*LOCALFILTER = 1;
		contactTask = new LoadContact();
		contactTask.execute("") ;*/
		if(val.equals(""))
		{
			contactAdaptor = new CommunityFallowerAdapter(this, this.getBaseContext(), kainatContactList);
			contact_list.setAdapter(contactAdaptor);
			//contactAdaptor.setNewItemList(kainatContactList);
			runOnUiThread(new Runnable() {
				public void run() {
					contactAdaptor.notifyDataSetChanged();
					contactAdaptor.notifyDataSetInvalidated();
				}
			});
		}else
		{
			// TODO Auto-generated method stub
			final ArrayList<KainatContact> searchList = new ArrayList<KainatContact>();
			for (KainatContact bud : kainatContactList) {
				if (bud.name.trim().toLowerCase().contains(val.toLowerCase().toString())) {
					searchList.add(bud);
				}
			}
			contactAdaptor = new CommunityFallowerAdapter(this, this.getBaseContext(), searchList);
			contact_list.setAdapter(contactAdaptor);
			//contactAdaptor.setNewItemList(searchList);
			/*runOnUiThread(new Runnable() {
				public void run() {
					contactAdaptor.notifyDataSetChanged();
					contactAdaptor.notifyDataSetInvalidated();
				}
			});*/
		}
		
	}
	
	private class GetUserForInfoChannel extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			String number = null;
//			boolean list_modified = false;
//			for(int i = 0; i < numberArray.size(); i++){
//				number = (String) numberArray.elementAt(i);
//				for (KainatContact kainatContact :kainatContactList) {
//					if(kainatContact.getMobileNumber() != null && kainatContact.getMobileNumber().equals(number)){
//						if(kainatContact.getIsInChannel() == 0){
//							kainatContact.setChannelValue((byte)0);
//							list_modified = true;
//							Log.i(TAG, "onPostExecute :: Setting channel value 2 for : "+number);
//						}
//					}
//				}
//			}
//			if(list_modified){
//				if (contactAdaptor != null)
//					contactAdaptor.notifyDataSetChanged();
//			}
		}
		@Override
		protected String doInBackground(String... urls) {
			String responseStr = null;
			String url = "http://www.yelatalkprod.com/tejas/feeds/group/find/followers";
			
			Log.i(TAG, "doInBackground :: URL : "+url);
			try {		
				JSONObject obj = new JSONObject();
				obj.put("channelName", gName);
				obj.put("userId", BusinessProxy.sSelf.getUserId());
				obj.put("mobileNumberSet", new JSONArray(numberArray.toArray()));
				
				Log.i(TAG, "doInBackground :: JSON DATA : "+obj.toString());
				// Create a new HttpClient and Post Header
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(url);
	            try {
	            	//Add Header
	            	String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
	    					BusinessProxy.sSelf.getUserId(),
	    					SettingData.sSelf.getPassword());
	            	httppost.addHeader("RT-APP-KEY", appKeyValue);
	            	Log.i(TAG, "doInBackground :: appKeyValue : "+appKeyValue);
	                // Add your data
	                StringEntity se = new StringEntity(obj.toString());  
//                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	                httppost.setEntity(se);
	                // Execute HTTP Post Request
	                HttpResponse responseHttp = httpclient.execute(httppost);
					if (responseHttp != null && responseHttp.getStatusLine().getStatusCode() ==  200) {
						responseStr = EntityUtils.toString(responseHttp.getEntity());
//						if(responseStr != null && responseStr.length() > 0)
//						Log.i(TAG, "doInBackground :: responseStr : "+responseStr);
					}
				} catch (ClientProtocolException e) {

				} catch (IOException e) {

				}
			} catch (Exception e) {
				}
			return responseStr;
		}

		@Override
		protected void onPostExecute(String response) {
			//{"channelName":"Jokology","followerSet":["919910040526","919899950714"]}
			Log.i(TAG, "onPostExecute :: response : "+response);
			try {
				JSONObject obj = new JSONObject(response);
				JSONArray members = obj.getJSONArray("followerSet");
				String number = null;
				boolean list_modified = false;
				for(int i = 0; i < members.length(); i++){
					number = (String) members.get(i);
					for (KainatContact kainatContact :kainatContactList) {
						if(kainatContact.getMobileNumber() != null && kainatContact.getMobileNumber().equals(number)){
							if(kainatContact.getIsInChannel() == 0){
								kainatContact.setChannelValue((byte)1);
								list_modified = true;
								Log.i(TAG, "onPostExecute :: Setting channel value 1 for : "+number);
							}
						}
					}
				}
				//Reset the button for members that are not in the channel, then show invite
				for(int i = 0; i < numberArray.size(); i++){
					number = (String) numberArray.elementAt(i);
					for (KainatContact kainatContact :kainatContactList) {
						if(kainatContact.getMobileNumber() != null && kainatContact.getMobileNumber().equals(number)){
							if(kainatContact.getIsInChannel() == 0){
								kainatContact.setChannelValue((byte)2);
								list_modified = true;
								Log.i(TAG, "onPostExecute :: Setting channel value 2 for : "+number);
							}
						}
					}
				}
				if(list_modified){
					if (contactAdaptor != null)
						contactAdaptor.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

	private String userName = "";
	private String status = "";
	private String descStr = "";
	ProgressDialog rTDialog;
	private void drawBottomBar() {
		// TODO Auto-generated method stub
		btn_invite				   = (Button)findViewById(R.id.btn_invite);
		txt_msg					   = (TextView)findViewById(R.id.txt_msg);
		txt_msg.setVisibility(View.VISIBLE);
		btn_invite.setVisibility(View.GONE);
		btn_invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//				rTDialog = new RTDialog(KainatCommunityFallowerContact.this, null, getString(R.string.updating));
				//				rTDialog.show();
				rTDialog = ProgressDialog.show(KainatCommunityFallowerContact.this, "", getString(R.string.loading_dot), true);

				for(int i = 0; i < contactInviteArr.size(); i++){
					userName = userName + contactInviteArr.get(i) + ",";
				}
				if(!userName.equals("") && userName.length() > 0){
					userName = userName.trim().substring(0, userName.length() - 1);
					//					System.out.println("Followers userName = "+ userName);
					//Hashtable<String, String> params = new Hashtable<String, String>();
					//					System.out.println("Group while press = "+gName);
					//					params.put("groupName", gName);
					//					params.put("addFollowers", userName);
					//					params.put("removeFollowers", "");
					//					addFallowers(params);


					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							String response = postDataOnServer(gName, userName, "");
							if(response != null){
								//							System.out.println("DATA = "+response);
								JSONObject jsonObject;
								try {
									jsonObject = new JSONObject(response);
									status = jsonObject.getString("status");
									descStr = jsonObject.getString("desc");
//									if(descStr != null)
//										Toast.makeText(KainatCommunityFallowerContact.this, descStr, Toast.LENGTH_SHORT).show();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if(status != null && status.equals("success")){
									KainatCommunityFallowerContact.this.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											CommunityFeed.Entry entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY+ "COMM");
											if(entry != null && userName != null && userName.length() > 0)
												entry.noOfMember = userName.split(",").length;
											UIActivityManager.refreshChannelList = true;
											if(UIActivityManager.directFromCreateChannel)
											{
												UIActivityManager.directFromCreateChannel = false;
												Intent intent = new Intent(KainatCommunityFallowerContact.this, CommunityChatActivity.class);
												startActivity(intent);
											}
											closeDialog();
											finish();
										}
									});
//									Toast.makeText(KainatCommunityFallowerContact.this, descStr, Toast.LENGTH_SHORT).show();
								}else{
									KainatCommunityFallowerContact.this.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											closeDialog();
											showSimpleAlert(getString(R.string.app_name), descStr);
										}
									});
								}
							}
						}
					}).start();
				}
			}
		});
	}
	private void closeDialog(){
		if(rTDialog!=null && rTDialog.isShowing())
			rTDialog.dismiss() ;
	}
	private String postDataOnServer(String gpName, String add, String remove){
		String strData = null;
		String url = null;
		url = "http://" + Urls.TEJAS_HOST
				+ "/tejas/feeds/api/group/editfollowers";
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("groupName", new String(gpName.trim().getBytes("UTF-8"))));
			nameValuePair.add(new BasicNameValuePair("addFollowers", new String(add.getBytes("UTF-8"))));
			nameValuePair.add(new BasicNameValuePair("removeFollowers", new String(remove.getBytes("UTF-8"))));
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
				//				System.out.println("DATA = "+IOUtils.read(ins));
				strData = IOUtils.read(ins);
				ins.close();
			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}
		return strData;
	}


	void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			// Update UI to reflect image being shared
		}
	}


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
		protected void onPreExecute() {
			rtDialog = ProgressDialog.show(KainatCommunityFallowerContact.this, "", getString(R.string.synch_contact), true);
		}

		@Override
		protected String doInBackground(String... params) {


			try{
				Thread.sleep(500);
			}catch (Exception e) {
				// TODO: handle exception
			}
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

			//			StringBuffer output = new StringBuffer();

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
							//							System.out.println("Name-"+name+"-"+phoneNumber);

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
							//							System.out.println("Email-"+email);

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
				Hashtable<String, String> postParams = new Hashtable<String, String>() ;
				String response = AdConnectionManager.getInstance()
						.uploadByteDataBody("http://"+ com.kainat.app.android.util.Urls.TEJAS_HOST +"/tejas/feeds/contact/upload",
								gson.toJson(contactUploadeModel), null, null);
				if(response!=null){
					parseContact(response) ;
				}
				Log.d(TAG, " ++  Contact upload response ++ "+response) ;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
			Log.d(TAG, gson.toJson(contactUploadeModel)) ;			
			return null;
		}
		@Override
		protected void onPostExecute(String result) {

			if(kainatContactList!= null && kainatContactList.size()<=0){
				no_contact.setVisibility(View.VISIBLE);
			}
			else
			{
				no_contact.setVisibility(View.GONE);
			//	if(LOCALFILTER != 1){
					if(contactAdaptor!=null){
						contactAdaptor.notifyDataSetChanged();
					}else
					{
						if(kainatContactList != null && kainatContactList.size() > 0){
							contactAdaptor = new CommunityFallowerAdapter(KainatCommunityFallowerContact.this, KainatCommunityFallowerContact.this, kainatContactList);
							contact_list.setAdapter(contactAdaptor);
						}
					}
				//	LOCALFILTER = 0;
				//}
			}


			if(rtDialog != null && rtDialog.isShowing())
				rtDialog.dismiss();




			if(getIntent().getBooleanExtra("REG_FLOW",false)){
				done.setVisibility(View.VISIBLE) ;
				refresh.setVisibility(View.GONE) ;
			}
			//Send request for some numbers.
//			if(kainatContactList.size() < MEMBER_COUNT)
//				MEMBER_COUNT = kainatContactList.size();
//            numberArray = new Vector<String>(MEMBER_COUNT);
//            for(int i = 0; i < MEMBER_COUNT; i++){
//            	if(((KainatContact)kainatContactList.get(i)).getIsInChannel() == 0)
//            		numberArray.add(((KainatContact)kainatContactList.get(i)).getMobileNumber());
//            }
//            if(numberArray.size() > 0)
//            	new GetUserForInfoChannel().execute("");
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			//			contactAdaptor.notifyDataSetChanged();
		}
	}

	public void fetchContacts() {


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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			KainatContact contact = (KainatContact) getItem(position);
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.kainat_contact_layout, null);
			convertView.setBackgroundResource(R.drawable.landingpagebackground);

			((TextView) convertView
					.findViewById(R.id.contactLayout_contactName))
					.setText(BusinessProxy.sSelf.getDisplayName(contact.getName()));

			com.kainat.app.android.uicontrol.CImageView imageView = (com.kainat.app.android.uicontrol.CImageView) convertView
					.findViewById(R.id.thumb) ;
			if(contact.getGender()!=null && contact.getGender().indexOf("F")!=-1){
				imageView.setImageResource(R.drawable.female) ;	
				imageView.setGender(3);
			}else{
				imageView.setImageResource(R.drawable.male) ;
				imageView.setGender(2);
			}
			ImageDownloader imageManager = new ImageDownloader(2);
			imageView.setTag(contact.getUserName()) ;
			imageManager.download(contact.getUserName(), imageView, KainatCommunityFallowerContact.this,imageManager.TYPE_THUMB_BUDDY);
			if(!group)
				convertView.findViewById(R.id.checkBox1).setVisibility(View.GONE);
			else
				convertView.findViewById(R.id.checkBox1).setVisibility(View.VISIBLE);

			convertView.findViewById(R.id.checkBox1).setTag(contact) ;
			convertView.findViewById(R.id.checkBox1).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int i = 0 ;
					for (KainatContact contact:kainatContactList) {
						if(contact.isSelected())
							i++ ;
					}

					//is chkIos checked?
					KainatContact contact = (KainatContact) v.getTag();

					if(i > 9){
						contact.setSelected(false);	
						((CheckBox)v).setChecked(false);
						AppUtil.showTost(KainatCommunityFallowerContact.this, "Can't select more than 9 contact!");
						return;
					}

					if (((CheckBox) v).isChecked()) {
						contact.setSelected(true);
					}else
						contact.setSelected(false);			
				}
			});
			if(contact.isSelected())
				((CheckBox)convertView.findViewById(R.id.checkBox1)).setChecked(true) ;
			else
				((CheckBox)convertView.findViewById(R.id.checkBox1)).setChecked(false) ;
			if(contact.getPhone()!=null){
				String number = contact.getPhone().toString().trim();
				number = number.replace("[", "");
				number = number.replace("]", "");
				((TextView) convertView.findViewById(R.id.mobile)).setText("+"+number);
			}
			if(contact.getEmail()!=null)
				((TextView) convertView.findViewById(R.id.email)).setText(contact.getEmail().toString());
			imageView.setOnClickListener(profileClick);
			return convertView;
		}

	}

	OnClickListener profileClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				String userName  = (String) v.getTag();
				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, userName);
				Intent intent = new Intent(KainatCommunityFallowerContact.this, ProfileViewActivity.class);
				intent.putExtra("USERID", userName);
				intent.putExtra("CallFrom",1);
				startActivity(intent);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	@Override
	public void onBackPressed() {
		if(!UIActivityManager.directFromCreateChannel)
		{
			finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
		}
	}

	private void startChat(String destinations){
		//		destinations = "jhhjhjjh<897987789798978>" ;
		//		System.out.println("KainatContactActivity::startChat: "+destinations);
		if (destinations != null
				&& destinations.trim().length() > 0) {
			finish();
			DataModel.sSelf
			.storeObject(
					DMKeys.COMPOSE_DESTINATION_CONTACTS,
					destinations);
			Intent intent = new Intent(KainatCommunityFallowerContact.this,ConversationsActivity.class);
			intent.putExtra(Constants.CONVERSATION_ID, "-1");
			intent.putExtra("START_CHAT_FROM_EXTERNAL",(byte) 1);
			intent.putExtra("MessageText",sharedText);
			startActivity(intent);
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
			//			System.out.println("---parseContact>"+jsonStr);
			if(jsonStr==null)
				return ;
			KeyValue.setString(KainatCommunityFallowerContact.this, KeyValue.CONTACT, jsonStr);

			kainatContactList.clear();
			Gson gson = new Gson() ;
			ContactUpDatedModel contactUpDatedModel = gson.fromJson(jsonStr,					
					ContactUpDatedModel.class);

			Set <String>set = contactUpDatedModel.mobileNumberUserBaseMap.keySet() ;

			for(String key :set){
				UserDetail userDetail = contactUpDatedModel.mobileNumberUserBaseMap.get(key) ;
				//			KainatContact kainatContact = new KainatContact() ;
				//			kainatContact.setName(userDetail.getName());
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setName(userDetail.getName());
				kainatContact.setUserId(userDetail.getUserName());
				kainatContact.setUserName(userDetail.getUserName());
				kainatContact.setRealName(userDetail.getName()+"<"+userDetail.getUserName()+">") ;
				kainatContact.setPhone( new Vector<String>(Arrays.asList(key)));
				kainatContact.setMobileNumber(key);
				kainatContact.setChannelValue((byte)0);//Initially 0 for everyone.
				kainatContactList.add(kainatContact);
			}

			set = contactUpDatedModel.emailUserBaseMap.keySet() ;

			for(String key :set){
				UserDetail userDetail = contactUpDatedModel.emailUserBaseMap.get(key) ;
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setName(userDetail.getName());
				kainatContact.setUserId(userDetail.getUserName());
				kainatContact.setUserName(userDetail.getUserName());
				kainatContact.setRealName(userDetail.getName()+"<"+userDetail.getUserName()+">") ;
				kainatContact.setMobileNumber(userDetail.getMobileNumber());
				kainatContact.setChannelValue((byte)0);//Initially 0 for everyone.
				kainatContactList.add(kainatContact);
			}
			if(preSelEntry != null){
				for(int i = 0; i < preSelEntry.size(); i++){
					final Entry entry = preSelEntry.get(i);
					for(int j = 0; j < kainatContactList.size(); j ++){
						if(kainatContactList.get(j).getUserName().equalsIgnoreCase(entry.userName)){
							((KainatContact)kainatContactList.get(i)).setChannelValue((byte)1);
//							kainatContactList.remove(j);
						}
					}

				}
			}
			//			System.out.println(contactUpDatedModel.toString());

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean getUserNameChecked(String username){
		boolean ishere = false;
		if(contactInviteArr != null)
		for(int i=0;i<contactInviteArr.size();i++){
			if(contactInviteArr.get(i).equals(username)){
				ishere = true;
				break;
			}
		}
		
		return ishere;
	}
	@Override
	public void clickedcount(int value, int position, String username ) {
		// TODO Auto-generated method stub
		if(value == 1)
		{
			inviteCount = inviteCount + 1;
		}else
		{
			inviteCount = inviteCount - 1;
		}

		btn_invite.setText(getString(R.string.add_lbl)+ " ("+inviteCount+")");

		if(inviteCount <= 0){
			txt_msg.setVisibility(View.VISIBLE);
			btn_invite.setVisibility(View.GONE);
		}else
		{
			txt_msg.setVisibility(View.GONE);
			btn_invite.setVisibility(View.VISIBLE);

		}
		contactnumber(position,value,username);
	}
	public String contactnumber(int pos,int value, String user_Name){
		String userName = null;
		try{
			userName = user_Name;
		//	userName=  kainatContactList.get(pos).getUserName();
		}catch(Exception e){

		}
		if(value == 1)
		{
			if(userName!=null)
				contactInviteArr.add(userName);
		}else
		{
			for(int i=0;i<contactInviteArr.size();i++){
				if(contactInviteArr.get(i).equals(userName)){
					contactInviteArr.remove(i);
				}
			}
		}

		return null;	
	}
	private void parseRT(String jsonStr){
		//id=91763554, name=nag nag<Qts>, realName=nag nag<Qts>, status=BKMARKONLINE, gender=F
		try{

			kainatContactList.clear();
			Gson gson = new Gson() ;
			ContactUpDatedModel contactUpDatedModel = gson.fromJson(jsonStr,					
					ContactUpDatedModel.class);

			Set <String>set = contactUpDatedModel.mobileNumberUserBaseMap.keySet() ;

			for(String key :set){
				UserDetail userDetail = contactUpDatedModel.mobileNumberUserBaseMap.get(key) ;
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setName(userDetail.getName());
				kainatContactList.add(kainatContact);
			}

			set = contactUpDatedModel.emailUserBaseMap.keySet() ;

			for(String key :set){
				UserDetail userDetail = contactUpDatedModel.emailUserBaseMap.get(key) ;
				KainatContact kainatContact = new KainatContact() ;
				kainatContact.setName(userDetail.getName());
				kainatContact.setUserId(userDetail.getUserName());
				kainatContact.setUserName(userDetail.getUserName());
				kainatContact.setRealName(userDetail.getName()+"<"+userDetail.getUserName()+">") ;
				kainatContactList.add(kainatContact);
			}

			//			System.out.println(contactUpDatedModel.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		super.onResume();
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
	public void removeFollower(final String remove){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String response = removeMemberFromChannel(gName, "", remove);
				if(response != null){
					//				System.out.println("DATA = "+response);
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(response);
						status = jsonObject.getString("status");
						descStr = jsonObject.getString("desc");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						if(rtDialog != null && rtDialog.isShowing())
							rtDialog.dismiss();
						e.printStackTrace();
					}
					if(rtDialog != null && rtDialog.isShowing())
						rtDialog.dismiss();
					if(status != null && status.equals("success")){
						KainatCommunityFallowerContact.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub

								if(contactAdaptor != null){
//									kainatContactList.remove(index)
									contactAdaptor.notifyDataSetChanged();
								}
								UIActivityManager.refreshChannelList = true;
							}
						});
					}else if(status != null && status.equals("error")){
						KainatCommunityFallowerContact.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								showSimpleAlert(getString(R.string.app_name), descStr);
							}
						});
					}
				}
			}
		}).start();
	}
	private String removeMemberFromChannel(String gpName, String add, String remove){
		String strData = null;
		String url = null;
		url = "http://" + Urls.TEJAS_HOST
				+ "/tejas/feeds/api/group/editfollowers";
		
		rtDialog = ProgressDialog.show(KainatCommunityFallowerContact.this, "", getString(R.string.please_wait_while_loadin), true);
		
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("groupName", new String(gpName.getBytes("UTF-8"))));
			nameValuePair.add(new BasicNameValuePair("addFollowers", new String(add.getBytes("UTF-8"))));
			nameValuePair.add(new BasicNameValuePair("removeFollowers", new String(remove.getBytes("UTF-8"))));
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
			if(rtDialog != null && rtDialog.isShowing())
				rtDialog.dismiss();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				//				System.out.println("DATA = "+IOUtils.read(ins));
				strData = IOUtils.read(ins);
				ins.close();
			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			if(rtDialog != null && rtDialog.isShowing())
				rtDialog.dismiss();
			return strData;
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			if(rtDialog != null && rtDialog.isShowing())
				rtDialog.dismiss();
			return strData;
		}
		return strData;
	}

}
