package com.alexvasilkov.gestures.sample.ex.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object GlideHelper {
    fun loadFull(image: ImageView, imageId: Int, thumbId: Int) {
        // We don't want Glide to crop or resize our image
        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL)
                .dontTransform()

        val thumbRequest = Glide.with(image)
                .load(thumbId)
                .apply(options)

        Glide.with(image)
                .load(imageId)
                .apply(options)
                .thumbnail(thumbRequest)
                .into(image)
    }
}
