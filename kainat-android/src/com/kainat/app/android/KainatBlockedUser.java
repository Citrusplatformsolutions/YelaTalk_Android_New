package com.kainat.app.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.ProfileViewActivity.BlockUserRequest;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.ConversationsViewHolder;
import com.kainat.app.android.bean.BlockedUser;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageLoader;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class KainatBlockedUser extends UIActivityManager implements OnClickListener, ThumbnailReponseHandler {
	private static final String TAG = KainatBlockedUser.class.getSimpleName();
	ArrayList<BlockedUser> blockuserArrList ;
	private CommunityMemberAdapter mMemberAdapter;
	private ImageLoader mImageLoader;
	ListView mainListBlockedUser;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.kainat_blocked_user);
		
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true)
				.showImageForEmptyUri(R.drawable.attachicon)
				.showImageOnFail(R.drawable.attachicon)
				.showImageOnLoading(R.drawable.attachicon).build();
		
		init();
	
		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
	
		
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Kainat Blocked User");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());	
		        
	}
	// MANOJ CODE Start
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		try{
			BlockUserListRequest BUTask = new BlockUserListRequest();
			BUTask.execute("Insert"); 
		}catch(Exception e){
			Log.v("blockUserButton Exception", ""+e);
		}
		super.onResume();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		mainListBlockedUser = (ListView)findViewById(R.id.mainListBlockedUser);
	//	mainListBlockedUser.setOnItemClickListener(onitemClickList);
		
		mainListBlockedUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				

			
			}
		});
		/*BlockUserListRequest BUTask = new BlockUserListRequest();
		BUTask.execute("Insert");*/
		
		mImageLoader = ImageLoader.getInstance(this);
		mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_RT);
	}
	
