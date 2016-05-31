package com.kainat.app.android.adaptor;

import android.content.Context;
import android.view.View;

import com.kainat.app.android.bean.FeaturedUserBean;
import com.kainat.app.android.bean.GroupEventBean;
import com.kainat.app.android.bean.LandingPageBean;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.Feed;

public interface RTAdaptorInf {
	public void viewActivity(View convertView, Context context, LandingPageBean landingPageBean,boolean download) ;
	public void viewMedia(View convertView, Context context, Feed.Entry entry) ;
	public void viewCommunity(View convertView, Context context, CommunityFeed.Entry entry) ;
	public void viewGroupEvent(View convertView, Context context, GroupEventBean groupEventBean) ;
	public void viewFeatureUser(View convertView, Context context, FeaturedUserBean featuredUserBean) ;
	public void viewRtLiveList(View convertView, Context context, GroupEventBean groupEventBean) ;
	public void viewMediaComment(View convertView, Context context, Feed.Entry entry) ;
	public void viewFeatureUserList(View convertView, Context context, FeaturedUserBean featuredUserBean) ;
}
