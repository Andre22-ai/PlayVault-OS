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
import java.util.stream.Collectors;

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
        // Al primo avvio, se il file non esiste, lo crea e ci mette un admin
        File file = new File(NOME_FILE);
        if (!file.exists()) {
            try {
                boolean creato = file.createNewFile();
                if (creato) {
                    Utente admin = new Utente("admin", "admin");
                    admin.setRuolo("ADMIN");
                    admin.aggiungiCrediti(100);
                    salvaUtente(admin); // Lo salva subito nel file!
                    LOGGER.info("[CSV] Creato nuovo file utenti.csv con account admin default.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "[CSV] Impossibile creare il file utenti.csv", e);
            }
        }
    }

    /**
     * Metodo di supporto: Legge tutte le righe del CSV e le trasforma in una Lista di Utenti
     */
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
                    
                    // Impostiamo i crediti. Se il tuo utente parte già con dei crediti nel costruttore,
                    // calcoliamo la differenza per impostare il valore esatto letto dal file
                    int creditiDaFile = Integer.parseInt(dati[3]);
                    int differenza = creditiDaFile - u.getCrediti();
                    u.aggiungiCrediti(differenza);
                    
                    lista.add(u);
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante la lettura del file", e);
        }
        
        return lista;
    }

    /**
     * Metodo di supporto: Prende una Lista di Utenti e la sovrascrive sul CSV
     */
    private void sovrascriviFile(List<Utente> utenti) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(NOME_FILE))) {
            for (Utente u : utenti) {
                pw.println(u.getUsername() + "," + u.getPassword() + "," + u.getRuolo() + "," + u.getCrediti());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante la scrittura del file", e);
        }
    }

    // ==========================================
    // METODI DELL'INTERFACCIA DAO
    // ==========================================

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
        
        // Controllo se esiste già
        boolean esiste = tutti.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(utente.getUsername()));
        
        if (esiste) {
            return false;
        }
        
        tutti.add(utente);
        sovrascriviFile(tutti); // Aggiorniamo il file
        return true;
    }

    @Override
    public boolean aggiungiCreditiAlDB(String username, int quantita) {
        List<Utente> tutti = leggiTuttiUtenti();
        boolean modificato = false;
        
        for (Utente u : tutti) {
            if (u.getUsername().equals(username)) {
                u.aggiungiCrediti(quantita);
                modificato = true;
                break;
            }
        }
        
        if (modificato) {
            sovrascriviFile(tutti); // Aggiorniamo il file con i nuovi crediti
            return true;
        }
        return false;
    }

    @Override
    public List<Utente> recuperaClassifica() {
        List<Utente> tutti = leggiTuttiUtenti();
        return tutti.stream()
                .sorted((u1, u2) -> Integer.compare(u2.getCrediti(), u1.getCrediti()))
                .collect(Collectors.toList());
    }
}