package com.progetto.controllo;

import java.time.Year;
import java.time.ZoneId; // FIX S8688: Importiamo il fuso orario

import com.progetto.database.VideogiocoDAO;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.SalvataggioFallitoException;

public class GestioneCatalogoControl {

    private final VideogiocoDAO videogiocoDAO;

    public GestioneCatalogoControl(VideogiocoDAO videogiocoDAO) {
        this.videogiocoDAO = videogiocoDAO;
    }

    public boolean aggiungiNuovoGioco(String titolo, String genere, String annoString, String dev, String desc)
            throws SalvataggioFallitoException {
        // Controllo campi vuoti
        if (titolo == null || titolo.isBlank() || genere == null || genere.isBlank() || annoString == null || annoString.isBlank() || dev == null || dev.isBlank()) {
            throw new IllegalArgumentException("Titolo, genere, anno e sviluppatore sono obbligatori.");
        }

        // Controllo anno valido
        try {
            int anno = Integer.parseInt(annoString);
            // FIX S8688: Dichiariamo esplicitamente il fuso orario di sistema
            if (anno < 1950 || anno > Year.now(ZoneId.systemDefault()).getValue() + 5) {
                throw new IllegalArgumentException("Anno non valido.");
            }

            // Creazione e salvataggio
            Videogioco nuovoGioco = new Videogioco(titolo, genere, anno, dev, desc);
            boolean salvato = videogiocoDAO.salvaGioco(nuovoGioco);
            if (!salvato) {
                throw new SalvataggioFallitoException("salvataggio gioco");
            }
            return true;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Anno non valido.", e);
        }
    }
}