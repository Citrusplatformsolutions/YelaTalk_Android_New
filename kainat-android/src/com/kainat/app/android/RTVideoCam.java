package com.kainat.app.android;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kainat.app.android.util.CameraInfo;

public class RTVideoCam extends Activity implements OnClickListener,
		SurfaceHolder.Callback,SensorEventListener {
	public static final int RT_VIDEO = 9999 ;
	MediaRecorder recorder;
	SurfaceHolder holder;
	int vw = 640;// 640x480
	int vh = 480;
	boolean recording = false;
	boolean recordingDone = false;
	public static final String TAG = "------VIDEOCAPTURE";
	CameraInfo cameraInfo ;
	Camera mCamera;
	int idsOptions[] = new int[] {6, 0, 1, 2, 3, 4, 5,  7 };
	private SensorManager sensorManager = null;
	 private ImageView rotatingImage;
	 String path = null ;
	 TextView timer ;
	 int fileSize = 3670016;//4194304;
	/** Called when the activity is first created. */
	 public static int selection = 0 ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
			
//		cameraInfo = CameraInfo.getInstance(this);
//		System.out.println("-------------camera info-----" + cameraInfo.getPreciewSize());
//		
//		String preview  = cameraInfo.getPreciewSize() ;
//		preview = preview.replace("[", "") ;
//		preview = preview.replace("]", "") ;
//		String srr[] = preview.split(",") ;
//
////		String resolution[] = new String [srr.length] ;
////		int resolutionInt[][] = new int [srr.length][2] ;
//		for (int i = 0; i < srr.length; i++) {
//			
//			resolution[i] = srr[i] ;
//			String t[] = srr[i].split("x");
//			resolutionInt[i][0] = Integer.parseInt(t[0].trim()) ;
//			resolutionInt[i][1] = Integer.parseInt(t[1].trim()) ;
//			System.out.println("-------------resolutionInt[i][0]-----" + resolutionInt[i][0]);
//			System.out.println("-------------resolutionInt[i][1]-----" + resolutionInt[i][1]);
//		}
		recorder = new MediaRecorder();// Instantiate our media recording object
		// initRecorder();
		setContentView(R.layout.camcorder_preview);
		rotatingImage = (ImageView) findViewById(R.id.record);
		timer = (TextView) findViewById(R.id.timer);
		SurfaceView cameraView = (SurfaceView) findViewById(R.id.camcorder_preview);
		 sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

//		http://stackoverflow.com/questions/7827687/android-camera-setdisplayorientation-strange-behavior-for-galaxy-tab
//		RelativeLayout.LayoutParams video_VIEWLAYOUT = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
//		video_VIEWLAYOUT.width = vh;
//		video_VIEWLAYOUT.height = (int) (((float)vh / (float)vw) *(float)200);
//		cameraView.setLayoutParams(video_VIEWLAYOUT);
		
		holder = cameraView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		select();
		holder.setSizeFromLayout();
//		cameraView.setClickable(true);// make the surface view clickable
//		cameraView.setOnClickListener((OnClickListener) this);// onClicklistener
																// to be called
																// when the
																// surface view
																// is clicked
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
	}

	int from = 0;
	String[] resolution = null/*{ "640x480", "640x384", "576x432", "480x320",
			"384x288", "352x288", "320x240", "240x160", "176x144" }*/;
	int[][] resolutionInt =      null/* { { 640, 480 }, {640,384 }, { 576, 432 }, { 480, 320 },
			{ 384, 288 }, { 352, 288 }, { 320, 240 }, { 240, 160 },{ 176, 144 } }*/;

	public void select() {
		// final CharSequence[] choice = {"640x480", "640x384", "576x432",
		// "480x320", "384x288", "352x288", "320x240", "240x160", "176x144"};

		// int from;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Select Resolution");
		alert.setSingleChoiceItems(resolution, 0,

		new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int

			which) {
				// if(choice[which]=="Choose from Gallery")
				// from=1;
				// else if (choice[which]=="Capture a photo")
				// from=2;
				System.out.println("-----------------size set----------"
						+ (resolutionInt[which][0] + "  , " + resolutionInt[which][1]));
				// recorder.setPreviewDisplay(holder.getSurface().setSize(choice2[which][0],choice2[which][1]));
				// recorder.setVideoSize(choice2[which][0],choice2[which][1]);//
				// best size for resolution.
				recorder.reset();
				vw = resolutionInt[which][0];
				vh = resolutionInt[which][1];
//				initRecorder();
//				prepareRecorder();
				System.out.println("-----------------size set----------"
						+ (resolutionInt[which][0] + "  , " + resolutionInt[which][1]));
			}
		});
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alert.show();
	}

	private void initRecorder() {// this takes care of all the mediarecorder
									// settings
//	 recorder.reset();
		System.out.println("-----------------init record-----------------");
		File OutputFile = new File("/sdcard/default.3gp");
		String video = "/DCIM/100MEDIA/Video";
	
		FileDescriptor d = new FileDescriptor();
		mCamera.stopPreview();
		mCamera.unlock() ;
		recorder.setCamera(mCamera);
			
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// default microphone to be used for audio
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA/*CAMERA*/);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		 recorder.setVideoFrameRate(25);//typically 12-15 best for normal use.
		// For 1080p usually 30fms is used.
		recorder.setVideoSize(vw, vh);// best size for resolution.
		recorder.setMaxFileSize(fileSize);//4,194,304 4 mb
		path = getPicPath() + "/" +System.currentTimeMillis()+ vw + "x" + vh + "test.mp4" ;
		recorder.setOutputFile(path);
		// System.out.println("------------output file-------"+getPicPath()+"/test.3gp");
