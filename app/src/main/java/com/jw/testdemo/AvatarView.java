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
 * Created by jie.wu on 2017/2/13.
 *  avatar 头像控件
 */

public class AvatarView extends View {

    private Paint mPaint, mBorderPaint, mTextPaint, mRoundPaint;
    private int viewWidth;
    private int viewHeight;
    private String text = "NULL";
    private float textSize, textOffset;
    /**
     * 圆的边框颜色
     **/
    private int mBorderColor = Color.WHITE;
    /**
     * 文本颜色
     */
    private int mTextColor = Color.BLACK;
    private int mTextSize;
    /**
     * 圆的边框宽度
     **/
    private int mBorderWidth;
    private Bitmap mOnlineSrc;
    private Bitmap mOfflineSrc;
    private Bitmap mAvatar;
    /**
     * 是否在线
     */
    private boolean isOnline = false;
    /**
     * 是否为选中状态
     */
    private boolean isCheck = false;
    private float currentValue = 0;
    /**
     * 取View宽高的最小值
     */
    private int min;
    /**
     * 动画是否结束
     */
    private boolean isEnd = false;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AvatarView, defStyleAttr, 0);
        mBorderColor = a.getColor(R.styleable.AvatarView_circleBorderColor, mBorderColor); //默认为白色
        mBorderWidth = a.getDimensionPixelSize(R.styleable.AvatarView_circleBorderWidth, dp2px(2f)); // 默认为2dp
        mTextColor = a.getColor(R.styleable.AvatarView_textColor, mTextColor); //默认为黑色
        mTextSize = a.getDimensionPixelSize(R.styleable.AvatarView_textSize, sp2px(14f)); // 默认为14sp
        mOnlineSrc = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.AvatarView_onlineSrc, R.drawable.online));
        mOfflineSrc = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.AvatarView_offlineSrc, R.drawable.offline));
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRoundPaint.setColor(mBorderColor);
        mRoundPaint.setStrokeWidth(mBorderWidth);
        mRoundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*// 设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            // match_parent , accurate
            viewWidth = specSize;
        } else {
            // 由图片决定的宽
            int imgSize = getPaddingLeft() + getPaddingRight() + mAvatar.getWidth();
            if (specMode == MeasureSpec.AT_MOST) {
                //wrap_content
                viewWidth = Math.min(imgSize, specSize);
            } else {
                viewWidth = imgSize;
            }
        }

        // 设置高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            // match_parent , accurate
            viewHeight = specSize;
        } else {
            // 由图片决定的宽
            int imgSize = getPaddingTop() + getPaddingBottom() + mAvatar.getHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                //wrap_content
                viewHeight = Math.min(imgSize, specSize);
            } else {
                viewHeight = imgSize;
            }
        }

        min = Math.min(viewWidth, viewHeight);
        //setMeasuredDimension(min, min);*/


        min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 处理在RelativeLayout布局中宽度不能自适应的问题
        if(viewWidth>0) {
            min = Math.min(viewWidth, min);
        }
        viewWidth = viewHeight = min;

        if(isCheck){
            setMeasuredDimension((int)currentValue, min);
        }else {
            if(!isEnd && currentValue > 0){
                setMeasuredDimension((int)currentValue, min);
            }else {
                setMeasuredDimension(min, min);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCheckState(canvas);
        drawText(canvas);
        drawCircleImage(canvas);
        drawOnlineSrc(canvas);
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
        if(bitmap == null) return null;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        //长度如果不一致，按小的值进行压缩
        Bitmap source = Bitmap.createScaledBitmap(bitmap, min, min, false);
        //创建与原图同样大小的画布
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        //绘制圆形
        canvas.drawCircle(min / 2, min / 2, min / 2 - mBorderWidth, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制图片
        canvas.drawBitmap(source, 0, 0, mPaint);
        //绘制圆的边框
        canvas.drawCircle(min / 2, min / 2, min / 2 - mBorderWidth, mBorderPaint);
        return target;
    }

    private void drawOnlineSrc(Canvas canvas) {
        if (mOnlineSrc == null || mOfflineSrc == null) return;

        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        float x = (float) (min / 2 + (min / 2 - mBorderWidth) * Math.cos(45 * Math.PI / 180) - mOnlineSrc.getWidth() / 4);
        float y = (float) (min / 2 + (min / 2 - mBorderWidth) * Math.sin(45 * Math.PI / 180) - mOnlineSrc.getHeight() / 4);
        matrix.postTranslate(x, y);
        if (isOnline) {
            canvas.drawBitmap(mOnlineSrc, matrix, mPaint);
        } else {
            canvas.drawBitmap(mOfflineSrc, matrix, mPaint);
        }
        canvas.restore();
    }

    private void drawCheckState(Canvas canvas) {
        if(isEnd || currentValue == 0) return;
        canvas.drawRoundRect(new RectF(0, 0, currentValue, viewHeight), min / 2, min / 2, mRoundPaint);
    }

    private void drawText(Canvas canvas){
        if(isEnd || currentValue == 0) return;
        // 计算Baseline绘制的X坐标
        int baseX = (int) (min + textOffset / 2 - textSize / 2 - mBorderWidth);
        // 计算Baseline绘制的Y坐标
        int baseY = (int) ((min / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        canvas.drawText(this.text, baseX, baseY, mTextPaint);
    }

    public void recycle() {
        if (mAvatar != null && mAvatar.isRecycled()) {
            mAvatar.recycle();
        }

        if (mOnlineSrc != null && mOnlineSrc.isRecycled()) {
            mOnlineSrc.recycle();
        }

        if (mOfflineSrc != null && mOfflineSrc.isRecycled()) {
            mOfflineSrc.recycle();
        }
    }

    private int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }

    private int dp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spVal, getResources().getDisplayMetrics());
    }

    public void setAvatarBitmap(Bitmap bitmap) {
        if (bitmap == null) return;
        this.mAvatar = bitmap;
        invalidate();
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public void setOnline(boolean isOnline) {
        if (this.isOnline != isOnline) {
            this.isOnline = isOnline;
            invalidate();
        }
    }

    public void setText(String text) {
        this.text = text;

    }

    public String getText(){
        return this.text;
    }

    public boolean isCheck() {
        return this.isCheck;
    }

    public void setCheck(boolean isCheck){
        if(this.isCheck != isCheck){
            this.isCheck = isCheck;
            if(isCheck) {
                textSize = mTextPaint.measureText(this.text);
                textOffset = textSize + dp2px(15);
                animationStart();
            } else {
                animationEnd();
            }
        }
    }

    private void animationStart(){
        doAnimation(min, min + textOffset);
    }

    private void animationEnd(){
        doAnimation(min + textOffset, min);
    }

    private void doAnimation(float start, float end){
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentValue = (float) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });
        animator.addListener(animatorListener);
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            isEnd = false;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if(!isCheck) {
                isEnd = true;
                invalidate();
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };
}
