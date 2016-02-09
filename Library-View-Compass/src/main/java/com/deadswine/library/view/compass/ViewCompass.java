package com.deadswine.library.view.compass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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

    private int[] mGradientColors2;
    private float[] mGradientSteps2;

    private int[] mGradientColors3;
    private float[] mGradientSteps3;

    private String[] mLetters;

    private Paint mPaintInnerRose;
    private Paint mPaintRingOuter;
    private Paint mPaintRingInner;
    private Paint mPaintInnerGradient;
    private Paint mPaintInnerText;
    private Paint mPaintMagnetometerArrow;
    private Paint mPaintMagnetometerArrowMirror;
    private Paint mPaintScale;
    private Paint mPaintPin;

    private Paint mPaintTarget;
    private Bitmap mBitmapInnerRose;
    private Bitmap mBitmapTarget;

    private Path mPathInnerCircle;
    private Path mPathScale;
    private Path mPathScaleLarge;

    private Path mPathCompassArrow;
    private Path mPathCompassArrowMirror;


    private int mWidthArrowWidest;
    private int mWidthArrowMiddle;
    private int mWidthArrowNarrowes;


    private int mCircleRadius;
    private int mCircleRadiusInner;

    private int mCircleRingOuterWidth;
    private int mCircleRingOuterPadding;

    private int mCircleRingInnerWidth;
    private int mCircleRingInnerPadding;
    private int mCircleInnerPadding;
    private int mCircleInnerRadius;
    private int mTargetWidth;

    private int mScalePadding;

    private int mCenterX;
    private int mCenterY;

    int size5;
    int size10;
    int size15;
    int sizeFar;

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
                0.80f,
                1f
        };


        mGradientColors2 = new int[]{
                getResources().getColor(R.color.compass_inner_gradient_start2),
                getResources().getColor(R.color.compass_inner_gradient_end2),
                getResources().getColor(R.color.compass_inner_gradient_tip2)
        };

        mGradientSteps2 = new float[]{
                0f,
                0.99f,
                1f
        };


        mGradientColors3 = new int[]{
                getResources().getColor(R.color.compass_inner_gradient_start2),
                getResources().getColor(R.color.compass_inner_gradient_end2),
        };

        mGradientSteps3 = new float[]{
                0f,
                0.45f
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

        mPaintMagnetometerArrowMirror = new Paint();
        mPaintMagnetometerArrowMirror.setStyle(Paint.Style.FILL);
        mPaintMagnetometerArrowMirror.setAntiAlias(true);


        mPaintRingOuter = new Paint();
        mPaintRingOuter.setColor(getResources().getColor(R.color.compass_ring_outer));
        mPaintRingOuter.setStyle(Paint.Style.FILL);
        mPaintRingOuter.setAntiAlias(true);

        mPaintRingInner = new Paint();
        mPaintRingInner.setColor(getResources().getColor(R.color.compass_ring_inner));
        mPaintRingInner.setStyle(Paint.Style.FILL);
        mPaintRingInner.setAntiAlias(true);

        mPaintPin = new Paint();
        mPaintPin.setColor(getResources().getColor(R.color.compass_pin));
        mPaintPin.setStyle(Paint.Style.FILL);
        mPaintPin.setStrokeWidth(UtilitiesView.dpFromPx(getContext(), 3));
        mPaintPin.setAntiAlias(true);


        mPaintScale = new Paint();
        mPaintScale.setColor(getResources().getColor(android.R.color.holo_red_dark));
        mPaintScale.setStyle(Paint.Style.FILL);
        mPaintScale.setStrokeWidth(UtilitiesView.dpFromPx(getContext(), 3));
        mPaintScale.setAntiAlias(true);


        mPathInnerCircle = new Path();
        mPathCompassArrow = new Path();
        mPathCompassArrowMirror = new Path();
        mPathScale = new Path();
        mPathScaleLarge = new Path();

        mCircleRingOuterWidth = UtilitiesView.dpToPx(getContext(), 12);
        mCircleRingOuterPadding = mCircleRingOuterWidth;

        mCircleRingInnerWidth = UtilitiesView.dpToPx(getContext(), 4);
        mCircleRingInnerPadding = mCircleRingOuterWidth + mCircleRingOuterPadding;

        mCircleInnerPadding = mCircleRingInnerPadding + mCircleRingInnerWidth;

        mScalePadding = mCircleRingInnerPadding + mCircleRingInnerWidth + UtilitiesView.dpToPx(getContext(), 8);


        mWidthArrowWidest = mCircleRingOuterWidth;
        mWidthArrowMiddle = mCircleRingOuterWidth / 2 + mCircleRingOuterWidth / 4;
        mWidthArrowNarrowes = mCircleRingOuterWidth / 4;

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

        size5 = UtilitiesView.dpToPx(getContext(), 5);
        size10 = UtilitiesView.dpToPx(getContext(), 10);
        size15 = UtilitiesView.dpToPx(getContext(), 15);
        sizeFar = (mCircleRadius - mCircleInnerPadding) - (size10 * 4);

        mAngleBase = -90;
        mAngleCurrent = 0;
        mAngleTarget = 0;
        mAngleMagnetometer = 0;

        RadialGradient gradient = new RadialGradient(mCenterX, mCenterY, mCircleRadius - mCircleInnerPadding, mGradientColors, mGradientSteps, Shader.TileMode.CLAMP);
        RadialGradient gradient2 = new RadialGradient(mCenterX, mCenterY, mCenterY - sizeFar, mGradientColors2, mGradientSteps2, Shader.TileMode.CLAMP);
        RadialGradient gradient3 = new RadialGradient(mCenterX, mCenterY, mCenterY - sizeFar, mGradientColors3, mGradientSteps3, Shader.TileMode.CLAMP);
        mPaintInnerGradient.setShader(gradient);
        mPaintMagnetometerArrow.setShader(gradient2);
        mPaintMagnetometerArrowMirror.setShader(gradient3);

        mBitmapInnerRose = UtilitiesView.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rose), (mCircleRadius), (mCircleRadius), UtilitiesView.ScalingLogic.FIT);

        mPathInnerCircle.reset();
        mPathInnerCircle.addCircle(mCenterX, mCenterY, (mCircleRadius - mCircleInnerPadding) - (size10 * 2), Path.Direction.CW);

        mPathCompassArrow.reset();
        mPathCompassArrow.moveTo(mCenterX - mWidthArrowWidest * 2, mCenterY);
        mPathCompassArrow.lineTo(mCenterX - mWidthArrowMiddle, mCenterY - size10);
        mPathCompassArrow.lineTo(mCenterX - mWidthArrowNarrowes, mCenterY - sizeFar);
        mPathCompassArrow.lineTo(mCenterX + mWidthArrowNarrowes, mCenterY - sizeFar);
        mPathCompassArrow.lineTo(mCenterX + mWidthArrowMiddle, mCenterY - size10);
        mPathCompassArrow.lineTo(mCenterX + mWidthArrowWidest * 2, mCenterY);
        mPathCompassArrow.close();

        mPathCompassArrowMirror.reset();
        mPathCompassArrowMirror.moveTo(mCenterX - mWidthArrowWidest * 2, mCenterY);
        mPathCompassArrowMirror.lineTo(mCenterX - mWidthArrowMiddle, mCenterY + size10);
        mPathCompassArrowMirror.lineTo(mCenterX - mWidthArrowNarrowes, mCenterY + sizeFar);
        mPathCompassArrowMirror.lineTo(mCenterX + mWidthArrowNarrowes, mCenterY + sizeFar);
        mPathCompassArrowMirror.lineTo(mCenterX + mWidthArrowMiddle, mCenterY + size10);
        mPathCompassArrowMirror.lineTo(mCenterX + mWidthArrowWidest * 2, mCenterY);
        mPathCompassArrowMirror.close();


        mPathScaleLarge.reset();
        mPathScaleLarge.moveTo(mCenterX, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 2)));
        mPathScaleLarge.lineTo(mCenterX - 2, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 2)));
        mPathScaleLarge.lineTo(mCenterX - 2, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 4)));
        mPathScaleLarge.lineTo(mCenterX, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 4)));
        mPathScaleLarge.close();


        mPathScale.reset();
        mPathScale.moveTo(mCenterX, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 2)));
        mPathScale.lineTo(mCenterX - 2, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 2)) + size5);
        mPathScale.lineTo(mCenterX - 2, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 4)) + size5);
        mPathScale.lineTo(mCenterX, mCenterY - ((mCircleRadius - mCircleInnerPadding) - (size5 * 4)));
        mPathScale.close();


