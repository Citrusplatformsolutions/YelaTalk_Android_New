package com.kainat.app.android;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.MyBase64;
import com.kainat.app.android.util.ResponseObject;

public class KainatSettings extends UIActivityManager implements
		OnClickListener {
	ToggleButton toggel_btn_play;
	private static final String TAG = VerificationActivity.class.getSimpleName();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.kainat_setting_screen);
		toggel_btn_play = (ToggleButton)findViewById(R.id.toggel_btn_autoplay);
		toggel_btn_play.setChecked(getAutoplay());
		toggel_btn_play.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			/*	if(isChecked)
				{
					SaveAutoplay(isChecked);
				}else
				{
					SaveAutoplay(isChecked);
				}*/
				saveAutoplay(isChecked);
			}
		});
		
		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		findViewById(R.id.choose_your_language).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onCreateDialog(null).show() ;
					}
				});
		findViewById(R.id.blockuser_ll).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(KainatSettings.this,
								KainatBlockedUser.class);
						intent.putExtra("PAGE", (byte) 1);
						intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
						startActivityForResult(intent,
								Constants.ACTIVITY_FOR_RESULT_INT);// (intent);
					}
				});
		
		findViewById(R.id.about_rocketalk).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(KainatSettings.this,
								AboutActivity.class);
						intent.putExtra("PAGE", (byte) 1);
						intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
						startActivityForResult(intent,
								Constants.ACTIVITY_FOR_RESULT_INT);// (intent);
					}
				});
		
		findViewById(R.id.about_rocketalk).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(KainatSettings.this,
								AboutActivity.class);
						intent.putExtra("PAGE", (byte) 1);
						intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
						startActivityForResult(intent,
								Constants.ACTIVITY_FOR_RESULT_INT);// (intent);
					}
				});
		findViewById(R.id.term_of_use).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(KainatSettings.this,
								CommunityWebViewActivityOld.class);
						startActivity(intent);
					}
				});
		findViewById(R.id.privacy_police).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(KainatSettings.this,
								CommunityWebViewActivityOld.class);
						startActivity(intent);
					}
				});
		findViewById(R.id.feedback).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						 Intent emailIntent = new Intent(Intent.ACTION_SEND);
						    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yelatalkfeedback@gmail.com"});
						    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "YelaTalk Feedback");
						    emailIntent.setType("text/plain");
//						    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
						    final PackageManager pm = getPackageManager();
						    final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
						    ResolveInfo best = null;
						    for(final ResolveInfo info : matches)
						        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
						            best = info;
						    if (best != null)
						        emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
						    startActivity(emailIntent);
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
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Settings Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	public void onBackPressed() {
		if (onBackPressedCheck())
			return;
		if (getIntent().getByteExtra("PAGE", (byte) 1) == 1) {
			finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
			return;
		}
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	@Override
	public void onError(int i) {
		// TODO Auto-generated method stub

	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_language).setItems(R.array.language,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							setLocale("en");

							break;
						case 1:
							KeyValue.setBoolean(KainatSettings.this, KeyValue.LANGUAGE_CHANGE,true);
							setLocale("en");
							finish();
//							Intent intent = new Intent(KainatSettings.this,
//									KainatHomeActivity.class);
//							startActivity(intent);

							break;
						case 2:
							KeyValue.setBoolean(KainatSettings.this, KeyValue.LANGUAGE_CHANGE,true);
							setLocale("ar");
							finish();
//							intent = new Intent(KainatSettings.this,
//									KainatHomeActivity.class);
//							startActivity(intent);
							break;
						default:
							break;
						}

					}
				});
		return builder.create();
	}

	public void setLocale(String lang) {
		KeyValue.setString(this, KeyValue.LANGUAGE,lang);
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String key = new String(MyBase64.encode(md.digest()));
				Log.i(TAG, "setLocale :: sigunature key ==> "+key);
			}
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
