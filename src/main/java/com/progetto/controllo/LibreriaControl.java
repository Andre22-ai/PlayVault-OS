package com.progetto.controllo;

import java.util.List;

import com.progetto.App;
import com.progetto.database.LibreriaDAO;
import com.progetto.database.VideogiocoDAO;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Utente;
import com.progetto.entita.Videogioco;


public class LibreriaControl {

    private final VideogiocoDAO videogiocoDAO;
    private final LibreriaDAO libreriaDAO;

    public LibreriaControl(VideogiocoDAO videogiocoDAO, LibreriaDAO libreriaDAO) {
        this.videogiocoDAO = videogiocoDAO;
        this.libreriaDAO = libreriaDAO;
    }

    
    public List<Videogioco> ottieniCatalogoCompleto() {
        return videogiocoDAO.recuperaTutti();
    }

    
    public List<Videogioco> ottieniMieiGiochi(String username) {
        return libreriaDAO.recuperaGiochiPropri(username);
    }

    

    
    public List<ElementoLibreria> ottieniLibreriaCompleta(String username) {
        return libreriaDAO.getLibreriaUtenteCompleta(username);
    }

    /**
     * Logica di business: Segna un gioco come completato e premia l'utente con l'esperienza.
     * @param utente L'utente corrente (dalla Sessione)
     * @param videogioco Il gioco appena completato
     * @return true se l'operazione va a buon fine, false altrimenti
     */
    public boolean completaGioco(Utente utente, Videogioco videogioco) {
        boolean aggiornato = libreriaDAO.impostaGiocoCompletato(utente.getUsername(), videogioco.getId());
        
        if (aggiornato) {
            utente.aggiungiEsperienza(videogioco.getExpFornita());
            
            App.getUtenteDAO().aggiornaEsperienza(utente.getUsername(), utente.getEsperienza());
            return true;
        }
        return false;
    }
}