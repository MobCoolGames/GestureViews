package com.alexvasilkov.gestures.sample

import android.view.Menu
import android.view.MenuItem

abstract class BaseSettingsActivity : BaseActivity() {
    val settingsMenu = SettingsMenu()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        settingsMenu.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (settingsMenu.onOptionsItemSelected(item)) {
            supportInvalidateOptionsMenu()
            onSettingsChanged()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    protected abstract fun onSettingsChanged()
}
