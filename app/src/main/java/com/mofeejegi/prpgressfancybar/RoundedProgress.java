package com.mofeejegi.prpgressfancybar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mofeejegi on 2020-02-16.
 */
public class RoundedProgress extends View {

    private Paint mPrimaryPaint;
    private Paint mShadowPaint;
    private RectF mRectF;
    private TextPaint mTextPaint;
    private Paint mBackgroundPaint;

    private boolean mDrawText = false;

    private int mShadowProgressColor;
    private int mPrimaryProgressColor;
    private int mBackgroundColor;

    private int mStrokeWidth;

    private float mProgress;
    private float mShadowProgress;
    private float mMaxProgress;

    private int mTextColor;

    private int mPrimaryCapSize;
    private int mShadowCapSize;

    private boolean mIsPrimaryCapVisible;
    private boolean mIsShadowCapVisible;

    private int x;
    private int y;
    private int mWidth = 0, mHeight = 0;


    public RoundedProgress(Context context) {
        super(context);
        init(context, null);
    }

    public RoundedProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundedProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        TypedArray a;
        if (attrs != null) {
            a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.RoundedProgress,
                    0, 0);
        } else {
            throw new IllegalArgumentException("Must have to pass the attributes");
        }

        try {
            //mDrawText = a.getBoolean(R.styleable.RoundedProgress_showProgressText, false);

            mBackgroundColor = a.getColor(R.styleable.RoundedProgress_backgroundColor, getResources().getColor(android.R.color.darker_gray));
            mPrimaryProgressColor = a.getColor(R.styleable.RoundedProgress_progressColor, getResources().getColor(android.R.color.darker_gray));
            mShadowProgressColor = getResources().getColor(android.R.color.black);

            mProgress = a.getFloat(R.styleable.RoundedProgress_progress, 0);
            mMaxProgress = a.getFloat(R.styleable.RoundedProgress_max, 100);
            mShadowProgress = mProgress + (mMaxProgress * 0.0025f);

            mStrokeWidth = a.getDimensionPixelSize(R.styleable.RoundedProgress_strokeWidth, 20);
            mTextColor = a.getColor(R.styleable.RoundedProgress_textColor, getResources().getColor(android.R.color.black));

            mPrimaryCapSize = a.getDimensionPixelSize(R.styleable.RoundedProgress_primaryCapSize, 20);
            mShadowCapSize = a.getDimensionPixelSize(R.styleable.RoundedProgress_shadowCapSize, 20);

            mIsPrimaryCapVisible = a.getBoolean(R.styleable.RoundedProgress_primaryCapVisibility, true);
            mIsShadowCapVisible = a.getBoolean(R.styleable.RoundedProgress_shadowCapVisibility, true);

        } finally {
            a.recycle();
        }

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mStrokeWidth);
        mBackgroundPaint.setColor(mBackgroundColor);

        mPrimaryPaint = new Paint();
        mPrimaryPaint.setAntiAlias(true);
        mPrimaryPaint.setStyle(Paint.Style.STROKE);
        mPrimaryPaint.setStrokeWidth(mStrokeWidth);
        mPrimaryPaint.setColor(mPrimaryProgressColor);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setStrokeWidth(mStrokeWidth);
        mShadowPaint.setColor(mShadowProgressColor);
        mShadowPaint.setShadowLayer(1, 0, 0, mShadowProgressColor);
        mShadowPaint.setAlpha(25);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);

        mRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        mTextPaint.setTextSize(w / 5);
        x = (w / 2) - ((int) (mTextPaint.measureText(mProgress + "%") / 2));
        y = (int) ((h / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        mWidth = w;
        mHeight = h;
        invalidate();
    }

    int startAngle = 140; //270

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPrimaryPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setStyle(Paint.Style.STROKE);

        // for drawing a full progress .. The background circle
        canvas.drawArc(mRectF, 0, 360, false, mBackgroundPaint);

        // for drawing a shadow progress circle
        float shadowSweepAngle = ((mShadowProgress * 360) / mMaxProgress);
        canvas.drawArc(mRectF, startAngle, shadowSweepAngle, false, mShadowPaint);

        // for drawing a main progress circle
        float primarySweepAngle = ((mProgress * 360) / mMaxProgress);
        canvas.drawArc(mRectF, startAngle, primarySweepAngle, false, mPrimaryPaint);

        // for cap of shadow progress,
        // include start angle in calculation to properly place the cap where the arc ends
        int r = (getHeight() - getPaddingLeft() * 2) / 2;      // Calculated from canvas width
        double trad = (shadowSweepAngle - (360 - startAngle)) * (Math.PI / 180d); // = 5.1051
        int x = (int) (r * Math.cos(trad));
        int y = (int) (r * Math.sin(trad));
        mShadowPaint.setStyle(Paint.Style.FILL);
        if (mIsShadowCapVisible)
            canvas.drawCircle(x + (mWidth / 2.0f), y + (mHeight / 2.0f), mShadowCapSize /2.0f, mShadowPaint);

        // for cap of primary progress,
        // include start angle in calculation to properly place the cap where the arc ends
        trad = (primarySweepAngle - (360 - startAngle)) * (Math.PI / 180d); // = 5.1051
        x = (int) (r * Math.cos(trad));
        y = (int) (r * Math.sin(trad));
        mPrimaryPaint.setStyle(Paint.Style.FILL);
        if (mIsPrimaryCapVisible) {
            canvas.drawCircle(x + (mWidth / 2.0f), y + (mHeight / 2.0f), mPrimaryCapSize / 2.0f, mPrimaryPaint);

            trad = (0 - (360 - startAngle)) * (Math.PI / 180d);
            x = (int) (r * Math.cos(trad));
            y = (int) (r * Math.sin(trad));
            canvas.drawCircle(x + (mWidth / 2.0f), y + (mHeight / 2.0f), mPrimaryCapSize / 2.0f, mPrimaryPaint);

        }


        if (mDrawText)
            canvas.drawText(mProgress + "%", x, y, mTextPaint);
    }

