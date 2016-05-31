package com.kainat.app.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kainat.util.AppUtil;
import com.example.kainat.util.CompressImage;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.R.string;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.User;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.Auth;
import com.kainat.app.android.facebook.DialogError;
import com.kainat.app.android.facebook.Facebook.DialogListener;
import com.kainat.app.android.facebook.FacebookError;
import com.kainat.app.android.facebook.Util;
import com.kainat.app.android.inf.AuthInf;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.RequestType;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class KainatCreateProfileActivity extends UIActivityManager implements
OnClickListener, AuthInf,DialogListener {

	com.kainat.app.android.uicontrol.CImageView imageView;
	ImageLoader imageLoader = ImageLoader.getInstance();
	KainatCreateProfileActivity mContext;
	boolean select;
	Handler handler = new Handler();
	Spinner image_spinner, gender_spineer;
	ProgressDialog rTDialog;
	EditText gender,email,name;
	TextView skip_done_save;
	int gender_vlaue;
	private final String URL_UPDATE_PROFILE = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/updateprofile?";
	boolean pictureClear ;
	private int CallFrom =0;
	int updateProfile;
	final byte UPDATE_PIC = 1;
	final byte bit_pic = 0;
	final byte UPDATE_NAME = 2;
	final byte bit_name = 1;
	final byte UPDATE_EMAIL = 4;
	final byte bit_email = 2;
	final byte UPDATE_GENDER = 8;
	final byte bit_gender = 3;
	boolean removeImage =false;
	boolean hasProfilePic;
	
	static String[] GRID_DATA= new String[] {
			"Sports" ,
			"News", 
			"Entertainment" ,
			"Lifestyle",
		"Spirituality" };
		static String[] GRID_DATA_IMG = new String[] { 
			"Sports",
			"News",
			"Entertainment",
			"Lifestyle",
		"Spirituality" };
		public static final int[] GRID_DATA_IMG_INT = new int[] {
			R.drawable.sports,
			R.drawable.news,
			R.drawable.entertainment,
			R.drawable.lifestyle,
			R.drawable.spirituality};
		HorizontalListView listViewIntrestes;
		LinearLayout layout_interests ;
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true)
				.resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.male_icon)
				.showImageOnFail(R.drawable.male_icon)
				.showImageOnLoading(R.drawable.male_icon)
				.build();
	
	ArrayList<String> str_Intrest =  new ArrayList<String>();
	ArrayList<String> str_Intrest_main =  new ArrayList<String>();
	ArrayList<String> str_Intrest_main_Id =  new ArrayList<String>();
	int Intrestchanged = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		KeyValue.setBoolean(this, KeyValue.VERIFIED, true);
		KeyValue.setBoolean(this, "INCOMP", true);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.complete_profile_activity);

		/*options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true)
				.showImageForEmptyUri(R.drawable.attachicon)
				.showImageOnFail(R.drawable.attachicon)
				.showImageOnLoading(R.drawable.attachicon).build();*/
		skip_done_save = (TextView) findViewById(R.id.done);
		layout_interests =(LinearLayout) findViewById(R.id.layout_interests);
		layout_interests.setVisibility(View.GONE);
		skip_done_save.setOnClickListener(this);
		imageView = (com.kainat.app.android.uicontrol.CImageView) findViewById(R.id.image);
		imageView.setOnClickListener(this);
		String IntrestAlready = getIntent().getStringExtra("IntrestAlready");
		String IntrestAlreadyID = getIntent().getStringExtra("IntrestAlreadyID");
	try{
		if(IntrestAlready != null || ! (IntrestAlready.isEmpty()))
		{
			str_Intrest= new ArrayList<String>(Arrays.asList(IntrestAlready.split(",")));
			str_Intrest_main_Id = new ArrayList<String>(Arrays.asList(IntrestAlreadyID.split(",")));
			str_Intrest_main =new ArrayList<String>(str_Intrest);
		}
	}catch(Exception e){
		Log.e("Intrest", ""+e);
	}
		email = (EditText) findViewById(R.id.email);
		email.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				//				System.out.println("Email::onTextChanged--> "+s);
				if(email.getText().toString().equalsIgnoreCase(SettingData.sSelf.getEmail()))
				{
					updateProfile &= ~(1 << bit_email);
					if(updateProfile > 0)
					{
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
							skip_done_save.setText(R.string.done);
						else
							skip_done_save.setText(R.string.next);
					}
					else 
					{
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
							skip_done_save.setVisibility(View.GONE);
						else
							skip_done_save.setText(R.string.skip);
					}
				}
				else
				{
					updateProfile |= UPDATE_EMAIL;
					skip_done_save.setVisibility(View.VISIBLE);
					if(getIntent().getIntExtra("CallFrom", 0) == 1)
						skip_done_save.setText(R.string.done);
					else
						skip_done_save.setText(R.string.next);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//				System.out.println("Email::beforeTextChanged--> "+s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//				System.out.println("Email::afterTextChanged--> "+s);
			}
		});
		if(skip_done_save.getText().toString().equals(getString(R.string.skip)))
		{
			skip_done_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_arrows_right, 0);
		}
		name = (EditText) findViewById(R.id.name);
		name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				//				System.out.println("Name::onTextChanged--> "+s);
				if(s.toString().equalsIgnoreCase((SettingData.sSelf.getFirstName()+" "+SettingData.sSelf.getLastName()).trim()))
				{
					updateProfile &= ~(1 << bit_name);
					if(updateProfile > 0)
					{
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
						{
							skip_done_save.setText(R.string.done);
							skip_done_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
						}
						else
						{
							skip_done_save.setText(R.string.next);
							skip_done_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_arrows_right, 0);
						}
					}
					else 
					{
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
							skip_done_save.setVisibility(View.GONE);
						else
						{
							skip_done_save.setText(R.string.skip);
							skip_done_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_arrows_right, 0);
						}
					}
				}
				else
				{
					updateProfile |= UPDATE_NAME;
					skip_done_save.setVisibility(View.VISIBLE);
					if(getIntent().getIntExtra("CallFrom", 0) == 1)
					{
						skip_done_save.setText(R.string.done);
						skip_done_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
					else
					{
						skip_done_save.setText(R.string.next);
						skip_done_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x_arrows_right, 0);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//				System.out.println("Name::beforeTextChanged--> "+s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//				System.out.println("Name::afterTextChanged--> "+s);
			}
		});

		gender = (EditText) findViewById(R.id.gender);
		gender.setOnClickListener(this);

		gender_spineer = (Spinner) findViewById(R.id.gender_spineer);
		image_spinner = (Spinner) findViewById(R.id.image_spinner);

		if(SettingData.sSelf.getFirstName()!=null &&  SettingData.sSelf.getLastName() !=null && SettingData.sSelf.getFirstName().length()>0 &&
				SettingData.sSelf.getLastName().length()>0)
		{
			if(!SettingData.sSelf.getFirstName().equalsIgnoreCase(getString(R.string.app_name)))
				name.setText(SettingData.sSelf.getFirstName()+" "+SettingData.sSelf.getLastName());
		}
		else if(SettingData.sSelf.getFirstName()!=null && 
				SettingData.sSelf.getLastName() !=null && !SettingData.sSelf.getLastName().equalsIgnoreCase(getString(R.string.app_name)))
		{
			if(!SettingData.sSelf.getFirstName().equalsIgnoreCase(getString(R.string.app_name)))
				name.setText(SettingData.sSelf.getFirstName());
		}
		else if(SettingData.sSelf.getFirstName()!=null && 
				SettingData.sSelf.getLastName() !=null && 
				SettingData.sSelf.getFirstName().length()>0 &&
				SettingData.sSelf.getLastName().length()>0)
		{
			if(!SettingData.sSelf.getFirstName().equalsIgnoreCase(getString(R.string.app_name)) && !SettingData.sSelf.getLastName().equalsIgnoreCase(getString(R.string.app_name)))
				name.setText(SettingData.sSelf.getFirstName()+" "+SettingData.sSelf.getLastName());
		}

		if(SettingData.sSelf.getFirstName()!=null && !SettingData.sSelf.getFirstName().equalsIgnoreCase(getString(R.string.app_name))){
			if(SettingData.sSelf.getEmail() != null){
				email.setText(SettingData.sSelf.getEmail());
			}else{
				email.setText("");
			}
		}
	/*	if(SettingData.sSelf.getGender()!= -1 && !SettingData.sSelf.getFirstName().equalsIgnoreCase(getString(R.string.app_name)))
		{
			if(SettingData.sSelf.getGender()==1)
				gender.setText(getString(R.string.male));
			else
				gender.setText(getString(R.string.female));
		}*/

		if(!KeyValue.getBoolean(this, "OLDUSER"))
		{
			skip_done_save.setText(R.string.next);
		}

		findViewById(R.login.login_fb).setOnClickListener(this);

		if(User.getInstance().userId!=null)
		{
			social = new Social();
			social.execute("");
		}
		if( /*getIntent().getBooleanExtra("oldUser", false) && */SettingData.sSelf!=null && SettingData.sSelf.getUserName()!=null){
			ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

				@Override
				public void onThumbnailResponse(ThumbnailImage value,
						byte[] data) {
					try {
					//	hasProfilePic = true;
						
						imageView.setImageBitmap(value.mBitmap);
						imageView.invalidate() ;						
					} catch (Exception e) {
					}
				}
			};
			hasProfilePic = true;
			imageView.setTag(SettingData.sSelf.getUserName()) ;
			imageLoader.displayImage(SettingData.sSelf.getUserName(), imageView, options);
			//imageManager.download(SettingData.sSelf.getUserName(),imageView, handler,ImageDownloader.TYPE_THUMB_BUDDY);
		}else
			findViewById(R.id.change_pic).setVisibility(View.GONE);


		findViewById(R.id.change_pic).setVisibility(View.GONE);

		((TextView)findViewById(R.id.prev)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				finish();
				onBackPressed();
			}
		});
		CallFrom = getIntent().getIntExtra("CallFrom", 0); 
		if(CallFrom ==1)
		{// From ProfileViewActivity Class (Edit case)
			skip_done_save.setVisibility(View.GONE);
			TextView or_profile = (TextView)findViewById(R.id.or_profile);
			TextView or_profile_txt = (TextView)findViewById(R.id.or_profile_txt);
			or_profile_txt.setVisibility(View.GONE);
			or_profile.setVisibility(View.GONE);
			TextView login_fb = (TextView)findViewById(R.login.login_fb);
			login_fb.setVisibility(View.GONE);
			//kfdhdfhos
			layout_interests.setVisibility(View.VISIBLE);
			TextView txt_header_profile =(TextView)findViewById(R.id.txt_header_profile);
			txt_header_profile.setText(R.string.update_profile_p);
			KeyValue.setBoolean(this, "INCOMP", false);
			((TextView)findViewById(R.id.prev)).setVisibility(View.VISIBLE);
		}
		
		//Show Intrests
		listViewIntrestes = (HorizontalListView) findViewById(R.id.listview_interests);
		listViewIntrestes.setAdapter(mAllCategoryAdapter);

		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Create Profile Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	private BaseAdapter mAllCategoryAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(ProfileViewActivity.this, SearchCommunityActivity.class);
