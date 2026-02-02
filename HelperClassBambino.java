package com.example.gfm_pronuntiapp_appfinale_esame;

public class HelperClassBambino {
    String cognome;
    String datanascita;
    String id;
    String idgenitore;
    String idlogopedista;
    String monete;
    String nome;

    public HelperClassBambino(){

    }

    public HelperClassBambino(String cognome, String datanascita, String id, String idgenitore, String idlogopedista, String monete, String nome) {
        this.cognome = cognome;
        this.datanascita = datanascita;
        this.id = id;
        this.idgenitore = idgenitore;
        this.idlogopedista = idlogopedista;
        this.monete = monete;
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getDatanascita() {
        return datanascita;
    }

    public void setDatanascita(String datanascita) {
        this.datanascita = datanascita;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdgenitore() {
        return idgenitore;
    }

    public void setIdgenitore(String idgenitore) {
        this.idgenitore = idgenitore;
    }

    public String getIdlogopedista() {
        return idlogopedista;
    }

    public void setIdlogopedista(String idlogopedista) {
        this.idlogopedista = idlogopedista;
    }

    public String getMonete() {
        return monete;
    }

    public void setMonete(String monete) {
        this.monete = monete;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
