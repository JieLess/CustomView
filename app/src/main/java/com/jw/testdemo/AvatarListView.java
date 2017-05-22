package com.jw.testdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by jie.wu on 2017/2/10.
 *
 */

public class AvatarListView extends View {
    private Paint mPaint;
    private Paint mBorderPaint;
    private int viewWidth;
    private int viewHeight;
    private ArrayList<Bitmap> bmps;

    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_RIGHT = 1;

    /**
     * 圆的边框颜色
     **/
    private int mBorderColor = Color.WHITE;
    /**
     * 圆的边框宽度
     **/
    private int mBorderWidth;
    /**
     * 对齐方式
     **/
    private int mDirection;

    public AvatarListView(Context context) {
        this(context, null);
    }

    public AvatarListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AvatarListView, defStyleAttr, 0);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.AvatarListView_circleBorderColor:
                    mBorderColor = ta.getColor(index, mBorderColor); //默认为白色
                    break;
                case R.styleable.AvatarListView_circleBorderWidth:
                    mBorderWidth = ta.getDimensionPixelSize(index, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics())); // 默认为2dp
                    break;
                case R.styleable.AvatarListView_direction:
                    mDirection = ta.getInt(index, DIRECTION_LEFT);
                    break;
            }
        }
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    public void setImageBitmaps(ArrayList<Bitmap> bitmaps) {
        if (bitmaps == null)
            throw new IllegalArgumentException("bitmaps can not be Null");
        if (bitmaps.size() > 6)
            throw new IllegalArgumentException("bitmaps size can not be greater than 6");
        this.bmps = bitmaps;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        join(canvas);
    }

    private void join(Canvas canvas) {
        if(bmps == null) return;

        int offset = 0;
        int min = Math.min(viewWidth, viewHeight);
        /*for (int index = 0; index < bmps.size(); index++) {
            canvas.save();
            int offset = index * (min * 2 / 3);
            if(offset>0){
                canvas.translate(offset, 0);
            }
            canvas.drawBitmap(createCircleImage(bmps.get(index), min), 0, 0, null);
            canvas.restore();
        }*/

        int count = bmps.size() - 1;
        for (int index = count; index >= 0; index--) {
            canvas.save();

            switch (mDirection){
                case DIRECTION_LEFT:
                    offset = index * (min * 3 / 4);
                    break;
                case DIRECTION_RIGHT:
                    offset = viewWidth - min - ((count - index) * (min * 3 / 4));
                    break;
                default:
                    offset = index * (min * 3 / 4);
                    break;
            }

            if (offset > 0) {
                canvas.translate(offset, 0);
            }
            canvas.drawBitmap(createCircleImage(bmps.get(index), min), 0, 0, null);
            canvas.restore();
        }
    }

    private Bitmap createCircleImage(Bitmap bitmap, int min) {
        mPaint.reset();
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
        canvas.drawCircle(min / 2, min / 2, min / 2 - mBorderWidth, mBorderPaint);
        return target;
    }
}
