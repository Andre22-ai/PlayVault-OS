package com.progetto.entita;


public class Sessione {

    private static Sessione istanza;
    private Utente utenteCorrente;

    private Sessione() {}

    
    public static Sessione getIstanza() {
        if (istanza == null) {
            istanza = new Sessione();
        }
        return istanza;
    }

    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public void terminaSessione() {
        this.utenteCorrente = null;
    }

    public void eseguiLogout() {
        terminaSessione();
    }
}
