package com.ic.myshop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ic.myshop.R;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;

public class AccountSettingActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private Button btnLogout;
    private static final AuthService authService = AuthService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authService.logout();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ACCOUNT_SETTING);
        btnBack = findViewById(R.id.toolbar_back_button);
        btnLogout = findViewById(R.id.btn_logout);
    }
}