package com.progetto.database;

import java.util.List;

import com.progetto.entita.Videogioco;

public interface VideogiocoDAO {
    boolean salvaGioco(Videogioco gioco);
    List<Videogioco> recuperaTutti();
    
    // --- NUOVO METODO: Soft Delete ---
    boolean nascondiGiocoDalCatalogo(int idGioco);
}