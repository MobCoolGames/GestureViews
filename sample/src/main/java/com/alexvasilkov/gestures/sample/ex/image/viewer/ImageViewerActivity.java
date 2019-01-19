package com.alexvasilkov.gestures.sample.ex.image.viewer;

import android.os.Bundle;
import android.widget.Toast;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.sample.R;
import com.alexvasilkov.gestures.sample.base.BaseSettingsActivity;
import com.alexvasilkov.gestures.sample.ex.utils.GlideHelper;
import com.alexvasilkov.gestures.sample.ex.utils.Painting;
import com.alexvasilkov.gestures.views.GestureImageView;

public class ImageViewerActivity extends BaseSettingsActivity {

    private static final int PAINTING_ID = 1;

    private GestureImageView imageViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_viewer_screen);
        setInfoText(R.string.info_image_viewer);

        imageViewer = findViewById(R.id.image_viewer);

        Settings settings = imageViewer.getController().getSettings();
        settings.setMaxZoom(6f);
        settings.setDoubleTapZoom(3f);

        imageViewer.setOnClickListener(view -> showToast("Single click"));

        final Painting painting = Painting.list(getResources())[PAINTING_ID];
        GlideHelper.loadFull(imageViewer, painting.imageId, painting.thumbId);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSettingsChanged() {
        getSettingsController().apply(imageViewer);
        imageViewer.getController().resetState();
    }
}
