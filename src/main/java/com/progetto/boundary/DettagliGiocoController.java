package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.AcquistoControl;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.GiocoGiaPossedutoException;
import com.progetto.exceptions.SaldoInsufficienteException;
import com.progetto.exceptions.SalvataggioFallitoException;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class DettagliGiocoController {

    private static final Logger LOGGER = Logger.getLogger(DettagliGiocoController.class.getName());

    @FXML private Label titoloLabel;
    @FXML private Label devAnnoLabel;
    @FXML private Label genereLabel;
    @FXML private TextArea descArea;
    @FXML private Label coverLabel;
    @FXML private Label idLabel;
    @FXML private Button backButton;
    @FXML private Button settingsButton;
    @FXML private Button purchaseButton;
    @FXML private Label loreLabel;

    private Videogioco giocoSelezionato; 
    
    // FIX SonarCloud: Variabile resa 'final'
    private final AcquistoControl acquistoControl;

    public DettagliGiocoController() {
        // FIX 2: Usiamo il polimorfismo! Chiediamo il database alla classe App
        this.acquistoControl = new AcquistoControl(App.getLibreriaDAO());
    }

    @FXML
    private void initialize() {
        // RIMOSSO IL BLOCCO settingsButton.setOnAction(...) 
        // Lasciamo che sia il file FXML a chiamare apriImpostazioni() tramite onAction
        aggiornaTesti();
    }

    public void setGioco(Videogioco gioco) {
        this.giocoSelezionato = gioco;
        
        // Imposta i dati grafici di base
        titoloLabel.setText(gioco.getTitolo());
        coverLabel.setText(gioco.getTitolo().split(" ")[0]);
        idLabel.setText("[ID: " + gioco.getId() + "]");
        devAnnoLabel.setText("STUDIO: " + gioco.getSviluppatore() + " // RELEASE: " + gioco.getAnnoUscita());
        genereLabel.setText("CLASS: " + gioco.getGenere());
        
        descArea.setText(gioco.getDescrizioneLocale());
        aggiornaTesti();
    }

    private void aggiornaTesti() {
        GestoreLingua lingua = GestoreLingua.getIstanza();
        if (backButton != null) backButton.setText(lingua.get("menu.settings.back").toUpperCase());
        if (settingsButton != null) settingsButton.setText(lingua.get("menu.settings").toUpperCase());
        if (purchaseButton != null) purchaseButton.setText(lingua.get("details.purchase").toUpperCase());
        if (loreLabel != null) loreLabel.setText(lingua.get("details.lore").toUpperCase());
        if (descArea != null && giocoSelezionato != null) {
            descArea.setText(giocoSelezionato.getDescrizioneLocale());
        }
    }

    /**
     * Metodo agganciato al pulsante INITIALIZE GAME ENGINE
     */
    @FXML
    private void eseguiAcquisto() {
        // FIX SonarCloud: Utilizzo del Logger parametrizzato invece della concatenazione col '+'
        LOGGER.log(Level.INFO, "[BOUNDARY] Richiesta acquisto per: {0}", giocoSelezionato.getTitolo());
        
        try {
            acquistoControl.tentaAcquisto(giocoSelezionato);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle(getTesto("details.alert.success"));
            alert.setContentText(getTesto("details.purchase.success"));
            alert.showAndWait();
        } catch (GiocoGiaPossedutoException e) {
            mostraAlert(getTesto("details.alert.warning"), e.getMessage());
        } catch (SaldoInsufficienteException e) {
            mostraAlert(getTesto("details.alert.access"), e.getMessage());
        } catch (SalvataggioFallitoException e) {
            mostraAlert(getTesto("details.alert.error"), e.getMessage());
        }
    }

    @FXML
    private void apriImpostazioni() {
        LOGGER.info("[BOUNDARY] Richiesta apertura Impostazioni da Dettagli Gioco...");
        try {
            App.setRoot("impostazioni");
        } catch (IOException e) {
             LOGGER.log(Level.SEVERE, "ERRORE CRITICO: Impossibile trovare il file impostazioni.fxml!", e);
        }
    }

    @FXML
    private void tornaAllaDashboard() throws IOException {
        App.setRoot("dashboard");
    }

    private String getTesto(String chiave) {
        return GestoreLingua.getIstanza().get(chiave);
    }

    private void mostraAlert(String titolo, String contenuto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(titolo);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}