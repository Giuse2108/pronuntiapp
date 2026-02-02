package com.example.gfm_pronuntiapp_appfinale_esame.ui.areagioco;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.Home_Figlio;
import com.example.gfm_pronuntiapp_appfinale_esame.MonitoraTerapiaAct;
import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.example.gfm_pronuntiapp_appfinale_esame.RiconoscimentoLogopedista;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class AreaGiocoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Bambino> bambiniList = new ArrayList<>();

    private AreaGiocoViewModel mViewModel;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_EMAIL = "username";
    private static final String KEY_LINGUA = "lingua";
    private static final String KEY_BAMBINO_AREA_GIOCO = "id_bambino";

    private static final String KEY_BAMBINO_MONITORA_TERAPIA = "monitora";

    private String idgenitore_db;
    private String emailgenitore;

    DatabaseReference reference;

    public static AreaGiocoFragment newInstance() {
        return new AreaGiocoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_area_gioco, container, false);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        recuperoemail_genitore();
        recuperoid_genitore(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AreaGiocoViewModel.class);
        // TODO: Use the ViewModel
    }

    private void recuperoemail_genitore(){
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            emailgenitore = sharedPreferences.getString(KEY_EMAIL, "");
        }
    }

    public void recuperoid_genitore(View view){
        reference = FirebaseDatabase.getInstance().getReference("Genitore");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String emaildb = childSnapshot.child("email").getValue(String.class);
                    String idgenitore_firebase = childSnapshot.child("idgenitore").getValue(String.class);

                    if (emaildb.equals(emailgenitore)) {
                        idgenitore_db = idgenitore_firebase;
                    }

                    Log.d("Firebase","IDGENITORE:" + idgenitore_db);

                    recuperoid_bambini(view);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Errore durante il recupero dei dati: " + databaseError.getMessage());
            }
        });
    }

    public void recuperoid_bambini(View view){
        reference = FirebaseDatabase.getInstance().getReference("Bambino");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String idbambino = childSnapshot.child("id").getValue(String.class);
                    String nome = childSnapshot.child("nome").getValue(String.class);
                    String cognome = childSnapshot.child("cognome").getValue(String.class);
                    String idgenitore = childSnapshot.child("idgenitore").getValue(String.class);

                    if (idgenitore.equals(idgenitore_db)) {
                        bambiniList.add(new Bambino(idbambino, nome + " " + cognome));
                    }
                }

                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                MyAdapter adapter = new MyAdapter(bambiniList, requireContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Errore durante il recupero dei dati: " + databaseError.getMessage());
            }
        });
    }

    private class Bambino {
        String id;
        String nomeCompleto;

        Bambino(String id, String nomeCompleto) {
            this.id = id;
            this.nomeCompleto = nomeCompleto;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Bambino> data;
        private Context context;

        public MyAdapter(ArrayList<Bambino> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Bambino bambino = data.get(position);
            holder.textViewName.setText(bambino.nomeCompleto);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView textViewName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Bambino bambino = data.get(position);

                    String scetlamx = getString(R.string.areagioco_scelta);
                    String agmx = getString(R.string.areagioco_areagioco);
                    String amtmx = getString(R.string.areagioco_monterap);

                    // Crea l'AlertDialog Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(scetlamx);
                    builder.setMessage("");

                    // Aggiungi i pulsanti con le loro azioni
                    builder.setPositiveButton(agmx, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Azione per il pulsante "Area gioco"
                            loadbambinoareagioco(bambino.id);
                        }
                    });

                    builder.setNegativeButton(amtmx, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Azione per il pulsante "Monitora terapia"
                            redirect_mon(bambino.id);
                        }
                    });

                    // Crea e mostra l'AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        }
    }

    private void redirect_mon(String bambino){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_BAMBINO_MONITORA_TERAPIA, bambino);
        editor.apply();

        Intent intent = new Intent(getContext(), MonitoraTerapiaAct.class);
        getContext().startActivity(intent);
    }

    private void loadbambinoareagioco(String bambino) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_BAMBINO_AREA_GIOCO, bambino);
        editor.apply();

        Intent intent = new Intent(getContext(), Home_Figlio.class);
        getContext().startActivity(intent);
    }

    private void loadlingua() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");

        String currentln = getLinguaCorrente(getContext());
        if(!lingua.equals(currentln)){
            applicamodifiche(lingua);
        }
    }

    private void applicamodifiche(String languageCode){
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        getActivity().recreate();
    }

    private String getLinguaCorrente(Context context) {
        Locale currentLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = context.getResources().getConfiguration().locale;
        }
        return currentLocale.getLanguage();
    }

    private boolean connessioneinternet(){

        if (NetworkUtils.isConnectedToInternet(getContext())) {
            return true;
        } else {
            String nocon = getString(R.string.no_internet);
            Toast.makeText(getContext(), nocon, Toast.LENGTH_SHORT).show();
            return false;
        }

    }


}