package com.kainat.app.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.BitmapScaler;
import com.kainat.app.android.util.Constants;

public class PhotoSlideShowActivity extends Activity implements OnClickListener {

	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 150;
	private static final int SWIPE_THRESHOLD_VELOCITY = 50;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	ViewFlipper imageFrame;
	List<String> ImageList;
	List<Bitmap> Imageview;
	RelativeLayout slideShowBtn;
	Handler handler;
	Runnable runnable;
	File parentFolder;
	Gallery g;
	List<String> imagesPath;
	int width ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_slideshow_main);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
		
		imagesPath = getIntent().getStringArrayListExtra("DATA");
		if (null == imagesPath) {
			finish();
			return;
		}
		imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);

		//get sd card path for images

		parentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		// + "/DCIM/Camera");

		addFlipperImages2(imageFrame);//, parentFolder);
		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		if(imagesPath.size()>1)
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event))
					return true;
				else
					return false;
			}
		};
		handler = new Handler();
		if(imagesPath.size()>1)
		imageFrame.setOnClickListener(PhotoSlideShowActivity.this);
		if(imagesPath.size()>1)
		imageFrame.setOnTouchListener(gestureListener);
		slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
		if(imagesPath.size()>1)
		slideShowBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

//				runnable = new Runnable() {
//
//					@Override
//					public void run() {
//						
//						handler.postDelayed(runnable, 3000);
//						Animation a = AnimationUtils.loadAnimation(PhotoSlideShowActivity.this, android.R.anim.fade_in) ;
//			            imageFrame.setInAnimation(a);
//			            a = AnimationUtils.loadAnimation(PhotoSlideShowActivity.this, android.R.anim.fade_out) ;
//						imageFrame.setOutAnimation(a);
//						imageFrame.showNext();
//						g.setSelection(imageFrame.getDisplayedChild());
//
//					}
//				};
//				handler.postDelayed(runnable, 500);
			}
		});

		g = (Gallery) findViewById(R.id.gallery1);
