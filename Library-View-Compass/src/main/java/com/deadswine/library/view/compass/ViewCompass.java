package com.deadswine.library.view.compass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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


    private Paint mPaintInnerRose;
    private Paint mPaintRingOuter;
    private Paint mPaintRingInner;

    private Paint mPaintInnerGradient;

    private Bitmap mBitmapInnerRose;


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

    {


        gradientColors = new int[]{
                getResources().getColor(R.color.compass_inner_gradient_start),
                getResources().getColor(R.color.compass_inner_gradient_start),
                getResources().getColor(R.color.compass_inner_gradient_end)
        };

        gradientStepps = new float[]{
                0f,
                0.7f,
                1f
        };

        mPaintInnerGradient = new Paint();
        mPaintInnerGradient.setDither(true);
        mPaintInnerGradient.setAntiAlias(true);

        mPaintInnerRose = new Paint();
       // mPaintInnerRose.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
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


//        paintElevation = new Paint();
//        paintElevation.setColor(getResources().getColor(R.color.app_green));
//        paintElevation.setStyle(Paint.Style.FILL);
//        paintElevation.setStrokeWidth(dpToPx(1));
//        paintElevation.setAntiAlias(true);
//
//
//        paintSpeed = new Paint();
//        paintSpeed.setColor(getResources().getColor(R.color.app_blue));
//        paintSpeed.setStyle(Paint.Style.STROKE);
//        paintSpeed.setStrokeWidth(dpToPx(1));
//        paintSpeed.setAntiAlias(true);




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

        RadialGradient gradient = new RadialGradient(centerX, centerY, circleRadius - circleInnerPadding, gradientColors, gradientStepps,Shader.TileMode.CLAMP);
      //  mBitmapInnerRose = BitmapFactory.decodeResource(getResources(), R.drawable.rose);

        mBitmapInnerRose =  UtilitiesView.  createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rose),(circleRadius ),(circleRadius), UtilitiesView.ScalingLogic.FIT);
       // mBitmapInnerRose = UtilitiesView.decodeSampledBitmapFromResource(getResources(), R.drawable.rose,(circleRadius - circleInnerPadding)/4,(circleRadius - circleInnerPadding)/4);

        mPaintInnerGradient.setShader(gradient);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawCircleRingOuter(canvas);
        drawCircleRingInner(canvas);
        drawCircleInnerBackground(canvas);
        drawCircleInnerRose(canvas);
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

      canvas.drawBitmap(mBitmapInnerRose, centerX-(mBitmapInnerRose.getWidth()/2), centerY-(mBitmapInnerRose.getHeight()/2), mPaintInnerRose);

    }

}
