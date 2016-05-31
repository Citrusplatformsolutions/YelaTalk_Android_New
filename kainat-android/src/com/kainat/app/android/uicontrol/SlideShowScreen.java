package com.kainat.app.android.uicontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kainat.util.CompressImage;
import com.kainat.app.android.CommunityChatActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.util.BitmapScaler;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SlideShowScreen extends UIActivityManager implements OnClickListener, VoiceMediaHandler {

	boolean slisShow= false;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 150;
	private static final int SWIPE_THRESHOLD_VELOCITY = 50;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	ViewFlipper imageFrame;
	List<String> ImageList;
	List imageViewVec;
	RelativeLayout slideShowBtn;
	Handler handler;
	Runnable runnable;
	ProgressBar pb;
	
	Gallery g;
	ArrayList<String> imagesPath = new ArrayList<String>();

	int width;
	TextView count;
	String mVoicePath= null; 
	//View time_panel;
	View time_panel;
	//	ImageAdapter adapter;

	private Thread flipThread;
	private int temp = -1;
	private int slideShowTime = 3000;

	public boolean availableVoice = false;
	LinearLayout media_play_layout,status_fields;
	TextView total_autio_time, played_autio_time;
	boolean voiceIsPlaying;
	private SeekBar baradd;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	String[] img_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		shouldStopMedia=false ;
		super.onCreate(savedInstanceState);

		/*if(!checkInternetConnection()){
			networkLossAlert();
			finish();
			return;
		}*/
		setContentView(R.layout.photo_slideshow_main);
		pb =(ProgressBar)findViewById(R.id.progressBar);
		 imageLoader = ImageLoader.getInstance();
		 options = new DisplayImageOptions.Builder().cacheInMemory(true)
						.cacheOnDisc(true).
						resetViewBeforeLoading(true)
						.showImageForEmptyUri(null)
						.showImageOnFail(null)
						.showImageOnLoading(null).build();
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
		imagesPath = new ArrayList<String>();
		imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);
		synch = new Vector<LoadMedia>();
		imageViewVec = new ArrayList();
		int index = 0 ;
		count = (TextView)findViewById(R.id.count);
		mVoicePath       = getIntent().getExtras().getString("AUDIO_URL");
		 img_url = getIntent().getExtras().getStringArray("IMAGE_ARR");
		/*if(CommunityChatActivity.slideBitmapArray != null){
			slideShow(CommunityChatActivity.slideBitmapArray);
		}*/
		if(img_url != null){
			slideShow(img_url);
		}
		
		else{
			finish();
			return;
		}
		// addFlipperImages2(imageFrame);//, parentFolder);
		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		//if (imagesPath.size() > 1)
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(imageFrame.isFlipping()){
					imageFrame.stopFlipping();
				}
				if (gestureDetector.onTouchEvent(event)){
					return true;
				}else
					return false;
			}
		};

		imageFrame.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showMenu();
				return false;
			}

		});
		handler = new Handler();
		//if (imagesPath.size() > 1)
		imageFrame.setOnClickListener(SlideShowScreen.this);
		//	if (imagesPath.size() > 1)
		imageFrame.setOnTouchListener(gestureListener);
		slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
		if (imagesPath.size() > 1)
			slideShowBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					slideShowBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {

							if(!slisShow)
								return;
							runnable = new Runnable() {

								@Override
								public void run() {

									handler.postDelayed(runnable, 3000);
									if(imageFrame.getDisplayedChild()>=imageUrl.size()-1)
									{imageFrame.setDisplayedChild(0) ;}
									imageFrame.showNext();
									count();
									switchImage();
								}
							};
							handler.postDelayed(runnable, 1000);
						}
					});
				}
			});

		g = (Gallery) findViewById(R.id.gallery1);
		// g.setSpacing(5);

		// Set the adapter to our custom adapter (below)
		//		g.setAdapter(adapter =new ImageAdapter(this));
		if (imagesPath.size() > 1)
			g.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						final int position, long id) {
					// ((TextView) findViewById(R.id.image_count)).setText("<" +
					// (position + 1) + "/" + adapter.getCount() + ">");
					handler.removeCallbacks(runnable);
					imageFrame.setDisplayedChild(position);
					// Intent intent = new Intent ();
					// intent.setAction(Intent.ACTION_VIEW);
					// Uri uri = Uri.fromFile(new
					// File(ImageList.get(imageFrame.getDisplayedChild())));
					// intent.setData(uri);
					// intent.setDataAndType(uri, "image/*");
					// startActivity(intent);
					// if(View.GONE ==
					// findViewById(R.id.gallery1).getVisibility())
					// findViewById(R.id.gallery1).setVisibility(View.VISIBLE);
					// else
					// findViewById(R.id.gallery1).setVisibility(View.GONE);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}

			});

		//		getCacheDirectory(this);
		count.setText((imageFrame.getDisplayedChild()+1)+"/"+img_url.length);
		//slideShow(img_url);
		
		
	}

	
	public void loadingPicture(String item, ImageView iv)
	{
		imageLoader.displayImage(item, iv, options, new SimpleImageLoadingListener() {
		    @Override
		    public void onLoadingStarted(String imageUri, View view) {
		    	pb.setVisibility(View.VISIBLE);
		    }

		    @Override
		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		    	pb.setVisibility(View.GONE);
		    }
		    
		    @Override
		    public void onLoadingFailed(String imageUri, View view,
		    		FailReason failReason) {
		    	// TODO Auto-generated method stub
		    	if(!checkInternetConnection()){
				networkLossAlert();
				finish();
				return;
			}
		    	//super.onLoadingFailed(imageUri, view, failReason);
		    }
		});
		
	}
	
	private void playAvailableVoice(String file_path, boolean auto_play) {
		media_play_layout = (LinearLayout) findViewById(R.id.media_play_layout);
		media_play_layout.setVisibility(View.VISIBLE);
		
		TextView imgStop = (TextView) findViewById(R.id.media_stop_play);
		imgStop.setOnClickListener(playerClickEvent);
		availableVoice = true;
		
		media_play_layout.setVisibility(View.VISIBLE);
		baradd = (SeekBar) media_play_layout
				.findViewById(R.id.mediavoicePlayingDialog_progressbar);
		if (((ImageView) media_play_layout.findViewById(R.id.media_play)) != null)
			((ImageView) media_play_layout.findViewById(R.id.media_play))
			.setOnClickListener(playerClickEvent);

		total_autio_time = ((TextView) media_play_layout
				.findViewById(R.id.audio_counter_max));
		played_autio_time = ((TextView) media_play_layout
				.findViewById(R.id.audio_counter_time));
		total_autio_time.setText("00:00)");
		played_autio_time.setText("(00:00 of ");
		openPlayScreen(file_path, auto_play, media_play_layout);
	}
	private void playAvailableVoice() {
		media_play_layout = (LinearLayout) findViewById(R.id.media_play_layout);
		media_play_layout.setVisibility(View.VISIBLE);
		
		TextView imgStop = (TextView) findViewById(R.id.media_stop_play);
		imgStop.setOnClickListener(playerClickEvent);
		availableVoice = true;
		// mVoiceMedia.startPlaying(mVoicePath, null);
		media_play_layout.setVisibility(View.VISIBLE);
		baradd = (SeekBar) media_play_layout
				.findViewById(R.id.mediavoicePlayingDialog_progressbar);
		if (((ImageView) media_play_layout.findViewById(R.id.media_play)) != null)
			((ImageView) media_play_layout.findViewById(R.id.media_play))
			.setOnClickListener(playerClickEvent);

		total_autio_time = ((TextView) media_play_layout
				.findViewById(R.id.audio_counter_max));
		played_autio_time = ((TextView) media_play_layout
				.findViewById(R.id.audio_counter_time));
		total_autio_time.setText("00:00)");
		played_autio_time.setText("(00:00 of ");


		if(mVoicePath.startsWith("http:")){
			openPlayScreen(mVoicePath, false, media_play_layout);
		}else{
			openPlayScreen(Downloader.getInstance().getPlayUrl(mVoicePath), false,
					media_play_layout);
		}
	}
	private View.OnClickListener playerClickEvent = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.media_play:
				ImageView imageView1 = null;
				try {

					if(!voiceIsPlaying){
						playAvailableVoice();
						return;
					}

					imageView1 = (ImageView) media_play_layout
							.findViewById(R.id.media_play);
					if (((String) v.getTag()).equals("PLAY")) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {

									// baradd.setTag(new Stats(object)) ;
									TextView tv = (TextView) media_play_layout
											.findViewById(R.id.streemStatus);
									tv.setTextColor(getResources().getColor(R.color.sub_heading));
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
						imageView1 = (ImageView) media_play_layout
								.findViewById(R.id.media_pause_play);
						imageView1.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
				}
				break;
			case R.id.media_stop_play:
				media_play_layout.setVisibility(View.GONE);
				voiceIsPlaying = false;
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
				break;
			}
		}
	};
	private void openPlayScreen(final String url, boolean autoPlay,
			final LinearLayout ln) {
		availableVoice = false;
		//		TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
		//		tv.setText("Please wait while getting audio...");
		voiceIsPlaying = true;
		RTMediaPlayer.setUrl(null);
		//tv.setTextColor(getResources().getColor(R.color.sub_heading));
		if (!autoPlay) {
			ImageView imageView1 = (ImageView) ln.findViewById(R.id.media_play);
			// imageView1.setTag(clickId+"^"+url);
			imageView1.setTag("PLAY");
			imageView1.setVisibility(View.INVISIBLE);
			imageView1.setOnClickListener(playerClickEvent);
		}
		RTMediaPlayer.setProgressBar(baradd);
		RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

			@Override
			public void voiceRecordingStarted(String recordingPath) {
			}

			@Override
			public void voiceRecordingCompleted(String recordedVoicePath) {
				// TODO Auto-generated method stub

			}

			public void voicePlayStarted() {
				if (availableVoice)
					return;
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
						imageView1.setOnClickListener(playerClickEvent);
						imageView1.setTag("PAUSE");
					}
					imageView1 = (ImageView) ln.findViewById(R.id.media_play);

					// imageView1.setTag(clickId+"^"+url);
					if (imageView1 != null) {
						imageView1.setOnClickListener(playerClickEvent);
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
				availableVoice = true;
				voiceIsPlaying = false;
				if(SlideShowScreen.this.handler != null)
					SlideShowScreen.this.handler.post(new Runnable() {
	
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
								media_play_layout.setVisibility(View.VISIBLE);
								RTMediaPlayer.clear();
								resetProgress();
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
					BusinessProxy.sSelf.animPlay = false;
					//					if (fullDialog != null && fullDialog.isShowing()) {
					//						fullDialog.dismiss();
					//					}
					// System.out.println("onerroe=======i");
					TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
					//tv.setTextColor(getResources().getColor(R.color.red));
					voiceIsPlaying = false;
					//tv.setText("Unable to play please try later!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onDureationchanged(final long total, final long current) {
				SlideShowScreen.this.handler.post(new Runnable() {

					@Override
					public void run() {
						try {

							total_autio_time.setText(""
									+ Utilities.converMiliSecond(total) + ")");
							total_autio_time.setTypeface(Typeface.DEFAULT_BOLD);
							played_autio_time.setText("("
									+ Utilities.converMiliSecond(current)
									+ " of ");
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
		ln.findViewById(R.id.streemStatus).setVisibility(View.GONE);
		// if(autoPlay)
		RTMediaPlayer._startPlay(url);
		ln.findViewById(R.id.media_play_layout).setVisibility(View.VISIBLE);
	}
	private void slideShow(final String[] bitmapArray){
		//final int[] imgArray = {R.drawable.entertainment, R.drawable.lifestyle, R.drawable.news, R.drawable.sports, R.drawable.popular};
		imageFrame.setInAnimation(AnimationUtils.loadAnimation(SlideShowScreen.this, R.anim.slide_left_in));
		imageFrame.setOutAnimation(AnimationUtils.loadAnimation(SlideShowScreen.this,R.anim.slide_left_out));
		imageFrame.setOnTouchListener(gestureListener);

		for(String item : bitmapArray){
			ImageView  textView = new ImageView(this);
			textView.setTag(item);
			//textView.setImageBitmap(item);
			if(item.contains("http")){
				loadingPicture(item, textView);
			//imageLoader.displayImage(item, textView, options);
			}else
			{
				item = "file://" +item ;
				loadingPicture(item, textView);
				//imageLoader.displayImage(item, textView, options);
			}
			imageFrame.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,	ViewGroup.LayoutParams.FILL_PARENT));
		}

		if(imageFrame.isFlipping()){
			imageFrame.stopFlipping();
		}else{
			if(bitmapArray.length > 1){
				imageFrame.setFlipInterval(slideShowTime);
				imageFrame.startFlipping();
			}
			flipThread = new Thread() {
				@Override
				public void run() {
					try {
						while(true) {
							sleep(100);
							SlideShowScreen.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									int pos = imageFrame.getDisplayedChild();
									if(pos != temp){
										count.setText((imageFrame.getDisplayedChild()+1)+"/"+img_url.length);
										temp = pos;
									}
								}
							});
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			flipThread.start();
		}
		if(mVoicePath != null && !mVoicePath.equals("")){
			String path = mVoicePath.substring(mVoicePath.lastIndexOf('/') + 1);
			if(isFileExists(path))
				playAvailableVoice(getLocalPath(path), false);
			else
				playAvailableVoice();
		}
	}

	@Override
	public void onBackPressed() {
		//	System.out.println("---------------------0-onBackPressed--");
		if(onBackPressedCheck())return;
		if(synch !=null)synch.clear() ;
		try{
			setResult(-999);
		}catch(Exception e){

		}
		//Stop Audio player, if Playing talking pics.
		if(RTMediaPlayer.isPlaying())
		{
			try {
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		finish();
		//		overridePendingTransition(R.anim.fade, R.anim.fade);
	}

	private void addFlipperImages2(ViewFlipper flipper) {
		try {
			int imageCount = imagesPath.size();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.FILL_PARENT);
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
					.getDefaultDisplay();
			int screenWidth = 200;// display.getWidth();
			int screenHeight = 200;// display.getHeight();
			String[] imageTypes = getResources().getStringArray(R.array.image);
			ImageList = new ArrayList<String>();
			imageViewVec = new ArrayList();
			flipper.removeAllViews();
			for (int count = 0; count < imageCount /*- 1*/; count++) {
				ImageView imageView = new ImageView(this);
				BitmapScaler scaler = null;
				try {
					String filename = imagesPath.get(count);
					//					System.out.println("----------------filename-----------"
					//							+ filename);
					ImageList.add(filename);
					//					System.out.println(filename);
					File f = new File(filename);
					scaler = new BitmapScaler(f, screenWidth);

					imageView.setImageBitmap(scaler.getScaled());

					imageViewVec.add(scaler.getScaled());
					// Bitmap imbm =
					// BitmapFactory.decodeFile(parent.listFiles()[count]
					// .getAbsolutePath());
					// imageView.setImageBitmap(imbm);
					imageView.setLayoutParams(params);
					flipper.addView(imageView);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					// TODO: handle exception
				}
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
		}
	}

	// http://zerocredibility.wordpress.com/2011/01/27/android-bitmap-scaling/
	/*private void addFlipperImages(ViewFlipper flipper, File parent) {
		try {
			int imageCount = parent.listFiles().length;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.FILL_PARENT);
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
					.getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			String[] imageTypes = getResources().getStringArray(R.array.image);
			ImageList = new ArrayList<String>();
			imageViewVec = new ArrayList();
			for (int count = 0; count < imageCount - 1; count++) {
				ImageView imageView = new ImageView(this);

				BitmapScaler scaler = null;
				try {
					String filename = parent.listFiles()[count]
							.getAbsolutePath();
					boolean isFile = false;
					for (final String type : imageTypes) {
						if (filename.endsWith("." + type)) {
							isFile = true;
							break;
						}
					}
					if (!isFile)
						continue;
					ImageList.add(filename);
					//					System.out.println(filename);
					File f = new File(filename);
					scaler = new BitmapScaler(f, screenWidth);

					imageView.setImageBitmap(scaler.getScaled());

					imageViewVec.add(scaler.getScaled());
					// Bitmap imbm =
					// BitmapFactory.decodeFile(parent.listFiles()[count]
					// .getAbsolutePath());
					// imageView.setImageBitmap(imbm);
					imageView.setLayoutParams(params);
					flipper.addView(imageView);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					// TODO: handle exception
				}
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
		}
	}
*/
	class MyGestureDetector extends SimpleOnGestureListener {

		// @Override
		// public boolean onDoubleTap(MotionEvent e) {
		// // TODO Auto-generated method stub
		// // showMenu();
		// return super.onDoubleTap(e);
		// }
		@SuppressWarnings("static-access")
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			if (View.GONE == findViewById(R.id.gallery1).getVisibility())
				findViewById(R.id.gallery1).setVisibility(View.VISIBLE);
			else
				findViewById(R.id.gallery1).setVisibility(View.GONE);
			if(!slisShow)return true;
			slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			handler.removeCallbacks(runnable);
			runnable = new Runnable() {

				@Override
				public void run() {
					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
				}
			};
			handler.postDelayed(runnable, 2000);
			return true;


		}

		@SuppressWarnings("static-access")
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if(!slisShow)return true;
			slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			handler.removeCallbacks(runnable);
			runnable = new Runnable() {

				@Override
				public void run() {
					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
				}
			};
			handler.postDelayed(runnable, 2000);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					//	handler.removeCallbacks(runnable);
					//					imageFrame.setInAnimation(inFromRightAnimation());
					//					imageFrame.setOutAnimation(outToLeftAnimation());
					if(imageFrame.getDisplayedChild()< 5 && img_url.length > 1){
						if(imageFrame.isFlipping()){
							imageFrame.stopFlipping();
						}
						imageFrame.setInAnimation(AnimationUtils.loadAnimation(SlideShowScreen.this, R.anim.slide_left_in));
						imageFrame.setOutAnimation(AnimationUtils.loadAnimation(SlideShowScreen.this,R.anim.slide_left_out));
						imageFrame.showNext();	
					}
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					//	handler.removeCallbacks(runnable);
					//					imageFrame.setInAnimation(inFromLeftAnimation());
					//					imageFrame.setOutAnimation(outToRightAnimation());
					if(imageFrame.getDisplayedChild()>0){
						if(imageFrame.isFlipping()){
							imageFrame.stopFlipping();
						}
						imageFrame.setInAnimation(AnimationUtils.loadAnimation(SlideShowScreen.this, R.anim.right_in));
						imageFrame.setOutAnimation(AnimationUtils.loadAnimation(SlideShowScreen.this,R.anim.right_out));
						imageFrame.showPrevious();	
					}
				}
			} catch (Exception e) {
				// nothing
			}
			g.setSelection(imageFrame.getDisplayedChild());
			count();

			return false;
		}

	}

	public void count(){
		count.setText((imageFrame.getDisplayedChild()+1)+"/"+img_url.length);
	}
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.imageFrames){
			if(imageFrame.isFlipping()){
				imageFrame.stopFlipping();
			}
		}
	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	public class ImageAdapter extends BaseAdapter {

		private int itemBackground;

		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.gallery1);
			itemBackground = a.getResourceId(
					R.styleable.gallery1_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			//			if(imageViewVec!=null)
			//			System.out.println("-----------------getcount---"+imageViewVec.size());
			//			if(imageFrame!=null)
			//				System.out.println("-----------------getcount---"+imageFrame.getChildCount());

			if (imageFrame == null)
				return -1;
			return imageFrame.getChildCount();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		private final Gallery.LayoutParams param = new Gallery.LayoutParams(
				100, 100);

		public View getView(int position, View convertView, ViewGroup parent) {

			/*
			 * LayoutInflater inflator = (LayoutInflater)
			 * mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 * convertView = inflator.inflate(R.layout.gallry_image, null);
			 * 
			 * ImageView m = (ImageView)convertView.findViewById(R.id.image);
			 * m.setImageBitmap(Imageview.get(position)); return m;
			 */
			//			System.out.println("--------------inside get view-------------");
			ImageView imageview = null;
			if (null == convertView) {
				imageview = new ImageView(mContext);
			} else {
				imageview = (ImageView) convertView;
			}

			//			imageview.s
			// imageview.setImageBitmap(Imageview.get(position));
			imageview.setTag(position);

			//			int phoneType = (width < 320) ? Constants.SMALL_PHONE
			//					: ((width >= 320 && width < 480) ? Constants.MEDIUM_PHONE
			//							: Constants.LARGE_PHONE);

			//			switch (phoneType) {
			//			case Constants.SMALL_PHONE:
			//				imageview.setLayoutParams(new Gallery.LayoutParams(150 / 2,
			//						120 / 2));
			//				break;
			//			case Constants.MEDIUM_PHONE:
			//				imageview.setLayoutParams(new Gallery.LayoutParams(150 / 2,
			//						120 / 2));
			//				break;
			//			case Constants.LARGE_PHONE:
			//				imageview.setLayoutParams(new Gallery.LayoutParams(150, 120));
			//				break;
			//			case Constants.XLARGE_PHONE:
			//				
			//				break;
			//			}

			Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(Utilities.getComposeThumbHeight(SlideShowScreen.this), Utilities.getComposeThumbHeight(SlideShowScreen.this));

			imageview.setPadding(5, 5, 5, 5) ;
			imageview.setLayoutParams(layoutParams);


			//			try{
			//				if(position < imagesPathAfterDounloaded.size() ){
			////				System.out.println("-------imagesPathAfterDounloaded.get(position)--------"+imagesPathAfterDounloaded.get(position));
			//					imageview.setImageDrawable((Drawable)imageViewVec.get(position));}
			//			}
			//	    		catch (Exception e) {
			//	    			
			//				}
			//				
			try{
				String url = imagesPath.get(position) ;
				System.out.println("----image url ----:"+url);
				getCacheDirectoryImagePrivate();

				String filename = String.valueOf(url.hashCode());
				String extension = "jpg";
				filename = filename + "." + extension;
				File f = new File(cacheDirImage, filename);
				Bitmap bm = null ;
				if (bm == null) {
					BitmapFactory.Options bfo = new BitmapFactory.Options();
					bfo.inSampleSize = 4;
					bm = BitmapFactory.decodeFile(f.getAbsolutePath(), bfo);
					//				bm = ImageUtils.rotateImage(path, bm);
				}
				bm = Bitmap.createScaledBitmap(bm, Utilities.getComposeThumbWidth(SlideShowScreen.this), Utilities.getComposeThumbHeight(SlideShowScreen.this), true);
				imageview.setImageBitmap(bm);

			}catch (Exception e) {
				// TODO: handle exception
			}
			catch (OutOfMemoryError e) {
				// TODO: handle exception
			}
			//			imageview.setBackgroundResource(itemBackground);
			imageview.setScaleType(ImageView.ScaleType.FIT_XY);

			return imageview;
		}

		private Context mContext;

	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	public void showMenu() {
		if (1 == 1)
			return;
		final Dialog dialog = new Dialog(SlideShowScreen.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.open_choice_dialog);
		dialog.setCancelable(true);

		TextView text1 = (TextView) dialog.findViewById(R.open_choice.message);
		text1.setText("");

		Button button1 = (Button) dialog.findViewById(R.open_choice.button1);
		button1.setText("Save");
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
				boolean s = Utilities.moveFileToSdCard(imagesPath
						.get(imageFrame.getDisplayedChild()));
				// addNotification("File sucessfully saved!",
				// "File sucessfully saved!") ;
				if (s)
					Toast.makeText(SlideShowScreen.this,
							"File sucessfully saved!", Toast.LENGTH_SHORT)
							.show();
				else
					Toast.makeText(SlideShowScreen.this,
							"Unable to save file!", Toast.LENGTH_SHORT).show();

			}
		});

		Button button2 = (Button) dialog.findViewById(R.open_choice.button2);
		button2.setText("Set as wallpapper");
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					getApplicationContext().setWallpaper(
							Utilities.getFileInputStream(imagesPath
									.get(imageFrame.getDisplayedChild())));
					Toast.makeText(SlideShowScreen.this,
							"File sucessfully set as wallpaper!",
							Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});
		Button cancelButton = (Button) dialog
				.findViewById(R.open_choice.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onStop() {


		super.onStop();

	}

	@Override
	protected void onPause() {
		try{
			recycleImageView();
			onBackPressed();
		}catch(Exception e){

		}

		super.onPause();
	}
	public void recycleImageView() {


		if (imageFrame != null) {
			imageFrame.setVisibility(View.GONE) ;
			for (int i = 0; i < imageFrame.getChildCount(); i++) {
				try {
					View v = imageFrame.getChildAt(i);
					if (v instanceof ImageView) {
						Drawable drawable = ((ImageView) v).getDrawable();
						if (drawable instanceof BitmapDrawable) {
							BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
							Bitmap bitmap = bitmapDrawable.getBitmap();
							bitmap.recycle();
							bitmapDrawable = null;
							System.out
							.println("--------------------recycle------");
						}
					}
				} catch (Exception e) {
					Log.v("Exception is",""+e.toString());
					e.printStackTrace();
				}
			}

		}
	}

	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT);
	List<String> imagesPathAfterDounloaded = new ArrayList<String>();
	private ImageView imageView;
	private LoadMedia currentLoadMedia = null;


	//	private static File cacheDir;

	//	private static File getCacheDirectory(Context context) {
	//
	//		if (cacheDir != null)
	//			return cacheDir;
	//		String sdState = android.os.Environment.getExternalStorageState();
	//		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
	//			File sdDir = android.os.Environment.getExternalStorageDirectory();
	//			cacheDir = new File(sdDir, Constants.imagePath);
	//		}
	//		else
	//			cacheDir = context.getCacheDir();
	//
	//		if (!cacheDir.exists())
	//			cacheDir.mkdirs();
	//		return cacheDir;
	//	}

	class LoadMedia extends AsyncTask<Object, Void, ZoomableImageView> {
		View view;
		// int pos =0;
		int pos;
		String parentID;
		String contentID;
		int contentType;

		public LoadMedia(int pos, String parentID, String contentID,
				int contentType, View view) {
			this.pos = pos;
			this.parentID = parentID;
			this.contentID = contentID;
			this.contentType = contentType;
			this.view = view;
			//			System.out.println("-------parentID--------" + parentID);
			//			System.out.println("-------contentID--------" + contentID);
			//			System.out.println("-------pos--------" + pos);
		}

		@Override
		protected ZoomableImageView doInBackground(Object... paramsl) {
			currentLoadMedia = this;
			if (view == null)
				view = (View) paramsl[0];
			try {
				String s = (String) view.getTag();
				Drawable	drawable = null ;
				//System.out.println("sssssssssssssssssssss="+s);
				String filename = String.valueOf(s.hashCode());

				/////
				String url = s ;
				imageUrl.add(url) ;
				////
				//				System.out.println("----image url ----:"+url);
				getCacheDirectoryImagePrivate();

				//				String filename = String.valueOf(url.hashCode());
				String extension = "jpg";
				filename = filename + "." + extension;
				File f = new File(cacheDirImage, filename);

				Bitmap myBitmap = ImageDownloader.getFromCache(url,ImageDownloader.TYPE_IMAGE); 

				//				if(Utilities.shouldCompressImage(f.length(),512)){
				//					myBitmap = Utilities.getCompressImage(f) ;
				//					if(myBitmap!=null)
				//					drawable = new BitmapDrawable(Resources.getSystem(),myBitmap);
				//				}
				CompressImage compressImage = new CompressImage(SlideShowScreen.this) ;
				if(myBitmap==null){

					try{
						if(pos==0){
							if(myBitmap!=null)
								drawable = new BitmapDrawable(getResources(),myBitmap);
							//						else
							//					drawable = Drawable.createFromPath(compressImage.compressImage(f.getAbsolutePath()));
							ImageDownloader.saveBigIamgeCache(((BitmapDrawable)drawable).getBitmap(), url);
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				//drawable = Drawable.createFromPath(f.getAbsolutePath());
				if(drawable==null  && !f.exists()){
					InputStream is = (InputStream) new URL(url).getContent();
					byte data[] = Utilities.inputStreamToByteArray(is);
					if(data.length>0)
					{
						filename = String.valueOf(url.hashCode());
						extension = "jpg";
						filename = filename + "." + extension;
						f = new File(cacheDirImage, filename);
						Utilities.writeFile(data, f, url.hashCode());
						//					drawable = Drawable.createFromPath(f.getAbsolutePath());

						myBitmap = null ;
						/*if(Utilities.shouldCompressImage(f.length(),512)){
						myBitmap = Utilities.getCompressImage(f) ;
						if(myBitmap!=null)
						drawable = new BitmapDrawable(Resources.getSystem(),myBitmap);
					}*/
						if(pos==0)
							if(myBitmap==null)
								//						drawable = Drawable.createFromPath(compressImage.compressImage(f.getAbsolutePath()));
								drawable = Drawable.createFromPath(f.getAbsolutePath());
						ImageDownloader.saveBigIamgeCache(((BitmapDrawable)drawable).getBitmap(), url);
					}
				}
				imageView = new ZoomableImageView(SlideShowScreen.this);
				imageView.setImageDrawable(drawable );
				imageView.setScaleType(ScaleType.FIT_CENTER);
				////
				//				String filename = String.valueOf(url.hashCode());

				/*String extension = MimeTypeMap.getFileExtensionFromUrl(s);
				if(s.indexOf(".")==-1)
					extension =  ImageDownloader.extensionDefault;//"jpg" ;
				if(extension==null||extension.trim().length()<=0){
					try{
						extension = s.substring(s.lastIndexOf(".")+1, s.length());
					}catch (Exception e) {
						extension =  ImageDownloader.extensionDefault;//"jpg" ;
					}
				}
				filename = filename+"."+extension ;

				File f = new File(cacheDirImage, filename);
				SoftReference<Bitmap> bitmapRef = (SoftReference<Bitmap>) ImageDownloader.imageCache
						.get(f.getPath());

				imageView = new ZoomableImageView(SlideShowScreen.this);
				Bitmap bitmap =null;
				if(bitmapRef==null||bitmapRef.get()==null){
					if(bitmapRef==null||bitmapRef.get()==null) {
						bitmap = BitmapFactory.decodeFile(f.getPath());
						bitmapRef = new SoftReference<Bitmap>(bitmap);
						if (bitmap != null) {
							ImageDownloader.imageCache.put(f.getPath(), bitmapRef);
						}
					}if(bitmapRef==null||bitmapRef.get()==null){
					bitmap = downloadBitmap(s) ;
					bitmapRef = new SoftReference<Bitmap>(bitmap);
					}
				}
				if(!checkInternetConnection()){
					networkLossAlert();
					finish();
				}
				if (bitmap == null) {
					int w = 50, h = 50;
					Bitmap.Config conf = Bitmap.Config.ARGB_8888; 
					bitmap = Bitmap.createBitmap(w, h, conf);
					Canvas canvas = new Canvas(bitmap);
				}

				ImageDownloader.imageCache.put(f.getPath(), bitmapRef);
				writeFile(bitmap, f);
				imagesPathAfterDounloaded.add(f.getPath());

				try{
					bitmapRef = (SoftReference<Bitmap>) ImageDownloader.imageCache
							.get(f.getPath());
					if(bitmapRef!=null){
					bitmap = bitmapRef.get();
					if(s.indexOf(".gif")!=-1){
						imageView=gifToJpeg(imageView);
					imageViewVec.add(new BitmapDrawable(imageView.getContext()
							.getResources(), bitmap));
					imageView.setImageBitmap(bitmap);
					}else{
						imageViewVec.add(new BitmapDrawable(imageView.getContext()
								.getResources(), bitmap));
						imageView.setImageBitmap(bitmap);
					}
					}
					}
		    	catch (Exception e) {
				}
				 */
				imageView.setLayoutParams(params);
				return imageView;


			} catch (Exception e) {
				e.printStackTrace();

				return null;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				makeToast(e.toString());
				return null;
			}
			// return null ;
		}
		ZoomableImageView imageView = null ;
		@Override
		protected void onPostExecute(ZoomableImageView imageViewl) {

			if (isCancelled()) {
				synch.clear();
				return;
			}
			imageView = imageViewl ;
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						if (imageView == null && !isCancelled()) {
							imageView = new ZoomableImageView(SlideShowScreen.this);
							//							BitmapScaler scaler = null;
							imageView.setBackgroundResource(R.drawable.media2big);
							imageView.setLayoutParams(params);
						}
						imageView.setTag(view.getTag());
						imageFrame.removeView(view);
						imageFrame.addView(imageView, pos);
						//						System.out.println("----------------------streem image created ");

						if(pos==0){
							//	imageFrame.showPrevious();
							//imageFrame.invalidate();
							if (android.os.Build.VERSION.SDK_INT >= 16.0) {
								// only for gingerbread and newer versions
								imageFrame.showPrevious();
								//Thread.sleep(1000);
								//imageFrame.showNext();
								//imageFrame.showPrevious();
								imageFrame.invalidate();
							}else{
								imageFrame.showNext();
								imageFrame.showPrevious();
								imageFrame.showNext();
								imageFrame.showPrevious();
								//Thread.sleep(1000);
								//imageFrame.showNext();
								//imageFrame.showPrevious();
								imageFrame.invalidate();
							}
						}
						if(imageFrame.getDisplayedChild()==pos)
							switchImage();
						//						if(synch.size()==1){
						//if(pos==0)
						{
							//							imageFrame.showPrevious();
							//							imageFrame.showNext();
							//							g.setSelection(/*g.getCount()-*/imageFrame.getDisplayedChild());							
						}
						//						g.setSelection(pos);
						//						adapter.notifyDataSetChanged();
						if(++pos < synch.size())
							((LoadMedia) synch.get(pos)).execute("");
						else{
							if(synch.size()>2){
								//								imageFrame.showNext();
								//								imageFrame.showNext();
							}
						}

					} catch (Exception e) {

						e.printStackTrace();
					}
				}
			});

		}
	}

	Vector synch = new Vector<LoadMedia>();

	static Bitmap downloadBitmap(String url) {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(params);

		final HttpGet getRequest = new HttpGet(url);
		try {
			getRequest.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
			getRequest.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					byte[] data = Utilities.readBytes(inputStream);// new

					Bitmap bitmap = null;

					bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, ImageDownloader.opt);
					return bitmap;
				} catch (Exception e) {
					e.printStackTrace();
					int w = 50, h = 50;
					Bitmap.Config conf = Bitmap.Config.ARGB_8888; 
					Bitmap bmp = Bitmap.createBitmap(w, h, conf); 
					Canvas canvas = new Canvas(bmp);
					return bmp;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			getRequest.abort();
			Log.w("ImageDownloader", "Error while retrieving bitmap from "
					+ url + e.toString());
			int w = 50, h = 50;
			Bitmap.Config conf = Bitmap.Config.ARGB_8888; 
			Bitmap bmp = Bitmap.createBitmap(w, h, conf); 
			Canvas canvas = new Canvas(bmp);
			return bmp;

		} finally {
			if (client != null) {
				// client.close();
			}
		}
		return null;
	}

	public ZoomableImageView gifToJpeg(ZoomableImageView view){
		RotateAnimation animation = new RotateAnimation(0f, 350f, 15f, 15f); 
		animation .setInterpolator(new LinearInterpolator());
		animation .setRepeatCount(Animation.INFINITE);
		animation .setDuration(700);

		// Start animating the image

		view.startAnimation(animation );

		// Later.. stop the animation
		view.setAnimation(null);
		return view;
	}
	//	private static void writeFile(Bitmap bmp, File f) {
	//		FileOutputStream out = null;
	//		if(f!=null&&f.exists())
	//		try {
	//			if(f.exists())
	//			out = new FileOutputStream(f);
	//			bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} finally {
	//			try {
	//				if (out != null)
	//					out.close();
	//			} catch (Exception ex) {
	//			}
	//		}
	//	}

	private static void writeFile(Bitmap bmp, File f) {
		//System.out.println("------------------image url write streem filename: "+f.getPath()) ;
		//System.out.println("------------------image url write streem filename: "+f.getAbsolutePath()) ;
		FileOutputStream out = null;
		if(f!=null&&!f.exists())
			try {
				//if(f.exists())

				f.createNewFile();
				out = new FileOutputStream(f);
				bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
				//System.out.println("------------------image url write streem filename saved: "+f.getPath()) ;
			} catch (Exception e) {
				//System.out.println("------image download streem : "+e.getMessage());
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
	}
	private void switchImage() {
		View linerlayoutToBeLoad =null;

		System.out
		.println("--------------------imageFrame index------"+imageFrame.getDisplayedChild());
		int selectedIndex = imageFrame.getDisplayedChild() ;
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < imageFrame.getChildCount(); i++) {
			try {

				View linerlayout = imageFrame.getChildAt(i);
				//				if (linerlayout instanceof ImageView) 
				//				{
				//					
				//				}else
				//					return;
				imageFrame.removeView(linerlayout);
				System.out
				.println("--------------------imageFrame index------"+imageFrame.getDisplayedChild());

				if(i==imageFrame.getDisplayedChild())
					linerlayoutToBeLoad = linerlayout ;


				linerlayout.setVisibility(View.GONE) ;
				linerlayout.setId(i);

				if (linerlayout instanceof ImageView) {
					//					ImageView imageView = (ImageView) linerlayout;
					Drawable drawable = ((ImageView)linerlayout).getDrawable();
					if (drawable instanceof BitmapDrawable) {
						BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
						Bitmap bitmap = bitmapDrawable.getBitmap();
						bitmap.recycle();
						bitmapDrawable = null;
						System.out
						.println("--------------------recycle------"+imageFrame.getChildCount());
					}
				}


				View bar = inflator.inflate(R.layout.progress_view, null);
				imageFrame.addView(bar,i);
				imageFrame.setDisplayedChild(selectedIndex);
				imageFrame.invalidate();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if(linerlayoutToBeLoad!=null){
			LoadImage loadImage = new LoadImage();
			loadImage.execute(linerlayoutToBeLoad);
		}
	}
	Vector<String> imageUrl = new Vector<String>();
	class LoadImage extends AsyncTask<View, Void, View> {
		View linerlayout;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}
		CImageView imageView;
		@Override
		protected View doInBackground(View... urls) {

			try{
				Drawable drawable =null ;
				linerlayout = urls[0] ;
				//			linerlayout.setVisibility(View.VISIBLE);
				String url = imageUrl.get(linerlayout.getId());//(String)linerlayout.getTag() ;
				//			for (int j = 0; j < linerlayout.getChildCount(); j++) 
				{
					//				View view = linerlayout.getChildAt(j);
					//				view.setVisibility(View.VISIBLE) ;
					//				linerlayout.removeAllViews();
					//if (view instanceof ImageView) 
					{
						imageView = new CImageView(SlideShowScreen.this);
						imageView.setLayoutParams(params) ;
						//				linerlayout.addView(imageView) ;
						String filename = String.valueOf(url.hashCode());
						String extension = "jpg";
						filename = filename + "." + extension;
						File f = new File(cacheDirImage, filename);
						System.out.println("----image url --LoadImage 1--:"+url +" :size : "+(f.length()/1024));
						//				Bitmap myBitmap = null ;
						Bitmap myBitmap = ImageDownloader.getFromCache(url,ImageDownloader.TYPE_IMAGE); 
						if(myBitmap!=null)
							drawable = new BitmapDrawable(getResources(),myBitmap);
						/*if(Utilities.shouldCompressImage(f.length())){
					myBitmap = Utilities.getCompressImage(f) ;
					if(myBitmap!=null)
					drawable = new BitmapDrawable(Resources.getSystem(),myBitmap);
				}*/
						CompressImage compressImage = new CompressImage(SlideShowScreen.this) ;
						if(myBitmap==null){
							try{
								//					drawable = Drawable.createFromPath(compressImage.compressImage(f.getAbsolutePath()));
								drawable = Drawable.createFromPath(f.getAbsolutePath());
								ImageDownloader.saveBigIamgeCache(((BitmapDrawable)drawable).getBitmap(), url);
							}catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						}
						imageView.setImageDrawable(drawable );
						imageView.setVisibility(View.VISIBLE);
						return imageView;
						//				break;
					}
				}
			}catch (Exception e) {
				e.fillInStackTrace() ;
				return null;
			}
			catch (OutOfMemoryError e) {
				e.fillInStackTrace() ;
				return null;
			}


		}

		@Override
		protected void onPostExecute(View view) {
			//			if(view!=null)
			//			System.out.println("----image url --LoadImage index--:"+linerlayout.getId());
			//			imageFrame.removeViewAt(linerlayout.getId());
			//			imageFrame.addView(imageView,linerlayout.getId());
			//			imageFrame.setDisplayedChild(linerlayout.getId());
			//			imageFrame.invalidate() ;
			try{
				System.out.println("----image url --LoadImage index--:"+linerlayout.getId());
				imageFrame.removeViewAt(linerlayout.getId());
				if(view!=null){
					imageFrame.addView(imageView,linerlayout.getId());
				}else
				{
					LayoutInflater inflator = (LayoutInflater) SlideShowScreen.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View bar = inflator.inflate(R.layout.progress_view, null);
					imageFrame.addView(bar,linerlayout.getId());	
				}
				imageFrame.setDisplayedChild(linerlayout.getId());
				imageFrame.invalidate() ;
			}catch (Exception e) {
				e.printStackTrace() ;
			}
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		//		overridePendingTransition(R.anim.fade, R.anim.fade);
	}
	private boolean isFileExists(String file_name)
	{
		String dir = Environment.getExternalStorageDirectory().getPath();
		String fullPath =new StringBuffer(dir).append('/').append(file_name).toString();
		return new File(fullPath).isFile();
	}
	private String getLocalPath(String file_name)
	{
		String dir = Environment.getExternalStorageDirectory().getPath();
		String fullPath =new StringBuffer(dir).append('/').append(file_name).toString();
		return fullPath;
	}

}