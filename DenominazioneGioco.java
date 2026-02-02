package com.example.gfm_pronuntiapp_appfinale_esame;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.button.MaterialButton;
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

public class DenominazioneGioco extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference_aiuto1;
    private StorageReference storageReference_aiuto2;
    private StorageReference storageReference_aiuto3;
    private StorageReference storageReference_immagine;
    private StorageReference storageReference_audio;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference_DENOMINAZIONE;
    private DatabaseReference reference_TERAPIA;
    private DatabaseReference reference_SCENARIO;
    private String idEsercizioPassati;

    private TextView labelnome;
    private TextView labeltipologia;
    private TextView labelmonete;

    private String idTerapia = "";
    private String idEsercizio = "";
    private ImageView imgview;

    private String immaginedacaricare;
    private Button playButton_aiuto1;
    private Button playButton_aiuto2;
    private Button playButton_aiuto3;

    private Button buttonconsegna;

    private ArrayList<String> terapia_correzione = new ArrayList<>();
    private ArrayList<String> terapia_data = new ArrayList<>();
    private ArrayList<String> terapia_idBambino = new ArrayList<>();
    private ArrayList<String> terapia_idEsericzio = new ArrayList<>();
    private ArrayList<String> terapia_idScenario = new ArrayList<>();
    private ArrayList<String> terapia_numaiuti = new ArrayList<>();
    private ArrayList<String> terapia_risposta = new ArrayList<>();

    private int aiutiusati = 0;

    private static final int REQUEST_PERMISSION_CODE = 200;

    private Button button_registra;
    private Button button_ascolta;

    boolean StartPlaying_audio = true;
    boolean StartRecording_audio = true;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String fileName_audio = null;
    private String stringapervariabile_fileName_audio = "";
    private ProgressDialog mProgress_audio;

    private boolean registraaudio = false;

    private boolean scnearioboolean = false;
    private String linkinseriti = "";

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denominazione_gioco);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        idEsercizioPassati = getIntent().getStringExtra("ideserciziodenominazione_gioco1");
        String[] parti = idEsercizioPassati.split(";");
        idEsercizio = parti[0];
        idTerapia = parti[1];

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Esercizio");
        reference_DENOMINAZIONE = database.getReference("Denominazione");
        reference_TERAPIA = database.getReference("Terapia");
        reference_SCENARIO = database.getReference("Scenario");

        labelnome = findViewById(R.id.labelnomedenominazione_gioco1);
        labeltipologia = findViewById(R.id.labeltipologiadenominazione_gioco1);
        labelmonete = findViewById(R.id.labelmonetedenominazione_gioco1);
        imgview = findViewById(R.id.imageviewesdenominazione_gioco1);

        labeltipologia.setText("Tipologia: Denominazione immagini");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference_audio =  firebaseStorage.getReference();

        recuperadatiTerapia();
        recuperadatiesercizio();
        recuperascenario();

        mProgress_audio = new ProgressDialog(DenominazioneGioco.this);
        playButton_aiuto1 = findViewById(R.id.aiutounoeserciziodenominazione_gioco1);
        playButton_aiuto1.setEnabled(false);
        playButton_aiuto1.setOnClickListener(v -> playAudio(storageReference_aiuto1,playButton_aiuto1));

        playButton_aiuto2 = findViewById(R.id.aiutodueeserciziodenominazione_gioco1);
        playButton_aiuto2.setEnabled(false);
        playButton_aiuto2.setOnClickListener(v -> playAudio(storageReference_aiuto2,playButton_aiuto2));

        playButton_aiuto3 = findViewById(R.id.aiutotreeserciziodenominazione_gioco1);
        playButton_aiuto3.setEnabled(false);
        playButton_aiuto3.setOnClickListener(v -> playAudio(storageReference_aiuto3,playButton_aiuto3));

        button_ascolta = findViewById(R.id.riascoltarispsostagioco1_gioco1);
        button_registra = findViewById(R.id.registrarisposta_gioco1);

        button_registra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName_audio = DenominazioneGioco.this.getExternalCacheDir().getAbsolutePath();
                fileName_audio += "/risposta_"+terapia_idBambino.get(0)+"_"+terapia_idEsericzio.get(0)+"_denominazione_"+idTerapia+".3gp";
                stringapervariabile_fileName_audio = "/risposta_"+terapia_idBambino.get(0)+"_"+terapia_idEsericzio.get(0)+"_denominazione_"+idTerapia+".3gp";

                if (ContextCompat.checkSelfPermission(DenominazioneGioco.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(StartRecording_audio,fileName_audio);
                    registraaudio = true;

                    if (StartRecording_audio) {
                        button_registra.setText("Ferma registrazione");
                    } else {
                        button_registra.setText("Inizia registrazione");
                    }
                    StartRecording_audio = !StartRecording_audio;
                } else {

                    String mx_mic_req = getString(R.string.permesso_microfono);

                    Toast.makeText(DenominazioneGioco.this, mx_mic_req, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_ascolta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(StartPlaying_audio,fileName_audio);
                if (StartPlaying_audio) {
                    button_ascolta.setText("Ferma ascolto");
                } else {
                    button_ascolta.setText("Inizia ascolto");
                }
                StartPlaying_audio = !StartPlaying_audio;
            }
        });

        buttonconsegna = findViewById(R.id.consegnaesercizio_gioco1);

        buttonconsegna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registraaudio == true){
                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true) {
                        caricaudio();
                    }
                }else {

                    String denregrisp_mx = getString(R.string.den_reg_risp);
                    Toast.makeText(DenominazioneGioco.this, denregrisp_mx, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Controlla se il permesso per registrare audio è già stato concesso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Richiedi il permesso
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        } else {
            // Il permesso è già stato concesso

            String dden_giacon_mx = getString(R.string.den_giacon);
            Toast.makeText(this, dden_giacon_mx, Toast.LENGTH_SHORT).show();
        }
    }

    private void caricaudio(){

        String mx_audioincan = getString(R.string.audioincar);
        mProgress_audio.setMessage(mx_audioincan);
        mProgress_audio.show();

        StorageReference filepath = storageReference_audio.child("audio risposte bambini").child("" + stringapervariabile_fileName_audio);

        Uri uri = Uri.fromFile(new File(fileName_audio));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress_audio.dismiss();
                inviarispsosta();
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

            char charToRemove = '/';
            String risp = stringapervariabile_fileName_audio.replace(Character.toString(charToRemove), "");

            HelperClassAreaTerapiaLogopedista helperClassTer = new HelperClassAreaTerapiaLogopedista("", loaddata, loadidbambino, loadidesercizio, idTerapia, "" + aiutiusati, risp);
            reference_TERAPIA.child(idTerapia).setValue(helperClassTer)
                    .addOnSuccessListener(aVoid -> {

                        Log.d("Firebase", "Dati salvati con successo");
                        if(scnearioboolean == true){
                            // Crea il layout inflater
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogLayout = inflater.inflate(R.layout.layout_popup_giochi, null);

                            // Trova il TextView nel layout personalizzato
                            TextView textViewDialogLinks = dialogLayout.findViewById(R.id.textViewDialogLinks);

                            String mx_link = getString(R.string.gioco_link);

                            // Dividi i link e crea la stringa HTML
                            String[] links = linkinseriti.split(",");
                            StringBuilder htmlText = new StringBuilder(mx_link +":<br>");
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
                        }else {
                            redirect();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Errore nel salvare i dati", e);
                    });
        }


    }

    public void redirect(){
        String mx_es_sv = getString(R.string.eserciziosvolto);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mx_es_sv)
                .setMessage("")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DenominazioneGioco.this, GiocoBambino.class));
                        finish();

                        Intent intent = new Intent(DenominazioneGioco.this, GiocoBambino.class);
                        //intent.putExtra("EXTRA_MESSAGE", "aggiorna");
                        startActivity(intent);
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
                            String aiuto1 = childSnapshot.child("aiuto1").getValue(String.class);
                            String aiuto2 = childSnapshot.child("aiuto2").getValue(String.class);
                            String aiuto3 = childSnapshot.child("aiuto3").getValue(String.class);

                            if(idesercizioden.equals(idEsercizio)) {
                                labelmonete.setText("Monete: " +ricom);
                                immaginedacaricare = "" + immagineDEN;
                                storageReference_immagine = firebaseStorage.getReference().child("foto esercizi/"+ immaginedacaricare);

                                storageReference_immagine.getDownloadUrl().addOnSuccessListener(uri ->
                                        Glide.with(DenominazioneGioco.this)
                                                .load(uri)
                                                .into(imgview)
                                ).addOnFailureListener(exception -> {});


                                storageReference_aiuto1 = firebaseStorage.getReference().child("audio esercizi/" + aiuto1);
                                playButton_aiuto1.setEnabled(true);

                                storageReference_aiuto2 = firebaseStorage.getReference().child("audio esercizi/" + aiuto2);
                                playButton_aiuto2.setEnabled(true);

                                storageReference_aiuto3 = firebaseStorage.getReference().child("audio esercizi/" + aiuto3);
                                playButton_aiuto3.setEnabled(true);
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void playAudio(StorageReference strg, Button btn) {

        String mx_audio = getString(R.string.audiook);
        String mx_noaudio = getString(R.string.audioerr);

        btn.setEnabled(false);
        aiutiusati ++;
        strg.getDownloadUrl().addOnSuccessListener(uri -> {
            String audioUrl = uri.toString();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(DenominazioneGioco.this, mx_audio, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Audio", "Error initializing MediaPlayer", e);
                Toast.makeText(DenominazioneGioco.this, mx_noaudio, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            Log.e("Audio", "Error getting audio URL", exception);
            Toast.makeText(DenominazioneGioco.this, mx_noaudio, Toast.LENGTH_SHORT).show();
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

    private void onRecord(boolean start, String nomefileaudio) {
        if (start) {
            startRecording(nomefileaudio);
        } else {
            stopRecording();
        }
    }

    private void startRecording(String filename) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filename);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void onPlay(boolean start,String filenameplay) {
        if (start) {
            startPlaying(filenameplay);
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(String filenameplay) {
        player = new MediaPlayer();
        try {
            player.setDataSource(filenameplay);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if(player != null){
            player.release();
            player = null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        String mx_mic_con = getString(R.string.microfonook);
        String mx_mic_neg = getString(R.string.microfonoerr);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Il permesso è stato concesso
                Toast.makeText(this, mx_mic_con, Toast.LENGTH_SHORT).show();
            } else {

                // Il permesso è stato negato
                Toast.makeText(this, mx_mic_neg, Toast.LENGTH_SHORT).show();

                startActivity(new Intent(DenominazioneGioco.this, MainActivity.class));
                finish();
            }
        }
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

        Intent intent = new Intent(DenominazioneGioco.this, GiocoBambino.class);
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