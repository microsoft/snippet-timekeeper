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
        Snippet.startCapture("app_start");
    }
}
