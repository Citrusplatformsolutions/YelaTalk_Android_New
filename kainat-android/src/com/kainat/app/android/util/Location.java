package com.kainat.app.android.util;

public class Location {
	public double latitude, longitude;
	public String address  ="";
	public byte prority = 0 ;
	public Location(double latitude,double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public int getLatitudeE6() {
		return (int) (latitude * 1000000);
	}

	public int getLongitudeE6() {
		return (int) (longitude * 1000000);
	}
}
