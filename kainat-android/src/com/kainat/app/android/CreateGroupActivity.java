package com.kainat.app.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kainat.util.AppUtil;
import com.example.kainat.util.CompressImage;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.model.KainatContact;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;

public class CreateGroupActivity extends UIActivityManager {

	// public Handler handler = new Handler(Looper.getMainLooper());
	private static final String TAG = CreateGroupActivity.class
			.getSimpleName();
	private TextView create_group ;
	private String communityName;
	private ImageButton backImgButton;
	private Button startBt;
	private String sharedText;
	private List<String> mImagesPath = new ArrayList<String>();
	private CreateGroupActivity mContext;
	private int updateProfile;

	private com.kainat.app.android.uicontrol.CImageView imageView;
	private EditText groupNameField;
	private ArrayList<KainatContact> kainatContactList = new ArrayList<KainatContact>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.kainat_create_group);

		imageView = (com.kainat.app.android.uicontrol.CImageView) findViewById(R.id.user_image);
		imageView.setOnClickListener(this);
		backImgButton = (ImageButton) findViewById(R.id.left_arrow_iv);
		backImgButton.setOnClickListener(this);
		startBt = (Button) findViewById(R.id.done);
		startBt.setOnClickListener(this);
		groupNameField = (EditText) findViewById(R.id.create_field_box);

		kainatContactList = (ArrayList<KainatContact>) getIntent().getExtras().getSerializable("KAINAT_CONTACT_LIST");

		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Create Group Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_image:
			onCreateDialogPricture().show();
			break;
		case R.id.left_arrow_iv:
			finish();
			break;
		case R.id.done:
//			if(kainatContactList != null){
//				StringBuffer sb = new StringBuffer() ;
//				for (KainatContact kainatContact :kainatContactList) {
//					if(kainatContact.isSelected())
//						sb.append(kainatContact.getRealName()+";") ;
//				}
//				if(sb.toString().length()>10){
//					if (getIntent().getBooleanExtra("ADDCONVERSATIONS", false)) {
//
//						if (sb.toString() != null && sb.toString().trim().length() > 0) {
//							DataModel.sSelf.storeObject(DMKeys.COMPOSE_DESTINATION_CONTACTS, sb.toString());
//							setResult(ConversationsActivity.ADDCONVERSATIONS);
//							finish();
//
//						}
//					}else{
//						startChat(sb.toString()) ;
//					}
//				}
//				else{
//					AppUtil.showTost(CreateGroupActivity.this, getString(R.string.slectc_contact_to_start_chat)) ;
//				}
//			}
//			startChat();
			sharedText = groupNameField.getText().toString();
			if(sharedText == null || (sharedText != null && sharedText.trim().length() == 0) && mImagesPath.size() == 0){
				startChat();
			}
			else
				new UploadPicTask().execute("");
			break;
		}
	}
	private void startChat(){
			Intent intent = new Intent(CreateGroupActivity.this, ConversationsActivity.class);
			intent.putExtra(Constants.CONVERSATION_ID, "-1");
			intent.putExtra("START_CHAT_FROM_EXTERNAL",(byte) 1);
			intent.putExtra("MessageText",sharedText);
			startActivity(intent);
			finish();
	}
