package com.alexvasilkov.gestures.internal;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.utils.GravityUtils;
import com.alexvasilkov.gestures.utils.MathUtils;

public class MovementBounds {

    private static final Matrix tmpMatrix = new Matrix();
    private static final float[] tmpPointArr = new float[2];
    private static final Rect tmpRect = new Rect();
    private static final RectF tmpRectF = new RectF();

    private final Settings settings;

    private final RectF bounds = new RectF();
    private float boundsRotation;
    private float boundsPivotX;
    private float boundsPivotY;

    public MovementBounds(Settings settings) {
        this.settings = settings;
    }

    public MovementBounds set(State state) {
        RectF area = tmpRectF;
        GravityUtils.getMovementAreaPosition(settings, tmpRect);
        area.set(tmpRect);

        final Rect pos = tmpRect;

        boundsRotation = 0f;
        boundsPivotX = boundsPivotY = 0f;

        state.get(tmpMatrix);
        if (!State.equals(boundsRotation, 0f)) {
            tmpMatrix.postRotate(-boundsRotation, boundsPivotX, boundsPivotY);
        }
        GravityUtils.getImagePosition(tmpMatrix, settings, pos);

        calculateNormalBounds(area, pos);

        state.get(tmpMatrix);

        RectF imageRect = tmpRectF;
        imageRect.set(0, 0, settings.getImageW(), settings.getImageH());
        tmpMatrix.mapRect(imageRect);

        tmpPointArr[0] = tmpPointArr[1] = 0f;
        tmpMatrix.mapPoints(tmpPointArr);

        bounds.offset(tmpPointArr[0] - imageRect.left, tmpPointArr[1] - imageRect.top);

        return this;
    }

    private void calculateNormalBounds(RectF area, Rect pos) {
        if (area.width() < pos.width()) {
            bounds.left = area.left - (pos.width() - area.width());
            bounds.right = area.left;
        } else {
            bounds.left = bounds.right = pos.left;
        }

        if (area.height() < pos.height()) {
            bounds.top = area.top - (pos.height() - area.height());
            bounds.bottom = area.top;
        } else {
            bounds.top = bounds.bottom = pos.top;
        }
    }

    public void extend(float x, float y) {
        tmpPointArr[0] = x;
        tmpPointArr[1] = y;

        if (boundsRotation != 0f) {
            tmpMatrix.setRotate(-boundsRotation, boundsPivotX, boundsPivotY);
            tmpMatrix.mapPoints(tmpPointArr);
        }

        bounds.union(tmpPointArr[0], tmpPointArr[1]);
    }


    public void getExternalBounds(RectF out) {
        if (boundsRotation == 0f) {
            out.set(bounds);
        } else {
            tmpMatrix.setRotate(boundsRotation, boundsPivotX, boundsPivotY);
            tmpMatrix.mapRect(out, bounds);
        }
    }

    public void restrict(float x, float y, float extraX, float extraY, PointF out) {
        tmpPointArr[0] = x;
        tmpPointArr[1] = y;

        if (boundsRotation != 0f) {
            tmpMatrix.setRotate(-boundsRotation, boundsPivotX, boundsPivotY);
            tmpMatrix.mapPoints(tmpPointArr);
        }

        tmpPointArr[0] = MathUtils.restrict(tmpPointArr[0], bounds.left - extraX, bounds.right + extraX);
        tmpPointArr[1] = MathUtils.restrict(tmpPointArr[1], bounds.top - extraY, bounds.bottom + extraY);

        if (boundsRotation != 0f) {
            tmpMatrix.setRotate(boundsRotation, boundsPivotX, boundsPivotY);
            tmpMatrix.mapPoints(tmpPointArr);
        }

        out.set(tmpPointArr[0], tmpPointArr[1]);
    }

    public void restrict(float x, float y, PointF out) {
        restrict(x, y, 0f, 0f, out);
    }
}
