package com.progetto.controllo;

import java.util.logging.Logger;

import com.progetto.database.UtenteDAO;
import com.progetto.entita.Utente;
import com.progetto.exceptions.UtenteGiaEsistenteException;


public class RegistrazioneControl {

    private static final Logger LOGGER = Logger.getLogger(RegistrazioneControl.class.getName());

    private final UtenteDAO utenteDao;

    
    public RegistrazioneControl(UtenteDAO utenteDao) {
        this.utenteDao = utenteDao;
    }

    /**
     * Esegue la logica di registrazione di un nuovo utente.
     * @param username Stringa passata dalla UI
     * @param password Stringa passata dalla UI
     * @param confermaPassword Stringa di conferma passata dalla UI
     * @return true se registrato con successo, false se fallisce le validazioni o esiste già
     */
    public boolean registraNuovoUtente(String username, String password, String confermaPassword)
            throws UtenteGiaEsistenteException {
        
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            LOGGER.warning("[CONTROL] Errore: Dati mancanti.");
            throw new IllegalArgumentException("Username e password sono obbligatori.");
        }

        if (!password.equals(confermaPassword)) {
            LOGGER.warning("[CONTROL] Errore: Le password non coincidono!");
            throw new IllegalArgumentException("Le password non coincidono.");
        }

        Utente nuovoUtente = new Utente(username, password);
        nuovoUtente.aggiungiCrediti(50);  

        boolean salvato = utenteDao.salvaUtente(nuovoUtente);
        if (!salvato) {
            throw new UtenteGiaEsistenteException(username);
        }
        return true;
    }
}