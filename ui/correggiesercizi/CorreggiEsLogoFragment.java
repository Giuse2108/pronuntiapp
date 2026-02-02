package com.example.gfm_pronuntiapp_appfinale_esame.ui.correggiesercizi;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class CorreggiEsLogoFragment extends Fragment {

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";
    private CorreggiEsLogoViewModel mViewModel;
    private String codgenitore = "";
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference_TERAPIA;
    private DatabaseReference reference_ESERCIZIO;
    private ArrayList<String> lista_nomebamb = new ArrayList<>();
    private ArrayList<String> lista_cognomebamb = new ArrayList<>();
    private ArrayList<String> lista_idbamb = new ArrayList<>();

    private Spinner spinnerbambini;

    private String[] dati;
    private String[] nomebambino;
    private String[] cognomebambino;
    private String[] idbambino;

    private ArrayList<String> array_terapia_idterapia = new ArrayList<>();
    private ArrayList<String> array_terapia_idbmabino = new ArrayList<>();
    private ArrayList<String> array_terapia_idesercizio = new ArrayList<>();
    private ArrayList<String> array_terapia_correzione = new ArrayList<>();
    private ArrayList<String> array_terapia_data = new ArrayList<>();
    private ArrayList<String> array_terapia_nomeesercizio = new ArrayList<>();
    private ArrayList<String> array_terapia_tipologiaesercizio = new ArrayList<>();

    private ArrayList<String> array_esercizio_idesercizio = new ArrayList<>();
    private ArrayList<String> array_esercizio_nomeesercizio = new ArrayList<>();
    private ArrayList<String> array_esercizio_tipologiaesercizio = new ArrayList<>();

    private ListView listView;

    private int check = 0;


    public static CorreggiEsLogoFragment newInstance() {
        return new CorreggiEsLogoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_correggi_es_logo, container, false);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        codgenitore = loadidgenitore();

        spinnerbambini = view.findViewById(R.id.spinnerbambinicorreggi);
        listView= view.findViewById(R.id.lista_esercizibambino);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Bambino");
        reference_TERAPIA = database.getReference("Terapia");
        reference_ESERCIZIO = database.getReference("Esercizio");


        caricabambinilogo(spinnerbambini);

        spinnerbambini.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Azioni da eseguire quando un element è selezionato
                String elementoSelezionato = parentView.getItemAtPosition(position).toString();

                if(!elementoSelezionato.equals("Seleziona")){

                    listView.setAdapter(null);
                    array_terapia_idterapia.clear();
                    array_terapia_idbmabino.clear();
                    array_terapia_idesercizio.clear();
                    array_terapia_correzione.clear();
                    array_terapia_data.clear();
                    array_terapia_nomeesercizio.clear();
                    array_esercizio_idesercizio.clear();
                    array_esercizio_nomeesercizio.clear();
                    array_terapia_tipologiaesercizio.clear();
                    array_esercizio_tipologiaesercizio.clear();

                    for(int i = 0; i < nomebambino.length; i++)
                    {
                        String nomedatrovare = nomebambino[i] + " " + cognomebambino[i];

                        if(elementoSelezionato.equals(nomedatrovare))
                        {
                            String idbambinotrovato = idbambino[i];
                            reference_TERAPIA.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                        String terapia_idterapia = childSnapshot.child("idTerapia").getValue(String.class);
                                        String terapia_idbmabino = childSnapshot.child("idBambino").getValue(String.class);
                                        String terapia_idesercizio = childSnapshot.child("idEsercizio").getValue(String.class);
                                        String terapia_correzione = childSnapshot.child("correzione").getValue(String.class);
                                        String terapia_data = childSnapshot.child("data").getValue(String.class);
                                        String terapia_risposta = childSnapshot.child("risposta").getValue(String.class);

                                        Log.d("Dati","idbambino1:" + terapia_idbmabino);
                                        Log.d("Dati","idbambinoIDTROVATO:" + idbambinotrovato);
                                        Log.d("Dati","idbambinoTERAPIACORR:" + terapia_correzione);
                                        Log.d("Dati","idbambinoTERAPIARISPOSTA:" + terapia_risposta);
                                        if(terapia_correzione.equals("") && terapia_idbmabino.equals(idbambinotrovato) && !terapia_risposta.equals(""))
                                        {
                                            Log.d("Dati","idbambino2:" + terapia_idbmabino);
                                            array_terapia_idterapia.add(terapia_idterapia);
                                            array_terapia_idbmabino.add(terapia_idbmabino);
                                            array_terapia_idesercizio.add(terapia_idesercizio);
                                            array_terapia_correzione.add(terapia_correzione);
                                            array_terapia_data.add(terapia_data);
                                        }

                                    }

                                    reference_ESERCIZIO.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                                String esercizio_idesercizio = childSnapshot.child("idEsercizio").getValue(String.class);
                                                String esercizio_nomeesercizio = childSnapshot.child("nome").getValue(String.class);
                                                String esercizio_tipologiaesercizio = childSnapshot.child("tipologia").getValue(String.class);

                                                array_esercizio_nomeesercizio.add(esercizio_nomeesercizio);
                                                array_esercizio_idesercizio.add(esercizio_idesercizio);
                                                array_esercizio_tipologiaesercizio.add(esercizio_tipologiaesercizio);

                                            }

                                            aggiungiarraynomi();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
                                        }
                                    });



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
                                }
                            });
                        }

                    }



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Azioni da eseguire quando nessun elemento è selezionato
            }
        });

        return view;
    }

    private void caricabambinilogo(Spinner spinbamb){

        ArrayList<String> lista_nomebamb = new ArrayList<>();
        ArrayList<String> lista_cognomebamb = new ArrayList<>();
        ArrayList<String> lista_idbamb = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String nome = childSnapshot.child("nome").getValue(String.class);
                    String cognome = childSnapshot.child("cognome").getValue(String.class);
                    String idbambino = childSnapshot.child("id").getValue(String.class);
                    String idgenitore = childSnapshot.child("idgenitore").getValue(String.class);

                    if(codgenitore.equals(idgenitore))
                    {
                        lista_nomebamb.add(nome);
                        lista_cognomebamb.add(cognome);
                        lista_idbamb.add(idbambino);
                    }

                }

                dati = new String[lista_cognomebamb.size() +1];
                dati[0] = "Seleziona";
                int count_lista = 0;
                int count_for = lista_cognomebamb.size();
                count_for ++;

                for(int i = 1; i < count_for; i++){
                    dati[i] = lista_nomebamb.get(count_lista) + " " +lista_cognomebamb.get(count_lista);
                    count_lista ++;
                }

                nomebambino = new String[lista_nomebamb.size()];
                cognomebambino = new String[lista_cognomebamb.size()];
                idbambino = new String[lista_idbamb.size()];

                for(int i = 0; i < lista_cognomebamb.size(); i++)
                {
                    nomebambino[i] = lista_nomebamb.get(i);
                    cognomebambino[i] = lista_cognomebamb.get(i);
                    idbambino[i] = lista_idbamb.get(i);
                }

                // Crea un ArrayAdapter utilizzando l'array di dati e un layout predefinito per gli elementi dello Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dati);

                // Specifica il layout da utilizzare quando l'elenco di scelte appare
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Applica l'adapter allo Spinner
                spinbamb.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void aggiungiarraynomi(){

        for(int k = 0; k < array_terapia_idesercizio.size(); k++){

            String iddaanalizzare = array_terapia_idesercizio.get(k);
            for(int j = 0; j < array_esercizio_nomeesercizio.size(); j++){
                String iddaanalizzare_2 = array_esercizio_idesercizio.get(j);
                if(iddaanalizzare.equals(iddaanalizzare_2)){
                    String nomeanalisi = array_esercizio_nomeesercizio.get(j);
                    String tipologiaanalisi = array_esercizio_tipologiaesercizio.get(j);
                    array_terapia_nomeesercizio.add(nomeanalisi);
                    array_terapia_tipologiaesercizio.add(tipologiaanalisi);
                }
            }

        }
            aggiungilista();
    }

    private void aggiungilista(){


        String[] idterapiadapassare = new String [array_terapia_idterapia.size()];
        String[] nomeesercizidapassare = new String [array_terapia_nomeesercizio.size()];
        String[] dataesercizidapassare = new String [array_terapia_data.size()];
        String[] idesercizidapassare = new String [array_terapia_idesercizio.size()];
        String[] terapiaesercizidapassare = new String [array_terapia_tipologiaesercizio.size()];

        for(int f = 0; f < array_terapia_nomeesercizio.size(); f++){
            idterapiadapassare[f] = array_terapia_idterapia.get(f);
            nomeesercizidapassare[f] = array_terapia_nomeesercizio.get(f);
            dataesercizidapassare[f] = array_terapia_data.get(f);
            idesercizidapassare[f] = array_terapia_idesercizio.get(f);
            terapiaesercizidapassare[f] = array_terapia_tipologiaesercizio.get(f);
        }

        ExerciseAdapter adapter = new ExerciseAdapter(getActivity(), nomeesercizidapassare, dataesercizidapassare, idesercizidapassare,terapiaesercizidapassare,idterapiadapassare);
        listView.setAdapter(adapter);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CorreggiEsLogoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.activity_dashboard_logopedista_drawer, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

        // Update the configuration
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

    private String loadidgenitore(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String idshared = sharedPreferences.getString(KEY_ID_FIREBASE_UTENTE, "");

        return idshared;
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