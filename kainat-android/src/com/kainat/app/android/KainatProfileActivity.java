package com.kainat.app.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.Content;
import com.kainat.app.android.bean.Interest;
import com.kainat.app.android.bean.ProfileBean;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.inf.DilogInf;
import com.kainat.app.android.uicontrol.MultiPhotoViewer;
import com.kainat.app.android.uicontrol.StreemMultiPhotoViewer;
import com.kainat.app.android.util.AdConnectionManager;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.CompressImage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.DrawableClickListener;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.InterestView;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.RTDialog;
import com.kainat.app.android.util.RTDialogNotification;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VoiceMedia;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;

public class KainatProfileActivity extends UIActivityManager implements
		OnClickListener,DialogInterface.OnClickListener,DilogInf {
	private static byte SCREEN_PROFILE_MAIN = 1;
	private static byte SCREEN_SELECT_INTEREST = 2;
	private static byte SCREEN_PROFILE_VIEW = 3;
	private static final byte POSITION_PICTURE = 0;
	private static final byte POSITION_VIDEO = 2;
	private static final byte POSITION_CAMERA_PICTURE = 3;
	private static final byte POSITION_GALLRY_PICTURE = 8;
	
	private static final byte POSITION_NATIVE_VOICE_RECORD = 4;
	protected static final int RESULT_SPEECH = 6;
	private final byte MEDIA_TYPE_IMAGE = 1;
	private final byte MEDIA_TYPE_VIDEO = 2;
	private boolean refreshOnResuming = true;
	//private int SCREEN_VIEW=0;
	private Uri picUri;
	final int PIC_CROP = 4;
	private static byte SCREEN_LOCATION_VIEW = 4;
	private static final int COMPOSE_IMAGES_IDS_START = 0x00A451D0;
	private static final String TAG = KainatProfileActivity.class.getSimpleName();
	long MAX_IMAGE_ATTACH_SIZE = BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
	private final LinearLayout.LayoutParams ITEM_VIEW_PARAM = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	private static long totalSize = 0;
	private static final int COUNTRY_CODE_REQUEST = 99;
	private static final byte POSITION_PICTURE_RT_CANVAS = 7;
	private static byte SCREEN_VIEW = SCREEN_PROFILE_MAIN;

	private final String URL_SAVE_INTEREST = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/interest/add?cat_id=";
	private final String URL_REMOVE_INTEREST = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/interest/delete?interest_id=";
	private final String URL_GET_INTEREST = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/interest/get?user_id=1574403";
	private final String URL_SEARCH_INTEREST = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/interest/add?cat_id=102&interes";
	private final String URL_VIEW_PROFILE = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/profile?user=";
	private final String URL_UPDATE_PROFILE = "http://" + Urls.TEJAS_HOST
			+ "/tejas/feeds/user/updateprofile?";
	
	// private final String URL_GET_INTEREST = "" ;
	//http://124.153.95.168:28080/tejas/feeds/user/updateprofile

	Handler handler = new Handler();
	ProgressWheel pw_two;
	View view;
	
	Context context;
	ListView listview;
	ArrayList<String> list;
	ProfileTask profileTask;
	boolean frontCam, rearCam;
	private String cameraImagePath = null;
	DatePickerDialog datePickerDialog;
	Calendar myCalendar;
	public View currentDateview;
	TextView dob;
	SeekBar baradd;
	TextView total_autio_time, played_autio_time;
	public boolean isAudio = false;
	
	public boolean audioClear = false;
	public boolean pictureClear = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		PackageManager pm = getPackageManager();
		frontCam = pm.hasSystemFeature("android.hardware.camera.front");
		rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		profileEdit();
		context = this;
		profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
		profileTask.execute(URL_VIEW_PROFILE + SettingData.sSelf.getUserName()
				+ "&mode=edit"+"&" + BusinessProxy.sSelf.thumbInfo);
//		BusinessProxy.sSelf.recordScreenStatistics(Constants.SCRTEEN_NAME_EDIT_PROFILE, true, true);// Add
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
		initLeftMenu();
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Profile Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());
	}

	public void profileEdit() {
		setContentView(R.layout.profile);
		dob = (TextView) findViewById(R.id.dob);
		dob.setFocusable(false);
		dob.setOnTouchListener(openDateDialog);
		findViewById(R.id.what_excites_you).setOnClickListener(this);
		findViewById(R.id.profile_link).setOnClickListener(this);
		view = findViewById(R.id.scroll) ;
		view.setVisibility(View.INVISIBLE);
		
		findViewById(R.id.VoiceUpdatedemo).setOnClickListener(this);
		
		myScreenName(Constants.SCRTEEN_NAME_EDIT_PROFILE) ;
	}

	public void profileView() {
		setContentView(R.layout.profile_view);
		profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
		profileTask.execute(URL_VIEW_PROFILE); 
	}

	
	
	@Override
	public boolean onBackPressedCheck() {
		if (SCREEN_VIEW == SCREEN_SELECT_INTEREST || SCREEN_VIEW ==SCREEN_PROFILE_VIEW) {
			SCREEN_VIEW = SCREEN_PROFILE_MAIN;
			profileEdit();
			view.setVisibility(View.VISIBLE);
			addSelectedItem(false);
			
			
			if (profileBean.pictureMediaList != null
					&& profileBean.pictureMediaList.size() > 0) {
				Content content = profileBean.pictureMediaList.get(0);//profileBean.pictureMediaList.size()-1
				loadProfilePic = new LoadProfilePic();
				loadProfilePic.execute(content.thumbUrl);
				if(profileBean.pictureMediaList.size()>1){
				((TextView) findViewById(R.id.photo_count)).setText(""
						+ profileBean.pictureMediaList.size());
				((TextView) findViewById(R.id.photo_count))
						.setVisibility(View.VISIBLE);
				}
			}
			if (profileBean.audioMediaList != null
					&& profileBean.audioMediaList.size() > 0) {
				Content content = profileBean.audioMediaList.get(0);
				voiceUrl = content.contentUrl ;
			}

//			((TextView) findViewById(R.id.title)).setText(profileBean.getDisplayName());
			
			((TextView) findViewById(R.id.display_name))
					.setText(profileBean.getDisplayName());
			((TextView) findViewById(R.id.gender)).setText(profileBean
					.getGender() + ", ");
			if(!profileBean.getAge().equalsIgnoreCase("00/00/0000") && Utilities.calCulateAge(profileBean
					.getAge())>0)
			((TextView) findViewById(R.id.age)).setText(""+Utilities.calCulateAge(profileBean
					.getAge()));
			else{
				((TextView) findViewById(R.id.age))
				.setText("16+,");
			}
			
			((TextView) findViewById(R.id.country)).setText(profileBean.getCountyCity());

			((TextView) findViewById(R.id.dob)).setText(profileBean
					.getBirthday());
			((EditText) findViewById(R.id.base_city))
					.setText(profileBean.getBaseCity());
			
			((TextView) findViewById(R.id.base_country))
			.setText(profileBean.getCountry());
			
			
			((EditText) findViewById(R.id.base_city)).setOnEditorActionListener(new OnEditorActionListener() {
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					Utilities.closeSoftKeyBoard(v, KainatProfileActivity.this);
					return true;
				}
			});
			
			((TextView) findViewById(R.id.language))
					.setText(profileBean.getLanguage());
			((TextView) findViewById(R.id.profile_link))
					.setText("http://myrt.me/"
							+ profileBean.getUsername());
			ImageView securityView = (ImageView) findViewById(R.id.tick);
			if(SettingData.sSelf.isSecurityQuestionSet() && SettingData.sSelf.isEmailVerified() && (SettingData.sSelf.getNumber() !=null && SettingData.sSelf.getNumber().trim().length()>0))
				securityView.setVisibility(View.VISIBLE);
					else
				securityView.setVisibility(View.GONE);
			if (voiceUrl != null
					&& voiceUrl.length() > 0) {
			((TextView) findViewById(R.id.VoiceUpdatedemo_txt))
			.setText("Profile message");
			((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.profile_voice);
			}else{
				
				((TextView) findViewById(R.id.VoiceUpdatedemo_txt)).setText("Speak about yourself");
				((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.recordedvoice);
				
				
			}
			
			if (profileBean.getLanguage()==null || profileBean.getLanguage().trim().length() <=0)
				((TextView) findViewById(R.id.language_textview)).setText("Language that you prefer");
				else
				((TextView) findViewById(R.id.language_textview))
				.setText("Language");
				
				if (profileBean.getBaseCity()==null || profileBean.getBaseCity().trim().length() <=0)
					((TextView) findViewById(R.id.base_cityxx)).setText("Enter your city");
					else
					((TextView) findViewById(R.id.base_cityxx))
					.setText("City");
				
			addMultiImages();
			
			if(profileBean!=null){
			Bitmap icon = null ;
			if(profileBean.getGender().equalsIgnoreCase("male"))
			icon = BitmapFactory.decodeResource(context.getResources(),
	                R.drawable.male);
			if(profileBean.getGender().equalsIgnoreCase("Female"))
				icon = BitmapFactory.decodeResource(context.getResources(),
		                R.drawable.female);
			
			((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
			}
			return true;
		}
		if(isChanged()){
			final DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
//					((TextView) findViewById(R.id.dob)).setText(profileBean.birthday) ;
//					((TextView) findViewById(R.id.base_country)).setText(profileBean.country) ;
//					((TextView) findViewById(R.id.base_city)).setText(profileBean.city) ;
//					((TextView) findViewById(R.id.language)).setText(profileBean.language) ;
//					mImagesPath = new ArrayList<String>();
//					mVoicePath = null ;
//					onBackPressed();
					finish() ;
				}
			};
			final DialogInterface.OnClickListener exitHandler2 = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			};
			showAlertMessage("RockeTalk", "Profile changes are not saved. Do you want to cancel the changes?", new int[] { DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE },
					new String[] { "Yes", "No" }, new DialogInterface.OnClickListener[] { exitHandler, exitHandler2 });
			
			return true ;
		}
		return super.onBackPressedCheck();
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	Dialog confirmationDialog;

	private void showAlert(String title, String[] positive, String negative,
			OnClickListener clickListener) {
		confirmationDialog = new Dialog(this);
		confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final LinearLayout.LayoutParams DIMENSION = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		DIMENSION.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		DIMENSION.topMargin = 2;
		DIMENSION.bottomMargin = 2;
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);
		layout.setBackgroundResource(R.drawable.diloag_bg);
		TextView textView = new TextView(this);
		textView.setText("RockeTalk");
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(18);
		textView.setLayoutParams(DIMENSION);
		textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD),
				Typeface.BOLD);
		layout.addView(textView);

		if (title != null) {
			textView = new TextView(this);
			textView.setText(title);
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.BLACK);
			textView.setLayoutParams(DIMENSION);
			layout.addView(textView);
		}
		DIMENSION.leftMargin = 20;
		DIMENSION.rightMargin = 20;
		Button button = new Button(this);
		for (int i = 0; i < positive.length; i++) {
			button = new Button(this);
			button.setText(positive[i]);
			button.setBackgroundResource(R.drawable.button_small);
			button.setTextColor(Color.WHITE);

			button.setLayoutParams(DIMENSION);
			button.setOnClickListener(clickListener);
			button.setId((i + 1));
			button.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD),
					Typeface.BOLD);

			layout.addView(button);
		}

		layout.addView(new TextView(this));
		DIMENSION.bottomMargin = 10;
		button = new Button(this);
		ViewGroup.LayoutParams p = button.getLayoutParams();
		button.setOnClickListener(clickListener);
		button.setId(0);
		button.setText(negative);
		button.setTextColor(Color.WHITE);
		button.setBackgroundResource(R.drawable.blackbutton);

		button.setLayoutParams(DIMENSION);
		button.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD),
				Typeface.BOLD);
		layout.addView(button);

		confirmationDialog.addContentView(layout, DIMENSION);

		confirmationDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		confirmationDialog.show();
	}

	private void addMultiImages() {
		String s[] = (String[]) DataModel.sSelf
				.removeObject(DMKeys.MULTI_IMAGES);
		if (s != null) {
			mImagesPath = new ArrayList<String>();
			for (int i = 0; i < s.length; i++) {
				try {
				//	picUri =s[i];
					mImagesPath.add(s[i]);
					if(!s[i].startsWith("file://"))
						s[i] ="file://"+s[i];
					
					Uri uriPath=Uri.parse(s[i]);
					
					picUri =uriPath;
					Thread.sleep(2000);
					performCrop();
				

					/*Bitmap bm = ImageUtils.getImageFileThumb(
							ProfileActivity.this, ImageUtils.getImagesFileId(
									ProfileActivity.this, s[i]));

					ImageView img = ((ImageView) findViewById(R.id.signup_thumb));
					
						if(bm!=null){
						
						img.setImageBitmap(bm);
						}else{
							BitmapFactory.Options bfo = new BitmapFactory.Options();
							bfo.inSampleSize = 4;
							bm = BitmapFactory.decodeFile(mImagesPath.get(0), bfo);
							bm = ImageUtils.rotateImage(mImagesPath.get(0), bm);
							img.setImageBitmap(bm);
						}*/
//					img.setImageBitmap(bm);
//					Utilities.startAnimition(ProfileActivity.this, img,
//							R.anim.shrink_from_top);

				} catch (Exception e) {
				}
			}
			if(s.length>1){
			((TextView) findViewById(R.id.photo_count)).setText("" + s.length);
			findViewById(R.id.photo_count).setVisibility(View.VISIBLE);
			}
		}else{
			/*if(mImagesPath!=null && mImagesPath.size()>0){
			Bitmap bm = ImageUtils.getImageFileThumb(
					ProfileActivity.this, ImageUtils.getImagesFileId(
							ProfileActivity.this, mImagesPath.get(0)));
			System.out.println("s==============================================skm");
			ImageView img = ((ImageView) findViewById(R.id.signup_thumb));
//			img.setImageBitmap(bm);
			
			if(bm!=null){
				
				img.setImageBitmap(bm);
				}else{
					BitmapFactory.Options bfo = new BitmapFactory.Options();
					bfo.inSampleSize = 4;
					bm = BitmapFactory.decodeFile(mImagesPath.get(0), bfo);
					bm = ImageUtils.rotateImage(mImagesPath.get(0), bm);
					img.setImageBitmap(bm);
				}
//			Utilities.startAnimition(ProfileActivity.this, img,
//					R.anim.shrink_from_top);
			
			if(mImagesPath.size()>1){
				((TextView) findViewById(R.id.photo_count)).setText("" + mImagesPath.size());
				findViewById(R.id.photo_count).setVisibility(View.VISIBLE);
				}
			}*/
		}

	}

	private void openCamera() {
		/*	if ((frontCam && rearCam) || (!frontCam && rearCam)) {
			cameraImagePath = null;
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			cameraImagePath = file.getPath();
			
			Uri outputFileUri = Uri.fromFile(file);
			picUri =outputFileUri;
			Intent i = new Intent(ProfileActivity.this, DgCamActivity.class);
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			i.putExtra("urlpath", cameraImagePath);

			this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		}
		if (frontCam && !rearCam) {*/
			cameraImagePath = null;
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			cameraImagePath = file.getPath();
			Uri outputFileUri = Uri.fromFile(file);
			picUri =outputFileUri;
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			i.putExtra("urlpath", cameraImagePath);
			this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		//}

	}

	@Override
	protected void onResume() {
		super.onResume();
		//addMultiImages();

		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
		{
			findViewById(R.id.few_step).setVisibility(View.VISIBLE) ;
		}
		if(findViewById(R.id.base_city)!=null)
		findViewById(R.id.base_city).setFocusable(true) ;
		BusinessProxy.sSelf.leftMenuAdFlag =false ;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				
				case PIC_CROP:
					if(mImagesPath!=null && mImagesPath.size()==5){
						showSimpleAlert("Error",
								"Max attachment reached. Can not attach more!");
						return;
					}
					
					
					if(cameraImagePath!=null && cameraImagePath.length()>0)
					if (mImagesPath.contains(cameraImagePath)) {
						showSimpleAlert("Error",
								"This image has already been attached!");
						return;
					}
					
					Bundle extras = data.getExtras();
	    			//get the cropped bitmap
	    			Bitmap thePic = extras.getParcelable("data");
					
					Bitmap bm = null;
					//CompressImage compressImage = new CompressImage(ProfileActivity.this);
					//cameraImagePath =compressImage.compressImage(cameraImagePath) ;
					CompressImage compressImage = new CompressImage(KainatProfileActivity.this);
					cameraImagePath =compressImage.compressImage(cameraImagePath) ;
					if (cameraImagePath!=null && cameraImagePath.length()>0 )
					if ((frontCam && rearCam) || (!frontCam && rearCam)) {
						/*BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither = false; // Disable Dithering mode
						bfo.inPurgeable = true;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);*/
						//if(cameraImagePath!=null && cameraImagePath.length()>0)
						//bm = ImageUtils.imageRotateafterClickforprofile(thePic,
								//cameraImagePath);
						bm=thePic;
					}
					if (cameraImagePath!=null && cameraImagePath.length()>0 && frontCam && !rearCam) {
						//bm = ImageUtils.getImageFor(cameraImagePath, 4);
						bm=thePic;
					}
				if (cameraImagePath!=null && cameraImagePath.length()>0 && isSizeReachedMaximum(cameraImagePath)) {
						/*if (BusinessProxy.sSelf.MaxSizeData == 2) {
							showSimpleAlert(
									"Error",
									"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
						} else {*/
							showSimpleAlert("Error",
									"Max attachment reached. Can not attach more!");
						//}
						return;
					}
					if(cameraImagePath!=null && cameraImagePath.length()>0)
					mImagesPath.add(cameraImagePath);
//					LinearLayout layout = (LinearLayout) findViewById(R.id.composeScreen_attachedImageLayout);
//					refreshOnResuming = false;
//					// addMultiImages();
//					addImageToList(cameraImagePath, layout, MEDIA_TYPE_IMAGE,
//							bm, mImagesPath);

					if(mImagesPath!=null && mImagesPath.size()>0)
					{
						/*bm = ImageUtils.getImageFileThumb(
								ProfileActivity.this, ImageUtils.getImagesFileId(
										ProfileActivity.this, mImagesPath.get(0)));*/
						ImageView img = ((ImageView) findViewById(R.id.signup_thumb));
						if(bm!=null){
							bm = ImageUtils.imageRotateafterClickforprofilePics(thePic,
									cameraImagePath);
							//img.setImageBitmap(bm);
						img.setImageBitmap(bm);
						}else{
							/*BitmapFactory.Options bfo = new BitmapFactory.Options();
							bfo.inSampleSize = 4;
							bm = BitmapFactory.decodeFile(mImagesPath.get(0), bfo);*/
							//bm = ImageUtils.rotateImage(mImagesPath.get(0), thePic);
							System.out.println("Image Size===="+mImagesPath.size());
							bm = ImageUtils.imageRotateafterClickforprofilePics(thePic,
									cameraImagePath);
							img.setImageBitmap(bm);
							
						}
						if(img!=null){
							img.invalidate();
						}
						profileImageCounter();
					}
					break;
				case POSITION_GALLRY_PICTURE:
					//Uri uriPath=Uri.parse(cameraImagePath);
					//picUri =uriPath;
					picUri = data.getData();
					/*if(cameraImagePath!=null && cameraImagePath.length()>0)
					if ((frontCam && rearCam) || (!frontCam && rearCam)) {
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither = false; // Disable Dithering mode
						bfo.inPurgeable = true;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
						
						//bm = ImageUtils.imageRotateafterClick(bm,
							//	cameraImagePath);
					}*/
					Thread.sleep(2000);
					
					performCrop();
					break;
				case POSITION_CAMERA_PICTURE:
					//picUri = data.getData();
					/*if(cameraImagePath!=null && cameraImagePath.length()>0)
					if ((frontCam && rearCam) || (!frontCam && rearCam)) {
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither = false; // Disable Dithering mode
						bfo.inPurgeable = true;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
						
						//bm = ImageUtils.imageRotateafterClick(bm,
							//	cameraImagePath);
					}*/
					Thread.sleep(2000);
					
					performCrop();
				/*	if (mImagesPath.contains(cameraImagePath)) {
						showSimpleAlert("Error",
								"This image has already been attached!");
						return;
					}
					
					
					
					Bitmap bm = null;
					if ((frontCam && rearCam) || (!frontCam && rearCam)) {
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither = false; // Disable Dithering mode
						bfo.inPurgeable = true;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
						bm = ImageUtils.imageRotateafterClick(bm,
								cameraImagePath);
					}
					if (frontCam && !rearCam) {
						bm = ImageUtils.getImageFor(cameraImagePath, 4);
					}
					if (isSizeReachedMaximum(cameraImagePath)) {
						if (BusinessProxy.sSelf.MaxSizeData == 2) {
							showSimpleAlert(
									"Error",
									"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
						} else {
							showSimpleAlert("Error",
									"Max attachment reached. Can not attach more!");
						//}
						return;
					}
					mImagesPath.add(cameraImagePath);
//					LinearLayout layout = (LinearLayout) findViewById(R.id.composeScreen_attachedImageLayout);
//					refreshOnResuming = false;
//					// addMultiImages();
//					addImageToList(cameraImagePath, layout, MEDIA_TYPE_IMAGE,
//							bm, mImagesPath);

					if(mImagesPath!=null && mImagesPath.size()==1)
					{
						bm = ImageUtils.getImageFileThumb(
								ProfileActivity.this, ImageUtils.getImagesFileId(
										ProfileActivity.this, mImagesPath.get(0)));
						ImageView img = ((ImageView) findViewById(R.id.signup_thumb));
						if(bm!=null){
						
						img.setImageBitmap(bm);
						}else{
							BitmapFactory.Options bfo = new BitmapFactory.Options();
							bfo.inSampleSize = 4;
							bm = BitmapFactory.decodeFile(mImagesPath.get(0), bfo);
							bm = ImageUtils.rotateImage(mImagesPath.get(0), bm);
							img.setImageBitmap(bm);
						}
					}*/
					break;
				case RESULT_SPEECH:
					if (resultCode == RESULT_OK && null != data) {

						ArrayList<String> text = data
								.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

						((TextView) findViewById(R.id.composeScreen_msgBox))
								.setText(text.get(0));
					}
					break;
				case POSITION_PICTURE_RT_CANVAS:
				case POSITION_PICTURE:
					String capturedPath = getPath(data.getData());
					if (mImagesPath.contains(capturedPath)) {
						showSimpleAlert("Error",
								"This image has already been attached!");
						return;
					}
					Bitmap bm1 = ImageUtils.getImageFor(capturedPath, 4);
					if (isSizeReachedMaximum(bm1)) {
						if (BusinessProxy.sSelf.MaxSizeData == 2) {
							showSimpleAlert(
									"Error",
									"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
						} else {
							showSimpleAlert("Error",
									"Max attachment reached. Can not attach more!");
						}
						return;
					}
					mImagesPath.add(capturedPath);

					layout = (LinearLayout) findViewById(R.id.composeScreen_attachedImageLayout);
					refreshOnResuming = false;
					// addMultiImages();
					addImageToList(capturedPath, layout, MEDIA_TYPE_IMAGE, bm1,
							mImagesPath);
					break;
				case COUNTRY_CODE_REQUEST:
					if (data == null) {
						return;
					}
					int selectedCountry = data.getIntExtra("country_index", -1);
					if (selectedCountry > -1) {
						((Button) findViewById(R.id.all_countryCodeBox))
								.setText(Constants.COUNTRY_CODE[selectedCountry]);
					}
					break;

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addImageToList(String path, LinearLayout layout, int mediaType) {
		addImageToList(path, layout, mediaType, null, null);
	}

	private void addImageToList(String path, LinearLayout layout,
			int mediaType, Bitmap bm, List<String> mImagesPath) {
		
		if(mImagesPath.size()>1){
		((TextView) findViewById(R.id.photo_count)).setText(""
				+ mImagesPath.size());
		findViewById(R.id.photo_count).setVisibility(View.VISIBLE);
		}
		RTImageView view = new RTImageView(KainatProfileActivity.this);
		view.setId(COMPOSE_IMAGES_IDS_START + mImagesPath.size());
		view.setClickable(true);
		view.setPadding(3, 3, 3, 3);
		view.setOnClickListener(this);
		view.setScaleType(ScaleType.CENTER);
		view.setLayoutParams(ITEM_VIEW_PARAM);

		if (bm == null) {
			BitmapFactory.Options bfo = new BitmapFactory.Options();
			bfo.inSampleSize = 4;
			bm = BitmapFactory.decodeFile(path, bfo);
			bm = ImageUtils.rotateImage(path, bm);
		}
		bm = Bitmap.createScaledBitmap(bm, 60, 60, true);
		view.setImageBitmap(bm);
		ImageView image = (ImageView) findViewById(R.id.signup_thumb);
		image.setImageBitmap(bm);
		// layout.addView(view);

	}

	private boolean isSizeReachedMaximum(long newSize) {

		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	// ////////////////////
	private void calculateTotSize() {
		if (mImagesPath != null) {
			totalSize = 0;
			for (int i = 0; i < mImagesPath.size(); i++) {
				try {
					// totalSize = 0 ;
					long size = ImageUtils.getImageSize(mImagesPath.get(i));
					if (isSizeReachedMaximum(size)) {
					}
				} catch (Exception e) {
				}
			}
		}
	}

	private boolean isSizeReachedMaximum(Bitmap bm) {
		long newSize = 0;
		if (null != bm) {
			newSize = ImageUtils.getImageSize(bm);
		}
		// makeToast("Image Size: " + newSize + ", Total Attachment Size: " +
		// totalSize);
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	private boolean isSizeReachedMaximum(String newPath) {
		FileInputStream fin = null;

		long newSize = 0;
		if (null != newPath) {
			try {
				fin = new FileInputStream(newPath);
				newSize = fin.available();
			} catch (IOException ex) {
				if (Logger.ENABLE)
					Logger.error(TAG, "Error getting size for path - "
							+ newPath, ex);
			} finally {
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
					}
				}
			}
		}
		// makeToast("Image Size: " + newSize + ", Total Attachment Size: " +
		// totalSize);
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	private String getMaxSizeText() {
		/*
		 * double sizeInMB = totalSize / 1024.0 / 1024.0; String text =
		 * Double.toString(sizeInMB); int index = text.indexOf('.'); if (-1 !=
		 * index && index + 5 < text.length()) { text = text.substring(0, index
		 * + 5); }
		 */
		/*
		 * return String .format(
		 * "Max count: 20 within max size: 4 MB. Current attached size: %s MB",
		 * text);
		 */
		if (BusinessProxy.sSelf.MaxSizeData == 10) {
			BusinessProxy.sSelf.MaxSizeData = 4;
			MAX_IMAGE_ATTACH_SIZE = BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
		}
		return ("Max count: 5 within max size: "
				+ BusinessProxy.sSelf.MaxSizeData
				+ " MB. Current attached size: " + onMbKbReturnResult(totalSize));
	}

	private String getMaxSizeTextVideo() {
		double sizeInMB = totalSize / 1024.0 / 1024.0;
		String text = Double.toString(sizeInMB);
		int index = text.indexOf('.');
		if (-1 != index && index + 5 < text.length()) {
			text = text.substring(0, index + 5);
		}

		if (BusinessProxy.sSelf.MaxSizeData == 10) {
			BusinessProxy.sSelf.MaxSizeData = 4;
			MAX_IMAGE_ATTACH_SIZE = BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
		}
		return String.format("Max size: " + BusinessProxy.sSelf.MaxSizeData
				+ " MB. Current attached size: %s MB", text);
	}

	public String getPath(Uri uri) {

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();
		}
	}

	private static final byte POSITION_MULTI_SELECT_PICTURE = 5;
	private List<String> mImagesPath = new ArrayList<String>();
	String voiceUrl= null;//"http://74.208.228.56/Android/songs/TumHiHo.mp3" ;//view.getTag().toString();;
	public void onClick(final View v) {
		
		if(CustomMenu.isShowing()){
			return; 
		}
		
		switch (v.getId()) {
		
		case R.id.location_saveLocation:
			profileTask = new ProfileTask(RequestType.UPDATE_LOCATON);
			profileTask.execute(URL_UPDATE_PROFILE);
			break;
		case R.id.drop_down:
			showInterest();
			break;
		case R.id.back:
			onBackPressed() ;
			break;
		case R.id.save:
			if(isChanged()){
				profileTask = new ProfileTask(RequestType.UPDATE_PROFILE);
				profileTask.execute(URL_UPDATE_PROFILE);
			}else
				onBackPressed();
			break;
//		case R.id.base_cityxx:
//		case R.id.base_citydemolay:
//		case R.id.base_citydemolaylayout:
//		case R.id.base_city:
//			/*Intent location = new Intent(ProfileActivity.this,SettingUIActivity.class);
//			location.putExtra("location", "location");
////			location.putExtra("country", "");
////			location.putExtra("country", "");
////			location.putExtra("country", "");
////			location.putExtra("country", "");
//			startActivity(location);*/
//			showLocationScreen(true);
//		break;

		case R.id.languagelayout:
		case R.id.languagelay:
		case R.id.language_textview:
		case R.id.language:
			showLangaugeSelector();
			break;
			case R.id.secureBoxlayout:
			case R.id.secureBoxlay:
			case R.id.secureBox:
			case R.id.secureBoxxxx:
				//showAccSettings();
//				if(SettingData.sSelf.getEmail()==null || SettingData.sSelf.getEmail().trim().length()==0)
//				SettingData.sSelf.setEmail(profileBean.email) ;
//				Intent setting = new Intent(ProfileActivity.this,SettingUIActivity.class);
//				setting.putExtra("secure", "secure");
//				startActivity(setting);
				break;
		case R.id.signup_passwordBox:
			
			break;
		case R.id.base_country :
		case R.id.locationcountry:
		case R.id.location_country:
			Utilities.closeSoftKeyBoard(findViewById(R.id.location_address),
					this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.location_city), this);
			Utilities
					.closeSoftKeyBoard(findViewById(R.id.location_state), this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.location_pin), this);
			showCountrySelector();
			break;
		case R.id.location_state_textview:
			Utilities.closeSoftKeyBoard(findViewById(R.id.location_address),
					this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.location_city), this);
			Utilities
					.closeSoftKeyBoard(findViewById(R.id.location_state), this);
			Utilities.closeSoftKeyBoard(findViewById(R.id.location_pin), this);
			showStateSelector();
			break;
		
		case R.id.VoiceUpdatedemo:
		case R.id.voiceupdatelayout:
			//http://74.208.228.56/Android/songs/TumHiHo.mp3
		
		if(voiceUrl!=null && voiceUrl.length()>0){
			
			HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
			menuItems.put(0, "Preview");
			menuItems.put(1, "Re-Record");
			
			menuItems.put(2, "Clear");
			menuItems.put(3, "Cancel");
			openActionSheet(menuItems, new OnMenuItemSelectedListener() {
				@Override
				public void MenuItemSelectedEvent(Integer selection) {
					switch (selection) {
					
					case 0:
						((LinearLayout)findViewById(R.id.media_play_layout)).setVisibility(View.VISIBLE);
					
							flagForSoundPlay=true;
							
							layout = (LinearLayout) findViewById(R.id.media_play_layout);
							layout.setVisibility(View.VISIBLE);

							baradd = (SeekBar) layout
									.findViewById(R.id.mediavoicePlayingDialog_progressbar);
							resetProgress();
							if (((ImageView) layout.findViewById(R.id.media_play)) != null)
								((ImageView) layout.findViewById(R.id.media_play))
										.setOnClickListener(addClick);

							total_autio_time = ((TextView) layout
									.findViewById(R.id.audio_counter_max));
							played_autio_time = ((TextView) layout
									.findViewById(R.id.audio_counter_time));
							//total_autio_time.setText("00:00)");
							//played_autio_time.setText("(00:00 of ");
							total_autio_time.setText("");
							played_autio_time.setText("");
							handler.post(new Runnable() {
								// http://media-rtd4.onetouchstar.com/2012/09/24/05/1195675151493550080.mp3
								@Override
								public void run() {

									openPlayScreen(voiceUrl, false, layout);
								}
							});
							
							((TextView)layout.findViewById(R.id.media_stop_play)).setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if(layout!=null)
										layout.setVisibility(View.GONE);
									
										RTMediaPlayer.reset();
										RTMediaPlayer.clear();
								}
							});
						break;
					case 1:
						voiceUrl=null;
						if (voiceUrl != null
								&& voiceUrl.length() > 0) {
						((TextView) findViewById(R.id.VoiceUpdatedemo_txt))
						.setText("Profile message");
						((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.profile_voice);
						}else{
							
							((TextView) findViewById(R.id.VoiceUpdatedemo_txt)).setText("Speak about yourself");
							((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.recordedvoice);
							
							
						}
						showAudio(v);
						break;
					case 2:
					audioClear = true ;
						break;
					
					}

				}
			}, null);
		}else{
			showAudio(v);
		}
		break;
		
		case R.id.signup_thumb:
		
			if ((profileBean!=null && profileBean.pictureMediaList !=null && profileBean.pictureMediaList.size()>0)) {
				HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
				menuItems.put(0, "Preview");
				menuItems.put(1, "Clear All");
				menuItems.put(2, "Cancel");
				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {
						switch (selection) {
						case 0:
							if((profileBean!=null && profileBean.pictureMediaList !=null && profileBean.pictureMediaList.size()>0)){
								Feed.Entry entry = new Feed().new Entry();// feed.entry.get(Integer.parseInt(index));
								entry.media = new Vector<String>();
								
								for (int j = 0; j < profileBean.pictureMediaList.size(); j++) {
//								for (int j = profileBean.pictureMediaList.size()-1; j >=0; j--) {
									Content content = profileBean.pictureMediaList.get(j);
									entry.media.add(content.contentUrl);
									entry.media.add("image");
									entry.media.add(content.contentUrl);
									entry.media.add("local");
									entry.media
											.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
									entry.media.add("image");
									entry.media.add(content.contentUrl);
									entry.media.add("thumb");
								}
								DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
								Intent intentpics = new Intent(KainatProfileActivity.this,
										StreemMultiPhotoViewer.class);
								startActivityForResult(intentpics, Constants.ResultCode);
							}else{
							Intent intent = new Intent(KainatProfileActivity.this,
									MultiPhotoViewer.class);
							intent.putStringArrayListExtra("DATA",
									(ArrayList<String>) mImagesPath);
							startActivity(intent);
							}
							break;
						case 1:
							((ImageView) findViewById(R.id.signup_thumb))
									.setImageBitmap(null);
							((ImageView) findViewById(R.id.signup_thumb))
									.setBackgroundResource(R.drawable.border);
							((TextView) findViewById(R.id.photo_count))
									.setText("");
							findViewById(R.id.photo_count).setVisibility(
									View.GONE);
							mImagesPath = new ArrayList<String>();
							if(profileBean!=null){
								profileBean.pictureMediaList = new Vector<Content>() ;
								pictureClear=true ;
							}
							
							cameraImagePath =null;
							
							Bitmap icon = null ;
							if(profileBean.getGender().equalsIgnoreCase("male"))
							icon = BitmapFactory.decodeResource(context.getResources(),
					                R.drawable.male);
							if(profileBean.getGender().equalsIgnoreCase("Female"))
								icon = BitmapFactory.decodeResource(context.getResources(),
						                R.drawable.female);
							
							((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
							break;
						
						}

					}
				}, null);

			}
			else if ((mImagesPath != null && mImagesPath.size() > 0) )//|| (profileBean!=null && profileBean.pictureMediaList !=null && profileBean.pictureMediaList.size()>0)) 
			{
				HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
				menuItems.put(0, "Preview");
				menuItems.put(1, "Clear All");
				menuItems.put(2, "Camera");
				menuItems.put(3, "Library");
				menuItems.put(4, "Cancel");
				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {
						switch (selection) {
						case 0:
							if((profileBean!=null && profileBean.pictureMediaList !=null && profileBean.pictureMediaList.size()>0)){
								Feed.Entry entry = new Feed().new Entry();// feed.entry.get(Integer.parseInt(index));
								entry.media = new Vector<String>();
								
								for (int j = 0; j < profileBean.pictureMediaList.size(); j++) {
//								for (int j = profileBean.pictureMediaList.size()-1; j >=0; j--) {
									Content content = profileBean.pictureMediaList.get(j);
									entry.media.add(content.contentUrl);
									entry.media.add("image");
									entry.media.add(content.contentUrl);
									entry.media.add("local");
									entry.media
											.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
									entry.media.add("image");
									entry.media.add(content.contentUrl);
									entry.media.add("thumb");
								}
								DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
								Intent intentpics = new Intent(KainatProfileActivity.this,
										StreemMultiPhotoViewer.class);
								startActivityForResult(intentpics, Constants.ResultCode);
							}else{
							Intent intent = new Intent(KainatProfileActivity.this,
									MultiPhotoViewer.class);
							intent.putStringArrayListExtra("DATA",
									(ArrayList<String>) mImagesPath);
							startActivity(intent);
							}
							break;
						case 1:
							((ImageView) findViewById(R.id.signup_thumb))
									.setImageBitmap(null);
							((ImageView) findViewById(R.id.signup_thumb))
									.setBackgroundResource(R.drawable.border);
							((TextView) findViewById(R.id.photo_count))
									.setText("");
							findViewById(R.id.photo_count).setVisibility(
									View.GONE);
							mImagesPath = new ArrayList<String>();
							cameraImagePath =null;
							if(profileBean!=null){
								profileBean.pictureMediaList = new Vector<Content>() ;
								pictureClear=true ;
							}
							
							
							Bitmap icon = null ;
							if(profileBean.getGender().equalsIgnoreCase("male"))
							icon = BitmapFactory.decodeResource(context.getResources(),
					                R.drawable.male);
							if(profileBean.getGender().equalsIgnoreCase("Female"))
								icon = BitmapFactory.decodeResource(context.getResources(),
						                R.drawable.female);
							
							((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
							
							// findViewById(R.id.composeScreen_availableVoice).setVisibility(View.INVISIBLE);
							break;
						case 2:
							
								
							openCamera();
							break;
						case 3:
							if (mImagesPath != null && mImagesPath.size() > 0) {
								String[] data = new String[mImagesPath.size()];
								for (int i = 0; i < mImagesPath.size(); i++) {
									data[i] = mImagesPath.get(i);
								}
								DataModel.sSelf.storeObject("3333", data);
							}

							Intent i = new Intent(Intent.ACTION_PICK,
					                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							cameraImagePath = null;
							File file = new File(Environment.getExternalStorageDirectory(),
									getRandomNumber() + ".jpg");
							cameraImagePath = file.getPath();
							Uri outputFileUri = Uri.fromFile(file);
							picUri =outputFileUri;
							i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
							i.putExtra("urlpath", cameraImagePath);
							//i.putExtra("GALLARY", "GAL");
					        startActivityForResult(i, POSITION_GALLRY_PICTURE);
							
						/*	Intent	intent = new Intent(ProfileActivity.this,
									RocketalkMultipleSelectImage.class);
							intent.putExtra("MAX", 5);// getIntent().getIntExtra("MAX",
														// 5) ;
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
									| Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivityForResult(intent,
									POSITION_MULTI_SELECT_PICTURE);*/
							break;
						case 4:

							break;
						}

					}
				}, null);

			} else {
				HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
				menuItems.put(0, "Camera");
				menuItems.put(1, "Library");
				menuItems.put(2, "Cancel");

				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {
						switch (selection) {
						case 0:
							openCamera();
							break;
						case 1:
							if (mImagesPath != null && mImagesPath.size() > 0) {
								String[] data = new String[mImagesPath.size()];
								for (int i = 0; i < mImagesPath.size(); i++) {
									data[i] = mImagesPath.get(i);
								}
								DataModel.sSelf.storeObject("3333", data);
							}
							Intent i = new Intent(Intent.ACTION_PICK,
					                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							cameraImagePath = null;
							File file = new File(Environment.getExternalStorageDirectory(),
									getRandomNumber() + ".jpg");
							cameraImagePath = file.getPath();
							Uri outputFileUri = Uri.fromFile(file);
							picUri =outputFileUri;
							i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
							i.putExtra("urlpath", cameraImagePath);
							//i.putExtra("GALLARY", "GAL");
					        startActivityForResult(i, POSITION_GALLRY_PICTURE);
							/*Intent intent = new Intent(ProfileActivity.this,
									RocketalkMultipleSelectImage.class);
							intent.putExtra("MAX", 1);// getIntent().getIntExtra("MAX",
														// 5) ;
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
									| Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivityForResult(intent,
									POSITION_MULTI_SELECT_PICTURE);*/
							// findViewById(R.id.composeScreen_availableVoice).setVisibility(View.INVISIBLE);
							break;
						case 2:

							break;
						case 3:

							break;
						case 4:

							break;
						}

					}
				}, null);

			}
			
			// //
			break;
		case R.id.add_selected_items:
		case R.id.what_excites_you:
			SCREEN_VIEW = SCREEN_SELECT_INTEREST;
			setContentView(R.layout.select_interest);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					initInterest();
					addSelectedItem(true);
				}
			});
			
			break;
		case R.id.profile_link:

			OnClickListener clickListener1 = new OnClickListener() {
				public void onClick(View view) {
					switch (view.getId()) {
					case 0:// negative
						confirmationDialog.dismiss();
						break;
					case 1:// positive
						confirmationDialog.dismiss();

						break;
					}
				}
			};
			String[] bt = new String[] { "    Copy URL   " };
			bt = new String[] { "Open in browser", "Share on Facebook",
					"Share on Twitter" };
			showAlert(null, bt, "  Cancel  ", clickListener1);

			break;

		case COMPOSE_IMAGES_IDS_START:
		case COMPOSE_IMAGES_IDS_START + 1:
		case COMPOSE_IMAGES_IDS_START + 2:
		case COMPOSE_IMAGES_IDS_START + 3:
		case COMPOSE_IMAGES_IDS_START + 4:
		case COMPOSE_IMAGES_IDS_START + 5:
		case COMPOSE_IMAGES_IDS_START + 6:
		case COMPOSE_IMAGES_IDS_START + 7:
		case COMPOSE_IMAGES_IDS_START + 8:
		case COMPOSE_IMAGES_IDS_START + 9:
		case COMPOSE_IMAGES_IDS_START + 10:
		case COMPOSE_IMAGES_IDS_START + 11:
		case COMPOSE_IMAGES_IDS_START + 12:
		case COMPOSE_IMAGES_IDS_START + 13:
		case COMPOSE_IMAGES_IDS_START + 14:
		case COMPOSE_IMAGES_IDS_START + 15:
		case COMPOSE_IMAGES_IDS_START + 16:
		case COMPOSE_IMAGES_IDS_START + 17:
		case COMPOSE_IMAGES_IDS_START + 18:
		case COMPOSE_IMAGES_IDS_START + 19:
		case COMPOSE_IMAGES_IDS_START + 20:
			Intent intent = new Intent(KainatProfileActivity.this,
					MultiPhotoViewer.class);
			// intent.setAction(Intent.ACTION_VIEW);
			// intent.setDataAndType(Uri.fromFile(new
			// File(path)), "image/*");
			intent.putStringArrayListExtra("DATA",
					(ArrayList<String>) mImagesPath);
			startActivity(intent);
			break;
		case R.id.skip:
