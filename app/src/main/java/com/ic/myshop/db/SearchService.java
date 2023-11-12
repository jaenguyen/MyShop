package com.ic.myshop.db;

import com.ic.myshop.helper.ApiService;
import com.ic.myshop.model.Product;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchService {

    private static final SearchService searchService = new SearchService();
    private SearchService() {

    }

    public static SearchService getInstance() {
        return searchService;
    }

    public void addProduct(Product product) {
        try {
            ApiService.apiService.addProduct(product).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {

                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {

                }
            });
        } catch (Exception e) {

        }
    }

}
