package com.alexvasilkov.gestures

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.alexvasilkov.gestures.views.interfaces.ClipView

class GestureImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle), ClipView {
    private val clipViewHelper = ClipHelper(this)
    private val clipBoundsHelper = ClipHelper(this)
    private val imageViewMatrix = Matrix()
    var controller = GestureController(this)

    init {
        controller.settings.initFromAttributes(context, attrs)
        controller.addOnStateChangeListener(object : GestureController.OnStateChangeListener {
            override fun onStateChanged(state: State) {
                applyState(state)
            }

            override fun onStateReset(oldState: State, newState: State) {
                applyState(newState)
            }
        })

        scaleType = ImageView.ScaleType.MATRIX
    }

    override fun draw(canvas: Canvas) {
        clipBoundsHelper.onPreDraw(canvas)
        clipViewHelper.onPreDraw(canvas)
        super.draw(canvas)
        clipViewHelper.onPostDraw(canvas)
        clipBoundsHelper.onPostDraw(canvas)
    }

    override fun clipView(rect: RectF?, rotation: Float) {
        clipViewHelper.clipView(rect, rotation)
    }

    override fun onTouchEvent(event: MotionEvent) = controller.onTouch(this, event)

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        controller.settings.setViewport(width - paddingLeft - paddingRight, height - paddingTop - paddingBottom)
        controller.resetState()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        val settings = controller.settings
        val oldWidth = settings.imageWidth.toFloat()
        val oldHeight = settings.imageHeight.toFloat()

        if (drawable == null) {
            settings.setImage(0, 0)
        } else if (drawable.intrinsicWidth == -1 || drawable.intrinsicHeight == -1) {
            settings.setImage(settings.viewportWidth, settings.viewportHeight)
        } else {
            settings.setImage(drawable.intrinsicWidth, drawable.intrinsicHeight)
        }

        val newWidth = settings.imageWidth.toFloat()
        val newHeight = settings.imageHeight.toFloat()

        if (newWidth > 0f && newHeight > 0f && oldWidth > 0f && oldHeight > 0f) {
            val scaleFactor = Math.min(oldWidth / newWidth, oldHeight / newHeight)
            controller.stateController.setTempZoomPatch(scaleFactor)
            controller.updateState()
            controller.stateController.setTempZoomPatch(0f)
        } else {
            controller.resetState()
        }
    }

    private fun applyState(state: State) {
        state[imageViewMatrix]
        imageMatrix = imageViewMatrix
    }
}
