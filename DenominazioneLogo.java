package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gfm_pronuntiapp_appfinale_esame.ui.areaterapia.HelperClassAreaTerapiaLogopedista;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class DenominazioneLogo extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference storageReference_immagine;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference_DENOMINAZIONE;
    private DatabaseReference reference_TERAPIA;
    private DatabaseReference reference_BAMBINO;
    private String idEsercizioPassati;

    private TextView labelnome;
    private TextView labeltipologia;
    private TextView labelmonete;

    private String idTerapia = "";
    private String idEsercizio = "";
    private ImageView imgview;

    private String immaginedacaricare;
    private Button playButton;

    private Button buttoncorretto;
    private Button buttonerrato;

    private ArrayList<String> terapia_correzione = new ArrayList<>();
    private ArrayList<String> terapia_data = new ArrayList<>();
    private ArrayList<String> terapia_idBambino = new ArrayList<>();
    private ArrayList<String> terapia_idEsericzio = new ArrayList<>();
    private ArrayList<String> terapia_numaiuti = new ArrayList<>();
    private ArrayList<String> terapia_risposta = new ArrayList<>();


    private ArrayList<String> tabbambino_cognome = new ArrayList<>();
    private ArrayList<String> tabbambino_datanascita = new ArrayList<>();
    private ArrayList<String> tabbambino_id = new ArrayList<>();
    private ArrayList<String> tabbambino_idgenitore = new ArrayList<>();
    private ArrayList<String> tabbambino_idlogopedista = new ArrayList<>();
    private ArrayList<String> tabbambino_monete = new ArrayList<>();
    private ArrayList<String> tabbambino_nome = new ArrayList<>();
    private int monetedaaggiungere = 0;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        setContentView(R.layout.activity_denominazione_logo);

        idEsercizioPassati = getIntent().getStringExtra("ideserciziodenominazione");
        String[] parti = idEsercizioPassati.split(";");
        idEsercizio = parti[0];
        idTerapia = parti[1];


        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Esercizio");
        reference_DENOMINAZIONE = database.getReference("Denominazione");
        reference_TERAPIA = database.getReference("Terapia");
        reference_BAMBINO = database.getReference("Bambino");

        labelnome = findViewById(R.id.labelnomedenominazione);
        labeltipologia = findViewById(R.id.labeltipologiadenominazione);
        labelmonete = findViewById(R.id.labelmonetedenominazione);
        imgview = findViewById(R.id.imageviewesdenominazione);

        labeltipologia.setText("Tipologia: Denominazione immagini");

        firebaseStorage = FirebaseStorage.getInstance();

        recuperadatiTerapia();
        recuperadatiesercizio();

        playButton = findViewById(R.id.playeserciziodenominazione);
        playButton.setEnabled(false);

        playButton.setOnClickListener(v -> playAudio());

        buttoncorretto = findViewById(R.id.button_corretto_denominazione);
        buttonerrato = findViewById(R.id.button_errato_denominazione);

        buttoncorretto.setOnClickListener(v -> correggies("Corretto"));
        buttonerrato.setOnClickListener(v -> correggies("Errato"));
    }

    private void recuperadatibmabino(){
        reference_BAMBINO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String IDBAMBINO = terapia_idBambino.get(0);
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String cognome_bamb = childSnapshot.child("cognome").getValue(String.class);
                    String datanascita_bamb = childSnapshot.child("datanascita").getValue(String.class);
                    String id_bamb = childSnapshot.child("id").getValue(String.class);
                    String idgenitore_bamb = childSnapshot.child("idgenitore").getValue(String.class);
                    String idlogopedista_bamb = childSnapshot.child("idlogopedista").getValue(String.class);
                    String monete_bamb = childSnapshot.child("monete").getValue(String.class);
                    String nome_bamb = childSnapshot.child("nome").getValue(String.class);

                    if(id_bamb.equals(IDBAMBINO)) {
                        tabbambino_cognome.add(cognome_bamb);
                        tabbambino_datanascita.add(datanascita_bamb);
                        tabbambino_id.add(id_bamb);
                        tabbambino_idgenitore.add(idgenitore_bamb);
                        tabbambino_idlogopedista.add(idlogopedista_bamb);
                        tabbambino_monete.add(monete_bamb);
                        tabbambino_nome.add(nome_bamb);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void correggies(String valore){
        boolean connessoainternet = connessioneinternet();
        if(connessoainternet == true) {
            String loaddata = terapia_data.get(0);
            String loadidbambino = terapia_idBambino.get(0);
            String loadidesercizio = terapia_idEsericzio.get(0);
            String loadnumaiuti = terapia_numaiuti.get(0);
            String loadrisposta = terapia_risposta.get(0);

            Log.d("Firebase", "" + loaddata);
            Log.d("Firebase", "" + loadidbambino);
            Log.d("Firebase", "" + loadidesercizio);
            Log.d("Firebase", "" + loadnumaiuti);
            Log.d("Firebase", "" + loadrisposta);
            Log.d("Firebase", "" + idTerapia);

            if (valore != null && loaddata != null && loadidbambino != null && loadidesercizio != null && idTerapia != null && loadnumaiuti != null && loadrisposta != null) {

                HelperClassAreaTerapiaLogopedista helperClassTer = new HelperClassAreaTerapiaLogopedista(valore, loaddata, loadidbambino, loadidesercizio, idTerapia, loadnumaiuti, loadrisposta);
                reference_TERAPIA.child(idTerapia).setValue(helperClassTer)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firebase", "Dati salvati con successo");

                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Errore nel salvare i dati", e);
                        });

                if (valore.equals("Corretto")) {
                    String load_cogn = tabbambino_cognome.get(0);
                    String load_datanascita = tabbambino_datanascita.get(0);
                    String load_id = tabbambino_id.get(0);
                    String load_idgenitore = tabbambino_idgenitore.get(0);
                    String load_idlogo = tabbambino_idlogopedista.get(0);
                    String load_monete = tabbambino_monete.get(0);
                    String load_nome = tabbambino_nome.get(0);

                    int moneteFinali = 0;
                    int monbm = Integer.parseInt(load_monete);
                    moneteFinali = monetedaaggiungere + monbm;

                    HelperClassBambino helperBamb = new HelperClassBambino(load_cogn, load_datanascita, load_id, load_idgenitore, load_idlogo, "" + moneteFinali, load_nome);
                    reference_BAMBINO.child(load_id).setValue(helperBamb)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firebase", "Dati salvati con successo");
                                redirect();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firebase", "Errore nel salvare i dati", e);
                            });
                } else {
                    redirect();
                }

            } else {
                Log.e("Firebase", "Uno o più valori sono null");
            }
        }

    }

    public void redirect(){

        String mx_audiocor = getString(R.string.eserciziocorretto);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mx_audiocor)
                .setMessage("")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DenominazioneLogo.this, Home_Genitore.class));
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void recuperadatiesercizio(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String nome = childSnapshot.child("nome").getValue(String.class);
                    String id = childSnapshot.child("idEsercizio").getValue(String.class);

                    if(id.equals(idEsercizio)) {
                        labelnome.setText("Nome: " +nome);
                    }
                }

                reference_DENOMINAZIONE.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String idesercizioden = childSnapshot.child("idEsercizio_DEN").getValue(String.class);
                            String immagineDEN = childSnapshot.child("immagine").getValue(String.class);
                            String ricom = childSnapshot.child("ricompensa").getValue(String.class);

                            if(idesercizioden.equals(idEsercizio)) {
                                labelmonete.setText("Monete: " +ricom);

                                monetedaaggiungere = Integer.parseInt(ricom);
                                immaginedacaricare = "" + immagineDEN;
                                storageReference_immagine = firebaseStorage.getReference().child("foto esercizi/"+ immaginedacaricare);

                                storageReference_immagine.getDownloadUrl().addOnSuccessListener(uri ->
                                        Glide.with(DenominazioneLogo.this)
                                                .load(uri)
                                                .into(imgview)
                                ).addOnFailureListener(exception -> {});
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void recuperadatiTerapia(){
        reference_TERAPIA.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String correzione = childSnapshot.child("correzione").getValue(String.class);
                    String data = childSnapshot.child("data").getValue(String.class);
                    String idBambino = childSnapshot.child("idBambino").getValue(String.class);
                    String idEsericzio = childSnapshot.child("idEsercizio").getValue(String.class);
                    String idtep = childSnapshot.child("idTerapia").getValue(String.class);
                    String numaiuti = childSnapshot.child("num_aiuti_util").getValue(String.class);
                    String risposta = childSnapshot.child("risposta").getValue(String.class);

                    if(idtep.equals(idTerapia)) {
                        terapia_correzione.add(correzione);
                        terapia_data.add(data);
                        terapia_idBambino.add(idBambino);
                        terapia_idEsericzio.add(idEsericzio);
                        terapia_numaiuti.add(numaiuti);
                        terapia_risposta.add(risposta);

                        storageReference = firebaseStorage.getReference().child("audio risposte bambini/" + risposta);
                        playButton.setEnabled(true);
                    }
                }

                recuperadatibmabino();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void playAudio() {

        String playaudio_mx = getString(R.string.riproduzioneaudio);
        String playaudioerr_mx = getString(R.string.riproduzioneaudiofallita);


        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String audioUrl = uri.toString();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(DenominazioneLogo.this, playaudio_mx, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Audio", "Error initializing MediaPlayer", e);
                Toast.makeText(DenominazioneLogo.this, playaudioerr_mx, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            Log.e("Audio", "Error getting audio URL", exception);
            Toast.makeText(DenominazioneLogo.this, playaudioerr_mx, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private void loadlingua() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");

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

    public void onTopBarClicked(View view) {
        // Your desired action on clicking the top bar

        Intent intent = new Intent(DenominazioneLogo.this, Home_Genitore.class);
        startActivity(intent);

    }

    private boolean connessioneinternet(){

        if (NetworkUtils.isConnectedToInternet(this)) {
            return true;
        } else {
            String nocon = getString(R.string.no_internet);
            Toast.makeText(this, nocon, Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}