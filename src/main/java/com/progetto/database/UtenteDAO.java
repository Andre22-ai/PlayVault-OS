package com.progetto.database;  

import com.progetto.entita.Utente;

public interface UtenteDAO {
    
    Utente autentica(String username, String password);

    boolean salvaUtente(Utente utente);

    boolean aggiungiCreditiAlDB(String username, int quantita);

    boolean aggiornaPassword(String username, String nuovaPassword);

    boolean eliminaAccount(String username);

    boolean aggiornaEsperienza(String username, int nuovaEsperienza);

    java.util.List<Utente> recuperaClassifica();
}