package com.example.insectdetector;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class RiwayatPagerAdapter extends FragmentStateAdapter {

    public RiwayatPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new RiwayatLokasiFragment() : new RiwayatDeteksiFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Dua tab
    }
}

