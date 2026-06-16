package com.progetto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.progetto.database.UtenteDAOMySQL;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HallOfFameController implements Initializable {

    @FXML private Label avatarLabel;
    @FXML private Label userNameLabel;
    @FXML private Label levelLabel;
    @FXML private FlowPane leaderboardPane;

    private UtenteDAOMySQL utenteDAO;

    public HallOfFameController() {
        this.utenteDAO = new UtenteDAOMySQL();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Carichiamo i dati del Profilo (Colonna Sinistra)
        Utente corrente = Sessione.getIstanza().getUtenteCorrente();
        if (corrente != null) {
            userNameLabel.setText(corrente.getUsername().toUpperCase());
            avatarLabel.setText(corrente.getUsername().substring(0, 1).toUpperCase());
            levelLabel.setText("LEVEL " + (corrente.getCrediti() / 2)); // Simuliamo un livello basato sui crediti
        }

        // 2. Carichiamo la Classifica Globale (Area Destra)
        caricaClassifica();
    }

    private void caricaClassifica() {
        leaderboardPane.getChildren().clear(); // Puliamo i trofei finti dell'FXML
        
        List<Utente> topPlayers = utenteDAO.recuperaClassifica();
        int rank = 1;

        for (Utente u : topPlayers) {
            VBox rankCard = creaCardClassifica(u, rank++);
            leaderboardPane.getChildren().add(rankCard);
        }
    }

    private VBox creaCardClassifica(Utente u, int rank) {
        // Creiamo una card cyberpunk per ogni giocatore in classifica
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setPrefHeight(140.0);
        card.setPrefWidth(140.0);
        card.setSpacing(5.0);
        
        // Colore dinamico: Oro per il primo, Argento per il secondo, Bronzo per il terzo, Cyan per gli altri
        String color;
        String icon;
        switch(rank) {
            case 1: color = "#ffea00"; icon = "🥇"; break;
            case 2: color = "#c0c0c0"; icon = "🥈"; break;
            case 3: color = "#cd7f32"; icon = "🥉"; break;
            default: color = "#00ffff"; icon = "👤"; break;
        }

        card.setStyle("-fx-background-color: #0d0012; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(32));
        
        Label nameLbl = new Label(u.getUsername().toUpperCase());
        nameLbl.setTextFill(Color.web(color));
        nameLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 14));

        Label creditsLbl = new Label(u.getCrediti() + " CR");
        creditsLbl.setTextFill(Color.WHITE);
        creditsLbl.setFont(Font.font("Consolas", 12));

        card.getChildren().addAll(iconLbl, nameLbl, creditsLbl);
        
        DropShadow ds = new DropShadow(10, Color.web(color));
        card.setEffect(ds);
        
        return card;
    }

    @FXML
    private void tornaAllaDashboard() throws IOException {
        App.setRoot("dashboard");
    }
}