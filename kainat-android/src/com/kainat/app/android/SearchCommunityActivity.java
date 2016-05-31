package com.kainat.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.UIActivityManager.GetTags;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.DateFormatUtils;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ImageLoader;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;
import com.kainat.app.autocomplete.tag.RoundedBackgroundSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class SearchCommunityActivity extends UICommunityManager implements View.OnClickListener, ThumbnailReponseHandler, OnScrollListener, OnEditorActionListener {
	private final String SEARCH_COMMUNITY = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/group/searched/advance";
	private final String SEARCH_COMMUNITY_TAG = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/group/searched/hashtag";
	public static final String SCREEN_MODE = "SCREEN_MODE";
	private int selectedTab = 1;
	private ListView mMainList;
	private CommunityAdapter mSearchAdapter;
	private ImageLoader mImageLoader;
	public static byte TAB_NAME = 0;
	public static final byte TAB_COMMUNITY = 0;
	public static final byte TAB_SEARCH = 1;
	public static final byte TAB_PENDING = 2;
	public static final int LOADING_ID = -1000;
	private byte SEARCH_TYPE = 0;
	private static final byte SEARCH_BY_GROUP = 0;
	private static final byte SEARCH_BY_CATEGORY = 1;
	private GridView grid;
	private String getRequestParms = "";
	private String categoryName = "";
	public long buzzSentTime = 0;
	private EditText nameEditor;
	private ImageView categories;
	private Button back_button;
	private String user = null;
	int va_pos= -1;

	private Button totalButtons[] = new Button[30];
	private boolean spacialcase = false;
	int idsReport[] = new int[] { 1, 2,3};
	String searchString = "";
	String UrlIs ="";
	private int coming_from = 0;
	String searchedHashTag;
	ToggleButton ch;
	ScrollView scrollview_tag;
	public static final int COMM_COUNT = 50;
	ProgressDialog rTDialog;
	TextView skip;
	String idsNameReport[] = new String[] { "Advance search","Create new community","Cancel" };
	private CharSequence text = "";
	private LinearLayout hashTagLayout;
	private ImageView backIv;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.search_community_screen);
		idsNameReport = new String[] { getString(R.string.advance_search),getString(R.string.create_channel),getString(R.string.cancel) };
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).
				resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.attachicon)
				.showImageOnFail(R.drawable.attachicon)
				.showImageOnLoading(R.drawable.attachicon).build();
		/*nameEditor.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					//startLoginProcessing(v);
					return true;
				}
				return false;
			}
		});*/
		init();
		initLeftMenu();
		backIv = (ImageView)findViewById(R.id.back_iv) ;		
		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();	
			}
		});
		try{
			coming_from = getIntent().getIntExtra("TYPE", 0);
			searchString = getIntent().getStringExtra("CommunityType");
			UrlIs =  getIntent().getStringExtra("UrlIs");
			if(coming_from == (byte)1)
			{ // Discover
				hashTagLayout.setVisibility(View.GONE);
				scrollview_tag.setVisibility(View.GONE);
				skip.setVisibility(View.GONE);
				nameEditor.setVisibility(View.GONE);
				((TextView)findViewById(R.id.title_search)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.title_search)).setText(searchString);
				((LinearLayout)findViewById(R.id.search_bar_layout)).setVisibility(View.GONE);
				menuNew.setVisibility(View.INVISIBLE);
				backIv.setVisibility(View.VISIBLE);

			}else if(coming_from == (byte)3){ //  Home search
				nameEditor.setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.title_search)).setText(getResources().getString(R.string.find_community));
				//				((ImageView)findViewById(R.id.community_createButton)).setVisibility(View.VISIBLE);
				menuNew.setVisibility(View.INVISIBLE);
				backIv.setVisibility(View.VISIBLE);
				skip.setVisibility(View.GONE);
				text = getFeaturedTagsFormat();
				if(text != null && !text.equals("")){
					hashTagLayout.setVisibility(View.VISIBLE);
					scrollview_tag.setVisibility(View.VISIBLE);
					drawFeaturedTags();

				}
				text = getTrendingTagsFormat();
				if(text != null && !text.equals("")){
					drawTrendingTags();

				}
			}
			else if(coming_from == (byte)4)
			{ // First Time
				hashTagLayout.setVisibility(View.GONE);
				scrollview_tag.setVisibility(View.GONE);
				nameEditor.setVisibility(View.GONE);
				skip.setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.title_search)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.title_search)).setText(getResources().getString(R.string.suggested_channel));
				((LinearLayout)findViewById(R.id.search_bar_layout)).setVisibility(View.GONE);
				menuNew.setVisibility(View.INVISIBLE);
				backIv.setVisibility(View.GONE);

			}

			else
			{ //  Find Community
				nameEditor.setVisibility(View.VISIBLE);
				skip.setVisibility(View.GONE);
				((TextView)findViewById(R.id.title_search)).setText(getResources().getString(R.string.find_community));
				//				((ImageView)findViewById(R.id.community_createButton)).setVisibility(View.VISIBLE);
				menuNew.setVisibility(View.VISIBLE);
				backIv.setVisibility(View.INVISIBLE);
				text = getFeaturedTagsFormat();
				if(text != null && !text.equals("")){
					hashTagLayout.setVisibility(View.VISIBLE);
					scrollview_tag.setVisibility(View.VISIBLE);
					drawFeaturedTags();

				}
				text = getTrendingTagsFormat();
				if(text != null && !text.equals("")){
					drawTrendingTags();

				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		if(searchString != null && !searchString.equals(""))
		{
			((TextView) findViewById(R.id.community_noContent)).setVisibility(View.GONE);
			searchCommunity(searchString,1);
		}
		
		if(isInternetOn())
			new GetTags().execute("") ;
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Channel Search Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());	
	}
	private void drawFeaturedTags(){
		TextView featureTextView = (TextView) findViewById(R.id.featured_hashtag_text);
		MovementMethod m = featureTextView.getMovementMethod();
		if ((m == null) || !(m instanceof LinkMovementMethod))
		{
			featureTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}
		boolean isTrue = true;
		while(isTrue){
			text = setSpanBetweenTokens(text, "@",  new RoundedBackgroundSpan(getResources().getColor(R.color.tag_bg_color), getResources().getColor(R.color.tag_text_color))//);red(0xFFFF0000, 0xFFFFFFFF)//(0xf7f7f7, 0xff868686)
			//text = setSpanBetweenTokens(text, "@", new RoundedBackgroundSpan(R.color.tag_bg_color, R.color.sub_heading)//);
			, new ClickableSpan()
			{
				@Override
				public void onClick(View v)
				{
					// When the span is clicked, show some text on-screen.
					int i = ((TextView) v).getSelectionStart();
					int j = ((TextView) v).getSelectionEnd();
					String tagName = ((TextView) v).getText().toString().substring(i, j).replace(" #", "#");
					String tagValue = ((TextView) v).getText().toString().substring(i, j).replace(" #", "");
					//					System.out.println("Print Value = "+tagValue + " --> L,"+tagValue.length());
					//onTagClickAction(tagValue);
					hashTagLayout.setVisibility(View.GONE);
					scrollview_tag.setVisibility(View.GONE);
					searchCommunity(tagName,1);

				}
			});
			if(text.toString().contains("@"))
				isTrue = true;
			else
				isTrue = false;
		}
		featureTextView.setText(text);
	}

	private void drawTrendingTags(){
		TextView trendingTextView = (TextView) findViewById(R.id.trending_hashtag_text);
		MovementMethod m = trendingTextView.getMovementMethod();
		if ((m == null) || !(m instanceof LinkMovementMethod))
		{
			trendingTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}
		boolean isTrue = true;
		while(isTrue){
			text = setSpanBetweenTokens(text, "@",  new RoundedBackgroundSpan(getResources().getColor(R.color.tag_bg_color), getResources().getColor(R.color.tag_text_color))//);red(0xFFFF0000, 0xFFFFFFFF)//(0xf7f7f7, 0xff868686)
			//text = setSpanBetweenTokens(text, "@", new RoundedBackgroundSpan(R.color.tag_bg_color, R.color.sub_heading)//);
			, new ClickableSpan()
			{
				@Override
				public void onClick(View v)
				{
					// When the span is clicked, show some text on-screen.
					int i = ((TextView) v).getSelectionStart();
					int j = ((TextView) v).getSelectionEnd();
					String tagName = ((TextView) v).getText().toString().substring(i, j).replace(" #", "#");
					String tagValue = ((TextView) v).getText().toString().substring(i, j).replace(" #", "");
					//					System.out.println("Print Value = "+tagValue + " --> L,"+tagValue.length());
					//onTagClickAction(tagValue);
					hashTagLayout.setVisibility(View.GONE);
					scrollview_tag.setVisibility(View.GONE);
					searchCommunity(tagName,1);

				}
			});
			if(text.toString().contains("@"))
				isTrue = true;
			else
				isTrue = false;
		}
		trendingTextView.setText(text);
	}
	private void onTagClickAction(String name){
		if (name != null && !name.equals("")) {
			getRequestParms = "?groupname=" + URLEncoder.encode(name) + "&category=&user=" + URLEncoder.encode(user) + "&owner=";
			if(Logger.ENABLE)Logger.debug("getRequestParms", getRequestParms);
			feed = null;
			postParam.clear();
			if(name!=null && name.trim().length()>0)
				postParam.put("groupname", name);
			//searchCommunity(name,1);
			hashTagLayout.setVisibility(View.GONE);
			scrollview_tag.setVisibility(View.GONE);
			//			executeRequestData(SEARCH_COMMUNITY+getRequestParms, null, null);
			searchCommunity("#"+name, 1);

		}
	}
	private  CharSequence setSpanBetweenTokens(CharSequence text,
			String token, CharacterStyle... cs)
	{//http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring
		// Start and end refer to the points where the span will apply
		try{
			int tokenLen = token.length();
			int start = text.toString().indexOf(token) + tokenLen;
			int end = text.toString().indexOf(token, start);

			if (start > -1 && end > -1)
			{
				// Copy the spannable string to a mutable spannable string
				SpannableStringBuilder ssb = new SpannableStringBuilder(text);
				for (CharacterStyle c : cs)
					ssb.setSpan(c, start, end, 0);

				// Delete the tokens before and after the span
				ssb.delete(end, end + tokenLen);
				ssb.delete(start - tokenLen, start);

				text = ssb;
			}
			return text;
		}catch(Exception e){
			return "";
		}


	}
	private String getFeaturedTagsFormat(){
		String tagsCode = "";
		if(UIActivityManager.featuredTags != null){
			String[] tagArray = UIActivityManager.featuredTags.split(",");
			ArrayList<String> list = new ArrayList<String>();
			for(int t = 0; t < tagArray.length; t++){
				list.add("@ #"+tagArray[t]+"@");
			}
			for(int l = 0; l < list.size(); l++){
				tagsCode = tagsCode + list.get(l)+" ";
			}
			tagsCode = tagsCode.substring(0, tagsCode.length() - 1);
		}else{
			tagsCode = null;
		}
		return tagsCode;
	}

	private String getTrendingTagsFormat(){
		String tagsCode = "";
		if(UIActivityManager.trendingTags != null){
			String[] tagArray = UIActivityManager.trendingTags.split(",");
			ArrayList<String> list = new ArrayList<String>();
			for(int t = 0; t < tagArray.length; t++){
				list.add("@ #"+tagArray[t]+"@");
			}
			for(int l = 0; l < list.size(); l++){
				tagsCode = tagsCode + list.get(l)+" ";
			}
			tagsCode = tagsCode.substring(0, tagsCode.length() - 1);
		}else{
			tagsCode = null;
		}
		return tagsCode;
	}
	public void init(){
		skip = (TextView)findViewById(R.id.skip);
		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		TAB_NAME 	= TAB_COMMUNITY;
		SEARCH_TYPE = SEARCH_BY_GROUP;
		user 		= SettingData.sSelf.getUserName();
		iLoggedUserID = BusinessProxy.sSelf.getUserId();
		//		setContentView(R.layout.search_community_screen);
		//		screenSlide(SearchCommunityActivity.this, R.layout.search_community_screen);

		menuNew = (ImageButton)findViewById(R.id.menu) ;		
		menuNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList) ;	
			}
		});


		hashTagLayout  = (LinearLayout)findViewById(R.id.hashtag_layout);
		scrollview_tag = (ScrollView)findViewById(R.id.scrollview_tag);
		hashTagLayout.setVisibility(View.GONE);
		scrollview_tag.setVisibility(View.GONE);
		categories = (ImageView) findViewById(R.id.searchwith_category_id);
		categories.setOnClickListener(this);
		mMainList = (ListView) findViewById(R.id.community_searchList);
		mMainList.setOnScrollListener(this);
		mSearchAdapter = new CommunityAdapter(this);
		mImageLoader = ImageLoader.getInstance(this);
		mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
		startThread();
		nameEditor = ((EditText) findViewById(R.id.communityScreen_searchBox));
		nameEditor.setText(null);
		//nameEditor.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
		//nameEditor.performAccessibilityAction(KeyEvent.KEYCODE_ENTER, null);

		nameEditor.setOnEditorActionListener(this);
		if (feed == null) {
			((TextView) findViewById(R.id.community_noContent)).setText(getResources().getString(R.string.enter_comm_search));
			((TextView) findViewById(R.id.community_noContent)).setVisibility(View.GONE);
			mMainList.setVisibility(View.GONE);
			return;
		} else {
			((TextView) findViewById(R.id.community_noContent)).setVisibility(View.GONE);
			mMainList.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void onResume() {
		if (DataModel.sSelf.getRemoveBoolean(DMKeys.NEW_INTENT) == true)
		{
			feed=null;
			mPostURL=null;
			//			init() ;
		}
		super.onResume();
		reloadeViewAfterChangeLanguag();
	}
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}
	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		//String[] categories = getResources().getStringArray(R.array.community_category);
		String[] categories = BusinessProxy.sSelf.communityCategories;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return categories.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			//if (convertView == null) 
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.category_layout, null);
			}
			convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			totalButtons[position] = (Button) convertView.findViewById(R.id.categories_buttons);
			ViewGroup.LayoutParams  l = totalButtons[position].getLayoutParams() ;
			Display display = getWindowManager().getDefaultDisplay();
			l.width = ((display.getWidth()-30)/3) ;
			//l.height = l.height*3 ;
			totalButtons[position].setText(categories[position]);
			//			totalButtons[position].setId(30203);
			totalButtons[position].setOnClickListener(SearchCommunityActivity.this);
			if (totalButtons[position] != null) {
				if (!categories[position].equals(categoryName))//@drawable/mpil_centerselector
					totalButtons[position].setBackgroundResource(R.drawable.black_butn);
				else
					totalButtons[position].setBackgroundResource(R.drawable.roundedrectgradient_green);
			}
			return convertView;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comm_cancel_button:
			switch (SEARCH_TYPE) {
			case SEARCH_BY_CATEGORY:
				SEARCH_TYPE = SEARCH_BY_GROUP;
				//setContentView(R.layout.search_community_screen);
				//				screenSlide(SearchCommunityActivity.this, R.layout.search_community_screen);
				setContentView(R.layout.search_community_screen);
				init();
				initLeftMenu();

				categories = (ImageView) findViewById(R.id.searchwith_category_id);
				categories.setOnClickListener(this);
				nameEditor = ((EditText) findViewById(R.id.communityScreen_searchBox));
				nameEditor.setText(null);
				nameEditor.setOnEditorActionListener(this);
				return ;
			case SEARCH_BY_GROUP:
				popScreen();
				return ;
			}
			break;
		case R.id.comm_search_button:
			Utilities.closeSoftKeyBoard(findViewById(R.id.communityScreen_searchBox), SearchCommunityActivity.this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.communityCategory_searchBox), SearchCommunityActivity.this);
			String groupName = "";
			String ownerName = "";
			String categoryName1 = "";
			feed = null;
			String categoryName = String.valueOf(((Spinner) findViewById(R.id.category)).getSelectedItem());

			switch (SEARCH_TYPE) {
			case SEARCH_BY_GROUP:
				groupName = ((EditText) findViewById(R.id.communityScreen_searchBox)).getText().toString();
				break;
			case SEARCH_BY_CATEGORY:
				groupName = ((EditText) findViewById(R.id.communityScreen_searchBox)).getText().toString();
				ownerName = ((EditText) findViewById(R.id.communityCategory_searchBox)).getText().toString();
				categoryName1 = categoryName;
				break;
			}
			//			try {
			postParam.clear();
			if(groupName!=null && groupName.trim().length()>0)
				postParam.put("groupname", groupName);
			if(categoryName1!=null && categoryName1.trim().length()>0 && !categoryName1.equalsIgnoreCase(getString(R.string.select_category)))
				postParam.put("category", categoryName1);
			if(user!=null && user.trim().length()>0)
				postParam.put("user", user);
			if(ownerName!=null && ownerName.trim().length()>0)
				postParam.put("owner", ownerName);

			//				getRequestParms = "?groupname=" + URLEncoder.encode(groupName, Constants.ENC) + "&category=" + URLEncoder.encode(categoryName1) + "&user=" + URLEncoder.encode(user) + "&owner="
			//						+ URLEncoder.encode(ownerName);
			//			} catch (UnsupportedEncodingException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			if(Logger.ENABLE)Logger.debug("getRequestParms", getRequestParms);
			//			executeRequestData(SEARCH_COMMUNITY + getRequestParms, null, null);
			executeRequestData(SEARCH_COMMUNITY, null, null);
			break;
		case R.id.categories_buttons:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			calledAction(v);
			break;
		case R.id.community_createButton:
			((ImageView)findViewById(R.id.community_createButton)).setImageResource(R.drawable.x_options_sel);
			Intent intent = new Intent(this, CreateCommunityActivity.class);
			//startActivity(intent);
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			action(idsReport, idsNameReport, (byte) 1);
			quickAction.show(v);
			//			super.pushNewScreen(CreateKainatHomeActivity.class, false);
			break;
		case R.id.web_back_button:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			categories = null;
			//setContentView(R.layout.search_community_screen);
			//			screenSlide(SearchCommunityActivity.this, R.layout.search_community_screen);
			setContentView(R.layout.search_community_screen);
			init();
			initLeftMenu();
			SEARCH_TYPE = SEARCH_BY_GROUP;
			categories = (ImageView) findViewById(R.id.searchwith_category_id);
			categories.setOnClickListener(this);
			mMainList = (ListView) findViewById(R.id.community_searchList);
			mMainList.setOnScrollListener(this);
			mSearchAdapter = new CommunityAdapter(this);
			mImageLoader = ImageLoader.getInstance(this);
			mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
			nameEditor = ((EditText) findViewById(R.id.communityScreen_searchBox));
			nameEditor.setText(null);
			nameEditor.setOnEditorActionListener(this);
			break;
		case R.id.searchwith_category_id:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			setContentView(R.layout.category_community_screen);
			SEARCH_TYPE = SEARCH_BY_CATEGORY;
			categoryName = "";
			back_button = (Button) findViewById(R.id.web_back_button);
			back_button.setOnClickListener(this);
			//			grid = (GridView) findViewById(R.id.grid_community);
			//			grid.setAdapter(new ImageAdapter(this));
			break;
		case LOADING_ID:
			if(feed!= null && feed.entry!= null){
				va_pos=feed.entry.size();

			}
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			TextView textView = ((TextView) v.findViewById(R.id.loadingtext));
			if (textView.getText().toString().equalsIgnoreCase("Load More")) {
				requestednextPage = true;
			}
			textView.setText("Loading..");
			if (requestednextPage) {
				String aURL = CommunityFeed.getAttrRel(feed.links,"next");
				if(aURL == null || aURL.equalsIgnoreCase("NA") || aURL.length() <= 0)
					aURL = feed.nexturl ;
				oldFeed = feed;
				executeRequestData(aURL, null, null);
			}
			break;
		case R.id.home_icon:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			if (BusinessProxy.sSelf.mCacheURLs.size() > 0)
				BusinessProxy.sSelf.mCacheURLs.removeAllElements();
			pushNewScreen(KainatHomeActivity.class, false);
			break;
		case R.id.bottom_tab_mycommunities1:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			TAB_NAME = TAB_COMMUNITY;
			pushNewScreen(KainatHomeActivity.class, false);//popScreen();// 
			break;
		case R.id.bottom_tab_pending_request:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			TAB_NAME = TAB_PENDING;
			pushNewScreen(KainatHomeActivity.class, false);//popScreen();
			break;
		case R.id.communityImage:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			CommunityFeed.Entry entry = feed.entry.get((Integer) v.getTag());
			DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
			intent = new Intent(SearchCommunityActivity.this, CommunityProfileNewActivity.class);
			intent.putExtra("group_name", entry.groupName);
			intent.putExtra("group_id", entry.groupId);
			intent.putExtra("group_desc", entry.description);
			intent.putExtra("group_pic", entry.thumbUrl);
			startActivity(intent);
			break;
		case R.id.communityInfoLayout1:
		case R.id.linearluot_out:
		case R.id.communityName:
		case R.id.moreButton:
		case R.id.communityInfo:
			Utilities.closeSoftKeyBoard(v, SearchCommunityActivity.this);
			entry = feed.entry.get((Integer) v.getTag());
			if (entry.memberPermission.equalsIgnoreCase("user")) {
				DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
				intent = new Intent(SearchCommunityActivity.this, CommunityProfileNewActivity.class);
				intent.putExtra("group_name", entry.groupName);
				intent.putExtra("group_id", entry.groupId);
				intent.putExtra("group_desc", entry.description);
				intent.putExtra("group_pic", entry.thumbUrl);
				startActivity(intent);
			} else {
				//Goto Chat Activity
				DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
				intent = new Intent(SearchCommunityActivity.this, CommunityChatActivity.class);
				intent.putExtra("message_url", entry.messageUrl);
				intent.putExtra("message_id", entry.messageId);
				intent.putExtra("group_name", entry.groupName);
				intent.putExtra("group_desc", entry.description);
				intent.putExtra("group_pic", entry.thumbUrl);
				startActivity(intent);
			}
			break;			
		}	
	}

	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		MenuInflater inflater = getMenuInflater();
	//		inflater.inflate(R.menu.search_community, menu);
	//		return true;
	//	}
	public void action(int ids[], String[] name, final byte type) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL,false,true,name.length);

		for (int i = 0; i < name.length; i++) {
			ActionItem nextItem = new ActionItem(ids[i], name[i], null);
			quickAction.addActionItem(nextItem);
		}

		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				ActionItem actionItem = quickAction.getActionItem(pos);
				if (type == 1) {//report
					//					Toast.makeText(getApplicationContext(), actionId + " : REPORT : " + pos, Toast.LENGTH_SHORT).show();
					if (actionId == 1) {//delete

						setContentView(R.layout.category_community_screen);
						initLeftMenu();
						findViewById(R.id.prev).setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								onBackPressed();
							}
						});

						SEARCH_TYPE = SEARCH_BY_CATEGORY;
						categoryName = "";
						//						back_button = (Button) findViewById(R.id.web_back_button);
						//						back_button.setOnClickListener(SearchCommunityActivity.this);
						//						grid = (GridView) findViewById(R.id.grid_community);
						//						grid.setAdapter(new ImageAdapter(SearchCommunityActivity.this));

					} else if (actionId == 2) {

						Intent intent = new Intent(SearchCommunityActivity.this, CreateCommunityActivity.class);
						startActivity(intent);

					} else if (actionId == 3) {

					}else if(actionId == 4){

					}
				}
			}

		});
		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				((ImageView)findViewById(R.id.community_createButton)).setImageResource(R.drawable.x_options);
			}
		});
	}
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id._search:
			String groupName = "";
			String ownerName = "";
			String categoryName1 = "";
			feed = null;
			switch (SEARCH_TYPE) {
			case SEARCH_BY_GROUP:
				groupName = ((EditText) findViewById(R.id.communityScreen_searchBox)).getText().toString();
				break;
			case SEARCH_BY_CATEGORY:
				groupName = ((EditText) findViewById(R.id.communityScreen_searchBox)).getText().toString();
				ownerName = ((EditText) findViewById(R.id.communityCategory_searchBox)).getText().toString();
				categoryName1 = categoryName;
				break;
			}
			getRequestParms = "?groupname=" + URLEncoder.encode(groupName) + "&category=" + URLEncoder.encode(categoryName1) + "&user=" + URLEncoder.encode(user) + "&owner="
					+ URLEncoder.encode(ownerName);
			if(Logger.ENABLE)Logger.debug("getRequestParms", getRequestParms);
			executeRequestData(SEARCH_COMMUNITY + getRequestParms, null, null);
			return true;
		case R.id._cancel:
			switch (SEARCH_TYPE) {
			case SEARCH_BY_CATEGORY:
				SEARCH_TYPE = SEARCH_BY_GROUP;
				//setContentView(R.layout.search_community_screen);
				//				screenSlide(SearchCommunityActivity.this, R.layout.search_community_screen);
				setContentView(R.layout.search_community_screen);
				init();
				initLeftMenu();
				categories = (ImageView) findViewById(R.id.searchwith_category_id);
				categories.setOnClickListener(this);
				nameEditor = ((EditText) findViewById(R.id.communityScreen_searchBox));
				nameEditor.setText(null);
				nameEditor.setOnEditorActionListener(this);
				return true;
			case SEARCH_BY_GROUP:
				popScreen();
				return true;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id._search:
			Utilities.closeSoftKeyBoard(findViewById(R.id.communityScreen_searchBox), SearchCommunityActivity.this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.communityCategory_searchBox), SearchCommunityActivity.this);
			String groupName = "";
			String ownerName = "";
			String categoryName1 = "";
			feed = null;
			switch (SEARCH_TYPE) {
			case SEARCH_BY_GROUP:
				groupName = ((EditText) findViewById(R.id.communityScreen_searchBox)).getText().toString();
				break;
			case SEARCH_BY_CATEGORY:
				ownerName = ((EditText) findViewById(R.id.communityCategory_searchBox)).getText().toString();
				categoryName1 = categoryName;
				break;
			}
			getRequestParms = "?groupname=" + URLEncoder.encode(groupName) + "&category=" + URLEncoder.encode(categoryName1) + "&user=" + URLEncoder.encode(user) + "&owner="
					+ URLEncoder.encode(ownerName);
			if(Logger.ENABLE)Logger.debug("getRequestParms", getRequestParms);
			executeRequestData(SEARCH_COMMUNITY + getRequestParms, null, null);
			return true;
		case R.id._cancel:
			Utilities.closeSoftKeyBoard(findViewById(R.id.communityScreen_searchBox), SearchCommunityActivity.this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.communityCategory_searchBox), SearchCommunityActivity.this);
			switch (SEARCH_TYPE) {
			case SEARCH_BY_CATEGORY:
				SEARCH_TYPE = SEARCH_BY_GROUP;
				//setContentView(R.layout.search_community_screen);
				//				screenSlide(SearchCommunityActivity.this, R.layout.search_community_screen);
				setContentView(R.layout.search_community_screen);
				init();
				initLeftMenu();
				categories = (ImageView) findViewById(R.id.searchwith_category_id);
				categories.setOnClickListener(this);
				nameEditor = ((EditText) findViewById(R.id.communityScreen_searchBox));
				nameEditor.setText(null);
				nameEditor.setOnEditorActionListener(this);
				return true;
			case SEARCH_BY_GROUP:
				Utilities.closeSoftKeyBoard(findViewById(R.id.communityScreen_searchBox), SearchCommunityActivity.this);
				Utilities.closeSoftKeyBoard(findViewById(R.id.communityCategory_searchBox), SearchCommunityActivity.this);
				popScreen();
				return true;
			}
		}
		return false;
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	Hashtable<String, String> postParam = new Hashtable<String, String>();
	private class SearchTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			

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

				urls[0] = urls[0] + "&textMode=base64&limit="+COMM_COUNT;

				HttpPost httppostw = new HttpPost( urls[0]);
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					for (Iterator<String> iterator = postParam.keySet().iterator(); iterator.hasNext();) {
						String key = iterator.next();
						String value = postParam.get(key);
						nameValuePairs.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"))));
					}
					httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
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
				mResponseData = responseString.getBytes();
				return responseString;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(SearchCommunityActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			if (rTDialog!=null && rTDialog.isShowing()) {
				rTDialog.dismiss();
				//rTDialog = null;
			}
			if(mCallBackTimer!=null)
				mCallBackTimer.cancel();
			mCallBackTimer = new Timer();
			if(response!=null)
				mCallBackTimer.schedule(new OtsSchedularTask(SearchCommunityActivity.this, DATA_CALLBACK, (byte)0), 0);
			else 
			{
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(SearchCommunityActivity.this, ERROR_CALLBACK, (byte)0), 0);
			}
			super.onPostExecute(response);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if ((rTDialog != null) && rTDialog.isShowing())
			rTDialog.dismiss();
		rTDialog = null;
	}
	public void executeRequestData(String aURL, String aMedia, byte[] postData) {
		switch (SEARCH_TYPE) {
		case SEARCH_BY_GROUP:
			mMainList = (ListView) findViewById(R.id.community_searchList);
			mMainList.setOnScrollListener(this);
			mSearchAdapter = new CommunityAdapter(this);
			mImageLoader = ImageLoader.getInstance(this);
			mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
			break;
		case SEARCH_BY_CATEGORY:
			SEARCH_TYPE = SEARCH_BY_GROUP;
			//	setContentView(R.layout.search_community_screen);
			//			screenSlide(SearchCommunityActivity.this, R.layout.search_community_screen);
			setContentView(R.layout.search_community_screen);
			init();
			initLeftMenu();
			categories = (ImageView) findViewById(R.id.searchwith_category_id);
			categories.setOnClickListener(this);
			mMainList = (ListView) findViewById(R.id.community_searchList);
			mMainList.setOnScrollListener(this);
			mSearchAdapter = new CommunityAdapter(this);
			mImageLoader = ImageLoader.getInstance(this);
			mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
			nameEditor = ((EditText) findViewById(R.id.communityScreen_searchBox));
			nameEditor.setText(null);
			nameEditor.setOnEditorActionListener(this);
			break;
		}
		//showLoggingDialog("RockeTalk");//, "Loading..", false);
		showLoadingDialog();
		//		super.setPostData(postData);

		//		super.executeRequest(aURL, aMedia);
		searchTask = new SearchTask() ;
		searchTask.execute(aURL) ;
	}

	SearchTask searchTask ;
	class CProgressDialog extends ProgressDialog {
		public CProgressDialog(Context context) {
			super(context);
			lodingCanceled = false;
		}

		public void onBackPressed() {
			if(onBackPressedCheck())return;
			return;
			//			feed = (CommunityFeed) DataModel.sSelf.getObject(DMKeys.FEED);
			//			if(feed == null){
			//				
			//			}
			//			lodingCanceled = true;
			//			helper.close();
			//			dismiss();
			//			super.onBackPressed();
		}
	}


	protected void showLoadingDialog() {
		//		showAnimationDialog("", getString(R.string.please_wait_while_loadin), true,
		//				PROGRESS_CANCEL_HANDLER);
		rTDialog = ProgressDialog.show(SearchCommunityActivity.this, "", getString(R.string.loading_dot), true);
	}

	protected void showLoggingDialog(String aString) {
		mProgressDialog = new CProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.please_wait_while_loadin)+"\n" + aString + "..");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancelRequest = true ;
				rTDialog.dismiss();
			}
		});
		mProgressDialog.show();
	}
	private void startThumbnailThread(int maxCount) {
		if(Logger.ENABLE)Logger.debug("startThumbnailThread", "startThumbnailThread");
		try {
			int start = mMainList.getFirstVisiblePosition();
			String[] names = new String[maxCount];
			int[] indexs = new int[maxCount];
			Entry entry = null;
			for (int i = 0; i < maxCount; ++i) {
				entry = feed.entry.elementAt(start + i);
				names[i] = entry.thumbUrl;
				indexs[i] = start + i;
			}
			mImageLoader.cleanAndPutRequest(names, indexs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseXMLData(final String aTitle) {
		super.parseXMLData();
		if (feed == null||feed.entry==null || feed.entry.size()==0) {
			runOnUiThread(new Runnable() {
				public void run() {
					((TextView) findViewById(R.id.community_noContent)).setText(getString(R.string.result_not_found));
					((TextView) findViewById(R.id.community_noContent)).setVisibility(View.VISIBLE);
				}
			});
			return;
		} else {
			runOnUiThread(new Runnable() {
				public void run() {
					((TextView) findViewById(R.id.community_noContent)).setVisibility(View.GONE);
				}
			});
		}
//		BusinessProxy.sSelf.recordScreenStatistics(Feed.getAttrCode(feed.tracking), true, true);//Add Record
		if ((feed != null && feed.waterMark != null && feed.waterMark.length() > 10) || feed != null && feed.waterMarkWide != null && feed.waterMarkWide.length() > 10) {
			runOnUiThread(new Runnable() {
				public void run() {
					Display display = getWindowManager().getDefaultDisplay();
					int height = display.getHeight();
					Drawable drawable = null;
					if (height <= 240) {
						if (feed.waterMark != null && feed.waterMark.length() > 10) {
							drawable = LoadImageFromWebOperations(feed.waterMarkWide);
							if (drawable == null)
								drawable = LoadImageFromWebOperations(feed.waterMarkWide);
						} else {
							drawable = LoadImageFromWebOperations(feed.waterMark);
							if (drawable == null)
								drawable = LoadImageFromWebOperations(feed.waterMarkWide);
						}
					} else {
						if (feed.waterMark != null && feed.waterMark.length() > 10) {
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
		DataModel.sSelf.storeObject(DMKeys.FEED_COMMUNITY_SEARCH, feed);

		if (feed.entry.size() <= 0) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					//					showSimpleAlert("Sorry!", getString(R.string.search_community_not_found)/*feed.title*/);
					if (feed == null) {
						((TextView) findViewById(R.id.community_noContent)).setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.community_noContent)).setText(getString(R.string.result_not_found));
						mMainList.setVisibility(View.GONE);
						//						return;
					} else {
						((TextView) findViewById(R.id.community_noContent)).setVisibility(View.GONE);
						mMainList.setVisibility(View.VISIBLE);
					}
				}
			});

			return;
		}
		runOnUiThread(new Runnable() {
			public void run() {
				((TextView) findViewById(R.id.community_noContent)).setVisibility(View.GONE);
				mMainList.setVisibility(View.VISIBLE);
				//int lastPos = mMainList.getLastVisiblePosition() - 1;
				mMainList.setAdapter(mSearchAdapter);
				if(va_pos!=-1 && mMainList!= null){
					if(va_pos-2 > 0)
						mMainList.setSelection(va_pos-2);
				}
				//
				if (null != feed && null != feed.entry) {
					if (feed.entry.size() > 10) {
						startThumbnailThread(10);
					} else {
						startThumbnailThread(feed.entry.size());
					}
				}
				if (aTitle != null && aTitle.trim().length() > 0)
					((TextView) findViewById(R.id.community_screenTitle)).setText(aTitle);
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
	}

	private Drawable LoadImageFromWebOperations(String url) {
		if(1==1)
			return null ;
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("Exc=" + e);
			makeToast(e.toString());
			return null;
		} catch (OutOfMemoryError e) {
			makeToast(e.toString());
			return null;
		}
	}

	public void onTaskCallback(Object parameter, byte req) {
		if (lodingCanceled)
			return;

		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case HTTP_TIMEOUT:
			break;
		case DATA_CALLBACK:
			if (null != mResponseData) {
				parseXMLData(null);
			} else {
				showSimpleAlert("Error:", "Unable to retrieve\n" + null);
			}
			break;
		case ERROR_CALLBACK:
			//showSimpleAlert("Error", getString(R.string.unable_to_connect_pls_try_later));
			break;
		}
		if(rTDialog != null)
			rTDialog.dismiss();
		super.cancelThread();
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

		public CommunityAdapter(Context context) {
			this.context = context;
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

		public View getView(final int position, View convertView, ViewGroup arg2) {
			View listItemView = null;
			try {
				final CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				if (convertView != null) {
					ProgressBar progressBar = ((ProgressBar) convertView.findViewById(R.id.progressbar));
					if (progressBar != null) {
						convertView = null;
					}
				}
				if (position == getCount() - 1) {
					String aURL = null;
					try {
						/*String rel = CommunityFeed.getAttrRel(feed.link);
						if (rel.equalsIgnoreCase("next")) {
							aURL = CommunityFeed.getAttrHref(feed.link);
						}*/
						aURL = CommunityFeed.getAttrRel(feed.links,"next");
						if(aURL == null || aURL.equalsIgnoreCase("NA") || aURL.length() <= 0)
							aURL = feed.nexturl ;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (aURL != null && aURL.length() > 3 && !aURL.equalsIgnoreCase("NA")) {
						LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						listItemView = inflator.inflate(R.layout.loading_layout, null);
						listItemView.setOnClickListener(SearchCommunityActivity.this);
						listItemView.setId(LOADING_ID);
						if (requestednextPage) {
							ProgressBar progressBar = ((ProgressBar) listItemView.findViewById(R.id.progressbar));
							TextView textView = ((TextView) listItemView.findViewById(R.id.loadingtext));
							progressBar.setVisibility(View.VISIBLE);
							textView.setText(getString(R.string.loading_dot));
						}
						return listItemView;
					}
				}
				if (convertView == null) {
					listItemView = LayoutInflater.from(context).inflate(R.layout.find_community_list_row, null);
				} else {
					listItemView = (View) convertView;
				}
				if (entry == null)
					return listItemView;

				LinearLayout linearluot_out = (LinearLayout) listItemView.findViewById(R.id.linearluot_out);
				linearluot_out.setTag(position);
				linearluot_out.setOnClickListener(SearchCommunityActivity.this);
				LinearLayout mediaLayout = (LinearLayout) listItemView.findViewById(R.id.communityInfoLayout1);
				mediaLayout.setTag(position);

				ImageView imageView = null;
				imageView = ((ImageView) listItemView.findViewById(R.id.communityImage));
				imageView.setImageBitmap(null);//setImageResource(R.drawable.def2);
				imageView.setTag(position);
				String n = entry.thumbUrl.trim() ;
				if (selectedTab == 4) {
					n = entry.userName ;
				}				 				

				//				Bitmap bitmap = mImageLoader.getCacheBitmap(n);
				//				if (null != bitmap && !entry.userName.trim().equalsIgnoreCase("Rocketalk Help")) {
				//					imageView.setImageBitmap(bitmap);
				//				} else {
				//					if(bitmap != null){
				//						imageView.setImageBitmap(bitmap);
				//					}
				//				}

				//				entry.thumbUrl

				//				Log.i(TAG, "com name - "+entry.groupName +", URL - "+entry.thumbUrl);
				/*imageManager.loadForCommunity(true);
				imageManager.download(entry.thumbUrl, imageView, handler, ImageDownloader.TYPE_THUMB_BUDDY);*/

				imageLoader.displayImage(entry.thumbUrl,imageView,options);
				//				if(!entry.thumbUrl.trim().equals(""))
				//			    {
				//			    }
				//				else
				//			    {
				//			     imageView.setVisibility(View.VISIBLE);
				//			     imageView.setBackgroundResource(R.drawable.attachicon);
				//			    }

				TextView textView = ((TextView) listItemView.findViewById(R.id.communityName));
				if (selectedTab == 1 || selectedTab == 3) {//My Communities
					if (entry.groupName != null && entry.groupName.trim().length() > 0) {
						textView.setText(entry.groupName);
					}
				} else if (selectedTab == 2) {//Recommended Communities
					if (entry.displayName != null && entry.displayName.trim().length() > 0) {
						textView.setText(entry.displayName);
					}
				} else if (selectedTab == 4) {//Pending Request
					if (entry.userName != null && entry.userName.trim().length() > 0) {
						textView.setText(entry.userName);
					}
				}

				if(entry.joinOrLeave!=null && entry.joinOrLeave.length()>2){
					//					joinOrLeave
					Button view = (Button)listItemView.findViewById(R.id.join_leave);

					view.setTag(entry) ;
					view.setVisibility(View.VISIBLE);
					//					view.setText(entry.joinOrLeave) ;
					if(entry.joinOrLeave.equalsIgnoreCase("join")){
						//						view.setText(getString(R.string.follow)) ;
						view.setBackgroundResource(R.drawable.com_following);
						//						view.setCompoundDrawables(left, top, right, bottom)

						Drawable search_unsel = getResources().getDrawable(
								R.drawable.x_arrows_right);
						Rect r = null;

						Drawable[] d = view.getCompoundDrawables();
						r = null;
						if (d[2] != null)
							r = d[2].getBounds();
						if (r != null)
							search_unsel.setBounds(r);
						//						view.setCompoundDrawables(null, null,  search_unsel, null);

						//view.set
						//@drawable/x_arrows_left
					}
					else{
						//						view.setText(getString(R.string.unfollow)) ;
						view.setBackgroundResource(R.drawable.com_follow);
						Drawable search_unsel = getResources().getDrawable(
								R.drawable.x_arrows_left);
						Rect r = null;

						Drawable[] d = view.getCompoundDrawables();
						r = null;
						if (d[2] != null)
							r = d[2].getBounds();
						if (r != null)
							search_unsel.setBounds(r);
						//						view.setCompoundDrawables(null, null,  search_unsel, null);
					}
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							joinLeaveCommunity(v);
							mSearchAdapter.notifyDataSetChanged();
							mSearchAdapter.notifyDataSetInvalidated();
							int index = mMainList.getFirstVisiblePosition();
							View vo = mMainList.getChildAt(0);
							int top = (vo == null) ? 0 : vo.getTop();

							// notify dataset changed or re-assign adapter here

							// restore the position of listview
							mMainList.setSelectionFromTop(index, top);
							//mMainList.setSelection(position);
						}
					});
					if(entry.ownerUsername.equalsIgnoreCase(SettingData.sSelf.getUserName())){
						view.setVisibility(View.GONE);
					}
				}
				else
					listItemView.findViewById(R.id.join_leave).setVisibility(View.GONE);


				textView = ((TextView) listItemView.findViewById(R.id.communityInfo));
				StringBuilder builder = new StringBuilder();
				boolean flag = false;
				if (selectedTab == 1 || selectedTab == 3) {//My Communities and Communities of Recommended Community
					if (entry.noOfMember > 0) {
						builder.append(entry.noOfMember+" ").append(getString(R.string.member));
						if (entry.noOfMember > 1) {
							builder.append("s");
						}
						flag = true;
					}
					else
					{
						builder.append(", 0"+" ").append(getString(R.string.online));
						//						if (entry.noOfMessage > 1) {
						//							builder.append("s");
						//						}
					}
					//					if (entry.noOfOnlineMember > 0) {
					//						if (flag) {
					//							builder.append(", ");
					//						}
					//						builder.append(entry.noOfOnlineMember+" ").append(getString(R.string.online));
					//						flag = true;
					//					}else
					//					{
					//						builder.append(", 0"+" ").append(getString(R.string.online));
					//						//						if (entry.noOfMessage > 1) {
					//						//							builder.append("s");
					//						//						}
					//					}
					if (entry.noOfMessage > 0) {
						if (flag) {
							builder.append(", ");
						}
						builder.append(entry.noOfMessage+" ").append(getString(R.string.posts));
						if (entry.noOfMessage > 1) {
							builder.append("s");
						}
					}else
					{
						builder.append(", 0"+" ").append(getString(R.string.posts));
						//						if (entry.noOfMessage > 1) {
						//							builder.append("s");
						//						}
					}
				} else if (selectedTab == 2) {//Recommended Communities
					if (entry.count > 0) {
						builder.append(entry.count).append(" Communit");
						if (entry.count > 1) {
							builder.append("ies");
						} else {
							builder.append("y");
						}
					}
				}

				if (selectedTab == 4) {//Pending Request
					if (entry.groupName != null && entry.groupName.trim().length() > 0) {
						builder.append("wants to join ");
						builder.append("<b>" + entry.groupName + "</b>");
					}
				}
				textView.setText(Html.fromHtml(builder.toString()));

				textView = (TextView) listItemView.findViewById(R.id.fn_ln);
				textView.setVisibility(View.GONE);
				if (selectedTab == 4) {//Pending Request
					if ((entry.firstName != null && entry.firstName.trim().length() > 0) || (entry.lastName != null && entry.lastName.trim().length() > 0)) {
						textView.setVisibility(View.VISIBLE);
						textView.setText(entry.firstName + " " + entry.lastName);
						textView.setVisibility(View.VISIBLE);
					}
				} else {
					textView.setVisibility(View.GONE);
				}

				textView = ((TextView) listItemView.findViewById(R.id.modratedCommunityLabel));
				if (selectedTab == 4) {
					textView.setVisibility(View.GONE);
				} else {
					if (entry.moderated.equalsIgnoreCase("YES")) {
						textView.setVisibility(View.VISIBLE);
					} else {
						textView.setVisibility(View.GONE);
					}
				}
				//				textView.setText(textView.getText()+" : "+entry.moderated);

				textView = ((TextView) listItemView.findViewById(R.id.privateCommunityLabel));
				if (selectedTab == 4) {
					textView.setVisibility(View.GONE);
				} else {
					if (entry.publicCommunity.equalsIgnoreCase("NO")) {
						textView.setVisibility(View.VISIBLE);
					} else {
						textView.setVisibility(View.GONE);
					}
				}

				textView = ((TextView) listItemView.findViewById(R.id.createdOnWithPermission));
				if (selectedTab == 1 || selectedTab == 3) {
					textView.setVisibility(View.GONE);
					Drawable image = null;
					if (entry.memberPermission.equalsIgnoreCase("OWNER")) {
						image = getResources().getDrawable(R.drawable.communityowner);
					} else if (entry.memberPermission.equalsIgnoreCase("MEMBER")) {
						image = getResources().getDrawable(R.drawable.communitymembers);
					} else if (entry.memberPermission.equalsIgnoreCase("ADMIN")) {
						image = getResources().getDrawable(R.drawable.communityadmin);
					}
					if (image != null) {
						textView.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
					}else
						textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					if (entry.createdOn != null && entry.createdOn.trim().length() > 0) {
						StringBuilder strBuilder = new StringBuilder();
						strBuilder.append(" "+getString(R.string.created_on)+" ");
						strBuilder.append(DateFormatUtils.convertDate(entry.createdOn, "yyyy-MM-dd", "MMM dd, yyyy",SearchCommunityActivity.this));
						textView.setText(strBuilder.toString());
					}
				} else if (selectedTab == 2 || selectedTab == 4) {
					textView.setVisibility(View.GONE);
				}


				String[] receivedTagArray = entry.tags.split(",");
				String[] temp = new String[receivedTagArray.length];
				String[] temp2 = new String[receivedTagArray.length];
				if(receivedTagArray != null && receivedTagArray.length > 0)
				{
					if(searchedHashTag != null && searchedHashTag.trim().length() > 0)
					{
						if(receivedTagArray.length < 4)
							temp = receivedTagArray;
						else
						{
							for(int i = 0, j = 0; i < receivedTagArray.length; i++)
							{
								if(receivedTagArray[i].startsWith(searchedHashTag))
									temp[j++] = receivedTagArray[i];
								else
									temp2[i] = receivedTagArray[i];
							}
							if(temp[1] == null)
							{
								for(int i = 0; i < temp2.length; i++)
								{
									if(temp2[i] != null)
									{
										temp[1] = temp2[i];
										temp2[i] = null;
										break;
									}
								}
								for(int i = 0; i < temp2.length; i++)
								{
									if(temp2[i] != null)
									{
										temp[2] = temp2[i];
										temp2[i] = null;
										break;
									}
								}
							}
							else if(temp[2] ==  null)
							{
								for(int i = 0; i < temp2.length; i++)
								{
									if(temp2[i] != null)
									{
										temp[2] = temp2[i];
										temp2[i] = null;
									}
								}
							}
						}
					}
					else
						temp = receivedTagArray;
				}

				final String[] tagsArray = temp;
				receivedTagArray = null;
				temp = null;
				temp2 = null;
				searchedHashTag = null;
				if(tagsArray != null){
					if(tagsArray[0] != null && !tagsArray[0].equals("")){
						final TextView hashTag01 = (TextView)listItemView.findViewById(R.id.hashtag_text01);
						hashTag01.setText(" #"+tagsArray[0]);
						hashTag01.setVisibility(View.VISIBLE);
						hashTag01.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//								System.out.println("Tag 01 = "+tagsArray[0].replace(" #", ""));
								//								onTagClickAction(tagsArray[0].replace(" #", ""));
								onTagClickAction(tagsArray[0]);
							}
						});
					}
					if(tagsArray.length > 1 && tagsArray[1] != null && !tagsArray[1].equals("")){
						final TextView hashTag02 = (TextView)listItemView.findViewById(R.id.hashtag_text02);
						hashTag02.setText(" #"+tagsArray[1]);
						hashTag02.setVisibility(View.VISIBLE);
						hashTag02.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//								System.out.println("Tag 02 = "+tagsArray[1].replace(" #", ""));
								//								onTagClickAction(tagsArray[1].replace(" #", ""));
								onTagClickAction(tagsArray[1]);
							}
						});
					}
					if(tagsArray.length > 2 && tagsArray[2] != null && !tagsArray[2].equals("")){
						final TextView hashTag03 = (TextView)listItemView.findViewById(R.id.hashtag_text03);
						hashTag03.setText(" #"+tagsArray[2]);
						hashTag03.setVisibility(View.VISIBLE);
						hashTag03.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//								System.out.println("Tag 03 = "+tagsArray[2].replace(" #", ""));
								//								onTagClickAction(tagsArray[2].replace(" #", ""));
								onTagClickAction(tagsArray[2]);
							}
						});
					}
				}
				ImageView moreImage = (ImageView) listItemView.findViewById(R.id.moreButton);
				moreImage.setTag(position);
				if (selectedTab == 1 || selectedTab == 3 || selectedTab == 4) {
					moreImage.setVisibility(View.GONE);
				} else if (selectedTab == 2) {
					moreImage.setVisibility(View.VISIBLE);
				}

				if (selectedTab == 4) {
					StringBuffer key = new StringBuffer();
					key.append(entry.groupName).append("_").append(entry.userName);
					Hashtable<String, Hashtable<String, String>> table = entry.buttons.get(key.toString());
					for (Iterator<String> iterator = table.keySet().iterator(); iterator.hasNext();) {
						String key1 = iterator.next();
						Hashtable<String, String> attrs = table.get(key1);
						View button = null;
						if (key1.equalsIgnoreCase("Accept")) {
							button = listItemView.findViewById(R.id.community_acceptButton);
						} else if (key1.equalsIgnoreCase("Decline")) {
							button = listItemView.findViewById(R.id.community_declineButton);
						} else if (key1.equalsIgnoreCase("Ignore")) {
							button = listItemView.findViewById(R.id.community_ignoreButton);
						}
						if (button != null) {
							button.setVisibility(View.VISIBLE);
							button.setTag((CommunityFeed.getAttrAction(attrs) + "~~" + position));
						}
					}
				} else {
					listItemView.findViewById(R.id.community_acceptButton).setVisibility(View.GONE);
					listItemView.findViewById(R.id.community_declineButton).setVisibility(View.GONE);
					listItemView.findViewById(R.id.community_ignoreButton).setVisibility(View.GONE);
				}
				return listItemView;
			} catch (Exception e) {
				return listItemView;
			}

		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (SEARCH_TYPE) {
		case SEARCH_BY_CATEGORY:
			break;
		default:
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				int maxCount = view.getLastVisiblePosition() - view.getFirstVisiblePosition() + 1;
				startThumbnailThread(maxCount);
			}
		}
	}

	public void onThumbnailResponse(final ThumbnailImage value, byte[] data) {
		runOnUiThread(new Runnable() {
			public void run() {
				int index = mMainList.getFirstVisiblePosition();
				index = value.mIndex - index;
				View view = mMainList.getChildAt(index);
				if (null == view)
					return;
				ImageView img = (ImageView) view.findViewById(R.community_list_row.communityImage);
				if (null != img) {
					img.setVisibility(View.VISIBLE);
					img.setImageBitmap(value.mBitmap);
				}
			}
		});
	}

	private void calledAction(View view) {
		runOnUiThread(new Runnable() {
			public void run() {
				for (byte i = 0; i < totalButtons.length; i++) {
					if (totalButtons[i] != null)
						totalButtons[i].setBackgroundResource(R.drawable.black_butn);
				}
			}
		});
		Button button = ((Button) view);
		categoryName = (button.getText().toString());
		button.setBackgroundResource(R.drawable.roundedrectgradient_green);
	}

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH ) {
			String name = v.getText().toString();
			if (name != null && !name.equals("")) {
				getRequestParms = "?groupname=" + URLEncoder.encode(name) + "&category=&user=" + URLEncoder.encode(user) + "&owner=";
				if(Logger.ENABLE)Logger.debug("getRequestParms", getRequestParms);
				feed = null;
				postParam.clear();
				if(name!=null && name.trim().length()>0)
					postParam.put("groupname", name);
				//searchCommunity(name,1);
				hashTagLayout.setVisibility(View.GONE);
				scrollview_tag.setVisibility(View.GONE);
				if(isInternetOn()){
					if(name.contains("#"))
					{
						String str="";
						//	String str=null;

						Character c1 = new Character(name.charAt(0));
						Character c2 = new Character('#');
						int res = c1.compareTo(c2);
						if( res == 0 ){
							str = name.substring(1,name.length());
						}
						/*	if(Character.toString(name.charAt(0)).matches("#")){

						}*/
						/*	if( Character.toString(name.charAt(name.length())).equals("#")){
							str = name.substring(0,(name.length()-1));
						}*/

						//searchedHashTag = strValue.substring(1,strValue.length());
						searchedHashTag = str;
						postParam.clear();
						postParam.put("hashtag", str);
						executeRequestData("http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/searched/hashtag?", null, null);
					}
					/*	
					if( Character.toString(name.charAt(0)).equals("#")){
						{
							searchedHashTag = name.substring(1,name.length());
							executeRequestData("http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/searched/hashtag?hashtag="+name.substring(1,name.length()), null, null);
						}
					}*/else{
						//					executeRequestData(SEARCH_COMMUNITY+getRequestParms, null, null);
						executeRequestData(SEARCH_COMMUNITY, null, null);
					}
				}else
				{
					((TextView) findViewById(R.id.community_noContent)).setText(getResources().getString(R.string.network_fail));
					((TextView) findViewById(R.id.community_noContent)).setVisibility(View.VISIBLE);
				}

				return false;
			}
		}
		return false;
	}
	public void searchCommunity(String strValue,int val){
		if(isInternetOn()){

			if(val==0)
			{
				getRequestParms = "?groupname=" + URLEncoder.encode(strValue) + "&category="+ URLEncoder.encode(strValue) +"&user=" + URLEncoder.encode(user) + "&owner=";
			}
			else
			{
				getRequestParms = "?";
			}

			if(Logger.ENABLE)
				Logger.debug("getRequestParms", getRequestParms);
			feed = null;

			postParam.clear();
			if(strValue!=null && strValue.trim().length()>0)
				if(val==0)
				{
					postParam.put("groupname", strValue);}
			if(val==1)
			{
				postParam.put("category", strValue);}
			if(!strValue.equals(getResources().getString(R.string.popular))){
				if(strValue.contains("#"))
				{
					String str = null;
					if( Character.toString(strValue.charAt(0)).equals("#")){
						str = strValue.substring(1,strValue.length());
					}
					searchedHashTag = str;
					postParam.clear();
					postParam.put("hashtag", str);
					executeRequestData("http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/searched/hashtag?", null, null);
				}else{
					if(UrlIs.equals("")){
						executeRequestData(SEARCH_COMMUNITY + getRequestParms, null, null);
					}else
					{
						executeRequestData(UrlIs, null, null);
					}
				}
			}else
			{
				hashTagLayout.setVisibility(View.GONE);
				scrollview_tag.setVisibility(View.GONE);
				executeRequestData("http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/attributed/popular", null, null);
			}

		}else
		{
			((TextView) findViewById(R.id.community_noContent)).setText(getResources().getString(R.string.network_fail));
			((TextView) findViewById(R.id.community_noContent)).setVisibility(View.VISIBLE);
		}
	}
}