//        mPathScaleLarge.reset();
//
//        mPathScaleLarge.moveTo(mCenterX, mCenterY - size10);
//        mPathScaleLarge.lineTo(mCenterX , mCenterY - sizeFar);
//        mPathScaleLarge.lineTo(mCenterX + size5, mCenterY - sizeFar);
//        mPathScaleLarge.lineTo(mCenterX + size5, mCenterY - size10);
//
//        mPathScaleLarge.close();

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
            drawTextDirections(canvas);
        }

        drawMagnetometerArrow(canvas);

        drawCircleInnerPin(canvas);

        if (mIsAngleTargetSet) {
            drawTarget(canvas, mAngleTarget);
        }


        drawScale(canvas);

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
        canvas.drawCircle(mCenterX, mCenterY, mCircleRingOuterWidth, mPaintPin);
    }

    private void drawTextDirections(Canvas canvas) {
        canvas.save();
        canvas.rotate(mAngleBase, mCenterX, mCenterY);

        // Skip fist letter as it should not be rotated its at position "0"
        canvas.drawTextOnPath("N", mPathInnerCircle, 0, mCircleRingOuterWidth, mPaintInnerText);

        for (int i = 1; i < mLetters.length; i++) {
            drawRotatedText(mLetters[i], canvas);

        }
        canvas.restore();
    }

    private void drawRotatedText(String text, Canvas canvas) {
        canvas.rotate(45, mCenterX, mCenterY); //FIXME we should take in to account width of the mLetters and place angle on the correct position
        canvas.drawTextOnPath(text, mPathInnerCircle, 0, mCircleRingOuterWidth, mPaintInnerText);

    }

    private void drawScale(Canvas canvas) {

        int scalesToDraw = 6;
        int scalesIterations = 8;
        canvas.save();

        canvas.rotate(mAngleBase, mCenterX, mCenterY);

        for (int i = 0; i < scalesIterations; i++) {
            canvas.rotate(8f, mCenterX, mCenterY);
            for (int j = 0; j < scalesToDraw; j++) {
                canvas.rotate(2.8f, mCenterX, mCenterY);
                canvas.drawPath(mPathScaleLarge, mPaintScale);
                canvas.rotate(2.8f, mCenterX, mCenterY);
                canvas.drawPath(mPathScale, mPaintScale);

            }
            //      canvas.rotate(8f, mCenterX, mCenterY);


            canvas.rotate(16f, mCenterX, mCenterY);

            for (int j = 0; j < scalesToDraw; j++) {
                canvas.rotate(2.7f, mCenterX, mCenterY);
                canvas.drawPath(mPathScaleLarge, mPaintScale);
                canvas.rotate(2.7f, mCenterX, mCenterY);
                canvas.drawPath(mPathScale, mPaintScale);

            }
        }


        canvas.restore();

    }


    PointF pointTargetPosition;

    private void drawTarget(Canvas canvas, float angle) {

        pointTargetPosition = UtilitiesView.getPosition(mCenterX, mCenterY, (mCircleRadius - mCircleInnerPadding) - (size10 * 2), angle);//= UtilitiesView.getPointOnCircle((mCircleRadius - mCircleInnerPadding) - 20, angle, new PointF(mCenterX, mCenterY));

        canvas.save();

        canvas.rotate(-mAngleMagnetometer, mCenterX, mCenterY);
        canvas.rotate(100, pointTargetPosition.x, pointTargetPosition.y); // rotate target arrow to point outside of compass

        canvas.drawCircle(pointTargetPosition.x, pointTargetPosition.y, mTargetWidth, mPaintTarget);

        canvas.drawBitmap(mBitmapTarget, pointTargetPosition.x - (mBitmapTarget.getWidth() / 2), pointTargetPosition.y - (mBitmapTarget.getHeight() / 2), mPaintInnerRose);
        canvas.restore();
    }

    private void drawMagnetometerArrow(Canvas canvas) {
        canvas.save();
        canvas.rotate(-mAngleMagnetometer, mCenterX, mCenterY);
        canvas.drawPath(mPathCompassArrow, mPaintMagnetometerArrow);
        canvas.drawPath(mPathCompassArrowMirror, mPaintMagnetometerArrowMirror);

        canvas.restore();
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

