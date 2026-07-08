package com.progetto.boundary;

import java.io.IOException;

import com.progetto.App;
import com.progetto.controllo.RecensioneControl;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.RecensioneInvalidaException;
import com.progetto.exceptions.SalvataggioFallitoException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ScriviRecensioneController {

    // --- LA NOSTRA COSTANTE PER LA VISTA DASHBOARD ---
    private static final String VIEW_DASHBOARD = "dashboard";

    @FXML private Label titoloGiocoLabel;
    @FXML private ComboBox<Integer> votoCombo;
    @FXML private TextArea commentoArea;

    private Videogioco giocoDaRecensire;
    
    // FIX SonarCloud: Reso 'final'
    private final RecensioneControl recensioneControl;
    
    private boolean modalitaModifica = false; 

    public ScriviRecensioneController() {
        // NOTA: Tra poco modificheremo anche RecensioneControl per fargli accettare
        // un DAO dinamico da App.getRecensioneDAO(), proprio come gli altri!
        this.recensioneControl = new RecensioneControl(App.getRecensioneDAO(), App.getUtenteDAO());
    }

    @FXML
    public void initialize() {
        votoCombo.getItems().addAll(1, 2, 3, 4, 5);
        votoCombo.setValue(5);
    }

    public void setGioco(Videogioco gioco) {
        this.giocoDaRecensire = gioco;
        titoloGiocoLabel.setText("TARGET: " + gioco.getTitolo());
    }

    public void setRecensioneDaModificare(Recensione r) {
        this.modalitaModifica = true;
        
        // Ricreiamo un "finto" gioco solo per avere ID e Titolo a disposizione
        // I 5 parametri del costruttore rispettano esattamente la firma della tua Entity
        this.giocoDaRecensire = new Videogioco(r.getNomeGioco(), "", 0, "", "");
        this.giocoDaRecensire.setId(r.getIdGioco());
        
        titoloGiocoLabel.setText("TARGET: " + r.getNomeGioco() + " (EDIT MODE)");
        votoCombo.setValue(r.getVoto()); 
        commentoArea.setText(r.getCommento()); 
    }

    @FXML
    private void inviaRecensione() throws IOException {
        String testo = commentoArea.getText().trim();
        if (testo.isEmpty()) {
            mostraAlert("WARNING", "Il log di sistema (commento) non può essere vuoto!");
            return;
        }

        String username = Sessione.getIstanza().getUtenteCorrente().getUsername();
        int voto = votoCombo.getValue();
        Recensione r = new Recensione(username, giocoDaRecensire.getId(), voto, testo);

        // BIVIO: Stiamo modificando o inserendo?
        if (modalitaModifica) {
            boolean successo = recensioneControl.modificaRecensionePersonale(r);
            if (successo) {
                mostraAlert("SYSTEM UPDATED", "Log modificato con successo. Nessun credito extra erogato.");
                App.setRoot(VIEW_DASHBOARD); 
            } else {
                mostraAlert("ERROR", "Impossibile modificare il log.");
            }
        } else {
            try {
                recensioneControl.elaboraRecensione(r);
                mostraAlert("REWARD UNLOCKED", "Recensione acquisita nei server. Hai guadagnato +15 CREDITS!");
                App.setRoot(VIEW_DASHBOARD);
            } catch (RecensioneInvalidaException | SalvataggioFallitoException e) {
                mostraAlert("ACCESS DENIED", e.getMessage());
            }
        }
    }

    @FXML
    private void annulla() throws IOException { 
        App.setRoot(VIEW_DASHBOARD); 
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}