//			finish();
//			intent = new Intent(KainatProfileActivity.this, FriendOnRT.class);
//			if (!Utilities.getBoolean(KainatProfileActivity.this,
//					Constants.CAN_UPLOAD_CONTACT))
//				intent = new Intent(KainatProfileActivity.this, KainatHomeActivity.class);
//			startActivity(intent);
//			Utilities.setBoolean(KainatProfileActivity.this,
//					Constants.CAN_UPLOAD_CONTACT, false);
			break;
		}
	}

	Interest interest;
	Spinner spinner2;

	public void initInterest() {

//		BusinessProxy.sSelf.recordScreenStatistics(Constants.SCRTEEN_NAME_INTEREST, true, true);// Add
		myScreenName(Constants.SCRTEEN_NAME_INTEREST) ;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		
		if (selectedInterest!=null && selectedInterest.size() >= 1){
			findViewById(R.id.hint).setVisibility(View.INVISIBLE); 
		}
		
		findViewById(R.id.drop_down).setOnClickListener(this);
		findViewById(R.id.add_this_to_interest).setOnClickListener(this);
		
		listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<String>();
		for (int i = 0; i < Constants.values.length; ++i) {
			list.add(Constants.values[i]);
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				R.layout.histroy, list);

		spinner2.setAdapter(adapter);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (selectedInterest.size() >= 10) {
					return;
				}
				((TextView) findViewById(R.id.interest_enter))
						.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				interest = new Interest();

				interest.setName(Constants.values[position]);
				interest.setCatId(Constants.valuesId[position]);
//				interest.setIcon(Constants.icon[position]);
				interest.setIcon(Constants.iconHash.get(Constants.valuesId[position]));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((TextView) findViewById(R.id.selected_interest))
						.setText(Constants.values[position]);
				Drawable search_unsel = getResources().getDrawable(Constants.iconHash.get(Constants.valuesId[position])
						/*Constants.icon[position]*/);
				Rect r = null;
				Drawable[] d = ((TextView) findViewById(R.id.selected_interest))
						.getCompoundDrawables();
				r = null;
				if (d[0] != null)
					r = d[0].getBounds();
				if (r != null)
					search_unsel.setBounds(r);
				search_unsel.setBounds(5, 0, 30, 30);
				((TextView) findViewById(R.id.selected_interest))
						.setCompoundDrawables(search_unsel, null, null, null);

				((TextView) findViewById(R.id.selected_interest))
						.setVisibility(View.GONE);
				((TextView) findViewById(R.id.interest_enter))
						.setVisibility(View.VISIBLE);
				// ((TextView)findViewById(R.id.add_this_to_interest)).setVisibility(View.VISIBLE)
				// ;
				listview.setVisibility(View.GONE);
				interest = new Interest();

				interest.setName(Constants.values[position]);
				interest.setId(Long.parseLong(Constants.valuesId[position]));
				interest.setIcon(/*Constants.icon[position]*/Constants.iconHash.get(Constants.valuesId[position]));

				// for (int i = 0; i < values.length; i++) {
				// if (list.get(position).toLowerCase()
				// .equalsIgnoreCase(values[i].toLowerCase())) {
				// position = i;
				// break;
				// }
				// }
				// if (!vector.contains(position))
				// vector.add(position);
				// addSelectedItem();
			}
		});
		EditText inputEditText = (EditText) findViewById(R.id.interest_enter);
		inputEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				// list = new ArrayList<String>();
				// for (int i = 0; i < values.length; ++i) {
				// if (values[i].toLowerCase().startsWith(
				// s.toString().toLowerCase()))
				// list.add(values[i]);
				// }
				// final StableArrayAdapter adapter = new StableArrayAdapter(
				// context, R.layout.histroy, list);
				// listview.setAdapter(adapter);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (findViewById(R.id.add_this_to_interest).getVisibility() != View.VISIBLE)
					findViewById(R.id.add_this_to_interest).setVisibility(
							View.VISIBLE);
			}
		});
		findViewById(R.id.select_interest).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						listview.setVisibility(View.VISIBLE);
					}
				});
		((EditText)findViewById(R.id.interest_enter)).setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if(((EditText) findViewById(R.id.interest_enter))
						.getText().toString().trim().length()<=0){
					showSimpleAlert("", "Enter some interest");
					return false;
				}
				Interest intr = new Interest();
				if(interest==null){
					interest = new Interest();
					interest.setName(Constants.values[selectedInterestInd]);
					interest.setCatId(Constants.valuesId[selectedInterestInd]);
					interest.setIcon(/*Constants.icon[selectedInterestInd]*/Constants.iconHash.get(Constants.valuesId[selectedInterestInd]));
//					System.out.println("-----------null that is why init again-----");
				}
				if(intr!=null && interest!=null){
				intr.setCatId(interest.getCatId());
				intr.setIcon(interest.getIcon());

				intr.setName(((EditText) findViewById(R.id.interest_enter))
						.getText().toString());

				profileTask = new ProfileTask(RequestType.ADD_INTEREST);
				profileTask.intr = intr;
				profileTask.execute(URL_SAVE_INTEREST + URLEncoder.encode(""+intr.getCatId())
						+ "&interest_name=" + URLEncoder.encode(intr.getName()));

				if (selectedInterest.size() >= 1){
					findViewById(R.id.hint).setVisibility(View.INVISIBLE); 
				}
				
				if (selectedInterest.size() >= 10) {
//					Toast.makeText(ProfileActivity.this, "Limite over",
//							Toast.LENGTH_LONG).show();
					//showSimpleAlert("Message", "You have allowed to add maximum 10 interest");
					v.setVisibility(View.GONE);
					return false;
				}
				}
				return true;
			}
		});
		findViewById(R.id.add_this_to_interest).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						if(((EditText) findViewById(R.id.interest_enter))
								.getText().toString().trim().length()<=0){
							showSimpleAlert("", "Enter some interest");
							return;
						}
						Interest intr = new Interest();
						if(interest==null){
							interest = new Interest();
							interest.setName(Constants.values[selectedInterestInd]);
							interest.setCatId(Constants.valuesId[selectedInterestInd]);
							interest.setIcon(/*Constants.icon[selectedInterestInd]*/Constants.iconHash.get(Constants.valuesId[selectedInterestInd]));
//							System.out.println("-----------null that is why init again-----");
						}
						if(intr!=null && interest!=null){
						intr.setCatId(interest.getCatId());
						intr.setIcon(interest.getIcon());

						intr.setName(((EditText) findViewById(R.id.interest_enter))
								.getText().toString());

						profileTask = new ProfileTask(RequestType.ADD_INTEREST);
						profileTask.intr = intr;
						profileTask.execute(URL_SAVE_INTEREST + URLEncoder.encode(""+intr.getCatId())
								+ "&interest_name=" + URLEncoder.encode(intr.getName()));

						if (selectedInterest.size() >= 1){
							findViewById(R.id.hint).setVisibility(View.INVISIBLE); 
						}
						
						if (selectedInterest.size() >= 10) {
//							Toast.makeText(ProfileActivity.this, "Limite over",
//									Toast.LENGTH_LONG).show();
							//showSimpleAlert("Message", "You have allowed to add maximum 10 interest");
							v.setVisibility(View.GONE);
							return;
						}
						}
					}
				});

		// selectedInterest.add(interest);
	}

	public void onBackPressed() {
		
		if(CustomMenu.isShowing()){
			CustomMenu.hide() ;
			return; 
		}
		
		if (onBackPressedCheck())
			return;
		if (getIntent().getByteExtra("PAGE", (byte) 1) == 1) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		super.onBackPressed();
	}

	DrawableClickListener interestClickListener = new DrawableClickListener() {

		@Override
		public void onClick(DrawablePosition target, View view) {
			Interest interest = (Interest) view.getTag();
//			System.out.println("----interestClickListener close listener------"
//					+ interest.getName());
			switch (target) {
			case LEFT:
				break;

			case RIGHT:
				profileTask = new ProfileTask(RequestType.REMOVE_INTEREST);
				profileTask.intr = interest;
				profileTask.execute(URL_REMOVE_INTEREST + interest.getId());
				break;

			case BOTTOM:
				break;

			case TOP:

				break;

			default:
				break;
			}
		}

		@Override
		public void onClick(DrawablePosition target) {
		}
	};
	Display display;
	LinearLayout llay;// = new LinearLayout(this);
	Vector<Integer> vector = new Vector<Integer>();
	Vector<Interest> selectedInterest = new Vector<Interest>();

	public void addSelectedItem(boolean listenere) {
		try {
			if(!listenere){
				addSelectedItemPlain();
				return;
			}
			display = getWindowManager().getDefaultDisplay();
			LinearLayout add_selected_items = (LinearLayout) findViewById(R.id.add_selected_items);
			add_selected_items.setOnClickListener(this) ;
			add_selected_items.removeAllViews();
			add_selected_items.measure(display.getWidth(), display.getHeight());

			llay = new LinearLayout(this);
			llay.setOrientation(LinearLayout.HORIZONTAL);

			LinearLayout.LayoutParams llp = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			for (int i = 0; i < selectedInterest.size(); i++) {
				Interest inter = selectedInterest.get(i);
				
			//////
				LayoutInflater inflater = LayoutInflater
						.from(KainatProfileActivity.this);
				inflater = LayoutInflater.from(KainatProfileActivity.this);
				InterestView	v = (InterestView) inflater.inflate(
						R.layout.interest_testview_screen2, null, false);

//				v.setText("    " + Constants.values[position]);
				Drawable search_unsel = getResources().getDrawable(
						inter.getIcon());
				Rect r = null;
				Drawable[] d = v.getCompoundDrawables();
				r = null;
				if (d[0] != null)
					r = d[0].getBounds();
				if (r != null)
					search_unsel.setBounds(r);
				
				Drawable close = getResources().getDrawable(R.drawable.interest_remove);
				if (d[2] != null)
					r = d[2].getBounds();
				if (r != null)
					close.setBounds(r);
				v.setTag(inter);
				v.setCompoundDrawables(search_unsel, null, close, null);
				v.setText(" " + inter.getName() + "  ");
				if(listenere)
					v.setDrawableClickListener(interestClickListener);
				//////
				
				/*InterestView v = new InterestView(this);
				v.setTextColor(getResources().getColor(R.color.sub_heading));
				v.setText(" "+inter.getName()+" ");
				v.setGravity(Gravity.CENTER);
				v.setPadding(5, 5, 5, 5);//new
				v.setTag(inter);
				v.setTextSize(12) ;
				if(listenere)
				v.setDrawableClickListener(interestClickListener);
				
				int h = 55 ;
				if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
					
				} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_LARGE) {
					 h = 40 ;
				} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_NORMAL) {
					v.setText("      " + interest.getName() + "  ");
					 h = 40 ;
				} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_SMALL) {
					 h = 40 ;
					 v.setText("     " + interest.getName() + "  ");
				}
				
				LinearLayout.LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, h);
				layoutParams.setMargins(10, 5, 0, 0);//layoutParams.setMargins(10, 5, 0, 0);
				v.setLayoutParams(layoutParams);

				Drawable search_unsel = getResources().getDrawable(
						inter.getIcon());
				Drawable close = getResources().getDrawable(R.drawable.interest_remove);
				Rect r = null;
				Drawable[] d = v.getCompoundDrawables();
				r = null;
				if (d[0] != null)
					r = d[0].getBounds();
				if (r != null)
					search_unsel.setBounds(r);
				search_unsel.setBounds(5, 1, 39, 39);;//search_unsel.setBounds(0, 0, 45, 45);
				if( h==40)
				search_unsel.setBounds(5, 1, 24, 24);
				
				close.setBounds(0, 0, 40, 40);
				if( h==40)
					close.setBounds(0, 0, 23, 23);
				
				if(listenere)
				v.setCompoundDrawables(search_unsel, null, close, null);
				else
					v.setCompoundDrawables(search_unsel, null, null, null);
					
				v.setBackgroundResource(R.drawable.border_interest);
				v.requestLayout();
				if (canAdd(llay, v, null)) {
					llay.addView(v);
					System.out.println("----if-s[i]:" + inter.getName());
				} else {
					add_selected_items.addView(llay);
					llay = new LinearLayout(this);
					llay.setOrientation(LinearLayout.HORIZONTAL);
					System.out.println("----else-s[i]:" + inter.getName());
					llay.addView(v);
				}*/
				
				
				View view  = new View(KainatProfileActivity.this);
				LinearLayout.LayoutParams layoutParams =
						new LayoutParams(5, 5);
				view.setLayoutParams(layoutParams) ;
				if (canAdd(llay, v,add_selected_items)) {
					if(llay.getChildCount()>0){
						llay.addView(view);
						view  = new View(KainatProfileActivity.this);
						layoutParams =
								new LayoutParams(5, 5);
						view.setLayoutParams(layoutParams) ;
					}
					llay.addView(v);
					llay.addView(view);
//					System.out.println("----if-s[i]:" + interest.getName());
				} else {
					
					add_selected_items.addView(llay);
					llay = new LinearLayout(this);
					llay.setOrientation(LinearLayout.HORIZONTAL);
//					System.out.println("----else-s[i]:" + interest.getName());
					
//					layoutParams =
//							new LayoutParams(5, 5);
//					view.setLayoutParams(layoutParams) ;
					llay.addView(view);
					
					add_selected_items.addView(llay);
					llay = new LinearLayout(this);
					llay.setOrientation(LinearLayout.HORIZONTAL);
//					System.out.println("----else-s[i]:" + interest.getName());
					llay.addView(v);
				}
			}
			add_selected_items.addView(llay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("----selectedInterest.size()-----"
				+ selectedInterest.size());
		
		if (selectedInterest.size() >= 1) 
		if (findViewById(R.id.hint) != null)
			findViewById(R.id.hint).setVisibility(View.VISIBLE);
		else
			findViewById(R.id.hint).setVisibility(View.GONE);
		
		if (selectedInterest.size() >= 10) {
			if (findViewById(R.id.interest_enter) != null)
				findViewById(R.id.interest_enter).setVisibility(View.GONE);
			
			if (findViewById(R.id.hint) != null)
				findViewById(R.id.hint).setVisibility(View.VISIBLE);
//			showSimpleAlert("Message", "You have allowed to add maximum 10 interest");
			
			return;
		} else {
			if (findViewById(R.id.interest_enter) != null)
				findViewById(R.id.interest_enter).setVisibility(View.VISIBLE);
		}
	}

	public void addSelectedItemPlain() {
		try {
			display = getWindowManager().getDefaultDisplay();
			LinearLayout add_selected_items = (LinearLayout) findViewById(R.id.add_selected_items);
			add_selected_items.setOnClickListener(this) ;
			add_selected_items.removeAllViews();
			add_selected_items.measure(display.getWidth(), display.getHeight());
			// String s[] = new String[profileBean.interest.size()];
			// Integer icon[] = new Integer[profileBean.interest.size()];
			// for (int i = 0; i <profileBean.interest.size(); i++) {
			// s[i] = ()profileBean.interest.get(i);
			// icon[i] = this.icon[vector.get(i)];
			// }
			llay = new LinearLayout(this);
			llay.setOrientation(LinearLayout.HORIZONTAL);

			LinearLayout.LayoutParams llp = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			for (int i = 0; i < profileBean.interest.size(); i++) {
				Interest interest = (Interest) profileBean.interest.get(i);
//				TextView v = new TextView(this);
				
			//////
							LayoutInflater inflater = LayoutInflater
									.from(KainatProfileActivity.this);
							inflater = LayoutInflater.from(KainatProfileActivity.this);
							TextView	v = (TextView) inflater.inflate(
									R.layout.interest_testview_screen, null, false);

//							v.setText("    " + Constants.values[position]);
							Drawable search_unsel = getResources().getDrawable(
									interest.getIcon());
							Rect r = null;
							Drawable[] d = v.getCompoundDrawables();
							r = null;
							if (d[0] != null)
								r = d[0].getBounds();
							if (r != null)
								search_unsel.setBounds(r);
							v.setCompoundDrawables(search_unsel, null, null, null);
							v.setText(" " + interest.getName() + "  ");
							//////
				/*v.setTextColor(getResources().getColor(R.color.sub_heading));
				v.setText(" "+interest.getName()+"  ");
				v.setGravity(Gravity.CENTER);
				v.setPadding(5, 5, 5, 5);
				v.setTextSize(12) ;
//				MarginLayoutParams layoutParams = new MarginLayoutParams(source)
//				(15, 15, 15, 15);
				
				int h = 55 ;
				if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
					
				} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_LARGE) {
					 h = 40 ;
				} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_NORMAL) {
					v.setText("      " + interest.getName() + "  ");
					 h = 40 ;
				} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_SMALL) {
					 h = 40 ;
					 v.setText("     " + interest.getName() + "  ");
				}
				
				LinearLayout.LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, h);
//				layoutParams.setMargins(10, 10, 10, 10);
//				layoutParams.setPadding(5, 5, 5, 5);
				
				layoutParams.setMargins(10, 5, 0, 0);
				v.setLayoutParams(layoutParams);
				
//				MarginLayoutParams mr = new MarginLayoutParams(layoutParams);
//				mr.l=10;
			
				v.setLayoutParams(layoutParams);

				Drawable search_unsel = getResources().getDrawable(interest.getIcon());
				Rect r = null;
				Drawable[] d = v.getCompoundDrawables();
				r = null;
				if (d[0] != null)
					r = d[0].getBounds();
				if (r != null)
					search_unsel.setBounds(r);
				search_unsel.setBounds(5, 1, 39, 39);
				if( h==40)
					search_unsel.setBounds(5, 1, 24, 24);
				v.setCompoundDrawables(search_unsel, null, null, null);

				v.setBackgroundResource(R.drawable.border_interest);
				v.requestLayout();*/
//				if (canAdd(llay, v,add_selected_items)) {
//					llay.addView(v);
//					System.out.println("----if-s[i]:" + interest.getName());
//				} else {
//					add_selected_items.addView(llay);
//					llay = new LinearLayout(this);
//					llay.setOrientation(LinearLayout.HORIZONTAL);
//					System.out.println("----else-s[i]:" + interest.getName());
//					llay.addView(v);
//				}
							View view  = new View(KainatProfileActivity.this);
							LinearLayout.LayoutParams layoutParams =
									new LayoutParams(5, 5);
							view.setLayoutParams(layoutParams) ;
							if (canAdd(llay, v,add_selected_items)) {
								if(llay.getChildCount()>0){
									llay.addView(view);
									view  = new View(KainatProfileActivity.this);
									layoutParams =
											new LayoutParams(5, 5);
									view.setLayoutParams(layoutParams) ;
								}
								llay.addView(v);
								llay.addView(view);
//								System.out.println("----if-s[i]:" + interest.getName());
							} else {
								
								add_selected_items.addView(llay);
								llay = new LinearLayout(this);
								llay.setOrientation(LinearLayout.HORIZONTAL);
//								System.out.println("----else-s[i]:" + interest.getName());
								
//								layoutParams =
//										new LayoutParams(5, 5);
//								view.setLayoutParams(layoutParams) ;
								llay.addView(view);
								
								add_selected_items.addView(llay);
								llay = new LinearLayout(this);
								llay.setOrientation(LinearLayout.HORIZONTAL);
//								System.out.println("----else-s[i]:" + interest.getName());
								llay.addView(v);
							}
			}
			add_selected_items.addView(llay);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canAdd(LinearLayout linearLayout, TextView textView,LinearLayout add_selected_items) {
//		add_selected_items.measure(0, 0);
//		textView.setTypeface(null, Typeface.BOLD); 
		linearLayout.measure(0, 0);
		textView.measure(0, 0);
		int maxWidth = display.getWidth() - 35;//add_selected_items.getMeasuredWidth();//
		if(add_selected_items==null){
			float ps =Utilities.convertDpToPixel(20, this);
//			System.out.println("----convertDpToPixel 60-------->"+Utilities.convertDpToPixel(20, this));
			maxWidth = display.getWidth() - (int)ps*2;
		}
//		System.out.println("--------maxWifht:"+maxWidth+"  dip w :"+display.getWidth()+" : "+add_selected_items.getWidth());
		int totWid = 0;
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View v = linearLayout.getChildAt(i);
			v.measure(0, 0);
			totWid = totWid + v.getMeasuredWidth() + 10;
		}

		if (linearLayout.getChildCount() == 0)
			return true;
		if (maxWidth < (totWid + textView.getMeasuredWidth()+10))
			return false;
		return true;
	}

	private class StableArrayAdapter extends BaseAdapter {

		// HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {

			// for (int i = 0; i < objects.size(); ++i) {
			// mIdMap.put(objects.get(i), i);
			// }
		}

		@Override
		public long getItemId(int position) {
			String item = Constants.values[position];
			return 0;// Constants.valuesId[position];
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Constants.values.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return Constants.values[position];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				// TextView v = new TextView(ProfileActivity.this);

				LayoutInflater inflater = LayoutInflater
						.from(KainatProfileActivity.this);
				inflater = LayoutInflater.from(KainatProfileActivity.this);
				TextView v = (TextView) inflater.inflate(
						R.layout.interest_testview, null, false);

				v.setText("    " + Constants.values[position]);
				Drawable search_unsel = getResources().getDrawable(Constants.iconHash.get(Constants.valuesId[position])
						/*Constants.icon[position]*/);
				Rect r = null;
				Drawable[] d = v.getCompoundDrawables();
				r = null;
				if (d[0] != null)
					r = d[0].getBounds();
				if (r != null)
					search_unsel.setBounds(r);
				v.setCompoundDrawables(search_unsel, null, null, null);
				return v;
			} catch (Exception e) {
				e.printStackTrace();
				return new TextView(KainatProfileActivity.this);
			}

		}

	}

	public enum RequestType {
		ADD_INTEREST, REMOVE_INTEREST, GET_INTEREST, SEARCH_INTEREST, VIEW_PROFILE, UPDATE_PROFILE, UPDATE_LOCATON
	}

	public void parseContent(JSONArray jSONArray, int forContent) {
		try {
			if (forContent == 0)
				profileBean.pictureMediaList = new Vector<Content>();// 0
			if (forContent == 1)
				profileBean.audioMediaList = new Vector<Content>();// 1
			if (forContent == 2)
				profileBean.videoMediaList = new Vector<Content>();// 2
			for (int i = 0; i < jSONArray.length(); i++) {
				JSONObject nameObjectw = jSONArray.getJSONObject(i);
				Content content = new Content();
				if (nameObjectw.has("contentId")) {
					content.contentId = nameObjectw.getString("contentId");
				}
				if (nameObjectw.has("contentType")) {
					content.contentId = nameObjectw.getString("contentType");
				}
				if (nameObjectw.has("contentUrl")) {
					content.contentUrl = nameObjectw.getString("contentUrl");
				}
				if (nameObjectw.has("thumbUrl")) {
					content.thumbUrl = nameObjectw.getString("thumbUrl");
				}
				if (content.contentId != null
						&& content.contentId.trim().equalsIgnoreCase("image")) {
					profileBean.pictureMediaList.add(content);
				}
				if (content.contentId != null
						&& content.contentId.trim().equalsIgnoreCase("audio")) {
					profileBean.audioMediaList.add(content);
				}
				if (content.contentId != null
						&& content.contentId.trim().equalsIgnoreCase("video")) {
					profileBean.videoMediaList.add(content);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	RTDialogNotification dialogNotification ;
	public static ProfileBean profileBean;
	RTDialog rTDialog;
	
	private class ProfileTask extends AsyncTask<String, Void, String> {
		RequestType requestType;
		Drawable drawable;
		Interest intr;

		public ProfileTask(RequestType requestType) {
			this.requestType = requestType;
		}

		@Override
		protected void onPreExecute() {
//			rTDialog = new RTDialog(ProfileActivity.this, null,
//					getString(R.string.updating));
			
			if(requestType==RequestType.VIEW_PROFILE)
				rTDialog = new RTDialog(KainatProfileActivity.this, null,
						getString(R.string.feateching_profile));//feateching_profile
			else
				rTDialog = new RTDialog(KainatProfileActivity.this, null,
						getString(R.string.updating));
			
			rTDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			String url = urls[0];
			try {
				
				Thread.sleep(1000) ;
//				System.out.println("----url------"+url);
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				String param = "";
				String img_file_id = null;
				
//				if(profileBean!=null && profileBean.pictureMediaList!=null)
//				for (int j = profileBean.pictureMediaList.size()-1; j >=0; j--) {
//					Content content = profileBean.pictureMediaList.get(j);
//					if (img_file_id == null)
//						img_file_id = "file_ids=" + content.contentId + ",";
//					else
//						img_file_id = img_file_id + content.contentId+ ",";
//				}
//				if(profileBean!=null && profileBean.audioMediaList!=null)
//					for (int j = profileBean.audioMediaList.size()-1; j >=0; j--) {
//						Content content = profileBean.audioMediaList.get(j);
//						if (img_file_id == null)
//							img_file_id = "file_ids=" + content.contentId + ",";
//						else
//							img_file_id = img_file_id + content.contentId+ ",";
//					}
//				if(profileBean!=null && profileBean.videoMediaList!=null)
//					for (int j = profileBean.videoMediaList.size()-1; j >=0; j--) {
//						Content content = profileBean.videoMediaList.get(j);
//						if (img_file_id == null)
//							img_file_id = "file_ids=" + content.contentId + ",";
//						else
//							img_file_id = img_file_id + content.contentId+ ",";
//					}
//				String aud_file_id = null;
				if (RequestType.UPDATE_PROFILE == requestType || RequestType.UPDATE_LOCATON== requestType) {
					// mImagesPath
					boolean saving = false ;
					if (RequestType.UPDATE_PROFILE == requestType){
					Vector<String> fileIdVec = new Vector<String>();
					for (int i = 0; i < mImagesPath.size(); i++) {
						pictureClear=false ;
						
						CompressImage compressImage = new CompressImage(KainatProfileActivity.this) ;
						String path = compressImage.compressImage(mImagesPath
								.get(i)) ;
						
						String fileId = Utilities.createMediaID(path,Constants.ID_FOR_UPDATE_PROFILE);
						if(fileId.indexOf("}")!=-1){
							return fileId ;
						}
						// fileIdVec.add(fileId) ;
						if (fileId != null) {
							if (img_file_id == null)
								img_file_id = "file_ids=" + fileId + ",";
							else
								img_file_id = img_file_id + fileId+ ",";
						}
//						System.out.println("-----------image file id  :"
//								+ fileId);
						saving =true ;
					}
					if (mVoicePath != null) {
						audioClear = false ;
						String fileId = Utilities.createMediaID(mVoicePath,Constants.ID_FOR_UPDATE_PROFILE);
						// fileIdVec.add(fileId) ;
						if (fileId != null) {
							if (img_file_id == null)
								img_file_id = "file_ids=" + fileId + ",";
							else
								img_file_id = img_file_id + fileId+ ",";
//							System.out.println("-----------voice file id  :"
//									+ fileId);
						}
						saving =true ;
					}
					String s = URLEncoder.encode(dob.getText().toString()) ;
					if(s!=null  && !s.equals(URLEncoder.encode(profileBean.getBirthday()))){
					param = param + "dob="+s + "&";
					saving =true ;
					}
					
					s = URLEncoder.encode(((TextView) findViewById(R.id.language))
								.getText().toString()) ;
					if(s!=null  && !s.equals(URLEncoder.encode(profileBean.getLanguage())))
						param = param + "language="+s+ "&";
						saving =true ;
					}
					if (img_file_id != null){
						param = img_file_id + "&";
					}
					//audio=clear&picture=clear
					// for location
					String s = URLEncoder.encode(((TextView) findViewById(R.id.base_city))
							.getText().toString()) ;
					if(s!=null  && !s.equals(URLEncoder.encode(profileBean.getCity()))){
					param = param+"city="
							+ s+ "&";
					saving =true ;
					}
					s = URLEncoder.encode(((TextView) findViewById(R.id.base_country))
							.getText().toString()) ;
					if(s!=null && !s.equals(URLEncoder.encode(profileBean.getCountry()))){
					param = param+"country="
							+ s + "&";
					saving =true ;
					}
//					if (RequestType.UPDATE_LOCATON == requestType)
//					{
//					
//					
//					
//					String s = URLEncoder.encode(((TextView) findViewById(R.id.location_city))
//							.getText().toString()) ;
//					if(s!=null  && !s.equalsIgnoreCase(URLEncoder.encode(profileBean.getCity()))){
//					param = param+"city="
//							+ s+ "&";
//					saving =true ;
//					}
//					
//					s = URLEncoder.encode(((TextView) findViewById(R.id.location_country))
//							.getText().toString()) ;
//					if(s!=null && !s.equalsIgnoreCase(URLEncoder.encode(profileBean.getCountry()))){
//					param = param+"country="
//							+ s + "&";
//					saving =true ;
//					}
//					}
					
					if(audioClear)
						param = param+"audio="
								+ "clear"+ "&";
					if(pictureClear)
						param = param+"picture="
								+ "clear"+ "&";
					audioClear=false;
					pictureClear=false;
					
					System.out.println("-----param:" + param);
					System.out.println("-----param url:" + url+param);
					String response = "" ;
					if(param!=null && param.trim().length()>0)
					response = AdConnectionManager.getInstance()
							.uploadByteData2(url+param, null, headerParam, null);
					else{
						response = null;;//ResponseError._Nothing_to_save ;
					}
//					System.out.println("-----response:" + response);

					return response;

				} 
//				else if (RequestType.ADD_INTEREST == requestType) {
//					String response = AdConnectionManager.getInstance()
//							.uploadByteData2(url, null, headerParam, null);
//					return response;
//				} else if (RequestType.REMOVE_INTEREST == requestType) {
//					String response = AdConnectionManager.getInstance()
//							.uploadByteData2(url, null, headerParam, null);
//					return response;
//				} 
				else if (RequestType.VIEW_PROFILE == requestType) {
					
					profileBean = new ProfileBean();
					String response = AdConnectionManager.getInstance().uploadByteData2(url, null, headerParam, null);
					JSONObject jsonObjectT = new JSONObject(response);
					if (jsonObjectT.has("status")
							&& jsonObjectT.getString("status")
									.equalsIgnoreCase("1")) {

						JSONObject jsonObject = new JSONObject(
								jsonObjectT.getString("UserProfileFeed"));

						if (jsonObject.has("userId"))
							profileBean.userId = jsonObject.getString("userId");
						if (jsonObject.has("username"))
							profileBean.username = jsonObject
									.getString("username");
						if (jsonObject.has("firstName"))
							profileBean.firstName = jsonObject
									.getString("firstName");
						if (jsonObject.has("lastName"))
							profileBean.lastName = jsonObject
									.getString("lastName");
						if (jsonObject.has("address"))
							profileBean.address = jsonObject
									.getString("address");
						if (jsonObject.has("city"))
							profileBean.city = jsonObject.getString("city");
						if (jsonObject.has("state")){
							profileBean.state = jsonObject.getString("state");
							SettingData.sSelf.setState(profileBean.state) ;
						}
						if (jsonObject.has("zip"))
							profileBean.zip = jsonObject.getString("zip");
						if (jsonObject.has("country")){
							profileBean.country = jsonObject
									.getString("country");
							SettingData.sSelf.setCountry(profileBean.country) ;
							
						}
						if (jsonObject.has("gender"))
							profileBean.gender = jsonObject.getString("gender");
						if (jsonObject.has("timeLastUpdate"))
							profileBean.timeLastUpdate = jsonObject
									.getString("timeLastUpdate");
						if (jsonObject.has("birthday"))
							profileBean.birthday = jsonObject
									.getString("birthday");
						if (jsonObject.has("numberOfBuddies"))
							profileBean.numberOfBuddies = jsonObject
									.getString("numberOfBuddies");
						if (jsonObject.has("numberOfPosts"))
							profileBean.numberOfPosts = jsonObject
									.getString("numberOfPosts");
						if (jsonObject.has("numberOfMediaFollowers"))
							profileBean.numberOfMediaFollowers = jsonObject
									.getString("numberOfMediaFollowers");
						if (jsonObject.has("numberOfMediaFollowing"))
							profileBean.numberOfMediaFollowing = jsonObject
									.getString("numberOfMediaFollowing");
						if (jsonObject.has("mobileNumber")){
							profileBean.mobileNumber = jsonObject
									.getString("mobileNumber");
							SettingData.sSelf.setMobile(profileBean.mobileNumber) ;
							SettingData.sSelf.setNumber(profileBean.mobileNumber) ;
							
						}
						if (jsonObject.has("isMobileNumberVerified")){
							profileBean.isMobileNumberVerified = Boolean.parseBoolean(jsonObject
									.getString("isMobileNumberVerified"));
							SettingData.sSelf.setMobileNumberVerified(profileBean.isMobileNumberVerified) ;
						}
						
						
						
						
						
						if (jsonObject.has("email")){
							profileBean.email = jsonObject.getString("email");
							SettingData.sSelf.setEmail(profileBean.email) ;
						}
						if (jsonObject.has("isEmailVerified")){
							profileBean.isEmailVerified = Boolean.parseBoolean(jsonObject
									.getString("isEmailVerified"));
							SettingData.sSelf.setEmailVerified(profileBean.isEmailVerified) ;
						}
						if (jsonObject.has("language"))
							profileBean.language = jsonObject
									.getString("language");
						if (jsonObject.has("isSecurityQuestionSet")){
							profileBean.isSecurityQuestionSet = Boolean.parseBoolean(jsonObject
									.getString("isSecurityQuestionSet"));
							SettingData.sSelf.setSecurityQuestionSet(profileBean.isSecurityQuestionSet) ;
						}

						if (jsonObject.has("pictureMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("pictureMediaList");
							parseContent(jSONArray, 0);
						}
						if (jsonObject.has("audioMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("audioMediaList");
							parseContent(jSONArray, 1);
						}
						if (jsonObject.has("videoMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("videoMediaList");
							parseContent(jSONArray, 2);
						}

						if (jsonObject.has("userInterest")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("userInterest");
							profileBean.interest = new Vector<Interest>();

							for (int i = 0; i < jSONArray.length(); i++) {
								JSONObject nameObjectw = jSONArray
										.getJSONObject(i);
								Interest interet = new Interest();
								if (nameObjectw.has("interestId")) {
									interet.setId(Long.parseLong(nameObjectw
											.getString("interestId")));
								}
								if (nameObjectw.has("interestName")) {
									interet.setName(nameObjectw
											.getString("interestName"));
								}
								if (nameObjectw.has("catId")) {
									interet.setCatId(nameObjectw
											.getString("catId"));
								}
								for (int j = 0; j < Constants.valuesId.length; j++) {
									if (interet.getCatId().equalsIgnoreCase(Constants.valuesId[j])) {
										interet.setIcon(Constants.iconHash.get(Constants.valuesId[j])
												/*Constants.icon[j]*/);
										break;
									}
								}
								profileBean.interest.add(interet);
							}
							selectedInterest = profileBean.interest ;
						}
					}
					return response;
				}

			} catch (Exception e) {
				
				e.printStackTrace();
				someThingWentWrong(getResources().getString(R.string.something_went_wrong));
				
				return null;
			} catch (OutOfMemoryError e) {
				someThingWentWrong(getResources().getString(R.string.something_went_wrong));
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
//			System.out.println("-----result:" + result);
			if (result != null) {
				if (RequestType.ADD_INTEREST == requestType) {
					try {
						JSONObject jsonObjectT = new JSONObject(result);
						if (jsonObjectT.getString("status").equalsIgnoreCase(
								"1") ) {
							
							intr.setId(Long.parseLong(jsonObjectT
									.getString("interestId")));
							selectedInterest.add(intr);
							addSelectedItem(true);
							((EditText) findViewById(R.id.interest_enter))
									.setText("");
							findViewById(R.id.add_this_to_interest)
									.setVisibility(View.GONE);
							
							Utilities.setBoolean(KainatProfileActivity.this, Constants.PROFILE_UPDATED, true) ;
							
						} else if (jsonObjectT.getString("status")
								.equalsIgnoreCase("2")) {
							showSimpleAlert("",
									jsonObjectT.getString("desc"));
						}
					} catch (Exception e) {

					}

				} else if (RequestType.REMOVE_INTEREST == requestType) {
					try {
						JSONObject jsonObjectT = new JSONObject(result);
						if (jsonObjectT.getString("status").equalsIgnoreCase(
								"1")) {

							for (int i = 0; i < selectedInterest.size(); i++) {
								Interest is = selectedInterest.get(i);
								if (is.getId() == intr.getId()) {
									selectedInterest.remove(is);
									break;
								}
							}

							addSelectedItem(true);
							((EditText) findViewById(R.id.interest_enter))
									.setText("");
							findViewById(R.id.add_this_to_interest)
									.setVisibility(View.GONE);
							findViewById(R.id.add_selected_items_layout).invalidate() ;
							Utilities.setBoolean(KainatProfileActivity.this, Constants.PROFILE_UPDATED, true) ;
							
						} else if (jsonObjectT.getString("status")
								.equalsIgnoreCase("2")) {
							showSimpleAlert("",
									jsonObjectT.getString("desc"));
						}
					} catch (Exception e) {
					}
				}
				else if (RequestType.UPDATE_LOCATON == requestType) {
					try {
						JSONObject jsonObjectT = new JSONObject(result);
						if (jsonObjectT.getString("status").equalsIgnoreCase(
								"1")) {
							
							profileBean.setCity(((TextView) findViewById(R.id.location_city))
									.getText().toString());
							profileBean.setCountry(((TextView) findViewById(R.id.location_country))
									.getText().toString());
							onBackPressed() ;
							
							dialogNotification = new RTDialogNotification(KainatProfileActivity.this, null,
									"Location changes updated",KainatProfileActivity.this);
//							dialogNotification.changeMessgae("Location changes updated") ;
							dialogNotification.show();
							
//							showSimpleAlert("","Location changes updated");
							
						} else if (jsonObjectT.getString("status")
								.equalsIgnoreCase("2")) {
							showSimpleAlert("",
									jsonObjectT.getString("desc"));
						}
					} catch (Exception e) {
						e.printStackTrace() ;
					}
				}else if (RequestType.VIEW_PROFILE == requestType) {
					addSelectedItem(false);
					if (profileBean.pictureMediaList != null
							&& profileBean.pictureMediaList.size() > 0) {
						Content content = profileBean.pictureMediaList.get(0);//profileBean.pictureMediaList.size()-1
						loadProfilePic = new LoadProfilePic();
						loadProfilePic.execute(content.thumbUrl);
						if (profileBean.pictureMediaList.size() > 1) {
						((TextView) findViewById(R.id.photo_count)).setText(""
								+ profileBean.pictureMediaList.size());
						((TextView) findViewById(R.id.photo_count))
								.setVisibility(View.VISIBLE);
						}
					}
					if (profileBean.audioMediaList != null
							&& profileBean.audioMediaList.size() > 0) {
						Content content = profileBean.audioMediaList.get(0);
						voiceUrl = content.contentUrl ;
//						findViewById(R.id.profile_voice).setVisibility(
//								View.VISIBLE);
//						findViewById(R.id.profile_voice).setTag(
//								content.contentUrl);
					}

					((TextView) findViewById(R.id.
							base_country)).setText(profileBean.getCountry());
					
					((TextView) findViewById(R.id.display_name))
							.setText(profileBean.getDisplayName());
					((TextView) findViewById(R.id.gender)).setText(profileBean
							.getGender() + ", ");
					if(!profileBean.getAge().equalsIgnoreCase("00/00/0000") && Utilities.calCulateAge(profileBean
							.getAge())>0)
					((TextView) findViewById(R.id.age)).setText(""+Utilities.calCulateAge(profileBean
							.getAge())+", ");
					else{
						((TextView) findViewById(R.id.age))
						.setText("16+,");
					}
					((TextView) findViewById(R.id.country)).setText(profileBean.getCountyCity());

					((TextView) findViewById(R.id.dob)).setText(profileBean
							.getBirthday());
					((EditText) findViewById(R.id.base_city))
							.setText(profileBean.getBaseCity());
					((TextView) findViewById(R.id.language))
							.setText(profileBean.getLanguage());
					
					if (profileBean.getLanguage()==null || profileBean.getLanguage().trim().length() <=0)
					((TextView) findViewById(R.id.language_textview)).setText("Language that you prefer");
					else
					((TextView) findViewById(R.id.language_textview))
					.setText("Language");
					
					if (profileBean.getBaseCity()==null || profileBean.getBaseCity().trim().length() <=0)
						((TextView) findViewById(R.id.base_cityxx)).setText("Enter your city");
						else
						((TextView) findViewById(R.id.base_cityxx))
						.setText("City");
					
					((TextView) findViewById(R.id.profile_link))
							.setText("http://myrt.me/"
									+ profileBean.getUsername());
					
					Bitmap icon = null ;
					if(profileBean.getGender().equalsIgnoreCase("male"))
					icon = BitmapFactory.decodeResource(context.getResources(),
			                R.drawable.male);
					if(profileBean.getGender().equalsIgnoreCase("Female"))
						icon = BitmapFactory.decodeResource(context.getResources(),
				                R.drawable.female);
					
					((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
					
					
					
//					((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
					
					 
					ImageView securityView = (ImageView) findViewById(R.id.tick);
					//System.out.println(SettingData.sSelf.isSecurityQuestionSet() +"email=="+SettingData.sSelf.isEmailVerified()+"mobil=="+SettingData.sSelf.isMobileNumberVerified());
					if(SettingData.sSelf.isSecurityQuestionSet() && SettingData.sSelf.isEmailVerified() && (profileBean.mobileNumber!=null && profileBean.mobileNumber.trim().length()>0))//SettingData.sSelf.isMobileNumberVerified())
					securityView.setVisibility(View.VISIBLE);
						else
					securityView.setVisibility(View.GONE);
					if (voiceUrl != null
							&& voiceUrl.length() > 0) {
					((TextView) findViewById(R.id.VoiceUpdatedemo_txt))
					.setText("Profile message");
					((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.profile_voice);
					}else{
						((TextView) findViewById(R.id.VoiceUpdatedemo_txt)).setText("Speak about yourself");
						((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.recordedvoice);
					}
					//Profile message

				}
				else if (RequestType.UPDATE_PROFILE == requestType) {
					try {
						JSONObject jsonObjectT = new JSONObject(result);
						if (jsonObjectT.getString("status").equalsIgnoreCase(
								"1") ||  jsonObjectT.getString("status").equalsIgnoreCase(
										"error")) {

							closeWithoutAsk = true ;
							Utilities.setBoolean(KainatProfileActivity.this, Constants.PROFILE_UPDATED, true) ;
							DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									onBackPressed() ;
								}
							};
							//{"message":"Media creation is not available for suspended or mobile unverified users","status":"error"}
//							showSimpleAlert("",
//									jsonObjectT.getString("desc"),listener);
							String msg = null ;
							if(jsonObjectT.has("desc"))
								msg = jsonObjectT.getString("desc") ;
							if(jsonObjectT.has("message"))
								msg = jsonObjectT.getString("message") ;
							dialogNotification = new RTDialogNotification(KainatProfileActivity.this, null,
									msg,KainatProfileActivity.this);
//							dialogNotification.changeMessgae(jsonObjectT.getString("desc")) ;
							dialogNotification.show();
							
						} else if (jsonObjectT.getString("status")
								.equalsIgnoreCase("2")) {
							
							if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
								closeWithoutAsk = true ;
								onBackPressed() ;
									showSimpleAlert("",
											jsonObjectT.getString("desc"));
						}
					} catch (Exception e) {
						e.printStackTrace() ;
					}
				}
				view.setVisibility(View.VISIBLE);
			}
			else{
//				closeWithoutAsk = true ;
				onBackPressed() ;
			}
			if (rTDialog != null && rTDialog.isShowing()) {
				rTDialog.closeLoading();
				rTDialog.dismiss();

				rTDialog = null;
			}
			
		}
		
	}
	public void cancelAlertDilog() {
		
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)){
//			Intent intent = new Intent(KainatProfileActivity.this, KainatHomeActivity.class);
//			if(Utilities.getBoolean(KainatProfileActivity.this, Constants.CAN_UPLOAD_CONTACT) )
//				 intent = new Intent(KainatProfileActivity.this,FriendOnRT.class);
//	//		pushNewScreen(intent, AddSecurityQuestionActivity.class, false);
		}
		else if(profileTask!=null && profileTask.requestType== RequestType.UPDATE_PROFILE)
			onBackPressed();
	}
	LoadProfilePic loadProfilePic;

	private class LoadProfilePic extends AsyncTask<String, Void, String> {

		Drawable drawable;

		@Override
		protected void onPreExecute() {
			Bitmap icon = null ;
			if(profileBean.getGender().equalsIgnoreCase("male"))
			icon = BitmapFactory.decodeResource(context.getResources(),
	                R.drawable.male);
			if(profileBean.getGender().equalsIgnoreCase("Female"))
				icon = BitmapFactory.decodeResource(context.getResources(),
		                R.drawable.female);
			
			((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
			
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {

			try {
//				System.out.println("----image url ----:"+urls[0]);
				getCacheDirectoryImage();
				
				String filename = String.valueOf(urls[0].hashCode());
				String extension = "jpg";
				filename = filename + "." + extension;
				File f = new File(cacheDirImage, filename);
				drawable = Drawable.createFromPath(f.getAbsolutePath());
				if(drawable==null){
				InputStream is = (InputStream) new URL(urls[0]).getContent();
				byte data[] = Utilities.inputStreamToByteArray(is);
				if(data.length>0)
				{
					filename = String.valueOf(urls[0].hashCode());
					extension = "jpg";
					filename = filename + "." + extension;
					f = new File(cacheDirImage, filename);
					writeFile(data, f, urls[0].hashCode());
					drawable = Drawable.createFromPath(f.getAbsolutePath());
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
				makeToast(e.toString());
				return null;
			} catch (OutOfMemoryError e) {
				makeToast(e.toString());
				return null;
			}
			// drawable=loadImage(User.getInstance().profilePic);
			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			//
			try{
			ImageView i = ((ImageView) findViewById(R.id.signup_thumb));
			if(drawable!=null){
				Bitmap bitmap=drawableToBitmap(drawable);
				i.setImageBitmap(bitmap);
				}
			//if(drawable!=null)
			//i.setImageDrawable(drawable);
			else
			{
				Bitmap icon = null ;
				if(profileBean.getGender().equalsIgnoreCase("male"))
				icon = BitmapFactory.decodeResource(context.getResources(),
		                R.drawable.male);
				if(profileBean.getGender().equalsIgnoreCase("Female"))
					icon = BitmapFactory.decodeResource(context.getResources(),
			                R.drawable.female);
				
				((ImageView)findViewById(R.id.signup_thumb)).setImageBitmap(icon) ;
			}
			Utilities.startAnimition(KainatProfileActivity.this, i, R.anim.bounce);
			}catch(Exception e){
				
			}
		}
	}

	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	private void writeFile(byte[] data, File f, int id) {

		FileOutputStream out = null;
		if (f != null && !f.exists() && data != null) {
			try {

				f.createNewFile();
				out = new FileOutputStream(f);
				out.write(data);

			} catch (Exception e) {
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}

	}

	final Runnable RunnableThread = new Runnable() {
		public void run() {
			// running = true;
			while (!running && progress < 180) {
				// pw_two.rimColor=getResources().getColor(R.color.blue);
				// pw_two.circleColor=getResources().getColor(R.color.blue);
				pw_two.incrementProgress();
				// if(seconds<60){
				progress++;
				// }
				try {
					Thread.sleep((338*2));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			running = false;
		}
	};
	Thread progressThread;
	int seconds;
	int progress = 0;
	long totaltime;

	public void voiceRecording(final View promptsView) {
		try {
			if (mVoiceMedia == null)
				mVoiceMedia = new VoiceMedia(KainatProfileActivity.this,
						KainatProfileActivity.this);
			mVoiceMedia.startRecording(60*2);
			flagVoice = false;
			counterValueTap = 1;
			seconds = 60*2;
			buttonRecord.setVisibility(View.GONE);
			buttonstop.setVisibility(View.VISIBLE);
			buttonPlay.setVisibility(View.GONE);
			playLayout.setVisibility(View.GONE);
			stopLayout.setVisibility(View.VISIBLE);
			recordingLayout.setVisibility(View.GONE);
			buttonRerecord.setVisibility(View.GONE);
			rerecordLayout.setVisibility(View.GONE);
			sendLayout.setVisibility(View.GONE);
			buttonsend.setVisibility(View.GONE);
			((ImageView) promptsView.findViewById(R.id.sound_icon))
					.setImageResource(R.drawable.microphonerotate);
			// final
			// voiceRecordingStarted(mVoicePath);
			running = false;
			if (!running) {
				progress = 0;
				pw_two.resetCount();
				progressThread = new Thread(RunnableThread);
				progressThread.start();
			}
			TimerCountDown = new Timer("akm", true);
			TimerCountDown.schedule(new TimerTask() {

				@Override
				public void run() {
					textCounter.post(new Runnable() {

						public void run() {
							if (flagVoice)
								return;
							seconds--;
							long value = seconds * 1000;
							// System.out.println("seconds==="+seconds);
							totaltime = value;
							if (seconds > -1) {
								if((120-seconds)<60)
								textCounter.setText(""+(120-seconds)+" sec") ;
								else if ((120-seconds)==120)
									textCounter.setText("2 min") ;
								else
									textCounter.setText("1:"+((120-seconds)-60)+" min") ;
										
//										Utilities
//										.converMiliSecondForAudioStatus(value)
//										+ " sec");

								textCounter.invalidate();
							} else {
								try {
									if (TimerCountDown != null) {
										TimerCountDown.cancel();
										TimerCountDown = null;
									}
									running = true;
									buttonRecord.setVisibility(View.GONE);
									buttonstop.setVisibility(View.GONE);
									buttonPlay.setVisibility(View.VISIBLE);
									playLayout.setVisibility(View.VISIBLE);
									stopLayout.setVisibility(View.GONE);
									buttonRerecord.setVisibility(View.VISIBLE);
									rerecordLayout.setVisibility(View.VISIBLE);
									sendLayout.setVisibility(View.VISIBLE);
									buttonsend.setVisibility(View.VISIBLE);

									((ImageView) promptsView
											.findViewById(R.id.sound_icon))
											.setImageResource(R.drawable.microphoneland);
									flagVoice = true;
									if (mVoiceMedia != null)
										mVoiceMedia.stopRec();
									if (mVoicePath != null) {
										toBeAttached |= Payload.PAYLOAD_BITMAP_VOICE;
									}
								} catch (Exception e) {
									// TODO: handle exception
									if (TimerCountDown != null) {
										TimerCountDown.cancel();
										TimerCountDown = null;
									}
									running = true;
									if (progressThread != null) {
										// progressThread.stop();
										progressThread = null;
									}
									((ImageView) promptsView
											.findViewById(R.id.sound_icon))
											.setImageResource(R.drawable.microphoneland);
									buttonRecord.setVisibility(View.VISIBLE);
									buttonstop.setVisibility(View.GONE);
									buttonPlay.setVisibility(View.GONE);
									playLayout.setVisibility(View.GONE);
									stopLayout.setVisibility(View.GONE);
									recordingLayout.setVisibility(View.GONE);
									buttonRerecord.setVisibility(View.GONE);
									sendLayout.setVisibility(View.GONE);
									buttonsend.setVisibility(View.GONE);
									flagVoice = true;
									mVoiceMedia = null;
									mVoicePath = null;
								}
							}
						}
					});

				}
			}, 1000, 1000);

			// System.out.println("start recording");
		} catch (Exception e) {
			if (TimerCountDown != null) {
				TimerCountDown.cancel();
				TimerCountDown = null;
			}
			((ImageView) promptsView.findViewById(R.id.sound_icon))
					.setImageResource(R.drawable.microphoneland);
			buttonRecord.setVisibility(View.VISIBLE);
			buttonstop.setVisibility(View.GONE);
			buttonPlay.setVisibility(View.GONE);
			buttonRerecord.setVisibility(View.GONE);
			playLayout.setVisibility(View.GONE);
			stopLayout.setVisibility(View.GONE);
			buttonRerecord.setVisibility(View.GONE);
			recordingLayout.setVisibility(View.GONE);
			sendLayout.setVisibility(View.GONE);
			buttonsend.setVisibility(View.GONE);
			running = true;
			if (progressThread != null) {
				// progressThread.stop();
				progressThread = null;
			}
			flagVoice = true;
			mVoiceMedia = null;
			mVoicePath = null;
			// TODO: handle exception
		}
	}

	public void voicePlayPause() {
		if (mVoiceMedia != null) {
			mVoiceMedia.stopVoicePlaying();
			// playingtext.setVisibility(View.INVISIBLE);
			running = true;
			runningPlay = true;
			layoutclicktopause.setVisibility(View.GONE);
			sendLayout.setVisibility(View.VISIBLE);
			rerecordLayout.setVisibility(View.VISIBLE);
			playLayout.setVisibility(View.VISIBLE);
			if (progressThread != null) {
				// progressThread.stop();
				progressThread = null;
			}
		}

	}

	private void playAvailableVoice() {
		runningPlay = false;
		mVoiceMedia.startNewPlaying(mVoicePath, null, false);
		// System.out.println("playing.......");
	}

	@Override
	public void voiceRecordingStarted(String recordingPath) {

	}

	@Override
	public void voiceRecordingCompleted(final String recordedVoicePath) {
		mVoicePath = recordedVoicePath;

		if (TimerCountDown != null) {
			TimerCountDown.cancel();
			TimerCountDown = null;
		}
		KainatProfileActivity.this.handler.post(new Runnable() {

			@Override
			public void run() {
				counterValueTap = 2;
				// System.out.println("recording completed");
				buttonPlay.setVisibility(View.VISIBLE);
				buttonRerecord.setVisibility(View.VISIBLE);
				rerecordLayout.setVisibility(View.VISIBLE);
				// buttonRerecord.setVisibility(View.VISIBLE);
				buttonRecord.setVisibility(View.GONE);

			}
		});

	}

	@Override
	public void voicePlayStarted() {
		// TODO Auto-generated method stub
		// System.out.println("play voice started ====");
		running = false;
		runningPlay = false;
		// playingtext.setVisibility(View.VISIBLE);
		sendLayout.setVisibility(View.GONE);
		rerecordLayout.setVisibility(View.GONE);
		if (!running) {
			progress = 0;
			if (pw_two != null)
				;
			pw_two.resetCount();

			// pw_two.barColor=Color.YELLOW;
			progressThread = new Thread(RunnableThread);
			progressThread.start();
			layoutclicktopause.setVisibility(View.VISIBLE);
			playLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void voicePlayCompleted() {
		// TODO Auto-generated method stub
		// System.out.println("play voice completed ====");
		counterValueTap = 2;
		// playingtext.setVisibility(View.INVISIBLE);
		running = true;
		runningPlay = true;
		layoutclicktopause.setVisibility(View.GONE);
		sendLayout.setVisibility(View.VISIBLE);
		rerecordLayout.setVisibility(View.VISIBLE);
		playLayout.setVisibility(View.VISIBLE);
		if (progressThread != null) {
			// progressThread.stop();
			progressThread = null;
		}
	}

	@Override
	public void onError(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDureationchanged(final long total, final long current) {

	}

	private String mVoicePath;
	TextView buttonRecord, buttonPlay, buttonClear, buttonclose, buttonsend,
			buttonstop, textCounter, buttonRerecord, textclose;
	LinearLayout cancleLayout, recordingLayout, playLayout, sendLayout,
			rerecordLayout, stopLayout, layoutclicktopause, screentap;
	boolean flagVoice = false;
	boolean running = true;
	boolean runningPlay = true;
	int counterValueTap = 0;
	Dialog alertDialogBuilder = null;
	private VoiceMedia mVoiceMedia;
	boolean flagForSoundPlay = false;
	LinearLayout layout;
	int toBeAttached;
	Timer TimerCountDown = null;

	public void showAudio(View view) {
		LayoutInflater li = LayoutInflater.from(KainatProfileActivity.this);
		final View promptsView = li.inflate(R.layout.mystatus_dialogstatusforprofile,
				null);
		
		BusinessProxy.sSelf.stopbuzzchatoperation = true;
		flagVoice = true;
		running = true;
		runningPlay = true;
		counterValueTap = 0;
		alertDialogBuilder = new Dialog(KainatProfileActivity.this, R.style.Dialog) {
			public void onBackPressed() {
				if (!flagVoice) {
					// showSimpleAlert("Alert", "Please stop Recording !");
					// return;
					if (mVoiceMedia != null)
						mVoiceMedia.stopRec();
					flagVoice = true;
				}
				if (!runningPlay) {
					// showSimpleAlert("Alert", "Please stop Playing !");
					// return;
					voicePlayPause();
					running = true;
					runningPlay = true;
				}
				BusinessProxy.sSelf.stopbuzzchatoperation = false;
				counterValueTap = 0;
				if (alertDialogBuilder != null
						&& alertDialogBuilder.isShowing())
					alertDialogBuilder.dismiss();
				// RTMediaPlayer.reset();
			};
		};

		if (flagForSoundPlay) {

			layout.setVisibility(View.GONE);
			RTMediaPlayer.reset();
			RTMediaPlayer.clear();
			flagForSoundPlay = false;
		}

		if (alertDialogBuilder != null && alertDialogBuilder.isShowing()) {
			alertDialogBuilder.dismiss();
		}

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setContentView(promptsView);

		android.view.ViewGroup.LayoutParams params = alertDialogBuilder
				.getWindow().getAttributes();
		// params.width = LayoutParams.FILL_PARENT;
		// LayoutParams buttonLayoutParams = new
		// LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// buttonLayoutParams.setMargins(50, 10, 0, 0);
		// params.
		alertDialogBuilder.getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) params);
		alertDialogBuilder.getWindow().setGravity(Gravity.BOTTOM);
		// alertDialogBuilder.setCanceledOnTouchOutside(true);

		Window window = alertDialogBuilder.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.setBackgroundDrawable(new ColorDrawable(
				android.graphics.Color.TRANSPARENT));
		// window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
		// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		// mAddDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// mContext.getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
		// WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// userInput = (EditText) promptsView
		// .findViewById(R.id.editTextDialogUserInput);
		// textclose=(TextView)promptsView.findViewById(R.id.textclose);
		 ((TextView)promptsView.findViewById(R.id.totalcounter)).setText("2 Min");

		
		
		buttonRecord = (TextView) promptsView.findViewById(R.id.clicktorecord);
		buttonPlay = (TextView) promptsView.findViewById(R.id.clicktoplay);
		buttonRerecord = (TextView) promptsView.findViewById(R.id.clicktoclear);
		buttonclose = (TextView) promptsView.findViewById(R.id.canclemydialog);
		buttonsend = (TextView) promptsView.findViewById(R.id.sendtomystauts);
		buttonstop = (TextView) promptsView.findViewById(R.id.clicktostop);

		cancleLayout = (LinearLayout) promptsView
				.findViewById(R.id.layoutcanclemydialog);
		recordingLayout = (LinearLayout) promptsView
				.findViewById(R.id.layoutclicktorecord);
		playLayout = (LinearLayout) promptsView
				.findViewById(R.id.layoutclicktoplay);
		sendLayout = (LinearLayout) promptsView
				.findViewById(R.id.layoutsendtomystauts);
		rerecordLayout = (LinearLayout) promptsView
				.findViewById(R.id.layoutclicktoclear);
		stopLayout = (LinearLayout) promptsView
				.findViewById(R.id.layoutclicktostop);
		textCounter = (TextView) promptsView.findViewById(R.id.countertext);
		layoutclicktopause = (LinearLayout) promptsView
				.findViewById(R.id.layoutclicktopause);
		pw_two = (ProgressWheel) promptsView.findViewById(R.id.progressBarTwo);
		// screentap
		screentap = (LinearLayout) promptsView.findViewById(R.id.layoutid);
		if (mVoicePath != null) {
			counterValueTap = 2;
			buttonRecord.setVisibility(View.GONE);
			buttonstop.setVisibility(View.GONE);
			buttonPlay.setVisibility(View.VISIBLE);
			playLayout.setVisibility(View.VISIBLE);
			stopLayout.setVisibility(View.GONE);
			recordingLayout.setVisibility(View.GONE);
			buttonRerecord.setVisibility(View.VISIBLE);
			rerecordLayout.setVisibility(View.VISIBLE);
			sendLayout.setVisibility(View.VISIBLE);
			buttonsend.setVisibility(View.VISIBLE);
			textCounter.setText(Utilities
					.converMiliSecondForAudioStatus(totaltime) + " sec");
		}

		screentap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (counterValueTap == 0) {
					voiceRecording(promptsView);
				} else if (counterValueTap == 1) {
					try {
						if (!flagVoice) {
							if (mVoiceMedia != null)
								mVoiceMedia.stopRec();
						}
						if (mVoicePath != null) {
							toBeAttached |= Payload.PAYLOAD_BITMAP_VOICE;
						}
						buttonRecord.setVisibility(View.GONE);
						buttonstop.setVisibility(View.GONE);
						buttonPlay.setVisibility(View.VISIBLE);
						playLayout.setVisibility(View.VISIBLE);
						stopLayout.setVisibility(View.GONE);
						recordingLayout.setVisibility(View.GONE);
						buttonRerecord.setVisibility(View.VISIBLE);
						rerecordLayout.setVisibility(View.VISIBLE);
						sendLayout.setVisibility(View.VISIBLE);
						buttonsend.setVisibility(View.VISIBLE);
						counterValueTap = 2;
						flagVoice = true;
						running = true;
						runningPlay = true;
						((ImageView) promptsView.findViewById(R.id.sound_icon))
								.setImageResource(R.drawable.microphoneland);
						if (progressThread != null) {
							// progressThread.stop();
							progressThread = null;
						}

						if (TimerCountDown != null) {
							TimerCountDown.cancel();
							TimerCountDown = null;
						}
						// System.out.println("stop recording");
					} catch (Exception e) {
						if (TimerCountDown != null) {
							TimerCountDown.cancel();
							TimerCountDown = null;
						}
						((ImageView) promptsView.findViewById(R.id.sound_icon))
								.setImageResource(R.drawable.microphoneland);
						flagVoice = true;
						mVoiceMedia = null;
						mVoicePath = null;
						running = true;
						runningPlay = true;
						if (progressThread != null) {
							// progressThread.currentThread().stop();
							// progressThread.stop();
							progressThread = null;
						}
						buttonRecord.setVisibility(View.VISIBLE);
						buttonstop.setVisibility(View.GONE);
						buttonPlay.setVisibility(View.GONE);
						playLayout.setVisibility(View.GONE);
						stopLayout.setVisibility(View.GONE);
						buttonRerecord.setVisibility(View.GONE);
						rerecordLayout.setVisibility(View.GONE);
						recordingLayout.setVisibility(View.VISIBLE);
						sendLayout.setVisibility(View.GONE);
						buttonsend.setVisibility(View.GONE);
						// TODO: handle exception
					}

				} else if (counterValueTap == 2) {
					counterValueTap = 3;
					playAvailableVoice();

				} else if (counterValueTap == 3) {
					counterValueTap = 2;
					voicePlayPause();
					running = true;
					runningPlay = true;
				}
			}
		});

		// ((ToggleButton)promptsView.findViewById(R.id.fbOnOff));
		((ToggleButton) promptsView.findViewById(R.id.fbOnOff))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

					}
				});
		((TextView) promptsView.findViewById(R.id.textclose))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!flagVoice) {
							// showSimpleAlert("Alert",
							// "Please stop Recording !");
							// return;
							if (mVoiceMedia != null) {
								mVoiceMedia.stopRec();
							}
							flagVoice = true;
						}
						if (!runningPlay) {
							// showSimpleAlert("Alert",
							// "Please stop Playing !");
							// return;
							voicePlayPause();
							running = true;
							runningPlay = true;
						}
						if (alertDialogBuilder != null
								&& alertDialogBuilder.isShowing()) {
							alertDialogBuilder.dismiss();

						}
						mVoicePath=null;
					}
					
				});

		stopLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (!flagVoice) {
						if (mVoiceMedia != null)
							mVoiceMedia.stopRec();
					}
					if (mVoicePath != null) {
						toBeAttached |= Payload.PAYLOAD_BITMAP_VOICE;
					}
					buttonRecord.setVisibility(View.GONE);
					buttonstop.setVisibility(View.GONE);
					buttonPlay.setVisibility(View.VISIBLE);
					playLayout.setVisibility(View.VISIBLE);
					stopLayout.setVisibility(View.GONE);
					recordingLayout.setVisibility(View.GONE);
					buttonRerecord.setVisibility(View.VISIBLE);
					rerecordLayout.setVisibility(View.VISIBLE);
					sendLayout.setVisibility(View.VISIBLE);
					buttonsend.setVisibility(View.VISIBLE);
					counterValueTap = 2;
					flagVoice = true;
					running = true;
					runningPlay = true;
					((ImageView) promptsView.findViewById(R.id.sound_icon))
							.setImageResource(R.drawable.microphoneland);
					if (progressThread != null) {
						// progressThread.stop();
						progressThread = null;
					}

					if (TimerCountDown != null) {
						TimerCountDown.cancel();
						TimerCountDown = null;
					}
					// System.out.println("stop recording");
				} catch (Exception e) {
					if (TimerCountDown != null) {
						TimerCountDown.cancel();
						TimerCountDown = null;
					}
					((ImageView) promptsView.findViewById(R.id.sound_icon))
							.setImageResource(R.drawable.microphoneland);
					flagVoice = true;
					mVoiceMedia = null;
					mVoicePath = null;
					running = true;
					runningPlay = true;
					if (progressThread != null) {
						// progressThread.currentThread().stop();
						// progressThread.stop();
						progressThread = null;
					}
					buttonRecord.setVisibility(View.VISIBLE);
					buttonstop.setVisibility(View.GONE);
					buttonPlay.setVisibility(View.GONE);
					playLayout.setVisibility(View.GONE);
					stopLayout.setVisibility(View.GONE);
					buttonRerecord.setVisibility(View.GONE);
					rerecordLayout.setVisibility(View.GONE);
					recordingLayout.setVisibility(View.VISIBLE);
					sendLayout.setVisibility(View.GONE);
					buttonsend.setVisibility(View.GONE);
					// TODO: handle exception
				}
			}
		});

		recordingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if(buttonRecord.getText().equals("Click To Record")){
				voiceRecording(promptsView);
				// }

			}
		});
		playLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				counterValueTap = 3;
				playAvailableVoice();
			}
		});

		rerecordLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {

					File file = new File(mVoicePath);
					if (file.exists()) {
						file.delete();
					}

					// buttonRecord
					// =(Button)promptsView.findViewById(R.id.clicktorecord);
					buttonRecord.setVisibility(View.VISIBLE);
					recordingLayout.setVisibility(View.VISIBLE);
					// buttonRecord.setText("Click To Record");
					buttonRerecord.setVisibility(View.GONE);
					rerecordLayout.setVisibility(View.GONE);
					buttonPlay.setVisibility(View.GONE);
					sendLayout.setVisibility(View.GONE);
					buttonsend.setVisibility(View.GONE);
					// buttonRerecord.setVisibility(View.GONE);
					((ImageView) promptsView.findViewById(R.id.sound_icon))
							.setImageResource(R.drawable.microphonerotate);

					if (TimerCountDown != null) {
						TimerCountDown.cancel();
						TimerCountDown = null;
					}
					if (progressThread != null) {
						// progressThread.stop();
						progressThread = null;
					}
					if (textCounter != null) {
						textCounter.setText("0 sec");
					}

					flagVoice = true;
					mVoiceMedia = null;
					toBeAttached = 0;
					mVoicePath = null;
					// buttonRecord.setOnClickListener(this);
					voiceRecording(promptsView);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

		sendLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// if(mVoiceMedia!=null && flagVoice )
					// return;
					BusinessProxy.sSelf.stopbuzzchatoperation = false;

					if (!flagVoice) {
						if (mVoiceMedia != null) {
							mVoiceMedia.stopRec();
						}
						flagVoice = true;
						// showSimpleAlert("Alert", "Please stop Recording !");
						// return;

					}
					if (!runningPlay) {
						// counterValueTap=2;
						voicePlayPause();
						running = true;
						runningPlay = true;
						// showSimpleAlert("Alert", "Please stop Playing !");
						// return;
					}
					// sendMessage(null);
					if (alertDialogBuilder != null
							&& alertDialogBuilder.isShowing())
						alertDialogBuilder.dismiss();
					// TODO Auto-generated method stub

					new Thread(new Runnable() {

						@Override
						public void run() {
							// audioUpadte(mVoicePath);
						}
					}).start();

					if (TimerCountDown != null) {
						TimerCountDown.cancel();
						TimerCountDown = null;
						mVoiceMedia = null;
						// toBeAttached=0;
						mVoicePath = null;
					}

					if (progressThread != null) {
						// progressThread.stop();
						progressThread = null;
					}

					((TextView) findViewById(R.id.VoiceUpdatedemo_txt)).setText("Profile message");
					((ImageView)findViewById(R.id.VoiceUpdatedemo)).setImageResource(R.drawable.profile_voice);
					
				} catch (Exception e) {
					// TODO: handle exception
					if (TimerCountDown != null) {
						TimerCountDown.cancel();
						TimerCountDown = null;
					}
					running = true;
					runningPlay = true;
					if (progressThread != null) {
						// progressThread.stop();
						progressThread = null;
					}
					flagVoice = true;
					mVoiceMedia = null;
					toBeAttached = 0;
					mVoicePath = null;
				}
			}

		});

		layoutclicktopause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				counterValueTap = 2;
				voicePlayPause();
				running = true;
				runningPlay = true;
			}
		});

		buttonclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (alertDialogBuilder != null) {
					alertDialogBuilder.dismiss();
				}
				BusinessProxy.sSelf.stopbuzzchatoperation = false;
				counterValueTap = 0;
				// mVoiceMedia=null;
				// toBeAttached=0;
				mVoicePath = null;
			}
		});

		/*
		 * ((ImageButton)promptsView.findViewById(R.id.composeScreen_addSmiley)).
		 * setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub showCustomList("Select Smiley", null, "Cancel",
		 * HomeActivity.this, HomeActivity.this); } });
		 */

		alertDialogBuilder.show();// AtLocation(HomeActivity.this.getWindow().getDecorView(),
									// Gravity.NO_GRAVITY, 10, 0);

	}

	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar dob = new GregorianCalendar(view.getYear(),
					view.getMonth(), view.getDayOfMonth());
			Calendar dateOfBirth = new GregorianCalendar(view.getYear(),
					view.getMonth(), view.getDayOfMonth());
			Calendar today = Calendar.getInstance();
			int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
			if (age < Constants.MIN_AGE) {
				showSimpleAlert("Error",
						"You must be 16 years or above to register with RockeTalk.");
				return;
			}

			String s = dob.get(Calendar.DAY_OF_MONTH) + "/"
					+ (dob.get(Calendar.MONTH) + 1) + "/"
					+ dob.get(Calendar.YEAR);

			((TextView) currentDateview).setText(s);
		}

	};
	OnTouchListener openDateDialog = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
