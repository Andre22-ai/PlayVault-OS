package com.progetto.DAO; // o DAO se hai usato la maiuscola

import com.progetto.Entity.Utente;

public interface UtenteDAO {
    
    // Metodo esistente per il login
    Utente autentica(String username, String password);

    // IL NUOVO METODO CHE MANCAVA A JAVA
    boolean salvaUtente(Utente utente);

    boolean aggiungiCreditiAlDB(String username, int quantita);

    // Aggiungi questa riga nel file UtenteDAO.java
    java.util.List<Utente> recuperaClassifica();
}