package com.progetto.DAO;

public interface LibreriaDAO {
    // Controlla se il giocatore possiede già questo gioco
    boolean verificaPossesso(String username, int idGioco);
    
    // Esegue l'acquisto (Scala crediti + Aggiunge in libreria)
    boolean acquistaGioco(String username, int idGioco, int costo);

    java.util.List<com.progetto.Entity.Videogioco> recuperaGiochiPropri(String username);
}