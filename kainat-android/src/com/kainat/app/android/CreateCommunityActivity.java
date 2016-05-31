package com.kainat.app.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.CommunityTable;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.uicontrol.MultiPhotoViewer;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.CompressImage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageLoader;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;
import com.kainat.app.autocomplete.tag.ChipsAdapter;
import com.kainat.app.autocomplete.tag.ChipsItem;
import com.kainat.app.autocomplete.tag.ChipsMultiAutoCompleteTextview;
import com.rockerhieu.emojicon.EmojiconEditText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CreateCommunityActivity extends UICommunityManager implements
OnItemClickListener, ThumbnailReponseHandler {
	private static final String TAG = CreateCommunityActivity.class.getSimpleName();
	boolean log = false;
	private CheckedTextView chked_status;
	private Button back_button;
	private ImageButton attachImg;
	private ImageView coverImgBg = null;
	private CheckBox termsChk;
	private EditText nameEditor, descriptionEditor;
	private String category;
	private String cameraImagePath = null;
	private static final byte POSITION_PICTURE = 0;
	private static final byte POSITION_CAMERA_PICTURE = 1;
	public static final byte MODE_ATTACH_PHOTO = 1;
	public static final byte MODE_NORMAL = 2;
	public static final String SCREEN_MODE = "SCREEN_MODE";
	public static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public static final Pattern MOBILE_NUMBER_PATTERN_WITH_CC = Pattern.compile("^\\d{1,5}[-]\\d{6,12}$");
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\d{8,16}$");
	public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,29}$");
	public static final Pattern NAME_PATTERN = Pattern.compile("^\\s*([A-Za-z]{3,})(\\s*([A-Za-z ]+?))?\\s*$");
	private byte mode;
	public long buzzSentTime = 0;
	private MyCustomDialog dialog;
	private CommunityFeed.Entry entry = null;
	private boolean communityPictureDirty = false;
	boolean frontCam, rearCam;
	final Pattern SUBJECT_PATTERN = Pattern.compile("^[a-zA-Z0-9@.#$%^&* _&\\\\]+$");
	private ImageView nextText, editButton;
	private ChipsAdapter chipsAdapter = null;
	private String tagStr = "";
	private int tagCount = 0;
	private boolean isEdit;
	private ProgressDialog rtDialog;

	ArrayList<String> addTagList = new ArrayList<String>();
	private String oldTagCode = "";
	private ChipsMultiAutoCompleteTextview minTagField = null;
	private TextWatcher mWatcher = null;
	private int closeCounter = -1;
	InputMethodManager imm = null;
	private EditText catagoryField;
    private String channelDescBeforeEdit;
    private boolean isPublicChannelBeforeEdit;
    private boolean isPublicChannelAfterEdit;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.kainat_create_community_screen);
		communityPictureDirty = false;
		attachImg = (ImageButton) findViewById(R.id.attach_photo_id);
		attachImg.setOnClickListener(this);
		back_button = (Button) findViewById(R.id.web_back_button);
		back_button.setOnClickListener(this);
		dialog = new MyCustomDialog(this);
		coverImgBg = (ImageView) findViewById(R.id.cover_img_bg);
		if(getIntent().getExtras() != null)
			isEdit = getIntent().getExtras().getBoolean("EDIT");
		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		BusinessProxy.sSelf.communityCategories = getResources().getStringArray(R.array.community_new_select);

		if (BusinessProxy.sSelf.communityCategories != null) {
			Spinner sp = (Spinner) findViewById(R.id.cmunty_ctgry_nam_id);
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,BusinessProxy.sSelf.communityCategories);
			spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(spinnerArrayAdapter);
		}

		((TextView) findViewById(R.id.titleText))
		.setText(R.string.create_channel);
		nextText = (ImageView) findViewById(R.id.create_bt);
		editButton = (ImageView) findViewById(R.id.update_community_submit);
		nextText.setOnClickListener(this);
		editButton.setOnClickListener(this);
		PackageManager pm = getPackageManager();
		frontCam = pm.hasSystemFeature("android.hardware.camera.front");

		rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		TextView termsLink = (TextView) findViewById(R.id.terms_condition_link);
		String text = "<a href=" + Urls.COM_TERM_AND_SERVICES + ">"
				+ getString(R.string.terms_of_service) + "</a>";
		termsLink.setText(Html.fromHtml(text));
		termsLink.setOnClickListener(this);
		termsChk = (CheckBox) findViewById(R.id.terms_condition_id);
		termsChk.setOnClickListener(this);
		chked_status = (CheckedTextView) findViewById(R.id.availiablity_id);
		descriptionEditor = (EmojiconEditText)findViewById(R.id.community_description_id);

		// descriptionEditor.setText(Utilities.sRegId + " NP");

		nameEditor = (EditText) findViewById(R.id.community_name_id);
		nameEditor.setText(null);
		nameEditor.setFocusable(true);
		//Set cpecial character filter in Channel Name field
