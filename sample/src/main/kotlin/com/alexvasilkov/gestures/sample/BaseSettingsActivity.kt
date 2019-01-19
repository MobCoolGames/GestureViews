package com.alexvasilkov.gestures.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

abstract class BaseSettingsActivity : BaseActivity() {
    private val settingsMenu = SettingsMenu()

    protected val settingsController: SettingsController
        get() = settingsMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsMenu.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        settingsMenu.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

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
