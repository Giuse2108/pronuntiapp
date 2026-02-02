package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.LocalDate;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAdjusters;

public class GiocoBambino extends AppCompatActivity{

    private ImageView personaggioscelto; // serve per allineare i button creati successivamente al di sotto dell'immagine
    private LinearLayout linLay;
    int i;
    private float x, y;
    int numEsercizi = 10;
    private float initialX, initialY;
    float newX, newY;
    private int activeButtonIndex = 0; // Mantieni traccia del pulsante attivo corrente
    private List<FrameLayout> frameLayouts = new ArrayList<>();
    private boolean dialogShown = false;
    private List<ImageView> imageViews = new ArrayList<>(); // Aggiungi una lista per le ImageView
    private FirebaseDatabase db;
    private MediaPlayer mediaPlayer; // Oggetto per inserire il suono ad ogni livello
    private MediaPlayer levelUp;

    private String idbambinoGioco = "";
    private ConstraintLayout sfondo;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference_sfondo;
    private StorageReference storageReference_personaggio;

    private ColorMatrixColorFilter filter;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";
    private static final String KEY_BAMBINO_AREA_GIOCO = "id_bambino";
    private static final String KEY_BAMBINO_AREA_GIOCO_PERSONAGGIOSCELTO = "personaggio";

    private Button backbutotn;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadlingua();
        AndroidThreeTen.init(this);

        boolean connessoainternet = connessioneinternet();


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference_sfondo =  firebaseStorage.getReference();
        storageReference_personaggio =  firebaseStorage.getReference();

        setContentView(R.layout.activity_gioco_bambino);
        idbambinoGioco = loadidbambino();

        personaggioscelto = findViewById(R.id.imageViewPersonaggio);
        loadpersonaggio();

        linLay = findViewById(R.id.linLayout);

        linLay.setGravity(Gravity.CENTER_HORIZONTAL);

        //recupera i dati dal database
        db = FirebaseDatabase.getInstance();

        // Inizializza il MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.button_sound);
        levelUp = MediaPlayer.create(this, R.raw.levelup_button);

        loadsfondo();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        DatabaseReference terapiaEsercizi = db.getReference().child("Terapia");

        terapiaEsercizi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataSnapshot> filteredEsercizi = new ArrayList<>();
                ArrayList<String> idesercizigioco = new ArrayList<String>();
                ArrayList<String> risposteControlloGioco = new ArrayList<String>();

                for (DataSnapshot esercizioSnapshot : dataSnapshot.getChildren()) {
                    String idBambino = esercizioSnapshot.child("idBambino").getValue(String.class);
                    String risposta = esercizioSnapshot.child("risposta").getValue(String.class);
                    String esscelto = esercizioSnapshot.child("idEsercizio").getValue(String.class);
                    String terap = esercizioSnapshot.child("idTerapia").getValue(String.class);

                    String dataterap = esercizioSnapshot.child("data").getValue(String.class);

                    String replacedcheck_data;

                    replacedcheck_data = dataterap.replaceFirst("/", "-");
                    replacedcheck_data = replacedcheck_data.replaceFirst("/", "-");

                    String pattern = "dd-MM-yyyy"; // Pattern della data

                    boolean isInCurrentWeek = isDateInCurrentWeek(replacedcheck_data, pattern);
                    Log.d("Data", "La data " + dataterap + " è nella settimana corrente: " + isInCurrentWeek);


                    if (idbambinoGioco.equals(idBambino) && isInCurrentWeek == true) { //da cambiare per renderlo dinamico!!!
                        filteredEsercizi.add(esercizioSnapshot);
                        idesercizigioco.add(terap + ";" + esscelto);
                        risposteControlloGioco.add(risposta);

                    }
                    Log.w(TAG, "numero esercizi recuperati del bambino con id "+idbambinoGioco+": " + filteredEsercizi.size());
                }
                numEsercizi = filteredEsercizi.size();
                Log.w(TAG, "numero di esercizi per il bambino con id "+idbambinoGioco+": " + numEsercizi);
                for(i=0; i<numEsercizi; i++) {
                    DataSnapshot esercizioSnapshot = filteredEsercizi.get(i);
                    String idEsercizio =idesercizigioco.get(i);

                    // Crea un FrameLayout per combinare ImageView e TextView
                    FrameLayout frameLayout = new FrameLayout(GiocoBambino.this);
                    frameLayout.setTag(idEsercizio);

                    // Crea un'ImageView per l'immagine del pulsante
                    ImageView imageView = new ImageView(GiocoBambino.this);
                    Drawable drawable = ContextCompat.getDrawable(GiocoBambino.this, R.drawable.pulsante_esercizio);

                    // Applica il filtro di saturazione
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(0.5f); // Riduzione del 50% della saturazione

                    // Combina i filtri
                    ColorMatrix combinedMatrix = new ColorMatrix();
                    combinedMatrix.postConcat(colorMatrix); // Applica il filtro di saturazione

                    filter = new ColorMatrixColorFilter(combinedMatrix);
                    imageView.setColorFilter(filter);

                    // Imposta l'immagine con colore modificato nell'ImageView
                    imageView.setPadding(16, 16, 16, 16); // Padding per aggiungere spazio intorno all'immagine
                    imageView.setImageDrawable(drawable);

                    // Imposta dimensioni personalizzate per l'ImageView
                    FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                            dpToPx(100), // Larghezza desiderata in dp, convertita in px
                            dpToPx(100)  // Altezza desiderata in dp, convertita in px
                    );
                    imageLayoutParams.gravity = Gravity.CENTER; // Centra l'immagine all'interno del FrameLayout
                    imageView.setLayoutParams(imageLayoutParams);

