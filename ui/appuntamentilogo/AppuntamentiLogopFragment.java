package com.example.gfm_pronuntiapp_appfinale_esame.ui.appuntamentilogo;


import androidx.lifecycle.ViewModelProvider;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import java.util.ArrayList;
import java.util.Locale;

public class AppuntamentiLogopFragment extends Fragment {

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";
    private AppuntamentiLogopViewModel mViewModel;
    private DatabaseReference reference;
    private DatabaseReference reference_2;
    private DatabaseReference reference_3;
    private LinearLayout appointmentsLayout;
    private String idLogopedista = "";

    private ArrayList<String> ALidapp = new ArrayList<>();
    private ArrayList<String> ALiddataapp = new ArrayList<>();
    private ArrayList<String> ALidgenitoreapp = new ArrayList<>();
    private ArrayList<String> ALincontroapp = new ArrayList<>();
    private ArrayList<String> ALmotivoapp = new ArrayList<>();
    private ArrayList<String> ALoraapp = new ArrayList<>();
    private ArrayList<String> ALnomegenitoreapp = new ArrayList<>();
    private ArrayList<String> ALcognomegenitoreapp = new ArrayList<>();

    public static AppuntamentiLogopFragment newInstance() {
        return new AppuntamentiLogopFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appuntamenti_logop, container, false);
        loadlingua();
        boolean connessoainternet = connessioneinternet();
        appointmentsLayout = view.findViewById(R.id.appointmentsLayout);
        idLogopedista = recuperacodicelogopedista();
        Log.d("Dati","CodLogopedistaLoggato:" + idLogopedista);
        recupera_appuntamenti(inflater);

