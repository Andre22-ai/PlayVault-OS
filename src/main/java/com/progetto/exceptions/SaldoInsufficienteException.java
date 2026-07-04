package com.progetto.exceptions;

public class SaldoInsufficienteException extends Exception {
    public SaldoInsufficienteException(int creditiAttuali, int costo) {
        super("Acquisto negato: hai " + creditiAttuali + " crediti, ma ne servono " + costo + ".");
    }
}