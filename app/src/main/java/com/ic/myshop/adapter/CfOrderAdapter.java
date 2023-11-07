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
import com.ic.myshop.activity.DetailConfirmOrderMainActivity;
import com.ic.myshop.activity.ProductActivity;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Order;
import com.ic.myshop.output.BuyItem;
import com.ic.myshop.output.OrderOutput;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CfOrderAdapter extends RecyclerView.Adapter<CfOrderAdapter.CfOrderViewHolder> {

    private Context context;
    private List<OrderOutput> orders;

    public CfOrderAdapter(Context context) {
        this.context = context;
        orders = new ArrayList<>();
    }

    public CfOrderAdapter(Context context, List<OrderOutput> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void clear() {
        this.orders.clear();
    }

    public void addOrder(OrderOutput orders) {
        this.orders.add(orders);
        notifyDataSetChanged();
    }

    public void addOrders(List<OrderOutput> orders) {
        this.orders.addAll(orders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CfOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirm_order, parent, false);
        return new CfOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CfOrderViewHolder holder, int position) {
        OrderOutput order = orders.get(position);
        if (order == null) return;
        holder.nameProduct.setText(order.getNameProduct());
        holder.priceProduct.setText(String.format("₫ %s", ConversionHelper.formatNumber(order.getPrice())));
        Glide.with(context)
                .load(order.getImageUrl())
                .fitCenter()
                .into(holder.imgProduct);
        holder.quantity.setText(String.format("x %d", order.getQuantity()));
        holder.reQuantity.setText(String.format("Tổng số tiền: ", order.getQuantity()));
        holder.totalPrice.setText(String.format("₫ %s", ConversionHelper.formatNumber(order.getPrice() * order.getQuantity())));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailConfirmOrderMainActivity.class);
                intent.putExtra("order", orders.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (orders != null) return orders.size();
        return 0;
    }

    public List<OrderOutput> getCartItems() {
        return orders;
    }

    public class CfOrderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProduct;
        private TextView nameProduct;
        private TextView priceProduct;
        private TextView quantity;
        private TextView reQuantity;
        private TextView totalPrice;


        public CfOrderViewHolder(@NonNull View itemView) {
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
