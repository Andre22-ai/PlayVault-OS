package com.progetto.boundary;

import java.util.List;
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
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.CredenzialiErrateException;
import com.progetto.exceptions.GiocoGiaPossedutoException;
import com.progetto.exceptions.RecensioneInvalidaException;
import com.progetto.exceptions.SaldoInsufficienteException;
import com.progetto.exceptions.SalvataggioFallitoException;
import com.progetto.exceptions.UtenteGiaEsistenteException;

/**
 * Motore della CLI per PLAYVAULT.
 * Compatibile con Java 11 (senza switch expressions).
 */
@SuppressWarnings("java:S106")
public class CliEngine {

    private static final Logger LOGGER = Logger.getLogger(CliEngine.class.getName());
    
    // Costanti per risolvere SonarCloud (java:S1192)
    private static final String PROMPT_USERNAME = "Username: ";
    private static final String ERR_ID_INVALIDO = "[ERRORE] ID non valido.";

    private boolean inEsecuzione;
    private String utenteCorrente;

    private final AutenticazioneControl authControl;
    private final RegistrazioneControl regControl;
    private final GestioneCatalogoControl catalogoControl;
    private final LibreriaControl libreriaControl;
    private final RecensioneControl recensioneControl;
    private final AcquistoControl acquistoControl;
    private final ClassificaControl classificaControl; 

    public CliEngine(AutenticazioneControl authControl, RegistrazioneControl regControl, 
                     GestioneCatalogoControl catalogoControl, LibreriaControl libreriaControl,
                     RecensioneControl recensioneControl, AcquistoControl acquistoControl,
                     ClassificaControl classificaControl) {
        this.authControl = authControl;
        this.regControl = regControl;
        this.catalogoControl = catalogoControl;
        this.libreriaControl = libreriaControl;
        this.recensioneControl = recensioneControl;
        this.acquistoControl = acquistoControl;
        this.classificaControl = classificaControl;
        this.inEsecuzione = true;
        this.utenteCorrente = null;
    }

