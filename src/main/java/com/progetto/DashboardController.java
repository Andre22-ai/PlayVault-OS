package com.progetto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.controllo.LibreriaControl;
import com.progetto.controllo.RecensioneControl;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Videogioco;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardController implements Initializable {

    // --- INIZIALIZZAZIONE DEL LOGGER ---
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    // --- LA NOSTRA COSTANTE PER IL FONT ---
    private static final String FONT_FAMILY = "Consolas";

    @FXML private FlowPane catalogoPane;
    @FXML private Label creditsLabel; 
    @FXML private Label playerLabel;
    @FXML private Button libraryButton;
    @FXML private Button catalogButton;
    @FXML private Button reviewsButton;
    @FXML private Button settingsButton;

    // FIX SonarCloud: Variabili rese 'final'
    private final LibreriaControl libreriaControl;
    private final RecensioneControl recensioneControl;

    public DashboardController() {
        // ORA PASSIMO ENTRAMBI I DAO CORRETTI!
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
        apriLibreria();
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
        List<Videogioco> mieiGiochi = libreriaControl.ottieniMieiGiochi(username);
        
        if (mieiGiochi.isEmpty()) {
            Label vuotoLbl = new Label(getTesto("dashboard.empty.library"));
            vuotoLbl.setTextFill(Color.web("#ff00ff"));
            vuotoLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
            vuotoLbl.setStyle("-fx-text-alignment: center;");
            catalogoPane.getChildren().add(vuotoLbl);
            return;
        }

        for (Videogioco gioco : mieiGiochi) {
            VBox card = creaCardGioco(gioco, false); 
            catalogoPane.getChildren().add(card);
        }
    }

    @FXML
    private void apriCatalogoAcquisti() {
        LOGGER.info("[BOUNDARY] Switch -> ADD GAME (Store / Global Catalogue)");
        catalogoPane.getChildren().clear();
        
        List<Videogioco> catalogoModello = libreriaControl.ottieniCatalogoCompleto();
        
        for (Videogioco gioco : catalogoModello) {
            VBox card = creaCardGioco(gioco, true); 
            catalogoPane.getChildren().add(card);
        }
    }

    @FXML
    private void apriMieRecensioni() {
        LOGGER.info("[BOUNDARY] Switch -> MY REVIEWS (Personal Logs)");
        catalogoPane.getChildren().clear();
        
        String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
        List<Recensione> mieRecensioni = recensioneControl.ottieniRecensioniPersonali(username);
        
        if (mieRecensioni.isEmpty()) {
            Label vuotoLbl = new Label(getTesto("dashboard.empty.reviews"));
            vuotoLbl.setTextFill(Color.web("#ffea00"));
            vuotoLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
            vuotoLbl.setStyle("-fx-text-alignment: center;");
            catalogoPane.getChildren().add(vuotoLbl);
            return;
        }

        for (Recensione r : mieRecensioni) {
            VBox card = new VBox();
            card.setPrefWidth(300.0);
            card.setSpacing(10.0);
            card.setPadding(new Insets(15.0));
            card.setStyle("-fx-background-color: #0d0012; -fx-border-color: #ffea00; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
            
            Label gameLbl = new Label(getTesto("dashboard.target") + " " + r.getNomeGioco());
            gameLbl.setTextFill(Color.web("#00ffff"));
            gameLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
            
            String stelle = "★".repeat(r.getVoto()) + "☆".repeat(5 - r.getVoto());
            Label ratingLbl = new Label(getTesto("dashboard.rating") + " " + stelle);
            ratingLbl.setTextFill(Color.web("#ffea00"));
            ratingLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 14));
            
            Label logLbl = new Label(getTesto("dashboard.log") + " " + r.getCommento());
            logLbl.setTextFill(Color.web("#39ff14"));
            logLbl.setFont(Font.font(FONT_FAMILY, 14));
            logLbl.setWrapText(true);
            
            HBox buttonBox = new HBox(15.0);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));

            Button editBtn = new Button(getTesto("dashboard.edit"));
            editBtn.setStyle("-fx-background-color: #000000; -fx-border-color: #00ffff; -fx-text-fill: #00ffff; -fx-border-radius: 5; -fx-cursor: hand;");
            editBtn.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("scrivi_recensione.fxml"));
                    Parent root = loader.load();
                    ScriviRecensioneController controller = loader.getController();
                    controller.setRecensioneDaModificare(r); 
                    editBtn.getScene().setRoot(root);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Errore caricamento scrivi_recensione.fxml", ex);
                }
            });

            Button delBtn = new Button(getTesto("dashboard.delete"));
            delBtn.setStyle("-fx-background-color: #000000; -fx-border-color: #ff0000; -fx-text-fill: #ff0000; -fx-border-radius: 5; -fx-cursor: hand;");
            delBtn.setOnAction(e -> {
                boolean eliminata = recensioneControl.eliminaRecensionePersonale(username, r.getIdGioco());
                if(eliminata) {
                    apriMieRecensioni(); 
                }
            });

            buttonBox.getChildren().addAll(editBtn, delBtn);
            card.getChildren().addAll(gameLbl, ratingLbl, logLbl, buttonBox);
            catalogoPane.getChildren().add(card);
        }
    }

    private VBox creaCardGioco(Videogioco gioco, boolean modalitaNegozio) {
        String neonColor = modalitaNegozio ? "#00ffff" : "#39ff14"; 

        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setPrefHeight(280.0);
        card.setPrefWidth(200.0);
        card.setSpacing(15.0);
        card.setPadding(new Insets(15.0, 0, 0, 0));
        card.setStyle("-fx-background-color: #000000; -fx-border-color: " + neonColor + "; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        DropShadow ombra = new DropShadow();
        ombra.setColor(Color.web(neonColor));
        ombra.setRadius(10.0);
        ombra.setSpread(0.1);
        card.setEffect(ombra);

        // Aggiungi Label con ID del gioco
        Label idLbl = new Label("[ID: " + gioco.getId() + "]");
        idLbl.setTextFill(Color.web("#888888"));
        idLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 10.0));
        VBox.setMargin(idLbl, new Insets(0, 0, -10.0, 0));

        Label titoloLbl = new Label(gioco.getTitolo());
        titoloLbl.setTextFill(Color.web(neonColor));
        titoloLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16.0));

        VBox coverBox = new VBox();
        coverBox.setAlignment(Pos.CENTER);
        coverBox.setPrefHeight(120.0);
        coverBox.setPrefWidth(160.0);
        coverBox.setStyle("-fx-background-color: " + (modalitaNegozio ? "#002b2b" : "#0a2b00") + "; -fx-border-color: " + neonColor + "; -fx-border-radius: 5;");
        VBox.setMargin(coverBox, new Insets(0, 10.0, 0, 10.0));
        
        String parolaCover = gioco.getTitolo().split(" ")[0];
        Label coverTesto = new Label(parolaCover);
        coverTesto.setTextFill(Color.web(neonColor));
        coverTesto.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        coverBox.getChildren().add(coverTesto);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        if (modalitaNegozio) {
            Button buyBtn = new Button(getTesto("dashboard.buy.info"));
            buyBtn.setMaxWidth(Double.MAX_VALUE);
            buyBtn.setPrefHeight(35.0);
            buyBtn.setStyle("-fx-background-color: #000000; -fx-border-color: #00ffff; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #00ffff; -fx-font-weight: bold; -fx-cursor: hand;");
            VBox.setMargin(buyBtn, new Insets(0, 20.0, 15.0, 20.0));
            
            buyBtn.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("dettagli_gioco.fxml"));
                    Parent root = loader.load();
                    DettagliGiocoController controller = loader.getController();
                    controller.setGioco(gioco);
                    buyBtn.getScene().setRoot(root);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Errore caricamento dettagli_gioco.fxml", ex);
                }
            });
            card.getChildren().addAll(idLbl, titoloLbl, coverBox, spacer, buyBtn);
            
        } else {
            HBox buttonBox = new HBox(10.0);
            buttonBox.setAlignment(Pos.CENTER);
            VBox.setMargin(buttonBox, new Insets(0, 10.0, 15.0, 10.0));

            Button launchBtn = new Button(getTesto("dashboard.launch"));
            launchBtn.setPrefHeight(35.0);
            launchBtn.setPrefWidth(90.0);
            launchBtn.setStyle("-fx-background-color: #000000; -fx-border-color: #39ff14; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #39ff14; -fx-font-weight: bold; -fx-cursor: hand;");
            launchBtn.setOnAction(e -> {
                // FIX SonarCloud: Logging parametrizzato invece della concatenazione
                LOGGER.log(Level.INFO, "[SYSTEM RUNTIME] Esecuzione binaria di: {0} avviata su PlayVault OS.", gioco.getTitolo());
            });

            Button reviewBtn = new Button(getTesto("dashboard.review"));
            reviewBtn.setPrefHeight(35.0);
            reviewBtn.setPrefWidth(80.0);
            reviewBtn.setStyle("-fx-background-color: #000000; -fx-border-color: #ffea00; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #ffea00; -fx-font-weight: bold; -fx-cursor: hand;");
            
            reviewBtn.setOnAction(e -> {
                try {
                    // FIX SonarCloud: Logging parametrizzato
                    LOGGER.log(Level.INFO, "[BOUNDARY] Apertura terminale di recensione per: {0}", gioco.getTitolo());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("scrivi_recensione.fxml"));
                    Parent root = loader.load();
                    
                    ScriviRecensioneController controller = loader.getController();
                    controller.setGioco(gioco);
                    
                    reviewBtn.getScene().setRoot(root);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Errore caricamento scrivi_recensione.fxml", ex);
                }
            });

            buttonBox.getChildren().addAll(launchBtn, reviewBtn);
            card.getChildren().addAll(idLbl, titoloLbl, coverBox, spacer, buttonBox);
        }

        return card;
    }

   @FXML
    private void apriImpostazioni() {
        // Usiamo il Logger ufficiale al posto del System.out
        LOGGER.info("[BOUNDARY] Richiesta apertura Impostazioni (Bottone cliccato con successo)."); 
        
        try {
            App.setRoot("impostazioni");
        } catch (IOException e) {
            // Il Logger.log gestisce automaticamente anche l'errore (rimpiazzando e.printStackTrace())
            LOGGER.log(Level.SEVERE, "ERRORE CRITICO: Non trovo il file impostazioni.fxml!", e);
        }
    }

    @FXML
    private void esciDalGioco() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void vaiAllaHallOfFame() throws IOException {
        App.setRoot("hall_of_fame");
    }
}