package com.alexvasilkov.gestures.sample

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import com.alexvasilkov.gestures.GestureImageView

class SettingsMenu : SettingsController {
    private var isZoomEnabled = true
    private var isRotationEnabled = false

    fun onCreateOptionsMenu(menu: Menu) {
        addBoolMenu(menu, isZoomEnabled, R.string.menu_enable_zoom)
        addBoolMenu(menu, isRotationEnabled, R.string.menu_enable_rotation)
    }

    private fun addBoolMenu(menu: Menu, checked: Boolean, @StringRes titleId: Int) {
        val item = menu.add(Menu.NONE, titleId, 0, titleId)
        item.isCheckable = true
        item.isChecked = checked
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.string.menu_enable_zoom -> isZoomEnabled = !isZoomEnabled
            R.string.menu_enable_rotation -> isRotationEnabled = !isRotationEnabled
            else -> return false
        }

        return true
    }

    override fun apply(view: GestureImageView) {
        val settings = view.controller.settings
        settings.isZoomEnabled = isZoomEnabled
        settings.isRotationEnabled = isRotationEnabled
    }
}
