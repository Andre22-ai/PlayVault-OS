package com.progetto;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.Entity.Sessione;
import com.progetto.Entity.Utente; // Importante: aggiunto per leggere l'oggetto utente
import com.progetto.controllo.AutenticazioneControl;
import com.progetto.database.UtenteDAOMySQL;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller di Livello BOUNDARY (Presentazione).
 * Gestisce l'interazione con l'utente ma NON contiene logica di business.
 */
public class LoginController {

    // --- NUOVO: Inizializzazione del Logger ---
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // 1. Colleghiamo i campi di testo dell'interfaccia grafica
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // 2. Riferimento al livello Control (Orchestratore)
    private AutenticazioneControl authControl;

    /**
     * Costruttore. Qui facciamo la "Dependency Injection" manuale.
     * Inizializziamo il Control passandogli il nostro Database.
     */
    public LoginController() {
        this.authControl = new AutenticazioneControl(new UtenteDAOMySQL());
    }

    /**
     * Metodo scatenato dal click sul bottone "INSERT COIN & START".
     */
    @FXML
    private void accediAllaDashboard() throws IOException {
        // A. Cattura dei dati (Responsabilità del Boundary)
        String userInserito = usernameField.getText();
        String passInserita = passwordField.getText();

        LOGGER.info("[BOUNDARY] L'utente ha premuto Login. Delego al Control...");

        // B. Esecuzione della Logica (Responsabilità del Control)
        boolean accessoConsentito = authControl.eseguiLogin(userInserito, passInserita);

        // C. Risposta visiva e Navigazione (Responsabilità del Boundary)
        if (accessoConsentito) {
            Utente utenteLoggato = Sessione.getIstanza().getUtenteCorrente();
            LOGGER.info("[BOUNDARY] Accesso Consentito! Benvenuto " + utenteLoggato.getUsername());
            
            // ==================================================
            // IL BIVIO RBAC (Role-Based Access Control)
            // ==================================================
            if ("ADMIN".equals(utenteLoggato.getRuolo())) {
                LOGGER.info("[SISTEMA] Accesso Amministratore Rilevato. Inizializzazione Override...");
                App.setRoot("admin_dashboard"); // Naviga al pannello di controllo
            } else {
                LOGGER.info("[SISTEMA] Accesso Giocatore Rilevato. Caricamento Libreria...");
                App.setRoot("dashboard"); // Naviga alla dashboard standard
            }
            // ==================================================
            
        } else {
            // Se le credenziali sono errate, puliamo i campi per fargli riprovare
            LOGGER.warning("[BOUNDARY] Accesso Negato. Riprova.");
            usernameField.clear();
            passwordField.clear();
            usernameField.setPromptText("ERROR_INVALID_CODE");
        }
    }

    /**
     * Reindirizza l'utente alla schermata di registrazione.
     */
    @FXML
    private void vaiAllaRegistrazione() throws IOException {
        LOGGER.info("[BOUNDARY] Navigazione verso la schermata di Registrazione...");
        App.setRoot("registrazione");
    }
}