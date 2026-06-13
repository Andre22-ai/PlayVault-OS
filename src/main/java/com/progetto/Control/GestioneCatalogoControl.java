package com.progetto.Control;

import java.time.Year;

import com.progetto.DAO.VideogiocoDAO;
import com.progetto.Entity.Videogioco;

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
            if (anno < 1950 || anno > Year.now().getValue() + 5) return false;
            
            // Creazione e salvataggio
            Videogioco nuovoGioco = new Videogioco(titolo, genere, anno, dev, desc);
            return videogiocoDAO.salvaGioco(nuovoGioco);
            
        } catch (NumberFormatException e) {
            return false; // L'anno non è un numero
        }
    }
}