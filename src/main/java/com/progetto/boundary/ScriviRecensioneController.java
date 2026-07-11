package com.progetto.boundary;

import java.io.IOException;

import com.progetto.App;
import com.progetto.controllo.RecensioneControl;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.RecensioneInvalidaException;
import com.progetto.exceptions.SalvataggioFallitoException;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ScriviRecensioneController {

    private static final String VIEW_DASHBOARD = "dashboard";

    @FXML private Label titoloGiocoLabel;
    @FXML private ComboBox<Integer> votoCombo;
    @FXML private TextArea commentoArea;

    private Videogioco giocoDaRecensire;
    
    private final RecensioneControl recensioneControl;
    
    private boolean modalitaModifica = false; 

    public ScriviRecensioneController() {
        this.recensioneControl = new RecensioneControl(App.getRecensioneDAO(), App.getUtenteDAO());
    }

    @FXML
    public void initialize() {
        votoCombo.getItems().addAll(1, 2, 3, 4, 5);
        votoCombo.setValue(5);
    }

    public void setGioco(Videogioco gioco) {
        this.giocoDaRecensire = gioco;
        
        // --- FIX LINGUA: Traduzione del prefisso TARGET ---
        String testoTarget = GestoreLingua.getIstanza().get("card.recensione.target");
        titoloGiocoLabel.setText(testoTarget + ": " + gioco.getTitolo());
    }

    public void setRecensioneDaModificare(Recensione r) {
        this.modalitaModifica = true;
        
        this.giocoDaRecensire = new Videogioco(r.getNomeGioco(), "", 0, "", "");
        this.giocoDaRecensire.setId(r.getIdGioco());
        
        // --- FIX LINGUA: Traduzione del prefisso TARGET e (EDIT MODE) ---
        String testoTarget = GestoreLingua.getIstanza().get("card.recensione.target");
        String testoEdit = GestoreLingua.getIstanza().get("recensione.edit_mode");
        titoloGiocoLabel.setText(testoTarget + ": " + r.getNomeGioco() + " " + testoEdit);
        
        votoCombo.setValue(r.getVoto()); 
        commentoArea.setText(r.getCommento()); 
    }

    @FXML
    @SuppressWarnings("unused")
    private void inviaRecensione() throws IOException {
        String testo = commentoArea.getText().trim();
        if (testo.isEmpty()) {
            mostraAlert("WARNING", GestoreLingua.getIstanza().get("alert.recensione.vuota"));
            return;
        }

        String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
        int voto = votoCombo.getValue();
        Recensione r = new Recensione(username, giocoDaRecensire.getId(), voto, testo);

        if (modalitaModifica) {
            boolean successo = recensioneControl.modificaRecensionePersonale(r);
            if (successo) {
                mostraAlert("SYSTEM UPDATED", GestoreLingua.getIstanza().get("alert.recensione.modificata"));
                App.setRoot(VIEW_DASHBOARD); 
            } else {
                mostraAlert("ERROR", GestoreLingua.getIstanza().get("alert.recensione.errore"));
            }
        } else {
            try {
                recensioneControl.elaboraRecensione(r);
                mostraAlert("REWARD UNLOCKED", GestoreLingua.getIstanza().get("alert.recensione.successo"));
                App.setRoot(VIEW_DASHBOARD);
            } catch (RecensioneInvalidaException | SalvataggioFallitoException e) {
                mostraAlert("ACCESS DENIED", e.getMessage());
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void annulla() throws IOException { 
        App.tornaIndietro(); 
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}