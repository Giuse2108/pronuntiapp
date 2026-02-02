package com.example.gfm_pronuntiapp_appfinale_esame;

public class HelperClassregistrazioneLogopedista {
    String codLogopedista,nome,cognome,email,password,sede;

    public String getCodLogopedista() {
        return codLogopedista;
    }

    public void setCodLogopedista(String codLogopedista) {
        this.codLogopedista = codLogopedista;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public HelperClassregistrazioneLogopedista() {
    }

    public HelperClassregistrazioneLogopedista(String codLogopedista, String nome, String cognome, String email, String password, String sede) {
        this.codLogopedista = codLogopedista;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.sede = sede;
    }
}
