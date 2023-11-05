package com.ic.myshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.CartActivity;
import com.ic.myshop.adapter.ProductAdapter;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class NotificationFragment extends Fragment {

    private ImageView btnCart;
    private ProgressBar progressBar;
    private RecyclerView rcvProduct;
    private GridLayoutManager layoutManager;
    private ProductAdapter productAdapter;
    private long maxScore = Long.MAX_VALUE;
    private boolean isScrolling = false;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCart = view.findViewById(R.id.btn_cart);
        db = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        rcvProduct = view.findViewById(R.id.rcv_product);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rcvProduct.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(getContext());
        rcvProduct.setAdapter(productAdapter);

        Intent intentCart = new Intent(getContext(), CartActivity.class);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentCart);
            }
        });

        db.collection(DatabaseConstant.PRODUCTS)
//                .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
//                .startAt(Long.MAX_VALUE)
//                .limit(4)
                .whereNotEqualTo("parentId", dbFactory.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

//        rcvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//                    isScrolling = true;
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int currentItem = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int scrollOutItem = layoutManager.findFirstVisibleItemPosition();
//
//                if (isScrolling && currentItem + scrollOutItem == totalItemCount) {
//                    isScrolling = false;
//                    loadMoreProduct();
//                }
//            }
//        });
    }

//    private void loadMoreProduct() {
//        progressBar.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                db.collection(DatabaseConstant.PRODUCTS)
//                        .orderBy(InputParam.CREATED_TIME, Query.Direction.DESCENDING)
//                        .startAfter(maxScore)
//                        .limit(4)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Product product = document.toObject(Product.class);
//                                        productAdapter.addProduct(product);
//                                    }
//                                    progressBar.setVisibility(View.GONE);
//                                    if (!task.getResult().isEmpty()) {
//                                        maxScore = task.getResult().getDocuments().
//                                                get(task.getResult().size() - 1).getLong(InputParam.CREATED_TIME);
//                                    }
//                                } else {
//                                    isScrolling = false;
//                                }
//                            }
//                        });
//            }
//        }, 1000);
//    }
}
