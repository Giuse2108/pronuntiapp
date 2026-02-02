package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
public class MonitoraTerapiaAct extends AppCompatActivity {

    private ProgressDialog mProgress_img;
    private Uri image_uri;
    private ImageView imageView;
    private ProgressBar circularProgressBar;
    private TextView percentageText;

    private ProgressBar circularProgressBar2;
    private TextView percentageText2;

    private ProgressBar circularProgressBar3;
    private TextView percentageText3;

    // Variabili per tenere traccia degli esercizi
    private int eserciziTotali = 0;  // Imposta il numero totale di esercizi
    private int eserciziCompletati = 0; // Esercizi completati dall'utente
    private int eserciziCorretti = 0;
    private int eserciziErrati = 0;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final String TAG = "ExerciseListFragment";
    private RecyclerView recyclerView;
    private ExerciseAdapter_monitoraTerapia adapter;
    private ArrayList<Exercise> exerciseList;
    private ArrayList<String> terapiaList_nomeese = new ArrayList<>();
    private ArrayList<String> terapiaList_data = new ArrayList<>();
    private ArrayList<String> terapiaList_correzione = new ArrayList<>();
    private ArrayList<String> terapiaList_idesercizio = new ArrayList<>();
    private ArrayList<String> terapiaList_idterap = new ArrayList<>();

