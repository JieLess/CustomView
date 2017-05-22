package com.jw.testdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by jie.wu on 2017/2/10.
 */

public class RoundImageView extends ImageView {

    private final int DEFAULT_RADIUS = 10;
    private int width, height;

    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint, mPaint;
    private RectF mRoundRect, mRect;
    private int x_radius;
    private int y_radius;
    private int borderColor;
    private int borderWidth;
    private boolean focusable, isFocus = false;

    public RoundImageView(Context context) {
        super(context);
        initObjectAttribute();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
        x_radius = a.getDimensionPixelSize(R.styleable.RoundImageView_x_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));
        y_radius = a.getDimensionPixelSize(R.styleable.RoundImageView_y_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));
        borderWidth = a.getDimensionPixelSize(R.styleable.RoundImageView_borderWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        borderColor = a.getColor(R.styleable.RoundImageView_borderColor, 0xFF4498EE);
        focusable = a.getBoolean(R.styleable.RoundImageView_focusable, false);
        a.recycle();

        initObjectAttribute();
    }

    private void initObjectAttribute() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setStyle(Paint.Style.FILL);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mRoundRect = new RectF();
        mRect = new RectF();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        createBitmapShader();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
//		createBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        createBitmapShader();
    }

    private void createBitmapShader() {
        mBitmapShader = null;
        Drawable mDrawable = getDrawable();
        if (null == mDrawable) {
            return;
        }

        if (mDrawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) mDrawable;
            Bitmap bitmap = bd.getBitmap();
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        } else //if Drawable instanceof NinePathDrawable ，the code below is bad , because a view reference two bitmap ( one in NinePath , other is here)
        {
            int w = mDrawable.getIntrinsicWidth();
            int h = mDrawable.getIntrinsicHeight();

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mDrawable.setBounds(0, 0, w, h);
            mDrawable.draw(canvas);
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(focusable && isFocus!=gainFocus){
            isFocus = gainFocus;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        if (null == mDrawable || null == mBitmapShader) {
            return;
        }
        Matrix mDrawMatrix = getImageMatrix();
        if (null == mDrawMatrix) {
            mDrawMatrix = new Matrix();
        }
        mBitmapShader.setLocalMatrix(mDrawMatrix);
        mBitmapPaint.setShader(mBitmapShader);

        if(isFocus && focusable){
            mRect.set(0, 0, width, height);
            canvas.drawRoundRect(mRect, x_radius, x_radius, mPaint);
            mRoundRect.set(borderWidth, borderWidth, width-borderWidth, height-borderWidth);
        }else {
            mRoundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }
        canvas.drawRoundRect(mRoundRect, x_radius, y_radius, mBitmapPaint);
    }
}