package com.csmy.minyuanplus.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.csmy.minyuanplus.R;

/**
 * 自定义View,实现圆角，圆形等效果
 * Created by Zero on 16/8/18.
 */
public class CustomColorView extends View {
    /**
     * 颜色
     */
    private int mColor;
    /**
     * 字体大小
     */
    private int mTextSize;
    /**
     * 要画的数字
     */
    private int mTextInt;
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
    /**
     * 是否画数字
     */
    private boolean mIsDrawText = false;

    public CustomColorView(Context context) {
        this(context, null);
    }

    public CustomColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 初始化一些自定义的参数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomColorView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomColorView_circleColor:
                    mColor = a.getColor(0, 0xffffff);
                    break;
                case R.styleable.CustomColorView_textSize:
                    mTextSize = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f
                                    , getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            mWidth = specSize;
        }

        /*
        设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mHeight = specSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int min = Math.min(mWidth, mHeight);
        /*
         * 长度如果不一致，按小的值进行压缩
         */
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mColor);
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        if (mIsDrawRight) {
            /*
             *绘制对勾
             */
            Paint linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(5f);
            linePaint.setColor(Color.WHITE);
            linePaint.setStyle(Paint.Style.STROKE);
            Path path = new Path();
            path.moveTo(min / 4, min / 2);
            path.lineTo(min / 2, min * 3 / 4);
            path.lineTo(min * 3 / 4, min / 4);
            canvas.drawPath(path, linePaint);
        }
        if (mIsDrawText) {
            /*
            绘制数字
             */
            String num = mTextInt + "";
            Paint numPaint = new Paint();
            numPaint.setStrokeWidth(3);
            numPaint.setTextSize(mTextSize);
            numPaint.setColor(Color.WHITE);
            numPaint.setTextAlign(Paint.Align.LEFT);
            Rect bounds = new Rect();
            numPaint.getTextBounds(num, 0, num.length(), bounds);
            Paint.FontMetricsInt fontMetrics = numPaint.getFontMetricsInt();
            int baseLine = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(num, getMeasuredWidth() / 2 - bounds.width() / 2, baseLine, numPaint);
//            canvas.drawText(num, getMeasuredWidth() / 2 - bounds.width(), baseLine, numPaint);

        }
    }


    public void setCircleColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public void setDrawRight(boolean drawRight) {
        this.mIsDrawRight = drawRight;
        invalidate();
    }

    public void setTextInt(int i) {
            mIsDrawText = true;
            mTextInt = i;
            invalidate();
    }

}
