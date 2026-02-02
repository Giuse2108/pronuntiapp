package com.example.gfm_pronuntiapp_appfinale_esame.ui.correggiesercizi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gfm_pronuntiapp_appfinale_esame.DenominazioneLogo;
import com.example.gfm_pronuntiapp_appfinale_esame.MainActivity;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.example.gfm_pronuntiapp_appfinale_esame.RiconoscimentoLogopedista;
import com.example.gfm_pronuntiapp_appfinale_esame.RipetizioneLogopedista;

public class ExerciseAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] nomeEsercizi;
    private final String[] dateEsercizi;
    private final String[] idEsercizi;
    private final String[] tipologiaEsercizi;
    private final String[] idterapia;
    public ExerciseAdapter(Context context, String[] nomeEsercizi, String[] dateEsercizi, String[] idEsercizi, String[] tipologiaEsercizi, String[] idterapia) {
        super(context, R.layout.item_exercise, nomeEsercizi);
        this.context = context;
        this.nomeEsercizi = nomeEsercizi;
        this.dateEsercizi = dateEsercizi;
        this.idEsercizi = idEsercizi;
        this.tipologiaEsercizi = tipologiaEsercizi;
        this.idterapia = idterapia;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        }

        TextView textViewNome = convertView.findViewById(R.id.textViewNome);
        TextView textViewData = convertView.findViewById(R.id.textViewData);
        Button buttonId = convertView.findViewById(R.id.buttonId);

        textViewNome.setText(nomeEsercizi[position]);
        textViewData.setText(dateEsercizi[position]);

        buttonId.setOnClickListener(v -> {

            Bundle bundle = new Bundle();

            if(tipologiaEsercizi[position].equals("Denominazione"))
            {

                bundle.putString("ideserciziodenominazione", idEsercizi[position] + ";" + idterapia[position]);
                Intent intent = new Intent(context, DenominazioneLogo.class);
                intent.putExtra("ideserciziodenominazione", idEsercizi[position] + ";" + idterapia[position]);
                context.startActivity(intent);
            }

            if(tipologiaEsercizi[position].equals("Ripetizione"))
            {
                bundle.putString("idesercizioripetizione", idEsercizi[position] + ";" + idterapia[position]);
                Intent intent = new Intent(context, RipetizioneLogopedista.class);
                intent.putExtra("idesercizioripetizione", idEsercizi[position] + ";" + idterapia[position]);
                context.startActivity(intent);
            }

            if(tipologiaEsercizi[position].equals("Riconoscimento"))
            {
                bundle.putString("idesercizioriconoscimento", idEsercizi[position] + ";" + idterapia[position]);
                Intent intent = new Intent(context, RiconoscimentoLogopedista.class);
                intent.putExtra("idesercizioriconoscimento", idEsercizi[position] + ";" + idterapia[position]);
                context.startActivity(intent);
            }
            /*
            new AlertDialog.Builder(context)
                    .setTitle("ID Esercizio")
                    .setMessage("ID: " + idEsercizi[position] + " Tipologia" + tipologiaEsercizi[position])
                    .setPositiveButton("OK", null)
                    .show();*/

        });

        return convertView;
    }
}
