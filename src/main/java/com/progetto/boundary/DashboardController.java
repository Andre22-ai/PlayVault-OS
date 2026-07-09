package com.progetto.boundary;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.LibreriaControl;
import com.progetto.controllo.RecensioneControl;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Videogioco;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DashboardController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    @FXML private FlowPane catalogoPane;
    @FXML private Label creditsLabel; 
    @FXML private Label playerLabel;
    @FXML private Button libraryButton;
    @FXML private Button catalogButton;
    @FXML private Button reviewsButton;
    @FXML private Button settingsButton;

    private final LibreriaControl libreriaControl;
    private final RecensioneControl recensioneControl;

    public DashboardController() {
        this.libreriaControl = new LibreriaControl(App.getVideogiocoDAO(), App.getLibreriaDAO());
        this.recensioneControl = new RecensioneControl(App.getRecensioneDAO(), App.getUtenteDAO()); 
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("[BOUNDARY] Sincronizzazione Sessione Utente...");
        
        if (Sessione.getIstanza().getUtenteCorrente() != null) {
            playerLabel.setText(Sessione.getIstanza().getUtenteCorrente().getUsername().toUpperCase());
            creditsLabel.setText(getTesto("dashboard.credits") + ": " + Sessione.getIstanza().getUtenteCorrente().getCrediti());
        }
        aggiornaTesti();
        apriLibreria(); // Apre la libreria di default
    }

    private void aggiornaTesti() {
        GestoreLingua lingua = GestoreLingua.getIstanza();
        if (libraryButton != null) libraryButton.setText(lingua.get("dashboard.library").toUpperCase());
        if (catalogButton != null) catalogButton.setText(lingua.get("dashboard.catalog").toUpperCase());
        if (reviewsButton != null) reviewsButton.setText(lingua.get("dashboard.reviews").toUpperCase());
        if (settingsButton != null) settingsButton.setText(lingua.get("dashboard.settings").toUpperCase());
        if (creditsLabel != null && Sessione.getIstanza().getUtenteCorrente() != null) {
            creditsLabel.setText(lingua.get("dashboard.credits") + ": " + Sessione.getIstanza().getUtenteCorrente().getCrediti());
        }
    }

    private String getTesto(String chiave) {
        return GestoreLingua.getIstanza().get(chiave);
    }

    @FXML
    private void apriLibreria() {
        LOGGER.info("[BOUNDARY] Switch -> MY LIBRARY (Owned Games)");
        catalogoPane.getChildren().clear();
        
        String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
        List<ElementoLibreria> miaLibreria = libreriaControl.ottieniLibreriaCompleta(username);
        
        if (miaLibreria.isEmpty()) {
            Label vuotoLbl = new Label(getTesto("dashboard.empty.library"));
            // Questo stile inline minimale va bene per i messaggi vuoti
            vuotoLbl.setStyle("-fx-text-fill: #ff00ff; -fx-font-family: Consolas; -fx-font-weight: bold; -fx-font-size: 16px;");
            catalogoPane.getChildren().add(vuotoLbl);
            return;
        }

        for (ElementoLibreria elemento : miaLibreria) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("card_libreria.fxml"));
                VBox card = loader.load();
                
                CardLibreriaController miniController = loader.getController();
                miniController.setDati(elemento, Sessione.getIstanza().getUtenteCorrente(), libreriaControl, this::apriLibreria);
                
                catalogoPane.getChildren().add(card);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore caricamento card_libreria.fxml", e);
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriCatalogoAcquisti() {
        LOGGER.info("[BOUNDARY] Switch -> ADD GAME (Store / Global Catalogue)");
        catalogoPane.getChildren().clear();
        
        List<Videogioco> catalogoModello = libreriaControl.ottieniCatalogoCompleto();
        
        for (Videogioco gioco : catalogoModello) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("card_negozio.fxml"));
                VBox card = loader.load();
                
                CardNegozioController miniController = loader.getController();
                miniController.setDati(gioco);
                
                catalogoPane.getChildren().add(card);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore caricamento card_negozio.fxml", e);
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriMieRecensioni() {
        LOGGER.info("[BOUNDARY] Switch -> MY REVIEWS (Personal Logs)");
        catalogoPane.getChildren().clear();
        
        String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
        List<Recensione> mieRecensioni = recensioneControl.ottieniRecensioniPersonali(username);
        
        if (mieRecensioni.isEmpty()) {
            Label vuotoLbl = new Label(getTesto("dashboard.empty.reviews"));
            vuotoLbl.setStyle("-fx-text-fill: #ffea00; -fx-font-family: Consolas; -fx-font-weight: bold; -fx-font-size: 16px;");
            catalogoPane.getChildren().add(vuotoLbl);
            return;
        }

        for (Recensione r : mieRecensioni) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("card_recensione.fxml"));
                VBox card = loader.load();
                
                CardRecensioneController miniController = loader.getController();
                miniController.setDati(r, recensioneControl, username, this::apriMieRecensioni);
                
                catalogoPane.getChildren().add(card);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore caricamento card_recensione.fxml", e);
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriImpostazioni() {
        LOGGER.info("[BOUNDARY] Richiesta apertura Impostazioni (Bottone cliccato con successo)."); 
        try {
            App.setRoot("impostazioni");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "ERRORE CRITICO: Non trovo il file impostazioni.fxml!", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void esciDalGioco() throws IOException {
        App.setRoot("login");
    }

    @FXML
    @SuppressWarnings("unused")
    private void vaiAllaHallOfFame() throws IOException {
        App.setRoot("hall_of_fame");
    }
}