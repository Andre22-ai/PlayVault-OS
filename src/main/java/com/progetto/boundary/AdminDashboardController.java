package com.progetto.boundary;

import java.io.IOException;

import com.progetto.App;
import com.progetto.controllo.GestioneCatalogoControl;
import com.progetto.exceptions.SalvataggioFallitoException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdminDashboardController {

    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField devField;
    @FXML private TextArea descArea;
    @FXML private Label statusLabel;

    private final GestioneCatalogoControl catalogoControl;

    public AdminDashboardController() {
        // FIX 2: Chiediamo ad App qual è il database scelto per i videogiochi!
        this.catalogoControl = new GestioneCatalogoControl(App.getVideogiocoDAO());
    }

    @FXML
    private void eseguiUpload() {
        String titolo = titleField.getText();
        String genere = genreField.getText();
        String anno = yearField.getText();
        String dev = devField.getText();
        String desc = descArea.getText();

        try {
            catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, desc);
            statusLabel.setText("SYSTEM OVERRIDE: DATA UPLOADED.");
            statusLabel.setTextFill(javafx.scene.paint.Color.web("#39ff14"));
            titleField.clear(); genreField.clear(); yearField.clear(); devField.clear(); descArea.clear();
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            statusLabel.setText("ERROR: " + e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void eseguiLogout() throws IOException {
        App.setRoot("login");
    }
}