//		nameEditor.setFilters(new InputFilter[] { filter });
		if(!isEdit)
			nameEditor.addTextChangedListener(new TextWatcher() {
				//			int startPos = 0;
				String text_before_change = null;
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					Log.i(TAG, "onTextChanged : Text after change : "+s+", start - "+start+", before - "+before);
				}
	
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					text_before_change = s.toString();
					Log.i(TAG, "beforeTextChanged text : "+s);
				}
	
				@Override
				public void afterTextChanged(Editable s) {
					Log.i(TAG, "afterTextChanged Text after change : "+s.toString());
					try{
	//					Pattern ALPHANUMERIC_ARABIC_CHARS = Pattern.compile("^[- \\w_\\u0600-\\u06FF]+", Pattern.UNICODE_CASE);
	//					Matcher alpha_numeric_arabic = ALPHANUMERIC_ARABIC_CHARS.matcher(s.toString());
						
						Pattern ALPHANUMERIC = Pattern.compile("^[ \\w._-]+", Pattern.UNICODE_CASE);
						Pattern ARABIC_CHARS = Pattern.compile("[\\u0600-\\u06FF]*+", Pattern.UNICODE_CASE);
						Matcher alphaNumeric = ALPHANUMERIC.matcher(s.toString());
						Matcher arabic = ARABIC_CHARS.matcher(s.toString());
						if(!alphaNumeric.matches() && !arabic.matches()){
							//Means some special char is entered other than - [a-zA-Z0-9_- \\u0600-\\u06FF]
							//\\u0600-\\u06FF is uni-code reference values
							//Show and Alert that special char is not allow for this action
							nameEditor.setText(text_before_change);
							nameEditor.setSelection(nameEditor.getText().length());
							showToast(getString(R.string.channel_name_validation_alert)+s.toString().substring(s.toString().lastIndexOf(text_before_change) + text_before_change.length()));
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});

		nameEditor.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String name = v.getText().toString();
					if (checkNameValidation(name)) {
						checkAvailability(name, v);
					}
				}
				return false;
			}
		});
		nameEditor.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					Utilities
					.closeSoftKeyBoard(v, CreateCommunityActivity.this);
					String name = ((TextView) v).getText().toString();
					if (name == null || name.trim().equals("")) {
						return;
					}
					if (checkNameValidation(name)) {
						checkAvailability(name, ((TextView) v));
					}
				}
			}
		});
		catagoryField = (EditText) findViewById(R.id.catagory_filed_id);
		catagoryField.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).performClick();
				Utilities.closeSoftKeyBoard(v,
						CreateCommunityActivity.this);
				return false;
			}
		});
		((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Utilities.closeSoftKeyBoard(view,
						CreateCommunityActivity.this);
				catagoryField.setText(((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		((Spinner) findViewById(R.id.cmunty_ctgry_nam_id))
		.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				Utilities.closeSoftKeyBoard(view,
						CreateCommunityActivity.this);
				return false;
			}
		});

		entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY);
		if (entry != null) {
			boolean check = false;
			((TextView) findViewById(R.id.titleText))
			.setText(R.string.update_community);
			nameEditor.setText(entry.groupName);
			nameEditor.setEnabled(false);
			nameEditor.setFocusable(false);
			nameEditor.setInputType(InputType.TYPE_NULL);
			if(UIActivityManager.directFromCreateChannel){
				channelDescBeforeEdit = entry.description;
				descriptionEditor.setText(entry.description);
			}
			else
			{
				try{
					byte[] messageText = Base64.decode(entry.description, Base64.DEFAULT);
					descriptionEditor.setText(new String(messageText, "utf-8"));
					channelDescBeforeEdit = descriptionEditor.getText().toString().trim();
				}catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					channelDescBeforeEdit = entry.description;
					descriptionEditor.setText(entry.description);
					e.printStackTrace();
				}catch(Exception ex){
					channelDescBeforeEdit = entry.description;
					descriptionEditor.setText(entry.description);
					ex.printStackTrace();
				}

			}
			String[] values = getResources().getStringArray(R.array.community_category);
			if (BusinessProxy.sSelf.communityCategories != null)
				values = BusinessProxy.sSelf.communityCategories;
			for (int i = 0; i < values.length; i++) 
			{
				if (values[i].equalsIgnoreCase(entry.category)) 
				{
					((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).setSelection(i);
					check = true;
					break;
				}
			}
			if(!check && getLocale() != null && getLocale().equalsIgnoreCase("en"))
			{
				values = getResources().getStringArray(R.array.community_new_select_arabic);
				for (int i = 0; i < values.length; i++) 
				{
					if (values[i].equalsIgnoreCase(entry.category)) 
					{
						((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).setSelection(i);
						check = true;
						break;
					}
				}
			}
			if(!check && getLocale() != null && getLocale().equalsIgnoreCase("ar"))
			{
				values = getResources().getStringArray(R.array.community_new_select_english);
				for (int i = 0; i < values.length; i++) 
				{
					if (values[i].equalsIgnoreCase(entry.category)) 
					{
						((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).setSelection(i);
						check = true;
						break;
					}
				}
			}
			check = false;
			isPublicChannelBeforeEdit = entry.publicCommunity.equalsIgnoreCase("YES");
			((ToggleButton) findViewById(R.id.public_id))
			.setChecked(isPublicChannelBeforeEdit);

			((ToggleButton) findViewById(R.id.auto_accept))
			.setChecked(entry.autoAcceptUser.equalsIgnoreCase("YES"));

			((ToggleButton) findViewById(R.id.moderation_toggle))
			.setChecked(entry.moderated.equalsIgnoreCase("YES"));

			findViewById(R.id.termsLayout).setVisibility(View.GONE);

			/*		findViewById(R.id.update_community_submit).setVisibility(
					View.VISIBLE);*/

			((LinearLayout) findViewById(R.id.public_toggle)).setVisibility(View.VISIBLE);

			if(UIActivityManager.directFromCreateChannel)
			{
				//Set local Image Here, Image local path is - entry.thumbUrl
				coverImgBg.setImageURI(Uri.parse(entry.thumbUrl));
			}
			else
			{
				ImageLoader loader = ImageLoader
						.getInstance(new ThumbnailReponseHandler() {
							public void onThumbnailResponse(
									final ThumbnailImage value, byte[] data) {
								runOnUiThread(new Runnable() {
									public void run() {
										//attachImg.setImageBitmap(value.mBitmap);
										Drawable d1 = new BitmapDrawable(getResources(), value.mBitmap);
										coverImgBg.setImageDrawable(d1);
									}
								});
							}
						});
				loader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_XML);
				loader.cleanAndPutRequest(new String[] { entry.thumbUrl }, new int[] { 0 });
			}

			termsChk.setChecked(true);
			termsChk.setVisibility(View.VISIBLE);
			nextText.setVisibility(View.VISIBLE);

		}

		minTagField = (ChipsMultiAutoCompleteTextview) findViewById(R.id.min_three_tag);
		//Set special character filer in hash tag field
		minTagField.setFilters(new InputFilter[] { filter });

		ArrayList<ChipsItem> arrCountry = new ArrayList<ChipsItem>();
		chipsAdapter = new ChipsAdapter(this, arrCountry);
		minTagField.setAdapter(chipsAdapter);

		mWatcher = new TextWatcher() {
			String text_before_change = null;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onTextChanged : Text after change : "+s+", start - "+start+", before - "+before);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				text_before_change = s.toString();
				Log.i(TAG, "beforeTextChanged text : "+s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.toString().length() > text_before_change.length()){
					Pattern ALPHANUMERIC = Pattern.compile("^[ \\w#]+", Pattern.UNICODE_CASE);
					Pattern ARABIC_CHARS = Pattern.compile("[\\u0600-\\u06FF]*+", Pattern.UNICODE_CASE);
					Matcher alphaNumeric = ALPHANUMERIC.matcher(s.toString());
					Matcher arabic = ARABIC_CHARS.matcher(s.toString());
					String entered_char = s.toString().substring(s.toString().lastIndexOf(text_before_change) + text_before_change.length());
					String last_tag = s.toString().substring(s.toString().lastIndexOf(' ') + 1);
					if((entered_char.equals("#") 
							&& last_tag.startsWith("#")
							&& last_tag.indexOf('#') != last_tag.lastIndexOf('#')) 
							|| (entered_char.length() == 0 
							&& last_tag.equals("##"))){
						showToast(getString(R.string.channel_tag_one_hash));
						minTagField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
						return;
					}else if(entered_char.equals("#") 
							&& last_tag.length() > 1
							&& last_tag.indexOf('#') != 0){
						showToast(getString(R.string.channel_tag_hash_allowed_in_between));
						minTagField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
						return;
					}else if(!alphaNumeric.matches() && !arabic.matches()){
						//Means some special char is entered other than - [a-zA-Z0-9_- \\u0600-\\u06FF]
						//\\u0600-\\u06FF is uni-code reference values
						//Show and Alert that special char is not allow for this action
	//					minTagField.setText(text_before_change);
						showToast(entered_char + " " + getString(R.string.channel_tag_validation_alert));
						minTagField.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
						return;
					}
				}

				if(minTagField.nCounter != 0){
					if(minTagField.nCounter == 10 && closeCounter != minTagField.nCounter){
						minTagField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(minTagField.getText().toString().length()) });
						imm.hideSoftInputFromWindow(minTagField.getWindowToken(), 0); 
					}else{
						minTagField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(160) });
					}
					closeCounter = minTagField.nCounter;
				}
			}
		};
		minTagField.addTextChangedListener(mWatcher);
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		if(isEdit)
			t.setScreenName("Edit Channel Screen");
		else
			t.setScreenName("Create Channel Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(CreateCommunityActivity.this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(CreateCommunityActivity.this).reportActivityStop(this);
    }
	private String toUnicode(char ch) {
		return String.format("\\u%04x", (int) ch);
	}

	private InputFilter filter = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

			if (source != null && Constants.BLOBKED_CHARS_TAGS.contains(("" + source))) {
				return "";
			}
			return null;
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(entry != null && entry.tags != null && !entry.tags.equals("")){
			tagStr = entry.tags;
			minTagField.setText(tagStr.replace("," , " "));
			minTagField.setChips();
		}
	}
	private boolean checkNameValidation(String name) {
		if (1 == 1)
			return true;

		if (name == null || name.equals("")) {
			showSimpleAlert(getString(R.string.app_name),
					getString(R.string.please_enter_a_community_name));
			findViewById(R.id.community_name_id).requestFocus();
			return false;
		}
		if (name.charAt(0) >= 'A' && name.charAt(0) <= 'Z'
				|| name.charAt(0) >= 'a' && name.charAt(0) <= 'z')
			return true;
		// showSimpleAlert("Error",
		// " 'Community Name' should not start with numeric & special character!");
		chked_status.setTextColor(Color.RED);
		chked_status
		.setText(getString(R.string.name_should_start_with_an_alphabet));
		return false;
	}

	private void saveLocalData() {
		if (entry == null) {
			return;
		}
		entry.description = descriptionEditor.getText().toString();
		entry.category = String
				.valueOf(((Spinner) findViewById(R.id.cmunty_ctgry_nam_id))
						.getSelectedItem());
		// entry.publicCommunity = ((ToggleButton)
		// findViewById(R.id.public_id)).getText().toString().equals("ON") ?
		// "YES" : "NO";
		// entry.autoAcceptUser = ((ToggleButton)
		// findViewById(R.id.auto_accept)).getText().toString().equals("ON") ?
		// "YES" : "NO";
		// entry.moderated = ((ToggleButton)
		// findViewById(R.id.moderation_toggle)).getText().toString().equals("ON")
		// ? "YES" : "NO";

		// String isPublic = null;//
		if (((ToggleButton) findViewById(R.id.public_id)).isChecked())
			entry.publicCommunity = "YES";
		else
			entry.publicCommunity = "NO";
		// String autoAcceptUser = null ;
		if (((ToggleButton) findViewById(R.id.auto_accept)).isChecked())
			entry.autoAcceptUser = "YES";
		else
			entry.autoAcceptUser = "NO";
		entry.moderated = "NO";
	}

	private String status = "";
	private JSONObject jsonObject;
	private boolean createCommunity(final Hashtable<String, String> postParams,
			final byte[] imageData, final boolean update, final boolean public_private) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				String url = null;
				if (update) {
					url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/group/update";
				} else {
					url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/group/create";
				}
				Log.i(TAG, "createCommunity :: run() : url : "+url+", \npost params : "+postParams.toString());
				try {
					KeyValue.setBoolean(CreateCommunityActivity.this, KeyValue.CREATE_COMMUNITY, true);
					onBack = true;
					final String response = uploadPhoto(url, postParams, imageData);
					if(rtDialog != null && rtDialog.isShowing())
						rtDialog.dismiss();
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								jsonObject = new JSONObject(response);
								status = jsonObject.getString("status");
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (status.equals("error")) {
								try {
									String s = getString(R.string.comm_creat_sucess);
									if (jsonObject.getString("message") != null
											&& jsonObject.getString("message").indexOf(
													"succ") != -1)
										s = getString(R.string.comm_creat_sucess);
									else if (jsonObject.getString("message") != null && (jsonObject.getString("message").indexOf("alre") != -1 || jsonObject.getString("message").indexOf("is not available") != -1))
										s = getString(R.string.comm_name_already_taken);
									else if (jsonObject.getString("message") != null)
										s = jsonObject.getString("message");
									else
										s = getString(R.string.please_try_after_some_times);
									showValidationMessage("Error", s);
								} catch (JSONException e) {
									showValidationMessage("Error", e.toString());
								}
							} else {
								try {
									String s = getString(R.string.comm_creat_sucess);
									if (jsonObject.getString("message") != null && jsonObject.getString("message").indexOf("succ") != -1)
									{
										//{"message":"Community created successfully!!","groupId":126282,"status":"success"}
										s = getString(R.string.comm_creat_sucess);
										if(isEdit){
											if(public_private)
												s = getString(R.string.comm_update_success);
											else
												s = getString(R.string.comm_update_success);
										}
										runOnUiThread(new Runnable() {
											@SuppressLint("NewApi")
											public void run() {
												if(entry == null)
													entry = new CommunityFeed.Entry(); 
												if(entry != null){
													//http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/message/126012?textMode=base64
													//create Message URL
													String groupID = null;;
													try {
														if(jsonObject.has("groupId"))
															groupID = jsonObject.getString("groupId");
													} catch (JSONException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													entry.messageUrl = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/group/message/" + groupID + "?textMode=base64";
													try{
														if(groupID != null)
															entry.groupId = Integer.parseInt(groupID);
													}
													catch(NumberFormatException nfe)
													{
														nfe.printStackTrace();
													}
													entry.groupName = nameEditor.getText().toString();
													entry.description = descriptionEditor.getText().toString();
													if(category != null)
														entry.category = category;
													//http://"+Urls.TEJAS_HOST+"/rtMediaServer/get/1_1_1_A_I_I3_e0xgjhg8r5.jpg?height=300&width=300
													//Add local Image path - cameraImagePath

													if(cameraImagePath != null)
														entry.thumbUrl = cameraImagePath;
													if(!isEdit)
														entry.noOfMember = 0;
													if(!tagStr.equals(""))
														entry.tags = tagStr;
													entry.ownerDisplayName = SettingData.sSelf.getFirstName() + " " + SettingData.sSelf.getLastName();
													//													DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
												}

												if(isEdit)
												{
													//Check if private/public has been changed then update in DB locally
													int compare = Boolean.compare(isPublicChannelBeforeEdit, isPublicChannelAfterEdit);
													if(compare == 0){
														
													}else{
														setChannelPublic(isPublicChannelAfterEdit ? "YES" : "NO");
													}
													//@TODO - Check here if other values needs to be updated here.
													if(public_private){
														showToast(getString(R.string.comm_update_success));
													}else{
														isEdit = false;
														Intent intent = new Intent();
														intent.putExtra("EDIT", true);
														setResult(Activity.RESULT_OK, intent);
														finish();
														UIActivityManager.directFromCreateChannel = true;
														UIActivityManager.backFromUpdateChannel = true;
														showToast(getString(R.string.comm_update_success));
													}
												}
												else
												{
													if(cameraImagePath != null)
														entry.thumbUrl = cameraImagePath;
													DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
													UIActivityManager.directFromCreateChannel = true;
													Intent intent = new Intent(CreateCommunityActivity.this, KainatCommunityFallowerContact.class);
													intent.putExtra("group", true);
													intent.putExtra("GROUP_NAME", nameEditor.getText().toString());
													startActivity(intent);
													finish();
												}
												UIActivityManager.refreshChannelList = true;
											}
										});
									}
									else if (jsonObject.getString("message") != null && jsonObject.getString("message").indexOf("alre") != -1)
										s = getString(R.string.comm_name_already_taken);

								} catch (JSONException e) {
									showValidationMessage("Error", e.toString());
								}
							}
						}
					});

				} catch (final Exception e1) {
					e1.printStackTrace();
					if(rtDialog != null && rtDialog.isShowing())
						rtDialog.dismiss();
					runOnUiThread(new Runnable() {
						public void run() {
							showValidationMessage("Error", e1.toString());
							chked_status.setTextColor(Color.BLACK);
							chked_status.setText("Check Available!");
						}
					});
				}
			}
		}, 10);
		return true;
	}
