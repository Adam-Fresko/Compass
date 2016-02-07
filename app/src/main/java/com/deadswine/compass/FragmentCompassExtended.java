package com.deadswine.compass;

import android.util.Log;

import com.deadswine.library.location.Otto.EventMagneticDirectionChanged;
import com.deadswine.library.location.Otto.Otto;
import com.deadswine.library.view.compass.FragmentCompass;
import com.squareup.otto.Subscribe;


/**
 * Created by Adam Fręśko - Deadswine Studio on 07.02.2016.
 * Deadswine.com
 */

public class FragmentCompassExtended extends FragmentCompass {


    @Override
    public void onPause() {
        super.onPause();

        Otto.getInstance().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Otto.getInstance().unregister(this);
    }


    @Subscribe
    public void onEventMagneticDirectionChanged(EventMagneticDirectionChanged event) {

        getViewCompass().setAngleMagnetometer((float) event.getAngle());

    }
}
