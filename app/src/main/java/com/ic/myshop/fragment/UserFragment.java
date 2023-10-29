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

import com.ic.myshop.R;
import com.ic.myshop.activity.CartActivity;
import com.ic.myshop.activity.MyProductActivity;
import com.ic.myshop.activity.AccountSettingActivity;

public class UserFragment extends Fragment {

    private ImageView btnSetting, btnCart;
    private TextView txtAccountSetting, txtMyProduct;

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

        Intent intentAccountSetting = new Intent(getContext(), AccountSettingActivity.class);
        Intent intentMyProduct = new Intent(getContext(), MyProductActivity.class);
        Intent intentCart = new Intent(getContext(), CartActivity.class);
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
    }
}
