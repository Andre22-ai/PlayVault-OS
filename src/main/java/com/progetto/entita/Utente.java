package com.progetto.entita;

/**
 * Rappresenta l'entità di dominio del Giocatore (Entity).
 * Segue il principio di incapsulamento. Nessuna logica di UI o DB è permessa qui.
 */
public class Utente {
    
    private String username;
    private String password;
    private int crediti; // Aggiungiamo un dato di business base
    private String ruolo;

    /**
     * Costruttore dell'entità Utente.
     * * @param username L'identificativo univoco del giocatore.
     * @param password La chiave d'accesso (in un sistema reale dovrebbe essere hashata).
     */
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.crediti = 0; // Default iniziale
        this.ruolo = "PLAYER";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCrediti() {
        return crediti;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public void setCrediti(int crediti) {
        this.crediti = crediti;
    }

    /**
     * Esempio di logica di dominio (Rich Domain Model).
     * L'entità gestisce il proprio stato, non lo fa un controller esterno.
     */
    public void aggiungiCrediti(int quantita) {
        if (quantita > 0) {
            this.crediti += quantita;
        }
    }
}