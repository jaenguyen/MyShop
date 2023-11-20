package com.ic.myshop.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ic.myshop.R;
import com.ic.myshop.constant.Constant;
import com.ic.myshop.fragment.sales_order.ViewPagerSalesOrderAdapter;

public class MySOrderActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageButton btnBack;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales_order);
        init();

        ViewPagerSalesOrderAdapter viewPagerOderAdapter = new ViewPagerSalesOrderAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerOderAdapter);
        tabLayout.setupWithViewPager(viewPager);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.MY_SALES_ORDER);
        btnBack = findViewById(R.id.toolbar_back_button);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }
}
