package com.progetto.graphiccontrollercli;

import java.util.Scanner;

import com.progetto.controllo.GestioneCatalogoControl;
import com.progetto.exceptions.SalvataggioFallitoException;

@SuppressWarnings("java:S106") // S106: Nelle CLI, System.out è l'output standard per l'interfaccia utente
public class AdminGraphicControllerCLI {

    private final GestioneCatalogoControl catalogoControl;

    // Costanti per risolvere SonarQube S1192 (Stringhe duplicate)
    private static final String MSG_ERRORE = "[ERRORE] ";
    private static final String MSG_OK = "[OK] ";

    public AdminGraphicControllerCLI(GestioneCatalogoControl catalogoControl) {
        this.catalogoControl = catalogoControl;
    }

    /**
     * Questo metodo è l'equivalente del "caricamento della schermata" in JavaFX.
     * Prende il controllo finché l'admin non decide di uscire da questo menu.
     */
    public void avviaMenuAdmin(Scanner scanner) {
        boolean inMenu = true;
        
        while (inMenu) {
            System.out.println("\n=== OVERRIDE ZONE // ADMIN DASHBOARD ===");
            System.out.println("1. Aggiungi nuovo gioco");
            System.out.println("2. Modifica gioco esistente");
            System.out.println("3. Nascondi/Rimuovi gioco");
            System.out.println("4. Torna al menu principale");
            System.out.print("Scelta Admin: ");
            
            String scelta = scanner.nextLine().trim();
            
            switch (scelta) {
                case "1":
                    aggiungiGioco(scanner);
                    break;
                case "2":
                    modificaGioco(scanner);
                    break;
                case "3":
                    rimuoviGioco(scanner);
                    break;
                case "4":
                    inMenu = false; // Esce da questo controller e torna al CliEngine
                    break;
                default:
                    System.out.println(MSG_ERRORE + "Scelta non valida.");
            }
        }
    }

    private void aggiungiGioco(Scanner scanner) {
        System.out.println("\n--- INSERIMENTO NUOVO GIOCO ---");
        System.out.print("Titolo: ");
        String titolo = scanner.nextLine().trim();
        System.out.print("Genere: ");
        String genere = scanner.nextLine().trim();
        System.out.print("Anno di uscita (es. 2022): ");
        String anno = scanner.nextLine().trim();
        System.out.print("Sviluppatore: ");
        String dev = scanner.nextLine().trim();
        System.out.print("Descrizione (IT): ");
        String descIt = scanner.nextLine().trim();
        System.out.print("Descrizione (EN): ");
        String descEn = scanner.nextLine().trim();
        
        try {
            catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, descIt, descEn);
            System.out.println(MSG_OK + "Gioco aggiunto con successo al database!");
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }

    private void modificaGioco(Scanner scanner) {
        System.out.println("\n--- MODIFICA GIOCO ESISTENTE ---");
        System.out.print("Inserisci l'ID del gioco da modificare: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Nuovo Titolo: ");
            String titolo = scanner.nextLine().trim();
            System.out.print("Nuovo Genere: ");
            String genere = scanner.nextLine().trim();
            System.out.print("Nuovo Anno di uscita: ");
            String anno = scanner.nextLine().trim();
            System.out.print("Nuovo Sviluppatore: ");
            String dev = scanner.nextLine().trim();
            System.out.print("Nuova Descrizione (IT): ");
            String descIt = scanner.nextLine().trim();
            System.out.print("Nuova Descrizione (EN): ");
            String descEn = scanner.nextLine().trim();
            
            catalogoControl.modificaGiocoEsistente(id, titolo, genere, anno, dev, descIt, descEn);
            System.out.println(MSG_OK + "Gioco modificato con successo!");
        } catch (NumberFormatException e) {
            System.out.println(MSG_ERRORE + "ID non valido. Deve essere un numero.");
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }

    private void rimuoviGioco(Scanner scanner) {
        System.out.println("\n--- RIMOZIONE GIOCO ---");
        System.out.print("Inserisci l'ID del gioco da nascondere: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            catalogoControl.rimuoviGioco(id);
            System.out.println(MSG_OK + "Gioco rimosso dal catalogo con successo!");
        } catch (NumberFormatException e) {
            System.out.println(MSG_ERRORE + "ID non valido.");
        } catch (Exception e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }
}