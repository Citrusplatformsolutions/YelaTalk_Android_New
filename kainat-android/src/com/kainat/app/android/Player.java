package com.kainat.app.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class Player extends Activity implements OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
	private int width = 0;
	private int height = 0;
	private MediaPlayer player;
	private TappableSurfaceView surface;
	private SurfaceHolder holder;
	private View topPanel = null;
	private View bottomPanel = null;
	private long lastActionTime = 0L;
	private boolean isPaused = false;
	private Button go = null;
	private ProgressBar timeline = null;
	private ImageButton media = null;
	private String url;
	private ProgressDialog dialog;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

//		Thread.setDefaultUncaughtExceptionHandler(onBlooey);

		setContentView(R.layout.player);

		surface = (TappableSurfaceView) findViewById(R.id.surface);
		surface.addTapListener(onTap);
		holder = surface.getHolder();
		holder.addCallback(this);
		holder.setFixedSize(176, 144);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		topPanel = findViewById(R.id.top_panel);
		bottomPanel = findViewById(R.id.bottom_panel);

		timeline = (ProgressBar) findViewById(R.id.timeline);

		media = (ImageButton) findViewById(R.id.media);
		media.setOnClickListener(onMedia);

		go = (Button) findViewById(R.id.go);
		go.setOnClickListener(onGo);

		url = getIntent().getStringExtra("url");
	}

	@Override
	protected void onResume() {
		super.onResume();

		isPaused = false;
		surface.postDelayed(onEverySecond, 1000);
	}

	@Override
	protected void onPause() {
		super.onPause();

		isPaused = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (player != null) {
			player.release();
			player = null;
		}

		surface.removeTapListener(onTap);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		lastActionTime = SystemClock.elapsedRealtime();

		if (keyCode == KeyEvent.KEYCODE_BACK && (topPanel.getVisibility() == View.VISIBLE || bottomPanel.getVisibility() == View.VISIBLE)) {
			clearPanels(true);

			return (true);
		}

		return (super.onKeyDown(keyCode, event));
	}

	public void onCompletion(MediaPlayer arg0) {
		media.setEnabled(false);
	}

	public synchronized void onPrepared(MediaPlayer mediaplayer) {
		width = player.getVideoWidth();
		height = player.getVideoHeight();
		if (width != 0 && height != 0) {
			dialog.dismiss();
			holder.setFixedSize(width, height);
			timeline.setProgress(0);
			timeline.setMax(player.getDuration());
			player.start();
		}

		media.setEnabled(true);
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		// no-op
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		// no-op
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// no-op
	}

	public synchronized void playVideo() {
		try {
			media.setEnabled(false);
			if (player != null) {
				synchronized (player) {
					player.stop();
					player.release();
					player.reset();
					player = null;
				}
			}
			player = new MediaPlayer();//.create(Player.this, Uri.parse(url), holder);
			player.setScreenOnWhilePlaying(true);
			player.setDataSource(url);
			player.setDisplay(holder);
			//			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setOnPreparedListener(Player.this);
			player.prepareAsync();
			player.setOnCompletionListener(Player.this);
		} catch (Throwable t) {
			dialog.dismiss();
			Log.e("Player", "Exception in media prep", t);
			goBlooey(t);
		}
	}

	private void clearPanels(boolean both) {
		lastActionTime = 0;

		bottomPanel.setVisibility(View.GONE);
	}

	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
	}

	private TappableSurfaceView.TapListener onTap = new TappableSurfaceView.TapListener() {
		public void onTap(MotionEvent event) {
			lastActionTime = SystemClock.elapsedRealtime();

			bottomPanel.setVisibility(View.VISIBLE);
		}
	};

	private Runnable onEverySecond = new Runnable() {
		public void run() {
			if (lastActionTime > 0 && SystemClock.elapsedRealtime() - lastActionTime > 3000) {
				clearPanels(false);
			}

			if (player != null) {
				timeline.setProgress(player.getCurrentPosition());
			}

			if (!isPaused) {
				surface.postDelayed(onEverySecond, 1000);
			}
		}
	};

	private View.OnClickListener onGo = new View.OnClickListener() {
		public void onClick(View v) {
			topPanel.setVisibility(View.GONE);
			dialog = ProgressDialog.show(Player.this, "", "Loading...");
			new Thread(new Runnable() {
				public void run() {
					playVideo();
					clearPanels(true);
					dialog.dismiss();
				}
			}).start();
		}
	};

	private View.OnClickListener onMedia = new View.OnClickListener() {
		public void onClick(View v) {
			lastActionTime = SystemClock.elapsedRealtime();

			if (player != null) {
				if (player.isPlaying()) {
					media.setImageResource(R.drawable.ic_media_play);
					player.pause();
				} else {
					media.setImageResource(R.drawable.ic_media_pause);
					player.start();
				}
			}
		}
	};

//	private Thread.UncaughtExceptionHandler onBlooey = new Thread.UncaughtExceptionHandler() {
//		public void uncaughtException(Thread thread, Throwable ex) {
//			Log.e("Player", "Uncaught exception", ex);
//			goBlooey(ex);
//		}
//	};
}