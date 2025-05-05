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
        adapter = new RiwayatDeteksiAdapter(riwayatList);
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        return view;
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
