package com.progetto.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestore della connessione al database MySQL (Pattern Singleton Sicuro).
 */
public class GestoreConnessione {

    // --- NUOVO: Inizializziamo il Logger ---
    private static final Logger LOGGER = Logger.getLogger(GestoreConnessione.class.getName());
    
    private static Connection connessione;
    private static final Properties props = new Properties();

    private GestoreConnessione() {
        throw new IllegalStateException("Utility class - non instanziabile");
    }

    // Carichiamo i dati dal file segreto una sola volta all'avvio
    static {
        try (InputStream input = GestoreConnessione.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                LOGGER.severe("[DATABASE] ERRORE: File database.properties non trovato in src/main/resources!");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            // Passiamo l'eccezione al logger per avere la traccia completa
            LOGGER.log(Level.SEVERE, "[DATABASE] ERRORE durante la lettura delle credenziali", ex);
        }
    }

    public static Connection getConnessione() {
        try {
            if (connessione == null || connessione.isClosed()) {
                // Leggiamo i parametri in modo sicuro dal file .properties
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connessione = DriverManager.getConnection(url, user, password);
                LOGGER.info("[DATABASE] Connessione a MySQL stabilita con successo.");
            }
        } catch (SQLException e) {
            // Log dell'errore critico con stack trace allegato
            LOGGER.log(Level.SEVERE, "[DATABASE] ERRORE CRITICO: Impossibile connettersi a MySQL", e);
        }
        return connessione;
    }
}