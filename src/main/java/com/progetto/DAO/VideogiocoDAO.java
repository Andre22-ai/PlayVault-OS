package com.progetto.DAO;
import java.util.List;

import com.progetto.Entity.Videogioco;

public interface VideogiocoDAO {
    boolean salvaGioco(Videogioco gioco);
    List<Videogioco> recuperaTutti(); // Ci servirà per la dashboard dei giocatori
}