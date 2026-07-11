package com.progetto.boundary;

import java.io.IOException;
import java.util.List;

import com.progetto.App;
import com.progetto.controllo.GestioneCatalogoControl;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.SalvataggioFallitoException;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdminDashboardController {

    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField devField;
    
    // --- FIX: Due aree separate ---
    @FXML private TextArea descItArea;
    @FXML private TextArea descEnArea;
    
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> gameSelectorComboBox; 

    private final GestioneCatalogoControl catalogoControl;

    public AdminDashboardController() {
        this.catalogoControl = new GestioneCatalogoControl(App.getVideogiocoDAO());
    }

    @FXML
    public void initialize() {
        aggiornaListaGiochi();
    }

    private void aggiornaListaGiochi() {
        if (gameSelectorComboBox != null) {
            gameSelectorComboBox.getItems().clear();
            List<Videogioco> giochi = App.getVideogiocoDAO().recuperaTutti();
            for (Videogioco g : giochi) {
                gameSelectorComboBox.getItems().add(g.getId() + " - " + g.getTitolo());
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiUpload() {
        String titolo = titleField.getText();
        String genere = genreField.getText();
        String anno = yearField.getText();
        String dev = devField.getText();
        
        // --- FIX: Prendi i due testi separati ---
        String descIt = descItArea.getText();
        String descEn = descEnArea.getText();

        try {
            // Invio entrambi i testi al Control
            catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, descIt, descEn);
            
            statusLabel.setText("SYSTEM OVERRIDE: DATA UPLOADED.");
            statusLabel.setTextFill(javafx.scene.paint.Color.web("#39ff14"));
            
            // Pulisci tutti i campi
            titleField.clear(); genreField.clear(); yearField.clear(); devField.clear(); 
            descItArea.clear(); descEnArea.clear();
            
            aggiornaListaGiochi();
            
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            statusLabel.setText("ERROR: " + e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiRimozione() {
        String selezione = gameSelectorComboBox != null ? gameSelectorComboBox.getValue() : null;
        
        if (selezione == null || selezione.trim().isEmpty()) {
            statusLabel.setText("ERROR: SELEZIONARE UN GIOCO DALLA LISTA.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            String idStr = selezione.split(" - ")[0];
            int idGioco = Integer.parseInt(idStr);
            catalogoControl.rimuoviGioco(idGioco); 
            
            statusLabel.setText("SYSTEM OVERRIDE: GAME DE-LISTED (SOFT DELETE).");
            statusLabel.setTextFill(javafx.scene.paint.Color.web("#39ff14"));
            aggiornaListaGiochi();
            
        } catch (Exception e) {
            statusLabel.setText("ERROR: " + e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiLogout() throws IOException {
        App.setRoot("login");
    }
}