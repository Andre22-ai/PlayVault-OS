package com.progetto.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.entita.Utente;

/**
 * Versione File System del DAO.
 * Salva i dati permanentemente in un file di testo (utenti.csv).
 * Formato del file: username,password,ruolo,crediti
 */
public class UtenteDAOcsv implements UtenteDAO {

    private static final String NOME_FILE = "utenti.csv";
    private static final Logger LOGGER = Logger.getLogger(UtenteDAOcsv.class.getName());

    public UtenteDAOcsv() {
        File file = new File(NOME_FILE);
        if (!file.exists()) {
            try {
                boolean creato = file.createNewFile();
                if (creato) {
                    Utente admin = new Utente("admin", "admin");
                    admin.setRuolo("ADMIN");
                    // Usiamo setCrediti per essere precisi al 100%
                    admin.setCrediti(100);
                    salvaUtente(admin); 
                    LOGGER.info("[CSV] Creato nuovo file utenti.csv con account admin default.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "[CSV] Impossibile creare il file utenti.csv", e);
            }
        }
    }

    private List<Utente> leggiTuttiUtenti() {
        List<Utente> lista = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(NOME_FILE))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                if (riga.trim().isEmpty()) continue;
                
                String[] dati = riga.split(",");
                if (dati.length >= 4) {
                    Utente u = new Utente(dati[0], dati[1]);
                    u.setRuolo(dati[2]);
                    
                    // FIX 1: Impostiamo forzatamente il valore esatto scritto nel file.
                    // Bypassiamo qualsiasi calcolo strano di "aggiungiCrediti".
                    u.setCrediti(Integer.parseInt(dati[3]));
                    
                    lista.add(u);
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante la lettura del file", e);
        }
        
        return lista;
    }

    private void sovrascriviFile(List<Utente> utenti) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(NOME_FILE))) {
            for (Utente u : utenti) {
                pw.println(u.getUsername() + "," + u.getPassword() + "," + u.getRuolo() + "," + u.getCrediti());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante la scrittura del file", e);
        }
    }

    @Override
    public Utente autentica(String username, String password) {
        List<Utente> tutti = leggiTuttiUtenti();
        return tutti.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean salvaUtente(Utente utente) {
        List<Utente> tutti = leggiTuttiUtenti();
        
        boolean esiste = tutti.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(utente.getUsername()));
        
        if (esiste) {
            return false;
        }
        
        tutti.add(utente);
        sovrascriviFile(tutti); 
        return true;
    }

    @Override
    public boolean aggiungiCreditiAlDB(String username, int quantita) {
        List<Utente> tutti = leggiTuttiUtenti();
        boolean modificato = false;
        
        for (Utente u : tutti) {
            if (u.getUsername().equals(username)) {
                
                // FIX 2: Simuliamo il vincolo del Database. 
                // Se la spesa ci manda in negativo, blocchiamo l'acquisto!
                if (u.getCrediti() + quantita < 0) {
                    return false; // Crediti insufficienti
                }
                
                // FIX 3: Usiamo la matematica cruda. 
                // Se quantita è -15, farà: crediti + (-15) = sottrazione corretta.
                u.setCrediti(u.getCrediti() + quantita);
                
                modificato = true;
                break;
            }
        }
        
        if (modificato) {
            sovrascriviFile(tutti); 
            return true;
        }
        return false;
    }

    @Override
    public boolean aggiornaPassword(String username, String nuovaPassword) {
        List<Utente> tutti = leggiTuttiUtenti();

        for (Utente utente : tutti) {
            if (utente.getUsername().equals(username)) {
                utente.setPassword(nuovaPassword);
                sovrascriviFile(tutti);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminaAccount(String username) {
        List<Utente> tutti = leggiTuttiUtenti();
        boolean rimosso = tutti.removeIf(utente -> utente.getUsername().equals(username));
        if (rimosso) {
            sovrascriviFile(tutti);
        }
        return rimosso;
    }

    @Override
    public List<Utente> recuperaClassifica() {
        List<Utente> tutti = leggiTuttiUtenti();
        return tutti.stream()
                .sorted((u1, u2) -> Integer.compare(u2.getCrediti(), u1.getCrediti()))
                .toList(); // <-- FIX SonarCloud (S6204): Sostituito collect(Collectors.toList())
    }
}