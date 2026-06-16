package com.progetto.controllo;

import java.util.List;

import com.progetto.database.LibreriaDAO;
import com.progetto.database.LibreriaDAOMySQL;
import com.progetto.database.VideogiocoDAO;
import com.progetto.entita.Videogioco;

/**
 * Controller di Business per la visualizzazione dei cataloghi.
 */
public class LibreriaControl {

    private VideogiocoDAO videogiocoDAO;
    private LibreriaDAO libreriaDAO; // Il nuovo "motore" per la libreria personale

    public LibreriaControl(VideogiocoDAO videogiocoDAO) {
        this.videogiocoDAO = videogiocoDAO;
        this.libreriaDAO = new LibreriaDAOMySQL(); // Inizializziamo il collegamento
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