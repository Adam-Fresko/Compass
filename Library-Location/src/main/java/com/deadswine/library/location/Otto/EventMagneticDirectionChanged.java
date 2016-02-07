package com.deadswine.library.location.Otto;



/**
 * Created by Adam Fręśko - Deadswine Studio on 07.02.2016.
 * Deadswine.com
 */

public class EventMagneticDirectionChanged {

    double angle;


    public EventMagneticDirectionChanged(double angle) {
        this.angle = angle;
    }


    public double getAngle() {
        return angle;
    }
}
