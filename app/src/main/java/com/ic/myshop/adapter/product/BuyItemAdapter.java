package com.ic.myshop.adapter.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ic.myshop.R;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.output.BuyItem;

import java.util.ArrayList;
import java.util.List;

public class BuyItemAdapter extends RecyclerView.Adapter<BuyItemAdapter.BuyItemViewHolder> {

    private Context context;
    private List<BuyItem> products;

    public BuyItemAdapter(Context context) {
        this.context = context;
        products = new ArrayList<>();
    }

    public void addProduct(BuyItem product) {
        this.products.add(product);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BuyItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy, parent, false);
        return new BuyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyItemViewHolder holder, int position) {
        BuyItem product = products.get(position);
        if (product == null) return;
        holder.nameProduct.setText(product.getName());
        holder.priceProduct.setText(String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice())));
        Glide.with(context)
                .load(product.getImageUrl())
                .fitCenter()
                .into(holder.imgProduct);
        holder.quantity.setText(String.format("x %d", product.getQuantity()));
        holder.reQuantity.setText(String.format("Tổng số tiền (%s sản phẩm): ", product.getQuantity()));
        holder.totalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice() * product.getQuantity())));
    }

    @Override
    public int getItemCount() {
        if (products != null) return products.size();
        return 0;
    }

    public List<BuyItem> getCartItems() {
        return products;
    }

    public class BuyItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProduct;
        private TextView nameProduct;
        private TextView priceProduct;
        private TextView quantity;
        private TextView reQuantity;
        private TextView totalPrice;


        public BuyItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.image_view);
            nameProduct = itemView.findViewById(R.id.txt_name);
            priceProduct = itemView.findViewById(R.id.txt_price);
            quantity = itemView.findViewById(R.id.txt_quantity);
            reQuantity = itemView.findViewById(R.id.txt_re_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}
