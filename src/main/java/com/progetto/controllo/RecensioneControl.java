package com.progetto.controllo;

import com.progetto.database.RecensioneDAO;
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.exceptions.RecensioneInvalidaException;
import com.progetto.exceptions.SalvataggioFallitoException;

public class RecensioneControl {

    private final RecensioneDAO recensioneDAO;
    private final UtenteDAO utenteDAO;

    public RecensioneControl(RecensioneDAO recensioneDAO, UtenteDAO utenteDAO) {
        this.recensioneDAO = recensioneDAO;
        this.utenteDAO = utenteDAO;
    }

    public String elaboraRecensione(Recensione recensione)
            throws RecensioneInvalidaException, SalvataggioFallitoException {
        if (recensione == null || recensione.getUsername() == null || recensione.getUsername().trim().isEmpty()
                || recensione.getCommento() == null || recensione.getCommento().isBlank()
                || recensione.getVoto() < 1 || recensione.getVoto() > 10) {
            throw new RecensioneInvalidaException("dati mancanti o voto non valido");
        }

        boolean salvata = recensioneDAO.salvaRecensione(recensione);

        if (!salvata) {
            throw new RecensioneInvalidaException("hai già lasciato una recensione per questo gioco");
        }

        boolean accreditati = utenteDAO.aggiungiCreditiAlDB(recensione.getUsername(), 15);

        if (accreditati) {
            Utente corrente = Sessione.getIstanza().getUtenteCorrente();
            if (corrente != null) {
                corrente.setCrediti(corrente.getCrediti() + 15);
            }
            return "SUCCESS";
        }

        throw new SalvataggioFallitoException("accredito crediti");
    }

    public java.util.List<Recensione> ottieniRecensioniGioco(int idGioco) {
        return recensioneDAO.recuperaRecensioniPerGioco(idGioco);
    }

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