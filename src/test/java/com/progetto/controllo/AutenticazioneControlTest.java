package com.progetto.controllo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.progetto.database.UtenteDAOMemory;
import com.progetto.exceptions.CredenzialiErrateException;

class AutenticazioneControlTest {

    private AutenticazioneControl authControl;

    @BeforeEach
    void setUp() {
        // Creando il DAO in memoria, viene automaticamente generato l'utente "admin" (pass: "admin")
        UtenteDAOMemory utenteDAO = new UtenteDAOMemory();
        authControl = new AutenticazioneControl(utenteDAO);
    }

    @Test
    void testLoginConCredenzialiCorrette() throws CredenzialiErrateException {
        // ACT (Agisci): Usiamo l'admin pre-generato dal tuo DAO
        boolean risultato = authControl.eseguiLogin("admin", "admin");

        // ASSERT (Verifica)
        assertTrue(risultato, "Il login deve restituire true con credenziali corrette");
    }

    @Test
    void testLoginConPasswordSbagliata() {
        // ACT & ASSERT: Verifica che venga lanciata l'eccezione se la password è errata
        assertThrows(CredenzialiErrateException.class, () -> {
            authControl.eseguiLogin("admin", "passwordSbagliata123");
        }, "Il login deve lanciare CredenzialiErrateException se la password è sbagliata");
    }

    @Test
    void testLoginConUtenteInesistente() {
        // ACT & ASSERT: Verifica che venga lanciata l'eccezione per un utente che non c'è
        assertThrows(CredenzialiErrateException.class, () -> {
            authControl.eseguiLogin("utenteFantasma", "passwordAcaso");
        }, "Il login deve lanciare CredenzialiErrateException per un utente inesistente");
    }

    @Test
    void testLoginConInputVuoto() {
        // ACT & ASSERT: Verifica che venga lanciata l'eccezione se i campi sono vuoti o nulli
        assertThrows(CredenzialiErrateException.class, () -> {
            authControl.eseguiLogin("", " ");
        }, "Il login deve lanciare CredenzialiErrateException se username o password sono vuoti");
    }
}