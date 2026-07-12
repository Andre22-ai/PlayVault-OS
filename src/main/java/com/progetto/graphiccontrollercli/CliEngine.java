package com.progetto.graphiccontrollercli;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.controllo.AcquistoControl;
import com.progetto.controllo.AutenticazioneControl;
import com.progetto.controllo.ClassificaControl;
import com.progetto.controllo.GestioneCatalogoControl;
import com.progetto.controllo.LibreriaControl;
import com.progetto.controllo.RecensioneControl;
import com.progetto.controllo.RegistrazioneControl;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

public class CliEngine {
    private static final Logger LOGGER = Logger.getLogger(CliEngine.class.getName());

    private final LoginGraphicControllerCLI loginController;
    private final GiocatoreGraphicControllerCLI giocatoreController;
    private final AdminGraphicControllerCLI adminController;

    public CliEngine(AutenticazioneControl authControl, RegistrazioneControl regControl, 
                     GestioneCatalogoControl catalogoControl, LibreriaControl libreriaControl,
                     RecensioneControl recensioneControl, AcquistoControl acquistoControl,
                     ClassificaControl classificaControl) {
        
        this.loginController = new LoginGraphicControllerCLI(authControl, regControl);
        this.giocatoreController = new GiocatoreGraphicControllerCLI(libreriaControl, recensioneControl, acquistoControl, classificaControl);
        this.adminController = new AdminGraphicControllerCLI(catalogoControl);
    }

    public void avvia() {
        System.out.println("\n========================================");
        System.out.println("       BENVENUTO IN PLAYVAULT CLI       ");
        System.out.println("========================================");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                Utente utenteCorrente = Sessione.getIstanza().getUtenteCorrente();

                if (utenteCorrente == null) {
                    // Cede il controllo alla schermata di Login/Registrazione
                    loginController.avviaMenuAccesso(scanner);
                    
                } else if ("ADMIN".equalsIgnoreCase(utenteCorrente.getRuolo())) {
                    // Cede il controllo alla Dashboard di Amministrazione
                    adminController.avviaMenuAdmin(scanner);
                    
                } else {
                    // Cede il controllo al Menu principale del Giocatore
                    giocatoreController.avviaMenuGiocatore(scanner);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore fatale nel motore CLI", e);
        }
    }
}