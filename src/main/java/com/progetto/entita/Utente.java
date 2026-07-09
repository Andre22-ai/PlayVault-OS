package com.progetto.entita;

/**
 * Rappresenta l'entità di dominio del Giocatore (Entity).
 * Segue il principio di incapsulamento. Nessuna logica di UI o DB è permessa qui.
 */
public class Utente {
    
    private final String username;
    private String password;
    private int crediti; 
    private String ruolo;

    /**
     * Costruttore dell'entità Utente.
     * * @param username L'identificativo univoco del giocatore.
     * @param password La chiave d'accesso (in un sistema reale dovrebbe essere hashata).
     */
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.crediti = 0; 
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

    private int esperienza = 0; 

    public void aggiungiEsperienza(int exp) {
        this.esperienza += exp;
    }

    public void setEsperienza(int esperienza) {
        this.esperienza = esperienza;
    }

    public int getEsperienza() {
        return esperienza;
    }

    public int getLivello() {
        return (esperienza / 100) + 1;
    }

    public double getProgressoLivello() {
        return (esperienza % 100) / 100.0;
    }

    
    public void aggiungiCrediti(int quantita) {
        if (quantita > 0) {
            this.crediti += quantita;
        }
    }
}