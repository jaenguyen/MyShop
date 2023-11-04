package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ic.myshop.R;
import com.ic.myshop.adapter.CartItemAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Cart;
import com.ic.myshop.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private long totalPrice = 0;
    private TextView txtTotalPrice;
    private TextView toolbarTitle;
    private ImageButton btnBack;
    //empty cart
    private ImageView imageEmptyCart;
    private TextView txtEmptyCart;
    // rcv
    private RecyclerView rcvCartItem;
    private LinearLayoutManager linearLayoutManager;
    private CartItemAdapter cartItemAdapter;
    //db
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        db.collection(DatabaseConstant.CARTS).document("cart_" + dbFactory.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Cart cart = documentSnapshot.toObject(Cart.class);
                            Map<String, Integer> quantityProducts = cart.getQuantityProducts();
                            if (quantityProducts.isEmpty()) {
                                imageEmptyCart.setVisibility(View.VISIBLE);
                                txtEmptyCart.setVisibility(View.VISIBLE);
                            }
                            for (String cartItemId : quantityProducts.keySet()) {
                                db.collection(DatabaseConstant.PRODUCTS).document(cartItemId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    Product product = documentSnapshot.toObject(Product.class);
                                                    cartItemAdapter.addCartItem(product, quantityProducts.get(product.getId()));
                                                } else {
                                                    // TODO: xử lý sản phẩm ko get được
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        cartItemAdapter.setCartItemClickListener(new CartItemAdapter.CartItemClickListener() {
            @Override
            public void onDeleteCartItem(int position) {
                Product product = cartItemAdapter.getProduct(position);
                String id = product.getId();
                dbFactory.deleteCart(id);
                if (cartItemAdapter.isSelected(id)) {
                    int quantity = cartItemAdapter.getQuantity(id);
                    totalPrice -= product.getPrice() * quantity;
                    txtTotalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(totalPrice)));
                }
                cartItemAdapter.deleteCartItem(product);
                if (cartItemAdapter.getItemCount() == 0) {
                    imageEmptyCart.setVisibility(View.VISIBLE);
                    txtEmptyCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCheckboxClick(int position, boolean isChecked) {
                Product product = cartItemAdapter.getProduct(position);
                String id = product.getId();
                int quantity = cartItemAdapter.getQuantity(id);
                if (isChecked) {
                    totalPrice += product.getPrice() * quantity;
                    cartItemAdapter.updateSelected(id, true, false);
                } else {
                    totalPrice -= product.getPrice() * quantity;
                    cartItemAdapter.updateSelected(id, false, true);
                }
                txtTotalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(totalPrice)));
            }

            @Override
            public void onAddButtonClick(int position) {
                Product product = cartItemAdapter.getProduct(position);
                String id = product.getId();
                int currentQuantity = cartItemAdapter.getQuantity(product.getId());
                if (currentQuantity < product.getSellNumber()) {
                    currentQuantity++;
                    dbFactory.updateCart(id, currentQuantity);
                    cartItemAdapter.updateQuantity(id, currentQuantity);
                    if (cartItemAdapter.isSelected(id)) {
                        totalPrice += product.getPrice();
                        txtTotalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(totalPrice)));
                    }
                } else {
                    Toast.makeText(CartActivity.this, String.format(MessageConstant.ADD_WARNING, product.getSellNumber()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRemoveButtonClick(int position) {
                Product product = cartItemAdapter.getProduct(position);
                String id = product.getId();
                int currentQuantity = cartItemAdapter.getQuantity(product.getId());
                if (currentQuantity > 1) {
                    currentQuantity--;
                    dbFactory.addToCart(id, currentQuantity);
                    cartItemAdapter.updateQuantity(id, currentQuantity);
                    if (cartItemAdapter.isSelected(id)) {
                        totalPrice -= product.getPrice();
                        txtTotalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(totalPrice)));
                    }
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.CART);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        //empty
        imageEmptyCart = findViewById(R.id.image_empty_cart);
        txtEmptyCart = findViewById(R.id.txt_empty_cart);
        // rcv
        rcvCartItem = findViewById(R.id.rcv_cart_item);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvCartItem.setLayoutManager(linearLayoutManager);
        cartItemAdapter = new CartItemAdapter(this);
        rcvCartItem.setAdapter(cartItemAdapter);
        //db
        db = FirebaseFirestore.getInstance();
    }
}