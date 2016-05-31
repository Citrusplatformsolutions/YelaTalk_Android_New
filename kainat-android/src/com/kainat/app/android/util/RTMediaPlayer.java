package com.kainat.app.android.util;

import java.io.FileInputStream;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kainat.app.android.YelatalkApplication;
import com.kainat.app.android.engine.VoiceMediaHandler;

public class RTMediaPlayer {
	private static boolean _dirty;
	private static boolean _loop;
	private static MediaPlayer _mediaPlayer = new MediaPlayer();
	private static boolean _paused;
	private static boolean _prepared;
	private static VoiceMediaHandler mediaHandler;
	private static int bufferRet = 0 ;
	private boolean log = false;
	private static final String TAG = RTMediaPlayer.class.getSimpleName();
	static {
		_dirty = true;
		_loop = false;
		_paused = false;
		_prepared = false;
	}

	public static void setMediaHandler(VoiceMediaHandler mediaHandlerl) {
		mediaHandler = mediaHandlerl;
	}

	public static String getUrl() {
		return urln;
	}
	public static void setUrl(String url) {
		urln = url;
	}
	// public static boolean isPlaying(){
	// return _mediaPlayer.isPlaying();
	// }
	private static String urln;

	public static boolean playFile(String path) {

		try {
			_mediaPlayer = new MediaPlayer();

			FileInputStream fin = new FileInputStream(path);
			_mediaPlayer.setDataSource(fin.getFD());

			_mediaPlayer.prepare();
			_mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					// System.out.println("-----------indide prepare method----");
					if (_prepared) {
						if(!mp.isPlaying()){
							setStreem();
							mp.start();
						}
						mediaHandler.voicePlayStarted();
						startProgressBar();
					}
					// System.out.println("-----------getDuration()--------"
					// + getDuration());
				}
			});
			_mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if(!isProxyNotSupport())
						YelatalkApplication.am.setSpeakerphoneOn(false); 
					mediaHandler.onError(1);
					return false;
				}
			});
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if(!isProxyNotSupport())
						YelatalkApplication.am.setSpeakerphoneOn(false); 
					playerStart = false;
				}
			});
			_mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {				
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					bufferRet = percent ;
				}
			});
			//Why this value is assigned to 100?? May be old code, someone has written
