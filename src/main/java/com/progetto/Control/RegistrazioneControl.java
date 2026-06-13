package com.progetto.Control;

import com.progetto.DAO.UtenteDAO;
import com.progetto.Entity.Utente;

/**
 * Controller di Caso d'Uso per la Registrazione (Livello CONTROL).
 * Applica le regole di business prima di delegare il salvataggio al DAO.
 */
public class RegistrazioneControl {

    private final UtenteDAO utenteDao;

    /**
     * Costruttore con Dependency Injection per il DAO.
     */
    public RegistrazioneControl(UtenteDAO utenteDao) {
        this.utenteDao = utenteDao;
    }

    /**
     * Esegue la logica di registrazione di un nuovo utente.
     * * @param username Stringa passata dalla UI
     * @param password Stringa passata dalla UI
     * @param confermaPassword Stringa di conferma passata dalla UI
     * @return true se registrato con successo, false se fallisce le validazioni o esiste già
     */
    public boolean registraNuovoUtente(String username, String password, String confermaPassword) {
        // 1. Validazione input (Regole di Business)
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("[CONTROL] Errore: Dati mancanti.");
            return false;
        }
        
        if (!password.equals(confermaPassword)) {
            System.out.println("[CONTROL] Errore: Le password non coincidono!");
            return false;
        }

        // 2. Creazione dell'Entity pura (High Cohesion)
        Utente nuovoUtente = new Utente(username, password);
        nuovoUtente.aggiungiCrediti(50); // Bonus di benvenuto per i nuovi registrati

        // 3. Salvataggio delegato al DAO (Low Coupling)
        return utenteDao.salvaUtente(nuovoUtente);
    }
}