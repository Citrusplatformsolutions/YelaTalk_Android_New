

package com.kainat.app.android.facebook;




import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;

import com.kainat.app.android.facebook.Facebook.DialogListener;
import com.kainat.app.android.facebook.SessionEvents.AuthListener;
import com.kainat.app.android.facebook.SessionEvents.LogoutListener;

public class Login  {
    
    private Facebook mFb;
    private Handler mHandler;
    private SessionListener mSessionListener = new SessionListener();
    private String[] mPermissions;
    private Activity mActivity;
    Context mContext;
    Bundle mParam;
    public Login(Activity mctivity) {
       super();
       mActivity = mctivity;
      
    }
    
    public Login(Context context, AttributeSet attrs) {
        super();
        mContext=context;
    }
    
    public Login(Context context, AttributeSet attrs, int defStyle) {
        super();
        mContext=context;
    }
//    String[] main_items = { "Update Status", "App Requests", "Get Friends", "Upload Photo",
//            "Place Check-in", "Run FQL Query", "Graph API Explorer", "Token Refresh" };
//    String[] permissions = { "offline_access", "publish_stream", "user_photos", 
//            "photo_upload" };
//    String[] permissions = {"publish_stream", "user_photos", "publish_actions","read_stream",
//         "photo_upload"};
    //@"email",@"user_photos",@"user_about_me",@"basic_info", @"user_birthday"
//    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins",
//            "photo_upload","email","user_birthday","basic_info","user_about_me" };
    
//    String[] permissions = { "user_photos",
//            "email","user_birthday","basic_info"};
    
    
    String[] permissions = { "user_photos",
            "photo_upload","email","user_birthday","basic_info","user_about_me" };
    
    
    //String[] permissions = {""};
    public void init(final Activity activity, final Facebook fb ) {
    	//mActivity = activity;    
    	init(activity, fb, permissions);
    	
    }
    
    public void init(final Activity activity, final Facebook fb,
                     final String[] permissions) {
    	mActivity = activity;
        mFb = fb;
        mPermissions = permissions;
        mHandler = new Handler();
        
      
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
       ButtonOnClickListener();
    }
    
    private final void ButtonOnClickListener(){
            if (mFb.isSessionValid()) {
                SessionEvents.onLogoutBegin();
                AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
                asyncRunner.logout(mActivity, new LogoutRequestListener());
            } else {
                mFb.authorize(mActivity, mPermissions,
                              new LoginDialogListener());
            }
        
    }

    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
        @Override
        public void onSharingDone() {
        	
        	// TODO Auto-generated method stub
        	
        }
    }
    
    private class LogoutRequestListener extends BaseRequestListener {
        public void onComplete(String response, final Object state) {
            // callback should be run in the original thread, 
            // not the background thread
            mHandler.post(new Runnable() {
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }
    
    private class SessionListener implements AuthListener, LogoutListener {
        
        public void onAuthSucceed() {
        	
            SessionStore.save(mFb, mActivity);
        }

        public void onAuthFail(String error) {
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish() {
            SessionStore.clear(mActivity);
          
    }
    
}
}
