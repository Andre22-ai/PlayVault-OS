package com.progetto.database;

import java.util.ArrayList;
import java.util.List;

import com.progetto.entita.Utente;


public class UtenteDAOMemory implements UtenteDAO {

    private final List<Utente> utentiInMemoria;

    public UtenteDAOMemory() {
        this.utentiInMemoria = new ArrayList<>();
        
        Utente admin = new Utente("admin", "admin");
        admin.setRuolo("ADMIN");
        admin.aggiungiCrediti(100);
        this.utentiInMemoria.add(admin);
    }

    @Override
    public Utente autentica(String username, String password) {
        return utentiInMemoria.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean salvaUtente(Utente utente) {
        boolean esisteGia = utentiInMemoria.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(utente.getUsername()));
        
        if (esisteGia) {
            return false; 
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
    public boolean aggiornaPassword(String username, String nuovaPassword) {
        for (Utente utente : utentiInMemoria) {
            if (utente.getUsername().equals(username)) {
                utente.setPassword(nuovaPassword);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminaAccount(String username) {
        return utentiInMemoria.removeIf(utente -> utente.getUsername().equals(username));
    }

    @Override
    public List<Utente> recuperaClassifica() {
        return utentiInMemoria.stream()
                .sorted((u1, u2) -> Integer.compare(u2.getCrediti(), u1.getCrediti()))
                .toList(); 
    }

    @Override
    public boolean aggiornaEsperienza(String username, int nuovaEsperienza) {
        for (Utente utente : utentiInMemoria) {
            if (utente.getUsername().equals(username)) {
                utente.setEsperienza(nuovaEsperienza);
                return true;
            }
        }
        return false;
    }
}