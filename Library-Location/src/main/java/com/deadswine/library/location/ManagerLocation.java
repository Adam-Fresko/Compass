package com.deadswine.library.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.deadswine.library.location.Otto.EventLocationChanged;
import com.deadswine.library.location.Otto.Otto;

/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */
public class ManagerLocation {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;

    private void log(String log) {
        Log.d(TAG, log);
    }

    static final int UPDATE_INTERVAL = 1000;
    static final int UPDATE_INTERVAL_FASTEST = 500;
    static final float UPDATE_DISTANCE = 10;


    private static ManagerLocation instance = null;
    private Context mContext;

    private android.location.LocationListener mLocationListener;
    private LocationManager mLocationManager;

    private boolean isInProggress;

    /**
     * Returns singleton object.<br> Its best to prevent multiple instantiation of this class
     *
     * @param context
     * @return
     */
    public static ManagerLocation getInstance(Context context) {

        if (instance == null) {
            instance = new ManagerLocation(context);
        }

        return instance;
    }

    protected ManagerLocation(Context context) {
        mContext = context;

    }


    public void locationStart() {
        log("locationStart");


        if (mLocationListener != null && mLocationManager != null) {
            locationPause();
        }

        mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                log("Latitude GPS : " + location.getLatitude());
                log("Longitude GPS: " + location.getLongitude());

                Otto.getInstance().post(new EventLocationChanged(location));
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
        };


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(mContext, "Location Permissions is not granted", Toast.LENGTH_SHORT).show();

        } else {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            }

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, mLocationListener);
            isInProggress = true;


        }


    }


    public void locationPause() {
        log("locationPause");


        if (mLocationManager != null) {

            if (mLocationListener != null) {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(mContext, "Location Permissions is not granted", Toast.LENGTH_SHORT).show();
                } else {
                    mLocationManager.removeUpdates(mLocationListener);
                    isInProggress = false;
                }

            }

            mLocationManager = null;
            mLocationListener = null;

            mContext = null;
            instance = null;

        }

    }

    /**
     * Returns true if location tracking has started otherwise returns false
     *
     * @return
     */
    public boolean locationToggle() {

        if (isInProggress) {
            locationPause();
            return false;
        } else {
            locationStart();

            return true;
        }


    }


}
