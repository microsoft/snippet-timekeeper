package com.microsoft.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.snippet.Snippet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Snippet.find("app_start").endCapture();
    }
}