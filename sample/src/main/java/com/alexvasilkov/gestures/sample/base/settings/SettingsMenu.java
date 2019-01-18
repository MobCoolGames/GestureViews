package com.alexvasilkov.gestures.sample.base.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexvasilkov.android.commons.state.InstanceState;
import com.alexvasilkov.android.commons.state.InstanceStateManager;
import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.sample.R;
import com.alexvasilkov.gestures.views.interfaces.GestureView;

import androidx.annotation.StringRes;

public class SettingsMenu implements SettingsController {

    @InstanceState
    private boolean isZoomEnabled = true;
    @InstanceState
    private boolean isRotationEnabled = false;
    @InstanceState
    private boolean isRestrictRotation = false;
    @InstanceState
    private boolean isOverzoomEnabled = true;

    public void setValuesFrom(Settings settings) {
        isZoomEnabled = settings.isZoomEnabled();
        isRotationEnabled = settings.isRotationEnabled();
        isRestrictRotation = settings.isRestrictRotation();
    }

    public void onSaveInstanceState(Bundle outState) {
        InstanceStateManager.saveInstanceState(this, outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        InstanceStateManager.restoreInstanceState(this, savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu) {
        addBoolMenu(menu, isZoomEnabled, R.string.menu_enable_zoom);
        addBoolMenu(menu, isRotationEnabled, R.string.menu_enable_rotation);
        addBoolMenu(menu, isRestrictRotation, R.string.menu_restrict_rotation);
        addBoolMenu(menu, isOverzoomEnabled, R.string.menu_enable_overzoom);
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
            case R.string.menu_restrict_rotation:
                isRestrictRotation = !isRestrictRotation;
                break;
            case R.string.menu_enable_overzoom:
                isOverzoomEnabled = !isOverzoomEnabled;
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public void apply(GestureView view) {
        float overzoom = isOverzoomEnabled ? Settings.OVERZOOM_FACTOR : 1f;

        view.getController().getSettings()
                .setZoomEnabled(isZoomEnabled)
                .setRotationEnabled(isRotationEnabled)
                .setRestrictRotation(isRestrictRotation)
                .setOverzoomFactor(overzoom);
    }
}
