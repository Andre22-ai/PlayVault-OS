package com.progetto;

import java.util.Scanner;

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
import com.progetto.database.VideogiocoDAO;
import com.progetto.database.VideogiocoDAOMemory;
import com.progetto.database.VideogiocoDAOMySQL;
import com.progetto.graphiccontrollercli.CliEngine;

/**
 * PLAYVAULT Entry Point per il Terminale (CLI)
 */
public class AppCLI {

    private static final String MENU_SEPARATOR = "========================================";

    @SuppressWarnings("java:S106")
    public static void main(String[] args) {
        
        // 1. IL TRY-WITH-RESOURCES: Chiuderà lo scanner da solo alla fine del blocco!
        try (Scanner scanner = new Scanner(System.in)) {
            
            // Variabili per i Database polimorfi
            UtenteDAO utenteDAOScelto;
            VideogiocoDAO videogiocoDAOScelto;
            LibreriaDAO libreriaDAOScelto;
            RecensioneDAO recensioneDAOScelto; 

            // ==========================================================
            // FASE 1: SCELTA DEL DATABASE
            // ==========================================================
            System.out.println(MENU_SEPARATOR);
            System.out.println("      PLAYVAULT - TERMINAL EDITION      ");
            System.out.println("      CONFIGURAZIONE SALVATAGGIO        ");
            System.out.println(MENU_SEPARATOR);
            System.out.println("1. Database MySQL (Full-Version DBMS)");
            System.out.println("2. File CSV (Full-Version File System)");
            System.out.println("3. Memoria RAM (Demo-Version In-Memory)");
            System.out.print("Scegli la persistenza (1, 2 o 3): ");
            
            String sceltaDB = scanner.nextLine().trim();
            
            // 2. LO SWITCH MODERNO: Addio catena di if/else!
            switch (sceltaDB) {
                case "2" -> {
                    utenteDAOScelto = new UtenteDAOcsv();
                    videogiocoDAOScelto = new VideogiocoDAOMemory();
                    libreriaDAOScelto = new LibreriaDAOcsv();
                    recensioneDAOScelto = new RecensioneDAOcsv();
                    System.out.println("[INFO] Selezionato File System (CSV).\n");
                }
                case "3" -> {
                    utenteDAOScelto = new UtenteDAOMemory();
                    videogiocoDAOScelto = new VideogiocoDAOMemory(); 
                    libreriaDAOScelto = new LibreriaDAOMemory(); 
                    recensioneDAOScelto = new RecensioneDAOMemory();
                    System.out.println("[INFO] Selezionata RAM per tutto il sistema (Demo).\n");
                }
                default -> {
                    // Default cattura sia "1" che eventuali tasti sbagliati premuti dall'utente
                    utenteDAOScelto = new UtenteDAOMySQL();
                    videogiocoDAOScelto = new VideogiocoDAOMySQL();
                    libreriaDAOScelto = new LibreriaDAOMySQL();
                    recensioneDAOScelto = new RecensioneDAOMySQL(); 
                    System.out.println("[INFO] Selezionato MySQL.\n");
                }
            }

            App.setUtenteDAO(utenteDAOScelto);
            App.setVideogiocoDAO(videogiocoDAOScelto);
            App.setLibreriaDAO(libreriaDAOScelto);
            App.setRecensioneDAO(recensioneDAOScelto);

            // ==========================================================
            // FASE 2: INIZIALIZZAZIONE DELLA CLI E DEI CONTROLLER
            // ==========================================================
            System.out.println("[Avviando la modalità Terminale...]\n");
            
            AutenticazioneControl authControl = new AutenticazioneControl(utenteDAOScelto);
            RegistrazioneControl regControl = new RegistrazioneControl(utenteDAOScelto);
            ClassificaControl classificaControl = new ClassificaControl(utenteDAOScelto);
            
            GestioneCatalogoControl catalogoControl = new GestioneCatalogoControl(videogiocoDAOScelto);
            LibreriaControl libreriaControl = new LibreriaControl(videogiocoDAOScelto, libreriaDAOScelto); 
            AcquistoControl acquistoControl = new AcquistoControl(libreriaDAOScelto);
            
            RecensioneControl recensioneControl = new RecensioneControl(recensioneDAOScelto, utenteDAOScelto); 
            
            CliEngine motoreCLI = new CliEngine(
                authControl, regControl, catalogoControl, 
                libreriaControl, recensioneControl, acquistoControl, classificaControl
            );
            
            motoreCLI.avvia();

            System.out.println("\n[Spegnimento di PlayVault in corso. Arrivederci!]");
            
            // IL VECCHIO scanner.close() È STATO RIMOSSO.
        }
    }
}