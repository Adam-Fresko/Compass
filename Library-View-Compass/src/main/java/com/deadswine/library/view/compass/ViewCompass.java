package com.deadswine.library.view.compass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.deadswine.library.view.compass.Utilities.UtilitiesView;


/**
 * Created by Adam Fręśko - Deadswine Studio on 06.02.2016.
 * Deadswine.com
 */

public class ViewCompass extends View {
    private final String TAG = this.getClass().getSimpleName();
    boolean isDebug = true;

    public void log(String log) {
        Log.d(TAG, log);
    }


    private int[] mGradientColors;
    private float[] mGradientSteps;

    private String[] mLetters;

    private Paint mPaintInnerRose;
    private Paint mPaintRingOuter;
    private Paint mPaintRingInner;
    private Paint mPaintInnerGradient;
    private Paint mPaintInnerText;
    private Paint mPaintMagnetometerArrow;

    private Paint mPaintTarget;
    private Bitmap mBitmapInnerRose;
    private Bitmap mBitmapTarget;

    private Path mPathInnerCircle;
    private Path mPathMagnetomereArrow;

    private int mCircleRadius;
    private int mCircleRadiusInner;

    private int mCircleRingOuterWidth;
    private int mCircleRingOuterPadding;

    private int mCircleRingInnerWidth;
    private int mCircleRingInnerPadding;
    private int mCircleInnerPadding;

    private int mTargetWidth;

    private int mScalePadding;

    private int mCenterX;
    private int mCenterY;

    private float mAngleBase;
    private float mAngleCurrent; // used for rotation testing
    private float mAngleTarget;
    private float mAngleMagnetometer;

    private float mAngleMagnetometerOld;
    private float mAngleMagnetometerTarget;

    private boolean mIsAngleTargetSet;
    private boolean mIsAngleMagnetometerSet;

    private int mFramesLeftToDraw;

