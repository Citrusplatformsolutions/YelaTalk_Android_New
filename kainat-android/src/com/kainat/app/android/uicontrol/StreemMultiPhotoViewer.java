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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kainat.util.CompressImage;
import com.kainat.app.android.R;
import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.BitmapScaler;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;

public class StreemMultiPhotoViewer extends UIActivityManager implements
OnClickListener {

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
	File parentFolder;
	Gallery g;
	List<String> imagesPath = new ArrayList<String>();
	int width;
	TextView count;
	//	ImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		shouldStopMedia=false ;
		super.onCreate(savedInstanceState);

		if(!checkInternetConnection()){
			networkLossAlert();
			finish();
			return;
		}
		setContentView(R.layout.photo_slideshow_main);
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
		CommunityFeed.Entry entry = null;
		boolean audio = false;

		try{
			entry = (CommunityFeed.Entry) DataModel.sSelf.getObject(DMKeys.ENTRY + "COMM");
			if(entry !=null){
				DataModel.sSelf.removeObject(DMKeys.ENTRY
						+ "COMM");

				for (int j = 0; j < (entry.media.size()); j += 8) {

					if (audio) {
						j = j - 4;
						audio = false;
					}
					if (((String) entry.media.elementAt(j + 1))
							.equalsIgnoreCase("audio")){
						audio = true;
						continue;
					}

					String s = (String) entry.media.elementAt(j + 1);
					if (!s.equalsIgnoreCase("image"))
						continue;
					imagesPath.add((String) entry.media.elementAt(j + 2));
					View bar = inflator.inflate(R.layout.progress_view_green, null);
					imageFrame.addView(bar);
					bar.setTag((String) entry.media.elementAt(j + 2));

					synch.add(new LoadMedia(index++, "" + j, "" + j, 1, bar));

				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try{
			Feed.Entry entryf;
			entryf = (Feed.Entry) DataModel.sSelf.removeObject(DMKeys.ENTRY
					+ "COMM");


			if(entry == null && entryf !=null){
				for (int j = 0; j < (entryf.media.size()); j += 8) {
					if (audio) {
						j = j - 4;
						audio = false;
					}
					if (((String) entryf.media.elementAt(j + 1))
							.equalsIgnoreCase("audio")){
						audio = true;
						continue;
					}

					String s = (String) entryf.media.elementAt(j + 1);
					if (!s.equalsIgnoreCase("image"))
						continue;
					imagesPath.add((String) entryf.media.elementAt(j + 2));
					View bar = inflator.inflate(R.layout.progress_view_green, null);
					imageFrame.addView(bar);
					bar.setTag((String) entryf.media.elementAt(j + 2));
					//			imageViewVec.add(null);
					synch.add(new LoadMedia(index++, "" + j, "" + j, 1, bar));
					//			System.out.println("-----------st----reem multi view -----------"
					//					+ (String) entryf.media.elementAt(j + 2));

				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		if (synch.size() > 0)
			((LoadMedia) synch.get(0)).execute("");
		if (null == imagesPath) {
			finish();
			return;
		}

		// get sd card path for images

		parentFolder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		// + "/DCIM/Camera");

		// addFlipperImages2(imageFrame);//, parentFolder);
		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		if (imagesPath.size() > 1)
			gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event))
					return true;
				else
					return false;
			}
		};
		//		if (imagesPath.size() == 1)
		//			findViewById(R.id.gallery1).setVisibility(View.GONE);

		imageFrame.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showMenu();
				return false;
			}

		});
		handler = new Handler();
		if (imagesPath.size() > 1)
			imageFrame.setOnClickListener(StreemMultiPhotoViewer.this);
		if (imagesPath.size() > 1)
			imageFrame.setOnTouchListener(gestureListener);
		slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
		if (imagesPath.size() > 1)
			slideShowBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					slideShowBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {

							if(!slisShow)return;
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
		count.setText((imageFrame.getDisplayedChild()+1)+"/"+synch.size());
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
	private void addFlipperImages(ViewFlipper flipper, File parent) {
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

			/*
			 * slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			 * slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			 * handler.removeCallbacks(runnable); runnable = new Runnable() {
			 * 
			 * @Override public void run() {
			 * slideShowBtn.setVisibility(slideShowBtn.INVISIBLE); } };
			 * handler.postDelayed(runnable, 2000);
			 * 
			 * Intent intent = new Intent();
			 * intent.setAction(Intent.ACTION_VIEW); File src = new
			 * File(ImageList.get(imageFrame.getDisplayedChild()));
			 * 
			 * 
			 * String pathf =
			 * Environment.getExternalStorageDirectory().getAbsolutePath()
			 * +"/.RockeTalk";//// "/RockeTalk/Chat/"; File destf = new
			 * File(pathf); try { boolean exists = destf.exists(); if (!exists)
			 * { destf.mkdirs(); } String path =
			 * Environment.getExternalStorageDirectory().getAbsolutePath()
			 * +"/.RockeTalk/sys.jpg";//// "/RockeTalk/Chat/"; File dest = new
			 * File(path);
			 * 
			 * FileIOUtility.copyFile(src, dest);
			 * 
			 * Uri uri = Uri.fromFile(dest.getAbsoluteFile());//new
			 * File(ImageList.get(imageFrame.getDisplayedChild())));
			 * intent.setData(uri); intent.setDataAndType(uri, "image/*");
			 * startActivity(intent); } catch (IOException e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); }
			 */

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
					handler.removeCallbacks(runnable);
					//					imageFrame.setInAnimation(inFromRightAnimation());
					//					imageFrame.setOutAnimation(outToLeftAnimation());
					if(imageFrame.getDisplayedChild()<imageUrl.size()-1){
						imageFrame.showNext();
						switchImage();
					}
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					handler.removeCallbacks(runnable);
					//					imageFrame.setInAnimation(inFromLeftAnimation());
					//					imageFrame.setOutAnimation(outToRightAnimation());
					if(imageFrame.getDisplayedChild()>0){
						imageFrame.showPrevious();
						switchImage();
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
		count.setText((imageFrame.getDisplayedChild()+1)+"/"+synch.size());
	}
	@Override
	public void onClick(View view) {

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

			Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(Utilities.getComposeThumbHeight(StreemMultiPhotoViewer.this), Utilities.getComposeThumbHeight(StreemMultiPhotoViewer.this));

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
				bm = Bitmap.createScaledBitmap(bm, Utilities.getComposeThumbWidth(StreemMultiPhotoViewer.this), Utilities.getComposeThumbHeight(StreemMultiPhotoViewer.this), true);
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
		final Dialog dialog = new Dialog(StreemMultiPhotoViewer.this,
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
					Toast.makeText(StreemMultiPhotoViewer.this,
							"File sucessfully saved!", Toast.LENGTH_SHORT)
							.show();
				else
					Toast.makeText(StreemMultiPhotoViewer.this,
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
					Toast.makeText(StreemMultiPhotoViewer.this,
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
				CompressImage compressImage = new CompressImage(StreemMultiPhotoViewer.this) ;
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
				imageView = new ZoomableImageView(StreemMultiPhotoViewer.this);
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

				imageView = new ZoomableImageView(StreemMultiPhotoViewer.this);
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
							imageView = new ZoomableImageView(StreemMultiPhotoViewer.this);
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
						imageView = new CImageView(StreemMultiPhotoViewer.this);
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
						CompressImage compressImage = new CompressImage(StreemMultiPhotoViewer.this) ;
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
					LayoutInflater inflator = (LayoutInflater) StreemMultiPhotoViewer.this
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
}