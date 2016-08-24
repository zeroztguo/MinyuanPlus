package com.csmy.minyuanplus.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.csmy.minyuanplus.R;

/**
 * 自定义View,实现圆角，圆形等效果
 * Created by Zero on 16/8/18.
 */
public class CustomImageView extends View {
    /**
     * 图片类型
     */
    private int type;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;
    /**
     * 图片
     */
    private Bitmap mSrc;
    /**
     * 圆角的大小
     */
    private int mRadius;
    /**
     * 控件的宽度
     */
    private int mWidth;
    /**
     * 控件的高度
     */
    private int mHeight;


    /**
     * 是否画对勾
     */
    private boolean mIsDrawRight = false;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 初始化一些自定义的参数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomImageView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomImageView_src:
                    mSrc = BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomImageView_type:
                    type = a.getInt(attr, 0);//默认为Circle
                    break;
                case R.styleable.CustomImageView_borderRadius:
                    mRadius = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
                                    getResources().getDisplayMetrics()));//默认为10dp
                    break;
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            mWidth = specSize;
        } else {
            //由图片决定的宽
            int desireByImg = getPaddingLeft() + getPaddingRight()
                    + mSrc.getWidth();
            if (specMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(desireByImg, specSize);
            } else {
                mWidth = desireByImg;
            }
        }

        /*
        设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mHeight = specSize;
        } else {
            int desire = getPaddingTop() + getPaddingBottom()
                    + mSrc.getHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(desire, specSize);
            } else {
                mHeight = desire;
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (type) {
            case TYPE_CIRCLE:
                int min = Math.min(mWidth, mHeight);
                /**
                 * 长度如果不一致，按小的值进行压缩
                 */
                mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
                canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
                if (mIsDrawRight) {
                    /**
                     * 绘制对勾
                     */
                    Bitmap rightBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.right);
                    rightBitmap = Bitmap.createScaledBitmap(rightBitmap, min / 4, min / 4, false);
                    canvas.drawBitmap(createCircleImage(rightBitmap, min / 4), min * 3 / 4, min * 3 / 4, null);
                }
                break;
            case TYPE_ROUND:
                canvas.drawBitmap(createRoundConerImage(mSrc), 0, 0, null);
                break;
        }
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /*
        产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /*
        使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);


        return target;
    }

    /**
     * 根据原图添加圆角
     *
     * @param source
     * @return
     */
    private Bitmap createRoundConerImage(Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rect, mRadius, mRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    public void setSrc(Bitmap src) {
        this.mSrc = src;
        invalidate();
    }

    public void setDrawRight(boolean drawRight) {
        this.mIsDrawRight = drawRight;
        invalidate();
    }

}
