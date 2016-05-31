package com.kainat.app.android;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.R;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.KeyValue;

public class TourActivity extends FragmentActivity implements OnClickListener {

	Handler handler = new Handler();
	GalleryNavigator navi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tour_activity);
		final ViewPager pager = (ViewPager) findViewById(R.id.pager);

		FragmentManager fm = getSupportFragmentManager();
		navi = (GalleryNavigator) findViewById(R.id.count);
		navi.setSize(3);
		navi.setPosition(0);
		navi.setLoadedSize(0);
		navi.invalidate();
		final LoginFragmentAdopter pagerAdapter = new LoginFragmentAdopter(fm);
		//pagerAdapter.setFragments(getApplicationContext());
		pager.setAdapter(pagerAdapter);

		findViewById(R.id.done).setOnClickListener(this) ;


		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {


				navi.setPosition(position);
				navi.invalidate();

				if(position == 2)
					findViewById(R.id.done).setVisibility(View.GONE);
				else
					findViewById(R.id.done).setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Tour Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
		case R.id.done:
			finish();
			Intent intent = new Intent(TourActivity.this,
					KainatContactActivity.class);
			intent.putExtra("HEADER_TITLE", getString(R.string.friend));
			intent.putExtra("REG_FLOW", true);
			startActivity(intent);
			/*Intent intent = new Intent(TourActivity.this,InterestActivity.class);
			intent.putExtra("REG_FLOW", true);
			startActivity(intent);*/
			break;
		}
	}

	class LoginFragmentAdopter extends FragmentPagerAdapter {

		final int PAGE_COUNT = 3;
		LoginFragmentTwo fragA;
		LoginFragmentTwo fragB;
		LoginFragmentTwo fragC;
		LastTourPage fragD;
		public LoginFragmentAdopter(FragmentManager fm) {
			super(fm);
			setFragments(getApplicationContext());
		}

		public void setFragments(Context c){
			String lan = KeyValue.getString(TourActivity.this, KeyValue.LANGUAGE);
			// Set up the simple base fragments
			fragA = new LoginFragmentTwo(0,lan);
			fragB = new LoginFragmentTwo(1,lan);
		//	fragC = new LoginFragmentTwo(2,lan);
			
			if(lan!=null && lan.equalsIgnoreCase("ar")){
				fragD = new LastTourPage(0);
			}else
			{
				fragD = new LastTourPage(1);
			}
		

			//Resources res = c.getResources();

			/*   fragA.changeText("This is Fragment A!");
		        fragB.changeText("This is Fragment B!");
		        fragC.changeText("This is Fragment C!");

		        fragA.changeBG(res.getColor(R.color.dev_blue));
		        fragB.changeBG(res.getColor(R.color.dev_green));
		        fragC.changeBG(res.getColor(R.color.dev_orange));*/

		}
		@Override
		public Fragment getItem(int position) {
			
			  Fragment frag = null;
		        if(position == 0){
		            frag = fragA;
		        }
		        else if(position == 1){
		            frag = fragB;
		        }
		      else if(position == 2){
		            frag = fragD;
		        }
		        //else if(position == 3){
		       //     frag = fragD;
		       // }
		        return frag;
			/*String lan 					= KeyValue.getString(TourActivity.this, KeyValue.LANGUAGE);
			LoginFragmentTwo myFragment = new LoginFragmentTwo(arg0,lan);
			Bundle data 				= new Bundle();
			myFragment.setArguments(data);
			return myFragment;
			*/
			/*	if (arg0 == 0) {
				int res = R.drawable.tour_1 ;//feel

				if(lan!=null && lan.equalsIgnoreCase("ar"))
					res = R.drawable.tour_11 ;

				LoginFragmentTwo myFragment = new LoginFragmentTwo(res);
				Bundle data = new Bundle();
				myFragment.setArguments(data);
				return myFragment;
			} else if (arg0 == 1) {
				int res = R.drawable.tour_2 ;//talk

				if(lan!=null && lan.equalsIgnoreCase("ar"))
					res = R.drawable.tour_22 ;

				LoginFragmentTwo myFragment = new LoginFragmentTwo(res);
				Bundle data = new Bundle();
				myFragment.setArguments(data);
				return myFragment;
			} else if (arg0 == 2) {
				int res = R.drawable.tour_3 ;//discover

				if(lan!=null && lan.equalsIgnoreCase("ar"))
					res = R.drawable.tour_33 ;

				LoginFragmentTwo myFragment = new LoginFragmentTwo(res);
				Bundle data = new Bundle();
				myFragment.setArguments(data);
				return myFragment;
			}
			else if (arg0 == 3) {
				int res = R.drawable.tour_4 ;//connect

				if(lan!=null && lan.equalsIgnoreCase("ar"))
					res = R.drawable.tour_44 ;

				LastTourPage myFragment = new LastTourPage(res);
				Bundle data = new Bundle();
				myFragment.setArguments(data);
				return myFragment;
			}*/
			//return null;
		}
		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page #" + (position + 1);
		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
}
