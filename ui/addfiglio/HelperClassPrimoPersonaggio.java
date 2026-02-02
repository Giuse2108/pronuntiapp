package com.example.gfm_pronuntiapp_appfinale_esame.ui.addfiglio;

public class HelperClassPrimoPersonaggio {

    String codAcquisto;
    String idBambino;

    public HelperClassPrimoPersonaggio(){

    }
    public HelperClassPrimoPersonaggio(String codAcquisto, String idBambino) {
        this.codAcquisto = codAcquisto;
        this.idBambino = idBambino;
    }
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
