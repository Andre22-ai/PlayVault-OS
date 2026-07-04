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
        inizializzaCatalogoDemo();
    }

    private void inizializzaCatalogoDemo() {
        aggiungiGiocoDemo(new Videogioco(
            "Cyberpunk 2077",
            "RPG",
            2020,
            "CD Projekt RED",
            "Step into Night City, where chrome, corruption, and destiny collide in a neon-soaked future ruled by power and secrets.",
            "Entra in Night City, dove chrome, corruzione e destino si scontrano in un futuro neon dominato da potere e segreti."
        ));
        aggiungiGiocoDemo(new Videogioco(
            "Hollow Knight",
            "Metroidvania",
            2017,
            "Team Cherry",
            "A haunting journey through a ruined kingdom where every corridor hides a secret and every defeat sharpens your resolve.",
            "Un viaggio inquietante attraverso un regno in rovina dove ogni corridoio nasconde un segreto e ogni sconfitta affila la tua determinazione."
        ));
        aggiungiGiocoDemo(new Videogioco(
            "Elden Ring",
            "Souls-like",
            2022,
            "FromSoftware",
            "Traverse the Lands Between and forge your legend as the next Elden Lord in a world of myth, ruin, and ancient power.",
            "Attraversa l'Interregno e forgia la tua leggenda come il prossimo Lord Ancestrale in un mondo di mito, rovina e antico potere."
        ));
    }

    private void aggiungiGiocoDemo(Videogioco gioco) {
        salvaGioco(gioco);
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