package com.progetto.database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.progetto.App;
import com.progetto.entita.Recensione;
import com.progetto.entita.Videogioco;

public class RecensioneDAOMemory implements RecensioneDAO {

    private final List<Recensione> recensioniInMemoria;

    public RecensioneDAOMemory() {
        this.recensioniInMemoria = new ArrayList<>();
    }

    @Override
    public boolean salvaRecensione(Recensione recensione) {
        // Controlla se l'utente ha già recensito questo gioco (simula il vincolo UNIQUE)
        boolean esiste = recensioniInMemoria.stream()
                .anyMatch(r -> r.getUsername().equals(recensione.getUsername()) && r.getIdGioco() == recensione.getIdGioco());
        
        if (esiste) return false;
        
        recensioniInMemoria.add(recensione);
        return true;
    }

    @Override
    public boolean aggiornaRecensione(Recensione recensione) {
        for (Recensione r : recensioniInMemoria) {
            if (r.getUsername().equals(recensione.getUsername()) && r.getIdGioco() == recensione.getIdGioco()) {
                r.setVoto(recensione.getVoto());
                r.setCommento(recensione.getCommento());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminaRecensione(String username, int idGioco) {
        return recensioniInMemoria.removeIf(r -> r.getUsername().equals(username) && r.getIdGioco() == idGioco);
    }

    @Override
    public List<Recensione> recuperaRecensioniPerGioco(int idGioco) {
        return recensioniInMemoria.stream()
                .filter(r -> r.getIdGioco() == idGioco)
                .collect(Collectors.toList());
    }

    @Override
    public List<Recensione> recuperaRecensioniUtente(String username) {
        List<Recensione> listaPersonale = new ArrayList<>();
        
        // Recuperiamo i giochi attuali per simulare la JOIN e prendere i titoli
        List<Videogioco> catalogo = App.getVideogiocoDAO().recuperaTutti();

        for (Recensione r : recensioniInMemoria) {
            if (r.getUsername().equals(username)) {
                // Cerchiamo il titolo del gioco
                String titolo = catalogo.stream()
                        .filter(g -> g.getId() == r.getIdGioco())
                        .map(Videogioco::getTitolo)
                        .findFirst()
                        .orElse("Titolo Sconosciuto");
                
                // Creiamo una copia per non sporcare i dati in RAM e le impostiamo il nome
                Recensione copia = new Recensione(r.getUsername(), r.getIdGioco(), r.getVoto(), r.getCommento());
                copia.setNomeGioco(titolo);
                listaPersonale.add(copia);
            }
        }
        return listaPersonale;
    }
}