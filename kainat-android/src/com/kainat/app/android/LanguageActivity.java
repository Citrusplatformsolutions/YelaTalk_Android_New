package com.kainat.app.android;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.MyBase64;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.format.SettingData;

public class LanguageActivity extends Activity implements OnClickListener {

	Handler handler = new Handler();
	// Spinner spinner;
	boolean select = false;
	// 305561084 iPad Store ID
	// 11-16 21:23:33.575: I/System.out(1518): ---sigunature
	// key-key-----:kZCTsf15P8DImimg/TL7Lvi/G6s=
	// 11-16 21:23:33.855: I/System.out(1518): ---sigunature
	// key-key-----:kZCTsf15P8DImimg/TL7Lvi/G6s=

	Spinner country_spinner;
	private TextView selectionText;
	private byte cheatValue = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		String lan = KeyValue
				.getString(LanguageActivity.this, KeyValue.CONTACT);
		if (lan == null)
			lan = "en";

		if (lan != null)
			setLocale(lan);
		setContentView(R.layout.language_activity);

		TextView tv = (TextView) findViewById(R.id.textView1);
		
		((ImageView) findViewById(R.id.logo_iv)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(cheatValue == 6){
					cheatValue = 0;
					AlertDialog.Builder alert_dialog = new AlertDialog.Builder(LanguageActivity.this);
					LinearLayout alert = new LinearLayout(LanguageActivity.this);
					alert.setOrientation(LinearLayout.VERTICAL);
					
				    final EditText server_address = new EditText(LanguageActivity.this);
				    TextView server_change2 = new TextView(LanguageActivity.this);
				    final CharSequence[] servers = {"Production", "Development"};
				    ImageView img = new ImageView(LanguageActivity.this);
				    LayoutParams params = new LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
				    img.setBackgroundColor(R.color.sub_heading);
				    img.setLayoutParams(params);
				    
				    server_change2.setTextSize(18);
				    server_change2.setText("    Or input other server ip/domain:");
				    server_address.setText(Urls.TEJAS_HOST);
				    int selected_server = (Urls.SERVER  == Urls.SERVER_TYPE_LIVE) ? 0 : ((Urls.SERVER  == Urls.SERVER_TYPE_LIVE) ? 1 : -1);
				    
				    alert_dialog.setTitle("Change Server");
				    alert_dialog.setSingleChoiceItems(servers, selected_server, new  DialogInterface.OnClickListener(){

				    		    @Override
				    		    public void onClick(DialogInterface dialog, int which) 
				    		    {
				    		        if(servers[which]=="Production")
				    		        {
				    		        	Urls.SERVER  = Urls.SERVER_TYPE_LIVE;
				    		        	Urls.TEJAS_HOST  = Urls.URL_LIVE;
				    		        	server_address.setText(Urls.TEJAS_HOST);
				    		        }
				    		        else if (servers[which]=="Development")
				    		        {
				    		        	Urls.SERVER  = Urls.SERVER_TYPE_DEV;
				    		        	Urls.TEJAS_HOST  = Urls.URL_DEV;
				    		        	server_address.setText(Urls.TEJAS_HOST);
				    		        }
				    		    }

				    		});
				    
				    alert.addView(img);
				    alert.addView(server_change2);
				    alert.addView(server_address);
				    alert_dialog.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	if(server_address.getText().toString().trim().length() > 5){
					        	Urls.TEJAS_HOST = server_address.getText().toString().trim();
					        	dialog.cancel();
				        	}
				        }
				        
				    });
				    alert_dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            dialog.cancel();
				        }
				    });
				    alert_dialog.setView(alert);
				    alert_dialog.show();
				}else
					cheatValue++;
			}
		});
		
		String[] some_array = getResources().getStringArray(R.array.language);

		findViewById(R.id.language_layout).setOnClickListener(this);
		((ImageView)findViewById(R.id.arraow_iv)).setOnClickListener(this);
		selectionText = (TextView)findViewById(R.id.language_text);
		selectionText.setOnClickListener(this);

		country_spinner = (Spinner) findViewById(R.id.language_spinner);
		country_spinner.setAdapter(new ArrayAdapter<String>(this,
				R.layout.language_testview_screen, some_array));

		// Typeface tf = Farsi.GetFarsiFont(this);
		// tv.setTypeface(tf);
		// tv.setText(Farsi.Convert(getResources().getString(R.string.welcome_to_kainat)));

		// spinner = (Spinner) findViewById(R.id.language_spinner);
		country_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View arg1,
					int arg2, long arg3) {

				try{
					selectionText.setText(country_spinner.getSelectedItem().toString());
				}catch (Exception e) {
					// TODO: handle exception
				}

				if (select) {
					switch (arg2) {
					case 1:
						setLocale("en");
						finish();
						Intent intent = new Intent(LanguageActivity.this,
								VerificationActivity.class);
						startActivity(intent);
						break;
					case 2:
						setLocale("ar");
						finish();
						intent = new Intent(LanguageActivity.this,
								VerificationActivity.class);
						startActivity(intent);
						break;
					case 3:
						setLocale("ar");
						break;
					default:
						break;
					}

				}
			}

			//
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				System.out.println("onNothingSelected");
			}
		});
		country_spinner.setSelection(-1);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				select = true;
			}
		}, 500);

		ImageDownloader imageManager = new ImageDownloader();
		ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

			@Override
			public void onThumbnailResponse(ThumbnailImage value,
					byte[] data) {
				try {
//					System.out.println();
				} catch (Exception e) {
				}
			}
		};	

		imageManager
		.download(
				SettingData.sSelf.getUserName(),
				new CImageView(this), handler,
				ImageDownloader.TYPE_THUMB_BUDDY);

		//		System.out.println("http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/pic?format=150x150&user=kainat41907".hashCode());
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Language Selection Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	int cheatCounter = 0;
	public void cheatClicked(View v)
	{
		cheatCounter++;
		if(cheatCounter == 5)
		{
			cheatCounter = 0;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Change hostname");

			alert.setMessage("Type host name with port number(Ex- 192.168.1.1:18080)");
			// Set an EditText view to get user input 
			final EditText servername = new EditText(this);
			alert.setView(servername);
			alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if(servername.getText().toString() != null && servername.getText().toString().trim().length() > 8)
						Urls.TEJAS_HOST = servername.getText().toString();
					// Do something with value!
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
				}
			});
			alert.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.language_layout:
		case R.id.arraow_iv:
		case R.id.language_text:
			country_spinner.performClick();
			break;
		case R.id.terms_condition_link:
			Intent intent = new Intent(LanguageActivity.this, CommunityWebViewActivityOld.class);
			startActivity(intent);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setLocale("in");
	}

	public void setLocale(String lang) {
		KeyValue.setString(LanguageActivity.this, KeyValue.LANGUAGE, lang);
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		// Locale locale = new Locale("in");
		// Locale.setDefault(locale);
		// Configuration config = new Configuration();
		// config.locale = locale;
		// getBaseContext().getResources().updateConfiguration(config,
		// getBaseContext().getResources().getDisplayMetrics());

		// Intent refresh = new Intent(this, AndroidLocalize.class);
		// startActivity(refresh);

		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String key = new String(MyBase64.encode(md.digest()));
				// String key = new String(Base64.encodeBytes(md.digest()));
				// Log.e("Hash key", key);
				System.out.println("---sigunature key-key-----:" + key);
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
					setLocale("ar");
					break;
				case 2:
					setLocale("ar");
					break;
				default:
					break;
				}
				finish();
				Intent intent = new Intent(LanguageActivity.this,
						LoginActivity.class);
				// LoginActivity.class);
				startActivity(intent);
			}
		});
		return builder.create();
	}
}
