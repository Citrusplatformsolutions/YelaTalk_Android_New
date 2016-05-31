package com.kainat.app.android;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.CommunityFeedParser;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ImageLoader;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.rockerhieu.emojicon.EmojiconTextView;

public class CommunityMemberActivity extends UIActivityManager implements Runnable, OnClickListener, OnSchedularListener, ThumbnailReponseHandler, OnScrollListener, TextWatcher {
	private static final String TAG = CommunityMemberActivity.class.getSimpleName();
	private static final byte SUB_SCREEN_MAIN = 1;
	// User media icons
	private static final short SEARCH_INIT_DELAY = 500;
	private static final byte TASK_SEARCH = 10;
	public static final int LOADING_ID = -1000;
	private CommunityMemberAdapter mMemberAdapter;
	private ListView mMainList;
	private byte mCurrentScreen = SUB_SCREEN_MAIN;
	private Timer mSearchTimer;
	private ImageLoader mImageLoader;
	private int iLoggedUserID;
	private Thread mThread;
	private String mPostURL;
	private boolean mIsRunning = true;
	private byte[] mResponseData;
	private HttpConnectionHelper helper = null;
	private int responseCode;
	private Timer mCallBackTimer;
	private static final int CHUNK_LENGTH = 1024;
	private static final byte DATA_CALLBACK = 1;
	private static final byte ERROR_CALLBACK = 2;
	private static final byte HTTP_TIMEOUT = 3;
	private View loading = null;
	private CommunityFeed feed;
	private CommunityFeed oldFeed;
	private boolean requestednextPage = false;
	private TextView title;
	String comm_name;
	private ImageView add_member_community;
	private String url;
	private ProgressDialog rtDialog;
	private static final int MEMBER_COUNT=100;
	DisplayImageOptions options;
	
