// FIX S120: Package tutto in minuscolo per rispettare le convenzioni Java
package com.progetto.controllo;

import com.progetto.DAO.LibreriaDAO;
import com.progetto.Entity.Sessione;
import com.progetto.Entity.Utente;
import com.progetto.Entity.Videogioco;

public class AcquistoControl {

    private LibreriaDAO libreriaDAO;
    
    // FIX S116 + S1170: Aggiunto "static" per renderla una costante ufficiale e zittire gli errori sul maiuscolo
    private static final int COSTO_GIOCO = 15; // Prezzo fisso per tutti i giochi

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