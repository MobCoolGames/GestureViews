package com.alexvasilkov.gestures.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import com.alexvasilkov.android.commons.state.InstanceState
import com.alexvasilkov.android.commons.state.InstanceStateManager
import com.alexvasilkov.gestures.GestureView

class SettingsMenu : SettingsController {
    @InstanceState
    private var isZoomEnabled = true
    @InstanceState
    private var isRotationEnabled = false

    fun onSaveInstanceState(outState: Bundle) {
        InstanceStateManager.saveInstanceState(this, outState)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        InstanceStateManager.restoreInstanceState(this, savedInstanceState)
    }

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

    override fun apply(view: GestureView) {
        val settings = view.controller.settings
        settings.isZoomEnabled = isZoomEnabled
        settings.isRotationEnabled = isRotationEnabled
    }
}
