package com.progetto.entita;

/**
 * Gestisce lo stato globale dell'utente attualmente autenticato.
 * Implementa il Design Pattern Creazionale: Singleton.
 */
public class Sessione {

    private static Sessione istanza;
    private Utente utenteCorrente;

    // Costruttore privato: impedisce l'istanziazione dall'esterno
    private Sessione() {}

    /**
     * Punto di accesso globale all'unica istanza di Sessione.
     * Metodo thread-safe base.
     */
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
}
