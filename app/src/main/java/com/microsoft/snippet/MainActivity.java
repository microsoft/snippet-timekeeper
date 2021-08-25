package com.microsoft.snippet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Snippet.install(new Snippet.MeasuredExecutionPath());
        Snippet.capture(() -> {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        });

    }
}