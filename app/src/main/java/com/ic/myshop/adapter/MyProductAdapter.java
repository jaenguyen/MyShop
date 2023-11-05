package com.ic.myshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ic.myshop.R;
import com.ic.myshop.activity.ProductActivity;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;

    public MyProductAdapter(Context context) {
        this.context = context;
        products = new ArrayList<>();
    }

    public MyProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> products) {
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        if (product == null) return;
        holder.nameProduct.setText(product.getName());
        holder.priceProduct.setText(String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice())));
        holder.soldNumber.setText(String.format("Đã bán %d", product.getSoldNumber()));
        Glide.with(context)
                .load(product.getImageUrl())
                .fitCenter()
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (products != null) return products.size();
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProduct;
        private TextView nameProduct;
        private TextView priceProduct;
        private TextView soldNumber;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.img_product);
            nameProduct = itemView.findViewById(R.id.name_product);
            priceProduct = itemView.findViewById(R.id.price_product);
            soldNumber = itemView.findViewById(R.id.sold_number);
        }
    }
}
