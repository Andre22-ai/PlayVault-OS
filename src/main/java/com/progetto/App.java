package com.progetto;

import java.io.IOException;
import java.util.logging.Logger;

import com.progetto.database.LibreriaDAO;
import com.progetto.database.RecensioneDAO;
import com.progetto.database.UtenteDAO;
import com.progetto.database.VideogiocoDAO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static Scene scene;

    
    private static UtenteDAO utenteDAOScelto;
    private static VideogiocoDAO videogiocoDAOScelto;
    private static LibreriaDAO libreriaDAOScelto;
    private static RecensioneDAO recensioneDAOScelto; 

    
    public static UtenteDAO getUtenteDAO() { return utenteDAOScelto; }
    public static void setUtenteDAO(UtenteDAO dao) { utenteDAOScelto = dao; }

    public static VideogiocoDAO getVideogiocoDAO() { return videogiocoDAOScelto; }
    public static void setVideogiocoDAO(VideogiocoDAO dao) { videogiocoDAOScelto = dao; }

    public static LibreriaDAO getLibreriaDAO() { return libreriaDAOScelto; }
    public static void setLibreriaDAO(LibreriaDAO dao) { libreriaDAOScelto = dao; }

    public static RecensioneDAO getRecensioneDAO() { return recensioneDAOScelto; } 
    public static void setRecensioneDAO(RecensioneDAO dao) { recensioneDAOScelto = dao; }

    private static void inizializzaScena(Parent root) {
        scene = new Scene(root, 1000, 600);
    }

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("[SISTEMA] Avvio Interfaccia Grafica JavaFX...");
        
        inizializzaScena(loadFXML("selettore_db"));
        
        stage.setScene(scene);
        stage.setTitle("PLAYVAULT - Boot Sequence");
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        LOGGER.info("[SISTEMA] Bootstrap dell'applicazione in corso...");
        launch(args);
    }
}