package com.alexvasilkov.gestures.sample;

import android.app.Application;

import com.alexvasilkov.gestures.internal.GestureDebug;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GestureDebug.setDebugFps(BuildConfig.DEBUG);
        GestureDebug.setDebugAnimator(BuildConfig.DEBUG);
    }
}
