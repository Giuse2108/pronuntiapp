package com.example.gfm_pronuntiapp_appfinale_esame.ui.appuntamenti;

public class HelperClassAppuntamentiGenitore {

    String data;
    String idappuntamento;
    String idgenitore;
    String idlogopedista;
    String incontro;
    String motivo;
    String ora;
    String stato;

    public HelperClassAppuntamentiGenitore(){

    }

    public HelperClassAppuntamentiGenitore(String data, String idappuntamento, String idgenitore, String idlogopedista, String incontro, String motivo, String ora, String stato) {
        this.data = data;
        this.idappuntamento = idappuntamento;
        this.idgenitore = idgenitore;
        this.idlogopedista = idlogopedista;
        this.incontro = incontro;
        this.motivo = motivo;
        this.ora = ora;
        this.stato = stato;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIdappuntamento() {
        return idappuntamento;
    }

    public void setIdappuntamento(String idappuntamento) {
        this.idappuntamento = idappuntamento;
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

    public String getIncontro() {
        return incontro;
    }

    public void setIncontro(String incontro) {
        this.incontro = incontro;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }
}
