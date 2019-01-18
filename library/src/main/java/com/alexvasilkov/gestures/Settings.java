package com.alexvasilkov.gestures;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Settings {

    public static final float MAX_ZOOM = 5f;
    public static final float DOUBLE_TAP_ZOOM = 3f;
    public static final float OVERZOOM_FACTOR = 2f;
    public static final long ANIMATIONS_DURATION = 300L;

    private int viewportW;
    private int viewportH;
    private int imageW;
    private int imageH;
    private float minZoom = 0f;
    private float maxZoom = MAX_ZOOM;
    private float doubleTapZoom = DOUBLE_TAP_ZOOM;
    private boolean isZoomEnabled = true;
    private boolean isRotationEnabled = false;

    Settings() {
    }

    public void initFromAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.GestureView);
        minZoom = arr.getFloat(R.styleable.GestureView_gest_minZoom, minZoom);
        maxZoom = arr.getFloat(R.styleable.GestureView_gest_maxZoom, maxZoom);
        doubleTapZoom = arr.getFloat(R.styleable.GestureView_gest_doubleTapZoom, doubleTapZoom);
        isZoomEnabled = arr.getBoolean(R.styleable.GestureView_gest_zoomEnabled, isZoomEnabled);
        isRotationEnabled = arr.getBoolean(R.styleable.GestureView_gest_rotationEnabled, isRotationEnabled);

        arr.recycle();
    }

    public Settings setViewport(int width, int height) {
        viewportW = width;
        viewportH = height;
        return this;
    }

    public Settings setImage(int width, int height) {
        imageW = width;
        imageH = height;
        return this;
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

    public int getViewportW() {
        return viewportW;
    }

    public int getViewportH() {
        return viewportH;
    }

    public int getImageW() {
        return imageW;
    }

    public int getImageH() {
        return imageH;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public float getDoubleTapZoom() {
        return doubleTapZoom;
    }

    public boolean isZoomEnabled() {
        return isZoomEnabled;
    }

    public boolean isRotationEnabled() {
        return isRotationEnabled;
    }

    public boolean isDoubleTapEnabled() {
        return isZoomEnabled;
    }

    public boolean isEnabled() {
        return isZoomEnabled || isRotationEnabled;
    }

    public boolean hasImageSize() {
        return imageW != 0 && imageH != 0;
    }

    public boolean hasViewportSize() {
        return viewportW != 0 && viewportH != 0;
    }
}
