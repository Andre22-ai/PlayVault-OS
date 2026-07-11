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
import com.progetto.utils.BadgeUtils;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardLibreriaController {

    private static final Logger LOGGER = Logger.getLogger(CardLibreriaController.class.getName());

    @FXML private Label idLbl;
    @FXML private Label titoloLbl;
    @FXML private Label coverTesto;
    @FXML private VBox gameBox;

    private ElementoLibreria elemento;
    private Utente utenteCorrente;
    private LibreriaControl libreriaControl;
    private Runnable onAggiornamento; 

    public void setDati(ElementoLibreria elemento, Utente utente, LibreriaControl control, Runnable onAggiornamento) {
        this.elemento = elemento;
        this.utenteCorrente = utente;
        this.libreriaControl = control;
        this.onAggiornamento = onAggiornamento;

        Videogioco gioco = elemento.getVideogioco();
        
        idLbl.setText("[ID: " + gioco.getId() + "]");
        titoloLbl.setText(gioco.getTitolo());
        
        // --- LOGICA BADGE (Gestita da BadgeUtils) ---
        coverTesto.setText(""); 
        coverTesto.getStyleClass().clear();
        coverTesto.setStyle("-fx-background-color: transparent; -fx-alignment: center;");
        coverTesto.setGraphic(BadgeUtils.generaBadgeGeneri(gioco.getGenere()));

        costruisciAreaGamification(gioco);
    }

    private void costruisciAreaGamification(Videogioco gioco) {
        gameBox.getChildren().clear();

        if (elemento.isCompletato()) {
            String testoCompletato = GestoreLingua.getIstanza().get("card.libreria.completato");
            Label lblCompletato = new Label(testoCompletato + " ✔");
            lblCompletato.getStyleClass().add("testo-titolo-verde"); 
            gameBox.getChildren().add(lblCompletato);
        } else {
            String testoCompleta = GestoreLingua.getIstanza().get("card.libreria.completa");
            Button btnCompleta = new Button(testoCompleta + " (" + gioco.getExpFornita() + " XP)");
            btnCompleta.getStyleClass().add("btn-outline-ciano"); 
            
            btnCompleta.setOnAction(e -> {
                boolean successo = libreriaControl.completaGioco(utenteCorrente, gioco);
                if (successo) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                    alert.setHeaderText(null);
                    
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
            
            Locale localeAttuale = GestoreLingua.getIstanza().getLocaleCorrente();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", localeAttuale);
            loader.setResources(bundle);
            
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.setGioco(elemento.getVideogioco());
            idLbl.getScene().setRoot(root);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Errore caricamento recensione", ex);
        }
    }
}