package com.progetto.database;

import java.util.List;

import com.progetto.entita.Videogioco;

public interface VideogiocoDAO {
    boolean salvaGioco(Videogioco gioco);
    List<Videogioco> recuperaTutti();
    
    // --- METODO: Soft Delete ---
    boolean nascondiGiocoDalCatalogo(int idGioco);
    
    // --- NUOVO METODO: Update / Modifica ---
    boolean aggiornaGioco(Videogioco gioco);
}