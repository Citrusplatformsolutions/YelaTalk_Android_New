package com.kainat.app.android;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.RTDialog;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.rockerhieu.emojicon.EmojiconTextView;

public class CommunityProfileActivity extends UICommunityManager{
	ImageView imageView;
	TextView edt_com_desc;
	TextView public_comm;
	TextView comm_name;
	TextView community_edit,community_name;
	RTDialog rTDialog;
	String groupName,group_desc,group_pic;
	ImageDownloader imageManager;
	ThumbnailReponseHandler handler;
	private CommunityFeed.Entry entry = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_profile);
		groupName    = getIntent().getStringExtra("group_name");
		group_desc   = getIntent().getStringExtra("group_desc");
		group_pic    = getIntent().getStringExtra("group_pic");
		imageManager = new ImageDownloader();
		handler      = new ThumbnailReponseHandler() {

			@Override
			public void onThumbnailResponse(ThumbnailImage value,
					byte[] data) {
				try {

				} catch (Exception e) {
				}
			}
		};	
		init();
		
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Community Profile Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());	
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	if(entry != null)
		community_name.setText(entry.groupName);
	else
		community_name.setText(groupName);
	if(entry != null)
	{
		try {
			byte[] messageText = Base64.decode(entry.description, Base64.DEFAULT);
			edt_com_desc.setText(new String(messageText, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	else if(group_desc != null)
	{
		try {
			byte[] messageText = Base64.decode(group_desc, Base64.DEFAULT);
			edt_com_desc.setText(new String(messageText, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if(entry != null)
		comm_name.setText(entry.groupName);
	else
		comm_name.setText(groupName);
	
	if(entry != null && !entry.thumbUrl.trim().equals(""))
	{
		imageManager.download(entry.thumbUrl,imageView, handler,ImageDownloader.TYPE_THUMB_COMMUNITY);
	}else
	{
		imageView.setVisibility(View.VISIBLE);
		imageView.setBackgroundResource(R.drawable.attachicon);
	}
	super.onResume();
}
	private void init() {
		// TODO Auto-generated method stub
		entry 			 = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY);
		imageView 		 = (ImageView) findViewById(R.id.image_community_profile);
		community_name 	 = (TextView)findViewById(R.id.community_name);
		edt_com_desc     = (EmojiconTextView)findViewById(R.id.edt_community_description_id);
		public_comm      = (TextView)findViewById(R.id.public_comm);
		comm_name        = (TextView)findViewById(R.id.comm_name);
		community_edit   = (TextView)findViewById(R.id.community_edit);
	
		
		imageView.setBackgroundResource(R.drawable.attachicon);
        if(entry != null && entry.ownerId != BusinessProxy.sSelf.getUserId())
        	community_edit.setVisibility(View.GONE);
        else
        	community_edit.setVisibility(View.VISIBLE);
        if(entry != null && entry.publicCommunity != null && entry.publicCommunity.equalsIgnoreCase("yes"))
        	public_comm.setVisibility(View.VISIBLE);
        else
        	public_comm.setVisibility(View.GONE);
        
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		community_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DataModel.sSelf.storeObject(DMKeys.ENTRY, entry);
				Intent intent = new Intent(CommunityProfileActivity.this, CreateCommunityActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onTaskCallback(Object parameter, byte mRequestObjNo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	
	private void closeDialog(){
		if(rTDialog!=null && rTDialog.isShowing())
			rTDialog.dismiss() ;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		DataModel.sSelf.removeObject(DMKeys.ENTRY);
	}
}