//				intent.putExtra("TYPE", 1);
//				intent.putExtra("CommunityType", v.getTag().toString());
//				intent.putExtra("UrlIs","");
//				startActivity(intent);

			}
		};

		@Override
		public int getCount() {
			return GRID_DATA.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_profile_interest_row, null);
			TextView title = (TextView) retval.findViewById(R.id.txt_community_name);
			final CheckBox check_box = (CheckBox) retval.findViewById(R.id.check_box);
			check_box.setTag(position);
			int abc = -1;
			abc = str_Intrest.indexOf(GRID_DATA[position]);
			if (abc == -1) {
			} else {
			    check_box.setChecked(true);
			}
			/*check_box.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!check_box.isChecked())
						check_box.setChecked(true);
					else
						check_box.setChecked(false);
				}
			});*/
			check_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						str_Intrest.add(GRID_DATA[(Integer)check_box.getTag()]);
					}else
					{
						str_Intrest.remove(str_Intrest.indexOf(GRID_DATA[(Integer)check_box.getTag()]));
					}
					skip_done_save.setVisibility(View.VISIBLE);
					skip_done_save.setText(R.string.done);
				}
			});
			title.setText(GRID_DATA[position]);
			retval.setTag(GRID_DATA[position]);
			ImageView img_community_background = (ImageView)retval.findViewById(R.id.img_community_background);
			img_community_background.setId(6000+position);
			imageLoader.displayImage("drawable://"+GRID_DATA_IMG_INT[position], img_community_background, options);
			return retval;
		}

	};
	
	@Override
	protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(KainatCreateProfileActivity.this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(KainatCreateProfileActivity.this).reportActivityStop(this);
    }

	public void getProfileInformation() {
		final String mUserId,mUserToken,mUserName,mUserEmail;
	    try {

	        JSONObject profile = Util.parseJson(Auth.mFacebook.request("me"));
	        Log.e("Profile", "" + profile);

	        mUserId = profile.getString("id");
	        mUserToken = Auth.mFacebook.getAccessToken();
	        mUserName = profile.getString("name");
	        mUserEmail = profile.getString("email");

	        runOnUiThread(new Runnable() {

	            public void run() {

	                Log.e("FaceBook_Profile",""+mUserId+"\n"+mUserToken+"\n"+mUserName+"\n"+mUserEmail);

	                Toast.makeText(getApplicationContext(),
	                        "Name: " + mUserName + "\nEmail: " + mUserEmail,
	                        Toast.LENGTH_LONG).show();



	            }

	        });

	    } catch (FacebookError e) {

	        e.printStackTrace();
	    } catch (MalformedURLException e) {

	        e.printStackTrace();
	    } catch (JSONException e) {

	        e.printStackTrace();
	    } catch (IOException e) {

	        e.printStackTrace();
	    }

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.terms_condition_link:
			Intent intent = new Intent(KainatCreateProfileActivity.this, CommunityWebViewActivity.class);
			startActivity(intent);
			break;
		case R.id.gender:
			onCreateDialogGendeer().show();
			break;
		case R.id.image:
			if(hasProfilePic)
				onCreateDialogPricture().show();
			else
				onCreateDialogPrictureWithoutClear().show();
			break;
		case R.id.done:
			//			finish();
			//			Intent intent = new Intent(CompleteProfileActivity.this,
			//					TourActivity.class);
			//			startActivity(intent);

			//			EditText gender,email,name;
			//if(CallFrom==0){
			//if(name.isFocused()  || email.isFocused()){
			
			InputMethodManager imm = (InputMethodManager)getSystemService(KainatCreateProfileActivity.INPUT_METHOD_SERVICE);
			// if (imm.isAcceptingText()) {
				 imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			 //   }
			//}
				 
				 new ProfileTask().execute(URL_UPDATE_PROFILE) ;
			if(skip_done_save.getText().toString().equalsIgnoreCase(getString(R.string.skip)))
			{
				//Intent intent02 = new Intent(KainatCreateProfileActivity.this,InterestActivity.class);
				Intent intent02 = new Intent(KainatCreateProfileActivity.this,KainatHomeActivity.class);
				intent02.putExtra("REG_FLOW", true);
				startActivity(intent02);
				KeyValue.setBoolean(KainatCreateProfileActivity.this, "INCOMP", false);
				finish();
				return;
			}
				/*if(getIntent().getIntExtra("CallFrom", 0) !=  1)
				{

					if(mImagesPath.contains("del")||(SettingData.sSelf.getFirstName()!=null && SettingData.sSelf.getFirstName().length() > 0)){

						Intent intent01 = new Intent(mContext,
								KainatHomeActivity.class);	
						KeyValue.setBoolean(KainatCreateProfileActivity.this, "INCOMP", false);
						startActivity(intent01);
						try {
							handleLoginResponseNew(null);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						Intent intent02 = new Intent(KainatCreateProfileActivity.this, TourActivity.class);
						startActivity(intent02);
						KeyValue.setBoolean(KainatCreateProfileActivity.this, "INCOMP", false);
					}

				}
*/			

			//			if(!Utilities.checkName(name.getText().toString()))
			if(name.getText().toString().trim().length() < 3)
			{
				AppUtil.showTost(this, getString(R.string.enter_name));
				name.requestFocus();
				return ;
			}
			if(str_Intrest.size()<=0 && layout_interests.isShown()){
				AppUtil.showTost(this, getString(R.string.PleaseIntrest));
				
				return ;
			}
			if(email.getText().toString().length()<=0){
				AppUtil.showTost(this, getString(R.string.enter_email));
				email.requestFocus();
				return ;
			}
			if (!Utilities.checkEmail(email.getText().toString())) {
				//				showSimpleAlert("Error", "Please enter a valid email!");
				AppUtil.showTost(this, getString(R.string.please_enter_valid_email));
				email.requestFocus();
				return;
			}

			String names[] = name.getText().toString().split(" ",2) ;
			if(names!=null){
				if(names.length > 1){
					SettingData.sSelf.setFirstName(User.getInstance().firstName=names[0]);
					SettingData.sSelf.setLastName(User.getInstance().lastName=names[1]);						
				}else if(names.length == 1){
					SettingData.sSelf.setFirstName(User.getInstance().firstName=names[0]);
					SettingData.sSelf.setLastName(User.getInstance().lastName="");	
				}else{
					SettingData.sSelf.setFirstName(User.getInstance().firstName="");
					SettingData.sSelf.setLastName(User.getInstance().lastName="");	
				}
			}else{
				SettingData.sSelf.setFirstName(User.getInstance().firstName="");
				SettingData.sSelf.setLastName(User.getInstance().lastName="");	
			}

			//			SettingData.sSelf.setFirstName(name.getText().toString());
			//			SettingData.sSelf.setLastName(");	

			if(gender.getText()!=null){
				if(gender.getText().toString().equalsIgnoreCase(getString(R.string.male)))
					SettingData.sSelf.setGender(1);
				else if(gender.getText().toString().equalsIgnoreCase(getString(R.string.female)))
					SettingData.sSelf.setGender(2);
				else 
					SettingData.sSelf.setGender(2);
			}
			SettingData.sSelf.setEmail(email.getText().toString());
			//
			//Hit data to server 
			//
			//
			try{
				ArrayList<String> newInsertionHit = new ArrayList<String>();
		if(str_Intrest.size()>0){
			for(int x_arr = 0;x_arr<str_Intrest.size();x_arr++){
				for(int y_arr = 0;y_arr<str_Intrest_main.size();y_arr++){
					if(str_Intrest.get(x_arr)==str_Intrest_main.get(y_arr)){
						str_Intrest_main.remove(y_arr);
						str_Intrest_main_Id.remove(y_arr);
						str_Intrest.remove(x_arr);
					}
				}
			}
			for(int i=0;i<str_Intrest.size();i++){
				AddInterestTask addInterest = new AddInterestTask();
				addInterest.execute(str_Intrest.get(i));
			}
			for(int ii=0;ii<str_Intrest_main_Id.size();ii++){
				DeleteInterestTask deleteInterest = new DeleteInterestTask();
				deleteInterest.execute(str_Intrest_main_Id.get(ii));
				/*if(str_Intrest_main_Id.size()-1 == ii){
					finish();
				}*/
			}
		}else
		{
			Intrestchanged = 0;
		}
			
		}catch(Exception e){
			Log.v("Get_InterestTask Exception", ""+e);
		}
			//
			//
			
			
			break;
		case R.login.login_fb:
			// if (!checkInternetConnection()) {
			// networkLossAlert();
			// return;
			// }
			rtDialog = ProgressDialog.show(KainatCreateProfileActivity.this, "", getString(R.string.updating), true);
			// cleanSetting();
			Auth.getInstance().setListener(this);
			Auth.getInstance().initFB(this);
			break;
		}
	}

	ProgressDialog rtDialog;
	boolean imageSet ;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {

			if (resultCode == mContext.RESULT_OK) {

				switch (requestCode) {
				case 32665:
					if (Auth.getInstance().mFacebook != null){
						Auth.getInstance().mFacebook.authorizeCallback(requestCode,resultCode, data);
						getProfileInformation();
					}
					break;
				case AppUtil.PIC_CROP:
					imageSet = true;
					pictureClear = true ;
					//					File tempFile = AppUtil.getTempFile();

					String filePath= Environment.getExternalStorageDirectory()
							+"/"+Constants.contentTemp+"/"+AppUtil.TEMP_PHOTO_FILE;
					//					System.out.println("path "+filePath);

					AppUtil.capturedPath1 = filePath ;
					mImagesPath.clear();
					mImagesPath.add(AppUtil.capturedPath1) ;
					Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
					//                    _image = (ImageView) findViewById(R.id.image);
					imageView.setImageBitmap(selectedImage);
					if(mImagesPath.size() > 0)
					{
						updateProfile |= UPDATE_PIC;
						skip_done_save.setVisibility(View.VISIBLE);
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
							skip_done_save.setText(R.string.done);
						else
							skip_done_save.setText(R.string.next);
					}
					//					Bundle extras = data.getExtras();
					//					AppUtil.setImage(imageView,
					//							(Bitmap) extras.getParcelable("data"));
					//					saveBitmap((Bitmap) extras.getParcelable("data"));
					break;
				case AppUtil.PIC_CROP_LEFT:
					Bundle	extras = data.getExtras();
					imageSet = true;
					AppUtil.setImage(imageView,
							(Bitmap) extras.getParcelable("data"));
					//					saveBitmap((Bitmap) extras.getParcelable("data"));
					break;
				case AppUtil.PIC_CROP_RIGHT:
					extras = data.getExtras();
					imageSet = true;
					AppUtil.setImage(imageView,
							(Bitmap) extras.getParcelable("data"));
					//					saveBitmap((Bitmap) extras.getParcelable("data"));
					break;
				case AppUtil.POSITION_CAMERA_PICTURE:
				case AppUtil.POSITION_GALLRY_PICTURE:
					if (data != null && data.getData() != null) 
					{
						Uri uri = data.getData() ;
						AppUtil.capturedPath1 = AppUtil.getPath(uri,mContext);
						BitmapFactory.Options o = new BitmapFactory.Options();
						o.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(getAbsolutePath(uri), o);
						int imageHeight = o.outHeight;
						int imageWidth = o.outWidth;
						if(imageHeight<Constants.CROP_DIMEN && imageWidth<Constants.CROP_DIMEN)
						{					        	
							//							AppUtil.showTost(CompleteProfileActivity.this, getString(R.string.image_size_too_small)) ;
							//							return;
							mImagesPath.clear();
							mImagesPath.add(AppUtil.capturedPath1) ;
							selectedImage =  BitmapFactory.decodeFile(AppUtil.capturedPath1);
							imageView.setImageBitmap(selectedImage);
							if(mImagesPath.size() > 0)
							{
								updateProfile |= UPDATE_PIC;
								skip_done_save.setVisibility(View.VISIBLE);
								if(getIntent().getIntExtra("CallFrom", 0) == 1)
									skip_done_save.setText(R.string.done);
								else
									skip_done_save.setText(R.string.next);
							}
						}
						else
						{
							CompressImage compressImage = new CompressImage(mContext);
							AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
						}
					}
					performCrop(AppUtil.PIC_CROP);
					break;
				case AppUtil.POSITION_GALLRY_PICTURE_LEFT:
					if (data != null && data.getData() != null) {
						AppUtil.capturedPath1 = AppUtil.getPath(data.getData(),
								mContext);
						CompressImage compressImage = new CompressImage(
								mContext);
						AppUtil.capturedPath1 = compressImage
								.compressImage(AppUtil.capturedPath1);
					}
					performCrop(AppUtil.PIC_CROP_LEFT);
					break;
				case AppUtil.POSITION_GALLRY_PICTURE_RIGHT:
					if (data != null && data.getData() != null) {
						AppUtil.capturedPath1 = AppUtil.getPath(data.getData(),
								mContext);
						CompressImage compressImage = new CompressImage(
								mContext);
						AppUtil.capturedPath1 = compressImage
								.compressImage(AppUtil.capturedPath1);
					}
					performCrop(AppUtil.PIC_CROP_RIGHT);
					break;
				}
			}
			else if (resultCode == mContext.RESULT_CANCELED)
			{
				mImagesPath.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		// performCrop();
	}

	public String getAbsolutePath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	private void performCrop(byte resultCode) {
		try {
			//			System.out.println("----AppUtil.capturedPath1:" + AppUtil.capturedPath1);

			mImagesPath.clear();
			mImagesPath.add(AppUtil.capturedPath1) ;
			//			
			//			
			//			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
			//			        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			//			photoPickerIntent.setType("image/*");
			//			photoPickerIntent.putExtra("crop", "true");
			//			photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
			//			photoPickerIntent.putExtra("outputFormat", Bitmap.Constants.COMPRESS_TYPE.toString());
			//			startActivityForResult(photoPickerIntent, resultCode);

			//			if(1==1)return ;
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
			//			System.out.println("----outputFileUri:" + outputFileUri);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("outputX", 300);
			cropIntent.putExtra("outputY", 300);
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("scale", true);
			try {
				cropIntent.putExtra("return-data", false);
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
				cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());	
				startActivityForResult(cropIntent, resultCode);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public Dialog onCreateDialogPricture() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_picture).setItems(R.array.image_choose01,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					removeImage = false;
					AppUtil.openCamera(mContext, AppUtil.capturedPath1,
							AppUtil.POSITION_CAMERA_PICTURE);
					break;
				case 1:
					removeImage = false;
					AppUtil.openImageGallery(mContext,
							AppUtil.POSITION_GALLRY_PICTURE);
					break;
				case 2:
					removeImage = true;
					imageView.setImageResource(R.drawable.male_icon);
					new ProfileTask().execute(URL_UPDATE_PROFILE) ;
					UIActivityManager.myProfilePicURL = null;
					break;
				}
			}
		});
		return builder.create();
	}
	public Dialog onCreateDialogPrictureWithoutClear() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_picture).setItems(R.array.image_choose02,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					removeImage = false;
					AppUtil.openCamera(mContext, AppUtil.capturedPath1,
							AppUtil.POSITION_CAMERA_PICTURE);
					break;
				case 1:
					removeImage = false;
					AppUtil.openImageGallery(mContext,
							AppUtil.POSITION_GALLRY_PICTURE);
					break;
				}
			}
		});
		return builder.create();
	}


	public Dialog onCreateDialogGendeer() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_gender).setItems(R.array.gender,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					gender.setText(getString(R.string.male));
					if(!imageSet)
						imageView.setImageResource(R.drawable.male_icon);
					//					SettingData.sSelf.setGender(1);
					break;
				case 1:
					gender.setText(getString(R.string.female));
					if(!imageSet)
						imageView.setImageResource(R.drawable.male_icon);
					//					SettingData.sSelf.setGender(2);
					break;
				case 2:
					gender.setText("Not Specified");

					if(!imageSet)
						imageView.setImageResource(R.drawable.male_icon);
					//					SettingData.sSelf.setGender(3);
					break;
				}
				gender_vlaue = which + 1;
				if(SettingData.sSelf.getGender() == gender_vlaue)
				{
					updateProfile &= ~(1 << bit_gender);
					if(updateProfile > 0)
					{
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
							skip_done_save.setText(R.string.done);
						else
							skip_done_save.setText(R.string.next);
					}
					else 
					{
						if(getIntent().getIntExtra("CallFrom", 0) == 1)
							skip_done_save.setVisibility(View.GONE);
						else
							skip_done_save.setText(R.string.skip);
					}
				}
				else
				{
					updateProfile |= UPDATE_GENDER;
					skip_done_save.setVisibility(View.VISIBLE);
					if(getIntent().getIntExtra("CallFrom", 0) == 1)
						skip_done_save.setText(R.string.done);
					else
						skip_done_save.setText(R.string.next);
				}
			}
		});
		return builder.create();
	}

	@Override
	public void onAuthSucceed() {
		social = new Social();
		social.execute("");
		//		AppUtil.showTost(mContext, "onAuthSucceed") ;
	}

	Social social;

	@Override
	public void onAuthFail(String error) {
		// TODO Auto-generated method stub
		//		AppUtil.showTost(mContext, "onAuthFail") ;
		closeDialog();
	}

	@Override
	public void onLogoutBegin() {
		// TODO Auto-generated method stub
		//		AppUtil.showTost(mContext, "onLogoutBegin") ;

	}

	@Override
	public void onLogoutFinish() {
		//		AppUtil.showTost(mContext, "onLogoutFinish") ;
		closeDialog();
	}

	private class Social extends AsyncTask<String, Void, String> {

		Bitmap bitmap ;
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			do {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			} while (User.getInstance().userId == null);

			bitmap = AppUtil.getBitmapFromURL(User.getInstance().profilePic) ;
			//			saveBitmap(bitmap);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			//			AppUtil.showTost(mContext, User.getInstance().userId) ;
			if(User.getInstance().firstName != null && User.getInstance().firstName.length() > 0 || (User.getInstance().lastName != null && User.getInstance().lastName.length() > 0))
			{
				name.setText(User.getInstance().firstName+" "+User.getInstance().lastName);
				updateProfile |= UPDATE_NAME;
			}
			if(User.getInstance().email != null && User.getInstance().email.length() > 0)
			{
				email.setText(User.getInstance().email);
				updateProfile |= UPDATE_EMAIL;
			}
			if(User.getInstance().gender != null && User.getInstance().gender.length() > 0)
			{
				gender.setText(User.getInstance().gender);
				updateProfile |= UPDATE_GENDER;
			}

			closeDialog();
			imageSet = true;
			AppUtil.setImage(imageView,bitmap);
			super.onPostExecute(result);

			//			if(User.getInstance().gender!=null){
			//				if(User.getInstance().gender.equalsIgnoreCase("male")){
			//					imageView.setImageResource(R.drawable.male);
			//				}else if(User.getInstance().gender.equalsIgnoreCase("female")){
			//					imageView.setImageResource(R.drawable.female);
			//				}else
			//					imageView.setImageResource(R.drawable.female);
			//			}

		}
	}

	private void closeDialog(){
		if(rtDialog != null && rtDialog.isShowing())
			rtDialog.dismiss();
	}

	String url = "https://m.ak.fbcdn.net/profile.ak/hprofile-ak-xpf1/v/t1.0-1/c14.0.50.50/p50x50/10613092_1456810377936853_7796915155812873034_n.jpg?oh=97f308de3788119acb9eb8d9727e7a19&oe=55195342&__gda__=1423990422_5fa79f19dde366f9e63111d8d84271e3";


	//	private void saveBitmap(Bitmap finalBitmap){
	//		String root = Environment.getExternalStorageDirectory().toString();
	//		File myDir = new File(root + "/kainat");    
	//		myDir.mkdirs();
	//		Random generator = new Random();
	//		int n = 10000;
	//		n = generator.nextInt(n);
	////		String fname = "kainat-"+ n +".jpg";
	//		
	//		String fname = "kainat_profile.jpg";
	//		
	//		
	//		
	//		File file = new File (myDir, fname);
	//		
	//		
	//		mImagesPath.clear();
	//		mImagesPath.add(file.getAbsolutePath()) ;
	//		
	//		
	//		if (file.exists ()) file.delete (); 
	//		try {
	//		       FileOutputStream out = new FileOutputStream(file);
	//		       finalBitmap.compress(Constants.COMPRESS_TYPE, Constants.COMPRESS, out);
	//		       out.flush();
	//		       out.close();
	//
	//		} catch (Exception e) {
	//		       e.printStackTrace();
	//		}
	//
	//	}
	private List<String> mImagesPath = new ArrayList<String>();
	private class ProfileTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			rtDialog = ProgressDialog.show(KainatCreateProfileActivity.this, "", getString(R.string.updating), true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				String img_file_id = null;
				if((updateProfile & UPDATE_PIC) > 0)
					for (int i = 0; i < mImagesPath.size(); i++) {
						String fileId = Utilities.createMediaID(mImagesPath.get(i),Constants.ID_FOR_UPDATE_PROFILE);
						if(fileId.indexOf("}")!=-1)
						{
							if(fileId.contains("message"))
							{
								final JSONObject jsonObject = new JSONObject(fileId);
								if(jsonObject.has("message"))
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											try {
												AppUtil.showTost(KainatCreateProfileActivity.this, jsonObject.getString("message"));
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}		
										}
									});
							}
							return null ;//fileId ;
						}
						// fileIdVec.add(fileId) ;
						if (fileId != null) {
							if (img_file_id == null)
								img_file_id =  fileId + "";
							else
								img_file_id = img_file_id + fileId+ "";
						}
					}
				String s = "Nepal";
				//					if(s!=null){
				//						param = param+"country="
				//								+ s + "&";
				////						saving =true ;
				//						}
				Hashtable<String, String> postParam = new Hashtable<String, String>();
				String temp = null;
				if(KeyValue.getBoolean(KainatCreateProfileActivity.this, "OLDUSER") || !KeyValue.getBoolean(KainatCreateProfileActivity.this, "INCOMP"))
					postParam.put("mode", "update");
				else
					postParam.put("mode", "create");
				if((updateProfile & UPDATE_NAME) > 0)
				{
					s = SettingData.sSelf.getFirstName();
					if(s!=null){
						//						param = param+"fname=" +  URLEncoder.encode(s) + "&";
						temp = s;//FN
						//						postParam.put("fname", s);
					}
					s = SettingData.sSelf.getLastName();
					if(s!=null && s.trim().length() > 0)
					{
						temp += " " + s;//FN LN
						//						param = param+"lname=" + URLEncoder.encode(s) + "&";
						//						postParam.put("lname", s);
					}
					postParam.put("name", temp);
					temp = null;
				}
				if((updateProfile & UPDATE_EMAIL) > 0)
				{
					s = SettingData.sSelf.getEmail();
					if(s!=null)
					{
						//						param = param+"email=" + URLEncoder.encode(s) + "&";
						postParam.put("email", s);
					}
				}
				if((updateProfile & UPDATE_GENDER) > 0)
				{
					s = SettingData.sSelf.getGender()==1 ? "M" : "F";
					if(s!=null)
					{
						//						param = param+"gender=" + URLEncoder.encode(s) + "&";
						postParam.put("gender", s);
					}
				}
				
				if(removeImage== true)
				{
					postParam.put("image_file_id", "del");
				}else
				{
					if(img_file_id!=null)
					{
						//					param = param+"image_file_id=" + URLEncoder.encode(img_file_id);
						postParam.put("image_file_id", img_file_id);
					}
				}
				if(pictureClear )
					postParam.put("picture", "clear");

				//					"picture="
				//					+ "clear"

				//					if(audioClear)
				//						param = param+"audio="
				//								+ "clear"+ "&";
				//					if(pictureClear)
				//						param = param+"picture="
				//								+ "clear"+ "&";

				//					http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/completereg?app-key=15769055:aaaaaa&fname=Prince&lname=Kael&gender=M&email=kael%40kaell.com&image_file_id=1_1_5_E_I_I3_dyj5a3elg1

				//					String response = AdConnectionManager.getInstance()
				//							.uploadByteData2(url+param, null, headerParam, null);

				//					url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/completereg?" ;

				String responseString = "" ;
				HttpClient httpclientE = new DefaultHttpClient();
				HttpPost httppostw = new HttpPost( "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/completereg?");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					for (Iterator<String> iterator = postParam.keySet().iterator(); iterator.hasNext();) {
						String key = iterator.next();
						String value = postParam.get(key);
						nameValuePairs.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"))));
					}
					httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

