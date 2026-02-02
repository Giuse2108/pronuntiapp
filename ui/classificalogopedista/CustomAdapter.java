package com.example.gfm_pronuntiapp_appfinale_esame.ui.classificalogopedista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gfm_pronuntiapp_appfinale_esame.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String[]> {
    private Context context;
    private ArrayList<String[]> values;

    public CustomAdapter(Context context, ArrayList<String[]> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView textView1 = rowView.findViewById(R.id.nomeB_log);
        TextView textView2 = rowView.findViewById(R.id.cognomeB_log);
        TextView textView3 = rowView.findViewById(R.id.moneteB_log);

        textView1.setText("Nome");
        textView2.setText("Cognome");
        textView3.setText("Monete");
        String[] rowValues = values.get(position);
        textView1.setText(rowValues[0]);
        textView2.setText(rowValues[1]);
        textView3.setText(rowValues[2]);

        return rowView;
    }
}
