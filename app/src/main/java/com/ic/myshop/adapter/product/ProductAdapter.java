package com.ic.myshop.adapter.product;

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
import com.ic.myshop.activity.usecase.ProductActivity;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private List<String> productIds;

    public ProductAdapter(Context context) {
        this.context = context;
        products = new ArrayList<>();
        productIds = new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (isExist(product.getId())) return;
        this.products.add(product);
        productIds.add(product.getId());
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> products) {
        this.products.addAll(products);
        for (Product product : products) productIds.add(product.getId());
        notifyDataSetChanged();
    }

    public void clear() {
        this.products.clear();
        this.productIds.clear();
        notifyDataSetChanged();
    }

    public boolean isExist(String id) {
        return productIds.contains(id);
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
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra(InputParam.PRODUCT, products.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (products != null) return products.size();
        return 0;
    }

    public void sort(int sortType) {
        Comparator<Product> comparator = null;
        switch (sortType) {
            case 0:
                comparator = Comparator.comparingLong(Product::getCreatedTime);
                break;
            case 1:
                comparator = Comparator.comparingLong(Product::getSoldNumber);
                break;
            case 2:
                comparator = Comparator.comparingLong(Product::getLikes);
                break;
            case 3:
            case 4:
                comparator = Comparator.comparingLong(Product::getPrice);
                break;
        }
        if (sortType != 3) comparator = comparator.reversed();
        Collections.sort(products, comparator);
        notifyDataSetChanged();
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
