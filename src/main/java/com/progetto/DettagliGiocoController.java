package com.progetto;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.controllo.AcquistoControl;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.GiocoGiaPossedutoException;
import com.progetto.exceptions.SaldoInsufficienteException;
import com.progetto.exceptions.SalvataggioFallitoException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private Videogioco giocoSelezionato; 
    
    // FIX SonarCloud: Variabile resa 'final'
    private final AcquistoControl acquistoControl;

    public DettagliGiocoController() {
        // FIX 2: Usiamo il polimorfismo! Chiediamo il database alla classe App
        this.acquistoControl = new AcquistoControl(App.getLibreriaDAO());
    }

    public void setGioco(Videogioco gioco) {
        this.giocoSelezionato = gioco;
        
        // Imposta i dati grafici di base
        titoloLabel.setText(gioco.getTitolo());
        coverLabel.setText(gioco.getTitolo().split(" ")[0]);
        idLabel.setText("[ID: " + gioco.getId() + "]");
        devAnnoLabel.setText("STUDIO: " + gioco.getSviluppatore() + " // RELEASE: " + gioco.getAnnoUscita());
        genereLabel.setText("CLASS: " + gioco.getGenere());
        
        // Stampiamo a video SOLO la descrizione originale del gioco!
        descArea.setText(gioco.getDescrizione());
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
            alert.setTitle("SYSTEM OVERRIDE");
            alert.setContentText("TRANSACTION COMPLETE! Gioco aggiunto alla tua libreria. (-15 CREDITS)");
            alert.showAndWait();
        } catch (GiocoGiaPossedutoException e) {
            mostraAlert("WARNING", e.getMessage());
        } catch (SaldoInsufficienteException e) {
            mostraAlert("ACCESS DENIED", e.getMessage());
        } catch (SalvataggioFallitoException e) {
            mostraAlert("ERROR", e.getMessage());
        }
    }

    @FXML
    private void tornaAllaDashboard() throws IOException {
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