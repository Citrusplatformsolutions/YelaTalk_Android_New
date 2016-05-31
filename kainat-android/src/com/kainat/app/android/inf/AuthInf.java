package com.kainat.app.android.inf;

public interface AuthInf {
	
	public void onAuthSucceed();

	public void onAuthFail(String error);

	public void onLogoutBegin();

	public void onLogoutFinish();
	
}
