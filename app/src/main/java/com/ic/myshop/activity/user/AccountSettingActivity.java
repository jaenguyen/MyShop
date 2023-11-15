package com.ic.myshop.activity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ic.myshop.R;
import com.ic.myshop.activity.setting.AddressActivity;
import com.ic.myshop.activity.setting.UserInfoActivity;
import com.ic.myshop.activity.func.LoginActivity;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;

public class AccountSettingActivity extends AppCompatActivity {

    private TextView toolbarTitle, accPriFunc, addressFunc, creditFunc;
    private ImageButton btnBack;
    private Button btnLogout;
    private static final AuthService authService = AuthService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        init();

        accPriFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        addressFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
                startActivity(intent);
            }
        });

        creditFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AccountSettingActivity.this, MessageConstant.NOT_HAVE_FUNCTION, Toast.LENGTH_SHORT).show();
            }
        });


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
        accPriFunc = findViewById(R.id.acc_pri_func);
        addressFunc = findViewById(R.id.address_func);
        creditFunc = findViewById(R.id.credit_func);
    }
}