package com.progetto.entita;

public class Videogioco {
    private int id;
    private String titolo;
    private String genere;
    private int annoUscita;
    private String sviluppatore;
    private String descrizione;

    // Costruttore
    public Videogioco(String titolo, String genere, int annoUscita, String sviluppatore, String descrizione) {
        this.titolo = titolo;
        this.genere = genere;
        this.annoUscita = annoUscita;
        this.sviluppatore = sviluppatore;
        this.descrizione = descrizione;
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
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}