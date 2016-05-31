package com.kainat.app.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.helper.CommunityTable;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.CommunityFeed.ReportedByUser;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.format.SettingData;
import com.kainat.app.autocomplete.tag.RoundedBackgroundSpan;
import com.rockerhieu.emojicon.EmojiconTextView;
public class CommunityProfileNewActivity  extends UICommunityManager{
	ImageView imageView,imgEditCommunity,reportBtn;
	ProgressBar channel_detail_progress;
	TextView edt_com_desc;
	TextView public_comm;
	TextView comm_name;
	LinearLayout broadcast_layout;
	TextView title_community,community_edit,community_name,txt_owner_name,txt_postcount,txt_follow_count,txt_comm_desc,txt_report_count;
	ProgressDialog rTDialog;
	String groupName,group_desc,group_pic, tags_name,groupid, adminState;
	//ImageDownloader imageManager;
	ThumbnailReponseHandler handler;
	LinearLayout layout_post_btn,layout_follow_btn,linearlayout_push;
	private CommunityFeed.Entry entry = null;
	Button btn_join_leave,btn_post;
	ToggleButton toggel_btn_pushnotification;
	ToggleButton toggel_btn_broadcast;
	//private CharSequence text = "@ #RAVINDRA@ @ #ManojSingh@ @ #NarendraModi@ @ #Prime@ @ #AndroidDeveloper@ @ #Ios@ ";
	private CharSequence text = "";
	/*"@ #RAVINDRA@ @ #ManojSingh@ @ #NarendraModi@ @ #Prime@ @ #AndroidDeveloper@ @ #Ios@ @ Rajveer#@ @ #AajTak@ @ #Media@ @ #NaveeJindal@ @ #ShrdhaKapur@ @ #RajivRanjan@ @ #Amitabh@ @ #Delhi@ @ #IPL2015@ @ #NasruddinShah@" +
			" @ #IndiaNews@ @ #TimesNow@ @ #PoliticalStands@ @ #RamMandirIssue@ @ #KalHonHo@ @ #SundaySpecial@";*/

