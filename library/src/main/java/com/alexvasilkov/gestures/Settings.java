package com.alexvasilkov.gestures;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Settings {

    private static final float MAX_ZOOM = 5f;
    private static final float DOUBLE_TAP_ZOOM = 3f;
    static final float OVERZOOM_FACTOR = 2f;
    public static final long ANIMATIONS_DURATION = 300L;

    private int viewportWidth;
    private int viewportHeight;
    private int imageWidth;
    private int imageHeight;
    private float maxZoom = MAX_ZOOM;
    private float doubleTapZoom = DOUBLE_TAP_ZOOM;
    private boolean isZoomEnabled = true;
    private boolean isRotationEnabled = false;

    public void initFromAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.GestureView);
        maxZoom = arr.getFloat(R.styleable.GestureView_gest_maxZoom, maxZoom);
        doubleTapZoom = arr.getFloat(R.styleable.GestureView_gest_doubleTapZoom, doubleTapZoom);
        isZoomEnabled = arr.getBoolean(R.styleable.GestureView_gest_zoomEnabled, isZoomEnabled);
        isRotationEnabled = arr.getBoolean(R.styleable.GestureView_gest_rotationEnabled, isRotationEnabled);

        arr.recycle();
    }

    public void setViewport(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
    }

    public void setImage(int width, int height) {
        imageWidth = width;
        imageHeight = height;
    }

    public Settings setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
        return this;
    }

    public Settings setDoubleTapZoom(float doubleTapZoom) {
        this.doubleTapZoom = doubleTapZoom;
        return this;
    }

    public Settings setZoomEnabled(boolean enabled) {
        isZoomEnabled = enabled;
        return this;
    }

    public Settings setRotationEnabled(boolean enabled) {
        isRotationEnabled = enabled;
        return this;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    float getDoubleTapZoom() {
        return doubleTapZoom;
    }

    boolean isZoomEnabled() {
        return isZoomEnabled;
    }

    boolean isRotationEnabled() {
        return isRotationEnabled;
    }

    boolean isDoubleTapEnabled() {
        return isZoomEnabled;
    }

    boolean isEnabled() {
        return isZoomEnabled || isRotationEnabled;
    }

    boolean hasImageSize() {
        return imageWidth != 0 && imageHeight != 0;
    }

    boolean hasViewportSize() {
        return viewportWidth != 0 && viewportHeight != 0;
    }
}
