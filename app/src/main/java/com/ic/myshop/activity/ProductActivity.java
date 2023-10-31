package com.ic.myshop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class ProductActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtName, txtPrice, txtSoldNumber, txtType, txtDescription;
    private ImageButton btnBack, btnAddToCart;
    private ImageView imageView;
    private Product product;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        init();

        product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            Glide.with(getApplicationContext())
                    .load(product.getImageUrl())
                    .fitCenter()
                    .into(imageView);
            txtName.setText(product.getName());
            txtPrice.setText(String.format("₫ %d", product.getPrice()));
            txtSoldNumber.setText(String.format("Đã bán %d", product.getSoldNumber()));
            txtType.setText(product.getType());
            txtDescription.setText(product.getDescription());
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbFactory.addToCart(product.getId(), 1);
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.PRODUCT_DETAIL);
        btnBack = findViewById(R.id.toolbar_back_button);
        imageView = findViewById(R.id.image_view);
        txtName = findViewById(R.id.txt_name);
        txtPrice = findViewById(R.id.txt_price);
        txtSoldNumber = findViewById(R.id.txt_sold_number);
        txtType = findViewById(R.id.txt_type);
        txtDescription = findViewById(R.id.txt_description);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
    }
}