	public final String PUSH_URL = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings";
	public final String BROAD_CAST_URL = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/settings/mute";
	private boolean isEdit;
	int pushcounter = 0;
	private static final String TAG = CommunityProfileNewActivity.class.getSimpleName();
	private boolean log = false;
	private boolean changeFromNetwork;
	private TextView tagTextView ;
	GetChannelDetail getChannelDetails;
	RelativeLayout relative_report;
	boolean channelPicView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Stop Channel Refresh
		stopChannelRefresh();
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.community_profile_new);

	//	imageManager = new ImageDownloader();
		handler      = new ThumbnailReponseHandler() {

			@Override
			public void onThumbnailResponse(ThumbnailImage value,byte[] data) {
				try {
				}
				catch (Exception e) {
				}
			}
		};	
		init();
		//Set here channel default data, that is being carried from the previous activity.
		setDataCommunityProfile();
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Channel Profile Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(CommunityProfileNewActivity.this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(CommunityProfileNewActivity.this).reportActivityStop(this);
    }
	
	private void stopChannelRefresh(){
		//Stop Channel Refresh
		//Set refresh time to 10 secs.
		UIActivityManager.startChannelRefresh = false;
		RefreshService.setRefreshTime(5000);
	}
	private void startChannelRefresh(){
		if(!UIActivityManager.startChannelRefresh){
			UIActivityManager.startChannelRefresh = true;
			RefreshService.setRefreshTime(5000);
		}
	}

	private String getTagsFormat(){
		String tagsCode = "";
		if(tags_name != null && tags_name.trim().length() > 0){
			String[] tagArray = tags_name.split(",");
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(channelPicView)
			channelPicView = false;
		else{
			entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY+ "COMM");
			if(getIntent().getExtras() != null && !isEdit)
				isEdit = getIntent().getExtras().getBoolean("EDIT");
			if(entry != null)
			{
				groupid		 = ""+entry.groupId;
				groupName    = entry.groupName;
				group_desc   = entry.description;
				group_pic    = entry.thumbUrl;
				tags_name    = entry.tags;
				adminState   = entry.adminState;
			}
			else
			{
				groupName    = getIntent().getStringExtra("group_name");
				groupid    = getIntent().getStringExtra("group_id");
				group_desc   = getIntent().getStringExtra("group_desc");
				group_pic    = getIntent().getStringExtra("group_pic");
				tags_name = getIntent().getStringExtra("tags_name");
			}
			if(title_community != null)
				title_community.setText(groupName);
			tagTextView.setText("");
			tagTextView.setVisibility(View.GONE);
			getChannelDetails = new GetChannelDetail();
			getChannelDetails.execute("");
		}
	}
	
	@Override
	protected void onPause() {
		feed = null;
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 501) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle b = data.getExtras();
				if (b != null) {
					isEdit = b.getBoolean("EDIT");
				}  
			} else if (resultCode == 0) {
			}
		}
	}  

	private void drawTags(){
		tagTextView.setVisibility(View.VISIBLE);
		MovementMethod m = tagTextView.getMovementMethod();
		if ((m == null) || !(m instanceof LinkMovementMethod))
		{
			tagTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}
		boolean isTrue = true;
		while(isTrue){
			text = setSpanBetweenTokens(text, "@",  new RoundedBackgroundSpan(getResources().getColor(R.color.tag_bg_color), getResources().getColor(R.color.tag_text_color))//);
			, new ClickableSpan()
			{
				@Override
				public void onClick(View v)
				{
					// When the span is clicked, show some text on-screen.
					int i = ((TextView) v).getSelectionStart();
					int j = ((TextView) v).getSelectionEnd();
					String tagValue = ((TextView) v).getText().toString().substring(i, j).replace(" #", "");
					//					Toast.makeText(CommunityProfileNewActivity.this, "Tocken Clicked = "+tagValue, Toast.LENGTH_SHORT).show();
					//					System.out.println("Print Value = "+tagValue + " --> L,"+tagValue.length());

				}
			});
			if(text.toString().contains("@"))
				isTrue = true;
			else
				isTrue = false;
		}
		tagTextView.setText(text);
	}
	private void init() {
		// TODO Auto-generated method stub
		txt_owner_name	 = (TextView)findViewById(R.id.txt_owner_name);
		txt_postcount	 = (TextView)findViewById(R.id.txt_postcount);
		txt_follow_count = (TextView)findViewById(R.id.txt_follow_count);
		title_community  = (EmojiconTextView)findViewById(R.id.title_community);
		txt_comm_desc    = (EmojiconTextView)findViewById(R.id.txt_comm_desc);
		txt_report_count = (TextView)findViewById(R.id.report_counter);
		imageView        = (ImageView)findViewById(R.id.community_pic);
		channel_detail_progress  = (ProgressBar)findViewById(R.id.channel_detail_progress);
		imgEditCommunity = (ImageView)findViewById(R.id.edit_community);
		btn_join_leave   = (Button) findViewById(R.id.btn_join_leave);
		btn_post   		 = (Button) findViewById(R.id.btn_post);
		layout_post_btn  = (LinearLayout)findViewById(R.id.layout_post_btn);
		layout_follow_btn=  (LinearLayout)findViewById(R.id.layout_follow_btn);
		linearlayout_push=  (LinearLayout)findViewById(R.id.linearlayout_push);
		toggel_btn_pushnotification = (ToggleButton)findViewById(R.id.toggel_btn_pushnotification);
		broadcast_layout = (LinearLayout)findViewById(R.id.linearlayout_broadcast);
		toggel_btn_broadcast = (ToggleButton)findViewById(R.id.toggel_btn_broadcast);
		tagTextView = (TextView) findViewById(R.id.tag_text);
		//-----------------------------------
		relative_report = (RelativeLayout)findViewById(R.id.relative_report);
		reportBtn = (ImageView)findViewById(R.id.edit_report);
		relative_report.setVisibility(View.GONE);
		txt_report_count = (TextView)findViewById(R.id.report_counter);
		//-----------------------------------
		reportBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ReportedRequest request = new ReportedRequest();
				request.execute();
			}
		});
	/*	relative_report.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(entry != null){
					DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entrys);
					Intent intent = new Intent(CommunityProfileNewActivity.this, CommunityChatActivity.class);
					intent.putExtra("SYSTEM_MSG_VIEW", true);
					if(deleteMessageAPI != null && deleteMessageAPI.length() > 0){
						intent.putExtra("DELETE_MESSAGE_URL", deleteMessageAPI);
						intent.putExtra("DELETE_MESSAGE_ID", message.messageId);
						startActivityForResult(intent, PARENT_ACTIVITY_CALLBACK);
					}
				}
			}
		});*/
		txt_owner_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(entry != null){
					Intent	intent = new Intent(CommunityProfileNewActivity.this, ProfileViewActivity.class);
					intent.putExtra("USERID", ""+entry.ownerId) ;
					intent.putExtra("CallFrom",1);
					startActivity(intent);
				}
			}
		});
		
		layout_post_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(UIActivityManager.directFromCreateChannel)
					finish();
				else
				{
					Intent intent = new Intent(CommunityProfileNewActivity.this, CommunityChatActivity.class);
					intent.putExtra("message_url", entry.messageUrl);
					intent.putExtra("tags_name", entry.tags);
					intent.putExtra("group_name", entry.groupName);
					intent.putExtra("group_desc", entry.description);
					intent.putExtra("group_pic", entry.thumbUrl);
					startActivity(intent);
					finish();
				}
			}
		});
		imgEditCommunity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(entry != null)
					entry.tags = tags_name;
				DataModel.sSelf.storeObject(DMKeys.ENTRY, entry);
				//				UIActivityManager.directFromCreateChannel = true;
				feed = null;
				Intent intent = new Intent(CommunityProfileNewActivity.this, CreateCommunityActivity.class);
				intent.putExtra("EDIT", true);
				startActivityForResult(intent, 501);
			}
		});

		layout_follow_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommunityProfileNewActivity.this,CommunityMemberActivity.class);
				intent.putExtra("member_url", entry.searchMemberUrl);
				intent.putExtra("OwnerUserName", entry.ownerUsername);
				intent.putExtra("title",entry.groupName);
				DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
				startActivity(intent);
			}
		});
		btn_join_leave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				joinLeaveCommunity();
			}
		});

		btn_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(UIActivityManager.directFromCreateChannel)
					finish();
				else
				{
					Intent intent = new Intent(CommunityProfileNewActivity.this, CommunityChatActivity.class);
					intent.putExtra("message_url", entry.messageUrl);
					intent.putExtra("tags_name", entry.tags);
					intent.putExtra("group_name", entry.groupName);
					intent.putExtra("group_desc", entry.description);
					intent.putExtra("group_pic", entry.thumbUrl);
					startActivity(intent);
				}
			}
		});
		findViewById(R.id.prev_communites).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DataModel.sSelf.removeObject(DMKeys.ENTRY + "COMM");
				onBackPressed();
			}
		});
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(group_pic != null && !group_pic.trim().equals(""))
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					if(group_pic.startsWith("http://"))
						intent.setDataAndType(Uri.parse(group_pic), "image/*");
					else
						intent.setDataAndType(Uri.parse("file://" + group_pic), "image/*");
					channelPicView = true;
					startActivity(intent);
				}
			}
		});
		setDataCommunityProfile();
		toggel_btn_pushnotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				String val="TURNOFF";
				if(isChecked)
				{
					val="TURNON";

				}else{
					val="TURNOFF";
				}
				//if(entry.pushNotif != null && !entry.pushNotif.equalsIgnoreCase("TURNON")){
				if(pushcounter !=0)
					method_call(PUSH_URL, val);
				pushcounter = 1;
				//}
			}
		});
		toggel_btn_broadcast.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(changeFromNetwork)
				{
					changeFromNetwork = false;
					return;
				}
				String val="unmute";
				if(isChecked)
				{
					val="mute";
					final String broadcast = val;
					new AlertDialog.Builder(CommunityProfileNewActivity.this).setMessage(getString(R.string.broadcast_alert))
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {
							method_call(BROAD_CAST_URL, broadcast);
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {
							// method_call(BROAD_CAST_URL, broadcast);
							toggel_btn_broadcast.setChecked(false);
						}
					}).setCancelable(false).show();

				}else{
					val="unmute";
					method_call(BROAD_CAST_URL, val);
				}
			}
		});
	}
	/**
	 * Given either a Spannable String or a regular String and a token, apply
	 * the given CharacterStyle to the span between the tokens, and also remove
	 * tokens.
	 * <p>
	 * For example, {@code setSpanBetweenTokens("Hello ##world##!", "##",
	 * new ForegroundColorSpan(0xFFFF0000));} will return a CharSequence {@code
	 * "Hello world!"} with {@code world} in red.
	 *
	 * @param text The text, with the tokens, to adjust.
	 * @param token The token string; there should be at least two instances of
	 *            token in text.
	 * @param cs The style to apply to the CharSequence. WARNING: You cannot
	 *            send the same two instances of this parameter, otherwise the
	 *            second call will remove the original span.
	 * @return A Spannable CharSequence with the new style applied.
	 *
	 * @see http://developer.android.com/reference/android/text/style/CharacterStyle.html
	 */
	public  CharSequence setSpanBetweenTokens(CharSequence text,
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
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		if(rTDialog != null && rTDialog.isShowing()){
//			rTDialog.dismiss();
//			startChannelRefresh();
//			return;
//		}
		if(channel_detail_progress != null && channel_detail_progress.getVisibility() == View.VISIBLE){
			channel_detail_progress.setVisibility(View.GONE);
			startChannelRefresh();
			return;
		}
		if(getChannelDetails != null && !getChannelDetails.isCancelled())
			getChannelDetails.cancel(true);
		DataModel.sSelf.removeObject(DMKeys.ENTRY + "COMM");
		UIActivityManager.backFromUpdateChannel = false;
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	// Follow and Join
	//
	//
	//
	public void joinLeaveCommunity(){
		StringBuilder buffer = new StringBuilder(
				"msgto:Community Manager<a:communitymgr>?text="+entry.joinOrLeave+"::Name##"+entry.groupName);
		clickHandler(buffer.toString(),false);
		if(entry.joinOrLeave.equalsIgnoreCase("join"))
		{
			entry.joinOrLeave = "leave";
			setMemberPermission(entry.groupName, "member");
			//			UIActivityManager.refreshChannelList = true;
		}
		else
		{
			entry.joinOrLeave = "join";
			setMemberPermission(entry.groupName, "user");
			removeChannel(entry.groupName);
		}
		refershFollowUnfollow();
	}
	private void setMemberPermission(String channelName, String permissionType)
	{
		try
		{
			Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
					new String[] { ""+channelName}, null, null, null);
			if(cursor.getCount() == 1)
			{
				ContentValues values = new ContentValues();
				values.put(CommunityTable.MEMBER_PERMISSION, permissionType);

				int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
						new String[] {channelName});
				//				Log.i(TAG, "setMemberPermission :: for Channel : "+channelName+", to : "+permissionType);
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeChannel(String channelName)
	{
		try
		{
			Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
					new String[] { ""+channelName}, null, null, null);
			if(cursor.getCount() == 1)
			{
				int row  = BusinessProxy.sSelf.sqlDB.delete(CommunityTable.TABLE, CommunityTable.GROUP_NAME +"= ? ",
						new String[] {channelName});
				if(row > 0)
					UIActivityManager.refreshChannelList = true;
				
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refershFollowUnfollow() {
		// TODO Auto-generated method stub
		if(entry != null)
			if(entry.joinOrLeave.equalsIgnoreCase("join")){
				btn_join_leave.setBackgroundResource(R.drawable.com_following);
				btn_post.setVisibility(View.GONE);
				linearlayout_push.setVisibility(View.GONE);
				timelineFollow = false;
			}else
			{
				btn_join_leave.setBackgroundResource(R.drawable.com_follow);
				linearlayout_push.setVisibility(View.VISIBLE);
				timelineFollow = true;
			}
	}

	private void setDataCommunityProfile(){
		entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY+ "COMM");
		if(getIntent().getExtras() != null && !isEdit)
			isEdit = getIntent().getExtras().getBoolean("EDIT");
		if(entry != null)
		{
			groupid		 = ""+entry.groupId;
			groupName    = entry.groupName;
//			group_desc   = entry.description;
			group_pic    = entry.thumbUrl;
			tags_name    = entry.tags;
			try{
				byte[] messageText = Base64.decode(entry.description, Base64.DEFAULT);
				group_desc = new String(messageText, "utf-8");
			}catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				group_desc = entry.description;
				e.printStackTrace();
			}catch(Exception ex){
				group_desc = entry.description;
				ex.printStackTrace();
			}
		}
		else
		{
			groupName    = getIntent().getStringExtra("group_name");
			groupid    = getIntent().getStringExtra("group_id");
			group_desc   = getIntent().getStringExtra("group_desc");
			group_pic    = getIntent().getStringExtra("group_pic");
			tags_name = getIntent().getStringExtra("tags_name");
		}
		text = "";
		text = getTagsFormat();

		if(log)
			Log.i(TAG, "setDataCommunityProfile :: ChannelName : "+entry.groupName+", ChannelID : "+entry.groupId);
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(text != null)
					drawTags();
				if(entry!=null)
				{
					if(entry.ownerUsername.equalsIgnoreCase(SettingData.sSelf.getUserName()) || UIActivityManager.directFromCreateChannel || isEdit)
					{
						imgEditCommunity.setVisibility(View.VISIBLE);
						btn_join_leave.setVisibility(View.GONE);
						broadcast_layout.setVisibility(View.VISIBLE);
						if(entry.adminState != null && entry.adminState.equalsIgnoreCase("mute"))
						{
							changeFromNetwork = true;
							toggel_btn_broadcast.setChecked(true);
						}
						else
							toggel_btn_broadcast.setChecked(false);
					}else
					{
						imgEditCommunity.setVisibility(View.GONE);
						btn_join_leave.setVisibility(View.VISIBLE);
						broadcast_layout.setVisibility(View.GONE);
					}
					if(entry.pushNotif != null)
					{
						if(entry.pushNotif != null && entry.pushNotif.equalsIgnoreCase("TURNON"))
							toggel_btn_pushnotification.setChecked(true);
						else
						{
							toggel_btn_pushnotification.setChecked(false);
							pushcounter = 1;
						}
					}
				}
				if(group_desc != null)
				{
					if(UIActivityManager.directFromCreateChannel || isEdit)
						txt_comm_desc.setText(group_desc);
					else
					{
						try{
							byte[] messageText = Base64.decode(entry.description, Base64.DEFAULT);
							txt_comm_desc.setText(new String(messageText, "utf-8"));
						}catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							if(entry != null)
								txt_comm_desc.setText(entry.description);
							e.printStackTrace();
						}catch(Exception ex){
							if(entry != null)
								txt_comm_desc.setText(entry.description);
							ex.printStackTrace();
						}
					}
				}

				if(group_pic != null && !group_pic.trim().equals(""))
				{
					//http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/thumb/126058?msgId=8350
					if(group_pic.startsWith("http://"))
					{
						if(group_pic.contains("/thumb/"))
							imageView.setImageResource(R.drawable.com_channel_bg);
						else
						{
							//if(group_pic.indexOf('?') != -1)
								//group_pic = group_pic.substring(0, group_pic.indexOf('?'));
							//imageManager.displayImage(group_pic,imageView, handler,ImageDownloader.TYPE_THUMB_COMMUNITY);
							imageLoader.displayImage(group_pic, imageView, options);
						}
					}
					else
					{
						imageView.setVisibility(View.VISIBLE);
						imageView.setImageURI(Uri.parse(group_pic));
					}
				}else
				{
					//imageView.setVisibility(View.VISIBLE);
					//imageView.setBackgroundResource(R.drawable.attachicon);
				}

				if(entry!= null && !entry.ownerDisplayName.equals("")){
					txt_owner_name.setText(entry.ownerDisplayName);
				}
				if(entry!= null && entry.noOfMember!=0){
					txt_follow_count.setText(""+entry.noOfMember);
				}else
				{
					txt_follow_count.setText("0");
				}
				if(entry!= null && entry.noOfMessage!=0){
					txt_postcount.setText(""+entry.noOfMessage);
				}else
				{
					txt_postcount.setText("0");
				}
				if(entry != null && entry.reportCount >0 && imgEditCommunity.isShown()){
					relative_report.setVisibility(View.VISIBLE);
					txt_report_count.setText(""+entry.reportCount);
				}else
				{
					relative_report.setVisibility(View.GONE);
					txt_report_count.setText("");
				}
				refershFollowUnfollow();
				changeFromNetwork = false;
			}
		});
	}

	public int clickHandler(String aString,boolean showAlert) {
		String destination = "msgto:";
		String rtmlRow = aString;
		String command;
		int index;
		index = rtmlRow.indexOf(destination);
		if (index != Constants.ERROR_NOT_FOUND) {
			rtmlRow = rtmlRow
					.substring(index + destination.toString().length());
			index = rtmlRow.indexOf('?');
			if (index == Constants.ERROR_NOT_FOUND)
				return Constants.ERROR_NOT_FOUND;
			destination = rtmlRow.substring(0, index);
			rtmlRow = rtmlRow.substring(index + 1);
			command = "text=";
			index = rtmlRow.indexOf(command);
			if (index == Constants.ERROR_NOT_FOUND)
				return Constants.ERROR_NOT_FOUND;
			String alert = ",alert=";
			int alertIndex = rtmlRow.indexOf(alert);
			if (alertIndex == Constants.ERROR_NOT_FOUND) {
				command = rtmlRow.substring(index + command.length());
				rtmlRow = rtmlRow.substring(index + command.length());
			} else {
				rtmlRow = rtmlRow.substring(index + command.length());
				alertIndex = rtmlRow.indexOf(alert);
				command = rtmlRow.substring(0, alertIndex);
				rtmlRow = rtmlRow.substring(alertIndex + alert.length());
			}
			if(showAlert)
				sendRTMLRequest(destination, command, getString(R.string.yourrequesthasbeensent));
			else
				sendRTMLRequest(destination, command,null);

		}

		return Constants.ERROR_NONE;
	}
	//
	//
	//
	//

	//Hit PUsh On or Off to the server

	private String postDataOnServer(String url, String gpName, String op,int typeHit){
		//		url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings";
		//http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings?groupName=pkrpriv&op={TURNOFF or TURNON}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		if(typeHit == 1){
		nameValuePair.add(new BasicNameValuePair("groupName", gpName));
		nameValuePair.add(new BasicNameValuePair("op", op));//{TURNOFF or TURNON}
		}
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			//httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));.
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int responceCode = response.getStatusLine().getStatusCode();
			if(responceCode == 200){
				//InputStream ins = response.getEntity().getContent();
				String strResponse = EntityUtils.toString(response.getEntity());
				//	System.out.println("DATA = "+IOUtils.read(ins));
				UIActivityManager.refreshChannelList = true;
				return strResponse;
			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public void method_call(final String url, final String op)
	{
		if(rTDialog != null){
			rTDialog.setMessage(getString(R.string.updating));
			rTDialog.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String responce = postDataOnServer(url, entry.groupName , op , 1);
				CommunityProfileNewActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(responce != null && responce.contains("success")){
							try{
								JSONObject  jobj = new JSONObject(responce);
								if(responce.contains("desc")){
									String desc = jobj.optString("desc").toString();
									//									showSimpleAlert(getString(R.string.app_name), desc);
									if(rTDialog!=null && rTDialog.isShowing())
										rTDialog.dismiss();
									if(desc != null && desc.length() > 0)
										Toast.makeText(CommunityProfileNewActivity.this, desc, Toast.LENGTH_SHORT).show();
								}	
							}catch(Exception e){
								if(rTDialog!=null && rTDialog.isShowing())
									rTDialog.dismiss();
							}
						}
						else{
							if(rTDialog!=null && rTDialog.isShowing())
								rTDialog.dismiss();
							try{
						        JSONObject  jobj = new JSONObject(responce);
						        if(responce.contains("desc")){
						         String desc = jobj.optString("desc").toString();
						         if(desc != null && desc.length() > 0)
						          Toast.makeText(CommunityProfileNewActivity.this, desc, Toast.LENGTH_SHORT).show();
						        } 
						       }catch(Exception e){
						        if(rTDialog!=null && rTDialog.isShowing())
						         rTDialog.dismiss();
						       }
						}

					}
				});
			}
		}).start();
	}
	// Manoj Singh
	//Date 19-05-2015

	private class GetChannelDetail extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//			rTDialog = new RTDialog(CommunityProfileNewActivity.this, null, getString(R.string.loading_dot));
			//			rTDialog.show();
