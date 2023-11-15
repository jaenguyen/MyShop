package com.ic.myshop.activity.setting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ic.myshop.R;
import com.ic.myshop.adapter.address.AddressAdapter;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.User;

import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private TextView toolbarTitle, btnAddAddress;
    private ImageButton btnBack;
    // rcv
    private RecyclerView rcvAddressItem;
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;
    // db
    private User user;
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        db.collection(DatabaseConstant.USERS).document(dbFactory.getUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        user = value.toObject(User.class);
                        List<Address> addresses = user.getAddresses();
                        addressAdapter.addAddresses(addresses);
                    }
                });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);
                // Inflate layout cho dialog từ tệp XML
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_address, null);
                builder.setView(dialogView);
                // Khởi tạo các thành phần trong dialog
                TextView txtName = dialogView.findViewById(R.id.txt_name);
                TextView txtPhone = dialogView.findViewById(R.id.txt_phone);
                TextView txtStreet = dialogView.findViewById(R.id.txt_street);
                Button btnAddAddress = dialogView.findViewById(R.id.btn_add_address);
                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                // Thiết lập sự kiện cho nút add, remove
                btnAddAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = txtName.getText().toString().trim();
                        String phone = txtPhone.getText().toString().trim();
                        String street = txtStreet.getText().toString();
                        Address address = new Address(name, phone, street);
                        user.addAddress(address);
                        dbFactory.updateAddresses(user);
                        addressAdapter.addAddress(address);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.EDIT_ADDRESS);
        btnBack = findViewById(R.id.toolbar_back_button);
        btnAddAddress = findViewById(R.id.btn_add_address);

        rcvAddressItem = findViewById(R.id.rcv_address_item);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvAddressItem.setLayoutManager(linearLayoutManager);
        addressAdapter = new AddressAdapter(this);
        rcvAddressItem.setAdapter(addressAdapter);

        db = FirebaseFirestore.getInstance();
    }
}