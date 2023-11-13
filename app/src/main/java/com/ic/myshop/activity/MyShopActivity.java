package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.ProductAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;
import com.ic.myshop.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyShopActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private FloatingActionButton btnAddProduct;
    private ProgressBar progressBar;
    private RecyclerView rcvProduct;
    private GridLayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private long maxScore = Long.MAX_VALUE;
    private boolean isScrolling = false;
    private static final DbFactory dbFactory = DbFactory.getInstance();
    private Spinner sortSpinner;
    private CircleImageView avatar;
    private TextView txtName;
    private TextView txtEmail;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);
        init();
        userId = getIntent().getStringExtra(InputParam.USER_ID);
        if (userId == null || userId.isEmpty()) {
            userId = dbFactory.getUserId();
        }
        dbFactory.getUser(userId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        Glide.with(getApplicationContext())
                                .load(user.getAvatar())
                                .fitCenter()
                                .into(avatar);
                        txtName.setText(user.getName());
                        txtEmail.setText(user.getEmail());
                    }
                });
        dbFactory.getProductsSelfDefault(userId, maxScore, 6)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productAdapter.addProduct(product);
                            }
                            if (!task.getResult().isEmpty()) {
                                maxScore = task.getResult().getDocuments().
                                        get(task.getResult().size() - 1).getLong(InputParam.CREATED_TIME);
                            }
                        } else {

                        }
                    }
                });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rcvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentItem = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int scrollOutItem = layoutManager.findFirstVisibleItemPosition();

                if (isScrolling && currentItem + scrollOutItem == totalItemCount) {
                    isScrolling = false;
                    loadMoreProduct();
                }
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 0: nổi bật, 1: được yêu thích, 2: giá tăng dần, 3: giá giảm dần
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int sortType, long id) {
                productAdapter.sort(sortType);
                rcvProduct.scrollToPosition(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.MY_SHOP);
        btnBack = findViewById(R.id.toolbar_back_button);
        progressBar = findViewById(R.id.progress_bar);
        btnAddProduct = findViewById(R.id.btn_add);
        rcvProduct = findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(this);
        rcvProduct.setAdapter(productAdapter);
        sortSpinner = findViewById(R.id.sort_spinner);
        sortSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_type_product,
                getResources().getStringArray(R.array.sort)));
        avatar = findViewById(R.id.avatar);
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
    }

    private void loadMoreProduct() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dbFactory.getProductsSelfDefault(userId, maxScore, 6)
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Product product = document.toObject(Product.class);
                                        productAdapter.addProduct(product);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.getResult().isEmpty()) {
                                        maxScore = task.getResult().getDocuments().
                                                get(task.getResult().size() - 1).getLong(InputParam.CREATED_TIME);
                                    }
                                } else {
                                    isScrolling = false;
                                }
                            }
                        });
            }
        }, 2000);
    }
}