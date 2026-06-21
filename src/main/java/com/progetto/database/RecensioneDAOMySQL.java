package com.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.entita.Recensione;

public class RecensioneDAOMySQL implements RecensioneDAO {

    // --- NUOVO: Inizializzazione del Logger ---
    private static final Logger LOGGER = Logger.getLogger(RecensioneDAOMySQL.class.getName());

    /**
     * Tenta di salvare la recensione nel database. 
     * Restituisce true se va a buon fine, false se l'utente ha già recensito questo gioco 
     * (grazie al blocco UNIQUE su MySQL) o se c'è un errore.
     */
    public boolean salvaRecensione(Recensione recensione) {
        String query = "INSERT INTO recensioni (username, id_gioco, voto, commento) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, recensione.getUsername());
            stmt.setInt(2, recensione.getIdGioco());
            stmt.setInt(3, recensione.getVoto());
            stmt.setString(4, recensione.getCommento());
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            // Se scatta l'eccezione, probabilmente è violato il vincolo UNIQUE (recensione già esistente)
            LOGGER.log(Level.SEVERE, "[DAO] Impossibile salvare la recensione (già esistente o errore DB)", e);
            return false;
        }
    }

    // --- NUOVO: Modifica una recensione esistente ---
    public boolean aggiornaRecensione(Recensione recensione) {
        String query = "UPDATE recensioni SET voto = ?, commento = ? WHERE username = ? AND id_gioco = ?";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, recensione.getVoto());
            stmt.setString(2, recensione.getCommento());
            stmt.setString(3, recensione.getUsername());
            stmt.setInt(4, recensione.getIdGioco());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore aggiornamento recensione", e);
            return false;
        }
    }

    // --- NUOVO: Elimina una recensione ---
    public boolean eliminaRecensione(String username, int idGioco) {
        String query = "DELETE FROM recensioni WHERE username = ? AND id_gioco = ?";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setInt(2, idGioco);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore eliminazione recensione", e);
            return false;
        }
    }

    /**
     * Recupera la lista di tutte le recensioni associate a un determinato videogioco,
     * ordinate dalla più recente alla più vecchia.
     */
    public List<Recensione> recuperaRecensioniPerGioco(int idGioco) {
        List<Recensione> lista = new ArrayList<>();
        // Estraiamo chi l'ha scritta, il voto e il commento
        String query = "SELECT username, voto, commento FROM recensioni WHERE id_gioco = ? ORDER BY id_recensione DESC";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idGioco);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Ricostruiamo l'oggetto Recensione con i dati estratti
                    Recensione r = new Recensione(
                        rs.getString("username"),
                        idGioco,
                        rs.getInt("voto"),
                        rs.getString("commento")
                    );
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante il recupero delle recensioni", e);
        }
        return lista;
    }

    /**
     * Estrae tutte le recensioni di un singolo utente e recupera anche il titolo del gioco.
     */
    public List<Recensione> recuperaRecensioniUtente(String username) {
        List<Recensione> lista = new ArrayList<>();
        String query = "SELECT r.voto, r.commento, r.id_gioco, v.titolo " +
                       "FROM recensioni r " +
                       "JOIN videogiochi v ON r.id_gioco = v.id_gioco " +
                       "WHERE r.username = ? ORDER BY r.id_recensione DESC";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione(
                        username, 
                        rs.getInt("id_gioco"), 
                        rs.getInt("voto"), 
                        rs.getString("commento")
                    );
                    r.setNomeGioco(rs.getString("titolo")); // Salviamo il titolo estratto!
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore recupero recensioni utente", e);
        }
        return lista;
    }
}