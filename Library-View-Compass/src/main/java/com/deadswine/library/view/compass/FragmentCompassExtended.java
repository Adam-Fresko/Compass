package com.deadswine.library.view.compass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class FragmentCompassExtended extends Fragment{
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;
    public void log(String log) {
        Log.d(TAG, log);
    }

    private View mContentView;

    private ViewCompass mViewCompass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_compass, container, false);
        }

        mViewCompass = (ViewCompass) mContentView.findViewById(R.id.compass_view_compass);

        return mContentView;
    }


    public ViewCompass getViewCompass() {
        return mViewCompass;
    }
}
