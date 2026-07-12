package com.progetto.graphiccontrollercli;

import java.util.List;
import java.util.Scanner;

import com.progetto.controllo.AcquistoControl;
import com.progetto.controllo.ClassificaControl;
import com.progetto.controllo.ImpostazioniControl;
import com.progetto.controllo.LibreriaControl;
import com.progetto.controllo.RecensioneControl;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.GiocoGiaPossedutoException;
import com.progetto.exceptions.RecensioneInvalidaException;
import com.progetto.exceptions.SaldoInsufficienteException;
import com.progetto.exceptions.SalvataggioFallitoException;
import com.progetto.utils.GestoreLingua;

@SuppressWarnings("java:S106") // S106: In una CLI UI, System.out è il canale di output corretto.
public class GiocatoreGraphicControllerCLI {

    private final LibreriaControl libreriaControl;
    private final RecensioneControl recensioneControl;
    private final AcquistoControl acquistoControl;
    private final ClassificaControl classificaControl;
    private final ImpostazioniControl impostazioniControl;

    private static final String MSG_ERRORE = "[ERRORE] ";
    private static final String MSG_OK = "[OK] ";
    
    // Costanti per risolvere SonarQube S1192 (Stringhe duplicate)
    private static final String MSG_INFO = "\n[INFO] ";
    private static final String DECORATORE_TITOLO = "===";

    public GiocatoreGraphicControllerCLI(LibreriaControl libreriaControl, RecensioneControl recensioneControl,
                                         AcquistoControl acquistoControl, ClassificaControl classificaControl) {
        this.libreriaControl = libreriaControl;
        this.recensioneControl = recensioneControl;
        this.acquistoControl = acquistoControl;
        this.classificaControl = classificaControl;
        this.impostazioniControl = new ImpostazioniControl(com.progetto.App.getUtenteDAO());
    }

    public void avviaMenuGiocatore(Scanner scanner) {
        boolean inMenu = true;
        while (inMenu) {
            Utente u = Sessione.getIstanza().getUtenteCorrente();
            if (u == null) return; 

            stampaMenuPrincipale(u);
            String scelta = scanner.nextLine().trim();
            inMenu = elaboraSceltaPrincipale(scanner, scelta, u);
        }
    }

    private void stampaMenuPrincipale(Utente u) {
        System.out.println("\n==============================");
        System.out.println("        " + GestoreLingua.getIstanza().get("cli.menu.title") + "        ");
        System.out.println("==============================");
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.catalog"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.library"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.details"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.buy"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.reviews"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.write_review"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.ranking"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.profile"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.settings"));
        System.out.println(GestoreLingua.getIstanza().get("cli.menu.logout"));
        
        System.out.print("PlayVault [" + u.getUsername() + " | CR: " + u.getCrediti() + "]> ");
    }

