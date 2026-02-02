package com.example.gfm_pronuntiapp_appfinale_esame.ui.addesercizio;

import static android.app.Activity.RESULT_OK;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gfm_pronuntiapp_appfinale_esame.NetworkUtils;
import com.example.gfm_pronuntiapp_appfinale_esame.R;
import com.example.gfm_pronuntiapp_appfinale_esame.ui.addfiglio.HelperClassAddFiglio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.Locale;

public class AddEsercizioFragment extends Fragment {


    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";

    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_LINGUA = "lingua";

    private Spinner tipes;

    private LinearLayout lyes1;
    private LinearLayout lyes2;
    private LinearLayout lyes3;

    private Button buttones1;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final int REQUEST_PERMISSION_CODE = 200;
    private ImageView imageView_es1;
    private MaterialButton button_imageView_es1;

    private AddEsercizioViewModel mViewModel;

    private Uri image;
    private Uri image2;
    private Uri image3;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference mStorage_fileAudio;
    private MaterialButton button_registra_aiuto1_es1;
    private MaterialButton button_registra_aiuto2_es1;
    private MaterialButton button_registra_aiuto3_es1;
    private MaterialButton button_ascolta_aiuto1_es1;
    private MaterialButton button_ascolta_aiuto2_es1;
    private MaterialButton button_ascolta_aiuto3_es1;
    private MaterialButton button_ascoltaaudio_es2;
    private MaterialButton button_registraaudio_es2;
    boolean StartPlaying_audio_es2 = true;
    boolean StartRecording_audio_es2 = true;
    boolean StartPlaying_aiuto1_es1 = true;
    boolean StartPlaying_aiuto2_es1 = true;
    boolean StartPlaying_aiuto3_es1 = true;
    boolean StartRecording_aiuto1_es1 = true;
    boolean StartRecording_aiuto2_es1 = true;
    boolean StartRecording_aiuto3_es1 = true;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private static final String LOG_TAG = "AudioRecordTest";

    //variabili primo esercizio
    private static String fileName_aiuto1_es1 = null;
    private static String fileName_aiuto2_es1 = null;
    private static String fileName_aiuto3_es1 = null;
    private String stringapervariabile_fileName_aiuto1_es1 = "";
    private String stringapervariabile_fileName_aiuto2_es1 = "";
    private String stringapervariabile_fileName_aiuto3_es1 = "";
    private ProgressDialog mProgress_aiuto1_es1;
    private ProgressDialog mProgress_aiuto2_es1;
    private ProgressDialog mProgress_aiuto3_es1;
    private ProgressDialog mProgress_img_es1;
    private boolean registratoaiuto1_es1 = false;
    private boolean registratoaiuto2_es1 = false;
    private boolean registratoaiuto3_es1 = false;
    private String nomeesercizio1 = "";
    private String idesperhelper_es1 = "";
    private String monete_es1 = "";
    private String immagine_es1 = "";
    private int idDenominazione_es1 = 0;

    //variabili secondo esercizio
    private String nomeesercizio2 = "";
    private static String fileName_audio_es2 = null;
    private String stringapervariabile_fileName_audio_es2 = "";
    private int idRipetizione_es2 = 0;
    private String monete_es2 = "";
    private boolean registratoaudio_es2 = false;
    private MaterialButton addesercizio2;
    private ProgressDialog mProgress_es2;

    //variabili esericizo 3
    private ProgressDialog mProgress_img1_es3;
    private ProgressDialog mProgress_img2_es3;
    private ProgressDialog mProgress_audio_es3;
    private MaterialButton button_ascolta_audio_es3;
    private MaterialButton button_registra_audio_es3;
    private boolean registraaudio_es3 = false;
    private static String fileName_audio_es3 = null;
    private String stringapervariabile_fileName_audio_es3 = "";
    private String nomeesercizio3 = "";
    private String monete_es3 = "";
    private MaterialButton addesercizio3;
    boolean StartPlaying_audio_es3 = true;
    boolean StartRecording_audio_es3 = true;
    private ImageView imageView1_es3;
    private ImageView imageView2_es3;
    private String immagine1_es3 = "";
    private String immagine2_es3 = "";
    private int idRiconoscimento_es3 = 0;
    private MaterialButton image1es3_button;
    private MaterialButton image2es3_button;

