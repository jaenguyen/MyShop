package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.ic.myshop.R;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.validator.AuthValidator;

public class LoginActivity extends AppCompatActivity {

    private TextView txtSignup, txtForgotPassword;
    private EditText txtEmail, txtPassword;
    private Button btnLogin;
    private static final AuthService authService = AuthService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().toLowerCase().trim();
                String password = txtPassword.getText().toString().trim();
                if (!AuthValidator.checkEmail(email)) {
                    Toast.makeText(getApplicationContext(),
                            String.format(MessageConstant.ENTER_AGAIN, Constant.EMAIL),
                            Toast.LENGTH_SHORT).show();
                } else if (AuthValidator.isNone(password)) {
                    Toast.makeText(getApplicationContext(),
                            String.format(MessageConstant.ENTER_AGAIN, Constant.PASSWORD),
                            Toast.LENGTH_SHORT).show();
                } else {
                    authService.login(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        Toast.makeText(getApplicationContext(),
                                                MessageConstant.LOGIN_SUCCESS,
                                                Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                MessageConstant.EMAIL_PASSWORD_NOT_CORRECT,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void init() {
        txtSignup = findViewById(R.id.txt_signup);
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    @Override
    public void onBackPressed() {
        // TODO: Xử lý out ra màn hình chính
    }
}