//		 recorder.setVideoEncodingBitRate(256000);//
//		 recorder.setAudioEncodingBitRate(8000);
		// recorder.setMaxDuration(600000);
		recorder.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				if(what==mr.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
				RTVideoCam.this.onClick(findViewById(R.id.record)) ;
			}
		});
		holder.setSizeFromLayout();
		holder.setFixedSize(vw, vh) ;		
	}

	public static String getPicPath() {
		// File file = new File(Environment.getExternalStorageDirectory(),
		// getRandomNumber() + ".jpg");

		File path = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM");
		if (path.exists()) {
			File test1 = new File(path, "Camera/");
			if (test1.exists()) {
				path = test1;
			} else {
				File test2 = new File(path, "100ANDRO/");
				if (test2.exists()) {
					path = test2;
				} else {
					File test3 = new File(path, "100MEDIA/");
					if (!test3.exists()) {
						test3.mkdirs();
					}
					path = test3;
				}
			}
		} else {
			path = new File(path, "Camera/");
			path.mkdirs();
		}
		// System.out.println("------camera path----------"+path.getAbsolutePath());
		return path.getAbsolutePath();
	}

	/*
	 * if(record.setMaxDuration>60000){
	 * 
	 * recorder.stop(); MediaRecorder.OnInfoListener; Toast display =
	 * Toast.makeText(this, "You have exceeded the record time",
	 * Toast.LENGTH_SHORT);// toast shows a display of little sorts
	 * display.show(); return true; }
	 */

	private void prepareRecorder() {
		recorder.setPreviewDisplay(holder.getSurface());

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			finish();
			onBackPressed();
		} catch (IOException e) {
			e.printStackTrace();
			finish();
			onBackPressed();
		}
	}

	public void onClick(View v) {
		if(recordingDone)return ;
		switch ( v.getId()) {
		case R.id.record:
			if (recording) {
				recordingDone = true ;
				if(countDownTimer!=null)
				countDownTimer.cancel();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								try{
									Toast display = Toast.makeText(RTVideoCam.this, "Saving...",
											Toast.LENGTH_SHORT);// toast shows a display of little sorts
									display.show();
									recorder.stop();
									getIntent().setData(Uri.parse(path)) ;
									setResult(RESULT_OK, getIntent()) ;
									finish();
								}catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
					}
				}).start();
				recordingDone = true ;
//				mCamera.unlock() ;
//				recorder.stop();
				recording = false;