    private int img1_es3_cliccato = 0;
    private int img2_es3_cliccato = 0;

    //variabili firebase
    private FirebaseDatabase database_es;
    private DatabaseReference reference_es;
    private DatabaseReference reference_es_DENOMINAZIONE;
    private DatabaseReference reference_es_RIPETIZIONE;
    private DatabaseReference reference_es_RICONOSCIMENTO;


    private int ultimo_id_esercizio = 0;

    private String codlogopedista = "";

    private String immagineload;

    public static AddEsercizioFragment newInstance() {
        return new AddEsercizioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_esercizio, container, false);

        loadlingua();
        boolean connessoainternet = connessioneinternet();
        immagineload = getString(R.string.immagineload);

        codlogopedista = recuperacodicelogopedista();
        Log.d("Dati","CodLogopedistaLoggato:" + codlogopedista);

        database_es = FirebaseDatabase.getInstance();
        reference_es = database_es.getReference("Esercizio");
        reference_es_DENOMINAZIONE = database_es.getReference("Denominazione");
        reference_es_RIPETIZIONE = database_es.getReference("Ripetizione");
        reference_es_RICONOSCIMENTO = database_es.getReference("Riconoscimento");

        recuperaultimoidesercizio();
        recuperaultimoidDenominazione();
        recuperaultimoidRipetizione();
        recuperaidriconoscimento();

        mProgress_aiuto1_es1 = new ProgressDialog(getContext());
        mProgress_aiuto2_es1= new ProgressDialog(getContext());
        mProgress_aiuto3_es1 = new ProgressDialog(getContext());
        mProgress_img_es1 = new ProgressDialog(getContext());
        mProgress_es2 = new ProgressDialog(getContext());
        mProgress_img1_es3 = new ProgressDialog(getContext());
        mProgress_img2_es3 = new ProgressDialog(getContext());
        mProgress_audio_es3 = new ProgressDialog(getContext());
        mStorage_fileAudio = FirebaseStorage.getInstance().getReference();

        tipes = view.findViewById(R.id.tipes_spinner);
        lyes1 = view.findViewById(R.id.layout_esercizi1);
        lyes2 = view.findViewById(R.id.layout_esercizi2);
        lyes3 = view.findViewById(R.id.layout_esercizi3);
        buttones1 = view.findViewById(R.id.btn_registraesercizio_es1_logo);
        imageView_es1 = view.findViewById(R.id.immaginie_es1_logo);
        button_imageView_es1 = view.findViewById(R.id.selectImage_es1_logo);

        button_registra_aiuto1_es1 = view.findViewById(R.id.btn_registra_aiuto1_esercizio1);
        button_registra_aiuto2_es1 = view.findViewById(R.id.btn_registra_aiuto2_esercizio1);
        button_registra_aiuto3_es1 = view.findViewById(R.id.btn_registra_aiuto3_esercizio1);

        button_ascolta_aiuto1_es1 = view.findViewById(R.id.btn_ascolta_aiuto1_esercizio1);
        button_ascolta_aiuto2_es1 = view.findViewById(R.id.btn_ascolta_aiuto2_esercizio1);
        button_ascolta_aiuto3_es1 = view.findViewById(R.id.btn_ascolta_aiuto3_esercizio1);

        button_registraaudio_es2 = view.findViewById(R.id.btn_registra_esercizio2);
        button_ascoltaaudio_es2 = view.findViewById(R.id.btn_riascolta_esercizio2);
        addesercizio2 = view.findViewById(R.id.btn_registraesercizio_es2_logo);


