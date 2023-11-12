package com.ic.myshop.helper;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ic.myshop.model.Product;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService {
    public static final String URL = "http://10.0.2.2:8080/apis/";

    Gson gson = new GsonBuilder().create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("matchAll")
    Call<List<Product>> get();

    @POST("product")
    Call<Product> addProduct(@Body Product product);

    @GET("search")
    Call<List<Product>> search(@QueryMap Map<String, Object> params);
}
