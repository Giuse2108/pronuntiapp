package com.example.gfm_pronuntiapp_appfinale_esame.ui.visualizzaapplogo;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class VisualizzaAppLogoFragment extends Fragment {

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";
    private DatabaseReference reference;
    private DatabaseReference reference_2;
    private String idLogopedista = "";
    private LinearLayout Layout_visualizza_appuntamenti;
    private VisualizzaAppLogoViewModel mViewModel;

    public static VisualizzaAppLogoFragment newInstance() {
        return new VisualizzaAppLogoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_visualizza_app_logo, container, false);
        loadlingua();
        boolean connessoainternet = connessioneinternet();
        idLogopedista =  recuperacodicelogopedista();
        Log.d("Dati","CodLogopedistaLoggato:" + idLogopedista);


        Layout_visualizza_appuntamenti = view.findViewById(R.id.visualizza_appointmentsLayout_logo);

        caricaappuntamenti(inflater);

        return view;
    }

    private void caricaappuntamenti(LayoutInflater inflater){
        reference = FirebaseDatabase.getInstance().getReference("Appuntamento");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Itera attraverso tutti i nodi figlio
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {


                    String data_app = childSnapshot.child("data").getValue(String.class);
                    String idgenitoreapp = childSnapshot.child("idgenitore").getValue(String.class);
                    String incontro = childSnapshot.child("incontro").getValue(String.class);
                    String motivo = childSnapshot.child("motivo").getValue(String.class);
                    String ora = childSnapshot.child("ora").getValue(String.class);
                    String stato = childSnapshot.child("stato").getValue(String.class);

                    if(stato.equals("Accettato"))
                    {
                        reference_2 = FirebaseDatabase.getInstance().getReference("Genitore");

                        reference_2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot_GEN) {

                                // Itera attraverso tutti i nodi figlio
                                for (DataSnapshot childSnapshot : dataSnapshot_GEN.getChildren()) {
                                    // Ottieni i valori di email e password per ciascun nodo figlio

                                    String idgenitore = childSnapshot.child("idgenitore").getValue(String.class);
                                    String nomegenitore = childSnapshot.child("name").getValue(String.class);
                                    String cognomegenitore = childSnapshot.child("cognome").getValue(String.class);

                                    if(idgenitoreapp.equals(idgenitore))
                                    {
                                        View visualizza_appointmentView = inflater.inflate(R.layout.cardview_visualizza_appointment_logopedista, null);

                                        TextView nomeecogometxv = visualizza_appointmentView.findViewById(R.id.visualizza_nomecognomegenapp);
                                        TextView dataeoratxv = visualizza_appointmentView.findViewById(R.id.visualizza_dataoraapp);
                                        TextView motivotxv = visualizza_appointmentView.findViewById(R.id.visualizza_motivoapp);
                                        TextView luogotxv = visualizza_appointmentView.findViewById(R.id.visualizza_luogoapp);


                                        String datamex = getString(R.string.app_data);
                                        String motmex = getString(R.string.app_motivo);
                                        String oramex = getString(R.string.app_ora);
                                        String luogmex = getString(R.string.app_luogo);

                                        // Imposta i dati del genitore e dell'appuntamento nella CardView
                                        nomeecogometxv.setText(nomegenitore + " " + cognomegenitore);
                                        dataeoratxv.setText(datamex +":" + data_app + " "+oramex+":" + ora);
                                        motivotxv.setText(motmex +":" + motivo);
                                        luogotxv.setText(luogmex +":" + incontro);

                                        Layout_visualizza_appuntamenti.addView(visualizza_appointmentView);

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
                            }
                        });

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VisualizzaAppLogoViewModel.class);
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