package com.kainat.app.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.kainat.app.android.uicontrol.CamcorderView;

public class Camcorder extends Activity {

	     private CamcorderView camcorderView; 
	     private boolean recording = false; 

	     /** Called when the activity is first created. */ 
	     @Override 
	     public void onCreate(Bundle savedInstanceState) { 
	          super.onCreate(savedInstanceState); 
	          requestWindowFeature(Window.FEATURE_NO_TITLE); 
	          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
	          setContentView(R.layout.camcorder_preview); 
	          
	          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	          camcorderView = (CamcorderView) findViewById(R.id.camcorder_preview); 
 	     } 
	     
	     @Override
	    public void onBackPressed() {
	    	 setResult(2);
	    	 finish();
	   
	    }
         @Override 
         public boolean onKeyDown(int keyCode, KeyEvent event) 
         { 
             if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) 
             { 
           	  if (recording) { 
           		  	camcorderView.stopRecording();
                    finish(); 
                } else { 
                    recording = true; 
                    camcorderView.startRecording(); 
                } 
                 return true; 
             } 
             return super.onKeyDown(keyCode, event); 
         }	     
}