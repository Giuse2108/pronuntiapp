package com.example.gfm_pronuntiapp_appfinale_esame.ui.areaterapia;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
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
import java.util.Iterator;
import java.util.Locale;


public class AreaTerapiaFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private DatabaseReference reference3;
    private Button button;
    private Button button1;
    private Button button2;
    private ListView listView;
    private ArrayList<String> list = new ArrayList<>();
    private String oggettoSelezionato;
    private String esercizioSelezionato;
    private String data;
    private String codiceLogopedista="";
    private ArrayList<String> bambini = new ArrayList<String>();

    private ArrayList<String> denominazione = new ArrayList<String>();
    // private String[][] denominazione;
    private ArrayList<String> ripetizione = new ArrayList<String>();
    //private String[][] ripetizione;
    private ArrayList<String> riconoscimento = new ArrayList<String>();
    // private String[][] riconoscimento;
    private int idultimaterapia;
    private int contatore=0;


    private ArrayList<String> idEsercizio_denominazione = new ArrayList<String>();
    private ArrayList<String> nomeEsercizio_denominazione = new ArrayList<String>();

    private ArrayList<String> idEsercizio_ripetizione = new ArrayList<String>();
    private ArrayList<String> nomeEsercizio_ripetizione = new ArrayList<String>();

    private ArrayList<String> idEsercizio_riconoscimento = new ArrayList<String>();
    private ArrayList<String> nomeEsercizio_riconoscimento = new ArrayList<String>();

    private ArrayList<String> data_finaleArray = new ArrayList<String>();
    private ArrayList<String> ideserciziofinale_Array = new ArrayList<String>();

    private String idBambino_finale;

    private Spinner spinner;

    private ArrayList<String> idbambino_confronto = new ArrayList<String>();
    private ArrayList<String> nome_cognome = new ArrayList<String>();

    private ArrayAdapter arrayadapter;

    private AreaTerapiaViewModel mViewModel;

    private String selesmex;
    public static AreaTerapiaFragment newInstance() {
        return new AreaTerapiaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_terapia, container, false);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        selesmex = getString(R.string.areaterap_seles);
        codiceLogopedista = recuperacodicelogopedista();
        Log.d("Dati","CodLogopedistaLoggato:" + codiceLogopedista);

        spinner= view.findViewById(R.id.spinner_bambini);

        reference = FirebaseDatabase.getInstance().getReference("Bambino");
        reference3 = FirebaseDatabase.getInstance().getReference("Terapia");
        recupera_terapia();
        bambini.add("Seleziona");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String nome = childSnapshot.child("nome").getValue(String.class);
                    String cognome = childSnapshot.child("cognome").getValue(String.class);
                    String codBambino= childSnapshot.child("id").getValue(String.class);
                    String codlogopedista = childSnapshot.child("idlogopedista").getValue(String.class);

                    if(codiceLogopedista.equals(codlogopedista))
                    {
                        bambini.add(nome +" "+cognome);
                        idbambino_confronto.add(codBambino);
                        nome_cognome.add(nome+" "+cognome);
                    }

                }

                carica_bambini_spinner();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });

        reference2 = FirebaseDatabase.getInstance().getReference("Esercizio");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String nomeEsercizio= childSnapshot.child("nome").getValue(String.class);
                    String codEsercizio= childSnapshot.child("idEsercizio").getValue(String.class);
                    String tipologiaEsercizio= childSnapshot.child("tipologia").getValue(String.class);
                    String codlogopedista = childSnapshot.child("idLogopedista").getValue(String.class);


                    if(codiceLogopedista.equals(codlogopedista) && tipologiaEsercizio.equals("Denominazione"))
                    {
                        //Log.d("Dati","aaa:"+codEsercizio+" "+nomeEsercizio);
                        denominazione.add(nomeEsercizio);

                        idEsercizio_denominazione.add(codEsercizio);
                        nomeEsercizio_denominazione.add(nomeEsercizio);
                    }
                    else if (codiceLogopedista.equals(codlogopedista) && tipologiaEsercizio.equals("Ripetizione"))
                    {
                        ripetizione.add(nomeEsercizio);

                        idEsercizio_ripetizione.add(codEsercizio);
                        nomeEsercizio_ripetizione.add(nomeEsercizio);
                    }
                    else if (codiceLogopedista.equals(codlogopedista) && tipologiaEsercizio.equals("Riconoscimento"))
                    {
                        riconoscimento.add(nomeEsercizio);

                        idEsercizio_riconoscimento.add(codEsercizio);
                        nomeEsercizio_riconoscimento.add(nomeEsercizio);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });

        //GESTIONE DELLA LISTVIEW PER ESERCIZI ASSEGNATI
        listView = view.findViewById(R.id.listassegnati);
        arrayadapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayadapter);


        //GESTIONE DEL BUTTON CHE PERMETTE DI ACCEDERE ALLA LISTA DEGLI ESERCIZI
        button=(Button) view.findViewById(R.id.aggiungi_esercizio);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SELETTORE DELLA DATA DI SCADENZA DEGLI ESERCIZI
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Seleziona la scadenza dell'esercizio")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date(selection));
                        Log.d("data","" + MessageFormat.format("Data selezionata: {0}", date));
                        data=MessageFormat.format("{0}", date);
                        mostraPopup();
                    }
                });
                // materialDatePicker.show(requireActivity().getSupportFragmentManager(), "tag");
                materialDatePicker.show(requireActivity().getSupportFragmentManager(),"tag");

            }
        });


        //BUTTON CHE PERMETTE DI CARICARE LA TERAPIA SUL DB
        button1=(Button) view.findViewById(R.id.conferma_terapia);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                for(int i=0; i<contatore; i++)
                {
                    Log.d("Dati", "Data finale:"+data_finaleArray.get(i));
                    Log.d("Dati", "Id esercizio:"+ideserciziofinale_Array.get(i));
                    Log.d("Dati", "contatore1:"+i);
                    Log.d("Dati", "contatore2:"+contatore);
                    Log.d("Dati", "idbambino:"+idBambino_finale);

                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true) {
                        String replacedDATA = data_finaleArray.get(i).replace("-", "/");
                        idultimaterapia = idultimaterapia + 1;
                        HelperClassAreaTerapiaLogopedista helperClassTerLog = new HelperClassAreaTerapiaLogopedista("", replacedDATA, "" + idBambino_finale, "" + ideserciziofinale_Array.get(i), "" + idultimaterapia, "", "");
                        reference3.child("" + idultimaterapia).setValue(helperClassTerLog);
                    }


                }

                String complterapmex = getString(R.string.areaterap_complterap);

                Toast.makeText(requireContext(), complterapmex, Toast.LENGTH_SHORT).show();
                clearTerapia();

            }
        });

        button2 = (Button) view.findViewById(R.id.annulla_terapia);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearTerapia();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AreaTerapiaViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        // Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();



        for (int c=0; c<idbambino_confronto.size();c++)
        {
            String confronto_nome_cognome=nome_cognome.get(c);
            if (text.equals(confronto_nome_cognome))
            {
                idBambino_finale=idbambino_confronto.get(c);
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //FUNZIONE CHE MOSTRA IL POPUP PER LA SELEZIONE DEGLI ESERCIZI
    private void mostraPopup() {

        final String[] oggetti = {"Denominazione", "Ripetizione", "Riconoscimento"};

        String tipesmex = getString(R.string.areaterap_seltipes);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(tipesmex)
                .setItems(oggetti, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        oggettoSelezionato = oggetti[which];

                        if(oggettoSelezionato==oggetti[0])
                        {
                            esercizitipo1();
                        } else if (oggettoSelezionato==oggetti[1]) {
                            esercizitipo2();
                        } else if (oggettoSelezionato==oggetti[2]) {
                            esercizitipo3();
                        }

                        // Visualizza un Toast con l'oggetto selezionato
                        //Toast.makeText(AreaTerapiaLogopedista.this, "Hai selezionato: " + oggettoSelezionato, Toast.LENGTH_SHORT).show();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void esercizitipo1()
    {
        //Log.d("Dati", "x:"+denominazione);
        //final String[] oggettit1 = {"Esercizio 1", "Esercizio 2", "Esercizio 3"};
        String[] denominazioneArray = denominazione.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(selesmex)
                .setItems(denominazioneArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        esercizioSelezionato = denominazioneArray[which];

                        aggiungi();

                        // Visualizza un Toast con l'oggetto selezionato
                        //Toast.makeText(AreaTerapiaLogopedista.this, "Hai selezionato: " + esercizioSelezionato, Toast.LENGTH_SHORT).show();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void esercizitipo2()
    {
        //final String[] oggettit2 = {"Esercizio 4", "Esercizio 5", "Esercizio 6"};

        String[] ripetizioneArray = ripetizione.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(selesmex)
                .setItems(ripetizioneArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        esercizioSelezionato = ripetizioneArray[which];

                        aggiungi();

                        // Visualizza un Toast con l'oggetto selezionato
                        //Toast.makeText(AreaTerapiaLogopedista.this, "Hai selezionato: " + esercizioSelezionato, Toast.LENGTH_SHORT).show();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void esercizitipo3()
    {
        //final String[] oggettit3 = {"Esercizio 7", "Esercizio 8", "Esercizio 9"};

        String[] riconoscimentoArray = riconoscimento.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(selesmex)
                .setItems(riconoscimentoArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        esercizioSelezionato = riconoscimentoArray[which];

                        aggiungi();

                        // Visualizza un Toast con l'oggetto selezionato
                        // Toast.makeText(AreaTerapiaLogopedista.this, "Hai selezionato: " + esercizioSelezionato, Toast.LENGTH_SHORT).show();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //FUNZIONE CHE AGGIUNGE L'ESERCIZIO E LA DATA SELEZIONATI ALLA LISTA DEGLI ESERCIZI ASSEGNATI
    private void aggiungi() {
        Log.d("Errore", "funzione");
        list.add(oggettoSelezionato + " - "+ esercizioSelezionato + " - " + data); // Aggiungi l'oggetto selezionato alla lista
        contatore++;
        if (oggettoSelezionato.equals("Denominazione"))
        {
            for (int i=0; i<nomeEsercizio_denominazione.size();i++)
            {
                String eserciziodn=nomeEsercizio_denominazione.get(i);
                String iddn=idEsercizio_denominazione.get(i);
                if (esercizioSelezionato.equals(eserciziodn))
                {
                    data_finaleArray.add(data);
                    ideserciziofinale_Array.add(iddn);
                }
            }
        }
        else if (oggettoSelezionato.equals("Ripetizione"))
        {
            for (int i=0; i<nomeEsercizio_ripetizione.size();i++)
            {
                String esercizior=nomeEsercizio_ripetizione.get(i);
                String idr=idEsercizio_ripetizione.get(i);
                if (esercizioSelezionato.equals(esercizior))
                {
                    data_finaleArray.add(data);
                    ideserciziofinale_Array.add(idr);
                }
            }
        }
        else if (oggettoSelezionato.equals("Riconoscimento"))
        {
            for (int i=0; i<nomeEsercizio_riconoscimento.size();i++)
            {
                String eserciziori=nomeEsercizio_riconoscimento.get(i);
                String idri=idEsercizio_riconoscimento.get(i);
                if (esercizioSelezionato.equals(eserciziori))
                {
                    data_finaleArray.add(data);
                    ideserciziofinale_Array.add(idri);
                }
            }
        }
        ArrayAdapter arrayAdapter = (ArrayAdapter) listView.getAdapter(); // Ottieni l'ArrayAdapter dalla ListView
        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged(); // Aggiorna la ListView
        }
    }

    private void carica_bambini_spinner(){


        ArrayAdapter adapter= new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, bambini);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void recupera_terapia(){

        ArrayList<String> terapia = new ArrayList<String>();

        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {


                    String codTerapia= childSnapshot.child("idTerapia").getValue(String.class);

                    terapia.add(codTerapia);

                }
                idultimaterapia= Integer.parseInt(terapia.get(terapia.size()-1));
                Log.d("Dati","idT:"+idultimaterapia);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void clearTerapia()
    {
        Iterator<String> iterator = list.iterator(); // Usa il tipo appropriato invece di Object
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        arrayadapter.notifyDataSetChanged();

        data_finaleArray.clear();
        ideserciziofinale_Array.clear();
        contatore=0;
    }

    private String recuperacodicelogopedista() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String logocd = sharedPreferences.getString(KEY_ID_FIREBASE_UTENTE, "");

        return logocd;

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