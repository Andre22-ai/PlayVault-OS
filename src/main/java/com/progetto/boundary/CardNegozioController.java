package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.entita.Videogioco;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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
        
        // 1. Resetta la vecchia Label "coverTesto" per farla diventare un contenitore invisibile
        coverTesto.setText("");
        coverTesto.getStyleClass().clear();
        coverTesto.setStyle("-fx-background-color: transparent; -fx-alignment: center;");
        
        // 2. Crea una scatola orizzontale (HBox) per contenere i nostri mini-riquadri
        HBox contenitoreIcone = new HBox(15); // 15 pixel di spazio tra un riquadro e l'altro
        contenitoreIcone.setAlignment(Pos.CENTER);
        
        String g = gioco.getGenere() != null ? gioco.getGenere().toUpperCase() : "";
        boolean genereTrovato = false;
        
        // 3. Aggiunge un mini-riquadro per ogni genere trovato!
        if (g.contains("ACTION") || g.contains("AZIONE")) { contenitoreIcone.getChildren().add(creaMiniIcona("⚔️", "cover-action")); genereTrovato = true; }
        if (g.contains("RPG") || g.contains("GDR") || g.contains("FANTASY")) { contenitoreIcone.getChildren().add(creaMiniIcona("🔮", "cover-rpg")); genereTrovato = true; }
        if (g.contains("SPORT")) { contenitoreIcone.getChildren().add(creaMiniIcona("⚽", "cover-sports")); genereTrovato = true; }
        if (g.contains("SHOOTER") || g.contains("SPARATUTTO")) { contenitoreIcone.getChildren().add(creaMiniIcona("🎯", "cover-shooter")); genereTrovato = true; }
        if (g.contains("RACING") || g.contains("CORSE")) { contenitoreIcone.getChildren().add(creaMiniIcona("🏎️", "cover-racing")); genereTrovato = true; }
        if (g.contains("STRATEGY") || g.contains("STRATEGIA")) { contenitoreIcone.getChildren().add(creaMiniIcona("♟️", "cover-strategy")); genereTrovato = true; }
        if (g.contains("HORROR")) { contenitoreIcone.getChildren().add(creaMiniIcona("💀", "cover-horror")); genereTrovato = true; }
        
        // Se non rientra in nessuna categoria, mettiamo il controller base
        if (!genereTrovato) {
            contenitoreIcone.getChildren().add(creaMiniIcona("🎮", "cover-default"));
        }
        
        // 4. Inserisce tutto il blocco di quadratini dentro la tua label principale dell'FXML
        coverTesto.setGraphic(contenitoreIcone);
    }

    // Metodo "Fabbrica" per generare i quadratini perfetti
    // Metodo "Fabbrica" per generare i quadratini perfetti
    // Metodo "Fabbrica" per generare i quadratini perfetti e IDENTICI
    // Metodo "Fabbrica" per generare i quadratini perfetti e IDENTICI
    private Label creaMiniIcona(String icona, String classeCss) {
        Label miniLabel = new Label(icona);
        
        miniLabel.getStyleClass().addAll("cover-base", classeCss);
        
        // 1. Aumentiamo di un pelo il quadrato (70x70) per far respirare le spade
        miniLabel.setPrefSize(70, 70);
        miniLabel.setMinSize(70, 70);
        miniLabel.setMaxSize(70, 70);
        
        // 2. Riduciamo leggermente il font e azzeriamo il padding (margine interno) di default
        miniLabel.setStyle("-fx-font-size: 28px; -fx-padding: 0;"); 
        
        // 3. IL COLPO DI GRAZIA AI PUNTINI: Diciamo a JavaFX di tagliare il bordo 
        // in eccesso invece di mettere "..."
        miniLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        
        miniLabel.setAlignment(Pos.CENTER);
        
        return miniLabel;
    }

    @FXML
    @SuppressWarnings("unused")
    private void apriDettagliAcquisto() {
        try {
            DettagliGiocoController.giocoInMemoria = this.gioco;
            App.setRoot("dettagli_gioco");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento dettagli_gioco.fxml", ex);
        }
    }
}