    {
        mFramesLeftToDraw = 0;


        mLetters = new String[]{
                "N", "NE", "E", "SE", "S", "SW", "W", "NW"
        };

        mGradientColors = new int[]{
                getResources().getColor(R.color.compass_inner_gradient_start),
                getResources().getColor(R.color.compass_inner_gradient_start),
                getResources().getColor(R.color.compass_inner_gradient_end)
        };

        mGradientSteps = new float[]{
                0f,
                0.85f,
                1f
        };

        mTargetWidth = UtilitiesView.dpToPx(getContext(), 12);

        mPaintInnerGradient = new Paint();
        mPaintInnerGradient.setDither(true);
        mPaintInnerGradient.setAntiAlias(true);

        mPaintInnerText = new Paint();
        mPaintInnerText.setColor(getResources().getColor(R.color.compass_ring_inner));
        mPaintInnerText.setStyle(Paint.Style.FILL);
        mPaintInnerText.setAntiAlias(true);
        mPaintInnerText.setTextSize(UtilitiesView.dpToPx(getContext(), 20));

        mPaintInnerRose = new Paint();
        mPaintInnerRose.setAntiAlias(true);
        mPaintInnerRose.setFilterBitmap(true);


        mPaintTarget = new Paint();
        mPaintTarget.setColor(getResources().getColor(R.color.compass_target_background));
        mBitmapTarget = UtilitiesView.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_navigation_black_24dp), mTargetWidth, mTargetWidth, UtilitiesView.ScalingLogic.FIT);

        mPaintMagnetometerArrow = new Paint();
        mPaintMagnetometerArrow.setStyle(Paint.Style.FILL);
        mPaintMagnetometerArrow.setAntiAlias(true);
        mPaintMagnetometerArrow.setColor(Color.RED);

        mPaintRingOuter = new Paint();
        mPaintRingOuter.setColor(getResources().getColor(R.color.compass_ring_outer));
        mPaintRingOuter.setStyle(Paint.Style.FILL);
        mPaintRingOuter.setAntiAlias(true);

        mPaintRingInner = new Paint();
        mPaintRingInner.setColor(getResources().getColor(R.color.compass_ring_inner));
        mPaintRingInner.setStyle(Paint.Style.FILL);
        mPaintRingInner.setAntiAlias(true);


        mPathInnerCircle = new Path();
        mPathMagnetomereArrow = new Path();


        mCircleRingOuterWidth = UtilitiesView.dpToPx(getContext(), 12);
        mCircleRingOuterPadding = mCircleRingOuterWidth;

        mCircleRingInnerWidth = UtilitiesView.dpToPx(getContext(), 4);
        mCircleRingInnerPadding = mCircleRingOuterWidth + mCircleRingOuterPadding;


        mCircleInnerPadding = mCircleRingInnerPadding + mCircleRingInnerWidth;

        mScalePadding = mCircleRingInnerPadding + mCircleRingInnerWidth + UtilitiesView.dpToPx(getContext(), 8);


    }

    public ViewCompass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // WE are drawing circles, it will be easier if our view will be a square

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > (int) (height + 0.5) && height != 0) {
            width = (int) (height + 0.5);
        } else {
            height = (int) (width + 0.5);
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        log("onSizeChanged");

        mCircleRadius = getWidth() / 2;

        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;

        mAngleBase = -90;
        mAngleCurrent = 0;
        mAngleTarget = 0;
        mAngleMagnetometer = 0;

        RadialGradient gradient = new RadialGradient(mCenterX, mCenterY, mCircleRadius - mCircleInnerPadding, mGradientColors, mGradientSteps, Shader.TileMode.CLAMP);


        mBitmapInnerRose = UtilitiesView.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rose), (mCircleRadius), (mCircleRadius), UtilitiesView.ScalingLogic.FIT);


        mPaintInnerGradient.setShader(gradient);

        mPathInnerCircle.reset();
        mPathInnerCircle.addCircle(mCenterX, mCenterY, (mCircleRadius - mCircleInnerPadding) - 20, Path.Direction.CW);

    }

    boolean testRotation = false;
    float mAngleInterpolated;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircleRingOuter(canvas);
        drawCircleRingInner(canvas);
        drawCircleInnerBackground(canvas);
        drawCircleInnerRose(canvas);
        drawCircleInnerPin(canvas);

        // now we need to add rotation logic// rotating canvas and then resetting should be enough
        canvas.save();

        if (testRotation) {
            if (mAngleCurrent == 360) {
                mAngleCurrent = 0;
            }

            canvas.rotate(mAngleBase + mAngleCurrent, mCenterX, mCenterY); // -90 makes fist letter pointing to top
            drawTextDirections(canvas);
            canvas.restore();

            mAngleCurrent += 1;

        } else {
            // FIXME this will also require some smoothing// but it works atm
// // Smoothing prototype code
//            if (mFramesLeftToDraw <= 10 || mAngleMagnetometer ==mAngleMagnetometerTarget ) {
//                // we can draw next value interpolation
//                log("interpolating " + mFramesLeftToDraw);
//                mAngleInterpolated = UtilitiesView.lerp(mAngleMagnetometerOld, mAngleMagnetometerTarget, (mFramesLeftToDraw * 10) / 100);
//
//                canvas.rotate(mAngleBase + (-mAngleInterpolated), mCenterX, mCenterY); // -90 makes fist letter pointing to top
//                drawTextDirections(canvas);
//                canvas.restore();
//
//                mFramesLeftToDraw++;
//
//            } else {
//                log("NOT interpolating " + mFramesLeftToDraw);
//                mFramesLeftToDraw = 1;
//                log("NOT interpolating " + mFramesLeftToDraw);
//                mAngleMagnetometerOld = mAngleInterpolated;
//                mAngleMagnetometerTarget = mAngleMagnetometer;
//
//                canvas.rotate(mAngleBase + (-mAngleMagnetometerOld), mCenterX, mCenterY); // -90 makes fist letter pointing to top
//                drawTextDirections(canvas);
//                canvas.restore();
//
//            }

            canvas.rotate(mAngleBase + (-mAngleMagnetometer), mCenterX, mCenterY); // -90 makes fist letter pointing to top
            drawTextDirections(canvas);
            canvas.restore();


        }

        drawMagnetometerArrow(canvas, mAngleMagnetometer);

        if (mIsAngleTargetSet) {
            drawTarget(canvas, mAngleTarget);
        }


    postInvalidateDelayed(16); // 60 fps

    }

    private void drawCircleRingOuter(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius - mCircleRingOuterWidth, mPaintRingOuter);
    }

    private void drawCircleRingInner(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius - mCircleRingInnerPadding, mPaintRingInner);
    }

    private void drawCircleInnerBackground(Canvas canvas) {

        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius - mCircleInnerPadding, mPaintInnerGradient);
    }

    private void drawCircleInnerRose(Canvas canvas) {

        canvas.drawBitmap(mBitmapInnerRose, mCenterX - (mBitmapInnerRose.getWidth() / 2), mCenterY - (mBitmapInnerRose.getHeight() / 2), mPaintInnerRose);

    }

    private void drawCircleInnerPin(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mCircleRingOuterWidth, mPaintRingOuter);
    }

    private void drawTextDirections(Canvas canvas) {

        // Skip fist letter as it should not be rotated its at position "0"
        canvas.drawTextOnPath("N", mPathInnerCircle, 0, mCircleRingOuterWidth, mPaintInnerText);

        for (int i = 1; i < mLetters.length; i++) {
            drawRotatedText(mLetters[i], canvas);

        }

    }

    private void drawRotatedText(String text, Canvas canvas) {
        canvas.rotate(45, mCenterX, mCenterY); //FIXME we should take in to account width of the mLetters and place angle on the correct position
        canvas.drawTextOnPath(text, mPathInnerCircle, 0, mCircleRingOuterWidth, mPaintInnerText);

    }

    PointF pointTargetPosition;


    private void drawTarget(Canvas canvas, float angle) {

        pointTargetPosition = UtilitiesView.getPosition(mCenterX, mCenterY, (mCircleRadius - mCircleInnerPadding), angle);//= UtilitiesView.getPointOnCircle((mCircleRadius - mCircleInnerPadding) - 20, angle, new PointF(mCenterX, mCenterY));

        canvas.save();


        canvas.rotate(-mAngleMagnetometer, mCenterX, mCenterY);
        canvas.drawCircle(pointTargetPosition.x, pointTargetPosition.y, mTargetWidth, mPaintTarget);

        canvas.drawBitmap(mBitmapTarget, pointTargetPosition.x - (mBitmapTarget.getWidth() / 2), pointTargetPosition.y - (mBitmapTarget.getHeight() / 2), mPaintInnerRose);
        canvas.restore();
    }


    private void drawMagnetometerArrow(Canvas canvas, float angle) {

        //FIXME missing rotation
        canvas.drawPath(mPathMagnetomereArrow, mPaintMagnetometerArrow);

    }


    public void setAngleTarget(float mAngleTarget) {

        this.mAngleTarget = mAngleTarget;
        mIsAngleTargetSet = true;
    }

    public void setAngleMagnetometer(float angle) {

        mAngleMagnetometer = angle;
        mIsAngleMagnetometerSet = true;

    }
}

