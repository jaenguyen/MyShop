package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.ProductAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class ListProductActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private RecyclerView rcvProduct;
    private GridLayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private long maxScore = Long.MAX_VALUE;
    private boolean isScrolling = false;
    private static final DbFactory dbFactory = DbFactory.getInstance();
    private String type, field;
    FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);

        type = (String) getIntent().getSerializableExtra(InputParam.TYPE);
        field = (String) getIntent().getSerializableExtra(InputParam.FIELD);

        init();
        dbFactory.getListProduct(type, field, maxScore, 6).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        productAdapter.addProduct(product);
                    }
                    if (!task.getResult().isEmpty()) {
                        if (type != null) {
                            maxScore = task.getResult().getDocuments().
                                    get(task.getResult().size() - 1).getLong(InputParam.CREATED_TIME);
                        }
                        if (field != null) {
                            maxScore = task.getResult().getDocuments().
                                    get(task.getResult().size() - 1).getLong(field);
                        }
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
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.LIST_PRODUCT);
        btnBack = findViewById(R.id.toolbar_back_button);
        progressBar = findViewById(R.id.progress_bar);
        rcvProduct = findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(this);
        rcvProduct.setAdapter(productAdapter);
        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setVisibility(View.GONE);
    }

    private void loadMoreProduct() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dbFactory.getListProduct(type, field, maxScore, 6).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productAdapter.addProduct(product);
                            }
                            progressBar.setVisibility(View.GONE);
                            if (!task.getResult().isEmpty()) {
                                if (type != null) {
                                    maxScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(InputParam.CREATED_TIME);
                                }
                                if (field != null) {
                                    maxScore = task.getResult().getDocuments().
                                            get(task.getResult().size() - 1).getLong(field);
                                }
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