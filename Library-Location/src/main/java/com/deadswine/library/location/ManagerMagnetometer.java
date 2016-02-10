package com.deadswine.library.location;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.deadswine.library.location.Otto.EventLocationChanged;
import com.deadswine.library.location.Otto.EventMagneticDirectionChanged;
import com.deadswine.library.location.Otto.Otto;

import java.util.ArrayDeque;


/**
 * Created by Adam Fręśko - Deadswine Studio on 07.02.2016.
 * Deadswine.com
 */

public class ManagerMagnetometer implements SensorEventListener {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }

    private static ManagerMagnetometer instance = null;
    private Context mContext;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;


    // globals
    private float[] gravityData = new float[3];
    private float[] geomagneticData  = new float[3];
    private boolean hasGravityData = false;
    private boolean hasGeomagneticData = false;
    private double  rotationInDegrees;

    private AngleLowpassFilter mAngleLowpassFilter;

    /**
     * Returns singleton object.<br> Its best to prevent multiple instantiation of this class
     *
     * @param context
     * @return
     */
    public static ManagerMagnetometer getInstance(Context context) {

        if (instance == null) {
            instance = new ManagerMagnetometer(context);
        }

        return instance;
    }

    protected ManagerMagnetometer(Context context) {
        mContext = context;


    }


    public void start() {

        if (mSensorManager == null) {

            mAngleLowpassFilter = new AngleLowpassFilter();

            mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);

        }

    }

    public void stop() {

        if (mSensorManager != null) {

            mSensorManager.unregisterListener(this, mMagnetometer);
            mSensorManager.unregisterListener(this, mAccelerometer);

            mSensorManager = null;
            mMagnetometer = null;
            mAccelerometer = null;

            mAngleLowpassFilter=null;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, gravityData, 0, 3);
                hasGravityData = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, geomagneticData, 0, 3);
                hasGeomagneticData = true;
                break;
            default:
                return;
        }

        if (hasGravityData && hasGeomagneticData) {
            float identityMatrix[] = new float[9];
            float rotationMatrix[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, identityMatrix,
                    gravityData, geomagneticData);

            if (success) {
                float orientationMatrix[] = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientationMatrix);
                float rotationInRadians = orientationMatrix[0];

                mAngleLowpassFilter.add(rotationInRadians); // this will smooth readings a litle
                rotationInDegrees = Math.toDegrees(mAngleLowpassFilter.average());

                Otto.getInstance().post(new EventMagneticDirectionChanged(rotationInDegrees));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public class AngleLowpassFilter {

        private final int LENGTH = 10;

        private float sumSin, sumCos;

        private ArrayDeque<Float> queue = new ArrayDeque<Float>();

        public void add(float radians){

            sumSin += (float) Math.sin(radians);

            sumCos += (float) Math.cos(radians);

            queue.add(radians);

            if(queue.size() > LENGTH){

                float old = queue.poll();

                sumSin -= Math.sin(old);

                sumCos -= Math.cos(old);
            }
        }

        public float average(){

            int size = queue.size();

            return (float) Math.atan2(sumSin / size, sumCos / size);
        }
    }
}
