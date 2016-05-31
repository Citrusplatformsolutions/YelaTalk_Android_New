package com.kainat.app.android.util.format;

import java.io.Serializable;

public class Payload implements Serializable {

	/*
	 * Payload types.
	 */
	public static final byte PAYLOAD_TYPE_VOICE = 1;
	public static final byte PAYLOAD_TYPE_TEXT = 2;
	public static final byte PAYLOAD_TYPE_PICTURE = 3;
	public static final byte PAYLOAD_TYPE_RTML = 4;
	public static final byte PAYLOAD_TYPE_VIDEO = 7;
	public static final byte PAYLOAD_VIDEO_THUMB = 8;
	public static final byte PAYLOAD_VIDEO_URL = 9;

	/*
	 * Payload bitmaps
	 */
	public static final int PAYLOAD_BITMAP_VOICE = 1 << 0;
	public static final int PAYLOAD_BITMAP_TEXT = 1 << 1;
	public static final int PAYLOAD_BITMAP_PICTURE = 1 << 2;
	public static final int PAYLOAD_BITMAP_VIDEO = 1 << 6;
	public static final int PAYLOAD_BITMAP_RTML = 1 << 9;
	public static final int PAYLOAD_BITMAP_VIDEO_THUMB = 1 << 10;
	public static final int PAYLOAD_BITMAP_VIDEO_URL = 1 << 11;
	
	public byte[][] mVoice;
	public byte[] mVoiceType;
	public byte[][] mText;
	public byte[] mTextType;
	public byte[][] mPicture;
	public byte[] mPictureType;
	public byte[][] mRTML;
	public byte[] mRTMLType;
	public byte[][] mVideo;
	public byte[] mVideoType;
	
	public String[] mSlideShowURLs;

	public int mPayloadTypeBitmap;

	public Payload() {
	}

	public Payload(int count) {
		mVoice = new byte[count][];
		mVoiceType = new byte[count];
		mText = new byte[count][];
		mTextType = new byte[count];
		mPicture = new byte[count][];
		mPictureType = new byte[count];
		mRTML = new byte[count][];
		mRTMLType = new byte[count];
		mVideo = new byte[count][];
		mVideoType = new byte[count];
		
		mSlideShowURLs = new String[count];
	}

	public void close() {
		mVoice = null;
		mVoiceType = null;
		mText = null;
		mTextType = null;
		mPicture = null;
		mPictureType = null;
		mRTML = null;
		mRTMLType = null;
		mVideo = null;
		mVideoType = null;
	}
}
