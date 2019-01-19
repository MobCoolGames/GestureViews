package com.alexvasilkov.gestures.sample.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.alexvasilkov.gestures.sample.base.settings.SettingsController
import com.alexvasilkov.gestures.sample.base.settings.SettingsMenu

abstract class BaseSettingsActivity : BaseActivity() {

    private val settingsMenu = SettingsMenu()

    protected val settingsController: SettingsController
        get() = settingsMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsMenu.onRestoreInstanceState(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBarNotNull.setDisplayHomeAsUpEnabled(true)
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