//    public void setDrawText(boolean mDrawText) {
//        this.mDrawText = mDrawText;
//        invalidate();
//    }
//
//    public void setBackgroundColor(int mBackgroundColor) {
//        this.mBackgroundColor = mBackgroundColor;
//        invalidate();
//    }
//
//    public void setSecondaryProgressColor(int mSecondaryProgressColor) {
//        this.mShadowProgressColor = mSecondaryProgressColor;
//        invalidate();
//    }
//
//    public void setPrimaryProgressColor(int mPrimaryProgressColor) {
//        this.mPrimaryProgressColor = mPrimaryProgressColor;
//        invalidate();
//    }
//
//    public void setStrokeWidth(int mStrokeWidth) {
//        this.mStrokeWidth = mStrokeWidth;
//        invalidate();
//    }
//
//    public void setProgress(int mProgress) {
//        this.mProgress = mProgress;
//        invalidate();
//    }
//
//    public void setSecondaryProgress(int mSecondaryProgress) {
//        this.mShadowProgress = mSecondaryProgress;
//        invalidate();
//    }
//
//    public void setTextColor(int mTextColor) {
//        this.mTextColor = mTextColor;
//        invalidate();
//    }
//
//    public void setPrimaryCapSize(int mPrimaryCapSize) {
//        this.mPrimaryCapSize = mPrimaryCapSize;
//        invalidate();
//    }
//
//    public void setSecondaryCapSize(int mSecondaryCapSize) {
//        this.mShadowCapSize = mSecondaryCapSize;
//        invalidate();
//    }
//
//    public boolean isPrimaryCapVisible() {
//        return mIsPrimaryCapVisible;
//    }
//
//    public void setIsPrimaryCapVisible(boolean mIsPrimaryCapVisible) {
//        this.mIsPrimaryCapVisible = mIsPrimaryCapVisible;
//    }
//
//    public boolean isSecondaryCapVisible() {
//        return mIsShadowCapVisible;
//    }
//
//    public void setIsSecondaryCapVisible(boolean mIsSecondaryCapVisible) {
//        this.mIsShadowCapVisible = mIsSecondaryCapVisible;
//    }
//
//
//    public int getSecondaryProgressColor() {
//        return mShadowProgressColor;
//    }
//
//    public int getPrimaryProgressColor() {
//        return mPrimaryProgressColor;
//    }
//
//    public int getProgress() {
//        return mProgress;
//    }
//
//    public int getBackgroundColor() {
//        return mBackgroundColor;
//    }
//
//    public int getSecodaryProgress() {
//        return mShadowProgress;
//    }
//
//    public int getPrimaryCapSize() {
//        return mPrimaryCapSize;
//    }
//
//    public int getSecondaryCapSize() {
//        return mShadowCapSize;
//    }
}
