package com.progetto.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibreriaDAOMySQL implements LibreriaDAO {

    @Override
    public boolean verificaPossesso(String username, int idGioco) {
        String query = "SELECT * FROM libreria WHERE username = ? AND id_gioco = ?";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, idGioco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Ritorna true se trova almeno una riga
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            // Se c'è un errore (es. gioco già posseduto), annulliamo tutto
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            System.err.println("[DAO] Errore durante l'acquisto: " + e.getMessage());
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    @Override
    public java.util.List<com.progetto.Entity.Videogioco> recuperaGiochiPropri(String username) {
        java.util.List<com.progetto.Entity.Videogioco> mieiGiochi = new java.util.ArrayList<>();
        String query = "SELECT v.* FROM videogiochi v JOIN libreria l ON v.id_gioco = l.id_gioco WHERE l.username = ?";
        
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
            System.err.println("[DAO] Errore recupero libreria personale: " + e.getMessage());
        }
        return mieiGiochi;
    }
}