    public void avvia() {
        System.out.println("\n========================================");
        System.out.println("       BENVENUTO IN PLAYVAULT CLI       ");
        System.out.println("========================================");
        
        // MODIFICA 1: Stampiamo subito il menu all'avvio!
        stampaAiutoGuest();

        try (Scanner scanner = new Scanner(System.in)) {
            while (inEsecuzione) {
                if (utenteCorrente == null) {
                    System.out.print("PlayVault (Ospite)> ");
                } else {
                    String ruolo = getRuoloUtente();
                    int crediti = getCreditiUtente();
                    System.out.print("PlayVault [" + utenteCorrente + " | " + ruolo + " | Crediti: " + crediti + "]> ");
                }

                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }

                String[] token = input.split("\\s+");
                String comando = token[0].toLowerCase();

                if (utenteCorrente == null) {
                    gestisciComandiGuest(comando, token, scanner);
                } else {
                    gestisciComandiLoggato(comando, token, scanner);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore fatale nel motore CLI", e);
        }
    }

    private void gestisciComandiGuest(String comando, String[] argomenti, Scanner scanner) {
        switch (comando) {
            case "aiuto":
                stampaAiutoGuest();
                break;

            case "login":
                if (argomenti.length < 3) {
                    System.out.print(PROMPT_USERNAME);
                    String username = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();
                    eseguiLogin(username, password);
                } else {
                    eseguiLogin(argomenti[1], argomenti[2]);
                }
                break;

            case "registrati":
                eseguiRegistrazione(scanner);
                break;

            case "esci":
                System.out.println("Chiusura in corso... Arrivederci!");
                this.inEsecuzione = false;
                break;

            default:
                System.out.println("Comando sconosciuto. Digita 'aiuto'.");
        }
    }

    private void eseguiLogin(String username, String password) {
        System.out.println("Verifica credenziali in corso...");
        try {
            authControl.eseguiLogin(username, password);
            this.utenteCorrente = username;
            System.out.println("[OK] Accesso effettuato! Benvenuto " + utenteCorrente.toUpperCase());
            stampaAiutoUtente();
        } catch (CredenzialiErrateException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }
    }

    private void eseguiRegistrazione(Scanner scanner) {
        System.out.println("\n--- CREAZIONE NUOVO ACCOUNT ---");
        System.out.print(PROMPT_USERNAME);
        String user = scanner.nextLine().trim();
        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();
        System.out.print("Conferma Password: ");
        String confPass = scanner.nextLine().trim();

        try {
            regControl.registraNuovoUtente(user, pass, confPass);
            System.out.println("[OK] Account creato con successo! Ora puoi fare il login.");
        } catch (UtenteGiaEsistenteException | SalvataggioFallitoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }
    }

    private void gestisciComandiLoggato(String comando, String[] argomenti, Scanner scanner) {
        switch (comando) {
            case "aiuto":
                stampaAiutoUtente();
                break;
            case "catalogo":
                visualizzaCatalogo();
                break;
            case "libreria":
                visualizzaLibreriaPersonale();
                break;
            case "compra":
                gestisciAcquistoComando(argomenti, scanner);
                break;
            case "dettagli":
                gestisciDettagliComando(argomenti, scanner);
                break;
            case "recensioni":
                gestisciRecensioniComando(argomenti, scanner);
                break;
            case "scrivi_recensione":
                gestisciScritturaRecensioneComando(argomenti, scanner);
                break;
            case "classifica":
                visualizzaClassifica();
                break;
            case "aggiungi":
                if (isUserAdmin()) {
                    aggiungiNuovoGioco(scanner);
                } else {
                    System.out.println("[ERRORE] Solo gli amministratori possono aggiungere giochi.");
                }
                break;
            case "profilo":
                visualizzaProfilo();
                break;
            case "logout":
                System.out.println("Disconnessione in corso... Arrivederci " + utenteCorrente + "!");
                this.utenteCorrente = null;
                Sessione.getIstanza().terminaSessione();
                // MODIFICA 3: Appena esci, ti ricorda i comandi per rientrare!
                stampaAiutoGuest();
                break;
            case "esci":
                System.out.println("Chiusura in corso... Arrivederci!");
                this.inEsecuzione = false;
                break;
            default:
                System.out.println("Comando non valido. Digita 'aiuto'.");
        }
    }

    // NUOVO METODO AGGIUNTO: Per stampare il menu Ospite
    private void stampaAiutoGuest() {
        System.out.println("\n--- COMANDI OSPITE ---");
        System.out.println("login      -> Accedi al sistema");
        System.out.println("registrati -> Crea un nuovo account");
        System.out.println("esci       -> Chiudi l'applicazione\n");
    }

    private void stampaAiutoUtente() {
        System.out.println("\n--- COMANDI DISPONIBILI ---");
        System.out.println("catalogo              -> Visualizza tutti i giochi disponibili");
        System.out.println("libreria              -> Visualizza i tuoi giochi acquistati");
        System.out.println("dettagli <id>         -> Mostra i dettagli di un gioco");
        System.out.println("compra <id>           -> Acquista un gioco (costo: 15 crediti)");
        System.out.println("recensioni <id>       -> Leggi le recensioni di un gioco");
        System.out.println("scrivi_recensione <id> -> Scrivi una recensione (+15 crediti)");
        System.out.println("classifica            -> Mostra la Hall of Fame dei giocatori");
        System.out.println("profilo               -> Visualizza il tuo profilo");
        if (isUserAdmin()) {
            System.out.println("aggiungi              -> Aggiungi un nuovo gioco al catalogo (Admin)");
        }
        System.out.println("logout                -> Disconnettiti");
        System.out.println("esci                  -> Chiudi l'applicazione\n");
    }

    private void visualizzaCatalogo() {
        List<Videogioco> catalogo = libreriaControl.ottieniCatalogoCompleto();
        if (catalogo == null || catalogo.isEmpty()) {
            System.out.println("\n[INFO] Nessun gioco disponibile nel catalogo.\n");
            return;
        }
        stampaTabellaGiochi("CATALOGO GIOCHI", catalogo);
    }

    private void visualizzaLibreriaPersonale() {
        List<Videogioco> mieiGiochi = libreriaControl.ottieniMieiGiochi(utenteCorrente);
        if (mieiGiochi == null || mieiGiochi.isEmpty()) {
            System.out.println("\n[INFO] La tua libreria è vuota. Compra alcuni giochi!\n");
            return;
        }
        stampaTabellaGiochi("LA MIA LIBRERIA", mieiGiochi);
    }

    private void stampaTabellaGiochi(String titoloHeader, List<Videogioco> lista) {
        System.out.println("\n=== " + titoloHeader + " ===");
        System.out.println(String.format("%-5s | %-30s | %-15s | %-5s", "ID", "Titolo", "Genere", "Anno"));
        System.out.println("--------+---------------------------------+-----------------+------");
        for (Videogioco gioco : lista) {
            System.out.println(String.format("%-5d | %-30s | %-15s | %-5d", 
                gioco.getId(), truncate(gioco.getTitolo(), 28), truncate(gioco.getGenere(), 13), gioco.getAnnoUscita()));
        }
        System.out.println();
    }

    private void visualizzaDettagliGioco(int idGioco) {
        List<Videogioco> catalogo = libreriaControl.ottieniCatalogoCompleto();
        if (catalogo == null) {
            System.out.println("[ERRORE] Impossibile recuperare il catalogo.\n");
            return;
        }
        Videogioco gioco = catalogo.stream().filter(g -> g.getId() == idGioco).findFirst().orElse(null);
        if (gioco == null) {
            System.out.println("[ERRORE] Gioco non trovato.\n");
            return;
        }
        System.out.println("\n=== DETTAGLI GIOCO ===");
        System.out.println("ID: " + gioco.getId());
        System.out.println("Titolo: " + gioco.getTitolo());
        System.out.println("Genere: " + gioco.getGenere());
        System.out.println("Anno: " + gioco.getAnnoUscita());
        System.out.println("Sviluppatore: " + gioco.getSviluppatore());
        System.out.println("Descrizione: " + gioco.getDescrizione());
        System.out.println("Prezzo: 15 crediti\n");
    }

    private void visualizzaRecensioniGioco(int idGioco) {
        List<Recensione> recensioni = recensioneControl.ottieniRecensioniGioco(idGioco);
        if (recensioni == null || recensioni.isEmpty()) {
            System.out.println("\n[INFO] Nessuna recensione per questo gioco.\n");
            return;
        }
        System.out.println("\n=== RECENSIONI ===");
        for (Recensione r : recensioni) {
            System.out.println(String.format("[%s] %d/5 - %s", r.getUsername(), r.getVoto(), r.getCommento()));
        }
        System.out.println();
    }

    private void visualizzaClassifica() {
        System.out.println("\n=== HALL OF FAME ===");
        List<Utente> topPlayers = classificaControl.ottieniTopPlayers();
        if (topPlayers == null || topPlayers.isEmpty()) {
            System.out.println("Nessun utente in classifica.");
            return;
        }
        int rank = 1;
        for (Utente u : topPlayers) {
            String medaglia = switch (rank) {
                case 1 -> "🥇";
                case 2 -> "🥈";
                case 3 -> "🥉";
                default -> "👤";
            };
            System.out.println(String.format("%s %d° | %-15s | Livello: %-3d | %-4d CR", 
                medaglia, rank, u.getUsername().toUpperCase(), (u.getCrediti() / 2), u.getCrediti()));
            rank++;
        }
        System.out.println();
    }

    private void scriviRecensione(Scanner scanner, int idGioco) {
        System.out.println("\n--- SCRIVI RECENSIONE ---");
        System.out.print("Voto (1-5): ");
        try {
            int voto = Integer.parseInt(scanner.nextLine().trim());
            if (voto < 1 || voto > 5) {
                System.out.println("[ERRORE] Il voto deve essere tra 1 e 5.");
                return;
            }
            System.out.print("Testo della recensione: ");
            String testo = scanner.nextLine().trim();

            Recensione nuovaRecensione = new Recensione(utenteCorrente, idGioco, voto, testo);
            try {
                recensioneControl.elaboraRecensione(nuovaRecensione);
                System.out.println("[OK] Recensione salvata! Hai guadagnato 15 crediti!");
            } catch (RecensioneInvalidaException | SalvataggioFallitoException e) {
                System.out.println("[ERRORE] " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERRORE] Voto non valido.");
        }
    }

    private void eseguiAcquisto(int idGioco) {
        List<Videogioco> catalogo = libreriaControl.ottieniCatalogoCompleto();
        if (catalogo == null) {
            System.out.println("[ERRORE] Impossibile recuperare il catalogo.\n");
            return;
        }
        Videogioco gioco = catalogo.stream().filter(g -> g.getId() == idGioco).findFirst().orElse(null);
        if (gioco == null) {
            System.out.println("[ERRORE] Gioco non trovato.\n");
            return;
        }

        try {
            acquistoControl.tentaAcquisto(gioco);
            System.out.println("[OK] Acquisto completato! Il gioco è stato aggiunto alla tua libreria.");
        } catch (GiocoGiaPossedutoException | SaldoInsufficienteException | SalvataggioFallitoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }
    }

    private void aggiungiNuovoGioco(Scanner scanner) {
        System.out.println("\n--- INSERIMENTO NUOVO GIOCO ---");
        System.out.print("Titolo: ");
        String titolo = scanner.nextLine().trim();
        System.out.print("Genere: ");
        String genere = scanner.nextLine().trim();
        System.out.print("Anno di uscita (es. 2022): ");
        String anno = scanner.nextLine().trim();
        System.out.print("Sviluppatore: ");
        String dev = scanner.nextLine().trim();
        System.out.print("Breve descrizione: ");
        String desc = scanner.nextLine().trim();

        try {
            catalogoControl.aggiungiNuovoGioco(titolo, genere, anno, dev, desc);
            System.out.println("[OK] Gioco aggiunto con successo al database!");
        } catch (IllegalArgumentException | SalvataggioFallitoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }
    }

    private void visualizzaProfilo() {
        Utente utente = Sessione.getIstanza().getUtenteCorrente();
        if (utente == null) {
            System.out.println("[ERRORE] Impossibile recuperare i dati dell'utente.\n");
            return;
        }
        System.out.println("\n=== IL MIO PROFILO ===");
        System.out.println(PROMPT_USERNAME + utente.getUsername());
        System.out.println("Ruolo: " + utente.getRuolo());
        System.out.println("Crediti: " + utente.getCrediti());
        System.out.println();
    }

    private void gestisciAcquistoComando(String[] args, Scanner sc) {
        try {
            int id = args.length < 2 ? Integer.parseInt(sc.nextLine().trim()) : Integer.parseInt(args[1]);
            eseguiAcquisto(id);
        } catch (NumberFormatException e) { System.out.println(ERR_ID_INVALIDO); }
    }

    private void gestisciDettagliComando(String[] args, Scanner sc) {
        try {
            int id = args.length < 2 ? Integer.parseInt(sc.nextLine().trim()) : Integer.parseInt(args[1]);
            visualizzaDettagliGioco(id);
        } catch (NumberFormatException e) { System.out.println(ERR_ID_INVALIDO); }
    }

    private void gestisciRecensioniComando(String[] args, Scanner sc) {
        try {
            int id = args.length < 2 ? Integer.parseInt(sc.nextLine().trim()) : Integer.parseInt(args[1]);
            visualizzaRecensioniGioco(id);
        } catch (NumberFormatException e) { System.out.println(ERR_ID_INVALIDO); }
    }

    private void gestisciScritturaRecensioneComando(String[] args, Scanner sc) {
        try {
            int id = args.length < 2 ? Integer.parseInt(sc.nextLine().trim()) : Integer.parseInt(args[1]);
            scriviRecensione(sc, id);
        } catch (NumberFormatException e) { System.out.println(ERR_ID_INVALIDO); }
    }

    private int getCreditiUtente() {
        Utente u = Sessione.getIstanza().getUtenteCorrente();
        return u != null ? u.getCrediti() : 0;
    }

    private String getRuoloUtente() {
        Utente u = Sessione.getIstanza().getUtenteCorrente();
        return u != null ? u.getRuolo() : "UNKNOWN";
    }

    private boolean isUserAdmin() {
        return "ADMIN".equalsIgnoreCase(getRuoloUtente());
    }

    private String truncate(String str, int length) {
        if (str != null && str.length() > length) {
            return str.substring(0, length - 3) + "...";
        }
        return str != null ? str : "";
    }
}