package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private String lastVisibleProductName;
    private boolean isLoading;
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

        db.collection("products").orderBy("name").startAt("p1").limit(2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    isLoading = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        productList.add(product);
                        productAdapter.notifyDataSetChanged();
                    }
                    if (!task.getResult().isEmpty()) {
                        lastVisibleProductName = task.getResult().getDocuments().get(task.getResult().size() - 1).getString("name");
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + 4)) {
                    // Xác định rằng bạn đang trong quá trình nạp dữ liệu để tránh nạp dữ liệu quá nhiều lần
                    isLoading = true;

                    // Thực hiện truy vấn Firestore mới để lấy dữ liệu tiếp theo
                    loadMoreData();
                }
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.MY_PRODUCT);
        btnBack = findViewById(R.id.toolbar_back_button);
    }

    private void loadMoreData() {
        // Tăng giới hạn và nạp thêm dữ liệu
        db.collection("products")
                .orderBy("name")
                .startAfter(lastVisibleProductName) // Truyền tên của sản phẩm cuối cùng hiển thị
                .limit(4)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Xóa indicator "isLoading"
                            isLoading = false;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productList.add(product);
                                productAdapter.notifyDataSetChanged();
                            }

                            // Cập nhật tên của sản phẩm cuối cùng
                            if (!task.getResult().isEmpty()) {
                                lastVisibleProductName = task.getResult().getDocuments().get(task.getResult().size() - 1).getString("name");
                            }
                        } else {
                            // Xóa indicator "isLoading" nếu có lỗi
                            isLoading = false;
                        }
                    }
                });
    }
}