                    frameLayout.addView(imageView);
                    frameLayouts.add(frameLayout);
                    imageViews.add(imageView); // Aggiungi l'ImageView alla lista
                    imageView.setTag(idEsercizio);

                    // Crea un TextView per il numero corrispondente al livello
                    TextView textView = new TextView(GiocoBambino.this);
                    textView.setText(String.valueOf(i + 1));
                    textView.setTextSize(20);
                    textView.setTextColor(Color.WHITE);
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setGravity(Gravity.CENTER); // Centra il testo sopra l'immagine

                    // Aggiungi il TextView al FrameLayout, sopra l'ImageView
                    FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    );
                    textLayoutParams.gravity = Gravity.CENTER; // Posiziona il TextView al centro dell'ImageView
                    frameLayout.addView(textView, textLayoutParams);

                    // Imposta il colore del pulsante in base al livello attivo
                    if (i <= activeButtonIndex ) {
                        imageView.setColorFilter(filter); // Colore verde per i livelli sbloccati
                    } else {

                        imageView.setColorFilter(Color.GRAY); // Colore grigio per i livelli bloccati
                    }

                    // Aggiungi il FrameLayout al LinearLayout
                    LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    frameLayoutParams.setMargins(0, 10, 0, 10);
                    linLay.addView(frameLayout, frameLayoutParams);


                    if (i != numEsercizi - 1) {
                        LinearLayout verticalLayout = new LinearLayout(GiocoBambino.this);
                        verticalLayout.setOrientation(LinearLayout.HORIZONTAL);
                        verticalLayout.setBackgroundColor(Color.GREEN);

                        for (int j = 0; j < 3; j++) {
                            View verticalView = new View(GiocoBambino.this);
                            verticalView.setLayoutParams(new LinearLayout.LayoutParams(13, 25));
                            verticalLayout.addView(verticalView);
                        }

                        // Set gravity on the vertical layout
                        LinearLayout.LayoutParams verticalLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        verticalLayoutParams.width = 13;
                        verticalLayoutParams.height = 50;
                        verticalLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                        linLay.addView(verticalLayout, verticalLayoutParams);
                    }

                    personaggioscelto.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    // Salva la posizione iniziale del tocco
                                    initialX = event.getRawX() - v.getX();
                                    initialY = event.getRawY() - v.getY();
                                    return true;

                                case MotionEvent.ACTION_MOVE:
                                    // Calcola la nuova posizione basata sul movimento del tocco
                                    newX = event.getRawX() - initialX;
                                    newY = event.getRawY() - initialY;

                                    ViewGroup parent = (ViewGroup) v.getParent();
                                    int scrollViewHeight = parent.getHeight();

                                    // Limita la nuova posizione entro i limiti dello schermo
                                    newX = Math.max(0, Math.min(newX, screenWidth - v.getWidth()));
                                    newY = Math.max(0, Math.min(newY, scrollViewHeight - v.getHeight()));

                                    // Imposta la nuova posizione di topolino
                                    v.setX(newX);
                                    v.setY(newY);

                                    // Disabilita l'intercettazione del tocco dello ScrollView se il personaggio è vicino ai bordi
                                    if (newY > scrollViewHeight - v.getHeight() || newY < 0) {
                                        v.getParent().requestDisallowInterceptTouchEvent(false);
                                    } else {
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                    }

                                    // Controlla la vicinanza ai FrameLayout (pulsanti)
                                    // Controlla la vicinanza ai FrameLayout (pulsanti)
                                    // Controlla la vicinanza ai FrameLayout (pulsanti)
                                    for (i = 0; i < frameLayouts.size(); i++) {
                                        FrameLayout frameLayout = frameLayouts.get(i);
                                        if (isViewOverlapping(personaggioscelto, frameLayout)) {
                                            String idEsercizio = (String) frameLayout.getTag(); // Recupera l'ID dell'esercizio dal tag

                                            if (i == activeButtonIndex) {
                                                // Solo se il FrameLayout corrente è l'attivo corrente
                                                // Riproduci il suono
                                                if (mediaPlayer != null) {
                                                    mediaPlayer.start();
                                                }

                                                if (!dialogShown) {
                                                    dialogShown = true; // Imposta il flag su true

                                                    try {
                                                        // Mostra il dialogo di conferma
                                                        String esgiocomex = getString(R.string.gioco_esercizio);
                                                        String esgiocosimex = getString(R.string.scper_scegli_si);


                                                        AlertDialog.Builder builder = new AlertDialog.Builder(GiocoBambino.this);
                                                        builder.setTitle(esgiocomex)
                                                                .setMessage("") // Mostra l'ID dell'esercizio
                                                                .setPositiveButton(esgiocosimex, new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        Log.w(TAG, "sono entrato nell'onClick");
                                                                        if (activeButtonIndex <= frameLayouts.size() - 1) {
                                                                            Log.w(TAG, "sono entrato nell'if dopo l'onClick");

                                                                            recuperatipologia(idEsercizio);

                                                                            // Cambia il colore del pulsante precedente a grigio
                                                                            imageViews.get(activeButtonIndex).setColorFilter(Color.GRAY);
                                                                            // Incrementa l'indice del pulsante attivo
                                                                            activeButtonIndex++;
                                                                            // Cambia il colore del nuovo pulsante attivo a verde
                                                                            imageViews.get(activeButtonIndex).setColorFilter(filter);
                                                                        }

                                                                        // Reimposta il flag dialogShown su false
                                                                        dialogShown = false;

                                                                        // Riproduci il suono
                                                                        if (levelUp != null) {
                                                                            levelUp.start();
                                                                        }
                                                                    }
                                                                })
                                                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        // Reimposta il flag dialogShown su false

                                                                        dialogShown = false;
                                                                        dialog.dismiss();
                                                                    }
                                                                });

                                                        // Mostra il dialogo
                                                        builder.create().show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else if (i > activeButtonIndex && !dialogShown) {

                                                dialogShown = true;

                                                String giocononpuoimsx = getString(R.string.gioco_nonpuoisv);
                                                String descmex = getString(R.string.gioco_descnonpuoi);

                                                AlertDialog.Builder builder = new AlertDialog.Builder(GiocoBambino.this);
                                                builder.setTitle(giocononpuoimsx)
                                                        .setMessage(descmex)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialogShown = false;
                                                            }
                                                        });

                                                builder.create().show();
                                            }
                                        }
                                    }


                                    return true;

                                case MotionEvent.ACTION_UP:
                                    break;
                            }
                            return false;
                        }
                    });

                }

                for(int y = 0; y < risposteControlloGioco.size(); y++){
                    String rispgioco = risposteControlloGioco.get(y);

                    if(rispgioco.equals("")){
                        activeButtonIndex = y;

                        // Cambia il colore del pulsante precedente a grigio
                        imageViews.get(0).setColorFilter(Color.parseColor("#178CC3"));

                        // Incrementa l'indice del pulsante attivo
                        // Cambia il colore del nuovo pulsante attivo a verde
                        imageViews.get(activeButtonIndex).setColorFilter(filter);
                        break;
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Errore durante l'accesso alla tabella Terapia", databaseError.toException());
            }
        });
    }

    public static boolean isDateInCurrentWeek(String dateString, String pattern) {
        // Crea un formatter per la data in base al pattern specificato
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // Converte la stringa in un oggetto LocalDate
        LocalDate inputDate = LocalDate.parse(dateString, formatter);

        // Ottiene la data corrente
        LocalDate now = LocalDate.now();

        // Ottiene il primo giorno della settimana corrente
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Ottiene l'ultimo giorno della settimana corrente
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Verifica se la data inserita è all'interno della settimana corrente
        return (inputDate.isEqual(startOfWeek) || inputDate.isAfter(startOfWeek)) &&
                (inputDate.isEqual(endOfWeek) || inputDate.isBefore(endOfWeek));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (levelUp != null) {
            levelUp.release();
            levelUp = null;
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // Metodo per verificare se il personaggio si sovrappone completamente al pulsante
    private boolean isViewOverlapping(View firstView, View secondView) {
        Rect firstRect = new Rect();
        firstView.getHitRect(firstRect);

        Rect secondRect = new Rect();
        secondView.getHitRect(secondRect);

        // Verifica se i bordi del personaggio sono contenuti all'interno dei bordi del pulsante
        return secondRect.contains(firstRect);

    }

    private void recuperatipologia(String parametri){

        String[] parametro = parametri.split(";");

        String idTerapia = parametro[0];
        String idEsercizio = parametro[1];


        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Esercizio");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String tipologiaEsercizio_Finale = "";
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String codEsercizio= childSnapshot.child("idEsercizio").getValue(String.class);
                    String tipologiaEsercizio = childSnapshot.child("tipologia").getValue(String.class);

                    if(codEsercizio.equals(idEsercizio))
                    {
                        tipologiaEsercizio_Finale = tipologiaEsercizio;
                    }


                }

                Log.d("Dati", "Tipologia:" + tipologiaEsercizio_Finale);

                if(tipologiaEsercizio_Finale.equals("Denominazione"))
                {

                    Intent intent = new Intent(GiocoBambino.this, DenominazioneGioco.class);
                    intent.putExtra("ideserciziodenominazione_gioco1", idEsercizio +";"+idTerapia);
                    startActivity(intent);
                }

                if(tipologiaEsercizio_Finale.equals("Ripetizione"))
                {
                    Intent intent = new Intent(GiocoBambino.this, RipetizioneGioco.class);
                    intent.putExtra("idesercizioripetizione_gioco2", idEsercizio +";"+idTerapia);
                    startActivity(intent);
                }

                if(tipologiaEsercizio_Finale.equals("Riconoscimento"))
                {
                    Intent intent = new Intent(GiocoBambino.this, RiconoscimentoGioco.class);
                    intent.putExtra("idesercizioriconoscimento_gioco3", idEsercizio +";"+idTerapia);
                    startActivity(intent);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void loadsfondo(){

        DatabaseReference scenario = db.getReference().child("Scenario");

        scenario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean cambiasfondo = false;
                ArrayList<String> sfondi_bambino = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String idbm_SCENARIO = childSnapshot.child("idBambino").getValue(String.class);
                    String file_SCENARIO = childSnapshot.child("file").getValue(String.class);
                    String tipologia_SCENARIO = childSnapshot.child("tipologia").getValue(String.class);

                    if(idbm_SCENARIO.equals(idbambinoGioco) && tipologia_SCENARIO.equals("Background"))
                    {
                        cambiasfondo = true;
                        sfondi_bambino.add(file_SCENARIO);
                    }


                }

                if(cambiasfondo == true){
                    int pickrandom = sfondi_bambino.size() - 1;

                    Random random = new Random();
                    int randomNumber = random.nextInt(pickrandom + 1);
                    Log.d("Random","NUM:" + randomNumber);

                    String immaginerandom = "" + sfondi_bambino.get(randomNumber);
                    Log.d("Random","IMG:" + immaginerandom);
                    storageReference_sfondo = firebaseStorage.getReference().child("scenario/"+ immaginerandom);

                    storageReference_sfondo.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(GiocoBambino.this)
                                .asDrawable()
                                .load(uri)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        // Imposta l'immagine come sfondo dell'Activity
                                        getWindow().setBackgroundDrawable(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Gestisci il caricamento cancellato
                                    }
                                });
                    }).addOnFailureListener(exception -> {
                        // Gestisci il fallimento
                    });


                }

                //storageReference_sfondo
                if(cambiasfondo == false){
                    //sfondo = findViewById(R.id.percorsobambinoback);


                    //sfondo.setBackgroundResource(R.drawable.percorso_bambino);

                    storageReference_sfondo = firebaseStorage.getReference().child("scenario/percorso_bambino.png");

                    storageReference_sfondo.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(GiocoBambino.this)
                                .asDrawable()
                                .load(uri)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        // Imposta l'immagine come sfondo dell'Activity
                                        getWindow().setBackgroundDrawable(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Gestisci il caricamento cancellato
                                    }
                                });
                    }).addOnFailureListener(exception -> {
                        // Gestisci il fallimento
                    });
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

    private String loadidbambino() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String idbmload = sharedPreferences.getString(KEY_BAMBINO_AREA_GIOCO, "");

        return idbmload;
    }

    private void loadpersonaggio(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String pers = sharedPreferences.getString(KEY_BAMBINO_AREA_GIOCO_PERSONAGGIOSCELTO, "");

        Log.d("Dati","Personaggio: "+ pers);
        storageReference_personaggio = firebaseStorage.getReference().child("personaggi/"+ pers + ".png");

        storageReference_personaggio.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(GiocoBambino.this)
                        .load(uri)
                        .into(personaggioscelto)
        ).addOnFailureListener(exception -> {});

    }

    public void onTopBarClicked_gioco(View view){
        Intent intent = new Intent(GiocoBambino.this, ScegliPersonaggio.class);
        //intent.putExtra("EXTRA_MESSAGE", "");
        startActivity(intent);
        finish();
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