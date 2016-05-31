package com.kainat.app.android;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kainat.app.android.util.Logger;

public class C2DMRegisterService extends Service {
	public static String strSenderId = "rocketalkconnect@gmail.com";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private static final String ACTION = "com.google.android.c2dm.intent.REGISTRATION";
	private static final String ACTION2 = "com.google.android.c2dm.intent.RECEIVE";

	@Override
    public void onCreate() {
        super.onCreate();
        if(Logger.ENABLE)
        System.out.println("::::::::::::::::::::::::::::C2DMRegisterService:::::::::::::::::::::C2DMRegisterService");
		
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
		registrationIntent.putExtra("sender", strSenderId);
		startService(registrationIntent);
    }
	
	@Override
	public void onDestroy() {
		//code to execute when the service is shutting down
	}

	@Override
	public void onStart(Intent intent, int startid) {
		//code to execute when the service is starting up
	}
}