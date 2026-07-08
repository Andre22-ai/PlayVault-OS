package com.progetto.database;

import java.util.List;

import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Videogioco;

public interface LibreriaDAO {
    
    boolean verificaPossesso(String username, int idGioco);
    
    boolean acquistaGioco(String username, int idGioco, int costo);

    List<Videogioco> recuperaGiochiPropri(String username);

    

    
    List<ElementoLibreria> getLibreriaUtenteCompleta(String username);

    /**
     * Imposta il flag 'completato' a true per un gioco specifico posseduto dall'utente.
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti.
     */
    boolean impostaGiocoCompletato(String username, int idGioco);
}