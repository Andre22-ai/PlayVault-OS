package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.entita.Videogioco;
import com.progetto.utils.BadgeUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CardNegozioController {

    private static final Logger LOGGER = Logger.getLogger(CardNegozioController.class.getName());

    @FXML private Label idLbl;
    @FXML private Label titoloLbl;
    @FXML private Label coverTesto;

    private Videogioco gioco;

    public void setDati(Videogioco gioco) {
        this.gioco = gioco;
        idLbl.setText("[ID: " + gioco.getId() + "]");
        titoloLbl.setText(gioco.getTitolo());
        
        // --- LOGICA BADGE (Gestita da BadgeUtils) ---
        coverTesto.setText("");
        coverTesto.getStyleClass().clear();
        coverTesto.setStyle("-fx-background-color: transparent; -fx-alignment: center;");
        coverTesto.setGraphic(BadgeUtils.generaBadgeGeneri(gioco.getGenere()));
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriDettagliAcquisto() {
        try {
            // 1. Salviamo il gioco nella memoria globale del dettaglio
            DettagliGiocoController.giocoInMemoria = this.gioco;
            
            // 2. Usiamo App.setRoot per aggiornare la cronologia di navigazione
            App.setRoot("dettagli_gioco");
            
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento dettagli_gioco.fxml", ex);
        }
    }
}