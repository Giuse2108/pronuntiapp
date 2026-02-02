package com.example.gfm_pronuntiapp_appfinale_esame;

public class HelperClassScenario {
    String descrizione;
    String file;
    String idBambino;
    String idScenario;



    String idTerapia;
    String tipologia;

    public HelperClassScenario(){

    }

    public HelperClassScenario(String descrizione, String file, String idBambino, String idScenario, String idTerapia, String tipologia) {
        this.descrizione = descrizione;
        this.file = file;
        this.idBambino = idBambino;
        this.idScenario = idScenario;
        this.idTerapia = idTerapia;
        this.tipologia = tipologia;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getIdBambino() {
        return idBambino;
    }

    public void setIdBambino(String idBambino) {
        this.idBambino = idBambino;
    }

    public String getIdScenario() {
        return idScenario;
    }

    public void setIdScenario(String idScenario) {
        this.idScenario = idScenario;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getIdTerapia() {
        return idTerapia;
    }

    public void setIdTerapia(String idTerapia) {
        this.idTerapia = idTerapia;
    }
}
