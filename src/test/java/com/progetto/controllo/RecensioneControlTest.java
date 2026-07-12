package com.progetto.controllo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.progetto.database.RecensioneDAO;
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Recensione;
import com.progetto.entita.Utente;
import com.progetto.exceptions.RecensioneInvalidaException;

class RecensioneControlTest {

    private RecensioneControl recensioneControl;

    @BeforeEach
    void setUp() {
        // Stub per RecensioneDAO
        RecensioneDAO fintoRecensioneDAO = new RecensioneDAO() {
            public boolean salvaRecensione(Recensione r) { return true; }
            public boolean aggiornaRecensione(Recensione r) { return true; }
            public boolean eliminaRecensione(String u, int id) { return true; }
            public java.util.List<Recensione> recuperaRecensioniPerGioco(int id) { return null; }
            public java.util.List<Recensione> recuperaRecensioniUtente(String u) { return null; }
        };

        // Stub per UtenteDAO
        UtenteDAO fintoUtenteDAO = new UtenteDAO() {
            // Implementa solo il metodo necessario per il test
            public boolean aggiungiCreditiAlDB(String username, int crediti) { return true; }
            // Gli altri metodi dell'interfaccia UtenteDAO puoi lasciarli vuoti/ritornare null

            @Override
            public Utente autentica(String username, String password) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean salvaUtente(Utente utente) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean aggiornaPassword(String username, String nuovaPassword) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean eliminaAccount(String username) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean aggiornaEsperienza(String username, int nuovaEsperienza) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<Utente> recuperaClassifica() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        recensioneControl = new RecensioneControl(fintoRecensioneDAO, fintoUtenteDAO);
    }

    @Test
    void testRecensioneInvalidaVotoNonValido() {
        Recensione r = new Recensione("tester", 1, 11, "Commento valido"); // Voto 11 (fuori range 1-10)
        
        assertThrows(RecensioneInvalidaException.class, () -> {
            recensioneControl.elaboraRecensione(r);
        }, "Il sistema deve lanciare RecensioneInvalidaException se il voto è > 10");
    }

    @Test
    void testRecensioneInvalidaCommentoVuoto() {
        Recensione r = new Recensione("tester", 1, 5, "   "); // Commento vuoto/spazi
        
        assertThrows(RecensioneInvalidaException.class, () -> {
            recensioneControl.elaboraRecensione(r);
        }, "Il sistema deve lanciare RecensioneInvalidaException se il commento è vuoto");
    }
}