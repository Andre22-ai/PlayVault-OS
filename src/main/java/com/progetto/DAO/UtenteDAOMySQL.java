package com.progetto.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Aggiunto per gestire la classifica
import java.util.List;      // Aggiunto per gestire la classifica

import com.progetto.Entity.Utente;

/**
 * Implementazione reale del Data Access Object per MySQL.
 */
public class UtenteDAOMySQL implements UtenteDAO {

    @Override
    public Utente autentica(String username, String password) {
        String query = "SELECT * FROM utenti WHERE BINARY username = ? AND BINARY password = ?";
        
        // Uso del try-with-resources per chiudere automaticamente connessione e statement
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Inseriamo i parametri in modo sicuro (Addio SQL Injection!)
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // L'utente esiste, estraiamo i dati per costruire l'Entity pura
                    Utente utente = new Utente(rs.getString("username"), rs.getString("password"));
                    utente.aggiungiCrediti(rs.getInt("crediti"));
                    utente.setRuolo(rs.getString("ruolo"));
                    return utente;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore durante l'autenticazione: " + e.getMessage());
        }
        return null; // Credenziali errate o utente non trovato
    }

    @Override
    public boolean salvaUtente(Utente utente) {
        String query = "INSERT INTO utenti (username, password, crediti) VALUES (?, ?, ?)";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setInt(3, utente.getCrediti());
            
            // executeUpdate restituisce il numero di righe modificate
            int righeInserite = stmt.executeUpdate();
            return righeInserite > 0;
            
        } catch (SQLException e) {
            System.err.println("[DAO] Errore durante il salvataggio (possibile utente duplicato): " + e.getMessage());
            return false;
        }
    }

    // =================================================================
    // STEP 2: IL MOTORE DELLA CLASSIFICA (HALL OF FAME)
    // =================================================================
    @Override
    public List<Utente> recuperaClassifica() {
        List<Utente> classifica = new ArrayList<>();
        // Estraiamo solo i PLAYER, ordinati per crediti (dal più alto al più basso), massimo 10 risultati
        String query = "SELECT username, password, crediti, ruolo FROM utenti WHERE ruolo = 'PLAYER' ORDER BY crediti DESC LIMIT 10";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Utente u = new Utente(rs.getString("username"), rs.getString("password"));
                u.setCrediti(rs.getInt("crediti")); // Assegniamo i crediti reali letti dal DB
                u.setRuolo(rs.getString("ruolo"));
                classifica.add(u);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore nel recupero della classifica globale: " + e.getMessage());
        }
        return classifica;
    }

    // =================================================================
    // STEP 3: SISTEMA DI RICOMPENSA (GAMIFICATION)
    // =================================================================
    public boolean aggiungiCreditiAlDB(String username, int quantita) {
        String query = "UPDATE utenti SET crediti = crediti + ? WHERE username = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, quantita);
            stmt.setString(2, username);
            
            int righeModificate = stmt.executeUpdate();
            return righeModificate > 0;
            
        } catch (SQLException e) {
            System.err.println("[DAO] Errore durante l'accredito dei fondi: " + e.getMessage());
            return false;
        }
    }



}