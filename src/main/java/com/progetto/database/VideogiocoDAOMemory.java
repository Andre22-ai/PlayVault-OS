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
    private int prossimoIdDisponibile; 

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
            "Entra in Night City, dove chrome, corruzione e destino si scontrano in un futuro neon dominato da potere e segreti.",
            "Step into Night City, where chrome, corruption, and destiny collide in a neon-soaked future ruled by power and secrets."
        ));
        aggiungiGiocoDemo(new Videogioco(
            "Hollow Knight",
            "Metroidvania",
            2017,
            "Team Cherry",
            "Un viaggio inquietante attraverso un regno in rovina dove ogni corridoio nasconde un segreto e ogni sconfitta affila la tua determinazione.",
            "A haunting journey through a ruined kingdom where every corridor hides a secret and every defeat sharpens your resolve."
        ));
        aggiungiGiocoDemo(new Videogioco(
            "Elden Ring",
            "Souls-like",
            2022,
            "FromSoftware",
            "Attraversa l'Interregno e forgia la tua leggenda come il prossimo Lord Ancestrale in un mondo di mito, rovina e antico potere.",
            "Traverse the Lands Between and forge your legend as the next Elden Lord in a world of myth, ruin, and ancient power."
        ));
    }

    private void aggiungiGiocoDemo(Videogioco gioco) {
        salvaGioco(gioco);
    }

    @Override
    public List<Videogioco> recuperaTutti() {
        return new ArrayList<>(catalogoInMemoria);
    }

    @Override
    public boolean salvaGioco(Videogioco gioco) {
        if (gioco == null) {
            return false;
        }
        
        boolean esisteGia = catalogoInMemoria.stream()
                .anyMatch(v -> v.getTitolo().equalsIgnoreCase(gioco.getTitolo()));
                
        if (esisteGia) {
            return false;
        }

        gioco.setId(prossimoIdDisponibile);
        prossimoIdDisponibile++;
        
        catalogoInMemoria.add(gioco);
        return true;
    }

    // --- NUOVO METODO: Implementazione per l'interfaccia DAO ---
    @Override
    public boolean nascondiGiocoDalCatalogo(int idGioco) {
        // Rimuove l'oggetto dalla lista in memoria, simulando il soft-delete
        // (Il gioco non verrà più restituito da recuperaTutti)
        return catalogoInMemoria.removeIf(gioco -> gioco.getId() == idGioco);
    }
}