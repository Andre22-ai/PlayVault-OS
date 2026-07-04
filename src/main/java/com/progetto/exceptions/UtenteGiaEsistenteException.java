package com.progetto.exceptions;

public class UtenteGiaEsistenteException extends Exception {
    public UtenteGiaEsistenteException(String username) {
        super("Impossibile registrarsi: l'utente '" + username + "' esiste già nel sistema.");
    }
}