//					System.out.println(nameValuePairs.toString());
					httppostw.setHeader("RT-Params", ClientProperty.RT_PARAMS);				
					httppostw.setHeader("client_param",
							ClientProperty.CLIENT_PARAMS + "::requesttype##"
									+ RequestType.LOGIN_FB_AUTO);
					httppostw.setHeader("RT-APP-KEY", HttpHeaderUtils
							.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),
									SettingData.sSelf.getPassword()));
					HttpResponse response = httpclientE.execute(httppostw);
					HttpEntity r_entity = response.getEntity();
					//					System.out.println("r_entity.getContentEncoding(): "+r_entity.getContentEncoding());
					//					System.out.println("r_entity.getContentType(): "+r_entity.getContentType());
					responseString = EntityUtils.toString(r_entity);
					//					System.out.println("CompleteProfileActivity::ProfileTask::doInBackground():responseString -> "+responseString);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//					responseString = AdConnectionManager.getInstance()
				//							.uploadByteData2(url, postParam, headerParam, null);
				ImageDownloader.clearCache();

				Gson gson = new Gson();
				final com.kainat.app.android.bean.Error error = gson.fromJson(responseString,
						com.kainat.app.android.bean.Error.class);
				if(error!=null){
					if(error.getStatus()==2){/*
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AppUtil.showTost(KainatCreateProfileActivity.this, error.getDesc());		
							}
						});
						return null;
					*/}
				}
				return responseString;

			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatCreateProfileActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			try{
				closeDialog();
			}catch(Exception e){
				
			}
			
			if(response!=null)
			{	
				if(CallFrom == 0)
				{	
					if(removeImage){
						removeImage = false;
						hasProfilePic = false;
						return;
					}
					if(KeyValue.getBoolean(KainatCreateProfileActivity.this, "OLDUSER")){
						Intent intent01 = new Intent(mContext,
								KainatHomeActivity.class);	
						KeyValue.setBoolean(KainatCreateProfileActivity.this, "INCOMP", false);
						startActivity(intent01);
						try {
							handleLoginResponseNew(null);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						Intent intent = new Intent(KainatCreateProfileActivity.this, TourActivity.class);
						startActivity(intent);
						KeyValue.setBoolean(KainatCreateProfileActivity.this, "INCOMP", false);
					}
				}
				else
				{
					Utilities.setBoolean(KainatCreateProfileActivity.this, Constants.PROFILE_UPDATED, true);
				//	Utilities.getBoolean(ProfileViewActivity.this, Constants.PROFILE_UPDATED)
					/*Intent intent = new Intent(CompleteProfileActivity.this, KainatHomeActivity.class);
					startActivity(intent);
					KeyValue.setBoolean(CompleteProfileActivity.this, "INCOMP", false);*/
				}
				updateProfile = 0;
				finish();
			}
			if(Intrestchanged == 1 && (getIntent().getIntExtra("CallFrom", 0) == 1))
			{
				//Intrestchanged = 0 ;
				finish();
			}else if(Intrestchanged == 2){
				
			}
			//			else// In this case no alert will be shown, as alert is already shown in doinbackground.
			//			{
			//				AppUtil.showTost(CompleteProfileActivity.this, getString(R.string.something_went_wrong)) ;
			//			}

			super.onPostExecute(response);
		}
	}

	final ImageDownloader imageManager = new ImageDownloader();

	@Override
	protected void onDestroy() {
		finish();
		ImageDownloader.removeBitmoaCache();
		super.onDestroy();

	}

