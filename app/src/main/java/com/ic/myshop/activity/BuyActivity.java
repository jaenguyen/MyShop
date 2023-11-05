package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ic.myshop.R;
import com.ic.myshop.adapter.BuyItemAdapter;
import com.ic.myshop.adapter.CartItemAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.fragment.UserFragment;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;
import com.ic.myshop.output.BuyItem;

import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Map;

public class BuyActivity extends AppCompatActivity {

    private long totalPrice = 0;
    private TextView txtTotalPrice, btnBuy;
    private TextView toolbarTitle;
    private TextView txtNameAddress, txtPhoneAddress, txtStreetAddress;
    private ImageButton btnBack;
    // rcv
    private RecyclerView rcvBuyItem;
    private LinearLayoutManager linearLayoutManager;
    private BuyItemAdapter buyItemAdapter;
    // db
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();
    private User user;
    private Address address;
    private Map<String, Integer> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        init();

        cartItems = (Map<String, Integer>) getIntent().getSerializableExtra("cartItems");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        db.collection("users").document(dbFactory.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            user = documentSnapshot.toObject(User.class);
                            List<Address> addresses = user.getAddresses();
                            if (addresses == null || addresses.isEmpty()) {
                                // Tạo một Dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
                                // Inflate layout cho dialog từ tệp XML
                                View dialogView = getLayoutInflater().inflate(R.layout.new_address_dialog, null);
                                builder.setView(dialogView);
                                // Khởi tạo các thành phần trong dialog
                                TextView txtName = dialogView.findViewById(R.id.txt_name);
                                TextView txtPhone = dialogView.findViewById(R.id.txt_phone);
                                TextView txtStreet = dialogView.findViewById(R.id.txt_street);
                                Button btnAddAddress = dialogView.findViewById(R.id.btn_add_address);
                                // Tạo và hiển thị dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                // Thiết lập sự kiện cho nút add, remove
                                btnAddAddress.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = txtName.getText().toString().trim();
                                        String phone = txtPhone.getText().toString().trim();
                                        String street = txtStreet.getText().toString();
                                        Address address = new Address(name, phone, street);
                                        txtNameAddress.setText(String.format("Họ tên: %s", address.getName()));
                                        txtPhoneAddress.setText(String.format("Số điện thoại: %s", address.getPhone()));
                                        txtStreetAddress.setText(String.format("Địa chỉ: %s", address.getStreet()));
                                        user.addAddress(address);
                                        dbFactory.updateAddresses(user);
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                Address address = user.getAddresses().get(0);
                                txtNameAddress.setText(String.format("Họ tên: %s", address.getName()));
                                txtPhoneAddress.setText(String.format("Số điện thoại: %s", address.getPhone()));
                                txtStreetAddress.setText(String.format("Địa chỉ: %s", address.getStreet()));
                            }
                        }
                    }
                });

        for (String id : cartItems.keySet()) {
            db.collection("products").document(id)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                Product product = documentSnapshot.toObject(Product.class);
                                int quantity = cartItems.get(id);
                                BuyItem buyItem = new BuyItem(product.getId(), product.getImageUrl(), product.getName(), product.getPrice(), product.getParentId());
                                buyItem.setQuantity(quantity);
                                buyItemAdapter.addProduct(buyItem);
                                totalPrice += product.getPrice() * quantity;
                                txtTotalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(totalPrice)));
                            }
                        }
                    });
        }

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<BuyItem> buyItems = buyItemAdapter.getCartItems();
                for (BuyItem buyItem : buyItems) {
                    dbFactory.buyProduct(buyItem);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.BUY);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        btnBuy = findViewById(R.id.btn_buy);
        // db
        db = FirebaseFirestore.getInstance();
        txtNameAddress = findViewById(R.id.txt_name_address);
        txtPhoneAddress = findViewById(R.id.txt_phone_address);
        txtStreetAddress = findViewById(R.id.txt_street_address);
        // rcv
        rcvBuyItem = findViewById(R.id.rcv_buy_item);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvBuyItem.setLayoutManager(linearLayoutManager);
        buyItemAdapter = new BuyItemAdapter(this);
        rcvBuyItem.setAdapter(buyItemAdapter);
    }
}