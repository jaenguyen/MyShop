package com.ic.myshop.fragment.sales_order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerSalesOrderAdapter extends FragmentStatePagerAdapter {


    public ViewPagerSalesOrderAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ConfirmSOrderFragment();
            case 1:
                return new DeliveredSOrderFragment();
            case 2:
                return new CompletedSOrderFragment();
            case 3:
                return new CanceledSOrderFragment();
            default:
                return new ConfirmSOrderFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Chờ xác nhận";
                break;
            case 1:
                title = "Đang giao";
                break;
            case 2:
                title = "Đã hoàn thành";
                break;
            case 3:
                title = "Đã hủy";
        }
        return title;
    }
}
