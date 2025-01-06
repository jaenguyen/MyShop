package com.ic.myshop.activity.func;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ic.myshop.R;
import com.ic.myshop.auth.AuthService;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.validator.AuthValidator;

public class ForgotActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private EditText txtEmail;
    private Button btnResetPassword;
    private static final AuthService authService = AuthService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().toLowerCase().trim();
                if (!AuthValidator.checkEmail(email)) {
                    Toast.makeText(getApplicationContext(),
                            String.format(MessageConstant.ENTER_AGAIN, Constant.EMAIL),
                            Toast.LENGTH_SHORT).show();
                } else {
                    authService.resetPassword(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),
                                                MessageConstant.CHECK_EMAIL_RESET_PASSWORD,
                                                Toast.LENGTH_SHORT).show();
                                    } else {

                                    }
                                }
                            });
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.FORGOT_PASSWORD);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtEmail = findViewById(R.id.txt_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
    }
}