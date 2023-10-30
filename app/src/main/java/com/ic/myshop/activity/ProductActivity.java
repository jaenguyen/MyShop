package com.ic.myshop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;

public class ProductActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.PRODUCT_DETAIL);
        btnBack = findViewById(R.id.toolbar_back_button);
    }
}