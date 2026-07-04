package com.progetto.controllo;

import com.progetto.database.UtenteDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

/**
 * Controller di business per le operazioni di impostazioni utente.
 */
public class ImpostazioniControl {

    private final UtenteDAO utenteDAO;

    public ImpostazioniControl(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    public boolean cambiaPassword(String username, String nuovaPassword) {
        if (username == null || username.isBlank() || nuovaPassword == null || nuovaPassword.isBlank()) {
            return false;
        }
        return utenteDAO.aggiornaPassword(username, nuovaPassword);
    }

    public boolean eliminaAccount(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        boolean eliminato = utenteDAO.eliminaAccount(username);
        if (eliminato) {
            Sessione.getIstanza().eseguiLogout();
        }
        return eliminato;
    }

    public Utente getUtenteCorrente() {
        return Sessione.getIstanza().getUtenteCorrente();
    }
}
