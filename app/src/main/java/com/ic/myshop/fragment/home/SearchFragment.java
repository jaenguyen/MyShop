package com.ic.myshop.fragment.home;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ic.myshop.R;
import com.ic.myshop.activity.usecase.CartActivity;
import com.ic.myshop.adapter.product.ProductAdapter;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ApiService;
import com.ic.myshop.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private ImageView btnCart;
    private RecyclerView rcvProduct;
    protected GridLayoutManager gridLayoutManager;
    private ProductAdapter productAdapter;
    private SearchView searchView;

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
        rcvProduct = view.findViewById(R.id.rcv_search);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvProduct.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(getContext());
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
                    Map<String, Object> params = new HashMap<>();
                    params.put(InputParam.NAME, query);
                    ApiService.apiService.search(params).enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            productAdapter.clear();
                            List<Product> products = response.body();
                            productAdapter.addProducts(products);
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {

                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
