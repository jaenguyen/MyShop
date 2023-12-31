package com.ic.myshop.adapter.address;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<Address> addresses;
    private AddressClickListener addressClickListener;

    public AddressAdapter(Context context) {
        this.context = context;
        addresses = new ArrayList<>();
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
        notifyDataSetChanged();
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
        notifyDataSetChanged();
    }

    public void addAddresses(List<Address> addresses) {
        this.addresses.clear();
        this.addresses.addAll(addresses);
        notifyDataSetChanged();
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
        if (addresses != null) return addresses.size();
        return 0;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private TextView name;
        private TextView phone;
        private TextView street;


        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_name_address);
            phone = itemView.findViewById(R.id.txt_phone_address);
            street = itemView.findViewById(R.id.txt_street_address);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
            if (addressClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            addressClickListener.onDeleteAddress(position);
                            return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem deleteAddress = contextMenu.add(Menu.NONE, 1, 1, Constant.DELETE_ADDRESS);
            deleteAddress.setOnMenuItemClickListener(this);
        }
    }

    public interface AddressClickListener {
        void onDeleteAddress(int position);
    }

    public void setAddressClickListener(AddressClickListener listener) {
        this.addressClickListener = listener;
    }
}
