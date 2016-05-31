package com.kainat.app.android.util;

import android.content.Context;

public class CameraInfo {
	public String FORMAT;
	public String SIZE;
	public String FOCUS_MODE;
	public String ANTIBANDING;
	public String COLOR_EFFECT;
	public String FLASH_MODE;
	public String JPEG_THUMBNAIL_SIZE;
	public String PREVIEW_FORMAT;
	public String PREVIEW_FPS_RANGE;
	public String PREVIEW_SIZE;
	public String SCENE_MODE;
	public String VIDEO_SIZE;
	public String WHITE_BALANCE;
	public String ZOOM_SUPPORTED;
	public String SMOOTH_ZOOM_SUPPORTED;
	public String MAX_ZOOM;

	private static CameraInfo instance;

	private CameraInfo() {
	}

	public static CameraInfo getInstance(Context context) {
		if (instance == null) {
			instance = new CameraInfo();
			/**
			 * this line is copmented by nagendra because it throw exception is some device 
			 * folloing error we got from android market
			 * 
			 * java.lang.ExceptionInInitializerError
				at com.kainat.app.android.util.CameraInfo.getInstance(CameraInfo.java:31)
				at com.kainat.app.android.util.ClientProperty.<init>(ClientProperty.java:39)
				at com.kainat.app.android.RocketalkApplication.onCreate(RocketalkApplication.java:19)
				at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:969)
				at android.app.ActivityThread.handleBindApplication(ActivityThread.java:4244)
				at android.app.ActivityThread.access$3000(ActivityThread.java:125)
				at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2071)
				at android.os.Handler.dispatchMessage(Handler.java:99)
				at android.os.Looper.loop(Looper.java:123)
				at android.app.ActivityThread.main(ActivityThread.java:4668)
				at java.lang.reflect.Method.invokeNative(Native Method)
				at java.lang.reflect.Method.invoke(Method.java:521)
				at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:878)
				at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:636)
				at dalvik.system.NativeStart.main(Native Method)
				Caused by: java.lang.RuntimeException: Fail to connect to camera service
				at android.hardware.Camera.native_setup(Native Method)
				at android.hardware.Camera.<init>(Camera.java:185)
				at android.hardware.Camera.open(Camera.java:165)
				at com.kainat.app.android.util.CameraWrapper.initInstance(CameraWrapper.java:38)
				at com.kainat.app.android.util.CameraWrapper.<clinit>(CameraWrapper.java:33)

			 */
			if(StringUtils.toString(CameraWrapper.getSupportedAntibanding())!=null)
		    instance.ANTIBANDING = StringUtils.toString(CameraWrapper.getSupportedAntibanding());
			
			if(StringUtils.toString(CameraWrapper.getSupportedColorEffects())!=null)
			instance.COLOR_EFFECT = StringUtils.toString(CameraWrapper.getSupportedColorEffects());
			
			if(StringUtils.toString(CameraWrapper.getSupportedFlashModes())!=null)
		    instance.FLASH_MODE = StringUtils.toString(CameraWrapper.getSupportedFlashModes());
			
			if(StringUtils.toString(CameraWrapper.getSupportedFocusModes())!=null)
			instance.FOCUS_MODE = StringUtils.toString(CameraWrapper.getSupportedFocusModes());
			
			if(StringUtils.toString(CameraWrapper.getSupportedPictureFormats())!=null)
			instance.FORMAT = StringUtils.toString(CameraWrapper.getSupportedPictureFormats());
			
			if(StringUtils.toString(CameraWrapper.getSupportedJpegThumbnailSizes())!=null)
		instance.JPEG_THUMBNAIL_SIZE = StringUtils.toString(CameraWrapper.getSupportedJpegThumbnailSizes());
			
			if(StringUtils.toString(CameraWrapper.getMaxZoom())!=null)
		instance.MAX_ZOOM = StringUtils.toString(CameraWrapper.getMaxZoom());
			
			if(StringUtils.toString(CameraWrapper.getSupportedPreviewFormats())!=null)
		instance.PREVIEW_FORMAT = StringUtils.toString(CameraWrapper.getSupportedPreviewFormats());
			
			//if(StringUtils.toString(CameraWrapper.getSupportedPreviewFpsRange())!=null)
			//instance.PREVIEW_FPS_RANGE = StringUtils.toString(CameraWrapper.getSupportedPreviewFpsRange());
			
			if(StringUtils.toString(CameraWrapper.getSupportedPreviewSizes())!=null)
		    instance.PREVIEW_SIZE = StringUtils.toString(CameraWrapper.getSupportedPreviewSizes());
			
			if(StringUtils.toString(CameraWrapper.getSupportedSceneModes())!=null)
			instance.SCENE_MODE = StringUtils.toString(CameraWrapper.getSupportedSceneModes());
			
			if(StringUtils.toString(CameraWrapper.getSupportedPictureSizes())!=null)
		instance.SIZE = StringUtils.toString(CameraWrapper.getSupportedPictureSizes());
			
			if((CameraWrapper.isSmoothZoomSupported() ? "Supported" : "Not Supported")!=null)
		instance.SMOOTH_ZOOM_SUPPORTED = CameraWrapper.isSmoothZoomSupported() ? "Supported" : "Not Supported";
			
			if(StringUtils.toString(CameraWrapper.getSupportedVideoSizes())!=null)
		instance.VIDEO_SIZE = StringUtils.toString(CameraWrapper.getSupportedVideoSizes());
			
			if(StringUtils.toString(CameraWrapper.getSupportedWhiteBalance())!=null)
		instance.WHITE_BALANCE = StringUtils.toString(CameraWrapper.getSupportedWhiteBalance());
			
			if((CameraWrapper.isZoomSupported() ? "Supported" : "Not Supported")!=null)
		instance.ZOOM_SUPPORTED = CameraWrapper.isZoomSupported() ? "Supported" : "Not Supported";
		}
		return instance;
	}
	public String getPreciewSize(){
		if(PREVIEW_SIZE!=null && PREVIEW_SIZE.length()>0)
			return PREVIEW_SIZE.toString() ;
		else
			return null ;
	}
	public String toString() {
		StringBuffer stb = new StringBuffer();
		if(FORMAT!=null && FORMAT.length()>0)
		stb.append("FORMAT##" + FORMAT).append("::");
		if(SIZE!=null && SIZE.length()>0)
		stb.append("SIZE##" + SIZE).append("::");
		if(FOCUS_MODE!=null && FOCUS_MODE.length()>0)
		stb.append("FOCUS_MODE##" + FOCUS_MODE).append("::");
		if(ANTIBANDING!=null && ANTIBANDING.length()>0)
		stb.append("ANTIBANDING##" + ANTIBANDING).append("::");
		if(COLOR_EFFECT!=null && COLOR_EFFECT.length()>0)
		stb.append("COLOR_EFFECT##" + COLOR_EFFECT).append("::");
		if(FLASH_MODE!=null && FLASH_MODE.length()>0)
		stb.append("FLASH_MODE##" + FLASH_MODE).append("::");
		if(JPEG_THUMBNAIL_SIZE!=null && JPEG_THUMBNAIL_SIZE.length()>0)
		stb.append("JPEG_THUMBNAIL_SIZE##" + JPEG_THUMBNAIL_SIZE).append("::");
		if(PREVIEW_FORMAT!=null && PREVIEW_FORMAT.length()>0)
		stb.append("PREVIEW_FORMAT##" + PREVIEW_FORMAT).append("::");
		//if(PREVIEW_FPS_RANGE!=null && PREVIEW_FPS_RANGE.length()>0)
		//stb.append("PREVIEW_FPS_RANGE##" + PREVIEW_FPS_RANGE).append("::");
		if(PREVIEW_SIZE!=null && PREVIEW_SIZE.length()>0)
		stb.append("PREVIEW_SIZE##" + PREVIEW_SIZE).append("::");
		if(SCENE_MODE!=null && SCENE_MODE.length()>0)
		stb.append("SCENE_MODE##" + SCENE_MODE).append("::");
		if(VIDEO_SIZE!=null && VIDEO_SIZE.length()>0)
		stb.append("VIDEO_SIZE##" + VIDEO_SIZE).append("::");
		if(WHITE_BALANCE!=null && WHITE_BALANCE.length()>0)
		stb.append("WHITE_BALANCE##" + WHITE_BALANCE).append("::");
		if(ZOOM_SUPPORTED!=null && ZOOM_SUPPORTED.length()>0)
		stb.append("ZOOM_SUPPORTED##" + ZOOM_SUPPORTED).append("::");
		if(SMOOTH_ZOOM_SUPPORTED!=null && SMOOTH_ZOOM_SUPPORTED.length()>0)
		stb.append("SMOOTH_ZOOM_SUPPORTED##" + SMOOTH_ZOOM_SUPPORTED).append("::");
		if(MAX_ZOOM!=null && MAX_ZOOM.length()>0)
		stb.append("MAX_ZOOM##" + MAX_ZOOM);

		return stb.toString();
	}
}
