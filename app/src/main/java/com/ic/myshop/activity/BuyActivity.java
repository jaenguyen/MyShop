package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ic.myshop.R;
import com.ic.myshop.constant.MessageConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.helper.ConversionHelper;
import com.ic.myshop.model.Address;
import com.ic.myshop.model.User;

import java.util.List;

public class BuyActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        init();

        db.collection("users").document(dbFactory.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    User user = documentSnapshot.toObject(User.class);
                    List<Address> addresses = user.getAddresses();
                    if (addresses == null || addresses.isEmpty()) {
                        // Tạo một Dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(BuyActivity.this);
                        // Inflate layout cho dialog từ tệp XML
                        View dialogView = getLayoutInflater().inflate(R.layout.new_address_dialog, null);
                        builder.setView(dialogView);
                        // Khởi tạo các thành phần trong dialog
                        TextView txtName = dialogView.findViewById(R.id.txt_name);
                        TextView txtPhone = dialogView.findViewById(R.id.txt_phone);
                        TextView txtAddress = dialogView.findViewById(R.id.txt_address);
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
                                String street = txtAddress.getText().toString();
                                addresses.add(new Address(name, phone, street));
                                dbFactory.updateAddresses(addresses);
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        });

    }

    private void init() {
        db = FirebaseFirestore.getInstance();
    }
}