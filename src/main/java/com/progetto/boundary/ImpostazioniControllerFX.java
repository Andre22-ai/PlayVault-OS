package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.ImpostazioniControl;
import com.progetto.entita.Sessione;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ImpostazioniControllerFX {

    private static final Logger LOGGER = Logger.getLogger(ImpostazioniControllerFX.class.getName());

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
    @SuppressWarnings("unused")
    private void initialize() {
        aggiornaInterfaccia();
    }

    @FXML
    @SuppressWarnings("unused")
    private void selezionaItaliano() {
        GestoreLingua.getIstanza().impostaLingua("it");
        aggiornaInterfaccia();
        mostraAlertInformazione(getTesto("settings.language.changed.title"), getTesto("settings.language.changed"));
    }

    @FXML
    @SuppressWarnings("unused")
    private void selezionaEnglish() {
        GestoreLingua.getIstanza().impostaLingua("en");
        aggiornaInterfaccia();
        mostraAlertInformazione(getTesto("settings.language.changed.title"), getTesto("settings.language.changed"));
    }

    @FXML
    @SuppressWarnings("unused")
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
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della password", e);
            mostraAlertErrore(e.getMessage());
        }
    }

    @FXML
    @SuppressWarnings("unused")
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
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'account", e);
                    mostraAlertErrore(e.getMessage());
                }
            }
        });
    }

    @FXML
    @SuppressWarnings("unused")
    private void tornaAllaDashboard() { // <- IL NOME DEVE RIMANERE QUESTO PER L'FXML!
        try {
            // Ma dentro usiamo la memoria intelligente che abbiamo creato:
            App.tornaIndietro(); 
        } catch (IOException e) {
            e.printStackTrace();
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

        // Rimuoviamo prima le classi esistenti per evitare conflitti
        italianoButton.getStyleClass().removeAll("btn-lang-selected", "btn-lang-unselected");
        englishButton.getStyleClass().removeAll("btn-lang-selected", "btn-lang-unselected");

        // Applichiamo la logica CSS
        italianoButton.getStyleClass().add(italiano ? "btn-lang-selected" : "btn-lang-unselected");
        englishButton.getStyleClass().add(italiano ? "btn-lang-unselected" : "btn-lang-selected");
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