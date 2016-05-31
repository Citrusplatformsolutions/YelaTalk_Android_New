package com.kainat.app.android;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.example.kainat.util.AppUtil;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.ResponseObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class KainatDiscoverNewActivity  extends UICommunityManager{
	LinearLayout lay_feature_channels,lm_suggested,lm_all_channels;
	int suggested = 0;
	static String[] GRID_DATA= new String[] {"Popular",
		"Sports" ,
		"News", 
		"Entertainment" ,
		"Lifestyle",
	"Spirituality" };
	static String[] GRID_DATA_IMG = new String[] { "Popular",
		"Sports",
		"News",
		"Entertainment",
		"Lifestyle",
	"Spirituality" };
	public static final int[] GRID_DATA_IMG_INT = new int[] {
		R.drawable.popular,
		R.drawable.sports,
		R.drawable.news,
		R.drawable.entertainment,
		R.drawable.lifestyle,
		R.drawable.spirituality};
	ProgressDialog rTDialog;
	int channelType = 0;
	HorizontalListView listview,listview_two,listview_three;
	//	LinearLayout ll_one,ll_two;
	DisplayImageOptions optionss = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisk(true)
			.resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.imagedefault)
			.showImageOnFail(R.drawable.imagedefault)
			.showImageOnLoading(R.drawable.imagedefault)
			.build();
	/*.postProcessor(new BitmapProcessor() {
				@Override
				public Bitmap process(Bitmap bmp) {
					return Bitmap.createScaledBitmap(bmp, 300, 300, false);
				}
			}).build();*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_new_activity);
		init();
		if(feed_discover!=null && feed_discover.entry!=null){
			listview.setAdapter(mAdapter);
		}
		if(feed_suggested!=null && feed_suggested.entry!=null){
			listview_two.setAdapter(mSuggestionAdapter);
		}
	}
	private void init() {

		Button btn_create_channel = (Button)findViewById(R.id.btn_create_channel);
		btn_create_channel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(KainatDiscoverNewActivity.this,
						CreateCommunityActivity.class);
				startActivity(intent);

			}
		});

		//		ll_one  = (LinearLayout)findViewById(R.id.ll_one);
		//		ll_two = (LinearLayout)findViewById(R.id.ll_two);
		// TODO Auto-generated method stub
		lay_feature_channels = (LinearLayout) findViewById(R.id.mainlayout_outer_feature_channels);
		//	createCommunityView(null,lay_feature_channels);
		lm_suggested = (LinearLayout) findViewById(R.id.mainlayout_outer_sugested);
		//	createCommunityView(null,lm_suggested);
		lm_all_channels = (LinearLayout) findViewById(R.id.mainlayout_outer_txt_all_channels);
		createCategoryView(GRID_DATA,GRID_DATA_IMG,lm_all_channels);

		//"http://54.172.48.159/tejas/feeds/api/group/searched/advance?category=
		//searchTask.execute("http://yelatalkprod.com/tejas/feeds/api/group/searched/advance?") ;
		//+ "/tejas/feeds/api/group/searched/advance?category=" + UIActivityManager.selectedCategoryName
		// NEW 
		listview = (HorizontalListView) findViewById(R.id.listview);
		/*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	   @Override
	   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	     // Object listItem = list.getItemAtPosition(position);
		   CommunityFeed.Entry entry = feed.entry.get(position);
			DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",entry);

			Intent intent = new Intent(KainatDiscoverNewActivity.this, CommunityChatActivity.class);
			intent.putExtra("message_url", feed.entry.get(position).messageUrl);
			intent.putExtra("tags_name", feed.entry.get(position).tags);
			intent.putExtra("group_name", feed.entry.get(position).groupName);
			intent.putExtra("group_desc", feed.entry.get(position).description);
			intent.putExtra("group_pic", feed.entry.get(position).thumbUrl);
			startActivity(intent);
	   } 
	});*/
		
	/*	listview.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
});*/
		listview_two= (HorizontalListView) findViewById(R.id.listview_two);
		listview_three = (HorizontalListView) findViewById(R.id.listview_three);
		listview_three.setAdapter(mAllCategoryAdapter);

		// END
	}
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}
	/*	public void createCommunityView(CommunityFeed arrStr , final LinearLayout parentLinearlayout){

		int length = arrStr.entry.size();
		//int length = 50;
		if(parentLinearlayout != null)
		parentLinearlayout.removeAllViews();
		for(int j=0;j<length;j++) 
		{   
			final View view = getLayoutInflater() .inflate(R.layout.community_grid_new_row, null);
			TextView community = (TextView) view .findViewById(R.id.txt_community_name);
			ImageView img_community_background = (ImageView)view.findViewById(R.id.img_community_background);
			String uri = "@drawable/com_channel_bg";  // where myresource.png is the file
			int imageResource = getResources().getIdentifier(uri, null, getPackageName());
			img_community_background.setImageResource(imageResource);
			imageLoader.displayImage(arrStr.entry.get(j).thumbUrl,img_community_background,options);
			view.setId(j+1);
			community.setText(arrStr.entry.get(j).groupName);
			final int index = j;
			// Set click listener for button
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.i("TAG", "index :" + index);
					Toast.makeText(getApplicationContext(), 
							"Clicked Button Index :" + index, 
							Toast.LENGTH_LONG).show();

				}
			});
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					parentLinearlayout.addView(view);  
				}
			});		
		}

	}
	 */

	//All Category Making
	public void createCategoryView(final String[] arrStr_text ,String[] arrStr_img, LinearLayout parentLinearlayout){

		int length = arrStr_text.length;
		//int length = 50;
		parentLinearlayout.removeAllViews();
		for(int j=0;j<length;j++) 
		{   
			View view = null;
			view = getLayoutInflater() .inflate(R.layout.community_grid_new_row, null);
			final TextView community = (TextView) view .findViewById(R.id.txt_community_name);
			final ImageView img_community_background = (ImageView)view.findViewById(R.id.img_community_background);
			TextView txt_membercount = (TextView) view.findViewById(R.id.txt_membercount);
			TextView txt_postcount = (TextView) view.findViewById(R.id.txt_messagecount);
			LinearLayout  layout_post_btn =(LinearLayout)view.findViewById(R.id.layout_post_btn);
			LinearLayout ll_iconfollow = (LinearLayout)view.findViewById(R.id.ll_iconfollow);
			ll_iconfollow.setVisibility(View.VISIBLE);
			layout_post_btn.setVisibility(view.GONE);
			txt_membercount.setVisibility(view.GONE);
			txt_postcount.setVisibility(view.GONE);
			/*String uri = "@drawable/lovesong";  // where myresource.png is the file
			int imageResource = getResources().getIdentifier(uri, null, getPackageName());
			img_community_background.setImageResource(imageResource);*/
			int res 	  = this.getResources().getIdentifier(arrStr_img[j].toLowerCase(), "drawable", this.getPackageName());
			img_community_background.setImageResource(res);
			view.setId(j+1);
			view.setTag(arrStr_text[j]);
			community.setText(arrStr_text[j]);
			final int index = j;
			// Set click listener for button
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					Intent intent = new Intent(KainatDiscoverNewActivity.this,SearchCommunityActivity.class);
					intent.putExtra("TYPE", 1);
					intent.putExtra("CommunityType", v.getTag().toString());
					intent.putExtra("UrlIs","");
					startActivity(intent);

				}
			});

			parentLinearlayout.addView(view);  
		}

	}
	//

	private class SearchTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				if(feed_discover==null || feed_discover.entry==null)
				showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String url = urls[0];
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				String responseString = "" ;
				HttpClient httpclientE = new DefaultHttpClient();

				if(urls[0].indexOf("?")!=-1)
					urls[0] = urls[0] + "&"+BusinessProxy.thumbInfo;
				else
					urls[0] = urls[0] + "?"+BusinessProxy.thumbInfo;

				urls[0] = urls[0] + "&textMode=base64&limit=50";

				HttpPost httppostw = new HttpPost( urls[0]);
				try {
					/*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					String key = "category";
					String value ="News";
					nameValuePairs.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"))));*/
					//	httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
					httppostw.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
					HttpResponse response = httpclientE.execute(httppostw);
					HttpEntity r_entity = response.getEntity();


					responseString = EntityUtils.toString(r_entity);
					//					System.out.println(responseString);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				UICommunityManager.mResponseDataDISCOVER = responseString.getBytes();
				return responseString;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatDiscoverNewActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			try{
			if (rTDialog!=null && rTDialog.isShowing()) {
				rTDialog.dismiss();
				//rTDialog = null;
			}

			if(mCallBackTimer!=null)
				mCallBackTimer.cancel();
			mCallBackTimer = new Timer();
			if(response!=null){
				channelType = 0;
				mCallBackTimer.schedule(new OtsSchedularTask(KainatDiscoverNewActivity.this, DATA_CALLBACK, (byte)0), 0);
			}
			else 
			{
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(KainatDiscoverNewActivity.this, ERROR_CALLBACK, (byte)0), 0);
			}
			SearchSuggestedTask searchTaskt = new SearchSuggestedTask() ;
			if(KeyValue.getString(KainatDiscoverNewActivity.this, KeyValue.INTEREST)!=null)
			{
				String str = KeyValue.getString(KainatDiscoverNewActivity.this, KeyValue.INTEREST);
				if (str.endsWith(",")) {
					str = str.substring(0, str.length() - 1);
					}
				searchTaskt.execute("http://54.172.48.159/tejas/feeds/api/group/searched/advance?category="+str);
			}
			else{
				searchTaskt.execute("http://54.172.48.159/tejas/feeds/api/group/searched/advance?category=Sports");
			}
			}
			catch(Exception e){
				Log.v("Exception",""+e);
			}
			super.onPostExecute(response);
		}
	}

	//Suggested
	private class SearchSuggestedTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			if(feed_suggested==null || feed_suggested.entry==null)
			showLoadingDialog();
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String url = urls[0];
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);

				String responseString = "" ;
				HttpClient httpclientE = new DefaultHttpClient();

				if(urls[0].indexOf("?")!=-1)
					urls[0] = urls[0] + "&"+ BusinessProxy.thumbInfo;
				else
					urls[0] = urls[0] + "?"+BusinessProxy.thumbInfo;

				urls[0] = urls[0] + "&textMode=base64&limit=50";

				HttpPost httppostw = new HttpPost( urls[0]);
				try {
					/*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					String key = "category";
					String value ="News";
					nameValuePairs.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"))));*/
					//	httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
					httppostw.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
					HttpResponse response = httpclientE.execute(httppostw);
					HttpEntity r_entity = response.getEntity();
					responseString = EntityUtils.toString(r_entity);
					//					System.out.println(responseString);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				UICommunityManager.mResponseDataSuggested = responseString.getBytes();
				return responseString;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatDiscoverNewActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {

			try{
				if (rTDialog!=null && rTDialog.isShowing()) {
					rTDialog.dismiss();
					//rTDialog = null;
				}
				if(mCallBackTimer!=null)
					mCallBackTimer.cancel();
				mCallBackTimer = new Timer();
				if(response!=null){
					channelType = 0;
					mCallBackTimer.schedule(new OtsSchedularTask(KainatDiscoverNewActivity.this, DATA_CALLBACK, (byte)0), 0);
				}
				else 
				{
					mCallBackTimer = new Timer();
					mCallBackTimer.schedule(new OtsSchedularTask(KainatDiscoverNewActivity.this, ERROR_CALLBACK, (byte)0), 0);
				}
				if (rTDialog!=null && rTDialog.isShowing()) {
					rTDialog.dismiss();
					//rTDialog = null;
				}
			}catch(Exception e){

			}

			super.onPostExecute(response);
		}
	}

	//
	protected void showLoadingDialog() {
		//		showAnimationDialog("", getString(R.string.please_wait_while_loadin), true,
		//				PROGRESS_CANCEL_HANDLER);
//if(rTDialog!=null && !rTDialog.isShowing())
		rTDialog = ProgressDialog.show(KainatDiscoverNewActivity.this, "", getString(R.string.loading_dot), true);
	}
	@Override
	public void onTaskCallback(Object parameter, byte mRequestObjNo) {

		if (lodingCanceled)
			return;

		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case HTTP_TIMEOUT:
			break;
		case DATA_CALLBACK:
			if (null != UICommunityManager.mResponseDataDISCOVER) {
				parseXMLData_discover(null);
				 /* runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				        	mAdapter.notifyDataSetChanged();
				        }
				    });*/
				
				
			} else if(null != UICommunityManager.mResponseDataSuggested){
				parseXMLData_suggested(null);
				/* runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				        	mSuggestionAdapter.notifyDataSetChanged();
				        }
				    });*/
				
			}
			else{
				showSimpleAlert("Error:", "Unable to retrieve\n" + null);
			}
			break;
		case ERROR_CALLBACK:
			//showSimpleAlert("Error", getString(R.string.unable_to_connect_pls_try_later));
			break;
		}
		/*	if(rTDialog != null)
			rTDialog.dismiss();*/
		super.cancelThread();

	}
	private void parseXMLData_discover(final String aTitle) {
		parseXMLData_discover();

		if(feed_discover !=null && feed_discover.entry !=null){
			//createCommunityView(feed,lay_feature_channels);
			//	createCommunityView(feed,lm_suggested);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					//listview.setAdapter(mAdapter);
					listview.setAdapter(mAdapter);
				}
			});


		}
	}

	private void parseXMLData_suggested(final String aTitle) {
		parseXMLData_suggested();

		if(feed_suggested !=null && feed_suggested.entry !=null){
			//createCommunityView(feed,lay_feature_channels);
			//	createCommunityView(feed,lm_suggested);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					//listview.setAdapter(mAdapter);
					listview_two.invalidate();
					mSuggestionAdapter.notifyDataSetChanged();
					listview_two.setAdapter(mSuggestionAdapter);
				}
			});
			//listview_two.setAdapter(mSuggestionAdapter);

		}
	}
	private void parseXMLData(final String aTitle) {
		parseXMLData();

		if(feed !=null && feed.entry !=null){
			
			//createCommunityView(feed,lay_feature_channels);
			//	createCommunityView(feed,lm_suggested);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					listview.setAdapter(mAdapter);
					//listview_two.setAdapter(mSuggestionAdapter);
				}
			});


		}


	}
	//
	// Date 04-01-2015
	// MAnoj Singh

	private BaseAdapter mAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			@Override
			public void onClick(View v) {

				String int_val = (String)v.getTag();
				int pos = Integer.parseInt(int_val);

				CommunityFeed.Entry entry = feed_discover.entry.get(pos);
				DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",entry);
				Intent intent = new Intent(KainatDiscoverNewActivity.this, CommunityChatActivity.class);
				intent.putExtra("message_url", feed_discover.entry.get(pos).messageUrl);
				intent.putExtra("tags_name", feed_discover.entry.get(pos).tags);
				intent.putExtra("group_name", feed_discover.entry.get(pos).groupName);
				intent.putExtra("group_desc", feed_discover.entry.get(pos).description);
				intent.putExtra("group_pic", feed_discover.entry.get(pos).thumbUrl);
				startActivity(intent);
			}
		};

		@Override
		public int getCount() {
			return feed_discover.entry.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval 			 = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_grid_new_row, null);
			TextView title 			 = (TextView) retval.findViewById(R.id.txt_community_name);
			TextView txt_membercount = (TextView) retval.findViewById(R.id.txt_membercount);
			TextView txt_postcount   = (TextView) retval.findViewById(R.id.txt_messagecount);
			Button btn_feature 		 = (Button)   retval.findViewById(R.id.btn_feature);
			ImageView img_community_background = (ImageView)retval.findViewById(R.id.img_community_background);
			img_community_background.setTag(""+position);
			btn_feature.setVisibility(View.GONE);
			LinearLayout ll_iconfollow = (LinearLayout)retval.findViewById(R.id.ll_iconfollow);
			ll_iconfollow.setVisibility(View.VISIBLE);
			ImageView img_follow = (ImageView)retval.findViewById(R.id.img_follow);
			if(feed_discover.entry.get(position).joinOrLeave.equalsIgnoreCase("join")){
				img_follow.setBackgroundResource(R.drawable.iconfollow);
			}else{
				img_follow.setBackgroundResource(R.drawable.iconfollowing);

			}
			img_follow.setTag(position);
			img_follow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					joinLeaveCommunity(1,(Integer)v.getTag(),feed_discover);
				}
			});

			/*  Button button = (Button) retval.findViewById(R.id.clickbutton);
	            button.setOnClickListener(mOnButtonClicked);*/
			img_community_background.setOnClickListener(mOnButtonClicked);
			title.setText(feed_discover.entry.get(position).groupName);
			retval.setTag(""+position);
			txt_membercount.setText(""+feed_discover.entry.get(position).noOfMember);
			txt_postcount.setText(""+feed_discover.entry.get(position).noOfMessage);

			img_community_background.setId(3000+position);
			imageLoader.displayImage(feed_discover.entry.get(position).thumbUrl,img_community_background,optionss);

			return retval;
		}

	};

	BaseAdapter mSuggestionAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			@Override
			public void onClick(View v) {
				String int_val = (String)v.getTag();
				int pos = Integer.parseInt(int_val);

				CommunityFeed.Entry entry = feed_suggested.entry.get(pos);
				DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",entry);

				Intent intent = new Intent(KainatDiscoverNewActivity.this, CommunityChatActivity.class);
				intent.putExtra("message_url", feed_suggested.entry.get(pos).messageUrl);
				intent.putExtra("tags_name", feed_suggested.entry.get(pos).tags);
				intent.putExtra("group_name", feed_suggested.entry.get(pos).groupName);
				intent.putExtra("group_desc", feed_suggested.entry.get(pos).description);
				intent.putExtra("group_pic", feed_suggested.entry.get(pos).thumbUrl);
				startActivity(intent);


			}
		};

		@Override
		public int getCount() {
			if(feed_suggested!=null){
			return feed_suggested.entry.size();
			}else
				return -1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval 			 = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_grid_new_row, null);
			TextView title 			 = (TextView) retval.findViewById(R.id.txt_community_name);
			TextView txt_membercount = (TextView) retval.findViewById(R.id.txt_membercount);
			TextView txt_postcount   = (TextView) retval.findViewById(R.id.txt_messagecount);
			LinearLayout ll_iconfollow = (LinearLayout)retval.findViewById(R.id.ll_iconfollow);
			ImageView img_follow = (ImageView)retval.findViewById(R.id.img_follow);
			ll_iconfollow.setVisibility(View.VISIBLE);
			if(feed_suggested.entry.get(position).joinOrLeave.equalsIgnoreCase("join")){
				img_follow.setBackgroundResource(R.drawable.iconfollow);
			}else{
				img_follow.setBackgroundResource(R.drawable.iconfollowing);
			}
			img_follow.setTag(position);
			img_follow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					joinLeaveCommunity(2,(Integer)v.getTag(),feed_suggested);
				}
			});
			Button btn_feature = (Button)retval.findViewById(R.id.btn_feature);
			btn_feature.setVisibility(View.GONE);

			/*  Button button = (Button) retval.findViewById(R.id.clickbutton);
	            button.setOnClickListener(mOnButtonClicked);*/
			retval.setTag(""+position);
			retval.setOnClickListener(mOnButtonClicked);
			title.setText(feed_suggested.entry.get(position).groupName);
			txt_membercount.setText(""+feed_suggested.entry.get(position).noOfMember);
			txt_postcount.setText(""+feed_suggested.entry.get(position).noOfMessage);
			ImageView img_community_background = (ImageView)retval.findViewById(R.id.img_community_background);
			img_community_background.setId(5000+position);
			imageLoader.displayImage(feed_suggested.entry.get(position).thumbUrl,img_community_background,optionss);
			return retval;
		}

	};

	// End 

	// This is for All Category

	private BaseAdapter mAllCategoryAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KainatDiscoverNewActivity.this,SearchCommunityActivity.class);
				intent.putExtra("TYPE", 1);
				intent.putExtra("CommunityType", v.getTag().toString());
				intent.putExtra("UrlIs","");
				startActivity(intent);

			}
		};

		@Override
		public int getCount() {
			return GRID_DATA.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_grid_new_row, null);
			TextView title = (TextView) retval.findViewById(R.id.txt_community_name);
			TextView txt_membercount = (TextView) retval.findViewById(R.id.txt_membercount);
			TextView txt_postcount = (TextView) retval.findViewById(R.id.txt_messagecount);
			LinearLayout  layout_post_btn =(LinearLayout)retval.findViewById(R.id.layout_post_btn);
			LinearLayout ll_iconfollow = (LinearLayout)retval.findViewById(R.id.ll_iconfollow);
			ll_iconfollow.setVisibility(View.GONE);
			Button btn_feature = (Button)retval.findViewById(R.id.btn_feature);
			btn_feature.setVisibility(View.GONE);

			layout_post_btn.setVisibility(retval.GONE);
			txt_membercount.setVisibility(retval.GONE);
			txt_postcount.setVisibility(retval.GONE);
			/*  Button button = (Button) retval.findViewById(R.id.clickbutton);
	            button.setOnClickListener(mOnButtonClicked);*/
			retval.setOnClickListener(mOnButtonClicked);
			title.setText(GRID_DATA[position]);
			retval.setTag(GRID_DATA[position]);
			/*  txt_membercount.setText(""+feed.entry.get(position).noOfMember);
	            txt_postcount.setText(""+feed.entry.get(position).noOfMessage);*/
			ImageView img_community_background = (ImageView)retval.findViewById(R.id.img_community_background);
			img_community_background.setId(6000+position);
			imageLoader.displayImage("drawable://"+GRID_DATA_IMG_INT[position],img_community_background,optionss);
			return retval;
		}

	};
	// FOLOW and unfollow

	public void joinLeaveCommunity(int from,int position, CommunityFeed f1){
		StringBuilder buffer = new StringBuilder(
				"msgto:Community Manager<a:communitymgr>?text="+f1.entry.get(position).joinOrLeave+"::Name##"+f1.entry.get(position).groupName);
		clickHandler(buffer.toString(),false);

		if(f1.entry.get(position).joinOrLeave.equalsIgnoreCase("join"))

			f1.entry.get(position).joinOrLeave = "leave" ;

		else

			f1.entry.get(position).joinOrLeave = "join" ;
		if(from == 1){
			mAdapter.notifyDataSetChanged();
		}else
		{
			mSuggestionAdapter.notifyDataSetChanged();
		}

	}
	public void theMethodYouWantToCall(){
        // do what ever you want here
		SearchTask searchTask = new SearchTask() ;
		searchTask.execute("http://54.172.48.159/tejas/feeds/api/group/attributed/featured?");
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UICommunityManager.mResponseDataDISCOVER != null && null != UICommunityManager.mResponseDataSuggested){
			parseXMLData_discover(null);
			parseXMLData_suggested(null);
		}else{
			SearchTask searchTask = new SearchTask() ;
			searchTask.execute("http://54.172.48.159/tejas/feeds/api/group/attributed/featured?");
		}

	}
}
