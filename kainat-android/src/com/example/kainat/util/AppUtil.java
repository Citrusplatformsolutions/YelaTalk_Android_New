package com.example.kainat.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.kainat.app.android.R;
import com.kainat.app.android.util.Constants;

public class AppUtil {

	public static final byte POSITION_GALLRY_PICTURE = 1;
	public static final byte POSITION_CAMERA_PICTURE = 2;
	public static final byte POSITION_CAMERA_PICTURE_LEFT = 3;
	public static final byte POSITION_CAMERA_PICTURE_RIGHT = 4;
	public static final byte POSITION_GALLRY_PICTURE_LEFT = 5;
	public static final byte POSITION_GALLRY_PICTURE_RIGHT = 6;
	public static final byte PIC_CROP = 7;
	public static final byte PIC_CROP_LEFT = 8;
	public static final byte PIC_CROP_RIGHT = 9;

	public static final byte ADD_COLOR = 10;

	public static final String cost = "$%s";//(%s$ per day)";

	public static final int RATE_CONST = 20;

	public static final int RADIUS = 4000;
	public static final double lat = 28.6100;
	public static final double lon = 77.2300;

	public static String capturedPath1, capturedPath2;



	public static void openCamera(Object context, String cameraImagePathL,
			byte resultCode) {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			// if(capturedPath1==null)
			capturedPath1 = file.getPath();
			// if(capturedPath2==null)
			// capturedPath2 = file.getPath();
			Uri outputFileUri = Uri.fromFile(file);
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			((Activity) context).startActivityForResult(i, resultCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openImageGallery(Object context, byte resultCode) {
		try {
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			((Activity) context).startActivityForResult(i, resultCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static long getRandomNumber() {
		return System.currentTimeMillis();// rand.nextInt();
	}

	public static String getPath(Uri uri, Context context) {

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = ((Activity) context).managedQuery(uri, projection,
					null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();
		}
	}

	public static void setImage(ImageView imageView, Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
	}
	
	public static void showTost(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		//toast_screen.xml
//		LayoutInflater inflater = LayoutInflater.from(context);;
//		View retView = inflater.inflate(R.layout.toast_screen, null,
//				false);
//		((TextView)retView.findViewById(R.id.msg)).setText(msg);
//		toast.setView(retView);
		toast.show();
	}
	

	public static void clearAppData() {
		
		capturedPath1 = null;
		capturedPath2 = null;
		// if(templates!=null)
		// templates.clear();
	}

	public static void showAlert(final Activity activity,String title,String msg,final int action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								// MainActivity.this.finish();
								if(action==1){
									activity.finish();
//									Intent intent = new Intent(activity,
//											MenuActivity.class);
//									activity.startActivity(intent);
								}
							}
						});
//				.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						// if this button is clicked, just close
//						// the dialog box and do nothing
//						dialog.cancel();
//					}
//				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        // Log exception
	        return null;
	    }
	}
	public static boolean checkInternetConnection(final Activity context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null

		&& conMgr.getActiveNetworkInfo().isAvailable()

		&& conMgr.getActiveNetworkInfo().isConnected()) {
			// System.out.println("================network avilable==========================");
			return true;

		} else {
			// System.out.println("================network not avilable==========================");
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					showTost(context, context.getString(R.string.check_net_connection)) ;
				}
			});
			return false;

		}
	}
	public static final String TEMP_PHOTO_FILE = "temp.png";
	public static File getTempFile() {

	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

	    	  File file = new File(Environment.getExternalStorageDirectory().getPath(), Constants.contentTemp);//"MyFolder/Images");
	  	    if (!file.exists()) {
	  	        file.mkdirs();
	  	    }
	  	    
	         file = new File(Environment.getExternalStorageDirectory(),Constants.contentTemp+"/"+TEMP_PHOTO_FILE);
	        try {
	            file.createNewFile();
	        } catch (IOException e) {}

	        return file;
	    } else {

	        return null;
	    }
	}
	public static Uri getTempUri() {
	    return Uri.fromFile(getTempFile());
	}	
}