    private ArrayList<String> esericziList_idesercizi = new ArrayList<>();
    private ArrayList<String> esericziList_nomiterapia = new ArrayList<>();
    private ArrayList<String> idscenario_arrlist = new ArrayList<>();
    private int selectedExerciseId;
    private int idScenario = 0;
    private String idBambino = "";
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference_immaginescenario;
    private FirebaseDatabase database;
    private DatabaseReference reference_esercizio;
    private DatabaseReference reference_scenario;
    private DatabaseReference reference_bambino;
    private DatabaseReference reference_terapia;
    private TextView  nomebm;
    private TextView cognbm;
    private TextView monetebm;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";
    private static final String KEY_BAMBINO_MONITORA_TERAPIA = "monitora";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitora_terapia);

        loadlingua();

        boolean connessoainternet = connessioneinternet();

        idBambino = loadidbm();

        recyclerView = findViewById(R.id.recyclerView);

        database = FirebaseDatabase.getInstance();
        reference_esercizio = database.getReference("Esercizio");
        reference_scenario = database.getReference("Scenario");
        reference_terapia = database.getReference("Terapia");
        reference_bambino = database.getReference("Bambino");

        mProgress_img = new ProgressDialog(MonitoraTerapiaAct.this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference_immaginescenario = firebaseStorage.getReference();

        // Initialize exercise list and adapter
        exerciseList = new ArrayList<>();

        circularProgressBar = findViewById(R.id.circularProgressBar1);
        percentageText = findViewById(R.id.percentageText1);

        circularProgressBar2 = findViewById(R.id.circularProgressBar2);
        percentageText2 = findViewById(R.id.percentageText2);

        circularProgressBar3 = findViewById(R.id.circularProgressBar3);
        percentageText3 = findViewById(R.id.percentageText3);

        monetebm = findViewById(R.id.monetebm_moni);
        cognbm = findViewById(R.id.cognomebm_moni);
        nomebm = findViewById(R.id.nomebm_moni);

        caricabambino();
        caricaterapia();
        recuperascenario();
    }

    public void onTopBarClicked(View view) {
        // Your desired action on clicking the top bar
        Intent intent = new Intent(MonitoraTerapiaAct.this, Home_Genitore.class);
        startActivity(intent);
    }

    private void recuperascenario(){
        reference_scenario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idsc = childSnapshot.child("idScenario").getValue(String.class);

                    idscenario_arrlist.add(idsc);

                }

                idScenario = getMaxId(idscenario_arrlist);
                idScenario ++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    public static int getMaxId(ArrayList<String> idList) {
        // Crea una lista di interi
        ArrayList<Integer> intList = new ArrayList<>();

        // Converte ogni stringa in un intero e la aggiunge alla lista di interi
        for (String id : idList) {
            intList.add(Integer.parseInt(id));
        }

        // Restituisce il massimo valore trovato nella lista di interi
        return Collections.max(intList);
    }

    private void caricabambino(){
        reference_bambino.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idbm_db = childSnapshot.child("id").getValue(String.class);
                    String cognomebm_db = childSnapshot.child("cognome").getValue(String.class);
                    String nomebm_db = childSnapshot.child("nome").getValue(String.class);
                    String monetebm_db = childSnapshot.child("monete").getValue(String.class);

                    if(idBambino.equals(idbm_db))
                    {
                        String mxnom = getString(R.string.class_nom);
                        String mxc = getString(R.string.class_cognom);
                        String mxmon = getString(R.string.class_mon);

                        nomebm.setText(mxnom +": " +nomebm_db);
                        cognbm.setText(mxc  + ": " +cognomebm_db);
                        monetebm.setText(mxmon + ": " +monetebm_db);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MonitoraTerapiaAct.this);
        View view = LayoutInflater.from(MonitoraTerapiaAct.this).inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        EditText linkEditText = view.findViewById(R.id.linkEditText);
        imageView = view.findViewById(R.id.immaginepopup);
        Button buttonload = view.findViewById(R.id.caricafotopopup);

        buttonload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        String mxtitl = getString(R.string.mx_scn);
        String mxdesc = getString(R.string.mx_desc);
        String mxcar = getString(R.string.mx_car);
        String mxes = getString(R.string.mx_esc);




        builder.setTitle(mxtitl)
                .setMessage(mxdesc)
                .setPositiveButton(mxcar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String link = linkEditText.getText().toString();
                        Log.w(TAG, "sono entrato nell'onClick con il link: " + link);

                        boolean connessoainternet = connessioneinternet();
                        if(connessoainternet == true){
                            onLinkSubmitted(selectedExerciseId, link);
                        }

                    }
                })
                .setNegativeButton(mxes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void onLinkSubmitted(int exerciseId, String link) {
        // Handle link submission here, you can pass these values to the activity or do any other necessary action
        // For demonstration purposes, just printing them
        for (Exercise exercise : exerciseList) {
            if (exercise.getId() == exerciseId) {
                if(image_uri != null){
                    uploadImage(image_uri,idScenario,exerciseId,link);
                }else if(!link.equals(""))
                {
                    HelperClassScenario helperClassScenario2 = new HelperClassScenario(link,"",idBambino,"" + idScenario,"" +exerciseId,"Link");
                    reference_scenario.child("" + idScenario).setValue(helperClassScenario2);

                    idScenario++;

                }else {

                    String mxerrscen = getString(R.string.errscen);

                    Toast.makeText(MonitoraTerapiaAct.this, mxerrscen, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadImage(Uri file, int idscen, int ter, String link) {

        String mxloadimg = getString(R.string.immagineload);
        String mxdaticar = getString(R.string.daticar);

        mProgress_img.setMessage(mxloadimg);
        mProgress_img.show();

        String immagine = "scenario_"+idscen;
        StorageReference imageRef = storageReference_immaginescenario.child("scenario/"+ immagine);

        imageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        image_uri = null;
                        HelperClassScenario helperClassScenario = new HelperClassScenario("",immagine,idBambino,"" + idScenario,"" +ter,"Background");
                        reference_scenario.child("" + idScenario).setValue(helperClassScenario);

                        idScenario++;


                        if(!link.equals("")){
                            HelperClassScenario helperClassScenario2 = new HelperClassScenario(link,"",idBambino,"" + idScenario,"" +ter,"Link");
                            reference_scenario.child("" + idScenario).setValue(helperClassScenario2);
                            idScenario++;
                        }

                        mProgress_img.dismiss();

                        Toast.makeText(MonitoraTerapiaAct.this, mxdaticar, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to upload image
                        Toast.makeText(MonitoraTerapiaAct.this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void caricaterapia(){
        reference_terapia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idterapia = childSnapshot.child("idTerapia").getValue(String.class);
                    String correzione_terapia = childSnapshot.child("correzione").getValue(String.class);
                    String idbambinoterapia = childSnapshot.child("idBambino").getValue(String.class);
                    String idesercizio = childSnapshot.child("idEsercizio").getValue(String.class);
                    String dataterapia = childSnapshot.child("data").getValue(String.class);

                    if(idBambino.equals(idbambinoterapia))
                    {
                        Log.d("Dati", "DENTRO:");
                        eserciziTotali ++;

                        terapiaList_idterap.add(idterapia);
                        terapiaList_idesercizio.add(idesercizio);
                        terapiaList_data.add(dataterapia);

                        if(correzione_terapia.equals(""))
                        {
                            Log.d("Dati", "DENTRO2:");
                            terapiaList_correzione.add("Non svolto");
                        }else
                        {
                            terapiaList_correzione.add(correzione_terapia);

                            if(correzione_terapia.equals("Corretto"))
                            {
                                eserciziCorretti ++;
                                eserciziCompletati ++;
                            }else
                            {
                                eserciziErrati ++;
                                eserciziCompletati ++;
                            }


                        }

                    }

                }

                if(eserciziCompletati != 0){
                    esercizi_completati(eserciziCompletati);
                }

                if(eserciziCorretti != 0){
                    esercizi_corretti(eserciziCorretti);
                }

                if(eserciziErrati != 0){
                    esercizi_errati(eserciziErrati);
                }

                Log.d("Dati", "DATI1:" + eserciziCompletati);
                Log.d("Dati", "DATI2:" + eserciziCorretti);
                Log.d("Dati", "DATI3:" + eserciziErrati);
                caricaesercizi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });

    }
    private void caricaesercizi(){


        reference_esercizio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idesercizio = childSnapshot.child("idEsercizio").getValue(String.class);
                    String nome_esercizio = childSnapshot.child("nome").getValue(String.class);

                    esericziList_nomiterapia.add(nome_esercizio);
                    esericziList_idesercizi.add(idesercizio);
                }


                Log.d("Dati","GRANDEZZA ARRAY TERAPIA" + terapiaList_idterap.size());
                for(int i = 0; i < terapiaList_idterap.size(); i++)
                {
                    for(int j = 0; j < esericziList_nomiterapia.size(); j++){
                        String ide = terapiaList_idesercizio.get(i);
                        String idesercizoi_ese = esericziList_idesercizi.get(j);
                        String nome_ese = esericziList_nomiterapia.get(j);
                        if(ide.equals(idesercizoi_ese))
                        {
                            terapiaList_nomeese.add(nome_ese);
                        }
                    }

                }


                Log.d("Dati","Dimensione" + terapiaList_idterap.size());
                for(int i = 0; i < terapiaList_idterap.size(); i++)
                {
                    Log.d("Dati","cont:" + i);

                    Log.d("Dati","nome:" + terapiaList_nomeese.get(i));
                    Log.d("Dati","data:" + terapiaList_data.get(i));
                    Log.d("Dati","correzione:" + terapiaList_correzione.get(i));
                    int idadapter = Integer.parseInt(terapiaList_idterap.get(i));
                    Log.d("Dati","idadapter:" + idadapter);
                    exerciseList.add(new Exercise(idadapter, terapiaList_nomeese.get(i), terapiaList_data.get(i), terapiaList_correzione.get(i)));
                }


                adapter = new ExerciseAdapter_monitoraTerapia(MonitoraTerapiaAct.this, exerciseList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MonitoraTerapiaAct.this));

                // Add divider item decoration to the RecyclerView
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);

                // Set click listener for exercise items
                adapter.setOnItemClickListener(new ExerciseAdapter_monitoraTerapia.OnItemClickListener() {
                    @Override
                    public void onItemClick(int exerciseId) {
                        selectedExerciseId = exerciseId;
                        showAlertDialog();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });


    }

    private void esercizi_completati(int completati) {
        eserciziCompletati = completati;
        int percentage = (eserciziCompletati * 100) / eserciziTotali;
        circularProgressBar.setProgress(percentage);
        percentageText.setText(percentage + "%");
    }

    private void esercizi_corretti(int corretti) {
        eserciziCorretti = corretti;
        int percentage = (eserciziCorretti * 100) / eserciziTotali;
        circularProgressBar2.setProgress(percentage);
        percentageText2.setText(percentage + "%");
    }

    private void esercizi_errati(int errati) {
        eserciziErrati = errati;
        int percentage = (eserciziErrati * 100) / eserciziTotali;
        circularProgressBar3.setProgress(percentage);
        percentageText3.setText(percentage + "%");
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(MonitoraTerapiaAct.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(MonitoraTerapiaAct.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

        } else {
            // Permission is already granted, proceed to pick an image
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        String permmx_rif = getString(R.string.permesso_galleria);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to pick an image
                pickImageFromGallery();
            } else {
                // Permission denied
                Toast.makeText(MonitoraTerapiaAct.this, permmx_rif, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Image is picked successfully, set it to ImageView
            image_uri = data.getData();
            if (image_uri != null) {
                imageView.setImageURI(image_uri);
            }
        }
    }

    private void loadlingua() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");

        String currentln = getLinguaCorrente(MonitoraTerapiaAct.this);
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

    private String loadidbm(){

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String idred = sharedPreferences.getString(KEY_BAMBINO_MONITORA_TERAPIA, "it");

        return idred;
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