//-------------------------------------------------------------------------------------------------------
	private void setChannelPublic(String value)
	{
		try
		{
			Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
					new String[] { ""+entry.groupName}, null, null, null);
			if(cursor.getCount() == 1)
			{
				ContentValues values = new ContentValues();
				values.put(CommunityTable.IS_PUBLIC, value);
				int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
						new String[] {entry.groupName});
				if(rowUpdated == 1){
					Log.v(TAG, "setChannelPublic :: updated with IS_PUBLIC : "+value);
				}
					
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
//-------------------------------------------------------------------------------------------------------
	private String uploadPhoto(String urlStr,
			Hashtable<String, String> postParams, byte[] imageData)
					throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			// This is the standard format for a multipart request
			byte messageByteArray[] = new byte[0];
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					"\r\n--".getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					BOUNDRY.getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					"\r\n".getBytes("UTF-8"));
			for (Iterator<String> iterator = postParams.keySet().iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				String value = postParams.get(key);

				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Disposition: form-data; charset=utf-8; name=\""
						.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						key.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\"\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						value.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n".getBytes("UTF-8"));
			}
			if (imageData != null) {
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Disposition: form-data; charset=utf-8; name=\"picData\""
						.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Type: application/octet-stream"
						.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						imageData);
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"--\r\n".getBytes("UTF-8"));
			}

			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; charset=utf-8; boundary=" + BOUNDRY);
			conn.addRequestProperty("RT-APP-KEY",
					"" + BusinessProxy.sSelf.getUserId());
			conn.setRequestProperty("RT-APP-KEY",
					"" + BusinessProxy.sSelf.getUserId());
			conn.addRequestProperty("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());
			conn.setRequestProperty("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());
			conn.setRequestProperty("locale", KeyValue.getString(
					CreateCommunityActivity.this, KeyValue.LANGUAGE));
			writeToFile(new String(messageByteArray));

			// Send the body
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(messageByteArray);
			dataOS.flush();
			dataOS.close();

			// Ensure we got the HTTP 200 response code
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new Exception(String.format(
						"Received the response code %d from the URL %s",
						responseCode, url));
			}

			// Read the response
			is = conn.getInputStream();
			return IOUtils.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					// do nothing
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private static void writeToFile(String stacktrace) {
//		File file = new File("/sdcard/comm_post.txt");
//		if (!file.exists()) {
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
//					true));
//			writer.write(stacktrace);
//			writer.flush();
//			writer.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private boolean checkAvailability(final String communityName,
			final TextView v) {
		if (communityName == null || communityName.equals("")) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			chked_status.setTextColor(Color.BLACK);
			chked_status.setText("");
			return false;
		}
		chked_status.setTextColor(Color.BLACK);
		chked_status.setText("Checking...");
		new Timer().schedule(new TimerTask() {
			public void run() {
				HttpConnectionHelper helper = new HttpConnectionHelper(
						"http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/api/group/checkavailability?"
						+ "groupName="
						+ URLEncoder.encode(communityName));
				helper.setRequestMethod(HttpGet.METHOD_NAME);
				helper.setHeader("RT-APP-KEY",
						"" + BusinessProxy.sSelf.getUserId());
				helper.setHeader("RT-DEV-KEY",
						"" + BusinessProxy.sSelf.getClientId());
				if (Logger.ENABLE)
					Logger.debug("RT-APP-KEY " + " RT-DEV-KEY",
							+BusinessProxy.sSelf.getUserId() + " , "
									+ BusinessProxy.sSelf.getClientId());
				helper.setConnectTimeout(30);
				helper.setReadTimeout(120);
				int responseCode = 0;
				try {
					responseCode = helper.getResponseCode();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (Logger.ENABLE)
					Logger.info(TAG, "--run()--[INFO] - ResponseCode = "
							+ responseCode);
				switch (responseCode) {
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_ACCEPTED:
					InputStream inputStream = null;
					try {
						inputStream = helper.getInputStream();
					} catch (IOException e) {
						e.printStackTrace();
					}
					int CHUNK_LENGTH = 32 * 1024;
					ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
					byte[] chunk = new byte[CHUNK_LENGTH];
					int len;
					try {
						while (-1 != (len = inputStream.read(chunk))) {
							buffer.append(chunk, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					byte[] mResponseData = buffer.toByteArray();
					mResponseData.toString();
					String responseData = new String(mResponseData);
					try {
						JSONObject jsonObject = new JSONObject(responseData);// jsonArray.getJSONObject(i);
						final String status = jsonObject.getString("status");
						if (Logger.ENABLE)
							Logger.debug("status", status);
						runOnUiThread(new Runnable() {
							public void run() {
								if (status.equals("error")) {
									chked_status.setTextColor(Color.RED);
									chked_status.setText("Not Available!");
								} else {
									chked_status.setTextColor(Color.BLUE);
									chked_status.setText("Available!");
								}
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					runOnUiThread(new Runnable() {
						public void run() {
							chked_status.setTextColor(Color.BLACK);
							chked_status.setText("Retry Check!!!");
						}
					});
				}
			}
		}, 10);
		return true;
	}

//	public void onBackPressed() {
//		if (onBackPressedCheck())
//			return;
//		DataModel.sSelf.removeObject(DMKeys.ENTRY);
//		finish();
//		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
//	}
	
	public void onBackPressed() {
		  if (onBackPressedCheck())
		   return;
		  //  DataModel.sSelf.storeObject(DMKeys.ENTRY, entry);
		  String str = minTagField.getText().toString();
		  String str2 = null;
		  if(entry != null){
			  byte[] messageText = Base64.decode(entry.description, Base64.DEFAULT);
			  String val=null;
			  try {
			    val = new String(messageText, "utf-8");
			  } catch (UnsupportedEncodingException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
			  }
			  str2 = entry.tags;
			  str2 = str2.replaceAll(",", " ");
			  if(!(entry.groupName.equals(nameEditor.getText().toString()))  
					  || !(catagoryField.getText().toString().equals(entry.category))  
					  || !(descriptionEditor.getText().toString().equals(val)) 
					  || (cameraImagePath!=null) || !(str.equals(str2))){
			   //nameEditor.setText(entry.groupName);
			   AlertDialog.Builder builder = new AlertDialog.Builder(this);
			   builder.setMessage(R.string.save_unsaved)
			      .setCancelable(false)
			      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			          public void onClick(DialogInterface dialog, int id) {
			           editButton.performClick();
			           //dialog.cancel();
			          }
			      }).setNegativeButton("No", new DialogInterface.OnClickListener() {
			          public void onClick(DialogInterface dialog, int id) {
			               CreateCommunityActivity.this.finish();
			               DataModel.sSelf.removeObject(DMKeys.ENTRY);
			               overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);  
			          }
			      });
			   AlertDialog alert = builder.create();
			   alert.show();
		   }else
			{
			  DataModel.sSelf.removeObject(DMKeys.ENTRY);
			  finish();
			  overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);  
			}
		 }else
		  {
			  DataModel.sSelf.removeObject(DMKeys.ENTRY);
			  finish();
			  overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);  
		  }
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			pushNewScreen(KainatHomeActivity.class, false);
			break;
		}
		return true;
	}

	public void notificationFromTransport(ResponseObject response) {
		if (Logger.ENABLE)
			Logger.verbose(
					TAG,
					"notificationFromTransport-- Response Code= "
							+ response.getResponseCode() + "  Sent Op= "
							+ response.getSentOp());
		if (response.getError() != Constants.ERROR_NONE) {
			return;
		}
	}

	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		if (MODE_NORMAL != mode) {
			return;
		}
	}

	private long totalSize = 0;

	private boolean isSizeReachedMaximum(Bitmap bm) {
		long newSize = 0;
		if (null != bm) {
			newSize = ImageUtils.getImageSize(bm);
		}
		if ((totalSize + newSize) > Constants.COMPOSE_MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	public String getPath(Uri uri) {
		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			Cursor cursor = getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, null);

			// String[] projection = { MediaStore.Images.Media.DATA };
			// Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// cursor.moveToFirst();
			String p = null;
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int dataColumnIndex = cursor
						.getColumnIndex(MediaStore.Images.Media.DATA);
				int id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
				// System.out.println(cursor.getString(dataColumnIndex));
				// System.out.println("--ID:---"+cursor.getString(id));
				if (uri.getPath().indexOf(cursor.getString(id)) != -1) {
					// System.out.println("----GOT");
					p = cursor.getString(dataColumnIndex);
				}
			}
			// cursor.getString(column_index);
			// cursor.close();
			// if(p==null)
			// p = uri.getPath();

			return p;
		} catch (Exception e) {
			return uri.getPath();
		}
	}

	// public String getPath(Uri uri) {
	// String[] projection = { MediaStore.Images.Media.DATA };
	// Cursor cursor = managedQuery(uri, projection, null, null, null);
	// int column_index =
	// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// cursor.moveToFirst();
	// return cursor.getString(column_index);
	// }

	String currentSelected;
	boolean onBack = false;

	private void showValidationMessage(String title, String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				CreateCommunityActivity.this);
		alertDialogBuilder.setTitle(getString(R.string.app_name));
		alertDialogBuilder
		.setMessage(message)
		.setCancelable(true)
		.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void showDailog(final View v, String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				CreateCommunityActivity.this);

		// set title
		alertDialogBuilder.setTitle("");

		// set dialog message
		alertDialogBuilder
		.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				dialog.cancel();
				if(isEdit){
					String isPublic = null;
					String groupName = nameEditor.getText().toString();
					if (((ToggleButton) findViewById(R.id.public_id)).isChecked())
						isPublic = "YES";
					else
						isPublic = "NO";
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("groupName", groupName);
					params.put("isPublic", isPublic);
					isPublicChannelAfterEdit = !isPublicChannelBeforeEdit;
					createCommunity(params, null, true, true);
				}
			}
		})
		.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				((ToggleButton) findViewById(R.id.public_id)).setChecked(true);
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	public void onClick01(View v) {
		Intent intent = new Intent(CreateCommunityActivity.this,
				TokenDummyActivity.class);
		startActivity(intent);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.web_back_button:
			// popScreen();
			finish();
			break;
		case R.id.terms_condition_link:
			Intent intent = new Intent(CreateCommunityActivity.this,
					CommunityWebViewActivity.class);
			startActivity(intent);
			// pushNewScreen(CommunityWebViewActivity.class, false);
			break;
			// case R.id.cancelButton:
			// dialog.dismiss();
			// break;
		case R.id.public_id:
			boolean isChecked = ((ToggleButton) v).isChecked();
			if(isChecked) {
				if(isEdit){
					String isPublic = null;
					String groupName = nameEditor.getText().toString();
					if (((ToggleButton) findViewById(R.id.public_id)).isChecked())
						isPublic = "YES";
					else
						isPublic = "NO";
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("groupName", groupName);
					params.put("isPublic", isPublic);
					isPublicChannelAfterEdit = !isPublicChannelBeforeEdit;
					createCommunity(params, null, true, true);
				}
			}
			else{
				showDailog(v, getString(R.string.community_create_private_str));
			}
			break;
		case R.id.update_community_submit: {
			// minTagField.getText() gives -- > googlevoice myvoice ourvoice letspostvoice nicetags beautiful
			// required -- > googlevoice,myvoice,ourvoice,letspostvoice,nicetags,beautiful
			//Note that tagStr = ""; - this is must condition
			tagStr = "";
			String tags = minTagField.getText().toString().trim();
			if(tags.indexOf(' ') != -1)
				tags = tags.replace(' ', ',');
			String[] tagArray = chipsAdapter.getTags();
			if(tagArray != null){
				for(int b = 0; b < tagArray.length; b++){
					tagStr = tagStr + tagArray[b]+",";
				}
				tagStr = tagStr.substring(0, tagStr.length() - 1);
			}
			else{
				//Safe case to handle 
				tagStr = tags;
			}
			if(tagStr.trim().length() <= 0 || tagStr.equals("")){
//				showValidationMessage(getString(R.string.app_name), getString(R.string.please_enter_min_tag));
				AppUtil.showTost(CreateCommunityActivity.this, getString(R.string.please_enter_min_tag));
				return;
			}
			if (descriptionEditor.getText().toString() == null
					|| descriptionEditor.getText().toString().trim().equals("")
					|| descriptionEditor.getText().toString().trim().length() <= 0) 
			{
				AppUtil.showTost(CreateCommunityActivity.this, getString(R.string.please_enter_description)) ;
				return;
			}
			if (((Spinner) findViewById(R.id.cmunty_ctgry_nam_id))
					.getSelectedItemPosition()==0) 
			{
				AppUtil.showTost(CreateCommunityActivity.this, getString(R.string.select_category)) ;
				return;
			}
			rtDialog = ProgressDialog.show(CreateCommunityActivity.this, "", getString(R.string.please_wait_while_loadin), true);
			String groupName = nameEditor.getText().toString();
			if(groupName.equals(""))
				groupName = entry.groupName;
			String description = descriptionEditor.getText().toString();
			category = String.valueOf(((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).getSelectedItem());
			// String isPublic = ((ToggleButton)
			// findViewById(R.id.public_id)).getText().toString().equals("ON") ?
			// "YES" : "NO"; // Id @+id/public_id
			// String autoAcceptUser = ((ToggleButton)
			// findViewById(R.id.auto_accept)).getText().toString().equals("ON")
			// ? "YES" : "NO"; // Id @+id/auto_accept
			// String isModerated = ((ToggleButton)
			// findViewById(R.id.moderation_toggle)).getText().toString().equals("ON")
			// ? "YES" : "NO"; //"NO";

			String isPublic = null;//
			if (((ToggleButton) findViewById(R.id.public_id)).isChecked())
				isPublic = "YES";
			else
				isPublic = "NO";
			String autoAcceptUser = "YES";

			//			if (((ToggleButton) findViewById(R.id.auto_accept)).isChecked())
			//				autoAcceptUser = "YES";
			//			else
			//				autoAcceptUser = "NO";

			String isModerated = "NO";

			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("groupName", groupName);
//			if (!description.equalsIgnoreCase(entry.description)) {
//				params.put("description", description);
//			}
			if (!description.equalsIgnoreCase(channelDescBeforeEdit)) {
				params.put("description", description);
			}
			if (entry!= null && entry.category != null && (!category.equalsIgnoreCase(entry.category))) {
				params.put("category", category);
			}
			if (entry!= null && entry.moderated != null && (!isModerated.equalsIgnoreCase(entry.moderated))) {
				params.put("isModerated", isModerated);
			}
			if (entry!= null && entry.autoAcceptUser != null && (!autoAcceptUser.equalsIgnoreCase(entry.autoAcceptUser))) {
				params.put("autoAcceptUser", autoAcceptUser);
			}
			if (entry!= null && entry.publicCommunity != null && (!isPublic.equalsIgnoreCase(entry.publicCommunity))) {
				params.put("isPublic", isPublic);
				isPublicChannelAfterEdit = !isPublicChannelBeforeEdit;
			}
//			if (!tagStr.equalsIgnoreCase(entry.tags)) 
			{
				if(tagStr.indexOf('#') != -1)
					tagStr = tagStr.replace("#", "");
				params.put("tags", tagStr);
			}
			byte[] imageBinaryData = null;
			//			if (communityPictureDirty) 
			{
				//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				//				Drawable drawable = attachImg.getDrawable();
				//				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				//				bitmap.compress(Bitmap.Constants.COMPRESS_TYPE, Constants.COMPRESS,
				//						stream);
				//				imageBinaryData = stream.toByteArray();
				//				try {
				//					stream.flush();
				//					stream.close();
				//				} catch (IOException e) {
				//					e.printStackTrace();
				//				}
				imageBinaryData = Utilities.getFileData(cameraImagePath);
			}

			createCommunity(params, imageBinaryData, true, false);
		}
		return;
		case R.id.create_bt:
			if(isEdit)
			{
				editButton.performClick();
				return;
			}
			if (((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).getSelectedItemPosition()==0){
				showValidationMessage(getString(R.string.app_name), getString(R.string.select_category));
				return;
			}
			if (nameEditor.getText().toString() == null
					|| nameEditor.getText().toString().trim().equals("")) {
				//				showSimpleAlert(getString(R.string.app_name),
				//						getString(R.string.please_enter_a_community_name));// dialog.show();
				showValidationMessage(getString(R.string.app_name), getString(R.string.please_enter_a_community_name));
				return;
			}
			/*if(!SUBJECT_PATTERN.matcher(nameEditor.getText().toString()).matches()){
				showValidationMessage(getString(R.string.please_enter_a_community_name));
				//showSimpleAlert(getString(R.string.app_name), getString(R.string.please_enter_a_community_name));// dialog.show();
				return;
			}*/
			//tagStr = getTotalTags();
			String[] tagArray = chipsAdapter.getTags();
			tagStr = "";

			if(tagArray != null)
			{
				if(tagArray.length > 10)
				{
					Toast.makeText(this, getString(R.string.total_tag_count), Toast.LENGTH_SHORT).show();
					return;
				}
				for(int b = 0; b < tagArray.length; b++)
				{
					if(tagArray[b].length() > 15)
					{
						Toast.makeText(this, getString(R.string.single_tag_length_alert), Toast.LENGTH_SHORT).show();
						return;
					}
					tagStr = tagStr + tagArray[b]+",";
				}
				tagStr = tagStr.substring(0, tagStr.length() - 1);
			}
			if(tagStr.length() <= 0 || tagStr.equals(""))
			{
				Log.i(TAG, "Push Tag on Server = "+tagStr);
				showValidationMessage(getString(R.string.app_name), getString(R.string.please_enter_min_tag));
				return;
			}


			if (descriptionEditor.getText().toString() == null
					|| descriptionEditor.getText().toString().trim().equals("")
					|| descriptionEditor.getText().toString().trim().length() <= 0
					) {
				showValidationMessage(getString(R.string.app_name), getString(R.string.please_enter_description));
				return;
			}
			rtDialog = ProgressDialog.show(CreateCommunityActivity.this, "", getString(R.string.please_wait_while_loadin), true);


			String groupName = nameEditor.getText().toString().trim();
			String description = descriptionEditor.getText().toString();
			category = String.valueOf(((Spinner) findViewById(R.id.cmunty_ctgry_nam_id)).getSelectedItem());
			// String isPublic = ((ToggleButton)
			// findViewById(R.id.public_id)).getText().toString().equals("ON")
			// ? "YES" : "NO"; // Id @+id/public_id

			String isPublic = null;//
			if (((ToggleButton) findViewById(R.id.public_id)).isChecked())
				isPublic = "YES";
			else
				isPublic = "NO";

			String autoAcceptUser = "YES";

			//			if (((ToggleButton) findViewById(R.id.auto_accept)).isChecked())
			//				autoAcceptUser = "YES";
			//			else
			//				autoAcceptUser = "NO";

			String isModerated = "NO";

			// String autoAcceptUser = ((ToggleButton)
			// findViewById(R.id.auto_accept)).getText().toString().equals("ON")
			// ? "YES" : "NO"; // Id @+id/auto_accept
			// String isModerated = "NO";//((ToggleButton)
			// findViewById(R.id.moderation_toggle)).getText().toString().equals("ON")
			// ? "YES" : "NO"; //"NO";
			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("groupName", groupName);
			params.put("description", description);
			params.put("category", category);
			params.put("isModerated", isModerated);
			params.put("autoAcceptUser", autoAcceptUser);
			params.put("isPublic", isPublic);

			if(tagStr.indexOf('#') != -1)
				tagStr = tagStr.replace("#", "");
			params.put("tags", tagStr);
			byte[] imageBinaryData = null;
			//				if (communityPictureDirty) 
			{
				//					ByteArrayOutputStream stream = new ByteArrayOutputStream();
				//					Drawable drawable = attachImg.getDrawable();
				//					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				//					bitmap.compress(Bitmap.Constants.COMPRESS_TYPE, 100, stream);
				//					imageBinaryData = stream.toByteArray();
				//					try {
				//						stream.flush();
				//						stream.close();
				//					} catch (IOException e) {
				//						e.printStackTrace();
				//					}
				imageBinaryData = Utilities.getFileData(cameraImagePath);
			}
			if(isEdit)
				createCommunity(params, imageBinaryData, true, false);
			else
				createCommunity(params, imageBinaryData, false, false);

			//				return;
			//			showSimpleAlert(getString(R.string.app_name),
			//					getString(R.string.please_enter_community_name));// dialog.show();
			break;
		case R.id.terms_condition_id:
			if (termsChk.isChecked()) {
				nextText.setVisibility(View.VISIBLE);
			} else {
				nextText.setVisibility(View.GONE);
			}
			break;
		case R.id.home_icon:
			if (BusinessProxy.sSelf.mCacheURLs.size() > 0)
				BusinessProxy.sSelf.mCacheURLs.removeAllElements();
			pushNewScreen(KainatHomeActivity.class, false);
			break;
		case R.id.attach_photo_id:

			if (cameraImagePath != null) {
				HashMap menuItems = new HashMap<Integer, String>();
				menuItems.put(0, getString(R.string.preview));

				menuItems.put(1, getString(R.string.clear));
				menuItems.put(2, getString(R.string.cancel));
				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {

						// int id = COMPOSE_IMAGES_IDS_START - view.getId();
						// id = Math.abs(id);
						String path = cameraImagePath;// "";//
						// mImagesPath.get(id);
						// if (id >= mImagesPath.size())
						// path = mImagesPath.get(mImagesPath.size() - 1);
						// else
						// path = mImagesPath.get(id);
						closeActionSheet();
						switch (selection) {
						case 0:
							// saveMessageContents();
							Intent intent = new Intent(
									CreateCommunityActivity.this,
									MultiPhotoViewer.class);
							// intent.setAction(Intent.ACTION_VIEW);
							// intent.setDataAndType(Uri.fromFile(new
							// File(path)), "image/*");
							ArrayList<String> mImagesPath = new ArrayList<String>();
							mImagesPath.add(path);
							intent.putStringArrayListExtra("DATA",
									(ArrayList<String>) mImagesPath);
							startActivity(intent);
							closeActionSheet();
							break;
						case 1:
							try{
								communityPictureDirty = false;
								cameraImagePath = null;
								coverImgBg.setImageDrawable(null);
								totalSize = 0;
							}catch (Exception e) {
								e.printStackTrace() ;
							}
							closeActionSheet();
							//attachImg.setImageResource(R.drawable.attachicon);
							coverImgBg.setBackgroundResource(R.drawable.com_channel_bg);
							break;
						default:
							closeActionSheet();
							break;
						}

					}
				}, null);
				return;
			}

			mode = MODE_ATTACH_PHOTO;
			final Dialog dialogVideo = new Dialog(CreateCommunityActivity.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialogVideo.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogVideo.setContentView(R.layout.open_choice_dialog_new);
			dialogVideo.setCancelable(true);

			TextView text1 = (TextView) dialogVideo
					.findViewById(R.open_choice_create.message);
			text1.setText("");// String.format("Max count: 20 within max size: 4 MB. Current attached size: %s MB",
			// (totalSize / 1024.0 / 1024.0)));

			// TextView text1 = (TextView)
			// dialogVideo.findViewById(R.open_choice.message);
			// text1.setText(msg);

			Button button1 = (Button) dialogVideo
					.findViewById(R.open_choice_create.button1);
			button1.setText(getString(R.string.choose_from_library));
			button1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialogVideo.dismiss();
					if (!communityPictureDirty) {
						//						Intent intent = new Intent();
						//						intent.setType("image/*");
						//						intent.setAction(Intent.ACTION_GET_CONTENT);
						//						startActivityForResult(Intent.createChooser(intent,
						//								getString(R.string.select_picture)),
						//								POSITION_PICTURE);
						AppUtil.openImageGallery(CreateCommunityActivity.this,
								AppUtil.POSITION_GALLRY_PICTURE);
					} else {
						if (cameraImagePath != null) {
							Intent intent = new Intent(
									CreateCommunityActivity.this,
									MultiPhotoViewer.class);
							ArrayList<String> imlp = new ArrayList<String>();
							imlp.add(cameraImagePath);
							intent.putStringArrayListExtra("DATA", imlp);
							startActivity(intent);
						}
					}
				}

			});

			Button button2 = (Button) dialogVideo
					.findViewById(R.open_choice_create.button2);
			button2.setText(getString(R.string.take_photo));
			button2.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					dialogVideo.dismiss();
					if (!communityPictureDirty) {
						openCamera();
					} else {
						communityPictureDirty = false;
						runOnUiThread(new Runnable() {
							public void run() {
								cameraImagePath = null;

							}
						});
					}
				}
			});
			Button cancelButton = (Button) dialogVideo
					.findViewById(R.open_choice_create.cancelButton);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialogVideo.dismiss();
					// openCameraForVedioRecording();
				}
			});
			dialogVideo.show();
			// dialog.show();
			break;
		}
	}

	public void onThumbnailResponse(final ThumbnailImage value, byte[] data) {
	}

	public void voicePlayCompleted() {
	}

	public void voicePlayStarted() {
	}

	private void openCamera() {

		AppUtil.openCamera(this, AppUtil.capturedPath1,
				AppUtil.POSITION_CAMERA_PICTURE);

		//		cameraImagePath = null;
		//		File file = new File(Environment.getExternalStorageDirectory(),
		//				getRandomNumber() + ".jpg");
		//		cameraImagePath = file.getPath();
		//		Uri outputFileUri = Uri.fromFile(file);
		//		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		//		i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		//		this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
	}



	boolean imageSet;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {

			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case AppUtil.PIC_CROP:
					imageSet = true;
					String filePath= Environment.getExternalStorageDirectory() +"/"+Constants.contentTemp+"/"+AppUtil.TEMP_PHOTO_FILE;
					cameraImagePath = AppUtil.capturedPath1 = filePath ;                   
					coverImgBg.setImageURI(Uri.parse(cameraImagePath));
					break;
				case AppUtil.POSITION_CAMERA_PICTURE:
				case AppUtil.POSITION_GALLRY_PICTURE:
					imageSet = true;
					if (data != null && data.getData() != null) {
						Uri uri = data.getData();
						AppUtil.capturedPath1 = AppUtil.getPath(uri, CreateCommunityActivity.this);
						BitmapFactory.Options o = new BitmapFactory.Options();
						o.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(getAbsolutePath(uri), o);
						int imageHeight = o.outHeight;
						int imageWidth = o.outWidth;
						if (imageHeight < Constants.CROP_DIMEN && imageWidth < Constants.CROP_DIMEN) 
						{
							cameraImagePath = AppUtil.capturedPath1;                   
						}
						else
						{
							CompressImage compressImage = new CompressImage(mContext);
							cameraImagePath = AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
						}
					}
					if(requestCode == AppUtil.POSITION_CAMERA_PICTURE)
						performCrop(AppUtil.PIC_CROP);
					else{
						coverImgBg.setImageURI(Uri.parse(cameraImagePath));
					}
					//					performCrop(AppUtil.PIC_CROP);
					break;
				}
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
			System.out.println("----AppUtil.capturedPath1:"
					+ AppUtil.capturedPath1);

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
			System.out.println("----outputFileUri:" + outputFileUri);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("crop", "true");
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("outputX", Constants.CROP_DIMEN);
			cropIntent.putExtra("outputY", Constants.CROP_DIMEN);
			//			cropIntent.putExtra("outputFormat", Constants.COMPRESS_TYPE.toString());
			cropIntent.putExtra("scale", true);
			try {
				cropIntent.putExtra("return-data", false);
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
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

	private void performCropXXX(byte resultCode) {
		try {
			System.out.println("----AppUtil.capturedPath1:"
					+ AppUtil.capturedPath1);

			cameraImagePath = AppUtil.capturedPath1;

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
			System.out.println("----outputFileUri:" + outputFileUri);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("crop", "true");
			//			cropIntent.putExtra("aspectX", 1);
			//			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("outputX", Constants.CROP_DIMEN);
			cropIntent.putExtra("outputY", Constants.CROP_DIMEN);
			// cropIntent.putExtra("scale", true);
			try {
				// cropIntent.putExtra("return-data", true);
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
				startActivityForResult(cropIntent, resultCode);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	protected void onActivityResultXXX(int requestCode, int resultCode,
			Intent data) {
		try {
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case POSITION_CAMERA_PICTURE:
					communityPictureDirty = true;
					Bitmap bm = null;
					Thread.sleep(500);
					CompressImage compressImage = new CompressImage(
							CreateCommunityActivity.this);
					cameraImagePath = compressImage
							.compressImage(cameraImagePath);

					if ((frontCam && rearCam) || (!frontCam && rearCam)) {
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither = false; // Disable Dithering mode
						bfo.inPurgeable = true;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
						// bm =ImageUtils.rotate(bm,90,cameraImagePath);
						// bm=ImageUtils.imageRotateafterClick(bm,cameraImagePath);
						if (isSizeReachedMaximum(bm)) {
							showSimpleAlert(getString(R.string.info),
									getString(R.string.max_attachment_reached));

							bfo = new BitmapFactory.Options();
							bfo.inSampleSize = Constants.IN_SAMPLE_SIZE;
							bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
							// bm = Bitmap.createScaledBitmap(bm, 75, 75, true);
							attachImg.setImageBitmap(bm);

							return;
						}
						// bm = Bitmap.createScaledBitmap(bm, 75, 75, true);
						attachImg.setImageBitmap(bm);
					}
					if (frontCam && !rearCam) {
						bm = ImageUtils.getImageFor(cameraImagePath, 4);
						if (isSizeReachedMaximum(bm)) {
							showSimpleAlert(getString(R.string.info),
									getString(R.string.max_attachment_reached));
							BitmapFactory.Options bfo = new BitmapFactory.Options();
							bfo.inSampleSize = Constants.IN_SAMPLE_SIZE;
							bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
							// bm = Bitmap.createScaledBitmap(bm, 75, 75, true);
							attachImg.setImageBitmap(bm);
							return;
						}
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inSampleSize = 4;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
						// bm = Bitmap.createScaledBitmap(bm, 75, 75, true);
						attachImg.setImageBitmap(bm);
					}
					break;
				case POSITION_PICTURE:
					communityPictureDirty = true;
					if (Logger.ENABLE)
						Logger.debug("POSITION_PICTURE",
								"onActivityResult - POSITION_PICTURE");
					String capturedPath = getPath(data.getData());
					cameraImagePath = capturedPath;
					Bitmap bm1 = ImageUtils.getImageFor(capturedPath, 4);
					BitmapFactory.Options bfo = new BitmapFactory.Options();
					bfo.inSampleSize = 4;// Constants.IN_SAMPLE_SIZE;
					bm1 = BitmapFactory.decodeFile(capturedPath, bfo);
					// bm1 = Bitmap.createScaledBitmap(bm1, 75, 75,
					// true);//kainat
					attachImg.setImageBitmap(bm1);
					break;
				}
			} else {
				File f = new File(cameraImagePath);
				if (!f.exists())
					cameraImagePath = null;
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG,
						"Error getting size for path - " + ex.toString(), ex);
		} catch (OutOfMemoryError ex) {
			if (Logger.ENABLE)
				Logger.error(TAG,
						"Error getting size for path - " + ex.toString(), ex);
		}
	}

	byte[] getFileData(String filePath) throws IOException {
		FileInputStream fin = new FileInputStream(filePath);
		byte[] data = new byte[fin.available()];
		fin.read(data, 0, data.length);
		return data;
	}

	public class MyCustomDialog extends Dialog implements OnItemClickListener {
		Context context;

		public MyCustomDialog(Context context) {
			super(context);
			this.context = context;
		}

		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			refreshList();
		}

		public void refreshList() {
			switch (mode) {
			case MODE_ATTACH_PHOTO:
				/*
				 * setTitle("Select Profile Photo:");
				 * setContentView(R.layout.open_choice_dialog_new
				 * );//community_profile_photo_list_screen); ListView lst =
				 * (ListView) findViewById(R.id.open_choice_list_id); String
				 * valuess[] = getResources().getStringArray(R.array.
				 * community_profile_photo_options_delete) ;
				 * if(!communityPictureDirty) valuess =
				 * getResources().getStringArray
				 * (R.array.community_profile_photo_options) ;
				 * 
				 * ArrayAdapter<String> adapter = new
				 * ArrayAdapter<String>(CreateCommunityActivity.this,
				 * android.R.layout.simple_list_item_1, android.R.id.text1,
				 * valuess); lst.setAdapter(adapter);
				 * lst.setOnItemClickListener(this); Button cancelButton =
				 * (Button)
				 * dialog.findViewById(R.open_choice_create.cancelButton);
				 * cancelButton
				 * .setOnClickListener((android.view.View.OnClickListener)
				 * context);
				 */

				/*
				 * builder.setItems(new String[] { "Attach from file system",
				 * "Click using camera" }, new DialogInterface.OnClickListener()
				 * { public void onClick(DialogInterface dialog, int which) {
				 * switch (which) { case 0:
				 * openThumbnailsToAttach(POSITION_PICTURE); break; case 1:
				 * dialog.dismiss(); openCamera(); break; } dialog.dismiss(); }
				 * }); builder.setNegativeButton("Cancel", null);
				 * builder.create(); builder.show();
				 */

				final Dialog dialogVideo = new Dialog(
						CreateCommunityActivity.this,
						android.R.style.Theme_Translucent_NoTitleBar);
				dialogVideo.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogVideo.setContentView(R.layout.open_choice_dialog_new);
				dialogVideo.setCancelable(true);

				TextView text1 = (TextView) dialogVideo
						.findViewById(R.open_choice_create.message);
				text1.setText("");// String.format("Max count: 20 within max size: 4 MB. Current attached size: %s MB",
				// (totalSize / 1024.0 / 1024.0)));

				// TextView text1 = (TextView)
				// dialogVideo.findViewById(R.open_choice.message);
				// text1.setText(msg);

				Button button1 = (Button) dialogVideo
						.findViewById(R.open_choice_create.button1);
				button1.setText(getString(R.string.choose_from_library));
				button1.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						dialogVideo.dismiss();
						if (!communityPictureDirty) {
							//							Intent intent = new Intent();
							//							intent.setType("image/*");
							//							intent.setAction(Intent.ACTION_GET_CONTENT);
							//							startActivityForResult(Intent.createChooser(intent,
							//									getString(R.string.select_picture)),
							//									POSITION_PICTURE);
							AppUtil.openImageGallery(CreateCommunityActivity.this,
									AppUtil.POSITION_GALLRY_PICTURE);
						} else {
							if (cameraImagePath != null) {
								Intent intent = new Intent(
										CreateCommunityActivity.this,
										MultiPhotoViewer.class);
								ArrayList<String> imlp = new ArrayList<String>();
								imlp.add(cameraImagePath);
								intent.putStringArrayListExtra("DATA", imlp);
								startActivity(intent);
							}
						}
					}

				});

				Button button2 = (Button) dialogVideo
						.findViewById(R.open_choice_create.button2);
				button2.setText(getString(R.string.take_photo));
				button2.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						dialogVideo.dismiss();
						if (!communityPictureDirty) {
							openCamera();
						} else {
							communityPictureDirty = false;
							runOnUiThread(new Runnable() {
								public void run() {
									cameraImagePath = null;
									attachImg
									.setBackgroundResource(R.drawable.ic_menu_camera);
									attachImg
									.setImageResource(R.drawable.ic_menu_camera);
								}
							});
						}
					}
				});
				Button cancelButton = (Button) dialogVideo
						.findViewById(R.open_choice_create.cancelButton);
				cancelButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						dialogVideo.dismiss();
						// openCameraForVedioRecording();
					}
				});
				dialogVideo.show();

				break;
			}
		}

		public void onStart() {
			refreshList();
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			switch (mode) {
			case MODE_ATTACH_PHOTO:
				dismiss();
				switch (position) {
				case 0: // browse photo
					//					if (!communityPictureDirty) {
					//						Intent intent = new Intent();
					//						intent.setType("image/*");
					//						intent.setAction(Intent.ACTION_GET_CONTENT);
					//						startActivityForResult(Intent.createChooser(intent,
					//								getString(R.string.select_picture)),
					//								POSITION_PICTURE);
					//					} else {
					//						if (cameraImagePath != null) {
					//							Intent intent = new Intent(
					//									CreateCommunityActivity.this,
					//									MultiPhotoViewer.class);
					//							ArrayList<String> imlp = new ArrayList<String>();
					//							imlp.add(cameraImagePath);
					//							intent.putStringArrayListExtra("DATA", imlp);
					//							startActivity(intent);
					//						}
					//					}
					AppUtil.openImageGallery(CreateCommunityActivity.this,
							AppUtil.POSITION_GALLRY_PICTURE);
					break;
				case 1: // camera photo
					if (!communityPictureDirty) {
						openCamera();
					} else {
						communityPictureDirty = false;
						runOnUiThread(new Runnable() {
							public void run() {
								cameraImagePath = null;
								attachImg
								.setBackgroundResource(R.drawable.ic_menu_camera);
								attachImg
								.setImageResource(R.drawable.ic_menu_camera);
							}
						});
					}
					break;
				}
				break;
			}
		}
	}

	public void cancelAlert() {
		saveLocalData();
		DataModel.sSelf.removeObject(DMKeys.ENTRY);
		Intent intent = new Intent(CreateCommunityActivity.this, KainatInviteActivity.class);
		startActivity(intent);
		//		// finish();
		//		runOnUiThread(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				if (onBack)
		//					onBackPressed();
		//			}
		//		});
	}

	public void onTaskCallback(Object parameter, byte req) {

	}
}