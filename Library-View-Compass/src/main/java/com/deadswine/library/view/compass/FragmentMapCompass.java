package com.deadswine.library.view.compass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class FragmentMapCompass extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }

    private View mContentView;
    private FrameLayout mFrameLayout;


    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Marker mMapMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_compass_map, container, false);
        }

        mFrameLayout = (FrameLayout) mContentView.findViewById(R.id.content);

        mMapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.content, mMapFragment).commit();

        mMapFragment.getMapAsync(this);

        return mContentView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMapMarker != null)
            mMapMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);

        mMapMarker = mGoogleMap.addMarker(markerOptions);
    }
}
