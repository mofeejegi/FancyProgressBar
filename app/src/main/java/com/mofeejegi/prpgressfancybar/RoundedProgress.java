package com.mofeejegi.prpgressfancybar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import static java.lang.Math.max;

/**
 * Created by mofeejegi on 2020-02-16.
 */
public class RoundedProgress extends View {

    private Paint mShadowPaint;
    private Paint mPoorBarPaint;
    private Paint mFairBarPaint;
    private Paint mGoodBarPaint;
    private Paint mVeryGoodBarPaint;
    private Paint mExcellentBarPaint;
    private Paint mBackgroundPaint;

    private RectF mRectF;
    private TextPaint mTextPaint;

    private boolean mDrawText = false;

    private int mStrokeWidth;

    private float mUpdatedProgress;
    private float mFinalProgress;
    private float mMaxProgress;

    private float mMin = 300;
    private float mPoorMax = 549 - mMin;
    private float mFairMax = 649 - mMin;
    private float mGoodMax = 749 - mMin;
    private float mVeryGoodMax = 799 - mMin;
    private float mExcellentMax = 900 - mMin;
    private float mMax = mExcellentMax;

    private int mPoorMaxAngle = 70; //+0 (@startAngle = 140 deg from top)
    private int mFairMaxAngle = 125; //+55
    private int mGoodMaxAngle = 175; //+50
    private int mVeryGoodMaxAngle = 215; //+40
    private int mExcellentMaxAngle = 260; //+45
    private int mMaxAngle = mExcellentMaxAngle;


    private int mTextColor;

    private int mPrimaryCapSize;
    private int mShadowCapSize;

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

        int mPoorProgressColor;
        int mFairProgressColor;
        int mGoodProgressColor;
        int mVeryGoodProgressColor;
        int mExcellentProgressColor;
        int mBackgroundColor;
        int mShadowColor;

