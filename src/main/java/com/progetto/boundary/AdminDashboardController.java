package com.progetto.boundary;

import java.io.IOException;
import java.util.List;

import com.progetto.App;
import com.progetto.controllo.GestioneCatalogoControl;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.SalvataggioFallitoException;
import com.progetto.utils.GestoreLingua;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class AdminDashboardController {

    // Costanti per i colori di stato
    private static final Color COLOR_SUCCESS = Color.web("#39ff14"); // Verde Hacker
    private static final Color COLOR_ERROR = Color.RED;
    private static final Color COLOR_INFO = Color.web("#00ffff"); // Ciano per l'attesa

    @FXML private Label mainTitleLabel; // NUOVO: Riferimento al titolo principale

    // Campi di input testuali
    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField devField;
    
    // Aree di testo bilingue
    @FXML private TextArea descItArea;
    @FXML private TextArea descEnArea;
    
    // Etichette di stato e selettore per la rimozione
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> gameSelectorComboBox; 

    // Elementi UI da tradurre dinamicamente
    @FXML private Button btnSwitchLang;
    @FXML private Button btnLogout;
    @FXML private Label lblTitle;
    @FXML private Label lblDev;
    @FXML private Label lblGenre;
    @FXML private Label lblYear;
    @FXML private Button btnUpload;
    @FXML private Label lblDanger;
    @FXML private Button btnDelist;

    private final GestioneCatalogoControl catalogoControl;

    public AdminDashboardController() {
        this.catalogoControl = new GestioneCatalogoControl(App.getVideogiocoDAO());
    }

    @FXML
    public void initialize() {
        aggiornaListaGiochi();
        aggiornaTestiUI(); 
        
        // Imposta il messaggio di "AWAITING INPUT..." iniziale nella lingua corretta
        statusLabel.setText(GestoreLingua.getIstanza().get("admin.status.awaiting"));
        statusLabel.setTextFill(COLOR_INFO);
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
        
        String descIt = descItArea.getText();
        String descEn = descEnArea.getText();

        try {
            catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, descIt, descEn);
            
            statusLabel.setText("SYSTEM OVERRIDE: DATA UPLOADED.");
            statusLabel.setTextFill(COLOR_SUCCESS);
            
            titleField.clear(); genreField.clear(); yearField.clear(); devField.clear(); 
            descItArea.clear(); descEnArea.clear();
            
            aggiornaListaGiochi();
            
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            statusLabel.setText("ERROR: " + e.getMessage());
            statusLabel.setTextFill(COLOR_ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiRimozione() {
        String selezione = gameSelectorComboBox != null ? gameSelectorComboBox.getValue() : null;
        
        if (selezione == null || selezione.trim().isEmpty()) {
            statusLabel.setText("ERROR: SELEZIONARE UN GIOCO DALLA LISTA.");
            statusLabel.setTextFill(COLOR_ERROR);
            return;
        }

        try {
            String idStr = selezione.split(" - ")[0];
            int idGioco = Integer.parseInt(idStr);
            catalogoControl.rimuoviGioco(idGioco); 
            
            statusLabel.setText("SYSTEM OVERRIDE: GAME DE-LISTED (SOFT DELETE).");
            statusLabel.setTextFill(COLOR_SUCCESS);
            aggiornaListaGiochi();
            
        } catch (Exception e) {
            statusLabel.setText("ERROR: " + e.getMessage());
            statusLabel.setTextFill(COLOR_ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void cambiaLingua() {
        String linguaAttuale = GestoreLingua.getIstanza().getLocaleCorrente().getLanguage();
        
        if ("it".equalsIgnoreCase(linguaAttuale)) {
            GestoreLingua.getIstanza().impostaLingua("en");
        } else {
            GestoreLingua.getIstanza().impostaLingua("it");
        }
        
        aggiornaTestiUI();
        
        statusLabel.setText(GestoreLingua.getIstanza().get("admin.msg.lang_set"));
        statusLabel.setTextFill(COLOR_SUCCESS);
    }

    private void aggiornaTestiUI() {
        // Applica le traduzioni a tutte le label e i bottoni, incluso il titolo e la combobox
        if (mainTitleLabel != null) {
            mainTitleLabel.setText(GestoreLingua.getIstanza().get("admin.title"));
        }
        
        if (gameSelectorComboBox != null) {
            gameSelectorComboBox.setPromptText(GestoreLingua.getIstanza().get("admin.combo.prompt"));
        }

        btnSwitchLang.setText(GestoreLingua.getIstanza().get("admin.btn.switch"));
        btnLogout.setText(GestoreLingua.getIstanza().get("admin.btn.logout"));
        lblTitle.setText(GestoreLingua.getIstanza().get("admin.lbl.title"));
        lblDev.setText(GestoreLingua.getIstanza().get("admin.lbl.dev"));
        lblGenre.setText(GestoreLingua.getIstanza().get("admin.lbl.genre"));
        lblYear.setText(GestoreLingua.getIstanza().get("admin.lbl.year"));
        btnUpload.setText(GestoreLingua.getIstanza().get("admin.btn.upload"));
        lblDanger.setText(GestoreLingua.getIstanza().get("admin.lbl.danger"));
        btnDelist.setText(GestoreLingua.getIstanza().get("admin.btn.delist"));
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiLogout() throws IOException {
        App.setRoot("login");
    }
}