//			rTDialog = ProgressDialog.show(CommunityProfileNewActivity.this, "", getString(R.string.loading_dot), true);
			channel_detail_progress.setVisibility(View.VISIBLE);
			imgEditCommunity.setVisibility(View.GONE);
			relative_report.setVisibility(View.GONE);
				disableViews();
			channel_detail_progress.getIndeterminateDrawable().setColorFilter(0xFF28D7FA, android.graphics.PorterDuff.Mode.MULTIPLY);
		}
		@Override
		protected String doInBackground(String... urls) {
			String responseStr = null;
			try {				
				String url = "http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/api/group/groupid/"
						+ groupid +"?iThumbFormat=600x600&vThumbFormat=300x300&iFormat=600x600";
				if(log)
					Log.i(TAG, "GetChannelDetail::doInBackground :: URL : "+url);
			/*	int timeoutConnection = 1000;
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 3000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);*/
				
				
				
				DefaultHttpClient client = new DefaultHttpClient();
				HttpParams params = client.getParams();
//				HttpConnectionParams.setConnectionTimeout(params, 3000);
//				HttpConnectionParams.setSoTimeout(params,3000);
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("RT-APP-KEY", ""+BusinessProxy.sSelf.getUserId());

				try {
					HttpResponse responseHttp = client.execute(httpget);
					if (responseHttp != null) {
						responseStr = EntityUtils.toString(responseHttp.getEntity());
						if(log)
							Log.i(TAG, "GetChannelDetail::doInBackground :: Channel Detail ==> "+responseStr);
					}
					mResponseData = responseStr.getBytes();
					//return responseStr;
				} catch (ClientProtocolException e) {
					Log.i(TAG, "GetChannelDetail::doInBackground :: EXCEPTION ==> ClientProtocolException"+e);	

				} catch (IOException e) {
					Log.i(TAG, "GetChannelDetail::doInBackground :: EXCEPTION ==> IOException"+e);	

				}
			} catch (Exception e) {
				Log.i(TAG, "GetChannelDetail::doInBackground :: EXCEPTION ==> "+e);			}
			return responseStr ;
		}

		@Override
		protected void onPostExecute(String response) {
			imgEditCommunity.setVisibility(View.VISIBLE);
			if(log)
				Log.v(TAG, "onPostExecute :: response : "+response);
			if(response!=null){
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(CommunityProfileNewActivity.this, DATA_CALLBACK, (byte)0), 0);
			}
			else 
			{
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(CommunityProfileNewActivity.this, ERROR_CALLBACK, (byte)0), 0);
			}
			enableViews();
		}
	}

	private void parseXMLData(final String aTitle) {
		super.parseXMLData();
		if (feed == null||feed.entry==null || feed.entry.size()==0) 
		{

		} 
		else 
		{
			pushcounter = 0;
			DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", feed.entry.get(0));
			if(log)
				Log.i(TAG, "onOptionsItemSelected :: ChannelName : "+feed.entry.get(0).groupName+", ChannelID : "+feed.entry.get(0).groupId);
		}
		setDataCommunityProfile();
		//BusinessProxy.sSelf.recordScreenStatistics(Feed.getAttrCode(feed.tracking), true, true);//Add Record
//		if(rTDialog!=null && rTDialog.isShowing())
//			rTDialog.dismiss() ;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(channel_detail_progress != null)
					channel_detail_progress.setVisibility(View.GONE);
			}
		});
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
				UIActivityManager.directFromCreateChannel = false;
				parseXMLData(null);
			} else {
//				if(rTDialog!=null && rTDialog.isShowing())
//					rTDialog.dismiss() ;
				if(channel_detail_progress != null)
					channel_detail_progress.setVisibility(View.GONE);
				showSimpleAlert("Error:", "Unable to retrieve\n" + null);
			}
			break;
		case ERROR_CALLBACK:
			if(rTDialog!=null && rTDialog.isShowing())
				rTDialog.dismiss() ;
			showSimpleAlert("Error", getString(R.string.network_fail));
			break;
		}
		dismissAnimationDialog();
		super.cancelThread();
	}
	private void disableViews(){
		layout_post_btn.setClickable(false);
		layout_follow_btn.setClickable(false);
		imgEditCommunity.setClickable(false);
		btn_join_leave.setClickable(false);
		imageView.setClickable(false);
	}

	private void enableViews(){
		layout_post_btn.setClickable(true);
		layout_follow_btn.setClickable(true);
		imgEditCommunity.setClickable(true);
		btn_join_leave.setClickable(true);
		imageView.setClickable(true);
	}
	//
	//================================================================
	//
	ProgressDialog reportDialog;
	public class ReportedRequest extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			reportDialog = ProgressDialog.show(CommunityProfileNewActivity.this, "", getString(R.string.loading_dot), true);
			reportDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServer(entry.reportMessageUrl, entry.groupName, "",0);
			if (strData != null  && (strData.trim().indexOf("1") != -1)) {
				parserReportedMessage(strData);
				return strData;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "DeleteRequest :: onPostExecute : result : "+result);
			if(result != null){
				//deleteTextLocalMessage(result);
				parserReportedMessage(result);
			}
			else
				Toast.makeText(CommunityProfileNewActivity.this, getString(R.string.error),  Toast.LENGTH_SHORT).show();
			reportDialog.dismiss();
		}
	}
	private void parserReportedMessage(String strData) {

		CommunityFeed feed = new CommunityFeed();

		try {
			JSONObject mainStr =  new JSONObject(strData);
			if(strData.contains("channelReportedMessageFeed"))
			{
				String channelReportedMessageFeed = mainStr.getString("channelReportedMessageFeed");
				JSONObject channelReportedMessageFeedObj =  new JSONObject(channelReportedMessageFeed);
				feed.groupId=channelReportedMessageFeedObj.getInt("groupId");
				if(channelReportedMessageFeedObj.has("nextUrl"))
				feed.nexturl = channelReportedMessageFeedObj.getString("nextUrl");
				if(channelReportedMessageFeed.contains("channelReportedMessageList")){
					JSONArray channelReportedMessageListArray = channelReportedMessageFeedObj.getJSONArray("channelReportedMessageList");

					// looping through All entry
					for (int i = 0; i < channelReportedMessageListArray.length(); i++) {
						CommunityFeed.Entry entry = new CommunityFeed.Entry();
						JSONObject row = channelReportedMessageListArray.getJSONObject(i);
						entry.groupId = row.getInt("groupId");
						entry.groupName = row.getString("groupName");
						entry.messageId = row.getString("messageId");
						entry.createdDate = row.getString("createdDate");
						entry.senderId = row.getInt("senderId");
						entry.senderName = row.getString("senderUsername");
						entry.firstName = row.getString("senderFirstName");
						entry.lastName = row.getString("senderLastName");
						entry.reportCount = row.getInt("reportCount");
						entry.imgThumbUrl = row.getString("senderThumbUrl");
						entry.imgUrl = row.getString("senderProfileUrl");
						entry.thumbUrl = group_pic;

						if(row.has("text")){
							entry.messageText = row.getString("text");
						}

						JSONArray mediaContentUrlListArray = row.getJSONArray("mediaContentUrlList");
						// looping through All entry
						Vector media = new Vector<String>();
						for (int j = 0; j < mediaContentUrlListArray.length(); j++) {
							JSONObject row_media = mediaContentUrlListArray.getJSONObject(j);

							media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
							String mediaType = row_media.getString("contentType");
							if(mediaType.equals("image")){
								media.add("image");
							}else if(mediaType.equals("video")){
								media.add("video");
							}
							else if(mediaType.equals("audio")){
								media.add("audio");
							}
							String contentUrl = row_media.getString("contentUrl");
							media.add(contentUrl);
							media.add("normal");

							if(mediaType.equals("image") || mediaType.equals("video"))
							{
								media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
								if(mediaType.equals("image")){
									media.add("image");
								}else if(mediaType.equals("video")){
									media.add("video");
								}
								media.add(row_media.getString("thumbUrl"));
								media.add("thumb");
							}
						}
						entry.media = media; 
						JSONArray reportedByListArray = row.getJSONArray("reportedByList");
						// looping through All entry
						String reportedby = "";
						Vector<ReportedByUser> ReportedByUser = new Vector<ReportedByUser>();
						for (int k = 0; k < reportedByListArray.length(); k++) {
							ReportedByUser RBU = new ReportedByUser();
							JSONObject row_reportedByList = reportedByListArray.getJSONObject(k);
							RBU.firstName=row_reportedByList.getString("firstName");
							RBU.lastName =row_reportedByList.getString("lastName");
							RBU.userId = row_reportedByList.getString("userId");
							RBU.userName = row_reportedByList.getString("userName");
							reportedby = reportedby + row_reportedByList.getString("firstName")+ row_reportedByList.getString("lastName") + ", ";
							ReportedByUser.add(RBU);
						}
						if(reportedby.endsWith(", ")){
							reportedby  = reportedby.substring(0, reportedby.length() - 2);
						}
						entry.reportedBy = reportedby;
						entry.ReportedByUser = ReportedByUser;
						feed.entry.add(entry);
					}
				}
				if(entry !=null){
					DataModel.sSelf.storeObject("FEED_REPORT", feed);
					
					//ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
					//if(mngr.)
					
					Intent intent = new Intent(CommunityProfileNewActivity.this, ChannelReportActivity.class);
					intent.putExtra("SYSTEM_MSG_VIEW_REPORT", true);
					UIActivityManager.systemMessageReportAction = true;
					/*if(deleteMessageAPI != null && deleteMessageAPI.length() > 0){
							intent.putExtra("DELETE_MESSAGE_URL", "");
							intent.putExtra("DELETE_MESSAGE_ID", -1);*/
					startActivity(intent);
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	//
	//=================================================================
	//
	
}