//		g.setSpacing(5);

		// Set the adapter to our custom adapter (below)
		g.setAdapter(new ImageAdapter(this));
		if(imagesPath.size()>1)
		g.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int position, long id) {
				//                ((TextView) findViewById(R.id.image_count)).setText("<" + (position + 1) + "/" + adapter.getCount() + ">");
				handler.removeCallbacks(runnable);
				imageFrame.setDisplayedChild(position);
				//            	Intent intent = new Intent ();
				//                intent.setAction(Intent.ACTION_VIEW);
				//                Uri uri = Uri.fromFile(new File(ImageList.get(imageFrame.getDisplayedChild())));
				//                intent.setData(uri);
				//                intent.setDataAndType(uri, "image/*");
				//                startActivity(intent);
				if(View.GONE == findViewById(R.id.gallery1).getVisibility())
					findViewById(R.id.gallery1).setVisibility(View.VISIBLE);
				else
					findViewById(R.id.gallery1).setVisibility(View.GONE);
			}

			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Photo SlideShow Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	private void addFlipperImages2(ViewFlipper flipper) {
		try {
			int imageCount = imagesPath.size();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			String[] imageTypes = getResources().getStringArray(R.array.image);
			ImageList = new ArrayList<String>();
			Imageview = new ArrayList<Bitmap>();
			flipper.removeAllViews();
			for (int count = 0; count < imageCount /*- 1*/; count++) {
				ImageView imageView = new ImageView(this);
 
				BitmapScaler scaler = null;
				try {
					String filename = imagesPath.get(count);
					//System.out.println("----------------filename-----------"+filename);
					ImageList.add(filename);
					//System.out.println(filename);
					File f = new File(filename);
					scaler = new BitmapScaler(f, screenWidth);

					imageView.setImageBitmap(scaler.getScaled());

					Imageview.add(scaler.getScaled());
					//            Bitmap imbm = BitmapFactory.decodeFile(parent.listFiles()[count]
					//                    .getAbsolutePath());
					//            imageView.setImageBitmap(imbm);
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

	//    http://zerocredibility.wordpress.com/2011/01/27/android-bitmap-scaling/
	private void addFlipperImages(ViewFlipper flipper, File parent) {
		try {
			int imageCount = parent.listFiles().length;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			String[] imageTypes = getResources().getStringArray(R.array.image);
			ImageList = new ArrayList<String>();
			Imageview = new ArrayList<Bitmap>();
			for (int count = 0; count < imageCount - 1; count++) {
				ImageView imageView = new ImageView(this);

				BitmapScaler scaler = null;
				try {
					String filename = parent.listFiles()[count].getAbsolutePath();
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
					//System.out.println(filename);
					File f = new File(filename);
					scaler = new BitmapScaler(f, screenWidth);

					imageView.setImageBitmap(scaler.getScaled());

					Imageview.add(scaler.getScaled());
					//            Bitmap imbm = BitmapFactory.decodeFile(parent.listFiles()[count]
					//                    .getAbsolutePath());
					//            imageView.setImageBitmap(imbm);
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

	class MyGestureDetector extends SimpleOnGestureListener {
		@SuppressWarnings("static-access")
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			/*slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			handler.removeCallbacks(runnable);
			runnable = new Runnable() {

				@Override
				public void run() {
					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
				}
			};
			handler.postDelayed(runnable, 2000);

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			File src = new File(ImageList.get(imageFrame.getDisplayedChild()));
			
			
			String pathf = Environment.getExternalStorageDirectory().getAbsolutePath() +"/.RockeTalk";//// "/RockeTalk/Chat/";
			File destf = new File(pathf);
			try {
				boolean exists = destf.exists();
				if (!exists) {
					destf.mkdirs();
				}				
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/.RockeTalk/sys.jpg";//// "/RockeTalk/Chat/";
			File dest = new File(path);
			
			FileIOUtility.copyFile(src, dest);
			
			Uri uri = Uri.fromFile(dest.getAbsoluteFile());//new File(ImageList.get(imageFrame.getDisplayedChild())));
			intent.setData(uri);
			intent.setDataAndType(uri, "image/*");
			startActivity(intent);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			return true;
		}

		@SuppressWarnings("static-access")
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
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
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					handler.removeCallbacks(runnable);
					imageFrame.setInAnimation(inFromRightAnimation());
					imageFrame.setOutAnimation(outToLeftAnimation());
					imageFrame.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					handler.removeCallbacks(runnable);
					imageFrame.setInAnimation(inFromLeftAnimation());
					imageFrame.setOutAnimation(outToRightAnimation());
					imageFrame.showPrevious();

				}
			} catch (Exception e) {
				// nothing
			}
			g.setSelection(imageFrame.getDisplayedChild());
			return false;
		}

	}

	@Override
	public void onClick(View view) {

	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.2f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.2f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.2f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.2f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
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
			return Imageview.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		private final Gallery.LayoutParams param = new Gallery.LayoutParams(100, 100);

		public View getView(int position, View convertView, ViewGroup parent) {

			/*
			 * LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); convertView = inflator.inflate(R.layout.gallry_image, null);
			 * 
			 * ImageView m = (ImageView)convertView.findViewById(R.id.image); m.setImageBitmap(Imageview.get(position)); return m;
			 */

			ImageView imageview = null;
			if (null == convertView) {
				imageview = new ImageView(mContext);
			} else {
				imageview = (ImageView) convertView;
			}
			imageview.setImageBitmap(Imageview.get(position));
			imageview.setTag(position);
			//            Bitmap bitmap = BitmapFactory.decodeFile(mItems.get(position),  BITMAP_OPTIONS);
			//            imageview.setImageBitmap(bitmap);
			//            imageview.setImageURI(Uri.parse(mItems.get(position)));
//			imageview.setLayoutParams(param);
			 int phoneType = (width < 320) ? Constants.SMALL_PHONE : ((width >= 320 && width < 480) ? Constants.MEDIUM_PHONE : Constants.LARGE_PHONE);
			 
			 switch (phoneType) {
				case Constants.SMALL_PHONE:
					imageview.setLayoutParams(new Gallery.LayoutParams(150/2, 120/2));
					break;
				case Constants.MEDIUM_PHONE:
					imageview.setLayoutParams(new Gallery.LayoutParams(150/2, 120/2));
					break;
				case Constants.LARGE_PHONE:
					imageview.setLayoutParams(new Gallery.LayoutParams(150, 120));
					break;
				case Constants.XLARGE_PHONE:
					imageview.setLayoutParams(new Gallery.LayoutParams(150, 120));
					break;
				}
			
			imageview.setBackgroundResource(itemBackground);
			imageview.setScaleType(ImageView.ScaleType.FIT_XY);

//			imagevsiew.setBackgroundResource(R.drawable.im_avatar_picture_border_normal);
			//((TextView) findViewById(R.id.image_count)).setText("<" + (position+1) + "/" + mItems.size() + ">");
			
			return imageview;
		}

		private Context mContext;

	}
	
}
