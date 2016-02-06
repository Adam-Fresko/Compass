package com.deadswine.library.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class ServiceLocation extends Service {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;
    public void log(String log) {
        Log.d(TAG, log);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
