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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

public class Home_Genitore extends AppCompatActivity {

    private CardView aggiungifiglio;
    private CardView areagioco;
    private CardView logout;
    private CardView appuntamenti;
    private CardView cambial;
    private CardView correggies;

    private static final String PREFS_NAME = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_REDIRECT_PAGINA_GENITORE = "redirect";
    private static final String KEY_LINGUA = "lingua";
    private ImageView imgview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_genitore);

        loadlingua();

        aggiungifiglio = findViewById(R.id.card_aggiungifiglio);
        areagioco = findViewById(R.id.card_areagiocogen);
        logout = findViewById(R.id.esci_homegenitore);
        appuntamenti = findViewById(R.id.card_appuntamentigen);
        cambial = findViewById(R.id.cambialingua_activity);
        correggies = findViewById(R.id.correggies_homegenitore);

        correggies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Correggi esercizio");
                startActivity(new Intent(Home_Genitore.this, DashboardGenitore.class));
                finish();
            }
        });

        aggiungifiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Aggiungi figlio");
                startActivity(new Intent(Home_Genitore.this, DashboardGenitore.class));
                finish();
            }
        });

        areagioco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Area gioco");
                startActivity(new Intent(Home_Genitore.this, DashboardGenitore.class));
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Genitore.this, MainActivity.class));
                finish();
            }
        });

        appuntamenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salva_redirect("Appuntamenti");
                startActivity(new Intent(Home_Genitore.this, DashboardGenitore.class));
                finish();
            }
        });

        cambial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Genitore.this, Cambialingua_act.class);
                intent.putExtra("activity_cambio", "Genitore");
                startActivity(intent);
            }
        });
    }

    private void salva_redirect(String redirect) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_REDIRECT_PAGINA_GENITORE, redirect);
        editor.apply();
    }

    private void loadlingua() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");
        String currentln = getLinguaCorrente(this);
        if (!lingua.equals(currentln)) {
            applicamodifiche(lingua);
        }
    }

    private void applicamodifiche(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
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