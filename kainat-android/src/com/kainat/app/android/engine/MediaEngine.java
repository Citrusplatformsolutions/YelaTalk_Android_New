package com.kainat.app.android.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaRecorder;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.Utilities;

public final class MediaEngine implements OnCompletionListener, OnErrorListener, OnInfoListener, android.media.MediaRecorder.OnErrorListener, android.media.MediaRecorder.OnInfoListener {

	private static final String TAG = MediaEngine.class.getSimpleName();

	public static MediaEngine sSelf;
	private MediaPlayer mMediaPlayer;
	private MediaEventsHandler mParent;
	private  MediaRecorder recorder ;//= new MediaRecorder();
//	private  CustomAudioRecorder recorder ;//= new MediaRecorder();
	private String path;
	private Context context;
	private boolean isRecordingStart = false;
	private MediaEngine() {
	}

	public static void initMediaEngineInstance(Context context) {
		sSelf = new MediaEngine();
		sSelf.context = context;
	}

	public static MediaEngine getMediaEngineInstance() {
		if (sSelf == null) {
			sSelf = new MediaEngine();
		}
		return sSelf;
	}

	
	public void playResource(int resourceId) {
		if (resourceId == -1 || BusinessProxy.sSelf.mIsApplicationMute || Utilities.isSilentMode(context))
			return;
		play(loadResToByteArray(resourceId, context), null);
	}

	public void play(byte[] soundByteArray, MediaEventsHandler parent) {
		try {
			if ((null != mMediaPlayer && mMediaPlayer.isPlaying()) || BusinessProxy.sSelf.mUIActivityManager.getCurrentState() != Constants.UI_STATE_IDLE) {
				return;
			}
			File tempMp3 = new File(context.getCacheDir(), "kurchina.mp3");
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			playFile(tempMp3.getAbsolutePath(), parent, false);
		} catch (IOException ex) {
		}
	}

