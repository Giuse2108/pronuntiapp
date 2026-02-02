package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.example.gfm_pronuntiapp_appfinale_esame.ui.addfiglio.AddFiglioFragment;

import java.util.Locale;

public class Cambialingua_act extends AppCompatActivity {

    private Switch switch1;
    private Switch switch2;
    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";

    private String cambio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambialingua);

        cambio = getIntent().getStringExtra("activity_cambio");

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        switch1.setEnabled(false);
        switch2.setEnabled(false);

        loadlingua();

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch1.setEnabled(false);
                switch2.setEnabled(true);
                switch2.setChecked(false);
                cambialingua("it");
            }
        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch1.setEnabled(true);
                switch1.setChecked(false);
                switch2.setEnabled(false);
                cambialingua("en");
            }
        });
    }

    private void loadlingua() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");

        if(lingua.equals("it")){
            switch1.setChecked(true);
            switch2.setEnabled(true);
        }

        if(lingua.equals("en")){
            switch2.setChecked(true);
            switch1.setEnabled(true);
        }

        String currentln = getLinguaCorrente(Cambialingua_act.this);
        if(!lingua.equals(currentln)){
            cambialingua(lingua);
        }
    }

    private void cambialingua(String lng){

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_LINGUA, lng);
        editor.apply();

        applicamodifiche(lng);
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

    public void onTopBarClicked(View view) {
        // Your desired action on clicking the top bar
        if(cambio.equals("Logopedista"))
        {
            Intent intent = new Intent(Cambialingua_act.this, Home_Logopedista.class);
            startActivity(intent);
        }

        if(cambio.equals("Genitore")){
            Intent intent = new Intent(Cambialingua_act.this, Home_Genitore.class);
            startActivity(intent);
        }

    }
}