//			System.out.println("----event.getAction():" + event.getAction());
			if (event.getAction() == MotionEvent.ACTION_UP) {

				if (datePickerDialog != null && datePickerDialog.isShowing())
					return true;

				myCalendar = Calendar.getInstance();
				currentDateview = v;
				myCalendar.set((myCalendar.get(Calendar.YEAR) - 16),
						myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH));
				try {
					String dob = ((TextView) v).getText().toString();
					String dobArr[] = dob.split("/");
					myCalendar.set(Integer.parseInt(dobArr[2]),
							(Integer.parseInt(dobArr[1]) - 1),
							Integer.parseInt(dobArr[0]));
				} catch (Exception e) {
					// TODO: handle exception
				}
				datePickerDialog = new DatePickerDialog(KainatProfileActivity.this,
						date, myCalendar.get(Calendar.YEAR),
						myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH));
				// datePickerDialog.
				// datePickerDialog.
				// datePickerDialog.show();
				v.post(new Runnable() {

					@Override
					public void run() {
						datePickerDialog.show();
					}
				});
			}
			return false;
		}
	};
	private void openPlayScreen(final String url, boolean autoPlay,
			final LinearLayout ln) {
		TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
		tv.setText("Please wait while getting audio...");
		RTMediaPlayer.setUrl(null);
		tv.setTextColor(0xff6AABB4);
		if (!autoPlay) {
			ImageView imageView1 = (ImageView) ln.findViewById(R.id.media_play);
			// imageView1.setTag(clickId+"^"+url);
			imageView1.setTag("PLAY");
			imageView1.setVisibility(View.INVISIBLE);
			imageView1.setOnClickListener(addClick);
		}
		RTMediaPlayer.setProgressBar(baradd);
		RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

			@Override
			public void voiceRecordingStarted(String recordingPath) {
				// TODO Auto-generated method stub

			}

			@Override
			public void voiceRecordingCompleted(String recordedVoicePath) {
				// TODO Auto-generated method stub
				
			}

			public void voicePlayStarted() {

				try {
					ln.findViewById(R.id.streemStatus).setVisibility(View.GONE);
					LinearLayout d = ((LinearLayout) ln
							.findViewById(R.id.media_play_layout));
					d.setVisibility(View.VISIBLE);
					ImageView imageView1 = (ImageView) ln
							.findViewById(R.id.media_close);// media_play
					// imageView1.setImageResource(R.drawable.pause);
					if (imageView1 != null) {
						imageView1.setVisibility(View.INVISIBLE);
						imageView1.setOnClickListener(addClick);
						imageView1.setTag("PAUSE");
					}
					imageView1 = (ImageView) ln.findViewById(R.id.media_play);

					// imageView1.setTag(clickId+"^"+url);
					if (imageView1 != null) {
						imageView1.setOnClickListener(addClick);
						imageView1.setTag("PLAY");
						imageView1.setBackgroundResource(R.drawable.addpause);
						imageView1.setTag("PAUSE");
						imageView1.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			public void voicePlayCompleted() {
				KainatProfileActivity.this.handler.post(new Runnable() {

					@Override
					public void run() {
						// closePlayScreen();
						// System.out.println("-----voicePlayCompleted-----");
						try {
							ImageView imageView1 = (ImageView) ln
									.findViewById(R.id.media_play);
							imageView1
									.setBackgroundResource(R.drawable.addplay);
							imageView1.setTag("PLAY");
							RTMediaPlayer.reset();
							layout.setVisibility(View.GONE);
							RTMediaPlayer.clear();
							resetProgress() ;
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
				// isFormDisplay = false ;
				// shouldStartDisplayTimer();
			}

			public void onError(int i) {
				try {
					TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
					tv.setTextColor(0xffff0000);
					tv.setText("Unable to play please try later!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onDureationchanged(final long total, final long current) {
				KainatProfileActivity.this.handler.post(new Runnable() {

					@Override
					public void run() {
						try {
//							System.out.println("---------total : "+total);
							total_autio_time.setText(""
									+ Utilities.converMiliSecond(total)+")");
							total_autio_time.setTypeface(Typeface.DEFAULT_BOLD);
							played_autio_time.setText("("
									+ Utilities.converMiliSecond(current)+" of ");
							played_autio_time
									.setTypeface(Typeface.DEFAULT_BOLD);
						} catch (Exception e) {
							// if(isAudio)
							RTMediaPlayer.reset();
						}
					}
				});
			}
		});
		ln.findViewById(R.id.streemStatus).setVisibility(View.VISIBLE);
		// if(autoPlay)
		RTMediaPlayer._startPlay(url);
		ln.findViewById(R.id.media_play_layout).setVisibility(View.VISIBLE);
	}
	private View.OnClickListener addClick = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.media_play:
				ImageView imageView1 = null;
				try {

					imageView1 = (ImageView) layout
							.findViewById(R.id.media_play);
					if (((String) v.getTag()).equals("PLAY")) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {

									// baradd.setTag(new Stats(object)) ;
									TextView tv = (TextView) layout
											.findViewById(R.id.streemStatus);
									tv.setTextColor(0xff6AABB4);
									if (RTMediaPlayer.getUrl() != null)
										RTMediaPlayer.start();
									else {
										try {
											//
											RTMediaPlayer._startPlay(v.getTag()
													.toString());

										} catch (Exception e) {
											// e.print
										}
									}
								} catch (Exception e) {
								}
							}
						}).start();
						imageView1.setBackgroundResource(R.drawable.addpause);
						imageView1.setTag("PAUSE");

					} else if (((String) v.getTag()).equals("PAUSE")) {
						imageView1.setBackgroundResource(R.drawable.addplay);
						imageView1.setTag("PLAY");
						RTMediaPlayer.pause();
						imageView1 = (ImageView) layout
								.findViewById(R.id.media_pause_play);
						imageView1.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
				}
				break;
			case R.id.media_stop_play:
				layout.setVisibility(View.GONE);
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
				break;
			}
		}
	};
	
	
	
	private int lastSelectedIndex = -1;
	private AlertDialog mCountryPicker, mLanguagePicker;
	private String mCountryName;
	private String mStateName;
	private String mLangauge;
	
	private void showLangaugeSelector() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				KainatProfileActivity.this);
		int index = 0;
		if (null != mLangauge) {
			for (index = 0; index < Constants.LANGUAGE.length; index++) {
				if (Constants.LANGUAGE[index].equals(mLangauge)) {
					break;
				}
			}
		}
		if (Constants.LANGUAGE.length == index) {
			index = 0;
		}
		lastSelectedIndex = index;

		dialog.setTitle("Select Langauge");
		dialog.setSingleChoiceItems(Constants.LANGUAGE, index,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mLangauge = Constants.LANGUAGE[which];
					}
				});
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (mLangauge == null) {
					return;
				}
				((TextView) findViewById(R.id.language))
						.setText(mLangauge);
				((TextView) findViewById(R.id.language_textview))
					.setText("Language");
					
					
					
				/*if (!mCountryName.equalsIgnoreCase("India")) {
					((EditText) findViewById(R.id.location_state))
							.setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.location_state_textview))
							.setVisibility(View.GONE);
				} else {
					((EditText) findViewById(R.id.location_state))
							.setVisibility(View.GONE);
					((TextView) findViewById(R.id.location_state_textview))
							.setVisibility(View.VISIBLE);
				}*/
				/*if (!mLangauge
						.equals(Constants.COUNTRY_LIST1[lastSelectedIndex])) {
					((EditText) findViewById(R.id.location_state))
							.setText(null);
					((TextView) findViewById(R.id.location_state_textview))
							.setText(null);
				}*/
			}
		});
		mLanguagePicker = dialog.create();
		mLanguagePicker.show();
	}
	public int selectedInterestInd = 0 ;
	private void showInterest() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				KainatProfileActivity.this);
		

		dialog.setTitle("Select Interest");
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				R.layout.histroy, list);
		dialog.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				dialog.dismiss() ;
				if (selectedInterest.size() >= 10) {
					showSimpleAlert("",
							"You have already added 10 interests!");
					return;
				}
				selectedInterestInd = position ;
				((TextView) findViewById(R.id.interest_enter))
						.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				interest = new Interest();

				interest.setName(Constants.values[position]);
				interest.setCatId(Constants.valuesId[position]);
				interest.setIcon(/*Constants.icon[position]*/Constants.iconHash.get(Constants.valuesId[position]));
				
				
				
					TextView v = ((TextView)findViewById(R.id.drop_down));
