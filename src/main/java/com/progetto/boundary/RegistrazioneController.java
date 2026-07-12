package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.RegistrazioneControl;
import com.progetto.exceptions.UtenteGiaEsistenteException;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrazioneController {

    private static final Logger LOGGER = Logger.getLogger(RegistrazioneController.class.getName());

    // Campi di testo
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmField;

    // Etichette e Bottoni per la traduzione dinamica
    @FXML private Label lblTitle;
    @FXML private Label lblSubtitle;
    @FXML private Label lblUsername;
    @FXML private Label lblPassword;
    @FXML private Label lblConfirm;
    @FXML private Button btnRegister;
    @FXML private Button btnBack;
    @FXML private Button btnSwitchLang;

    private final RegistrazioneControl regControl;

    public RegistrazioneController() {
        this.regControl = new RegistrazioneControl(App.getUtenteDAO());
    }

    @FXML
    public void initialize() {
        // Appena si apre la schermata, si adegua subito alla lingua impostata in memoria
        aggiornaTestiUI();
    }

    @FXML
    @SuppressWarnings("unused")
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

    @FXML
    @SuppressWarnings("unused")
    private void tornaAlLogin() throws IOException {
        LOGGER.info("[BOUNDARY] Registrazione annullata. Ritorno al Login.");
        App.setRoot("login");
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
        // Aggiorna Bottoni
        if (btnSwitchLang != null) btnSwitchLang.setText(GestoreLingua.getIstanza().get("reg.btn.switch"));
        if (btnRegister != null) btnRegister.setText(GestoreLingua.getIstanza().get("reg.btn.register"));
        if (btnBack != null) btnBack.setText(GestoreLingua.getIstanza().get("reg.btn.back"));
        
        // Aggiorna Etichette
        if (lblTitle != null) lblTitle.setText(GestoreLingua.getIstanza().get("reg.lbl.title"));
        if (lblSubtitle != null) lblSubtitle.setText(GestoreLingua.getIstanza().get("reg.lbl.subtitle"));
        if (lblUsername != null) lblUsername.setText(GestoreLingua.getIstanza().get("reg.lbl.username"));
        if (lblPassword != null) lblPassword.setText(GestoreLingua.getIstanza().get("reg.lbl.password"));
        if (lblConfirm != null) lblConfirm.setText(GestoreLingua.getIstanza().get("reg.lbl.confirm"));
        
        // Aggiorna Suggerimenti (Placeholder) nei campi
        if (regUsernameField != null) regUsernameField.setPromptText(GestoreLingua.getIstanza().get("reg.prompt.username"));
        if (regPasswordField != null) regPasswordField.setPromptText(GestoreLingua.getIstanza().get("reg.prompt.password"));
        if (regConfirmField != null) regConfirmField.setPromptText(GestoreLingua.getIstanza().get("reg.prompt.confirm"));
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        String titoloErrore = GestoreLingua.getIstanza().get("reg.alert.error_title");
        alert.setTitle(titoloErrore != null ? titoloErrore : "Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}