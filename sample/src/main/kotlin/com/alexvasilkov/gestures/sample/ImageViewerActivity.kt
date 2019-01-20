package com.alexvasilkov.gestures.sample

import android.os.Bundle
import android.widget.Toast
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

        GlideHelper.loadFull(image_viewer, R.drawable.painting_01, R.drawable.painting_thumb_01)
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onSettingsChanged() {
        settingsMenu.apply(image_viewer)
        image_viewer.controller.resetState()
    }
}