//					v.setTextColor(Color.BLACK);
					v.setText("    "+Constants.values[position]);
//					v.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
//					v.setTextSize(17) ;
//					v.setTypeface(null, Typeface.BOLD); 
//					LinearLayout.LayoutParams layoutParams = new LayoutParams(
//							LayoutParams.FILL_PARENT, 100);
//					layoutParams.setMargins(10, 5, 0, 0);
//					v.setLayoutParams(layoutParams);
//					v.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					Drawable search_unsel = getResources().getDrawable(Constants.iconHash.get(Constants.valuesId[position])
							/*Constants.icon[position]*/);
					Drawable drop = getResources().getDrawable(R.drawable.drop_down_arrow);
					Rect r = null;
					Drawable[] d = v.getCompoundDrawables();
					r = null;
					if (d[0] != null)
						r = d[0].getBounds();
					if (r != null)
						search_unsel.setBounds(r);
					
					if (d[2] != null)
						r = d[2].getBounds();
					if (r != null)
						drop.setBounds(r);
					
//					search_unsel.setBounds(5, 0, 30, 30);
					v.setCompoundDrawables(search_unsel, null, drop, null);

			}
		});
		
//		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				if (mLangauge == null) {
//					return;
//				}
//				((TextView) findViewById(R.id.language))
//						.setText(mLangauge);
//				
//			}
//		});
		mLanguagePicker = dialog.create();
		mLanguagePicker.show();
	}
	
	
	private void showCountrySelector() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				KainatProfileActivity.this);
		int index = 0;
		if (null != mCountryName) {
			for (index = 0; index < Constants.COUNTRY_LIST1.length; index++) {
				if (Constants.COUNTRY_LIST1[index].equals(mCountryName)) {
					break;
				}
			}
		}
		if (Constants.COUNTRY_LIST1.length == index) {
			index = 0;
		}
		lastSelectedIndex = index;

		dialog.setTitle("Select Country");
		dialog.setSingleChoiceItems(Constants.COUNTRY_LIST1, index,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mCountryName = Constants.COUNTRY_LIST1[which];
					}
				});
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (mCountryName == null) {
					return;
				}
				((TextView) findViewById(R.id.base_country))
						.setText(mCountryName);
