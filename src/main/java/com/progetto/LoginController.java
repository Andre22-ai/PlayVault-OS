package com.progetto;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.controllo.AutenticazioneControl;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller di Livello BOUNDARY (Presentazione).
 * Gestisce l'interazione con l'utente ma NON contiene logica di business.
 */
public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // 1. Colleghiamo i campi di testo dell'interfaccia grafica
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // 2. Riferimento al livello Control (Orchestratore)
    // FIX SonarCloud: Messo "final" visto che lo assegniamo solo nel costruttore
    private final AutenticazioneControl authControl;

    /**
     * Costruttore. Qui facciamo la "Dependency Injection" manuale.
     */
    public LoginController() {
        // FIX 2: Addio MySQL fisso! Chiediamo ad App.java il database scelto dall'utente all'avvio
        this.authControl = new AutenticazioneControl(App.getUtenteDAO());
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
            
            // FIX SonarCloud: Usiamo i parametri {0} nel logger invece di unire le stringhe col '+'
            LOGGER.log(Level.INFO, "[BOUNDARY] Accesso Consentito! Benvenuto {0}", utenteLoggato.getUsername());
            
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