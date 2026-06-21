package com.progetto.database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.progetto.entita.Utente;

/**
 * Versione DEMO (In-Memory) del DAO.
 * Salva i dati in una lista temporanea. Si azzera alla chiusura dell'app.
 */
public class UtenteDAOMemory implements UtenteDAO {

    // Questa è la nostra "tabella" temporanea nella RAM
    private final List<Utente> utentiInMemoria;

    public UtenteDAOMemory() {
        this.utentiInMemoria = new ArrayList<>();
        
        // BONUS: Creiamo un utente amministratore di default per fare subito i test!
        Utente admin = new Utente("admin", "admin");
        admin.setRuolo("ADMIN");
        admin.aggiungiCrediti(100);
        this.utentiInMemoria.add(admin);
    }

    @Override
    public Utente autentica(String username, String password) {
        // Cerca nella lista un utente con quello username e password
        return utentiInMemoria.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean salvaUtente(Utente utente) {
        // Controlla se l'utente esiste già
        boolean esisteGia = utentiInMemoria.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(utente.getUsername()));
        
        if (esisteGia) {
            return false; // Utente già presente
        }
        
        utentiInMemoria.add(utente);
        return true;
    }

    @Override
    public boolean aggiungiCreditiAlDB(String username, int quantita) {
        Utente u = utentiInMemoria.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (u != null) {
            u.aggiungiCrediti(quantita);
            return true;
        }
        return false;
    }

    @Override
    public List<Utente> recuperaClassifica() {
        // Ordina la lista in base ai crediti (dal più grande al più piccolo)
        return utentiInMemoria.stream()
                .sorted((u1, u2) -> Integer.compare(u2.getCrediti(), u1.getCrediti()))
                .collect(Collectors.toList());
    }
}