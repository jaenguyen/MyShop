package com.ic.myshop.activity.sales_order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.usecase.ProductActivity;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.constant.Payment;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ApiService;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Product;
import com.ic.myshop.push.MessagePush;
import com.ic.myshop.output.OrderOutput;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailConfirmSOrderMainActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtNameAddress, txtPhoneAddress, txtStreetAddress, txtName,
            txtPrice, txtQuantity, totalPrice, txtId, txtCreatedTime, txtPaymentForm;
    private ImageButton btnBack;
    private ImageView imageView;
    private Button btnCancel;
    private OrderOutput orderOutput;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_confirm_sales_order_main);
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
        txtCreatedTime.setText(ConversionHelper.formatDateTime(orderOutput.getCreatedTime()));
        txtPaymentForm.setText(Payment.valueOf(orderOutput.getPayment()));
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
                int status = 1;
                dbFactory.updateStatusOrder(orderOutput.getId(), status);
                ApiService.apiService2.push(MessagePush.getParams(orderOutput.getParentId(), orderOutput.getId(), status))
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                System.out.println(response.body());
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                System.out.println(t.getMessage());
                            }
                        });
                Toast.makeText(DetailConfirmSOrderMainActivity.this, MessageConstant.ORDER_TO_DELIVERY, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productId = orderOutput.getProductId();
                dbFactory.getProduct(productId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                        intent.putExtra("product", task.getResult().toObject(Product.class));
                        startActivity(intent);
                    }
                });
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
        txtPaymentForm = findViewById(R.id.txt_payment_form);
        imageView = findViewById(R.id.image_view);
        btnCancel = findViewById(R.id.btn_cancel);
    }
}