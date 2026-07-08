package com.progetto.boundary;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.progetto.App;
import com.progetto.controllo.HallOfFameControl; 
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar; 
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HallOfFameController implements Initializable {

    @FXML private Label avatarLabel;
    @FXML private Label userNameLabel;
    
    @FXML private Label lblLivello;
    @FXML private ProgressBar barraEsperienza;
    @FXML private Label lblEsperienza;
    @FXML private Label lblGiochiCompletati;
    @FXML private Label lblGeneriPreferiti;
    
    @FXML private FlowPane leaderboardPane;
    @FXML private Button settingsButton;

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

        if (settingsButton != null) {
            settingsButton.setOnAction(event -> {
                try {
                    App.setRoot("impostazioni");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        caricaClassifica();
    }

    private void caricaClassifica() {
        leaderboardPane.getChildren().clear(); 
        
        List<Utente> topPlayers = utenteDAO.recuperaClassifica();
        int rank = 1;

        for (Utente u : topPlayers) {
            VBox rankCard = creaCardClassifica(u, rank++);
            leaderboardPane.getChildren().add(rankCard);
        }
    }

    private VBox creaCardClassifica(Utente u, int rank) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setPrefHeight(140.0);
        card.setPrefWidth(140.0);
        card.setSpacing(5.0);
        
        String[] badge = switch (rank) {
            case 1 -> new String[]{"#ffea00", "🥇"};
            case 2 -> new String[]{"#c0c0c0", "🥈"};
            case 3 -> new String[]{"#cd7f32", "🥉"};
            default -> new String[]{"#00ffff", "👤"};
        };
        String color = badge[0];
        String icon = badge[1];

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
    private void apriImpostazioni() throws IOException {
        App.setRoot("impostazioni");
    }

    @FXML
    private void tornaAllaDashboard() throws IOException {
        App.setRoot("dashboard");
    }
}