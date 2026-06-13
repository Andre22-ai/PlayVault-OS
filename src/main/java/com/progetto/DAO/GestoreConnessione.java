package com.progetto.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestore della connessione al database MySQL (Pattern Singleton).
 */
public class GestoreConnessione {

    // Modifica questi parametri se in fase di installazione hai usato password diverse
    private static final String URL = "jdbc:mysql://localhost:3306/playvault_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Inter"; // Metti la TUA password di MySQL qui!

    private static Connection connessione;

    public static Connection getConnessione() {
        try {
            if (connessione == null || connessione.isClosed()) {
                // Instauriamo il ponte di connessione con MySQL
                connessione = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DATABASE] Connessione a MySQL stabilita con successo.");
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE] ERRORE CRITICO: Impossibile connettersi a MySQL.");
            e.printStackTrace();
        }
        return connessione;
    }
}