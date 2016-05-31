package com.kainat.app.android;

import com.kainat.app.android.core.BusinessProxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkConnectivityListener extends BroadcastReceiver {
	private static final String TAG = NetworkConnectivityListener.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			 Log.d(TAG , "onReceive :: CONNECTIVITY CHANGED ");
			 ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
				if(isConnected){
					//Start refresh service
					 if(BusinessProxy.sSelf.mUIActivityManager != null){
						 Log.d(TAG , "onReceive :: CONNECTED : restartServices()");
//						 UIActivityManager.startChannelRefresh = true;
//						 BusinessProxy.sSelf.mUIActivityManager.restartServices();
					 }
				}else{
					//Start refresh service
					 if(BusinessProxy.sSelf.mUIActivityManager != null){
						 Log.d(TAG , "onReceive :: DISCONNECTED : stopServices()");
//						 UIActivityManager.startChannelRefresh = false;
//						 BusinessProxy.sSelf.mUIActivityManager.stopServices();
					 }
				}
					
		 }
	}
}
