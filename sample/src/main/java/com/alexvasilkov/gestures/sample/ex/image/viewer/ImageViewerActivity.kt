package com.alexvasilkov.gestures.sample.ex.image.viewer

import android.os.Bundle
import android.widget.Toast
import com.alexvasilkov.gestures.sample.R
import com.alexvasilkov.gestures.sample.base.BaseSettingsActivity
import com.alexvasilkov.gestures.sample.ex.utils.GlideHelper
import com.alexvasilkov.gestures.sample.ex.utils.Painting
import kotlinx.android.synthetic.main.image_viewer_screen.*

class ImageViewerActivity : BaseSettingsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.image_viewer_screen)
        setInfoText(R.string.info_image_viewer)

        val settings = image_viewer.controller.settings
        settings.maxZoom = 6f
        settings.doubleTapZoom = 3f

        image_viewer.setOnClickListener {
            showToast("Single click")
        }

        val painting = Painting(R.drawable.painting_01, R.drawable.painting_thumb_01)
        GlideHelper.loadFull(image_viewer, painting.imageId, painting.thumbId)
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onSettingsChanged() {
        settingsController.apply(image_viewer)
        image_viewer.controller.resetState()
    }
}
