package com.progetto.controllo;

import java.util.logging.Logger;

import com.progetto.database.UtenteDAO;
import com.progetto.entita.Utente;

/**
 * Controller di Caso d'Uso per la Registrazione (Livello CONTROL).
 * Applica le regole di business prima di delegare il salvataggio al DAO.
 */
public class RegistrazioneControl {

    // --- NUOVO: Inizializziamo il Logger per questa classe ---
    private static final Logger LOGGER = Logger.getLogger(RegistrazioneControl.class.getName());

    private final UtenteDAO utenteDao;

    /**
     * Costruttore con Dependency Injection per il DAO.
     */
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
    public boolean registraNuovoUtente(String username, String password, String confermaPassword) {
        // 1. Validazione input (Regole di Business)
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            LOGGER.warning("[CONTROL] Errore: Dati mancanti.");
            return false;
        }
        
        if (!password.equals(confermaPassword)) {
            LOGGER.warning("[CONTROL] Errore: Le password non coincidono!");
            return false;
        }

        // 2. Creazione dell'Entity pura (High Cohesion)
        Utente nuovoUtente = new Utente(username, password);
        nuovoUtente.aggiungiCrediti(50); // Bonus di benvenuto per i nuovi registrati

        // 3. Salvataggio delegato al DAO (Low Coupling)
        return utenteDao.salvaUtente(nuovoUtente);
    }
}