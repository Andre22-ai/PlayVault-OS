package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.AutenticazioneControl;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.exceptions.CredenzialiErrateException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final AutenticazioneControl authControl;

   
    public LoginController() {
        this.authControl = new AutenticazioneControl(App.getUtenteDAO());
    }

    
    @FXML
    @SuppressWarnings("unused")
    private void accediAllaDashboard() throws IOException {
        String userInserito = usernameField.getText();
        String passInserita = passwordField.getText();

        LOGGER.info("[BOUNDARY] L'utente ha premuto Login. Delego al Control...");

        try {
            authControl.eseguiLogin(userInserito, passInserita);

            Utente utenteLoggato = Sessione.getIstanza().getUtenteCorrente();

            LOGGER.log(Level.INFO, "[BOUNDARY] Accesso Consentito! Benvenuto {0}", utenteLoggato.getUsername());

            
            if ("ADMIN".equals(utenteLoggato.getRuolo())) {
                LOGGER.info("[SISTEMA] Accesso Amministratore Rilevato. Inizializzazione Override...");
                App.setRoot("admin_dashboard"); 
            } else {
                LOGGER.info("[SISTEMA] Accesso Giocatore Rilevato. Caricamento Libreria...");
                App.setRoot("dashboard"); 
            }
        } catch (CredenzialiErrateException e) {
            LOGGER.warning(e.getMessage());
            usernameField.clear();
            passwordField.clear();
            mostraErrore(e.getMessage());
        }
    }

   
    @FXML
    @SuppressWarnings("unused")
    private void vaiAllaRegistrazione() throws IOException {
        LOGGER.info("[BOUNDARY] Navigazione verso la schermata di Registrazione...");
        App.setRoot("registrazione");
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Accesso negato");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}