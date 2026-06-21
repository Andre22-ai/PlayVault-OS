package com.progetto.database;

import java.util.ArrayList;
import java.util.List;

import com.progetto.entita.Videogioco;

/**
 * Versione DEMO (In-Memory) del Catalogo Giochi.
 * I dati vivono solo finché l'app è aperta.
 */
public class VideogiocoDAOMemory implements VideogiocoDAO {

    private final List<Videogioco> catalogoInMemoria;
    private int prossimoIdDisponibile; // Simula l'AUTO_INCREMENT di MySQL

    public VideogiocoDAOMemory() {
        this.catalogoInMemoria = new ArrayList<>();
        this.prossimoIdDisponibile = 1;
        
        // BONUS: Pre-popoliamo il catalogo con un paio di giochi 
        // così la Demo non è completamente vuota all'avvio!
        salvaGioco(new Videogioco("Cyberpunk 2077", "RPG", 2020, "CD Projekt RED", "Un gioco di ruolo open world nell'oscura Night City."));
        salvaGioco(new Videogioco("Hollow Knight", "Metroidvania", 2017, "Team Cherry", "Un'epica avventura in un regno di insetti in rovina."));
        salvaGioco(new Videogioco("Elden Ring", "Souls-like", 2022, "FromSoftware", "Esplora l'Interregno e diventa il Lord Ancestrale."));
    }

    @Override
    public List<Videogioco> recuperaTutti() {
        // Restituiamo una copia della lista per sicurezza
        return new ArrayList<>(catalogoInMemoria);
    }

    @Override
    public boolean salvaGioco(Videogioco gioco) {
        if (gioco == null) {
            return false;
        }
        
        // Controllo se esiste già un gioco con lo stesso titolo (case-insensitive)
        boolean esisteGia = catalogoInMemoria.stream()
                .anyMatch(v -> v.getTitolo().equalsIgnoreCase(gioco.getTitolo()));
                
        if (esisteGia) {
            return false;
        }

        // Assegniamo l'ID progressivo simulando MySQL
        gioco.setId(prossimoIdDisponibile);
        prossimoIdDisponibile++;
        
        catalogoInMemoria.add(gioco);
        return true;
    }
}