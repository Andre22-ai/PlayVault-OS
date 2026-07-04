package com.progetto;

import java.io.IOException;
import java.util.Scanner;

import com.progetto.boundary.CliEngine;
import com.progetto.controllo.AcquistoControl;
import com.progetto.controllo.AutenticazioneControl;
import com.progetto.controllo.ClassificaControl;
import com.progetto.controllo.GestioneCatalogoControl;
import com.progetto.controllo.LibreriaControl;
import com.progetto.controllo.RecensioneControl;
import com.progetto.controllo.RegistrazioneControl;
import com.progetto.database.LibreriaDAO;
import com.progetto.database.LibreriaDAOMemory;
import com.progetto.database.LibreriaDAOMySQL;
import com.progetto.database.LibreriaDAOcsv;
import com.progetto.database.RecensioneDAO;
import com.progetto.database.RecensioneDAOMemory;
import com.progetto.database.RecensioneDAOMySQL;
import com.progetto.database.RecensioneDAOcsv;
import com.progetto.database.UtenteDAO;
import com.progetto.database.UtenteDAOMemory;
import com.progetto.database.UtenteDAOMySQL;
import com.progetto.database.UtenteDAOcsv;
import com.progetto.database.VideogiocoDAO;        // <-- NUOVI IMPORT RECENSIONI
import com.progetto.database.VideogiocoDAOMemory;
import com.progetto.database.VideogiocoDAOMySQL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App - PLAYVAULT Main Entry Point
 */
public class App extends Application {

    private static Scene scene;

    // --- COSTANTE PER IL FIX SONARCLOUD (java:S1192) ---
    private static final String MENU_SEPARATOR = "========================================";

    // ==========================================
    // LE NOSTRE VARIABILI POLIMORFE GLOBALI
    // ==========================================
    private static UtenteDAO utenteDAOScelto;
    private static VideogiocoDAO videogiocoDAOScelto;
    private static LibreriaDAO libreriaDAOScelto;
    private static RecensioneDAO recensioneDAOScelto; 

    // ==========================================
    // GETTER PER I CONTROLLER GRAFICI
    // ==========================================
    public static UtenteDAO getUtenteDAO() { return utenteDAOScelto; }
    public static VideogiocoDAO getVideogiocoDAO() { return videogiocoDAOScelto; }
    public static LibreriaDAO getLibreriaDAO() { return libreriaDAOScelto; }
    public static RecensioneDAO getRecensioneDAO() { return recensioneDAOScelto; } 

    // Metodo statico per risolvere il Blocker SonarCloud (java:S2696)
    private static void inizializzaScena(Parent root) {
        scene = new Scene(root, 1000, 600);
    }

