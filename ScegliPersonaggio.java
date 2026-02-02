package com.example.gfm_pronuntiapp_appfinale_esame;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class ScegliPersonaggio extends AppCompatActivity {

    private GridLayout gridLayout;
    FirebaseStorage firebaseStorage;
    String characterId;
    int codAcquisto = 1;
    private TreeSet<String> charactersAdded = new TreeSet<>();
    String imageUrl;
    String characterName;
    boolean isColor = false;
    private List<String> imageUrls = new ArrayList<>();

    private String IDBAMBINOREDIRECT = "";

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";
    private static final String KEY_BAMBINO_AREA_GIOCO = "id_bambino";
    private static final String KEY_BAMBINO_AREA_GIOCO_PERSONAGGIOSCELTO = "personaggio";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scegli_personaggio);

        loadlingua();
        IDBAMBINOREDIRECT = loadidbambino();

        gridLayout = findViewById(R.id.gridLayout);

        firebaseStorage = FirebaseStorage.getInstance();

        boolean connessoainternet = connessioneinternet();

        if(connessoainternet == true){
            downloadAllImages();
        }

    }

    private int calculateNoOfColumns(int totalImages) {
        Point size = getScreenSize();
        int screenWidth = size.x;
        int imageWidth = 400;
        return Math.max(1, screenWidth / imageWidth);
    }

    private Point getScreenSize() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void downloadAllImages() {
        StorageReference imageRefBW = firebaseStorage.getReference().child("personaggiNonSbloccati/");
        imageRefBW.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> items = listResult.getItems();
                int totalImages = items.size();
                int numColumns = calculateNoOfColumns(totalImages);
                gridLayout.setColumnCount(numColumns);

                // Lista per memorizzare gli URL delle immagini
                List<String> imageUrls = new ArrayList<>();

                for (StorageReference item : items) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            imageUrls.add(imageUrl); // Aggiungi l'URL alla lista

                            if (imageUrls.size() == totalImages) { // Se abbiamo scaricato tutte le immagini
                                // Ordina gli URL delle immagini
                                Collections.sort(imageUrls);

                                // Aggiungi le immagini al GridLayout nell'ordine della lista ordinata
                                for (String url : imageUrls) {
                                    addImageView(url);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Unable to recover URL image");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list all images", e);
            }
        });
    }


    private void addImageView(String imageUrl) {
        ImageView image = new ImageView(this);
        image.setTag(imageUrl);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errore);

        Glide.with(ScegliPersonaggio.this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(image);

        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.TRANSPARENT);
        border.setStroke(5, Color.GREEN);

        image.setBackground(border);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 300;
        params.height = 300;

        int marginInPixels = 40;
        params.leftMargin = marginInPixels;
        params.topMargin = marginInPixels;
        params.rightMargin = marginInPixels;
        params.bottomMargin = marginInPixels;

        String[] urlParts = imageUrl.split("/");
        String fileName = urlParts[urlParts.length - 1];
        if (fileName.contains("personaggiNonSbloccati%2F")) {
            characterName = fileName.replace("NonSbloccato.png", "").replace("personaggiNonSbloccati%2F", "");
        } else if (imageUrl.contains("personaggi%2F")) {
            characterName = fileName.replace("personaggi%2F", "");
        } else {
            Log.e(TAG, "Percorso non riconosciuto: " + imageUrl);
            return;
        }
        String[] urlParts2 = characterName.split("\\?");
        characterName = urlParts2[0];
        image.setTag(characterName);

        image.setClickable(false);

        Log.d(TAG, "Adding image view for character: " + characterName);
        checkChildCharacter(IDBAMBINOREDIRECT,characterName, image);

        gridLayout.addView(image, params);
    }

    private void checkChildCharacter(String childId, String characterName, ImageView imageView) {

        FirebaseDatabase database_acquisto = FirebaseDatabase.getInstance();
        DatabaseReference reference_acquisto = database_acquisto.getReference("Personaggio_Bambino");

        reference_acquisto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idchild = childSnapshot.child("idBambino").getValue(String.class);
                    String prodottoacq = childSnapshot.child("prodottoacq").getValue(String.class);
                    String nodo = childSnapshot.getKey();

                    String[] parti = nodo.split("_");
                    String idperconfronto = parti[0];

                    Log.w(TAG, "ID bambino: " + idperconfronto);
                    Log.w(TAG, "ID bambinodb: " + idchild);
                    Log.w(TAG, "ID bambinoparametrofunzione: " + idchild);
                    Log.w(TAG, "ID bambinoparametrofunzioneimg: " + characterName);
                    if(idchild.equals(childId) && idperconfronto.equals(characterName))
                    {
                        changeImageToColor(characterName, imageView);
                        imageView.setClickable(true);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showConfirmationDialog(imageView, (String) v.getTag());
                            }
                        });
                        Log.w(TAG, "Personaggio già acquistato per il bambino: " + childSnapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    public void showConfirmationDialog(ImageView imageView, String characterName) {
        Log.w(TAG, "Il personaggio cliccato è: " + characterName);

        String message = getString(R.string.scper_scegli);
        String si_message = getString(R.string.scper_scegli_si);

        AlertDialog.Builder builder = new AlertDialog.Builder(ScegliPersonaggio.this);
        builder.setTitle(characterName)
                .setMessage(message)
                .setPositiveButton(si_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        iniziagioco(characterName);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void iniziagioco(String personaggio){

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_BAMBINO_AREA_GIOCO_PERSONAGGIOSCELTO, personaggio);

        editor.apply();

        Log.d("Dati","gioco_bamb");

        Intent intent = new Intent(ScegliPersonaggio.this, GiocoBambino.class);
        //intent.putExtra("EXTRA_MESSAGE", "");
        startActivity(intent);
        finish();

    }

    private void changeImageToColor(String characterName, ImageView imageView) {
        // Crea l'URL dell'immagine a colori utilizzando il nome del personaggio
        String colorImageUrl = "https://firebasestorage.googleapis.com/v0/b/gfm-pronuntiapp-appfinaleesame.appspot.com/o/personaggi%2F" + characterName + ".png?alt=media";

        Log.w(TAG, "colorImageUrl: " + colorImageUrl);

        // Controlla se l'attività è in fase di chiusura o distrutta
        if (isFinishing() || isDestroyed()) {
            Log.e(TAG, "Impossibile caricare l'immagine: l'attività è distrutta o in fase di chiusura.");
            return;
        }

        // Usa Glide per caricare l'immagine a colori e impostarla nell'ImageView
        Glide.with(this)
                .load(colorImageUrl)
                .into(imageView);
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


    public void onTopBarClicked_scegli(View view){
        Intent intent = new Intent(ScegliPersonaggio.this, Home_Figlio.class);
        //intent.putExtra("EXTRA_MESSAGE", "");
        startActivity(intent);
        finish();
    }

    private boolean connessioneinternet(){

        if (NetworkUtils.isConnectedToInternet(this)) {
            return true;
        } else {
            Toast.makeText(this, "Non connesso a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}