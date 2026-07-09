package com.progetto.boundary;

import com.progetto.entita.Utente;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardClassificaController {

    @FXML private VBox cardBox;
    @FXML private Label iconLbl;
    @FXML private Label nameLbl;
    @FXML private Label creditsLbl;

    public void setDati(Utente u, int rank) {
        nameLbl.setText(u.getUsername().toUpperCase());
        creditsLbl.setText(u.getCrediti() + " CR");

        // Applichiamo stile e icona in base alla posizione in classifica
        switch (rank) {
            case 1 -> {
                iconLbl.setText("🥇");
                cardBox.getStyleClass().add("rank-gold");
                nameLbl.getStyleClass().add("testo-gold");
            }
            case 2 -> {
                iconLbl.setText("🥈");
                cardBox.getStyleClass().add("rank-silver");
                nameLbl.getStyleClass().add("testo-silver");
            }
            case 3 -> {
                iconLbl.setText("🥉");
                cardBox.getStyleClass().add("rank-bronze");
                nameLbl.getStyleClass().add("testo-bronze");
            }
            default -> {
                iconLbl.setText("👤");
                cardBox.getStyleClass().add("rank-standard");
                nameLbl.getStyleClass().add("testo-standard");
            }
        }
    }
}