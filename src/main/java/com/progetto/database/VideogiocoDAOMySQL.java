package com.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.entita.Videogioco;

public class VideogiocoDAOMySQL implements VideogiocoDAO {

    private static final Logger LOGGER = Logger.getLogger(VideogiocoDAOMySQL.class.getName());

    @Override
    public boolean salvaGioco(Videogioco gioco) {
        String query = "INSERT INTO videogiochi (titolo, genere, anno_uscita, sviluppatore, descrizione_it, descrizione_en, exp_fornita) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, gioco.getTitolo());
            stmt.setString(2, gioco.getGenere());
            stmt.setInt(3, gioco.getAnnoUscita());
            stmt.setString(4, gioco.getSviluppatore());
            stmt.setString(5, gioco.getDescrizioneIt());
            stmt.setString(6, gioco.getDescrizioneEn());
            stmt.setInt(7, gioco.getExpFornita()); 

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (colonnaMancante(e)) {
                return salvaGiocoCompatibile(gioco);
            }
            LOGGER.log(Level.SEVERE, "[DAO] Errore salvataggio", e);
            return false;
        }
    }

    private boolean salvaGiocoCompatibile(Videogioco gioco) {
        String query = "INSERT INTO videogiochi (titolo, genere, anno_uscita, sviluppatore, descrizione) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gioco.getTitolo());
            stmt.setString(2, gioco.getGenere());
            stmt.setInt(3, gioco.getAnnoUscita());
            stmt.setString(4, gioco.getSviluppatore());
            stmt.setString(5, gioco.getDescrizioneLocale());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore salvataggio compatibile", e);
            return false;
        }
    }

    @Override
    public List<Videogioco> recuperaTutti() {
        List<Videogioco> catalogo = new ArrayList<>();
        String query = "SELECT id_gioco, titolo, genere, anno_uscita, sviluppatore, descrizione_it, descrizione_en, exp_fornita FROM videogiochi WHERE visibile = true";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String descIt = rs.getString("descrizione_it");
                String descEn = rs.getString("descrizione_en");
                
                // Creiamo l'oggetto usando direttamente i dati del DB, proteggendoci da eventuali null
                Videogioco gioco = new Videogioco(
                    rs.getString("titolo"),
                    rs.getString("genere"),
                    rs.getInt("anno_uscita"),
                    rs.getString("sviluppatore"),
                    descIt != null ? descIt : "",
                    descEn != null ? descEn : ""
                );
                
                gioco.setId(rs.getInt("id_gioco"));
                gioco.setExpFornita(rs.getInt("exp_fornita")); 
                catalogo.add(gioco);
            }

        } catch (SQLException e) {
            if (colonnaMancante(e)) {
                return recuperaTuttiCompatibili();
            }
            LOGGER.log(Level.SEVERE, "[DAO] Errore nel recupero del catalogo", e);
        }

        return catalogo;
    }

    private List<Videogioco> recuperaTuttiCompatibili() {
        List<Videogioco> catalogo = new ArrayList<>();
        String query = "SELECT id_gioco, titolo, genere, anno_uscita, sviluppatore, descrizione FROM videogiochi WHERE visibile = true";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String descDb = rs.getString("descrizione");
                String descSicura = descDb != null ? descDb : "";
                
                // Nel DB compatibile abbiamo una sola colonna, la usiamo per entrambe le lingue
                Videogioco gioco = new Videogioco(
                    rs.getString("titolo"),
                    rs.getString("genere"),
                    rs.getInt("anno_uscita"),
                    rs.getString("sviluppatore"),
                    descSicura,
                    descSicura
                );
                
                gioco.setId(rs.getInt("id_gioco"));
                catalogo.add(gioco);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore nel recupero del catalogo compatibile", ex);
        }

        return catalogo;
    }

    @Override
    public boolean nascondiGiocoDalCatalogo(int idGioco) {
        String query = "UPDATE videogiochi SET visibile = false WHERE id_gioco = ?";
        
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, idGioco);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore durante l'operazione di rimozione logica (Soft Delete)", e);
            return false;
        }
    }

    private boolean colonnaMancante(SQLException e) {
        String message = e.getMessage();
        return message != null && (message.contains("Unknown column") || message.contains("doesn't exist") || message.contains("unknown column"));
    }
}