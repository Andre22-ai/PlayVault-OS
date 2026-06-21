package com.progetto.controllo;

import java.util.List;

import com.progetto.database.LibreriaDAO;
import com.progetto.database.VideogiocoDAO;
import com.progetto.entita.Videogioco;

/**
 * Controller di Business per la visualizzazione dei cataloghi.
 */
public class LibreriaControl {

    // FIX SonarCloud: Mettiamo 'final'
    private final VideogiocoDAO videogiocoDAO;
    private final LibreriaDAO libreriaDAO;

    // FIX: Ora accetta entrambi i database dall'esterno!
    public LibreriaControl(VideogiocoDAO videogiocoDAO, LibreriaDAO libreriaDAO) {
        this.videogiocoDAO = videogiocoDAO;
        this.libreriaDAO = libreriaDAO;
    }

    /**
     * Usato dal bottone ADD GAME (Negozio/Store): estrae tutto il catalogo dal database.
     */
    public List<Videogioco> ottieniCatalogoCompleto() {
        return videogiocoDAO.recuperaTutti();
    }

    /**
     * Usato dal bottone MY LIBRARY (Vault personale): estrae solo i giochi che l'utente ha comprato.
     */
    public List<Videogioco> ottieniMieiGiochi(String username) {
        return libreriaDAO.recuperaGiochiPropri(username);
    }
}