package com.ic.myshop.adapter.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ic.myshop.R;
import com.ic.myshop.model.Address;

import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressViewHolder> {

    private List<Address> addresses;
    private OnAddressClickListener onAddressClickListener;

    public AddressListAdapter(List<Address> addresses) {
        this.addresses = addresses;
    }

    public AddressListAdapter(List<Address> addresses, OnAddressClickListener listener) {
        this.addresses = addresses;
        this.onAddressClickListener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addresses.get(position);
        if (address == null) return;
        holder.name.setText(String.format("Họ tên: %s", address.getName()));
        holder.phone.setText(String.format("Số điện thoại: %s", address.getPhone()));
        holder.street.setText(String.format("Địa chỉ: %s", address.getStreet()));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView phone;
        private TextView street;


        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_name_address);
            phone = itemView.findViewById(R.id.txt_phone_address);
            street = itemView.findViewById(R.id.txt_street_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onAddressClickListener != null) {
                        onAddressClickListener.onAddressClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnAddressClickListener {
        void onAddressClick(int position);
    }
}
