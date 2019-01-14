package com.example.boro_.mediscan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ScanningView extends View {

    Context ctx;
    private Rect scanBar = new Rect();
    private Paint paint = new Paint();
    private ValueAnimator valueAnimator = new ValueAnimator();
    private ValueAnimator iconAnimator = new ValueAnimator();
    private int scanBarPosition = 0;
    private int scanBarThickness;
    private int opacity;
    private int duration;
    private int scanBarColor;
    private int orientation;
    private Boolean isReversing = false;
    private static final int SCAN_ORIENTATION_VERTICAL = 0;
    private static final int SCAN_ORIENTATION_HORIZONTAL = 1;

    private ImageView iconView;
    private int touchIconColor;
    private int touchIconAnimationDuration;


    public ScanningView(Context context) {
        super(context);
        ctx = context;
    }

    public ScanningView(Context context, AttributeSet attrs) {

        super(context, attrs);
        ctx = context;
        initiate(attrs);
        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

    }

    public ScanningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        initiate(attrs);
    }

    public ScanningView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ctx = context;
        initiate(attrs);
    }


    private void initiate(AttributeSet attrs){

        if(attrs == null) return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.ScanningView);

        setScanBarThickness(typedArray.getInteger(R.styleable.ScanningView_scanbar_thickness,200));

        setScanBarColor(typedArray.getColor(R.styleable.ScanningView_scanbar_color,Color.RED));

        setOpacity(typedArray.getInteger(R.styleable.ScanningView_scanbar_opacity,100));

        setScanOrientation(typedArray.getInt(R.styleable.ScanningView_animation_orientation,SCAN_ORIENTATION_VERTICAL));

        setAnimationDuration(typedArray.getInteger(R.styleable.ScanningView_scan_animation_duration,800));

        setTouchIconColor(typedArray.getInt(R.styleable.ScanningView_touchIconColor, Color.RED));

        setTouchIconAnimationDuration(typedArray.getInt(R.styleable.ScanningView_touchIconAnimationDuration, 2000));

        typedArray.recycle();
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(){

        @Override
        public void onGlobalLayout() {

            setupAnimation();
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setupIcon();
    }

    ValueAnimator.AnimatorUpdateListener animatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            scanBarPosition = (int)animation.getAnimatedValue();

            positionScanBar();

            invalidate();
        }
    };

    ValueAnimator.AnimatorUpdateListener iconAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            iconView.setImageAlpha((int)animation.getAnimatedValue());

        }
    };

    private void setupIcon(){

        iconView = ((View) getParent()).findViewById(R.id.touchInfo);

        if(iconView == null) return;

        iconView.setColorFilter(Color.CYAN);

        iconAnimator.setDuration(2000);
        iconAnimator.setRepeatCount(ValueAnimator.INFINITE);
        iconAnimator.setRepeatMode(ValueAnimator.REVERSE);
        iconAnimator.setIntValues(0,255);
        iconAnimator.addUpdateListener(iconAnimatorListener);
        iconAnimator.start();

    }

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
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
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

    public void setTouchIconColor(int color){


        this.touchIconColor = color;
    }

    public void setTouchIconAnimationDuration(int duration){


        this.touchIconAnimationDuration = duration;
    }


/*    public void startScanAnimation(final onScanEndCallback animationCallback){

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationCallback.onScanComplete();
            }
        });

        valueAnimator.start();
    }*/

    public void startScanAnimation(){

        if(!valueAnimator.isRunning()){
            iconAnimator.end();
            valueAnimator.start();
            iconView.setVisibility(GONE);
        }

    }

    public void endAnimation(){

        if(valueAnimator.isRunning()){
            valueAnimator.end();
            iconView.setVisibility(VISIBLE);
            iconAnimator.start();

        }


    }

    public interface onScanEndCallback {

        void onScanComplete();
    }

}
