package com.example.gfm_pronuntiapp_appfinale_esame.ui.addfiglio;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFiglioFragment extends Fragment {

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_EMAIL = "username";
    private static final String KEY_LINGUA = "lingua";

    private AddFiglioViewModel mViewModel;
    private Button reg_btn;
    private EditText nome_bambino_input;
    private EditText cognome_bambino_input;
    private MaterialButton datapicker;
    private TextView visulizzadata;

    private List<String> lista_logopedisti = new ArrayList<>();

    private Spinner spinner_log;

    private ArrayList<ArrayList<String>> matrice_dati_logopedisti = new ArrayList<>();

    private String idgenitore;

    FirebaseDatabase database;
    DatabaseReference reference;

    private DatabaseReference reference_acquisto;


    public static AddFiglioFragment newInstance() {
        return new AddFiglioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_figlio, container, false);

        loadlingua();
        boolean connessoainternet = connessioneinternet();

        reg_btn = view.findViewById(R.id.reg_button);
        datapicker = view.findViewById(R.id.dataPicker);
        visulizzadata = view.findViewById(R.id.label_inserimentodata);
        nome_bambino_input = view.findViewById(R.id.input_nome_bambino);
        cognome_bambino_input = view.findViewById(R.id.input_cognome_bambino);
        spinner_log = view.findViewById(R.id.logopedisti_spinner);

        datapicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Seleziona data di nascita")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date(selection));
                        visulizzadata.setText(MessageFormat.format("Data selezionata: {0}", date));
                    }
                });
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "tag");
            }
        });

        recuperalogopedisti();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome_bambino = nome_bambino_input.getText().toString();
                String cognome_bambino = cognome_bambino_input.getText().toString();
                String data_bambino = visulizzadata.getText().toString();

                String data_bambino_finale = data_bambino.replace("Data selezionata: ", "");

                String logopedista_scelto = spinner_log.getSelectedItem().toString();

                if(!nome_bambino.equals("") && !cognome_bambino.equals("") && !data_bambino_finale.equals("Nessuna data selezionata") && !logopedista_scelto.equals("Seleziona"))
                {
                    String username_genitore = recuperoemailgen();
                    String codlop = codloprecupero(logopedista_scelto);

                    if(!username_genitore.equals(""))
                    {
                        boolean connessoainternet = connessioneinternet();
                        if(connessoainternet == true){
                            idgenrecupero(username_genitore,nome_bambino,cognome_bambino,data_bambino_finale,codlop);
                        }

                    }
                    else
                    {
                        Toast.makeText(requireContext(), "Errore account", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    String checkmex = getString(R.string.addfiglio_check);


                    Toast.makeText(requireContext(), checkmex, Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddFiglioViewModel.class);
        // TODO: Use the ViewModel
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String recuperoemailgen() {

        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedUsername = sharedPreferences.getString(KEY_EMAIL, "");

            return savedUsername;
        } else {
            return "";
        }

    }

    private void registrabambino(int username_genitore,String nome_bambino,String cognome_bambino,String data_bambino_finale, String codlogoped){
        Log.d("ERROREINPUT","ID GEN: "+username_genitore);
        Log.d("ERROREINPUT","NOME BAMB:" + nome_bambino);
        Log.d("ERROREINPUT","COGNOME BAMB:" +cognome_bambino);
        Log.d("ERROREINPUT","DATA BAMBINO:" +data_bambino_finale);
        Log.d("ERROREINPUT","COD LOG:"+codlogoped);

        ArrayList<Integer> vettoreIdBambini = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Bambino");

        final int[] massimo = {1};

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String nomeOggetto = snapshot.getKey();

                    vettoreIdBambini.add(Integer.valueOf(nomeOggetto));

                    Log.d("Firebase", "Nome dell'oggetto: " + nomeOggetto);


                }

                for (int numero : vettoreIdBambini) {
                    if (numero > massimo[0]) {
                        massimo[0] = numero;
                    }
                }

                int idbambinonuovo = massimo[0] + 1;
                Log.d("Firebase", "Max: " + idbambinonuovo);

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Bambino");

                HelperClassAddFiglio helperClassBam = new HelperClassAddFiglio("" + idbambinonuovo,"" + nome_bambino,"" + cognome_bambino,"" + data_bambino_finale,"0","" + codlogoped,"" + username_genitore);
                reference.child("" + idbambinonuovo).setValue(helperClassBam);

                addpersonaggio("" + idbambinonuovo);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Questo metodo viene chiamato se c'è un errore durante la lettura dei dati.
                Log.e("Firebase", "Errore durante il recupero dei dati: " + databaseError.getMessage());
            }
        });
    }

    private void addpersonaggio(String idbm){

        reference_acquisto = FirebaseDatabase.getInstance().getReference("Personaggio_Bambino");

        HelperClassPrimoPersonaggio helperClassPrimPers = new HelperClassPrimoPersonaggio("1",idbm);
        reference_acquisto.child("Topolino_"+idbm).setValue(helperClassPrimPers);

        String addfigliomex = getString(R.string.addfiglio_bambinoadd);

        Toast.makeText(requireContext(), addfigliomex, Toast.LENGTH_SHORT).show();
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

    private String codloprecupero(String sceltalogo){
        String[] parts = sceltalogo.split("CODICE:");

        Log.d("ERROREINPUT","CODICE LOGOPEDISTA INPUT BAMBINO:" + parts[1]);
        return parts[1];
    }

    private void idgenrecupero(String usergen,String nome_bambino,String cognome_bambino,String data_bambino_finale,String codlop){

        reference = FirebaseDatabase.getInstance().getReference("Genitore");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idgen_fire = childSnapshot.child("idgenitore").getValue(String.class);
                    String emailgen_fire = childSnapshot.child("email").getValue(String.class);

                    if(emailgen_fire.equals(usergen))
                    {
                        idgenitore = idgen_fire;
                    }
                }

                registrabambino(Integer.parseInt(idgenitore),nome_bambino,cognome_bambino,data_bambino_finale,codlop);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });

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