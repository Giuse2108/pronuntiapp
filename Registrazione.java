package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Registrazione extends AppCompatActivity {

    private Spinner spinner_ruoli;
    private TextView labelSede;
    private EditText input_sede;

    private Button registrati_btn;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_LINGUA = "lingua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        loadlingua();
        spinner_ruoli = findViewById(R.id.ruolo_spinner);
        labelSede = findViewById(R.id.label_sede);
        input_sede = findViewById(R.id.input_sede);

        registrati_btn = findViewById(R.id.reg_button);

        String regfallitamx = getString(R.string.regfallita);

        registrati_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ruolo = spinner_ruoli.getSelectedItem().toString();

                if(ruolo.equals("Genitore"))
                {
                    EditText input_nome = findViewById(R.id.input_nome_utente);
                    String nome = input_nome.getText().toString();
                    EditText input_cognome = findViewById(R.id.input_cognome);
                    String cognome = input_cognome.getText().toString();
                    EditText input_email = findViewById(R.id.input_email);
                    String email = input_email.getText().toString();
                    EditText input_password = findViewById(R.id.input_password);
                    String password = input_password.getText().toString();
                    String password_finale;

                    try {
                        password_finale = calculateMD5(password);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }

                    if(!nome.equals("") && !cognome.equals("") && !email.equals("") && !password.equals(""))
                    {
                        registraGenitore(nome,cognome,email,password_finale);
                    }
                    else
                    {
                        Toast.makeText(Registrazione.this, regfallitamx, Toast.LENGTH_SHORT).show();
                    }

                }

                if(ruolo.equals("Logopedista"))
                {
                    EditText input_nome = findViewById(R.id.input_nome_utente);
                    String nome = input_nome.getText().toString();
                    EditText input_cognome = findViewById(R.id.input_cognome);
                    String cognome = input_cognome.getText().toString();
                    EditText input_email = findViewById(R.id.input_email);
                    String email = input_email.getText().toString();
                    EditText input_password = findViewById(R.id.input_password);
                    String password = input_password.getText().toString();
                    String password_finale;

                    try {
                        password_finale = calculateMD5(password);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }

                    EditText input_sede = findViewById(R.id.input_sede);
                    String sede = input_sede.getText().toString();

                    if(!nome.equals("") && !cognome.equals("") && !email.equals("") && !password.equals("") && !sede.equals(""))
                    {
                        boolean connessoainternet = connessioneinternet();
                        if(connessoainternet == true){
                            registraLogopedista(nome,cognome,email,password_finale,sede);
                        }

                    }
                    else
                    {
                        Toast.makeText(Registrazione.this, regfallitamx, Toast.LENGTH_SHORT).show();
                    }
                }

                if(ruolo.equals("Seleziona"))
                {
                    Toast.makeText(Registrazione.this, regfallitamx, Toast.LENGTH_SHORT).show();
                }
            }
        });


        spinner_ruoli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = parentView.getItemAtPosition(position).toString();

                if(selectedOption.equals("Genitore") || selectedOption.equals("Seleziona"))
                {
                    labelSede.setVisibility(View.GONE);
                    input_sede.setVisibility(View.GONE);
                }

                if(selectedOption.equals("Logopedista"))
                {
                    labelSede.setVisibility(View.VISIBLE);
                    input_sede.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }

    public void registraGenitore(String nome_G,String cognome_G,String email_G,String password_G){

        ArrayList<Integer> vettoreIdGenitori = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Genitore");

        final int[] massimo = {1};

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String nomeOggetto = snapshot.getKey();

                    vettoreIdGenitori.add(Integer.valueOf(nomeOggetto));

                    Log.d("Firebase", "Nome dell'oggetto: " + nomeOggetto);


                }

                for (int numero : vettoreIdGenitori) {
                    if (numero > massimo[0]) {
                        massimo[0] = numero;
                    }
                }

                int idgennuovo = massimo[0] + 1;
                Log.d("Firebase", "Max: " + idgennuovo);

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Genitore");

                HelperClassRegistrazioneGenitore helperClassGen = new HelperClassRegistrazioneGenitore("" + idgennuovo,"" + nome_G,"" + cognome_G,"" + email_G,"" + password_G);
                reference.child("" + idgennuovo).setValue(helperClassGen);

                startActivity(new Intent(Registrazione.this, DashboardGenitore.class));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Questo metodo viene chiamato se c'è un errore durante la lettura dei dati.
                Log.e("Firebase", "Errore durante il recupero dei dati: " + databaseError.getMessage());
            }
        });

    }

    public void registraLogopedista(String nome_L,String cognome_L,String email_L,String password_L,String sede_L){

        ArrayList<String> vettoreIdLogopedisti = new ArrayList<>();
        final Boolean[] esci = {true};

        Random random = new Random();
        final int[] numeroCasuale = {random.nextInt(1001)};
        final String[] cod_gen = {nome_L.substring(0, 2) + "" + cognome_L.substring(0, 2) + "" + numeroCasuale[0]};

        reference = FirebaseDatabase.getInstance().getReference("Genitore");

        final int[] massimo = {1};

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String nomeOggetto = snapshot.getKey();

                    vettoreIdLogopedisti.add(nomeOggetto);

                    Log.d("Firebase", "Nome dell'oggetto: " + nomeOggetto);


                }

                do{

                    for (String idlog : vettoreIdLogopedisti) {
                        if (idlog.equals(cod_gen[0])) {
                            esci[0] = false;
                        }
                    }

                    if(esci[0] == false){
                        numeroCasuale[0] = random.nextInt(1001);
                        cod_gen[0] = nome_L.substring(0, 2) + ""+ cognome_L.substring(0, 2) + "" + numeroCasuale[0];
                    }

                }while(esci[0] == false);

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Logopedista");

                HelperClassregistrazioneLogopedista helperClassLog = new HelperClassregistrazioneLogopedista("" + cod_gen[0],"" + nome_L,"" + cognome_L,"" + email_L,"" + password_L,"" + sede_L);
                reference.child("" + cod_gen[0]).setValue(helperClassLog);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Questo metodo viene chiamato se c'è un errore durante la lettura dei dati.
                Log.e("Firebase", "Errore durante il recupero dei dati: " + databaseError.getMessage());
            }
        });
    }

    private static String calculateMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());

        byte[] byteData = md.digest();

        // Convertire il byte in formato esadecimale
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteData) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
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