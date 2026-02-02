package com.example.gfm_pronuntiapp_appfinale_esame.ui.addesercizio;

public class HelperClassEsercizio {

    String Nome;
    String Tipologia;
    String idEsercizio;
    String idLogopedista;

    public HelperClassEsercizio(String Nome, String Tipologia, String idEsercizio, String idLogopedista) {
        this.Nome = Nome;
        this.Tipologia = Tipologia;
        this.idEsercizio = idEsercizio;
        this.idLogopedista = idLogopedista;
    }
    public HelperClassEsercizio(){

    }

    public String getNome() {
        return Nome;
    }

    public String getTipologia() {
        return Tipologia;
    }

    public String getIdEsercizio() {
        return idEsercizio;
    }

    public String getIdLogopedista() {
        return idLogopedista;
    }

    public void setNome(String Nome) {
        this.Nome = Nome;
    }

    public void setTipologia(String Tipologia) {
        this.Tipologia = Tipologia;
    }

    public void setIdEsercizio(String idEsercizio) {
        this.idEsercizio = idEsercizio;
    }

    public void setIdLogopedista(String idLogopedista) {
        this.idLogopedista = idLogopedista;
    }

}
