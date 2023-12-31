package com.ic.myshop.activity.usecase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.product.CartItemAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Cart;
import com.ic.myshop.model.Product;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private long totalPrice = 0;
    private TextView txtTotalPrice, btnBuy, toolbarTitle, txtEmptyCart;
    private ImageButton btnBack;
    //empty cart
    private ImageView imageEmptyCart;
    // rcv
    private RecyclerView rcvCartItem;
    private LinearLayoutManager linearLayoutManager;
    private CartItemAdapter cartItemAdapter;
    //db
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

        dbFactory.getCart(dbFactory.getUserId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                dbFactory.getProduct(cartItemId)
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

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selected = cartItemAdapter.getSelected();
                if (selected == null || selected.isEmpty()) {
                    Toast.makeText(CartActivity.this, MessageConstant.NOT_SELECTED_PRODUCT, Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Integer> cartItemBuy= cartItemAdapter.getBuyProducts(selected);
                    for (String productId : cartItemBuy.keySet()) {
                        Product product = cartItemAdapter.getProduct(productId);
                        if (cartItemBuy.get(productId) > product.getSellNumber()) {
                            Toast.makeText(CartActivity.this, String.format(MessageConstant.NOT_ENOUGH_QUANTITY, product.getName()), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), BuyActivity.class);
                    intent.putExtra(InputParam.CART_ITEMS, (Serializable) cartItemBuy);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.CART);
        btnBack = findViewById(R.id.toolbar_back_button);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        btnBuy = findViewById(R.id.btn_buy);
        //empty
        imageEmptyCart = findViewById(R.id.image_empty_cart);
        txtEmptyCart = findViewById(R.id.txt_empty_cart);
        // rcv
        rcvCartItem = findViewById(R.id.rcv_cart_item);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvCartItem.setLayoutManager(linearLayoutManager);
        cartItemAdapter = new CartItemAdapter(this);
        rcvCartItem.setAdapter(cartItemAdapter);
    }
}