    @Override
    public void start(Stage stage) throws IOException {
        inizializzaScena(loadFXML("login"));
        stage.setScene(scene);
        stage.setTitle("PLAYVAULT - Arcade Game Vault");
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

    @SuppressWarnings("java:S106")
    public static void main(String[] args) {
        @SuppressWarnings("resource") 
        Scanner scanner = new Scanner(System.in);
        
        // ==========================================================
        // FASE 1: SCELTA DEL DATABASE (Il Requisito del Professore)
        // ==========================================================
        System.out.println(MENU_SEPARATOR);
        System.out.println("      CONFIGURAZIONE SALVATAGGIO        ");
        System.out.println(MENU_SEPARATOR);
        System.out.println("1. Database MySQL (Full-Version DBMS)");
        System.out.println("2. File CSV (Full-Version File System)");
        System.out.println("3. Memoria RAM (Demo-Version In-Memory)");
        System.out.print("Scegli il motore dati (1, 2 o 3): ");
        
        String sceltaDB = scanner.nextLine().trim();
        
        // Creiamo i DAO in base alla scelta (Polimorfismo di salvataggio!)
        if (sceltaDB.equals("2")) {
            utenteDAOScelto = new UtenteDAOcsv();
            videogiocoDAOScelto = new VideogiocoDAOMemory(); // Usa la memoria demo per avere i 3 giochi pronti
            libreriaDAOScelto = new LibreriaDAOcsv();       // PERSISTENZA CSV ATTIVA!
            recensioneDAOScelto = new RecensioneDAOcsv();   // PERSISTENZA CSV ATTIVA!
            System.out.println("[INFO] Selezionato File System (utenti.csv, libreria.csv, recensioni.csv).\n");
            
        } else if (sceltaDB.equals("3")) {
            utenteDAOScelto = new UtenteDAOMemory();
            videogiocoDAOScelto = new VideogiocoDAOMemory(); 
            libreriaDAOScelto = new LibreriaDAOMemory(); 
            recensioneDAOScelto = new RecensioneDAOMemory(); // <-- MODALITA' RAM COMPLETA AL 100%
            System.out.println("[INFO] Selezionata RAM per tutto il sistema. I dati andranno persi alla chiusura.\n");
            
        } else {
            utenteDAOScelto = new UtenteDAOMySQL();
            videogiocoDAOScelto = new VideogiocoDAOMySQL();
            libreriaDAOScelto = new LibreriaDAOMySQL();
            recensioneDAOScelto = new RecensioneDAOMySQL(); 
            System.out.println("[INFO] Selezionato MySQL (Assicurati che il server sia acceso).\n");
        }

        // ==========================================================
        // FASE 2: SCELTA DELL'INTERFACCIA (GUI o Terminale)
        // ==========================================================
        boolean appAttiva = true;

        while (appAttiva) {
            System.out.println(MENU_SEPARATOR);
            System.out.println(" SELEZIONA MODALITA' DI AVVIO PLAYVAULT ");
            System.out.println(MENU_SEPARATOR);
            System.out.println("1. Avvia con Interfaccia Grafica (GUI - JavaFX)");
            System.out.println("2. Avvia nel Terminale (CLI)");
            System.out.println("3. Spegni il sistema");
            System.out.print("Scelta (1, 2 o 3): ");

            String sceltaUI = scanner.nextLine().trim();

            if (sceltaUI.equals("2")) {
                System.out.println("\n[Avviando la modalità Terminale...]\n");
                
                // Passiamo i Database polimorfi ai rispettivi Control!
                AutenticazioneControl authControl = new AutenticazioneControl(utenteDAOScelto);
                RegistrazioneControl regControl = new RegistrazioneControl(utenteDAOScelto);
                ClassificaControl classificaControl = new ClassificaControl(utenteDAOScelto);
                
                GestioneCatalogoControl catalogoControl = new GestioneCatalogoControl(videogiocoDAOScelto);
                LibreriaControl libreriaControl = new LibreriaControl(videogiocoDAOScelto, libreriaDAOScelto); 
                AcquistoControl acquistoControl = new AcquistoControl(libreriaDAOScelto);
                
                RecensioneControl recensioneControl = new RecensioneControl(recensioneDAOScelto, utenteDAOScelto); 
                
                // Facciamo partire la CLI
                CliEngine motoreCLI = new CliEngine(
                    authControl, regControl, catalogoControl, 
                    libreriaControl, recensioneControl, acquistoControl, classificaControl
                );
                
                motoreCLI.avvia();

                System.out.println("\n[Ritorno al menu principale...]\n");

            } else if (sceltaUI.equals("1")) {
                System.out.println("\n[Avviando la modalità Grafica...]\n");
                appAttiva = false; // Fermiamo il ciclo testuale perché JavaFX prende il controllo
                launch(args); 

            } else if (sceltaUI.equals("3")) {
                System.out.println("\nSpegnimento di PlayVault in corso. Arrivederci!");
                appAttiva = false; // Rompe il ciclo while e il programma si chiude

            } else {
                System.out.println("\n[ERRORE] Scelta non valida. Inserisci 1, 2 o 3.\n");
            }
        }
    }
}