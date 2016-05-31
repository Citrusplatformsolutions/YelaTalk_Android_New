package com.kainat.app.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.MediaUserLikeAdapter;
import com.kainat.app.android.adaptor.MediaUserLikeAdaptorInf;
import com.kainat.app.android.bean.MediaLikeDislike;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;

public class LikeUserActivity extends UIActivityManager implements View.OnClickListener, ThumbnailReponseHandler, OnScrollListener,MediaUserLikeAdaptorInf {
	private static final String TAG = LikeUserActivity.class.getSimpleName();
	private static final byte SUB_SCREEN_MAIN = 1;
	Handler handler = new Handler();
	MediaUserLikeAdapter mediaUserLikeAdapter ;
	Button button ;
	ListView listView ;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		screenSlide(this, R.layout.like_user);
		listView = (ListView)findViewById(R.likeuser.list) ;
		button = (Button)findViewById(R.userlike.more) ;
		findViewById(R.id.list_layout).setVisibility(View.GONE);
		findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
		findViewById(R.id.loading_progress_bar).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.title)).setText("Likes");//getIntent().getStringExtra("TITLE")) ;
		int mRowsUpdated =getContentResolver().delete(MediaContentProvider.CONTENT_URI_LIKES,null, null);
		FeedRequester.requestLikeUser(this, getIntent().getStringExtra("URL"));
		
		// Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication())
						.getTracker(TrackerName.APP_TRACKER);
				t.setScreenName("LikeUser Screen");
				t.set("&uid",""+BusinessProxy.sSelf.getUserId());
				t.send(new HitBuilders.AppViewBuilder().build());
	}
	
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		finish();
	}
	

	
	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
	}


	@Override
	public void notificationFromTransport(ResponseObject response) {
	}
	
	@Override
	public void onResponseSucess(Object respons,int requestForID){
		
		}
	@Override
	public void onResponseSucess(String respons,int requestForID){
		if(requestForID!=Constants.FEED_GET_LIKE_USE)return ;
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mediaUserLikeAdapter==null){
					mediaUserLikeAdapter =new MediaUserLikeAdapter(LikeUserActivity.this, null, true, LikeUserActivity.this) ;
					listView.setAdapter(mediaUserLikeAdapter);
				}
				
				mediaUserLikeAdapter.notifyDataSetChanged();
				if(listView.getCount()>0)
				{
					
					findViewById(R.id.loading_layout).setVisibility(View.GONE);
					findViewById(R.id.loading_progress_bar).setVisibility(View.GONE);
					findViewById(R.id.not_found).setVisibility(View.GONE);
					findViewById(R.id.list_layout).setVisibility(View.VISIBLE);
					findViewById(R.likeuser.list).setVisibility(View.VISIBLE);
//					listView.setAdapter(mediaUserLikeAdapter);
//					mediaUserLikeAdapter.notifyDataSetChanged();
					if(listView!=null){
						listView.postDelayed(new Runnable() {
							
							@Override
							public void run() {
//								if(idel)
								fetchImage();
							}
						}, 500);
					}
				}
				else
				{
					findViewById(R.id.list_layout).setVisibility(View.GONE);
					findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.loading_progress_bar).setVisibility(View.GONE);
					findViewById(R.id.not_found).setVisibility(View.VISIBLE);
				}
			}
		});
	}
	@Override
	public void onResponseSucess(String respons,int requestForID, int subType,int totNewFeed){
		
		}
	@Override
	public void onError(String err){
		
		}
	@Override
	public void onError(String err,int requestForID){
		
	if(requestForID!=Constants.FEED_GET_LIKE_USE)return ;
	runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
			
			
				findViewById(R.id.list_layout).setVisibility(View.GONE);
				findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.loading_progress_bar).setVisibility(View.GONE);
				findViewById(R.id.not_found).setVisibility(View.VISIBLE);
			
		}
	});
	}
	@Override
	public void onNotification(int requestCode, String sender, String msg){
		}
	@Override
	public void onNotificationThreadInbox(int requestCode, String sender, String msg){
		
		}


	class ViewHolder
	{
		CImageView memberImage ;
		TextView userName ;
		Button loadmore ;
		MediaLikeDislike mediaLikeDislike;
		LinearLayout text_layout ;
	}
	public void viewMediaUserLikeDislik(View convertView, Context context, MediaLikeDislike mediaLikeDislike) {
		
		ViewHolder viewHolder =	(ViewHolder)convertView.getTag() ;
		if(viewHolder==null){
			viewHolder = new ViewHolder() ;
			viewHolder.memberImage = (CImageView)convertView.findViewById(R.userlike.memberImage) ;
			viewHolder.userName = (TextView)convertView.findViewById(R.userlike.username) ;
			viewHolder.loadmore = (Button)convertView.findViewById(R.userlike.more) ;
			viewHolder.text_layout = (LinearLayout)convertView.findViewById(R.userlike.text_layout) ;
		}
		viewHolder.memberImage.setVisibility(View.VISIBLE) ;
		viewHolder.text_layout.setVisibility(View.VISIBLE) ;
		viewHolder.loadmore.setVisibility(View.GONE) ;
		viewHolder.mediaLikeDislike =  mediaLikeDislike ;
		
		viewHolder.userName.setText(mediaLikeDislike.userDisplayName) ;
		ImageDownloader imageManager = new ImageDownloader(2);
		imageManager.handler = handler;
		imageManager.download(mediaLikeDislike.userThumbUrl,
				viewHolder.memberImage, this, ImageDownloader.TYPE_THUMB_BUDDY);
		
		viewHolder.memberImage.setTag(mediaLikeDislike.userName);
		viewHolder.memberImage.setOnClickListener(LikeUserActivity.this);
		
		if(mediaLikeDislike.nextUrl!=null && mediaLikeDislike.nextUrl.startsWith("http") && mediaLikeDislike.userId.equalsIgnoreCase("-1"))
		{
			viewHolder.loadmore.setTag(mediaLikeDislike.nextUrl) ;
			viewHolder.loadmore.setOnClickListener(LikeUserActivity.this) ;
			viewHolder.loadmore.setVisibility(View.VISIBLE) ;
			viewHolder.memberImage.setVisibility(View.GONE) ;
			viewHolder.text_layout.setVisibility(View.GONE) ;
			if(FeedRequester.feedTaskLeftMenu!=null && FeedRequester.feedTaskLeftMenu.getStatus()!=Status.FINISHED)
				viewHolder.loadmore.setText("Loading...") ;
		}
	}
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			idel = true;
			ImageDownloader.idel = true;
			
			if(idel){
				if(listView!=null){
					listView.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if(idel)
							fetchImage();
						}
					}, 500);
				}
			}
		} else {
			ImageDownloader.idel = false;
			idel = false;
		}
	}
	public void fetchImage(){
		boolean s = false ;
		if(listView!=null && idel){
			int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0
			for (int i = 0; i <= firstPosition+visibleItemCount; i++) 
//			for (int i = firstPosition+visibleItemCount; i >= 0; i--) 
			{
				if(listView!=null){
				View convertView = listView.getChildAt(i);
				if(convertView!=null){
				ViewHolder activityViewHolder = (ViewHolder)convertView.getTag() ;
				if(activityViewHolder!=null && activityViewHolder.mediaLikeDislike!=null)
				{
					if(s)
					Toast.makeText(this, activityViewHolder.mediaLikeDislike.userName, Toast.LENGTH_LONG).show() ;
					s = false ;
					
					/////
					if (activityViewHolder.mediaLikeDislike.userThumbUrl != null) {
						ImageDownloader imageManager = new ImageDownloader(2);
						imageManager.handler = handler;
						imageManager.download(activityViewHolder.mediaLikeDislike.userThumbUrl, activityViewHolder.memberImage,
								LikeUserActivity.this, ImageDownloader.TYPE_THUMB_BUDDY);
					}
				}
				}
				}
			}
			
		}
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if(visibleItemCount==0)
			ImageDownloader.idel = true;
	}
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.userlike.memberImage:
//			DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, (String)v.getTag());
//			Intent intent = new Intent(this, WebviewActivity.class);
//			intent.putExtra("ActivityForResult", true);
//			startActivityForResult(intent, Constants.ResultCode);// (intent);
			break;
		case R.userlike.more:
			FeedRequester.requestLikeUser(this, (String)v.getTag());//getIntent().getStringExtra("URL"));
			((Button)v).setText("Loading...") ;
			break;
		default:
			break;
		}
	}
}