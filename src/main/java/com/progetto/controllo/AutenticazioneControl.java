package com.progetto.controllo;

import com.progetto.database.UtenteDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.exceptions.CredenzialiErrateException;

/**
 * Controller di Caso d'Uso (BCE Architecture).
 * Gestisce esclusivamente il flusso di Autenticazione.
 */
public class AutenticazioneControl {

    private final UtenteDAO utenteDao;

    /**
     * Costruttore con Dependency Injection.
     * Permette un Low Coupling passando il DAO dall'esterno.
     */
    public AutenticazioneControl(UtenteDAO utenteDao) {
        this.utenteDao = utenteDao;
    }

    /**
     * Esegue il tentativo di login orchestrando DAO e Sessione.
     * * @param username Stringa passata dalla UI
     * @param password Stringa passata dalla UI
     * @return true se il login ha successo, false altrimenti
     */
    public boolean eseguiLogin(String username, String password) throws CredenzialiErrateException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new CredenzialiErrateException();
        }

        Utente utenteTrovato = utenteDao.autentica(username, password);

        if (utenteTrovato != null) {
            Sessione.getIstanza().setUtenteCorrente(utenteTrovato);
            return true;
        }

        throw new CredenzialiErrateException();
    }
}