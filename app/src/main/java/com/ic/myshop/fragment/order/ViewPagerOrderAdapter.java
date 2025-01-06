package com.ic.myshop.fragment.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerOrderAdapter extends FragmentStatePagerAdapter {


    public ViewPagerOrderAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ConfirmOrderFragment();
            case 1:
                return new DeliveredOrderFragment();
            case 2:
                return new CompletedOrderFragment();
            case 3:
                return new CanceledOrderFragment();
            default:
                return new ConfirmOrderFragment();
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
