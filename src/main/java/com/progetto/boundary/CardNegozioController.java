package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.entita.Videogioco;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        coverTesto.setText(gioco.getTitolo().split(" ")[0]);
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriDettagliAcquisto() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("dettagli_gioco.fxml"));
            Parent root = loader.load();
            DettagliGiocoController controller = loader.getController();
            controller.setGioco(gioco);
            idLbl.getScene().setRoot(root);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento dettagli_gioco.fxml", ex);
        }
    }
}