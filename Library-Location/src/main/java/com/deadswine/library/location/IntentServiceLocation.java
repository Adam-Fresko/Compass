package com.deadswine.library.location;

import android.util.Log;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class IntentServiceLocation {
    private final String TAG = this.getClass().getSimpleName();

    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }
}
