package com.progetto.exceptions;

public class RecensioneInvalidaException extends Exception {
    public RecensioneInvalidaException(String motivo) {
        super("Impossibile salvare la recensione: " + motivo);
    }
}