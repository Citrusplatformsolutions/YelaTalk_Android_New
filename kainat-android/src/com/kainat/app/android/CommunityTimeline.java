package com.kainat.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.TimlineListAdapter;
import com.kainat.app.android.bean.ChannelPostAPIGetFeed;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.CommunityChatDB;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.SharedPrefManager;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.format.SettingData;

public class CommunityTimeline extends UICommunityManager implements
		OnScrollListener {
	ListView chatList_timline;
	ImageView img_add_timeline, img_timeline_chat, img_nodata;
	static String groupname, thumburl, tags_name, ownername, broadcast;
	int group_id;
	int IsAdmin;
	ProgressDialog rtDialog;
	ChannelPostAPIGetFeed channelPostForm;
	CommunityChatDB CCB;
	TextView new_refresh_feed_txt, community_timeline_title;
	int maxChannelPostid = -1;
	Timer timer;
	public static int LOAD_DATA_MYSELF = 0;
	SharedPrefManager iPrefManager = null;
	RelativeLayout rel_lay_firttime_help;
	ImageView img_firsttime;
	int optionIDsTop[] = new int[] { 1, 3, 2 };
	String idsNameOptionsTop[] = null;
	TimlineListAdapter TLA;
	LinearLayout layout_broadcast;
	private CommunityFeed.Entry entry = null;
	int downloadInprocess = 0;
	int glbLastItem =0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		setContentView(R.layout.community_timline);
		CCB = new CommunityChatDB(this);
		idsNameOptionsTop = new String[] { getString(R.string.profile),
				getString(R.string.followers_u), getString(R.string.cancel) };
		showHelpScreen();
		entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY + "COMM");
		init();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		iLoggedUserID = BusinessProxy.sSelf.getUserId();
		if (channelPostForm != null
				&& channelPostForm.getChannelPostAPIList() != null) {

			chatList_timline.setVisibility(View.VISIBLE);
			img_nodata.setVisibility(View.GONE);

		} else {
			chatList_timline.setVisibility(View.GONE);
			img_nodata.setVisibility(View.VISIBLE);
		}
	}

	private void showHelpScreen() {
		// TODO Auto-generated method stub
		iPrefManager = SharedPrefManager.getInstance();
		rel_lay_firttime_help = (RelativeLayout) findViewById(R.id.rel_lay_firttime_help);
		rel_lay_firttime_help.setVisibility(View.GONE);
		img_firsttime = (ImageView) findViewById(R.id.img_firsttime);
		img_firsttime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iPrefManager.saveHelpScreenTimeline(true);
				rel_lay_firttime_help.setVisibility(View.GONE);
			}
		});

		boolean val = iPrefManager.getHelpScreenTimeline();
		if (!val) {
			rel_lay_firttime_help.setVisibility(View.VISIBLE);
		} else {
			rel_lay_firttime_help.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (LOAD_DATA_MYSELF == 1) {
			String[] abc = new String[3];
			abc[0] = groupname.trim();
			abc[1] = "" + 1;
			abc[2] = "" + maxChannelPostid;

			postDataRequestForList request = new postDataRequestForList(2);
			request.execute(abc);
			LOAD_DATA_MYSELF = 0;

		}

		if (!timelineFollow) {
			img_add_timeline.setVisibility(View.GONE);
		} else {

			// img_add_timeline.setVisibility(View.VISIBLE);
		}

	}

	private void setToBroadcast(String state) {
		// TODO Auto-generated method stub
		if (state != null) {
			if (state.equalsIgnoreCase("mute")) {
				layout_broadcast.setVisibility(View.VISIBLE);
				img_add_timeline.setVisibility(View.GONE);
			} else if (state.equalsIgnoreCase("active")) {
				layout_broadcast.setVisibility(View.GONE);
				img_add_timeline.setVisibility(View.VISIBLE);
			}
		}
	}

	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			groupname = intent.getStringExtra("group_name");
			group_id = intent.getIntExtra("group_id", 0);
			thumburl = intent.getStringExtra("thumburl");
			tags_name = intent.getStringExtra("tags_name");
			ownername = intent.getStringExtra("ownername");
			IsAdmin = intent.getIntExtra("IsAdmin", 0);
			broadcast = intent.getStringExtra("broadcast");
			channelPostForm = CCB.fetchTimelinedata(groupname);

		}

		community_timeline_title = (TextView) findViewById(R.id.community_timeline_title);
		layout_broadcast = (LinearLayout) findViewById(R.id.layout_broadcast);
		community_timeline_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
				Intent intentC = new Intent(CommunityTimeline.this,
						CommunityProfileNewActivity.class);
				intentC.putExtra("group_name", groupname);
				intentC.putExtra("tags_name", tags_name);
				intentC.putExtra("group_id", "" + group_id);

				startActivity(intentC);

			}
		});
		community_timeline_title.setText(groupname);
		if (thumburl.trim().startsWith("http://"))
			imageLoader
					.displayImage(thumburl,
							(CImageView) findViewById(R.id.comm_profile_image),
							options);
		else
			((CImageView) findViewById(R.id.comm_profile_image))
					.setImageURI(Uri.parse(thumburl));

		if (channelPostForm != null
				&& channelPostForm.getChannelPostAPIList() != null
				&& channelPostForm.getChannelPostAPIList().size() > 0) {
			String[] abc = new String[2];
			abc[0] = groupname.trim();
			abc[1] = ""
					+ channelPostForm.getChannelPostAPIList().get(0)
							.getChannelPostId();
			RefreshDataRequestForList request = new RefreshDataRequestForList();
			request.execute(abc);
		} else {
			String[] abc = new String[3];
			abc[0] = groupname.trim();
			abc[1] = "" + 0;
			abc[2] = "" + maxChannelPostid;

			postDataRequestForList request = new postDataRequestForList(2);
			request.execute(abc);
		}

		// TODO Auto-generated method stub
		chatList_timline = (ListView) findViewById(R.id.chatList_timline);

		chatList_timline.setOnScrollListener(this);
		img_add_timeline = (ImageView) findViewById(R.id.img_add_timeline);

		img_nodata = (ImageView) findViewById(R.id.img_nodata);
		img_timeline_chat = (ImageView) findViewById(R.id.img_timeline_chat);
		new_refresh_feed_txt = (TextView) findViewById(R.id.new_refresh_feed_txt);

		new_refresh_feed_txt.setVisibility(View.GONE);
		if (channelPostForm != null
				&& channelPostForm.getChannelPostAPIList() != null
				&& channelPostForm.getChannelPostAPIList().size() > 0) {
			chatList_timline.setVisibility(View.VISIBLE);
			img_nodata.setVisibility(View.GONE);
			TLA = new TimlineListAdapter(CommunityTimeline.this,
					channelPostForm, IsAdmin);
			TLA.refreshMe(TLA, chatList_timline);
			chatList_timline.setAdapter(TLA);
			registerForContextMenu(chatList_timline);
		} else {
			chatList_timline.setVisibility(View.GONE);
			img_nodata.setVisibility(View.VISIBLE);
		}

		/*
		 * TimlineListAdapter TLA = new TimlineListAdapter(this,
		 * channelPostForm); chatList_timline.setAdapter(TLA);
		 */

		new_refresh_feed_txt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] abc = new String[3];
				abc[0] = groupname;
				abc[1] = "" + 1;
				abc[2] = "" + maxChannelPostid;

				postDataRequestForList request = new postDataRequestForList(2);
				request.execute(abc);
			}
		});

		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		img_timeline_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
			}
		});

		img_add_timeline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(CommunityTimeline.this,
						CommunityPostActivity.class);
				i.putExtra("group_name", groupname);
				i.putExtra("group_id", "" + group_id);
				startActivity(i);
			}
		});

		setTimerForAdvertise();

		findViewById(R.id.menu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ImageView) findViewById(R.id.menu))
						.setImageResource(R.drawable.x_options_sel);
				action(optionIDsTop, idsNameOptionsTop, (byte) 3);
				quickAction.show(v);
			}
		});
		if (!(entry.ownerId == BusinessProxy.sSelf.getUserId()))
			setToBroadcast(broadcast);
	}

	@Override
	public void onScroll(AbsListView lw, final int firstVisibleItem,
			final int visibleItemCount, final int totalItemCount) {

		switch (lw.getId()) {
		case R.id.chatList_timline:

			// Make your calculation stuff here. You have all your
			// needed info from the parameters of this function.

			// Sample calculation to determine if the last
			// item is fully visible.
			if (channelPostForm != null
					&& !channelPostForm.getChannelPostAPIList().isEmpty()) {
				final int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					if(downloadInprocess == 0){
					String[] abc = new String[3];
					abc[0] = groupname;
					abc[1] = "" + 2;
					abc[2] = "" + 0;
					//lw.setSelection(lastItem);
					glbLastItem =lastItem;
					postDataRequestForList request = new postDataRequestForList(1);
					request.execute(abc);
					/*chatList_timline.post(new Runnable() {
					        @Override
					        public void run() {
					            // Select the last row so it will scroll into view...
					        	chatList_timline.setSelection(lastItem - 1);
					        }
					    });*/
				
					}
					/*
					 * if(preLast!=lastItem){ //to avoid multiple calls for last
					 * item Log.d("Last", "Last"); preLast = lastItem;
					 */
				}
			}
		}
	}

	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	void setTimerForAdvertise() {
		timer = new Timer();
		TimerTask updateProfile = new CustomTimerTask();
		timer.scheduleAtFixedRate(updateProfile, 50000, 50000);
	}

	public class CustomTimerTask extends TimerTask {

		private Handler mHandler = new Handler();

		@Override
		public void run() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							try {
								if (channelPostForm != null
										&& channelPostForm
												.getChannelPostAPIList() != null
										&& channelPostForm
												.getChannelPostAPIList().size() > 0) {
									String[] abc = new String[2];
									abc[0] = groupname;
									abc[1] = ""
											+ channelPostForm
													.getChannelPostAPIList()
													.get(0).getChannelPostId();
									RefreshDataRequestForList request = new RefreshDataRequestForList();
									request.execute(abc);
								} else {
									String[] abc = new String[2];
									abc[0] = groupname;
									abc[1] = "" + 0;
									RefreshDataRequestForList request = new RefreshDataRequestForList();
									request.execute(abc);
								}
							} catch (Exception e) {
								Log.v("Exception recursive cll", "" + e);
							}
							/*
							 * String[] abc = new String[3]; abc[0] = groupname;
							 * abc[1] = ""+1; abc[2] = ""+ maxChannelPostid;
							 * 
							 * postDataRequestForList request = new
							 * postDataRequestForList(); request.execute(abc);
							 */
						}
					});
				}
			}).start();

		}

	}

	@Override
	public void onTaskCallback(Object parameter, byte mRequestObjNo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	// Getting list of data
	public class postDataRequestForList extends
			AsyncTask<String, Integer, String> {
		int showloder = 1;
		postDataRequestForList(int showloder){
			this.showloder =showloder;
		}
		@Override
		protected void onPreExecute() {
			downloadInprocess = 1;
			if(showloder == 2){
			rtDialog = ProgressDialog.show(CommunityTimeline.this, "",
					getString(R.string.refreshing_list), true);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServer(params[0], params[1], params[2]);
			return strData;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				// {"message":"Channel post created successfully.","status":"success","channelPostId":44}
				JSONObject jmain;
				try {
					// GSON PARSING
					channelPostForm = new Gson().fromJson(result,
							ChannelPostAPIGetFeed.class);
					// Inserting data in db
					CCB.saveTimeLineList(channelPostForm);
					jmain = new JSONObject(result);
					if (jmain.has("nextUrl")) {
						KeyValue.setString(CommunityTimeline.this, "TIMELINE"
								+ group_id, jmain.getString("nextUrl"));
					}else if (jmain.has("prevUrl")) {
						KeyValue.setString(CommunityTimeline.this, "TIMELINE"
								+ group_id, "");
					}

					// if(channelPostAPIList != null &&
					// channelPostAPIList.size() > 0 )
					channelPostForm = CCB.fetchTimelinedata(groupname);
					// parseAllData(result);
					if (channelPostForm != null
							&& channelPostForm.getChannelPostAPIList() != null
							&& channelPostForm.getChannelPostAPIList().size() > 0) {

						chatList_timline.setVisibility(View.VISIBLE);
						img_nodata.setVisibility(View.GONE);
						TLA = new TimlineListAdapter(CommunityTimeline.this,
								channelPostForm, IsAdmin);
						TLA.refreshMe(TLA, chatList_timline);
						chatList_timline.setAdapter(TLA);
						if(glbLastItem < channelPostForm.getChannelPostAPIList().size()){
							chatList_timline.setSelection(glbLastItem);
						}
						registerForContextMenu(chatList_timline);
					} else {
						chatList_timline.setVisibility(View.GONE);
						img_nodata.setVisibility(View.VISIBLE);
					}
					new_refresh_feed_txt.setVisibility(View.GONE);
					jmain = new JSONObject(result);
					/*
					 * if (jmain.has("message")) runOnUiThread(new Runnable() {
					 * 
					 * @Override public void run() { try {
					 * AppUtil.showTost(CommunityTimeline.this,
					 * jmain.getString("message")); } catch (JSONException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 * } });
					 */
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			Log.i("TAG", "DeleteRequest :: onPostExecute : result : " + result);
			if(showloder == 2){
			closeDialog();
			}
			// finish();
			downloadInprocess = 0;
		}
	}

	private String postDataOnServer(String gpName, String isrefresh,
			String maxid) {
		String strData = null;
		String url = null;
		// gpName = gpName.replaceAll(" ", "+");
		// URL:
		// http://www.yelatalkprod.com/tejas/feeds/api/channel/post/get?groupName=comedynight
		if (isrefresh.equals("0")) {// if(Integer.parseInt("isrefresh")== 0)

			url = "http://"
					+ Urls.URL_LIVE
					+ "/tejas/feeds/api/channel/post/get?iThumbFormat=300x300&vThumbFormat=300x300&iFormat=600x600";
		} else if (isrefresh.equals("1")) {
			// /feeds/api/channel/post/refresh
			url = "http://"
					+ Urls.URL_LIVE
					+ "/tejas/feeds/api/channel/post/refresh?&iThumbFormat=300x300&vThumbFormat=300x300&iFormat=600x600";
		} else if (isrefresh.equals("2")) {
			String val = (KeyValue.getString(CommunityTimeline.this,
					("TIMELINE" + group_id)));
			if (!(val == null) && !(val.equals(""))) {
				url = val;
			} else {
				return null;

			}
		} /*
		 * else { return null; }
		 */
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("groupName", new String(
					gpName.getBytes("UTF-8"))));
			/*
			 * nameValuePair.add(new
			 * BasicNameValuePair("vThumbFormat","300x300"));
			 */
			if (isrefresh.equals("1")) {
				nameValuePair.add(new BasicNameValuePair("maxChannelPostId",
						new String(maxid.getBytes("UTF-8"))));
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int responceCode = response.getStatusLine().getStatusCode();

			if (responceCode == 200) {
				InputStream ins = response.getEntity().getContent();
				// System.out.println("DATA = "+IOUtils.read(ins));
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

	private void closeDialog() {
		if (rtDialog != null && rtDialog.isShowing())
			rtDialog.dismiss();
	}

	//
	//
	// DATE 06-05-2016
	// Refresh
	public class RefreshDataRequestForList extends
			AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			/*
			 * rtDialog = ProgressDialog.show(CommunityTimeline.this, "",
			 * getString(R.string.refreshing_list), true);
			 */
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postRefreshDataOnServer(params[0], params[1]);
			return strData;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				// {"message":"Channel post created successfully.","status":"success","channelPostId":44}
				final JSONObject jmain;
				try {

					jmain = new JSONObject(result);
					if (jmain.has("numberOfNewPosts")) {
						int val = jmain.getInt("numberOfNewPosts");
						if (val > 0) {
							new_refresh_feed_txt.setVisibility(View.VISIBLE);
							// new_refresh_feed_txt.setText(""+val
							// +(R.string.new_feed));

						} else {
							new_refresh_feed_txt.setVisibility(View.GONE);
						}
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			Log.i("TAG", "DeleteRequest :: onPostExecute : result : " + result);
			// closeDialog();
			// finish();

		}
	}

	private String postRefreshDataOnServer(String gpName, String maxid) {
		String strData = null;
		String url = null;
		// gpName = gpName.replaceAll(" ", "+");
		// URL:
		// http://www.yelatalkprod.com/tejas/feeds/api/channel/post/count?groupName=Cricket+Fan+Club&maxChannelPostId=28
		maxChannelPostid = Integer.parseInt(maxid);
		url = "http://" + Urls.URL_LIVE + "/tejas/feeds/api/channel/post/count";
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("groupName", new String(
					gpName.getBytes("UTF-8"))));
			nameValuePair.add(new BasicNameValuePair("maxChannelPostId",
					new String(maxid.getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
		} catch (Exception e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int responceCode = response.getStatusLine().getStatusCode();

			if (responceCode == 200) {
				InputStream ins = response.getEntity().getContent();
				// System.out.println("DATA = "+IOUtils.read(ins));
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

	public void action(int ids[], String[] name, final byte type) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, true,
				name.length);

		for (int i = 0; i < name.length; i++) {
			ActionItem nextItem = new ActionItem(ids[i], name[i], null);
			quickAction.addActionItem(nextItem);
		}

		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						onOptionsItemSelected(actionId);
					}
				});

		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				((ImageView) findViewById(R.id.menu))
						.setImageResource(R.drawable.x_options);
			}
		});
	}

	public boolean onOptionsItemSelected(int item) {
		try {
			switch (item) {
			case 1:

				DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
				Intent intentC = new Intent(CommunityTimeline.this,
						CommunityProfileNewActivity.class);
				intentC.putExtra("group_name", groupname);
				intentC.putExtra("tags_name", tags_name);
				intentC.putExtra("group_id", "" + group_id);

				startActivity(intentC);
				// this.finish();

				return true;
			case 3:
				/*
				 * stopTimerToRefresh(); if(!feedLoadedFirstTime) {
				 * cancelLoading(); resetLoading(); switchedToScreenWhileLoading
				 * = true; Log.i(TAG,
				 * "onOptionsItemSelected :: CommunityMemberActivity called.");
				 * }
				 */
				Intent intents = new Intent(CommunityTimeline.this,
						CommunityMemberActivity.class);
				String val = "http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/api/group/searchmember?groupid="
						+ group_id + "&user=";
				intents.putExtra("member_url", val);
				intents.putExtra("OwnerUserName", ownername);
				intents.putExtra(
						"title",
						((TextView) findViewById(R.id.community_timeline_title))
								.getText());
				startActivity(intents);
				return true;
			case 4:
				intents = new Intent(CommunityTimeline.this,
						KainatContactActivity.class);// BuddyActivity
				intents.putExtra("group", true);
				intents.putExtra("from_community", true);
				intents.putExtra("community_name", groupname);
				// intent.putExtra(BuddyActivity.SCREEN_MODE,
				// BuddyActivity.MODE_ONLY_FRIENDS);
				startActivity(intents);
				return true;
			case 5:
				/*
				 * DialogInterface.OnClickListener handler = new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) { if(entry !=
				 * null){ StringBuilder text = new StringBuilder(
				 * "DeleteCommunity::Name##"); text.append(entry.groupName); if
				 * (BusinessProxy.sSelf.sendNewTextMessage(
				 * "Community manager<a:communitymgr>", text.toString(), true))
				 * { CommunityChatActivity.this.finish();
				 * DataModel.sSelf.storeObject( DMKeys.COMMUNITY_DELETED,
				 * Boolean.TRUE); } } } }; showAlertMessage(
				 * getString(R.string.app_name),
				 * getString(R.string.do_you_really_want_to_delete_this_community
				 * ), new int[] { DialogInterface.BUTTON_POSITIVE,
				 * DialogInterface.BUTTON_NEUTRAL }, new String[] { "Yes", "No"
				 * }, new DialogInterface.OnClickListener[] { handler, null });
				 */

				return true;
			case 6:
				/*
				 * handler = new DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) { if(entry !=
				 * null){ StringBuilder text = new
				 * StringBuilder("Leave::Name##"); text.append(entry.groupName);
				 * if (BusinessProxy.sSelf.sendNewTextMessage(
				 * "Community manager<a:communitymgr>", text.toString(), true))
				 * { CommunityChatActivity.this.finish();
				 * DataModel.sSelf.storeObject( DMKeys.COMMUNITY_DELETED,
				 * Boolean.TRUE); } } } }; showAlertMessage(
				 * getString(R.string.app_name),
				 * getString(R.string.do_you_really_want_to_leave_this_community
				 * ), new int[] { DialogInterface.BUTTON_POSITIVE,
				 * DialogInterface.BUTTON_NEUTRAL }, new String[] {
				 * getString(R.string.yes), getString(R.string.no), }, new
				 * DialogInterface.OnClickListener[] { handler, null });
				 */

				return true;
			case 7: {/*
					 * 
					 * DialogInterface.OnClickListener exitHandler = new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface dialog, int which) { if(entry !=
					 * null){ StringBuilder text = new
					 * StringBuilder("Report::grpN##");
					 * text.append(entry.groupName); text.append("::gId##");
					 * text.append(feed.groupId); if
					 * (BusinessProxy.sSelf.sendNewTextMessage(
					 * "Community Manager<a:communitymgr>", text.toString(),
					 * true)) {
					 * 
					 * new Thread(new Runnable() { public void run() {
					 * showSimpleAlert( getString(R.string.info),
					 * getString(R.string.yourrequesthasbeensent)); }
					 * }).start();// } } } }; showAlertMessage(
					 * getString(R.string.confirm), getString(R.string.
					 * do_you_really_want_to_report_this_community), new int[] {
					 * DialogInterface.BUTTON_POSITIVE,
					 * DialogInterface.BUTTON_NEGATIVE }, new String[] {
					 * getString(R.string.yes), getString(R.string.no) }, new
					 * DialogInterface.OnClickListener[] { exitHandler, null });
					 */
			}
				return true;
			default:

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}
