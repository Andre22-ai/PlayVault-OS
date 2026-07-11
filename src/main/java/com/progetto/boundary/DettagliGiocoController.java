package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.AcquistoControl;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.GiocoGiaPossedutoException;
import com.progetto.exceptions.SaldoInsufficienteException;
import com.progetto.exceptions.SalvataggioFallitoException;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class DettagliGiocoController {

    private static final Logger LOGGER = Logger.getLogger(DettagliGiocoController.class.getName());

    // --- MEMORIA GLOBALE PER IL GIOCO DA MOSTRARE ---
    public static Videogioco giocoInMemoria = null;
    // ------------------------------------------------

    @FXML private Label titoloLabel;
    @FXML private Label devAnnoLabel;
    @FXML private Label genereLabel;
    @FXML private TextArea descArea;
    @FXML private Label coverLabel;
    @FXML private Label idLabel;
    @FXML private Button backButton;
    @FXML private Button settingsButton;
    @FXML private Button purchaseButton;
    @FXML private Label loreLabel;

    private Videogioco giocoSelezionato; 
    
    private final AcquistoControl acquistoControl;

    public DettagliGiocoController() {
        this.acquistoControl = new AcquistoControl(App.getLibreriaDAO());
    }

    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        // Se c'è un gioco in memoria (perché siamo tornati indietro dalle impostazioni),
        // lo ripristina automaticamente a schermo!
        if (giocoInMemoria != null) {
            setGioco(giocoInMemoria);
        }
        aggiornaTesti();
    }

    public void setGioco(Videogioco gioco) {
        this.giocoSelezionato = gioco;
        giocoInMemoria = gioco; 
        
        titoloLabel.setText(gioco.getTitolo());
        idLabel.setText("[ID: " + gioco.getId() + "]");
        devAnnoLabel.setText("STUDIO: " + gioco.getSviluppatore() + " // RELEASE: " + gioco.getAnnoUscita());
        genereLabel.setText("CLASS: " + gioco.getGenere());
        descArea.setText(gioco.getDescrizioneLocale());
        
        // --- NUOVA LOGICA BADGE MULTIPLI ---
        coverLabel.setText("");
        coverLabel.getStyleClass().clear();
        coverLabel.setStyle("-fx-background-color: transparent; -fx-alignment: center;");
        
        javafx.scene.layout.HBox contenitoreIcone = new javafx.scene.layout.HBox(15);
        contenitoreIcone.setAlignment(javafx.geometry.Pos.CENTER);
        
        String g = gioco.getGenere() != null ? gioco.getGenere().toUpperCase() : "";
        boolean genereTrovato = false;
        
        if (g.contains("ACTION") || g.contains("AZIONE")) { contenitoreIcone.getChildren().add(creaMiniIcona("⚔️", "cover-action")); genereTrovato = true; }
        if (g.contains("RPG") || g.contains("GDR") || g.contains("FANTASY")) { contenitoreIcone.getChildren().add(creaMiniIcona("🔮", "cover-rpg")); genereTrovato = true; }
        if (g.contains("SPORT")) { contenitoreIcone.getChildren().add(creaMiniIcona("⚽", "cover-sports")); genereTrovato = true; }
        if (g.contains("SHOOTER") || g.contains("SPARATUTTO")) { contenitoreIcone.getChildren().add(creaMiniIcona("🎯", "cover-shooter")); genereTrovato = true; }
        if (g.contains("RACING") || g.contains("CORSE")) { contenitoreIcone.getChildren().add(creaMiniIcona("🏎️", "cover-racing")); genereTrovato = true; }
        if (g.contains("STRATEGY") || g.contains("STRATEGIA")) { contenitoreIcone.getChildren().add(creaMiniIcona("♟️", "cover-strategy")); genereTrovato = true; }
        if (g.contains("HORROR")) { contenitoreIcone.getChildren().add(creaMiniIcona("💀", "cover-horror")); genereTrovato = true; }
        
        if (!genereTrovato) {
            contenitoreIcone.getChildren().add(creaMiniIcona("🎮", "cover-default"));
        }
        
        coverLabel.setGraphic(contenitoreIcone);
        // -----------------------------------
        
        aggiornaTesti();
    }

    // Aggiungi questo metodo in fondo alla classe
    private Label creaMiniIcona(String icona, String classeCss) {
        Label miniLabel = new Label(icona);
        miniLabel.getStyleClass().addAll("cover-base", classeCss);
        miniLabel.setPrefSize(70, 70);
        miniLabel.setMinSize(70, 70);
        miniLabel.setMaxSize(70, 70);
        miniLabel.setStyle("-fx-font-size: 28px; -fx-padding: 0;"); 
        miniLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        miniLabel.setAlignment(javafx.geometry.Pos.CENTER);
        return miniLabel;
    }

    private void aggiornaTesti() {
        GestoreLingua lingua = GestoreLingua.getIstanza();
        if (backButton != null) backButton.setText(lingua.get("menu.settings.back").toUpperCase());
        if (settingsButton != null) settingsButton.setText(lingua.get("menu.settings").toUpperCase());
        if (purchaseButton != null) purchaseButton.setText(lingua.get("details.purchase").toUpperCase());
        if (loreLabel != null) loreLabel.setText(lingua.get("details.lore").toUpperCase());
        if (descArea != null && giocoSelezionato != null) {
            descArea.setText(giocoSelezionato.getDescrizioneLocale());
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiAcquisto() {
        LOGGER.log(Level.INFO, "[BOUNDARY] Richiesta acquisto per: {0}", giocoSelezionato.getTitolo());
        
        try {
            acquistoControl.tentaAcquisto(giocoSelezionato);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle(getTesto("details.alert.success"));
            alert.setContentText(getTesto("details.purchase.success"));
            alert.showAndWait();
        } catch (GiocoGiaPossedutoException e) {
            mostraAlert(getTesto("details.alert.warning"), e.getMessage());
        } catch (SaldoInsufficienteException e) {
            mostraAlert(getTesto("details.alert.access"), e.getMessage());
        } catch (SalvataggioFallitoException e) {
            mostraAlert(getTesto("details.alert.error"), e.getMessage());
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriImpostazioni() {
        LOGGER.info("[BOUNDARY] Richiesta apertura Impostazioni da Dettagli Gioco...");
        try {
            // App sa che veniamo dai "dettagli_gioco" grazie a App.java
            App.setRoot("impostazioni");
        } catch (IOException e) {
             LOGGER.log(Level.SEVERE, "ERRORE CRITICO: Impossibile trovare il file impostazioni.fxml!", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void tornaAllaDashboard() throws IOException {
        // Svuotiamo la memoria quando chiudiamo la card per evitare "fantasmi"
        giocoInMemoria = null; 
        
        // Torna alla Dashboard. La Dashboard leggerà il "tabAttivo" = 1 
        // e ricaricherà automaticamente la tab "Aggiungi Gioco"!
        App.setRoot("dashboard");
    }

    private String getTesto(String chiave) {
        return GestoreLingua.getIstanza().get(chiave);
    }

    private void mostraAlert(String titolo, String contenuto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(titolo);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}