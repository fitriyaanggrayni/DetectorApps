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

public class RiwayatDeteksiAdapter extends RecyclerView.Adapter<RiwayatDeteksiAdapter.ViewHolder> {

    private final List<RiwayatDeteksiModel> dataList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("id", "ID"));

    public RiwayatDeteksiAdapter(List<RiwayatDeteksiModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat_deteksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatDeteksiModel model = dataList.get(position);

        Timestamp timestamp = model.getTimestamp();
        String waktu = timestamp != null ? dateFormat.format(timestamp.toDate()) : "Tidak tersedia";

        holder.textFile.setText(model.getNamaFile());
        holder.textHasil.setText(model.getHasil());
        holder.textWaktu.setText(waktu);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFile, textHasil, textWaktu;

        ViewHolder(View itemView) {
            super(itemView);
            textFile = itemView.findViewById(R.id.textNamaFile);
            textHasil = itemView.findViewById(R.id.textHasil);
            textWaktu = itemView.findViewById(R.id.textTimestamp);
        }
    }
}