	ImageView clean_search_iv_member;
	EditText communitymemeberserch;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		init();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true)
				.showImageForEmptyUri(R.drawable.attachicon)
				.showImageOnFail(R.drawable.attachicon)
				.showImageOnLoading(R.drawable.attachicon).build();
		
		// Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication())
						.getTracker(TrackerName.APP_TRACKER);
				t.setScreenName("Community Member Screen");
				t.set("&uid",""+BusinessProxy.sSelf.getUserId());
				t.send(new HitBuilders.AppViewBuilder().build());	
	}

	private void init(){
		
		DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, false);
		iLoggedUserID = BusinessProxy.sSelf.getUserId();
		mThread = new Thread(this);
		mThread.start();
		comm_name = getIntent().getStringExtra("title");

		//setContentView(R.layout.community_view_member);
		screenSlide(this, R.layout.community_view_member);
		// DATE : 10-12-2015
		// NAME : MANOJ SINGH
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
		
		
		
		EditText editor = (EditText) findViewById(R.community_member.searchBox);
		editor.addTextChangedListener(CommunityMemberActivity.this);
		mMainList = (ListView) findViewById(R.community_member.mainList);
		title     = (EmojiconTextView)findViewById(R.id.title);
		add_member_community = (ImageView)findViewById(R.id.add_member_community);
		if((getIntent().getStringExtra("OwnerUserName")!=null))
		{
			if((getIntent().getStringExtra("OwnerUserName")).equalsIgnoreCase(SettingData.sSelf.getUserName()) || UIActivityManager.directFromCreateChannel)
			{
				add_member_community.setVisibility(View.VISIBLE);
			}else
			{
				add_member_community.setVisibility(View.GONE);
			}
		}
		else
		{
			add_member_community.setVisibility(View.GONE);
		}
		add_member_community.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				DataModel.sSelf.storeObject("CommunityMember", getMemberItem());
				Intent intent = new Intent(CommunityMemberActivity.this,
						KainatCommunityFallowerContact.class);
				intent.putExtra("group", true);
				intent.putExtra("GROUP_NAME", comm_name);
				//intent.putExtra("SEL_MEMBER_LIST", getMemberItem());
				startActivity(intent);
				finish();
			}
		});
		mMainList.setOnScrollListener(this);
		mMemberAdapter = new CommunityMemberAdapter(this);
		mImageLoader = ImageLoader.getInstance(this);
		mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_RT);

		url = getIntent().getStringExtra("member_url");
		if (url.lastIndexOf("\"") != -1)
			url = url.substring(0, url.lastIndexOf("\"")) ;

		url = url.replaceAll("%22", "");
		if(comm_name==null||comm_name.length()<=0)
			comm_name = "Community Member" ;
		((TextView) findViewById(R.id.title)).setText(comm_name);
		((TextView) findViewById(R.id.prev_communites)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		executeRequestData(url);
	}
	
	private void localFilter(String val) {
		String nexturl = feed.nexturl;
		if(val.equals(""))
		{
			mMemberAdapter.setNewItemList(feed.entry);
		}else
		{
			// TODO Auto-generated method stub
			final Vector<Entry> searchList = new Vector<CommunityFeed.Entry>();
			for (Entry bud : feed.entry) {
				if (bud.firstName.trim().toLowerCase().contains(val.toLowerCase().toString())) {
					searchList.add(bud);
				}
			}
			
			mMemberAdapter.setNewItemList(searchList);
		/*	new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startThumbnailThread(10);
				}
			}).start();
			*/
		
		}
		
	}
	
	
	private ArrayList<Entry> getMemberItem(){
		ArrayList<Entry> entryList = new ArrayList<CommunityFeed.Entry>();
		if(mMemberAdapter != null){
			for(int i = 0; i < mMemberAdapter.getCount(); i++){
				entryList.add(mMemberAdapter.getItem(i));
			}
		}
		return entryList;
	}
	protected void onResume() {
		super.onResume();
		if (DataModel.sSelf.getRemoveBoolean(DMKeys.NEW_INTENT) == true){
			init() ;
		}
		LinearLayout layout = (LinearLayout) findViewById(R.id.dummy_latout);
		if (layout != null) {
			layout.requestFocus();
		}
	}

	private void executeRequestData(String aURL) {
		if (aURL == null || aURL.trim().length() <= 0)
			return;
		mPostURL = aURL;
		mIsRunning = true;
		mResponseData = null;
		rtDialog = ProgressDialog.show(CommunityMemberActivity.this, "", getString(R.string.please_wait_while_loadin), true);
		synchronized (this) {
			notify();
		}
	}

	class CProgressDialog extends ProgressDialog {
		public CProgressDialog(Context context) {
			super(context);
		}

		public void onBackPressed() {
			return;
		}
	}

	/*protected void showLoggingDialog(String aString) {
		mProgressDialog = new CProgressDialog(this);
		mProgressDialog.setMessage("Please wait while loading " + aString + "..");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}*/

	public void run() {
		while (mIsRunning) {
			try {
				synchronized (this) {
					wait();
				}
				if (null != mPostURL && 0 < mPostURL.trim().length()) {
					mPostURL = mPostURL + "&limit="+MEMBER_COUNT;
//					Log.i(TAG, "mPostURL : "+mPostURL);
					helper = new HttpConnectionHelper(mPostURL);
					helper.setHeader("RT-APP-KEY", "" + iLoggedUserID);
					helper.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
					mCallBackTimer = new Timer();
					responseCode = helper.getResponseCode();
					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
						String contentEncoding = helper.getHttpHeader("Content-Encoding");
						InputStream inputStream = null;
						if (null == contentEncoding) {
							inputStream = helper.getInputStream();
						} else if (contentEncoding.equals("gzip")) {
							inputStream = new GZIPInputStream(helper.getInputStream());
						}
						ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
						byte[] chunk = new byte[CHUNK_LENGTH];
						int len;
						while (-1 != (len = inputStream.read(chunk))) {
							buffer.append(chunk, 0, len);
						}
						this.mResponseData = buffer.toByteArray();
//						String resData = new String(mResponseData);
//						Log.i(TAG, "Response : "+new String(mResponseData));
//						System.out.println("Member = "+resData);
						break;
					default:
						if (lDialogProgress != null && lDialogProgress.isShowing()) {
							lDialogProgress.dismiss();
						}
						break;
					}
					mCallBackTimer.cancel();
					mCallBackTimer = new Timer();
					mCallBackTimer.schedule(new OtsSchedularTask(this, DATA_CALLBACK, (byte)0), 0);
					
				}
			} catch (Exception ex) {
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(this, ERROR_CALLBACK, (byte)0), 0);
			}
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			int maxCount = view.getLastVisiblePosition() - view.getFirstVisiblePosition() + 1;
			startThumbnailThread(maxCount);
		}
	}

	private void startThumbnailThread(int maxCount) {
		try {
			if (maxCount > mMemberAdapter.getCount())
				maxCount = mMemberAdapter.getCount();
			int start = mMainList.getFirstVisiblePosition();
			String[] names = new String[maxCount];
			int[] indexs = new int[maxCount];
			Entry entry = null;
			for (int i = 0; i < maxCount; ++i) {
				entry = feed.entry.elementAt(start + i);
				if (null != entry) {
					names[i] = entry.userName;
					indexs[i] = start + i;
				}
			}
			mImageLoader.cleanAndPutRequest(names, indexs);
		} catch (Exception e) {
			//			e.printStackTrace();
		}
	}


	public void onTaskCallback(Object parameter, byte req) {
		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case TASK_SEARCH:
			if(mMemberAdapter == null)
				return ;
			final Vector<Entry> searchList = new Vector<CommunityFeed.Entry>();
			String text = ((EditText) findViewById(R.community_member.searchBox)).getText().toString().trim().toUpperCase();
			if ("".equals(text)) {
				if(mMemberAdapter != null)
					mMemberAdapter.setNewItemList(feed.entry);
				return;
			}
			if (feed == null) {
				return;
			}
			for (Entry bud : feed.entry) {
				if (bud.userName.trim().toUpperCase().startsWith(text)) {
					searchList.add(bud);
				}
			}
			mMemberAdapter.setNewItemList(searchList);
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startThumbnailThread(10);
				}
			}).start();
			break;
		case HTTP_TIMEOUT:
			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
			}
			helper.close();
			showSimpleAlert("Error", "Unable to retrieve\n" + mCurrentScreen);
			break;
		case DATA_CALLBACK:
			if (responseCode == -1) {
				mIsRunning = true;
				return;
			}
			mIsRunning = false;
			if (null != mResponseData) {
				parseXMLData();
				if (lDialogProgress != null && lDialogProgress.isShowing()) {
					lDialogProgress.dismiss();
				}
				//mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);//buy n
				resetLoading();
			} else {
				if (lDialogProgress != null && lDialogProgress.isShowing()) {
					lDialogProgress.dismiss();
				}
				feed = oldFeed;
				resetLoading();
				showSimpleAlert("Error:", "Unable to retrieve\n" + mCurrentScreen);
				
			}
			//communitymemeberserch.setText("");
			break;
		case ERROR_CALLBACK:
			mIsRunning = false;
			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
			}
			resetLoading();
			//showSimpleAlert("Error", getString(R.string.unable_to_connect_pls_try_later));
			break;
		}
		if(rtDialog != null && rtDialog.isShowing())
			rtDialog.dismiss();
	}

	public void resetLoading() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (loading != null) {
					ProgressBar progressBar = ((ProgressBar) loading.findViewById(R.id.progressbar));
					progressBar.setVisibility(View.GONE);
					TextView textView = ((TextView) loading.findViewById(R.id.loadingtext));
					textView.setTextColor(R.color.heading);
					textView.setText(getString(R.string.load_more));
//					textView.setText("Load More");
				}
			}
		});
	}

	private void parseXMLData() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			CommunityFeedParser myXMLHandler = new CommunityFeedParser();
			xr.setContentHandler(myXMLHandler);
			String xml = new String(mResponseData);
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			//			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(mResponseData);

			//			System.out.println(xml);
			mResponseData = null;
			xr.parse(new InputSource(arrayInputStream));
			if (feed != null) {
				for (int i = 0; i < myXMLHandler.feed.entry.size(); i++) {
					feed.entry.add(myXMLHandler.feed.entry.elementAt(i));
				}
				feed.nexturl = myXMLHandler.feed.nexturl;
				feed.link = myXMLHandler.feed.link;
				feed.links = myXMLHandler.feed.links;
			} else
				feed = myXMLHandler.feed;
			//			if(myXMLHandler.feed!=null)
			//				myScreenName(myXMLHandler.feed);
			//			BusinessProxy.sSelf.recordScreenStatistics(CommunityFeed.getAttrCode(feed.tracking), true, true);//Add Record
			requestednextPage = false;
			if (feed.entry.size() <= 0) {
				runOnUiThread(new Runnable() {
					public void run() {
						((TextView) findViewById(R.community_member.noContent)).setVisibility(View.VISIBLE);
					}
				});
				return;
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						((TextView) findViewById(R.community_member.noContent)).setVisibility(View.GONE);
					}
				});
			}
			
			//Sorting
			Collections.sort(feed.entry, new Comparator<Entry>() {
			        @Override
			        public int compare(Entry  entry1, Entry  entry2)
			        {
			            return  entry1.firstName.compareTo(entry2.firstName);
			        }
			    });
			
			
			
			runOnUiThread(new Runnable() {
				public void run() {
					hideShowNoMessageView();
					int lastPos = mMainList.getLastVisiblePosition() - 1;
					mMemberAdapter.replaceFeed(feed);
					if(mMainList.getCount()<=0)
						mMainList.setAdapter(mMemberAdapter);
					else
						mMemberAdapter.notifyDataSetChanged() ;

					mMainList.setSelection(lastPos);
					if (null != feed && null != feed.entry) {
						if (feed.entry.size() > 10) {
							startThumbnailThread(10);
						} else {
							startThumbnailThread(feed.entry.size());
						}
					}
				}
			});
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (null != feed && null != feed.entry) {
						if (feed.entry.size() > 10) {
							startThumbnailThread(10);
						} else {
							startThumbnailThread(feed.entry.size());
						}
					}
				}
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error(TAG, "parseXMLData - " + ex.getMessage(), ex);
			}
			resetLoading();
		}
	}

	public void hideShowNoMessageView() {
		if (feed != null && feed.entry != null && feed.entry.size() <= 0) {
			((TextView) findViewById(R.community_member.noContent)).setVisibility(View.VISIBLE);
			((ListView) findViewById(R.community_member.mainList)).setVisibility(View.INVISIBLE);
		} else {
			((TextView) findViewById(R.community_member.noContent)).setVisibility(View.GONE);
			((ListView) findViewById(R.community_member.mainList)).setVisibility(View.VISIBLE);
		}
	}
	ImageDownloader imageManager = new ImageDownloader();
	ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

		@Override
		public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
			try {

			} catch (Exception e) {
			}
		}
	};	
	private class CommunityMemberAdapter extends BaseAdapter {
		private CommunityFeed feed;
		private Context context;

		public CommunityMemberAdapter(Context context) {
			this.context = context;
		}

		public void setFeed(CommunityFeed feed) {
			if (this.feed == null) {
				this.feed = new CommunityFeed();
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
		}

		public void setNewItemList(Vector<Entry> itemList) {
			this.feed.entry.clear();
			this.feed.entry.addAll(itemList);
			runOnUiThread(new Runnable() {
				public void run() {
					notifyDataSetChanged();
					notifyDataSetInvalidated();
				}
			});
		}

		public int getCount() {
			if (feed == null || feed.entry == null)
				return -1;
			if(feed.nexturl != "" || !feed.nexturl.equals("")){
				return feed.entry.size()+1;
			}
			return feed.entry.size();
		}

		public Entry getItem(int position) {
			if (position > -1 && null != feed && position < feed.entry.size())
				return feed.entry.get(position);
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View listItemView = null;
			try {
				final CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				if (convertView != null) {
					ProgressBar progressBar = ((ProgressBar) convertView.findViewById(R.id.progressbar));
					if (progressBar != null) {
						convertView = null;
					}
				}
				if (position == getCount()-1) {
					String aURL = null;
					try {
						String rel = CommunityFeed.getAttrRel(feed.link);
						if (rel.equalsIgnoreCase("next")) {
							aURL = CommunityFeed.getAttrHref(feed.link);
						}
						if(aURL == null || !aURL.equalsIgnoreCase("NA") || aURL.length() <= 0 )
							aURL = feed.nexturl ;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (aURL != null && aURL.length() > 3 && !aURL.equalsIgnoreCase("NA")) {
						LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						listItemView = inflator.inflate(R.layout.loading_layout, null);
						listItemView.setOnClickListener(CommunityMemberActivity.this);
						listItemView.setId(LOADING_ID);
						if (requestednextPage) {
							ProgressBar progressBar = ((ProgressBar) listItemView.findViewById(R.id.progressbar));
							TextView textView = ((TextView) listItemView.findViewById(R.id.loadingtext));
							progressBar.setVisibility(View.VISIBLE);
							textView.setText("Loading");
						}
						return listItemView;
					}
				}
				if (convertView == null) {
					listItemView = LayoutInflater.from(context).inflate(R.layout.community_memberlist_row, null);
				} else {
					listItemView = (View) convertView;
				}
				if (entry == null)
					return listItemView;

				final ImageView item_img = (ImageView) listItemView.findViewById(R.community_memberlist_row.memberImage_test);

				if(entry.genderType.equalsIgnoreCase("M")){
					item_img.setImageResource(R.drawable.male);
				}else if(entry.genderType.equalsIgnoreCase("F")){
					item_img.setImageResource(R.drawable.female);
				}

				//imageManager.loadForCommunity(true);
				if(entry.thumbUrl != null && !entry.thumbUrl.equals(""))
					imageLoader.displayImage(entry.thumbUrl, item_img, options);
				Log.v("entry.thumbUrl", "manoj"+entry.thumbUrl);
			//	imageManager.download(entry.thumbUrl, item_img, null, ImageDownloader.TYPE_THUMB_BUDDY);
				final ImageView buttonImage = (ImageView) listItemView.findViewById(R.community_memberlist_row.user_type);
				//final Button buttonImage = (Button) listItemView.findViewById(R.community_memberlist_row.user_type);

				if(!entry.userName.equalsIgnoreCase(SettingData.sSelf.getUserName()) && (getIntent().getStringExtra("OwnerUserName")).equalsIgnoreCase(SettingData.sSelf.getUserName())) {
					buttonImage.setImageResource(R.drawable.retake);
					buttonImage.setVisibility(View.VISIBLE);
					buttonImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//							System.out.println("Delete User = "+entry.userName);
							rtDialog = ProgressDialog.show(CommunityMemberActivity.this, "", getString(R.string.please_wait_while_loadin), true);
							removeFallower(comm_name, "", entry.userName);
						}
					});
				}else{
					buttonImage.setVisibility(View.GONE);
				}

				/*	Bitmap image = mImageLoader.getCacheBitmap(entry.thumbUrl);
				if (null == image) {
					item_img.setImageResource(R.drawable.defaultprofile);
				} else {
					item_img.setImageBitmap(image);
				}*/
				item_img.setTag(entry.userName);

				((TextView) listItemView.findViewById(R.community_memberlist_row.memberName)).setText((entry.lastName != null) ? (entry.firstName + " " + entry.lastName) : entry.firstName);

				return listItemView;
			} catch (Exception e) {
				return listItemView;
			}
		}
	}
	private String status = "";
	private String descStr = "";
	private void removeFallower(final String gpName, String add, final String remove){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String response = postDataOnServer(gpName, "", remove);
				if(response != null){
					//				System.out.println("DATA = "+response);
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(response);
						status = jsonObject.getString("status");
						descStr = jsonObject.getString("desc");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(rtDialog != null && rtDialog.isShowing())
						rtDialog.dismiss();
					if(status != null && status.equals("success")){
						CommunityMemberActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub

								if(mMemberAdapter != null){
									for(int i = 0; i < feed.entry.size(); i++){
										if(feed.entry.elementAt(i).userName.equalsIgnoreCase(remove)){
											feed.entry.removeElementAt(i);
										}
									}
									mMemberAdapter.setNewItemList(feed.entry);
									mMemberAdapter.notifyDataSetChanged();
								}
								UIActivityManager.refreshChannelList = true;
							}
						});
					}else if(status != null && status.equals("error")){
						CommunityMemberActivity.this.runOnUiThread(new Runnable() {
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

	public void onBackPressed() {
		if(onBackPressedCheck())return;
		finish();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_icon:
			if (BusinessProxy.sSelf.mCacheURLs.size() > 0)
				BusinessProxy.sSelf.mCacheURLs.removeAllElements();
			pushNewScreen(KainatHomeActivity.class, false);
			break;
		case R.id.searchScreen_inputBox:
			if (Logger.ENABLE) {
				Logger.debug(TAG, "Input box clicked.");
			}
			startThumbnailThread(10);
			break;
		case R.community_memberlist_row.memberImage_test:
			Utilities.closeSoftKeyBoard(v, this);
			DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, v.getTag().toString());

			/*Intent intent = new Intent(CommunityMemberActivity.this, WebviewActivity.class);
			intent.putExtra("PAGE", (byte) 1);
			startActivity(intent);
			 */
			//DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, feed.userName);
			// pushNewScreen(WebviewActivity.class, false);

			Intent intent = new Intent(CommunityMemberActivity.this, ProfileViewActivity.class);
			intent.putExtra("USERID",(String)v.getTag());
			intent.putExtra("CallFrom",1);
			startActivity(intent);
		case LOADING_ID:
			loading = v;
			ProgressBar progressBar = ((ProgressBar) v.findViewById(R.id.progressbar));
			if (progressBar != null) {
				TextView textView = ((TextView) v.findViewById(R.id.loadingtext));
				progressBar.setVisibility(View.VISIBLE);
				if (textView.getText().toString().equalsIgnoreCase(getString(R.string.load_more))) {
					requestednextPage = true;
				}
				textView.setText("Loading..");
			}
			if (requestednextPage) {
				String aURL = "";

				aURL = CommunityFeed.getAttrRel(feed.links,"next");
				if(aURL != null || !aURL.equalsIgnoreCase("NA"))
					aURL = feed.nexturl ;
				oldFeed = feed;
				
				executeRequestData(aURL);//executeRequestData(aURL, mCurrentScreen);
			}
			break;
		}
	}

	public void onThumbnailResponse(final ThumbnailImage value, byte[] data) {
		if (mMainList == null)
			return;
		runOnUiThread(new Runnable() {
			public void run() {
				int index = mMainList.getFirstVisiblePosition();
				index = value.mIndex - index;
				View view = mMainList.getChildAt(index);
				if (null == view) {
					return;
				}
				ImageView img = (ImageView) view.findViewById(R.community_memberlist_row.memberImage_test);
				if (null != img) {
					img.setVisibility(View.VISIBLE);
					//img.setImageBitmap(value.mBitmap);
				}
			}
		});
	}

	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (null != mSearchTimer) {
			mSearchTimer.cancel();
			mSearchTimer = null;
		}
		mSearchTimer = new Timer();
		mSearchTimer.schedule(new OtsSchedularTask(this, new Byte(TASK_SEARCH), (byte)0), SEARCH_INIT_DELAY);
	}

	public void notificationFromTransport(ResponseObject response) {

	}
	//	public void myScreenName(CommunityFeed feed){
	//		//		System.out.println("------------media screen name-------------- : "+name);
	//		//		BusinessProxy.sSelf.dynamicScreen = name;// "PBK" ;//LIST_BOX = LIST_BOX_CONTACT;
	//		//		new Thread(new Runnable() {
	//		//			
	//		//			@Override
	//		//			public void run() {			
	//		//				BusinessProxy.sSelf.addPush.pushAdd(CommunityChatActivity.this);
	//		//			}
	//		//		}).start();
	//
	//		String name = Feed.getAttrCode(feed.tracking) ;
	//
	//		BusinessProxy.sSelf.dynamicScreen = name;
	//		if(name==null||name.length()<0||name.equalsIgnoreCase("NA"))
	//			BusinessProxy.sSelf.dynamicScreen = "ComMembers";
	//		//	System.out.println("------------community member screen name-------------- : "+BusinessProxy.sSelf.dynamicScreen);
	//
	//		BusinessProxy.sSelf.SEO = feed.seo;		
	//		new Thread(new Runnable() {			
	//			@Override
	//			public void run() {
	//				BusinessProxy.sSelf.addPush.closeAdd() ;
	//				BusinessProxy.sSelf.addPush.noAddScreen = null ;
	//				BusinessProxy.sSelf.addPush.pushAdd(CommunityMemberActivity.this);
	//			}
	//		}).start();
	//	}
}