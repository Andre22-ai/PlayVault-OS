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

    // --- FIX S1192: Costanti per le stringhe ripetute delle classi CSS ---
    private static final String CSS_LANG_SELECTED = "btn-lang-selected";
    private static final String CSS_LANG_UNSELECTED = "btn-lang-unselected";

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
            // FIX S4507: Log dell'errore nel terminale, alert generico all'utente
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della password", e);
            mostraAlertErrore("Si è verificato un errore imprevisto.");
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
                    // FIX S4507: Log dell'errore nel terminale, alert generico all'utente
                    LOGGER.log(Level.SEVERE, "Errore di navigazione durante l'eliminazione dell'account", e);
                    mostraAlertErrore("Si è verificato un errore di sistema.");
                }
            }
        });
    }

    @FXML
    @SuppressWarnings("unused")
    private void tornaAllaDashboard() { 
        try {
            App.tornaIndietro(); 
        } catch (IOException e) {
            // --- FIX S4507: Assicurato l'uso esclusivo del LOGGER al posto del printStackTrace ---
            LOGGER.log(Level.SEVERE, "Errore critico durante la navigazione all'indietro", e);
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

        // --- FIX S1192: Applicazione delle costanti CSS ---
        italianoButton.getStyleClass().removeAll(CSS_LANG_SELECTED, CSS_LANG_UNSELECTED);
        englishButton.getStyleClass().removeAll(CSS_LANG_SELECTED, CSS_LANG_UNSELECTED);

        italianoButton.getStyleClass().add(italiano ? CSS_LANG_SELECTED : CSS_LANG_UNSELECTED);
        englishButton.getStyleClass().add(italiano ? CSS_LANG_UNSELECTED : CSS_LANG_SELECTED);
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