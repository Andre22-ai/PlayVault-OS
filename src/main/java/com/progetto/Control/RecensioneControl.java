package com.progetto.Control;

import com.progetto.DAO.RecensioneDAOMySQL;
import com.progetto.DAO.UtenteDAOMySQL;
import com.progetto.Entity.Recensione;
import com.progetto.Entity.Sessione;
import com.progetto.Entity.Utente;

public class RecensioneControl {

    private RecensioneDAOMySQL recensioneDAO;
    private UtenteDAOMySQL utenteDAO;

    public RecensioneControl() {
        this.recensioneDAO = new RecensioneDAOMySQL();
        this.utenteDAO = new UtenteDAOMySQL();
    }

    public String elaboraRecensione(Recensione recensione) {
        // 1. Tentiamo di salvare la recensione nel DB
        boolean salvata = recensioneDAO.salvaRecensione(recensione);
        
        if (!salvata) {
            return "ALREADY_REVIEWED"; // Il DB ci ha bloccato (vincolo UNIQUE)
        }

        // 2. Se salvata con successo, eroghiamo la ricompensa di 5 crediti!
        boolean accreditati = utenteDAO.aggiungiCreditiAlDB(recensione.getUsername(), 15);
        
        if (accreditati) {
            // Aggiorniamo anche la RAM, così la Dashboard aggiorna subito il numerino!
            Utente corrente = Sessione.getIstanza().getUtenteCorrente();
            corrente.setCrediti(corrente.getCrediti() + 15);
            return "SUCCESS";
        } else {
            return "SYSTEM_ERROR";
        }
    }

    // Metodo per estrarre le recensioni e mostrarle nel negozio
    public java.util.List<Recensione> ottieniRecensioniGioco(int idGioco) {
        return recensioneDAO.recuperaRecensioniPerGioco(idGioco);
    }

    // Recupera lo storico personale dell'utente
    public java.util.List<Recensione> ottieniRecensioniPersonali(String username) {
        return recensioneDAO.recuperaRecensioniUtente(username);
    }

    public boolean modificaRecensionePersonale(Recensione r) {
        return recensioneDAO.aggiornaRecensione(r);
    }

    public boolean eliminaRecensionePersonale(String username, int idGioco) {
        return recensioneDAO.eliminaRecensione(username, idGioco);
    }



}