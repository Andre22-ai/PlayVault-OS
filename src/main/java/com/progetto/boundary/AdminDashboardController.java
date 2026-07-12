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

public class AdminDashboardController {

    // Variabile di stato per capire se stiamo modificando (se è null, stiamo inserendo)
    private Integer idInModifica = null;

    @FXML private Label mainTitleLabel;

    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField devField;
    
    @FXML private TextArea descItArea;
    @FXML private TextArea descEnArea;
    
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> gameSelectorComboBox; 
    
    @FXML private ComboBox<String> editSelectorComboBox; 
    @FXML private Label lblEditZone;
    @FXML private Button btnLoad;
    @FXML private Button btnCancelEdit;

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
        btnCancelEdit.setVisible(false); 
        impostaStatoVisivo(GestoreLingua.getIstanza().get("admin.status.awaiting"), "status-default");
    }

    private void aggiornaListaGiochi() {
        if (gameSelectorComboBox != null) gameSelectorComboBox.getItems().clear();
        if (editSelectorComboBox != null) editSelectorComboBox.getItems().clear();
        
        List<Videogioco> giochi = App.getVideogiocoDAO().recuperaTutti();
        for (Videogioco g : giochi) {
            String voce = g.getId() + " - " + g.getTitolo();
            if (gameSelectorComboBox != null) gameSelectorComboBox.getItems().add(voce);
            if (editSelectorComboBox != null) editSelectorComboBox.getItems().add(voce);
        }
    }

    // --- METODO HELPER PER IL CSS ---
    private void impostaStatoVisivo(String messaggio, String classeCssCss) {
        statusLabel.setText(messaggio);
        // Rimuove tutti i colori precedenti
        statusLabel.getStyleClass().removeAll("status-default", "status-success", "status-error", "status-info");
        // Aggiunge il nuovo colore
        statusLabel.getStyleClass().add(classeCssCss);
    }

    @FXML
    @SuppressWarnings("unused")
    private void caricaDatiPerModifica() {
        String selezione = editSelectorComboBox != null ? editSelectorComboBox.getValue() : null;
        if (selezione == null || selezione.trim().isEmpty()) return;

        int idGioco = Integer.parseInt(selezione.split(" - ")[0]);

        Videogioco giocoTrovato = App.getVideogiocoDAO().recuperaTutti().stream()
                .filter(v -> v.getId() == idGioco)
                .findFirst().orElse(null);

        if (giocoTrovato != null) {
            titleField.setText(giocoTrovato.getTitolo());
            devField.setText(giocoTrovato.getSviluppatore());
            genreField.setText(giocoTrovato.getGenere());
            yearField.setText(String.valueOf(giocoTrovato.getAnnoUscita()));
            descItArea.setText(giocoTrovato.getDescrizioneIt()); 
            descEnArea.setText(giocoTrovato.getDescrizioneEn());

            idInModifica = idGioco;
            btnUpload.setText(GestoreLingua.getIstanza().get("admin.btn.update"));
            
            // Applica lo stile CSS Edit Mode (rimuovendo prima il Magenta base)
            btnUpload.getStyleClass().remove("btn-admin-magenta");
            if (!btnUpload.getStyleClass().contains("btn-admin-edit-mode")) {
                btnUpload.getStyleClass().add("btn-admin-edit-mode");
            }
            
            btnCancelEdit.setVisible(true);
            impostaStatoVisivo(GestoreLingua.getIstanza().get("admin.msg.edit_mode"), "status-info");
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void annullaModifica() {
        svuotaCampi();
        idInModifica = null;
        btnUpload.setText(GestoreLingua.getIstanza().get("admin.btn.upload"));
        
        // Ripristina lo stile CSS Magenta rimuovendo quello giallo
        btnUpload.getStyleClass().remove("btn-admin-edit-mode");
        if (!btnUpload.getStyleClass().contains("btn-admin-magenta")) {
            btnUpload.getStyleClass().add("btn-admin-magenta");
        }
        
        btnCancelEdit.setVisible(false);
        editSelectorComboBox.getSelectionModel().clearSelection();
        impostaStatoVisivo("EDIT MODE ABORTED.", "status-error");
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
            if (idInModifica == null) {
                catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, descIt, descEn);
                impostaStatoVisivo("SYSTEM OVERRIDE: DATA UPLOADED.", "status-success");
            } else {
                catalogoControl.modificaGiocoEsistente(idInModifica, titolo, genere, anno, dev, descIt, descEn);
                impostaStatoVisivo(GestoreLingua.getIstanza().get("admin.msg.update_ok"), "status-success");
                annullaModifica(); 
            }
            
            svuotaCampi();
            aggiornaListaGiochi();
            
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            impostaStatoVisivo("ERROR: " + e.getMessage(), "status-error");
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiRimozione() {
        String selezione = gameSelectorComboBox != null ? gameSelectorComboBox.getValue() : null;
        if (selezione == null || selezione.trim().isEmpty()) {
            impostaStatoVisivo("ERROR: SELEZIONARE UN GIOCO DALLA LISTA.", "status-error");
            return;
        }

        try {
            int idGioco = Integer.parseInt(selezione.split(" - ")[0]);
            catalogoControl.rimuoviGioco(idGioco); 
            
            impostaStatoVisivo("SYSTEM OVERRIDE: GAME DE-LISTED.", "status-success");
            
            if (idInModifica != null && idInModifica == idGioco) {
                annullaModifica(); 
            }
            
            aggiornaListaGiochi();
        } catch (Exception e) {
            impostaStatoVisivo("ERROR: " + e.getMessage(), "status-error");
        }
    }

    private void svuotaCampi() {
        titleField.clear(); genreField.clear(); yearField.clear(); devField.clear(); 
        descItArea.clear(); descEnArea.clear();
    }

    @FXML
    @SuppressWarnings("unused")
    private void cambiaLingua() {
        String lang = GestoreLingua.getIstanza().getLocaleCorrente().getLanguage();
        GestoreLingua.getIstanza().impostaLingua("it".equalsIgnoreCase(lang) ? "en" : "it");
        aggiornaTestiUI();
    }

    private void aggiornaTestiUI() {
        if (mainTitleLabel != null) mainTitleLabel.setText(GestoreLingua.getIstanza().get("admin.title"));
        if (gameSelectorComboBox != null) gameSelectorComboBox.setPromptText(GestoreLingua.getIstanza().get("admin.combo.prompt"));
        
        if (editSelectorComboBox != null) editSelectorComboBox.setPromptText(GestoreLingua.getIstanza().get("admin.combo.edit_prompt"));
        if (lblEditZone != null) lblEditZone.setText(GestoreLingua.getIstanza().get("admin.lbl.edit_zone"));
        if (btnLoad != null) btnLoad.setText(GestoreLingua.getIstanza().get("admin.btn.load"));
        if (btnCancelEdit != null) btnCancelEdit.setText(GestoreLingua.getIstanza().get("admin.btn.cancel_edit"));

        btnSwitchLang.setText(GestoreLingua.getIstanza().get("admin.btn.switch"));
        btnLogout.setText(GestoreLingua.getIstanza().get("admin.btn.logout"));
        lblTitle.setText(GestoreLingua.getIstanza().get("admin.lbl.title"));
        lblDev.setText(GestoreLingua.getIstanza().get("admin.lbl.dev"));
        lblGenre.setText(GestoreLingua.getIstanza().get("admin.lbl.genre"));
        lblYear.setText(GestoreLingua.getIstanza().get("admin.lbl.year"));
        
        lblDanger.setText(GestoreLingua.getIstanza().get("admin.lbl.danger"));
        btnDelist.setText(GestoreLingua.getIstanza().get("admin.btn.delist"));
        
        if (idInModifica == null) {
            btnUpload.setText(GestoreLingua.getIstanza().get("admin.btn.upload"));
            impostaStatoVisivo(GestoreLingua.getIstanza().get("admin.status.awaiting"), "status-default");
        } else {
            btnUpload.setText(GestoreLingua.getIstanza().get("admin.btn.update"));
            impostaStatoVisivo(GestoreLingua.getIstanza().get("admin.msg.edit_mode"), "status-info");
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void eseguiLogout() throws IOException {
        App.setRoot("login");
    }
}