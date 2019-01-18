package com.alexvasilkov.gestures.internal.detectors;

import android.view.MotionEvent;

public class RotationGestureDetector {

    private static final float ROTATION_SLOP = 5f;

    private final OnRotationGestureListener listener;

    private float focusX;
    private float focusY;
    private float initialAngle;
    private float currAngle;
    private float prevAngle;
    private boolean isInProgress;
    private boolean isGestureAccepted;

    public RotationGestureDetector(OnRotationGestureListener listener) {
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                cancelRotation();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    initialAngle = prevAngle = currAngle = computeRotation(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() >= 2 && (!isInProgress || isGestureAccepted)) {
                    currAngle = computeRotation(event);
                    focusX = 0.5f * (event.getX(1) + event.getX(0));
                    focusY = 0.5f * (event.getY(1) + event.getY(0));
                    boolean isAlreadyStarted = isInProgress;
                    tryStartRotation();
                    boolean isAccepted = !isAlreadyStarted || processRotation();
                    if (isAccepted) {
                        prevAngle = currAngle;
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    cancelRotation();
                }
                break;

            default:
        }

        return true;
    }

    private void tryStartRotation() {
        if (isInProgress || Math.abs(initialAngle - currAngle) < ROTATION_SLOP) {
            return;
        }
        isInProgress = true;
        isGestureAccepted = listener.onRotationBegin(this);
    }

    private void cancelRotation() {
        if (!isInProgress) {
            return;
        }

        isInProgress = false;
        if (isGestureAccepted) {
            listener.onRotationEnd(this);
            isGestureAccepted = false;
        }
    }

    private boolean processRotation() {
        return isInProgress && isGestureAccepted && listener.onRotate(this);
    }

    private float computeRotation(MotionEvent event) {
        return (float) Math.toDegrees(Math.atan2(event.getY(1) - event.getY(0), event.getX(1) - event.getX(0)));
    }

    public float getFocusX() {
        return focusX;
    }

    public float getFocusY() {
        return focusY;
    }

    public float getRotationDelta() {
        return currAngle - prevAngle;
    }

    public interface OnRotationGestureListener {
        boolean onRotate(RotationGestureDetector detector);

        boolean onRotationBegin(RotationGestureDetector detector);

        void onRotationEnd(RotationGestureDetector detector);
    }
}
