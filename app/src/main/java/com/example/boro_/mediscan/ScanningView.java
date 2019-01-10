package com.example.boro_.mediscan;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

public class ScanningView extends View {

    private Rect scanBar = new Rect();
    private Paint paint = new Paint();
    private ValueAnimator valueAnimator = new ValueAnimator();
    private int scanBarPosition = 0;
    private int scanBarThickness;
    private int opacity;
    private int duration;
    private int scanBarColor;
    private int orientation;
    private Boolean isReversing = false;
    private static final int SCAN_ORIENTATION_VERTICAL = 0;
    private static final int SCAN_ORIENTATION_HORIZONTAL = 1;

    //public enum ScanOrientation {SCAN_ORIENTATION_VERTICAL, SCAN_ORIENTATION_HORIZONTAL};


    public ScanningView(Context context) {
        super(context);

    }

    public ScanningView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initiate(attrs);
        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

    }

    public ScanningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initiate(attrs);
    }

    public ScanningView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initiate(attrs);
    }


    private void initiate(AttributeSet attrs){

        if(attrs == null) return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.ScanningView);

        setScanBarThickness(typedArray.getInteger(R.styleable.ScanningView_scanbar_thickness,200));

        setScanBarColor(typedArray.getColor(R.styleable.ScanningView_scanbar_color,Color.RED));

        setOpacity(typedArray.getInteger(R.styleable.ScanningView_scanbar_opacity,100));

        setScanOrientation(typedArray.getInt(R.styleable.ScanningView_animation_orientation,SCAN_ORIENTATION_VERTICAL));

        setAnimationDuration(typedArray.getInteger(R.styleable.ScanningView_animation_speed,800));

    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(){

        @Override
        public void onGlobalLayout() {

            setupAnimation();
        }
    };

    ValueAnimator.AnimatorUpdateListener animatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            scanBarPosition = (int)animation.getAnimatedValue();

            positionScanBar();

            invalidate();
        }
    };

    private void setupAnimation(){

        if(orientation == SCAN_ORIENTATION_VERTICAL){
            valueAnimator.setIntValues(0,getHeight() + scanBarThickness + 5);

            scanBar.left = 0;
            scanBar.right = getWidth();
        }
        else {

            valueAnimator.setIntValues(0,getWidth() + scanBarThickness + 5);

            scanBar.top = 0;
            scanBar.bottom = getHeight();

        }

        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(animatorListener);

        paint.setAlpha(opacity);

    }

    private void positionScanBar(){

        if(orientation == SCAN_ORIENTATION_VERTICAL){

            scanBar.top = scanBarPosition - scanBarThickness;
            scanBar.bottom = scanBarPosition;

            if(scanBarPosition >= getHeight() + scanBarThickness) isReversing = true;

            if(!isReversing){
                paint.setShader(new LinearGradient(0, scanBar.top, 0, scanBar.bottom, Color.TRANSPARENT, scanBarColor,Shader.TileMode.CLAMP));
            }
            else {

                paint.setShader(new LinearGradient(0, scanBar.top, 0, scanBar.bottom, scanBarColor, Color.TRANSPARENT,Shader.TileMode.CLAMP));
                if(scanBarPosition <= 0) isReversing = false;
            }

        }
        else{

            scanBar.left = scanBarPosition - scanBarThickness;
            scanBar.right = scanBarPosition;

            if(scanBarPosition >= getWidth() + scanBarThickness) isReversing = true;

            if(!isReversing){
                paint.setShader(new LinearGradient(scanBar.left, 0, scanBar.right, 0, Color.TRANSPARENT, scanBarColor,Shader.TileMode.CLAMP));

            }
            else {
                paint.setShader(new LinearGradient(scanBar.left, 0, scanBar.right, 0, scanBarColor, Color.TRANSPARENT,Shader.TileMode.CLAMP));
                if(scanBarPosition <= 0){

                    isReversing = false;
                }
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(scanBar,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setOpacity(int opacity){
        if(opacity > 255){
            this.opacity = 255;
        }
        else if(opacity < 0){
            this.opacity = 0;
        }
        else {
            this.opacity = opacity;
        }
    }

    public void setScanOrientation(int orientation){

        if(orientation > 1 || orientation < 0) orientation = 0;

        this.orientation = orientation;
    }

    public void setAnimationDuration(int duration){

        if(duration > 8000){
            this.duration = 8000;
        }
        else if(duration <= 0){
             this.duration = 5;
        }
        else {
            this.duration = duration;
        }

    }

    public void setScanBarThickness(int thickness){
        if(thickness > 1000){
            this.scanBarThickness = 1000;
        }
        else if(thickness <= 0){
            scanBarThickness = 1;
        }
        else{
            this.scanBarThickness = thickness;
        }

    }

    public void setScanBarColor(int color){

        this.scanBarColor = color;
    }

    public void startScanAnimation(){

        valueAnimator.start();
    }


}
