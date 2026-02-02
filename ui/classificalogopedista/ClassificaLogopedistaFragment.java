package com.example.gfm_pronuntiapp_appfinale_esame.ui.classificalogopedista;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClassificaLogopedistaFragment extends Fragment {

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";

    private ClassificaLogopedistaViewModel mViewModel;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private ArrayList<String[]> matrice = new ArrayList<>();
    private String codiceLogopedista="";

    public static ClassificaLogopedistaFragment newInstance() {
        return new ClassificaLogopedistaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classifica_logopedista, container, false);
        loadlingua();
        boolean connessoainternet = connessioneinternet();
        codiceLogopedista = recuperacodicelogopedista();
        Log.d("Dati","CodLogopedistaLoggato:" + codiceLogopedista);

        ListView listView = view.findViewById(R.id.listclassifica);

        List<String> list = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Bambino");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String nome = childSnapshot.child("nome").getValue(String.class);
                    String cognome = childSnapshot.child("cognome").getValue(String.class);
                    String codlogopedista = childSnapshot.child("idlogopedista").getValue(String.class);
                    String monete = childSnapshot.child("monete").getValue(String.class);


                    if(codiceLogopedista.equals(codlogopedista))
                    {
                        matrice.add(new String[]{nome, cognome, monete});

                    }

                }


                // Converto la lista in un array bidimensionale per l'ordinamento
                String[][] matriceArray = matrice.toArray(new String[0][0]);

                // Ordino la matrice usando un comparatore personalizzato
                Arrays.sort(matriceArray, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] a, String[] b) {
                        return Integer.compare(Integer.parseInt(b[2]), Integer.parseInt(a[2]));
                    }
                });

                // Pulisco la lista originale e aggiungo la riga di intestazione
                matrice.clear();
                String nomarr = getString(R.string.class_nom);
                String cognarr = getString(R.string.class_cognom);
                String monarr = getString(R.string.class_mon);
                matrice.add(new String[]{nomarr, cognarr, monarr});

                // Aggiungo i valori ordinati alla lista
                matrice.addAll(Arrays.asList(matriceArray));

                CustomAdapter adapter = new CustomAdapter(getContext(), matrice);
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ClassificaLogopedistaViewModel.class);
        // TODO: Use the ViewModel
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