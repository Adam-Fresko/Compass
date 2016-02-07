package com.deadswine.compass;

import com.deadswine.library.location.Otto.EventMagneticDirectionChanged;
import com.deadswine.library.location.Otto.Otto;
import com.squareup.otto.Subscribe;


/**
 * Created by Adam Fręśko - Deadswine Studio on 07.02.2016.
 * Deadswine.com
 */

public class FragmentCompassExtended extends com.deadswine.library.view.compass.FragmentCompassExtended {


    @Override
    public void onPause() {
        super.onPause();

        Otto.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Otto.getInstance().register(this);

    }


    @Subscribe
    public void onEventMagneticDirectionChanged(EventMagneticDirectionChanged event) {

        getViewCompass().setAngleMagnetometer((float) event.getAngle());

    }
}
