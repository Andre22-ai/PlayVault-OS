package com.progetto.boundary;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.HallOfFameControl; 
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar; 
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class HallOfFameController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HallOfFameController.class.getName());

    @FXML private Label avatarLabel;
    @FXML private Label userNameLabel;
    
    @FXML private Label lblLivello;
    @FXML private ProgressBar barraEsperienza;
    @FXML private Label lblEsperienza;
    @FXML private Label lblGiochiCompletati;
    @FXML private Label lblGeneriPreferiti;
    
    @FXML private FlowPane leaderboardPane;

    private final UtenteDAO utenteDAO;
    private final HallOfFameControl hallControl; 

    public HallOfFameController() {
        this.utenteDAO = App.getUtenteDAO(); 
        this.hallControl = new HallOfFameControl(App.getLibreriaDAO());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utente corrente = Sessione.getIstanza().getUtenteCorrente();
        if (corrente != null) {
            userNameLabel.setText(corrente.getUsername().toUpperCase());
            avatarLabel.setText(corrente.getUsername().substring(0, 1).toUpperCase());
            
            lblLivello.setText("LEVEL " + corrente.getLivello());
            barraEsperienza.setProgress(corrente.getProgressoLivello());
            lblEsperienza.setText("Exp: " + (corrente.getEsperienza() % 100) + "/100");

            long completati = hallControl.calcolaGiochiCompletati(corrente);
            String genereTop = hallControl.calcolaGenerePreferito(corrente);

            lblGiochiCompletati.setText(String.valueOf(completati));
            lblGeneriPreferiti.setText(genereTop.toUpperCase());
        }

        caricaClassifica();
    }

    private void caricaClassifica() {
        leaderboardPane.getChildren().clear(); 
        
        List<Utente> topPlayers = utenteDAO.recuperaClassifica();
        int rank = 1;

        for (Utente u : topPlayers) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("card_classifica.fxml"));
                VBox rankCard = loader.load();
                
                CardClassificaController miniController = loader.getController();
                miniController.setDati(u, rank++);
                
                leaderboardPane.getChildren().add(rankCard);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore durante il caricamento della card classifica FXML", e);
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriImpostazioni() {
        try {
            App.setRoot("impostazioni");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore navigazione verso impostazioni", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void tornaAllaDashboard() {
        try {
            App.setRoot("dashboard");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore navigazione verso dashboard", e);
        }
    }
}