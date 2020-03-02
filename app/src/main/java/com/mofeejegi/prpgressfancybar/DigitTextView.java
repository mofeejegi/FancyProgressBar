package com.mofeejegi.prpgressfancybar;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by mofeejegi on 2020-02-29.
 */
public class DigitTextView extends FrameLayout {

    private int ANIMATION_DURATION = 750;
    TextView currentTextView, nextTextView;

    public DigitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DigitTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.digit_text_view, this);

        currentTextView = findViewById(R.id.currentTextView);
        nextTextView = getRootView().findViewById(R.id.nextTextView);

        nextTextView.setTranslationY(getHeight()*2);

        setValue(0, false, false);
    }

    public void setValue(final int desiredValue, boolean isInitialSet, boolean shouldReverse) {
        if (currentTextView.getText() == null || currentTextView.getText().length() == 0) {
            currentTextView.setText(String.format(Locale.getDefault(), "%d", desiredValue));
        }

        final int oldValue = Integer.parseInt(currentTextView.getText().toString());

        if (isInitialSet) {
            nextTextView.setTextColor(getResources().getColor(R.color.cibilBackground));
            new Handler().postDelayed(() -> nextTextView.setTextColor(Color.BLACK), 1500);
        }

        int dir = shouldReverse ? -1 : 1;

        if (isInitialSet && (oldValue != desiredValue)) {
            ANIMATION_DURATION = 750 / (Math.abs(oldValue - desiredValue));
            Log.e("TAG", "setValue: " + ANIMATION_DURATION);
        }

        if (oldValue > desiredValue) {
            nextTextView.setText(String.format(Locale.getDefault(), "%d", oldValue-1));

            currentTextView.animate().translationY(-getHeight()*dir).setDuration(ANIMATION_DURATION).start();
            nextTextView.setTranslationY(getHeight()*dir);

            nextTextView.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION).withEndAction(() -> {
                currentTextView.setText(String.format(Locale.getDefault(), "%d", oldValue - 1));
                currentTextView.setTranslationY(0);
                if (oldValue - 1 != desiredValue) {
                    setValue(desiredValue, false, shouldReverse);
                }
            }).start();

        } else if (oldValue < desiredValue) {
            nextTextView.setText(String.format(Locale.getDefault(), "%d", oldValue+1));

            currentTextView.animate().translationY(getHeight()*dir).setDuration(ANIMATION_DURATION).start();
            nextTextView.setTranslationY(-getHeight()*dir);

            nextTextView.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION).withEndAction(() -> {
                currentTextView.setText(String.format(Locale.getDefault(), "%d", oldValue + 1));
                currentTextView.setTranslationY(0);
                if (oldValue + 1 != desiredValue) {
                    setValue(desiredValue, false, shouldReverse);
                }
            }).start();

        } else {
            nextTextView.setText(currentTextView.getText());
        }



    }
}