//				Toast display = Toast.makeText(this, "Saving...",
//					Toast.LENGTH_SHORT);// toast shows a display of little sorts
//			display.show();
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						try{
//							Thread.sleep(1000) ;
//						getIntent().setData(Uri.parse(path)) ;
//						setResult(RESULT_OK, getIntent()) ;
//						finish();
//						}catch (Exception e) {
//							// TODO: handle exception
//						}
//					}
//				});
//				getIntent().setData(Uri.parse(path)) ;
////				setIntent(i);
//				
////				initRecorder();
////				prepareRecorder();
////				Toast display = Toast.makeText(this, "Stopped Recording",
////						Toast.LENGTH_SHORT);// toast shows a display of little sorts
////				display.show();
////				v.setBackgroundResource(R.drawable.recordvideo2) ;
//				setResult(RESULT_OK, getIntent()) ;
//				finish();

			} else {
//				if (mPreviewRunning) {
//	 				mCamera.stopPreview();
////	 				mCamera.release();
//					mCamera.unlock();
//	 			}
				v.setBackgroundResource(R.drawable.recordvideo1) ;
//				camera.release();
//				recorder.setCamera(mCamera) ;
				initRecorder();
				Log.v(TAG, "surfaceCreated");
				prepareRecorder();
				recorder.start();
				Log.v(TAG, "Recording Started");
				recording = true;
				findViewById(R.id.setting).setVisibility(View.GONE) ;
				startCountdonw() ;
			}
	
			break;

		case R.id.setting:
//			select() ;
			if (quickAction != null)
				quickAction.hide();
			action(idsOptions, resolution, (byte) 3);
			quickAction.show(v);
			break;
		}
			}

	public void surfaceCreated(SurfaceHolder holder) {
		System.out
				.println("-------------------surfaceCreated---------------------------");
		
		cameraInfo = CameraInfo.getInstance(this);
		System.out.println("-------------camera info-----" + cameraInfo.getPreciewSize());
		
		String preview  = cameraInfo.getPreciewSize() ;
		preview = preview.replace("[", "") ;
		preview = preview.replace("]", "") ;
		String srr[] = preview.split(",") ;
		resolution = new String [srr.length] ;
		resolutionInt = new int [srr.length][2] ;
//		String resolution[] = new String [srr.length] ;
//		int resolutionInt[][] = new int [srr.length][2] ;
		idsOptions = new int [srr.length] ;
		for (int i = 0; i < srr.length; i++) {
			
			idsOptions[i] = i ;
			resolution[i] = srr[i] ;
			if(srr[i].indexOf("320x240") != -1)
				selection = i;
			String t[] = srr[i].split("x");
			resolutionInt[i][0] = Integer.parseInt(t[0].trim()) ;
			resolutionInt[i][1] = Integer.parseInt(t[1].trim()) ;
			System.out.println("-------------resolutionInt[i][0]-----" + resolutionInt[i][0]);
			System.out.println("-------------resolutionInt[i][1]-----" + resolutionInt[i][1]);
		}
		vw = resolutionInt[selection][0];
		vh = resolutionInt[selection][1];
		mCamera = Camera.open();
//		initRecorder();
//		Log.v(TAG, "surfaceCreated");
//		prepareRecorder();
	}
	 private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
	        final double ASPECT_TOLERANCE = 0.2;
	        double targetRatio = (double) w / h;
	        if (sizes == null)
	            return null;

	        Size optimalSize = null;
	        double minDiff = Double.MAX_VALUE;

	        int targetHeight = h;

	        // Try to find an size match aspect ratio and size
	        for (Size size : sizes) {
	            Log.d(TAG, "Checking size " + size.width + "w " + size.height
	                    + "h");
	            double ratio = (double) size.width / size.height;
	            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
	                continue;
	            if (Math.abs(size.height - targetHeight) < minDiff) {
	                optimalSize = size;
	                minDiff = Math.abs(size.height - targetHeight);
	            }
	        }

	        // Cannot find the one match the aspect ratio, ignore the
	        // requirement
	        if (optimalSize == null) {
	            minDiff = Double.MAX_VALUE;
	            for (Size size : sizes) {
	                if (Math.abs(size.height - targetHeight) < minDiff) {
	                    optimalSize = size;
	                    minDiff = Math.abs(size.height - targetHeight);
	                }
	            }
	        }
	        return optimalSize;
	    }
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		
		if (mPreviewRunning) {
			 				mCamera.stopPreview();
			 			}
			 			Camera.Parameters p = mCamera.getParameters();
			 			

