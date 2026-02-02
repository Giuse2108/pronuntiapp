package com.example.gfm_pronuntiapp_appfinale_esame;

import static android.content.ContentValues.TAG;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gfm_pronuntiapp_appfinale_esame.ui.areaterapia.HelperClassAreaTerapiaLogopedista;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class RiconoscimentoGioco extends AppCompatActivity {

    private CardView cardimg1;
    private CardView cardimg2;
    private MediaPlayer mediaPlayer;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference_IMG1;
    private StorageReference storageReference_IMG2;
    private StorageReference storageReference_audio;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference_RICONOSCIMENTO;
    private DatabaseReference reference_TERAPIA;
    private DatabaseReference reference_SCENARIO;
    private String idEsercizioPassati;

    private TextView labelnome;
    private TextView labeltipologia;
    private TextView labelmonete;

    private String idTerapia = "";
    private String idEsercizio = "";
    private ImageView imgview_1;
    private ImageView imgview_2;

    private String immaginedacaricare_1;
    private String immaginedacaricare_2;

    private Button buttonconsegna;
    private Button ascoltabtn;

    private String risp = "";
    private ArrayList<String> terapia_correzione = new ArrayList<>();
    private ArrayList<String> terapia_data = new ArrayList<>();
    private ArrayList<String> terapia_idBambino = new ArrayList<>();
    private ArrayList<String> terapia_idEsericzio = new ArrayList<>();
    private ArrayList<String> terapia_idScenario = new ArrayList<>();
    private ArrayList<String> terapia_numaiuti = new ArrayList<>();
    private ArrayList<String> terapia_risposta = new ArrayList<>();

    private boolean scnearioboolean = false;
    private String linkinseriti = "";

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riconoscimento_gioco);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        idEsercizioPassati = getIntent().getStringExtra("idesercizioriconoscimento_gioco3");
        String[] parti = idEsercizioPassati.split(";");
        idEsercizio = parti[0];
        idTerapia = parti[1];

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Esercizio");
        reference_RICONOSCIMENTO = database.getReference("Riconoscimento");
        reference_TERAPIA = database.getReference("Terapia");
        reference_SCENARIO = database.getReference("Scenario");

        labelnome = findViewById(R.id.labelnomeRICONOSCIMENTO_giocoes3);
        labeltipologia = findViewById(R.id.labeltipologiaRICONOSCIMENTO_giocoes3);
        labelmonete = findViewById(R.id.labelmoneteRICONOSCIMENTO_giocoes3);

        imgview_1 = findViewById(R.id.imgview1riconoscimento_giocoes3);
        imgview_2 = findViewById(R.id.imgview2riconoscimento_giocoes3);

        labeltipologia.setText("Tipologia: Riconoscimento di coppie minime");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference_audio =  firebaseStorage.getReference();

        cardimg1 = findViewById(R.id.cardviewimg1_gioco3);
        cardimg1.setCardBackgroundColor(getColorFromResource(R.color.my_primary));
        cardimg2 = findViewById(R.id.cardviewimg2_gioco3);
        cardimg2.setCardBackgroundColor(getColorFromResource(R.color.my_primary));

        recuperadatiTerapia();
        recuperadatiesercizio();
        recuperascenario();

        ascoltabtn = findViewById(R.id.ascoltaaudio_giocoes3);
        ascoltabtn.setEnabled(false);
        ascoltabtn.setOnClickListener(v -> playAudio());

        buttonconsegna = findViewById(R.id.button_consegnaesercizio_es3);

        buttonconsegna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!risp.equals("")){
                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true) {
                        inviarispsosta();
                    }
                }else {
                    String imgerr = getString(R.string.riconoscimento_img);

                    Toast.makeText(RiconoscimentoGioco.this, imgerr, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void inviarispsosta(){
        String loaddata = terapia_data.get(0);
        String loadidbambino = terapia_idBambino.get(0);
        String loadidesercizio = terapia_idEsericzio.get(0);
        String loadnumaiuti = terapia_numaiuti.get(0);
        String loadrisposta = terapia_risposta.get(0);


        if (loaddata != null && loadidbambino != null && loadidesercizio != null && idTerapia != null && loadnumaiuti != null && loadrisposta != null) {

            HelperClassAreaTerapiaLogopedista helperClassTer = new HelperClassAreaTerapiaLogopedista("", loaddata, loadidbambino, loadidesercizio, idTerapia, loadnumaiuti, risp);
            reference_TERAPIA.child(idTerapia).setValue(helperClassTer)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Dati salvati con successo");
                        if(scnearioboolean == true){
                            // Crea il layout inflater
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogLayout = inflater.inflate(R.layout.layout_popup_giochi, null);

                            // Trova il TextView nel layout personalizzato
                            TextView textViewDialogLinks = dialogLayout.findViewById(R.id.textViewDialogLinks);

                            String linkmx = getString(R.string.gioco_link);

                            // Dividi i link e crea la stringa HTML
                            String[] links = linkinseriti.split(",");
                            StringBuilder htmlText = new StringBuilder(linkmx + ":<br>");
                            for (String link : links) {
                                htmlText.append("<a href='").append(link).append("'>").append(link).append("</a><br>");
                            }

                            // Imposta il testo con HTML
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                textViewDialogLinks.setText(Html.fromHtml(htmlText.toString(), Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                textViewDialogLinks.setText(Html.fromHtml(htmlText.toString()));
                            }

                            // Abilita i link cliccabili
                            textViewDialogLinks.setMovementMethod(LinkMovementMethod.getInstance());

                            // Crea e mostra l'AlertDialog
                            new AlertDialog.Builder(this)
                                    .setTitle("")
                                    .setView(dialogLayout)
                                    .setPositiveButton("CHIUDI", (dialog, id) -> {
                                        Log.w(TAG, "sono entrato nell'onClick");
                                        dialog.dismiss();
                                        redirect();
                                    })
                                    .setNegativeButton("", (dialog, id) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        }
                        else {
                            redirect();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Errore nel salvare i dati", e);
                    });
        }


    }

    public void redirect(){
        String mxred = getString(R.string.eserciziosvolto);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mxred)
                .setMessage("")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RiconoscimentoGioco.this, GiocoBambino.class));
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

                reference_RICONOSCIMENTO.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String idesercizioric = childSnapshot.child("idEsercizio_RIC").getValue(String.class);
                            String audioRIC = childSnapshot.child("audio").getValue(String.class);
                            String img1RIC = childSnapshot.child("immagine1").getValue(String.class);
                            String img2RIC = childSnapshot.child("immagine2").getValue(String.class);
                            String ricom = childSnapshot.child("ricompensa").getValue(String.class);

                            if(idesercizioric.equals(idEsercizio)) {
                                labelmonete.setText("Monete: " +ricom);

                                immaginedacaricare_1 = "" + img1RIC;
                                storageReference_IMG1 = firebaseStorage.getReference().child("foto esercizi/"+ immaginedacaricare_1);

                                storageReference_IMG1.getDownloadUrl().addOnSuccessListener(uri ->
                                        Glide.with(RiconoscimentoGioco.this)
                                                .load(uri)
                                                .into(imgview_1)
                                ).addOnFailureListener(exception -> {});

                                // Aggiungi il listener per il click sull'ImageView
                                imgview_1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        risp = immaginedacaricare_1; // Salva il nome dell'immagine cliccata
                                        cardimg1.setCardBackgroundColor(getColorFromResource(R.color.GAME));
                                        cardimg2.setCardBackgroundColor(getColorFromResource(R.color.my_primary)); // Cambia il colore della seconda CardView a my_primary
                                        Toast.makeText(RiconoscimentoGioco.this, "IMG1: " + risp, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                immaginedacaricare_2 = "" + img2RIC;
                                storageReference_IMG2 = firebaseStorage.getReference().child("foto esercizi/"+ immaginedacaricare_2);

                                storageReference_IMG2.getDownloadUrl().addOnSuccessListener(uri ->
                                        Glide.with(RiconoscimentoGioco.this)
                                                .load(uri)
                                                .into(imgview_2)
                                ).addOnFailureListener(exception -> {});

                                // Aggiungi il listener per il click sull'ImageView
                                imgview_2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        risp = immaginedacaricare_2;
                                        cardimg2.setCardBackgroundColor(getColorFromResource(R.color.GAME));
                                        cardimg1.setCardBackgroundColor(getColorFromResource(R.color.my_primary)); // Cambia il colore della seconda CardView a my_primary
                                        Toast.makeText(RiconoscimentoGioco.this, "IMG2:"+ risp, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                storageReference_audio = firebaseStorage.getReference().child("audio esercizi/" + audioRIC);
                                ascoltabtn.setEnabled(true);


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

    private int getColorFromResource(@ColorRes int colorResource) {
        return ContextCompat.getColor(this, colorResource);
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void playAudio() {
        String playaudiomx = getString(R.string.audiook);
        String playaudioerrmx = getString(R.string.audioerr);

        storageReference_audio.getDownloadUrl().addOnSuccessListener(uri -> {
            String audioUrl = uri.toString();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(RiconoscimentoGioco.this, playaudiomx, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Audio", "Error initializing MediaPlayer", e);
                Toast.makeText(RiconoscimentoGioco.this, playaudioerrmx, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            Log.e("Audio", "Error getting audio URL", exception);
            Toast.makeText(RiconoscimentoGioco.this, playaudioerrmx, Toast.LENGTH_SHORT).show();
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

    private void recuperascenario(){

        reference_SCENARIO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int check = 0;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String descLINK = childSnapshot.child("descrizione").getValue(String.class);
                    String itTerapLINK = childSnapshot.child("idTerapia").getValue(String.class);
                    String tipologiaLINK = childSnapshot.child("tipologia").getValue(String.class);

                    if(itTerapLINK.equals(idTerapia) && tipologiaLINK.equals("Link")) {
                        scnearioboolean = true;
                        if(check == 0){
                            linkinseriti = descLINK;
                            check++;
                        }else {
                            linkinseriti = linkinseriti + "," + descLINK;
                        }

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

        Intent intent = new Intent(RiconoscimentoGioco.this, GiocoBambino.class);
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