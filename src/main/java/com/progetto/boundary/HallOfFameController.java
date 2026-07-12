package com.progetto.boundary;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.HallOfFameControl; 
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.utils.GestoreLingua;

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
            
            // --- FIX LINGUA: Traduzione dinamica di Livello ed Esperienza ---
            String testoLivello = GestoreLingua.getIstanza().get("profilo.livello");
            String testoExp = GestoreLingua.getIstanza().get("profilo.esperienza");
            
            lblLivello.setText(testoLivello + " " + corrente.getLivello());
            barraEsperienza.setProgress(corrente.getProgressoLivello());
            lblEsperienza.setText(testoExp + ": " + (corrente.getEsperienza() % 100) + "/100");

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

        // --- FIX LINGUA: Pre-carichiamo il bundle per le mini-card ---
        Locale localeAttuale = GestoreLingua.getIstanza().getLocaleCorrente();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", localeAttuale);

        for (Utente u : topPlayers) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("card_classifica.fxml"));
                
                // --- FIX LINGUA: Iniettiamo il bundle nel loader ---
                loader.setResources(bundle);
                // ---------------------------------------------------
                
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
    private void apriImpostazioni() {
        try {
            // SBAGLIATO: Questo cancella la memoria e ti farà tornare sempre alla Dashboard!
            // App.setRoot("impostazioni"); 
            
            // CORRETTO: Salva la schermata attuale (HOF o Card) e poi apre le impostazioni
            App.cambiaSchermata("impostazioni"); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void tornaAllaDashboard() { // Il nome rimane uguale per l'FXML
        try {
            // Sostituiamo App.setRoot("dashboard") con la nostra nuova memoria:
            App.tornaIndietro(); 
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore navigazione indietro", e);
        }
    }
}