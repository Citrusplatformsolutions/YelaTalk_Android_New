package com.kainat.app.android.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class CameraWrapper {
	private static Parameters mParameters;

	private static Method mGetSupportedAntibanding;
	private static Method mGetSupportedColorEffects;
	private static Method mGetSupportedFlashModes;
	private static Method mGetSupportedFocusModes;
	private static Method mGetSupportedPictureFormats;
	private static Method mGetSupportedJpegThumbnailSizes;
	private static Method mGetMaxZoom;
	private static Method mGetSupportedPreviewFormats;
	private static Method mGetSupportedPreviewFpsRange;
	private static Method mGetSupportedPreviewFrameRates;
	private static Method mGetSupportedPreviewSizes;
	private static Method mGetSupportedSceneModes;
	private static Method mGetSupportedPictureSizes;
	private static Method mGetSupportedVideoSizes;
	private static Method mGetSupportedWhiteBalance;
	private static Method mIsZoomSupported;
	private static Method mIsSmoothZoomSupported;

	static {
		initInstance();
		initMethods();
	}

	private static void initInstance() {
		try{
		Camera camera = Camera.open();
		if(camera!=null)
		mParameters = camera.getParameters();
		camera.release();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void initMethods() {
		try{
		mGetSupportedAntibanding = getMethodSafe("getSupportedAntibanding", null);
		mGetSupportedColorEffects = getMethodSafe("getSupportedColorEffects", null);
		mGetSupportedFlashModes = getMethodSafe("getSupportedFlashModes", null);
		mGetSupportedFocusModes = getMethodSafe("getSupportedFocusModes", null);
		mGetSupportedPictureFormats = getMethodSafe("getSupportedPictureFormats", null);
		mGetSupportedJpegThumbnailSizes = getMethodSafe("getSupportedJpegThumbnailSizes", null);
		mGetMaxZoom = getMethodSafe("getMaxZoom", null);
		mGetSupportedPreviewFormats = getMethodSafe("getSupportedPreviewFormats", null);
		mGetSupportedPreviewFpsRange = getMethodSafe("getSupportedPreviewFpsRange", null);
		mGetSupportedPreviewFrameRates = getMethodSafe("getSupportedPreviewFrameRates", null);
		mGetSupportedPreviewSizes = getMethodSafe("getSupportedPreviewSizes", null);
		mGetSupportedSceneModes = getMethodSafe("getSupportedSceneModes", null);
		mGetSupportedPictureSizes = getMethodSafe("getSupportedPictureSizes", null);
		mGetSupportedVideoSizes = getMethodSafe("getSupportedVideoSizes", null);
		mGetSupportedWhiteBalance = getMethodSafe("getSupportedWhiteBalance", null);
		mIsZoomSupported = getMethodSafe("isZoomSupported", null);
		mIsSmoothZoomSupported = getMethodSafe("isSmoothZoomSupported", null);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static Method getMethodSafe(String methodName, Class[] params) {
		try {
			return Camera.Parameters.class.getMethod(methodName, params);
		} catch (NoSuchMethodException ignore) {
			return null;
		}
	}

	public static List<String> getSupportedAntibanding() {
		try {
			return (List<String>) mGetSupportedAntibanding.invoke(mParameters, null);
		} catch (Exception ignore) {
			return null;
			//return Collections.<String> emptyList();
		}
	}

	public static List<String> getSupportedColorEffects() {
		try {
			return (List<String>) mGetSupportedColorEffects.invoke(mParameters, null);
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedFlashModes() {
		try {
			return (List<String>) mGetSupportedFlashModes.invoke(mParameters, null);
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedFocusModes() {
		try {
			return (List<String>) mGetSupportedFocusModes.invoke(mParameters, null);
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedPictureFormats() {
		try {
			List<Integer> formats = (List<Integer>) mGetSupportedPictureFormats.invoke(mParameters, null);
			List<String> results = new ArrayList<String>();
			for (Integer each : formats) {
				switch (each) {
				case 256: // ImageFormat.JPEG at API Level 8
					results.add("JPEG");
					break;
				case 16: // ImageFormat.NV16 at API Level 8
					results.add("NV16");
					break;
				case 17: // ImageFormat.NV21 at API Level 8
					results.add("NV21");
					break;
				case 4: // ImageFormat.RBG_565 at API Level 8
					results.add("RGB_565");
					break;
				case 20: // ImageFormat.YUY2 at API Level 8
					results.add("YUY2");
					break;
				case 842094169: // ImageFormat.YV12 at API Level 9
					results.add("YV12");
					break;
				default:
					results.add("UNKNOWN");
					break;
				}
			}
			return results;
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedJpegThumbnailSizes() {
		try {
			List<Camera.Size> sizes = (List<Camera.Size>) mGetSupportedJpegThumbnailSizes.invoke(mParameters, null);
			List<String> results = new ArrayList<String>();
			for (Camera.Size each : sizes) {
				results.add(each.width + "x" + each.height);
			}
			return results;
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static Integer getMaxZoom() {
		try {
			return (Integer) mGetMaxZoom.invoke(mParameters, null);
		} catch (Exception ignore) {
			return 100;
		}
	}

	public static List<String> getSupportedPreviewFormats() {
		try {
			List<Integer> formats = (List<Integer>) mGetSupportedPreviewFormats.invoke(mParameters, null);
			List<String> results = new ArrayList<String>();
			for (Integer each : formats) {
				switch (each) {
				case 256: // ImageFormat.JPEG at API Level 8
					results.add("JPEG");
					break;
				case 16: // ImageFormat.NV16 at API Level 8
					results.add("NV16");
					break;
				case 17: // ImageFormat.NV21 at API Level 8
					results.add("NV21");
					break;
				case 4: // ImageFormat.RBG_565 at API Level 8
					results.add("RGB_565");
					break;
				case 20: // ImageFormat.YUY2 at API Level 8
					results.add("YUY2");
					break;
				case 842094169: // ImageFormat.YV12 at API Level 9
					results.add("YV12");
					break;
				default:
					results.add("UNKNOWN");
					break;
				}
			}
			return results;
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedPreviewFpsRange() {
		try {
			if (mGetSupportedPreviewFpsRange != null) {
				//return (List<String>) mGetSupportedPreviewFpsRange.invoke(mParameters, null);
				List<Object> formats = (List<Object>)mGetSupportedPreviewFpsRange.invoke(mParameters, null);
				List<String> results = new ArrayList<String>((formats.size()));
				
				for (Object myInt : formats) { 
					results.add(myInt.toString()); 
				}
				return results;
			} else {
				//return (List<String>) mGetSupportedPreviewFrameRates.invoke(mParameters, null);
				List<Object> formats = (List<Object>)mGetSupportedPreviewFrameRates.invoke(mParameters, null);
				List<String> results = new ArrayList<String>((formats.size()));
				
				for (Object myInt : formats) { 
					results.add(myInt.toString()); 
				}
				return results;
			}
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedPreviewSizes() {
		try {
			List<Camera.Size> sizes = (List<Camera.Size>) mGetSupportedPreviewSizes.invoke(mParameters, null);
			List<String> results = new ArrayList<String>();
			for (Camera.Size each : sizes) {
				results.add(each.width + "x" + each.height);
			}
			return results;
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedSceneModes() {
		try {
			return (List<String>) mGetSupportedSceneModes.invoke(mParameters, null);
		} catch (Exception ignore) {
		//	return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedPictureSizes() {
		try {
			List<Camera.Size> sizes = (List<Camera.Size>) mGetSupportedPictureSizes.invoke(mParameters, null);
			List<String> results = new ArrayList<String>();
			for (Camera.Size each : sizes) {
				results.add(each.width + "x" + each.height);
			}
			return results;
		} catch (Exception ignore) {
		//	return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedVideoSizes() {
		try {
			//return (List<String>) mGetSupportedVideoSizes.invoke(mParameters, null);
			List<Camera.Size> sizes = (List<Camera.Size>) mGetSupportedVideoSizes.invoke(mParameters, null);
			List<String> results = new ArrayList<String>();
			for (Camera.Size each : sizes) {
				results.add(each.width + "x" + each.height);
			}
			return results;
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static List<String> getSupportedWhiteBalance() {
		try {
			return (List<String>) mGetSupportedWhiteBalance.invoke(mParameters, null);
		} catch (Exception ignore) {
			//return Collections.<String> emptyList();
			return null;
		}
	}

	public static boolean isZoomSupported() {
		try {
			return (Boolean) mIsZoomSupported.invoke(mParameters, null);
		} catch (Exception ignore) {
			return false;
		}
	}

	public static boolean isSmoothZoomSupported() {
		try {
			return (Boolean) mIsSmoothZoomSupported.invoke(mParameters, null);
		} catch (Exception ignore) {
			return false;
		}
	}
}