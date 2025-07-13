package com.example.insectdetector;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RiwayatLokasiFragment extends Fragment {

    private RecyclerView recyclerView;
    private RiwayatLokasiAdapter adapter;
    private List<RiwayatLokasiModel> lokasiList;

    public RiwayatLokasiFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_lokasi, container, false);

        recyclerView = view.findViewById(R.id.recyclerLokasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lokasiList = new ArrayList<>();
        adapter = new RiwayatLokasiAdapter(lokasiList, getContext());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ambil tombol dari Activity (pastikan berada di Activity yang sesuai)
        ExtendedFloatingActionButton fabClearAll = getActivity().findViewById(R.id.fabClearAll);

        fabClearAll.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin menghapus semua riwayat lokasi?")
                    .setPositiveButton("Hapus Semua", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("riwayat_lokasi")
                                .get()
                                .addOnSuccessListener(querySnapshots -> {
                                    for (DocumentSnapshot doc : querySnapshots) {
                                        doc.getReference().delete();
                                    }
                                    lokasiList.clear(); // ← gunakan list yang benar
                                    adapter.notifyDataSetChanged();
                                });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("riwayat_lokasi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lokasiList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        RiwayatLokasiModel model = doc.toObject(RiwayatLokasiModel.class);
                        if (model != null) {
                            model.setDocumentId(doc.getId());
                            lokasiList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
