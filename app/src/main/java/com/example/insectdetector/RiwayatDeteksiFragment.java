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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import androidx.appcompat.app.AlertDialog;


public class RiwayatDeteksiFragment extends Fragment {

    private RecyclerView recyclerView;
    private RiwayatDeteksiAdapter adapter;
    private List<RiwayatDeteksiModel> riwayatList;

    public RiwayatDeteksiFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_deteksi, container, false);

        recyclerView = view.findViewById(R.id.recyclerDeteksi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        riwayatList = new ArrayList<>();
        adapter = new RiwayatDeteksiAdapter(getContext(), riwayatList);
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ambil tombol dari Activity
        ExtendedFloatingActionButton fabClearAll = getActivity().findViewById(R.id.fabClearAll);

        fabClearAll.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin menghapus semua riwayat deteksi?")
                    .setPositiveButton("Hapus Semua", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("riwayat_deteksi")
                                .get()
                                .addOnSuccessListener(querySnapshots -> {
                                    for (DocumentSnapshot doc : querySnapshots) {
                                        doc.getReference().delete();
                                    }
                                    riwayatList.clear();
                                    adapter.notifyDataSetChanged();
                                });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }


    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("riwayat_deteksi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    riwayatList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        RiwayatDeteksiModel model = doc.toObject(RiwayatDeteksiModel.class);
                        riwayatList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
