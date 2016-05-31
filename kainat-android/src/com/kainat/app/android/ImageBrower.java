package com.kainat.app.android;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;

import com.kainat.app.android.common.PathUtils;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.Utilities;

public class ImageBrower extends Activity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
	private String currentPath = null;
	private String[] elements = null;
	private Gallery mGallery = null;
	private ImageSwitcher mSwitcher = null;
	private String orgImageName = null;
	private int orgImagePos = 0;
	private Drawable[] res = null;
	private int screenHeight;
	private int screenWidth;

	private void showImage(int paramInt) {
		if(Logger.ENABLE)
		System.out.println("----------------------------show image-----------------------" + paramInt);
		InputStream localObject = null;
		try {
			if (this.res[paramInt] == null)
				this.res[paramInt] = Drawable.createFromPath("/mnt/sdcard/DCIM/sketch/" + ImageBrower.this.access[paramInt]);//reateFromStream((InputStream)localObject, ImageBrower.this.access[paramInt]);
			Bitmap localObjectC = this.mSwitcher.getDrawingCache();
			if (localObjectC != null)
				((Bitmap) localObjectC).recycle();

			Bitmap bitmapOrg = BitmapFactory.decodeFile("/mnt/sdcard/DCIM/sketch/" + ImageBrower.this.access[paramInt]);//decodeResource(getResources(),
			this.mSwitcher.setImageDrawable(scaleImage(bitmapOrg, (int) Utilities.convertPixelsToDp(1000, this), (int) Utilities.convertPixelsToDp(2000, this)));
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	ImageView localImageView;
	float scaleWidth, scaleHeight;
	float zoom = 0.0f;

	void setImageMatrix(ImageView image) {
		Matrix mtrx = new Matrix();
		mtrx.postScale(scaleWidth, scaleHeight);
		image.setImageMatrix(mtrx);
		image.setScaleType(ScaleType.MATRIX);
		image.invalidate();
	}

	/*
	 * void ZoomIn(float zoom) { zoom += 0.01; scaledWidth += zoom; scaledHeight += zoom; ShowImage(); }
	 * 
	 * void ZoomOut(float zoom) { zoom -= 0.01; scaledWidth -= zoom; scaledHeight -= zoom; }
	 */

	public InputStream getFileInputStream(String paramString) {
		return null;//this.service.getFileInputStream(paramString);
	}

	String[] access;

	public void getImagesFromCurrentPath() {
		this.elements = access = Utilities.getAllfile("/mnt/sdcard/DCIM/sketch/");
		for (int i = 0; i < access.length; i++) {
			if(Logger.ENABLE)
			System.out.println("access----------------------------->" + access[i]);
		}
		this.elements = new String[access.length];
		this.res = new Drawable[access.length];
	}

	public View makeView() {
		ImageView localImageView = new ImageView(this);
		localImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		localImageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
		return localImageView;
	}

	int curIndex = 0;
	int downX, upX;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		getWindow().setFlags(2000, 1024);
		//    WaitingDialog.show(this, 2131230842, 2131230849);
		setContentView(R.layout.image_brower);
		java.lang.Object localObject = getIntent().getExtras();
		//    if (localObject != null)
		this.orgImageName = "/mnt/sdcard/DCIM/sketch/";//((Bundle)localObject).getString("ABSOLUTE_FILE_PATH");
		if (this.orgImageName != null) {
			localObject = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics((DisplayMetrics) localObject);
			this.screenWidth = ((DisplayMetrics) localObject).widthPixels;
			this.screenHeight = (((DisplayMetrics) localObject).heightPixels - 80);
			this.mSwitcher = ((ImageSwitcher) findViewById(R.id.switcher));
			this.mSwitcher.setFactory(new ViewFactory() {

				@Override
				public View makeView() {
					localImageView = new ImageView(ImageBrower.this);
					localImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
					localImageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					return localImageView;
				}
			});
			mSwitcher.setImageResource(R.drawable.add);
			/*
			 * mSwitcher.setOnTouchListener(new OnTouchListener() {
			 * 
			 * @Override public boolean onTouch(View v, MotionEvent event) { if (event.getAction() == MotionEvent.ACTION_DOWN) { downX = (int) event.getX(); return true; } else if (event.getAction()
			 * == MotionEvent.ACTION_UP) { upX = (int) event.getX(); if (upX - downX > 100) { mSwitcher.setInAnimation(AnimationUtils.loadAnimation(ImageBrower.this, android.R.anim.slide_in_left));
			 * mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(ImageBrower.this, android.R.anim.slide_out_right)); mSwitcher.setImageResource(R.drawable.arrow); } else if (downX - upX > 100)//
			 * { mSwitcher.setInAnimation(AnimationUtils.loadAnimation(ImageBrower.this, android.R.anim.slide_in_left)); mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(ImageBrower.this,
			 * android.R.anim.slide_out_right)); mSwitcher.setImageResource(R.drawable.arrow_sett); } return true; } //return false; });
			 */
			//});
			this.mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
			this.mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
			this.currentPath = "/mnt/sdcard/DCIM/sketch/";//PathUtils.getFilePath(this.orgImageName) + "/";
			getImagesFromCurrentPath();
			if ((this.elements != null) && (this.elements.length != 0)) {
				this.mGallery = ((Gallery) findViewById(R.id.gallery));
				this.mGallery.setAdapter(new ImageAdapter());//this, this));
				this.mGallery.setOnItemSelectedListener(this);
				this.mGallery.setSelection(this.orgImagePos);
				this.mGallery.requestFocus();
				registerForContextMenu(this.mGallery);

			} else {
			}
		}
	}

	public void onDestroy() {
		for (int i = 0; i < this.res.length; ++i) {
			ImageView localImageView = (ImageView) this.mGallery.getChildAt(i);
			if (localImageView != null)
				localImageView.setImageDrawable(null);
		}
		this.mSwitcher.setImageDrawable(null);
		this.mSwitcher.removeAllViews();
		this.res = null;
		this.elements = null;
		this.mGallery = null;
		this.mSwitcher = null;
		super.onDestroy();
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
		//showImage(paramInt);
	}

	public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
		//showImage(paramInt);
	}

	public void onNothingSelected(AdapterView<?> paramAdapterView) {
	}

	@Override
	protected void onStart() {
		//	  Intent localIntent = new Intent("android.intent.action.VIEW");
		//	    localIntent.addCategory("android.intent.category.DEFAULT");
		//	    localIntent.addFlags(268435456);
		//	    localIntent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/DCIM/sketch/")), "image/*");
		//	    startActivity(localIntent);
		super.onStart();
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public ImageAdapter() {
			// this.mLayoutInflater = paramImageBrower2.getLayoutInflater();
		}

		public float getAlpha(int i, int paramInt) {
			return Math.max(0.20000000298023224F, 1.0F - 0.20000000298023224F * Math.abs(paramInt));
		}

		public int getCount() {
			return ImageBrower.this.access.length;
		}

		public java.lang.Object getItem(int paramInt) {
			return ImageBrower.this.access[paramInt];
		}

		public float getScale(int paramInt) {
			float f;
			if (paramInt != 0)
				f = 0.60000002384185791F;
			else
				f = 1.0F;
			return Math.max(0.0F, f);
		}

		public View getView(int pos, View paramView, ViewGroup paramViewGroup) {
			InputStream localInputStream;
			ImageView localImageView;
			if (paramView == null) {

				LayoutInflater inflator = (LayoutInflater) ImageBrower.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				localImageView = (ImageView) inflator.inflate(R.layout.wallpaper_item, paramViewGroup, false);
				if (ImageBrower.this.access[pos] != null) {
					localInputStream = Utilities.getFileInputStream("/mnt/sdcard/DCIM/sketch/" + ImageBrower.this.access[pos]);
					Drawable f = Drawable.createFromStream(localInputStream, PathUtils.getFileName(ImageBrower.this.access[pos]));
					localImageView.setImageResource(17301567);
					localImageView.getDrawable().setDither(true);
					localImageView.setAdjustViewBounds(true);
					localImageView.setLayoutParams(new Gallery.LayoutParams(100, 80));
					localImageView.setImageDrawable(f);

					paramView = (ImageView) localImageView;
					return paramView;
				} else
					return paramView;
			}
			return paramView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private Drawable scaleImage(Bitmap bitmap, int boundBoxInDpw, int boundBoxInDph) {
		// Get the ImageView and its bitmap
		//      Drawable drawing = view.getDrawable();
		//      Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

		// Get current dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundBoxInDpw) / width;
		float yScale = ((float) boundBoxInDph) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(scaledBitmap);
		/*
		 * width = scaledBitmap.getWidth(); height = scaledBitmap.getHeight();
		 * 
		 * // Apply the scaled bitmap view.setImageDrawable(result);
		 * 
		 * // Now change ImageView's dimensions to match the scaled image LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams(); params.width = width; params.height =
		 * height; view.setLayoutParams(params);
		 */
	}

	private void scaleImage(ImageView view, int boundBoxInDp) {
		// Get the ImageView and its bitmap
		Drawable drawing = view.getDrawable();
		Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

		// Get current dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundBoxInDp) / width;
		float yScale = ((float) boundBoxInDp) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);
		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();

		// Apply the scaled bitmap
		view.setImageDrawable(result);

		// Now change ImageView's dimensions to match the scaled image
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	private int dpToPx(int dp) {
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
}