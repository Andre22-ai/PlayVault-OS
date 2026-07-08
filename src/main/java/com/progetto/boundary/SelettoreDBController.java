package com.progetto.boundary;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.database.LibreriaDAOMemory;
import com.progetto.database.LibreriaDAOMySQL;
import com.progetto.database.LibreriaDAOcsv;
import com.progetto.database.RecensioneDAOMemory;
import com.progetto.database.RecensioneDAOMySQL;
import com.progetto.database.RecensioneDAOcsv;
import com.progetto.database.UtenteDAOMemory;
import com.progetto.database.UtenteDAOMySQL;
import com.progetto.database.UtenteDAOcsv;
import com.progetto.database.VideogiocoDAOMemory;
import com.progetto.database.VideogiocoDAOMySQL;

import javafx.fxml.FXML;

public class SelettoreDBController {

    private static final Logger LOGGER = Logger.getLogger(SelettoreDBController.class.getName());
    
    private static final String SCHERMATA_LOGIN = "login";

    @FXML
    private void avviaMySQL() throws IOException {
        LOGGER.info("[BOOT] Avvio motori su MySQL...");
        App.setUtenteDAO(new UtenteDAOMySQL());
        App.setVideogiocoDAO(new VideogiocoDAOMySQL());
        App.setLibreriaDAO(new LibreriaDAOMySQL());
        App.setRecensioneDAO(new RecensioneDAOMySQL());
        
        App.setRoot(SCHERMATA_LOGIN);
    }

    @FXML
    private void avviaCSV() throws IOException {
        LOGGER.info("[BOOT] Avvio motori su File System (CSV)...");
        App.setUtenteDAO(new UtenteDAOcsv());
        App.setVideogiocoDAO(new VideogiocoDAOMemory()); 
        App.setLibreriaDAO(new LibreriaDAOcsv());
        App.setRecensioneDAO(new RecensioneDAOcsv());
        
        App.setRoot(SCHERMATA_LOGIN);
    }

    @FXML
    private void avviaDemo() throws IOException {
        LOGGER.info("[BOOT] Avvio motori in RAM (Demo)...");
        App.setUtenteDAO(new UtenteDAOMemory());
        App.setVideogiocoDAO(new VideogiocoDAOMemory());
        App.setLibreriaDAO(new LibreriaDAOMemory());
        App.setRecensioneDAO(new RecensioneDAOMemory());
        
        App.setRoot(SCHERMATA_LOGIN);
    }
}