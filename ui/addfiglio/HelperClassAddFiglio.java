package com.example.gfm_pronuntiapp_appfinale_esame.ui.addfiglio;

public class HelperClassAddFiglio {
    String id,nome,cognome,datanascita,monete,idlogopedista,idgenitore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDatanascita() {
        return datanascita;
    }

    public void setDatanascita(String datanascita) {
        this.datanascita = datanascita;
    }

    public String getMonete() {
        return monete;
    }

    public void setMonete(String monete) {
        this.monete = monete;
    }

    public String getIdlogopedista() {
        return idlogopedista;
    }

    public void setIdlogopedista(String idlogopedista) {
        this.idlogopedista = idlogopedista;
    }

    public String getIdgenitore() {
        return idgenitore;
    }

    public void setIdgenitore(String idgenitore) {
        this.idgenitore = idgenitore;
    }

    public HelperClassAddFiglio() {
    }

    public HelperClassAddFiglio(String id, String nome, String cognome, String datanascita, String monete, String idlogopedista, String idgenitore) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.datanascita = datanascita;
        this.monete = monete;
        this.idlogopedista = idlogopedista;
        this.idgenitore = idgenitore;
    }
}