        try {
            //mDrawText = a.getBoolean(R.styleable.RoundedProgress_showProgressText, false);

            mBackgroundColor = getResources().getColor(R.color.cibilBackground);
            mPoorProgressColor = getResources().getColor(R.color.cibilPoor);
            mFairProgressColor = getResources().getColor(R.color.cibilFair);
            mGoodProgressColor = getResources().getColor(R.color.cibilGood);
            mVeryGoodProgressColor = getResources().getColor(R.color.cibilVeryGood);
            mExcellentProgressColor = getResources().getColor(R.color.cibilExcellent);
            mShadowColor = getResources().getColor(android.R.color.black);

            mFinalProgress = max(a.getFloat(R.styleable.RoundedProgress_progress, mMin) - mMin, 0);
            mUpdatedProgress = mFinalProgress; // use for animation purposes
            mMaxProgress = mExcellentMax;

            mStrokeWidth = a.getDimensionPixelSize(R.styleable.RoundedProgress_strokeWidth, 35);
            mTextColor = a.getColor(R.styleable.RoundedProgress_textColor, getResources().getColor(android.R.color.black));

            mPrimaryCapSize = a.getDimensionPixelSize(R.styleable.RoundedProgress_primaryCapSize, 35);
            mShadowCapSize = a.getDimensionPixelSize(R.styleable.RoundedProgress_shadowCapSize, 20);

        } finally {
            a.recycle();
        }

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mStrokeWidth);
        mBackgroundPaint.setColor(mBackgroundColor);

        mPoorBarPaint = new Paint();
        mPoorBarPaint.setAntiAlias(true);
        mPoorBarPaint.setStyle(Paint.Style.STROKE);
        mPoorBarPaint.setStrokeWidth(mStrokeWidth);
        mPoorBarPaint.setColor(mPoorProgressColor);

        mFairBarPaint = new Paint();
        mFairBarPaint.setAntiAlias(true);
        mFairBarPaint.setStyle(Paint.Style.STROKE);
        mFairBarPaint.setStrokeWidth(mStrokeWidth);
        mFairBarPaint.setColor(mFairProgressColor);

        mGoodBarPaint = new Paint();
        mGoodBarPaint.setAntiAlias(true);
        mGoodBarPaint.setStyle(Paint.Style.STROKE);
        mGoodBarPaint.setStrokeWidth(mStrokeWidth);
        mGoodBarPaint.setColor(mGoodProgressColor);

        mVeryGoodBarPaint = new Paint();
        mVeryGoodBarPaint.setAntiAlias(true);
        mVeryGoodBarPaint.setStyle(Paint.Style.STROKE);
        mVeryGoodBarPaint.setStrokeWidth(mStrokeWidth);
        mVeryGoodBarPaint.setColor(mVeryGoodProgressColor);

        mExcellentBarPaint = new Paint();
        mExcellentBarPaint.setAntiAlias(true);
        mExcellentBarPaint.setStyle(Paint.Style.STROKE);
        mExcellentBarPaint.setStrokeWidth(mStrokeWidth);
        mExcellentBarPaint.setColor(mExcellentProgressColor);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL));
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(mShadowColor);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);

        mRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        mTextPaint.setTextSize(w / 5);
        x = (w / 2) - ((int) (mTextPaint.measureText(mUpdatedProgress + "%") / 2));
        y = (int) ((h / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        mWidth = w;
        mHeight = h;
        invalidate();
    }

    int startAngle = 140; //270

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawLevelArc(canvas, mBackgroundPaint, mMax, mMaxAngle, false);

        // Show shadow in excellent progress bar except when it reaches the end
        if (mFinalProgress > mVeryGoodMax)
            drawLevelArc(canvas, mExcellentBarPaint, mExcellentMax, mExcellentMaxAngle, mUpdatedProgress != mMaxProgress);

        if (mFinalProgress > mGoodMax)
            drawLevelArc(canvas, mVeryGoodBarPaint, mVeryGoodMax, mVeryGoodMaxAngle, true);

        if (mFinalProgress > mFairMax)
            drawLevelArc(canvas, mGoodBarPaint, mGoodMax, mGoodMaxAngle, true);

        if (mFinalProgress > mPoorMax)
            drawLevelArc(canvas, mFairBarPaint, mFairMax, mFairMaxAngle, true);

        if (mFinalProgress > 0)
            drawLevelArc(canvas, mPoorBarPaint, mPoorMax, mPoorMaxAngle, true);


        if (mDrawText)
            canvas.drawText(mUpdatedProgress+"", x, y, mTextPaint);
    }

    private void drawLevelArc(Canvas canvas, Paint progressBarPaint, float maxSectionProgress, float maxAngle, boolean shouldShowShadow) {
        int r = (getHeight() - getPaddingLeft() * 2) / 2; // Calculated from canvas width
        int x, y;
        double trad;
        float updatedProgress;

        // Interpolate for animation purposes
        if (maxSectionProgress < mFinalProgress)
            updatedProgress = maxSectionProgress * (mUpdatedProgress/mFinalProgress);
        else
            updatedProgress = mUpdatedProgress;

        if (shouldShowShadow) {
            float shadowSweepAngle = ((updatedProgress * maxAngle) / (maxSectionProgress)) + 2f;

            // for cap of shadow progress,
            // include start angle in calculation to properly place the cap where the arc ends
            // Draw shadow cap at end
            trad = (shadowSweepAngle - (360 - startAngle)) * (Math.PI / 180d); // = 5.1051
            x = (int) (r * Math.cos(trad));
            y = (int) (r * Math.sin(trad));
            canvas.drawCircle(x + (mWidth / 2.0f), y + (mHeight / 2.0f), mShadowCapSize / 2.0f, mShadowPaint);

        } else {
            // Background view or completed excellent progress should always draw the complete arc
            updatedProgress = maxSectionProgress;
        }


        float progressSweepAngle = ((updatedProgress * maxAngle) / (maxSectionProgress));
        canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, progressBarPaint);

        // for cap of primary progress,
        // include start angle in calculation to properly place the cap where the arc ends
        // Draw cap at end
        trad = (progressSweepAngle - (360 - startAngle)) * (Math.PI / 180d); // = 5.1051
        x = (int) (r * Math.cos(trad));
        y = (int) (r * Math.sin(trad));
        progressBarPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x + (mWidth / 2.0f), y + (mHeight / 2.0f), mPrimaryCapSize / 2.0f, progressBarPaint);

        // Draw cap at start
        trad = (0 - (360 - startAngle)) * (Math.PI / 180d);
        x = (int) (r * Math.cos(trad));
        y = (int) (r * Math.sin(trad));
        canvas.drawCircle(x + (mWidth / 2.0f), y + (mHeight / 2.0f), mPrimaryCapSize / 2.0f, progressBarPaint);

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
//    public void setPrimaryProgressColor(int mPoorProgressColor) {
//        this.mPoorProgressColor = mPoorProgressColor;
//        invalidate();
//    }
//
//    public void setStrokeWidth(int mStrokeWidth) {
//        this.mStrokeWidth = mStrokeWidth;
//        invalidate();
//    }
//
//    public void setProgress(int mUpdatedProgress) {
//        this.mUpdatedProgress = mUpdatedProgress;
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
//        return mPoorProgressColor;
//    }
//
//    public int getProgress() {
//        return mUpdatedProgress;
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
