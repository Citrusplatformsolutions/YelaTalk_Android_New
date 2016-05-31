package com.kainat.app.android;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ResponseObject;

public class KainatInviteSelection extends UIActivityManager{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kainat_invite_selection);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		init();
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Invite Main Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());	
	}
	private void init() {
		// TODO Auto-generated method stub
		Button btn_contact  = (Button)findViewById(R.id.btn_contact);
		Button btn_whatsapp = (Button)findViewById(R.id.btn_whatapp);
		Button btn_fb 		= (Button)findViewById(R.id.btn_fb);
		btn_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(KainatInviteSelection.this,KainatInviteActivity.class);
				startActivity(intent);
				//	finish();
			}
		});
		btn_fb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//com.facebook.katana

				// TODO Auto-generated method stub

				PackageManager pm=getPackageManager();
				try {
					String text =getResources().getString(R.string.invite_text);
					Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					//				    	    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.invite));
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);    
					startActivity(Intent.createChooser(shareIntent, getString(R.string.invite)));
				} catch (Exception e) {
					//				        Toast.makeText(KainatInviteSelection.this, "Facebook not Installed", Toast.LENGTH_SHORT).show();
				}  


			}
		});
		btn_whatsapp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				PackageManager pm=getPackageManager();
				try {

					Intent waIntent = new Intent(Intent.ACTION_SEND);
					waIntent.setType("text/plain");
					String text = getResources().getString(R.string.invite_text);

					PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
					//Check if package exists or not. If not then code 
					//in catch block will be called
					waIntent.setPackage("com.whatsapp");

					waIntent.putExtra(Intent.EXTRA_TEXT, text);
					startActivity(Intent.createChooser(waIntent, "Share with"));

				} catch (NameNotFoundException e) {
					Toast.makeText(KainatInviteSelection.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
				}  

			}
		});


		menuNew = (ImageButton)findViewById(R.id.menu) ;		
		menuNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList) ;	
			}
		});
		initLeftMenu();
	}
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}
}
