package com.progetto.controllo;

import com.progetto.database.VideogiocoDAO;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.SalvataggioFallitoException;

public class GestioneCatalogoControl {

    private final VideogiocoDAO videogiocoDAO;

    public GestioneCatalogoControl(VideogiocoDAO videogiocoDAO) {
        this.videogiocoDAO = videogiocoDAO;
    }

    // ... altri metodi ...

    // --- FIX: Aggiunti i parametri descIt e descEn ---
    public boolean aggiungiNuovoGioco(String titolo, String genere, String annoString, String dev, String descIt, String descEn)
            throws SalvataggioFallitoException {
            
        if (titolo == null || titolo.isBlank() || genere == null || genere.isBlank() || annoString == null || annoString.isBlank() || dev == null || dev.isBlank()) {
            throw new IllegalArgumentException("Titolo, genere, anno e sviluppatore sono obbligatori.");
        }

        try {
            int anno = Integer.parseInt(annoString);
            if (anno < 1950 || anno > java.time.Year.now(java.time.ZoneId.systemDefault()).getValue() + 5) {
                throw new IllegalArgumentException("Anno non valido.");
            }

            // ==========================================
            // AUTO-FILL INTELLIGENTE (SMART FALLBACK)
            // ==========================================
            boolean mancaIt = (descIt == null || descIt.isBlank());
            boolean mancaEn = (descEn == null || descEn.isBlank());

            if (mancaIt && !mancaEn) {
                // L'admin ha scritto solo in inglese. Copiamo in italiano.
                descIt = descEn;
            } else if (mancaEn && !mancaIt) {
                // L'admin ha scritto solo in italiano. Copiamo in inglese.
                descEn = descIt;
            } else if (mancaIt && mancaEn) {
                // L'admin non ha scritto nulla in entrambi i campi.
                descIt = "Descrizione non disponibile.";
                descEn = "Description not available.";
            }
            // ==========================================

            Videogioco nuovoGioco = new Videogioco(titolo, genere, anno, dev, descIt, descEn);
            
            boolean salvato = videogiocoDAO.salvaGioco(nuovoGioco);
            if (!salvato) {
                throw new SalvataggioFallitoException("salvataggio gioco (verifica che le colonne descrizione_it e descrizione_en esistano nel DB)");
            }
            return true;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Anno non valido.", e);
        }
    }

    // --- NUOVO METODO PER LA RIMOZIONE LOGICA (SOFT DELETE) ---
    public boolean rimuoviGioco(int idGioco) throws SalvataggioFallitoException {
        if (idGioco <= 0) {
            throw new IllegalArgumentException("L'ID del gioco deve essere un numero maggiore di zero.");
        }

        // Richiama il DAO per nascondere il gioco
        boolean rimosso = videogiocoDAO.nascondiGiocoDalCatalogo(idGioco);
        
        if (!rimosso) {
            throw new SalvataggioFallitoException("de-listing del gioco (verifica che l'ID esista e sia corretto)");
        }
        
        return true;
    }

    public void modificaGiocoEsistente(int idGioco, String titolo, String genere, String anno, String sviluppatore, String descIt, String descEn) throws SalvataggioFallitoException {
        // Controllo base
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere vuoto.");
        }
        
        // 1. Convertiamo l'anno da String a int (gestendo eventuali errori se l'admin scrive lettere)
        int annoConvertito;
        try {
            annoConvertito = Integer.parseInt(anno);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("L'anno di uscita deve essere un numero valido.");
        }
        
        // 2. Usiamo il costruttore ESATTO del tuo file Videogioco.java
        Videogioco giocoModificato = new Videogioco(titolo, genere, annoConvertito, sviluppatore, descIt, descEn);
        
        // 3. Impostiamo l'ID del gioco da modificare usando il setter
        giocoModificato.setId(idGioco); 
        
        // 4. Inviamo l'aggiornamento al database
        boolean successo = videogiocoDAO.aggiornaGioco(giocoModificato);
        
        if (!successo) {
            throw new SalvataggioFallitoException("Errore critico del database durante la sovrascrittura.");
        }
    }
}