//				if (!mCountryName.equalsIgnoreCase("India")) {
//					((EditText) findViewById(R.id.location_state))
//							.setVisibility(View.VISIBLE);
//					((TextView) findViewById(R.id.location_state_textview))
//							.setVisibility(View.GONE);
//				} else {
//					((EditText) findViewById(R.id.location_state))
//							.setVisibility(View.GONE);
//					((TextView) findViewById(R.id.location_state_textview))
//							.setVisibility(View.VISIBLE);
//				}
//				if (!mCountryName
//						.equals(Constants.COUNTRY_LIST1[lastSelectedIndex])) {
//					((EditText) findViewById(R.id.location_state))
//							.setText(null);
//					((TextView) findViewById(R.id.location_state_textview))
//							.setText(null);
//				}
			}
		});
		mCountryPicker = dialog.create();
		mCountryPicker.show();
	}

	private void showStateSelector() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				KainatProfileActivity.this);
		int index = 0;
		if (null != mStateName && null != mCountryName
				&& mCountryName.equalsIgnoreCase("India")) {
			for (index = 0; index < Constants.STATE_NAME.length; index++) {
				if (Constants.STATE_NAME[index].equals(mStateName)) {
					break;
				}
			}
		}
		if (Constants.STATE_NAME.length == index) {
			index = 0;
		}
		lastSelectedIndex = index;

		dialog.setTitle("Select State");
		dialog.setSingleChoiceItems(Constants.STATE_NAME, index,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mStateName = Constants.STATE_NAME[which];
					}
				});
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (mStateName == null) {
					return;
				}
				((TextView) findViewById(R.id.location_state_textview))
						.setText(mStateName);
			}
		});
		mCountryPicker = dialog.create();
		mCountryPicker.show();
	}
	public void showLocationScreen(final boolean showValues) {
		SCREEN_VIEW = SCREEN_PROFILE_VIEW;
		Utilities.closeSoftKeyBoard(findViewById(R.id.profile_name), this);
		runOnUiThread(new Runnable() {
			public void run() {
			
				//screenSlide(SettingUIActivity.this, R.layout.setting_location);
				setContentView(R.layout.setting_location);
				if (showValues) {
					mCountryName = SettingData.sSelf.getCountry();
					mStateName = SettingData.sSelf.getState();
					((EditText) findViewById(R.id.location_address))
							.setText(SettingData.sSelf.getAddress());
					((EditText) findViewById(R.id.location_city))
							.setText(SettingData.sSelf.getCity());
					((EditText) findViewById(R.id.location_pin))
							.setText(SettingData.sSelf.getZip());
					((TextView) findViewById(R.id.location_country))
							.setText(mCountryName);
					if (mCountryName != null
							&& !mCountryName.equalsIgnoreCase("India")) {
						((EditText) findViewById(R.id.location_state))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.location_state_textview))
								.setVisibility(View.GONE);
						((EditText) findViewById(R.id.location_state))
								.setText(mStateName);
					} else {
						((EditText) findViewById(R.id.location_state))
								.setVisibility(View.GONE);
						((TextView) findViewById(R.id.location_state_textview))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.location_state_textview))
								.setText(mStateName);
					}
				}
				((TextView) findViewById(R.id.location_city))
				.setText(profileBean.getCity());
				((TextView) findViewById(R.id.location_country))
				.setText(profileBean.getCountry());
			}
		});
