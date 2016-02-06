package com.deadswine.library.location.Otto;

import android.location.Location;
import android.util.Log;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class EventLocationChanged {

    Location location;

    public EventLocationChanged(Location location) {
        this.location = location;
    }


    public Location getLocation() {
        return location;
    }
}
