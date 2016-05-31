package com.kainat.app.android.uicontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kainat.app.android.R;
import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.BitmapScaler;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;

public class MultiPhotoViewerMedia extends UIActivityManager implements OnClickListener {

	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 150;
	private static final int SWIPE_THRESHOLD_VELOCITY = 50;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	ViewFlipper imageFrame;
	List<String> ImageList;
//	List<Bitmap> Imageview;
	RelativeLayout slideShowBtn;
	Handler handler;
	Runnable runnable;
	File parentFolder;
	Gallery g;
	List<String> imagesPath;
	int width ;
	boolean isRecycleTrue = false; 
	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_slideshow_main);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
		memory();
		imagesPath = getIntent().getStringArrayListExtra("DATA");
		if (null == imagesPath) {
			finish();
			return;
		}
		imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);

		//get sd card path for images

		parentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		// + "/DCIM/Camera");
		if(BusinessProxy.sSelf.Imageview == null || (BusinessProxy.sSelf.Imageview != null && BusinessProxy.sSelf.Imageview.size() == 0))//isRecycleTrue = false; )
		{
			addFlipperImages2(imageFrame);//, parentFolder);
			isRecycleTrue = true; 
		}else{
		for (int i = 0; i < BusinessProxy.sSelf.Imageview.size(); i++) {
			ZoomableImageView imageView = new ZoomableImageView(this);//image vie to ZoomableImageView
			imageView.setLayoutParams(params);
			imageFrame.addView(imageView);imageView.setImageBitmap(BusinessProxy.sSelf.Imageview.get(i));
		}
		}
		
		
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
		if(imagesPath.size() == 1)
		findViewById(R.id.gallery1).setVisibility(View.GONE);
		
		imageFrame.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				showMenu();
				return false;
			}
			
		});
		handler = new Handler();
		if(imagesPath.size()>1)
		imageFrame.setOnClickListener(MultiPhotoViewerMedia.this);
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
//				if(View.GONE == findViewById(R.id.gallery1).getVisibility())
//					findViewById(R.id.gallery1).setVisibility(View.VISIBLE);
//				else
//					findViewById(R.id.gallery1).setVisibility(View.GONE);
			}

			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		memory();
	}
	
	@Override
	public void onBackPressed() {
		if(onBackPressedCheck())return;
		setResult(-999) ;
		finish() ;
	}
	private void addFlipperImages2(ViewFlipper flipper) {
		try {
			int imageCount = imagesPath.size();
			
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = 200;// display.getWidth();
			int screenHeight =  200;//display.getHeight();
			String[] imageTypes = getResources().getStringArray(R.array.image);
			ImageList = new ArrayList<String>();
			BusinessProxy.sSelf.Imageview = new ArrayList<Bitmap>();
			flipper.removeAllViews();
			for (int count = 0; count < imageCount /*- 1*/; count++) {
				ZoomableImageView imageView = new ZoomableImageView(this);//imageview to ZoomableImageView
				BitmapScaler scaler = null;
				try {
					String filename = imagesPath.get(count);
//					System.out.println("----------------filename-----------"+filename);
					ImageList.add(filename);
//					System.out.println(filename);
					File f = new File(filename);
					scaler = new BitmapScaler(f, screenWidth);

					imageView.setImageBitmap(scaler.getScaled());

					BusinessProxy.sSelf.Imageview.add(scaler.getScaled());
					//            Bitmap imbm = BitmapFactory.decodeFile(parent.listFiles()[count]
					//                    .getAbsolutePath());
					//            imageView.setImageBitmap(imbm);
					imageView.setLayoutParams(params);
					flipper.addView(imageView);
					
				} catch (IOException e) {
					onBackPressed();
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					onBackPressed();
				}
			}
		} catch (OutOfMemoryError e) {
			// 
			onBackPressed();
		}
	}

	
	class MyGestureDetector extends SimpleOnGestureListener {
		
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			// TODO Auto-generated method stub
////			showMenu();
//			return super.onDoubleTap(e);
//		}
		@SuppressWarnings("static-access")
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			
			if(View.GONE == findViewById(R.id.gallery1).getVisibility())
				findViewById(R.id.gallery1).setVisibility(View.VISIBLE);
			else
				findViewById(R.id.gallery1).setVisibility(View.GONE);
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
//			slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
//			slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
//			handler.removeCallbacks(runnable);
//			runnable = new Runnable() {
//
//				@Override
//				public void run() {
//					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
//				}
//			};
//			handler.postDelayed(runnable, 2000);
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
			return BusinessProxy.sSelf.Imageview.size();
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
			imageview.setImageBitmap(BusinessProxy.sSelf.Imageview.get(position));
			imageview.setTag(position);
			
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
			
			
			return imageview;
		}

				private Context mContext;

	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub
		
	}
	public void showMenu(){
		if(1==1) return ;
		final Dialog dialog = new Dialog(MultiPhotoViewerMedia.this,android.R.style.Theme_Translucent_NoTitleBar);
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
				boolean s =Utilities.moveFileToSdCard(imagesPath.get(imageFrame.getDisplayedChild())) ;
//				addNotification("File sucessfully saved!", "File sucessfully saved!") ;
				if(s)
				Toast.makeText(MultiPhotoViewerMedia.this, "File sucessfully saved!", Toast.LENGTH_SHORT)
				.show();
				else
					Toast.makeText(MultiPhotoViewerMedia.this, "Unable to save file!", Toast.LENGTH_SHORT)
					.show();	

			}
		});

		Button button2 = (Button) dialog.findViewById(R.open_choice.button2);
		button2.setText("Set as wallpapper");
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) { 
				try {
					getApplicationContext().setWallpaper(Utilities.getFileInputStream(imagesPath.get(imageFrame.getDisplayedChild()))) ;
					Toast.makeText(MultiPhotoViewerMedia.this, "File sucessfully set as wallpaper!", Toast.LENGTH_SHORT)
					.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});
		Button cancelButton = (Button) dialog.findViewById(R.open_choice.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	@Override
	protected void onStop() {
		
		recycleImageView();
		super.onStop();
		
	}
	public void recycleImageView() {

		if(!isRecycleTrue)return;
		if (imageFrame != null) {
			for (int i = 0; i < imageFrame.getChildCount(); i++) {
				try {
					View v = imageFrame.getChildAt(i);
					if (v instanceof ImageView) {
						Drawable drawable = ((ImageView)v).getDrawable();
						if (drawable instanceof BitmapDrawable) {
							BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
							Bitmap bitmap = bitmapDrawable.getBitmap();
							bitmap.recycle();
							bitmapDrawable = null;
							//System.out.println("--------------------recycle------");
						}
					}
				} catch (Exception e) {
					
				}
			}
			for (int i = 0; i < BusinessProxy.sSelf.Imageview.size(); i++) {
				Bitmap bitmap = BusinessProxy.sSelf.Imageview.get(i) ;
				if (bitmap !=null) {					
					bitmap.recycle();					
				}
			}
			BusinessProxy.sSelf.Imageview.clear() ;
			
		}
	}
}
