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
        String query = "INSERT INTO videogiochi (titolo, genere, anno_uscita, sviluppatore, descrizione_it, descrizione_en) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, gioco.getTitolo());
            stmt.setString(2, gioco.getGenere());
            stmt.setInt(3, gioco.getAnnoUscita());
            stmt.setString(4, gioco.getSviluppatore());
            stmt.setString(5, gioco.getDescrizioneIt());
            stmt.setString(6, gioco.getDescrizioneEn());

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
        String query = "SELECT id_gioco, titolo, genere, anno_uscita, sviluppatore, descrizione_it, descrizione_en FROM videogiochi";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String titolo = rs.getString("titolo");
                String descrizioneIt = rs.getString("descrizione_it");
                String descrizioneEn = rs.getString("descrizione_en");
                String[] descrizioni = descrizioniPerTitolo(titolo, descrizioneIt, descrizioneEn);
                Videogioco gioco = new Videogioco(
                    titolo,
                    rs.getString("genere"),
                    rs.getInt("anno_uscita"),
                    rs.getString("sviluppatore"),
                    descrizioni[0],
                    descrizioni[1]
                );
                gioco.setId(rs.getInt("id_gioco"));
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
        String query = "SELECT id_gioco, titolo, genere, anno_uscita, sviluppatore, descrizione FROM videogiochi";

        try (Connection conn = GestoreConnessione.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String titolo = rs.getString("titolo");
                String descrizioneDb = rs.getString("descrizione");
                String[] descrizioni = descrizioniPerTitolo(titolo, descrizioneDb, null);
                Videogioco gioco = new Videogioco(
                    titolo,
                    rs.getString("genere"),
                    rs.getInt("anno_uscita"),
                    rs.getString("sviluppatore"),
                    descrizioni[0],
                    descrizioni[1]
                );
                gioco.setId(rs.getInt("id_gioco"));
                catalogo.add(gioco);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "[DAO] Errore nel recupero del catalogo compatibile", ex);
        }

        return catalogo;
    }

    private String[] descrizioniPerTitolo(String titolo, String descrizioneIt, String descrizioneEn) {
        if (titolo == null) return new String[]{"", ""};

        return switch (titolo.toLowerCase()) {
            case "cyberpunk 2077" -> new String[]{
                "Step into Night City, where chrome, corruption, and destiny collide in a neon-soaked future ruled by power and secrets.",
                "Entra in Night City, dove chrome, corruzione e destino si scontrano in un futuro neon dominato da potere e segreti."
            };
            case "hollow knight" -> new String[]{
                "A haunting journey through a ruined kingdom where every corridor hides a secret and every defeat sharpens your resolve.",
                "Un viaggio inquietante attraverso un regno in rovina dove ogni corridoio nasconde un segreto e ogni sconfitta affila la tua determinazione."
            };
            case "elden ring" -> new String[]{
                "Traverse the Lands Between and forge your legend as the next Elden Lord in a world of myth, ruin, and ancient power.",
                "Attraversa l'Interregno e forgia la tua leggenda come il prossimo Lord Ancestrale in un mondo di mito, rovina e antico potere."
            };
            default -> new String[]{
                // Per tutti gli altri giochi usa il database, oppure l'altra lingua se manca
                descrizioneEn != null && !descrizioneEn.isBlank() ? descrizioneEn : descrizioneIt,
                descrizioneIt != null && !descrizioneIt.isBlank() ? descrizioneIt : descrizioneEn
            };
        };
    }

    private boolean colonnaMancante(SQLException e) {
        String message = e.getMessage();
        return message != null && (message.contains("Unknown column") || message.contains("doesn't exist") || message.contains("unknown column"));
    }
}