//	private void startChat(String destinations){
//		//		destinations = "jhhjhjjh<897987789798978>" ;
//		if (destinations != null && destinations.trim().length() > 0) {
//			DataModel.sSelf.storeObject(DMKeys.COMPOSE_DESTINATION_CONTACTS,destinations);
//			Intent intent = new Intent(CreateGroupActivity.this, ConversationsActivity.class);
//			intent.putExtra(Constants.CONVERSATION_ID, "-1");
//			intent.putExtra("START_CHAT_FROM_EXTERNAL",(byte) 1);
//			intent.putExtra("MessageText",sharedText);
//			startActivity(intent);
//			finish();
//		}
//	}

	public Dialog onCreateDialogPricture() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_picture).setItems(R.array.image_choose01,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					AppUtil.openCamera(mContext, AppUtil.capturedPath1,
							AppUtil.POSITION_CAMERA_PICTURE);
					break;
				case 1:
					AppUtil.openImageGallery(mContext,
							AppUtil.POSITION_GALLRY_PICTURE);
					break;
				}
			}
		});
		return builder.create();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {

			if (resultCode == mContext.RESULT_OK) {
				switch (requestCode) {
				case AppUtil.PIC_CROP:
					String filePath= Environment.getExternalStorageDirectory() +"/"+Constants.contentTemp+"/"+AppUtil.TEMP_PHOTO_FILE;
					AppUtil.capturedPath1 = filePath ;
					mImagesPath.clear();
					mImagesPath.add(AppUtil.capturedPath1) ;
					Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
					imageView.setImageBitmap(selectedImage);
					break;
				case AppUtil.POSITION_CAMERA_PICTURE:
					if (data != null && data.getData() != null) 
					{
						Uri uri = data.getData() ;
						AppUtil.capturedPath1 = AppUtil.getPath(uri,mContext);
					}
					CompressImage compressImage = new CompressImage(mContext);
					AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
					mImagesPath.add(AppUtil.capturedPath1);
					imageView.setImageURI(Uri.parse(AppUtil.capturedPath1));
					break;
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
							mImagesPath.clear();
							mImagesPath.add(AppUtil.capturedPath1) ;
							Bitmap selectedImages =  BitmapFactory.decodeFile(AppUtil.capturedPath1);
							imageView.setImageBitmap(selectedImages);
							if(mImagesPath.size() > 0)
							{
								//updateProfile |= UPDATE_PIC;
							}
						}
						else
						{
							compressImage = new CompressImage(mContext);
							AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
							imageView.setImageURI(Uri.parse(AppUtil.capturedPath1));
						}
					}
					mImagesPath.clear();
					mImagesPath.add(AppUtil.capturedPath1);
//					performCrop(AppUtil.PIC_CROP);
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
	private void performCrop(byte resultCode) {
		try {
			mImagesPath.clear();
			mImagesPath.add(AppUtil.capturedPath1) ;
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}





	@Override
	public void onBackPressed() {
		finish();
	}





	@Override
	protected void onResume() {
		reloadeViewAfterChangeLanguag();
		super.onResume();
	}



	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}
//---------------------------------------------------------------------------
	ProgressDialog rTDialog;
	private class UploadPicTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			rTDialog = ProgressDialog.show(CreateGroupActivity.this, "", getString(R.string.loading_dot), true);
		}
		@Override
		protected String doInBackground(String... urls) {
			String responseStr = null;
			try {
				if(mImagesPath != null && mImagesPath.size() > 0)
					responseStr = Utilities.createMediaID(mImagesPath.get(0),Constants.ID_FOR_UPDATE_PROFILE);
			} catch (Exception e) {
                  e.printStackTrace();
			}
			return responseStr;
		}

		@Override
		protected void onPostExecute(String response) {
			Log.i(TAG, "UploadPicTask :: onPostExecute :: response : "+response);
			rTDialog.dismiss();
			if(response != null && response.contains("\"status\":\"error\"")){
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(response);
					if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("error")){
						if(jsonObject.has("message"))
							Toast.makeText(CreateGroupActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
						mImagesPath.clear();
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//Switch to ConversationsActivity.
			Intent intent = new Intent(CreateGroupActivity.this, ConversationsActivity.class);
			intent.putExtra(Constants.CONVERSATION_ID, "-1");
			intent.putExtra("START_CHAT_FROM_EXTERNAL",(byte) 1);
			if(sharedText != null && sharedText.trim().length() > 0){
				intent.putExtra("MUC_SUB_NAME", sharedText);
				Log.i(TAG, "onPostExecute :: MUC_SUB_NAME : "+sharedText);
			}
			if(response != null && !response.contains("\"status\":\"error\"")){
				intent.putExtra("MUC_PIC_FILE_ID",response);
				if(mImagesPath != null && mImagesPath.get(0) != null && mImagesPath.size() > 0)
					intent.putExtra("MUC_LOCAL_IMG", mImagesPath.get(0));
				Log.i(TAG, "onPostExecute :: MUC_PIC_FILE_ID : "+response);
			}
			startActivity(intent);
			finish();
		}
	}

}
