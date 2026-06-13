package com.progetto;

import java.io.IOException;

import com.progetto.Control.GestioneCatalogoControl;
import com.progetto.DAO.VideogiocoDAOMySQL;

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

    private GestioneCatalogoControl catalogoControl;

    public AdminDashboardController() {
        this.catalogoControl = new GestioneCatalogoControl(new VideogiocoDAOMySQL());
    }

    @FXML
    private void eseguiUpload() {
        String titolo = titleField.getText();
        String genere = genreField.getText();
        String anno = yearField.getText();
        String dev = devField.getText();
        String desc = descArea.getText();

        boolean successo = catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, desc);

        if (successo) {
            statusLabel.setText("SYSTEM OVERRIDE: DATA UPLOADED.");
            statusLabel.setTextFill(javafx.scene.paint.Color.web("#39ff14"));
            titleField.clear(); genreField.clear(); yearField.clear(); devField.clear(); descArea.clear();
        } else {
            statusLabel.setText("ERROR: INVALID DATA.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void eseguiLogout() throws IOException {
        App.setRoot("login");
    }
}