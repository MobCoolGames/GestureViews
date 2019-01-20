package com.alexvasilkov.gestures

import android.content.Context
import android.util.AttributeSet

class Settings {
    companion object {
        private const val MAX_ZOOM = 5f
        private const val DOUBLE_TAP_ZOOM = 3f
        const val OVERZOOM_FACTOR = 2f
        const val ANIMATIONS_DURATION = 300L
    }

    var viewportWidth = 0
    var viewportHeight = 0
    var imageWidth = 0f
    var imageHeight = 0f
    var maxZoom = MAX_ZOOM
    var doubleTapZoom = DOUBLE_TAP_ZOOM
    var isZoomEnabled = true
    var isRotationEnabled = false

    fun getIsEnabled() = isZoomEnabled || isRotationEnabled

    fun initFromAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }

        context.obtainStyledAttributes(attrs, R.styleable.GestureImageView).apply {
            maxZoom = getFloat(R.styleable.GestureImageView_gest_maxZoom, maxZoom)
            doubleTapZoom = getFloat(R.styleable.GestureImageView_gest_doubleTapZoom, doubleTapZoom)
            isZoomEnabled = getBoolean(R.styleable.GestureImageView_gest_zoomEnabled, isZoomEnabled)
            isRotationEnabled = getBoolean(R.styleable.GestureImageView_gest_rotationEnabled, isRotationEnabled)
            recycle()
        }
    }

    fun setViewport(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
    }

    fun setImage(width: Float, height: Float) {
        imageWidth = width
        imageHeight = height
    }

    fun isDoubleTapEnabled() = isZoomEnabled

    fun hasImageSize() = imageWidth != 0f && imageHeight != 0f

    fun hasViewportSize() = viewportWidth != 0 && viewportHeight != 0
}
