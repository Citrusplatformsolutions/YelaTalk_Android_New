package com.kainat.app.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.VideoView;

import com.kainat.app.android.constanst.VideoTypes;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.ResponseObject;

public class VideoPlayer extends UIActivityManager {

	private String path = "rt2.3gp";
	private VideoView mVideoView;
	Intent intent;
	boolean fileMode = false;
	byte data[];

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.videoview);
		intent = getIntent();

		if (intent.getStringExtra("MODE") != null && intent.getStringExtra("MODE").equals("file")) {
			fileMode = true;

			path = intent.getStringExtra("VIDEO_PATH");
			if (path == null)
				path = com.kainat.app.android.util.Constants.defaultVideoPath;
		}
		else if (intent.getStringExtra("MODE") != null && intent.getStringExtra("MODE").equals("URI")) {
			fileMode = true;

			path = intent.getStringExtra("VIDEO_PATH");
			if (path == null)
				path = com.kainat.app.android.util.Constants.defaultVideoPath;
		}
		else {
			fileMode = false;
			// data = intent.getByteArrayExtra("DATA");
			data = (byte[]) DataModel.sSelf.removeObject(DMKeys.VIDEO_DATA);

			try {
				byte mediaType = DataModel.sSelf.removeByte(DMKeys.VIDEO_DATA_TYPE);
				if (mediaType == VideoTypes.VIDEO_3GP_FORMAT) {
					path = "rt2" + VideoTypes.EXTENSION_3GP;
				} else if (mediaType == VideoTypes.VIDEO_MP4_FORMAT) {
					path = "rt2" + VideoTypes.EXTENSION_MP4;
				}
			} catch (Exception e) {

			}
			//makeToast(path);
			createVideoPath(data);
			//showSimpleAlert("path:",path);        	     
		}
//		showSimpleAlert("path:",path);     
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
//		mVideoView.setVideoPath(path);
//		System.out.println(path);
		
		///mnt/sdcard/dcim/An awwwwww film from Huggies  Kajol, shadow and the baby.mp4
		String extension = MimeTypeMap.getFileExtensionFromUrl(path);
		if(extension==null||extension.trim().length()<=0){
			try{
				extension = path.substring(path.lastIndexOf(".")+1, path.length());
			}catch (Exception e) {
				extension = "3gp" ;
			}
		}
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		
		if(mimeType!=null){
		Uri uri = Uri.parse(path);
		Intent player = new Intent(Intent.ACTION_VIEW, uri);
		if (mimeType.equalsIgnoreCase("3gp")) {
			player.setDataAndType(uri, "video/3gpp");
		} else {
			player.setDataAndType(uri, mimeType);
		}
//		startActivity(player);
		
//		Intent videoIntent = new Intent(Intent.ACTION_VIEW);
//		videoIntent.setDataAndType(Uri.parse(path), "video/*");
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, videoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		startActivity(videoIntent);
		
		Intent videoIntent = new Intent(Intent.ACTION_VIEW);
		File file = new File(path);
//		System.out.println("------is file path + "+ path);
//		System.out.println("------is file exist + "+ file.exists());
//		System.out.println("------Uri.fromFile(file)+ "+ Uri.fromFile(file));
//		videoIntent.setDataAndType(Uri.fromFile(file), "video/mp4");
		if (mimeType.equalsIgnoreCase("3gp")) {
			videoIntent.setDataAndType(Uri.fromFile(file), "video/3gpp");
		} else {
			videoIntent.setDataAndType(Uri.fromFile(file), mimeType);
		}
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, videoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		startActivity(videoIntent);
		finish();
		}else{
//			Intent videoIntent = new Intent(Intent.ACTION_VIEW);
//			File file = new File(path);
//			videoIntent.setData(Uri.fromFile(file));
//			startActivity(videoIntent);
//			finish();
//			try{}catch (Exception e) {
//				finish();
//			}
			finish();
		}
			
	}

	final int MAX_DATA_CHUNK = 1024 * 40;

	public void createVideoPath(byte[] data) {
		try {
			File root = Environment.getExternalStorageDirectory();
			File tempMp3 = new File(root, path);

			tempMp3.delete();
			//tempMp3.deleteOnExit();
			/* if(!tempMp3.exists()) */
			
			String pathtodel = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" +path;// "/RockeTalk/Chat/";
			File exists = new File(pathtodel);//.exists();
			if (exists.exists()) {
				 exists.delete() ;
			}
			tempMp3.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(tempMp3);
			//fos.write(data);
			int dataLength = data.length;
			int iTotalBytesDone = 0;

			while (dataLength > 0) {
				if (dataLength >= MAX_DATA_CHUNK) {
					if (null != fos) {
						fos.write(data, iTotalBytesDone, MAX_DATA_CHUNK);
					}
					iTotalBytesDone += MAX_DATA_CHUNK;
					dataLength -= MAX_DATA_CHUNK;
				} else {
					if (null != fos) {
						fos.write(data, iTotalBytesDone, dataLength);
					}
					iTotalBytesDone += dataLength;
					dataLength = 0;
				}
			}
			fos.close();

			path = tempMp3.getAbsolutePath();
		} catch (IOException ex) {
			System.out.println("########################EXCEPTION HANDLED#############################");
			ex.printStackTrace();
			showSimpleAlert("Error", "Error--" + ex.toString());
		}
		catch (Exception ex) {
			System.out.println("########################EXCEPTION HANDLED#############################");
			ex.printStackTrace();
			showSimpleAlert("Error", "Error--" + ex.toString());
		}
	}

	@Override
	public void onBackPressed() {
		BusinessProxy.sSelf.mUIActivityManager.setCurrentState(Constants.UI_STATE_IDLE);
		if (fileMode) {
			mVideoView.stopPlayback();
//			if (intent.getStringExtra("BACK") != null && intent.getStringExtra("BACK").equals("COMPOSE")) {
//				pushNewScreen(ComposeActivity.class, false);
//			} 
//			else if (intent.getStringExtra("BACK") != null && intent.getStringExtra("BACK").equals("POST")) {
//				pushNewScreen(PostActivity.class, false);
//			}
//			else if (intent.getStringExtra("BACK") != null && intent.getStringExtra("BACK").equals("STATUS_UPDATE")) {
//				pushNewScreen(MyStatus.class, false);
//			} 
//			else
				if (intent.getStringExtra("BACK") != null && intent.getStringExtra("BACK").equals("MEDIA")) {
				super.onBackPressed();
			} else if (intent.getStringExtra("BACK") != null && intent.getStringExtra("BACK").equals("INBOX")) {
				finish();
			}
		} else {
			if (intent.getStringExtra("BACK") != null && intent.getStringExtra("BACK").equals("WEBVIEW")) {
				mVideoView.stopPlayback();
				//pushNewScreen(WebviewActivity.class, false);
				finish();
			} else {
				mVideoView.stopPlayback();
				//pushNewScreen(MessageActivity.class, false);
				finish();
			}
		}
	}

	protected void pushNewScreen(Class<?> clazz, boolean clearCurrentScreen) {
		Intent intent = new Intent(VideoPlayer.this, clazz);
		startActivityForResult(intent, 0);
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub
	}
}
