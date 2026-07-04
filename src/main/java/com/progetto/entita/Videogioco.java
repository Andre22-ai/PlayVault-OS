package com.progetto.entita;

import com.progetto.GestoreLingua;

public class Videogioco {
    private int id;
    private String titolo;
    private String genere;
    private int annoUscita;
    private String sviluppatore;
    private String descrizioneEn;
    private String descrizioneIt;

    // Costruttore base
    public Videogioco(String titolo, String genere, int annoUscita, String sviluppatore, String descrizione) {
        this(titolo, genere, annoUscita, sviluppatore, descrizione, descrizione);
    }

    public Videogioco(String titolo, String genere, int annoUscita, String sviluppatore, String descrizioneEn, String descrizioneIt) {
        this.titolo = titolo;
        this.genere = genere;
        this.annoUscita = annoUscita;
        this.sviluppatore = sviluppatore;
        this.descrizioneEn = descrizioneEn;
        this.descrizioneIt = descrizioneIt;
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }
    public int getAnnoUscita() { return annoUscita; }
    public void setAnnoUscita(int annoUscita) { this.annoUscita = annoUscita; }
    public String getSviluppatore() { return sviluppatore; }
    public void setSviluppatore(String sviluppatore) { this.sviluppatore = sviluppatore; }

    public String getDescrizioneLocale() {
        String linguaAttuale = GestoreLingua.getIstanza().getLocaleCorrente().getLanguage();
        
        // Questa spia ci dirà nel terminale cosa sta succedendo
        System.out.println("Switching lingua per " + titolo + " -> Sistema impostato su: " + linguaAttuale);

        if ("it".equalsIgnoreCase(linguaAttuale)) {
            return descrizioneIt != null && !descrizioneIt.isBlank() ? descrizioneIt : descrizioneEn;
        }
        return descrizioneEn != null && !descrizioneEn.isBlank() ? descrizioneEn : descrizioneIt;
    }

    public String getDescrizione() {
        return getDescrizioneLocale();
    }

    public void setDescrizione(String descrizione) {
        this.descrizioneEn = descrizione;
        this.descrizioneIt = descrizione;
    }

    public String getDescrizioneEn() { return descrizioneEn; }
    public void setDescrizioneEn(String descrizioneEn) { this.descrizioneEn = descrizioneEn; }
    public String getDescrizioneIt() { return descrizioneIt; }
    public void setDescrizioneIt(String descrizioneIt) { this.descrizioneIt = descrizioneIt; }
}