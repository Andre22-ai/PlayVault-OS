package com.progetto.exceptions;

public class SalvataggioFallitoException extends Exception {
    public SalvataggioFallitoException(String messaggio) {
        super("Errore critico di persistenza dati: " + messaggio);
    }
}