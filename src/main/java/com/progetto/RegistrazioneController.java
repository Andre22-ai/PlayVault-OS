package com.progetto;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.controllo.RegistrazioneControl;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller di Livello BOUNDARY (Presentazione).
 * Gestisce la schermata di Registrazione e invia i dati al Control.
 */
public class RegistrazioneController {

    private static final Logger LOGGER = Logger.getLogger(RegistrazioneController.class.getName());

    // 1. Colleghiamo i campi dell'interfaccia FXML
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmField;

    // 2. Riferimento al livello Control (Logica di Business)
    // FIX SonarCloud: Messo "final" per renderlo immutabile dopo la creazione
    private final RegistrazioneControl regControl;

    /**
     * Costruttore: Dependency Injection.
     */
    public RegistrazioneController() {
        // FIX 2: Chiediamo ad App.java il database Utenti scelto all'avvio (RAM, CSV o MySQL)!
        this.regControl = new RegistrazioneControl(App.getUtenteDAO());
    }

    /**
     * Cattura il click sul bottone di registrazione.
     */
    @FXML
    private void eseguiRegistrazione() throws IOException {
        String user = regUsernameField.getText();
        String pass = regPasswordField.getText();
        String conf = regConfirmField.getText();

        LOGGER.info("[BOUNDARY] Invio dati di registrazione al Control...");
        boolean successo = regControl.registraNuovoUtente(user, pass, conf);

        if (successo) {
            LOGGER.info("[BOUNDARY] Registrazione avvenuta con successo! Torno al Login...");
            App.setRoot("login"); // Teletrasporto al login
        } else {
            LOGGER.warning("[BOUNDARY] Errore di registrazione. Riprova.");
            regUsernameField.clear();
            regPasswordField.clear();
            regConfirmField.clear();
            regUsernameField.setPromptText("ERROR_INVALID_DATA");
        }
    }

    /**
     * Cattura il click sul link "ABORT" per tornare indietro.
     */
    @FXML
    private void tornaAlLogin() throws IOException {
        LOGGER.info("[BOUNDARY] Registrazione annullata. Ritorno al Login.");
        App.setRoot("login");
    }
}