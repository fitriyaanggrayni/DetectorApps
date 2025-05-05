package com.example.insectdetector;

import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.fragment.app.FragmentActivity;


public class RiwayatActivity extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    RiwayatPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat); 

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        adapter = new RiwayatPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Riwayat Lokasi" : "Riwayat Deteksi");
        }).attach();
    }
}
