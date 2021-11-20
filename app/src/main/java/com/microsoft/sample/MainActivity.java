package com.microsoft.sample;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.snippet.Snippet;
import com.microsoft.snippet.token.ILogToken;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // The capture API can be used to measure the code that can be passed as a lambda.
        // Adding this lambda captures the class, line, thread etc automatically into the logcat.
        // This cannot be use for code that returns a value as the lambda declared for closure is
        // is a non returning lambda. For the case that could return a value and are a little complex
        // use the log-token based API demonstrated below.
        Snippet.capture(()-> super.onCreate(savedInstanceState)); // Captures the code as a lambda.

        // Calling startCapture gives a log token to the caller that can we used to end the measurement.
        // The moment start capture is called, the measurement has started and when end capture will
        // be called on the log-token, that is when the measurement will end. Endcapture can be called only once
        // per log token.
        ILogToken token = Snippet.startCapture();
        setContentView(R.layout.activity_main);
        token.endCapture("Time to set the content view");


        Snippet.find("app_start").endCapture(); // End the measurement that started in the Application class

    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}