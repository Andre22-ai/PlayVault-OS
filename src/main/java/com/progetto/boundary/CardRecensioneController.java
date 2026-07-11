package com.progetto.boundary;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.RecensioneControl;
import com.progetto.entita.Recensione;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class CardRecensioneController {

    private static final Logger LOGGER = Logger.getLogger(CardRecensioneController.class.getName());

    @FXML private Label gameLbl;
    @FXML private Label ratingLbl;
    @FXML private Label logLbl;

    private Recensione recensione;
    private RecensioneControl recensioneControl;
    private String username;
    private Runnable onAggiornamento;

    public void setDati(Recensione recensione, RecensioneControl control, String username, Runnable onAggiornamento) {
        this.recensione = recensione;
        this.recensioneControl = control;
        this.username = username;
        this.onAggiornamento = onAggiornamento;

        // --- FIX LINGUA: Traduzione dei prefissi testuali ---
        // Nota: Assicurati di aggiungere queste 3 chiavi nei tuoi file messages_it.properties e messages_en.properties!
        String targetTxt = GestoreLingua.getIstanza().get("card.recensione.target");
        String ratingTxt = GestoreLingua.getIstanza().get("card.recensione.rating");
        String logTxt = GestoreLingua.getIstanza().get("card.recensione.log");

        gameLbl.setText(targetTxt + ": " + recensione.getNomeGioco());
        String stelle = "★".repeat(recensione.getVoto()) + "☆".repeat(5 - recensione.getVoto());
        ratingLbl.setText(ratingTxt + ": " + stelle);
        logLbl.setText(logTxt + ": " + recensione.getCommento());
    }

    @FXML
    @SuppressWarnings("unused")
    private void modificaRecensione() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("scrivi_recensione.fxml"));
            
            // --- FIX LINGUA: Iniezione del ResourceBundle ---
            Locale localeAttuale = GestoreLingua.getIstanza().getLocaleCorrente();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", localeAttuale);
            loader.setResources(bundle);
            // ------------------------------------------------
            
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.setRecensioneDaModificare(recensione); 
            gameLbl.getScene().setRoot(root);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento scrivi_recensione.fxml", ex);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eliminaRecensione() {
        boolean eliminata = recensioneControl.eliminaRecensionePersonale(username, recensione.getIdGioco());
        if (eliminata && onAggiornamento != null) {
            onAggiornamento.run(); // Ricarica la vista delle recensioni
        }
    }
}