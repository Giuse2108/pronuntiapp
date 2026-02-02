package com.example.gfm_pronuntiapp_appfinale_esame.ui.appuntamenti;

import androidx.lifecycle.ViewModelProvider;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.example.gfm_pronuntiapp_appfinale_esame.ui.addesercizio.HelperClassRiconoscimento;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppuntamentiFragment extends Fragment {

    private TextView labelInserimentoData;
    private TextView oraInserimentoData;
    private Button dataPickerButton;

    private Button oraPickerButton;

    private EditText luogoEditText;

    private EditText motivazioneEditText;

    private  Button addButton;

    private AppuntamentiViewModel mViewModel;

    private String codiceLogopedista;

    private List<String> lista_logopedisti = new ArrayList<>();

    private Spinner spinner_log;

    private ArrayList<ArrayList<String>> matrice_dati_logopedisti = new ArrayList<>();

    private RecyclerView recyclerView;
    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_EMAIL = "username";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference_APPUNTAMENTI;
    private int id_appuntamentilast = 0;

    private String idgenitore = "";
    private String logopedistascelto = "";
    private AppuntamentoListview adapter;

    public static AppuntamentiFragment newInstance() {
        return new AppuntamentiFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appuntamenti, container, false);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        idgenitore = loadidgenitore();

        recyclerView = view.findViewById(R.id.recycler_view_appuntamenti);

        spinner_log = view.findViewById(R.id.logopedisti_spinner_app);

        database = FirebaseDatabase.getInstance();
        reference_APPUNTAMENTI = database.getReference().child("Appuntamento");

        recuperaapp(view);
        recuperalogopedisti();

        // Trova i riferimenti agli elementi del layout
        labelInserimentoData = view.findViewById(R.id.label_inserimentodata);
        dataPickerButton = view.findViewById(R.id.dataPicker);
        oraPickerButton  = view.findViewById(R.id.oraPicker);
        luogoEditText = view.findViewById(R.id.luogo);
        oraInserimentoData = view.findViewById(R.id.label_inserimentoora);
        motivazioneEditText = view.findViewById(R.id.motivazione_genitore);

        // Imposta un listener per l'evento di clic sul bottone "Seleziona data"
        dataPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Seleziona data di appuntamento")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection));
                        labelInserimentoData.setText("Data selezionata: " + date);
                    }
                });
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "tag");
            }
        });

        oraPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int ora = cal.get(Calendar.HOUR_OF_DAY);
                int minuto = cal.get(Calendar.MINUTE);

                // Crea e mostra il TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                oraInserimentoData.setText("Ora selezionata: " + hourOfDay + ":" + minute);
                            }
                        }, ora, minuto, true); // I valori true indicano se visualizzare l'ora e i minuti nel dialogo
                timePickerDialog.show();

            }
        });

        // Imposta un listener per l'evento di clic sul bottone "Aggiungi"
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupera i dati inseriti dall'utente
                String dataAppuntamento = labelInserimentoData.getText().toString();
                String oraAppuntamento = oraInserimentoData.getText().toString();
                String luogoAppuntamento = luogoEditText.getText().toString();
                String  motivazioneGenitore = motivazioneEditText.getText().toString();

                if (dataAppuntamento.isEmpty() || oraAppuntamento.isEmpty() || luogoAppuntamento.isEmpty() || motivazioneGenitore.isEmpty() || logopedistascelto.equals("") || logopedistascelto.equals("Seleziona")) {
                    // Mostra un messaggio di errore
                    String compcamp= getString(R.string.app_compilacampi);
                    Toast.makeText(requireActivity(), compcamp, Toast.LENGTH_SHORT).show();
                } else {

                    String datadapassare = "";
                    String taglia = " ";
                    String[] parti = dataAppuntamento.split(taglia);
                    datadapassare = parti[2];

                    String oradapassare = "";
                    parti = oraAppuntamento.split(taglia);
                    oradapassare = parti[2];

                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true){
                        addappuntamento(datadapassare,oradapassare,luogoAppuntamento,motivazioneGenitore);
                    }

                }
            }
        });

        spinner_log.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedValue = (String) parentView.getItemAtPosition(position);
                // Ora puoi utilizzare selectedValue come desideri, ad esempio stamparlo in un log
                Log.d("Dati", "Valore selezionato: " + selectedValue);
                logopedistascelto = selectedValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Gestisci il caso in cui nessun elemento è selezionato
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AppuntamentiViewModel.class);
        // TODO: Use the ViewModel
    }

    public void addappuntamento(String dataAppuntamento_carica,String oraAppuntamento_carica,String luogoAppuntamento_carica,String motivazioneGenitore_carica){

        Log.d("Dati","" + dataAppuntamento_carica);
        Log.d("Dati","" + oraAppuntamento_carica);
        Log.d("Dati","" + luogoAppuntamento_carica);
        Log.d("Dati","" + motivazioneGenitore_carica);
        Log.d("Dati","" + idgenitore);
        Log.d("Dati","" + logopedistascelto);

        String idlogscelto = "";
        String taglia = "CODICE:";
        String[] parti = logopedistascelto.split(taglia);
        idlogscelto = parti[1];

        HelperClassAppuntamentiGenitore helperClassAPP = new HelperClassAppuntamentiGenitore(dataAppuntamento_carica,"" + id_appuntamentilast, idgenitore,idlogscelto,luogoAppuntamento_carica,motivazioneGenitore_carica,oraAppuntamento_carica,"");
        reference_APPUNTAMENTI.child("" + id_appuntamentilast).setValue(helperClassAPP);

        String addappmes= getString(R.string.appuntamentoagg);

        Toast.makeText(requireContext(), addappmes, Toast.LENGTH_SHORT).show();

        id_appuntamentilast ++;
    }

    private void recuperalogopedisti(){

        final int[] contatore = {0};
        lista_logopedisti.add("Seleziona");

        reference = FirebaseDatabase.getInstance().getReference("Logopedista");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String nome = childSnapshot.child("nome").getValue(String.class);
                    String cognome = childSnapshot.child("cognome").getValue(String.class);
                    String codlogopedista = childSnapshot.child("codLogopedista").getValue(String.class);

                    lista_logopedisti.add(nome + " " + cognome + " CODICE:" + codlogopedista);

                    matrice_dati_logopedisti.add(new ArrayList<>());
                    matrice_dati_logopedisti.get(contatore[0]).add(nome);
                    matrice_dati_logopedisti.get(contatore[0]).add(cognome);
                    matrice_dati_logopedisti.get(contatore[0]).add(codlogopedista);
                    contatore[0]++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, lista_logopedisti);

                spinner_log.setAdapter(adapter);

                for (ArrayList<String> row : matrice_dati_logopedisti) {
                    for (String num : row) {
                        Log.d("ERROREINPUT",""+ num);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private String loadidgenitore() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedIdGen = sharedPreferences.getString(KEY_ID_FIREBASE_UTENTE, "");

        return savedIdGen;

    }

    private void recuperaapp(View viewfrag){
        reference_APPUNTAMENTI.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Appuntamento> appuntamentiList = new ArrayList<>();
                ArrayList<String> idap_list = new ArrayList<>();
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                adapter = new AppuntamentoListview(appuntamentiList);
                recyclerView.setAdapter(adapter);

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String data_db = childSnapshot.child("data").getValue(String.class);
                    String idappuntamento_db = childSnapshot.child("idappuntamento").getValue(String.class);
                    String idgenitore_db = childSnapshot.child("idgenitore").getValue(String.class);
                    String incontro_db = childSnapshot.child("incontro").getValue(String.class);
                    String ora_db = childSnapshot.child("ora").getValue(String.class);
                    String stato_db = childSnapshot.child("stato").getValue(String.class);

                    if(idgenitore_db.equals(idgenitore) && stato_db.equals("Accettato")){
                        Appuntamento appuntamento = new Appuntamento(data_db, ora_db, incontro_db, stato_db);
                        appuntamentiList.add(appuntamento);
                    }
                    idap_list.add(idappuntamento_db);
                }

                id_appuntamentilast = getMaxValueFromList(idap_list);
                id_appuntamentilast ++;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private int getMaxValueFromList(ArrayList<String> list) {
        int maxValue = Integer.MIN_VALUE;
        for (String value : list) {
            try {
                int intValue = Integer.parseInt(value);
                if (intValue > maxValue) {
                    maxValue = intValue;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return maxValue;
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