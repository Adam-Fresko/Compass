package com.deadswine.compass;

import android.support.v4.app.Fragment;
import android.util.Log;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class FragmentCompass extends Fragment{
    private final String TAG = this.getClass().getSimpleName();


    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }
}
