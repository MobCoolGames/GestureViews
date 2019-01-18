package com.alexvasilkov.gestures.utils;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;

public class GravityUtils {

    private static final Matrix tmpMatrix = new Matrix();
    private static final RectF tmpRectF = new RectF();

    private static final Rect tmpRect1 = new Rect();
    private static final Rect tmpRect2 = new Rect();


    public static void getImagePosition(State state, Settings settings, Rect out) {
        state.get(tmpMatrix);
        getImagePosition(tmpMatrix, settings, out);
    }

    public static void getImagePosition(Matrix matrix, Settings settings, Rect out) {
        tmpRectF.set(0, 0, settings.getImageWidth(), settings.getImageHeight());

        matrix.mapRect(tmpRectF);

        final int width = Math.round(tmpRectF.width());
        final int height = Math.round(tmpRectF.height());

        tmpRect1.set(0, 0, settings.getViewportWidth(), settings.getViewportHeight());
        Gravity.apply(Gravity.CENTER, width, height, tmpRect1, out);
    }

    public static void getMovementAreaPosition(Settings settings, Rect out) {
        tmpRect1.set(0, 0, settings.getViewportWidth(), settings.getViewportHeight());
        Gravity.apply(Gravity.CENTER, settings.getViewportWidth(), settings.getViewportHeight(), tmpRect1, out);
    }

    public static void getDefaultPivot(Settings settings, Point out) {
        getMovementAreaPosition(settings, tmpRect2);
        Gravity.apply(Gravity.CENTER, 0, 0, tmpRect2, tmpRect1);
        out.set(tmpRect1.left, tmpRect1.top);
    }
}