//			 		    switch(mDisplay.getRotation()){
//			 	        case Surface.ROTATION_0:
//			 	            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
//			 	                mCamera.setDisplayOrientation(90);
//			 	                Log.d("Rotation_0", "whatever");
//			 	            }
//			 	            else{
//			 	                Log.d("Rotation_0", "whatever");
//			 	                p.setRotation(90);
//			 	                mCamera.setParameters(p);
//			 	            }
//			 	            break;
//			 	        case Surface.ROTATION_90:
//			 	            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
//			 	                mCamera.setDisplayOrientation(0);
//			 	                Log.d("Rotation_0", "whatever");
//			 	            }
//			 	            else{
//			 	                Log.d("Rotation_90", "whatever");
//			 	                param.setRotation(0);
//			 	                mCamera.setParameters(param);
//			 	            }
//			 		    }			 	            
			 			p.setPreviewSize(vw, vh);
			 			mCamera.setParameters(p);
			 			try {
			 				
			 		    if (Integer.parseInt(Build.VERSION.SDK) >= 8)
			 		          setDisplayOrientation(mCamera, 90);
			 		      else
			 		      {
			 		          if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			 		          {
			 		              p.set("orientation", "portrait");
			 		              p.set("rotation", 90);
			 		          }
			 		          if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			 		          {
			 		              p.set("orientation", "landscape");
			 		              p.set("rotation", 90);
			 		          }
			 		      }   
			 		    
			 		    
			 		/*   int previewWidth = 0;
		 		        int previewHeight = 0;

		 		        
		 		            Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		 		            int rotation = display.getRotation();

		 		            switch (rotation) {
		 		            case Surface.ROTATION_0:
		 		             
		 		                mCamera.setDisplayOrientation(90);
		 		                break;

		 		            case Surface.ROTATION_90:
		 		               
		 		                mCamera.setDisplayOrientation(0);
		 		                break;

		 		            case Surface.ROTATION_180:
		 		               
		 		                mCamera.setDisplayOrientation(270);
		 		                break;

		 		            case Surface.ROTATION_270:
		 		               
		 		                mCamera.setDisplayOrientation(180);
		 		                break;
		 		            }

		 		           
		 		    
		 				p.setPreviewSize(vw, vh);
			 			mCamera.setParameters(p);
		 				mCamera.setPreviewDisplay(holder);*/
			 		    
			 				//setDisplayOrientation(mCamera, 90);
			 				//p.set("orientation", "landscape");
			 				//p.set("rotation", 90);
			 				 //p.setRotation(360);
			 				 
			 			//	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			 				mCamera.setPreviewDisplay(holder);
	 			         //	mCamera.setDisplayOrientation(90) ;;
	 				     //setDisplayOrientation(mCamera,90) ;
			 			
			 			} catch (IOException e) {
			 				e.printStackTrace();
			 			}
//			 			p.setRotation(90);
		                mCamera.setParameters(p);
//		                recorder.setOrientationHint(2);
			 			mCamera.startPreview();
			 			mPreviewRunning = true;
			
//		try{
//				if(camera != null)
//			{
//					Camera.Parameters parameters = camera.getParameters();
//
//			        List<Size> sizes = parameters.getSupportedPreviewSizes();
//			        Size optimalSize = getOptimalPreviewSize(sizes, width, height);         
//			        Log.d(TAG, "Surface size is " + width + "w " + height + "h");
//			        Log.d(TAG, "Optimal size is " + optimalSize.width + "w " + optimalSize.height + "h");           
//			        parameters.setPreviewSize(optimalSize.width, optimalSize.height);           
//			        // parameters.setPreviewSize(width, height);            
//			        camera.setParameters(parameters);
//			        camera.startPreview();
//			        recorder.setCamera(camera) ;
//			}
//			
//			}catch (Exception e) {
//				// TODO: handle exception
//			}
//			
	}
	protected void setDisplayOrientation(Camera camera, int angle) {
	    Method downPolymorphic;
	    try {//setOrientationHint
	        downPolymorphic = camera.getClass().getMethod(
	                "setDisplayOrientation", new Class[] { int.class });
	        if (downPolymorphic != null)
	            downPolymorphic.invoke(camera, new Object[] { angle });
	    } catch (Exception e1) {
	    }
	}
	boolean mPreviewRunning = false; 
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (recording) {
			recorder.stop();
			recording = false;
		}
		if(countDownTimer !=null)
		countDownTimer.cancel();
		mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
//		camera.release() ;
		recorder.release();
		finish();

	}

