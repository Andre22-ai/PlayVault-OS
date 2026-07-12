package com.progetto.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;

public class UIUtils {

    private static final Logger LOGGER = Logger.getLogger(UIUtils.class.getName());

    // Costruttore privato (SonarQube docet!)
    private UIUtils() {
        throw new IllegalStateException("Classe di utilità: non può essere istanziata");
    }

    // Centralizza il navigatore per le impostazioni e il suo try-catch
    public static void navigaAImpostazioni() {
        try {
            App.cambiaSchermata("impostazioni"); 
        } catch (Exception e) {
            // Visto che abbiamo il LOGGER, usiamolo per tracciare l'errore!
            LOGGER.log(Level.SEVERE, "Errore nell'apertura delle impostazioni", e);
        }
    }

    // Centralizza la scorciatoia per la lingua
    public static String getTesto(String chiave) {
        return GestoreLingua.getIstanza().get(chiave);
    }
}