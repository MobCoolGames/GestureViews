package com.alexvasilkov.gestures.views.interfaces;

import android.graphics.RectF;

import androidx.annotation.Nullable;

public interface ClipView {
    void clipView(@Nullable RectF rect, float rotation);
}
