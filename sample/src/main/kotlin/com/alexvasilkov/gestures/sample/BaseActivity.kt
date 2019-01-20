package com.alexvasilkov.gestures.sample

import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    private var infoTextId = 0

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
        val layout = layoutInflater.inflate(R.layout.info_dialog, null)
        layout.findViewById<TextView>(R.id.info_text).text = getText(infoTextId)

        AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .show()
    }
}
