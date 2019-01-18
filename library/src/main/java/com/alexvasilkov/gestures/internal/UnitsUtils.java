package com.alexvasilkov.gestures.internal;

import android.content.Context;
import android.util.TypedValue;

public class UnitsUtils {

    private UnitsUtils() {
    }

    public static float toPixels(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
}
