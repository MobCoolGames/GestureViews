package com.alexvasilkov.gestures.utils;

import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.alexvasilkov.gestures.Settings;

public class FloatScroller {

    private final Interpolator interpolator;
    private boolean finished = true;
    private float startValue;
    private float finalValue;
    private float currValue;
    private long startRtc;

    public FloatScroller() {
        interpolator = new AccelerateDecelerateInterpolator();
    }

    public void forceFinished() {
        finished = true;
    }

    public void startScroll(float startValue, float finalValue) {
        finished = false;
        startRtc = SystemClock.elapsedRealtime();

        this.startValue = startValue;
        this.finalValue = finalValue;
        currValue = startValue;
    }

    public void computeScroll() {
        if (finished) {
            return;
        }

        long elapsed = SystemClock.elapsedRealtime() - startRtc;
        long duration = Settings.ANIMATIONS_DURATION;
        if (elapsed >= duration) {
            finished = true;
            currValue = finalValue;
            return;
        }

        float time = interpolator.getInterpolation((float) elapsed / duration);
        currValue = interpolate(startValue, finalValue, time);
    }

    public boolean isFinished() {
        return finished;
    }

    public float getFinal() {
        return finalValue;
    }

    public float getCurr() {
        return currValue;
    }

    private static float interpolate(float x1, float x2, float state) {
        return x1 + (x2 - x1) * state;
    }
}
