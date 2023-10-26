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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.ProductAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MyProductActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private RecyclerView rcvProduct;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private GridLayoutManager layoutManager;
    ProgressBar progressBar;
    private long maxScore;
    private boolean isScrolling = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);
        db = FirebaseFirestore.getInstance();

        init();
        rcvProduct = findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(layoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        rcvProduct.setAdapter(productAdapter);

        db.collection("products").orderBy("createdTime", Query.Direction.DESCENDING).
                startAt(Long.MAX_VALUE).limit(4).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        productList.add(product);
                        productAdapter.notifyDataSetChanged();
                    }
                    if (!task.getResult().isEmpty()) {
                        maxScore = task.getResult().getDocuments().get(task.getResult().size() - 1).getLong("createdTime");
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

                if (isScrolling && currentItem+scrollOutItem==totalItemCount) {
                    isScrolling = false;
                    loadMoreData();
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.MY_PRODUCT);
        btnBack = findViewById(R.id.toolbar_back_button);
        progressBar = findViewById(R.id.progress);
    }

    private void loadMoreData() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("products")
                        .orderBy("createdTime", Query.Direction.DESCENDING)
                        .startAfter(maxScore) // Truyền tên của sản phẩm cuối cùng hiển thị
                        .limit(4)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Product product = document.toObject(Product.class);
                                        productList.add(product);
                                        productAdapter.notifyDataSetChanged();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.getResult().isEmpty()) {
                                        maxScore = task.getResult().getDocuments().get(task.getResult().size() - 1).getLong("createdTime");
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