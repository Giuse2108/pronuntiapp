package com.example.gfm_pronuntiapp_appfinale_esame.ui.appuntamenti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gfm_pronuntiapp_appfinale_esame.R;

import java.util.List;

public class AppuntamentoListview extends RecyclerView.Adapter<AppuntamentoListview.AppuntamentoViewHolder> {
    private List<Appuntamento> appuntamentiList;

    public AppuntamentoListview(List<Appuntamento> appuntamentiList) {
        this.appuntamentiList = appuntamentiList;
    }

    @NonNull
    @Override
    public AppuntamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appuntamentigen_card_contnent, parent, false);
        return new AppuntamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppuntamentoViewHolder holder, int position) {
        Appuntamento appuntamento = appuntamentiList.get(position);
        holder.dataTextView.setText("Data: " + appuntamento.data);
        holder.oraTextView.setText("Ora: " + appuntamento.ora);
        holder.incontroTextView.setText("Incontro: " + appuntamento.incontro);
        holder.statoTextView.setText("Stato: " + appuntamento.stato);
    }

    @Override
    public int getItemCount() {
        return appuntamentiList.size();
    }

    static class AppuntamentoViewHolder extends RecyclerView.ViewHolder {
        TextView dataTextView;
        TextView oraTextView;
        TextView incontroTextView;
        TextView statoTextView;

        AppuntamentoViewHolder(View itemView) {
            super(itemView);
            dataTextView = itemView.findViewById(R.id.data_text);
            oraTextView = itemView.findViewById(R.id.ora_text);
            incontroTextView = itemView.findViewById(R.id.incontro_text);
            statoTextView = itemView.findViewById(R.id.stato_text);
        }
    }
}
