package com.example.insectdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.google.android.material.chip.Chip;


public class RiwayatDeteksiAdapter extends RecyclerView.Adapter<RiwayatDeteksiAdapter.ViewHolder> {
    private final Context context;
    private final List<RiwayatDeteksiModel> dataList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public RiwayatDeteksiAdapter(Context context, List<RiwayatDeteksiModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_deteksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatDeteksiModel model = dataList.get(position);
        Timestamp timestamp = model.getTimestamp();
        String waktu = timestamp != null ? dateFormat.format(timestamp.toDate()) : "Tidak tersedia";

        holder.textFile.setText(model.getNamaFile());
        holder.textHasilDeteksi.setText(model.getHasil());
        holder.textWaktu.setText(waktu);

        holder.buttonHapus.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        String namaFile = model.getNamaFile();
                        db.collection("riwayat_deteksi")
                                .whereEqualTo("nama_file", namaFile)
                                .get()
                                .addOnSuccessListener(querySnapshots -> {
                                    for (DocumentSnapshot doc : querySnapshots) {
                                        doc.getReference().delete();
                                    }
                                    dataList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, dataList.size());
                                });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFile, textWaktu;
        TextView textHasilDeteksi;

        Button buttonHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFile = itemView.findViewById(R.id.textNamaFile);
            textHasilDeteksi = itemView.findViewById(R.id.textHasilDeteksi);
            textWaktu = itemView.findViewById(R.id.textTimestamp);
            buttonHapus = itemView.findViewById(R.id.buttonHapus);
        }
    }

}
