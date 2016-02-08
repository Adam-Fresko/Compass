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
import com.deadswine.library.location.Otto.EventRequestPermission;
import com.deadswine.library.location.Otto.Otto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;


/**
 * Created by Adam Fręśko - Deadswine Studio on 08.02.2016.
 * Deadswine.com
 */

public class ManagerLocationGoogle implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }


    static final int UPDATE_INTERVAL = 1000;
    static final int UPDATE_INTERVAL_FASTEST = 500;
    static final float UPDATE_DISTANCE = 10;


    private static ManagerLocationGoogle instance = null;
    private Context mContext;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    LocationSettingsRequest.Builder mLocationSettingsRequestBuilder;

    private boolean isInProggress;

    /**
     * Returns singleton object.<br> Its best to prevent multiple instantiation of this class
     *
     * @param context
     * @return
     */
    public static ManagerLocationGoogle getInstance(Context context) {

        if (instance == null) {
            instance = new ManagerLocationGoogle(context);
        }

        return instance;
    }

    protected ManagerLocationGoogle(Context context) {
        mContext = context;

    }

    public void locationStart() {
        log("locationStart");

        if (mLocationRequest != null) {
            locationPause();
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(mContext, "Location Permissions is not granted", Toast.LENGTH_SHORT).show();

            Otto.getInstance().post(new EventRequestPermission());

        } else {

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(UPDATE_INTERVAL_FASTEST);
            mLocationRequest.setSmallestDisplacement(UPDATE_DISTANCE);

            mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();

            isInProggress = true;

        }

    }

    public void locationPause() {
        log("locationPause");


        if (mLocationRequest != null) {


            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }


        }


        isInProggress = false;

        mGoogleApiClient = null;
        mLocationRequest = null;

        mContext = null;
        instance = null;


    }


    @Override
    public void onConnected(Bundle bundle) {

        // WE ALREDY checked permission
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        locationPause();
        locationStart();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationPause();
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


    @Override
    public void onLocationChanged(Location location) {
        Otto.getInstance().post(new EventLocationChanged(location));
    }
}
