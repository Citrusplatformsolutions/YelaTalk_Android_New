package com.kainat.app.android;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.DiscoverAdapter;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ResponseObject;

public class KainatDiscoverActivity extends UIActivityManager {
	ListView listView;
	TextView title_discover;
	static String[] GRID_DATA= new String[] {"Sports" ,
		"News", 
		"Entertainment" ,
		"Lifestyle",
	"Spirituality" };
	static String[] GRID_DATA_IMG = new String[] { "Popular", "Sports", "News",	"Entertainment", "Lifestyle", "Spirituality" };
	DiscoverAdapter dA;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.kainat_discover_selection);
		init();
		initLeftMenu();
		Bundle bundleObj = getIntent().getExtras();
		if(bundleObj != null){
			if(bundleObj.getBoolean("INVISIBLE_MENU", false)){
				menuNew.setVisibility(View.GONE);
				onBackArrowClick();
			}
		}
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Discover Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());
	}
	private void onBackArrowClick(){
		ImageView backImageView = (ImageView) findViewById(R.id.back_iv);
		backImageView.setVisibility(View.VISIBLE);
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		if(getLocale().equalsIgnoreCase("en")){ 
			GRID_DATA =	getResources().getStringArray(R.array.community_recomendad2);
		}
		else{
			GRID_DATA =	getResources().getStringArray(R.array.community_recomendad_ar);
		}
		dA = new DiscoverAdapter(this, GRID_DATA, GRID_DATA_IMG);
		listView.setAdapter( dA );
		reloadeViewAfterChangeLanguag();
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
	}

	private void init() {
		title_discover = (TextView) findViewById(R.id.title_discover);
		title_discover.setText(getResources().getString(R.string.discover));
		if (getLocale().equalsIgnoreCase("en")) {
			GRID_DATA = getResources().getStringArray(
					R.array.community_recomendad2);
		} else {
			GRID_DATA = getResources().getStringArray(
					R.array.community_recomendad_ar);
		}
		// TODO Auto-generated method stub
		menuNew = (ImageButton) findViewById(R.id.menu);
		menuNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		});
		dA = new DiscoverAdapter(this, GRID_DATA, GRID_DATA_IMG);
		listView = (ListView) findViewById(R.id.grdview_discover);
		listView.setAdapter(dA);
		listView.setDivider(getResources().getDrawable(R.color.discover_list_divider));
		listView.setDividerHeight(1);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// Toast.makeText(getApplicationContext(),((TextView)
				// v.findViewById( R.id.grid_item_label )).getText(),
				// Toast.LENGTH_SHORT).show();
				/*
				 * Intent io = new Intent(KainatDiscoverActivity.this,
				 * KainatDiscoverCommunityActivty.class); startActivity(io);
				 */
				/*
				 * if(position==0) {
				 * 
				 * }else {
				 */
				Intent intent = new Intent(KainatDiscoverActivity.this,SearchCommunityActivity.class);
				intent.putExtra("TYPE", 1);
				intent.putExtra("CommunityType", GRID_DATA[position]);
				intent.putExtra("UrlIs","");
				startActivity(intent);
				// }
			}

		});
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}
}