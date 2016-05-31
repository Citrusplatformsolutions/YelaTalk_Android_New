package com.kainat.app.android.util;

public class DiscoveryBasicInfo {
	public String node;
	public String subtitle;
	public String url;
	
	@Override
	public String toString() {
		return "[node : "+node 
		+"][subtitle : "+subtitle+"]"	
		+"][url : "+url+"]"	
		;
	}
}
