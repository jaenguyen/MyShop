package com.ic.myshop.adapter.filter;

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
import com.ic.myshop.activity.usecase.ListCategoryProductActivity;
import com.ic.myshop.constant.InputParam;
import com.ic.myshop.constant.TypeProduct;
import com.ic.myshop.output.TypeProductItem;

import java.util.List;

public class TypeProductAdapter extends RecyclerView.Adapter<TypeProductAdapter.TypeProductItemViewHolder> {

    private Context context;
    private List<TypeProductItem> typeProductItems;

    public TypeProductAdapter(Context context, List<TypeProductItem> typeProductItems) {
        this.context = context;
        this.typeProductItems = typeProductItems;
    }

    @NonNull
    @Override
    public TypeProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_product, parent, false);
        return new TypeProductItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeProductItemViewHolder holder, int position) {
        TypeProductItem typeProductItem = typeProductItems.get(position);
        if (typeProductItem == null) return;
        holder.name.setText(typeProductItem.getName());
        Glide.with(context).load(typeProductItem.getResourceId()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ListCategoryProductActivity.class);
                intent.putExtra(InputParam.TYPE, TypeProduct.getTypeProduct(position + 1));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (typeProductItems != null) return typeProductItems.size();
        return 0;
    }

    public class TypeProductItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView image;

        public TypeProductItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_type_product);
            image = itemView.findViewById(R.id.img_type_product);
        }
    }
}
