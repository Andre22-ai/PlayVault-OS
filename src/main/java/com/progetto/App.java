package com.progetto;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;
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

    // --- NUOVO SISTEMA DI MEMORIA A STACK (Infinita) ---
    private static Stack<String> cronologia = new Stack<>();
    private static String schermataCorrente = "selettore_db";
    // ---------------------------------------------------

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
        
        URL cssUrl = App.class.getResource("style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            LOGGER.info("[SISTEMA] Foglio di stile globale (style.css) caricato con successo.");
        } else {
            LOGGER.warning("[SISTEMA] Foglio di stile globale (style.css) NON TROVATO!");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("[SISTEMA] Avvio Interfaccia Grafica JavaFX...");
        
        inizializzaScena(loadFXML("selettore_db"));
        
        stage.setScene(scene);
        stage.setTitle("PLAYVAULT - Boot Sequence");
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Usa setRoot() per i cambi "brutali" senza ritorno (es. Logout, o fine Login).
     * Svuota la memoria per impedire all'utente di tornare indietro.
     */
    public static void setRoot(String fxml) throws IOException {
        cronologia.clear(); // Azzera la memoria!
        schermataCorrente = fxml;
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Usa cambiaSchermata() quando apri un menu e vuoi permettere 
     * all'utente di tornare indietro con il tasto Back.
     */
    public static void cambiaSchermata(String fxml) throws IOException {
        cronologia.push(schermataCorrente); // Salva la pagina in cui ci troviamo ora
        schermataCorrente = fxml;           // Aggiorna la nuova destinazione
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Metodo invocato dai tasti "Torna Indietro" nelle tue schermate.
     */
    public static void tornaIndietro() throws IOException {
        if (!cronologia.isEmpty()) {
            String paginaPrecedente = cronologia.pop(); // Estrae l'ultima pagina salvata
            schermataCorrente = paginaPrecedente;
            scene.setRoot(loadFXML(paginaPrecedente));
        } else {
            // Se la memoria è vuota, usa un fallback d'emergenza
            LOGGER.warning("Nessuna schermata in memoria, ricarico la dashboard.");
            setRoot("dashboard"); // Assicurati che questo sia il nome corretto del tuo FXML principale
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        
        java.util.Locale localeAttuale = com.progetto.utils.GestoreLingua.getIstanza().getLocaleCorrente();
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages", localeAttuale);
        fxmlLoader.setResources(bundle);
        
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        LOGGER.info("[SISTEMA] Bootstrap dell'applicazione in corso...");
        launch(args);
    }
}