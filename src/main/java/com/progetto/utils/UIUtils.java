package com.progetto.utils;

import java.io.IOException;
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
        LOGGER.info("[BOUNDARY] Richiesta navigazione verso Impostazioni...");
        try {
            App.setRoot("impostazioni");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "ERRORE CRITICO: Non trovo il file impostazioni.fxml!", e);
        }
    }

    // Centralizza la scorciatoia per la lingua
    public static String getTesto(String chiave) {
        return GestoreLingua.getIstanza().get(chiave);
    }
}