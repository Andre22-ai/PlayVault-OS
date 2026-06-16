package com.progetto.database;

public interface LibreriaDAO {
    // Controlla se il giocatore possiede già questo gioco
    boolean verificaPossesso(String username, int idGioco);
    
    // Esegue l'acquisto (Scala crediti + Aggiunge in libreria)
    boolean acquistaGioco(String username, int idGioco, int costo);

    java.util.List<com.progetto.entita.Videogioco> recuperaGiochiPropri(String username);
}