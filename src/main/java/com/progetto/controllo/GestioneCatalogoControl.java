package com.progetto.controllo;

import java.time.Year;
import java.time.ZoneId; // FIX S8688: Importiamo il fuso orario

import com.progetto.database.VideogiocoDAO;
import com.progetto.entita.Videogioco;

public class GestioneCatalogoControl {

    private VideogiocoDAO videogiocoDAO;

    public GestioneCatalogoControl(VideogiocoDAO videogiocoDAO) {
        this.videogiocoDAO = videogiocoDAO;
    }

    public boolean aggiungiNuovoGioco(String titolo, String genere, String annoString, String dev, String desc) {
        // Controllo campi vuoti
        if (titolo.isBlank() || genere.isBlank() || annoString.isBlank() || dev.isBlank()) return false;

        // Controllo anno valido
        try {
            int anno = Integer.parseInt(annoString);
            // FIX S8688: Dichiariamo esplicitamente il fuso orario di sistema
            if (anno < 1950 || anno > Year.now(ZoneId.systemDefault()).getValue() + 5) return false;
            
            // Creazione e salvataggio
            Videogioco nuovoGioco = new Videogioco(titolo, genere, anno, dev, desc);
            return videogiocoDAO.salvaGioco(nuovoGioco);
            
        } catch (NumberFormatException e) {
            return false; // L'anno non è un numero
        }
    }
}