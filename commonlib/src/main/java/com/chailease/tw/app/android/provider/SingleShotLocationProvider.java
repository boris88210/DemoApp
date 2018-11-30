package com.chailease.tw.app.android.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;

import com.chailease.tw.app.android.utils.PermissionChecker;


/**
 * reference from
 * http://stackoverflow.com/questions/29657948/get-the-current-location-fast-and-once-in-android
 */
public class SingleShotLocationProvider {

	public static interface LocationCallback {
		public void onNewLocationAvailable(GPSCoordinates location);
	}

	// calls back to calling thread, note this is for low grain: if you want higher precision, swap the
	// contents of the else and if. Also be sure to check gps permission/settings are allowed.
	// call usually takes <10ms
	@RequiresPermission(allOf = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION})
	public static void requestSingleUpdate(final Context context, final LocationCallback callback) {    //   throws SecurityException
		final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (isNetworkEnabled) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);

			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			locationManager.requestSingleUpdate(criteria, new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
				}

				@Override
				public void onProviderEnabled(String provider) {
				}

				@Override
				public void onProviderDisabled(String provider) {
				}
			}, null);
		} else {
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (isGPSEnabled) {
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				locationManager.requestSingleUpdate(criteria, new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {
					}

					@Override
					public void onProviderEnabled(String provider) {
					}

					@Override
					public void onProviderDisabled(String provider) {
					}
				}, null);
			}
		}
	}

	public static void requestSingleUpdate(final Activity activity, final LocationGrantExecutor callback) {    //   throws SecurityException
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (callback.hasTry) throw new IllegalStateException("LocationGrantExecutor already try to request permission can not try again");    // this callback already to try to request permission do not try again in same time
			callback.hasTry = true;
			if(null != PermissionChecker.grantPermissions(activity, callback.PERMISSION_REQUEST_CODE,
					Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
				//  user has deny call phone permission, so need to show reason to user or try again
				PermissionChecker.grantPermissions(activity,
						callback.PERMISSION_REQUEST_CODE,
						android.Manifest.permission.CALL_PHONE);
			}
			return;
		} else {
			requestSingleUpdate(activity, (LocationCallback) callback);
		}
	}

	abstract public static class LocationGrantExecutor extends PermissionChecker.GrantExecutor implements LocationCallback {
		private boolean hasTry = false;
		private int PERMISSION_REQUEST_CODE;
		final public void setPermissionRequestCode(int requestCode) {
			PERMISSION_REQUEST_CODE = requestCode;
		}
	}

	// consider returning Location instead of this dummy wrapper class
	public static class GPSCoordinates {
		public float longitude = -1;
		public float latitude = -1;

		public GPSCoordinates(float theLatitude, float theLongitude) {
			longitude = theLongitude;
			latitude = theLatitude;
		}

		public GPSCoordinates(double theLatitude, double theLongitude) {
			longitude = (float) theLongitude;
			latitude = (float) theLatitude;
		}
	}

}