package com.alexvasilkov.gestures.sample.ex.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class GlideHelper {

    private GlideHelper() {
    }

    /**
     * Loads thumbnail and then replaces it with full image.
     */
    public static void loadFull(ImageView image, int imageId, int thumbId) {
        // We don't want Glide to crop or resize our image
        final RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL)
                .dontTransform();

        final RequestBuilder<Drawable> thumbRequest = Glide.with(image)
                .load(thumbId)
                .apply(options);

        Glide.with(image)
                .load(imageId)
                .apply(options)
                .thumbnail(thumbRequest)
                .into(image);
    }
}
