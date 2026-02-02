package com.example.gfm_pronuntiapp_appfinale_esame;

public class PersonaggioBambino {
    private String codAcquisto; // Assicurati di utilizzare i tipi di dati corretti
    private String idBambino;

    // Costruttore vuoto pubblico
    public PersonaggioBambino() {
    }

    public PersonaggioBambino(String codAcquisto, String idBambino) {
        this.codAcquisto = codAcquisto;
        this.idBambino = idBambino;
    }

    // Getter e setter per i campi
    public String getCodAcquisto() {
        return codAcquisto;
    }

    public void setCodAcquisto(String codAcquisto) {
        this.codAcquisto = codAcquisto;
    }

    public String getIdBambino() {
        return idBambino;
    }

    public void setIdBambino(String idBambino) {
        this.idBambino = idBambino;
    }
}
