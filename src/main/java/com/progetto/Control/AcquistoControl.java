package com.progetto.Control;

import com.progetto.DAO.LibreriaDAO;
import com.progetto.Entity.Sessione;
import com.progetto.Entity.Utente;
import com.progetto.Entity.Videogioco;

public class AcquistoControl {

    private LibreriaDAO libreriaDAO;
    private final int COSTO_GIOCO = 15; // Prezzo fisso per tutti i giochi

    public AcquistoControl(LibreriaDAO libreriaDAO) {
        this.libreriaDAO = libreriaDAO;
    }

    public String tentaAcquisto(Videogioco gioco) {
        Utente utenteCorrente = Sessione.getIstanza().getUtenteCorrente();
        
        // 1. Controllo: L'utente lo ha già comprato?
        if (libreriaDAO.verificaPossesso(utenteCorrente.getUsername(), gioco.getId())) {
            return "ALREADY_OWNED";
        }
        
        // 2. Controllo: Ha abbastanza crediti?
        if (utenteCorrente.getCrediti() < COSTO_GIOCO) {
            return "INSUFFICIENT_FUNDS";
        }

        // 3. Esecuzione: Deleghiamo al DAO la transazione sul database
        boolean successo = libreriaDAO.acquistaGioco(utenteCorrente.getUsername(), gioco.getId(), COSTO_GIOCO);
        
        if (successo) {
            // Aggiorniamo la RAM (l'Entity Java) togliendogli i crediti anche dalla visualizzazione
            utenteCorrente.setCrediti(utenteCorrente.getCrediti() - COSTO_GIOCO);
            return "SUCCESS";
        } else {
            return "SYSTEM_ERROR";
        }
    }
}