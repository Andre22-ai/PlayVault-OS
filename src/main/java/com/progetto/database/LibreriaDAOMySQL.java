package com.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.entita.ElementoLibreria; 
import com.progetto.entita.Videogioco;       

public class LibreriaDAOMySQL implements LibreriaDAO {

    private static final Logger LOGGER = Logger.getLogger(LibreriaDAOMySQL.class.getName());

    @Override
    public boolean verificaPossesso(String username, int idGioco) {
        String query = "SELECT id_gioco FROM libreria WHERE username = ? AND id_gioco = ?";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, idGioco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
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
            conn.setAutoCommit(false);

            try (PreparedStatement stmtA = conn.prepareStatement(queryAcquisto)) {
                stmtA.setString(1, username);
                stmtA.setInt(2, idGioco);
                stmtA.executeUpdate();
            }

            try (PreparedStatement stmtP = conn.prepareStatement(queryPagamento)) {
                stmtP.setInt(1, costo);
                stmtP.setString(2, username);
                stmtP.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "[DAO] Errore critico durante il rollback della transazione", ex);
            }
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'acquisto (possibile duplicato o fondi insufficienti)", e);
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'auto-commit", ex);
            }
        }
    }

    @Override
    public List<Videogioco> recuperaGiochiPropri(String username) {
        List<Videogioco> mieiGiochi = new ArrayList<>();

        String query = "SELECT v.id_gioco, v.titolo, v.genere, v.anno_uscita, v.sviluppatore, v.descrizione_it, v.descrizione_en " +
                       "FROM videogiochi v JOIN libreria l ON v.id_gioco = l.id_gioco WHERE l.username = ?";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String descrizioneIt = rs.getString("descrizione_it");
                    String descrizioneEn = rs.getString("descrizione_en");
                    Videogioco gioco = new Videogioco(
                        rs.getString("titolo"),
                        rs.getString("genere"),
                        rs.getInt("anno_uscita"),
                        rs.getString("sviluppatore"),
                        descrizioneEn != null ? descrizioneEn : descrizioneIt,
                        descrizioneIt != null ? descrizioneIt : descrizioneEn
                    );
                    gioco.setId(rs.getInt("id_gioco"));
                    mieiGiochi.add(gioco);
                }
            }
        } catch (SQLException e) {
            if (colonnaMancante(e)) {
                return recuperaGiochiPropriCompatibili(username);
            }
            LOGGER.log(Level.SEVERE, "[DAO] Errore recupero libreria personale", e);
        }
        return mieiGiochi;
    }

    private List<Videogioco> recuperaGiochiPropriCompatibili(String username) {
        List<Videogioco> mieiGiochi = new ArrayList<>();
        String query = "SELECT v.id_gioco, v.titolo, v.genere, v.anno_uscita, v.sviluppatore, v.descrizione " +
                       "FROM videogiochi v JOIN libreria l ON v.id_gioco = l.id_gioco WHERE l.username = ?";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String descrizione = rs.getString("descrizione");
                    Videogioco gioco = new Videogioco(
                        rs.getString("titolo"),
                        rs.getString("genere"),
                        rs.getInt("anno_uscita"),
                        rs.getString("sviluppatore"),
                        descrizione,
                        descrizione
                    );
                    gioco.setId(rs.getInt("id_gioco"));
                    mieiGiochi.add(gioco);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore recupero libreria personale compatibile", ex);
        }
        return mieiGiochi;
    }

    private boolean colonnaMancante(SQLException e) {
        String message = e.getMessage();
        return message != null && (message.contains("Unknown column") || message.contains("doesn't exist") || message.contains("unknown column"));
    }

    

    @Override
    public List<ElementoLibreria> getLibreriaUtenteCompleta(String username) {
        List<ElementoLibreria> lista = new ArrayList<>();
        
        String query = "SELECT v.id_gioco, v.titolo, v.genere, v.anno_uscita, v.sviluppatore, v.descrizione_it, v.descrizione_en, v.exp_fornita, l.completato " +
                       "FROM videogiochi v JOIN libreria l ON v.id_gioco = l.id_gioco WHERE l.username = ?";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String descrizioneIt = rs.getString("descrizione_it");
                    String descrizioneEn = rs.getString("descrizione_en");
                    Videogioco gioco = new Videogioco(
                        rs.getString("titolo"),
                        rs.getString("genere"),
                        rs.getInt("anno_uscita"),
                        rs.getString("sviluppatore"),
                        descrizioneEn != null ? descrizioneEn : descrizioneIt,
                        descrizioneIt != null ? descrizioneIt : descrizioneEn
                    );
                    gioco.setId(rs.getInt("id_gioco"));
                    gioco.setExpFornita(rs.getInt("exp_fornita")); 
                    
                    boolean completato = rs.getBoolean("completato");
                    lista.add(new ElementoLibreria(gioco, completato));
                }
            }
        } catch (SQLException e) {
            if (colonnaMancante(e)) {
                return getLibreriaUtenteCompletaCompatibile(username);
            }
            LOGGER.log(Level.SEVERE, "[DAO] Errore recupero libreria completa", e);
        }
        return lista;
    }

    private List<ElementoLibreria> getLibreriaUtenteCompletaCompatibile(String username) {
        List<ElementoLibreria> lista = new ArrayList<>();
        String query = "SELECT v.id_gioco, v.titolo, v.genere, v.anno_uscita, v.sviluppatore, v.descrizione, l.completato " +
                       "FROM videogiochi v JOIN libreria l ON v.id_gioco = l.id_gioco WHERE l.username = ?";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String descrizione = rs.getString("descrizione");
                    Videogioco gioco = new Videogioco(
                        rs.getString("titolo"),
                        rs.getString("genere"),
                        rs.getInt("anno_uscita"),
                        rs.getString("sviluppatore"),
                        descrizione,
                        descrizione
                    );
                    gioco.setId(rs.getInt("id_gioco"));
                    
                    boolean completato = rs.getBoolean("completato");
                    lista.add(new ElementoLibreria(gioco, completato));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore recupero libreria completa compatibile", ex);
        }
        return lista;
    }

    @Override
    public boolean impostaGiocoCompletato(String username, int idGioco) {
        String query = "UPDATE libreria SET completato = TRUE WHERE username = ? AND id_gioco = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setInt(2, idGioco);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'impostazione gioco completato", e);
            return false;
        }
    }
}