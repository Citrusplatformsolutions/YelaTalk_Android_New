package com.kainat.app.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.model.ImageList;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VoiceMedia;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KainatMultiImageSelection extends UIActivityManager{
	KainatMultiImageSelection mContext;
	private ArrayList<ImageList> imageUrls;
	private ArrayList<ImageList> imageUrls_autoselect;
	private DisplayImageOptions options;
	private ImageAdapter imageAdapter;
	TextView media_screenTitle_text;
	int count = 0;
	private List<Integer> mImagesPathID = new ArrayList<Integer>();
	private List<Integer> positionselected = new ArrayList<Integer>();
	private int maxImageToAttached = -1 ,cameraShow = -1;
	private LinearLayout captionBottomLayout;
	private ImageView headerCloseIv;
	private ImageView headerCameraIv;
	private ImageView voiceBt;
	private Button cancelBt;

	private int progress = 0;
	final public static byte UI_STATE_IDLE = 2;
	private int iCurrentState = UI_STATE_IDLE;
	final public static byte UI_STATE_RECORDING = 5;
	final public static byte UI_STATE_PLAYING = 12;
	static boolean wheelRunning;

	AudioProgressWheel wheelProgress;
	private String cameraImagePath = null;
	private static final byte POSITION_CAMERA_PICTURE 		= 1;
	private VoiceMedia mVoiceMedia;
	private String mRecordedVoicePath;
	private final byte RECORDING_MODE = 1;
	private final byte PLAY_MODE = 2;
	public static String TAG = KainatMultiImageSelection.class.getSimpleName();
	private Dialog dialog = null;
	private TextView leftTimeView ;
	private boolean isTimeOver = false;
	private boolean isAudioVisible = false;
	ImageLoader thumbImageLoader = ImageLoader.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = KainatMultiImageSelection.this;
		setContentView(R.layout.ac_image_grid);
		media_screenTitle_text = (TextView)findViewById(R.id.media_screenTitle_text);
		//Bundle bundle = getIntent().getExtras();
		//imageUrls = bundle.getStringArray(Extra.IMAGES);
		maxImageToAttached = getIntent().getIntExtra("MAX", Constants.MAX_IMAGE_SIZE) ;
		cameraShow = getIntent().getIntExtra("CAMERASHOW", -1) ;

		/*gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImageGalleryActivity(position);
			}
		});*/

		isAudioVisible = getIntent().getExtras().getBoolean("AUDIO_VISIBLITY", true);

		captionBottomLayout = (LinearLayout) findViewById(R.id.caption_layout);
		headerCloseIv = (ImageView) findViewById(R.id.header_close_bt);
		voiceBt = (ImageView) findViewById(R.id.voice_bt);
		if(!isAudioVisible)
			voiceBt.setVisibility(View.GONE);	

			headerCameraIv = (ImageView) findViewById(R.id.camera_bt);
			if(cameraShow == 2){
				headerCameraIv.setVisibility(View.GONE);
				((Button)findViewById(R.id.button1)).setText(getString(R.string.done));
			}
			cancelBt = (Button) findViewById(R.id.button2);
			headerCloseIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});
			cancelBt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});
			headerCameraIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					caputureImage();
				}
			});
			voiceBt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mRecordedVoicePath == null){
						showAudioDialog(RECORDING_MODE);
					}else{
						showAudioDialog(PLAY_MODE);
					}
				}
			});
			
		//Load Images Here - 
			loadImageFromGallery();
			int sint[] = (int[])DataModel.sSelf.removeObject("SELECTED_IMAGES");
			if(imageAdapter != null && sint != null && sint.length > 0)
				imageAdapter.setCheckedItems(sint);
			if(isCaptureImg && preSelArray != null){
				count = 0;
				imageAdapter.setCheckedItems(preSelArray);
				imageAdapter.notifyDataSetChanged();
			}
			//Add Google Analytics
			Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
	        t.setScreenName("Image Gallary Screen");
	        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
	        t.send(new HitBuilders.AppViewBuilder().build());	

		}
		private void loadImageFromGallery(){
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
//			final String orderBy = MediaStore.Images.Media.DATE_ADDED;
//			Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
			
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
					null, orderBy + " DESC");

			this.imageUrls = new ArrayList<ImageList>();
			this.imageUrls_autoselect = new ArrayList<ImageList>();
			for (int i = 0; i < imagecursor.getCount(); i++) {
				imagecursor.moveToPosition(i);
				int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
				ImageList il =new ImageList();
				il.setImageurl(imagecursor.getString(dataColumnIndex).toString());
				il.setImagePos(i);
				imageUrls.add(il);
			}


			options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.border_gray)
			.showImageForEmptyUri(R.drawable.border_gray)
			.cacheInMemory()
			.cacheOnDisc()
			.build();
			
