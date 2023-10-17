package com.ic.myshop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ic.myshop.R;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;

public class StartActivity extends AppCompatActivity {

    private Button start_btn;
    private SharedPreferences sharedPreferences;
    private static final AuthService authService = AuthService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();

        Intent intent;
        if (authService.isLogin()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
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
        sharedPreferences = getSharedPreferences(Constant.MAP, MODE_PRIVATE);
    }

    private boolean isFirstTime() {
        return sharedPreferences.getBoolean(Constant.IS_FIRST_TIME, true);
    }

    private void updateFirstTime() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.IS_FIRST_TIME, false);
        editor.apply();
    }
}