        button_ascolta_audio_es3 = view.findViewById(R.id.btn_riascolta_esercizio3);
        button_registra_audio_es3 = view.findViewById(R.id.btn_registra_esercizio3);
        addesercizio3 = view.findViewById(R.id.btn_registraesercizio_es3_logo);
        image2es3_button = view.findViewById(R.id.selectImage2_es3_logo);
        image1es3_button = view.findViewById(R.id.selectImage1_es3_logo);
        imageView1_es3 = view.findViewById(R.id.immaginie1_es3_logo);
        imageView2_es3 = view.findViewById(R.id.immaginie2_es3_logo);

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_CODE);
        }


        tipes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = parentView.getItemAtPosition(position).toString();

                if(selectedOption.equals("Denominazione immagini"))
                {
                    img1_es3_cliccato = 0;
                    img2_es3_cliccato = 0;

                    lyes1.setVisibility(View.VISIBLE);
                    lyes2.setVisibility(View.GONE);
                    lyes3.setVisibility(View.GONE);
                }

                if(selectedOption.equals("Ripetizione di sequenze di parole"))
                {
                    img1_es3_cliccato = 0;
                    img2_es3_cliccato = 0;
                    lyes1.setVisibility(View.GONE);
                    lyes2.setVisibility(View.VISIBLE);
                    lyes3.setVisibility(View.GONE);
                }

                if(selectedOption.equals("Riconoscimento di coppie minime"))
                {
                    lyes1.setVisibility(View.GONE);
                    lyes2.setVisibility(View.GONE);
                    lyes3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        button_imageView_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        buttones1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edittext_nomeesercizio = view.findViewById(R.id.input_nome_esercizio);
                String nomeesercizio = edittext_nomeesercizio.getText().toString();

                EditText edittext_moneteesuno = view.findViewById(R.id.monete_es1_logo);
                String moneteesuno = edittext_moneteesuno.getText().toString();

                
                if (registratoaiuto1_es1 != false && registratoaiuto2_es1 != false && registratoaiuto1_es1 != false && image != null && !nomeesercizio.equals("") && !moneteesuno.equals("")) {

                    idesperhelper_es1 = "" + ultimo_id_esercizio;
                    nomeesercizio1 = nomeesercizio;
                    monete_es1 = moneteesuno;

                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true){
                        uploadAudioes2();
                        uploadImage_es1(image);
                        registratoaiuto1_es1 = false;
                        registratoaiuto2_es1 = false;
                        registratoaiuto3_es1 = false;
                    }



                }
                else
                {
                    String esnonins = getString(R.string.esnonins);

                    Toast.makeText(requireContext(), esnonins, Toast.LENGTH_SHORT).show();
                }

            }
        });

        button_ascolta_aiuto1_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(StartPlaying_aiuto1_es1,fileName_aiuto1_es1);
                if (StartPlaying_aiuto1_es1) {
                    button_ascolta_aiuto1_es1.setText("Ferma ascolto");
                } else {
                    button_ascolta_aiuto1_es1.setText("Inizia ascolto");
                }
                StartPlaying_aiuto1_es1 = !StartPlaying_aiuto1_es1;
            }
        });

        button_ascolta_aiuto2_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(StartPlaying_aiuto2_es1,fileName_aiuto2_es1);
                if (StartPlaying_aiuto2_es1) {
                    button_ascolta_aiuto2_es1.setText("Ferma ascolto");
                } else {
                    button_ascolta_aiuto2_es1.setText("Inizia ascolto");
                }
                StartPlaying_aiuto2_es1 = !StartPlaying_aiuto2_es1;
            }
        });

        button_ascolta_aiuto3_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(StartPlaying_aiuto3_es1,fileName_aiuto3_es1);
                if (StartPlaying_aiuto3_es1) {
                    button_ascolta_aiuto3_es1.setText("Ferma ascolto");
                } else {
                    button_ascolta_aiuto3_es1.setText("Inizia ascolto");
                }
                StartPlaying_aiuto3_es1 = !StartPlaying_aiuto3_es1;
            }
        });

        button_registra_aiuto1_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName_aiuto1_es1 = requireContext().getExternalCacheDir().getAbsolutePath();
                fileName_aiuto1_es1 += "/aiuto1_"+ultimo_id_esercizio+"_denominazione_"+codlogopedista+".3gp";
                stringapervariabile_fileName_aiuto1_es1 = "/aiuto1_"+ultimo_id_esercizio+"_denominazione_"+codlogopedista+".3gp";

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(StartRecording_aiuto1_es1,fileName_aiuto1_es1);
                    registratoaiuto1_es1 = true;


                    if (StartRecording_aiuto1_es1) {
                        button_registra_aiuto1_es1.setText("Ferma registrazione");
                    } else {
                        button_registra_aiuto1_es1.setText("Inizia registrazione");
                    }
                    StartRecording_aiuto1_es1 = !StartRecording_aiuto1_es1;
                } else {
                    String permic = getString(R.string.permesso_microfono);

                    Toast.makeText(requireContext(), permic, Toast.LENGTH_SHORT).show();
                }


            }
        });

        button_registra_aiuto2_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName_aiuto2_es1 = requireContext().getExternalCacheDir().getAbsolutePath();
                fileName_aiuto2_es1 += "/aiuto2_"+ultimo_id_esercizio+"_denominazione_"+codlogopedista+".3gp";
                stringapervariabile_fileName_aiuto2_es1 = "/aiuto2_"+ultimo_id_esercizio+"_denominazione_"+codlogopedista+".3gp";

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(StartRecording_aiuto2_es1,fileName_aiuto2_es1);
                    registratoaiuto2_es1 = true;

                    if (StartRecording_aiuto2_es1) {
                        button_registra_aiuto2_es1.setText("Ferma registrazione");
                    } else {
                        button_registra_aiuto2_es1.setText("Inizia registrazione");
                    }
                    StartRecording_aiuto2_es1 = !StartRecording_aiuto2_es1;
                } else {
                    String permic = getString(R.string.permesso_microfono);

                    Toast.makeText(requireContext(), permic, Toast.LENGTH_SHORT).show();
                }


            }
        });

        button_registra_aiuto3_es1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName_aiuto3_es1 = requireContext().getExternalCacheDir().getAbsolutePath();
                fileName_aiuto3_es1 += "/aiuto3_"+ultimo_id_esercizio+"_denominazione_"+codlogopedista+".3gp";
                stringapervariabile_fileName_aiuto3_es1 = "/aiuto3_"+ultimo_id_esercizio+"_denominazione_"+codlogopedista+".3gp";

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(StartRecording_aiuto3_es1,fileName_aiuto3_es1);
                    registratoaiuto3_es1 = true;

                    if (StartRecording_aiuto3_es1) {
                        button_registra_aiuto3_es1.setText("Ferma registrazione");
                    } else {
                        button_registra_aiuto3_es1.setText("Inizia registrazione");
                    }
                    StartRecording_aiuto3_es1 = !StartRecording_aiuto3_es1;
                } else {
                    String permic = getString(R.string.permesso_microfono);

                    Toast.makeText(requireContext(), permic, Toast.LENGTH_SHORT).show();
                }

            }
        });

        button_registraaudio_es2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName_audio_es2 = requireContext().getExternalCacheDir().getAbsolutePath();
                fileName_audio_es2 += "/audio_"+ultimo_id_esercizio+"_ripetizione_"+codlogopedista+".3gp";
                stringapervariabile_fileName_audio_es2 = "/audio_"+ultimo_id_esercizio+"_ripetizione_"+codlogopedista+".3gp";

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(StartRecording_audio_es2,fileName_audio_es2);
                    registratoaudio_es2 = true;

                    if (StartRecording_audio_es2) {
                        button_registraaudio_es2.setText("Ferma registrazione");
                    } else {
                        button_registraaudio_es2.setText("Inizia registrazione");
                    }
                    StartRecording_audio_es2 = !StartRecording_audio_es2;
                } else {
                    String permic = getString(R.string.permesso_microfono);

                    Toast.makeText(requireContext(), permic, Toast.LENGTH_SHORT).show();
                }

            }
        });


        button_ascoltaaudio_es2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(StartPlaying_audio_es2,fileName_audio_es2);
                if (StartPlaying_audio_es2) {
                    button_ascoltaaudio_es2.setText("Ferma ascolto");
                } else {
                    button_ascoltaaudio_es2.setText("Inizia ascolto");
                }
                StartPlaying_audio_es2 = !StartPlaying_audio_es2;
            }
        });

        addesercizio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edittext_nomeesercizio = view.findViewById(R.id.input_nome_esercizio);
                String nomeesercizio = edittext_nomeesercizio.getText().toString();

                EditText edittext_moneteesuno = view.findViewById(R.id.monete_es2_logo);
                String moneteesuno = edittext_moneteesuno.getText().toString();


                if (registratoaudio_es2 != false && !nomeesercizio.equals("") && !moneteesuno.equals("")) {

                    idesperhelper_es1 = "" + ultimo_id_esercizio;
                    nomeesercizio2 = nomeesercizio;
                    monete_es2 = moneteesuno;


                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true){
                        uploadAudioes2();
                        registratoaudio_es2 = false;
                    }


                }
                else
                {
                    String esnonins = getString(R.string.esnonins);
                    Toast.makeText(requireContext(), esnonins, Toast.LENGTH_SHORT).show();
                }
            }
        });

        image1es3_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1_es3_cliccato = 1;
                checkPermissionAndPickImage();
            }
        });

        image2es3_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img2_es3_cliccato = 1;
                checkPermissionAndPickImage();
            }
        });

        button_registra_audio_es3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName_audio_es3 = requireContext().getExternalCacheDir().getAbsolutePath();
                fileName_audio_es3 += "/audio_"+ultimo_id_esercizio+"_riconoscimento_"+codlogopedista+".3gp";
                stringapervariabile_fileName_audio_es3 = "/audio_"+ultimo_id_esercizio+"_riconoscimento_"+codlogopedista+".3gp";

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(StartRecording_audio_es3,fileName_audio_es3);
                    registraaudio_es3 = true;

                    if (StartRecording_audio_es3) {
                        button_registra_audio_es3.setText("Ferma registrazione");
                    } else {
                        button_registra_audio_es3.setText("Inizia registrazione");
                    }
                    StartRecording_audio_es3 = !StartRecording_audio_es3;
                } else {
                    String permic = getString(R.string.permesso_microfono);

                    Toast.makeText(requireContext(), permic, Toast.LENGTH_SHORT).show();
                }

            }
        });


        button_ascolta_audio_es3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(StartPlaying_audio_es3,fileName_audio_es3);
                if (StartPlaying_audio_es3) {
                    String stopasc = getString(R.string.fermaasc);
                    button_ascolta_audio_es3.setText(stopasc);
                } else {
                    String startasc = getString(R.string.iniziasc);
                    button_ascolta_audio_es3.setText(startasc);
                }
                StartPlaying_audio_es3 = !StartPlaying_audio_es3;
            }
        });

        addesercizio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edittext_nomeesercizio = view.findViewById(R.id.input_nome_esercizio);
                String nomeesercizio = edittext_nomeesercizio.getText().toString();

                EditText edittext_moneteesuno = view.findViewById(R.id.monete_es3_logo);
                String moneteesuno = edittext_moneteesuno.getText().toString();


                if (registraaudio_es3 != false && !nomeesercizio.equals("") && !moneteesuno.equals("")) {

                    idesperhelper_es1 = "" + ultimo_id_esercizio;
                    nomeesercizio3 = nomeesercizio;
                    monete_es3 = moneteesuno;

                    boolean connessoainternet = connessioneinternet();
                    if(connessoainternet == true){
                        uploadImage1_es3(image2);
                        registraaudio_es3 = false;
                    }


                }
                else
                {
                    String esnonins = getString(R.string.esnonins);
                    Toast.makeText(requireContext(), esnonins, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
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


    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(),
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to pick an image
                pickImageFromGallery();
            } else {

                String galleriaperm = getString(R.string.permesso_galleria);

                // Permission denied
                Toast.makeText(requireContext(), galleriaperm, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Image is picked successfully, set it to ImageView
            image = data.getData();
            if (image != null) {
                if(img1_es3_cliccato == 1){
                    img1_es3_cliccato = 0;
                    image2 = image;
                    imageView1_es3.setImageURI(image2);
                }
                else if(img2_es3_cliccato == 1){
                    img2_es3_cliccato = 0;
                    image3 = image;
                    imageView2_es3.setImageURI(image3);
                }else{
                    imageView_es1.setImageURI(image);
                }

            }
        }
    }


    private void uploadImage_es1(Uri file) {

        mProgress_img_es1.setMessage(immagineload);
        mProgress_img_es1.show();

        immagine_es1 = "immagine_"+ultimo_id_esercizio+"_denominazione_" + codlogopedista;
        StorageReference imageRef = storageReference.child("foto esercizi/"+ immagine_es1);

        imageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        mProgress_img_es1.dismiss();
                        image = null;
                        uploadAudio_aiuto1_es1();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to upload image
                        String failed = getString(R.string.errore_immagini);
                        Toast.makeText(requireContext(), failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAudio_aiuto1_es1() {
        String audioincar = getString(R.string.audioincar);

        mProgress_aiuto1_es1.setMessage(audioincar);
        mProgress_aiuto1_es1.show();

        StorageReference filepath = mStorage_fileAudio.child("audio esercizi").child("" + stringapervariabile_fileName_aiuto1_es1);

        Uri uri = Uri.fromFile(new File(fileName_aiuto1_es1));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress_aiuto1_es1.dismiss();
                uploadAudio_aiuto2_es1();
            }
        });

    }

    private void uploadAudio_aiuto2_es1() {
        String audioincar = getString(R.string.audioincar);

        mProgress_aiuto2_es1.setMessage(audioincar);
        mProgress_aiuto2_es1.show();

        StorageReference filepath = mStorage_fileAudio.child("audio esercizi").child("" + stringapervariabile_fileName_aiuto2_es1);

        Uri uri = Uri.fromFile(new File(fileName_aiuto2_es1));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress_aiuto2_es1.dismiss();
                uploadAudio_aiuto3_es1();
            }
        });

    }

    private void uploadAudio_aiuto3_es1() {
        String audioincar = getString(R.string.audioincar);

        mProgress_aiuto3_es1.setMessage(audioincar);
        mProgress_aiuto3_es1.show();

        StorageReference filepath = mStorage_fileAudio.child("audio esercizi").child("" + stringapervariabile_fileName_aiuto3_es1);

        Uri uri = Uri.fromFile(new File(fileName_aiuto3_es1));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress_aiuto3_es1.dismiss();
                caricaesercizi();
            }
        });

    }

    private void caricaesercizi(){

        HelperClassEsercizio helperClassEs = new HelperClassEsercizio(nomeesercizio1,"Denominazione",idesperhelper_es1,codlogopedista);
        reference_es.child("" + idesperhelper_es1).setValue(helperClassEs);

        String idesercizioinsertdenominazione = "" + idDenominazione_es1;
        char charToRemove = '/';
        String new_stringapervariabile_fileName_aiuto1_es1 = stringapervariabile_fileName_aiuto1_es1.replace(Character.toString(charToRemove), "");
        String new_stringapervariabile_fileName_aiuto2_es1 = stringapervariabile_fileName_aiuto2_es1.replace(Character.toString(charToRemove), "");
        String new_stringapervariabile_fileName_aiuto3_es1 = stringapervariabile_fileName_aiuto3_es1.replace(Character.toString(charToRemove), "");
        HelperClassDenominazione helperClassEs_DENOMINAZIONE = new HelperClassDenominazione(new_stringapervariabile_fileName_aiuto1_es1,new_stringapervariabile_fileName_aiuto2_es1,new_stringapervariabile_fileName_aiuto3_es1,idesercizioinsertdenominazione,idesperhelper_es1,immagine_es1,monete_es1);
        reference_es_DENOMINAZIONE.child("" + idesercizioinsertdenominazione).setValue(helperClassEs_DENOMINAZIONE);

        String escar = getString(R.string.escaricato);
        Toast.makeText(requireContext(), escar, Toast.LENGTH_SHORT).show();
        ultimo_id_esercizio ++;
        idDenominazione_es1 ++;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddEsercizioViewModel.class);
        // TODO: Use the ViewModel
    }


    private void recuperaultimoidesercizio(){

        ArrayList<String> lista_id_esercizi = new ArrayList<>();

        reference_es.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idesericzio = childSnapshot.child("idEsercizio").getValue(String.class);

                    lista_id_esercizi.add(idesericzio);
                }

                ultimo_id_esercizio = trovaIdMassimo(lista_id_esercizi);
                ultimo_id_esercizio ++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });


    }

    public static int trovaIdMassimo(ArrayList<String> idList) {
        // Convertire la lista di stringhe in una lista di interi
        ArrayList<Integer> idIntList = new ArrayList<>();
        for (String id : idList) {
            idIntList.add(Integer.parseInt(id));
        }

        // Trovare l'ID massimo
        int maxId = Collections.max(idIntList);
        return maxId;
    }

    public void recuperaultimoidDenominazione(){

        ArrayList<String> lista_id_esercizidenominazione = new ArrayList<>();

        reference_es_DENOMINAZIONE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idesericzioden = childSnapshot.child("idDenominazione").getValue(String.class);

                    lista_id_esercizidenominazione.add(idesericzioden);
                }

                idDenominazione_es1 = trovaIdMassimo(lista_id_esercizidenominazione);
                idDenominazione_es1 ++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });

    }


    public void recuperaultimoidRipetizione(){
        ArrayList<String> lista_id_eserciziripetizione = new ArrayList<>();

        reference_es_RIPETIZIONE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idesericziorip = childSnapshot.child("idripetizione").getValue(String.class);

                    lista_id_eserciziripetizione.add(idesericziorip);
                }

                idRipetizione_es2 = trovaIdMassimo(lista_id_eserciziripetizione);
                idRipetizione_es2 ++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    public void uploadAudioes2(){
        String audioincar = getString(R.string.audioincar);
        mProgress_es2.setMessage(audioincar);
        mProgress_es2.show();

        StorageReference filepath = mStorage_fileAudio.child("audio esercizi").child("" + stringapervariabile_fileName_audio_es2);

        Uri uri = Uri.fromFile(new File(fileName_audio_es2));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress_es2.dismiss();
                caricaesercizi_ripetizione();
            }
        });
    }

    public void caricaesercizi_ripetizione(){

        HelperClassEsercizio helperClassEs = new HelperClassEsercizio(nomeesercizio2,"Ripetizione",idesperhelper_es1,codlogopedista);
        reference_es.child("" + idesperhelper_es1).setValue(helperClassEs);

        String idesercizioinsertripetizione = "" + idRipetizione_es2;
        char charToRemove = '/';
        String new_stringapervariabile_fileName_audio_es2 = stringapervariabile_fileName_audio_es2.replace(Character.toString(charToRemove), "");

        HelperClassRipetizione helperClassEs_RIPETIZIONE = new HelperClassRipetizione(new_stringapervariabile_fileName_audio_es2,idesperhelper_es1,idesercizioinsertripetizione,monete_es2);
        reference_es_RIPETIZIONE.child("" + idesercizioinsertripetizione).setValue(helperClassEs_RIPETIZIONE);

        String esc = getString(R.string.escaricato);
        Toast.makeText(requireContext(), esc, Toast.LENGTH_SHORT).show();
        ultimo_id_esercizio ++;
        idRipetizione_es2 ++;
    }

    public void uploadImage1_es3(Uri file){
        mProgress_img1_es3.setMessage(immagineload);
        mProgress_img1_es3.show();

        immagine1_es3 = "immagine1_"+ultimo_id_esercizio+"_riconoscimento_" + codlogopedista;
        StorageReference imageRef = storageReference.child("foto esercizi/"+ immagine1_es3);

        imageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        mProgress_img1_es3.dismiss();
                        image = null;
                        image2 = null;
                        uploadImage2_es3(image3);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to upload image
                        String erimg = getString(R.string.errore_immagini);
                        Toast.makeText(requireContext(), erimg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void uploadImage2_es3(Uri file){

        mProgress_img2_es3.setMessage(immagineload);
        mProgress_img2_es3.show();

        immagine2_es3 = "immagine2_"+ultimo_id_esercizio+"_riconoscimento_" + codlogopedista;
        StorageReference imageRef = storageReference.child("foto esercizi/"+ immagine2_es3);

        imageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        mProgress_img2_es3.dismiss();
                        image3 = null;
                        uploadAudio_es3();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to upload image

                        String erimg = getString(R.string.errore_immagini);
                        Toast.makeText(requireContext(), erimg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void uploadAudio_es3(){
        String audioincar = getString(R.string.audioincar);
        mProgress_audio_es3.setMessage(audioincar);
        mProgress_audio_es3.show();

        StorageReference filepath = mStorage_fileAudio.child("audio esercizi").child("" + stringapervariabile_fileName_audio_es3);

        Uri uri = Uri.fromFile(new File(fileName_audio_es3));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress_audio_es3.dismiss();
                caricadati_es3();
            }
        });
    }

    public void caricadati_es3(){
        HelperClassEsercizio helperClassEs = new HelperClassEsercizio(nomeesercizio3,"Riconoscimento",idesperhelper_es1,codlogopedista);
        reference_es.child("" + idesperhelper_es1).setValue(helperClassEs);

        String idesercizioinsertriconoscimento = "" + idRiconoscimento_es3;
        char charToRemove = '/';
        String new_stringapervariabile_fileName_audio_es3 = stringapervariabile_fileName_audio_es3.replace(Character.toString(charToRemove), "");


        HelperClassRiconoscimento helperClassEs_RICONOSCIMENTO = new HelperClassRiconoscimento(new_stringapervariabile_fileName_audio_es3,idesperhelper_es1,idesercizioinsertriconoscimento,immagine1_es3,immagine2_es3,monete_es3);
        reference_es_RICONOSCIMENTO.child("" + idesercizioinsertriconoscimento).setValue(helperClassEs_RICONOSCIMENTO);


        Toast.makeText(requireContext(), "Esercizio caricato", Toast.LENGTH_SHORT).show();
        ultimo_id_esercizio ++;
        idRiconoscimento_es3 ++;
    }

    public void recuperaidriconoscimento(){

        ArrayList<String> lista_id_eserciziriconoscimento = new ArrayList<>();

        reference_es_RICONOSCIMENTO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    String idesericzioric = childSnapshot.child("idriconoscimento").getValue(String.class);

                    lista_id_eserciziriconoscimento.add(idesericzioric);
                }

                idRiconoscimento_es3 = trovaIdMassimo(lista_id_eserciziriconoscimento);
                idRiconoscimento_es3 ++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Dati", "Errore nel leggere i dati", databaseError.toException());
            }
        });
    }

    private String recuperacodicelogopedista() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String logocd = sharedPreferences.getString(KEY_ID_FIREBASE_UTENTE, "");

        return logocd;

    }

    private void loadlingua() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lingua = sharedPreferences.getString(KEY_LINGUA, "it");

        String currentln = getLinguaCorrente(getContext());
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

        getActivity().recreate();
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

    private boolean connessioneinternet(){

        if (NetworkUtils.isConnectedToInternet(getContext())) {
            return true;
        } else {
            String nocon = getString(R.string.no_internet);
            Toast.makeText(getContext(), nocon, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

}