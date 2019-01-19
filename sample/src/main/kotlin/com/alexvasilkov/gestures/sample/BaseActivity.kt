package com.alexvasilkov.gestures.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alexvasilkov.android.commons.state.InstanceStateManager
import com.alexvasilkov.android.commons.ui.Views
import com.alexvasilkov.events.Events

abstract class BaseActivity : AppCompatActivity() {
    private var infoTextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InstanceStateManager.restoreInstanceState(this, savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Events.register(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        InstanceStateManager.saveInstanceState(this, outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Events.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (infoTextId != 0) {
            val item = menu.add(Menu.NONE, R.id.menu_info, Menu.NONE, R.string.menu_info)
            item.setIcon(R.drawable.ic_info_outline_white_24dp)
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_info -> {
                showInfoDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun setInfoText(@StringRes textId: Int) {
        infoTextId = textId
        invalidateOptionsMenu()
    }

    private fun showInfoDialog() {
        val layout = Views.inflate<View>(this, R.layout.info_dialog)
        val text = layout.findViewById<TextView>(R.id.info_text)
        text.text = getText(infoTextId)

        AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .show()
    }
}
