package com.example.gfm_pronuntiapp_appfinale_esame.ui.addesercizio;

public class HelperClassRiconoscimento {
    String audio;
    String idEsercizio_RIC;
    String idriconoscimento;
    String immagine1;
    String immagine2;
    String ricompensa;

    public HelperClassRiconoscimento(){

    }

    public HelperClassRiconoscimento(String audio, String idEsercizio_RIC, String idriconoscimento, String immagine1, String immagine2, String ricompensa) {
        this.audio = audio;
        this.idEsercizio_RIC = idEsercizio_RIC;
        this.idriconoscimento = idriconoscimento;
        this.immagine1 = immagine1;
        this.immagine2 = immagine2;
        this.ricompensa = ricompensa;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getIdEsercizio_RIC() {
        return idEsercizio_RIC;
    }

    public void setIdEsercizio_RIC(String idEsercizio_RIC) {
        this.idEsercizio_RIC = idEsercizio_RIC;
    }

    public String getIdriconoscimento() {
        return idriconoscimento;
    }

    public void setIdriconoscimento(String idriconoscimento) {
        this.idriconoscimento = idriconoscimento;
    }

    public String getImmagine1() {
        return immagine1;
    }

    public void setImmagine1(String immagine1) {
        this.immagine1 = immagine1;
    }

    public String getImmagine2() {
        return immagine2;
    }

    public void setImmagine2(String immagine2) {
        this.immagine2 = immagine2;
    }

    public String getRicompensa() {
        return ricompensa;
    }

    public void setRicompensa(String ricompensa) {
        this.ricompensa = ricompensa;
    }


}