//	@Override
//	public void onBackPressed() {
//		if(getIntent().getIntExtra("CallFrom", 0) ==  1){
//			super.onBackPressed();
//			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
//		}
//	}
	@Override
	public void onBackPressed() {
		if(!(getIntent().getBooleanExtra("REG_FLOW", false))){
			if(skip_done_save.isShown()){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.save_unsaved)
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(str_Intrest.size()>0){
						skip_done_save.performClick();
						dialog.cancel();
						}else
						{
							Toast.makeText(KainatCreateProfileActivity.this, "Select minimum one intrest .",  Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						KainatCreateProfileActivity.this.finish();
						overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);  
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else
			{
				KainatCreateProfileActivity.this.finish();
				// DataModel.sSelf.removeObject(DMKeys.ENTRY);
				overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);  
			}

		}
		/*if(User.getInstance()!= null)
	  if( !(name.getText().toString().equals(SettingData.sSelf.getFirstName()+" "+SettingData.sSelf.getLastName())) || !(gender.getText().toString().equals(User.getInstance().gender)) || (email.getText().toString().equals(User.getInstance().email))){

	   //nameEditor.setText(entry.groupName);


	  }else
	  {

	  }*/

		//  moveTaskToBack(true);
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete(Bundle values) {
		// TODO Auto-generated method stub
		  String name =  values.getString("name");
         
          
	}

	@Override
	public void onFacebookError(FacebookError e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(DialogError e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSharingDone() {
		// TODO Auto-generated method stub
		
	}

@Override
protected void onPause() {
	// TODO Auto-generated method stub
	imageManager.removeBitmoaCache();
	imageManager.clearImageview();
	super.onPause();
}

//Topic : Add Intrest to server webservice
	//Name: MAnoj Singh
	//
	private class AddInterestTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				//showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = AddInterestOnServer(urls[0]);
				if(strData != null)
				{
					Intrestchanged = 1;
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatCreateProfileActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			if(response!= null)
			{String stst="";
				try{/*
					JSONObject  jobj = new JSONObject(response);
					if(response.contains("message")){
						if(response.contains("status")){
							 stst = jobj.optString("status").toString();
						}
						if(stst.equals("success")){
							String desc = jobj.optString("message").toString();
							Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
						}else if(stst.equals("error"))
						{
							String ytError_msg = jobj.optString("ytErrors").toString();
							JSONArray jSONArray =  new JSONArray(ytError_msg);
							for (int i = 0; i < jSONArray.length(); i++) {
								JSONObject nameObjectw = jSONArray.getJSONObject(0);
								String desc = nameObjectw.optString("message").toString();
								Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
							}
						}
						//profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
						//profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
					}
				*/
					Intrestchanged = 1;
					}catch(Exception e){

				}
			}
		
		}
	}
	
	private String  AddInterestOnServer(String interest_name){
		String responseStr = null;
		try {				
			String url = "http://www.yelatalkprod.com/tejas/feeds/user/interest/add?cat_id=1&interest_name="+interest_name;
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
			try {
				HttpResponse responseHttp = client.execute(httpget);
				if (responseHttp != null) {
					responseStr = EntityUtils.toString(responseHttp.getEntity());
				}
			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}
		} catch (Exception e) {
		}
		return responseStr;
	}
	
	protected void showLoadingDialog() {
		//		showAnimationDialog("", getString(R.string.please_wait_while_loadin), true,
		//				PROGRESS_CANCEL_HANDLER);

		rTDialog = ProgressDialog.show(KainatCreateProfileActivity.this, "", getString(R.string.loading_dot), true);
	}
	//
	
	//End(25-02-2016)
	
	//
	//Topic : Delete Intrest to server webservice
		//Name: MAnoj Singh
		//
		private class DeleteInterestTask extends AsyncTask<String, Void, String> {

			@Override
			protected void onPreExecute() {			
				try{
					//showLoadingDialog();

				}catch(Exception e){

				}
			}
			@Override
			protected String doInBackground(String... urls) {
				try{
					String strData = DeleteInterestOnServer(urls[0]);
					if(strData != null)
					{
						Intrestchanged = 1;
						if(strData.contains("message") ){
							return strData;
						}
					}
					return null;
				}catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							AppUtil.showTost(KainatCreateProfileActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
						}
					});
					return null;
				}
				
			}

			@Override
			protected void onPostExecute(String response) {
				if(rTDialog!=null)
					rTDialog.dismiss();
				if(response!= null)
				{String stst="";
					try{/*
						JSONObject  jobj = new JSONObject(response);
						if(response.contains("message")){
							if(response.contains("status")){
								 stst = jobj.optString("status").toString();
							}
							if(stst.equals("success")){
								String desc = jobj.optString("message").toString();
								Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
							}else if(stst.equals("error"))
							{
								String ytError_msg = jobj.optString("ytErrors").toString();
								JSONArray jSONArray =  new JSONArray(ytError_msg);
								for (int i = 0; i < jSONArray.length(); i++) {
									JSONObject nameObjectw = jSONArray.getJSONObject(0);
									String desc = nameObjectw.optString("message").toString();
									Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
								}
							}
							//profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
							//profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
						}
					*/
						Intrestchanged = 1;	
					}catch(Exception e){

					}
				}
			
			}
		}
		
		private String  DeleteInterestOnServer(String interest_id){
			String responseStr = null;
			try {				
				String url = "http://www.yelatalkprod.com/tejas/feeds/user/interest/delete?interest_id="+interest_id;
				
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				httpget.addHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
				try {
					HttpResponse responseHttp = client.execute(httpget);
					if (responseHttp != null) {
						responseStr = EntityUtils.toString(responseHttp.getEntity());
					}
				} catch (ClientProtocolException e) {

				} catch (IOException e) {

				}
			} catch (Exception e) {
			}
			return responseStr;
		}
	
	
	
	
	//End
	

}
