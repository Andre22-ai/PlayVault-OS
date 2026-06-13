package com.progetto;

import java.io.IOException;

import com.progetto.Control.RecensioneControl;
import com.progetto.Entity.Recensione;
import com.progetto.Entity.Sessione;
import com.progetto.Entity.Videogioco;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ScriviRecensioneController {

    @FXML private Label titoloGiocoLabel;
    @FXML private ComboBox<Integer> votoCombo;
    @FXML private TextArea commentoArea;

    private Videogioco giocoDaRecensire;
    private RecensioneControl recensioneControl;
    private boolean modalitaModifica = false; // NUOVO: Ci dice se stiamo modificando!

    public ScriviRecensioneController() {
        this.recensioneControl = new RecensioneControl();
    }

    @FXML
    public void initialize() {
        votoCombo.getItems().addAll(1, 2, 3, 4, 5);
        votoCombo.setValue(5);
    }

    // Usato quando crei una recensione nuova
    public void setGioco(Videogioco gioco) {
        this.giocoDaRecensire = gioco;
        titoloGiocoLabel.setText("TARGET: " + gioco.getTitolo());
    }

    // NUOVO: Usato quando clicchi su "EDIT"
    public void setRecensioneDaModificare(Recensione r) {
        this.modalitaModifica = true;
        // Ricreiamo un "finto" gioco solo per avere ID e Titolo a disposizione
        this.giocoDaRecensire = new Videogioco(r.getNomeGioco(), "", 0, "", "");
        this.giocoDaRecensire.setId(r.getIdGioco());
        
        titoloGiocoLabel.setText("TARGET: " + r.getNomeGioco() + " (EDIT MODE)");
        votoCombo.setValue(r.getVoto()); // Pre-carica il vecchio voto
        commentoArea.setText(r.getCommento()); // Pre-carica il vecchio commento
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
                App.setRoot("dashboard");
            } else {
                mostraAlert("ERROR", "Impossibile modificare il log.");
            }
        } else {
            String esito = recensioneControl.elaboraRecensione(r);
            if (esito.equals("SUCCESS")) {
                mostraAlert("REWARD UNLOCKED", "Recensione acquisita nei server. Hai guadagnato +15 CREDITS!");
                App.setRoot("dashboard");
            } else if (esito.equals("ALREADY_REVIEWED")) {
                mostraAlert("ACCESS DENIED", "Hai già lasciato un log per questo gioco.");
            }
        }
    }

    @FXML
    private void annulla() throws IOException { App.setRoot("dashboard"); }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}