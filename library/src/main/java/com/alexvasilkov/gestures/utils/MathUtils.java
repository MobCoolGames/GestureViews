package com.alexvasilkov.gestures.utils;

import android.graphics.Matrix;

import com.alexvasilkov.gestures.State;

import androidx.annotation.Size;

public class MathUtils {
    private static final Matrix tmpMatrix = new Matrix();
    private static final Matrix tmpMatrixInverse = new Matrix();

    public static float restrict(float value, float minValue, float maxValue) {
        return Math.max(minValue, Math.min(value, maxValue));
    }

    private static float interpolate(float start, float end, float factor) {
        return start + (end - start) * factor;
    }

    public static void interpolate(State out, State start, State end, float factor) {
        interpolate(out, start, start.getX(), start.getY(), end, end.getX(), end.getY(), factor);
    }

    public static void interpolate(State out, State start, float startPivotX, float startPivotY, State end, float endPivotX, float endPivotY, float factor) {
        out.set(start);

        if (!State.equals(start.getZoom(), end.getZoom())) {
            float zoom = interpolate(start.getZoom(), end.getZoom(), factor);
            out.zoomTo(zoom, startPivotX, startPivotY);
        }

        float startRotation = start.getRotation();
        float endRotation = end.getRotation();
        float rotation = Float.NaN;

        if (Math.abs(startRotation - endRotation) <= 180f) {
            if (!State.equals(startRotation, endRotation)) {
                rotation = interpolate(startRotation, endRotation, factor);
            }
        } else {
            float startRotationPositive = startRotation < 0f ? startRotation + 360f : startRotation;
            float endRotationPositive = endRotation < 0f ? endRotation + 360f : endRotation;

            if (!State.equals(startRotationPositive, endRotationPositive)) {
                rotation = interpolate(startRotationPositive, endRotationPositive, factor);
            }
        }

        if (!Float.isNaN(rotation)) {
            out.rotateTo(rotation, startPivotX, startPivotY);
        }

        float dx = interpolate(0, endPivotX - startPivotX, factor);
        float dy = interpolate(0, endPivotY - startPivotY, factor);
        out.translateBy(dx, dy);
    }

    public static void computeNewPosition(@Size(2) float[] point, State initialState, State finalState) {
        initialState.get(tmpMatrix);
        tmpMatrix.invert(tmpMatrixInverse);
        tmpMatrixInverse.mapPoints(point);
        finalState.get(tmpMatrix);
        tmpMatrix.mapPoints(point);
    }
}
