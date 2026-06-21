package com.progetto.database;

import java.util.ArrayList;
import java.util.List;

import com.progetto.App;
import com.progetto.entita.Videogioco;

/**
 * Versione DEMO (In-Memory) della Libreria.
 * I dati degli acquisti vivono solo finché l'app è aperta.
 */
public class LibreriaDAOMemory implements LibreriaDAO {

    // Simula la tabella del database che collega un utente a un gioco
    private static class RigaLibreria {
        String username;
        int idGioco;

        RigaLibreria(String username, int idGioco) {
            this.username = username;
            this.idGioco = idGioco;
        }
    }

    // La nostra finta "tabella" relazionale in RAM
    private final List<RigaLibreria> tabellaLibreria;

    public LibreriaDAOMemory() {
        this.tabellaLibreria = new ArrayList<>();
    }

    @Override
    public boolean verificaPossesso(String username, int idGioco) {
        // Cerca se esiste una riga con quell'utente e quel gioco
        return tabellaLibreria.stream()
                .anyMatch(r -> r.username.equals(username) && r.idGioco == idGioco);
    }

    @Override
    public boolean acquistaGioco(String username, int idGioco, int costo) {
        // 1. Scaliamo i crediti all'utente (Simuliamo l'UPDATE).
        // Passiamo un costo negativo così la funzione "aggiungiCrediti" fa una sottrazione!
        boolean pagato = App.getUtenteDAO().aggiungiCreditiAlDB(username, -costo);
        
        // Se non ha pagato (es. crediti insufficienti gestiti a monte), fermiamo tutto
        if (!pagato) {
            return false;
        }

        // 2. Aggiungiamo il gioco in libreria (Simuliamo l'INSERT)
        tabellaLibreria.add(new RigaLibreria(username, idGioco));
        return true;
    }

    @Override
    public List<Videogioco> recuperaGiochiPropri(String username) {
        List<Videogioco> mieiGiochi = new ArrayList<>();
        
        // Recuperiamo tutto il catalogo dal database dei videogiochi attivo in questo momento
        List<Videogioco> tuttoIlCatalogo = App.getVideogiocoDAO().recuperaTutti();

        // Simuliamo la INNER JOIN di MySQL
        for (RigaLibreria riga : tabellaLibreria) {
            if (riga.username.equals(username)) {
                // Cerca il gioco corrispondente all'ID e aggiungilo alla lista
                tuttoIlCatalogo.stream()
                        .filter(g -> g.getId() == riga.idGioco)
                        .findFirst()
                        .ifPresent(mieiGiochi::add);
            }
        }
        
        return mieiGiochi;
    }
}