	public boolean playFileForMute(String path, MediaEventsHandler parent) {
		if (null != mMediaPlayer && mMediaPlayer.isPlaying() || BusinessProxy.sSelf.mUIActivityManager.getCurrentState() != Constants.UI_STATE_IDLE) {
			return false;
		}
		this.mParent = parent;
		try {
			mMediaPlayer = new MediaPlayer();
			FileInputStream fin = new FileInputStream(path);
			mMediaPlayer.setDataSource(fin.getFD());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnInfoListener(this);
			if (BusinessProxy.sSelf.mIsApplicationMute)
				mMediaPlayer.setVolume(0, 0);
			mMediaPlayer.start();

		} catch (IOException e) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "--Play file - " + path, e);
			}
			return false;
		}
		 catch (Exception e) {
			 System.out.println("########################EXCEPTION HANDLED#############################");
			 e.printStackTrace();
				if (Logger.ENABLE) {
					Logger.error(TAG, "--Play file - " + path, e);
				}
				return false;
			}
		return true;
	}

	public boolean playFile(String path, MediaEventsHandler parent, boolean isUrl) {
		this.mParent = parent;
		if (null != mMediaPlayer && mMediaPlayer.isPlaying() &&  BusinessProxy.sSelf.mUIActivityManager.getCurrentState() == Constants.UI_STATE_IDLE){
			parent.mediaEvent((byte)123);
		}
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()){
			BusinessProxy.sSelf.mUIActivityManager.setCurrentState(Constants.UI_STATE_PLAYING);
		}else{
			BusinessProxy.sSelf.mUIActivityManager.setCurrentState(Constants.UI_STATE_IDLE);
		}
		
		if (null != mMediaPlayer && mMediaPlayer.isPlaying() || BusinessProxy.sSelf.mUIActivityManager.getCurrentState() != Constants.UI_STATE_IDLE) {
//			System.out.println("isplaying====================false" +"mMediaPlayer.isPlaying==="+mMediaPlayer.isPlaying() + "currentstate==="+BusinessProxy.sSelf.mUIActivityManager.getCurrentState());
			return false;
		}
		
		try {
			mMediaPlayer = new MediaPlayer();
			if (!isUrl) {
				FileInputStream fin = new FileInputStream(path);
				mMediaPlayer.setDataSource(fin.getFD());
			} else {
				mMediaPlayer.setDataSource(path);
			}
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnInfoListener(this);
//			System.out.println("isplaying====================false" +"mMediaPlayer.isPlaying==="+mMediaPlayer.isPlaying() + "currentstate==="+BusinessProxy.sSelf.mUIActivityManager.getCurrentState());
//			RocketalkApplication.am.setMode(AudioManager.STREAM_MUSIC);			
//			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) ;
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error(TAG, "--Play file - " + path, e);
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error(TAG, "--Play file - " + path, ex);
			}
			return false;
		}
		return true;
	}

	public int getMediaDuration() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getDuration();
		else
			return 0;
	}

	public int getCurrentMediaTime() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getCurrentPosition();
		else
			return 0;
	}

	/**
	 * Stops a recording that has been previously started.
	 */
	public void stopRecorder() throws IOException 
	{
		isRecordingStart = false;
		if(recorder!=null)
		{
			recorder.stop();	    
			recorder.reset();
	        recorder.release();
	        BusinessProxy.sSelf.startRecording=false;
	        recorder = null;
	        
	     // Stop recording
			if(BusinessProxy.sSelf.startRecording){
		        recorder.stop();
		        recorder.reset();
		        recorder.release();
		        BusinessProxy.sSelf.startRecording=false;
		        recorder = null;
			}

		}
	}

	public void stopPlayer() {
		try {
			if (null != mMediaPlayer) {
				mMediaPlayer.stop();
				if (null != mParent) {
					mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_STOPPED);
				}
			}
		} catch (IllegalStateException ies) {
			if (Logger.ENABLE)
				Logger.error(TAG, "stop - Illegal state ", ies);
		}
	}

	public void resumePlay() {
		try {
			if (null != mMediaPlayer) {
				mMediaPlayer.start();
				if (null != mParent) {
					mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_RESUMED);
				}
			}
		} catch (IllegalStateException ies) {
			if (Logger.ENABLE)
				Logger.error(TAG, "resume -- Illegal state ", ies);
		}
	}

	public void pausePlay() {
		try {
			if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				if (null != mParent) {
					mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_PAUSED);
				}
			}
		} catch (IllegalStateException ies) {
			if (Logger.ENABLE)
				Logger.error(TAG, "pause -- Illegal state ", ies);
		}
	}

	public static byte[] loadResToByteArray(int resId, Context ctx) {

		byte[] s = null;
		try {
			InputStream is = ctx.getResources().openRawResource(resId);
			s = new byte[is.available()];
			is.read(s);
			is.close();
		} catch (Exception e) {
			if (Logger.ENABLE)
				Logger.error(TAG, "loadResToByteArray -- " + e.getMessage(), e);
		}
		return s;//return "<html><head><title>test</title></head><body><a href='l'>click here to see date (this doesnt work in m5 dk)</a>.</body></html>";
	}

	public void recorderWithNewPath(MediaEventsHandler parent, String path) {
		this.mParent = parent;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += ".amr";
//			path += ".m4a";
		}
		this.path = this.context.getCacheDir() + path;
	}

	public String getFilePath() {
		return this.path;
	}

	/**
	 * Starts a new recording.
	 */
	public boolean startRecording(int seconds) {
		isRecordingStart = true ;
		//        String state = android.os.Environment.getExternalStorageState();
		//        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
		//            throw new IOException("SD Card is not mounted.  It is " + state + ".");
		//        }
		try {
			// make sure the directory we plan to store the recording in exists
			File directory = new File(path).getParentFile();
			if (!directory.exists() && !directory.mkdirs()) {
				isRecordingStart = false ;
				throw new IOException("Path to file could not be created.");
			}
			
//			//Start Recording
//			recorder = CustomAudioRecorder.getInstanse(false);
//			// Compressed recording (AMR)
//			//recorder = CustomAudioRecorder.getInstanse(false); // Uncompressed recording (WAV)
//			recorder.setOutputFile(path);//filename
//			recorder.prepare();
//			recorder.start();

			
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			
			
			//High Quality Audio Recording
//			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
			
			recorder.setOutputFile(path);// must have an .m4a extension, if m4a is used
//			recorder.setAudioChannels(1);
//			recorder.setAudioSamplingRate(44100);
//			recorder.setAudioEncodingBitRate(96000);
			
			
			//For testing use this
//			path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/my_aac.m4a";
			
			//recorder.setMaxDuration(seconds * 1000);
			recorder.prepare();
			recorder.start();
			recorder.setOnErrorListener(this);
			recorder.setOnInfoListener(this);
			
		} catch (Exception ex) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "startRecording -- Error recording voice", ex);
			}
			isRecordingStart = false ;
			return false;
		}
		return true;
	}

	public void onCompletion(MediaPlayer mp) {
		mp.reset();
		if (null != mParent) {
			mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_COMPLETED);
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		mp.reset();
		if (null != mParent) {
			mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_FAILED);
		}
		return false;
	}

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	public void onError(MediaRecorder mr, int what, int extra) {
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
			try {
				stopRecorder();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_ERROR_UNKNOWN);
			}
			break;
		}
	}

	public void onInfo(MediaRecorder mr, int what, int extra) {
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED);
			}
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED);
			}
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_INFO_UNKNOWN);
			}
			break;
		}
	}

}
