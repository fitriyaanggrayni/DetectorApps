package com.example.insectdetector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RiwayatLokasiFragment extends Fragment {

    private RecyclerView recyclerView;
    private RiwayatAdapter adapter;
    private List<RiwayatModel> riwayatList;

    public RiwayatLokasiFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_lokasi, container, false);

        // Menghubungkan RecyclerView dengan layout
        recyclerView = view.findViewById(R.id.recyclerLokasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi list untuk data Riwayat
        riwayatList = new ArrayList<>();

        // Menyiapkan adapter dengan data
        adapter = new RiwayatAdapter(riwayatList);
        recyclerView.setAdapter(adapter);

        // Memuat data dari Firestore
        loadDataFromFirestore();

        return view;
    }

    // Method untuk load data dari Firestore
    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("riwayat_lokasi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    riwayatList.clear();  // Bersihkan data lama
                    // Ambil data dari Firestore
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        RiwayatModel model = doc.toObject(RiwayatModel.class);
                        riwayatList.add(model);  // Tambahkan model ke list
                    }
                    adapter.notifyDataSetChanged();  // Memberitahu adapter untuk refresh data
                });
    }
}
