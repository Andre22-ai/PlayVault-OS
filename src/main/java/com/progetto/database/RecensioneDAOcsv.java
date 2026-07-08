package com.progetto.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.entita.Recensione;
import com.progetto.entita.Videogioco;

/**
 * Implementazione del Data Access Object per le Recensioni tramite File CSV.
 */
public class RecensioneDAOcsv implements RecensioneDAO {

    private static final Logger LOGGER = Logger.getLogger(RecensioneDAOcsv.class.getName());
    private static final String FILE_NAME = "data/recensioni.csv";
    private static final String SEPARATORE = ";"; 

    public RecensioneDAOcsv() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                boolean creato = file.createNewFile();
                if (creato) {
                    LOGGER.info("[CSV] File recensioni.csv creato con successo.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "[CSV] Impossibile creare il file recensioni.csv", e);
            }
        }
    }

    @Override
    public boolean salvaRecensione(Recensione recensione) {
        List<Recensione> tutte = recuperaTutte();
        boolean giaRecensito = tutte.stream()
                .anyMatch(r -> r.getUsername().equals(recensione.getUsername()) && r.getIdGioco() == recensione.getIdGioco());

        if (giaRecensito) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(recensione.getUsername() + SEPARATORE +
                     recensione.getIdGioco() + SEPARATORE +
                     recensione.getVoto() + SEPARATORE +
                     recensione.getCommento());
            bw.newLine();
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore nel salvataggio della recensione", e);
            return false;
        }
    }

    @Override
    public boolean aggiornaRecensione(Recensione recensione) {
        List<Recensione> tutte = recuperaTutte();
        boolean modificata = false;

        for (Recensione r : tutte) {
            if (r.getUsername().equals(recensione.getUsername()) && r.getIdGioco() == recensione.getIdGioco()) {
                r.setVoto(recensione.getVoto());
                r.setCommento(recensione.getCommento());
                modificata = true;
                break;
            }
        }

        if (modificata) {
            scriviTutte(tutte);
        }
        return modificata;
    }


    @Override
    public boolean eliminaRecensione(String username, int idGioco) {
        List<Recensione> tutte = recuperaTutte();
        boolean rimossa = tutte.removeIf(r -> r.getUsername().equals(username) && r.getIdGioco() == idGioco);

        if (rimossa) {
            scriviTutte(tutte);
        }
        return rimossa;
    }

    @Override
    public List<Recensione> recuperaRecensioniPerGioco(int idGioco) {
        List<Recensione> filtrate = new ArrayList<>();
        List<Recensione> tutte = recuperaTutte();

        for (Recensione r : tutte) {
            if (r.getIdGioco() == idGioco) {
                filtrate.add(r);
            }
        }
        return filtrate;
    }

    @Override
    public List<Recensione> recuperaRecensioniUtente(String username) {
        List<Recensione> listaUtente = new ArrayList<>();
        List<Recensione> tutte = recuperaTutte();
        List<Videogioco> catalogoGiochi = App.getVideogiocoDAO().recuperaTutti();

        for (Recensione r : tutte) {
            if (r.getUsername().equals(username)) {
                String titolo = catalogoGiochi.stream()
                        .filter(g -> g.getId() == r.getIdGioco())
                        .map(Videogioco::getTitolo)
                        .findFirst()
                        .orElse("Gioco Sconosciuto");
                
                r.setNomeGioco(titolo);
                listaUtente.add(r);
            }
        }
        return listaUtente;
    }

    private List<Recensione> recuperaTutte() {
        List<Recensione> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(SEPARATORE);
                if (dati.length >= 4) {
                    Recensione r = new Recensione(
                        dati[0],
                        Integer.parseInt(dati[1]),
                        Integer.parseInt(dati[2]),
                        dati[3]
                    );
                    lista.add(r);
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore lettura recensioni", e);
        }
        return lista;
    }

    private void scriviTutte(List<Recensione> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Recensione r : lista) {
                bw.write(r.getUsername() + SEPARATORE +
                         r.getIdGioco() + SEPARATORE +
                         r.getVoto() + SEPARATORE +
                         r.getCommento());
                bw.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore sovrascrittura recensioni", e);
        }
    }
}