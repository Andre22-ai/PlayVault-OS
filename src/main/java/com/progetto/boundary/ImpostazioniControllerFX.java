package com.progetto.boundary;

import java.io.IOException;

import com.progetto.App;
import com.progetto.GestoreLingua;
import com.progetto.controllo.ImpostazioniControl;
import com.progetto.entita.Sessione;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ImpostazioniControllerFX {

    private static final String STYLE_SELECTED = "-fx-background-color: #ff00ff; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-color: #ff00ff; -fx-border-radius: 20; -fx-border-width: 2; -fx-cursor: hand;";
    private static final String STYLE_UNSELECTED = "-fx-background-color: #000000; -fx-text-fill: #00ffff; -fx-background-radius: 20; -fx-border-color: #00ffff; -fx-border-radius: 20; -fx-border-width: 2; -fx-cursor: hand;";

    @FXML private Button italianoButton;
    @FXML private Button englishButton;
    @FXML private PasswordField nuovaPasswordField;
    @FXML private Label titleLabel;
    @FXML private Label languageLabel;
    @FXML private Label securityLabel;
    @FXML private Label dangerLabel;
    @FXML private Button passwordButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    private final ImpostazioniControl impostazioniControl;

    public ImpostazioniControllerFX() {
        this.impostazioniControl = new ImpostazioniControl(App.getUtenteDAO());
    }

    @FXML
    private void initialize() {
        aggiornaInterfaccia();
    }

    @FXML
    private void selezionaItaliano() {
        GestoreLingua.getIstanza().impostaLingua("it");
        aggiornaInterfaccia();
        mostraAlertInformazione(getTesto("settings.language.changed.title"), getTesto("settings.language.changed"));
    }

    @FXML
    private void selezionaEnglish() {
        GestoreLingua.getIstanza().impostaLingua("en");
        aggiornaInterfaccia();
        mostraAlertInformazione(getTesto("settings.language.changed.title"), getTesto("settings.language.changed"));
    }

    @FXML
    private void aggiornaPassword() {
        try {
            String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
            String nuovaPassword = nuovaPasswordField.getText();
            boolean successo = impostazioniControl.cambiaPassword(username, nuovaPassword);
            if (successo) {
                mostraAlertInformazione(getTesto("settings.password.success.title"), getTesto("settings.password.changed"));
            } else {
                mostraAlertErrore(getTesto("settings.password.error"));
            }
        } catch (Exception e) {
            mostraAlertErrore(e.getMessage());
        }
    }

    @FXML
    private void eliminaAccount() {
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle(getTesto("settings.confirm.title"));
        conferma.setHeaderText(null);
        conferma.setContentText(getTesto("settings.confirm.delete"));

        conferma.showAndWait().ifPresent(risposta -> {
            if (risposta == javafx.scene.control.ButtonType.OK) {
                try {
                    String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
                    boolean successo = impostazioniControl.eliminaAccount(username);
                    if (successo) {
                        mostraAlertInformazione(getTesto("settings.delete.success.title"), getTesto("settings.account.deleted"));
                        App.setRoot("login");
                    } else {
                        mostraAlertErrore(getTesto("settings.delete.error"));
                    }
                } catch (Exception e) {
                    mostraAlertErrore(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void tornaAllaDashboard() {
        try {
            App.setRoot("dashboard");
        } catch (IOException e) {
            mostraAlertErrore(e.getMessage());
        }
    }

    private void aggiornaInterfaccia() {
        GestoreLingua lingua = GestoreLingua.getIstanza();
        boolean italiano = "it".equals(lingua.getLocaleCorrente().getLanguage());

        titleLabel.setText(lingua.get("menu.settings").toUpperCase());
        languageLabel.setText(lingua.get("settings.language.title").toUpperCase());
        securityLabel.setText(lingua.get("settings.security.title").toUpperCase());
        dangerLabel.setText(lingua.get("settings.danger.title").toUpperCase());
        italianoButton.setText(lingua.get("settings.language.italiano").toUpperCase());
        englishButton.setText(lingua.get("settings.language.english").toUpperCase());
        passwordButton.setText(lingua.get("menu.settings.password").toUpperCase());
        deleteButton.setText(lingua.get("menu.settings.delete").toUpperCase());
        backButton.setText(lingua.get("menu.settings.back").toUpperCase());
        nuovaPasswordField.setPromptText(lingua.get("settings.password.prompt"));

        italianoButton.setStyle(italiano ? STYLE_SELECTED : STYLE_UNSELECTED);
        englishButton.setStyle(italiano ? STYLE_UNSELECTED : STYLE_SELECTED);
    }

    private String getTesto(String chiave) {
        return GestoreLingua.getIstanza().get(chiave);
    }

    private void mostraAlertInformazione(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraAlertErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
