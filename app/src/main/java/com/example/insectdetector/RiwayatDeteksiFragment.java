package com.example.insectdetector;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RiwayatDeteksiFragment extends Fragment {

    private RecyclerView recyclerView;
    private RiwayatAdapter adapter;  // Gunakan RiwayatAdapter di sini
    private List<RiwayatModel> riwayatList;  // Gunakan RiwayatModel untuk data

    public RiwayatDeteksiFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_deteksi, container, false);

        // Menghubungkan RecyclerView
        recyclerView = view.findViewById(R.id.recyclerDeteksi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi list untuk menyimpan data model
        riwayatList = new ArrayList<>();

        // Adapter digunakan untuk RecyclerView
        adapter = new RiwayatAdapter(riwayatList);
        recyclerView.setAdapter(adapter);

        // Memanggil method untuk load data dari Firestore
        loadDataFromFirestore();

        return view;
    }

    // Memuat data dari Firestore
    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("riwayat_deteksi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    riwayatList.clear();
                    // Iterasi untuk mengambil setiap document dari Firestore
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        RiwayatModel model = doc.toObject(RiwayatModel.class);
                        riwayatList.add(model);
                    }
                    // Memberitahu adapter bahwa data sudah diperbarui
                    adapter.notifyDataSetChanged();
                });
    }
}
