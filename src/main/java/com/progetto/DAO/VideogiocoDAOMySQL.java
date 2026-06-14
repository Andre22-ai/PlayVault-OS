package com.progetto.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.Entity.Videogioco;

public class VideogiocoDAOMySQL implements VideogiocoDAO {

    // --- NUOVO: Inizializzazione del Logger ---
    private static final Logger LOGGER = Logger.getLogger(VideogiocoDAOMySQL.class.getName());

    @Override
    public boolean salvaGioco(Videogioco gioco) {
        String query = "INSERT INTO videogiochi (titolo, genere, anno_uscita, sviluppatore, descrizione) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, gioco.getTitolo());
            stmt.setString(2, gioco.getGenere());
            stmt.setInt(3, gioco.getAnnoUscita());
            stmt.setString(4, gioco.getSviluppatore());
            stmt.setString(5, gioco.getDescrizione());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // FIX S106: Logger al posto di System.err
            LOGGER.log(Level.SEVERE, "[DAO] Errore salvataggio", e);
            return false;
        }
    }

   @Override
    public List<Videogioco> recuperaTutti() {
        List<Videogioco> catalogo = new ArrayList<>();
        // FIX S6905: Prende TUTTO il catalogo ma dichiarando le singole colonne invece di usare "*"
        String query = "SELECT id_gioco, titolo, genere, anno_uscita, sviluppatore, descrizione FROM videogiochi"; 
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            // Cicliamo su tutte le righe che MySQL ci restituisce
            while (rs.next()) {
                Videogioco gioco = new Videogioco(
                    rs.getString("titolo"),
                    rs.getString("genere"),
                    rs.getInt("anno_uscita"),
                    rs.getString("sviluppatore"),
                    rs.getString("descrizione")
                );
                gioco.setId(rs.getInt("id_gioco")); 
                
                catalogo.add(gioco); // Mettiamo il gioco nella "scatola" da mandare alla UI
            }
            
        } catch (SQLException e) {
            // FIX S106: Logger al posto di System.err
            LOGGER.log(Level.SEVERE, "[DAO] Errore nel recupero del catalogo", e);
        }
        
        return catalogo;
    }
}