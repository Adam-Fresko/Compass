package com.deadswine.library.location.Otto;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Adam Fręśko on 31.03.2015.
 */
public final class Otto {

    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    //  new Bus(ThreadEnforcer.ANY); //  this should run on any thread

    public static Bus getInstance() {
        return BUS;
    }

    private Otto() {
        // No instances.
    }
}