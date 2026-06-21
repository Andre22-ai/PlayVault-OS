package com.progetto.database;

import java.util.List;

import com.progetto.entita.Recensione;

public interface RecensioneDAO {
    boolean salvaRecensione(Recensione recensione);
    boolean aggiornaRecensione(Recensione recensione);
    boolean eliminaRecensione(String username, int idGioco);
    List<Recensione> recuperaRecensioniPerGioco(int idGioco);
    List<Recensione> recuperaRecensioniUtente(String username);
}