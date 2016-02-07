package com.deadswine.library.view.compass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.v7.widget.ViewUtils;
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


    private int[] gradientColors;
    private float[] gradientStepps;

    private String[] letters;

    private Paint mPaintInnerRose;
    private Paint mPaintRingOuter;
    private Paint mPaintRingInner;
    private Paint mPaintInnerGradient;
    private Paint mPaintInnerText;


    private Bitmap mBitmapInnerRose;

    private Path mPathInner;


    private int circleRadius;
    private int circleRadiusInner;

    private int circleRingOuterWidth;
    private int circleRingOuterPadding;

    private int circleRingInnerWidth;
    private int circleRingInnerPadding;

    private int circleInnerPadding;

    private int scalePadding;

    private int centerX;
    private int centerY;

    private int angleBase;
    private int angleCurrent;

    {

        letters = new String[]{
                "N", "NE", "E", "SE", "S", "SW", "W", "NW"
        };


//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("NE", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);
//
//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("E", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);
//
//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("SE", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);
//
//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("s", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);
//
//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("SW", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);
//
//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("W", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);
//
//        canvas.rotate(45, centerX, centerY);
//        canvas.drawTextOnPath("NW", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);


        gradientColors = new int[]{
                getResources().getColor(R.color.compass_inner_gradient_start),
                getResources().getColor(R.color.compass_inner_gradient_start),
                getResources().getColor(R.color.compass_inner_gradient_end)
        };

        gradientStepps = new float[]{
                0f,
                0.85f,
                1f
        };

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

        mPaintRingOuter = new Paint();
        mPaintRingOuter.setColor(getResources().getColor(R.color.compass_ring_outer));
        mPaintRingOuter.setStyle(Paint.Style.FILL);
        mPaintRingOuter.setAntiAlias(true);

        mPaintRingInner = new Paint();
        mPaintRingInner.setColor(getResources().getColor(R.color.compass_ring_inner));
        mPaintRingInner.setStyle(Paint.Style.FILL);
        mPaintRingInner.setAntiAlias(true);


        mPathInner = new Path();


        circleRingOuterWidth = UtilitiesView.dpToPx(getContext(), 12);
        circleRingOuterPadding = circleRingOuterWidth;

        circleRingInnerWidth = UtilitiesView.dpToPx(getContext(), 4);
        circleRingInnerPadding = circleRingOuterWidth + circleRingOuterPadding;


        circleInnerPadding = circleRingInnerPadding + circleRingInnerWidth;

        scalePadding = circleRingInnerPadding + circleRingInnerWidth + UtilitiesView.dpToPx(getContext(), 8);

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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        circleRadius = getWidth() / 2;

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        angleBase = -90;
        angleCurrent = 0;

        RadialGradient gradient = new RadialGradient(centerX, centerY, circleRadius - circleInnerPadding, gradientColors, gradientStepps, Shader.TileMode.CLAMP);
        //  mBitmapInnerRose = BitmapFactory.decodeResource(getResources(), R.drawable.rose);

        mBitmapInnerRose = UtilitiesView.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rose), (circleRadius), (circleRadius), UtilitiesView.ScalingLogic.FIT);
        // mBitmapInnerRose = UtilitiesView.decodeSampledBitmapFromResource(getResources(), R.drawable.rose,(circleRadius - circleInnerPadding)/4,(circleRadius - circleInnerPadding)/4);

        mPaintInnerGradient.setShader(gradient);

        mPathInner.reset();
        mPathInner.addCircle(centerX, centerY, (circleRadius - circleInnerPadding) - 20, Path.Direction.CW);


    }


    boolean testRotation = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawCircleRingOuter(canvas);
        drawCircleRingInner(canvas);
        drawCircleInnerBackground(canvas);
        drawCircleInnerRose(canvas);
        drawCircleInnerPin(canvas);

        // now we need to add rotation logic// rotating canvas and then resetting should be enough

        canvas.save();

        if (testRotation) {
            if (angleCurrent == 360)
                angleCurrent = 0;


            canvas.rotate(angleBase + angleCurrent, centerX, centerY); // -90 makes fist letter pointing to top
            drawTextDirections(canvas);
            canvas.restore();

            angleCurrent += 1;

            postInvalidateDelayed(16); // 60 fps

        } else {

            canvas.rotate(angleBase + angleCurrent, centerX, centerY); // -90 makes fist letter pointing to top
            drawTextDirections(canvas);
            canvas.restore();
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
    }

    private void drawCircleRingOuter(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, circleRadius - circleRingOuterWidth, mPaintRingOuter);
    }

    private void drawCircleRingInner(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, circleRadius - circleRingInnerPadding, mPaintRingInner);
    }

    private void drawCircleInnerBackground(Canvas canvas) {

        canvas.drawCircle(centerX, centerY, circleRadius - circleInnerPadding, mPaintInnerGradient);
    }

    private void drawCircleInnerRose(Canvas canvas) {

        canvas.drawBitmap(mBitmapInnerRose, centerX - (mBitmapInnerRose.getWidth() / 2), centerY - (mBitmapInnerRose.getHeight() / 2), mPaintInnerRose);

    }


    private void drawCircleInnerPin(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, circleRingOuterWidth, mPaintRingOuter);
    }


    private void drawTextDirections(Canvas canvas) {

        canvas.drawTextOnPath("N", mPathInner, 0, circleRingOuterWidth, mPaintInnerText);

        for (int i = 1; i < letters.length; i++) {
            drawRotatedText(letters[i], canvas);

        }

    }


    private void drawRotatedText(String text, Canvas canvas) {

        canvas.rotate(45, centerX, centerY); //FIXME we should take in to account width of the letters and place angle on the correct position
        canvas.drawTextOnPath(text, mPathInner, 0, circleRingOuterWidth, mPaintInnerText);


    }


}
