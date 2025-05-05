package com.example.insectdetector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder> {

    private final List<RiwayatModel> riwayatList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("id", "ID"));

    // Constructor
    public RiwayatAdapter(List<RiwayatModel> riwayatList) {
        this.riwayatList = riwayatList;
    }

    @NonNull
    @Override
    public RiwayatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat, parent, false);
        return new RiwayatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatViewHolder holder, int position) {
        RiwayatModel riwayat = riwayatList.get(position);

        // Format tanggal
        String formattedTime = "Tidak tersedia";
        Timestamp timestamp = riwayat.getTimestamp();
        if (timestamp != null) {
            formattedTime = dateFormat.format(timestamp.toDate());
        }

        // Set data ke tampilan
        holder.timestampText.setText(formattedTime);
        holder.lokasiText.setText(riwayat.getLokasi());
        holder.deteksiText.setText(riwayat.getDeteksi());
    }

    @Override
    public int getItemCount() {
        return riwayatList.size();
    }

    static class RiwayatViewHolder extends RecyclerView.ViewHolder {
        TextView timestampText, lokasiText, deteksiText;

        public RiwayatViewHolder(View itemView) {
            super(itemView);
            timestampText = itemView.findViewById(R.id.timestampText);
            lokasiText = itemView.findViewById(R.id.lokasiText);
            deteksiText = itemView.findViewById(R.id.deteksiText);
        }
    }
}