        return view;
    }

    public void recupera_appuntamenti(LayoutInflater inflater) {
        reference = FirebaseDatabase.getInstance().getReference("Appuntamento");
        reference_3 = FirebaseDatabase.getInstance().getReference("Appuntamento");

        ValueEventListener appuntamentiListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Svuota le liste prima di riempirle nuovamente
                ALidapp.clear();
                ALiddataapp.clear();
                ALidgenitoreapp.clear();
                ALincontroapp.clear();
                ALmotivoapp.clear();
                ALoraapp.clear();
                ALnomegenitoreapp.clear();
                ALcognomegenitoreapp.clear();
                appointmentsLayout.removeAllViews(); // Rimuove tutte le view precedenti

                long appuntamenticount = dataSnapshot.getChildrenCount();
                Log.d("APP", "TOT APPUNTAMENTI:" + appuntamenticount);
                long appuntamentocorrente = 0;

                // Itera attraverso tutti i nodi figlio
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    appuntamentocorrente++;
                    Log.d("APP", "CURRENT APPUNTAMENTO:" + appuntamentocorrente);

                    // Ottieni i valori di email e password per ciascun nodo figlio
                    String idappunt = childSnapshot.child("idappuntamento").getValue(String.class);
                    String data_app = childSnapshot.child("data").getValue(String.class);
                    String idgenitoreapp = childSnapshot.child("idgenitore").getValue(String.class);
                    String incontro = childSnapshot.child("incontro").getValue(String.class);
                    String motivo = childSnapshot.child("motivo").getValue(String.class);
                    String ora = childSnapshot.child("ora").getValue(String.class);
                    String stato = childSnapshot.child("stato").getValue(String.class);

                    if (stato.equals("")) {
                        reference_2 = FirebaseDatabase.getInstance().getReference("Genitore");

                        long finalAppuntamentocorrente = appuntamentocorrente;
                        reference_2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot_GEN) {
                                long genitoricount = dataSnapshot_GEN.getChildrenCount();
                                Log.d("APP", "TOT GEN:" + genitoricount);
                                long genitorecorrente = 0;

                                // Itera attraverso tutti i nodi figlio
                                for (DataSnapshot childSnapshot : dataSnapshot_GEN.getChildren()) {
                                    genitorecorrente++;
                                    // Ottieni i valori di email e password per ciascun nodo figlio

                                    String idgenitore = childSnapshot.child("idgenitore").getValue(String.class);
                                    String nomegenitore = childSnapshot.child("name").getValue(String.class);
                                    String cognomegenitore = childSnapshot.child("cognome").getValue(String.class);

                                    Log.d("APP", "NOMEGEN1:" + nomegenitore + " COGNOMEGEN:" + cognomegenitore + " IDAPP1:" + idappunt);

                                    if (idgenitoreapp.equals(idgenitore)) {
                                        ALidapp.add(idappunt);
                                        ALiddataapp.add(data_app);
                                        ALidgenitoreapp.add(idgenitore);
                                        ALincontroapp.add(incontro);
                                        ALmotivoapp.add(motivo);
                                        ALoraapp.add(ora);
                                        ALnomegenitoreapp.add(nomegenitore);
                                        ALcognomegenitoreapp.add(cognomegenitore);

                                        Log.d("APP", "NOMEGEN2:" + nomegenitore + " COGNOMEGEN:" + cognomegenitore);
                                    }

                                    if (finalAppuntamentocorrente == appuntamenticount && genitorecorrente == genitoricount) {
                                        Log.d("APP", "FINITO TUTTO");
                                        crea_cardview(inflater);
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
        };

        // Rimuovi l'ascoltatore precedente (se esiste) prima di aggiungerne uno nuovo
        if (reference != null) {
            reference.removeEventListener(appuntamentiListener);
        }
        reference.addValueEventListener(appuntamentiListener);
    }


    private void crea_cardview(LayoutInflater inflater)
    {
        int numeroElementiAL = ALidapp.size();

        ArrayList<Button> acceptButtonList = new ArrayList<>();
        ArrayList<Button> rejectButtonList = new ArrayList<>();
        ArrayList<View> cardviewList = new ArrayList<>();

        for(int elemento = 0; elemento < numeroElementiAL; elemento++){

            View appointmentView = inflater.inflate(R.layout.cardview_appointment, null);
            cardviewList.add(appointmentView);

            TextView nomeecogometxv = appointmentView.findViewById(R.id.nomecognomegenapp);
            TextView dataeoratxv = appointmentView.findViewById(R.id.dataoraapp);
            TextView motivotxv = appointmentView.findViewById(R.id.motivoapp);
            TextView luogotxv = appointmentView.findViewById(R.id.luogoapp);
            Button acceptButton = appointmentView.findViewById(R.id.acceptButton);
            Button rejectButton = appointmentView.findViewById(R.id.rejectButton);

            String datamex = getString(R.string.app_data);
            String motmex = getString(R.string.app_motivo);
            String oramex = getString(R.string.app_ora);
            String luogmex = getString(R.string.app_luogo);

            // Imposta i dati del genitore e dell'appuntamento nella CardView
            nomeecogometxv.setText(ALnomegenitoreapp.get(elemento) + " " + ALcognomegenitoreapp.get(elemento));
            dataeoratxv.setText(datamex + ":" + ALiddataapp.get(elemento) + " "+oramex+":" + ALoraapp.get(elemento));
            motivotxv.setText(motmex + ":" + ALmotivoapp.get(elemento));
            luogotxv.setText(luogmex+":" + ALincontroapp.get(elemento));

            acceptButtonList.add(acceptButton);
            rejectButtonList.add(rejectButton);
            appointmentsLayout.addView(appointmentView);
        }

        for(int elemento = 0; elemento < numeroElementiAL; elemento++){
            int finalElemento = elemento;
            acceptButtonList.get(finalElemento).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true) {
                        Log.d("APP", "ID_APP:" + ALidapp.get(finalElemento));
                        HelperClassAppointment helperClassApp = new HelperClassAppointment("" + ALiddataapp.get(finalElemento), "" + ALidapp.get(finalElemento), "" + ALidgenitoreapp.get(finalElemento), "" + idLogopedista, "" + ALincontroapp.get(finalElemento), "" + ALmotivoapp.get(finalElemento), "" + ALoraapp.get(finalElemento), "Accettato");
                        reference_3.child("" + ALidapp.get(finalElemento)).setValue(helperClassApp);
                        //animateCardView(cardviewList.get(finalElemento));
                        cardviewList.get(finalElemento).setVisibility(View.GONE);
                    }


                }
            });
            rejectButtonList.get(finalElemento).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true) {
                        Log.d("APP", "ID_APP:" + ALidapp.get(finalElemento));
                        HelperClassAppointment helperClassApp = new HelperClassAppointment("" + ALiddataapp.get(finalElemento), "" + ALidapp.get(finalElemento), "" + ALidgenitoreapp.get(finalElemento), "" + idLogopedista, "" + ALincontroapp.get(finalElemento), "" + ALmotivoapp.get(finalElemento), "" + ALoraapp.get(finalElemento), "Rifiutato");
                        reference_3.child("" + ALidapp.get(finalElemento)).setValue(helperClassApp);
                        animateCardView(cardviewList.get(finalElemento));
                    }
                }
            });
        }

    }

    // Metodo per eseguire l'animazione sulla CardView
    private void animateCardView(View cardView) {
        // Animazione per far scorrere la cardView fuori verso destra
        ObjectAnimator slideOutAnimation = ObjectAnimator.ofFloat(cardView, "translationX", 0f, cardView.getWidth());
        slideOutAnimation.setDuration(500); // Durata dell'animazione in millisecondi

        // Avvia entrambe le animazioni contemporaneamente
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideOutAnimation);
        animatorSet.start();

        // Nascondi la view della cardView che è stata fatta scorrere fuori
        slideOutAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AppuntamentiLogopViewModel.class);
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