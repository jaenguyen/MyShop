package com.ic.myshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private final static String MAP = "map";
    private final static String IS_FIRST_TIME = "isFirstTime";

    private Button start_btn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();

        Intent intent = new Intent(this, MainActivity.class);
        if (isFirstTime()) {
            start_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                    finish();
                }
            });
            updateFirstTime();
        } else {
            startActivity(intent);
            finish();
        }
    }

    private void init() {
        start_btn = findViewById(R.id.start_btn);
        sharedPreferences = getSharedPreferences(MAP, MODE_PRIVATE);
    }

    private boolean isFirstTime() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }

    private void updateFirstTime() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME, false);
        editor.apply();
    }
}