//			bufferRet = 100;
			_mediaPlayer.start();
			startProgressBar();
			fin.close();
		} catch (IOException e) {

			return false;
		} catch (Exception ex) {

			return false;
		}
		return true;
	}
	public static boolean _startPlay(String url) {
		//		url = "http://74.208.228.56/akm/yashpreet/Jagjit.mp3" ;
		//		 System.out.println("-----------streem url---"+url);
		_prepared = true;
		_paused = false;
		callRinging = false ;
		urln = url;
		bufferRet = 0 ;
		try {
			if (_mediaPlayer.isPlaying())
				_mediaPlayer.pause();
			_mediaPlayer.reset();

			if (url.indexOf("http:") != -1){
				_mediaPlayer.setDataSource(url);
				_mediaPlayer.prepareAsync();
			}
			else {
				return playFile(url);
			}

			_mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					// System.out.println("-----------indide prepare method----");
					if (_prepared && !callRinging) {
						if(!mp.isPlaying()){
							//							RocketalkApplication.am.setMode(AudioManager.STREAM_MUSIC);
							//							_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) ;
							setStreem();
							mp.start();
						}
						mediaHandler.voicePlayStarted();
						startProgressBar();
					}
					if(callRinging){
						mediaHandler.voicePlayCompleted();
					}
					// System.out.println("-----------getDuration()--------"
					// + getDuration());
				}
			});
			_mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if(!isProxyNotSupport())
						YelatalkApplication.am.setSpeakerphoneOn(false); 
					mediaHandler.onError(1);
					return false;
				}
			});
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if(!isProxyNotSupport())
						YelatalkApplication.am.setSpeakerphoneOn(false); 
					playerStart = false;
				}
			});
			_mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {				
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					bufferRet = percent ;
				}
			});
			int i = 1;
			return true;
		} catch (Exception j) {
			return false;
		}
	}

	public static void clear() {
		reset();
		if(progressBar!=null)
		progressBar.setProgress(0);
		_paused = false;
		_dirty = true;
	}
	public static int getTotbuffer() {
		//		int i = _mediaPlayer.getCurrentPosition();// etCurrentMediaTime();
		return bufferRet;
	}
	public static int getCurrentMediaTime() {
		int i = _mediaPlayer.getCurrentPosition();// etCurrentMediaTime();
		return i;
	}

	public static int getDuration() {
		int i = _mediaPlayer.getDuration();
		return i;
	}

	public static MediaPlayer getPlayer() {
		return _mediaPlayer;
	}

	public static boolean isDirty() {
		return _dirty;
	}

	public static boolean isLooping() {
		return _loop;
	}

	public static boolean isPaused() {
		return _paused;
	}

	public static boolean isPlaying() {
		return _mediaPlayer.isPlaying();
	}

	public static void pause() {
		// if(_paused){
		// play();
		// }
		// else{
		if(!isProxyNotSupport())
			YelatalkApplication.am.setSpeakerphoneOn(false);
		_mediaPlayer.pause();
		_paused = true;
		// }
	}

	public static void resume() {

		_mediaPlayer.start();
		_paused = false;

	}

	public static void play() {
		if (_mediaPlayer.isPlaying())
			_mediaPlayer.pause();
		_mediaPlayer.start();
		_paused = false;
	}

	public static void start() {
		// if (_mediaPlayer.isPlaying())
		// _mediaPlayer.pause();
		callRinging = false ;
		_mediaPlayer.start();
		if (!playerStart) {
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					playerStart = false;
				}
			});
			startProgressBar();
			playerStart = true;
		}
		_paused = false;
	}

	public static void reset() {
		//		System.out.println("------------------------reset--------------------");
		setStreemSystem();
		playerStart = false;
		if(_mediaPlayer != null)
		{
			_mediaPlayer.reset();
		}
		_prepared = false;
		playerStart=false;
	}
	public static void resetPlayer() {
		if(_mediaPlayer != null)
		{
			_mediaPlayer.reset();
			_mediaPlayer.release();
		}
	}

	public static void seek(int seekTime) {
		// playerStart = false;
		_mediaPlayer.seekTo(seekTime);
		// _prepared = false;
	}

	public static void resetDirty() {
		_dirty = false;
	}

	public static void setDirty() {
		_dirty = true;
	}

	public static void setLooping(boolean paramBoolean) {
		_loop = paramBoolean;
	}

	private static ProgressBar progressBar;
	private static TextView total_autio_time;
	private static TextView played_autio_time;
	private static int playingPercentage = -1;

	public static void setTimeView(TextView total_autio_timeL,TextView played_autio_timeL){
		total_autio_time = total_autio_timeL;
		played_autio_time = played_autio_timeL;
	}
	public static void setProgressBar(ProgressBar progressBarl) {
		progressBar = progressBarl;
	}

	public static void setProgressBar(ProgressBar progressBarl,int position) {
		progressBar = progressBarl;
		playingPercentage = position;
	}

	public static void sendStopNotification() {
		playerStart = false ;
		mediaHandler.voicePlayCompleted();
	}

	private static boolean playerStart = false;
	private static boolean callRinging = false;
	private static void startProgressBar() {
		if(progressBar != null){
			progressBar.setProgress(0);
			progressBar.setMax(RTMediaPlayer.getDuration());
			progressBar.setEnabled(true);
		}
		class ProgressRunner implements Runnable {

			public ProgressRunner() {
			}

			@Override
			public void run() {
				int progress = RTMediaPlayer.getCurrentMediaTime();
				playerStart = true;
				if(!isProxyNotSupport())
					YelatalkApplication.am.setSpeakerphoneOn(true); 
				while (playerStart && progress < progressBar.getMax()) {
					try {
						changeSpeekerToHeadPhone();
						progress = RTMediaPlayer.getCurrentMediaTime();
						mediaHandler.onDureationchanged(RTMediaPlayer.getDuration(), progress);
						if(progressBar != null){
							progressBar.setProgress(progress);
						}
						Thread.sleep(100);
					} catch (Exception e) {
						sendStopNotification();
						return;
					}
				}
				sendStopNotification();
			}
		}

		new Thread(new ProgressRunner()).start();
	}
	public static void changeSpeekerToHeadPhone(){
		if(!isProxyNotSupport()){
			if(YelatalkApplication.proximityChanged){
				YelatalkApplication.am.setSpeakerphoneOn(true); 
			}else{
				YelatalkApplication.am.setSpeakerphoneOn(false); 
			}
		}
	}
	public static void setStreem(){
		if(!isProxyNotSupport()){
			YelatalkApplication.am.setMode(AudioManager.STREAM_MUSIC);
			if(_mediaPlayer!=null  && _mediaPlayer.isPlaying())
				_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) ;
		}
	}
	public static void setStreemSystem(){
		if(!isProxyNotSupport()){
			if(playerStart){
				playerStart = false;
				YelatalkApplication.am.setSpeakerphoneOn(false); 
				YelatalkApplication.am.setMode(AudioManager.STREAM_SYSTEM);
				if(_mediaPlayer!=null && _mediaPlayer.isPlaying())
					_mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM) ;
			}
		}
	}
	public static void setCallStreemSystem(){
		if(!isProxyNotSupport()){
			if(playerStart){
				YelatalkApplication.am.setSpeakerphoneOn(false); 
				YelatalkApplication.am.setMode(AudioManager.STREAM_SYSTEM);
				if(_mediaPlayer!=null && _mediaPlayer.isPlaying())
					_mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM) ;
				playerStart = false;
			}
		}
	}
	public static void callRinging(){
		callRinging = true;
	}
	public static void callEnd(){
		callRinging = false;
	}


	public static boolean isProxyNotSupport(){
		if(BuildInfo.getInstance().MANUFACTURER.equalsIgnoreCase("samsung"))
			return true;
		else
			return false ;
	}
}