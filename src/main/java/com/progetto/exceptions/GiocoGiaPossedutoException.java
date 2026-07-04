package com.progetto.exceptions;

public class GiocoGiaPossedutoException extends Exception {
    public GiocoGiaPossedutoException(String titolo) {
        super("Possiedi già il gioco '" + titolo + "' nel tuo Vault personale.");
    }
}