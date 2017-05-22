package com.jw.testdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by jie.wu on 2017/2/15.
 * 渐变进度条
 */

public class GradientProgressBar extends View {
    private final static int PADDING = 4;
    private int maxValue;
    private int progress;
    private Paint mPaint, mProgressPaint;
    private int bgColor;
    private int progressColor;
    private int gradientColor;
    private int mWidth, mHeight;
    private float currentValue;
    private float previousValue;

    public GradientProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public GradientProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GradientProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GradientProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientProgressBar);
        maxValue = a.getInteger(R.styleable.GradientProgressBar_max, 100);
        progress = a.getInteger(R.styleable.GradientProgressBar_progress, 50);
        bgColor = a.getColor(R.styleable.GradientProgressBar_bgColor, Color.GRAY);
        progressColor = a.getColor(R.styleable.GradientProgressBar_progressColor, 0xFFFF6600);
        gradientColor = a.getColor(R.styleable.GradientProgressBar_gradientColor, 0xFF99CC33);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawProgress(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setStrokeWidth(mHeight);
        canvas.drawLine(mHeight / 2 + PADDING, mHeight / 2, mWidth - mHeight / 2 - PADDING, mHeight / 2, mPaint);
    }

    private void drawProgress(Canvas canvas) {
        if (currentValue == 0 && progress > 0) {
            previousValue = getProgressWidth();
            drawProgressBar(canvas, previousValue);
        } else {
            drawProgressBar(canvas, currentValue);
        }
    }

    private void drawProgressBar(Canvas canvas, float value) {
        LinearGradient shader = new LinearGradient(0, 0, value, 0, new int[]{progressColor, gradientColor}, null, Shader.TileMode.CLAMP);
        mProgressPaint.setShader(shader);
        mProgressPaint.setStrokeWidth(mHeight);
        canvas.drawLine(mHeight / 2 + PADDING, mHeight / 2, value, mHeight / 2, mProgressPaint);
    }

    private void startAnimation() {
        doAnimation(previousValue > 0 ? previousValue : 0, getProgressWidth());
    }

    private void doAnimation(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                previousValue = currentValue;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(600);
        animator.start();
    }

    private float getProgressWidth() {
        float percent = 0;
        if (maxValue != 0) {
            percent = progress * 1.0f / maxValue;
        }
        return (mWidth - mHeight / 2 - PADDING) * percent;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress > maxValue) {
            this.progress = maxValue;
        } else if (progress < 0) {
            this.progress = 0;
        } else {
            this.progress = progress;
        }
        startAnimation();
    }

    public int getPercentage() {
        if (maxValue == 0 || progress == 0) {
            return 0;
        }
        return (int) (progress * 100.0 / maxValue);
    }
}
