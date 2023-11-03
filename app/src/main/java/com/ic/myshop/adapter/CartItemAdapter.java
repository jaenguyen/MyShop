package com.ic.myshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ic.myshop.R;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private Context context;
    private List<Product> products;
    private Map<String, Integer> quantityProducts;
    private List<String> selected;

    // event
    private CartItemClickListener cartItemClickListener;

    public CartItemAdapter(Context context) {
        this.context = context;
        products = new ArrayList<>();
        quantityProducts = new HashMap<>();
        selected = new ArrayList<>();
    }

    public Product getProduct(int position) {
        return products.get(position);
    }

    public int getQuantity(String id) {
        return quantityProducts.get(id);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void updateSelected(String id, boolean add, boolean remove) {
        if (add) selected.add(id);
        if (remove) selected.remove(id);
        notifyDataSetChanged();
    }

    public void updateQuantity(String id, int quantity) {
        quantityProducts.put(id, quantity);
        notifyDataSetChanged();
    }

    public boolean isSelected(String id) {
        return selected.contains(id);
    }

    public void addCartItem(Product product, int quantity) {
        this.products.add(product);
        quantityProducts.put(product.getId(), quantity);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartItemAdapter.CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        Product product = products.get(position);
        if (product == null) return;
        holder.nameProduct.setText(product.getName());
        holder.priceProduct.setText((String.format("₫ %s", ConversionHelper.formatNumber(product.getPrice()))));
        holder.sellNumber.setText(String.valueOf(quantityProducts.get(product.getId())));
        Glide.with(context)
                .load(product.getImageUrl())
                .fitCenter()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (products != null) return products.size();
        return 0;
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView imageView;
        private TextView nameProduct;
        private TextView priceProduct;
        private ImageView btnRemove, btnAdd;
        private TextView sellNumber;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.check);
            imageView = itemView.findViewById(R.id.image_view);
            nameProduct = itemView.findViewById(R.id.txt_name);
            priceProduct = itemView.findViewById(R.id.txt_price);
            sellNumber = itemView.findViewById(R.id.txt_quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnAdd = itemView.findViewById(R.id.btn_add);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (cartItemClickListener != null) {
                        cartItemClickListener.onCheckboxClick(getAdapterPosition(), checkBox.isChecked());
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cartItemClickListener != null) {
                        cartItemClickListener.onAddButtonClick(getAdapterPosition());
                    }
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cartItemClickListener != null) {
                        cartItemClickListener.onRemoveButtonClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface CartItemClickListener {
        void onCheckboxClick(int position, boolean isChecked);
        void onAddButtonClick(int position);
        void onRemoveButtonClick(int position);
    }

    public void setCartItemClickListener(CartItemClickListener listener) {
        this.cartItemClickListener = listener;
    }
}