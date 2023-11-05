package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.adapter.ProductAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Like;
import com.ic.myshop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class LikeProductsActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private List<String> likeProductIds;
    private RecyclerView rcvProduct;
    private GridLayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_products);
        init();

        db.collection(DatabaseConstant.LIKES)
                .whereEqualTo("userId", dbFactory.getUserId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()) {
                            likeProductIds.clear();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Like like = documentSnapshot.toObject(Like.class);
                                likeProductIds.add(like.getProductId());
                            }
                        }
                        productAdapter.clear();
                        for (String likeProductId : likeProductIds) {
                            db.collection("products").document(likeProductId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                Product product = documentSnapshot.toObject(Product.class);
                                                productAdapter.addProduct(product);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.LIKE_PRODUCTS);
        btnBack = findViewById(R.id.toolbar_back_button);
        db = FirebaseFirestore.getInstance();
        likeProductIds = new ArrayList<>();
        rcvProduct = findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(this);
        rcvProduct.setAdapter(productAdapter);
    }
}