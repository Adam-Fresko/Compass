package com.deadswine.library.view.compass;

import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deadswine.library.view.compass.Utilities.UtilitiesMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class FragmentCompassMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }

    private View mContentView;
    private FrameLayout mFrameLayout;
    private TextView tvDegree;

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    Polyline mPolyline;
    private Marker mMapMarker;
    private Marker mMapMarkerLocation;


    private InterfaceMap mInterface;

    public interface InterfaceMap {

        void onMapTargetChoosen(LatLng targetLatLng);

        void onMapLocationChanged(LatLng targetLatLng);
    }

    public void addInterface(InterfaceMap mInterface) {
        this.mInterface = mInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_compass_map, container, false);
        }

        mFrameLayout = (FrameLayout) mContentView.findViewById(R.id.content);
        tvDegree = (TextView) mContentView.findViewById(R.id.compass_map_tv);
        mMapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.content, mMapFragment).commit();

        mMapFragment.getMapAsync(this);

        return mContentView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (mGoogleMap == null)
            return;

        if (mMapMarker != null)
            mMapMarker.remove();

        if (mInterface == null)
            throw new UnknownError("You forgot to set interface");

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);

        mMapMarker = mGoogleMap.addMarker(markerOptions);
        mInterface.onMapTargetChoosen(mMapMarker.getPosition());
        drawLineBetweenLocationAndTarget();
    }


    public void setLocation(Location location) {

        if (mContentView == null) {
            return;
        }

        if (mMapMarkerLocation != null) {
            mMapMarkerLocation.remove();
        }

        LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(tmp);
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_button_unchecked_black_24dp));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        markerOptions.draggable(false);

        mMapMarkerLocation = mGoogleMap.addMarker(markerOptions);

        drawLineBetweenLocationAndTarget();
    }


    private void drawLineBetweenLocationAndTarget() {

        if (mMapMarker == null || mMapMarkerLocation == null || mGoogleMap == null)
            return;

        ArrayList<LatLng> tmp = new ArrayList<>(2);
        tmp.add(mMapMarker.getPosition());
        tmp.add(mMapMarkerLocation.getPosition());


        if (mPolyline == null) {
            mPolyline = UtilitiesMap.getPolylineBetweenPoints(getContext(), mGoogleMap, tmp);
        } else {
            mPolyline.setPoints(tmp);
        }


        if (mInterface == null)
            throw new UnknownError("You forgot to set interface");
        mInterface.onMapLocationChanged(mMapMarkerLocation.getPosition());


        printDegreesText();

    }


    private void printDegreesText() {

        tvDegree.setText("Degrees: " + UtilitiesMap.calcRotationAngleInDegrees(mMapMarker.getPosition(), mMapMarkerLocation.getPosition()));

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        drawLineBetweenLocationAndTarget();
        mInterface.onMapTargetChoosen(mMapMarker.getPosition());
    }
}
