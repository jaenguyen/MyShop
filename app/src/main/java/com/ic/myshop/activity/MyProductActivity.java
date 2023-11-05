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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.MyProductAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class MyProductActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private FloatingActionButton btnAddProduct;
    private ProgressBar progressBar;
    private RecyclerView rcvProduct;
    private GridLayoutManager layoutManager;
    private MyProductAdapter myProductAdapter;
    private long maxScore = Long.MAX_VALUE;
    private boolean isScrolling = false;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);
        init();
        db.collection(DatabaseConstant.PRODUCTS)
                .whereEqualTo("parentId", dbFactory.getUserId())
                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                .startAt(Long.MAX_VALUE).limit(4)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                myProductAdapter.addProduct(product);
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
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.MY_PRODUCT);
        btnBack = findViewById(R.id.toolbar_back_button);
        progressBar = findViewById(R.id.progress_bar);
        btnAddProduct = findViewById(R.id.btn_add);
        rcvProduct = findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(layoutManager);
        myProductAdapter = new MyProductAdapter(this);
        rcvProduct.setAdapter(myProductAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void loadMoreProduct() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection(DatabaseConstant.PRODUCTS)
                        .whereEqualTo("parentId", dbFactory.getUserId())
                        .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
                        .startAfter(maxScore)
                        .limit(4)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Product product = document.toObject(Product.class);
                                        myProductAdapter.addProduct(product);
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
        }, 1000);
    }
}