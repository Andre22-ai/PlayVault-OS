package com.progetto;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton per la gestione della lingua dell'applicazione.
 */
public class GestoreLingua {

    private static GestoreLingua istanza;
    private Locale localeCorrente;
    private ResourceBundle bundle;

    private GestoreLingua() {
        this.localeCorrente = Locale.ITALIAN;
        this.bundle = ResourceBundle.getBundle("messages", localeCorrente);
    }

    public static synchronized GestoreLingua getIstanza() {
        if (istanza == null) {
            istanza = new GestoreLingua();
        }
        return istanza;
    }

    public void impostaLingua(String lingua) {
        if ("en".equalsIgnoreCase(lingua)) {
            localeCorrente = Locale.US;
        } else {
            localeCorrente = Locale.ITALIAN;
        }
        bundle = ResourceBundle.getBundle("messages", localeCorrente);
    }

    public String get(String chiave) {
        return bundle.getString(chiave);
    }

    public Locale getLocaleCorrente() {
        return localeCorrente;
    }
}
