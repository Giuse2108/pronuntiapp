package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.example.gfm_pronuntiapp_appfinale_esame.ui.areagioco.AreaGiocoFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Home_Figlio extends AppCompatActivity {

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";

    private static final String KEY_BAMBINO_AREA_GIOCO = "id_bambino";

    private String bambino  ="";

    private TextView textbenv;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private CardView gioco_card;
    private CardView negozio_card;

    private CardView homegenitore_card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_figlio);

        loadlingua();
        bambino = loadidbambino();

        textbenv = findViewById(R.id.textbenvenuto);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Bambino");

        recuperadatibambino();

        gioco_card = findViewById(R.id.cardgioco);
        negozio_card = findViewById(R.id.cardshop);
        homegenitore_card = findViewById(R.id.tornahome_genitore);

        gioco_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Figlio.this, ScegliPersonaggio.class));
                finish();
            }
        });

        negozio_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Figlio.this, NegozioPersonaggi.class));
                finish();
            }
        });

        homegenitore_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Figlio.this, Home_Genitore.class));
                finish();
            }
        });

    }

    private void recuperadatibambino(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idbambdb = childSnapshot.child("id").getValue(String.class);
                    String nomebamdb = childSnapshot.child("nome").getValue(String.class);

                    if(bambino.equals(idbambdb)){
                        textbenv.setText("Benvenuto " + nomebamdb);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void loadlingua() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");

        String currentln = getLinguaCorrente(this);
        if(!lingua.equals(currentln)){
            applicamodifiche(lingua);
        }
    }

    private String loadidbambino() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String idbmload = sharedPreferences.getString(KEY_BAMBINO_AREA_GIOCO, "");

        return idbmload;
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