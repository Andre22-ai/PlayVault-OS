package com.progetto.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GestoreConnessione {

    private static final Logger LOGGER = Logger.getLogger(GestoreConnessione.class.getName());
    
    private static Connection connessione;
    private static final Properties props = new Properties();

    private GestoreConnessione() {
        throw new IllegalStateException("Utility class - non instanziabile");
    }

    static {
        try (InputStream input = GestoreConnessione.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                LOGGER.severe("[DATABASE] ERRORE: File database.properties non trovato in src/main/resources!");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "[DATABASE] ERRORE durante la lettura delle credenziali", ex);
        }
    }

    public static Connection getConnessione() {
        try {
            if (connessione == null || connessione.isClosed()) {
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connessione = DriverManager.getConnection(url, user, password);
                LOGGER.info("[DATABASE] Connessione a MySQL stabilita con successo.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DATABASE] ERRORE CRITICO: Impossibile connettersi a MySQL", e);
        }
        return connessione;
    }
}