package com.progetto.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter; 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.progetto.App;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Videogioco; 


public class LibreriaDAOcsv implements LibreriaDAO {

    private static final Logger LOGGER = Logger.getLogger(LibreriaDAOcsv.class.getName());
    private static final String FILE_NAME = "data/libreria.csv";
    private static final String SEPARATORE = ",";

    public LibreriaDAOcsv() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                boolean creato = file.createNewFile();
                if (creato) {
                    LOGGER.info("[CSV] File libreria.csv creato con successo.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "[CSV] Impossibile creare il file libreria.csv", e);
            }
        }
    }

    @Override
    public boolean verificaPossesso(String username, int idGioco) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(SEPARATORE);
                if (dati.length >= 2 && dati[0].equals(username) && Integer.parseInt(dati[1]) == idGioco) {
                    return true;
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante la verifica possesso gioco", e);
        }
        return false;
    }

    @Override
    public boolean acquistaGioco(String username, int idGioco, int costo) {
        // 1. Prima di tutto scaliamo i crediti all'utente nel file utenti.csv
        boolean pagamentoAvvenuto = App.getUtenteDAO().aggiungiCreditiAlDB(username, -costo);
        
        if (!pagamentoAvvenuto) {
            return false;
        }

        // 2. Se il pagamento è andato a buon fine, scriviamo l'acquisto nel CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(username + SEPARATORE + idGioco + SEPARATORE + "false");
            bw.newLine();
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante la scrittura dell'acquisto", e);
            
            App.getUtenteDAO().aggiungiCreditiAlDB(username, costo);
            return false;
        }
    }

    @Override
    public List<Videogioco> recuperaGiochiPropri(String username) {
        List<Videogioco> mieiGiochi = new ArrayList<>();
        List<Integer> idGiochiPosseduti = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(SEPARATORE);
                if (dati.length >= 2 && dati[0].equals(username)) {
                    idGiochiPosseduti.add(Integer.parseInt(dati[1]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante il recupero degli ID da libreria.csv", e);
            return mieiGiochi;
        }

        List<Videogioco> tuttoIlCatalogo = App.getVideogiocoDAO().recuperaTutti();

        for (int id : idGiochiPosseduti) {
            tuttoIlCatalogo.stream()
                    .filter(g -> g.getId() == id)
                    .findFirst()
                    .ifPresent(mieiGiochi::add);
        }

        return mieiGiochi;
    }

    

    @Override
    public List<ElementoLibreria> getLibreriaUtenteCompleta(String username) {
        List<ElementoLibreria> libreriaCompleta = new ArrayList<>();
        List<Videogioco> tuttoIlCatalogo = App.getVideogiocoDAO().recuperaTutti();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(SEPARATORE);
                if (dati.length >= 2 && dati[0].equals(username)) {
                    int idGioco = Integer.parseInt(dati[1]);
                    
                    boolean completato = false;
                    if (dati.length >= 3) {
                        completato = Boolean.parseBoolean(dati[2]);
                    }

                    final boolean isCompletato = completato;  
                    tuttoIlCatalogo.stream()
                            .filter(g -> g.getId() == idGioco)
                            .findFirst()
                            .ifPresent(gioco -> libreriaCompleta.add(new ElementoLibreria(gioco, isCompletato)));
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore durante il recupero della libreria completa", e);
        }

        return libreriaCompleta;
    }

    @Override
    public boolean impostaGiocoCompletato(String username, int idGioco) {
        List<String> righeAggiornate = new ArrayList<>();
        boolean modificato = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(SEPARATORE);
                
                if (dati.length >= 2 && dati[0].equals(username) && Integer.parseInt(dati[1]) == idGioco) {
                    righeAggiornate.add(username + SEPARATORE + idGioco + SEPARATORE + "true");
                    modificato = true;
                } else {
                    righeAggiornate.add(riga);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[CSV] Errore di lettura per l'impostazione completamento", e);
            return false;
        }

        if (modificato) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
                for (String rigaAggiornata : righeAggiornate) {
                    pw.println(rigaAggiornata);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "[CSV] Errore di scrittura per l'impostazione completamento", e);
                return false;
            }
        }

        return modificato;
    }
}