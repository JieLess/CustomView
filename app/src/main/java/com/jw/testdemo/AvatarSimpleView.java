package com.jw.testdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by jie.wu on 2017/2/22.
 *  avatar 头像控件
 */

public class AvatarSimpleView extends View {

    private Paint mPaint, mBorderPaint;
    private int viewWidth;
    private int viewHeight;
    /**
     * 圆的边框颜色
     **/
    private int mBorderColor = Color.WHITE;
    private int mFocusColor = Color.parseColor("#FF4498EE");
    /**
     * 圆的边框宽度
     **/
    private int mBorderWidth;
    private Bitmap mAvatar;
    /**
     * 是否为选中状态
     */
    private boolean isCheck = false;
    /**
     * 取View宽高的最小值
     */
    private int min;
    /**
     * 动画是否结束
     */
    private boolean isEnd = false;

    public AvatarSimpleView(Context context) {
        this(context, null);
    }

    public AvatarSimpleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarSimpleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AvatarView, defStyleAttr, 0);
        mBorderColor = a.getColor(R.styleable.AvatarView_circleBorderColor, mBorderColor); //默认为白色
        mFocusColor = a.getColor(R.styleable.AvatarSimpleView_focusBorderColor, mFocusColor);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.AvatarView_circleBorderWidth, dp2px(2f)); // 默认为2dp
        mAvatar = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.AvatarSimpleView_avatar, 0));
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 处理在RelativeLayout布局中宽度不能自适应的问题
        if(viewWidth>0) {
            min = Math.min(viewWidth, min);
        }
        viewWidth = viewHeight = min;
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircleImage(canvas);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        setCheck(!isCheck);
    }

    private void drawCircleImage(Canvas canvas){
        Bitmap bitmap = createCircleImage(mAvatar);
        if(bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    private Bitmap createCircleImage(Bitmap bitmap) {
        if(isCheck){
            mBorderPaint.setColor(mFocusColor);
        }else {
            mBorderPaint.setColor(mBorderColor);
        }

        mPaint.reset();
        mPaint.setAntiAlias(true);
        //创建与原图同样大小的画布
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        //长度如果不一致，按小的值进行压缩
        if(bitmap != null) {
            //绘制圆形
            canvas.drawCircle(min / 2, min / 2, min / 2 - mBorderWidth, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Bitmap source = Bitmap.createScaledBitmap(bitmap, min, min, false);
            //绘制图片
            canvas.drawBitmap(source, 0, 0, mPaint);
        }
        //绘制圆的边框
        canvas.drawCircle(min / 2, min / 2, min / 2 - mBorderWidth, mBorderPaint);
        return target;
    }

    public void recycle() {
        if (mAvatar != null && mAvatar.isRecycled()) {
            mAvatar.recycle();
        }
    }

    public void setAvatarBitmap(Bitmap bitmap) {
        if (bitmap == null) return;
        this.mAvatar = bitmap;
        invalidate();
    }

    public boolean isCheck() {
        return this.isCheck;
    }

    public void setCheck(boolean isCheck){
        if(this.isCheck != isCheck){
            this.isCheck = isCheck;
        }
    }

    private int dp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spVal, getResources().getDisplayMetrics());
    }
}
