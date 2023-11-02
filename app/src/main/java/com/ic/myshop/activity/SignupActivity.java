package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.ic.myshop.R;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.User;
import com.ic.myshop.validator.AuthValidator;

public class SignupActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtLogin;
    private ImageButton btnBack;
    private EditText txtEmail, txtPhone, txtPassword, txtPassword2;
    private Button btnSignup;
    private static final AuthService authService = AuthService.getInstance();
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().toLowerCase().trim();
                String phone = txtPhone.getText().toString().trim();
                String password = txtPassword.getText().toString();
                String password2 = txtPassword2.getText().toString();
                if (!AuthValidator.checkEmail(email)) {
                    Toast.makeText(getApplicationContext(),
                            String.format(MessageConstant.ENTER_AGAIN, Constant.EMAIL),
                            Toast.LENGTH_SHORT).show();
                } else if (!AuthValidator.checkPhone(phone)) {
                    Toast.makeText(getApplicationContext(),
                            String.format(MessageConstant.ENTER_AGAIN, Constant.PHONE),
                            Toast.LENGTH_SHORT).show();
                } else if (!AuthValidator.checkPassword(password, password2)) {
                    Toast.makeText(getApplicationContext(),
                            MessageConstant.PASSWORD_NOT_CORRECT,
                            Toast.LENGTH_SHORT).show();
                } else {
                    authService.createUser(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dbFactory.addUser(new User(dbFactory.getUserId(), email, password, phone));
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        Toast.makeText(getApplicationContext(),
                                                MessageConstant.SIGNUP_SUCCESS,
                                                Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                MessageConstant.SIGNUP_FAILURE,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.SIGN_UP);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtLogin = findViewById(R.id.txt_login);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        txtPassword = findViewById(R.id.txt_password);
        txtPassword2 = findViewById(R.id.txt_password2);
        btnSignup = findViewById(R.id.btn_signup);
    }
}