package com.ic.myshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ic.myshop.R;
import com.ic.myshop.activity.CartActivity;
import com.ic.myshop.adapter.SearchProductAdapter;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Product;

public class SearchFragment extends Fragment {

    private ImageView btnCart;
    private RecyclerView rcvProduct;
    protected LinearLayoutManager linearLayoutManager;
    private SearchProductAdapter productAdapter;
    private SearchView searchView;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.search_view);
        btnCart = view.findViewById(R.id.btn_cart);
        db = FirebaseFirestore.getInstance();
        rcvProduct = view.findViewById(R.id.rcv_search);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvProduct.setLayoutManager(linearLayoutManager);
        productAdapter = new SearchProductAdapter(getContext());
        rcvProduct.setAdapter(productAdapter);

        Intent intentCart = new Intent(getContext(), CartActivity.class);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentCart);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    db.collection(DatabaseConstant.PRODUCTS)
                            .whereGreaterThanOrEqualTo("name", query)
                            .whereLessThanOrEqualTo("name", query + "~")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        productAdapter.clear();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Product product = document.toObject(Product.class);
                                            productAdapter.addProduct(product);
                                        }
                                    } else {
                                    }
                                }
                            });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    db.collection(DatabaseConstant.PRODUCTS)
                            .whereGreaterThanOrEqualTo("name", newText)
                            .whereLessThanOrEqualTo("name", newText + '\uf8ff')
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        productAdapter.clear();

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Product product = document.toObject(Product.class);
                                            productAdapter.addProduct(product);
                                        }
                                    } else {
                                    }
                                }
                            });
                }
                return false;
            }
        });
    }
}