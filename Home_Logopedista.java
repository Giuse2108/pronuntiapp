package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Locale;

public class Home_Logopedista extends AppCompatActivity {

    private CardView addesercizi;
    private CardView areaterapia;
    private CardView appuntamenti;
    private CardView classifica;
    private CardView ling;
    private CardView logout_logo;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_REDIRECT_PAGINA_GENITORE = "redirect";
    private static final String KEY_LINGUA = "lingua";

    private static final String KEY_ID_FIREBASE_UTENTE = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_logopedista);

        loadlingua();

        addesercizi = findViewById(R.id.card_addesercizio);
        areaterapia = findViewById(R.id.card_addterapia);
        appuntamenti = findViewById(R.id.card_appuntamentilogo);
        classifica = findViewById(R.id.card_classificalogo);
        ling = findViewById(R.id.cambialingua_activity_logo);
        logout_logo = findViewById(R.id.logout_activity_logo);

        ling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Logopedista.this, Cambialingua_act.class);
                intent.putExtra("activity_cambio", "Logopedista");
                startActivity(intent);
            }
        });

        addesercizi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Aggiungi esercizi");
                startActivity(new Intent(Home_Logopedista.this, DashboardLogopedista.class));
                finish();
            }
        });

        areaterapia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Area terapia");
                startActivity(new Intent(Home_Logopedista.this, DashboardLogopedista.class));
                finish();
            }
        });

        appuntamenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Appuntamenti");
                startActivity(new Intent(Home_Logopedista.this, DashboardLogopedista.class));
                finish();
            }
        });

        classifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Classifica");
                startActivity(new Intent(Home_Logopedista.this, DashboardLogopedista.class));
                finish();
            }
        });

        logout_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Logopedista.this, MainActivity.class));
                finish();
            }
        });



    }

    private void salva_redirect(String redirect){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_REDIRECT_PAGINA_GENITORE, redirect);

        editor.apply();
    }

    private void loadlingua() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");
        String id = sharedPreferences.getString(KEY_ID_FIREBASE_UTENTE, "");

        Log.d("Dati",""+id);

        String currentln = getLinguaCorrente(this);
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

        recreate();
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
}