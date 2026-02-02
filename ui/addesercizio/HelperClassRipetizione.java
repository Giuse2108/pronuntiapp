package com.example.gfm_pronuntiapp_appfinale_esame.ui.addesercizio;

public class HelperClassRipetizione {

    String audio;
    String idEsercizio_RIP;
    String idripetizione;
    String ricompensa;

    public HelperClassRipetizione(String audio, String idEsercizio_RIP, String idripetizione, String ricompensa) {
        this.audio = audio;
        this.idEsercizio_RIP = idEsercizio_RIP;
        this.idripetizione = idripetizione;
        this.ricompensa = ricompensa;
    }

    public HelperClassRipetizione(){

    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getIdEsercizio_RIP() {
        return idEsercizio_RIP;
    }

    public void setIdEsercizio_RIP(String idEsercizio_RIP) {
        this.idEsercizio_RIP = idEsercizio_RIP;
    }

    public String getIdripetizione() {
        return idripetizione;
    }

    public void setIdripetizione(String idripetizione) {
        this.idripetizione = idripetizione;
    }

    public String getRicompensa() {
        return ricompensa;
    }

    public void setRicompensa(String ricompensa) {
        this.ricompensa = ricompensa;
    }

}
