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
import com.progetto.database.LibreriaDAOMySQL;
import com.progetto.database.UtenteDAOMySQL;
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

    // Metodo statico per risolvere il Blocker SonarCloud (java:S2696)
    private static void inizializzaScena(Parent root) {
        // Dimensioni impostate a 1000x600 come abbiamo progettato i file FXML
        scene = new Scene(root, 1000, 600);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // All'avvio carichiamo la schermata di login usando il metodo statico
        inizializzaScena(loadFXML("login"));
        
        stage.setScene(scene);
        stage.setTitle("PLAYVAULT - Arcade Game Vault");
        stage.setResizable(false); // Impedisce di ridimensionare la finestra per non rovinare il layout neon
        stage.show();
    }

    /**
     * Metodo fondamentale: Cambia la schermata attuale con una nuova.
     * Viene chiamato dai Controller. Es: App.setRoot("dashboard");
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carica il file FXML dalla cartella resources
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Sopprimiamo S106 per il menu testuale. 
    @SuppressWarnings("java:S106")
    public static void main(String[] args) {
        @SuppressWarnings("resource") 
        Scanner menuScanner = new Scanner(System.in);
        boolean appAttiva = true;

        // Ciclo continuo per permettere di tornare al menu dopo aver chiuso la CLI
        while (appAttiva) {
            System.out.println("========================================");
            System.out.println(" SELEZIONA MODALITA' DI AVVIO PLAYVAULT ");
            System.out.println("========================================");
            System.out.println("1. Avvia con Interfaccia Grafica (GUI - Finestre)");
            System.out.println("2. Avvia nel Terminale (CLI)");
            System.out.println("3. Spegni il sistema");
            System.out.print("Scelta (1, 2 o 3): ");

            String scelta = menuScanner.nextLine().trim();

            if (scelta.equals("2")) {
                System.out.println("\n[Avviando la modalità Terminale...]\n");
                
                // 1. Inizializziamo i Database (MySQL attuali)
                UtenteDAOMySQL utenteDAO = new UtenteDAOMySQL();
                VideogiocoDAOMySQL videogiocoDAO = new VideogiocoDAOMySQL();
                LibreriaDAOMySQL libreriaDAO = new LibreriaDAOMySQL();
                
                // 2. Passiamo i Database ai rispettivi Control (Dependency Injection)
                AutenticazioneControl authControl = new AutenticazioneControl(utenteDAO);
                RegistrazioneControl regControl = new RegistrazioneControl(utenteDAO);
                GestioneCatalogoControl catalogoControl = new GestioneCatalogoControl(videogiocoDAO);
                ClassificaControl classificaControl = new ClassificaControl(utenteDAO);
                AcquistoControl acquistoControl = new AcquistoControl(libreriaDAO);
                LibreriaControl libreriaControl = new LibreriaControl(videogiocoDAO); 
                RecensioneControl recensioneControl = new RecensioneControl(); 
                
                // 3. Facciamo partire la CLI iniettando l'arsenale al completo
                CliEngine motoreCLI = new CliEngine(
                    authControl, 
                    regControl, 
                    catalogoControl, 
                    libreriaControl, 
                    recensioneControl, 
                    acquistoControl, 
                    classificaControl
                );
                
                motoreCLI.avvia();

                // Quando esci dalla CLI, torna qui:
                System.out.println("\n[Ritorno al menu principale...]\n");

            } else if (scelta.equals("1")) {
                System.out.println("\n[Avviando la modalità Grafica...]\n");
                appAttiva = false; // Fermiamo il ciclo testuale perché JavaFX prende il controllo
                launch(args); 

            } else if (scelta.equals("3")) {
                System.out.println("\nSpegnimento di PlayVault in corso. Arrivederci!");
                appAttiva = false; // Rompe il ciclo while e il programma si chiude

            } else {
                System.out.println("\n[ERRORE] Scelta non valida. Inserisci 1, 2 o 3.\n");
            }
        }
    }
}