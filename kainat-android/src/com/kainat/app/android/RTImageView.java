package com.kainat.app.android;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RTImageView extends ImageView {

	private String mImagePah;

	/**
	 * @param context
	 */
	public RTImageView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public RTImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public RTImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Gets the value of mImagePah
	 * 
	 * @return The mImagePah
	 */
	public final String getImagePah() {
		return mImagePah;
	}

	/**
	 * Sets the value of imagePah
	 * 
	 * @param imagePah
	 *            The mImagePah to set
	 */
	public final void setImagePah(String imagePah) {
		mImagePah = imagePah;
		setImageURI(Uri.parse(imagePah));
	}

}
