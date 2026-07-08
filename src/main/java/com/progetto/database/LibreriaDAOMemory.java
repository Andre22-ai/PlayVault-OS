package com.progetto.database;

import java.util.ArrayList;
import java.util.List;

import com.progetto.App;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Videogioco;


public class LibreriaDAOMemory implements LibreriaDAO {

    private static class RigaLibreria {
        String username;
        int idGioco;
        boolean completato; 

        RigaLibreria(String username, int idGioco) {
            this.username = username;
            this.idGioco = idGioco;
            this.completato = false; 
        }
    }

    private final List<RigaLibreria> tabellaLibreria;

    public LibreriaDAOMemory() {
        this.tabellaLibreria = new ArrayList<>();
    }

    @Override
    public boolean verificaPossesso(String username, int idGioco) {
        return tabellaLibreria.stream()
                .anyMatch(r -> r.username.equals(username) && r.idGioco == idGioco);
    }

    @Override
    public boolean acquistaGioco(String username, int idGioco, int costo) {
        // 1. Scaliamo i crediti all'utente (Simuliamo l'UPDATE).
        // Passiamo un costo negativo così la funzione "aggiungiCrediti" fa una sottrazione
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
        
        List<Videogioco> tuttoIlCatalogo = App.getVideogiocoDAO().recuperaTutti();

        for (RigaLibreria riga : tabellaLibreria) {
            if (riga.username.equals(username)) {
                tuttoIlCatalogo.stream()
                        .filter(g -> g.getId() == riga.idGioco)
                        .findFirst()
                        .ifPresent(mieiGiochi::add);
            }
        }
        
        return mieiGiochi;
    }

    

    @Override
    public List<ElementoLibreria> getLibreriaUtenteCompleta(String username) {
        List<ElementoLibreria> libreriaCompleta = new ArrayList<>();
        List<Videogioco> tuttoIlCatalogo = App.getVideogiocoDAO().recuperaTutti();

        for (RigaLibreria riga : tabellaLibreria) {
            if (riga.username.equals(username)) {
                tuttoIlCatalogo.stream()
                        .filter(g -> g.getId() == riga.idGioco)
                        .findFirst()
                        .ifPresent(gioco -> libreriaCompleta.add(new ElementoLibreria(gioco, riga.completato)));
            }
        }
        return libreriaCompleta;
    }

    @Override
    public boolean impostaGiocoCompletato(String username, int idGioco) {
        for (RigaLibreria riga : tabellaLibreria) {
            if (riga.username.equals(username) && riga.idGioco == idGioco) {
                riga.completato = true; 
                return true;
            }
        }
        return false;
    }
}