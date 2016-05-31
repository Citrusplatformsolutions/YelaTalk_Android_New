package com.kainat.app.android.facebook;

import com.kainat.app.android.facebook.Facebook.DialogListener;


public abstract class BaseDialogListener implements DialogListener {

    public void onFacebookError(FacebookError e) {
        e.printStackTrace();
    }

    public void onError(DialogError e) {
        e.printStackTrace();        
    }

    public void onCancel() {        
    }
    public void onSharingDone() {        
    }
}
