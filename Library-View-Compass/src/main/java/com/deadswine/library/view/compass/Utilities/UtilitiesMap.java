package com.deadswine.library.view.compass.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class UtilitiesMap {

    public static final int widthDp = 2;


    public static Polyline getPolylineBetweenPoints(Context context, GoogleMap map, ArrayList<LatLng> latLngs) {

      return   map.addPolyline(new PolylineOptions().addAll(latLngs)
                .color(Color.BLACK)
                .width(UtilitiesView.dpToPx(context, widthDp))
                .geodesic(false)
                .zIndex(2.0f));


    }



    /**
     * Taken from http://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
     * @param centerPt
     * @param targetPt
     * @return
     */
    public static double calcRotationAngleInDegrees(LatLng centerPt, LatLng targetPt)
    {


        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(targetPt.latitude - centerPt.latitude, targetPt.longitude - centerPt.longitude);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        // addition would rotate it clockwise
        theta += Math.PI/2.0; // comment to disable rottation

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }



    /**
     * Taken from http://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
     * @param centerPt
     * @param targetPt
     * @return
     */
    public static double calcRotationAngleInDegrees(Location centerPt, LatLng targetPt)
    {


        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(targetPt.latitude - centerPt.getLatitude(), targetPt.longitude - centerPt.getLongitude());

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        // addition would rotate it clockwise
        theta += Math.PI/2.0; // comment to disable rottation

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }





}
