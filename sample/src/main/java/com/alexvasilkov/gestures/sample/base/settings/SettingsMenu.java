package com.alexvasilkov.gestures.sample.base.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexvasilkov.android.commons.state.InstanceState;
import com.alexvasilkov.android.commons.state.InstanceStateManager;
import com.alexvasilkov.gestures.sample.R;
import com.alexvasilkov.gestures.GestureView;

import androidx.annotation.StringRes;

public class SettingsMenu implements SettingsController {

    @InstanceState
    private boolean isZoomEnabled = true;
    @InstanceState
    private boolean isRotationEnabled = false;

    public void onSaveInstanceState(Bundle outState) {
        InstanceStateManager.saveInstanceState(this, outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        InstanceStateManager.restoreInstanceState(this, savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu) {
        addBoolMenu(menu, isZoomEnabled, R.string.menu_enable_zoom);
        addBoolMenu(menu, isRotationEnabled, R.string.menu_enable_rotation);
    }

    private void addBoolMenu(Menu menu, boolean checked, @StringRes int titleId) {
        MenuItem item = menu.add(Menu.NONE, titleId, 0, titleId);
        item.setCheckable(true);
        item.setChecked(checked);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.menu_enable_zoom:
                isZoomEnabled = !isZoomEnabled;
                break;
            case R.string.menu_enable_rotation:
                isRotationEnabled = !isRotationEnabled;
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public void apply(GestureView view) {
        view.getController().getSettings()
                .setZoomEnabled(isZoomEnabled)
                .setRotationEnabled(isRotationEnabled);
    }
}