/*	OnItemClickListener onitemClickList =new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			Intent intent = new Intent(KainatBlockedUser.this, ProfileViewActivity.class);
			intent.putExtra("USERID",blockuserArrList.get(pos).userId);
			intent.putExtra("CallFrom",1);
			startActivity(intent);
		};
	};*/
	public class BlockUserListRequest extends AsyncTask<String, Integer, String> {
		ProgressDialog rTDialog;
		@Override
		protected void onPreExecute() {
			rTDialog = ProgressDialog.show(KainatBlockedUser.this, "", getString(R.string.loading_dot), true);
			rTDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServerList();
			/*if (strData != null  && (strData.trim().indexOf("1") != -1)) {
				return strData;
			}*/
			if(strData != null)
			{
				parseBlockedUser(strData);
				if(strData.contains("desc") ){
					return strData;
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!= null)
			{
				try{
					JSONObject  jobj = new JSONObject(result);
					if(result.contains("desc")){
						if(blockuserArrList!=null && blockuserArrList.size()>0){
							mainListBlockedUser.setVisibility(View.VISIBLE);
						mMemberAdapter = new CommunityMemberAdapter(KainatBlockedUser.this);
						mainListBlockedUser.setAdapter(mMemberAdapter);
						}else
						{
							mainListBlockedUser.setVisibility(View.GONE);
						}
					//	String desc = jobj.optString("desc").toString();
					//	Toast.makeText(KainatBlockedUser.this, desc,  Toast.LENGTH_LONG).show();
					}
				}catch(Exception e){

				}
			}
			rTDialog.dismiss();
		}
	}

	private String postDataOnServerList(){
		String strData = null;
		String url_a = null;
		//if(systemMessageAction){
		//url = msgId;
		//}
		//else{
		/*if(type.equals(DELETE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/delete";
			}else if(type.equals(SHARE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/post/share";
			}else if(type.equals(REPORT_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/report";
			}else if(type.equals(SPAM_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/notspam";
			}*/
		//}
		//http://www.yelatalkprod.com/tejas/feeds/user/block?blockeeUserId=15769184
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/getblockedusers";
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("iThumbFormat", new String("200x200".trim().getBytes("UTF-8"))));
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
	private static String readResponse(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = new byte[2048];
		int len = 0;
		while ((len = is.read(data, 0, data.length)) >= 0) {
			bos.write(data, 0, len);
		}
		is.close();
		return new String(bos.toByteArray(), "UTF-8");
	}
	
	// MANOJ CODE END
	public void notificationFromTransport(ResponseObject response) {
	}


	public void onBackPressed() {
		if(onBackPressedCheck())return;
		if (getIntent().getByteExtra("PAGE", (byte) 1) == 1) {
			finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
			return;
		}
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	@Override
	public void onError(int i) {
		// TODO Auto-generated method stub
		
	}

	
	public void parseBlockedUser(String strData){
		try {
			blockuserArrList = new ArrayList<BlockedUser>();
			JSONObject mainStr = new JSONObject(strData);
			if(mainStr.has("blockUsersFeed")){
			String blockUsersFeed = mainStr.getString("blockUsersFeed");
			JSONObject mainStrBUF = new JSONObject(blockUsersFeed);
			if(mainStrBUF.has("blockedUsers")) {
				JSONArray blockedUsersListArray = mainStrBUF.getJSONArray("blockedUsers");
				// looping through All entry
				//Vector<ReportedByUser> ReportedByUser = new Vector<ReportedByUser>();
				for (int k = 0; k < blockedUsersListArray.length(); k++) {
					BlockedUser RBU = new BlockedUser();
					JSONObject row_reportedByList = blockedUsersListArray.getJSONObject(k);
					if(row_reportedByList.has("userId"))
					RBU.userId = row_reportedByList.getString("userId");
					if(row_reportedByList.has("userName"))
					RBU.userName = row_reportedByList.getString("userName");
					if(row_reportedByList.has("name"))
					RBU.name = row_reportedByList.getString("name");
					if(row_reportedByList.has("thumbUrl"))
					RBU.thumbUrl = row_reportedByList.getString("thumbUrl");
					if(row_reportedByList.has("profileUrl"))
					RBU.profileUrl = row_reportedByList.getString("profileUrl");
					
					blockuserArrList.add(RBU);
					//ReportedByUser.add(RBU);
				}
			}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ADAPTER
	private class CommunityMemberAdapter extends BaseAdapter {
		private ArrayList<BlockedUser> feed;
		private Context context;

		public CommunityMemberAdapter(Context context) {
			this.context = context;
			this.feed = blockuserArrList;
		}

		/*public void setFeed(CommunityFeed feed) {
			if (this.feed == null) {
				this.feed = new ArrayList<BlockedUser>();
			}
			this.feed.copyTo(feed);
		}
		public void replaceFeed(CommunityFeed feed) {
			this.feed = null ;
			if (this.feed == null) 
			{
				this.feed = new CommunityFeed();
			}
			this.feed.copyTo(feed);
		}*/


		public int getCount() {
			if (feed == null || feed == null)
				return -1;
			return feed.size();
		}

		public BlockedUser getItem(int position) {
			if (position > -1 && null != feed && position < feed.size())
				return feed.get(position);
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View listItemView = null;
			try {
				final BlockedUser entry = (BlockedUser) getItem(position);
				if (convertView != null) {
					ProgressBar progressBar = ((ProgressBar) convertView.findViewById(R.id.progressbar));
					if (progressBar != null) {
						convertView = null;
					}
				}
				if (convertView == null) {
					listItemView = LayoutInflater.from(context).inflate(R.layout.blocked_memberlist_row, null);
				} else {
					listItemView = (View) convertView;
				}
				if (entry == null)
					return listItemView;

				final ImageView item_img = (ImageView) listItemView.findViewById(R.community_memberlist_row.memberImage_test);
				if(entry.thumbUrl != null && !entry.thumbUrl.equals(""))
					imageLoader.displayImage(entry.thumbUrl, item_img, options);
				Log.v("entry.thumbUrl", "manoj"+entry.thumbUrl);
				item_img.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(KainatBlockedUser.this, ProfileViewActivity.class);
						intent.putExtra("USERID",entry.userId);
						intent.putExtra("CallFrom",1);
						startActivity(intent);
					}
				});
				
				
				((TextView) listItemView.findViewById(R.community_memberlist_row.memberName)).setText(entry.name);
				final ImageView buttonImage = (ImageView) listItemView.findViewById(R.community_memberlist_row.user_type);
				final Button unblock_user = (Button) listItemView.findViewById(R.community_memberlist_row.unblock_user);
				
				unblock_user.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//							System.out.println("Delete User = "+entry.userName);
					/*	rtDialog = ProgressDialog.show(CommunityMemberActivity.this, "", getString(R.string.please_wait_while_loadin), true);
						removeFallower(comm_name, "", entry.userName);*/
						try{
							BlockUserRequest blockUser = new BlockUserRequest();
							blockUser.execute(entry.userId);
						}catch(Exception e){
							Log.v("blockUserButton Exception", ""+e);
						}
					}
				});
				
				item_img.setTag(entry.userName);

				

				return listItemView;
			} catch (Exception e) {
				return listItemView;
			}
		}
	}

	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	//Block user here 
	
	public class BlockUserRequest extends AsyncTask<String, Integer, String> {
		ProgressDialog rTDialog;
		@Override
		protected void onPreExecute() {
			rTDialog = ProgressDialog.show(KainatBlockedUser.this, "", getString(R.string.loading_dot), true);
			rTDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServer(params[0]);
			/*if (strData != null  && (strData.trim().indexOf("1") != -1)) {
				return strData;
			}*/
			if(strData != null)
			{
				if(strData.contains("desc") ){
					return strData;
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!= null)
			{
				try{
					JSONObject  jobj = new JSONObject(result);
					if(result.contains("desc")){
						if(result.contains("status")){
							String stst = jobj.optString("status").toString();
							if(stst.equals("1")){
								try{
									BlockUserListRequest BUTask = new BlockUserListRequest();
									BUTask.execute("Insert"); 
								}catch(Exception e){
									Log.v("blockUserButton Exception", ""+e);
								}
							}
						}
						String desc = jobj.optString("desc").toString();
						Toast.makeText(KainatBlockedUser.this, desc,  Toast.LENGTH_LONG).show();
					}
				}catch(Exception e){

				}
			}
			rTDialog.dismiss();
		}
	}

	private String postDataOnServer(String userId){
		String strData = null;
		String url_a = null;
		//if(systemMessageAction){
		//url = msgId;
		//}
		//else{
		/*if(type.equals(DELETE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/delete";
			}else if(type.equals(SHARE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/post/share";
			}else if(type.equals(REPORT_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/report";
			}else if(type.equals(SPAM_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/notspam";
			}*/
		//}
		//http://www.yelatalkprod.com/tejas/feeds/user/block?blockeeUserId=15769184
		/*if(!profileBean.isBlocked){
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/block";
		}else
		{*/
			url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/unblock";
		//}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("blockeeUserId", new String(userId.trim().getBytes("UTF-8"))));
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
}
