package com.progetto.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestore della connessione al database MySQL (Pattern Singleton Sicuro).
 */
public class GestoreConnessione {

    private static Connection connessione;
    private static final Properties props = new Properties();

    // Carichiamo i dati dal file segreto una sola volta all'avvio
    static {
        try (InputStream input = GestoreConnessione.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("[DATABASE] ERRORE: File database.properties non trovato in src/main/resources!");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            System.err.println("[DATABASE] ERRORE durante la lettura delle credenziali: " + ex.getMessage());
        }
    }

    public static Connection getConnessione() {
        try {
            if (connessione == null || connessione.isClosed()) {
                // Leggiamo i parametri in modo sicuro senza scriverli nel codice!
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connessione = DriverManager.getConnection(url, user, password);
                System.out.println("[DATABASE] Connessione a MySQL stabilita con successo.");
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE] ERRORE CRITICO: Impossibile connettersi a MySQL. " + e.getMessage());
        }
        return connessione;
    }
}