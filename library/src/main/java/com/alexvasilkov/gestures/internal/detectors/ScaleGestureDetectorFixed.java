package com.alexvasilkov.gestures.internal.detectors;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

public class ScaleGestureDetectorFixed extends ScaleGestureDetector {

    private float currY;
    private float prevY;

    public ScaleGestureDetectorFixed(Context context, OnScaleGestureListener listener) {
        super(context, listener);
        warmUpScaleDetector();
    }

    private void warmUpScaleDetector() {
        long time = System.currentTimeMillis();
        MotionEvent event = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, 0f, 0f, 0);
        onTouchEvent(event);
        event.recycle();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final boolean result = super.onTouchEvent(event);

        prevY = currY;
        currY = event.getY();

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            prevY = event.getY();
        }

        return result;
    }

    private boolean isInDoubleTapMode() {
        return isQuickScaleEnabled() && getCurrentSpan() == getCurrentSpanY();
    }

    @Override
    public float getScaleFactor() {
        float factor = super.getScaleFactor();

        if (isInDoubleTapMode()) {
            return (currY > prevY && factor > 1f) || (currY < prevY && factor < 1f)
                    ? Math.max(0.8f, Math.min(factor, 1.25f)) : 1f;
        } else {
            return factor;
        }
    }

}
