package com.example.gfm_pronuntiapp_appfinale_esame;

public class HelperClassRegistrazioneGenitore {
    String idgenitore,name,cognome,email,password;

    public String getIdgenitore() {
        return idgenitore;
    }

    public void setIdgenitore(String idgenitore) {
        this.idgenitore = idgenitore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HelperClassRegistrazioneGenitore(){
    }

    public HelperClassRegistrazioneGenitore(String idgenitore, String name, String cognome, String email, String password) {
        this.idgenitore = idgenitore;
        this.name = name;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }
}