//		BusinessProxy.sSelf.recordScreenStatistics("SETT_B_L", true, true);// Add
																			// Record
	}
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	public void resetProgress() {
		// ProgressBar progressBar = (ProgressBar)
		// findViewById(R.id.progressbar);
		baradd.setVisibility(View.VISIBLE);
		baradd.setProgress(0);
		baradd.invalidate();
	}
	
//	public int calCulateAge(String date){
//		try{
//		 String dobArr[] = date.split("/") ;
//   	
//		int year=Integer.parseInt(dobArr[2]);
//		int month =(Integer.parseInt(dobArr[1])-1);
//		int datebirth =Integer.parseInt(dobArr[0]);
//Calendar dob = new GregorianCalendar(year,
//		month, datebirth);
//Calendar dateOfBirth = new GregorianCalendar(year,
//		month, datebirth);
//Calendar today = Calendar.getInstance();
//int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
//return age;
//		}catch (Exception e) {
//		return "" ;
//		}
//	}
	
	boolean closeWithoutAsk = false ;
	public boolean isChanged(){
		if(closeWithoutAsk)
			return false ;
		String txt = ((TextView) findViewById(R.id.dob)).getText().toString() ;
		if(!txt.equals(profileBean.birthday)){
			return true ;
		}
		txt = ((TextView) findViewById(R.id.base_country)).getText().toString() ;
		if(!txt.equals(profileBean.country)){
			return true ;
		}
		txt = ((TextView) findViewById(R.id.base_city)).getText().toString() ;
		if(!txt.equals(profileBean.city)){
			return true ;
		}
		txt = ((TextView) findViewById(R.id.language)).getText().toString() ;
		if(!txt.equals(profileBean.language)){
			return true ;
		}
		if(pictureClear)
			return true ;
		if(audioClear)
			return true ;
		
		if(mImagesPath!=null && mImagesPath.size()>0)
			return true ;
		if(mVoicePath!=null && mVoicePath.trim().length()>0)
			return true ;
					
		return false;
	}
	
	
	 private void performCrop(){
	    	//take care of exceptions
	    	try {
	    		//call the standard crop action intent (the user device may not support it)
		    	Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
		    	//indicate image type and Uri
		    	cropIntent.setDataAndType(picUri, "image/*");
		    	//set crop properties
		    	cropIntent.putExtra("crop", "true");
		    	//indicate aspect of desired crop
		    	cropIntent.putExtra("aspectX", 1);
		    	cropIntent.putExtra("aspectY", 1);
		    	//indicate output X and Y
		    	cropIntent.putExtra("outputX", 256);
		    	cropIntent.putExtra("outputY", 256);
		    	//retrieve data on return
		    	
		    	try {

		    		cropIntent.putExtra("return-data", true);
			    	//start the activity - we handle returning in onActivityResult
			        startActivityForResult(cropIntent, PIC_CROP);  

		    		} catch (ActivityNotFoundException e) {
		    		// Do nothing for now
		    		}


		    		//Read more: http://www.androidhub4you.com/2012/07/how-to-crop-image-from-camera-and.html#ixzz2nFohM4vz
		    	/*cropIntent.putExtra("return-data", true);
		    	//start the activity - we handle returning in onActivityResult
		        startActivityForResult(cropIntent, PIC_CROP); */ 
	    	}
	    	//respond to users whose devices do not support the crop action
	    	    catch(ActivityNotFoundException anfe){
	    		//display an error message
	    		String errorMessage = "Whoops - your device doesn't support the crop action!";
	    		Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
	    		toast.show();
	    	}
	    }
	 
	 
	 public void profileImageCounter(){
		 runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mImagesPath!=null && mImagesPath.size()>0){
				((TextView) findViewById(R.id.photo_count)).setText("" + mImagesPath.size());
				findViewById(R.id.photo_count).setVisibility(View.VISIBLE);
				}
			}
		});
	 }
}
