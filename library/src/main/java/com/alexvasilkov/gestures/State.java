package com.alexvasilkov.gestures;

import android.graphics.Matrix;

public class State {
    private static final float EPSILON = 0.001f;

    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];

    private float x;
    private float y;
    private float zoom = 1f;
    private float rotation;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZoom() {
        return zoom;
    }

    public float getRotation() {
        return rotation;
    }

    public void get(Matrix matrix) {
        matrix.set(this.matrix);
    }

    public void translateBy(float dx, float dy) {
        matrix.postTranslate(dx, dy);
        updateFromMatrix(false, false);
    }

    void translateTo(float x, float y) {
        matrix.postTranslate(-this.x + x, -this.y + y);
        updateFromMatrix(false, false);
    }

    void zoomBy(float factor, float pivotX, float pivotY) {
        matrix.postScale(factor, factor, pivotX, pivotY);
        updateFromMatrix(true, false);
    }

    public void zoomTo(float zoom, float pivotX, float pivotY) {
        matrix.postScale(zoom / this.zoom, zoom / this.zoom, pivotX, pivotY);
        updateFromMatrix(true, false);
    }

    void rotateBy(float angle, float pivotX, float pivotY) {
        matrix.postRotate(angle, pivotX, pivotY);
        updateFromMatrix(false, true);
    }

    public void rotateTo(float angle, float pivotX, float pivotY) {
        matrix.postRotate(-rotation + angle, pivotX, pivotY);
        updateFromMatrix(false, true);
    }

    public void set(float x, float y, float zoom, float rotation) {
        while (rotation < -180f) {
            rotation += 360f;
        }

        while (rotation > 180f) {
            rotation -= 360f;
        }

        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.rotation = rotation;

        matrix.reset();
        if (zoom != 1f) {
            matrix.postScale(zoom, zoom);
        }

        if (rotation != 0f) {
            matrix.postRotate(rotation);
        }
        matrix.postTranslate(x, y);
    }

    public void set(State other) {
        x = other.x;
        y = other.y;
        zoom = other.zoom;
        rotation = other.rotation;
        matrix.set(other.matrix);
    }

    State copy() {
        State copy = new State();
        copy.set(this);
        return copy;
    }

    private void updateFromMatrix(boolean updateZoom, boolean updateRotation) {
        matrix.getValues(matrixValues);
        x = matrixValues[2];
        y = matrixValues[5];
        if (updateZoom) {
            zoom = (float) Math.hypot(matrixValues[1], matrixValues[4]);
        }

        if (updateRotation) {
            rotation = (float) Math.toDegrees(Math.atan2(matrixValues[3], matrixValues[4]));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        State state = (State) obj;

        return equals(state.x, x) && equals(state.y, y) && equals(state.zoom, zoom) && equals(state.rotation, rotation);
    }

    @Override
    public int hashCode() {
        int result = (x != 0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != 0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (zoom != 0f ? Float.floatToIntBits(zoom) : 0);
        result = 31 * result + (rotation != 0f ? Float.floatToIntBits(rotation) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{x=" + x + ",y=" + y + ",zoom=" + zoom + ",rotation=" + rotation + "}";
    }

    public static boolean equals(float v1, float v2) {
        return v1 >= v2 - EPSILON && v1 <= v2 + EPSILON;
    }

    static int compare(float v1) {
        return v1 > 0f + EPSILON ? 1 : v1 < 0f - EPSILON ? -1 : 0;
    }
}