    // Risoluzione Complessità Cognitiva S3776
    private boolean elaboraSceltaPrincipale(Scanner scanner, String scelta, Utente u) {
        switch (scelta) {
            case "1": visualizzaCatalogo(); attendiInvio(scanner); return true;
            case "2": visualizzaLibreriaPersonale(); attendiInvio(scanner); return true;
            case "3": gestisciAzioneConId(scanner, "dettagli"); attendiInvio(scanner); return true;
            case "4": gestisciAzioneConId(scanner, "compra"); attendiInvio(scanner); return true;
            case "5": gestisciAzioneConId(scanner, "leggi_recensioni"); attendiInvio(scanner); return true;
            case "6": gestisciAzioneConId(scanner, "scrivi_recensione"); attendiInvio(scanner); return true;
            case "7": visualizzaClassifica(); attendiInvio(scanner); return true;
            case "8": visualizzaProfilo(); attendiInvio(scanner); return true;
            case "9": return gestisciImpostazioni(scanner); 
            case "0":
                System.out.println(GestoreLingua.getIstanza().get("cli.msg.logout") + " " + u.getUsername() + "!");
                Sessione.getIstanza().terminaSessione();
                return false;
            default: 
                System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("cli.error.invalid"));
                attendiInvio(scanner);
                return true;
        }
    }

    private void attendiInvio(Scanner scanner) {
        System.out.print("\n" + GestoreLingua.getIstanza().get("cli.prompt.pause"));
        scanner.nextLine(); 
    }

    private void gestisciAzioneConId(Scanner sc, String azione) {
        System.out.print(GestoreLingua.getIstanza().get("cli.prompt.id"));
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            switch (azione) {
                case "dettagli": visualizzaDettagliGioco(id); break;
                case "compra": eseguiAcquisto(id); break;
                case "leggi_recensioni": visualizzaRecensioniGioco(id); break;
                case "scrivi_recensione": scriviRecensione(sc, id); break;
                default: break; // Fix S131: Default in switch
            }
        } catch (NumberFormatException e) {
            System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("cli.error.invalid_id"));
        }
    }

    private boolean gestisciImpostazioni(Scanner scanner) {
        String utenteCorrente = Sessione.getIstanza().getUtenteCorrente().getUsername();
        boolean inImpostazioni = true;
        
        while (inImpostazioni) {
            System.out.println("\n--- IMPOSTAZIONI ---");
            System.out.println("1. " + GestoreLingua.getIstanza().get("menu.settings.language"));
            System.out.println("2. " + GestoreLingua.getIstanza().get("menu.settings.password"));
            System.out.println("3. " + GestoreLingua.getIstanza().get("menu.settings.delete"));
            System.out.println("4. " + GestoreLingua.getIstanza().get("menu.settings.back"));
            System.out.print("Scelta: ");
            
            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1": eseguiCambioLingua(); break;
                case "2": eseguiCambioPassword(scanner, utenteCorrente); break;
                case "3":
                    if (eseguiEliminaAccount(scanner, utenteCorrente)) return false; 
                    break;
                case "4": inImpostazioni = false; break;
                default: break; // Fix S131
            }
        }
        return true; 
    }

    // Estrazione metodi impostazioni per ridurre la Complessità (S3776)
    private void eseguiCambioLingua() {
        String langAttuale = GestoreLingua.getIstanza().getLocaleCorrente().getLanguage();
        String nuovaLang = "it".equalsIgnoreCase(langAttuale) ? "en" : "it";
        GestoreLingua.getIstanza().impostaLingua(nuovaLang);
        System.out.println(MSG_OK + GestoreLingua.getIstanza().get("settings.language.changed"));
    }

    private void eseguiCambioPassword(Scanner scanner, String utenteCorrente) {
        System.out.print(GestoreLingua.getIstanza().get("cli.settings.new_password") + " ");
        String nuovaPassword = scanner.nextLine().trim();
        if (impostazioniControl.cambiaPassword(utenteCorrente, nuovaPassword)) {
            System.out.println(MSG_OK + GestoreLingua.getIstanza().get("settings.password.changed"));
        } else {
            System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("settings.error.password"));
        }
    }

    private boolean eseguiEliminaAccount(Scanner scanner, String utenteCorrente) {
        System.out.print(GestoreLingua.getIstanza().get("settings.confirm.delete"));
        String conferma = scanner.nextLine().trim();
        if ("Y".equalsIgnoreCase(conferma)) {
            if (impostazioniControl.eliminaAccount(utenteCorrente)) {
                System.out.println(MSG_OK + GestoreLingua.getIstanza().get("settings.account.deleted"));
                Sessione.getIstanza().terminaSessione();
                return true; 
            } else {
                System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("settings.error.delete"));
            }
        }
        return false;
    }

    private void visualizzaCatalogo() {
        List<Videogioco> catalogo = libreriaControl.ottieniCatalogoCompleto();
        if (catalogo == null || catalogo.isEmpty()) {
            System.out.println(MSG_INFO + GestoreLingua.getIstanza().get("cli.catalog.empty") + "\n");
            return;
        }
        stampaTabellaGiochi(GestoreLingua.getIstanza().get("cli.catalog.title"), catalogo);
    }

    private void visualizzaLibreriaPersonale() {
        String utenteCorrente = Sessione.getIstanza().getUtenteCorrente().getUsername();
        List<Videogioco> mieiGiochi = libreriaControl.ottieniMieiGiochi(utenteCorrente);
        if (mieiGiochi == null || mieiGiochi.isEmpty()) {
            System.out.println(MSG_INFO + GestoreLingua.getIstanza().get("cli.library.empty") + "\n");
            return;
        }
        stampaTabellaGiochi(GestoreLingua.getIstanza().get("cli.library.title"), mieiGiochi);
    }

    private void stampaTabellaGiochi(String titoloHeader, List<Videogioco> lista) {
        System.out.println("\n" + DECORATORE_TITOLO + " " + titoloHeader + " " + DECORATORE_TITOLO);
        String headerTitle = GestoreLingua.getIstanza().get("cli.table.title");
        String headerGenre = GestoreLingua.getIstanza().get("cli.table.genre");
        String headerYear = GestoreLingua.getIstanza().get("cli.table.year");
        
        System.out.println(String.format("%-5s | %-30s | %-15s | %-5s", "ID", headerTitle, headerGenre, headerYear));
        System.out.println("--------+---------------------------------+-----------------+------");
        for (Videogioco gioco : lista) {
            System.out.println(String.format("%-5d | %-30s | %-15s | %-5s", 
                gioco.getId(), truncate(gioco.getTitolo(), 28), truncate(gioco.getGenere(), 13), gioco.getAnnoUscita()));
        }
        System.out.println();
    }

    private void visualizzaDettagliGioco(int idGioco) {
        List<Videogioco> catalogo = libreriaControl.ottieniCatalogoCompleto();
        Videogioco gioco = catalogo.stream().filter(g -> g.getId() == idGioco).findFirst().orElse(null);
        if (gioco == null) {
            System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("cli.error.game_not_found") + "\n");
            return;
        }
        System.out.println("\n" + DECORATORE_TITOLO + " " + GestoreLingua.getIstanza().get("cli.details.header") + " " + DECORATORE_TITOLO);
        System.out.println("ID: " + gioco.getId());
        System.out.println(GestoreLingua.getIstanza().get("cli.table.title") + ": " + gioco.getTitolo());
        System.out.println(GestoreLingua.getIstanza().get("cli.table.genre") + ": " + gioco.getGenere());
        System.out.println(GestoreLingua.getIstanza().get("cli.table.year") + ": " + gioco.getAnnoUscita());
        System.out.println(GestoreLingua.getIstanza().get("cli.details.dev") + ": " + gioco.getSviluppatore());
        System.out.println(GestoreLingua.getIstanza().get("cli.details.desc") + ": " + gioco.getDescrizioneLocale());
        System.out.println(GestoreLingua.getIstanza().get("cli.details.price") + "\n");
    }

    private void eseguiAcquisto(int idGioco) {
        List<Videogioco> catalogo = libreriaControl.ottieniCatalogoCompleto();
        Videogioco gioco = catalogo.stream().filter(g -> g.getId() == idGioco).findFirst().orElse(null);
        if (gioco == null) {
            System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("cli.error.game_not_found") + "\n");
            return;
        }
        try {
            acquistoControl.tentaAcquisto(gioco);
            System.out.println(MSG_OK + GestoreLingua.getIstanza().get("cli.buy.success"));
        } catch (GiocoGiaPossedutoException | SaldoInsufficienteException | SalvataggioFallitoException e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }

    private void visualizzaRecensioniGioco(int idGioco) {
        List<Recensione> recensioni = recensioneControl.ottieniRecensioniGioco(idGioco);
        if (recensioni == null || recensioni.isEmpty()) {
            System.out.println(MSG_INFO + GestoreLingua.getIstanza().get("cli.reviews.empty") + "\n");
            return;
        }
        System.out.println("\n" + DECORATORE_TITOLO + " " + GestoreLingua.getIstanza().get("cli.reviews.header") + " " + DECORATORE_TITOLO);
        for (Recensione r : recensioni) {
            System.out.println(String.format("[%s] %d/5 - %s", r.getUsername(), r.getVoto(), r.getCommento()));
        }
    }

    private void scriviRecensione(Scanner scanner, int idGioco) {
        System.out.println("\n--- " + GestoreLingua.getIstanza().get("cli.review.write.header") + " ---");
        System.out.print(GestoreLingua.getIstanza().get("cli.review.write.rating") + " ");
        try {
            int voto = Integer.parseInt(scanner.nextLine().trim());
            if (voto < 1 || voto > 5) {
                System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("cli.review.error.rating_range"));
                return;
            }
            System.out.print(GestoreLingua.getIstanza().get("cli.review.write.text") + " ");
            String testo = scanner.nextLine().trim();
            
            String utenteCorrente = Sessione.getIstanza().getUtenteCorrente().getUsername();
            Recensione nuovaRecensione = new Recensione(utenteCorrente, idGioco, voto, testo);
            
            recensioneControl.elaboraRecensione(nuovaRecensione);
            System.out.println(MSG_OK + GestoreLingua.getIstanza().get("cli.review.write.success"));
            
        } catch (NumberFormatException e) {
            System.out.println(MSG_ERRORE + GestoreLingua.getIstanza().get("cli.review.error.invalid_rating"));
        } catch (RecensioneInvalidaException | SalvataggioFallitoException e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }

    private void visualizzaClassifica() {
        System.out.println("\n" + DECORATORE_TITOLO + " " + GestoreLingua.getIstanza().get("cli.ranking.header") + " " + DECORATORE_TITOLO);
        List<Utente> listaDalFile = classificaControl.ottieniTopPlayers();
        
        if (listaDalFile == null || listaDalFile.isEmpty()) {
            System.out.println(GestoreLingua.getIstanza().get("cli.ranking.empty"));
            return;
        }
        
        List<Utente> topPlayers = new java.util.ArrayList<>(listaDalFile);
        topPlayers.sort((u1, u2) -> Integer.compare(u2.getCrediti(), u1.getCrediti()));

        int rank = 1;
        for (Utente u : topPlayers) {
            String medaglia = switch (rank) {
                case 1 -> "🥇"; case 2 -> "🥈"; case 3 -> "🥉"; default -> "👤";
            };
            int livelloCalcolato = (u.getCrediti() / 30) + 1;
            String lblLivello = GestoreLingua.getIstanza().get("cli.ranking.level");
            System.out.println(String.format("%s %d° | %-15s | %s: %-3d | %-4d CR", 
                medaglia, rank, u.getUsername().toUpperCase(), lblLivello, livelloCalcolato, u.getCrediti()));
            rank++;
        }
    }

    private void visualizzaProfilo() {
        Utente utente = Sessione.getIstanza().getUtenteCorrente();
        System.out.println("\n" + DECORATORE_TITOLO + " " + GestoreLingua.getIstanza().get("cli.profile.header") + " " + DECORATORE_TITOLO);
        System.out.println(GestoreLingua.getIstanza().get("cli.profile.username") + ": " + utente.getUsername());
        System.out.println(GestoreLingua.getIstanza().get("cli.profile.role") + ": " + utente.getRuolo());
        System.out.println(GestoreLingua.getIstanza().get("cli.profile.credits") + ": " + utente.getCrediti());
    }

    private String truncate(String str, int length) {
        if (str != null && str.length() > length) {
            return str.substring(0, length - 3) + "...";
        }
        return str != null ? str : "";
    }
}