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

public class RiwayatLokasiAdapter extends RecyclerView.Adapter<RiwayatLokasiAdapter.ViewHolder> {

    private final List<RiwayatLokasiModel> dataList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("id", "ID"));

    public RiwayatLokasiAdapter(List<RiwayatLokasiModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat_lokasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatLokasiModel model = dataList.get(position);

        String koordinat = model.getLatitude() + ", " + model.getLongitude();
        String waktu = model.getTimestamp() != null
                ? dateFormat.format(model.getTimestamp().toDate())
                : "Tidak tersedia";

        holder.textKoordinat.setText(koordinat);
        holder.textWaktu.setText(waktu);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textKoordinat, textWaktu;

        ViewHolder(View itemView) {
            super(itemView);
            textKoordinat = itemView.findViewById(R.id.textKoordinat);
            textWaktu = itemView.findViewById(R.id.textTimestampLokasi);
        }
    }


}