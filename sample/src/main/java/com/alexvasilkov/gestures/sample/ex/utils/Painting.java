package com.alexvasilkov.gestures.sample.ex.utils;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.alexvasilkov.gestures.sample.R;

public class Painting {
    public final int imageId;
    public final int thumbId;

    private Painting(int imageId, int thumbId) {
        this.imageId = imageId;
        this.thumbId = thumbId;
    }

    public static Painting[] list(Resources res) {
        final String[] titles = res.getStringArray(R.array.paintings_titles);
        final TypedArray images = res.obtainTypedArray(R.array.paintings_images);
        final TypedArray thumbs = res.obtainTypedArray(R.array.paintings_thumbs);

        final int size = titles.length;
        final Painting[] paintings = new Painting[size];

        for (int i = 0; i < size; i++) {
            final int imageId = images.getResourceId(i, -1);
            final int thumbId = thumbs.getResourceId(i, -1);
            paintings[i] = new Painting(imageId, thumbId);
        }

        images.recycle();
        thumbs.recycle();

        return paintings;
    }
}
