package com.mofeejegi.prpgressfancybar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by mofeejegi on 2020-02-16.
 */
public class RoundedProgress extends View {

    // TODO ANIMATION ONLY FORWARD, PUT CHECK FOR UPDATED > FINAL AT START TO NAVIGATE BACKWARDS

    public static int MODE_ANIMATED = 0;
    public static int MODE_STATIC = 1;

    private int mode = MODE_ANIMATED;

    private Paint mShadowPaint;
    private Paint progressBarPaint;

    int mPoorProgressColor;
    int mFairProgressColor;
    int mGoodProgressColor;
    int mVeryGoodProgressColor;
    int mExcellentProgressColor;
    int mBackgroundColor;
    int mShadowColor;

    private RectF mRectF;
    private TextPaint mTextPaint;

    private boolean mDrawText = false;

    private int mStrokeWidth;

    private int mUpdatedAlpha; // used for animation purposes
    private float mUpdatedProgress; // used for animation purposes
    private float mFinalProgress;

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

        // Disable hardware acceleration for this view to enable blur shadow
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        try {
            //mDrawText = a.getBoolean(R.styleable.RoundedProgress_showProgressText, false);

            mBackgroundColor = getResources().getColor(R.color.cibilBackground);
            mPoorProgressColor = getResources().getColor(R.color.cibilPoor);
            mFairProgressColor = getResources().getColor(R.color.cibilFair);
            mGoodProgressColor = getResources().getColor(R.color.cibilGood);
            mVeryGoodProgressColor = getResources().getColor(R.color.cibilVeryGood);
            mExcellentProgressColor = getResources().getColor(R.color.cibilExcellent);
            mShadowColor = getResources().getColor(android.R.color.black);

            mode = a.getBoolean(R.styleable.RoundedProgress_shouldAnimate, true) ? MODE_ANIMATED : MODE_STATIC;

            mFinalProgress = max(a.getFloat(R.styleable.RoundedProgress_progress, mMin) - mMin, 0);
            if (mode == MODE_STATIC)
                mUpdatedProgress = mFinalProgress;

            mStrokeWidth = a.getDimensionPixelSize(R.styleable.RoundedProgress_strokeWidth, 35);
            mTextColor = a.getColor(R.styleable.RoundedProgress_textColor, getResources().getColor(android.R.color.black));

            mPrimaryCapSize = a.getDimensionPixelSize(R.styleable.RoundedProgress_primaryCapSize, 35);
            mShadowCapSize = a.getDimensionPixelSize(R.styleable.RoundedProgress_shadowCapSize, 20);

        } finally {
            a.recycle();
        }

        progressBarPaint = new Paint();
        progressBarPaint.setAntiAlias(true);
        progressBarPaint.setStyle(Paint.Style.STROKE);
        progressBarPaint.setStrokeWidth(mStrokeWidth);
        progressBarPaint.setColor(mExcellentProgressColor);

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

        drawLevelArc(canvas, mBackgroundColor, mMax, mMaxAngle, false);

        if (mUpdatedProgress > 0) {

            // Show shadow in excellent progress bar except when it reaches the end
            if (mFinalProgress > mVeryGoodMax)
                drawLevelArc(canvas, mExcellentProgressColor, mExcellentMax, mExcellentMaxAngle, mUpdatedProgress != mMax);

            if (mFinalProgress > mGoodMax)
                drawLevelArc(canvas, mVeryGoodProgressColor, mVeryGoodMax, mVeryGoodMaxAngle, true);

            if (mFinalProgress > mFairMax)
                drawLevelArc(canvas, mGoodProgressColor, mGoodMax, mGoodMaxAngle, true);

            if (mFinalProgress > mPoorMax)
                drawLevelArc(canvas, mFairProgressColor, mFairMax, mFairMaxAngle, true);

            if (mFinalProgress > 0)
                drawLevelArc(canvas, mPoorProgressColor, mPoorMax, mPoorMaxAngle, true);

        }


        if (mDrawText)
            canvas.drawText(mUpdatedProgress+"", x, y, mTextPaint);
    }

    private void drawLevelArc(Canvas canvas, int progressBarColor, float maxSectionProgress, float maxAngle, boolean shouldShowShadow) {
        int r = (getHeight() - getPaddingLeft() * 2) / 2; // Calculated from canvas width
        int x, y;
        double trad;
        float updatedProgress;

        progressBarPaint.setStyle(Paint.Style.STROKE);
        progressBarPaint.setColor(progressBarColor);

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

    public void animateProgress(float startProgress) {
        if (mode == MODE_STATIC) return;

        // Make range 0 - 600
        startProgress = max(startProgress - 300, 0);
        startProgress = min(startProgress, 600);

        this.mUpdatedProgress = startProgress;

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofFloat(this, "updatedProgress", startProgress, mFinalProgress);
        progressAnimator.setDuration(1500);
        //progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
            if (mode == MODE_ANIMATED) {
            setUpdatedProgress(0);
        } else {
            setUpdatedProgress(mFinalProgress);
        }
    }

    public void setUpdatedProgress(float updatedProgress) {
        this.mUpdatedProgress = updatedProgress;
        invalidate();
    }

    public float getUpdatedProgress() {
        return mUpdatedProgress;
    }

    public void setProgress(float finalProgress) {
        this.mFinalProgress = finalProgress - mMin;
        invalidate();
    }

    public float getProgress() {
        return mFinalProgress;
    }
}
