/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kainat.app.android.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kainat.app.android.R;
import com.kainat.app.android.facebook.Facebook.DialogListener;
import com.kainat.app.android.inf.DilogInf;

public class RTDialogNotification extends Dialog {

	DilogInf dilogInf ;
    static final int FB_BLUE = 0xFF6D84B4;
    static final float[] DIMENSIONS_DIFF_LANDSCAPE = {20, 60};
    static final float[] DIMENSIONS_DIFF_PORTRAIT = {40, 60};
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;
    static final String DISPLAY_STRING = "touch";
    static final String FB_ICON = "icon.png";

    private String message;
    private  DialogListener mListener;
   
    private ImageView mCrossImage;

    private FrameLayout mContent;
    private TextView status;
    public RTDialogNotification(Context context, DialogListener listener,String mmsg,DilogInf dilogInf) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        message = mmsg ;
        mListener = listener;
       this.dilogInf = dilogInf ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContent = new FrameLayout(getContext());
        setContentView(R.layout.rt_dialog_notification);
        status =  (TextView)findViewById(R.rt_dialog.status) ;
       
        mCrossImage  =  (ImageView)findViewById(R.id.frm) ;
        
        if(message!=null)
        	changeMessgae(message) ;
       
        mContent.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				RTDialogNotification.this.dismiss() ;
				if(dilogInf!=null)
					dilogInf.cancelAlertDilog() ;
			}
		}, 3000) ;
        mContent.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				 Utilities.startAnimition(RTDialogNotification.this.getContext(), mCrossImage, R.anim.grow_from_midddle) ;
			}
		}, 3000) ;
       
    }
    
    @Override
    public void onBackPressed() {
    	
    }
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    }
    public void changeMessgae(String msg){
    	status.setText(msg);
    	Utilities.startAnimition(status.getContext(),status, R.anim.rail) ;
    }
    public void closeLoading(){
    }
   
}
