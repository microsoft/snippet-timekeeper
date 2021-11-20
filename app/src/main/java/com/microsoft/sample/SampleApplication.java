package com.microsoft.sample;

import android.app.Application;

import com.microsoft.snippet.Snippet;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Snippet.install(new Snippet.MeasuredExecutionPath());
            Snippet.newFilter("SampleFilter");
            Snippet.addFlag(Snippet.FLAG_METADATA_LINE | Snippet.FLAG_METADATA_THREAD_INFO);
        }

        // There are cases where the code flow is spread across different files,
        // in those kind of scenarios we use tag based startCapture call.
        // We can then use find() with the tag and end the capture.
        // It handles the case, when start capture is not called in warm launches and find() and endCapture()
        // are called. In those case the calls to find() and endCapture() are no-op.
        Snippet.startCapture("app_start"); // Start the measurement in Application class
    }
}
