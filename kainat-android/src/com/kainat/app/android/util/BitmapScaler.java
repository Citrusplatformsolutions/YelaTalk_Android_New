package com.kainat.app.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapScaler {
	private static class Size {
		int sample;
		float scale;
	}

	private Bitmap scaled;
	int newWidth ;
	public BitmapScaler(Resources resources, int resId, int newWidth)
			throws IOException {
		this.newWidth =newWidth ;
		Size size = getRoughSize(resources, resId, newWidth);
		roughScaleImage(resources, resId, size);
		scaleImage(newWidth);
	}

	public BitmapScaler(Context context,String path, int newWidth) throws IOException {
		InputStream is = null;
		this.newWidth =newWidth ;
		try {
			is = context.openFileInput(path);
//			System.out.println("--------1---is available --"+is.available());
			Size size = getRoughSize(is, newWidth);
			try {
				is =context.openFileInput(path);// new FileInputStream(file);
//				System.out.println("-------2----is available --"+is.available());
				roughScaleImage(is, size);
				scaleImage(newWidth);
			} finally {
				is.close();
			}
		} finally {
			is.close();
		}
	}

	
	public BitmapScaler(File file, int newWidth) throws IOException {
		InputStream is = null;
		this.newWidth =newWidth ;
		try {
			is = new FileInputStream(file);
//			System.out.println("--------1---is available --"+is.available());
			Size size = getRoughSize(is, newWidth);
			try {
				is = new FileInputStream(file);
//				System.out.println("-------2----is available --"+is.available());
				roughScaleImage(is, size);
				scaleImage(newWidth);
			} finally {
				is.close();
			}
		} finally {
			if(is!=null)
			{
				try{
			is.close();
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	BitmapScaler(AssetManager manager, String assetName, int newWidth)
			throws IOException {
		InputStream is = null;
		try {
			is = manager.open(assetName);
			Size size = getRoughSize(is, newWidth);
			try {
				is = manager.open(assetName);
				roughScaleImage(is, size);
				scaleImage(newWidth);
			} finally {
				is.close();
			}
		} finally {
			is.close();
		}
	}

	public Bitmap getScaled() {
		return scaled;
	}

	private void scaleImage(int newWidth) {
		int width = scaled.getWidth();
		int height = scaled.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float ratio = ((float) scaled.getWidth()) / newWidth;
		int newHeight = (int) (height / ratio);
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		scaled = Bitmap.createBitmap(scaled, 0, 0, width, height, matrix, true);
	}

	private void roughScaleImage(InputStream is, Size size) {
		Matrix matrix = new Matrix();
		matrix.postScale(size.scale, size.scale);

		BitmapFactory.Options scaledOpts = new BitmapFactory.Options();
		scaledOpts.inDither = true;
		scaledOpts.inPurgeable = true;
		scaledOpts.inInputShareable = false;
		scaledOpts.inSampleSize = size.sample;
		scaled = BitmapFactory.decodeStream(is, null, scaledOpts);
	}

	private void roughScaleImage(Resources resources, int resId, Size size) {
		Matrix matrix = new Matrix();
		matrix.postScale(size.scale, size.scale);

		BitmapFactory.Options scaledOpts = new BitmapFactory.Options();
		scaledOpts.inDither = true;
		scaledOpts.inPurgeable = true;
		scaledOpts.inInputShareable = false;
		scaledOpts.inSampleSize = size.sample;
		scaled = BitmapFactory.decodeResource(resources, resId, scaledOpts);
	}

	private Size getRoughSize(InputStream is, int newWidth) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, o);
//		this.newWidth = o.outWidth ;
//		System.out.println("------------outWidth----"+o.outWidth);
//		System.out.println("------------outHeight----"+o.outHeight);
//		System.out.println("------------getRoughSize----"+o.newWidth);
		Size size = getRoughSize(o.outWidth, o.outHeight, newWidth);
//		Size size = getRoughSize(o.outWidth, o.outHeight, o.outWidth);
		return size;
	}

	private Size getRoughSize(Resources resources, int resId, int newWidth) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inDither = true;
		o.inPurgeable = true;
		o.inInputShareable = false;
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, o);

		Size size = getRoughSize(o.outWidth, o.outHeight, newWidth);
		return size;
	}

	private Size getRoughSize(int outWidth, int outHeight, int newWidth) {
		Size size = new Size();
		size.scale = outWidth / newWidth;
		size.sample = 1;

		int width = outWidth;
		int height = outHeight;

		int newHeight = (int) (outHeight / size.scale);

		while (true) {
			if (width / 2 < newWidth || height / 2 < newHeight) {
				break;
			}
			width /= 2;
			height /= 2;
			size.sample *= 2;
		}
		return size;
	}
}
