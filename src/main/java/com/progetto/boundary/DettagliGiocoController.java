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
import com.progetto.utils.BadgeUtils;
import com.progetto.utils.GestoreLingua;
import com.progetto.utils.UIUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class DettagliGiocoController {

    private static final Logger LOGGER = Logger.getLogger(DettagliGiocoController.class.getName());

    // --- MEMORIA GLOBALE PER IL GIOCO DA MOSTRARE ---
    public static Videogioco giocoInMemoria = null;

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
    
    private final AcquistoControl acquistoControl;

    public DettagliGiocoController() {
        this.acquistoControl = new AcquistoControl(App.getLibreriaDAO());
    }

    // --- FIX S2696: Metodo statico per modificare il campo statico in sicurezza ---
    public static void impostaGiocoInMemoria(Videogioco gioco) {
        giocoInMemoria = gioco;
    }

    @FXML
    private void initialize() {
        if (giocoInMemoria != null) {
            setGioco(giocoInMemoria);
        }
        aggiornaTesti();
    }

    public void setGioco(Videogioco gioco) {
        this.giocoSelezionato = gioco;
        
        // Uso il metodo statico invece di assegnare direttamente la variabile!
        impostaGiocoInMemoria(gioco); 
        
        titoloLabel.setText(gioco.getTitolo());
        idLabel.setText("[ID: " + gioco.getId() + "]");
        devAnnoLabel.setText("STUDIO: " + gioco.getSviluppatore() + " // RELEASE: " + gioco.getAnnoUscita());
        genereLabel.setText("CLASS: " + gioco.getGenere());
        descArea.setText(gioco.getDescrizioneLocale());
        
        coverLabel.setText("");
        coverLabel.getStyleClass().clear();
        coverLabel.setStyle("-fx-background-color: transparent; -fx-alignment: center;");
        coverLabel.setGraphic(BadgeUtils.generaBadgeGeneri(gioco.getGenere()));
        
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

    @FXML
    @SuppressWarnings("unused")
    private void eseguiAcquisto() {
        LOGGER.log(Level.INFO, "[BOUNDARY] Richiesta acquisto per: {0}", giocoSelezionato.getTitolo());
        
        try {
            acquistoControl.tentaAcquisto(giocoSelezionato);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setHeaderText(null);
            // Utilizzo UIUtils per recuperare il testo della lingua
            alert.setTitle(UIUtils.getTesto("details.alert.success"));
            alert.setContentText(UIUtils.getTesto("details.purchase.success"));
            alert.showAndWait();
        } catch (GiocoGiaPossedutoException e) {
            mostraAlert(UIUtils.getTesto("details.alert.warning"), e.getMessage());
        } catch (SaldoInsufficienteException e) {
            mostraAlert(UIUtils.getTesto("details.alert.access"), e.getMessage());
        } catch (SalvataggioFallitoException e) {
            mostraAlert(UIUtils.getTesto("details.alert.error"), e.getMessage());
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriImpostazioni() {
        // Ridotto a una singola riga grazie alla classe UIUtils
        UIUtils.navigaAImpostazioni();
    }

    @FXML
    @SuppressWarnings("unused")
    private void tornaAllaDashboard() throws IOException {
        // Uso il metodo statico per svuotare la memoria in modo sicuro
        impostaGiocoInMemoria(null); 
        App.setRoot("dashboard");
    }

    private void mostraAlert(String titolo, String contenuto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(titolo);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}