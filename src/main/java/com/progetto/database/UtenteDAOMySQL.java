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


public class UtenteDAOMySQL implements UtenteDAO {

    private static final Logger LOGGER = Logger.getLogger(UtenteDAOMySQL.class.getName());

    @Override
    public Utente autentica(String username, String password) {
        String query = "SELECT username, password, crediti, ruolo, esperienza FROM utenti WHERE BINARY username = ? AND BINARY password = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utente utente = new Utente(rs.getString("username"), rs.getString("password"));
                    utente.setCrediti(rs.getInt("crediti")); 
                    utente.setRuolo(rs.getString("ruolo"));
                    utente.setEsperienza(rs.getInt("esperienza")); 
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
        String query = "INSERT INTO utenti (username, password, crediti, esperienza) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setInt(3, utente.getCrediti());
            stmt.setInt(4, utente.getEsperienza()); 
            
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
        String query = "SELECT username, password, crediti, ruolo, esperienza FROM utenti WHERE ruolo = 'PLAYER' ORDER BY esperienza DESC LIMIT 10";        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Utente u = new Utente(rs.getString("username"), rs.getString("password"));
                u.setCrediti(rs.getInt("crediti"));
                u.setRuolo(rs.getString("ruolo"));
                u.setEsperienza(rs.getInt("esperienza")); 
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

    @Override
    public boolean aggiornaPassword(String username, String nuovaPassword) {
        String query = "UPDATE utenti SET password = ? WHERE username = ?";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuovaPassword);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'aggiornamento della password", e);
            return false;
        }
    }

    @Override
    public boolean eliminaAccount(String username) {
        String query = "DELETE FROM utenti WHERE username = ?";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'eliminazione dell'account", e);
            return false;
        }
    }

    @Override
    public boolean aggiornaEsperienza(String username, int nuovaEsperienza) {
        String query = "UPDATE utenti SET esperienza = ? WHERE username = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, nuovaEsperienza);
            stmt.setString(2, username);
            
            int righeModificate = stmt.executeUpdate();
            return righeModificate > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'aggiornamento dell'esperienza", e);
            return false;
        }
    }
}