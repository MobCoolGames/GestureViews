package com.alexvasilkov.gestures.animation;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.ImageView;

class ImageViewHelper {

    private static final RectF tmpSrc = new RectF();
    private static final RectF tmpDst = new RectF();

    private ImageViewHelper() {
    }

    /**
     * Helper method to calculate drawing matrix. Based on ImageView source code.
     */
    static void applyScaleType(ImageView.ScaleType type,
                               int dwidth, int dheight,
                               int vwidth, int vheight,
                               Matrix outMatrix) {

        if (ImageView.ScaleType.CENTER == type) {
            // Center bitmap in view, no scaling.
            outMatrix.setTranslate((vwidth - dwidth) * 0.5f,
                    (vheight - dheight) * 0.5f);
        } else if (ImageView.ScaleType.CENTER_CROP == type) {
            float scale;
            float dx = 0;
            float dy = 0;

            if (dwidth * vheight > vwidth * dheight) {
                scale = (float) vheight / (float) dheight;
                dx = (vwidth - dwidth * scale) * 0.5f;
            } else {
                scale = (float) vwidth / (float) dwidth;
                dy = (vheight - dheight * scale) * 0.5f;
            }

            outMatrix.setScale(scale, scale);
            outMatrix.postTranslate(dx, dy);
        } else if (ImageView.ScaleType.CENTER_INSIDE == type) {
            float scale;
            float dx;
            float dy;

            if (dwidth <= vwidth && dheight <= vheight) {
                scale = 1.0f;
            } else {
                scale = Math.min((float) vwidth / (float) dwidth,
                        (float) vheight / (float) dheight);
            }

            dx = (vwidth - dwidth * scale) * 0.5f;
            dy = (vheight - dheight * scale) * 0.5f;

            outMatrix.setScale(scale, scale);
            outMatrix.postTranslate(dx, dy);
        } else {
            Matrix.ScaleToFit scaleToFit = Matrix.ScaleToFit.CENTER;
            // Generate the required transform.
            tmpSrc.set(0, 0, dwidth, dheight);
            tmpDst.set(0, 0, vwidth, vheight);
            outMatrix.setRectToRect(tmpSrc, tmpDst, scaleToFit);
        }
    }
}
