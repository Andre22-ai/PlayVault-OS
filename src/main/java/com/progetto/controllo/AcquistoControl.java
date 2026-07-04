// FIX S120: Package tutto in minuscolo per rispettare le convenzioni Java
package com.progetto.controllo;

import com.progetto.database.LibreriaDAO;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;
import com.progetto.entita.Videogioco;
import com.progetto.exceptions.GiocoGiaPossedutoException;
import com.progetto.exceptions.SaldoInsufficienteException;
import com.progetto.exceptions.SalvataggioFallitoException;

public class AcquistoControl {

    private final LibreriaDAO libreriaDAO;
    
    // FIX S116 + S1170: Aggiunto "static" per renderla una costante ufficiale e zittire gli errori sul maiuscolo
    private static final int COSTO_GIOCO = 15; // Prezzo fisso per tutti i giochi

    public AcquistoControl(LibreriaDAO libreriaDAO) {
        this.libreriaDAO = libreriaDAO;
    }

    public String tentaAcquisto(Videogioco gioco)
            throws GiocoGiaPossedutoException, SaldoInsufficienteException, SalvataggioFallitoException {
        Utente utenteCorrente = Sessione.getIstanza().getUtenteCorrente();
        if (utenteCorrente == null) {
            throw new IllegalStateException("Nessun utente corrente.");
        }

        // 1. Controllo: L'utente lo ha già comprato?
        if (libreriaDAO.verificaPossesso(utenteCorrente.getUsername(), gioco.getId())) {
            throw new GiocoGiaPossedutoException(gioco.getTitolo());
        }

        // 2. Controllo: Ha abbastanza crediti?
        if (utenteCorrente.getCrediti() < COSTO_GIOCO) {
            throw new SaldoInsufficienteException(utenteCorrente.getCrediti(), COSTO_GIOCO);
        }

        // 3. Esecuzione: Deleghiamo al DAO la transazione sul database
        boolean successo = libreriaDAO.acquistaGioco(utenteCorrente.getUsername(), gioco.getId(), COSTO_GIOCO);

        if (successo) {
            // Aggiorniamo la RAM (l'Entity Java) togliendogli i crediti anche dalla visualizzazione
            utenteCorrente.setCrediti(utenteCorrente.getCrediti() - COSTO_GIOCO);
            return "SUCCESS";
        }

        throw new SalvataggioFallitoException("acquisto gioco");
    }
}