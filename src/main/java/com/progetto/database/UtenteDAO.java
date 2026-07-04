package com.progetto.database; // o DAO se hai usato la maiuscola

import com.progetto.entita.Utente;

public interface UtenteDAO {
    
    // Metodo esistente per il login
    Utente autentica(String username, String password);

    // IL NUOVO METODO CHE MANCAVA A JAVA
    boolean salvaUtente(Utente utente);

    boolean aggiungiCreditiAlDB(String username, int quantita);

    boolean aggiornaPassword(String username, String nuovaPassword);

    boolean eliminaAccount(String username);

    java.util.List<Utente> recuperaClassifica();
}