package com.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibreriaDAOMySQL implements LibreriaDAO {

    // --- NUOVO: Inizializzazione del Logger ---
    private static final Logger LOGGER = Logger.getLogger(LibreriaDAOMySQL.class.getName());

    @Override
    public boolean verificaPossesso(String username, int idGioco) {
        // FIX S6905: Selezioniamo solo una colonna specifica invece di "*"
        String query = "SELECT id_gioco FROM libreria WHERE username = ? AND id_gioco = ?";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, idGioco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Ritorna true se trova almeno una riga
            }
        } catch (SQLException e) {
            // FIX S106: Logger al posto di printStackTrace
            LOGGER.log(Level.SEVERE, "[DAO] Errore verifica possesso", e);
            return false;
        }
    }

    @Override
    public boolean acquistaGioco(String username, int idGioco, int costo) {
        String queryAcquisto = "INSERT INTO libreria (username, id_gioco) VALUES (?, ?)";
        String queryPagamento = "UPDATE utenti SET crediti = crediti - ? WHERE username = ?";

        Connection conn = null;
        try {
            conn = GestoreConnessione.getConnessione();
            // Disattiviamo l'auto-salvataggio: Inizia la Transazione Sicura
            conn.setAutoCommit(false); 

            // 1. Aggiungiamo il gioco in libreria
            try (PreparedStatement stmtA = conn.prepareStatement(queryAcquisto)) {
                stmtA.setString(1, username);
                stmtA.setInt(2, idGioco);
                stmtA.executeUpdate();
            }

            // 2. Scaliamo i crediti
            try (PreparedStatement stmtP = conn.prepareStatement(queryPagamento)) {
                stmtP.setInt(1, costo);
                stmtP.setString(2, username);
                stmtP.executeUpdate();
            }

            // Se arriviamo qui senza crash, confermiamo entrambe le operazioni!
            conn.commit(); 
            return true;

        } catch (SQLException e) {
            // FIX S108: Blocco catch popolato con un log invece di essere lasciato vuoto
            try { 
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "[DAO] Errore critico durante il rollback della transazione", ex);
            }
            // FIX S106: Sostituito System.err
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'acquisto (possibile duplicato o fondi insufficienti)", e);
            return false;
        } finally {
            // FIX S108: Blocco catch popolato
            try { 
                if (conn != null) conn.setAutoCommit(true); 
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "[DAO] Errore durante il ripristino dell'auto-commit", ex);
            }
        }
    }

    @Override
    public java.util.List<com.progetto.Entity.Videogioco> recuperaGiochiPropri(String username) {
        java.util.List<com.progetto.Entity.Videogioco> mieiGiochi = new java.util.ArrayList<>();
        
        // FIX S6905: Esplicitiamo le colonne invece di usare "v.*"
        String query = "SELECT v.id_gioco, v.titolo, v.genere, v.anno_uscita, v.sviluppatore, v.descrizione " +
                       "FROM videogiochi v JOIN libreria l ON v.id_gioco = l.id_gioco WHERE l.username = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    com.progetto.Entity.Videogioco gioco = new com.progetto.Entity.Videogioco(
                        rs.getString("titolo"),
                        rs.getString("genere"),
                        rs.getInt("anno_uscita"),
                        rs.getString("sviluppatore"),
                        rs.getString("descrizione")
                    );
                    gioco.setId(rs.getInt("id_gioco")); 
                    mieiGiochi.add(gioco);
                }
            }
        } catch (SQLException e) {
            // FIX S106: Sostituito System.err
            LOGGER.log(Level.SEVERE, "[DAO] Errore recupero libreria personale", e);
        }
        return mieiGiochi;
    }
}