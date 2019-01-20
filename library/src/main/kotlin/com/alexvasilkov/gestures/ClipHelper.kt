package com.alexvasilkov.gestures

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.view.View

class ClipHelper(private val view: View) : ClipView {
    companion object {
        private val tmpMatrix = Matrix()
    }

    private var isClipping = false
    private val clipRect = RectF()
    private var clipRotation = 0f
    private val clipBounds = RectF()
    private val clipBoundsOld = RectF()

    override fun clipView(rect: RectF?, rotation: Float) {
        if (rect == null) {
            if (isClipping) {
                isClipping = false
                view.invalidate()
            }
        } else {
            if (isClipping) {
                clipBoundsOld.set(clipBounds)
            } else {
                clipBoundsOld.set(0f, 0f, view.width.toFloat(), view.height.toFloat())
            }

            isClipping = true

            clipRect.set(rect)
            clipRotation = rotation

            clipBounds.set(clipRect)
            if (!State.equals(rotation, 0f)) {
                tmpMatrix.setRotate(rotation, clipRect.centerX(), clipRect.centerY())
                tmpMatrix.mapRect(clipBounds)
            }

            val left = Math.min(clipBounds.left, clipBoundsOld.left).toInt()
            val top = Math.min(clipBounds.top, clipBoundsOld.top).toInt()
            val right = Math.max(clipBounds.right, clipBoundsOld.right).toInt() + 1
            val bottom = Math.max(clipBounds.bottom, clipBoundsOld.bottom).toInt() + 1
            view.invalidate(left, top, right, bottom)
        }
    }

    fun onPreDraw(canvas: Canvas) {
        if (isClipping) {
            canvas.save()

            if (State.equals(clipRotation, 0f)) {
                canvas.clipRect(clipRect)
            } else {
                canvas.rotate(clipRotation, clipRect.centerX(), clipRect.centerY())
                canvas.clipRect(clipRect)
                canvas.rotate(-clipRotation, clipRect.centerX(), clipRect.centerY())
            }
        }
    }

    fun onPostDraw(canvas: Canvas) {
        if (isClipping) {
            canvas.restore()
        }
    }
}
