package com.progetto;

import com.progetto.controllo.ImpostazioniControl;
import com.progetto.entita.Sessione;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Bozza del controller JavaFX per la schermata Impostazioni.
 * Le chiamate al business layer sono protette da try-catch.
 */
public class ImpostazioniControllerFX {

    // --- COSTANTI PER I MESSAGGI DI ALERT (FIX SonarCloud java:S1192) ---
    private static final String TIPO_ERROR = "ERROR";
    private static final String TIPO_INFO = "INFO";

    @FXML private Label titoloLabel;
    @FXML private PasswordField nuovaPasswordField;

    private final ImpostazioniControl impostazioniControl;

    public ImpostazioniControllerFX() {
        this.impostazioniControl = new ImpostazioniControl(App.getUtenteDAO());
    }

    @FXML
    private void initialize() {
        titoloLabel.setText(GestoreLingua.getIstanza().get("menu.settings"));
    }

    @FXML
    private void cambiaLingua() {
        GestoreLingua.getIstanza().impostaLingua("en");
        mostraMessaggio(TIPO_INFO, GestoreLingua.getIstanza().get("settings.language.changed"));
    }

    @FXML
    private void cambiaPassword() {
        try {
            String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
            String nuovaPassword = nuovaPasswordField.getText();
            boolean successo = impostazioniControl.cambiaPassword(username, nuovaPassword);
            if (successo) {
                mostraMessaggio(TIPO_INFO, GestoreLingua.getIstanza().get("settings.password.changed"));
            } else {
                mostraMessaggio(TIPO_ERROR, GestoreLingua.getIstanza().get("settings.error.password"));
            }
        } catch (Exception e) {
            mostraMessaggio(TIPO_ERROR, e.getMessage());
        }
    }

    @FXML
    private void eliminaAccount() {
        try {
            String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
            boolean successo = impostazioniControl.eliminaAccount(username);
            if (successo) {
                mostraMessaggio(TIPO_INFO, GestoreLingua.getIstanza().get("settings.account.deleted"));
            } else {
                mostraMessaggio(TIPO_ERROR, GestoreLingua.getIstanza().get("settings.error.delete"));
            }
        } catch (Exception e) {
            mostraMessaggio(TIPO_ERROR, e.getMessage());
        }
    }

    private void mostraMessaggio(String tipo, String messaggio) {
        Alert alert = new Alert(TIPO_ERROR.equals(tipo) ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(tipo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}