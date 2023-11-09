package com.ic.myshop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.output.OrderOutput;

public class DetailConfirmOrderMainActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtNameAddress, txtPhoneAddress, txtStreetAddress, txtName,
            txtPrice, txtQuantity, totalPrice, txtId, txtCreatedTime;
    private ImageButton btnBack;
    private ImageView imageView;
    private Button btnCancel;
    private OrderOutput orderOutput;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_confirm_order_main);
        orderOutput = (OrderOutput) getIntent().getSerializableExtra("order");
        init();
        txtNameAddress.setText(String.format("Họ và tên: %s", orderOutput.getAddress().getName()));
        txtPhoneAddress.setText(String.format("Số điện thoại: %s", orderOutput.getAddress().getPhone()));
        txtStreetAddress.setText(String.format("Địa chỉ: %s", orderOutput.getAddress().getStreet()));
        txtName.setText(orderOutput.getNameProduct());
        txtPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(orderOutput.getPrice())));
        txtQuantity.setText(String.format("x%d", orderOutput.getQuantity()));
        totalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(orderOutput.getTotalPrice())));
        txtId.setText(orderOutput.getId());
        txtCreatedTime.setText(ConversionHelper.formatDate(orderOutput.getCreatedTime()));
        Glide.with(this).load(orderOutput.getImageUrl()).into(imageView);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbFactory.updateStatusOrder(orderOutput.getId(), 1);
                onBackPressed();
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.ORDER_DETAIL);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtNameAddress = findViewById(R.id.txt_name_address);
        txtPhoneAddress = findViewById(R.id.txt_phone_address);
        txtStreetAddress = findViewById(R.id.txt_street_address);
        txtName = findViewById(R.id.txt_name);
        txtPrice = findViewById(R.id.txt_price);
        txtQuantity = findViewById(R.id.txt_quantity);
        totalPrice = findViewById(R.id.total_price);
        txtId = findViewById(R.id.txt_id);
        txtCreatedTime = findViewById(R.id.txt_createdTime);
        imageView = findViewById(R.id.image_view);
        btnCancel = findViewById(R.id.btn_cancel);
    }
}