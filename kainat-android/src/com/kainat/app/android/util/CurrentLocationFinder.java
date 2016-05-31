package com.kainat.app.android.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class CurrentLocationFinder {
	private Timer timer;
	private LocationManager lm;
	private LocationResult locationResult;
	private boolean gpsEnabled = false;
	private boolean networkEnabled = false;

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.init(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.init(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public boolean getLocation(Context context, LocationResult result) {
		if (result == null) {
			throw new RuntimeException("");
		}
		locationResult = result;
		if (lm == null) {
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}

		try {
			gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gpsEnabled && !networkEnabled) {
			return false;
		}

		if (gpsEnabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}
		if (networkEnabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		}
		timer = new Timer();
		timer.schedule(new GetLastLocation(), 3000);
		return true;
	}

	class GetLastLocation extends TimerTask {
		public void run() {
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
			if (gpsEnabled) {
				gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (networkEnabled) {
				net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime()) {
					locationResult.init(gps_loc);
				} else {
					locationResult.init(net_loc);
				}
				return;
			}

			if (gps_loc != null) {
				locationResult.init(gps_loc);
				return;
			}
			if (net_loc != null) {
				locationResult.init(net_loc);
				return;
			}
			locationResult.init(null);
		}
	}

	public static abstract class LocationResult {
		public void init(Location location) {
			gotLocation(location);
		}

		public abstract void gotLocation(Location location);
	}
}