//	 public void onSensorChanged(SensorEvent event) {
//
//	    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	 private RotateAnimation getRotateAnimation(float toDegrees) {
	        float compensation = 0;

	        if (Math.abs(degrees - toDegrees) > 180) {
	            compensation = 360;
	        }

	        // When the device is being held on the left side (default position for
	        // a camera) we need to add, not subtract from the toDegrees.
	        if (toDegrees == 0) {
	            compensation = -compensation;
	        }

	        // Creating the animation and the RELATIVE_TO_SELF means that he image
	        // will rotate on it center instead of a corner.
	        RotateAnimation animation = new RotateAnimation(degrees, toDegrees
	                - compensation, Animation.RELATIVE_TO_SELF, 0.5f,
	                Animation.RELATIVE_TO_SELF, 0.5f);

	        // Adding the time needed to rotate the image
	        animation.setDuration(250);

	        // Set the animation to stop after reaching the desired position. With
	        // out this it would return to the original state.
	        animation.setFillAfter(true);

	        return animation;
	    }


	private int orientation;
	private int degrees = -1;
	@Override
	public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                RotateAnimation animation = null;
                if (event.values[0] < 4 && event.values[0] > -4) {
                    if (event.values[1] > 0
                            && orientation != ExifInterface.ORIENTATION_ROTATE_90) {
                        // UP
                        orientation = ExifInterface.ORIENTATION_ROTATE_90;
                        animation = getRotateAnimation(270);
                        degrees = 270;
                    } else if (event.values[1] < 0
                            && orientation != ExifInterface.ORIENTATION_ROTATE_270) {
                        // UP SIDE DOWN
                        orientation = ExifInterface.ORIENTATION_ROTATE_270;
                        animation = getRotateAnimation(90);
                        degrees = 90;
                    }
                } else if (event.values[1] < 4 && event.values[1] > -4) {
                    if (event.values[0] > 0
                            && orientation != ExifInterface.ORIENTATION_NORMAL) {
                        // LEFT
                        orientation = ExifInterface.ORIENTATION_NORMAL;
                        animation = getRotateAnimation(0);
                        degrees = 0;
                    } else if (event.values[0] < 0
                            && orientation != ExifInterface.ORIENTATION_ROTATE_180) {
                        // RIGHT
                        orientation = ExifInterface.ORIENTATION_ROTATE_180;
                        animation = getRotateAnimation(180);
                        degrees = 180;
                    }
                }
                if (animation != null) {
//                     rotatingImage.startAnimation(animation);
                }
            }
        }
	}
	CountDownTimer countDownTimer ;
	int totTimeToRec = 1000*60*10;
	public void startCountdonw(){
		countDownTimer=	 new CountDownTimer(totTimeToRec, 1000) {
	     public void onTick(long millisUntilFinished) {
	    	 long milliseconds = (totTimeToRec- millisUntilFinished) / 1000 ;
	    	 milliseconds = milliseconds*1000 ;
	    	 int seconds = (int) (milliseconds / 1000) % 60 ;
	    	 int minutes = (int) ((milliseconds / (1000*60)) % 60);
	    	 int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
	    	 if(minutes <10 && seconds <10){
	    		 timer.setText("0"+minutes +":" +"0"+seconds);
	    	 }
	    	 else if(minutes <10)
	    	 {
	    		 timer.setText("0"+minutes +":" +seconds); 
	    	 }
	    	 else if(seconds <10)
	    	 {
	    		 timer.setText(minutes +":" +"0"+seconds);
	    	 }	    	 
	     }
	     public void onFinish() {
	    	 timer.setText("done!");
	     }
	  }.start();
	  
	}
	
	QuickAction quickAction;
	public void action(int ids[], String[] name, final byte type) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, false,name.length);

		for (int i = 0; i < name.length; i++) {
			ActionItem nextItem = new ActionItem(ids[i], name[i], null);
			quickAction.addActionItem(nextItem);
		}

		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						action(actionId);
					}
				});

		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				// Toast.makeText(getApplicationContext(), "Dismissed",
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void action(int which) {
		System.out.println("-----------------size set----------"
				+ (resolutionInt[which][0] + "  , " + resolutionInt[which][1]));
		// recorder.setPreviewDisplay(holder.getSurface().setSize(choice2[which][0],choice2[which][1]));
		// recorder.setVideoSize(choice2[which][0],choice2[which][1]);//
		// best size for resolution.
		selection = which ;
		recorder.reset();
		vw = resolutionInt[which][0];
		vh = resolutionInt[which][1];
	}

}