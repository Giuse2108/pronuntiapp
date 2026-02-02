package com.example.gfm_pronuntiapp_appfinale_esame.ui.areaterapia;

public class HelperClassAreaTerapiaLogopedista {
    String correzione, data, idBambino, idEsercizio, idTerapia, num_aiuti_util, risposta;

    public HelperClassAreaTerapiaLogopedista()
    {

    }

    public String getCorrezione() {
        return correzione;
    }

    public void setCorrezione(String correzione) {
        this.correzione = correzione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIdBambino() {
        return idBambino;
    }

    public void setIdBambino(String idBambino) {
        this.idBambino = idBambino;
    }

    public String getIdEsercizio() {
        return idEsercizio;
    }

    public void setIdEsercizio(String idEsercizio) {
        this.idEsercizio = idEsercizio;
    }

    public String getIdTerapia() {
        return idTerapia;
    }

    public void setIdTerapia(String idTerapia) {
        this.idTerapia = idTerapia;
    }

    public String getNum_aiuti_util() {
        return num_aiuti_util;
    }

    public void setNum_aiuti_util(String num_aiuti_util) {
        this.num_aiuti_util = num_aiuti_util;
    }

    public String getRisposta() {
        return risposta;
    }

    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }



    public HelperClassAreaTerapiaLogopedista(String correzione, String data, String idBambino, String idEsercizio, String idTerapia,String num_aiuti_util, String risposta) {
        this.correzione = correzione;
        this.data = data;
        this.idBambino = idBambino;
        this.idEsercizio = idEsercizio;
        this.idTerapia = idTerapia;
        this.num_aiuti_util = num_aiuti_util;
        this.risposta = risposta;
    }
}
