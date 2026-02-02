package com.example.gfm_pronuntiapp_appfinale_esame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button login_button;
    private EditText email_utente;
    private EditText password_utente;

    private CheckBox checkBox_ricordami;

    private Button guest_genitore;

    private Button guest_logopedista;

    private DatabaseReference reference;
    private DatabaseReference reference_2;

    private String id_utente;

    private static final String PREFS_NAME  = "com.exemple.gfm_pronuntiapp_appfinale_esame_DATI";
    private static final String KEY_EMAIL = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ID_FIREBASE_UTENTE = "id";
    private static final String KEY_REMEMBER_ME = "ricordami";
    private static final String KEY_LINGUA = "lingua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadlingua();
        login_button = findViewById(R.id.loginButton);
        email_utente = findViewById(R.id.email);
        password_utente = findViewById(R.id.password);
        checkBox_ricordami = findViewById(R.id.ricordami);
        guest_genitore = findViewById(R.id.loginButton_guest_genitore);
        guest_logopedista = findViewById(R.id.loginButton_guest_logopedista);

        String password_salvata = loaddati_login();


        guest_genitore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvaLOCALEGenitore();
            }
        });

        guest_logopedista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                salvaLOCALELOGOPEDISTA();
            }
        });



        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean connessoainternet = connessioneinternet();
                if(connessoainternet == true){
                    String password_md5;
                    boolean save_dati = false;

                    if(checkBox_ricordami.isChecked())
                    {
                        save_dati = true;
                    }

                    if(!password_salvata.equals(password_utente.getText().toString()))
                    {
                        try {
                            password_md5 = calculateMD5(password_utente.getText().toString());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        password_md5 = password_utente.getText().toString();
                    }

                    check_login(email_utente.getText().toString(),password_md5,save_dati);

                }

            }
        });

    }

    public void passa_registrazione(View view) {

        startActivity(new Intent(MainActivity.this, Registrazione.class));
        finish();

    }

    public void check_login(String username, String password, boolean salvadati) {
        final String[] ruolo_genitore_log = {""};

        reference = FirebaseDatabase.getInstance().getReference("Genitore");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Itera attraverso tutti i nodi figlio
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Ottieni i valori di email e password per ciascun nodo figlio
                    String email = childSnapshot.child("email").getValue(String.class);
                    String password = childSnapshot.child("password").getValue(String.class);
                    String fire_id_utente_gen = childSnapshot.child("idgenitore").getValue(String.class);

                    if(email.equals(username) && password.equals(password))
                    {
                        ruolo_genitore_log[0] = "Genitore";
                        id_utente = fire_id_utente_gen;
                    }

                    // Fai qualcosa con l'email e la password
                    Log.d("Dati", "Email: " + email + ", Password: " + password);


                }

                reference_2 = FirebaseDatabase.getInstance().getReference("Logopedista");

                reference_2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Itera attraverso tutti i nodi figlio
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            // Ottieni i valori di email e password per ciascun nodo figlio
                            String email = childSnapshot.child("email").getValue(String.class);
                            String password = childSnapshot.child("password").getValue(String.class);
                            String fire_id_utente_log = childSnapshot.child("codLogopedista").getValue(String.class);

                            if(email.equals(username) && password.equals(password))
                            {
                                ruolo_genitore_log[0] = "Logopedista";
                                id_utente = fire_id_utente_log;
                            }

                            // Fai qualcosa con l'email e la password
                            Log.d("Dati", "Email: " + email + ", Password: " + password);


                        }

                        if(!ruolo_genitore_log[0].equals("Genitore") && !ruolo_genitore_log[0].equals("Logopedista"))
                        {
                            ruolo_genitore_log[0] = "Fallito";
                        }

                        if(salvadati == true)
                        {
                            salvadati_genitore(username,password,id_utente);
                            login(ruolo_genitore_log[0]);
                        }
                        else
                        {
                            salvaemailogin(username,id_utente);
                            login(ruolo_genitore_log[0]);
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

    public void login(String messasggio){

        if(messasggio.equals("Genitore"))
        {

            startActivity(new Intent(MainActivity.this, Home_Genitore.class));
            finish();
        }

        if(messasggio.equals("Logopedista"))
        {
            Log.d("Logopedista","Logopedista");
            startActivity(new Intent(MainActivity.this, Home_Logopedista.class));
            finish();
        }

        if(messasggio.equals("Fallito"))
        {
            String loginFailed = getString(R.string.loginFailed);
            Toast.makeText(MainActivity.this, loginFailed, Toast.LENGTH_SHORT).show();
        }

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

    private void salvaemailogin(String email, String idutentedb) {
        String psw = "";
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, psw);
        editor.putString(KEY_ID_FIREBASE_UTENTE, idutentedb);
        editor.putBoolean(KEY_REMEMBER_ME, false);

        editor.apply();

        loaddati_login_2();
    }

    private String loaddati_login() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_EMAIL, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        Log.d("ERROREINPUT","loaddati_login :" + savedUsername);
        Log.d("ERROREINPUT","loaddati_login :" + savedPassword);
        Log.d("ERROREINPUT","loaddati_login :" + rememberMe);

        if(rememberMe == true)
        {
            email_utente.setText(savedUsername);
            password_utente.setText(savedPassword);
            checkBox_ricordami.setChecked(rememberMe);
        }

        return savedPassword;

    }

    private void loaddati_login_2() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_EMAIL, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        Log.d("ERROREINPUT","loaddati_login 2:" + savedUsername);
        Log.d("ERROREINPUT","loaddati_login 2:" + savedPassword);
        Log.d("ERROREINPUT","loaddati_login 2:" + rememberMe);


    }

    private void salvadati_genitore(String user_gen,String psw_gen,String idutentedb){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_EMAIL, user_gen);
        editor.putString(KEY_PASSWORD, psw_gen);
        editor.putString(KEY_ID_FIREBASE_UTENTE, idutentedb);
        editor.putBoolean(KEY_REMEMBER_ME, true);

        editor.apply();

        loaddati_login_2();
    }

    private void salvaLOCALEGenitore() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_EMAIL, "testgenfra@gmail.com");
        editor.putString(KEY_ID_FIREBASE_UTENTE, "2");
        editor.putBoolean(KEY_REMEMBER_ME, false);

        editor.apply();

        startActivity(new Intent(MainActivity.this, Home_Genitore.class));
        finish();


    }

    private void salvaLOCALELOGOPEDISTA() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_ID_FIREBASE_UTENTE, "FrMe379");
        editor.putBoolean(KEY_REMEMBER_ME, false);

        editor.apply();

        startActivity(new Intent(MainActivity.this, Home_Logopedista.class));
        finish();


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