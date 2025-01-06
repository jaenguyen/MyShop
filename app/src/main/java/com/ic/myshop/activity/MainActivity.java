package com.ic.myshop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ic.myshop.R;
import com.ic.myshop.fragment.home.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        viewPager = findViewById(R.id.view_pager);

        setUpViewPager();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    viewPager.setCurrentItem(0);
                } else if (itemId == R.id.navigation_search) {
                    viewPager.setCurrentItem(1);
                } else if (itemId == R.id.navigation_notify) {
                    viewPager.setCurrentItem(2);
                } else if (itemId == R.id.navigation_user) {
                    viewPager.setCurrentItem(3);
                }
                return true;
            }
        });
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        navView.getMenu().findItem(R.id.navigation_search).setChecked(true);
                        break;
                    case 2:
                        navView.getMenu().findItem(R.id.navigation_notify).setChecked(true);
                        break;
                    case 3:
                        navView.getMenu().findItem(R.id.navigation_user).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}