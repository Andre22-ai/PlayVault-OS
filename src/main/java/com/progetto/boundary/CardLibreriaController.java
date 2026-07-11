package com.progetto.boundary;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.controllo.LibreriaControl;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Utente;
import com.progetto.entita.Videogioco;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
        
        // --- NUOVA LOGICA BADGE MULTIPLI ---
        coverTesto.setText(""); 
        coverTesto.getStyleClass().clear();
        coverTesto.setStyle("-fx-background-color: transparent; -fx-alignment: center;");
        
        HBox contenitoreIcone = new HBox(15);
        contenitoreIcone.setAlignment(Pos.CENTER);
        
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
        
        coverTesto.setGraphic(contenitoreIcone);
        // -----------------------------------

        costruisciAreaGamification(gioco);
    }

    private void costruisciAreaGamification(Videogioco gioco) {
        gameBox.getChildren().clear();

        if (elemento.isCompletato()) {
            // --- FIX LINGUA: Label Completato ---
            String testoCompletato = GestoreLingua.getIstanza().get("card.libreria.completato");
            Label lblCompletato = new Label(testoCompletato + " ✔");
            lblCompletato.getStyleClass().add("testo-titolo-verde"); // Usa il CSS
            gameBox.getChildren().add(lblCompletato);
        } else {
            // --- FIX LINGUA: Bottone Completa ---
            String testoCompleta = GestoreLingua.getIstanza().get("card.libreria.completa");
            Button btnCompleta = new Button(testoCompleta + " (" + gioco.getExpFornita() + " XP)");
            btnCompleta.getStyleClass().add("btn-outline-ciano"); // Usa il CSS
            
            btnCompleta.setOnAction(e -> {
                boolean successo = libreriaControl.completaGioco(utenteCorrente, gioco);
                if (successo) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                    alert.setHeaderText(null);
                    
                    // --- FIX LINGUA: Testi Alert ---
                    String alertTitolo = GestoreLingua.getIstanza().get("alert.levelup.titolo");
                    String alertMsg = GestoreLingua.getIstanza().get("alert.levelup.messaggio");
                    
                    alert.setTitle(alertTitolo);
                    alert.setContentText(alertMsg + " " + gioco.getExpFornita() + " EXP!");
                    alert.showAndWait();
                    if (onAggiornamento != null) onAggiornamento.run(); 
                }
            });
            gameBox.getChildren().add(btnCompleta);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void lanciaGioco() {
        LOGGER.info(() -> "[SYSTEM RUNTIME] Lancio binario di: " + elemento.getVideogioco().getTitolo());
    }

    @FXML
    @SuppressWarnings("unused")
    private void recensisciGioco() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("scrivi_recensione.fxml"));
            
            // --- FIX LINGUA: Iniezione del ResourceBundle ---
            Locale localeAttuale = GestoreLingua.getIstanza().getLocaleCorrente();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", localeAttuale);
            loader.setResources(bundle);
            // ------------------------------------------------
            
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.setGioco(elemento.getVideogioco());
            idLbl.getScene().setRoot(root);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento recensione", ex);
        }
    }

    // --- METODO FABBRICA PER I BADGE DELLE ICONE ---
    private Label creaMiniIcona(String icona, String classeCss) {
        Label miniLabel = new Label(icona);
        miniLabel.getStyleClass().addAll("cover-base", classeCss);
        miniLabel.setPrefSize(70, 70);
        miniLabel.setMinSize(70, 70);
        miniLabel.setMaxSize(70, 70);
        miniLabel.setStyle("-fx-font-size: 28px; -fx-padding: 0;"); 
        miniLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        miniLabel.setAlignment(Pos.CENTER);
        return miniLabel;
    }
}