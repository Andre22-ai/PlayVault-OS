package com.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;      
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.entita.Utente;

/**
 * Implementazione reale del Data Access Object per MySQL.
 */
public class UtenteDAOMySQL implements UtenteDAO {

    // --- NUOVO: Inizializzazione del Logger ---
    private static final Logger LOGGER = Logger.getLogger(UtenteDAOMySQL.class.getName());

    @Override
    public Utente autentica(String username, String password) {
        String query = "SELECT username, password, crediti, ruolo FROM utenti WHERE BINARY username = ? AND BINARY password = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utente utente = new Utente(rs.getString("username"), rs.getString("password"));
                    utente.aggiungiCrediti(rs.getInt("crediti"));
                    utente.setRuolo(rs.getString("ruolo"));
                    return utente;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'autenticazione", e);
        }
        return null;
    }

    @Override
    public boolean salvaUtente(Utente utente) {
        String query = "INSERT INTO utenti (username, password, crediti) VALUES (?, ?, ?)";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setInt(3, utente.getCrediti());
            
            int righeInserite = stmt.executeUpdate();
            return righeInserite > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante il salvataggio (possibile utente duplicato)", e);
            return false;
        }
    }

    @Override
    public List<Utente> recuperaClassifica() {
        List<Utente> classifica = new ArrayList<>();
        String query = "SELECT username, password, crediti, ruolo FROM utenti WHERE ruolo = 'PLAYER' ORDER BY crediti DESC LIMIT 10";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Utente u = new Utente(rs.getString("username"), rs.getString("password"));
                u.setCrediti(rs.getInt("crediti"));
                u.setRuolo(rs.getString("ruolo"));
                classifica.add(u);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore nel recupero della classifica globale", e);
        }
        return classifica;
    }

    @Override
    public boolean aggiungiCreditiAlDB(String username, int quantita) {
        String query = "UPDATE utenti SET crediti = crediti + ? WHERE username = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, quantita);
            stmt.setString(2, username);
            
            int righeModificate = stmt.executeUpdate();
            return righeModificate > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'accredito dei fondi", e);
            return false;
        }
    }
}