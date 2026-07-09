package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.LibreriaControl;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Utente;
import com.progetto.entita.Videogioco;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardLibreriaController {

    private static final Logger LOGGER = Logger.getLogger(CardLibreriaController.class.getName());

    @FXML private Label idLbl;
    @FXML private Label titoloLbl;
    @FXML private Label coverTesto;
    @FXML private VBox gameBox; // Contenitore dinamico per bottone o spunta

    private ElementoLibreria elemento;
    private Utente utenteCorrente;
    private LibreriaControl libreriaControl;
    private Runnable onAggiornamento; // Callback per ricaricare la Dashboard

    public void setDati(ElementoLibreria elemento, Utente utente, LibreriaControl control, Runnable onAggiornamento) {
        this.elemento = elemento;
        this.utenteCorrente = utente;
        this.libreriaControl = control;
        this.onAggiornamento = onAggiornamento;

        Videogioco gioco = elemento.getVideogioco();
        
        idLbl.setText("[ID: " + gioco.getId() + "]");
        titoloLbl.setText(gioco.getTitolo());
        coverTesto.setText(gioco.getTitolo().split(" ")[0]);

        costruisciAreaGamification(gioco);
    }

    private void costruisciAreaGamification(Videogioco gioco) {
        gameBox.getChildren().clear();

        if (elemento.isCompletato()) {
            Label lblCompletato = new Label("COMPLETATO ✔");
            lblCompletato.getStyleClass().add("testo-titolo-verde"); // Usa il CSS
            gameBox.getChildren().add(lblCompletato);
        } else {
            Button btnCompleta = new Button("COMPLETA (" + gioco.getExpFornita() + " XP)");
            btnCompleta.getStyleClass().add("btn-outline-ciano"); // Usa il CSS
            
            btnCompleta.setOnAction(e -> {
                boolean successo = libreriaControl.completaGioco(utenteCorrente, gioco);
                if (successo) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.setTitle("Level Up!");
                    alert.setContentText("Complimenti! Hai guadagnato " + gioco.getExpFornita() + " EXP!");
                    alert.showAndWait();
                    if (onAggiornamento != null) onAggiornamento.run(); 
                }
            });
            gameBox.getChildren().add(btnCompleta);
        }
    }

    @FXML
    private void lanciaGioco() {
        LOGGER.info("[SYSTEM RUNTIME] Lancio binario di: " + elemento.getVideogioco().getTitolo());
    }

    @FXML
    private void recensisciGioco() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("scrivi_recensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.setGioco(elemento.getVideogioco());
            idLbl.getScene().setRoot(root);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento recensione", ex);
        }
    }
}