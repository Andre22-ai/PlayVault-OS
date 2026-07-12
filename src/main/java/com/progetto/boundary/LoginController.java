package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.AutenticazioneControl;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.exceptions.CredenzialiErrateException;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // Elementi preesistenti
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // NUOVI Elementi per la traduzione dinamica
    @FXML private Button btnSwitchLang;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Label lblTitle; 
    @FXML private Label lblUsername;
    @FXML private Label lblPassword;

    private final AutenticazioneControl authControl;

    public LoginController() {
        this.authControl = new AutenticazioneControl(App.getUtenteDAO());
    }

    @FXML
    public void initialize() {
        // All'avvio, traduciamo l'interfaccia nella lingua corrente
        aggiornaTestiUI();
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

    @FXML
    @SuppressWarnings("unused")
    private void cambiaLingua() {
        String linguaAttuale = GestoreLingua.getIstanza().getLocaleCorrente().getLanguage();
        
        if ("it".equalsIgnoreCase(linguaAttuale)) {
            GestoreLingua.getIstanza().impostaLingua("en");
        } else {
            GestoreLingua.getIstanza().impostaLingua("it");
        }
        
        aggiornaTestiUI();
    }

    private void aggiornaTestiUI() {
        // I null check evitano crash se l'FXML non ha ancora questi fx:id
        if (btnSwitchLang != null) btnSwitchLang.setText(GestoreLingua.getIstanza().get("login.btn.switch"));
        if (btnLogin != null) btnLogin.setText(GestoreLingua.getIstanza().get("login.btn.login"));
        if (btnRegister != null) btnRegister.setText(GestoreLingua.getIstanza().get("login.btn.register"));
        if (lblTitle != null) lblTitle.setText(GestoreLingua.getIstanza().get("login.lbl.title"));
        if (lblUsername != null) lblUsername.setText(GestoreLingua.getIstanza().get("login.lbl.username"));
        if (lblPassword != null) lblPassword.setText(GestoreLingua.getIstanza().get("login.lbl.password"));
        
        // Traduciamo anche i suggerimenti trasparenti nei campi di testo (Placeholder)
        if (usernameField != null) usernameField.setPromptText(GestoreLingua.getIstanza().get("login.prompt.username"));
        if (passwordField != null) passwordField.setPromptText(GestoreLingua.getIstanza().get("login.prompt.password"));
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        // Traduciamo anche il titolo del popup!
        String titoloErrore = GestoreLingua.getIstanza().get("login.alert.error_title");
        alert.setTitle(titoloErrore != null ? titoloErrore : "Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}