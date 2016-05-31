package com.kainat.app.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootHelper extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
//		System.out.println("::::::::::::::::::::::::::::BootHelper:::::::::::::::::::::BootHelper");
		
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			//if(Logger.ENABLE)
//			System.out.println("::::::::::::::::::::::::::::BootHelper:::::::::::::::::::::BootHelper");
//			 Intent myIntent = new Intent(context, GcmIntentService.class);
//		     myIntent.putExtra("extraData", "somedata");
//		     context.startService(myIntent);
		}
	}
}