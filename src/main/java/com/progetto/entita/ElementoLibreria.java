package com.progetto.entita;

public class ElementoLibreria {
    private final Videogioco videogioco;
    private boolean completato;

    public ElementoLibreria(Videogioco videogioco, boolean completato) {
        this.videogioco = videogioco;
        this.completato = completato;
    }

    public Videogioco getVideogioco() { return videogioco; }
    public boolean isCompletato() { return completato; }
    public void setCompletato(boolean completato) { this.completato = completato; }
}