package com.progetto.controllo;

import com.progetto.database.UtenteDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

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
    public boolean eseguiLogin(String username, String password) {
        // 1. Validazione base degli input
        if (username == null || username.trim().isEmpty() || password == null) {
            return false;
        }

        // 2. Interrogazione del livello dati tramite interfaccia astratta
        Utente utenteTrovato = utenteDao.autentica(username, password);

        // 3. Gestione del risultato e stato della sessione
        if (utenteTrovato != null) {
            Sessione.getIstanza().setUtenteCorrente(utenteTrovato);
            return true;
        }
        
        return false;
    }
}