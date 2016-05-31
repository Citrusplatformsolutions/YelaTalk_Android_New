package com.kainat.app.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.HttpSynch;
import com.kainat.app.android.helper.CommunityChatDB;
import com.kainat.app.android.helper.CommunityTable;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.CommunityFeedParser;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.FeedManager;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ImageLoader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class KainatCommunityActivity extends UICommunityManager implements
		Runnable, OnClickListener, OnSchedularListener,
		ThumbnailReponseHandler, OnScrollListener, HttpSynchInf {
	private static final String TAG = KainatCommunityActivity.class
			.getSimpleName();
	private int selectedTab = 1;
	private int prevSelectedTab = 1;
	private ListView mMainList;
	private GridView community_mainList_grid;
	private CommunityAdapterGrid mMediaGridAdapter;
	private CommunityAdapter mMediaAdapter;
	private CommunityFeed feed;
	private CommunityFeed oldFeed;
	public static final int ACTION_BACK = 5000;
	public static final int COMM_COUNT = 50;
	public static final int ACTION_BACK_SPECIFIED_USER_LIST = 6000;
	private static final byte HTTP_TIMEOUT = 3;
	private final String MY_COMMUNITY = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/api/group/user/userpublic";
	private final String RECOMMENDED_COMMUNITY = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/api/group/categories";
	private final String COMMUNITY_SEARCH = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/api/list/searched/advance?%@";
	private final String PENDING_INVITES = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/api/group/pendinginvites";
	int prevSelected = -1;
	String iThumbFormat = "iThumbFormat=100x100&vThumbFormat=100x100&iFormat=300x300";
	String i_newThumbFormat = "iThumbFormat=300x300&vThumbFormat=300x300&iFormat=300x300";
	private Thread mThread;
	private Timer mCallBackTimer;
	public static final int LOADING_ID = -1000;
	private static final byte DATA_CALLBACK = 1;
	private static final byte ERROR_CALLBACK = 2;
	private boolean mIsRunning = true;
	private String mPostURL = MY_COMMUNITY;
	private int iLoggedUserID;
	private byte[] mResponseData;
	private static final int CHUNK_LENGTH = 1024;
	private String mCurrentScreen = "";
	private ImageLoader mImageLoader;
	public static final String MEDIA_URL = "MEDIA_URL";
	public static final String ONLY_MEDAI_DISPLAY = "ONLY_MEDAI_DISPLAY";
	public static final String MEDIA_OPEN_MEDIA = "MEDIA_OPEN_MEDIA";
	public static final String MEDIA_OPEN_MEDIA_LIST = "MEDIA_URL_MEDIA_LIST";
	public static final int MEDIA_OPEN = 1;
	public static final int MEDIA_LIST_OPEN = 2;
	private String iSubscriberCount;
	private int resultCode = -1;
	private String iMediaFor;
	private int meidaSearchCriteria;
	private int mediaCategories;
	boolean cancelRequest = false;
	LinearLayout mLoadingBar;
	int tCom = 0;
	int idsReport[] = new int[] { 1, 2 };
	String idsNameReport[] = new String[] { "Create new community", "Cancel" };
	boolean started;
	View headerView;
	boolean mMyCommunities;
	// ImageLoader imageLoader;
	DisplayImageOptions options;
	ImageView clean_search_iv_member;
	EditText community_search;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, false);
		started = true;
		UIActivityManager.SuffelNeed = 2;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final int as = (metrics.widthPixels) - 30;
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.imagedefault)
				.showImageOnFail(R.drawable.imagedefault)
				.showImageOnLoading(R.drawable.imagedefault)
				.postProcessor(new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap bmp) {
						return Bitmap.createScaledBitmap(bmp, 300, 300, false);
					}
				}).build();

		if (KeyValue.getBoolean(KainatCommunityActivity.this, "OLDUSER")) {

		} else {
			// ((KainatHomeActivity)getParent()).updateTitle(getString(R.string.my_communities).toUpperCase());

			KeyValue.setBoolean(KainatCommunityActivity.this, "OLDUSER", true);
			String intrest = KeyValue.getString(KainatCommunityActivity.this,
					KeyValue.INTEREST);
			if (intrest != null && intrest != "") {

			} else {
				intrest = "Popular";
			}
			Intent intent = new Intent(KainatCommunityActivity.this,
					SearchCommunityActivity.class);
			intent.putExtra("TYPE", 4);
			// intent.putExtra("CommunityType", "Popular");
			intent.putExtra("CommunityType", intrest);
			intent.putExtra("UrlIs", "");
			startActivity(intent);
		}
		// Set core values to start refresh
		/*
		 * if(BusinessProxy.sSelf.sqlDB == null) { LandingPageDatabaseHelper
		 * database = null; if(database==null){ database = new
		 * LandingPageDatabaseHelper(KainatCommunityActivity.this); }
		 * if(BusinessProxy.sSelf.sqlDB==null){ BusinessProxy.sSelf.sqlDB =
		 * database.getWritableDatabase(); }
		 * //BusinessProxy.sSelf.initializeDatabase(); }
		 */
		if (UIActivityManager.launchedWithNoNetwork) {
			try {
				UIActivityManager.launchedWithNoNetwork = false;
				handleLoginResponseNew(null);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Add Google Analytics
		/*
		 * btn_create_channel_community =
		 * (Button)findViewById(R.id.btn_create_channel_community);
		 * btn_create_channel_community.setOnClickListener(new OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent intent = new Intent(KainatCommunityActivity.this,
		 * CreateCommunityActivity.class); startActivity(intent); } });
		 */
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Channel List Screen");
		t.set("&uid", "" + BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(KainatCommunityActivity.this)
				.reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(KainatCommunityActivity.this)
				.reportActivityStop(this);
	}

	public void showFeaturedcommunity(String url) {
		// setSelected(view);
		DataModel.sSelf.removeObject(DMKeys.FEED_MY_ALBUM_PLUS);
		// findViewById(R.id.home_icon).setVisibility(View.GONE);
		findViewById(R.id.community_createButton).setVisibility(View.GONE);
		// findViewById(R.id.community_backButton).setVisibility(View.GONE);
		findViewById(R.id.top_featured_linearLayout).setVisibility(View.GONE);
		findViewById(R.id.bottom_featured_linearLayout)
				.setVisibility(View.GONE);
		// findViewById(R.id.bottom_featured_linearLayout).setVisibility(View.GONE);
		findViewById(R.id.bottom_tab_mycommunities).setVisibility(
				View.INVISIBLE);
		findViewById(R.id.bottom_tab_community_search).setVisibility(
				View.INVISIBLE);
		findViewById(R.id.bottom_tab_pending_request).setVisibility(
				View.INVISIBLE);

		// CommunityFeed.Entry entry = feed.entry.get((Integer) view.getTag());
		feed = null;
		selectedTab = 3;

		executeRequestData(url, " " + "Featured Communities");
	}

	protected void onDestroy() {
		super.onDestroy();
		DataModel.sSelf.removeObject(DMKeys.ENTRY);
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			int maxCount = view.getLastVisiblePosition()
					- view.getFirstVisiblePosition() + 1;
			// startThumbnailThread(maxCount);
		}
	}

	private void startThumbnailThread(int maxCount) {
		try {
			if (maxCount > mMediaAdapter.getCount())
				maxCount = mMediaAdapter.getCount();
			int start = mMainList.getFirstVisiblePosition();
			String[] names = new String[maxCount];
			int[] indexs = new int[maxCount];
			Entry entry = null;
			for (int i = 0; i < maxCount; ++i) {
				entry = mMediaAdapter.getItem(start + i);// feed.entry.elementAt(start
				// + i);
				if (selectedTab == 4) {
					mImageLoader
							.setRequestType(ImageLoader.LAOD_IMAGES_FORM_RT);
					names[i] = entry.userName;
				} else {
					mImageLoader
							.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
					if (entry != null && entry.thumbUrl != null)
						names[i] = entry.thumbUrl;
				}

				indexs[i] = start + i;
			}
			if (selectedTab == 4) {
				mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_RT);
			} else
				mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
			mImageLoader.cleanAndPutRequest(names, indexs);
		} catch (Exception e) {
			e.printStackTrace();
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

	private class CommunityAdapter extends BaseAdapter {
		private Context context;
		private boolean mMyCommunities;

		public CommunityAdapter(Context context) {
			this.context = context;
		}

		public void setMyCommunities(boolean myCommunities) {
			this.mMyCommunities = myCommunities;
		}

		public void setNewItemList(Vector<Entry> itemList) {
			feed.entry.clear();
			feed.entry.addAll(itemList);
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
			return feed.entry.size();
		}

		public Entry getItem(int position) {
			if (position > -1 && null != feed && position < feed.entry.size())
				return feed.entry.get(position);
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup arg2) {
			View listItemView = null;
			try {
				final CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				if (convertView != null) {
					ProgressBar progressBar = ((ProgressBar) convertView
							.findViewById(R.id.progressbar));
					if (progressBar != null) {
						convertView = null;
					}
				}
				if (position == getCount() - 1) {
					String aURL = null;
					try {
						aURL = CommunityFeed.getAttrRel(feed.links, "next");
						if (aURL == null || aURL.equalsIgnoreCase("NA")
								|| aURL.length() <= 0)
							aURL = feed.nexturl;

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (aURL != null && aURL.length() > 3
							&& !aURL.equalsIgnoreCase("NA")) {
						LayoutInflater inflator = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						listItemView = inflator.inflate(
								R.layout.loading_layout, null);
						listItemView
								.setOnClickListener(KainatCommunityActivity.this);
						listItemView.setId(LOADING_ID);
						if (requestednextPage) {
							ProgressBar progressBar = ((ProgressBar) listItemView
									.findViewById(R.id.progressbar));
							TextView textView = ((TextView) listItemView
									.findViewById(R.id.loadingtext));
							progressBar.setVisibility(View.VISIBLE);
							textView.setText(getString(R.string.loading_dot));
						}
						return listItemView;
					}
				}
				if (convertView == null) {
					listItemView = LayoutInflater.from(context).inflate(
							R.layout.community_list_row, null);
				} else {
					listItemView = (View) convertView;
				}
				if (entry == null)
					return listItemView;

				LinearLayout mediaLayout = (LinearLayout) listItemView
						.findViewById(R.community_list_row.communityInfoLayout);
				mediaLayout.setTag(position);
				ImageView imageView = null;
				imageView = ((ImageView) listItemView
						.findViewById(R.community_list_row.communityImage));
				imageView.setImageBitmap(null);// setImageResource(R.drawable.def2);
				imageView.setTag(position);
				String n = entry.thumbUrl.trim();
				if (selectedTab == 4) {
					n = entry.userName;
				}

				// Bitmap bitmap = mImageLoader.getCacheBitmap(n);
				// if (null != bitmap &&
				// !entry.userName.trim().equalsIgnoreCase("Rocketalk Help")) {
				// imageView.setImageBitmap(bitmap);
				// } else {
				// if(bitmap != null){
				// imageView.setImageBitmap(bitmap);
				// }
				// }
				// entry.thumbUrl
				// Log.i(TAG, "com name - "+entry.groupName
				// +", URL - "+entry.thumbUrl);

				imageLoader.displayImage(entry.thumbUrl, imageView, options);
				/*
				 * imageManager.loadForCommunity(true);
				 * imageManager.download(entry.thumbUrl, imageView, handler,
				 * ImageDownloader.TYPE_THUMB_BUDDY);
				 */
				// if(!entry.thumbUrl.trim().equals(""))
				// {
				// }
				// else
				// {
				// imageView.setVisibility(View.VISIBLE);
				// imageView.setBackgroundResource(R.drawable.attachicon);
				// }

				TextView textView = ((TextView) listItemView
						.findViewById(R.community_list_row.communityName));
				if (selectedTab == 1 || selectedTab == 3) {// My Communities and
					// Communities of
					// Recommended
					// Community
					if (entry.groupName != null
							&& entry.groupName.trim().length() > 0) {
						textView.setText(entry.groupName);
					}
				} else if (selectedTab == 2) {// Recommended Communities
					if (entry.displayName != null
							&& entry.displayName.trim().length() > 0) {
						textView.setText(entry.displayName);
					}
				} else if (selectedTab == 4) {// Pending Request
					if (entry.userName != null
							&& entry.userName.trim().length() > 0) {
						textView.setText(entry.userName);
					}
				}

				// if (entry.joinOrLeave != null && entry.joinOrLeave.length() >
				// 2
				// && !mMyCommunities) {
				// // joinOrLeave
				// Button view = (Button)
				// listItemView.findViewById(R.id.join_leave);
				// view.setTag(entry);
				// view.setVisibility(View.VISIBLE);
				// if (entry.joinOrLeave.equalsIgnoreCase("join")) {
				// // view.setText(getString(R.string.follow)) ;
				// view.setBackgroundResource(R.drawable.com_following);
				// // view.setCompoundDrawables(left, top, right, bottom)
				//
				// Drawable search_unsel = getResources().getDrawable(
				// R.drawable.x_arrows_right);
				// Rect r = null;
				//
				// Drawable[] d = view.getCompoundDrawables();
				// r = null;
				// if (d[2] != null)
				// r = d[2].getBounds();
				// if (r != null)
				// search_unsel.setBounds(r);
				// // view.setCompoundDrawables(null, null, search_unsel,
				// // null);
				//
				// // view.set
				// // @drawable/x_arrows_left
				// } else {
				// // view.setText(getString(R.string.unfollow)) ;
				// view.setBackgroundResource(R.drawable.com_follow);
				// Drawable search_unsel = getResources().getDrawable(
				// R.drawable.x_arrows_left);
				// Rect r = null;
				//
				// Drawable[] d = view.getCompoundDrawables();
				// r = null;
				// if (d[2] != null)
				// r = d[2].getBounds();
				// if (r != null)
				// search_unsel.setBounds(r);
				// // view.setCompoundDrawables(null, null, search_unsel,
				// // null);
				// }
				// view.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// joinLeaveCommunity(v);
				// mMediaAdapter.notifyDataSetChanged();
				// mMediaAdapter.notifyDataSetInvalidated();
				//
				// }
				// });
				// } else
				// listItemView.findViewById(R.id.join_leave).setVisibility(View.GONE);

				textView = ((TextView) listItemView
						.findViewById(R.community_list_row.communityInfo));
				StringBuilder builder = new StringBuilder();
				boolean flag = false;
				if (selectedTab == 1 || selectedTab == 3) {// My Communities and
					// Communities of
					// Recommended
					// Community
					if (entry.noOfMember > 0) {
						builder.append(entry.noOfMember + " ").append(
								getString(R.string.member));
						if (entry.noOfMember > 1) {
							builder.append("s");
						}
						flag = true;
					} else {
						builder.append(", 0" + " ").append(
								getString(R.string.online));
						// if (entry.noOfMessage > 1) {
						// builder.append("s");
						// }
					}
					// if (entry.noOfOnlineMember > 0) {
					// if (flag) {
					// builder.append(", ");
					// }
					// builder.append(entry.noOfOnlineMember+" ").append(getString(R.string.online));
					// flag = true;
					// }else
					// {
					// builder.append(", 0"+" ").append(getString(R.string.online));
					// // if (entry.noOfMessage > 1) {
					// // builder.append("s");
					// // }
					// }
					if (entry.noOfMessage > 0) {
						if (flag) {
							builder.append(", ");
						}
						builder.append(entry.noOfMessage + " ").append(
								getString(R.string.posts));
						if (entry.noOfMessage > 1) {
							builder.append("s");
						}
					} else {
						builder.append(", 0" + " ").append(
								getString(R.string.posts));
						// if (entry.noOfMessage > 1) {
						// builder.append("s");
						// }
					}

				} else if (selectedTab == 2) {// Recommended Communities
					if (entry.count > 0) {
						builder.append(entry.count).append(" Communit");
						if (entry.count > 1) {
							builder.append("ies");
						} else {
							builder.append("y");
						}
					}
				}

				// New Message count
				if (entry.newMessageCount > 0) {
					((CImageView) listItemView
							.findViewById(R.id.unread_notification))
							.setVisibility(View.VISIBLE);
					((TextView) listItemView.findViewById(R.id.unread_count))
							.setVisibility(View.VISIBLE);

					// if(entry.newMessageCount < 51)
					((TextView) listItemView.findViewById(R.id.unread_count))
							.setText("" + entry.newMessageCount);
					// else
					// ((TextView)
					// listItemView.findViewById(R.id.unread_count)).setText("50+");
				} else {
					((CImageView) listItemView
							.findViewById(R.id.unread_notification))
							.setVisibility(View.GONE);
					((TextView) listItemView.findViewById(R.id.unread_count))
							.setVisibility(View.GONE);
				}

				if (selectedTab == 4) {// Pending Request
					if (entry.groupName != null
							&& entry.groupName.trim().length() > 0) {
						builder.append("wants to join ");
						builder.append("<b>" + entry.groupName + "</b>");
					}
				}
				textView.setText(Html.fromHtml(builder.toString()));

				textView = (TextView) listItemView
						.findViewById(R.community_list_row.fn_ln);
				textView.setVisibility(View.GONE);
				if (selectedTab == 4) {// Pending Request
					if ((entry.firstName != null && entry.firstName.trim()
							.length() > 0)
							|| (entry.lastName != null && entry.lastName.trim()
									.length() > 0)) {
						textView.setVisibility(View.VISIBLE);
						textView.setText(entry.firstName + " " + entry.lastName);
						textView.setVisibility(View.VISIBLE);
					}
				} else {
					textView.setVisibility(View.GONE);
				}

				textView = ((TextView) listItemView
						.findViewById(R.community_list_row.modratedCommunityLabel));
				if (selectedTab == 4) {
					textView.setVisibility(View.GONE);
				} else {
					if (entry.moderated.equalsIgnoreCase("YES")) {
						textView.setVisibility(View.VISIBLE);
					} else {
						textView.setVisibility(View.GONE);
					}
				}
				// textView.setText(textView.getText()+" : "+entry.moderated);

				textView = ((TextView) listItemView
						.findViewById(R.community_list_row.privateCommunityLabel));
				if (selectedTab == 4) {
					textView.setVisibility(View.GONE);
				} else {
					if (entry.publicCommunity.equalsIgnoreCase("NO")) {
						textView.setVisibility(View.VISIBLE);
						((ImageView) listItemView
								.findViewById(R.community_list_row.private_lock))
								.setVisibility(View.VISIBLE);
					} else {
						((ImageView) listItemView
								.findViewById(R.community_list_row.private_lock))
								.setVisibility(View.GONE);
						textView.setVisibility(View.INVISIBLE);
					}
				}

				textView = ((TextView) listItemView
						.findViewById(R.community_list_row.createdOnWithPermission));

				if (selectedTab == 1 || selectedTab == 3) {
					textView.setVisibility(View.VISIBLE);
					Drawable image = null;
					if (entry.memberPermission.equalsIgnoreCase("OWNER")) {
						image = getResources().getDrawable(
								R.drawable.communityowner);
					} else if (entry.memberPermission
							.equalsIgnoreCase("MEMBER")) {
						image = getResources().getDrawable(
								R.drawable.communitymembers);
					} else if (entry.memberPermission.equalsIgnoreCase("ADMIN")) {
						image = getResources().getDrawable(
								R.drawable.communityadmin);
					}

					if (image != null) {
						textView.setCompoundDrawablesWithIntrinsicBounds(image,
								null, null, null);
					} else
						textView.setCompoundDrawablesWithIntrinsicBounds(null,
								null, null, null);
					// if (entry.createdOn != null &&
					// entry.createdOn.trim().length() > 0)
					// {
					// StringBuilder strBuilder = new StringBuilder();
					// strBuilder.append(" " + getString(R.string.created_on)
					// + " ");
					// strBuilder.append(DateFormatUtils.convertDate(
					// entry.createdOn, "yyyy-MM-dd", "MMM dd, yyyy",
					// KainatCommunityActivity.this));
					// textView.setText(strBuilder.toString());
					// }
				} else if (selectedTab == 2 || selectedTab == 4) {
					textView.setVisibility(View.GONE);
				}

				ImageView moreImage = (ImageView) listItemView
						.findViewById(R.community_list_row.moreButton);
				moreImage.setTag(position);
				if (selectedTab == 1 || selectedTab == 3 || selectedTab == 4) {
					moreImage.setVisibility(View.GONE);
				} else if (selectedTab == 2) {
					moreImage.setVisibility(View.VISIBLE);
				}

				// Commented, as deleted from layout
				// if (selectedTab == 4)
				// {
				// StringBuffer key = new StringBuffer();
				// key.append(entry.groupName).append("_").append(entry.userName);
				// Hashtable<String, Hashtable<String, String>> table =
				// entry.buttons.get(key.toString());
				// for (Iterator<String> iterator = table.keySet().iterator();
				// iterator.hasNext();)
				// {
				// String key1 = iterator.next();
				// Hashtable<String, String> attrs = table.get(key1);
				// View button = null;
				// if (key1.equalsIgnoreCase("Accept")) {
				// button = listItemView
				// .findViewById(R.id.community_acceptButton);
				// } else if (key1.equalsIgnoreCase("Decline")) {
				// button = listItemView
				// .findViewById(R.id.community_declineButton);
				// } else if (key1.equalsIgnoreCase("Ignore")) {
				// button = listItemView
				// .findViewById(R.id.community_ignoreButton);
				// }
				// if (button != null) {
				// button.setVisibility(View.VISIBLE);
				// button.setTag((CommunityFeed.getAttrAction(attrs)
				// + "~~" + position));
				// }
				// }
				// } else {
				// listItemView.findViewById(R.id.community_acceptButton).setVisibility(View.GONE);
				// listItemView.findViewById(R.id.community_declineButton).setVisibility(View.GONE);
				// listItemView.findViewById(R.id.community_ignoreButton).setVisibility(View.GONE);
				// }
				return listItemView;
			} catch (Exception e) {
				return listItemView;
			}
		}
	}

	// GridView Work
	//
	//
	//
	private class CommunityAdapterGrid extends BaseAdapter {
		private Context context;
		private boolean mMyCommunities;

		public CommunityAdapterGrid(Context context) {
			this.context = context;
		}

		public void setMyCommunities(boolean myCommunities) {
			this.mMyCommunities = myCommunities;
		}

		public void setNewItemList(Vector<Entry> itemList) {
			feed.entry.clear();
			feed.entry.addAll(itemList);
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
			return feed.entry.size();
		}

		public Entry getItem(int position) {
			if (position > -1 && null != feed && position < feed.entry.size())
				return feed.entry.get(position);
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup arg2) {
			View listItemView = null;
			try {
				final CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				if (convertView != null) {
					ProgressBar progressBar = ((ProgressBar) convertView
							.findViewById(R.id.progressbar));
					if (progressBar != null) {
						convertView = null;
					}
				}

				if (convertView == null) {
					listItemView = LayoutInflater.from(context).inflate(
							R.layout.community_grid_new_row, null);
				} else {
					listItemView = (View) convertView;
				}
				if (entry == null)
					return listItemView;
				LinearLayout mediaLayout = (LinearLayout) listItemView
						.findViewById(R.community_list_grid_row.communityInfoLayout);
				mediaLayout.setTag(position);
				ImageView imageView = null;
				imageView = ((ImageView) listItemView
						.findViewById(R.id.img_community_background));
				imageView.setImageBitmap(null);// setImageResource(R.drawable.def2);
				imageView.setTag(entry);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						CommunityFeed.Entry entry = (CommunityFeed.Entry) v
								.getTag();
						DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",entry);
						Intent intent = new Intent(
								KainatCommunityActivity.this,
								CommunityChatActivity.class);
						startActivity(intent);
					}
				});
				ImageView imgIsowner = ((ImageView) listItemView
						.findViewById(R.id.img_isowner));
				ImageView img_follow = ((ImageView) listItemView
						.findViewById(R.id.img_follow));
				img_follow.setVisibility(View.GONE);
				LinearLayout ll_iconfollow = ((LinearLayout) listItemView
						.findViewById(R.id.ll_iconfollow));
				ll_iconfollow.setVisibility(View.GONE);
				LinearLayout ll_owner = ((LinearLayout) listItemView
						.findViewById(R.id.ll_owner));
				ll_owner.setVisibility(View.VISIBLE);
				imgIsowner.setVisibility(View.VISIBLE);
				if (entry.admin == 1)
					imgIsowner.setBackgroundResource(R.drawable.iconowner);
				else {
					imgIsowner.setBackgroundResource(R.drawable.iconmember);
				}
				String n = entry.thumbUrl.trim();
				if (selectedTab == 4) {
					n = entry.userName;
				}
				imageLoader.displayImage(entry.thumbUrl, imageView, options);
				TextView textView = ((TextView) listItemView
						.findViewById(R.id.txt_community_name));
				final Button btn_feature = ((Button) listItemView
						.findViewById(R.id.btn_feature));
				ImageView imageCounter = (ImageView) listItemView
						.findViewById(R.id.imageCounter);
				//
				// COUNTER UPDATE
				//
				// GRID
				if (entry.newMessageCount > 0) {
					((CImageView) listItemView
							.findViewById(R.id.unread_notification))
							.setVisibility(View.VISIBLE);
					((TextView) listItemView.findViewById(R.id.unread_count))
							.setVisibility(View.VISIBLE);

					// if(entry.newMessageCount < 51)
					((TextView) listItemView.findViewById(R.id.unread_count))
							.setText("" + entry.newMessageCount);
					// else
					// ((TextView)
					// listItemView.findViewById(R.id.unread_count)).setText("50+");
				} else {
					((CImageView) listItemView
							.findViewById(R.id.unread_notification))
							.setVisibility(View.GONE);
					((TextView) listItemView.findViewById(R.id.unread_count))
							.setVisibility(View.GONE);
				}

				//
				//
				//

				btn_feature.setTag(position + 200);

				if (entry.featured.equals("true")) {
					btn_feature.setVisibility(View.VISIBLE);
					// btn_feature.setBackgroundColor(R.color.gray);
					btn_feature.setBackgroundResource(R.color.red);
					btn_feature.setText(getString(R.string.unfeatured));
				} else if (entry.featured.equals("false")) {
					btn_feature.setVisibility(View.VISIBLE);
					btn_feature
							.setBackgroundResource(R.color.heading_text_green);
					btn_feature.setText(getString(R.string.featured));

				} else {
					btn_feature.setVisibility(View.GONE);
				}

				btn_feature.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// WEB SERVICE HIT
						String id = v.getTag().toString();
						int idd = Integer.parseInt(id);
						idd = idd - 200;
						boolean boolean2 = Boolean.parseBoolean(feed.entry
								.get(idd).featured);
						FeatureLoader fl = new FeatureLoader(""
								+ feed.entry.get(idd).groupId, !boolean2);
						Thread mThread = new Thread(fl);
						mThread.start();
						if (boolean2) {
							feed.entry.get(idd).featured = "false";
							btn_feature.setVisibility(View.VISIBLE);
							btn_feature.setBackgroundResource(R.color.red);
							btn_feature.setText(getString(R.string.unfeatured));
							/*
							 * btn_feature.setVisibility(View.VISIBLE);
							 * btn_feature.setBackgroundResource(R.color.red);
							 * btn_feature.setText("Un-Featured");
							 */
						} else {
							feed.entry.get(idd).featured = "true";
							btn_feature.setVisibility(View.VISIBLE);
							// btn_feature.setBackgroundColor(R.color.gray);
							btn_feature
									.setBackgroundResource(R.color.heading_text_green);
							btn_feature.setText(getString(R.string.featured));
						}
						community_mainList_grid.setSelection(idd);
						mMediaGridAdapter.notifyDataSetChanged();
						FeedTask ft = new FeedTask(KainatCommunityActivity.this);
						ft.saveCommunity(feed);
						// Toast.makeText(context, ""+v.getTag().toString(),
						// 1).show();
					}
				});

				/*
				 * textView = ((TextView) listItemView
				 * .findViewById(R.community_list_row.communityInfo));
				 */
				// StringBuilder builder = new StringBuilder();
				if (entry != null && entry.groupName != null)
					textView.setText(entry.groupName);

				TextView txt_membercount = (TextView) listItemView
						.findViewById(R.id.txt_membercount);
				TextView txt_postcount = (TextView) listItemView
						.findViewById(R.id.txt_messagecount);
				txt_membercount.setText(""
						+ feed.entry.get(position).noOfMember + " ");
				txt_postcount.setText("" + feed.entry.get(position).noOfMessage
						+ " ");
				return listItemView;
			} catch (Exception e) {
				return listItemView;
			}
		}
	}

	//
	//
	//
	//
	//
	private boolean lodingCanceled = false;

	class CProgressDialog extends ProgressDialog {
		public CProgressDialog(Context context) {
			super(context);
			lodingCanceled = false;
		}

		public void onBackPressed() {
			return;
			// feed = (CommunityFeed) DataModel.sSelf.getObject(DMKeys.FEED);
			// if(feed == null){
			//
			// }
			// lodingCanceled = true;
			// helper.close();
			// dismiss();
			// super.onBackPressed();
		}
	}

	protected void showLoggingDialog(String aString) {
		mProgressDialog = new CProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.please_wait_while_loadin)
				+ aString + "..");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cancelRequest = true;
						dialog.dismiss();
					}
				});
		mProgressDialog.show();
	}

	boolean backPressed;

	public void onBackPressed() {
		if (UIActivityManager.sharingFromOutside == 1) {
			UIActivityManager.sharingFromOutside = 0;
			finish();
		}

		if (onBackPressedCheck())
			return;

		if (getIntent().getBooleanExtra(Constants.FEATURED_COMMUNITY, false)) {
			finish();
			return;
		}
		if (selectedTab == 3) {
			backPressed = true;
			refreshRequest = false;
			onClickTab3();
			return;
		}

		// if (resultCode == ACTION_BACK_SPECIFIED_USER_LIST) {
		// Intent intent = new
		// Intent(KainatCommunityActivity.this,MediaVideoPlayer.class);
		// intent.putExtra("VIDEO_PATH", "/sdcard/f1.3gp");
		// startActivityForResult(intent, ACTION_BACK);
		// this.resultCode = -1;
		// return;
		// }
		// if (DataModel.sSelf.getBoolean(AbstractMedia.ONLY_MEDAI_DISPLAY)) {
		// DataModel.sSelf.storeObject(AbstractMedia.MEDIA_OPEN_MEDIA,
		// AbstractMedia.MEDIA_OPEN);
		// DataModel.sSelf.storeObject(AbstractMedia.MEDIA_URL,
		// DataModel.sSelf.getString(AbstractMedia.MEDIA_URL + 1));
		// finish();
		// }
		else if (getIntent().getByteExtra("PAGE", (byte) 0) == 1) {
			if (BusinessProxy.sSelf.mCacheURLs.size() > 0)
				BusinessProxy.sSelf.mCacheURLs
						.remove(BusinessProxy.sSelf.mCacheURLs.size() - 1);
			finish();
			return;
		} else
			// popScreen();
			HttpSynch.getInstance().cancel();
		moveTaskToBack(true);
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	private void hideTopBar() {
		((LinearLayout) findViewById(R.id.top_featured_linearLayout))
				.setVisibility(View.GONE);
	}

	private void showTopBar() {
		((LinearLayout) findViewById(R.id.top_featured_linearLayout))
				.setVisibility(View.VISIBLE);

	}

	private void hideHF() {
		((Button) findViewById(R.id.media_postButton)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.top_featured_linearLayout))
				.setVisibility(View.GONE);
	}

	private void showHF() {
		((Button) findViewById(R.id.media_postButton))
				.setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.top_featured_linearLayout))
				.setVisibility(View.VISIBLE);
	}

	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.buddydone, menu);
	// menu.getItem(0).setVisible(false);
	// menu.getItem(1).setVisible(true);
	// menu.getItem(2).setVisible(true);
	// menu.getItem(3).setVisible(true);
	// return true;
	// }
	boolean refreshRequest = false;

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.done:
			break;
		case R.id.home:
			// if (BusinessProxy.sSelf.mCacheURLs.size() > 0)
			// BusinessProxy.sSelf.mCacheURLs.removeAllElements();
			// pushNewScreen(HomeActivity.class, false);
			break;
		// case R.id.inbox:
		// Intent intent = new Intent(this, InboxAvtivity.class);
		// intent.putExtra("PAGE", (byte) 1);
		// startActivityForResult(intent, 1);
		// break;
		case R.id.refresh1:
			TextView title = ((TextView) findViewById(R.id.community_screenTitle));
			refreshRequest = true;
			if (title.getText().equals("Recommended")) {
				initList();
				feed = null;
				executeRequestData(RECOMMENDED_COMMUNITY, " "
						+ getString(R.string.recommended));
			} else if (title.getText().equals("My Communities")) {
				initList();
				feed = null;
				executeRequestData(MY_COMMUNITY, " "
						+ getString(R.string.my_communities));
			} else if (title.getText().equals("Pending Invites")) {
				initList();
				feed = null;
				executeRequestData(PENDING_INVITES, " "
						+ getString(R.string.pending_invites));
			} else {
				initList();
				feed = null;
				executeRequestData(aURL, aMedia);
			}

			break;
		}
		return true;
	}

	public void hideShowNoMessageView() {
		if (feed != null && feed.entry != null && feed.entry.size() <= 0) {
			((ListView) findViewById(R.id.community_mainList))
					.setVisibility(View.GONE);
			(findViewById(R.id.loading_linearlayout)).setVisibility(View.GONE);
			(findViewById(R.id.loading_linearlayoutk))
					.setVisibility(View.VISIBLE);
			if (selectedTab == 4) {
				mMainList.setVisibility(View.GONE);
				((TextView) findViewById(R.id.community_noContent))
						.setText(getString(R.string.there_are_no_pending_requests));
			} else if (selectedTab == 1)
				((TextView) findViewById(R.id.community_noContent))
						.setText(getString(R.string.you_have_not_joined_any_communities));
			else
				((TextView) findViewById(R.id.community_noContent))
						.setText(getString(R.string.no_entry));
			// ((ListView)
			// findViewById(R.id.community_mainList)).setVisibility(View.INVISIBLE);
		} else {
			(findViewById(R.id.loading_linearlayoutk)).setVisibility(View.GONE);
			/*
			 * ((ListView) findViewById(R.id.community_mainList))
			 * .setVisibility(View.VISIBLE);
			 */
			if (mMainList != null)
				mMainList.removeHeaderView(headerView);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.resultCode = resultCode;
		if (resultCode == ACTION_BACK_SPECIFIED_USER_LIST) {
			hideHF();
			feed = (CommunityFeed) DataModel.sSelf
					.getObject(DMKeys.FEED_BY_SPECIFIED_USER);
			runOnUiThread(new Runnable() {
				public void run() {
					hideShowNoMessageView();
					mImageLoader
							.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
					mMainList.setAdapter(mMediaAdapter);
					if (null != feed && null != feed.entry) {
						if (feed.entry.size() > 10) {
							startThumbnailThread(10);
						} else {
							startThumbnailThread(feed.entry.size());
						}
					}
					Feed.Entry entry = (Feed.Entry) DataModel.sSelf
							.getObject(DMKeys.ENTRY);
					String title = (String) DataModel.sSelf
							.getObject(DMKeys.MEDIA_TITLE);
					if (title != null && title.length() > 0)
						((TextView) findViewById(R.id.media_screenTitle))
								.setText(title);
					else {
						if (entry != null && entry.author.name != null)
							((TextView) findViewById(R.id.media_screenTitle))
									.setText("More media by "
											+ entry.author.name + "'s Media");
					}
				}
			});
		} else if (resultCode == ACTION_BACK) {
			showHF();
			selectedTab = ((Integer) DataModel.sSelf.getObject("SELECTED_TAB"))
					.intValue();
			if (selectedTab == 1 || selectedTab == 5)
				showTopBar();
			else
				hideTopBar();
			feed = (CommunityFeed) DataModel.sSelf
					.getObject(DMKeys.FEED_MY_ALBUM_PLUS);
			if (feed == null)
				feed = (CommunityFeed) DataModel.sSelf.getObject(DMKeys.FEED);
			runOnUiThread(new Runnable() {
				public void run() {
					hideShowNoMessageView();
					mImageLoader
							.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
					mMainList.setAdapter(mMediaAdapter);
					if (null != feed && null != feed.entry) {
						if (feed.entry.size() > 10) {
							startThumbnailThread(10);
						} else {
							startThumbnailThread(feed.entry.size());
						}
					}
					((TextView) findViewById(R.id.media_screenTitle))
							.setText(aTitle);
				}
			});
		}
	}

	OnClickListener searchMediaClickListener = new OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.media_search_voice:
			case R.id.media_search_picture:
			case R.id.media_search_video:
			case R.id.media_search_text:
				if (!view.isSelected()) {
					if (view.getId() == R.id.media_search_text)
						meidaSearchCriteria |= Payload.PAYLOAD_BITMAP_TEXT;
					else if (view.getId() == R.id.media_search_picture)
						meidaSearchCriteria |= Payload.PAYLOAD_BITMAP_PICTURE;
					else if (view.getId() == R.id.media_search_voice)
						meidaSearchCriteria |= Payload.PAYLOAD_BITMAP_VOICE;
					else if (view.getId() == R.id.media_search_video)
						meidaSearchCriteria |= Payload.PAYLOAD_BITMAP_VIDEO;
					view.setSelected(true);
					view.setBackgroundResource(R.drawable.greenbutt);
					Button button = (Button) view;
					button.setTextColor(0xff000000);
				} else {
					if (view.getId() == R.id.media_search_text)
						meidaSearchCriteria = meidaSearchCriteria
								& ~(1 << Payload.PAYLOAD_BITMAP_TEXT);
					else if (view.getId() == R.id.media_search_picture)
						meidaSearchCriteria = meidaSearchCriteria
								& ~(1 << Payload.PAYLOAD_BITMAP_PICTURE);
					else if (view.getId() == R.id.media_search_voice)
						meidaSearchCriteria = meidaSearchCriteria
								& ~(1 << Payload.PAYLOAD_BITMAP_VOICE);
					else if (view.getId() == R.id.media_search_video)
						meidaSearchCriteria = meidaSearchCriteria
								& ~(1 << Payload.PAYLOAD_BITMAP_VIDEO);
					view.setSelected(false);
					view.setBackgroundResource(R.drawable.lightblue);
					Button button = (Button) view;
					button.setTextColor(0xffffffff);
				}
				break;
			case R.id.media_cancel_button:
				searchDialog.dismiss();
				break;
			case R.id.community_search_buttton:
				searchMedia(view);
				break;
			case R.id.media_search_cat_button_1:
				mediaCategories = 0;
				mediaCategories |= 1 << 1;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_2:
				mediaCategories = 0;
				mediaCategories |= 1 << 2;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_3:
				mediaCategories = 0;
				mediaCategories |= 1 << 3;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_4:
				mediaCategories = 0;
				mediaCategories |= 1 << 4;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_5:
				mediaCategories = 0;
				mediaCategories |= 1 << 5;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_6:
				mediaCategories = 0;
				mediaCategories |= 1 << 6;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_7:
				mediaCategories = 0;
				mediaCategories |= 1 << 7;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_8:
				mediaCategories = 0;
				mediaCategories |= 1 << 8;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			case R.id.media_search_cat_button_9:
				mediaCategories = 0;
				mediaCategories |= 1 << 9;
				disableCategoryButtons();
				view.setBackgroundResource(R.drawable.greenbutt);
				break;
			}
		}
	};

	private void searchMedia(View view) {
		try {
			StringBuffer toSearch = new StringBuffer(COMMUNITY_SEARCH);
			String text = ((EditText) findViewById(R.id.media_search_tag))
					.getText().toString().trim();
			if (text != null && text.length() > 0) {
				toSearch.append("tag=");
				toSearch.append(text);
			}
			text = ((EditText) searchDialog
					.findViewById(R.id.medai_search_username)).getText()
					.toString().trim();
			if (text != null && text.length() > 0) {
				toSearch.append("&user=");
				toSearch.append(text);
			}
			if (meidaSearchCriteria > 0) {
				toSearch.append("&mediatype=");
				if ((meidaSearchCriteria & Payload.PAYLOAD_BITMAP_TEXT) > 0)
					toSearch.append("TEXT");
				if ((meidaSearchCriteria & Payload.PAYLOAD_BITMAP_VOICE) > 0)
					toSearch.append("VOICE");
				if ((meidaSearchCriteria & Payload.PAYLOAD_BITMAP_VIDEO) > 0)
					toSearch.append("VIDEO");
				if ((meidaSearchCriteria & Payload.PAYLOAD_BITMAP_PICTURE) > 0)
					toSearch.append("PICTURE");
			}
			if (mediaCategories > 0) {
				toSearch.append("&category=");
				if ((mediaCategories & 1 << 1) > 0)
					toSearch.append(URLEncoder.encode("Bollywood", "UTF-8"));
				else if ((mediaCategories & 1 << 2) > 0)
					toSearch.append(URLEncoder.encode("Hollywood", "UTF-8"));
				else if ((mediaCategories & 1 << 3) > 0)
					toSearch.append(URLEncoder.encode("Cricket & Sports",
							"UTF-8"));
				else if ((mediaCategories & 1 << 4) > 0)
					toSearch.append(URLEncoder.encode("Nature-Travel", "UTF-8"));
				else if ((mediaCategories & 1 << 5) > 0)
					toSearch.append(URLEncoder
							.encode("RockeTalk Star", "UTF-8"));
				else if ((mediaCategories & 1 << 6) > 0)
					toSearch.append(URLEncoder.encode("Jokes", "UTF-8"));
				else if ((mediaCategories & 1 << 7) > 0)
					toSearch.append(URLEncoder.encode("Shaayari", "UTF-8"));
				else if ((mediaCategories & 1 << 8) > 0)
					toSearch.append(URLEncoder.encode("People", "UTF-8"));
				else if ((mediaCategories & 1 << 9) > 0)
					toSearch.append(URLEncoder.encode("Others", "UTF-8"));
			}
			searchDialog.dismiss();
			meidaSearchCriteria = 0;
			mediaCategories = 0;
			((EditText) findViewById(R.id.media_search_tag)).setText("");
			executeRequestData(toSearch.toString(), " " + "Media Search Result");
		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println("Search Error - " + ex.toString());
		}
	}

	private void init() {
		// feed = null ;

		int s = DataModel.sSelf
				.getInt(KainatCommunityActivity.MEDIA_OPEN_MEDIA);
		DataModel.sSelf.storeObject(
				KainatCommunityActivity.MEDIA_OPEN_MEDIA_LIST, 0);
		DataModel.sSelf
				.storeObject(KainatCommunityActivity.MEDIA_OPEN_MEDIA, 0);
		Bundle bundle = getIntent().getExtras();

		tCom = 100;// DataModel.sSelf.getInt((DMKeys.TOT_COMMUNITE));
		if (s == 1) {
			spacialcase = true;
		}

		iLoggedUserID = BusinessProxy.sSelf.getUserId();
		// mThread = new Thread(this);
		// mThread.start();
		setContentView(R.layout.kainat_community_listing);

		// DATE : 14-12-2015
		// NAME : MANOJ SINGH
		clean_search_iv_member = (ImageView) findViewById(R.id.clean_search_iv_member);
		community_search = (EditText) findViewById(R.id.community_search_edt);
		community_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				// if(feed != null && feed.entry != null && feed.entry.size() >
				// 0){
				String text = community_search.getText().toString();
				try {
					localFilter(text);
				} catch (Exception e) {

				}

				// }
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
				/*
				 * String text = communitymemeberserch.getText().toString();
				 * localFilter(text);
				 */
				/*
				 * String text = communitymemeberserch.getText().toString();
				 * localFilter(""+arg0.toString());
				 */
			}
		});
		clean_search_iv_member.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				community_search.setText("");
			}
		});

		// END

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Log.i("GCM_DATA", "Extra:" + extras.getString("USERID"));
			Log.i("GCM_DATA", "Extra:" + extras.getString("CONVERSATIONID"));
			Log.i("GCM_DATA", "Extra:" + extras.getString("lastMsgId"));
			Log.i("GCM_DATA", "Extra:" + extras.getString("transactionId"));
		}
		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerView = inflator.inflate(R.layout.comm_list_header, null);
		headerView.findViewById(R.id.view_all).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Intent intent = new Intent(
						// KainatCommunityActivity.this,
						// CommunityActivity.class);
						// intent.putExtra("TYPE", 2);

						// startActivity(intent);
						// overridePendingTransition(R.anim.slide_left_in,
						// R.anim.slide_left_out);
					}
				});

		// screenSlide(KainatCommunityActivity.this,
		// R.layout.kainat_community_listing);
		mMainList = (ListView) findViewById(R.id.community_mainList);
		mMainList.setOnScrollListener(this);
		mMainList.addHeaderView(headerView);

		community_mainList_grid = (GridView) findViewById(R.id.community_mainList_grid);
		community_mainList_grid
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// Some 1475
						// Toast.makeText(KainatCommunityActivity.this,
						// "You Clicked at " +position,
						// Toast.LENGTH_SHORT).show();
						CommunityFeed.Entry entry = feed.entry
								.get((Integer) view.getTag());
						DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",
								entry);
						Intent intent = new Intent(
								KainatCommunityActivity.this,
								CommunityChatActivity.class);
						startActivity(intent);

					}
				});
		mMediaAdapter = new CommunityAdapter(this);
		mMediaGridAdapter = new CommunityAdapterGrid(this);
		// Load Data from DB
		// if(UIActivityManager.refreshChannelList)
		fetchDataFromDbCommunity();
		if (feed != null && feed.entry != null)
			if (feed.entry.isEmpty()) {
				feed = null;
			} else {
				community_mainList_grid.setVisibility(View.VISIBLE);
				community_mainList_grid.setAdapter(mMediaGridAdapter);
				mMainList.setVisibility(View.GONE);
				mMainList.setAdapter(mMediaAdapter);
			}
		mImageLoader = ImageLoader.getInstance(this);
		mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
		mLoadingBar = (LinearLayout) findViewById(R.id.loading_linearlayout);
		String mymediaURL = DataModel.sSelf.removeString(MEDIA_URL);
		try {

			switch (SearchCommunityActivity.TAB_NAME) {
			case SearchCommunityActivity.TAB_PENDING:
				SearchCommunityActivity.TAB_NAME = SearchCommunityActivity.TAB_COMMUNITY;
				onClick(findViewById(R.id.bottom_tab_pending_request));
				break;
			default:
				if (mymediaURL != null && mymediaURL.trim().length() > 0) {
					if (mymediaURL.indexOf("myalbums?") != -1) {
						selectedTab = 7;
					}
					String[] str = mymediaURL.split(",");// http://124.153.95.131/tejas/feeds/api/list/searched/user?user=nis2",subCNT="18",suburl="http://iphone.rocketalk.com/mypage/msubscriber/4987169",title="nis2
					if (str.length >= 3) {
						mPostURL = str[0];
						if (mPostURL.indexOf("\"") != -1)
							mPostURL = mPostURL.substring(0,
									mPostURL.lastIndexOf("\""));
						iSubscriberCount = str[1].substring(
								str[1].indexOf("=\"") + 2,
								str[1].lastIndexOf("\""));
						if (str.length > 3)
							iMediaFor = str[3]
									.substring(str[3].indexOf("=\"") + 2);
						TextView textview = (TextView) findViewById(R.id.media_subcriber);
						textview.setVisibility(View.VISIBLE);
						textview.setText(iSubscriberCount + " Subscribers");
					} else if (str.length == 2) {
						mPostURL = str[0];
						if (mPostURL.lastIndexOf("\"") == mPostURL.length() - 1)
							mPostURL = mPostURL.substring(0,
									mPostURL.lastIndexOf("\""));
						if (str[1].indexOf("textbox=") != -1)
							iMediaFor = str[1].substring(str[1]
									.indexOf("textbox=") + "textbox=".length());
						else
							iMediaFor = str[1]
									.substring(str[1].indexOf("=\"") + 2);
						if (iMediaFor.lastIndexOf("\"") == iMediaFor.length() - 1)
							iMediaFor = iMediaFor.substring(0,
									iMediaFor.lastIndexOf("\"")).trim();
					} else {
						iMediaFor = getIntent().getStringExtra("TITLE");
						mPostURL = str[0];
					}
					findViewById(R.id.top_tab_my_communities).setVisibility(
							View.GONE);
					findViewById(R.id.top_tab_recommended).setVisibility(
							View.GONE);
					findViewById(R.id.top_featured_linearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.bottom_tab_mycommunities).setVisibility(
							View.INVISIBLE);
					findViewById(R.id.bottom_tab_community_search)
							.setVisibility(View.INVISIBLE);
					findViewById(R.id.bottom_tab_pending_request)
							.setVisibility(View.INVISIBLE);
					((TextView) findViewById(R.id.community_screenTitle))
							.setText(iMediaFor);
					if (feed == null)
						executeRequestData(mPostURL, iMediaFor);
				} else if (feed == null) {
					if (tCom <= 0
							|| (bundle != null && (bundle.getString("landing")
									.equals("recom")))) {
						selectedTab = 2;
						LinearLayout view = ((LinearLayout) findViewById(R.id.top_featured_linearLayout));// .setVisibility(View.GONE);
						TextView textView = (TextView) findViewById(R.id.top_tab_my_communities);
						textView.setSelected(false);
						textView.setBackgroundResource(R.drawable.mc1);
						// case R.id.top_tab_my_communities:
						// case R.id.bottom_tab_mycommunities:
						view.setSelected(false);
						// view.setBackgroundResource(R.drawable.m11);
						textView.setFocusable(true);
						selectedTab = 2;
						TextView textview = (TextView) findViewById(R.id.top_tab_recommended);
						textview.setBackgroundResource(R.drawable.mc44);
						textview.setFocusable(true);
						view.setSelected(true);

						textview = ((TextView) findViewById(R.id.top_tab_my_communities));
						textview.setFocusable(false);
						textView.setSelected(false);
						textView.setBackgroundResource(R.drawable.mc1);
						textView = (TextView) findViewById(R.id.bottom_tab_mycommunities);
						textView.setSelected(false);
						if (feed == null)
							executeRequestData(RECOMMENDED_COMMUNITY, " "
									+ getString(R.string.recommended));

					} else {
						selectedTab = 1;
						LinearLayout view = ((LinearLayout) findViewById(R.id.top_featured_linearLayout));// .setVisibility(View.GONE);
						TextView textView = (TextView) findViewById(R.id.top_tab_my_communities);
						textView.setSelected(true);
						textView.setBackgroundResource(R.drawable.mc11);
						view.setSelected(false);
						textView.setFocusable(true);
						selectedTab = 1;
						TextView textview = (TextView) findViewById(R.id.top_tab_recommended);
						textview.setBackgroundResource(R.drawable.mc4);
						textview.setFocusable(false);
						view.setSelected(false);

						textview = ((TextView) findViewById(R.id.top_tab_my_communities));
						textview.setFocusable(true);
						textView.setSelected(true);
						textView.setBackgroundResource(R.drawable.mc11);
						textView = (TextView) findViewById(R.id.bottom_tab_mycommunities);
						textView.setSelected(true);
						executeRequestData(mPostURL, " "
								+ getString(R.string.my_communities));
					}
				} else {
					mMainList.setAdapter(mMediaAdapter);
					startThumbnailThread(10);
					if (UIActivityManager.refreshChannelList) {
						mPostURL = MY_COMMUNITY;
						UIActivityManager.refreshChannelList = false;
						KainatHomeActivity.showChannelRefreshLoader();
						mThread = new Thread(feedLoader = new FeedLoader());
						mThread.start();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (getIntent().getBooleanExtra("CREATE", false))// from web view
		{
			findViewById(R.id.community_createButton).setVisibility(View.GONE);
		}
		if (getIntent().getBooleanExtra(Constants.FEATURED_COMMUNITY, false)) {
			showFeaturedcommunity(getIntent().getStringExtra(
					Constants.FEATURED_COMMUNITY_URL));
		}
		findViewById(R.id.top_featured_linearLayout).setVisibility(View.GONE);

		hideShowNoMessageView();
	}

	private void localFilter(String val) {
		String nexturl = feed.nexturl;
		fetchDataFromDbCommunity();
		final Vector<Entry> searchList = new Vector<CommunityFeed.Entry>();
		if (val.equals("")) {
			for (Entry bud : feed.entry) {
				searchList.add(bud);
			}
			mMediaGridAdapter.setNewItemList(searchList);
			// mMediaAdapter.setNewItemList(searchList);
		} else {
			for (Entry bud : feed.entry) {
				/*
				 * if
				 * (bud.groupName.trim().startsWith(val.toLowerCase().toString
				 * ()) ||
				 * bud.groupName.trim().startsWith(val.toUpperCase().toString())
				 * ||
				 * bud.displayName.trim().startsWith(val.toLowerCase().toString
				 * ()) ||
				 * bud.displayName.trim().startsWith(val.toUpperCase().toString
				 * ())) { searchList.add(bud); }
				 */
				if (bud.groupName.toLowerCase().contains(
						val.toLowerCase().toString())) {
					searchList.add(bud);
				}
			}
			mMediaGridAdapter.setNewItemList(searchList);
			// mMediaAdapter.setNewItemList(searchList);

		}
		if (searchList.isEmpty() || searchList.size() <= 0) {
			for (Entry bud : feed.entry) {
				searchList.add(bud);
			}
			mMediaGridAdapter.setNewItemList(searchList);
			// mMediaAdapter.setNewItemList(searchList);
		}
	}

	protected void onResume() {
		if (KeyValue.getBoolean(this, KeyValue.CREATE_COMMUNITY)) {
			feed = null;
			init();
		} else if (DataModel.sSelf.getRemoveBoolean(DMKeys.NEW_INTENT) == true
				|| ((feed == null || feed.entry == null || feed.entry.size() <= 0) && started)) {
			init();
		} else {
			if (UIActivityManager.refreshChannelList) {
				fetchDataFromDbCommunity();
				if (mMediaAdapter != null)
					initList();
			}
			if (UIActivityManager.refreshChannelList) {
				mPostURL = MY_COMMUNITY;
				UIActivityManager.refreshChannelList = false;
				KainatHomeActivity.showChannelRefreshLoader();
				mThread = new Thread(feedLoader = new FeedLoader());
				mThread.start();
			}
		}
		if (!SettingData.sSelf.isEmailVerified()
				&& BusinessProxy.sSelf.isRegistered()) {
			initVerificationScreen();
		}
		boolean isCommDeleted = DataModel.sSelf
				.getBoolean(DMKeys.COMMUNITY_DELETED);
		if (isCommDeleted) {
			showSimpleAlert(getString(R.string.info),
					getString(R.string.yourrequesthasbeensent));
			DataModel.sSelf.removeObject(DMKeys.COMMUNITY_DELETED);
		}
		switch (SearchCommunityActivity.TAB_NAME) {
		case SearchCommunityActivity.TAB_PENDING:
			break;
		default:
		}
		mImageLoader = ImageLoader.getInstance(KainatCommunityActivity.this);
		mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
		if (getIntent().getBooleanExtra("CREATE", false))// from web view
		{
			findViewById(R.id.community_createButton).setVisibility(View.GONE);
		}
		if (feed != null && feed.entry != null && feed.entry.size() > 0) {
			(findViewById(R.id.loading_linearlayout)).setVisibility(View.GONE);
		}
		if(feed != null)
		{
			mThread = new Thread(feedLoader = new FeedLoader());
			mThread.start();	
		}
//		UIActivityManager.refreshChannelList = true;
//		if (UIActivityManager.refreshChannelList) {
//			mPostURL = MY_COMMUNITY;
//			UIActivityManager.refreshChannelList = false;
//			KainatHomeActivity.showChannelRefreshLoader();
//			mThread = new Thread(feedLoader = new FeedLoader());
//			mThread.start();
//		}
		super.onResume();
	}

	private void disableCategoryButtons() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_1))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_2))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_3))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_4))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_5))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_6))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_7))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_8))
							.setBackgroundResource(R.drawable.darkblue);
					((Button) searchDialog
							.findViewById(R.id.media_search_cat_button_9))
							.setBackgroundResource(R.drawable.darkblue);
				} catch (Exception ex) {
					ex.printStackTrace();
					// System.out.println("Button error - " + ex.toString());
					makeToast(ex.getMessage());
				}
			}
		});
	}

	public void setSelected(View view) {
		mMainList.setVisibility(View.INVISIBLE);
		TextView textView = (TextView) findViewById(R.id.top_tab_my_communities);
		textView.setSelected(false);

		textView = (TextView) findViewById(R.id.top_tab_recommended);
		textView.setSelected(false);

		textView = (TextView) findViewById(R.id.bottom_tab_mycommunities);
		textView.setSelected(false);

		textView = (TextView) findViewById(R.id.bottom_tab_community_search);
		textView.setSelected(false);

		textView = (TextView) findViewById(R.id.bottom_tab_pending_request);
		textView.setSelected(false);

		view.setSelected(true);
		// view.setBackgroundColor(0xff5781BB);
		textView.setFocusable(true);
	}

	public void initList() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				int lastListSelectedIndex = mMainList.getFirstVisiblePosition();
				mMediaAdapter = new CommunityAdapter(
						KainatCommunityActivity.this);
				mMainList.setAdapter(mMediaAdapter);
				mMediaAdapter.notifyDataSetChanged();
				mMediaAdapter.notifyDataSetInvalidated();
				mMainList.setSelection(lastListSelectedIndex);
				mMainList.invalidate();
				// grid work needed
				mMediaGridAdapter = new CommunityAdapterGrid(
						KainatCommunityActivity.this);
				community_mainList_grid.setAdapter(mMediaGridAdapter);
				mMediaGridAdapter.notifyDataSetChanged();
				mMediaGridAdapter.notifyDataSetInvalidated();
				// mMainList.setSelection(lastListSelectedIndex);
				community_mainList_grid.invalidate();
			}
		});

	}

	private int buttonIds[] = new int[12];
	private Dialog searchDialog;
	private View loading = null;
	private boolean requestednextPage = false;

	public void onClick(final View view) {

		if (Logger.ENABLE)
			Logger.debug(TAG,
					"-----------------------selectedTab----------------------"
							+ selectedTab);
		Drawable mycommunity_unsel = this.getResources().getDrawable(
				R.drawable.mycommunities_unsel);
		Drawable pending_unsel = this.getResources().getDrawable(
				R.drawable.pending_request_unsel);
		Drawable search_unsel = this.getResources().getDrawable(
				R.drawable.search_med);

		switch (view.getId()) {
		// case R.id.home_icon:
		// if (BusinessProxy.sSelf.mCacheURLs.size() > 0)
		// BusinessProxy.sSelf.mCacheURLs.removeAllElements();
		// pushNewScreen(HomeActivity.class, false);
		// break;
		case R.id.community_search_buttton:

			searchDialog = new Dialog(this);
			searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			searchDialog.setContentView(R.layout.media_search);
			searchDialog.setCancelable(true);
			searchDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			Button button = (Button) searchDialog
					.findViewById(R.id.media_search_text);
			button.setOnClickListener(searchMediaClickListener);
			button = (Button) searchDialog
					.findViewById(R.id.media_search_voice);
			button.setOnClickListener(searchMediaClickListener);
			button = (Button) searchDialog
					.findViewById(R.id.media_search_picture);
			button.setOnClickListener(searchMediaClickListener);
			button = (Button) searchDialog
					.findViewById(R.id.media_search_video);
			button.setOnClickListener(searchMediaClickListener);
			button = (Button) searchDialog
					.findViewById(R.id.media_search_video);
			button.setOnClickListener(searchMediaClickListener);
			button = (Button) searchDialog
					.findViewById(R.id.media_cancel_button);
			button.setOnClickListener(searchMediaClickListener);
			button = (Button) searchDialog
					.findViewById(R.id.media_search_button);
			button.setOnClickListener(searchMediaClickListener);
			int ctr = 0;
			buttonIds[ctr++] = R.id.media_search_cat_button_1;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_1);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_2;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_2);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_3;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_3);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_4;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_4);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_5;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_5);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_6;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_6);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_7;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_7);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_8;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_8);
			button.setOnClickListener(searchMediaClickListener);
			buttonIds[ctr++] = R.id.media_search_cat_button_9;
			button = (Button) searchDialog
					.findViewById(R.id.media_search_cat_button_9);
			button.setOnClickListener(searchMediaClickListener);
			searchDialog.show();
			break;
		case R.id.community_createButton:
			Intent intent = new Intent(KainatCommunityActivity.this,
					CreateCommunityActivity.class);
			// startActivity(intent);

			action(idsReport, idsNameReport, (byte) 1);
			quickAction.show(view);
			break;
		case LOADING_ID:
			loadFromCache = false;
			loading = view;
			ProgressBar progressBar = ((ProgressBar) view
					.findViewById(R.id.progressbar));
			if (progressBar != null) {
				TextView textView = ((TextView) view
						.findViewById(R.id.loadingtext));
				progressBar.setVisibility(View.VISIBLE);
				if (textView.getText().toString()
						.equalsIgnoreCase(getString(R.string.load_more))) {
					requestednextPage = true;
				}
				textView.setText(getString(R.string.loading_dot));
			}
			if (requestednextPage) {
				String aURL = "";
				// String rel = Feed.getAttrRel(feed.link);
				// if (rel.equalsIgnoreCase("next")) {
				// aURL = Feed.getAttrHref(feed.link);
				// }

				aURL = CommunityFeed.getAttrRel(feed.links, "next");
				if (aURL == null || aURL.equalsIgnoreCase("NA")
						|| aURL.length() <= 0)
					aURL = feed.nexturl;
				oldFeed = feed;
				executeRequestData(aURL, mCurrentScreen);
			}
			break;
		case R.id.top_tab_my_communities:
		case R.id.bottom_tab_mycommunities:
			loadFromCache = true;
			if (view.isSelected())
				return;
			saveFeed();
			initList();
			feed = null;
			mMainList.setSelection(0);
			setSelected(view);
			DataModel.sSelf.removeObject(DMKeys.FEED_MY_ALBUM_PLUS);
			selectedTab = 1;

			TextView textview = (TextView) findViewById(R.id.bottom_tab_mycommunities);
			// textview.setBackgroundResource(R.drawable.media_tabs_selected_one);
			Drawable img = view.getContext().getResources()
					.getDrawable(R.drawable.mycommunities_sel);
			Drawable[] d = textview.getCompoundDrawables();
			Rect r = null;
			if (d[1] != null)
				r = d[1].getBounds();
			if (r != null)
				img.setBounds(r);
			textview.setCompoundDrawables(null, img, null, null);
			textview.setFocusable(true);

			textview = ((TextView) findViewById(R.id.bottom_tab_community_search));
			textview.setFocusable(false);
			// textview.setBackgroundResource(R.drawable.media_tabs_one);
			d = textview.getCompoundDrawables();
			r = null;
			if (d[1] != null)
				r = d[1].getBounds();
			if (r != null)
				search_unsel.setBounds(r);
			textview.setCompoundDrawables(null, search_unsel, null, null);

			textview = ((TextView) findViewById(R.id.bottom_tab_pending_request));
			textview.setFocusable(false);
			// textview.setBackgroundResource(R.drawable.media_tabs_one);
			d = textview.getCompoundDrawables();
			r = null;
			if (d[1] != null)
				r = d[1].getBounds();
			if (r != null)
				pending_unsel.setBounds(r);
			textview.setCompoundDrawables(null, pending_unsel, null, null);

			showTopBar();
			textview = (TextView) findViewById(R.id.top_tab_my_communities);
			textview.setBackgroundResource(R.drawable.mc11);
			textview.setFocusable(true);
			textview = ((TextView) findViewById(R.id.top_tab_recommended));
			textview.setFocusable(false);
			textview.setBackgroundResource(R.drawable.mc4);

			if (tCom <= 0 && view.getId() == R.id.bottom_tab_mycommunities) {
				selectedTab = 2;
				LinearLayout view2 = ((LinearLayout) findViewById(R.id.top_featured_linearLayout));// .setVisibility(View.GONE);
				TextView textView = (TextView) findViewById(R.id.top_tab_my_communities);
				textView.setSelected(false);
				textview.setBackgroundResource(R.drawable.mc1);
				// case R.id.top_tab_my_communities:
				// case R.id.bottom_tab_mycommunities:
				view2.setSelected(false);
				// view2.setBackgroundResource(R.drawable.m1);
				textView.setFocusable(true);
				selectedTab = 2;
				textview = (TextView) findViewById(R.id.top_tab_recommended);
				textview.setBackgroundResource(R.drawable.mc44);
				textview.setFocusable(true);
				view2.setSelected(true);

				textview = ((TextView) findViewById(R.id.top_tab_my_communities));
				textview.setFocusable(false);
				textView.setSelected(false);
				textView = (TextView) findViewById(R.id.bottom_tab_mycommunities);
				textView.setSelected(false);
				// textview.setBackgroundColor(0xff9baabf);
				executeRequestData(RECOMMENDED_COMMUNITY,
						getString(R.string.recommended));
				return;
			} else if (tCom <= 0 && view.getId() == R.id.top_tab_my_communities) {
				lodingCanceled = true;
				selectedTab = 1;
				//
				runOnUiThread(new Runnable() {
					public void run() {
						(findViewById(R.id.loading_linearlayout))
								.setVisibility(View.GONE);
						mLoadingBar.setVisibility(View.GONE);

						(findViewById(R.id.community_mainList))
								.setVisibility(View.GONE);
						(findViewById(R.id.loading_linearlayout))
								.setVisibility(View.GONE);
						((TextView) findViewById(R.id.community_noContent))
								.setText(getString(R.string.you_have_not_joined_any_communities));
						(findViewById(R.id.loading_linearlayoutk))
								.setVisibility(View.VISIBLE);

					}
				});

				/*
				 * (
				 * findViewById(R.id.loading_linearlayout)).setVisibility(View.
				 * GONE);
				 * (findViewById(R.id.loading_linearlayoutk)).setVisibility
				 * (View.VISIBLE);
				 * ((TextView)findViewById(R.id.community_noContent
				 * )).setText("You have not joined any communities!");
				 */
				TextView title = ((TextView) findViewById(R.id.community_screenTitle));
				mCurrentScreen = getString(R.string.my_communities);
				title.setText(mCurrentScreen);
			} else
				executeRequestData(MY_COMMUNITY,
						getString(R.string.my_communities));
			break;
		case R.id.community_discover:
			intent = new Intent(KainatCommunityActivity.this,
					KainatDiscoverActivity.class);
			intent.putExtra("INVISIBLE_MENU", true);
			startActivity(intent);
			break;
		case R.id.bottom_tab_community_search:

			DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
			super.pushNewScreen(SearchCommunityActivity.class, false);// Toast.makeText(CommunityActivity.this,
			// "Search Button Clicked...",
			// Toast.LENGTH_LONG).show();
			break;
		case R.id.bottom_tab_pending_request:
			if (view.isSelected())
				return;
			feed = null;
			setSelected(view);
			DataModel.sSelf.removeObject(DMKeys.FEED_MY_ALBUM_PLUS);
			selectedTab = 4;
			myScreenName("GRP_PNDLST");
			// BusinessProxy.sSelf.recordScreenStatistics("GRP_PNDLST", true,
			// true);// Add
			// Record
			textview = (TextView) findViewById(R.id.bottom_tab_mycommunities);
			textview.setFocusable(false);
			// textview.setBackgroundResource(R.drawable.media_tabs_one);
			d = textview.getCompoundDrawables();
			r = null;
			if (d[1] != null)
				r = d[1].getBounds();
			if (r != null)
				mycommunity_unsel.setBounds(r);
			textview.setCompoundDrawables(null, mycommunity_unsel, null, null);

			textview = ((TextView) findViewById(R.id.bottom_tab_community_search));
			textview.setFocusable(false);
			// textview.setBackgroundResource(R.drawable.media_tabs_one);
			d = textview.getCompoundDrawables();
			r = null;
			if (d[1] != null)
				r = d[1].getBounds();
			if (r != null)
				search_unsel.setBounds(r);
			textview.setCompoundDrawables(null, search_unsel, null, null);

			textview = ((TextView) findViewById(R.id.bottom_tab_pending_request));
			// textview.setBackgroundResource(R.drawable.media_tabs_selected_one);
			img = view.getContext().getResources()
					.getDrawable(R.drawable.pending_request_sel);
			d = textview.getCompoundDrawables();
			r = null;
			if (d[1] != null)
				r = d[1].getBounds();
			if (r != null)
				img.setBounds(r);
			textview.setCompoundDrawables(null, img, null, null);
			textview.setFocusable(true);

			hideTopBar();
			executeRequestData(PENDING_INVITES,
					getString(R.string.pending_invites));
			break;
		case R.id.top_tab_recommended:
			if (view.isSelected())
				return;
			loadFromCache = true;
			saveFeed();
			initList();
			mMainList.setSelection(0);
			feed = null;
			setSelected(view);
			DataModel.sSelf.removeObject(DMKeys.FEED_MY_ALBUM_PLUS);
			selectedTab = 2;
			textview = (TextView) findViewById(R.id.top_tab_recommended);
			textview.setBackgroundResource(R.drawable.mc44);
			textview.setFocusable(true);
			textview = ((TextView) findViewById(R.id.top_tab_my_communities));
			textview.setFocusable(false);
			textview.setBackgroundResource(R.drawable.mc1);
			executeRequestData(RECOMMENDED_COMMUNITY,
					getString(R.string.recommended));
			break;
		case R.community_list_row.communityImage:
			CommunityFeed.Entry entry = feed.entry.get((Integer) view.getTag());
			DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
			intent = new Intent(KainatCommunityActivity.this,
					CommunityProfileNewActivity.class);
			intent.putExtra("group_name", entry.groupName);
			intent.putExtra("tags_name", entry.tags);
			intent.putExtra("group_id", entry.groupId);
			intent.putExtra("group_desc", entry.description);
			intent.putExtra("group_pic", entry.thumbUrl);
			startActivity(intent);
			break;
		case R.community_list_row.communityInfoLayout:
		case R.community_list_row.moreButton:
			try {
				if (selectedTab == 1) {
					entry = feed.entry.get((Integer) view.getTag());
					if (entry.memberPermission.equalsIgnoreCase("user")) {
						// Called when Not followed
						DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",
								entry);
						intent = new Intent(KainatCommunityActivity.this,
								CommunityProfileNewActivity.class);
						intent.putExtra("group_name", entry.groupName);
						intent.putExtra("tags_name", entry.tags);
						intent.putExtra("group_id", entry.groupId);
						intent.putExtra("group_desc", entry.description);
						intent.putExtra("group_pic", entry.thumbUrl);
						startActivity(intent);
					} else {
						// Goto Chat Activity
						if (entry.browseMsgUrl != null
								&& entry.browseMsgUrl.length() > 5) {
							// startActivity(intent);
						} else if (entry.messageUrl != null
								&& entry.messageUrl.length() > 5) {
							DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",
									entry);
							intent = new Intent(KainatCommunityActivity.this,
									CommunityChatActivity.class);
							startActivity(intent);
						}
					}
				} else if (selectedTab == 2) {
					if (view.isSelected()) {
						return;
					}
					saveFeed();
					setSelected(view);
					DataModel.sSelf.removeObject(DMKeys.FEED_MY_ALBUM_PLUS);
					// findViewById(R.id.home_icon).setVisibility(View.GONE);
					findViewById(R.id.community_createButton).setVisibility(
							View.GONE);
					// findViewById(R.id.community_backButton).setVisibility(View.GONE);
					findViewById(R.id.top_featured_linearLayout).setVisibility(
							View.GONE);
					// findViewById(R.id.bottom_featured_linearLayout).setVisibility(View.GONE);
					findViewById(R.id.bottom_tab_mycommunities).setVisibility(
							View.INVISIBLE);
					findViewById(R.id.bottom_tab_community_search)
							.setVisibility(View.INVISIBLE);
					findViewById(R.id.bottom_tab_pending_request)
							.setVisibility(View.INVISIBLE);
					backPressed = false;
					entry = feed.entry.get((Integer) view.getTag());
					feed = null;
					selectedTab = 3;
					executeRequestData(entry.url, entry.displayName);

				} else if (selectedTab == 3) {
					entry = feed.entry.get((Integer) view.getTag());
					if (entry.memberPermission.equalsIgnoreCase("user")) {
						// Goto Open WebViewActivity
						// DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
						// entry.groupName);
						// DataModel.sSelf.storeObject(DMKeys.XHTML_URL,
						// entry.profileUrl);
						// intent = new Intent(KainatCommunityActivity.this,
						// WebviewActivity.class);
						// intent.putExtra("from_community", true);
						// startActivity(intent);
					} else {
						// Goto Chat Activity
						if (entry.browseMsgUrl != null
								&& entry.browseMsgUrl.length() > 5) {
							// DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
							// entry.groupName);
							// DataModel.sSelf.storeObject(DMKeys.XHTML_URL,
							// entry.browseMsgUrl);
							// intent = new Intent(KainatCommunityActivity.this,
							// WebviewActivity.class);
							// // intent.putExtra("from_community", true);
							// startActivity(intent);
						} else if (entry.messageUrl != null
								&& entry.messageUrl.length() > 5) {
							DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",
									entry);
							intent = new Intent(KainatCommunityActivity.this,
									CommunityChatActivity.class);
							startActivity(intent);
						}
					}
				} else if (selectedTab == 4) {
					// Open Selected profile
					// CommunityFeed.Entry entry = feed.entry.get((Integer) view
					// .getTag());
					// DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
					// getString(R.string.app_name));
					// DataModel.sSelf.storeObject(DMKeys.XHTML_URL,
					// entry.profileUrl);
					// intent = new Intent(this, WebviewActivity.class);
					// intent.putExtra("PAGE", (byte) 1);
					// startActivity(intent);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		// case R.id.community_backButton:
		// if (view.isSelected())
		// return;
		// //setSelected(view);
		// if (selectedTab == 3) {
		// onClickTab3();
		// }
		// break;
		// case R.id.community_acceptButton:
		// case R.id.community_declineButton:
		// case R.id.community_ignoreButton:
		// String tag = (String) view.getTag();
		// if (tag == null || tag.trim().equals("")) {
		// return;
		// }
		// String[] vals = tag.split("~~");
		// tag = vals[0];
		// int pos = Integer.parseInt(vals[1]);
		// if (BusinessProxy.sSelf.sendNewTextMessage(
		// tag.substring(tag.indexOf(":") + 1, tag.indexOf("?")),
		// tag.substring(tag.indexOf("=") + 1), true)) {
		// showSimpleAlert(getString(R.string.info),
		// getString(R.string.yourrequesthasbeensent));
		// }
		// feed.entry.removeElementAt(pos);
		// mMediaAdapter.notifyDataSetChanged();
		// mMediaAdapter.notifyDataSetInvalidated();
		// hideShowNoMessageView();
		// break;
		}
	}

	private void onClickTab3() {
		feed = null;
		// findViewById(R.id.home_icon).setVisibility(View.GONE);
		findViewById(R.id.community_createButton).setVisibility(View.VISIBLE);
		// findViewById(R.id.community_backButton).setVisibility(View.GONE);
		findViewById(R.id.top_featured_linearLayout)
				.setVisibility(View.VISIBLE);
		// findViewById(R.id.bottom_featured_linearLayout).setVisibility(View.VISIBLE);
		findViewById(R.id.bottom_tab_mycommunities).setVisibility(View.VISIBLE);
		findViewById(R.id.bottom_tab_community_search).setVisibility(
				View.VISIBLE);
		findViewById(R.id.bottom_tab_pending_request).setVisibility(
				View.VISIBLE);

		selectedTab = 2;
		executeRequestData(RECOMMENDED_COMMUNITY,
				getString(R.string.recommended));
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
						ActionItem actionItem = quickAction.getActionItem(pos);
						if (type == 1) {// report
							// Toast.makeText(getApplicationContext(), actionId
							// + " : REPORT : " + pos,
							// Toast.LENGTH_SHORT).show();
							if (actionId == 1) {// delete
								DataModel.sSelf.storeObject(DMKeys.ENTRY, null);
								Intent intent = new Intent(
										KainatCommunityActivity.this,
										CreateCommunityActivity.class);
								startActivity(intent);

							} else if (actionId == 2) {

							} else if (actionId == 3) {

							} else if (actionId == 4) {

							}
						}
					}

				});
	}

	public void sendRTMLRequest(String aDestination, String aCommand,
			String aAlert) {
		try {
			OutboxTblObject newRequest = new OutboxTblObject(1,
					Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_REPLY);
			newRequest.mPayloadData.mText[0] = aCommand.getBytes(Constants.ENC);
			newRequest.mPayloadData.mTextType[0] = Constants.TEXT_TXT;
			newRequest.mPayloadData.mPayloadTypeBitmap |= Constants.TEXT_PAYLOAD_BITMAP;
			newRequest.mDestContacts = new String[] { aDestination };
			int ret = BusinessProxy.sSelf.sendToTransport(newRequest);
			if (ret == Constants.ERROR_NONE) {
				if (aAlert != null)
					showSimpleAlert("Information", (aAlert != null) ? aAlert
							: getString(R.string.yourrequesthasbeensent));
			} else if (ret == Constants.ERROR_OUTQUEUE_FULL) {
				showSimpleAlert(getString(R.string.info),
						"Your outbox is full. Please try after sometime!");
			} else {
				showSimpleAlert(getString(R.string.info), "Network busy!");
			}

		} catch (Exception ex) {
			showSimpleAlert(getString(R.string.info), "Action Failed!");
		}
	}

	public void onTaskCallback(Object parameter, byte req) {
		if (lodingCanceled)
			return;
		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case HTTP_TIMEOUT:
			// if (mProgressDialog != null && mProgressDialog.isShowing()) {
			// mProgressDialog.dismiss();
			// }
			cancelLoading();
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
				String stringResponce = new String(mResponseData);
				if (stringResponce
						.contains("<totalGroupCount>0</totalGroupCount>")/*
																		 * &&
																		 * feed
																		 * !=
																		 * null
																		 * &&
																		 * feed
																		 * .
																		 * entry
																		 * !=
																		 * null
																		 * &&
																		 * feed
																		 * .
																		 * entry
																		 * .
																		 * size(
																		 * ) <=
																		 * 0
																		 */) {
					KainatCommunityActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							//
							((ImageView) findViewById(R.id.defalut_channel_iv))
									.setVisibility(View.VISIBLE);
							((Button) findViewById(R.id.community_discover))
									.setVisibility(View.GONE);
							((Button) findViewById(R.id.community_discover))
									.setOnClickListener(KainatCommunityActivity.this);
							if (UIActivityManager.showDiscoverCounter == 0) {
								KainatHomeActivity parentActivity;
								parentActivity = (KainatHomeActivity) getParent();
								parentActivity.setTab(UIActivityManager.TAB_CHAT);
								UIActivityManager.showDiscoverCounter = 1;
							}
							// KainatHomeActivity.setTab(UIActivityManager.TAB_CHAT);
						}
					});
				} else {
					//Delete All Community Data
					FeedTask.deleteAll();
					parseXMLData(mCurrentScreen);
					KainatCommunityActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							((ImageView) findViewById(R.id.defalut_channel_iv))
									.setVisibility(View.GONE);
							((Button) findViewById(R.id.community_discover))
									.setVisibility(View.GONE);
						}
					});
				}
				cancelLoading();
				resetLoading();
				mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);// buy
				// n
				if (resultCode == ACTION_BACK_SPECIFIED_USER_LIST) {
					hideHF();
				}
			} else {
				cancelLoading();
				if (mCurrentScreen
						.indexOf("BollywoodCricket & SportsHollywoodNature-TravelRockeTalk StarJokesPeopleOthersSportsMy Public AlbumMyPrivate AlbumFor Friends Only") != -1) {
				}
				feed = oldFeed;
				resetLoading();
				showSimpleAlert("Error:", "Unable to retrieve\n"
						+ mCurrentScreen);
			}
			break;
		case ERROR_CALLBACK:
			mIsRunning = false;
			cancelLoading();
			resetLoading();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					moveOnError();
					/*
					 * Toast.makeText( KainatCommunityActivity.this,
					 * getString(R.string.unable_to_connect_pls_try_later),
					 * Toast.LENGTH_SHORT).show();
					 */
				}
			});
			break;
		}
	}

	public void resetLoading() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (loading != null) {
					ProgressBar progressBar = ((ProgressBar) loading
							.findViewById(R.id.progressbar));
					progressBar.setVisibility(View.GONE);
					TextView textView = ((TextView) loading
							.findViewById(R.id.loadingtext));
					textView.setText(getString(R.string.load_more));
				}

			}
		});
	}

	public void cancelLoading() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mLoadingBar.isShown()) {
					if (mMainList != null) {
						// mMainList.setVisibility(View.VISIBLE);
					}
					mLoadingBar.setVisibility(View.GONE);
				}
			}
		});
	}

	private String aTitle = "";
	private boolean spacialcase = false;
	// KeyValue.setString(this, KeyValue.INTEREST, buffer.toString()) ;
	private boolean hitInterest = false;

	private int firstVisiblePosition;

	private void parseXMLData(final String aTitle) {
		if (backPressed) {
			backPressed = false;
			return;
		}
		this.aTitle = aTitle;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			CommunityFeedParser myXMLHandler = new CommunityFeedParser();
			xr.setContentHandler(myXMLHandler);
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
					mResponseData);
			Log.w(TAG, "parseXMLData : Community List Feed => "+new String(mResponseData));
			mResponseData = null;
			xr.parse(new InputSource(arrayInputStream));
			if (feedLoader.lodingCanceled)
				return;
			if (feed != null) {
				// Clear all the feed
				feed.entry.clear();
				for (int i = 0; i < myXMLHandler.feed.entry.size(); i++) {
					feed.entry.add(myXMLHandler.feed.entry.elementAt(i));
				}
				feed.nexturl = myXMLHandler.feed.nexturl;
				feed.link = myXMLHandler.feed.link;
				feed.links = myXMLHandler.feed.links;
				if (myXMLHandler.feed.topMessageId != null
						&& myXMLHandler.feed.topMessageId.trim().length() > 0) {
					if (FeedRequester.lastChannelRefreshMessageID != null
							&& FeedRequester.lastChannelRefreshMessageID
									.equals("1"))
						FeedRequester.lastChannelRefreshMessageID = myXMLHandler.feed.topMessageId;
					// Update sharedPreference for Channel Top Message ID
					if (FeedRequester.lastChannelRefreshMessageID != null) {
						Utilities.setString(this, Constants.CH_TOP_ID,
								FeedRequester.lastChannelRefreshMessageID);
						Log.i(TAG,
								"parseXMLData :: set lastChannelRefreshMessageID in Shared Pref : "
										+ FeedRequester.lastChannelRefreshMessageID);
					}
				}
				if (mMainList != null) {
					firstVisiblePosition = mMainList.getFirstVisiblePosition();
					Log.w(TAG, "parseXMLData :: First visible position : "
							+ firstVisiblePosition);
				} else
					firstVisiblePosition = 0;
//				FeedTask ft = new FeedTask(KainatCommunityActivity.this);
				FeedTask.deleteAll();
				FeedTask.saveCommunity(feed);
				
				fetchDataFromDbCommunity();
				initList();
				// if(mMediaAdapter != null)
				// {
				// runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// mMediaAdapter.notifyDataSetChanged();
				// mMediaAdapter.notifyDataSetInvalidated();
				// mMainList.setTop(0);
				// mMainList.invalidate();
				// }
				// });
				// }
			} else {
				if(feed != null && feed.entry.size() > 0)
					feed.entry.clear();
				feed = myXMLHandler.feed;
				if (feed.topMessageId != null
						&& feed.topMessageId.trim().length() > 0) {
					if (FeedRequester.lastChannelRefreshMessageID != null
							&& FeedRequester.lastChannelRefreshMessageID
									.equals("1"))
						FeedRequester.lastChannelRefreshMessageID = feed.topMessageId;
					// Update sharedPreference for Channel Top Message ID
					if (FeedRequester.lastChannelRefreshMessageID != null) {
						Utilities.setString(this, Constants.CH_TOP_ID,
								FeedRequester.lastChannelRefreshMessageID);
						Log.i(TAG,
								"parseXMLData :: set lastChannelRefreshMessageID in Shared Pref : "
										+ FeedRequester.lastChannelRefreshMessageID);
					}
				}
				FeedTask ft = new FeedTask(KainatCommunityActivity.this);
				ft.saveCommunity(feed);
				fetchDataFromDbCommunity();
				if (mMediaAdapter != null)
					initList();
			}
			if (hitInterest) {
				Utilities.setBoolean(this, Constants.NO_CHANNLE_JOINED, true);
				hitInterest = false;
			}
			// //Start Channel refresh here.
			startChannelRefresh = true;
			// FeedRequester.requestChannelRefresh(this);
			// Hide Channel Refresh loader on KainatHomeActivity
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					KainatHomeActivity.hideChannelRefreshLoader();

				}
			});

			// Commented as, no need for stats for now
			// BusinessProxy.sSelf.recordScreenStatistics(Feed.getAttrCode(feed.tracking),
			// true, true);//Add Record
			requestednextPage = false;
			if (resultCode != ACTION_BACK_SPECIFIED_USER_LIST) {
			}
			if (feed.entry.size() <= 0) {
				if (hitInterest) {
					runOnUiThread(new Runnable() {
						public void run() {

							(findViewById(R.id.loading_linearlayout))
									.setVisibility(View.GONE);
							(findViewById(R.id.loading_linearlayoutk))
									.setVisibility(View.VISIBLE);
							if (selectedTab == 4)
								((TextView) findViewById(R.id.community_noContent))
										.setText(getString(R.string.there_are_no_pending_requests));
							else if (selectedTab == 1)
								((TextView) findViewById(R.id.community_noContent))
										.setText(getString(R.string.you_have_not_joined_any_communities));
							else
								((TextView) findViewById(R.id.community_noContent))
										.setText("No Entry!");
						}
					});
				} else {
					// runOnUiThread(new Runnable() {
					// //http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/searched/advance?category=Spirituality&app-key=15769184
					// @Override
					// public void run() {
					// mCurrentScreen = aMedia;
					// if (mLoadingBar != null)
					// mLoadingBar.setVisibility(View.VISIBLE);
					// if(1==0)
					// if (UIActivityManager.selectedCategoryName == null) {
					// mPostURL = "http://"
					// + Urls.TEJAS_HOST
					// +
					// "/tejas/feeds/api/group/searched/advance?category=Popular&limit="+COMM_COUNT;
					// } else
					// mPostURL = "http://"
					// + Urls.TEJAS_HOST
					// + "/tejas/feeds/api/group/searched/advance?category="
					// + UIActivityManager.selectedCategoryName +
					// "&limit="+COMM_COUNT;
					// mIsRunning = true;
					// mResponseData = null;
					// if (mMediaAdapter != null) {
					// mMyCommunities = false;
					// mMediaAdapter.setMyCommunities(false);
					// }
					// if (feedLoader != null)
					// feedLoader.lodingCanceled = true;
					//
					// mThread = new Thread(feedLoader = new FeedLoader());
					// mThread.start();
					// hitInterest = true;
					// }
					// });
					// return;
				}
				return;
			} else {
				// hitInterest=true ;
				runOnUiThread(new Runnable() {
					public void run() {
						(findViewById(R.id.loading_linearlayoutk))
								.setVisibility(View.GONE);
					}
				});
			}
			runOnUiThread(new Runnable() {
				public void run() {
					if (!hitInterest)
						mMainList.removeHeaderView(headerView);
					hideShowNoMessageView();
					// int lastPos = mMainList.getLastVisiblePosition() - 1;
					mMainList.setAdapter(mMediaAdapter);

					// if (prevSelected != selectedTab)
					// mMainList.setSelection(0);
					// else
					// mMainList.setSelection(lastPos);

					mMainList.setSelection(firstVisiblePosition);

					// if(firstVisiblePosition > 0)
					// mMainList.setSelection(firstVisiblePosition);
					// else
					// mMainList.setSelection(mMainList.getFirstVisiblePosition());

					prevSelected = selectedTab;

					if (null != feed && null != feed.entry) {
						if (feed.entry.size() > 10) {
							startThumbnailThread(10);
						} else {
							startThumbnailThread(feed.entry.size());
						}
					}
					if (aTitle != null && aTitle.trim().length() > 0)
						((TextView) findViewById(R.id.community_screenTitle))
								.setText(aTitle);
					if (spacialcase) {
						mMainList.setVisibility(View.GONE);
					}
				}
			});

			// new Thread(new Runnable() {
			// public void run() {
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// if (null != feed && null != feed.entry) {
			// if (feed.entry.size() > 10) {
			// startThumbnailThread(10);
			// } else {
			// startThumbnailThread(feed.entry.size());
			// }
			// }
			// }
			// }).start();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error(TAG, "parseXMLData - " + ex.getMessage(), ex);
			}
			resetLoading();
		}
	}

	public void openChatFromPush(String commUrl, String groupID,
			String groupName, String ownerName) {
		CommunityFeed.Entry entry = new CommunityFeed.Entry();
		if (entry != null) {
			entry.messageUrl = commUrl;
			entry.messageText = commUrl;
			entry.groupName = groupName;
			// entry.ownerUsername = ownerName;
			CommunityChatDB CCD = new CommunityChatDB(this);
			Entry localEntry = CCD.getCommunityDetail(groupID);
			if (localEntry != null) {
				entry.ownerUsername = localEntry.ownerUsername;
			}
			entry.groupId = Integer.parseInt(groupID);
			// entry.nexturl =
			// "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/message/"+groupID+"?textMode=base64&iThumbFormat=300x300&vThumbFormat=300x300&iFormat=300x300&app-key=15769260&pg=2";
			UIActivityManager.fromPushNot = 2;
			DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);

			Cursor communityCursor = BusinessProxy.sSelf.sqlDB.query(
					CommunityTable.TABLE, null, CommunityTable.GROUP_NAME
							+ " = ? ", new String[] { "" + groupName }, null,
					null, null);
			if (communityCursor.getCount() == 1) {
				communityCursor.moveToFirst();
				CommunityFeed.Entry communityEntry = CursorToObject
						.communityEntryObject(communityCursor);
				entry.thumbUrl = communityEntry.thumbUrl;
				communityCursor.close();
			}
		}
		Intent intent = new Intent(KainatCommunityActivity.this,
				CommunityChatActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	private Drawable LoadImageFromWebOperations(String url) {
		return null;

		// try {
		// InputStream is = (InputStream) new URL(url).getContent();
		// Drawable d = Drawable.createFromStream(is, "src name");
		// return d;
		// } catch (Exception e) {
		// e.printStackTrace();
		// makeToast(e.toString());
		// return null;
		// } catch (OutOfMemoryError e) {
		// makeToast(e.toString());
		// return null;
		// }
	}

	private void notifyThread() {
		synchronized (this) {
			notify();
		}
	}

	String aURL, aMedia;

	public void executeRequestData(String aURL, String aMedia) {
		this.aURL = aURL;
		this.aMedia = aMedia;
		if (Logger.ENABLE)
			Logger.debug(TAG,
					"----------------community selectedTab-----------------"
							+ selectedTab);
		(findViewById(R.id.loading_linearlayoutk)).setVisibility(View.GONE);

		if (!refreshRequest && getFeed()) {
			mCurrentScreen = aMedia;
			(findViewById(R.id.loading_linearlayout)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.community_screenTitle))
					.setText(mCurrentScreen);
			return;
		}
		lodingCanceled = false;
		if (aURL == null || aURL.trim().length() <= 0)
			return;
		if (aMedia != null)
			((TextView) findViewById(R.id.community_screenTitle))
					.setText(aMedia);
		if (mCurrentScreen != aMedia
				|| aMedia.startsWith("Media Search Result")
				|| aMedia.startsWith("Media+") || refreshRequest
				|| !hitInterest) {
			// showLoggingDialog(aMedia);
			if (mMainList != null)
				mMainList.setVisibility(View.GONE);
			mLoadingBar.setVisibility(View.VISIBLE);
			(findViewById(R.id.loading_linearlayout))
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.loading_text))
					.setText(getString(R.string.please_wait_while_loadin));// +
			// aMedia
			// +
			// "..");
		}
		mCurrentScreen = aMedia;
		mPostURL = aURL;
		mIsRunning = true;
		mResponseData = null;
		if (mPostURL.contains("/userpublic")) {
			mMyCommunities = true;
			if (mMediaAdapter != null)
				mMediaAdapter.setMyCommunities(mMyCommunities);
		} else
			mMyCommunities = false;

		// notifyThread();
		// mThread = new Thread(this);
		// mThread.start();

		if (feedLoader != null)
			feedLoader.lodingCanceled = true;
		// requestedTime = System.currentTimeMillis() ;
		// if (UIActivityManager.SuffelNeed == 2)
		{
			mThread = new Thread(feedLoader = new FeedLoader());
			mThread.start();
			// UIActivityManager.SuffelNeed = 1;
		}
	}

	FeedLoader feedLoader = null;// new FeedLoader() ;
	private HttpConnectionHelper helper = null;
	private int responseCode;

	class FeedLoader implements Runnable {
		public boolean lodingCanceled = false;

		@Override
		public void run() {
			// while (mIsRunning)
			{
				try {

					// synchronized (this) {
					// wait();
					// }
					// BusinessProxy.sSelf.writeLogsToFile(TAG
					// +":FeedLoader - Entry run loop");
					cancelRequest = false;
					if (null != mPostURL && 0 < mPostURL.trim().length()) {
						if (mPostURL.indexOf("?") != -1)
							mPostURL = mPostURL + "&" + i_newThumbFormat;
						else
							mPostURL = mPostURL + "?" + i_newThumbFormat;
						mPostURL = mPostURL + "&textMode=base64&limit="
								+ COMM_COUNT;
						helper = new HttpConnectionHelper(mPostURL);
						helper.setHeader("RT-APP-KEY", "" + iLoggedUserID);
						helper.setHeader("RT-DEV-KEY",
								"" + BusinessProxy.sSelf.getClientId());
						mCallBackTimer = new Timer();
						responseCode = helper.getResponseCode();
						// BusinessProxy.sSelf.writeLogsToFile(TAG
						// +" HTTP responseCode  "+responseCode);
						switch (responseCode) {
						case HttpURLConnection.HTTP_OK:
							String contentEncoding = helper
									.getHttpHeader("Content-Encoding");
							InputStream inputStream = null;
							if (null == contentEncoding) {
								inputStream = helper.getInputStream();
							} else if (contentEncoding.equals("gzip")) {
								inputStream = new GZIPInputStream(
										helper.getInputStream());
							}
							ByteArrayBuffer buffer = new ByteArrayBuffer(
									CHUNK_LENGTH);
							byte[] chunk = new byte[CHUNK_LENGTH];
							int len;
							while (!cancelRequest
									&& -1 != (len = inputStream.read(chunk))) {
								buffer.append(chunk, 0, len);
							}
							KainatCommunityActivity.this.mResponseData = buffer
									.toByteArray();
							if (cancelRequest)
								return;
							mCallBackTimer = new Timer();
							mCallBackTimer.schedule(new OtsSchedularTask(
									KainatCommunityActivity.this,
									DATA_CALLBACK, (byte) 0), 0);
							break;
						default:
							cancelLoading();
							mCallBackTimer = new Timer();
							mCallBackTimer.schedule(new OtsSchedularTask(
									KainatCommunityActivity.this,
									ERROR_CALLBACK, (byte) 0), 0);
							break;
						}
					}
				} catch (Exception ex) {
					mCallBackTimer = new Timer();
					mCallBackTimer.schedule(new OtsSchedularTask(
							KainatCommunityActivity.this, ERROR_CALLBACK,
							(byte) 0), 0);
				}
			}
		}
		/*
		 * public void run() { // while (mIsRunning) { try {
		 * 
		 * // synchronized (this) { // wait(); // } cancelRequest = false; if
		 * (null != mPostURL && 0 < mPostURL.trim().length()) { helper = new
		 * HttpConnectionHelper(mPostURL); helper.setHeader("RT-APP-KEY", "" +
		 * iLoggedUserID); helper.setHeader("RT-DEV-KEY", "" +
		 * BusinessProxy.sSelf.getClientId()); mCallBackTimer = new Timer();
		 * responseCode = helper.getResponseCode(); switch (responseCode) { case
		 * HttpURLConnection.HTTP_OK: String contentEncoding =
		 * helper.getHttpHeader("Content-Encoding"); InputStream inputStream =
		 * null; if (null == contentEncoding) { inputStream =
		 * helper.getInputStream(); } else if (contentEncoding.equals("gzip")) {
		 * inputStream = new GZIPInputStream(helper.getInputStream()); }
		 * ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH); byte[]
		 * chunk = new byte[CHUNK_LENGTH]; int len; while (!cancelRequest && -1
		 * != (len = inputStream.read(chunk))) { buffer.append(chunk, 0, len); }
		 * this.mResponseData = buffer.toByteArray(); break; default: // if
		 * (mProgressDialog.isShowing()) { // mProgressDialog.dismiss(); // }
		 * cancelLoading(); break; } if(cancelRequest)return ;
		 * mCallBackTimer.cancel(); mCallBackTimer = new Timer();
		 * mCallBackTimer.schedule(new OtsSchedularTask(this, DATA_CALLBACK),
		 * 0); } } catch (Exception ex) { mCallBackTimer = new Timer();
		 * mCallBackTimer.schedule(new OtsSchedularTask(this, ERROR_CALLBACK),
		 * 0); } }
		 */
	}

	public byte[] inputStreamToBarray(InputStream inputStream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[100];
		try {
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		} catch (IOException e) {
			makeToast(e.getMessage());
			e.printStackTrace();
		}
		try {
			buffer.flush();
		} catch (IOException e) {
			makeToast(e.getMessage());
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	public void onThumbnailResponse(final ThumbnailImage value, byte[] data) {
		runOnUiThread(new Runnable() {
			public void run() {
				int index = mMainList.getFirstVisiblePosition();
				index = value.mIndex - index;
				View view = mMainList.getChildAt(index);
				if (null == view)
					return;
				ImageView img = (ImageView) view
						.findViewById(R.community_list_row.communityImage);
				if (null != img) {
					img.setVisibility(View.VISIBLE);
					img.setImageBitmap(value.mBitmap);
				}
				if (mMainList != null) {
					// mMainList.invalidateViews();
					mMediaAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	public void saveFeed() {
		loadFromCache = true;
		if (Logger.ENABLE)
			Logger.debug(TAG,
					"-------------------save feed------------------------------");
		prevSelectedTab = selectedTab;
		// if(myAlbum || selectedTab == 8)return;
		// if(1==1)return ;
		/*
		 * if(selectedTab==1 ||selectedTab==5){
		 * FeedManager.storeFeed(FeedManager.MEDIA_FEATURED, feed); }else
		 * if(selectedTab==6){ FeedManager.storeFeed(FeedManager.MEDIA_FEATURED,
		 * feed); }
		 */

		if (selectedTab == 1) {
			FeedManager.storeFeedCom(FeedManager.MY_COMMUNITY, feed);
		} else if (selectedTab == 2) {
			FeedManager.storeFeedCom(FeedManager.RECOMENDED_COMMUNITY, feed);
		}
	}

	boolean loadFromCache = true;

	public boolean getFeed() {
		if (!loadFromCache)
			return false;
		if (Logger.ENABLE)
			Logger.debug(TAG, "-------------------get feed");
		if (selectedTab > 2)
			return false;
		if (selectedTab == 1) {
			feed = FeedManager.getFeedCom(FeedManager.MY_COMMUNITY);// , feed);
		} else if (selectedTab == 2) {
			feed = FeedManager.getFeedCom(FeedManager.RECOMENDED_COMMUNITY);// ,
			// feed);
		}
		if (feed != null) {
			reStore();
		}

		if (feed != null)
			return true;
		else
			return false;
	}

	private void reStore() {
		this.aTitle = aTitle;
		try {

			// BusinessProxy.sSelf.recordScreenStatistics(Feed.getAttrCode(feed.tracking),
			// true, true);// Add Record
			if ((feed != null && feed.waterMark != null && feed.waterMark
					.length() > 10)
					|| feed != null
					&& feed.waterMarkWide != null
					&& feed.waterMarkWide.length() > 10) {
				runOnUiThread(new Runnable() {
					public void run() {
						Display display = getWindowManager()
								.getDefaultDisplay();
						int height = display.getHeight();
						Drawable drawable = null;
						if (height <= 240) {
							if (feed.waterMark != null
									&& feed.waterMark.length() > 10) {
								drawable = LoadImageFromWebOperations(feed.waterMarkWide);
								if (drawable == null)
									drawable = LoadImageFromWebOperations(feed.waterMarkWide);
							} else {
								drawable = LoadImageFromWebOperations(feed.waterMark);
								if (drawable == null)
									drawable = LoadImageFromWebOperations(feed.waterMarkWide);
							}
						} else {
							if (feed.waterMark != null
									&& feed.waterMark.length() > 10) {
								drawable = LoadImageFromWebOperations(feed.waterMark);
								if (drawable == null)
									drawable = LoadImageFromWebOperations(feed.waterMarkWide);
							} else {
								drawable = LoadImageFromWebOperations(feed.waterMarkWide);
								if (drawable == null)
									drawable = LoadImageFromWebOperations(feed.waterMarkWide);
							}
						}
						feed.waterMarkDrawable = drawable;
						mMainList.setBackgroundDrawable(drawable);
						mMainList.setCacheColorHint(0);
					}
				});
			}
			requestednextPage = false;
			if (resultCode != ACTION_BACK_SPECIFIED_USER_LIST) {
			}
			if (feed.entry.size() <= 0) {
				runOnUiThread(new Runnable() {
					public void run() {
						(findViewById(R.id.loading_linearlayout))
								.setVisibility(View.GONE);
						(findViewById(R.id.loading_linearlayoutk))
								.setVisibility(View.VISIBLE);
						if (selectedTab == 4)
							((TextView) findViewById(R.id.community_noContent))
									.setText(getString(R.string.there_are_no_pending_requests));
						else if (selectedTab == 1)
							((TextView) findViewById(R.id.community_noContent))
									.setText(getString(R.string.you_have_not_joined_any_communities));
						else
							((TextView) findViewById(R.id.community_noContent))
									.setText(getString(R.string.no_entry));

					}
				});
				return;
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						(findViewById(R.id.loading_linearlayoutk))
								.setVisibility(View.GONE);
					}
				});
			}
			runOnUiThread(new Runnable() {
				public void run() {
					hideShowNoMessageView();
					int lastPos = mMainList.getLastVisiblePosition() - 1;
					mMainList.setAdapter(mMediaAdapter);
					if (prevSelected != selectedTab)
						mMainList.setSelection(0);
					else
						mMainList.setSelection(lastPos);
					prevSelected = selectedTab;

					if (null != feed && null != feed.entry) {
						if (feed.entry.size() > 10) {
							startThumbnailThread(10);
						} else {
							startThumbnailThread(feed.entry.size());
						}
					}
					if (aTitle != null && aTitle.trim().length() > 0)
						((TextView) findViewById(R.id.community_screenTitle))
								.setText(aTitle);
					if (spacialcase) {
						mMainList.setVisibility(View.GONE);
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
		myScreenName(feed);
	}

	boolean moveONError = false;

	public void moveOnError() {
		if (Logger.ENABLE)
			Logger.debug(TAG, "-------------------on error----- move to tab :"
					+ prevSelectedTab);
		moveONError = true;
		switch (prevSelectedTab) {
		case 1:
			if (feed == null || feed.entry == null || feed.entry.size() <= 0)
				onBackPressed();
			else if (feed == null || feed.entry == null
					|| feed.entry.size() <= 0)
				onClick(findViewById(R.id.top_tab_my_communities));
			break;
		case 2:
			if (feed == null || feed.entry == null || feed.entry.size() <= 0)
				onClick(findViewById(R.id.top_tab_recommended));
			break;

		}
	}

	public void myScreenName(CommunityFeed feed) {
		// System.out.println("------------media screen name-------------- : "+name);
		// BusinessProxy.sSelf.dynamicScreen = name;// "PBK" ;//LIST_BOX =
		// LIST_BOX_CONTACT;
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// BusinessProxy.sSelf.addPush.pushAdd(CommunityChatActivity.this);
		// }
		// }).start();

		String name = Feed.getAttrCode(feed.tracking);
		// System.out.println("------------media screen name-------------- : "+name);
		BusinessProxy.sSelf.dynamicScreen = name;
		BusinessProxy.sSelf.SEO = feed.seo;
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// BusinessProxy.sSelf.addPush.noAddScreen = null;
		// BusinessProxy.sSelf.addPush
		// .pushAdd(KainatCommunityActivity.this);
		// }
		// }).start();
	}

	public void fetchDataFromDbCommunity() {
		Vector<Entry> entry = new Vector<Entry>();
		Cursor cuCommunity;
		try{
		cuCommunity = getCursor();
		}catch(Exception e){
			cuCommunity =null;
		}
		if (cuCommunity != null) {
			cuCommunity.moveToFirst();
			for (int i = 0; i < cuCommunity.getCount(); i++) {
				Entry E = CursorToObject.communityEntryObject(cuCommunity);
				cuCommunity.moveToNext();
				// Log.i(TAG,
				// "fetchDataFromDbCommunity :: ChannelName : "+E.groupName+", ChannelID : "+E.groupId);
				entry.add(E);
				// feed.entry
			}
			feed = new CommunityFeed();
			feed.entry = null;
			feed.entry = entry;
		}
		// cuCommunity.close();
	}

	public Cursor getCursor() {
		Cursor cursor = null;
		try {

			cursor = BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE,
					null, null, null, null, null, CommunityTable.INSERT_TIME
							+ " DESC");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cursor;

	}

	public void onNotification(int requestCode, String sender, String msg) {
		try {
			switch (requestCode) {
			case 9:
				showNewMessageAlert(sender, msg);
				break;
			case Constants.CHANNEL_NEW_COUNTER_FOR_EXISTING:
				fetchDataFromDbCommunity();
				if (mMediaAdapter != null) {
					initList();
				}
				break;
			case Constants.CHANNEL_REPORT_MESSAGE:
				// Just Refresh the notification counter image
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						KainatHomeActivity.refreshNotificationCounter();
					}
				});
				break;
			case Constants.CHANNEL_NEW_COUNTER_FOR_NEW:
			case Constants.CHANNEL_YOU_BECOME_MEMBER:
				// Refresh Channel List
				boolean refresh = false;
				if (msg != null) {
					switch (requestCode) {
					case Constants.CHANNEL_YOU_BECOME_MEMBER:
						Log.i(TAG, "onNotification :: " + msg);
						refresh = Pattern.matches(
								".*? added you to channel (.*?)", msg);
						if (!refresh) {
							// mahesh removed you from channel MyChannel
							refresh = Pattern.matches(
									".*? removed you from channel (.*?)", msg);
							if (refresh) {
								refresh = false;
								String channelName = msg.substring(msg
										.lastIndexOf(' ') + 1);
								if (channelName != null
										&& channelName.length() > 0) {
									setMemberPermission(channelName, "user");
									fetchDataFromDbCommunity();
									if (mMediaAdapter != null) {
										initList();
									}
								}
							}
						}
						break;

					default:
						break;
					}
				}
				if (UIActivityManager.refreshChannelList || refresh) {
					mPostURL = MY_COMMUNITY;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							KainatHomeActivity.showChannelRefreshLoader();

						}
					});
					UIActivityManager.refreshChannelList = false;
					mThread = new Thread(feedLoader = new FeedLoader());
					mThread.start();
				}
			default:
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setMemberPermission(String channelName, String permissionType) {
		try {
			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
					CommunityTable.TABLE, null, CommunityTable.GROUP_NAME
							+ " = ? ", new String[] { "" + channelName }, null,
					null, null);
			if (cursor.getCount() == 1) {
				ContentValues values = new ContentValues();
				values.put(CommunityTable.MEMBER_PERMISSION, permissionType);

				int rowUpdated = BusinessProxy.sSelf.sqlDB.update(
						CommunityTable.TABLE, values, CommunityTable.GROUP_NAME
								+ "= ? ", new String[] { channelName });
				// Log.i(TAG,
				// "setMemberPermission :: for Channel : "+channelName+", to : "+permissionType);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	class FeatureLoader implements Runnable {
		public boolean lodingCanceled = false;
		String GroupIdFeatured;
		boolean featured = false;

		FeatureLoader(String groupid, boolean featured) {
			GroupIdFeatured = groupid;
			this.featured = featured;
		}

		@Override
		public void run() {
			// while (mIsRunning)
			{
				try {
					String PostURLFeature = "http://www.yelatalkprod.com/tejas/feeds/api/group/makefeatured?groupId="
							+ GroupIdFeatured;
					String PostURLUnFeature = "http://www.yelatalkprod.com/tejas/feeds/api/group/makeunfeatured?groupId="
							+ GroupIdFeatured;
					// synchronized (this) {
					// wait();
					// }
					// BusinessProxy.sSelf.writeLogsToFile(TAG
					// +":FeedLoader - Entry run loop");
					cancelRequest = false;
					if (featured) {
						helper = new HttpConnectionHelper(PostURLFeature);
					} else {
						helper = new HttpConnectionHelper(PostURLUnFeature);
					}
					String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword());
					helper.setHeader("RT-APP-KEY", appKeyValue);
					/*
					 * helper.setHeader("RT-APP-KEY", "" + iLoggedUserID);
					 * helper.setHeader("RT-DEV-KEY", "" +
					 * BusinessProxy.sSelf.getClientId());
					 */
					mCallBackTimer = new Timer();
					responseCode = helper.getResponseCode();

					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
						String contentEncoding = helper
								.getHttpHeader("Content-Encoding");
						InputStream inputStream = null;
						if (null == contentEncoding) {
							inputStream = helper.getInputStream();
						} else if (contentEncoding.equals("gzip")) {
							inputStream = new GZIPInputStream(
									helper.getInputStream());
						}
						ByteArrayBuffer buffer = new ByteArrayBuffer(
								CHUNK_LENGTH);
						byte[] chunk = new byte[CHUNK_LENGTH];
						int len;
						while (!cancelRequest
								&& -1 != (len = inputStream.read(chunk))) {
							buffer.append(chunk, 0, len);
						}
						KainatCommunityActivity.this.mResponseData = buffer
								.toByteArray();
						Log.w(TAG, new String(mResponseData));
					}

					/*
					 * if (null != PostURLFeature && 0 <
					 * PostURLFeature.trim().length()) { if
					 * (mPostURL.indexOf("?") != -1) mPostURL = mPostURL + "&" +
					 * i_newThumbFormat; else mPostURL = mPostURL + "?" +
					 * i_newThumbFormat; mPostURL = mPostURL +
					 * "&textMode=base64&limit=" + COMM_COUNT;
					 * 
					 * // BusinessProxy.sSelf.writeLogsToFile(TAG //
					 * +" HTTP responseCode  "+responseCode); }
					 */
				} catch (Exception ex) {
					mCallBackTimer = new Timer();
					mCallBackTimer.schedule(new OtsSchedularTask(
							KainatCommunityActivity.this, ERROR_CALLBACK,
							(byte) 0), 0);
				}
			}
		}
		/*
		 * public void run() { // while (mIsRunning) { try {
		 * 
		 * // synchronized (this) { // wait(); // } cancelRequest = false; if
		 * (null != mPostURL && 0 < mPostURL.trim().length()) { helper = new
		 * HttpConnectionHelper(mPostURL); helper.setHeader("RT-APP-KEY", "" +
		 * iLoggedUserID); helper.setHeader("RT-DEV-KEY", "" +
		 * BusinessProxy.sSelf.getClientId()); mCallBackTimer = new Timer();
		 * responseCode = helper.getResponseCode(); switch (responseCode) { case
		 * HttpURLConnection.HTTP_OK: String contentEncoding =
		 * helper.getHttpHeader("Content-Encoding"); InputStream inputStream =
		 * null; if (null == contentEncoding) { inputStream =
		 * helper.getInputStream(); } else if (contentEncoding.equals("gzip")) {
		 * inputStream = new GZIPInputStream(helper.getInputStream()); }
		 * ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH); byte[]
		 * chunk = new byte[CHUNK_LENGTH]; int len; while (!cancelRequest && -1
		 * != (len = inputStream.read(chunk))) { buffer.append(chunk, 0, len); }
		 * this.mResponseData = buffer.toByteArray(); break; default: // if
		 * (mProgressDialog.isShowing()) { // mProgressDialog.dismiss(); // }
		 * cancelLoading(); break; } if(cancelRequest)return ;
		 * mCallBackTimer.cancel(); mCallBackTimer = new Timer();
		 * mCallBackTimer.schedule(new OtsSchedularTask(this, DATA_CALLBACK),
		 * 0); } } catch (Exception ex) { mCallBackTimer = new Timer();
		 * mCallBackTimer.schedule(new OtsSchedularTask(this, ERROR_CALLBACK),
		 * 0); } }
		 */
	}
	//

}
