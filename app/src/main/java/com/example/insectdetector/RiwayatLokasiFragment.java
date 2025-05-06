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

    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("riwayat_lokasi")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lokasiList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        RiwayatLokasiModel model = doc.toObject(RiwayatLokasiModel.class);
                        if (model != null) {
                            model.setDocumentId(doc.getId()); // set documentId di model
                            lokasiList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
