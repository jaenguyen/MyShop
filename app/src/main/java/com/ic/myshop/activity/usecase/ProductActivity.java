package com.ic.myshop.activity.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.user.MyShopActivity;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductActivity extends AppCompatActivity {
    int quantity = 1;
    private TextView toolbarTitle, txtName, txtPrice, txtSoldNumber, txtType, txtDescription, btnBuyNow, txtNameShop;
    private ImageButton btnBack, btnAddToCart, btnChat;
    private ImageView imageView, imageViewLike;
    private Product product;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();
    boolean isLiked = false;
    // shop
    CircleImageView imgShop;
    Button btnViewShop;


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
            txtPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice())));
            txtSoldNumber.setText(String.format("Đã bán %d", product.getSoldNumber()));
            txtType.setText(product.getType());
            txtDescription.setText(product.getDescription());

            dbFactory.getUser(product.getParentId())
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            User user = task.getResult().toObject(User.class);
                            Glide.with(getApplicationContext())
                                    .load(user.getAvatar())
                                    .fitCenter()
                                    .into(imgShop);
                            txtNameShop.setText(user.getName());
                        }
                    });
        }

        db.collection(DatabaseConstant.LIKES)
                .whereEqualTo(InputParam.USER_ID, dbFactory.getUserId())
                .whereEqualTo(InputParam.PRODUCT_ID, product.getId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                        if (documentSnapshotList.isEmpty()) {
                            isLiked = false;
                            imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                        } else {
                            isLiked = true;
                            imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                        }
                    }
                });

        imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbFactory.updateLikeProduct(dbFactory.getUserId(), product.getId(), isLiked);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getParentId().equals(dbFactory.getUserId())) {
                    Toast.makeText(ProductActivity.this, MessageConstant.OWN_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                quantity = 1;
                // Tạo một Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                // Inflate layout cho dialog từ tệp XML
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_cart, null);
                builder.setView(dialogView);
                // Khởi tạo các thành phần trong dialog
                ImageView imageView1 = dialogView.findViewById(R.id.image_view);
                TextView txtPrice1 = dialogView.findViewById(R.id.txt_price);
                TextView txtSellNumber1 = dialogView.findViewById(R.id.txt_sell_number);
                ImageView btnRemove1 = dialogView.findViewById(R.id.btn_remove);
                ImageView btnAdd1 = dialogView.findViewById(R.id.btn_add);
                TextView txtQuantity1 = dialogView.findViewById(R.id.txt_quantity);
                Button btnAddToCart1 = dialogView.findViewById(R.id.btn_add_to_cart);
                // Set giá trị cho các thành phần trong dialog
                Glide.with(getApplicationContext())
                        .load(product.getImageUrl())
                        .fitCenter()
                        .into(imageView1);
                txtPrice1.setText(String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice())));
                txtSellNumber1.setText(String.valueOf(product.getSellNumber()));
                txtQuantity1.setText(String.valueOf(quantity));
                // Thiết lập sự kiện cho nút add, remove
                btnRemove1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quantity > 1) {
                            quantity--;
                            txtQuantity1.setText(String.valueOf(quantity));
                        }
                    }
                });
                btnAdd1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quantity < product.getSellNumber()) {
                            quantity++;
                            txtQuantity1.setText(String.valueOf(quantity));
                        }
                    }
                });
                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                btnAddToCart1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (product.getSellNumber() < 1) {
                            Toast.makeText(ProductActivity.this, MessageConstant.SOLD_OUT, Toast.LENGTH_SHORT).show();
                        } else {
                            dbFactory.updateCart(product.getId(), quantity);
                            dialog.dismiss();
                            Toast.makeText(ProductActivity.this, MessageConstant.ADD_TO_CART, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getParentId().equals(dbFactory.getUserId())) {
                    Toast.makeText(ProductActivity.this, MessageConstant.OWN_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                quantity = 1;
                // Tạo một Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                // Inflate layout cho dialog từ tệp XML
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_cart, null);
                builder.setView(dialogView);
                // Khởi tạo các thành phần trong dialog
                ImageView imageView1 = dialogView.findViewById(R.id.image_view);
                TextView txtPrice1 = dialogView.findViewById(R.id.txt_price);
                TextView txtSellNumber1 = dialogView.findViewById(R.id.txt_sell_number);
                ImageView btnRemove1 = dialogView.findViewById(R.id.btn_remove);
                ImageView btnAdd1 = dialogView.findViewById(R.id.btn_add);
                TextView txtQuantity1 = dialogView.findViewById(R.id.txt_quantity);
                Button btnAddToCart1 = dialogView.findViewById(R.id.btn_add_to_cart);
                btnAddToCart1.setText(Constant.BUY_NOW);
                // Set giá trị cho các thành phần trong dialog
                Glide.with(getApplicationContext())
                        .load(product.getImageUrl())
                        .fitCenter()
                        .into(imageView1);
                txtPrice1.setText(String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice())));
                txtSellNumber1.setText(String.valueOf(product.getSellNumber()));
                txtQuantity1.setText(String.valueOf(quantity));
                // Thiết lập sự kiện cho nút add, remove
                btnRemove1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quantity > 1) {
                            quantity--;
                            txtQuantity1.setText(String.valueOf(quantity));
                        }
                    }
                });
                btnAdd1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quantity < product.getSellNumber()) {
                            quantity++;
                            txtQuantity1.setText(String.valueOf(quantity));
                        }
                    }
                });
                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                btnAddToCart1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (product.getSellNumber() < 1) {
                            Toast.makeText(ProductActivity.this, MessageConstant.SOLD_OUT, Toast.LENGTH_SHORT).show();
                        } else {
                            HashMap<String, Integer> products = new HashMap<>();
                            products.put(product.getId(), quantity);
                            Intent intent = new Intent(getApplicationContext(), BuyActivity.class);
                            intent.putExtra(InputParam.CART_ITEMS, (Serializable) products);
                            intent.putExtra(Constant.BUY_NOW, true);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        btnViewShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyShopActivity.class);
                intent.putExtra(InputParam.USER_ID, product.getParentId());
                startActivity(intent);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductActivity.this, "Tính năng đang phát triển",
                        Toast.LENGTH_SHORT).show();
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
        imageViewLike = findViewById(R.id.image_view_like);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        db = FirebaseFirestore.getInstance();
        imgShop = findViewById(R.id.img_shop);
        txtNameShop = findViewById(R.id.txt_name_shop);
        btnViewShop = findViewById(R.id.btn_view_shop);
        btnChat = findViewById(R.id.chat);
    }
}