//			options = new DisplayImageOptions.Builder()
//					.cacheInMemory(false)
//					.cacheOnDisc(true)
//					.resetViewBeforeLoading(true)
//					.showStubImage(R.drawable.border_gray)
//					.showImageForEmptyUri(null)
//					.showImageOnFail(null)
//					.showImageOnLoading(null).build();
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion > android.os.Build.VERSION_CODES.JELLY_BEAN){
				// Do something for froyo and above versions
				imageAdapter = new ImageAdapter(this, imageUrls);
				GridView gridView = (GridView) findViewById(R.id.gridview);
				gridView.setAdapter(imageAdapter);
				// startActivity(intent); 

			} else{
				// do something for phones running an SDK before froyo
				imageAdapter = new ImageAdapter(this, imageUrls);
				GridView gridView = (GridView) findViewById(R.id.gridview);
				gridView.setAdapter(imageAdapter);
			}
		}
		private int TAKENPHOTO = 0;
		public void caputureImage() {
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		}
		private boolean isCaptureImg = false;
		private int[] preSelArray = {0};
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			if (resultCode != RESULT_OK) {
				return;
			}
			super.onActivityResult(requestCode, resultCode, data);
			if(data != null && requestCode == 0){
				Bitmap bp = (Bitmap) data.getExtras().get("data");
				if(bp != null){
					isCaptureImg = true;
					/*ArrayList<ImageList> selectedItems = imageAdapter.getCheckedItems();
			preSelArray = new int[selectedItems.size() + 1];

			for(int s = 0; s < selectedItems.size()+1; s++){
				if(s == 0)
					preSelArray[s] = 0; 
				else
					preSelArray[s] = selectedItems.get(s-1).getImagePos();
				count++;
			}*/
				}

			}
		}

		private boolean isCancelClick = false;
		private final int RECORDING_TIME = 2 * 60;
		public void showAudioDialog(final byte TYPE){
			progress = 0;
			isCancelClick = false;
			dialog = new Dialog(KainatMultiImageSelection.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.player_dialog);
			//dialog.setCancelable(true);
			final Runnable wheelThread = new Runnable() 
			{
				public void run() {
					wheelRunning = false;
					while(!wheelRunning)
					{
						if(progress < RECORDING_TIME)
						{
							isTimeOver = false;
							wheelProgress.incrementProgress(RECORDING_TIME);
							progress++;
							if(TYPE == PLAY_MODE)
								setPlayLeftTime(mVoiceMedia.getMediaDuration(), mVoiceMedia.getCurrentMediaTime());
							else if(TYPE == RECORDING_MODE){
								recordLeftTime(RECORDING_TIME - progress);
							}
						}
						else
						{
							isTimeOver = true;
							wheelRunning = true;
							iCurrentState = UI_STATE_IDLE;
							KainatMultiImageSelection.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									stopRecording();
								}
							});
						}
						try 
						{
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					wheelRunning = true;
				}
			};

			wheelProgress  = (AudioProgressWheel) dialog.findViewById(R.id.player_wheel);
			wheelProgress.setWheelType(AudioProgressWheel.RECORD_WHEEL);

			if(mRecordedVoicePath != null && TYPE == PLAY_MODE){
				wheelProgress.resetCount();
				startPlaying(null);
				Thread s = new Thread(wheelThread);
				s.start();
				if(wheelProgress != null)
					wheelProgress.setBackgroundResource(R.drawable.profile_listen);
			}else{
				iCurrentState = UI_STATE_IDLE;
				wheelProgress.resetCount();
				startRecording(null);
				Thread s = new Thread(wheelThread);
				s.start();
				if(wheelProgress != null)
					wheelProgress.setBackgroundResource(R.drawable.profile_audio);
			}
			leftTimeView = (TextView)dialog.findViewById(R.id.left_time);
			Button cancelBt = (Button)dialog.findViewById(R.id.button_01);
			cancelBt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isCancelClick = true;
					dialog.dismiss();
				}
			});

			final Button doneReset = (Button)dialog.findViewById(R.id.button_02);
			if(TYPE == RECORDING_MODE){
				doneReset.setText(getString(R.string.done));
			}else if(TYPE == PLAY_MODE){
				doneReset.setText(getString(R.string.reset));
			}
			doneReset.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					isCancelClick = false;
					progress = 0;
					// TODO Auto-generated method stub
					if(doneReset.getText().toString().equalsIgnoreCase(getString(R.string.done))){
						wheelRunning = true;
						stopRecording();
						if(wheelProgress != null){
							wheelProgress.setBackgroundResource(R.drawable.profile_audio);
							wheelProgress.resetCount();
						}
						voiceBt.setImageResource(R.drawable.profile_listen);

						dialog.dismiss();
					}else if(doneReset.getText().toString().equalsIgnoreCase(getString(R.string.reset))){// if(TYPE == PLAY_MODE){
						dialog.dismiss();
						wheelRunning = true;
						stopPlaying(v);
						if(wheelProgress != null)
							wheelProgress.setBackgroundResource(R.drawable.profile_listen);
						voiceBt.setImageResource(R.drawable.profile_listen);
						/////
						File file = new File(mRecordedVoicePath);
						if (file.exists()) {
							file.delete();
						}
						voiceBt.setImageResource(R.drawable.profile_audio);
						mRecordedVoicePath = null;
					}
				}
			});
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if(isCancelClick){
						progress = 0;
						if(TYPE == RECORDING_MODE){
							wheelRunning = true;
							stopRecording();
							if(wheelProgress != null)
								wheelProgress.setBackgroundResource(R.drawable.profile_audio);
							mRecordedVoicePath = null;
							voiceBt.setImageResource(R.drawable.profile_audio);
							dialog.dismiss();
						}else if(TYPE == PLAY_MODE){
							wheelRunning = true;
							stopPlaying(null);
							if(wheelProgress != null)
								wheelProgress.setBackgroundResource(R.drawable.profile_listen);
							dialog.dismiss();
						}
					}
				}
			});
			dialog.show();
		}
		public void setPlayLeftTime(final int total, final int time){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					leftTimeView.setText(Utilities.converMiliSecond(time)+" secs");
				}
			});
		}
		public void recordLeftTime(final int time){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					leftTimeView.setText(String.format("%02d:%02d", (time / 60), (time % 60))+" secs");
					//				leftTimeView.setText(time+" secs");
				}
			});
		}
		private void startRecording(View v)
		{
			if (mVoiceMedia == null)
				mVoiceMedia = new VoiceMedia(KainatMultiImageSelection.this, this);
			mVoiceMedia.startRecording(120);
			//		mVoiceMedia.startRecording("Done", "Cancel", null,
			//				120);
			iCurrentState = Constants.UI_STATE_RECORDING;
			Log.d(TAG, "startRecording()");
		}
		private void stopRecording()
		{
			if (mVoiceMedia != null)
				mVoiceMedia.stopRec();
			iCurrentState = Constants.UI_STATE_IDLE;
			Log.i(TAG, "stopRecording()");
		}
		private void startPlaying(View v) {
			if(mRecordedVoicePath != null)
			{
				mVoiceMedia.startNewPlaying(mRecordedVoicePath, null, false);
				iCurrentState = Constants.UI_STATE_PLAYING;
			}
			Log.i(TAG, "Playing started..");
		}
		private void stopPlaying(View v) {
			if (mVoiceMedia != null)
				mVoiceMedia.stopVoicePlaying();
			iCurrentState = Constants.UI_STATE_IDLE;
			Log.i(TAG, "stopPlaying()");
		}
		@Override
		public void onDureationchanged(final long total, final long current) {
			KainatMultiImageSelection.this.handler.post(new Runnable() {

				@Override
				public void run() {
					try {

						/*total_autio_time.setText(""
							+ Utilities.converMiliSecond(total) + ")");
				//	total_autio_time.setTypeface(Typeface.DEFAULT_BOLD);

					played_autio_time.setText("("
							+ Utilities.converMiliSecond(current)
							+ " of ");*/

						leftTimeView.setText(Utilities.converMiliSecond(current)+ " : "+Utilities.converMiliSecond(total));
						//					played_autio_time
						//					.setTypeface(Typeface.DEFAULT_BOLD);
					} catch (Exception e) {
						// if(isAudio)
						RTMediaPlayer.reset();
					}
				}
			});
		}
		@Override
		public void voicePlayCompleted() {
			Log.i(TAG, "voicePlayCompleted()");
			wheelRunning = true;
			iCurrentState = Constants.UI_STATE_IDLE;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(wheelProgress != null)
						wheelProgress.setBackgroundResource(R.drawable.voizd_instigator_play);
					if (mVoiceMedia != null)
						mVoiceMedia.stopVoicePlaying();
					dialog.dismiss();
				}
			});
		}
		@Override
		public void voiceRecordingCompleted(final String recordedVoicePath) {
			if(!isCancelClick){
				if(dialog.isShowing())
					dialog.dismiss();
				wheelRunning = true;
				mRecordedVoicePath = recordedVoicePath;
				iCurrentState = Constants.UI_STATE_IDLE;
				Log.i(TAG, "voiceRecordingCompleted(): mRecordedVoicePath -> "+mRecordedVoicePath);
				if(!isTimeOver){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(wheelProgress != null)
								wheelProgress.setBackgroundResource(R.drawable.voizd_instigator_play);
							voiceBt.setImageResource(R.drawable.profile_listen);
							dialog.dismiss();
						}
					});
				}
			}
			else
				mRecordedVoicePath = null;
		}
		@Override
		protected void onResume() {
			super.onResume();

//			loadImageFromGallery();
//			int sint[] = (int[])DataModel.sSelf.removeObject("SELECTED_IMAGES");
//			if(imageAdapter != null && sint != null && sint.length > 0)
//				imageAdapter.setCheckedItems(sint);
//			if(isCaptureImg && preSelArray != null){
//				count = 0;
//				imageAdapter.setCheckedItems(preSelArray);
//				imageAdapter.notifyDataSetChanged();
//			}
		}

		@Override
		protected void onStop() {
			thumbImageLoader.stop();
			super.onStop();
		}

		public void btnChoosePhotosClick(View v){

				ArrayList<ImageList> selectedItems = imageAdapter.getCheckedItems();
				Log.d(KainatMultiImageSelection.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
				String [] data = new String[selectedItems.size()];		
				int [] data_int = new int[selectedItems.size()];		
				for (int i = 0; i < selectedItems.size(); i++) {
					data[i] = selectedItems.get(i).getImageurl() ;
					data_int[i] = selectedItems.get(i).getImagePos();
				}
				Integer [] dataId = new Integer[mImagesPathID.size()];				
				for (int i = 0; i < mImagesPathID.size(); i++) {
					dataId[i] = mImagesPathID.get(i) ;
				}
				DataModel.sSelf.storeObject(DMKeys.MULTI_IMAGES, data);
				DataModel.sSelf.storeObject(DMKeys.MULTI_IMAGES+"ID", dataId);
				DataModel.sSelf.storeObject(DMKeys.MULTI_IMAGES+"IDINT", data_int);
				if(isAudioVisible){
					if((mRecordedVoicePath == null || (mRecordedVoicePath != null && mRecordedVoicePath.trim().length() == 0))){
						Toast.makeText(KainatMultiImageSelection.this, getString(R.string.talking_pic_alert),  Toast.LENGTH_SHORT).show();
						return;
					}
					else{
						DataModel.sSelf.storeObject(DMKeys.VOICE_PATH, mRecordedVoicePath);
					}
				}
				Intent path = new Intent().putExtra("all_path", data);
				setResult(RESULT_OK, path);
				finish();
			
		}

		@Override
		public void notificationFromTransport(ResponseObject response) {
			// TODO Auto-generated method stub

		}


		//Adapter Below
		public class ImageAdapter extends BaseAdapter {

			ArrayList<ImageList> mList;
			LayoutInflater mInflater;
			Context mContext;
			SparseBooleanArray mSparseBooleanArray;

			public ImageAdapter(Context context, ArrayList<ImageList> imageList) {
				// TODO Auto-generated constructor stub
				mContext = context;
				mInflater = LayoutInflater.from(mContext);
				mSparseBooleanArray = new SparseBooleanArray();
				mList = new ArrayList<ImageList>();
				this.mList = imageList;

			}

			public ArrayList<ImageList> getCheckedItems() {
				ArrayList<ImageList> mTempArry = new ArrayList<ImageList>();

				for(int i=0;i<mList.size();i++) {
					if(mSparseBooleanArray.get(i)) {
						mTempArry.add(mList.get(i));
					}
				}

				return mTempArry;
			}
			public void setCheckedItems(int[] mList) {
				for(int i = 0; i < mList.length; i++) {
					mSparseBooleanArray.put(mList[i], true);
					count++;
				}

			}

			@Override
			public int getCount() {
				return imageUrls.size();
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
				}
				final CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
				thumbImageLoader.displayImage("file://"+imageUrls.get(position).getImageurl(), imageView, options);
				if(count == 0)
				{
					media_screenTitle_text.setText(R.string.gallery);
					captionBottomLayout.setVisibility(View.GONE);
					headerCloseIv.setVisibility(View.VISIBLE);
				}else{
					headerCloseIv.setVisibility(View.GONE);
					captionBottomLayout.setVisibility(View.VISIBLE);
					media_screenTitle_text.setText(getString(R.string.selected) + " ("+ count+")");
				}
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mCheckBox.performClick();
					}
				});
				mCheckBox.setTag(position);
				mCheckBox.setChecked(mSparseBooleanArray.get(position));
				mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
				return convertView;
			}

			final OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					//  if(mSparseBooleanArray.get((Integer) buttonView.getTag()) || !isChecked){
					if(count==maxImageToAttached && isChecked){
						Toast.makeText(getApplicationContext(), R.string.max_attachment_reached, Toast.LENGTH_LONG).show();
						if(mSparseBooleanArray.get((Integer) buttonView.getTag())){
						}else{
							buttonView.setChecked(false);
						}
					}else{
						if(isChecked && !(mSparseBooleanArray.get((Integer) buttonView.getTag())))
						{
							count = count + 1;
						}else if(mSparseBooleanArray.get((Integer) buttonView.getTag()) && !isChecked)
						{
							count = count - 1;
						}
						if(count == 0)
						{
							media_screenTitle_text.setText(R.string.gallery);
							captionBottomLayout.setVisibility(View.GONE);
							headerCloseIv.setVisibility(View.VISIBLE);
						}else
						{
							headerCloseIv.setVisibility(View.GONE);
							captionBottomLayout.setVisibility(View.VISIBLE);
							media_screenTitle_text.setText(getString(R.string.selected) + " ("+ count+")");
						}
						mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
					}
				}
			};
		}


		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();

			try{
				String s[] = (String[])DataModel.sSelf.removeObject("3333");
				int sint[] = (int[])DataModel.sSelf.removeObject("3333"+"int");
				if(s != null )
					for (int i = 0; i < s.length; i++) {
						try{
							imageUrls_autoselect.get(i).setImageurl(s[i]) ;
							//imageUrls_autoselect.get(i).setImagePos(sint[i]) ;
							//long size = ImageUtils.getImageSize(s[i]) ;

							/*if (isSizeReachedMaximum(size)) {
					if(BusinessProxy.sSelf.MaxSizeData==2){
						showSimpleAlert("Error",
						"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
					}else{

					showSimpleAlert("Error","Max attachment reached. Can not attach more!");
					}
					return;
				}*/
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				if(sint != null )
					for (int i = 0; i < sint.length; i++) {
						try{
							//imageUrls_autoselect.get(i).setImageurl(s[i]) ;
							imageUrls_autoselect.get(i).setImagePos(sint[i]) ;
							//long size = ImageUtils.getImageSize(s[i]) ;

							/*if (isSizeReachedMaximum(size)) {
					if(BusinessProxy.sSelf.MaxSizeData==2){
						showSimpleAlert("Error",
						"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
					}else{

					showSimpleAlert("Error","Max attachment reached. Can not attach more!");
					}
					return;
				}
					}catch (Exception e) {
					}
				}*/
						}catch (Exception e) {
							e.printStackTrace();
						}
					}

				Integer sInt[] = (Integer[])DataModel.sSelf.removeObject("3333"+"ID");
				if(sInt != null )
				{
					for (int i = 0; i < sInt.length; i++) {
						try{
							mImagesPathID.add(sInt[i]) ;

						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else if(imageUrls != null && imageUrls.size() > 0)
				{
					for (int i = 0; i < imageUrls.size(); i++) {
						try{
							mImagesPathID.add(1) ;
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				((TextView)findViewById(R.id.info)).setText((getString(R.string.max_count)+": "+maxImageToAttached));//+" within max size: "+BusinessProxy.sSelf.MaxSizeData+" MB. Current attached size: "+onMbKbReturnResult(totalSize)
			}catch (Exception e) {
				// TODO: handle exception
				Log.v("Exception is", ""+e);
			}
		}

		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			//onBackPressed() ;
		}
		@Override
		public void onBackPressed() {	
			if(onBackPressedCheck())return;
			//		setResult(RESULT_OK);
			finish() ;

		}

	}
