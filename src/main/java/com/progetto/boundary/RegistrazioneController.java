package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.RegistrazioneControl;
import com.progetto.exceptions.UtenteGiaEsistenteException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class RegistrazioneController {

    private static final Logger LOGGER = Logger.getLogger(RegistrazioneController.class.getName());

    
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmField;

    
    private final RegistrazioneControl regControl;

    
    public RegistrazioneController() {
        this.regControl = new RegistrazioneControl(App.getUtenteDAO());
    }

    
    @FXML
    private void eseguiRegistrazione() throws IOException {
        String user = regUsernameField.getText();
        String pass = regPasswordField.getText();
        String conf = regConfirmField.getText();

        LOGGER.info("[BOUNDARY] Invio dati di registrazione al Control...");
        try {
            regControl.registraNuovoUtente(user, pass, conf);
            LOGGER.info("[BOUNDARY] Registrazione avvenuta con successo! Torno al Login...");
            App.setRoot("login"); 
        } catch (UtenteGiaEsistenteException e) {
            LOGGER.warning(e.getMessage());
            regUsernameField.clear();
            regPasswordField.clear();
            regConfirmField.clear();
            mostraErrore(e.getMessage());
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

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registrazione fallita");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}