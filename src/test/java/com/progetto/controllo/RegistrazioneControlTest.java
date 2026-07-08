package com.progetto.controllo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.progetto.database.UtenteDAOMemory;
import com.progetto.entita.Utente;
import com.progetto.exceptions.UtenteGiaEsistenteException;

class RegistrazioneControlTest {

    private UtenteDAOMemory utenteDAO;
    private RegistrazioneControl regControl;

    @BeforeEach
    void setUp() {
        utenteDAO = new UtenteDAOMemory(); 
        regControl = new RegistrazioneControl(utenteDAO);
    }

    @Test
    void testRegistrazioneNuovoUtenteConSuccesso() throws UtenteGiaEsistenteException {
        // ACT
        boolean risultato = regControl.registraNuovoUtente("nuovoPlayer", "pass123", "pass123");

        // ASSERT 1: Il metodo deve restituire true
        assertTrue(risultato, "La registrazione di un nuovo utente deve restituire true");
        
        // ASSERT 2: Chiediamo al DAO se lo ha salvato davvero e se ha i 50 crediti di bonus!
        Utente utenteSalvato = utenteDAO.autentica("nuovoPlayer", "pass123");
        assertNotNull(utenteSalvato, "Il nuovo utente deve trovarsi effettivamente nel database");
        assertEquals(50, utenteSalvato.getCrediti(), "Il nuovo utente deve ricevere 50 crediti di bonus iniziale");
    }

    @Test
    void testRegistrazioneUtenteGiaEsistente() {
        // ACT & ASSERT: L'utente "admin" viene creato di default dal costruttore di UtenteDAOMemory
        assertThrows(UtenteGiaEsistenteException.class, () -> {
            regControl.registraNuovoUtente("admin", "nuovaPass", "nuovaPass");
        }, "Deve lanciare UtenteGiaEsistenteException se l'username è già preso");
    }

    @Test
    void testRegistrazioneConPasswordNonCoincidenti() {
        // ACT & ASSERT: Inseriamo una conferma password diversa
        assertThrows(IllegalArgumentException.class, () -> {
            regControl.registraNuovoUtente("player2", "pass123", "passSbagliata");
        }, "Deve lanciare IllegalArgumentException se le password non coincidono");
    }

    @Test
    void testRegistrazioneConInputVuoto() {
        // ACT & ASSERT: Username vuoto
        assertThrows(IllegalArgumentException.class, () -> {
            regControl.registraNuovoUtente("", "pass123", "pass123");
        }, "Deve lanciare IllegalArgumentException se l'username è vuoto");
        
        // ACT & ASSERT: Password nulla
        assertThrows(IllegalArgumentException.class, () -> {
            regControl.registraNuovoUtente("player3", null, null);
        }, "Deve lanciare IllegalArgumentException se la password è nulla");
    }
}