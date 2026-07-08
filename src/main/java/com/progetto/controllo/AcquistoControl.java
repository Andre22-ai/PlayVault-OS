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
    
    private static final int COSTO_GIOCO = 15; 

    public AcquistoControl(LibreriaDAO libreriaDAO) {
        this.libreriaDAO = libreriaDAO;
    }

    public String tentaAcquisto(Videogioco gioco)
            throws GiocoGiaPossedutoException, SaldoInsufficienteException, SalvataggioFallitoException {
        Utente utenteCorrente = Sessione.getIstanza().getUtenteCorrente();
        if (utenteCorrente == null) {
            throw new IllegalStateException("Nessun utente corrente.");
        }

        if (libreriaDAO.verificaPossesso(utenteCorrente.getUsername(), gioco.getId())) {
            throw new GiocoGiaPossedutoException(gioco.getTitolo());
        }

        if (utenteCorrente.getCrediti() < COSTO_GIOCO) {
            throw new SaldoInsufficienteException(utenteCorrente.getCrediti(), COSTO_GIOCO);
        }

        boolean successo = libreriaDAO.acquistaGioco(utenteCorrente.getUsername(), gioco.getId(), COSTO_GIOCO);

        if (successo) {
            utenteCorrente.setCrediti(utenteCorrente.getCrediti() - COSTO_GIOCO);
            return "SUCCESS";
        }

        throw new SalvataggioFallitoException("acquisto gioco");
    }
}