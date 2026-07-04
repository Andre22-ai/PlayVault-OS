package com.progetto.exceptions;

public class CredenzialiErrateException extends Exception {
    public CredenzialiErrateException() {
        super("Accesso negato: Username o password non validi. Riprova.");
    }
}