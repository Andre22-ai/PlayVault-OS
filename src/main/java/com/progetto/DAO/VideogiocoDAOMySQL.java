package com.progetto.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.progetto.Entity.Videogioco;

public class VideogiocoDAOMySQL implements VideogiocoDAO {

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
            System.err.println("[DAO] Errore salvataggio: " + e.getMessage());
            return false;
        }
    }

   @Override
    public List<Videogioco> recuperaTutti() {
        List<Videogioco> catalogo = new ArrayList<>();
        String query = "SELECT * FROM videogiochi"; // Prende TUTTO il catalogo
        
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
            System.err.println("[DAO] Errore nel recupero del catalogo: " + e.getMessage());
        }
        
        return catalogo;
    }
}    