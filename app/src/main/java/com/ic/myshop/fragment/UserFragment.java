package com.ic.myshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ic.myshop.R;
import com.ic.myshop.activity.CartActivity;
import com.ic.myshop.activity.LikeProductsActivity;
import com.ic.myshop.activity.MyOrderActivity;
import com.ic.myshop.activity.MyProductActivity;
import com.ic.myshop.activity.AccountSettingActivity;
import com.ic.myshop.constant.DatabaseConstant;
import com.ic.myshop.db.DbFactory;
import com.ic.myshop.model.User;

public class UserFragment extends Fragment {

    private ImageView btnSetting, btnCart, avatar;
    private TextView txtAccountSetting, txtMyProduct, txtEmail, txtLikeProducts, txtMyOrder, txtName;
    //db
    private FirebaseFirestore db;
    private static final DbFactory dbFactory = DbFactory.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSetting = view.findViewById(R.id.btn_setting);
        btnCart = view.findViewById(R.id.btn_cart);
        txtAccountSetting = view.findViewById(R.id.txt_account_setting);
        txtMyProduct = view.findViewById(R.id.txt_my_product);
        avatar = view.findViewById(R.id.avatar);
        txtName = view.findViewById(R.id.txt_name);
        txtEmail = view.findViewById(R.id.txt_email);
        txtLikeProducts = view.findViewById(R.id.txt_like_product);
        txtMyOrder = view.findViewById(R.id.txt_my_order);
        db = FirebaseFirestore.getInstance();

        // get info user
        db.collection(DatabaseConstant.USERS).document(dbFactory.getUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            User user = value.toObject(User.class);
                            txtName.setText(user.getName());
                            txtEmail.setText(user.getEmail());
                            Glide.with(getActivity())
                                    .load(user.getAvatar())
                                    .fitCenter()
                                    .into(avatar);
                        }
                    }
                });

        Intent intentAccountSetting = new Intent(getContext(), AccountSettingActivity.class);
        Intent intentMyProduct = new Intent(getContext(), MyProductActivity.class);
        Intent intentCart = new Intent(getContext(), CartActivity.class);
        Intent intentLikeProducts = new Intent(getContext(), LikeProductsActivity.class);
        Intent intentMyOrder = new Intent(getContext(), MyOrderActivity.class);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentAccountSetting);
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentCart);
            }
        });

        txtAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentAccountSetting);
            }
        });

        txtMyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentMyProduct);
            }
        });

        txtLikeProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentLikeProducts);
            }
        });

        txtMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentMyOrder);
            }
        });
    }
}
