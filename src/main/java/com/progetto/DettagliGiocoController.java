package com.progetto;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.controllo.AcquistoControl;
import com.progetto.database.LibreriaDAOMySQL;
import com.progetto.entita.Videogioco;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class DettagliGiocoController {

    // --- NUOVO: Inizializzazione del Logger ---
    private static final Logger LOGGER = Logger.getLogger(DettagliGiocoController.class.getName());

    @FXML private Label titoloLabel;
    @FXML private Label devAnnoLabel;
    @FXML private Label genereLabel;
    @FXML private TextArea descArea;
    @FXML private Label coverLabel;
    @FXML private Label idLabel; // NUOVO: Label per l'ID del gioco

    private Videogioco giocoSelezionato; // Memorizziamo il gioco per poterlo comprare
    private AcquistoControl acquistoControl;

    public DettagliGiocoController() {
        this.acquistoControl = new AcquistoControl(new LibreriaDAOMySQL());
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
        // FIX S106: Utilizzo del Logger al posto di System.out
        LOGGER.info("[BOUNDARY] Richiesta acquisto per: " + giocoSelezionato.getTitolo());
        
        String risultato = acquistoControl.tentaAcquisto(giocoSelezionato);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.setHeaderText(null);
        
        switch (risultato) {
            case "SUCCESS":
                alert.setTitle("SYSTEM OVERRIDE");
                alert.setContentText("TRANSACTION COMPLETE! Gioco aggiunto alla tua libreria. (-15 CREDITS)");
                break;
            case "ALREADY_OWNED":
                alert.setTitle("WARNING");
                alert.setContentText("Possiedi già questo titolo nel tuo Vault.");
                break;
            case "INSUFFICIENT_FUNDS":
                alert.setTitle("ACCESS DENIED");
                alert.setContentText("Crediti insufficienti. Ricarica il tuo conto per procedere.");
                break;
            default:
                alert.setTitle("ERROR");
                alert.setContentText("Errore critico di sistema. Riprovare.");
                break;
        }
        alert.showAndWait();
    }

    @FXML
    private void tornaAllaDashboard() throws IOException {
        App.setRoot("dashboard");
    }
}